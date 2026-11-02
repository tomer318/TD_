package com.game.td.renderers

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Align
import com.game.td.Config
import com.game.td.ecs.components.*
import com.game.td.managers.AchievementManager
import com.game.td.managers.GameMode
import com.game.td.managers.SaveManager
import com.game.td.managers.SoundManager
import com.game.td.managers.WaveManager

enum class SpecialUnlockPopupType {
    NONE,
    REINFORCEMENTS,
    RAIN_OF_FIRE
}

enum class HintPopupType {
    NONE,
    ARMORED_ENEMIES,
    COMMAND_TROOPS,
    STRATEGY_BASICS,
    MAGIC_RESISTANT_ENEMIES,
    HEAVILY_ARMORED_ENEMIES
}

object TutorialRenderer {

    private val OUTLINE = Color(0.12f, 0.09f, 0.07f, 1f)
    private val PARCHMENT = Color(0.93f, 0.87f, 0.65f, 1f)
    private val PARCHMENT_DARK = Color(0.85f, 0.78f, 0.54f, 1f)

    private val oldMatrix = Matrix4()
    private val tempMatrix = Matrix4()

    private sealed class LeftBadgeItem {
        data class NewEnemyBadge(val enemyType: EnemyType) : LeftBadgeItem()
        data class TipBadge(val hintType: HintPopupType) : LeftBadgeItem()
    }

    private data class EnemyCardDetails(
        val title: String,
        val description: String,
        val bullets: List<String>,
        val footnote: String = "",
        val bottomTip1: String = "",
        val bottomTip2: String = ""
    )

    private data class Level4UnlockEntry(
        val subtitle: String,
        val title: String,
        val description: String,
        val towerType: TowerType,
        val specialization: TowerSpecialization,
        val soldierRank: String? = null
    )

    private val activeLeftBadges = mutableListOf<LeftBadgeItem>()
    private var showClickHereOnFirstBadge: Boolean = false

    private var currentLevelNum: Int = 1
    private var currentMode: GameMode = GameMode.CAMPAIGN

    var isLevel1CampaignActive: Boolean = false
        private set

    var isInstructionOpen: Boolean = false
        private set
    var instructionPage: Int = 1
        private set

    var activeTowerUpgradePopupLevel: Int = 0
        private set

    var activeSpecialPopup: SpecialUnlockPopupType = SpecialUnlockPopupType.NONE
        private set

    var activeEnemyPopup: EnemyType? = null
        private set

    var activeHintPopup: HintPopupType = HintPopupType.NONE
        private set

    var isReinforcementsUnlocked: Boolean = true
        private set
    var isRainOfFireUnlocked: Boolean = true
        private set

    private var hasTriggeredWave3Popup: Boolean = false
    private var hasTriggeredWave5Hint: Boolean = false
    private var hasTriggeredWave6Hint: Boolean = false
    private var hasTriggeredFinalWavePopup: Boolean = false
    private var hasTriggeredLevel2StrategyHint: Boolean = false
    private var hasTriggeredLevel3MagicHint: Boolean = false
    private var hasTriggeredLevel5Wave5TowerPopup: Boolean = false
    private var hasTriggeredLevel6HeavyArmorHint: Boolean = false

    fun initForLevel(levelNumber: Int, gameMode: GameMode) {
        currentLevelNum = levelNumber
        currentMode = gameMode

        activeLeftBadges.clear()
        showClickHereOnFirstBadge = false
        activeEnemyPopup = null
        activeHintPopup = HintPopupType.NONE
        activeTowerUpgradePopupLevel = 0
        activeSpecialPopup = SpecialUnlockPopupType.NONE

        hasTriggeredLevel2StrategyHint = false
        hasTriggeredLevel3MagicHint = false
        hasTriggeredLevel5Wave5TowerPopup = false
        hasTriggeredLevel6HeavyArmorHint = false

        if (levelNumber == 1 && gameMode == GameMode.CAMPAIGN) {
            isLevel1CampaignActive = true
            isInstructionOpen = true
            instructionPage = 1

            isReinforcementsUnlocked = false
            isRainOfFireUnlocked = false
            hasTriggeredWave3Popup = false
            hasTriggeredWave5Hint = false
            hasTriggeredWave6Hint = false
            hasTriggeredFinalWavePopup = false
        } else {
            isLevel1CampaignActive = false
            isInstructionOpen = false
            instructionPage = 1
            isReinforcementsUnlocked = true
            isRainOfFireUnlocked = true
            hasTriggeredWave3Popup = true
            hasTriggeredWave5Hint = true
            hasTriggeredWave6Hint = true
            hasTriggeredFinalWavePopup = true

            if (gameMode == GameMode.CAMPAIGN) {
                when (levelNumber) {
                    2 -> {
                        activeTowerUpgradePopupLevel = 2
                        SoundManager.playTowerUpgrade()
                    }
                    4 -> {
                        activeTowerUpgradePopupLevel = 3
                        SoundManager.playTowerUpgrade()
                    }
                    6, 7, 8, 9, 10 -> {
                        activeTowerUpgradePopupLevel = levelNumber
                        SoundManager.playTowerUpgrade()
                    }
                }
            }
        }
    }

    fun isModalOpen(): Boolean {
        return isInstructionOpen ||
            activeTowerUpgradePopupLevel > 0 ||
            activeSpecialPopup != SpecialUnlockPopupType.NONE ||
            activeEnemyPopup != null ||
            activeHintPopup != HintPopupType.NONE
    }

    fun onEnemySpawned(enemyType: EnemyType) {
        if (enemyType == EnemyType.SPIDER_HATCHLING) return

        val prefs = Gdx.app.getPreferences("kr_save_slot_${SaveManager.activeSlot}")
        val seenKey = "seen_new_enemy_${enemyType.name}"
        if (!prefs.getBoolean(seenKey, false)) {
            prefs.putBoolean(seenKey, true)
            prefs.flush()

            if (activeLeftBadges.none { it is LeftBadgeItem.NewEnemyBadge && it.enemyType == enemyType }) {
                activeLeftBadges.add(LeftBadgeItem.NewEnemyBadge(enemyType))
                showClickHereOnFirstBadge = true
                SoundManager.playTowerPreview()
            }
        }

        if (currentLevelNum == 3 && currentMode == GameMode.CAMPAIGN &&
            enemyType == EnemyType.SHAMAN && !hasTriggeredLevel3MagicHint
        ) {
            hasTriggeredLevel3MagicHint = true
            if (activeLeftBadges.none { it is LeftBadgeItem.TipBadge && it.hintType == HintPopupType.MAGIC_RESISTANT_ENEMIES }) {
                activeLeftBadges.add(LeftBadgeItem.TipBadge(HintPopupType.MAGIC_RESISTANT_ENEMIES))
                showClickHereOnFirstBadge = true
                SoundManager.playTowerPreview()
            }
        }
    }

    fun checkWaveTriggers(levelNumber: Int, gameMode: GameMode, currentWaveDisplay: Int, totalWaves: Int) {
        if (gameMode != GameMode.CAMPAIGN || isModalOpen()) return

        if (levelNumber == 1 && isLevel1CampaignActive) {
            if (currentWaveDisplay == 3 && !hasTriggeredWave3Popup) {
                hasTriggeredWave3Popup = true
                SoundManager.playTowerUpgrade()
                activeSpecialPopup = SpecialUnlockPopupType.REINFORCEMENTS
                return
            }

            if (currentWaveDisplay == 5 && !hasTriggeredWave5Hint) {
                hasTriggeredWave5Hint = true
                activeLeftBadges.add(LeftBadgeItem.TipBadge(HintPopupType.ARMORED_ENEMIES))
                showClickHereOnFirstBadge = true
                SoundManager.playTowerPreview()
            }

            if (currentWaveDisplay == 6 && !hasTriggeredWave6Hint) {
                hasTriggeredWave6Hint = true
                activeLeftBadges.add(LeftBadgeItem.TipBadge(HintPopupType.COMMAND_TROOPS))
                showClickHereOnFirstBadge = true
                SoundManager.playTowerPreview()
            }

            if (totalWaves > 0 && currentWaveDisplay == totalWaves && !hasTriggeredFinalWavePopup) {
                hasTriggeredFinalWavePopup = true
                SoundManager.playTowerUpgrade()
                activeSpecialPopup = SpecialUnlockPopupType.RAIN_OF_FIRE
            }
        }

        if (levelNumber == 2 && currentWaveDisplay == 1 && !hasTriggeredLevel2StrategyHint) {
            hasTriggeredLevel2StrategyHint = true
            activeLeftBadges.add(LeftBadgeItem.TipBadge(HintPopupType.STRATEGY_BASICS))
            showClickHereOnFirstBadge = true
            SoundManager.playTowerPreview()
        }

        if (levelNumber == 5 && currentWaveDisplay == 5 && !hasTriggeredLevel5Wave5TowerPopup) {
            hasTriggeredLevel5Wave5TowerPopup = true
            activeTowerUpgradePopupLevel = 5
            SoundManager.playTowerUpgrade()
            return
        }

        if (levelNumber == 6 && currentWaveDisplay == 1 && !hasTriggeredLevel6HeavyArmorHint) {
            hasTriggeredLevel6HeavyArmorHint = true
            activeLeftBadges.add(LeftBadgeItem.TipBadge(HintPopupType.HEAVILY_ARMORED_ENEMIES))
            showClickHereOnFirstBadge = true
            SoundManager.playTowerPreview()
        }
    }

    fun handleBadgeTouch(x: Float, y: Float): Boolean {
        if (isModalOpen() || activeLeftBadges.isEmpty()) return false

        val badgeX = 68f
        val startY = 562f
        val spacing = 78f

        for (i in activeLeftBadges.indices) {
            val badgeY = startY - i * spacing
            val hitCircle = Vector2(badgeX, badgeY).dst(x, y) <= 34f
            val hitBubble = (i == 0 && showClickHereOnFirstBadge && x in (badgeX + 30f)..(badgeX + 205f) && y in (badgeY - 24f)..(badgeY + 24f))

            if (hitCircle || hitBubble) {
                SoundManager.playClick()
                showClickHereOnFirstBadge = false
                when (val clicked = activeLeftBadges.removeAt(i)) {
                    is LeftBadgeItem.NewEnemyBadge -> {
                        activeEnemyPopup = clicked.enemyType
                        AchievementManager.onEnemyCardOpened()
                    }
                    is LeftBadgeItem.TipBadge -> {
                        activeHintPopup = clicked.hintType
                    }
                }
                return true
            }
        }
        return false
    }

    fun handleTouch(
        x: Float,
        y: Float,
        onUnlockReinforcements: () -> Unit = {},
        onUnlockRainOfFire: () -> Unit = {}
    ): Boolean {
        val cX = Config.WORLD_WIDTH / 2f
        val cY = Config.WORLD_HEIGHT / 2f

        if (isInstructionOpen) {
            if (instructionPage < 3) {
                if (x in (cX - 245f)..(cX - 5f) && y in (cY - 265f)..(cY - 195f)) {
                    SoundManager.playClick()
                    isInstructionOpen = false
                    return true
                }
                if (x in (cX + 5f)..(cX + 245f) && y in (cY - 265f)..(cY - 195f)) {
                    SoundManager.playClick()
                    instructionPage++
                    return true
                }
            } else {
                if (x in (cX - 135f)..(cX + 135f) && y in (cY - 275f)..(cY - 195f)) {
                    SoundManager.playBattleStart()
                    isInstructionOpen = false
                    return true
                }
            }
            return true
        }

        if (activeTowerUpgradePopupLevel > 0) {
            if (activeTowerUpgradePopupLevel in listOf(2, 3)) {
                val popupCenterY = Config.WORLD_HEIGHT / 2f + 28f
                val scrollY = popupCenterY - 340f / 2f
                val okBtnX = cX + 95f
                val okBtnY = scrollY - 62f
                val okBtnW = 185f
                val okBtnH = 60f

                if (x in (okBtnX - 15f)..(okBtnX + okBtnW + 15f) && y in (okBtnY - 15f)..(okBtnY + okBtnH + 15f)) {
                    SoundManager.playClick()
                    activeTowerUpgradePopupLevel = 0
                }
            } else {
                val entriesCount = getLevel4UnlockEntries(activeTowerUpgradePopupLevel).size
                val bottomScrollY = if (entriesCount == 1) Config.WORLD_HEIGHT / 2f - 105f else 132f
                val okBtnX = cX + 55f
                val okBtnY = bottomScrollY - 56f
                val okBtnW = 175f
                val okBtnH = 56f

                if (x in (okBtnX - 20f)..(okBtnX + okBtnW + 20f) && y in (okBtnY - 20f)..(okBtnY + okBtnH + 20f)) {
                    SoundManager.playClick()
                    activeTowerUpgradePopupLevel = 0
                }
            }
            return true
        }

        if (activeSpecialPopup != SpecialUnlockPopupType.NONE) {
            val popupCenterY = Config.WORLD_HEIGHT / 2f + 35f
            val scrollY = popupCenterY - 265f / 2f
            val okBtnX = cX + 95f
            val okBtnY = scrollY - 58f
            val okBtnW = 175f
            val okBtnH = 58f

            if (x in (okBtnX - 10f)..(okBtnX + okBtnW + 10f) && y in (okBtnY - 10f)..(okBtnY + okBtnH + 10f)) {
                SoundManager.playTowerBuild()
                when (activeSpecialPopup) {
                    SpecialUnlockPopupType.REINFORCEMENTS -> {
                        isReinforcementsUnlocked = true
                        onUnlockReinforcements()
                    }
                    SpecialUnlockPopupType.RAIN_OF_FIRE -> {
                        isRainOfFireUnlocked = true
                        onUnlockRainOfFire()
                    }
                    else -> {}
                }
                activeSpecialPopup = SpecialUnlockPopupType.NONE
                return true
            }
            return true
        }

        if (activeEnemyPopup != null) {
            val popupCenterY = Config.WORLD_HEIGHT / 2f + 35f
            val scrollY = popupCenterY - 265f / 2f
            val okBtnX = cX + 95f
            val okBtnY = scrollY - 58f
            val okBtnW = 175f
            val okBtnH = 58f

            if (x in (okBtnX - 15f)..(okBtnX + okBtnW + 15f) && y in (okBtnY - 15f)..(okBtnY + okBtnH + 15f)) {
                SoundManager.playClick()
                activeEnemyPopup = null
            }
            return true
        }

        if (activeHintPopup != HintPopupType.NONE) {
            val popupCenterY = Config.WORLD_HEIGHT / 2f + 22f
            val scrollY = popupCenterY - 390f / 2f
            val okBtnX = cX + 85f
            val okBtnY = scrollY - 58f
            val okBtnW = 175f
            val okBtnH = 58f

            if (x in (okBtnX - 15f)..(okBtnX + okBtnW + 15f) && y in (okBtnY - 15f)..(okBtnY + okBtnH + 15f)) {
                SoundManager.playClick()
                activeHintPopup = HintPopupType.NONE
            }
            return true
        }

        return false
    }

    private fun withScaledTransform(sr: ShapeRenderer, cx: Float, cy: Float, scale: Float, block: () -> Unit) {
        sr.end()
        oldMatrix.set(sr.transformMatrix)
        tempMatrix.idt().translate(cx, cy, 0f).scale(scale, scale, 1f)
        sr.transformMatrix = tempMatrix
        sr.begin(ShapeRenderer.ShapeType.Filled)
        block()
        sr.end()
        sr.transformMatrix = oldMatrix
        sr.begin(ShapeRenderer.ShapeType.Filled)
    }

    private fun drawRenderedStrategicPoint(sr: ShapeRenderer, cx: Float, cy: Float, scale: Float = 1f) {
        withScaledTransform(sr, cx, cy, scale) {
            TowerRenderer.renderStrategicPoint(sr, 0f, 0f, levelNumber = 1)
        }
    }

    private fun drawRenderedTower(
        sr: ShapeRenderer,
        type: TowerType,
        level: Int,
        cx: Float,
        cy: Float,
        scale: Float = 1f,
        specialization: TowerSpecialization = TowerSpecialization.NONE
    ) {
        withScaledTransform(sr, cx, cy, scale) {
            sr.color = Color(0.32f, 0.52f, 0.16f, 1f)
            sr.ellipse(-30f, -21f, 60f, 32f)
            sr.color = Color(0.62f, 0.58f, 0.48f, 1f)
            sr.ellipse(-25f, -18f, 50f, 26f)

            val dummyTower = Entity().apply {
                add(TransformComponent(position = Vector2(0f, 0f)))
                add(TowerComponent(towerType = type, level = level, specialization = specialization))
            }
            TowerRenderer.render(sr, dummyTower)
        }
    }

    private fun getEnemyAutoScale(type: EnemyType): Float = when (type) {
        EnemyType.VEZNAN_DEMON, EnemyType.JUGGERNAUT, EnemyType.JT -> 0.55f
        EnemyType.VEZNAN, EnemyType.YETI, EnemyType.MAGMA_ELEMENTAL, EnemyType.SPIDER_MATRIARCH -> 0.72f
        EnemyType.OGRE, EnemyType.DEMON_LORD, EnemyType.TROLL_CHIEFTAIN, EnemyType.MARAUDER -> 0.85f
        else -> 1.0f
    }

    private fun drawRenderedEnemy(sr: ShapeRenderer, type: EnemyType, cx: Float, cy: Float, scale: Float = 1f) {
        val finalScale = scale * getEnemyAutoScale(type)
        withScaledTransform(sr, cx, cy, finalScale) {
            val dummyEnemy = Entity().apply {
                add(TransformComponent(position = Vector2(0f, 0f)))
                add(HealthComponent(maxHealth = 100f, currentHealth = 100f))
                add(EnemyComponent(enemyType = type))
            }
            EnemyRenderer.render(sr, dummyEnemy, showHpBar = false)
        }
    }

    private fun drawRenderedEgg(sr: ShapeRenderer, cx: Float, cy: Float, scale: Float = 1f) {
        withScaledTransform(sr, cx, cy, scale) {
            val dummyEgg = Entity().apply {
                add(TransformComponent(position = Vector2(0f, 0f)))
            }
            EnemyRenderer.renderEgg(sr, dummyEgg)
        }
    }

    private fun drawRenderedSoldier(sr: ShapeRenderer, rankName: String, cx: Float, cy: Float, scale: Float = 1f) {
        withScaledTransform(sr, cx, cy, scale) {
            val dummySoldier = Entity().apply {
                add(TransformComponent(position = Vector2(0f, 0f)))
                add(HealthComponent(maxHealth = 100f, currentHealth = 100f))
                add(SoldierComponent(soldierRankName = rankName, targetPosition = Vector2(0f, 0f)))
            }
            SoldierRenderer.render(sr, dummySoldier, showHpBar = false)
        }
    }

    fun renderMapHints(
        sr: ShapeRenderer,
        batch: SpriteBatch,
        smallFont: BitmapFont,
        hasAnyTowerBuilt: Boolean,
        waveManager: WaveManager
    ) {
        val oldScaleX = smallFont.data.scaleX
        val oldScaleY = smallFont.data.scaleY

        renderLeftNotificationBadges(sr, batch, smallFont)

        if (isLevel1CampaignActive && !isModalOpen()) {
            if (!hasAnyTowerBuilt && waveManager.currentWaveIndex == -1) {
                drawMapCalloutBubble(
                    sr = sr,
                    batch = batch,
                    font = smallFont,
                    boxX = 395f,
                    boxY = 366f,
                    boxW = 160f,
                    boxH = 42f,
                    pointerTipX = 475f,
                    pointerTipY = 348f,
                    line1 = "BUILD HERE!",
                    line2 = ""
                )
            }

            if (waveManager.isCountdownActive && waveManager.currentWaveDisplay == 1) {
                drawMapCalloutBubble(
                    sr = sr,
                    batch = batch,
                    font = smallFont,
                    boxX = 590f,
                    boxY = 602f,
                    boxW = 275f,
                    boxH = 56f,
                    pointerTipX = 568f,
                    pointerTipY = 635f,
                    line1 = "INCOMING NEXT WAVE!",
                    line2 = "CLICK TO CALL IT EARLY"
                )
            }
        }

        smallFont.data.setScale(oldScaleX, oldScaleY)
    }

    private fun renderLeftNotificationBadges(
        sr: ShapeRenderer,
        batch: SpriteBatch,
        smallFont: BitmapFont
    ) {
        if (activeLeftBadges.isEmpty()) return

        val badgeX = 68f
        val startY = 562f
        val spacing = 78f

        for (i in activeLeftBadges.indices) {
            val item = activeLeftBadges[i]
            val badgeY = startY - i * spacing

            when (item) {
                is LeftBadgeItem.NewEnemyBadge -> {
                    sr.begin(ShapeRenderer.ShapeType.Filled)
                    sr.color = Color(0f, 0f, 0f, 0.35f)
                    sr.circle(badgeX + 2f, badgeY - 3f, 30f)

                    sr.color = OUTLINE
                    sr.circle(badgeX, badgeY, 30f)
                    sr.color = Color(0.98f, 0.78f, 0.12f, 1f)
                    sr.circle(badgeX, badgeY, 27f)
                    sr.color = OUTLINE
                    sr.circle(badgeX, badgeY, 23f)
                    sr.color = Color(0.32f, 0.28f, 0.20f, 1f)
                    sr.circle(badgeX, badgeY, 21f)

                    drawRenderedEnemy(sr, item.enemyType, badgeX - 2f, badgeY - 3f, scale = 1.05f)

                    val exX = badgeX + 22f
                    val exY = badgeY - 2f
                    sr.color = OUTLINE
                    sr.triangle(exX - 6f, exY + 20f, exX + 8f, exY + 18f, exX - 1f, exY - 4f)
                    sr.circle(exX - 2f, exY - 11f, 5.5f)
                    sr.color = Color(1f, 0.88f, 0.15f, 1f)
                    sr.triangle(exX - 4f, exY + 18f, exX + 5.5f, exY + 16.5f, exX - 1f, exY - 2f)
                    sr.circle(exX - 2f, exY - 11f, 3.8f)

                    sr.color = OUTLINE
                    sr.rect(badgeX - 32f, badgeY + 17f, 64f, 14f)
                    ComicFontRenderer.drawPlaqueText(sr, "NEW ENEMY", badgeX, badgeY + 24f, 8.5f, Color(1f, 0.90f, 0.18f, 1f))
                    sr.end()
                }

                is LeftBadgeItem.TipBadge -> {
                    sr.begin(ShapeRenderer.ShapeType.Filled)
                    sr.color = Color(0f, 0f, 0f, 0.35f)
                    sr.circle(badgeX + 2f, badgeY - 3f, 30f)

                    sr.color = OUTLINE
                    sr.circle(badgeX, badgeY, 30f)
                    sr.color = Color(0.20f, 0.72f, 0.96f, 1f)
                    sr.circle(badgeX, badgeY, 27f)
                    sr.color = OUTLINE
                    sr.circle(badgeX, badgeY, 23f)
                    sr.color = Color(0.14f, 0.22f, 0.30f, 1f)
                    sr.circle(badgeX, badgeY, 21f)

                    when (item.hintType) {
                        HintPopupType.ARMORED_ENEMIES -> drawArmorShieldIcon(sr, badgeX - 1f, badgeY - 1f, 0.65f, isMagic = false)
                        HintPopupType.MAGIC_RESISTANT_ENEMIES -> drawArmorShieldIcon(sr, badgeX - 1f, badgeY - 1f, 0.65f, isMagic = true)
                        HintPopupType.STRATEGY_BASICS -> drawSwordAndShieldIcon(sr, badgeX - 1f, badgeY - 1f, 0.68f)
                        HintPopupType.HEAVILY_ARMORED_ENEMIES -> drawHeavyHelmetBadgeIcon(sr, badgeX - 1f, badgeY - 2f, 0.75f)
                        else -> drawBlueRallyFlagIcon(sr, badgeX - 2f, badgeY - 2f, 0.65f)
                    }

                    val exX = badgeX + 22f
                    val exY = badgeY - 2f
                    sr.color = OUTLINE
                    sr.triangle(exX - 6f, exY + 20f, exX + 8f, exY + 18f, exX - 1f, exY - 4f)
                    sr.circle(exX - 2f, exY - 11f, 5.5f)
                    sr.color = Color(0.28f, 0.84f, 1f, 1f)
                    sr.triangle(exX - 4f, exY + 18f, exX + 5.5f, exY + 16.5f, exX - 1f, exY - 2f)
                    sr.circle(exX - 2f, exY - 11f, 3.8f)

                    sr.color = OUTLINE
                    sr.rect(badgeX - 18f, badgeY + 17f, 36f, 14f)
                    ComicFontRenderer.drawPlaqueText(sr, "TIP", badgeX, badgeY + 24f, 9.5f, Color(0.55f, 0.92f, 1f, 1f))
                    sr.end()
                }
            }

            if (i == 0 && showClickHereOnFirstBadge && !isModalOpen()) {
                drawMapCalloutBubble(
                    sr = sr,
                    batch = batch,
                    font = smallFont,
                    boxX = badgeX + 44f,
                    boxY = badgeY - 20f,
                    boxW = 148f,
                    boxH = 40f,
                    pointerTipX = badgeX + 31f,
                    pointerTipY = badgeY,
                    line1 = "CLICK HERE!",
                    line2 = ""
                )
            }
        }
    }

    private fun drawMapCalloutBubble(
        sr: ShapeRenderer,
        batch: SpriteBatch,
        font: BitmapFont,
        boxX: Float,
        boxY: Float,
        boxW: Float,
        boxH: Float,
        pointerTipX: Float,
        pointerTipY: Float,
        line1: String,
        line2: String
    ) {
        sr.begin(ShapeRenderer.ShapeType.Filled)
        sr.color = Color(0f, 0f, 0f, 0.35f)
        sr.rect(boxX + 3f, boxY - 3f, boxW, boxH)

        sr.color = OUTLINE
        sr.rect(boxX - 2f, boxY - 2f, boxW + 4f, boxH + 4f)
        if (pointerTipY < boxY) {
            sr.triangle(boxX + boxW / 2f - 10f, boxY, boxX + boxW / 2f + 10f, boxY, pointerTipX, pointerTipY)
        } else {
            sr.triangle(boxX, boxY + boxH / 2f - 9f, boxX, boxY + boxH / 2f + 9f, pointerTipX, pointerTipY)
        }

        sr.color = Color(0.95f, 0.92f, 0.78f, 1f)
        sr.rect(boxX, boxY, boxW, boxH)
        if (pointerTipY < boxY) {
            sr.triangle(boxX + boxW / 2f - 7f, boxY + 2f, boxX + boxW / 2f + 7f, boxY + 2f, pointerTipX, pointerTipY + 3f)
        } else {
            sr.triangle(boxX + 2f, boxY + boxH / 2f - 6f, boxX + 2f, boxY + boxH / 2f + 6f, pointerTipX + 4f, pointerTipY)
        }

        if (line2.isEmpty()) {
            ComicFontRenderer.drawRibbonBannerText(sr, line1, boxX + boxW / 2f, boxY + boxH / 2f, 14f)
        } else {
            ComicFontRenderer.drawRibbonBannerText(sr, line1, boxX + boxW / 2f, boxY + boxH - 16f, 13.5f)
        }
        sr.end()

        if (line2.isNotEmpty()) {
            batch.begin()
            font.data.setScale(0.84f)
            font.color = Color(0.25f, 0.20f, 0.14f, 1f)
            font.draw(batch, line2, boxX, boxY + 18f, boxW, Align.center, false)
            batch.end()
        }
    }

    fun renderPopups(
        sr: ShapeRenderer,
        batch: SpriteBatch,
        font: BitmapFont,
        smallFont: BitmapFont
    ) {
        if (!isModalOpen()) return

        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

        when {
            isInstructionOpen -> renderInstructionsScroll(sr, batch, font, smallFont, instructionPage)
            activeTowerUpgradePopupLevel in listOf(2, 3) -> renderNewTowerUpgradesPopup(sr, batch, font, smallFont, activeTowerUpgradePopupLevel)
            activeTowerUpgradePopupLevel >= 5 -> renderNewTowerUnlockedLevel4Popup(sr, batch, font, smallFont, activeTowerUpgradePopupLevel)
            activeSpecialPopup != SpecialUnlockPopupType.NONE -> renderSpecialUnlockPopup(sr, batch, font, smallFont, activeSpecialPopup)
            activeEnemyPopup != null -> renderNewEnemyPopup(sr, batch, font, smallFont, activeEnemyPopup!!)
            activeHintPopup != HintPopupType.NONE -> renderHintPopup(sr, batch, font, smallFont, activeHintPopup)
        }
    }

    private fun renderNewTowerUpgradesPopup(
        sr: ShapeRenderer,
        batch: SpriteBatch,
        font: BitmapFont,
        smallFont: BitmapFont,
        unlockedTier: Int
    ) {
        val cX = Config.WORLD_WIDTH / 2f
        val cY = Config.WORLD_HEIGHT / 2f + 28f
        val scrollW = 760f
        val scrollH = 340f
        val scrollX = cX - scrollW / 2f
        val scrollY = cY - scrollH / 2f

        val okBtnW = 185f
        val okBtnH = 60f
        val okBtnX = cX + 95f
        val okBtnY = scrollY - 62f

        val tabW = 440f
        val tabH = 66f
        val tabX = scrollX + 35f
        val tabY = scrollY + scrollH - 12f

        sr.begin(ShapeRenderer.ShapeType.Filled)
        sr.color = Color(0f, 0f, 0f, 0.40f)
        sr.rect(0f, 0f, Config.WORLD_WIDTH, Config.WORLD_HEIGHT)

        drawTabButton(sr, okBtnX, okBtnY, okBtnW, okBtnH, Color(0.16f, 0.38f, 0.58f, 1f))

        sr.color = OUTLINE
        sr.rect(tabX - 3f, tabY - 3f, tabW + 6f, tabH + 6f)
        sr.color = Color(0.44f, 0.34f, 0.25f, 1f)
        sr.rect(tabX, tabY, tabW, tabH)

        drawJaggedParchment(sr, scrollX, scrollY, scrollW, scrollH)

        ComicFontRenderer.drawPlaqueText(sr, "NEW TOWER UPGRADES!", tabX + tabW / 2f, tabY + tabH / 2f + 2f, 22f, Color(0.96f, 0.92f, 0.72f, 1f))
        ComicFontRenderer.drawRibbonBannerText(sr, "TOWERS LEVEL $unlockedTier AVAILABLE", cX, scrollY + scrollH - 42f, 21f)
        ComicFontRenderer.drawPlaqueText(sr, "OK!", okBtnX + okBtnW / 2f, okBtnY + okBtnH / 2f, 22f, Color.WHITE)

        val cardW = 166f
        val cardH = 162f
        val cardCentersX = listOf(cX - 270f, cX - 90f, cX + 90f, cX + 270f)
        val cardOffsetsY = listOf(scrollY + 12f, scrollY + 26f, scrollY + 14f, scrollY + 18f)

        for (i in 0..3) {
            val px = cardCentersX[i] - cardW / 2f
            val py = cardOffsetsY[i]

            sr.color = Color(0f, 0f, 0f, 0.28f)
            sr.rect(px + 5f, py - 5f, cardW, cardH)
            sr.color = OUTLINE
            sr.rect(px - 3f, py - 3f, cardW + 6f, cardH + 6f)
            sr.color = Color(0.88f, 0.80f, 0.56f, 1f)
            sr.rect(px, py, cardW, cardH)

            val inX = px + 10f
            val inY = py + 10f
            val inW = cardW - 20f
            val inH = cardH - 20f

            sr.color = Color(0.42f, 0.62f, 0.18f, 1f)
            sr.rect(inX, inY, inW, inH)
            sr.color = Color(0.84f, 0.74f, 0.46f, 1f)
            sr.rect(inX, inY, inW, inH * 0.52f)

            val cx = inX + inW / 2f
            val cy = inY + inH / 2f

            when (i) {
                0 -> {
                    val sRank = if (unlockedTier == 2) "Footman" else "Knight"
                    drawRenderedTower(sr, TowerType.BARRACKS, unlockedTier, cx, cy + 16f, scale = 1.45f)
                    drawRenderedSoldier(sr, sRank, cx - 36f, cy - 30f, scale = 1.35f)
                    drawRenderedSoldier(sr, sRank, cx - 2f, cy - 38f, scale = 1.35f)
                    drawRenderedSoldier(sr, sRank, cx + 34f, cy - 26f, scale = 1.35f)
                }
                1 -> drawRenderedTower(sr, TowerType.ARCHER, unlockedTier, cx, cy - 4f, scale = 1.65f)
                2 -> drawRenderedTower(sr, TowerType.MAGE, unlockedTier, cx, cy - 6f, scale = 1.65f)
                3 -> drawRenderedTower(sr, TowerType.ARTILLERY, unlockedTier, cx + 4f, cy - 4f, scale = 1.60f)
            }
        }
        sr.end()

        batch.begin()
        val oldSmallScaleX = smallFont.data.scaleX
        val oldSmallScaleY = smallFont.data.scaleY
        smallFont.data.setScale(1.00f)
        smallFont.color = OUTLINE
        smallFont.draw(
            batch,
            "YOU CAN NOW UPGRADE YOUR TOWERS UP TO LEVEL $unlockedTier.",
            scrollX, scrollY + scrollH - 74f, scrollW, Align.center, false
        )
        smallFont.data.setScale(oldSmallScaleX, oldSmallScaleY)
        batch.end()
    }

    private fun getLevel4UnlockEntries(levelNum: Int): List<Level4UnlockEntry> = when (levelNum) {
        5 -> listOf(
            Level4UnlockEntry(
                subtitle = "LEVEL 4 ARCHER TOWER",
                title = "RANGERS HIDEOUT",
                description = "HIGHEST RATE OF FIRE AND EXCELLENT RANGE. CAN POISON AND TRAP ENEMIES!",
                towerType = TowerType.ARCHER,
                specialization = TowerSpecialization.RANGERS_HIDEOUT
            )
        )
        6 -> listOf(
            Level4UnlockEntry(
                subtitle = "LEVEL 4 MAGE TOWER",
                title = "ARCANE WIZARD",
                description = "VERY HIGH DAMAGE, EXCELLENT AGAINST ARMORED ENEMIES",
                towerType = TowerType.MAGE,
                specialization = TowerSpecialization.ARCANE_WIZARD
            ),
            Level4UnlockEntry(
                subtitle = "LEVEL 4 BARRACKS",
                title = "HOLY ORDER",
                description = "SPAWNS PALADINS WITH FULL PLATE ARMOR THAT CAN BLOCK ENEMIES LONGER",
                towerType = TowerType.BARRACKS,
                specialization = TowerSpecialization.HOLY_ORDER,
                soldierRank = "Paladin"
            )
        )
        7 -> listOf(
            Level4UnlockEntry(
                subtitle = "LEVEL 4 MARKSMAN TOWER",
                title = "MUSKETEER GARRISON",
                description = "GREATEST RANGE AND VERY HIGH DAMAGE.",
                towerType = TowerType.ARCHER,
                specialization = TowerSpecialization.MUSKETEER_GARRISON
            )
        )
        8 -> listOf(
            Level4UnlockEntry(
                subtitle = "LEVEL 4 ARTILLERY",
                title = "500MM BIG BERTHA",
                description = "BIGGEST PIECE OF ARTILLERY THAT DEALS VERY HIGH AREA DAMAGE",
                towerType = TowerType.ARTILLERY,
                specialization = TowerSpecialization.BIG_BERTHA
            ),
            Level4UnlockEntry(
                subtitle = "LEVEL 4 BARRACKS",
                title = "BARBARIAN MEAD HALL",
                description = "SPAWNS AXE WIELDING BARBARIANS THAT CAN OBLITERATE MOST FOES.",
                towerType = TowerType.BARRACKS,
                specialization = TowerSpecialization.BARBARIAN_MEAD_HALL,
                soldierRank = "Barbarian"
            )
        )
        9 -> listOf(
            Level4UnlockEntry(
                subtitle = "LEVEL 4 MAGE TOWER",
                title = "SORCERER TOWER",
                description = "CURSES ENEMIES DAMAGING THEM AND REDUCING THEIR ARMOR FOR A WHILE.",
                towerType = TowerType.MAGE,
                specialization = TowerSpecialization.SORCERER_MAGE
            )
        )
        10 -> listOf(
            Level4UnlockEntry(
                subtitle = "LEVEL 4 ARTILLERY",
                title = "TESLA X104",
                description = "SHOOTS BOLTS OF LIGHTNING THAT CAN STRIKE MULTIPLE GROUND AND FLYING ENEMIES.",
                towerType = TowerType.ARTILLERY,
                specialization = TowerSpecialization.TESLA_X104
            )
        )
        else -> emptyList()
    }

    private fun renderNewTowerUnlockedLevel4Popup(
        sr: ShapeRenderer,
        batch: SpriteBatch,
        font: BitmapFont,
        smallFont: BitmapFont,
        levelNum: Int
    ) {
        val entries = getLevel4UnlockEntries(levelNum)
        if (entries.isEmpty()) return

        val isSnowTheme = levelNum in 7..9
        val isWastesTheme = levelNum >= 10

        val cX = Config.WORLD_WIDTH / 2f
        val scrollW = 620f
        val scrollX = cX - scrollW / 2f

        val cardH = if (entries.size == 1) 220f else 195f
        val gap = 22f
        val bottomScrollY = if (entries.size == 1) Config.WORLD_HEIGHT / 2f - 105f else 132f
        val topScrollY = bottomScrollY + (entries.size - 1) * (cardH + gap)

        val tabW = 390f
        val tabH = 62f
        val tabX = cX - tabW / 2f + 15f
        val tabY = topScrollY + cardH - 12f

        val okBtnW = 175f
        val okBtnH = 56f
        val okBtnX = cX + 55f
        val okBtnY = bottomScrollY - 56f

        sr.begin(ShapeRenderer.ShapeType.Filled)
        sr.color = Color(0f, 0f, 0f, 0.42f)
        sr.rect(0f, 0f, Config.WORLD_WIDTH, Config.WORLD_HEIGHT)

        drawTabButton(sr, okBtnX, okBtnY, okBtnW, okBtnH, Color(0.16f, 0.38f, 0.58f, 1f))

        sr.color = OUTLINE
        sr.rect(tabX - 3f, tabY - 3f, tabW + 6f, tabH + 6f)
        sr.color = Color(0.44f, 0.34f, 0.25f, 1f)
        sr.rect(tabX, tabY, tabW, tabH)

        ComicFontRenderer.drawPlaqueText(sr, "NEW TOWER UNLOCKED!", tabX + tabW / 2f, tabY + tabH / 2f + 2f, 20f, Color(0.96f, 0.92f, 0.72f, 1f))
        ComicFontRenderer.drawPlaqueText(sr, "OK!", okBtnX + okBtnW / 2f, okBtnY + okBtnH / 2f, 21f, Color.WHITE)

        for (idx in entries.indices) {
            val entry = entries[idx]
            val curScrollY = topScrollY - idx * (cardH + gap)

            drawJaggedParchment(sr, scrollX, curScrollY, scrollW, cardH)
            ComicFontRenderer.drawRibbonBannerText(sr, entry.title, scrollX + 410f, curScrollY + cardH - 68f, 20f)

            val picW = 195f
            val picH = cardH - 16f
            val picX = scrollX + 18f
            val picY = curScrollY + 8f

            sr.color = Color(0f, 0f, 0f, 0.25f)
            sr.rect(picX + 4f, picY - 4f, picW, picH)
            sr.color = OUTLINE
            sr.rect(picX - 3f, picY - 3f, picW + 6f, picH + 6f)
            sr.color = Color(0.92f, 0.85f, 0.65f, 1f)
            sr.rect(picX, picY, picW, picH)

            val inX = picX + 9f
            val inY = picY + 9f
            val inW = picW - 18f
            val inH = picH - 18f

            sr.color = when {
                isSnowTheme -> Color(0.76f, 0.82f, 0.88f, 1f)
                isWastesTheme -> Color(0.28f, 0.26f, 0.30f, 1f)
                else -> Color(0.42f, 0.62f, 0.18f, 1f)
            }
            sr.rect(inX, inY, inW, inH)

            sr.color = when {
                isSnowTheme -> Color(0.32f, 0.48f, 0.36f, 1f)
                isWastesTheme -> Color(0.18f, 0.16f, 0.20f, 1f)
                else -> Color(0.26f, 0.46f, 0.12f, 1f)
            }
            sr.circle(inX + 32f, inY + inH - 22f, 24f)
            sr.circle(inX + inW - 32f, inY + inH - 22f, 24f)
            if (isSnowTheme) {
                sr.color = Color(0.92f, 0.96f, 1f, 0.9f)
                sr.circle(inX + 32f, inY + inH - 16f, 16f)
                sr.circle(inX + inW - 32f, inY + inH - 16f, 16f)
            }

            sr.color = when {
                isSnowTheme -> Color(0.90f, 0.94f, 0.98f, 1f)
                isWastesTheme -> Color(0.48f, 0.45f, 0.48f, 1f)
                else -> Color(0.84f, 0.74f, 0.46f, 1f)
            }
            sr.rect(inX, inY, inW, inH * 0.52f)

            val towerCx = inX + inW / 2f
            val towerCy = if (entry.soldierRank != null) inY + inH * 0.62f else inY + inH * 0.46f
            val towerScale = if (entry.soldierRank != null) 1.45f else 1.70f

            drawRenderedTower(
                sr = sr,
                type = entry.towerType,
                level = 4,
                cx = towerCx,
                cy = towerCy,
                scale = towerScale,
                specialization = entry.specialization
            )

            if (entry.specialization == TowerSpecialization.MUSKETEER_GARRISON) {
                sr.color = Color(0.95f, 0.96f, 0.98f, 0.92f)
                sr.circle(towerCx + 22f, towerCy + 14f, 11f)
                sr.circle(towerCx + 32f, towerCy + 8f, 9f)
                sr.circle(towerCx - 16f, towerCy + 12f, 8f)
            }

            if (entry.specialization == TowerSpecialization.TESLA_X104) {
                val orbX = towerCx
                val orbY = towerCy + 40f
                sr.color = Color(0.25f, 0.88f, 1f, 0.95f)
                sr.rectLine(orbX, orbY, orbX + 28f, orbY - 14f, 5.5f)
                sr.rectLine(orbX + 28f, orbY - 14f, orbX + 18f, orbY - 36f, 5.5f)
                sr.rectLine(orbX + 18f, orbY - 36f, orbX + 46f, orbY - 68f, 5.5f)
                sr.color = Color.WHITE
                sr.rectLine(orbX, orbY, orbX + 28f, orbY - 14f, 2.2f)
                sr.rectLine(orbX + 28f, orbY - 14f, orbX + 18f, orbY - 36f, 2.2f)
                sr.rectLine(orbX + 18f, orbY - 36f, orbX + 46f, orbY - 68f, 2.2f)
            }

            if (entry.soldierRank != null) {
                drawRenderedSoldier(sr, entry.soldierRank, towerCx - 28f, inY + 26f, scale = 1.25f)
                drawRenderedSoldier(sr, entry.soldierRank, towerCx + 6f, inY + 24f, scale = 1.25f)
                if (entry.specialization == TowerSpecialization.BARBARIAN_MEAD_HALL) {
                    drawRenderedEnemy(sr, EnemyType.TROLL, towerCx + 42f, inY + 28f, scale = 1.25f)
                } else {
                    drawRenderedEnemy(sr, EnemyType.ORC, towerCx - 50f, inY + 24f, scale = 1.15f)
                }
            }
        }
        sr.end()

        batch.begin()
        val oldSmallScaleX = smallFont.data.scaleX
        val oldSmallScaleY = smallFont.data.scaleY

        for (idx in entries.indices) {
            val entry = entries[idx]
            val curScrollY = topScrollY - idx * (cardH + gap)
            val textX = scrollX + 235f
            val topTextY = curScrollY + cardH - 30f

            smallFont.data.setScale(0.92f)
            smallFont.color = OUTLINE
            smallFont.draw(batch, entry.subtitle, textX, topTextY)

            smallFont.data.setScale(0.92f)
            smallFont.color = Color(0.68f, 0.22f, 0.10f, 1f)
            val dLines = wrapTextLines(entry.description, 34)
            var dy = topTextY - 66f
            for (line in dLines) {
                smallFont.draw(batch, line, textX, dy)
                dy -= 19f
            }
        }

        smallFont.data.setScale(oldSmallScaleX, oldSmallScaleY)
        batch.end()
    }

    private fun renderNewEnemyPopup(
        sr: ShapeRenderer,
        batch: SpriteBatch,
        font: BitmapFont,
        smallFont: BitmapFont,
        enemyType: EnemyType
    ) {
        val info = getEnemyCardDetails(enemyType)
        val cX = Config.WORLD_WIDTH / 2f
        val cY = Config.WORLD_HEIGHT / 2f + 35f
        val scrollW = 680f
        val scrollH = 265f
        val scrollX = cX - scrollW / 2f
        val scrollY = cY - scrollH / 2f

        val okBtnW = 175f
        val okBtnH = 58f
        val okBtnX = cX + 95f
        val okBtnY = scrollY - 58f

        val bannerW = 430f
        val bannerH = 62f
        val bannerX = cX - bannerW / 2f
        val bannerY = 68f

        sr.begin(ShapeRenderer.ShapeType.Filled)
        sr.color = Color(0f, 0f, 0f, 0.40f)
        sr.rect(0f, 0f, Config.WORLD_WIDTH, Config.WORLD_HEIGHT)

        drawTabButton(sr, okBtnX, okBtnY, okBtnW, okBtnH, Color(0.16f, 0.19f, 0.22f, 1f))

        val tabW = 360f
        val tabH = 64f
        val tabX = scrollX + 42f
        val tabY = scrollY + scrollH - 12f
        sr.color = OUTLINE
        sr.rect(tabX - 3f, tabY - 3f, tabW + 6f, tabH + 6f)
        sr.color = Color(0.48f, 0.20f, 0.08f, 1f)
        sr.rect(tabX, tabY, tabW, tabH)

        drawJaggedParchment(sr, scrollX, scrollY, scrollW, scrollH)

        ComicFontRenderer.drawPlaqueText(sr, "NEW ENEMY!", tabX + tabW / 2f, tabY + tabH / 2f + 2f, 23f, Color(0.96f, 0.92f, 0.72f, 1f))
        ComicFontRenderer.drawPlaqueText(sr, "OK!", okBtnX + okBtnW / 2f, okBtnY + okBtnH / 2f, 22f, Color.WHITE)
        ComicFontRenderer.drawRibbonBannerText(sr, info.title, scrollX + 440f, scrollY + scrollH - 42f, 21f)

        val picX = scrollX + 26f
        val picY = scrollY + 25f
        val picW = 225f
        val picH = 215f
        sr.color = Color(0f, 0f, 0f, 0.25f)
        sr.rect(picX + 5f, picY - 5f, picW, picH)
        sr.color = Color(0.68f, 0.60f, 0.42f, 1f)
        sr.rect(picX - 3f, picY - 3f, picW + 6f, picH + 6f)
        sr.color = Color(0.95f, 0.90f, 0.72f, 1f)
        sr.rect(picX, picY, picW, picH)

        val innerX = picX + 12f
        val innerY = picY + 12f
        val innerW = picW - 24f
        val innerH = picH - 24f

        val isSnow = enemyType in listOf(EnemyType.TROLL, EnemyType.WINTER_WOLF, EnemyType.TROLL_CHAMPION, EnemyType.TROLL_CHIEFTAIN, EnemyType.YETI, EnemyType.ROCKET_RIDER, EnemyType.JT)
        val isWastes = enemyType in listOf(EnemyType.SKELETON, EnemyType.SKELETON_KNIGHT, EnemyType.NECROMANCER, EnemyType.DEMON_SPAWN, EnemyType.DEMON_LORD, EnemyType.DEMON_HOUND, EnemyType.DEMON_IMP, EnemyType.MAGMA_ELEMENTAL, EnemyType.VEZNAN, EnemyType.VEZNAN_DEMON)

        sr.color = when {
            isSnow -> Color(0.75f, 0.82f, 0.90f, 1f)
            isWastes -> Color(0.28f, 0.26f, 0.30f, 1f)
            else -> Color(0.42f, 0.62f, 0.18f, 1f)
        }
        sr.rect(innerX, innerY, innerW, innerH)

        sr.color = when {
            isSnow -> Color(0.58f, 0.68f, 0.78f, 1f)
            isWastes -> Color(0.18f, 0.16f, 0.20f, 1f)
            else -> Color(0.26f, 0.46f, 0.12f, 1f)
        }
        sr.circle(innerX + 35f, innerY + 155f, 28f)
        sr.circle(innerX + 85f, innerY + 165f, 32f)
        sr.circle(innerX + 165f, innerY + 155f, 30f)

        sr.color = when {
            isSnow -> Color(0.88f, 0.92f, 0.96f, 1f)
            isWastes -> Color(0.52f, 0.50f, 0.54f, 1f)
            else -> Color(0.84f, 0.74f, 0.46f, 1f)
        }
        sr.rect(innerX, innerY, innerW, innerH * 0.62f)

        when (enemyType) {
            EnemyType.GOBLIN -> {
                for (gp in listOf(
                    Vector2(innerX + 42f, innerY + 62f), Vector2(innerX + 82f, innerY + 86f),
                    Vector2(innerX + 68f, innerY + 38f), Vector2(innerX + 122f, innerY + 36f),
                    Vector2(innerX + 162f, innerY + 66f)
                )) drawRenderedEnemy(sr, EnemyType.GOBLIN, gp.x, gp.y, scale = 1.45f)
            }
            EnemyType.ORC -> {
                drawRenderedEnemy(sr, EnemyType.ORC, innerX + 38f, innerY + 58f, scale = 1.45f)
                drawRenderedEnemy(sr, EnemyType.ORC, innerX + 82f, innerY + 92f, scale = 1.45f)
                drawRenderedEnemy(sr, EnemyType.ORC, innerX + 115f, innerY + 46f, scale = 1.45f)
                drawRenderedSoldier(sr, "Militia", innerX + 125f, innerY + 102f, scale = 1.45f)
                drawRenderedSoldier(sr, "Militia", innerX + 155f, innerY + 46f, scale = 1.45f)
            }
            EnemyType.SHAMAN -> {
                drawRenderedEnemy(sr, EnemyType.SHAMAN, innerX + 42f, innerY + 68f, scale = 1.45f)
                drawRenderedEnemy(sr, EnemyType.SHAMAN, innerX + 96f, innerY + 95f, scale = 1.45f)
                for (hpPos in listOf(Vector2(innerX + 105f, innerY + 46f), Vector2(innerX + 162f, innerY + 56f))) {
                    sr.color = Color(1f, 0.62f, 0.12f, 0.65f)
                    sr.circle(hpPos.x, hpPos.y, 20f)
                    drawRenderedEnemy(sr, EnemyType.ORC, hpPos.x, hpPos.y, scale = 1.35f)
                    sr.color = Color(1f, 0.92f, 0.35f, 0.9f)
                    sr.rect(hpPos.x - 6f, hpPos.y - 2f, 12f, 4f)
                    sr.rect(hpPos.x - 2f, hpPos.y - 6f, 4f, 12f)
                }
            }
            EnemyType.BANDIT -> {
                drawRenderedEnemy(sr, EnemyType.BANDIT, innerX + 40f, innerY + 62f, scale = 1.45f)
                drawRenderedEnemy(sr, EnemyType.BANDIT, innerX + 85f, innerY + 98f, scale = 1.45f)
                drawRenderedEnemy(sr, EnemyType.BANDIT, innerX + 128f, innerY + 64f, scale = 1.45f)
                drawRenderedSoldier(sr, "Militia", innerX + 122f, innerY + 102f, scale = 1.40f)
                drawRenderedSoldier(sr, "Militia", innerX + 164f, innerY + 68f, scale = 1.40f)
            }
            EnemyType.BRIGAND -> {
                drawRenderedTower(sr, TowerType.MAGE, 2, innerX + 95f, innerY + 158f, scale = 1.05f)
                sr.color = Color(0.25f, 0.88f, 1f, 0.85f)
                sr.circle(innerX + 58f, innerY + 122f, 11f)
                sr.color = Color.WHITE
                sr.circle(innerX + 58f, innerY + 122f, 5.5f)
                drawRenderedEnemy(sr, EnemyType.BRIGAND, innerX + 42f, innerY + 75f, scale = 1.45f)
                drawRenderedEnemy(sr, EnemyType.BRIGAND, innerX + 104f, innerY + 52f, scale = 1.45f)
                drawRenderedEnemy(sr, EnemyType.BRIGAND, innerX + 162f, innerY + 95f, scale = 1.45f)
            }
            EnemyType.GIANT_SPIDER -> {
                sr.color = Color(0.92f, 0.95f, 0.98f, 0.85f)
                sr.rectLine(innerX + 5f, innerY + 175f, innerX + 55f, innerY + 145f, 2f)
                sr.rectLine(innerX + 5f, innerY + 150f, innerX + 50f, innerY + 168f, 2f)
                drawRenderedEnemy(sr, EnemyType.GIANT_SPIDER, innerX + 35f, innerY + 112f, scale = 1.35f)
                drawRenderedEnemy(sr, EnemyType.GIANT_SPIDER, innerX + 42f, innerY + 54f, scale = 1.35f)
                drawRenderedEnemy(sr, EnemyType.GIANT_SPIDER, innerX + 92f, innerY + 78f, scale = 1.35f)
                drawRenderedEnemy(sr, EnemyType.GIANT_SPIDER, innerX + 122f, innerY + 108f, scale = 1.35f)
                drawRenderedEnemy(sr, EnemyType.GIANT_SPIDER, innerX + 156f, innerY + 52f, scale = 1.35f)
            }
            EnemyType.MARAUDER -> {
                drawRenderedEnemy(sr, EnemyType.MARAUDER, innerX + 48f, innerY + 86f, scale = 1.45f)
                drawRenderedEnemy(sr, EnemyType.MARAUDER, innerX + 92f, innerY + 46f, scale = 1.45f)
                drawRenderedEnemy(sr, EnemyType.MARAUDER, innerX + 135f, innerY + 96f, scale = 1.45f)
                drawRenderedEnemy(sr, EnemyType.BRIGAND, innerX + 172f, innerY + 92f, scale = 1.25f)
            }
            EnemyType.SPIDER_MATRIARCH -> {
                drawRenderedEgg(sr, innerX + 38f, innerY + 76f, scale = 1.15f)
                drawRenderedEgg(sr, innerX + 36f, innerY + 34f, scale = 1.15f)
                drawRenderedEgg(sr, innerX + 64f, innerY + 56f, scale = 1.15f)
                drawRenderedEnemy(sr, EnemyType.SPIDER_MATRIARCH, innerX + 115f, innerY + 72f, scale = 1.65f)
                drawRenderedEnemy(sr, EnemyType.SPIDER_HATCHLING, innerX + 86f, innerY + 32f, scale = 1.35f)
                drawRenderedEnemy(sr, EnemyType.SPIDER_HATCHLING, innerX + 148f, innerY + 98f, scale = 1.35f)
                drawRenderedEnemy(sr, EnemyType.SPIDER_HATCHLING, innerX + 162f, innerY + 42f, scale = 1.35f)
            }
            EnemyType.DARK_KNIGHT -> {
                drawRenderedEnemy(sr, EnemyType.DARK_KNIGHT, innerX + 45f, innerY + 62f, scale = 1.45f)
                drawRenderedEnemy(sr, EnemyType.DARK_KNIGHT, innerX + 95f, innerY + 105f, scale = 1.45f)
                drawRenderedEnemy(sr, EnemyType.DARK_KNIGHT, innerX + 115f, innerY + 52f, scale = 1.45f)
                drawRenderedSoldier(sr, "Knight", innerX + 152f, innerY + 52f, scale = 1.40f)
            }
            EnemyType.GARGOYLE -> {
                drawRenderedSoldier(sr, "Knight", innerX + 72f, innerY + 36f, scale = 1.30f)
                drawRenderedSoldier(sr, "Knight", innerX + 106f, innerY + 44f, scale = 1.30f)
                drawRenderedSoldier(sr, "Knight", innerX + 138f, innerY + 38f, scale = 1.30f)
                drawRenderedEnemy(sr, EnemyType.GARGOYLE, innerX + 52f, innerY + 112f, scale = 1.35f)
                drawRenderedEnemy(sr, EnemyType.GARGOYLE, innerX + 98f, innerY + 95f, scale = 1.35f)
                drawRenderedEnemy(sr, EnemyType.GARGOYLE, innerX + 152f, innerY + 118f, scale = 1.35f)
            }
            EnemyType.TROLL -> {
                drawRenderedEnemy(sr, EnemyType.TROLL, innerX + 52f, innerY + 86f, scale = 1.45f)
                drawRenderedEnemy(sr, EnemyType.TROLL, innerX + 95f, innerY + 46f, scale = 1.45f)
                drawRenderedEnemy(sr, EnemyType.TROLL, innerX + 138f, innerY + 105f, scale = 1.45f)
            }
            EnemyType.WINTER_WOLF -> {
                drawRenderedEnemy(sr, EnemyType.WINTER_WOLF, innerX + 48f, innerY + 86f, scale = 1.40f)
                drawRenderedEnemy(sr, EnemyType.WINTER_WOLF, innerX + 105f, innerY + 46f, scale = 1.40f)
                drawRenderedEnemy(sr, EnemyType.WINTER_WOLF, innerX + 148f, innerY + 78f, scale = 1.40f)
            }
            EnemyType.TROLL_CHAMPION -> {
                drawRenderedEnemy(sr, EnemyType.TROLL_CHAMPION, innerX + 48f, innerY + 88f, scale = 1.40f)
                drawRenderedEnemy(sr, EnemyType.TROLL_CHAMPION, innerX + 78f, innerY + 42f, scale = 1.40f)
                drawRenderedEnemy(sr, EnemyType.TROLL_CHAMPION, innerX + 128f, innerY + 82f, scale = 1.40f)
                for (axePos in listOf(Vector2(innerX + 102f, innerY + 135f), Vector2(innerX + 158f, innerY + 122f))) {
                    sr.color = OUTLINE
                    sr.rectLine(axePos.x - 6f, axePos.y - 6f, axePos.x + 6f, axePos.y + 6f, 3.5f)
                    sr.color = Color(0.82f, 0.86f, 0.90f, 1f)
                    sr.circle(axePos.x - 4f, axePos.y + 4f, 5.5f)
                }
            }
            EnemyType.YETI -> drawRenderedEnemy(sr, EnemyType.YETI, innerX + innerW / 2f, innerY + 66f, scale = 2.25f)
            EnemyType.TROLL_CHIEFTAIN -> {
                for (mp in listOf(Vector2(innerX + 38f, innerY + 56f), Vector2(innerX + 132f, innerY + 108f), Vector2(innerX + 156f, innerY + 54f))) {
                    sr.color = Color(0.95f, 0.28f, 0.10f, 0.65f)
                    sr.ellipse(mp.x - 20f, mp.y - 18f, 40f, 15f)
                    drawRenderedEnemy(sr, EnemyType.TROLL, mp.x, mp.y, scale = 1.25f)
                }
                drawRenderedEnemy(sr, EnemyType.TROLL_CHIEFTAIN, innerX + 86f, innerY + 78f, scale = 1.55f)
            }
            EnemyType.DARK_SLAYER -> {
                drawRenderedEnemy(sr, EnemyType.DARK_SLAYER, innerX + 46f, innerY + 56f, scale = 1.45f)
                drawRenderedEnemy(sr, EnemyType.DARK_SLAYER, innerX + 98f, innerY + 96f, scale = 1.45f)
                drawRenderedSoldier(sr, "Barbarian", innerX + 156f, innerY + 88f, scale = 1.35f)
            }
            EnemyType.ROCKET_RIDER -> {
                for (rp in listOf(Vector2(innerX + 42f, innerY + 122f), Vector2(innerX + 42f, innerY + 78f), Vector2(innerX + 92f, innerY + 104f), Vector2(innerX + 148f, innerY + 96f))) {
                    sr.color = Color(0f, 0f, 0f, 0.26f)
                    sr.ellipse(rp.x - 10f, rp.y - 34f, 20f, 7f)
                    drawRenderedEnemy(sr, EnemyType.ROCKET_RIDER, rp.x, rp.y, scale = 1.25f)
                }
            }
            EnemyType.DEMON_SPAWN -> {
                drawRenderedEnemy(sr, EnemyType.DEMON_SPAWN, innerX + 42f, innerY + 56f, scale = 1.40f)
                drawRenderedEnemy(sr, EnemyType.DEMON_SPAWN, innerX + 68f, innerY + 108f, scale = 1.40f)
                drawRenderedEnemy(sr, EnemyType.DEMON_SPAWN, innerX + 135f, innerY + 108f, scale = 1.40f)
                drawRenderedSoldier(sr, "Knight", innerX + 168f, innerY + 108f, scale = 1.35f)
                drawRenderedSoldier(sr, "Knight", innerX + 138f, innerY + 58f, scale = 1.35f)
                val expX = innerX + 102f; val expY = innerY + 56f
                sr.color = Color(0.95f, 0.28f, 0.06f, 0.90f); sr.circle(expX, expY, 22f)
                sr.color = Color(1f, 0.62f, 0.12f, 0.95f); sr.circle(expX, expY, 15f)
                sr.color = Color(1f, 0.92f, 0.28f, 1f); sr.circle(expX, expY, 8f)
            }
            EnemyType.DEMON_LORD -> {
                for (sp in listOf(Vector2(innerX + 42f, innerY + 76f), Vector2(innerX + 115f, innerY + 44f), Vector2(innerX + 158f, innerY + 102f))) {
                    sr.color = Color(0.98f, 0.35f, 0.08f, 0.65f); sr.circle(sp.x, sp.y, 24f)
                    sr.color = Color(1f, 0.72f, 0.18f, 0.45f); sr.circle(sp.x, sp.y, 19f)
                    drawRenderedEnemy(sr, EnemyType.DEMON_SPAWN, sp.x, sp.y, scale = 1.25f)
                }
                drawRenderedEnemy(sr, EnemyType.DEMON_LORD, innerX + 94f, innerY + 96f, scale = 1.55f)
                sr.color = Color(1f, 0.52f, 0.10f, 0.85f); sr.circle(innerX + 74f, innerY + 114f, 11f)
                sr.color = Color(1f, 0.92f, 0.35f, 1f); sr.circle(innerX + 74f, innerY + 114f, 6f)
            }
            EnemyType.DEMON_HOUND -> {
                drawRenderedEnemy(sr, EnemyType.DEMON_HOUND, innerX + 42f, innerY + 95f, scale = 1.40f)
                drawRenderedEnemy(sr, EnemyType.DEMON_HOUND, innerX + 86f, innerY + 52f, scale = 1.40f)
                drawRenderedEnemy(sr, EnemyType.DEMON_HOUND, innerX + 148f, innerY + 86f, scale = 1.40f)
            }
            EnemyType.DEMON_IMP -> {
                for (ip in listOf(Vector2(innerX + 42f, innerY + 112f), Vector2(innerX + 95f, innerY + 66f), Vector2(innerX + 152f, innerY + 104f))) {
                    sr.color = Color(0f, 0f, 0f, 0.28f); sr.ellipse(ip.x - 10f, ip.y - 30f, 20f, 7f)
                    drawRenderedEnemy(sr, EnemyType.DEMON_IMP, ip.x, ip.y, scale = 1.35f)
                }
            }
            EnemyType.NECROMANCER -> {
                for (mp in listOf(Vector2(innerX + 34f, innerY + 78f), Vector2(innerX + 58f, innerY + 118f), Vector2(innerX + 95f, innerY + 42f), Vector2(innerX + 152f, innerY + 112f), Vector2(innerX + 158f, innerY + 64f))) {
                    sr.color = Color(0.24f, 0.22f, 0.24f, 1f); sr.ellipse(mp.x - 18f, mp.y - 12f, 36f, 16f)
                    drawRenderedEnemy(sr, EnemyType.SKELETON, mp.x, mp.y + 4f, scale = 1.15f)
                }
                val necX = innerX + 96f; val necY = innerY + 92f
                drawRenderedEnemy(sr, EnemyType.NECROMANCER, necX, necY, scale = 1.55f)
                for (side in listOf(-18f, 18f)) {
                    sr.color = Color(0.28f, 0.98f, 0.20f, 0.75f); sr.circle(necX + side, necY + 2f, 10f)
                    sr.color = Color(0.82f, 1f, 0.65f, 0.95f); sr.circle(necX + side, necY + 2f, 5f)
                }
            }
            EnemyType.MAGMA_ELEMENTAL -> drawRenderedEnemy(sr, EnemyType.MAGMA_ELEMENTAL, innerX + innerW / 2f, innerY + 66f, scale = 2.25f)
            else -> drawRenderedEnemy(sr, enemyType, innerX + innerW / 2f, innerY + 65f, scale = 2.15f)
        }

        if (info.bottomTip1.isNotEmpty()) {
            sr.color = Color(0f, 0f, 0f, 0.32f)
            sr.rect(bannerX + 4f, bannerY - 4f, bannerW, bannerH)
            sr.color = OUTLINE
            sr.rect(bannerX - 3f, bannerY - 3f, bannerW + 6f, bannerH + 6f)
            sr.color = Color(0.98f, 0.78f, 0.08f, 1f)
            sr.rect(bannerX, bannerY, bannerW, bannerH)

            val excX = bannerX + 38f
            val excY = bannerY + bannerH / 2f
            sr.color = OUTLINE
            sr.circle(excX, excY, 21f)
            sr.color = Color(0.98f, 0.84f, 0.18f, 1f)
            sr.circle(excX, excY, 17.5f)
            sr.color = OUTLINE
            sr.rect(excX - 2.5f, excY - 2f, 5f, 13f)
            sr.circle(excX, excY - 8.5f, 2.8f)
        }
        sr.end()

        batch.begin()
        val oldSmallScaleX = smallFont.data.scaleX
        val oldSmallScaleY = smallFont.data.scaleY

        val textX = scrollX + 275f
        smallFont.data.setScale(0.90f)
        smallFont.color = OUTLINE
        val descLines = wrapTextLines(info.description, 35)
        var curY = scrollY + scrollH - 72f
        val lineStep = if (descLines.size >= 4 || info.bullets.size >= 4) 17.5f else 19f
        for (line in descLines) {
            smallFont.draw(batch, line, textX, curY)
            curY -= lineStep
        }

        curY -= if (info.bullets.size >= 4) 6f else 10f
        smallFont.data.setScale(0.95f)
        val bulletStep = if (info.bullets.size >= 4) 19f else 21f
        for (b in info.bullets) {
            smallFont.color = OUTLINE
            smallFont.draw(batch, "-", textX, curY)
            smallFont.color = Color(0.68f, 0.22f, 0.10f, 1f)
            smallFont.draw(batch, b, textX + 18f, curY)
            curY -= bulletStep
        }

        if (info.footnote.isNotEmpty()) {
            curY -= 4f
            smallFont.data.setScale(0.76f)
            smallFont.color = Color(0.28f, 0.24f, 0.18f, 1f)
            val fnLines = wrapTextLines(info.footnote, 42)
            for (fn in fnLines) {
                smallFont.draw(batch, fn, textX, curY)
                curY -= 16f
            }
        }

        if (info.bottomTip1.isNotEmpty()) {
            smallFont.data.setScale(0.88f)
            smallFont.color = OUTLINE
            if (info.bottomTip2.isEmpty()) {
                smallFont.draw(batch, info.bottomTip1, bannerX + 74f, bannerY + 36f)
            } else {
                smallFont.draw(batch, info.bottomTip1, bannerX + 74f, bannerY + 42f)
                smallFont.draw(batch, info.bottomTip2, bannerX + 74f, bannerY + 22f)
            }
        }

        smallFont.data.setScale(oldSmallScaleX, oldSmallScaleY)
        batch.end()
    }

    private fun renderHintPopup(
        sr: ShapeRenderer,
        batch: SpriteBatch,
        font: BitmapFont,
        smallFont: BitmapFont,
        hintType: HintPopupType
    ) {
        val cX = Config.WORLD_WIDTH / 2f
        val cY = Config.WORLD_HEIGHT / 2f + 22f
        val scrollW = 660f
        val scrollH = 390f
        val scrollX = cX - scrollW / 2f
        val scrollY = cY - scrollH / 2f

        val okBtnW = 175f
        val okBtnH = 58f
        val okBtnX = cX + 85f
        val okBtnY = scrollY - 58f

        val tabW = 330f
        val tabH = 66f
        val tabX = cX - tabW / 2f
        val tabY = scrollY + scrollH - 12f

        sr.begin(ShapeRenderer.ShapeType.Filled)
        sr.color = Color(0f, 0f, 0f, 0.40f)
        sr.rect(0f, 0f, Config.WORLD_WIDTH, Config.WORLD_HEIGHT)

        drawTabButton(sr, okBtnX, okBtnY, okBtnW, okBtnH, Color(0.16f, 0.19f, 0.22f, 1f))

        sr.color = OUTLINE
        sr.rect(tabX - 3f, tabY - 3f, tabW + 6f, tabH + 6f)
        sr.color = Color(0.14f, 0.40f, 0.62f, 1f)
        sr.rect(tabX, tabY, tabW, tabH)

        drawJaggedParchment(sr, scrollX, scrollY, scrollW, scrollH)

        ComicFontRenderer.drawPlaqueText(sr, "HINT", cX, tabY + tabH / 2f + 2f, 26f, Color(0.96f, 0.92f, 0.72f, 1f))
        ComicFontRenderer.drawPlaqueText(sr, "OK!", okBtnX + okBtnW / 2f, okBtnY + okBtnH / 2f, 22f, Color.WHITE)

        when (hintType) {
            HintPopupType.ARMORED_ENEMIES -> {
                ComicFontRenderer.drawRibbonBannerText(sr, "ARMORED ENEMIES!", scrollX + 355f, scrollY + scrollH - 48f, 20f)
                drawArmorShieldIcon(sr, scrollX + 92f, scrollY + scrollH - 82f, 1.85f, isMagic = false)

                val diagramY = scrollY + 138f
                drawRenderedEnemy(sr, EnemyType.ORC, scrollX + 125f, diagramY, scale = 1.95f)

                val arrowStart = Vector2(scrollX + 172f, diagramY + 8f)
                val arrowMid = Vector2(scrollX + 220f, diagramY + 15f)
                val arrowEnd = Vector2(scrollX + 266f, diagramY + 4f)
                sr.color = Color(0.76f, 0.22f, 0.08f, 1f)
                sr.rectLine(arrowStart, arrowMid, 4.5f)
                sr.rectLine(arrowMid, arrowEnd, 4.5f)
                sr.triangle(arrowEnd.x + 8f, arrowEnd.y - 2f, arrowEnd.x - 10f, arrowEnd.y + 10f, arrowEnd.x - 8f, arrowEnd.y - 10f)

                drawRenderedTower(sr, TowerType.ARCHER, 1, scrollX + 345f, diagramY - 2f, scale = 1.25f)
                drawRenderedTower(sr, TowerType.BARRACKS, 1, scrollX + 435f, diagramY - 2f, scale = 1.25f)
                drawRenderedSoldier(sr, "Militia", scrollX + 458f, diagramY - 20f, scale = 1.20f)
                drawRenderedTower(sr, TowerType.ARTILLERY, 1, scrollX + 532f, diagramY - 2f, scale = 1.25f)
            }

            HintPopupType.MAGIC_RESISTANT_ENEMIES -> {
                ComicFontRenderer.drawRibbonBannerText(sr, "MAGIC RESISTANT ENEMIES!", scrollX + 395f, scrollY + scrollH - 48f, 18f)
                drawArmorShieldIcon(sr, scrollX + 92f, scrollY + scrollH - 82f, 1.85f, isMagic = true)

                val diagramY = scrollY + 142f
                drawRenderedEnemy(sr, EnemyType.SHAMAN, scrollX + 205f, diagramY, scale = 1.95f)

                val arrowStart = Vector2(scrollX + 252f, diagramY + 8f)
                val arrowMid = Vector2(scrollX + 305f, diagramY + 15f)
                val arrowEnd = Vector2(scrollX + 355f, diagramY + 4f)
                sr.color = Color(0.76f, 0.22f, 0.08f, 1f)
                sr.rectLine(arrowStart, arrowMid, 4.5f)
                sr.rectLine(arrowMid, arrowEnd, 4.5f)
                sr.triangle(arrowEnd.x + 8f, arrowEnd.y - 2f, arrowEnd.x - 10f, arrowEnd.y + 10f, arrowEnd.x - 8f, arrowEnd.y - 10f)

                drawRenderedTower(sr, TowerType.MAGE, 1, scrollX + 425f, diagramY - 2f, scale = 1.45f)
            }

            HintPopupType.STRATEGY_BASICS -> {
                ComicFontRenderer.drawRibbonBannerText(sr, "STRATEGY BASICS!", cX, scrollY + scrollH - 42f, 22f)
                val mapW = 540f
                val mapH = 210f
                val mapX = cX - mapW / 2f
                val mapY = scrollY + 24f

                sr.color = Color(0.75f, 0.68f, 0.46f, 1f)
                sr.rect(mapX - 3f, mapY - 3f, mapW + 6f, mapH + 6f)
                sr.color = Color(0.54f, 0.72f, 0.22f, 1f)
                sr.rect(mapX, mapY, mapW, mapH)

                val rEdge = Color(0.62f, 0.52f, 0.30f, 1f)
                val rFill = Color(0.84f, 0.75f, 0.48f, 1f)
                val p1 = Vector2(mapX + 10f, mapY + 75f)
                val p2 = Vector2(mapX + 215f, mapY + 105f)
                val p3 = Vector2(mapX + 275f, mapY + 195f)
                val p4 = Vector2(mapX + 445f, mapY + 195f)
                val p5 = Vector2(mapX + 465f, mapY + 75f)
                sr.color = rEdge
                sr.rectLine(p1, p2, 62f); sr.rectLine(p2, p3, 62f); sr.rectLine(p4, p5, 62f)
                sr.color = rFill
                sr.rectLine(p1, p2, 54f); sr.rectLine(p2, p3, 54f); sr.rectLine(p4, p5, 54f)

                val magePos = Vector2(mapX + 165f, mapY + 152f)
                val archPos = Vector2(mapX + 355f, mapY + 158f)
                val artPos = Vector2(mapX + 115f, mapY + 38f)
                val barrPos = Vector2(mapX + 355f, mapY + 98f)

                for (tp in listOf(magePos, archPos, artPos)) {
                    sr.color = Color(0.86f, 0.22f, 0.12f, 0.85f)
                    sr.ellipse(tp.x - 36f, tp.y - 18f, 72f, 32f)
                    sr.color = Color(0.54f, 0.72f, 0.22f, 1f)
                    sr.ellipse(tp.x - 31f, tp.y - 15f, 62f, 26f)
                }

                drawRenderedTower(sr, TowerType.MAGE, 1, magePos.x, magePos.y, scale = 0.95f)
                drawRenderedTower(sr, TowerType.ARCHER, 1, archPos.x, archPos.y, scale = 0.95f)
                drawRenderedTower(sr, TowerType.BARRACKS, 1, barrPos.x, barrPos.y, scale = 0.95f)
                drawRenderedTower(sr, TowerType.ARTILLERY, 1, artPos.x, artPos.y, scale = 0.95f)

                val chokeX = mapX + 235f
                val chokeY = mapY + 102f
                drawRenderedSoldier(sr, "Militia", chokeX - 8f, chokeY + 6f, scale = 1.05f)
                drawRenderedSoldier(sr, "Militia", chokeX + 14f, chokeY - 4f, scale = 1.05f)
                drawRenderedSoldier(sr, "Militia", chokeX - 18f, chokeY - 16f, scale = 1.05f)
                drawRenderedEnemy(sr, EnemyType.GOBLIN, chokeX - 24f, chokeY + 6f, scale = 1.05f)
                drawRenderedEnemy(sr, EnemyType.ORC, chokeX + 28f, chokeY - 2f, scale = 1.05f)
                drawRenderedEnemy(sr, EnemyType.GOBLIN, chokeX - 4f, chokeY - 18f, scale = 1.05f)

                sr.color = Color(0.86f, 0.22f, 0.10f, 1f)
                sr.rectLine(magePos.x + 18f, magePos.y + 18f, chokeX - 6f, chokeY + 24f, 3.8f)
                sr.rectLine(archPos.x - 24f, archPos.y + 12f, chokeX + 16f, chokeY + 22f, 3.8f)
                sr.rectLine(artPos.x + 18f, artPos.y + 24f, chokeX - 28f, chokeY - 12f, 3.8f)

                val noteX = mapX + 235f
                val noteY = mapY + 10f
                val noteW = 195f
                val noteH = 62f
                sr.color = OUTLINE
                sr.rect(noteX - 2f, noteY - 2f, noteW + 4f, noteH + 4f)
                sr.triangle(noteX + 35f, noteY + noteH, noteX + 52f, noteY + noteH, chokeX + 12f, chokeY - 10f)
                sr.color = Color(0.97f, 0.95f, 0.86f, 1f)
                sr.rect(noteX, noteY, noteW, noteH)
            }

            HintPopupType.HEAVILY_ARMORED_ENEMIES -> {
                ComicFontRenderer.drawRibbonBannerText(sr, "HEAVILY ARMORED ENEMIES!", cX, scrollY + scrollH - 42f, 20f)
                val mapW = 520f
                val mapH = 205f
                val mapX = cX - mapW / 2f
                val mapY = scrollY + 24f

                sr.color = Color(0.75f, 0.68f, 0.46f, 1f)
                sr.rect(mapX - 3f, mapY - 3f, mapW + 6f, mapH + 6f)
                sr.color = Color(0.84f, 0.75f, 0.48f, 1f)
                sr.rect(mapX, mapY, mapW, mapH)

                sr.color = Color(0.46f, 0.66f, 0.18f, 1f)
                sr.rect(mapX, mapY, 175f, 118f)
                sr.rect(mapX + mapW - 185f, mapY, 185f, 128f)

                val wizPos = Vector2(mapX + 105f, mapY + 86f)
                val arcPos = Vector2(mapX + mapW - 105f, mapY + 86f)

                drawRenderedTower(sr, TowerType.MAGE, 3, wizPos.x, wizPos.y, scale = 1.45f)
                drawRenderedTower(
                    sr = sr,
                    type = TowerType.MAGE,
                    level = 4,
                    cx = arcPos.x,
                    cy = arcPos.y,
                    scale = 1.45f,
                    specialization = TowerSpecialization.ARCANE_WIZARD
                )

                val dk1 = Vector2(mapX + 235f, mapY + 138f)
                val dk2 = Vector2(mapX + 252f, mapY + 95f)
                drawRenderedEnemy(sr, EnemyType.DARK_KNIGHT, dk1.x, dk1.y, scale = 1.35f)
                drawRenderedEnemy(sr, EnemyType.DARK_KNIGHT, dk2.x, dk2.y, scale = 1.35f)

                sr.color = Color(0.25f, 0.88f, 1f, 0.85f)
                sr.circle(mapX + 188f, mapY + 135f, 10f)
                sr.color = Color.WHITE
                sr.circle(mapX + 188f, mapY + 135f, 5f)

                sr.color = Color(0.92f, 0.25f, 1f, 0.90f)
                sr.rectLine(arcPos.x, arcPos.y + 26f, dk2.x + 6f, dk2.y, 6f)
                sr.color = Color.WHITE
                sr.rectLine(arcPos.x, arcPos.y + 26f, dk2.x + 6f, dk2.y, 2.2f)

                val noteW = 205f
                val noteH = 62f
                val noteX = cX - noteW / 2f
                val noteY = mapY + 6f
                sr.color = OUTLINE
                sr.rect(noteX - 2f, noteY - 2f, noteW + 4f, noteH + 4f)
                sr.triangle(noteX + 18f, noteY + noteH, noteX + 34f, noteY + noteH, wizPos.x + 38f, wizPos.y - 8f)
                sr.triangle(noteX + noteW - 34f, noteY + noteH, noteX + noteW - 18f, noteY + noteH, arcPos.x - 38f, arcPos.y - 8f)
                sr.color = Color(0.98f, 0.96f, 0.90f, 1f)
                sr.rect(noteX, noteY, noteW, noteH)
            }

            else -> {
                ComicFontRenderer.drawRibbonBannerText(sr, "COMMAND YOUR TROOPS!", scrollX + 365f, scrollY + scrollH - 48f, 19f)
                drawBlueRallyFlagIcon(sr, scrollX + 88f, scrollY + scrollH - 86f, 1.85f)

                val wheelCx = scrollX + 148f
                val wheelCy = scrollY + 135f
                sr.color = OUTLINE
                for (a in 0 until 360 step 15) {
                    val r1 = Math.toRadians(a.toDouble())
                    val r2 = Math.toRadians((a + 15).toDouble())
                    sr.rectLine(
                        wheelCx + (62f * Math.cos(r1)).toFloat(), wheelCy + (62f * Math.sin(r1)).toFloat(),
                        wheelCx + (62f * Math.cos(r2)).toFloat(), wheelCy + (62f * Math.sin(r2)).toFloat(),
                        7f
                    )
                }
                sr.color = Color(0.55f, 0.58f, 0.62f, 1f)
                for (a in 0 until 360 step 15) {
                    val r1 = Math.toRadians(a.toDouble())
                    val r2 = Math.toRadians((a + 15).toDouble())
                    sr.rectLine(
                        wheelCx + (62f * Math.cos(r1)).toFloat(), wheelCy + (62f * Math.sin(r1)).toFloat(),
                        wheelCx + (62f * Math.cos(r2)).toFloat(), wheelCy + (62f * Math.sin(r2)).toFloat(),
                        3.5f
                    )
                }

                drawRenderedTower(sr, TowerType.BARRACKS, 1, wheelCx, wheelCy, scale = 1.15f)

                val upY = wheelCy + 62f
                sr.color = OUTLINE
                sr.rect(wheelCx - 22f, upY - 18f, 44f, 38f)
                sr.color = Color(0.52f, 0.55f, 0.60f, 1f)
                sr.rect(wheelCx - 19f, upY - 15f, 38f, 32f)
                sr.color = OUTLINE
                sr.rect(wheelCx - 18f, upY - 28f, 36f, 14f)

                val sellY = wheelCy - 62f
                sr.color = OUTLINE
                sr.rect(wheelCx - 18f, sellY - 16f, 36f, 32f)
                sr.color = Color(0.52f, 0.55f, 0.60f, 1f)
                sr.rect(wheelCx - 15f, sellY - 13f, 30f, 26f)

                val rallyBtnX = wheelCx + 48f
                val rallyBtnY = wheelCy - 38f
                sr.color = Color(0.20f, 0.78f, 1f, 0.55f)
                sr.circle(rallyBtnX, rallyBtnY, 26f)
                sr.color = OUTLINE
                sr.circle(rallyBtnX, rallyBtnY, 20f)
                sr.color = Color(0.22f, 0.24f, 0.28f, 1f)
                sr.circle(rallyBtnX, rallyBtnY, 16f)
                drawBlueRallyFlagIcon(sr, rallyBtnX, rallyBtnY, 0.55f)

                sr.color = Color(0.76f, 0.22f, 0.08f, 1f)
                sr.rectLine(rallyBtnX + 26f, rallyBtnY - 42f, rallyBtnX + 16f, rallyBtnY - 24f, 3f)
                sr.triangle(rallyBtnX + 14f, rallyBtnY - 20f, rallyBtnX + 22f, rallyBtnY - 26f, rallyBtnX + 13f, rallyBtnY - 29f)

                val rangeCx = scrollX + 445f
                val rangeCy = scrollY + 142f
                sr.color = Color(0.22f, 0.68f, 0.92f, 0.85f)
                sr.ellipse(rangeCx - 145f, rangeCy - 72f, 290f, 144f)
                sr.color = Color(0.88f, 0.85f, 0.66f, 1f)
                sr.ellipse(rangeCx - 142f, rangeCy - 69f, 284f, 138f)

                drawRenderedTower(sr, TowerType.BARRACKS, 1, rangeCx - 10f, rangeCy + 16f, scale = 1.25f)
                drawRenderedSoldier(sr, "Militia", rangeCx - 58f, rangeCy - 36f, scale = 1.25f)
                drawRenderedSoldier(sr, "Militia", rangeCx - 34f, rangeCy - 22f, scale = 1.25f)
                drawRenderedSoldier(sr, "Militia", rangeCx - 12f, rangeCy - 38f, scale = 1.25f)

                sr.color = Color(0.14f, 0.48f, 0.78f, 1f)
                sr.rect(rangeCx + 4f, rangeCy - 38f, 16f, 9f)
                sr.triangle(rangeCx + 20f, rangeCy - 27f, rangeCx + 20f, rangeCy - 45f, rangeCx + 32f, rangeCy - 36f)

                val flagTargetX = rangeCx + 68f
                val flagTargetY = rangeCy - 36f
                sr.color = Color(0.18f, 0.58f, 0.92f, 0.85f)
                sr.ellipse(flagTargetX - 20f, flagTargetY - 9f, 40f, 18f)
                sr.color = Color(0.88f, 0.85f, 0.66f, 1f)
                sr.ellipse(flagTargetX - 14f, flagTargetY - 6f, 28f, 12f)
                drawBlueRallyFlagIcon(sr, flagTargetX, flagTargetY + 10f, 0.95f)

                sr.color = Color(0.76f, 0.22f, 0.08f, 1f)
                sr.rectLine(flagTargetX, flagTargetY - 44f, flagTargetX, flagTargetY - 26f, 3f)
                sr.triangle(flagTargetX, flagTargetY - 20f, flagTargetX - 5f, flagTargetY - 28f, flagTargetX + 5f, flagTargetY - 28f)
            }
        }
        sr.end()

        batch.begin()
        val oldSmallScaleX = smallFont.data.scaleX
        val oldSmallScaleY = smallFont.data.scaleY

        val headerX = scrollX + 170f
        val redHighlight = Color(0.76f, 0.20f, 0.08f, 1f)

        when (hintType) {
            HintPopupType.ARMORED_ENEMIES -> {
                smallFont.data.setScale(0.95f)
                smallFont.color = Color(0.24f, 0.20f, 0.16f, 1f)
                smallFont.draw(batch, "SOME ENEMIES WEAR DIFFERENT LEVELS OF", headerX, scrollY + scrollH - 76f)
                smallFont.draw(batch, "ARMOR THAT PROTECTS THEM AGAINST", headerX, scrollY + scrollH - 96f)
                smallFont.draw(batch, "NON-MAGICAL ATTACKS.", headerX, scrollY + scrollH - 116f)

                smallFont.data.setScale(0.84f)
                smallFont.color = redHighlight
                smallFont.draw(batch, "RESISTS", scrollX + 150f, scrollY + 195f, 140f, Align.center, false)
                smallFont.draw(batch, "DAMAGE FROM", scrollX + 150f, scrollY + 178f, 140f, Align.center, false)

                smallFont.data.setScale(0.98f)
                ComicFontRenderer.drawMultiColorLine(
                    batch, smallFont,
                    listOf(
                        Pair("ARMORED ENEMIES TAKE ", OUTLINE),
                        Pair("LESS DAMAGE ", redHighlight),
                        Pair("FROM", OUTLINE)
                    ),
                    scrollX + 115f, scrollY + 70f
                )
                ComicFontRenderer.drawMultiColorLine(
                    batch, smallFont,
                    listOf(
                        Pair("MARKSMEN, SOLDIERS ", redHighlight),
                        Pair("AND ", OUTLINE),
                        Pair("ARTILLERY.", redHighlight)
                    ),
                    scrollX + 145f, scrollY + 46f
                )
            }

            HintPopupType.MAGIC_RESISTANT_ENEMIES -> {
                smallFont.data.setScale(0.95f)
                smallFont.color = Color(0.24f, 0.20f, 0.16f, 1f)
                smallFont.draw(batch, "SOME ENEMIES ENJOY DIFFERENT LEVELS OF", headerX, scrollY + scrollH - 76f)
                smallFont.draw(batch, "MAGIC RESISTANCE THAT PROTECTS THEM", headerX, scrollY + scrollH - 96f)
                smallFont.draw(batch, "AGAINST MAGICAL ATTACKS.", headerX, scrollY + scrollH - 116f)

                smallFont.data.setScale(0.84f)
                smallFont.color = redHighlight
                smallFont.draw(batch, "RESISTS", scrollX + 235f, scrollY + 198f, 140f, Align.center, false)
                smallFont.draw(batch, "DAMAGE FROM", scrollX + 235f, scrollY + 181f, 140f, Align.center, false)

                smallFont.data.setScale(0.98f)
                smallFont.color = OUTLINE
                smallFont.draw(batch, "MAGIC RESISTANT ENEMIES TAKE", scrollX, scrollY + 72f, scrollW, Align.center, false)

                ComicFontRenderer.drawMultiColorLine(
                    batch, smallFont,
                    listOf(
                        Pair("LESS DAMAGE ", redHighlight),
                        Pair("FROM ", OUTLINE),
                        Pair("WIZARDS.", redHighlight)
                    ),
                    scrollX + 195f, scrollY + 46f
                )
            }

            HintPopupType.STRATEGY_BASICS -> {
                smallFont.data.setScale(0.94f)
                ComicFontRenderer.drawMultiColorLine(
                    batch, smallFont,
                    listOf(
                        Pair("BARRACKS ARE ", OUTLINE),
                        Pair("GOOD FOR BLOCKING THE ENEMY,", redHighlight)
                    ),
                    scrollX + 75f, scrollY + scrollH - 72f
                )
                ComicFontRenderer.drawMultiColorLine(
                    batch, smallFont,
                    listOf(
                        Pair("BUT ", OUTLINE),
                        Pair("LACK IN ATTACK POWER. ", redHighlight),
                        Pair("MAKE SURE YOU HAVE", OUTLINE)
                    ),
                    scrollX + 75f, scrollY + scrollH - 94f
                )
                ComicFontRenderer.drawMultiColorLine(
                    batch, smallFont,
                    listOf(
                        Pair("ENOUGH FIREPOWER TO SUPPORT THEM!", OUTLINE)
                    ),
                    scrollX + 75f, scrollY + scrollH - 116f
                )

                val mapX = cX - 270f
                val mapY = scrollY + 24f
                smallFont.data.setScale(0.84f)
                smallFont.color = OUTLINE
                smallFont.draw(batch, "SUPPORT YOUR", mapX + 235f, mapY + 62f, 195f, Align.center, false)
                smallFont.draw(batch, "SOLDIERS WITH", mapX + 235f, mapY + 44f, 195f, Align.center, false)
                smallFont.draw(batch, "RANGED TOWERS!", mapX + 235f, mapY + 26f, 195f, Align.center, false)
            }

            HintPopupType.HEAVILY_ARMORED_ENEMIES -> {
                smallFont.data.setScale(0.94f)
                ComicFontRenderer.drawMultiColorLine(
                    batch, smallFont,
                    listOf(
                        Pair("SOME ENEMIES WEAR ", OUTLINE),
                        Pair("HEAVY ARMOR ", redHighlight),
                        Pair("AND ARE", OUTLINE)
                    ),
                    scrollX + 75f, scrollY + scrollH - 72f
                )
                ComicFontRenderer.drawMultiColorLine(
                    batch, smallFont,
                    listOf(
                        Pair("ALMOST IMPERVIOUS TO PHYSICAL DAMAGE. IN ORDER", OUTLINE)
                    ),
                    scrollX + 75f, scrollY + scrollH - 94f
                )
                ComicFontRenderer.drawMultiColorLine(
                    batch, smallFont,
                    listOf(
                        Pair("TO KILL THEM ", OUTLINE),
                        Pair("HIT THEM WITH MAGIC ATTACKS!", redHighlight)
                    ),
                    scrollX + 75f, scrollY + scrollH - 116f
                )

                val noteW = 205f
                val noteX = cX - noteW / 2f
                val noteY = scrollY + 30f
                smallFont.data.setScale(0.84f)
                smallFont.color = OUTLINE
                smallFont.draw(batch, "USE THE HIGHEST", noteX, noteY + 50f, noteW, Align.center, false)
                smallFont.draw(batch, "LEVELS OF WIZARDS", noteX, noteY + 32f, noteW, Align.center, false)
                smallFont.draw(batch, "AVAILABLE!", noteX, noteY + 14f, noteW, Align.center, false)
            }

            else -> {
                smallFont.data.setScale(0.95f)
                smallFont.color = Color(0.24f, 0.20f, 0.16f, 1f)
                smallFont.draw(batch, "YOU CAN ADJUST YOUR SOLDIERS RALLY", headerX, scrollY + scrollH - 76f)
                smallFont.draw(batch, "POINT TO MAKE THEM DEFEND A", headerX, scrollY + scrollH - 96f)
                smallFont.draw(batch, "DIFFERENT AREA.", headerX, scrollY + scrollH - 116f)

                smallFont.data.setScale(0.82f)
                GameUIRenderer.drawBadgeCenteredText(batch, smallFont, "200", scrollX + 148f - 18f, scrollY + 135f + 62f - 28f, 36f, 14f, Color.WHITE)
                GameUIRenderer.drawBadgeCenteredText(batch, smallFont, "$", scrollX + 148f - 15f, scrollY + 135f - 62f - 13f, 30f, 26f, Color.WHITE)

                smallFont.data.setScale(0.84f)
                smallFont.color = Color(0.16f, 0.52f, 0.82f, 1f)
                smallFont.draw(batch, "RALLY RANGE", scrollX + 375f, scrollY + 202f, 140f, Align.center, false)

                smallFont.data.setScale(0.84f)
                smallFont.color = redHighlight
                smallFont.draw(batch, "SELECT THE RALLY", scrollX + 115f, scrollY + 48f, 190f, Align.center, false)
                smallFont.draw(batch, "POINT CONTROL", scrollX + 115f, scrollY + 31f, 190f, Align.center, false)

                smallFont.draw(batch, "SELECT WHERE YOU WANT", scrollX + 345f, scrollY + 52f, 230f, Align.center, false)
                smallFont.draw(batch, "TO MOVE YOUR SOLDIERS", scrollX + 345f, scrollY + 35f, 230f, Align.center, false)
            }
        }

        smallFont.data.setScale(oldSmallScaleX, oldSmallScaleY)
        batch.end()
    }

    private fun getEnemyCardDetails(type: EnemyType): EnemyCardDetails = when (type) {
        EnemyType.GOBLIN -> EnemyCardDetails("GOBLINS", "SMALL EVIL HUMANOIDS WITH NO OUTSTANDING ABILITIES.", listOf("LOW HEALTH", "AVERAGE SPEED"))
        EnemyType.ORC -> EnemyCardDetails("ORCS", "TOUGH SAVAGES WITH LIGHT ARMOR.", listOf("LIGHT ARMOR *", "SLOW SPEED"), "* ARMORED ENEMIES RECEIVE LESS DAMAGE FROM ARCHERS AND SOLDIERS", "MAGIC DAMAGE IGNORES", "ARMOR COMPLETELY")
        EnemyType.SHAMAN -> EnemyCardDetails("SHAMAN", "TRIBAL HEALERS RESISTANT TO MAGIC ATTACKS", listOf("HIGH MAGIC RESISTANCE *", "CAN HEAL ALLIES", "AVERAGE SPEED"), "* RESISTS DAMAGE FROM MAGE TOWERS", "ARCHER TOWERS ARE THE BEST", "WAY TO DEAL WITH SHAMANS")
        EnemyType.OGRE -> EnemyCardDetails("OGRES", "BIG, UGLY AND TOUGH HUMANOIDS. THEY'LL OBLITERATE EVERYTHING IN THEIR PATH.", listOf("VERY HIGH HEALTH", "VERY SLOW SPEED", "COSTS 3 LIVES"), bottomTip1 = "MAGES AND ARTILLERY ARE GREAT", bottomTip2 = "TO SUPPORT YOUR SOLDIERS.")
        EnemyType.BANDIT -> EnemyCardDetails("BANDITS", "SWIFT & AGILE, THEY CAN DISPATCH THEIR ENEMIES VERY QUICKLY.", listOf("FAST SPEED", "VERY HIGH DAMAGE", "CAN DODGE MELEE ATTACKS"), bottomTip1 = "BANDITS CAN EASILY OBLITERATE", bottomTip2 = "YOUR LOW LEVEL SOLDIERS.")
        EnemyType.BRIGAND -> EnemyCardDetails("BRIGANDS", "ARMORED OUTLAWS THAT CAN WITHSTAND A LOT OF PUNISHMENT", listOf("AVERAGE SPEED", "ARMORED"), bottomTip1 = "EXPLOSIONS IGNORE HALF THE", bottomTip2 = "ARMOR RATING")
        EnemyType.WULF -> EnemyCardDetails("WULVES", "VERY FAST VICIOUS CREATURES THAT CAN DODGE MELEE ATTACKS.", listOf("LOW HEALTH", "VERY FAST SPEED", "CAN DODGE MELEE ATTACKS"))
        EnemyType.MARAUDER -> EnemyCardDetails("MARAUDERS", "HULKING, WELL-TRAINED ARMORED OUTLAWS, OFTEN SEEN LEADING BRIGANDS AND BANDITS.", listOf("SLOW SPEED", "ARMORED", "COST 3 LIVES"))
        EnemyType.GIANT_SPIDER -> EnemyCardDetails("GIANT SPIDER", "QUICK, VICIOUS ENEMIES RESISTANT TO MAGIC.", listOf("HIGH MAGIC RESISTANCE", "LOW HEALTH", "FAST SPEED"), bottomTip1 = "MAKE SURE YOU HAVE ENOUGH", bottomTip2 = "SOLDIERS TO STOP THEM")
        EnemyType.SPIDER_MATRIARCH -> EnemyCardDetails("MATRIARCH", "TOUGH MAGIC RESISTANT BEAST THAT CAN SPAWN SEVERAL SPIDERLINGS.", listOf("HIGH MAGIC RESISTANCE", "SPAWNS SPIDERLINGS", "COST 2 LIVES", "AVERAGE SPEED"))
        EnemyType.GARGOYLE -> EnemyCardDetails("GARGOYLES", "VILE WINGED CREATURES THAT CAN FLY PAST BARRACKS AND ARTILLERY.", listOf("FLYING", "AVERAGE SPEED"), bottomTip1 = "FLYING ENEMIES CAN'T BE BLOCKED", bottomTip2 = "NOR TARGETED BY MOST ARTILLERY")
        EnemyType.SHADOW_ARCHER -> EnemyCardDetails("SHADOW ARCHERS", "DEADLY ACCURATE ARCHERS THAT WILL SHOOT AT YOUR NEARBY SOLDIERS.", listOf("RANGED ATTACK", "LOW MAGIC RESISTANCE", "AVERAGE SPEED"))
        EnemyType.DARK_KNIGHT -> EnemyCardDetails("DARK KNIGHTS", "FULL PLATED CORRUPTED KNIGHTS, ALMOST IMPERVIOUS TO PHYSICAL DAMAGE.", listOf("SLOW SPEED", "HEAVY ARMOR"), bottomTip1 = "MAGIC ATTACKS ARE THE BEST WAY", bottomTip2 = "TO DEAL WITH DARK KNIGHTS")
        EnemyType.WORG -> EnemyCardDetails("WORGS", "CUNNING FAST BEASTS THAT CAN DODGE MELEE ATTACKS AND RESIST MAGIC.", listOf("MAGIC RESISTANCE", "CAN DODGE MELEE ATTACKS", "FAST SPEED"))
        EnemyType.GOLEM_HEAD -> EnemyCardDetails("GOLEM HEADS", "MECHANICAL CONSTRUCTS OF DARK MAGIC.", listOf("MEDIUM ARMOR", "SLOW SPEED"))
        EnemyType.TROLL -> EnemyCardDetails("TROLLS", "VERY TOUGH WARRIORS THAT CAN REGENERATE THEIR WOUNDS QUICKLY.", listOf("VERY HIGH DAMAGE", "HEALTH REGENERATION", "AVERAGE SPEED"), bottomTip1 = "TRY TO KILL THEM FAST TO", bottomTip2 = "PREVENT REGENERATION.")
        EnemyType.WINTER_WOLF -> EnemyCardDetails("WINTER WOLVES", "CUNNING & DEADLY, THESE BEASTS CAN DEFEAT ALL BUT THE BEST WARRIORS.", listOf("HIGH MAGIC RESISTANCE", "CAN DODGE MELEE ATTACKS", "VERY FAST SPEED"))
        EnemyType.TROLL_CHAMPION -> EnemyCardDetails("TROLL CHAMPION", "ELITE TRIBAL WARRIORS EQUIPPED WITH VICIOUS THROWING AXES", listOf("RANGED ATTACK", "HEALTH REGENERATION", "AVERAGE SPEED"), bottomTip1 = "VICIOUS AXES DEAL FULL", bottomTip2 = "DAMAGE REGARDLESS OF ARMOR")
        EnemyType.TROLL_CHIEFTAIN -> EnemyCardDetails("TROLL CHIEFTAIN", "THE CHIEFTAIN CAN ENRAGE HIS BRETHREN, IMPROVING THEIR ATTACK DAMAGE, ARMOR RATING, SPEED AND HEALTH.", listOf("HIGH HEALTH REGENERATION", "CAN ENRAGE OTHER TROLLS", "VERY SLOW SPEED"))
        EnemyType.YETI -> EnemyCardDetails("YETI", "LARGE APE-MAN LIKE CREATURE THAT CAN OBLITERATE SCORES OF SOLDIERS WITH ONE STRIKE.", listOf("VERY HIGH HEALTH", "VERY SLOW SPEED", "COST 5 LIVES", "AREA ATTACK"), bottomTip1 = "YETI ATTACKS DEAL AREA", bottomTip2 = "DAMAGE")
        EnemyType.ROCKET_RIDER -> EnemyCardDetails("ROCKET RIDERS", "HALF MAN, HALF MACHINE; THE ROCKET RIDERS FOLLOW THEIR DARK LORD'S WILL WHEREVER THEY GO.", listOf("FLYING", "AVERAGE SPEED", "CAN USE TURBO"), bottomTip1 = "TURBO MAKES THEM FLY SUPER", bottomTip2 = "FAST FOR 2 SECONDS.")
        EnemyType.DARK_SLAYER -> EnemyCardDetails("DARK SLAYERS", "CLAIMED BY DARKNESS; SLAYERS ARE THE BANE OF ANYONE STANDING IN THEIR WAY.", listOf("VERY TOUGH", "SLOW SPEED", "GREAT ARMOR"))
        EnemyType.DEMON_SPAWN -> EnemyCardDetails("DEMON SPAWN", "TERRIBLE INFERNAL CREATURES THAT EXPLODE WHEN THEY DIE, DAMAGING NEARBY SOLDIERS.", listOf("MAGIC RESISTANCE", "INFERNAL COMBUSTION"))
        EnemyType.DEMON_LORD -> EnemyCardDetails("DEMON LORD", "DEMON LORDS LEAD FIENDISH ARMIES INTO BATTLE, PROTECTING THEIR MINIONS WITH INFERNAL SHIELDS.", listOf("SHIELDS OTHER DEMONS", "MAGIC RESISTANCE", "INFERNAL COMBUSTION"), bottomTip1 = "INFERNAL SHIELDS NEGATE DAMAGE", bottomTip2 = "FROM A LIMITED NUMBER OF ATTACKS.")
        EnemyType.DEMON_HOUND -> EnemyCardDetails("DEMON HOUNDS", "VICIOUS TERRIBLE CREATURES, THEY SERVE AS HUNTING DOGS FOR THE DEMON LORDS.", listOf("MAGIC RESISTANCE", "INFERNAL COMBUSTION", "FAST SPEED"))
        EnemyType.DEMON_IMP -> EnemyCardDetails("DEMON IMP", "FLYING MISCHIEVIOUS INFERNAL CREATURES THAT ARE OFTEN SEEN IN THE COMPANY OF GARGOYLES.", listOf("FLYING", "AVERAGE SPEED"))
        EnemyType.SKELETON -> EnemyCardDetails("SKELETONS", "MINDLESS AUTOMATONS THAT OBEY THE ORDERS OF EVIL NECROMANCERS.", listOf("SLOW SPEED", "IMMUNE TO POISON"))
        EnemyType.SKELETON_KNIGHT -> EnemyCardDetails("SKELETON KNIGHTS", "SKELETAL REMAINS OF FALLEN WARRIORS REANIMATED BY DARK MAGIC.", listOf("LIGHT ARMOR", "SLOW SPEED"))
        EnemyType.NECROMANCER -> EnemyCardDetails("NECROMANCER", "MASTER OF THE DARK ARTS, THE NECROMANCER SUMMONS UNDEAD MINIONS TO DO THEIR BIDDING.", listOf("SUMMONS UNDEAD", "RANGED ATTACK", "SLOW SPEED"))
        EnemyType.MAGMA_ELEMENTAL -> EnemyCardDetails("MAGMA ELEMENTAL", "MADE OF ROCK AND FIRE, MAGMA ELEMENTALS ARE BEINGS OF DEATH AND DESTRUCTION.", listOf("VERY HIGH HEALTH", "VERY SLOW SPEED", "COST 5 LIVES", "AREA ATTACK"))
        else -> EnemyCardDetails(type.title.uppercase(), "A FORMIDABLE FOE MARCHING AGAINST THE KINGDOM OF LINIREA!", listOf("HIGH THREAT", "BOSS ENEMY"))
    }

    private fun renderInstructionsScroll(
        sr: ShapeRenderer,
        batch: SpriteBatch,
        font: BitmapFont,
        smallFont: BitmapFont,
        page: Int
    ) {
        val cX = Config.WORLD_WIDTH / 2f
        val cY = Config.WORLD_HEIGHT / 2f + 18f
        val scrollW = 820f
        val scrollH = 430f
        val scrollX = cX - scrollW / 2f
        val scrollY = cY - scrollH / 2f

        sr.begin(ShapeRenderer.ShapeType.Filled)
        sr.color = Color(0f, 0f, 0f, 0.38f)
        sr.rect(0f, 0f, Config.WORLD_WIDTH, Config.WORLD_HEIGHT)

        if (page < 3) {
            drawTabButton(sr, cX - 240f, scrollY - 62f, 232f, 66f, Color(0.46f, 0.36f, 0.28f, 1f))
            drawTabButton(sr, cX + 8f, scrollY - 62f, 232f, 66f, Color(0.16f, 0.38f, 0.58f, 1f))
        } else {
            drawTabButton(sr, cX - 135f, scrollY - 72f, 270f, 76f, Color(0.16f, 0.38f, 0.58f, 1f))
        }

        val tabW = 390f
        val tabH = 68f
        val tabX = cX - tabW / 2f
        val tabY = scrollY + scrollH - 10f
        sr.color = Color(0f, 0f, 0f, 0.30f)
        sr.rect(tabX + 4f, tabY - 4f, tabW, tabH)
        sr.color = OUTLINE
        sr.rect(tabX - 3f, tabY - 3f, tabW + 6f, tabH + 6f)
        sr.color = Color(0.44f, 0.34f, 0.25f, 1f)
        sr.rect(tabX, tabY, tabW, tabH)

        drawJaggedParchment(sr, scrollX, scrollY, scrollW, scrollH)

        ComicFontRenderer.drawPlaqueText(sr, "INSTRUCTIONS", cX, tabY + tabH / 2f + 2f, 25f, Color(0.96f, 0.92f, 0.72f, 1f))

        if (page < 3) {
            ComicFontRenderer.drawPlaqueText(sr, "SKIP THIS!", cX - 124f, scrollY - 29f, 20f, Color.WHITE)
            ComicFontRenderer.drawPlaqueText(sr, "NEXT!", cX + 124f, scrollY - 29f, 20f, Color.WHITE)
        } else {
            ComicFontRenderer.drawPlaqueText(sr, "GOT IT!", cX, scrollY - 22f, 22f, Color.WHITE)
        }

        val pageTitle = when (page) {
            1 -> "OBJECTIVE"
            2 -> "TOWER CONSTRUCTION"
            else -> "BASIC TOWER TYPES"
        }
        ComicFontRenderer.drawRibbonBannerText(sr, pageTitle, cX, scrollY + scrollH - 36f, 22f)

        when (page) {
            1 -> drawPage1ObjectiveArt(sr, cX, scrollY)
            2 -> drawPage2ConstructionArt(sr, cX, scrollY)
            3 -> drawPage3BasicTowerTypesArt(sr, cX, scrollY)
        }
        sr.end()

        batch.begin()
        val oldSmallScaleX = smallFont.data.scaleX
        val oldSmallScaleY = smallFont.data.scaleY

        if (page == 3) {
            smallFont.data.setScale(0.78f)
            GameUIRenderer.drawBadgeCenteredText(batch, smallFont, "I'M READY NOW BRING IT ON!", cX - 135f, scrollY - 68f, 270f, 28f, Color(0.90f, 0.94f, 1f, 1f))
        }

        when (page) {
            1 -> {
                smallFont.data.setScale(1.04f)
                smallFont.color = OUTLINE
                smallFont.draw(batch, "PROTECT YOUR LANDS FROM THE ENEMY ATTACK", scrollX, scrollY + scrollH - 64f, scrollW, Align.center, false)
                smallFont.data.setScale(0.90f)
                smallFont.draw(batch, "BUILD DEFENSIVE TOWERS ALONG THE ROAD TO STOP THEM", scrollX, scrollY + scrollH - 86f, scrollW, Align.center, false)

                smallFont.data.setScale(0.80f)
                smallFont.color = OUTLINE
                smallFont.draw(batch, "DONT LET ENEMIES", cX - 15f, scrollY + 258f, 185f, Align.center, false)
                smallFont.draw(batch, "PAST THIS POINT", cX - 15f, scrollY + 242f, 185f, Align.center, false)

                smallFont.data.setScale(0.74f)
                smallFont.draw(batch, "BUILD TOWERS TO", cX + 42f, scrollY + 132f, 160f, Align.center, false)
                smallFont.draw(batch, "DEFEND THE ROAD", cX + 42f, scrollY + 117f, 160f, Align.center, false)

                smallFont.draw(batch, "EARN GOLD BY", cX - 80f, scrollY + 72f, 155f, Align.center, false)
                smallFont.draw(batch, "KILLING ENEMIES", cX - 80f, scrollY + 57f, 155f, Align.center, false)
            }

            2 -> {
                smallFont.data.setScale(1.00f)
                smallFont.color = OUTLINE
                smallFont.draw(batch, "BUILD TOWERS ON THE STRATEGIC POINTS TO", scrollX, scrollY + scrollH - 64f, scrollW, Align.center, false)
                smallFont.draw(batch, "STOP THE ENEMY HORDE FROM GETTING THROUGH.", scrollX, scrollY + scrollH - 86f, scrollW, Align.center, false)

                val step1X = cX - 220f
                val step2X = cX
                val step3X = cX + 220f

                smallFont.data.setScale(0.92f)
                smallFont.color = Color(0.76f, 0.16f, 0.10f, 1f)
                smallFont.draw(batch, "CLICK THESE!", step1X - 75f, scrollY + 245f, 150f, Align.center, false)
                smallFont.draw(batch, "WOOT!", step3X - 60f, scrollY + 258f, 120f, Align.center, false)

                smallFont.data.setScale(0.80f)
                smallFont.draw(batch, "CLICK THE TOWER YOU", step2X - 100f, scrollY + 62f, 200f, Align.center, false)
                smallFont.draw(batch, "WANT TO BUILD!", step2X - 100f, scrollY + 45f, 200f, Align.center, false)
                smallFont.draw(batch, "READY FOR ACTION!", step3X - 90f, scrollY + 108f, 180f, Align.center, false)

                smallFont.color = Color(0.32f, 0.28f, 0.22f, 1f)
                smallFont.draw(batch, "THIS IS A", step1X - 75f, scrollY + 128f, 150f, Align.center, false)
                smallFont.draw(batch, "STRATEGIC POINT", step1X - 75f, scrollY + 111f, 150f, Align.center, false)

                smallFont.data.setScale(0.76f)
                GameUIRenderer.drawBadgeCenteredText(batch, smallFont, "70", step2X - 48f - 18f, scrollY + 195f, 36f, 15f, Color(1f, 0.90f, 0.28f, 1f))
                GameUIRenderer.drawBadgeCenteredText(batch, smallFont, "70", step2X + 48f - 18f, scrollY + 195f, 36f, 15f, Color(1f, 0.90f, 0.28f, 1f))
                GameUIRenderer.drawBadgeCenteredText(batch, smallFont, "100", step2X - 48f - 18f, scrollY + 107f, 36f, 15f, Color(1f, 0.90f, 0.28f, 1f))
                GameUIRenderer.drawBadgeCenteredText(batch, smallFont, "150", step2X + 48f - 18f, scrollY + 107f, 36f, 15f, Color(1f, 0.90f, 0.28f, 1f))
            }

            3 -> {
                smallFont.data.setScale(1.00f)
                smallFont.color = OUTLINE
                smallFont.draw(batch, "THERE ARE FOUR BASIC TYPES OF TOWERS AVAILABLE.", scrollX, scrollY + scrollH - 64f, scrollW, Align.center, false)

                val colCenters = listOf(cX - 285f, cX - 95f, cX + 95f, cX + 285f)
                val titles = listOf("ARCHER TOWER", "BARRACKS", "MAGES GUILD", "ARTILLERY")
                val descLine1 = listOf("GOOD", "SOLDIERS BLOCK", "HIGH DAMAGE", "DEALS AREA")
                val descLine2 = listOf("RATE OF FIRE", "ENEMIES", "ARMOR PIERCING", "DAMAGE")

                for (i in 0..3) {
                    val colX = colCenters[i] - 85f
                    val colW = 170f

                    smallFont.data.setScale(0.95f)
                    smallFont.color = Color(0.74f, 0.20f, 0.10f, 1f)
                    smallFont.draw(batch, titles[i], colX, scrollY + 282f, colW, Align.center, false)

                    smallFont.data.setScale(0.82f)
                    smallFont.color = OUTLINE
                    smallFont.draw(batch, descLine1[i], colX, scrollY + 90f, colW, Align.center, false)
                    smallFont.draw(batch, descLine2[i], colX, scrollY + 72f, colW, Align.center, false)
                }
            }
        }

        smallFont.data.setScale(oldSmallScaleX, oldSmallScaleY)
        batch.end()
    }

    private fun drawPage1ObjectiveArt(sr: ShapeRenderer, cX: Float, scrollY: Float) {
        val mapW = 540f
        val mapH = 260f
        val mapX = cX - mapW / 2f
        val mapY = scrollY + 34f

        sr.color = Color(0.75f, 0.68f, 0.46f, 1f)
        sr.rect(mapX - 4f, mapY - 4f, mapW + 8f, mapH + 8f)
        sr.color = Color(0.55f, 0.72f, 0.24f, 1f)
        sr.rect(mapX, mapY, mapW, mapH)

        val roadColor = Color(0.84f, 0.75f, 0.48f, 1f)
        val roadEdge = Color(0.62f, 0.52f, 0.30f, 1f)
        val pts = listOf(
            Vector2(mapX + 12f, mapY + 72f),
            Vector2(mapX + 145f, mapY + 86f),
            Vector2(mapX + 245f, mapY + 78f),
            Vector2(mapX + 345f, mapY + 148f),
            Vector2(mapX + 465f, mapY + 178f),
            Vector2(mapX + mapW - 12f, mapY + 178f)
        )
        sr.color = roadEdge
        for (i in 0 until pts.size - 1) {
            sr.rectLine(pts[i], pts[i + 1], 68f)
            sr.circle(pts[i].x, pts[i].y, 34f)
        }
        sr.color = roadColor
        for (i in 0 until pts.size - 1) {
            sr.rectLine(pts[i], pts[i + 1], 60f)
            sr.circle(pts[i].x, pts[i].y, 30f)
        }

        val trees = listOf(
            Vector2(mapX + 45f, mapY + 215f), Vector2(mapX + 85f, mapY + 230f),
            Vector2(mapX + 135f, mapY + 205f), Vector2(mapX + 165f, mapY + 220f),
            Vector2(mapX + 265f, mapY + 225f), Vector2(mapX + 305f, mapY + 235f),
            Vector2(mapX + 475f, mapY + 55f), Vector2(mapX + 510f, mapY + 40f)
        )
        for (t in trees) {
            sr.color = Color(0.26f, 0.46f, 0.12f, 1f)
            sr.circle(t.x, t.y, 24f)
            sr.color = Color(0.38f, 0.62f, 0.18f, 1f)
            sr.circle(t.x - 2f, t.y + 3f, 18f)
        }

        drawRenderedStrategicPoint(sr, mapX + 82f, mapY + 142f, scale = 0.85f)
        drawRenderedTower(sr, TowerType.ARCHER, 1, mapX + 245f, mapY + 142f, scale = 0.95f)

        for (g in listOf(Vector2(mapX + 45f, mapY + 62f), Vector2(mapX + 82f, mapY + 68f), Vector2(mapX + 112f, mapY + 92f), Vector2(mapX + 132f, mapY + 58f))) {
            drawRenderedEnemy(sr, EnemyType.GOBLIN, g.x, g.y, scale = 0.95f)
        }

        val defX = mapX + 472f
        val defY = mapY + 168f
        withScaledTransform(sr, defX, defY, 0.85f) {
            MapRenderer.drawDefensePoint(sr, 0f, 0f, 36f, 38f, 36f, -42f)
        }

        sr.color = OUTLINE
        sr.rect(cX - 17f, scrollY + 223f, 189f, 48f)
        sr.triangle(cX + 145f, scrollY + 225f, cX + 165f, scrollY + 225f, defX - 10f, defY + 14f)
        sr.color = Color(0.97f, 0.96f, 0.92f, 1f)
        sr.rect(cX - 15f, scrollY + 225f, 185f, 44f)
        sr.triangle(cX + 147f, scrollY + 227f, cX + 163f, scrollY + 227f, defX - 8f, defY + 16f)

        sr.color = OUTLINE
        sr.rect(cX + 40f, scrollY + 101f, 164f, 42f)
        sr.triangle(cX + 42f, scrollY + 128f, cX + 42f, scrollY + 142f, mapX + 275f, mapY + 125f)
        sr.color = Color(0.97f, 0.96f, 0.92f, 1f)
        sr.rect(cX + 42f, scrollY + 103f, 160f, 38f)

        sr.color = OUTLINE
        sr.rect(cX - 82f, scrollY + 41f, 159f, 42f)
        sr.triangle(cX - 80f, scrollY + 68f, cX - 80f, scrollY + 82f, mapX + 150f, mapY + 62f)
        sr.color = Color(0.97f, 0.96f, 0.92f, 1f)
        sr.rect(cX - 80f, scrollY + 43f, 155f, 38f)
    }

    private fun drawPage2ConstructionArt(sr: ShapeRenderer, cX: Float, scrollY: Float) {
        val step1X = cX - 220f
        val step2X = cX
        val step3X = cX + 220f
        val centerY = scrollY + 175f

        drawRenderedStrategicPoint(sr, step1X, centerY, scale = 1.25f)
        drawRedArrow(sr, cX - 132f, centerY)
        drawRenderedStrategicPoint(sr, step2X, centerY, scale = 0.95f)

        sr.color = Color(0.24f, 0.24f, 0.26f, 0.95f)
        sr.rectLine(step2X - 48f, centerY + 44f, step2X + 48f, centerY + 44f, 5.5f)
        sr.rectLine(step2X - 48f, centerY - 44f, step2X + 48f, centerY - 44f, 5.5f)
        sr.rectLine(step2X - 48f, centerY + 44f, step2X - 48f, centerY - 44f, 5.5f)
        sr.rectLine(step2X + 48f, centerY + 44f, step2X + 48f, centerY - 44f, 5.5f)

        sr.color = Color(0.42f, 0.44f, 0.48f, 1f)
        sr.rect(step2X - 6f, centerY + 40f, 12f, 7f)
        sr.rect(step2X - 6f, centerY - 47f, 12f, 7f)
        sr.rect(step2X - 52f, centerY - 4f, 7f, 12f)
        sr.rect(step2X + 45f, centerY - 4f, 7f, 12f)

        val wheelButtons = listOf(
            Vector2(step2X - 48f, centerY + 44f),
            Vector2(step2X + 48f, centerY + 44f),
            Vector2(step2X - 48f, centerY - 44f),
            Vector2(step2X + 48f, centerY - 44f)
        )
        for (idx in wheelButtons.indices) {
            val b = wheelButtons[idx]
            sr.color = OUTLINE
            sr.rect(b.x - 21f, b.y - 16f, 42f, 40f)
            sr.color = Color(0.24f, 0.24f, 0.26f, 1f)
            sr.rect(b.x - 19f, b.y - 14f, 38f, 36f)
            sr.color = Color(0.15f, 0.13f, 0.11f, 0.98f)
            sr.rect(b.x - 15.5f, b.y - 10.5f, 31f, 29f)

            sr.color = Color(0.68f, 0.72f, 0.76f, 1f)
            sr.rect(b.x - 19f, b.y + 17f, 6f, 5f)
            sr.rect(b.x + 13f, b.y + 17f, 6f, 5f)
            sr.rect(b.x - 19f, b.y - 14f, 6f, 5f)
            sr.rect(b.x + 13f, b.y - 14f, 6f, 5f)

            val icx = b.x
            val icy = b.y + 4f
            when (idx) {
                0 -> {
                    for (ao in listOf(Vector2(-5f, -2.5f), Vector2(0f, 4f), Vector2(5f, -2.5f))) {
                        val ax = icx + ao.x; val ay = icy + ao.y
                        sr.color = Color(0.62f, 0.44f, 0.22f, 1f); sr.rectLine(ax - 1.5f, ay - 5f, ax + 1.5f, ay + 1f, 1.8f)
                        sr.color = Color(0.88f, 0.90f, 0.94f, 1f); sr.triangle(ax, ay + 5.5f, ax - 3.8f, ay + 1f, ax + 3.8f, ay + 1f)
                    }
                }
                1 -> {
                    sr.color = Color(0.80f, 0.82f, 0.86f, 1f)
                    sr.triangle(icx, icy - 8.5f, icx - 9.5f, icy + 7.5f, icx + 9.5f, icy + 7.5f)
                    sr.rect(icx - 9.5f, icy + 2.5f, 19f, 5f)
                    sr.color = Color(0.40f, 0.44f, 0.50f, 1f)
                    sr.triangle(icx, icy - 6.5f, icx - 7f, icy + 6f, icx + 7f, icy + 6f)
                }
                2 -> {
                    sr.color = Color(0.85f, 0.68f, 0.20f, 1f); sr.rectLine(icx - 6f, icy - 7.5f, icx + 1.5f, icy + 1f, 2.8f)
                    sr.color = Color(0.45f, 0.85f, 1f, 1f); sr.circle(icx + 1f, icy + 5.5f, 5.5f)
                    sr.color = Color.WHITE; sr.circle(icx - 0.5f, icy + 6.5f, 2f)
                }
                3 -> {
                    sr.color = Color(0.35f, 0.38f, 0.42f, 1f); sr.circle(icx - 1f, icy - 1.5f, 8f)
                    sr.color = Color(0.68f, 0.74f, 0.80f, 1f); sr.circle(icx - 3.5f, icy + 1.5f, 2.2f)
                    sr.color = Color(1f, 0.45f, 0.10f, 1f); sr.circle(icx + 1f, icy + 9.5f, 2.4f)
                }
            }

            sr.color = OUTLINE; sr.rect(b.x - 18f, b.y - 25f, 36f, 15f)
            sr.color = Color(0.10f, 0.09f, 0.08f, 0.98f); sr.rect(b.x - 16.5f, b.y - 23.5f, 33f, 12f)
        }

        drawRedArrow(sr, cX + 98f, centerY)
        drawRenderedTower(sr, TowerType.ARCHER, 1, step3X, centerY - 2f, scale = 1.40f)
    }

    private fun drawPage3BasicTowerTypesArt(sr: ShapeRenderer, cX: Float, scrollY: Float) {
        val colCenters = listOf(cX - 285f, cX - 95f, cX + 95f, cX + 285f)
        val artY = scrollY + 188f

        sr.color = Color(0.75f, 0.68f, 0.46f, 1f)
        for (divX in listOf(cX - 190f, cX, cX + 190f)) {
            for (dy in (scrollY + 52f).toInt()..(scrollY + 285f).toInt() step 8) {
                sr.rect(divX - 1f, dy.toFloat(), 2f, 4f)
            }
        }

        for (cx in colCenters) {
            val bx = cx - 84f; val by = scrollY + 52f; val bw = 168f; val bh = 54f
            sr.color = Color(0.75f, 0.68f, 0.46f, 1f); sr.rect(bx - 2f, by - 2f, bw + 4f, bh + 4f)
            sr.color = Color(0.95f, 0.91f, 0.74f, 1f); sr.rect(bx, by, bw, bh)
        }

        val c1 = colCenters[0]
        drawRenderedTower(sr, TowerType.ARCHER, 1, c1 - 15f, artY, scale = 1.30f)
        drawRenderedEnemy(sr, EnemyType.GOBLIN, c1 + 42f, artY - 22f, scale = 1.15f)
        sr.color = OUTLINE
        sr.rectLine(c1 + 6f, artY + 36f, c1 + 26f, artY + 18f, 2f)
        sr.rectLine(c1 + 22f, artY + 10f, c1 + 35f, artY - 8f, 2f)
        sr.rectLine(c1 + 48f, artY + 8f, c1 + 52f, artY - 8f, 2f)

        val c2 = colCenters[1]
        drawRenderedTower(sr, TowerType.BARRACKS, 1, c2 - 5f, artY + 12f, scale = 1.25f)
        drawRenderedSoldier(sr, "Militia", c2 - 34f, artY - 32f, scale = 1.20f)
        drawRenderedSoldier(sr, "Militia", c2 - 10f, artY - 18f, scale = 1.20f)
        drawRenderedSoldier(sr, "Militia", c2 + 15f, artY - 34f, scale = 1.20f)
        drawRenderedEnemy(sr, EnemyType.GOBLIN, c2 + 38f, artY - 34f, scale = 1.15f)

        val c3 = colCenters[2]
        drawRenderedTower(sr, TowerType.MAGE, 1, c3 - 10f, artY, scale = 1.30f)
        drawRenderedEnemy(sr, EnemyType.GOBLIN, c3 + 42f, artY - 18f, scale = 1.15f)
        sr.color = Color(0.25f, 0.85f, 1f, 0.9f); sr.circle(c3 + 18f, artY + 6f, 6.5f)
        sr.color = Color.WHITE; sr.circle(c3 + 18f, artY + 6f, 3.2f)

        val c4 = colCenters[3]
        drawRenderedTower(sr, TowerType.ARTILLERY, 1, c4 - 22f, artY + 2f, scale = 1.25f)
        sr.color = Color(0.42f, 0.38f, 0.34f, 0.9f); sr.circle(c4 + 28f, artY - 2f, 24f); sr.circle(c4 + 36f, artY + 14f, 18f)
        sr.color = Color(1f, 0.78f, 0.15f, 1f); sr.circle(c4 + 26f, artY - 12f, 12f); sr.circle(c4 + 38f, artY - 4f, 9f)
        drawRenderedEnemy(sr, EnemyType.GOBLIN, c4 + 2f, artY - 24f, scale = 1.05f)
        drawRenderedEnemy(sr, EnemyType.GOBLIN, c4 + 54f, artY - 18f, scale = 1.05f)
    }

    private fun renderSpecialUnlockPopup(
        sr: ShapeRenderer,
        batch: SpriteBatch,
        font: BitmapFont,
        smallFont: BitmapFont,
        popupType: SpecialUnlockPopupType
    ) {
        val cX = Config.WORLD_WIDTH / 2f
        val cY = Config.WORLD_HEIGHT / 2f + 35f
        val scrollW = 680f
        val scrollH = 265f
        val scrollX = cX - scrollW / 2f
        val scrollY = cY - scrollH / 2f

        val okBtnW = 175f
        val okBtnH = 58f
        val okBtnX = cX + 95f
        val okBtnY = scrollY - 58f

        val bannerW = 430f
        val bannerH = 62f
        val bannerX = cX - bannerW / 2f
        val bannerY = 68f

        sr.begin(ShapeRenderer.ShapeType.Filled)
        sr.color = Color(0f, 0f, 0f, 0.40f)
        sr.rect(0f, 0f, Config.WORLD_WIDTH, Config.WORLD_HEIGHT)

        drawTabButton(sr, okBtnX, okBtnY, okBtnW, okBtnH, Color(0.16f, 0.38f, 0.58f, 1f))

        val tabW = 410f
        val tabH = 64f
        val tabX = scrollX + 42f
        val tabY = scrollY + scrollH - 12f
        sr.color = OUTLINE
        sr.rect(tabX - 3f, tabY - 3f, tabW + 6f, tabH + 6f)
        sr.color = Color(0.44f, 0.34f, 0.25f, 1f)
        sr.rect(tabX, tabY, tabW, tabH)

        drawJaggedParchment(sr, scrollX, scrollY, scrollW, scrollH)

        ComicFontRenderer.drawPlaqueText(sr, "NEW SPECIAL AVAILABLE!", tabX + tabW / 2f, tabY + tabH / 2f + 2f, 20f, Color(0.96f, 0.92f, 0.72f, 1f))
        ComicFontRenderer.drawPlaqueText(sr, "OK!", okBtnX + okBtnW / 2f, okBtnY + okBtnH / 2f, 22f, Color.WHITE)

        val spellTitle = if (popupType == SpecialUnlockPopupType.REINFORCEMENTS) "CALL REINFORCEMENTS" else "RAIN OF FIRE"
        ComicFontRenderer.drawRibbonBannerText(sr, spellTitle, scrollX + 455f, scrollY + scrollH - 42f, 20f)

        val picX = scrollX + 26f
        val picY = scrollY + 25f
        val picW = 225f
        val picH = 215f
        sr.color = Color(0f, 0f, 0f, 0.25f)
        sr.rect(picX + 5f, picY - 5f, picW, picH)
        sr.color = Color(0.68f, 0.60f, 0.42f, 1f)
        sr.rect(picX - 3f, picY - 3f, picW + 6f, picH + 6f)
        sr.color = Color(0.95f, 0.90f, 0.72f, 1f)
        sr.rect(picX, picY, picW, picH)

        val innerX = picX + 12f
        val innerY = picY + 12f
        val innerW = picW - 24f
        val innerH = picH - 24f
        sr.color = Color(0.48f, 0.68f, 0.20f, 1f)
        sr.rect(innerX, innerY, innerW, innerH)
        sr.color = Color(0.84f, 0.74f, 0.46f, 1f)
        sr.rect(innerX, innerY, innerW, innerH * 0.62f)

        if (popupType == SpecialUnlockPopupType.REINFORCEMENTS) {
            GameUIRenderer.drawSpellCardPreview(sr, innerX + 58f, innerY + 118f, isFireSpell = false)
            drawRenderedSoldier(sr, "Farmer", innerX + 95f, innerY + 42f, scale = 1.40f)
            drawRenderedSoldier(sr, "Farmer", innerX + 125f, innerY + 62f, scale = 1.40f)
            drawRenderedEnemy(sr, EnemyType.ORC, innerX + 162f, innerY + 65f, scale = 1.40f)
        } else {
            GameUIRenderer.drawSpellCardPreview(sr, innerX + 58f, innerY + 118f, isFireSpell = true)
            sr.color = Color(1f, 0.45f, 0.08f, 1f)
            sr.circle(innerX + 135f, innerY + 155f, 12f)
            sr.circle(innerX + 115f, innerY + 115f, 10f)
            sr.color = Color(1f, 0.90f, 0.20f, 1f)
            sr.circle(innerX + 135f, innerY + 155f, 7f)
            sr.circle(innerX + 115f, innerY + 115f, 6f)
            sr.color = Color(0.38f, 0.34f, 0.32f, 0.95f)
            sr.circle(innerX + 138f, innerY + 55f, 34f)
            sr.color = Color(1f, 0.60f, 0.10f, 1f)
            sr.circle(innerX + 135f, innerY + 52f, 24f)
            sr.color = Color(1f, 0.92f, 0.28f, 1f)
            sr.circle(innerX + 135f, innerY + 52f, 15f)
            drawRenderedEnemy(sr, EnemyType.GOBLIN, innerX + 102f, innerY + 42f, scale = 1.25f)
            drawRenderedEnemy(sr, EnemyType.ORC, innerX + 172f, innerY + 48f, scale = 1.25f)
        }

        sr.color = Color(0f, 0f, 0f, 0.32f)
        sr.rect(bannerX + 4f, bannerY - 4f, bannerW, bannerH)
        sr.color = OUTLINE
        sr.rect(bannerX - 3f, bannerY - 3f, bannerW + 6f, bannerH + 6f)
        sr.color = Color(0.98f, 0.78f, 0.08f, 1f)
        sr.rect(bannerX, bannerY, bannerW, bannerH)

        val excX = bannerX + 38f
        val excY = bannerY + bannerH / 2f
        sr.color = OUTLINE
        sr.circle(excX, excY, 21f)
        sr.color = Color(0.98f, 0.84f, 0.18f, 1f)
        sr.circle(excX, excY, 17.5f)
        sr.color = OUTLINE
        sr.rect(excX - 2.5f, excY - 2f, 5f, 13f)
        sr.circle(excX, excY - 8.5f, 2.8f)
        sr.end()

        batch.begin()
        val oldSmallScaleX = smallFont.data.scaleX
        val oldSmallScaleY = smallFont.data.scaleY

        val textX = scrollX + 275f
        if (popupType == SpecialUnlockPopupType.REINFORCEMENTS) {
            smallFont.data.setScale(0.95f)
            smallFont.color = OUTLINE
            smallFont.draw(batch, "YOU CAN SUMMON TROOPS TO HELP", textX, scrollY + scrollH - 82f)
            smallFont.draw(batch, "YOU IN THE BATTLEFIELD.", textX, scrollY + scrollH - 102f)

            smallFont.draw(batch, "REINFORCEMENTS ARE FREE AND", textX, scrollY + scrollH - 142f)
            smallFont.draw(batch, "YOU CAN CALL THEM EVERY 10", textX, scrollY + scrollH - 162f)
            smallFont.draw(batch, "SECONDS.", textX, scrollY + scrollH - 182f)

            smallFont.data.setScale(0.88f)
            smallFont.draw(batch, "REINFORCEMENTS ARE VERY", bannerX + 74f, bannerY + 42f)
            smallFont.draw(batch, "VERSATILE, USE THEM OFTEN.", bannerX + 74f, bannerY + 22f)
        } else {
            smallFont.data.setScale(0.95f)
            smallFont.color = OUTLINE
            smallFont.draw(batch, "BLAST YOUR ENEMIES WITH FIRE", textX, scrollY + scrollH - 76f)
            smallFont.draw(batch, "FROM THE SKIES!", textX, scrollY + scrollH - 96f)

            smallFont.data.setScale(0.86f)
            smallFont.draw(batch, "RAIN OF FIRE IS BEST SAVED TO USE", textX, scrollY + scrollH - 132f)
            smallFont.draw(batch, "ON AN EMERGENCY OR A GREAT", textX, scrollY + scrollH - 150f)
            smallFont.draw(batch, "OPPORTUNITY SINCE IT HAS A VERY", textX, scrollY + scrollH - 168f)
            smallFont.draw(batch, "LONG COOLDOWN.", textX, scrollY + scrollH - 186f)

            smallFont.data.setScale(0.88f)
            smallFont.draw(batch, "ALWAYS AIM A LITTLE BIT AHEAD", bannerX + 74f, bannerY + 42f)
            smallFont.draw(batch, "OF YOUR TARGET.", bannerX + 74f, bannerY + 22f)
        }

        smallFont.data.setScale(oldSmallScaleX, oldSmallScaleY)
        batch.end()
    }

    private fun drawHeavyHelmetBadgeIcon(sr: ShapeRenderer, cx: Float, cy: Float, scale: Float) {
        val s = scale
        sr.color = OUTLINE
        sr.circle(cx, cy + 4f * s, 14f * s)
        sr.rect(cx - 14f * s, cy - 11f * s, 28f * s, 15f * s)
        sr.color = Color(0.75f, 0.80f, 0.86f, 1f)
        sr.circle(cx, cy + 4f * s, 11.5f * s)
        sr.rect(cx - 11.5f * s, cy - 9f * s, 23f * s, 13f * s)
        sr.color = OUTLINE
        sr.rect(cx - 8f * s, cy + 1f * s, 16f * s, 3.5f * s)
        sr.rect(cx - 2f * s, cy - 7f * s, 4f * s, 10f * s)
    }

    private fun drawArmorShieldIcon(sr: ShapeRenderer, cx: Float, cy: Float, scale: Float, isMagic: Boolean = false) {
        val s = scale
        sr.color = OUTLINE
        sr.triangle(cx - 20f * s, cy + 16f * s, cx + 20f * s, cy + 16f * s, cx, cy + 25f * s)
        sr.rect(cx - 20f * s, cy - 5f * s, 40f * s, 21f * s)
        sr.triangle(cx - 20f * s, cy - 5f * s, cx + 20f * s, cy - 5f * s, cx, cy - 26f * s)

        sr.color = if (isMagic) Color(0.18f, 0.72f, 0.94f, 1f) else Color(0.84f, 0.88f, 0.92f, 1f)
        sr.triangle(cx - 17f * s, cy + 14f * s, cx + 17f * s, cy + 14f * s, cx, cy + 21f * s)
        sr.rect(cx - 17f * s, cy - 4f * s, 34f * s, 18f * s)
        sr.triangle(cx - 17f * s, cy - 4f * s, cx + 17f * s, cy - 4f * s, cx, cy - 22f * s)

        sr.color = if (isMagic) Color(0.45f, 0.86f, 0.98f, 1f) else Color(0.55f, 0.60f, 0.65f, 1f)
        sr.rect(cx - 13f * s, cy - 3f * s, 13f * s, 15f * s)
        sr.triangle(cx - 13f * s, cy - 3f * s, cx, cy - 3f * s, cx, cy - 17f * s)

        sr.color = if (isMagic) Color(0.88f, 0.98f, 1f, 1f) else Color.WHITE
        sr.rect(cx, cy - 3f * s, 13f * s, 15f * s)
        sr.triangle(cx, cy - 3f * s, cx + 13f * s, cy - 3f * s, cx, cy - 17f * s)
    }

    private fun drawSwordAndShieldIcon(sr: ShapeRenderer, cx: Float, cy: Float, scale: Float) {
        val s = scale
        sr.color = OUTLINE
        sr.rect(cx - 14f * s, cy - 12f * s, 6f * s, 26f * s)
        sr.color = Color(0.92f, 0.76f, 0.18f, 1f)
        sr.rect(cx - 16f * s, cy - 6f * s, 10f * s, 3.5f * s)
        sr.color = Color.WHITE
        sr.rect(cx - 12.5f * s, cy - 2f * s, 3f * s, 15f * s)
        drawArmorShieldIcon(sr, cx + 4f * s, cy - 1f * s, scale * 0.72f, isMagic = false)
    }

    private fun drawBlueRallyFlagIcon(sr: ShapeRenderer, cx: Float, cy: Float, scale: Float) {
        val s = scale
        sr.color = OUTLINE
        sr.rect(cx - 8f * s, cy - 20f * s, 5.5f * s, 40f * s)
        sr.circle(cx - 5.2f * s, cy + 21f * s, 4f * s)
        sr.triangle(cx - 3f * s, cy + 19f * s, cx - 3f * s, cy + 1f * s, cx + 20f * s, cy + 11f * s)

        sr.color = Color(0.68f, 0.72f, 0.76f, 1f)
        sr.rect(cx - 6.5f * s, cy - 18f * s, 2.8f * s, 36f * s)
        sr.color = Color(0.20f, 0.58f, 0.94f, 1f)
        sr.triangle(cx - 3.5f * s, cy + 17f * s, cx - 3.5f * s, cy + 3f * s, cx + 17f * s, cy + 11f * s)
    }

    private fun drawJaggedParchment(sr: ShapeRenderer, x: Float, y: Float, w: Float, h: Float) {
        sr.color = Color(0f, 0f, 0f, 0.35f)
        sr.rect(x + 6f, y - 6f, w, h)

        sr.color = OUTLINE
        sr.rect(x - 3f, y - 3f, w + 6f, h + 6f)
        sr.color = PARCHMENT_DARK
        sr.rect(x, y, w, h)
        sr.color = PARCHMENT
        sr.rect(x + 6f, y + 6f, w - 12f, h - 12f)

        sr.color = OUTLINE
        sr.triangle(x + 85f, y + h + 3f, x + 102f, y + h + 3f, x + 93f, y + h - 18f)
        sr.triangle(x + w - 140f, y + h + 3f, x + w - 125f, y + h + 3f, x + w - 132f, y + h - 14f)
        sr.triangle(x + 95f, y - 3f, x + 110f, y - 3f, x + 102f, y + 15f)
        sr.triangle(x + w - 180f, y - 3f, x + w - 164f, y - 3f, x + w - 172f, y + 18f)
        sr.triangle(x - 3f, y + h * 0.45f, x - 3f, y + h * 0.45f + 16f, x + 22f, y + h * 0.45f + 8f)
        sr.triangle(x + w + 3f, y + h * 0.72f, x + w + 3f, y + h * 0.72f + 16f, x + w - 22f, y + h * 0.72f + 8f)
    }

    private fun drawTabButton(sr: ShapeRenderer, x: Float, y: Float, w: Float, h: Float, bgColor: Color) {
        sr.color = Color(0f, 0f, 0f, 0.32f)
        sr.rect(x + 4f, y - 4f, w, h)
        sr.color = OUTLINE
        sr.rect(x - 3f, y - 3f, w + 6f, h + 6f)
        sr.color = bgColor
        sr.rect(x, y, w, h)
        sr.color = Color(1f, 1f, 1f, 0.12f)
        sr.rect(x + 5f, y + h - 12f, w - 10f, 7f)
    }

    private fun drawRedArrow(sr: ShapeRenderer, cx: Float, cy: Float) {
        sr.color = Color(0.80f, 0.22f, 0.14f, 1f)
        sr.rect(cx - 18f, cy - 8f, 20f, 16f)
        sr.triangle(cx + 2f, cy + 18f, cx + 2f, cy - 18f, cx + 22f, cy)
    }

    private fun wrapTextLines(text: String, maxCharsPerLine: Int): List<String> {
        val words = text.split(" ")
        val lines = mutableListOf<String>()
        var current = ""
        for (w in words) {
            if (current.isEmpty()) current = w
            else if (current.length + 1 + w.length <= maxCharsPerLine) current += " $w"
            else {
                lines.add(current)
                current = w
            }
        }
        if (current.isNotEmpty()) lines.add(current)
        return lines
    }
}
