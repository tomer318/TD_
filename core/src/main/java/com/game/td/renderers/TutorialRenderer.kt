package com.game.td.renderers

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Align
import com.game.td.Config
import com.game.td.ecs.components.EnemyComponent
import com.game.td.ecs.components.EnemyType
import com.game.td.ecs.components.HealthComponent
import com.game.td.ecs.components.TransformComponent
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
    MAGIC_RESISTANT_ENEMIES
}

object TutorialRenderer {

    private val OUTLINE = Color(0.12f, 0.09f, 0.07f, 1f)
    private val PARCHMENT = Color(0.93f, 0.87f, 0.65f, 1f)
    private val PARCHMENT_DARK = Color(0.85f, 0.78f, 0.54f, 1f)

    // Hàng đợi các huy hiệu thông báo bên mép trái màn hình (NEW ENEMY! & TIP!)
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

    private val activeLeftBadges = mutableListOf<LeftBadgeItem>()
    private var showClickHereOnFirstBadge: Boolean = false

    private var currentLevelNum: Int = 1
    private var currentMode: GameMode = GameMode.CAMPAIGN

    // Trạng thái hướng dẫn & khóa Spell
    var isLevel1CampaignActive: Boolean = false
        private set

    var isInstructionOpen: Boolean = false
        private set
    var instructionPage: Int = 1
        private set

    // Bảng NEW TOWER UPGRADES! (0 = đóng, 2 = Mở tháp Lv2 ở Màn 2, 3 = Mở tháp Lv3 ở Màn 4)
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

    // Khởi tạo lại mỗi khi bắt đầu màn chơi (startNewGame)
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

            // Kiểm tra bật bảng NEW TOWER UPGRADES! (Chỉ hiện 1 lần duy nhất trên mỗi File Save)
            if (gameMode == GameMode.CAMPAIGN) {
                val prefs = Gdx.app.getPreferences("kr_save_slot_${SaveManager.activeSlot}")
                if (levelNumber == 2 && !prefs.getBoolean("seen_tower_upgrade_lvl2", false)) {
                    prefs.putBoolean("seen_tower_upgrade_lvl2", true)
                    prefs.flush()
                    activeTowerUpgradePopupLevel = 2
                    SoundManager.playTowerUpgrade()
                } else if (levelNumber == 4 && !prefs.getBoolean("seen_tower_upgrade_lvl3", false)) {
                    prefs.putBoolean("seen_tower_upgrade_lvl3", true)
                    prefs.flush()
                    activeTowerUpgradePopupLevel = 3
                    SoundManager.playTowerUpgrade()
                }
            }
        }
    }

    // Kiểm tra xem có đang mở bảng modal nào cần tạm dừng trận đấu không
    fun isModalOpen(): Boolean {
        return isInstructionOpen ||
            activeTowerUpgradePopupLevel > 0 ||
            activeSpecialPopup != SpecialUnlockPopupType.NONE ||
            activeEnemyPopup != null ||
            activeHintPopup != HintPopupType.NONE
    }

    // GỌI KHI QUÁI VẬT XUẤT HIỆN TRÊN BẢN ĐỒ:
    fun onEnemySpawned(enemyType: EnemyType) {
        if (enemyType == EnemyType.SPIDER_HATCHLING) return

        // 1. Kiểm tra bật huy hiệu NEW ENEMY! (1 lần duy nhất cho mỗi File Save)
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

        // 2. Ở Màn 3 Campaign: Khi Shaman xuất hiện -> Luôn bật huy hiệu TIP! (MAGIC RESISTANT ENEMIES!)
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

    // Tự động kiểm tra Wave hiện tại để bật Spell và Hint cho Màn 1, Màn 2, Màn 3
    fun checkWaveTriggers(levelNumber: Int, gameMode: GameMode, currentWaveDisplay: Int, totalWaves: Int) {
        if (gameMode != GameMode.CAMPAIGN || isModalOpen()) return

        // --- MÀN 1 CAMPAIGN ---
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

        // --- MÀN 2 CAMPAIGN: Wave 1 luôn xuất hiện TIP! (STRATEGY BASICS!) ---
        if (levelNumber == 2 && currentWaveDisplay == 1 && !hasTriggeredLevel2StrategyHint) {
            hasTriggeredLevel2StrategyHint = true
            activeLeftBadges.add(LeftBadgeItem.TipBadge(HintPopupType.STRATEGY_BASICS))
            showClickHereOnFirstBadge = true
            SoundManager.playTowerPreview()
        }
    }

    // Xử lý khi người chơi bấm vào các huy hiệu tròn NEW ENEMY! hoặc TIP! ở mép trái màn hình
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

    // Xử lý chạm trên các bảng Instructions, New Tower Upgrades, New Special, New Enemy & Hint
    fun handleTouch(
        x: Float,
        y: Float,
        onUnlockReinforcements: () -> Unit = {},
        onUnlockRainOfFire: () -> Unit = {}
    ): Boolean {
        val cX = Config.WORLD_WIDTH / 2f
        val cY = Config.WORLD_HEIGHT / 2f

        // 1. Đang mở bảng INSTRUCTIONS (Trang 1, 2, 3)
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

        // 2. Đang mở bảng NEW TOWER UPGRADES! (Màn 2 hoặc Màn 4)
        if (activeTowerUpgradePopupLevel > 0) {
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
            return true
        }

        // 3. Đang mở bảng NEW SPECIAL AVAILABLE! (Wave 3 hoặc Wave cuối)
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

        // 4. Đang mở bảng NEW ENEMY!
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

        // 5. Đang mở bảng HINT
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

    // =========================================================================
    // VẼ BONG BÓNG CHỈ DẪN TRÊN BẢN ĐỒ & CÁC HUY HIỆU "NEW ENEMY!" / "TIP!" BÊN TRÁI
    // =========================================================================
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

                    drawDummyEnemyEntity(sr, item.enemyType, badgeX - 2f, badgeY - 4f)

                    val exX = badgeX + 22f
                    val exY = badgeY - 2f
                    sr.color = OUTLINE
                    sr.triangle(exX - 6f, exY + 20f, exX + 8f, exY + 18f, exX - 1f, exY - 4f)
                    sr.circle(exX - 2f, exY - 11f, 5.5f)
                    sr.color = Color(1f, 0.88f, 0.15f, 1f)
                    sr.triangle(exX - 4f, exY + 18f, exX + 5.5f, exY + 16.5f, exX - 1f, exY - 2f)
                    sr.circle(exX - 2f, exY - 11f, 3.8f)

                    sr.color = OUTLINE
                    sr.rect(badgeX - 30f, badgeY + 17f, 60f, 14f)
                    sr.end()

                    batch.begin()
                    smallFont.data.setScale(0.66f)
                    GameUIRenderer.drawBadgeCenteredText(
                        batch, smallFont, "NEW ENEMY",
                        badgeX - 30f, badgeY + 17f, 60f, 14f,
                        Color(1f, 0.90f, 0.18f, 1f)
                    )
                    batch.end()
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
                    sr.end()

                    batch.begin()
                    smallFont.data.setScale(0.72f)
                    GameUIRenderer.drawBadgeCenteredText(
                        batch, smallFont, "TIP",
                        badgeX - 18f, badgeY + 17f, 36f, 14f,
                        Color(0.55f, 0.92f, 1f, 1f)
                    )
                    batch.end()
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
        sr.end()

        batch.begin()
        if (line2.isEmpty()) {
            font.data.setScale(1.02f)
            font.color = OUTLINE
            font.draw(batch, line1, boxX, boxY + boxH / 2f + 6f, boxW, Align.center, false)
        } else {
            font.data.setScale(1.00f)
            font.color = OUTLINE
            font.draw(batch, line1, boxX + 12f, boxY + boxH - 12f)
            font.data.setScale(0.82f)
            font.color = Color(0.25f, 0.20f, 0.14f, 1f)
            font.draw(batch, line2, boxX + 12f, boxY + 18f)
        }
        batch.end()
    }

    // =========================================================================
    // VẼ CÁC BẢNG POPUP: INSTRUCTIONS, NEW TOWER UPGRADES, NEW SPECIAL, NEW ENEMY & HINT
    // =========================================================================
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
            activeTowerUpgradePopupLevel > 0 -> renderNewTowerUpgradesPopup(sr, batch, font, smallFont, activeTowerUpgradePopupLevel)
            activeSpecialPopup != SpecialUnlockPopupType.NONE -> renderSpecialUnlockPopup(sr, batch, font, smallFont, activeSpecialPopup)
            activeEnemyPopup != null -> renderNewEnemyPopup(sr, batch, font, smallFont, activeEnemyPopup!!)
            activeHintPopup != HintPopupType.NONE -> renderHintPopup(sr, batch, font, smallFont, activeHintPopup)
        }
    }

    // =========================================================================
    // PHẦN 5 (MỚI): BẢNG "NEW TOWER UPGRADES!" CHO MÀN 2 (LVL 2) & MÀN 4 (LVL 3)
    // =========================================================================
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

        // 1. Nút OK! màu xanh dương ở góc dưới phải
        drawTabButton(sr, okBtnX, okBtnY, okBtnW, okBtnH, Color(0.16f, 0.38f, 0.58f, 1f))

        // 2. Bảng nâu "NEW TOWER UPGRADES!" trên đỉnh lệch trái (giống Hình 3 & 7)
        sr.color = OUTLINE
        sr.rect(tabX - 3f, tabY - 3f, tabW + 6f, tabH + 6f)
        sr.color = Color(0.44f, 0.34f, 0.25f, 1f)
        sr.rect(tabX, tabY, tabW, tabH)

        // 3. Tờ giấy da dê chính
        drawJaggedParchment(sr, scrollX, scrollY, scrollW, scrollH)

        // 4. Vẽ 4 tấm ảnh Polaroid của 4 loại tháp (Barracks, Archer, Mage, Artillery)
        val cardW = 166f
        val cardH = 162f
        val cardCentersX = listOf(cX - 270f, cX - 90f, cX + 90f, cX + 270f)
        val cardOffsetsY = listOf(scrollY + 12f, scrollY + 26f, scrollY + 14f, scrollY + 18f)

        for (i in 0..3) {
            val px = cardCentersX[i] - cardW / 2f
            val py = cardOffsetsY[i]

            // Khung Polaroid kem viền nâu
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

            // Nền cỏ & đường đất trong mỗi tấm ảnh Polaroid
            sr.color = Color(0.42f, 0.62f, 0.18f, 1f)
            sr.rect(inX, inY, inW, inH)
            sr.color = Color(0.84f, 0.74f, 0.46f, 1f)
            sr.rect(inX, inY, inW, inH * 0.52f)

            val cx = inX + inW / 2f
            val cy = inY + inH / 2f

            when (i) {
                0 -> {
                    // CỘT 1: BARRACKS (Lv2 Footmen hoặc Lv3 Knights)
                    drawMiniBarracksTower(sr, cx, cy + 16f, 1.35f, tier = unlockedTier)
                    drawMiniSoldier(sr, cx - 36f, cy - 30f, tier = unlockedTier)
                    drawMiniSoldier(sr, cx - 2f, cy - 38f, tier = unlockedTier)
                    drawMiniSoldier(sr, cx + 34f, cy - 26f, tier = unlockedTier)
                }
                1 -> {
                    // CỘT 2: ARCHER TOWER (Lv2 Marksmen hoặc Lv3 Sharpshooter)
                    drawMiniArcherTower(sr, cx, cy - 6f, 1.45f, tier = unlockedTier)
                }
                2 -> {
                    // CỘT 3: MAGE TOWER (Lv2 Adept hoặc Lv3 Wizard)
                    drawMiniMageTower(sr, cx, cy - 8f, 1.45f, tier = unlockedTier)
                }
                3 -> {
                    // CỘT 4: ARTILLERY (Lv2 Dwarven Artillery hoặc Lv3 Howitzer)
                    drawMiniArtilleryTower(sr, cx + 8f, cy - 8f, 1.40f, tier = unlockedTier)
                    // Các thùng thuốc nổ đỏ (Lv2) hoặc đạn pháo lớn (Lv3) xếp bên trái
                    if (unlockedTier == 2) {
                        sr.color = OUTLINE
                        sr.rect(cx - 52f, cy - 18f, 16f, 22f)
                        sr.rect(cx - 38f, cy - 26f, 16f, 22f)
                        sr.color = Color(0.86f, 0.24f, 0.12f, 1f)
                        sr.rect(cx - 50f, cy - 16f, 12f, 18f)
                        sr.rect(cx - 36f, cy - 24f, 12f, 18f)
                    } else {
                        sr.color = OUTLINE
                        sr.rect(cx - 52f, cy - 20f, 14f, 26f)
                        sr.rect(cx - 36f, cy - 26f, 14f, 26f)
                        sr.color = Color(0.55f, 0.58f, 0.62f, 1f)
                        sr.rect(cx - 50f, cy - 18f, 10f, 22f)
                        sr.rect(cx - 34f, cy - 24f, 10f, 22f)
                    }
                }
            }
        }
        sr.end()

        // 5. Vẽ chữ cho bảng NEW TOWER UPGRADES!
        batch.begin()
        val oldFontScaleX = font.data.scaleX
        val oldFontScaleY = font.data.scaleY
        val oldSmallScaleX = smallFont.data.scaleX
        val oldSmallScaleY = smallFont.data.scaleY

        font.data.setScale(1.85f)
        GameUIRenderer.drawBadgeCenteredText(
            batch, font, "NEW TOWER UPGRADES!",
            tabX, tabY + 6f, tabW, tabH,
            Color(0.96f, 0.92f, 0.72f, 1f)
        )

        font.data.setScale(1.65f)
        font.color = OUTLINE
        font.draw(
            batch,
            "TOWERS LEVEL $unlockedTier AVAILABLE",
            scrollX, scrollY + scrollH - 36f, scrollW, Align.center, false
        )

        smallFont.data.setScale(1.02f)
        smallFont.color = OUTLINE
        smallFont.draw(
            batch,
            "YOU CAN NOW UPGRADE YOUR TOWERS UP TO LEVEL $unlockedTier.",
            scrollX, scrollY + scrollH - 72f, scrollW, Align.center, false
        )

        font.data.setScale(1.60f)
        GameUIRenderer.drawBadgeCenteredText(batch, font, "OK!", okBtnX, okBtnY, okBtnW, okBtnH - 4f, Color.WHITE)

        font.data.setScale(oldFontScaleX, oldFontScaleY)
        smallFont.data.setScale(oldSmallScaleX, oldSmallScaleY)
        batch.end()
    }

    // =========================================================================
    // PHẦN 3: BẢNG GIỚI THIỆU QUÁI MỚI ("NEW ENEMY!") CHUẨN HÌNH 2, 3 & 5
    // =========================================================================
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
            isWastes -> Color(0.48f, 0.45f, 0.48f, 1f)
            else -> Color(0.84f, 0.74f, 0.46f, 1f)
        }
        sr.rect(innerX, innerY, innerW, innerH * 0.62f)

        when (enemyType) {
            EnemyType.GOBLIN -> {
                val gobPts = listOf(
                    Vector2(innerX + 42f, innerY + 62f),
                    Vector2(innerX + 82f, innerY + 86f),
                    Vector2(innerX + 68f, innerY + 38f),
                    Vector2(innerX + 122f, innerY + 36f),
                    Vector2(innerX + 162f, innerY + 66f)
                )
                for (gp in gobPts) drawDummyEnemyEntity(sr, EnemyType.GOBLIN, gp.x, gp.y)
            }
            EnemyType.ORC -> {
                drawDummyEnemyEntity(sr, EnemyType.ORC, innerX + 38f, innerY + 58f)
                drawDummyEnemyEntity(sr, EnemyType.ORC, innerX + 82f, innerY + 92f)
                drawDummyEnemyEntity(sr, EnemyType.ORC, innerX + 115f, innerY + 46f)
                drawMiniSoldier(sr, innerX + 125f, innerY + 102f)
                drawMiniSoldier(sr, innerX + 155f, innerY + 46f)
            }
            EnemyType.SHAMAN -> {
                // Hình Polaroid Shaman chuẩn Hình 5: Shaman giơ trượng phát sáng cam hồi máu cho đồng bọn
                drawDummyEnemyEntity(sr, EnemyType.SHAMAN, innerX + 42f, innerY + 68f)
                drawDummyEnemyEntity(sr, EnemyType.SHAMAN, innerX + 96f, innerY + 95f)
                // Hào quang hồi máu cam vàng quanh 2 đồng minh phía trước
                for (hpPos in listOf(Vector2(innerX + 105f, innerY + 46f), Vector2(innerX + 162f, innerY + 56f))) {
                    sr.color = Color(1f, 0.62f, 0.12f, 0.65f)
                    sr.circle(hpPos.x, hpPos.y, 20f)
                    drawDummyEnemyEntity(sr, EnemyType.ORC, hpPos.x, hpPos.y)
                    sr.color = Color(1f, 0.92f, 0.35f, 0.9f)
                    sr.rect(hpPos.x - 6f, hpPos.y - 2f, 12f, 4f)
                    sr.rect(hpPos.x - 2f, hpPos.y - 6f, 4f, 12f)
                }
            }
            else -> {
                drawDummyEnemyEntity(sr, enemyType, innerX + innerW / 2f, innerY + 65f)
            }
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
        val oldFontScaleX = font.data.scaleX
        val oldFontScaleY = font.data.scaleY
        val oldSmallScaleX = smallFont.data.scaleX
        val oldSmallScaleY = smallFont.data.scaleY

        font.data.setScale(1.85f)
        GameUIRenderer.drawBadgeCenteredText(
            batch, font, "NEW ENEMY!",
            tabX, tabY + 6f, tabW, tabH,
            Color(0.96f, 0.92f, 0.72f, 1f)
        )

        font.data.setScale(1.60f)
        GameUIRenderer.drawBadgeCenteredText(batch, font, "OK!", okBtnX, okBtnY, okBtnW, okBtnH - 4f, Color.WHITE)

        val textX = scrollX + 275f
        font.data.setScale(1.55f)
        font.color = OUTLINE
        font.draw(batch, info.title, textX, scrollY + scrollH - 36f)

        smallFont.data.setScale(0.92f)
        smallFont.color = OUTLINE
        val descLines = wrapTextLines(info.description, 34)
        var curY = scrollY + scrollH - 72f
        for (line in descLines) {
            smallFont.draw(batch, line, textX, curY)
            curY -= 19f
        }

        curY -= 10f
        smallFont.data.setScale(0.98f)
        for (b in info.bullets) {
            smallFont.color = OUTLINE
            smallFont.draw(batch, "-", textX, curY)
            smallFont.color = Color(0.68f, 0.22f, 0.10f, 1f)
            smallFont.draw(batch, b, textX + 18f, curY)
            curY -= 22f
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

        font.data.setScale(oldFontScaleX, oldFontScaleY)
        smallFont.data.setScale(oldSmallScaleX, oldSmallScaleY)
        batch.end()
    }

    // =========================================================================
    // PHẦN 4: BẢNG MẸO CHIẾN THUẬT ("HINT") CHO MÀN 1, MÀN 2 & MÀN 3
    // =========================================================================
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

        when (hintType) {
            HintPopupType.ARMORED_ENEMIES -> {
                drawArmorShieldIcon(sr, scrollX + 92f, scrollY + scrollH - 82f, 1.85f, isMagic = false)

                val diagramY = scrollY + 138f
                drawDummyEnemyEntity(sr, EnemyType.ORC, scrollX + 125f, diagramY, scale = 1.45f)

                val arrowStart = Vector2(scrollX + 172f, diagramY + 8f)
                val arrowMid = Vector2(scrollX + 220f, diagramY + 15f)
                val arrowEnd = Vector2(scrollX + 266f, diagramY + 4f)
                sr.color = Color(0.76f, 0.22f, 0.08f, 1f)
                sr.rectLine(arrowStart, arrowMid, 4.5f)
                sr.rectLine(arrowMid, arrowEnd, 4.5f)
                sr.triangle(arrowEnd.x + 8f, arrowEnd.y - 2f, arrowEnd.x - 10f, arrowEnd.y + 10f, arrowEnd.x - 8f, arrowEnd.y - 10f)

                drawMiniArcherTower(sr, scrollX + 345f, diagramY - 2f, 1.05f)
                drawMiniBarracksTower(sr, scrollX + 435f, diagramY - 2f, 1.05f)
                drawMiniSoldier(sr, scrollX + 455f, diagramY - 18f)
                drawMiniArtilleryTower(sr, scrollX + 528f, diagramY - 2f, 1.05f)
            }

            HintPopupType.MAGIC_RESISTANT_ENEMIES -> {
                // --- HÌNH 6 (MÀN 3): MAGIC RESISTANT ENEMIES! ---
                drawArmorShieldIcon(sr, scrollX + 92f, scrollY + scrollH - 82f, 1.85f, isMagic = true)

                val diagramY = scrollY + 142f
                drawDummyEnemyEntity(sr, EnemyType.SHAMAN, scrollX + 205f, diagramY, scale = 1.45f)

                val arrowStart = Vector2(scrollX + 252f, diagramY + 8f)
                val arrowMid = Vector2(scrollX + 305f, diagramY + 15f)
                val arrowEnd = Vector2(scrollX + 355f, diagramY + 4f)
                sr.color = Color(0.76f, 0.22f, 0.08f, 1f)
                sr.rectLine(arrowStart, arrowMid, 4.5f)
                sr.rectLine(arrowMid, arrowEnd, 4.5f)
                sr.triangle(arrowEnd.x + 8f, arrowEnd.y - 2f, arrowEnd.x - 10f, arrowEnd.y + 10f, arrowEnd.x - 8f, arrowEnd.y - 10f)

                drawMiniMageTower(sr, scrollX + 425f, diagramY - 2f, 1.25f)
            }

            HintPopupType.STRATEGY_BASICS -> {
                // --- HÌNH 4 (MÀN 2): STRATEGY BASICS! (BẢN ĐỒ HỖ TRỢ TRẠI LÍNH) ---
                val mapW = 540f
                val mapH = 210f
                val mapX = cX - mapW / 2f
                val mapY = scrollY + 24f

                sr.color = Color(0.75f, 0.68f, 0.46f, 1f)
                sr.rect(mapX - 3f, mapY - 3f, mapW + 6f, mapH + 6f)
                sr.color = Color(0.54f, 0.72f, 0.22f, 1f)
                sr.rect(mapX, mapY, mapW, mapH)

                // Con đường chữ S đi qua cụm tháp phòng thủ
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

                // Vòng tròn đỏ dưới chân 3 tháp tầm xa (Mage, Archer, Artillery)
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

                drawMiniMageTower(sr, magePos.x, magePos.y, 0.90f)
                drawMiniArcherTower(sr, archPos.x, archPos.y, 0.90f)
                drawMiniBarracksTower(sr, barrPos.x, barrPos.y, 0.90f)
                drawMiniArtilleryTower(sr, artPos.x, artPos.y, 0.90f)

                // Lính chặn đánh quái ở giữa đường + 3 mũi tên đỏ từ 3 tháp tầm xa bắn hỗ trợ
                val chokeX = mapX + 235f
                val chokeY = mapY + 102f
                drawMiniSoldier(sr, chokeX - 8f, chokeY + 6f)
                drawMiniSoldier(sr, chokeX + 14f, chokeY - 4f)
                drawMiniSoldier(sr, chokeX - 18f, chokeY - 16f)
                drawMiniGoblin(sr, chokeX - 22f, chokeY + 6f, 0.85f)
                drawMiniGoblin(sr, chokeX + 26f, chokeY - 2f, 0.85f)
                drawMiniGoblin(sr, chokeX - 4f, chokeY - 16f, 0.85f)

                sr.color = Color(0.86f, 0.22f, 0.10f, 1f)
                sr.rectLine(magePos.x + 18f, magePos.y + 18f, chokeX - 6f, chokeY + 24f, 3.8f)
                sr.rectLine(archPos.x - 24f, archPos.y + 12f, chokeX + 16f, chokeY + 22f, 3.8f)
                sr.rectLine(artPos.x + 18f, artPos.y + 24f, chokeX - 28f, chokeY - 12f, 3.8f)

                // Khung giấy ghi chú trắng kem: "SUPPORT YOUR SOLDIERS WITH RANGED TOWERS!"
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

            else -> {
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

                drawMiniBarracksTower(sr, wheelCx, wheelCy, 1.02f)

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

                drawMiniBarracksTower(sr, rangeCx - 10f, rangeCy + 14f, 1.15f)
                drawMiniSoldier(sr, rangeCx - 58f, rangeCy - 36f)
                drawMiniSoldier(sr, rangeCx - 36f, rangeCy - 22f)
                drawMiniSoldier(sr, rangeCx - 14f, rangeCy - 38f)

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
        val oldFontScaleX = font.data.scaleX
        val oldFontScaleY = font.data.scaleY
        val oldSmallScaleX = smallFont.data.scaleX
        val oldSmallScaleY = smallFont.data.scaleY

        font.data.setScale(1.95f)
        GameUIRenderer.drawBadgeCenteredText(
            batch, font, "HINT",
            tabX, tabY + 6f, tabW, tabH,
            Color(0.96f, 0.92f, 0.72f, 1f)
        )

        font.data.setScale(1.60f)
        GameUIRenderer.drawBadgeCenteredText(batch, font, "OK!", okBtnX, okBtnY, okBtnW, okBtnH - 4f, Color.WHITE)

        val headerX = scrollX + 170f
        when (hintType) {
            HintPopupType.ARMORED_ENEMIES -> {
                font.data.setScale(1.60f)
                font.color = OUTLINE
                font.draw(batch, "ARMORED ENEMIES!", headerX, scrollY + scrollH - 42f)

                smallFont.data.setScale(0.95f)
                smallFont.color = Color(0.24f, 0.20f, 0.16f, 1f)
                smallFont.draw(batch, "SOME ENEMIES WEAR DIFFERENT LEVELS OF", headerX, scrollY + scrollH - 76f)
                smallFont.draw(batch, "ARMOR THAT PROTECTS THEM AGAINST", headerX, scrollY + scrollH - 96f)
                smallFont.draw(batch, "NON-MAGICAL ATTACKS.", headerX, scrollY + scrollH - 116f)

                smallFont.data.setScale(0.82f)
                smallFont.color = Color(0.72f, 0.22f, 0.08f, 1f)
                smallFont.draw(batch, "RESISTS", scrollX + 150f, scrollY + 195f, 140f, Align.center, false)
                smallFont.draw(batch, "DAMAGE FROM", scrollX + 150f, scrollY + 178f, 140f, Align.center, false)

                smallFont.data.setScale(1.00f)
                smallFont.color = OUTLINE
                smallFont.draw(batch, "ARMORED ENEMIES TAKE ", scrollX + 95f, scrollY + 70f)
                smallFont.color = Color(0.74f, 0.22f, 0.08f, 1f)
                smallFont.draw(batch, "LESS DAMAGE", scrollX + 340f, scrollY + 70f)
                smallFont.color = OUTLINE
                smallFont.draw(batch, " FROM", scrollX + 485f, scrollY + 70f)

                smallFont.color = Color(0.74f, 0.22f, 0.08f, 1f)
                smallFont.draw(batch, "MARKSMEN, SOLDIERS", scrollX + 135f, scrollY + 46f)
                smallFont.color = OUTLINE
                smallFont.draw(batch, " AND ", scrollX + 365f, scrollY + 46f)
                smallFont.color = Color(0.74f, 0.22f, 0.08f, 1f)
                smallFont.draw(batch, "ARTILLERY.", scrollX + 422f, scrollY + 46f)
            }

            HintPopupType.MAGIC_RESISTANT_ENEMIES -> {
                font.data.setScale(1.55f)
                font.color = OUTLINE
                font.draw(batch, "MAGIC RESISTANT ENEMIES!", headerX, scrollY + scrollH - 42f)

                smallFont.data.setScale(0.95f)
                smallFont.color = Color(0.24f, 0.20f, 0.16f, 1f)
                smallFont.draw(batch, "SOME ENEMIES ENJOY DIFFERENT LEVELS OF", headerX, scrollY + scrollH - 76f)
                smallFont.draw(batch, "MAGIC RESISTANCE THAT PROTECTS THEM", headerX, scrollY + scrollH - 96f)
                smallFont.draw(batch, "AGAINST MAGICAL ATTACKS.", headerX, scrollY + scrollH - 116f)

                smallFont.data.setScale(0.82f)
                smallFont.color = Color(0.72f, 0.22f, 0.08f, 1f)
                smallFont.draw(batch, "RESISTS", scrollX + 235f, scrollY + 198f, 140f, Align.center, false)
                smallFont.draw(batch, "DAMAGE FROM", scrollX + 235f, scrollY + 181f, 140f, Align.center, false)

                smallFont.data.setScale(1.02f)
                smallFont.color = OUTLINE
                smallFont.draw(batch, "MAGIC RESISTANT ENEMIES TAKE", scrollX, scrollY + 72f, scrollW, Align.center, false)

                smallFont.color = Color(0.74f, 0.22f, 0.08f, 1f)
                smallFont.draw(batch, "LESS DAMAGE", scrollX + 180f, scrollY + 46f)
                smallFont.color = OUTLINE
                smallFont.draw(batch, " FROM ", scrollX + 332f, scrollY + 46f)
                smallFont.color = Color(0.74f, 0.22f, 0.08f, 1f)
                smallFont.draw(batch, "WIZARDS.", scrollX + 402f, scrollY + 46f)
            }

            HintPopupType.STRATEGY_BASICS -> {
                font.data.setScale(1.60f)
                font.color = OUTLINE
                font.draw(batch, "STRATEGY BASICS!", scrollX, scrollY + scrollH - 36f, scrollW, Align.center, false)

                smallFont.data.setScale(0.94f)
                smallFont.color = OUTLINE
                smallFont.draw(batch, "BARRACKS ARE ", scrollX + 75f, scrollY + scrollH - 72f)
                smallFont.color = Color(0.74f, 0.22f, 0.08f, 1f)
                smallFont.draw(batch, "GOOD FOR BLOCKING THE ENEMY,", scrollX + 222f, scrollY + scrollH - 72f)

                smallFont.color = OUTLINE
                smallFont.draw(batch, "BUT ", scrollX + 75f, scrollY + scrollH - 94f)
                smallFont.color = Color(0.74f, 0.22f, 0.08f, 1f)
                smallFont.draw(batch, "LACK IN ATTACK POWER.", scrollX + 120f, scrollY + scrollH - 94f)
                smallFont.color = OUTLINE
                smallFont.draw(batch, " MAKE SURE YOU HAVE", scrollX + 355f, scrollY + scrollH - 94f)
                smallFont.draw(batch, "ENOUGH FIREPOWER TO SUPPORT THEM!", scrollX + 75f, scrollY + scrollH - 116f)

                val mapX = cX - 270f
                val mapY = scrollY + 24f
                smallFont.data.setScale(0.78f)
                smallFont.color = OUTLINE
                smallFont.draw(batch, "SUPPORT YOUR", mapX + 235f, mapY + 62f, 195f, Align.center, false)
                smallFont.draw(batch, "SOLDIERS WITH", mapX + 235f, mapY + 44f, 195f, Align.center, false)
                smallFont.draw(batch, "RANGED TOWERS!", mapX + 235f, mapY + 26f, 195f, Align.center, false)
            }

            else -> {
                font.data.setScale(1.60f)
                font.color = OUTLINE
                font.draw(batch, "COMMAND YOUR TROOPS!", headerX, scrollY + scrollH - 42f)

                smallFont.data.setScale(0.95f)
                smallFont.color = Color(0.24f, 0.20f, 0.16f, 1f)
                smallFont.draw(batch, "YOU CAN ADJUST YOUR SOLDIERS RALLY", headerX, scrollY + scrollH - 76f)
                smallFont.draw(batch, "POINT TO MAKE THEM DEFEND A", headerX, scrollY + scrollH - 96f)
                smallFont.draw(batch, "DIFFERENT AREA.", headerX, scrollY + scrollH - 116f)

                smallFont.data.setScale(0.72f)
                GameUIRenderer.drawBadgeCenteredText(batch, smallFont, "200", scrollX + 148f - 18f, scrollY + 135f + 62f - 28f, 36f, 14f, Color.WHITE)
                GameUIRenderer.drawBadgeCenteredText(batch, smallFont, "$", scrollX + 148f - 15f, scrollY + 135f - 62f - 13f, 30f, 26f, Color.WHITE)

                smallFont.data.setScale(0.76f)
                smallFont.color = Color(0.16f, 0.52f, 0.82f, 1f)
                smallFont.draw(batch, "RALLY RANGE", scrollX + 375f, scrollY + 202f, 140f, Align.center, false)

                smallFont.data.setScale(0.80f)
                smallFont.color = Color(0.74f, 0.22f, 0.08f, 1f)
                smallFont.draw(batch, "SELECT THE RALLY", scrollX + 115f, scrollY + 48f, 190f, Align.center, false)
                smallFont.draw(batch, "POINT CONTROL", scrollX + 115f, scrollY + 31f, 190f, Align.center, false)

                smallFont.draw(batch, "SELECT WHERE YOU WANT", scrollX + 345f, scrollY + 52f, 230f, Align.center, false)
                smallFont.draw(batch, "TO MOVE YOUR SOLDIERS", scrollX + 345f, scrollY + 35f, 230f, Align.center, false)
            }
        }

        font.data.setScale(oldFontScaleX, oldFontScaleY)
        smallFont.data.setScale(oldSmallScaleX, oldSmallScaleY)
        batch.end()
    }

    // =========================================================================
    // CƠ SỞ DỮ LIỆU THÔNG TIN QUÁI MỚI ("NEW ENEMY!") CHUẨN WIKI KINGDOM RUSH
    // =========================================================================
    private fun getEnemyCardDetails(type: EnemyType): EnemyCardDetails = when (type) {
        EnemyType.GOBLIN -> EnemyCardDetails(
            title = "GOBLINS",
            description = "SMALL EVIL HUMANOIDS WITH NO OUTSTANDING ABILITIES.",
            bullets = listOf("LOW HEALTH", "AVERAGE SPEED")
        )
        EnemyType.ORC -> EnemyCardDetails(
            title = "ORCS",
            description = "TOUGH SAVAGES WITH LIGHT ARMOR.",
            bullets = listOf("LIGHT ARMOR *", "SLOW SPEED"),
            footnote = "* ARMORED ENEMIES RECEIVE LESS DAMAGE FROM ARCHERS AND SOLDIERS",
            bottomTip1 = "MAGIC DAMAGE IGNORES",
            bottomTip2 = "ARMOR COMPLETELY"
        )
        EnemyType.SHAMAN -> EnemyCardDetails(
            title = "SHAMAN",
            description = "TRIBAL HEALERS RESISTANT TO MAGIC ATTACKS",
            bullets = listOf("HIGH MAGIC RESISTANCE *", "CAN HEAL ALLIES", "AVERAGE SPEED"),
            footnote = "* RESISTS DAMAGE FROM MAGE TOWERS",
            bottomTip1 = "ARCHER TOWERS ARE THE BEST",
            bottomTip2 = "WAY TO DEAL WITH SHAMANS"
        )
        EnemyType.OGRE -> EnemyCardDetails(
            title = "OGRES",
            description = "BIG, UGLY AND TOUGH HUMANOIDS. THEY'LL OBLITERATE EVERYTHING IN THEIR PATH.",
            bullets = listOf("VERY HIGH HEALTH", "VERY SLOW SPEED", "COSTS 3 LIVES"),
            bottomTip1 = "MAGES AND ARTILLERY ARE GREAT",
            bottomTip2 = "TO SUPPORT YOUR SOLDIERS."
        )
        EnemyType.BANDIT -> EnemyCardDetails(
            title = "BANDITS",
            description = "SWIFT & DEADLY, THEY CAN DISPATCH THEIR ENEMIES VERY QUICKLY.",
            bullets = listOf("FAST SPEED", "VERY HIGH DAMAGE", "CAN DODGE MELEE ATTACKS")
        )
        EnemyType.BRIGAND -> EnemyCardDetails(
            title = "BRIGANDS",
            description = "ARMORED OUTLAWS THAT CAN WITHSTAND A LOT OF PUNISHMENT.",
            bullets = listOf("MEDIUM ARMOR *", "AVERAGE SPEED"),
            footnote = "* ARMORED ENEMIES RECEIVE LESS DAMAGE FROM ARCHERS AND SOLDIERS"
        )
        EnemyType.WULF -> EnemyCardDetails(
            title = "WULVES",
            description = "VERY FAST VICIOUS CREATURES THAT CAN DODGE MELEE ATTACKS.",
            bullets = listOf("LOW HEALTH", "VERY FAST SPEED", "CAN DODGE MELEE ATTACKS")
        )
        EnemyType.MARAUDER -> EnemyCardDetails(
            title = "MARAUDERS",
            description = "HULKING WELL-TRAINED ARMORED OUTLAWS, OFTEN SEEN LEADING BRIGANDS.",
            bullets = listOf("HEAVY ARMOR", "SLOW SPEED", "COSTS 3 LIVES")
        )
        EnemyType.GIANT_SPIDER -> EnemyCardDetails(
            title = "GIANT SPIDERS",
            description = "QUICK, VICIOUS ENEMIES RESISTANT TO MAGIC.",
            bullets = listOf("HIGH MAGIC RESISTANCE", "LOW HEALTH", "FAST SPEED")
        )
        EnemyType.SPIDER_MATRIARCH -> EnemyCardDetails(
            title = "SPIDER MATRIARCHS",
            description = "TOUGH MAGIC RESISTANT BEASTS THAT CAN SPAWN SEVERAL SPIDERLINGS.",
            bullets = listOf("HIGH MAGIC RESISTANCE", "SPAWNS SPIDERLINGS", "COSTS 2 LIVES")
        )
        EnemyType.GARGOYLE -> EnemyCardDetails(
            title = "GARGOYLES",
            description = "VILE WINGED CREATURES THAT CAN FLY PAST BARRACKS AND ARTILLERY.",
            bullets = listOf("FLYING (CANNOT BE BLOCKED)", "AVERAGE SPEED"),
            bottomTip1 = "FLYING ENEMIES CANNOT BE BLOCKED",
            bottomTip2 = "BY BARRACKS OR TARGETED BY ARTILLERY"
        )
        EnemyType.SHADOW_ARCHER -> EnemyCardDetails(
            title = "SHADOW ARCHERS",
            description = "DEADLY ACCURATE ARCHERS THAT WILL SHOOT AT YOUR NEARBY SOLDIERS.",
            bullets = listOf("RANGED ATTACK", "LOW MAGIC RESISTANCE", "AVERAGE SPEED")
        )
        EnemyType.DARK_KNIGHT -> EnemyCardDetails(
            title = "DARK KNIGHTS",
            description = "FULL PLATED CORRUPTED KNIGHTS, ALMOST IMPERVIOUS TO DAMAGE.",
            bullets = listOf("HEAVY ARMOR", "SLOW SPEED"),
            bottomTip1 = "USE MAGES TO MELT THROUGH",
            bottomTip2 = "HEAVILY ARMORED ENEMIES!"
        )
        EnemyType.WORG -> EnemyCardDetails(
            title = "WORGS",
            description = "CUNNING FAST BEASTS THAT CAN DODGE MELEE ATTACKS AND RESIST MAGIC.",
            bullets = listOf("MAGIC RESISTANCE", "CAN DODGE MELEE ATTACKS", "FAST SPEED")
        )
        EnemyType.GOLEM_HEAD -> EnemyCardDetails(
            title = "GOLEM HEADS",
            description = "MECHANICAL CONSTRUCTS OF DARK MAGIC.",
            bullets = listOf("MEDIUM ARMOR", "SLOW SPEED")
        )
        EnemyType.TROLL -> EnemyCardDetails(
            title = "TROLLS",
            description = "VERY TOUGH WARRIORS THAT CAN REGENERATE THEIR WOUNDS QUICKLY.",
            bullets = listOf("VERY HIGH DAMAGE", "HEALTH REGENERATION", "AVERAGE SPEED")
        )
        EnemyType.WINTER_WOLF -> EnemyCardDetails(
            title = "WINTER WOLVES",
            description = "CUNNING & DEADLY BEASTS THAT CAN DEFEAT ALL BUT THE BEST WARRIORS.",
            bullets = listOf("MAGIC RESISTANCE", "CAN DODGE MELEE ATTACKS", "VERY FAST SPEED")
        )
        EnemyType.TROLL_CHAMPION -> EnemyCardDetails(
            title = "TROLL CHAMPIONS",
            description = "ELITE TRIBAL WARRIORS EQUIPPED WITH VICIOUS THROWING AXES.",
            bullets = listOf("RANGED AXE ATTACK", "HEALTH REGENERATION", "COSTS 3 LIVES")
        )
        EnemyType.TROLL_CHIEFTAIN -> EnemyCardDetails(
            title = "TROLL CHIEFTAINS",
            description = "THEY CAN ENRAGE THEIR BRETHREN, IMPROVING THEIR COMBAT ABILITIES!",
            bullets = listOf("HIGH HEALTH REGENERATION", "CAN ENRAGE OTHER TROLLS", "VERY SLOW SPEED")
        )
        EnemyType.YETI -> EnemyCardDetails(
            title = "YETIS",
            description = "LARGE APE-MAN LIKE CREATURES THAT CAN OBLITERATE SCORES OF SOLDIERS.",
            bullets = listOf("SUPER HIGH HEALTH", "AREA ATTACK", "COSTS 5 LIVES")
        )
        EnemyType.ROCKET_RIDER -> EnemyCardDetails(
            title = "ROCKET RIDERS",
            description = "THE ROCKET RIDERS FOLLOW THEIR DARK LORD'S WILL WHEREVER THEY GO.",
            bullets = listOf("FLYING", "AVERAGE SPEED", "CAN USE TURBO BOOST")
        )
        EnemyType.DARK_SLAYER -> EnemyCardDetails(
            title = "DARK SLAYERS",
            description = "CLAIMED BY DARKNESS, SLAYERS ARE THE BANE OF ANYONE STANDING IN THEIR WAY.",
            bullets = listOf("GREAT ARMOR", "VERY HIGH DAMAGE", "SLOW SPEED")
        )
        EnemyType.DEMON_SPAWN -> EnemyCardDetails(
            title = "DEMON SPAWNS",
            description = "TERRIBLE INFERNAL CREATURES THAT EXPLODE WHEN THEY DIE.",
            bullets = listOf("MAGIC RESISTANCE", "INFERNAL COMBUSTION")
        )
        EnemyType.DEMON_LORD -> EnemyCardDetails(
            title = "DEMON LORDS",
            description = "LEADERS OF THE FIENDISH ARMIES, THEY SHIELD MINIONS WITH INFERNAL BARRIERS.",
            bullets = listOf("PROTECTS OTHER DEMONS", "MAGIC RESISTANCE", "INFERNAL COMBUSTION")
        )
        EnemyType.DEMON_HOUND -> EnemyCardDetails(
            title = "DEMON HOUNDS",
            description = "VICIOUS FAST CREATURES, THEY SERVE AS HUNTING DOGS OF THE DEMON LORDS.",
            bullets = listOf("MAGIC RESISTANCE", "FAST SPEED", "INFERNAL COMBUSTION")
        )
        EnemyType.DEMON_IMP -> EnemyCardDetails(
            title = "DEMON IMPS",
            description = "FLYING MISCHIEVOUS INFERNAL CREATURES OFTEN SEEN WITH GARGOYLES.",
            bullets = listOf("FLYING", "AVERAGE SPEED")
        )
        EnemyType.SKELETON -> EnemyCardDetails(
            title = "SKELETONS",
            description = "MINDLESS AUTOMATONS THAT OBEY THE ORDERS OF EVIL NECROMANCERS.",
            bullets = listOf("SLOW SPEED", "IMMUNE TO POISON")
        )
        EnemyType.SKELETON_KNIGHT -> EnemyCardDetails(
            title = "SKELETON KNIGHTS",
            description = "SKELETAL REMAINS OF FALLEN WARRIORS REANIMATED BY DARK MAGIC.",
            bullets = listOf("LIGHT ARMOR", "SLOW SPEED")
        )
        EnemyType.NECROMANCER -> EnemyCardDetails(
            title = "NECROMANCERS",
            description = "MASTERS OF THE DARK ARTS, THEY SUMMON UNDEAD MINIONS TO DO THEIR BIDDING.",
            bullets = listOf("SUMMONS UNDEAD", "RANGED MAGIC ATTACK", "SLOW SPEED")
        )
        EnemyType.MAGMA_ELEMENTAL -> EnemyCardDetails(
            title = "MAGMA ELEMENTALS",
            description = "MADE OF ROCK AND FIRE, THESE BEINGS LEAVE A TRAIL OF DESTRUCTION.",
            bullets = listOf("SUPER HIGH HEALTH", "AREA ATTACK", "COSTS 5 LIVES")
        )
        else -> EnemyCardDetails(
            title = type.title.uppercase(),
            description = "A FORMIDABLE FOE MARCHING AGAINST THE KINGDOM OF LINIREA!",
            bullets = listOf("HIGH THREAT", "BOSS ENEMY")
        )
    }

    // =========================================================================
    // PHẦN 1 & 2: CUỘN GIẤY HƯỚNG DẪN 3 TRANG & MỞ KHÓA SPELL
    // =========================================================================
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

        when (page) {
            1 -> drawPage1ObjectiveArt(sr, cX, scrollY)
            2 -> drawPage2ConstructionArt(sr, cX, scrollY)
            3 -> drawPage3BasicTowerTypesArt(sr, cX, scrollY)
        }
        sr.end()

        batch.begin()
        val oldFontScaleX = font.data.scaleX
        val oldFontScaleY = font.data.scaleY
        val oldSmallScaleX = smallFont.data.scaleX
        val oldSmallScaleY = smallFont.data.scaleY

        font.data.setScale(2.15f)
        GameUIRenderer.drawBadgeCenteredText(
            batch, font, "INSTRUCTIONS",
            tabX, tabY + 6f, tabW, tabH,
            Color(0.96f, 0.92f, 0.72f, 1f)
        )

        if (page < 3) {
            font.data.setScale(1.55f)
            GameUIRenderer.drawBadgeCenteredText(batch, font, "SKIP THIS!", cX - 240f, scrollY - 58f, 232f, 58f, Color.WHITE)
            GameUIRenderer.drawBadgeCenteredText(batch, font, "NEXT!", cX + 8f, scrollY - 58f, 232f, 58f, Color.WHITE)
        } else {
            font.data.setScale(1.65f)
            GameUIRenderer.drawBadgeCenteredText(batch, font, "GOT IT!", cX - 135f, scrollY - 42f, 270f, 42f, Color.WHITE)
            smallFont.data.setScale(0.78f)
            GameUIRenderer.drawBadgeCenteredText(batch, smallFont, "I'M READY NOW BRING IT ON!", cX - 135f, scrollY - 68f, 270f, 28f, Color(0.90f, 0.94f, 1f, 1f))
        }

        when (page) {
            1 -> {
                font.data.setScale(1.65f)
                font.color = OUTLINE
                font.draw(batch, "OBJECTIVE", scrollX, scrollY + scrollH - 28f, scrollW, Align.center, false)

                smallFont.data.setScale(1.08f)
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
                font.data.setScale(1.65f)
                font.color = OUTLINE
                font.draw(batch, "TOWER CONSTRUCTION", scrollX, scrollY + scrollH - 28f, scrollW, Align.center, false)

                smallFont.data.setScale(1.02f)
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
                GameUIRenderer.drawBadgeCenteredText(batch, smallFont, "70", step2X - 48f - 18f, scrollY + 198f, 36f, 14f, Color(1f, 0.90f, 0.28f, 1f))
                GameUIRenderer.drawBadgeCenteredText(batch, smallFont, "70", step2X + 48f - 18f, scrollY + 198f, 36f, 14f, Color(1f, 0.90f, 0.28f, 1f))
                GameUIRenderer.drawBadgeCenteredText(batch, smallFont, "100", step2X - 48f - 18f, scrollY + 86f, 36f, 14f, Color(1f, 0.90f, 0.28f, 1f))
                GameUIRenderer.drawBadgeCenteredText(batch, smallFont, "150", step2X + 48f - 18f, scrollY + 86f, 36f, 14f, Color(1f, 0.90f, 0.28f, 1f))
            }

            3 -> {
                font.data.setScale(1.65f)
                font.color = OUTLINE
                font.draw(batch, "BASIC TOWER TYPES", scrollX, scrollY + scrollH - 28f, scrollW, Align.center, false)

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

        font.data.setScale(oldFontScaleX, oldFontScaleY)
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
            Vector2(mapX + 10f, mapY + 72f),
            Vector2(mapX + 145f, mapY + 86f),
            Vector2(mapX + 245f, mapY + 78f),
            Vector2(mapX + 345f, mapY + 148f),
            Vector2(mapX + 465f, mapY + 178f),
            Vector2(mapX + mapW - 10f, mapY + 178f)
        )
        sr.color = roadEdge
        for (i in 0 until pts.size - 1) sr.rectLine(pts[i], pts[i + 1], 68f)
        sr.color = roadColor
        for (i in 0 until pts.size - 1) sr.rectLine(pts[i], pts[i + 1], 60f)

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

        drawMiniStrategicPoint(sr, mapX + 78f, mapY + 142f, 0.80f)
        drawMiniArcherTower(sr, mapX + 245f, mapY + 142f, 0.85f)

        val goblins = listOf(
            Vector2(mapX + 45f, mapY + 62f),
            Vector2(mapX + 82f, mapY + 68f),
            Vector2(mapX + 112f, mapY + 92f),
            Vector2(mapX + 132f, mapY + 58f)
        )
        for (g in goblins) drawMiniGoblin(sr, g.x, g.y, 0.85f)

        val defX = mapX + 472f
        val defY = mapY + 168f
        sr.color = Color(0.15f, 0.58f, 0.92f, 0.85f)
        sr.ellipse(defX - 32f, defY - 18f, 64f, 36f)
        sr.color = Color(0.55f, 0.85f, 1f, 0.95f)
        sr.ellipse(defX - 22f, defY - 12f, 44f, 24f)
        sr.color = roadColor
        sr.ellipse(defX - 14f, defY - 7f, 28f, 14f)

        for (fy in listOf(defY + 34f, defY - 38f)) {
            sr.color = OUTLINE
            sr.rect(defX + 36f, fy, 3f, 24f)
            sr.color = Color(0.22f, 0.56f, 0.92f, 1f)
            sr.rect(defX + 30f, fy + 10f, 15f, 12f)
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

        drawMiniStrategicPoint(sr, step1X, centerY, 1.35f)
        drawRedArrow(sr, cX - 132f, centerY)

        drawMiniStrategicPoint(sr, step2X, centerY, 1.05f)
        sr.color = OUTLINE
        sr.rectLine(step2X - 48f, centerY + 44f, step2X + 48f, centerY + 44f, 8f)
        sr.rectLine(step2X - 48f, centerY - 44f, step2X + 48f, centerY - 44f, 8f)
        sr.rectLine(step2X - 48f, centerY + 44f, step2X - 48f, centerY - 44f, 8f)
        sr.rectLine(step2X + 48f, centerY + 44f, step2X + 48f, centerY - 44f, 8f)
        sr.color = Color(0.55f, 0.58f, 0.62f, 1f)
        sr.rectLine(step2X - 48f, centerY + 44f, step2X + 48f, centerY + 44f, 4.5f)
        sr.rectLine(step2X - 48f, centerY - 44f, step2X + 48f, centerY - 44f, 4.5f)
        sr.rectLine(step2X - 48f, centerY + 44f, step2X - 48f, centerY - 44f, 4.5f)
        sr.rectLine(step2X + 48f, centerY + 44f, step2X + 48f, centerY - 44f, 4.5f)

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
            sr.color = Color(0.58f, 0.60f, 0.64f, 1f)
            sr.rect(b.x - 19f, b.y - 14f, 38f, 36f)
            sr.color = Color(0.18f, 0.16f, 0.14f, 1f)
            sr.rect(b.x - 15f, b.y - 10f, 30f, 28f)

            when (idx) {
                0 -> {
                    sr.color = Color(0.90f, 0.92f, 0.96f, 1f)
                    sr.rectLine(b.x - 6f, b.y - 2f, b.x + 4f, b.y + 8f, 2.5f)
                    sr.rectLine(b.x - 2f, b.y - 6f, b.x + 8f, b.y + 4f, 2.5f)
                }
                1 -> {
                    sr.color = Color(0.78f, 0.82f, 0.88f, 1f)
                    sr.rect(b.x - 7f, b.y - 2f, 14f, 11f)
                    sr.triangle(b.x - 7f, b.y - 2f, b.x + 7f, b.y - 2f, b.x, b.y - 8f)
                }
                2 -> {
                    sr.color = Color(0.85f, 0.65f, 0.20f, 1f)
                    sr.rectLine(b.x - 6f, b.y - 6f, b.x + 3f, b.y + 4f, 3f)
                    sr.color = Color(0.35f, 0.85f, 1f, 1f)
                    sr.circle(b.x + 3f, b.y + 6f, 5f)
                }
                3 -> {
                    sr.color = Color(0.35f, 0.38f, 0.42f, 1f)
                    sr.circle(b.x, b.y + 1f, 7.5f)
                    sr.color = Color(1f, 0.55f, 0.10f, 1f)
                    sr.circle(b.x + 3f, b.y + 9f, 2.5f)
                }
            }

            sr.color = OUTLINE
            sr.rect(b.x - 18f, b.y - 26f, 36f, 15f)
            sr.color = Color(0.12f, 0.10f, 0.08f, 1f)
            sr.rect(b.x - 16f, b.y - 24f, 32f, 11f)
        }

        drawRedArrow(sr, cX + 98f, centerY)
        drawMiniArcherTower(sr, step3X, centerY - 6f, 1.35f)
    }

    private fun drawPage3BasicTowerTypesArt(sr: ShapeRenderer, cX: Float, scrollY: Float) {
        val colCenters = listOf(cX - 285f, cX - 95f, cX + 95f, cX + 285f)
        val artY = scrollY + 185f

        sr.color = Color(0.75f, 0.68f, 0.46f, 1f)
        for (divX in listOf(cX - 190f, cX, cX + 190f)) {
            for (dy in (scrollY + 52f).toInt()..(scrollY + 285f).toInt() step 8) {
                sr.rect(divX - 1f, dy.toFloat(), 2f, 4f)
            }
        }

        for (cx in colCenters) {
            val bx = cx - 84f
            val by = scrollY + 52f
            val bw = 168f
            val bh = 54f
            sr.color = Color(0.75f, 0.68f, 0.46f, 1f)
            sr.rect(bx - 2f, by - 2f, bw + 4f, bh + 4f)
            sr.color = Color(0.95f, 0.91f, 0.74f, 1f)
            sr.rect(bx, by, bw, bh)
        }

        val c1 = colCenters[0]
        drawMiniArcherTower(sr, c1 - 15f, artY, 1.15f)
        drawMiniGoblin(sr, c1 + 42f, artY - 22f, 0.95f)
        sr.color = OUTLINE
        sr.rectLine(c1 + 5f, artY + 35f, c1 + 28f, artY + 15f, 2f)
        sr.rectLine(c1 + 22f, artY + 8f, c1 + 35f, artY - 12f, 2f)
        sr.rectLine(c1 + 48f, artY + 5f, c1 + 52f, artY - 10f, 2f)

        val c2 = colCenters[1]
        drawMiniBarracksTower(sr, c2 - 5f, artY + 12f, 1.10f)
        drawMiniSoldier(sr, c2 - 32f, artY - 32f)
        drawMiniSoldier(sr, c2 - 8f, artY - 18f)
        drawMiniSoldier(sr, c2 + 16f, artY - 34f)
        drawMiniGoblin(sr, c2 + 36f, artY - 34f, 0.95f)

        val c3 = colCenters[2]
        drawMiniMageTower(sr, c3 - 10f, artY, 1.12f)
        drawMiniGoblin(sr, c3 + 42f, artY - 18f, 0.95f)
        sr.color = Color(0.25f, 0.85f, 1f, 0.9f)
        sr.circle(c3 - 10f, artY + 34f, 7f)
        sr.circle(c3 + 18f, artY + 6f, 6f)
        sr.color = Color.WHITE
        sr.circle(c3 + 18f, artY + 6f, 3f)

        val c4 = colCenters[3]
        drawMiniArtilleryTower(sr, c4 - 22f, artY + 2f, 1.10f)
        sr.color = Color(0.42f, 0.38f, 0.34f, 0.9f)
        sr.circle(c4 + 28f, artY - 2f, 24f)
        sr.circle(c4 + 36f, artY + 14f, 18f)
        sr.color = Color(1f, 0.78f, 0.15f, 1f)
        sr.circle(c4 + 26f, artY - 12f, 12f)
        sr.circle(c4 + 38f, artY - 4f, 9f)
        drawMiniGoblin(sr, c4 + 2f, artY - 24f, 0.88f)
        drawMiniGoblin(sr, c4 + 54f, artY - 18f, 0.88f)
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
            drawMiniSoldier(sr, innerX + 95f, innerY + 42f)
            drawMiniSoldier(sr, innerX + 125f, innerY + 62f)
            drawMiniGoblin(sr, innerX + 158f, innerY + 65f, 1.15f)
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
        val oldFontScaleX = font.data.scaleX
        val oldFontScaleY = font.data.scaleY
        val oldSmallScaleX = smallFont.data.scaleX
        val oldSmallScaleY = smallFont.data.scaleY

        font.data.setScale(1.85f)
        GameUIRenderer.drawBadgeCenteredText(
            batch, font, "NEW SPECIAL AVAILABLE!",
            tabX, tabY + 6f, tabW, tabH,
            Color(0.96f, 0.92f, 0.72f, 1f)
        )

        font.data.setScale(1.60f)
        GameUIRenderer.drawBadgeCenteredText(batch, font, "OK!", okBtnX, okBtnY, okBtnW, okBtnH - 4f, Color.WHITE)

        val textX = scrollX + 275f
        if (popupType == SpecialUnlockPopupType.REINFORCEMENTS) {
            font.data.setScale(1.50f)
            font.color = OUTLINE
            font.draw(batch, "CALL REINFORCEMENTS", textX, scrollY + scrollH - 42f)

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
            font.data.setScale(1.55f)
            font.color = OUTLINE
            font.draw(batch, "RAIN OF FIRE", textX, scrollY + scrollH - 38f)

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

        font.data.setScale(oldFontScaleX, oldFontScaleY)
        smallFont.data.setScale(oldSmallScaleX, oldSmallScaleY)
        batch.end()
    }

    // =========================================================================
    // CÁC HÀM VẼ BIỂU TƯỢNG PHỤ TRỢ CHO CUỘN GIẤY, KHIÊN GIÁP, CỜ RALLY & THÁP LV 1/2/3
    // =========================================================================
    private fun drawDummyEnemyEntity(sr: ShapeRenderer, type: EnemyType, cx: Float, cy: Float, scale: Float = 1f) {
        val dummy = Entity().apply {
            add(TransformComponent(position = Vector2(cx, cy)))
            add(HealthComponent(maxHealth = 100f, currentHealth = 100f))
            add(EnemyComponent(enemyType = type))
        }
        EnemyRenderer.render(sr, dummy, showHpBar = false)
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
        // Thanh kiếm bên trái
        sr.color = OUTLINE
        sr.rect(cx - 14f * s, cy - 12f * s, 6f * s, 26f * s)
        sr.color = Color(0.92f, 0.76f, 0.18f, 1f)
        sr.rect(cx - 16f * s, cy - 6f * s, 10f * s, 3.5f * s)
        sr.color = Color.WHITE
        sr.rect(cx - 12.5f * s, cy - 2f * s, 3f * s, 15f * s)
        // Khiên bạc bên phải
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

    private fun drawMiniStrategicPoint(sr: ShapeRenderer, cx: Float, cy: Float, scale: Float) {
        val s = scale
        sr.color = Color(0.45f, 0.62f, 0.20f, 1f)
        sr.ellipse(cx - 34f * s, cy - 16f * s, 68f * s, 32f * s)
        sr.color = Color(0.80f, 0.72f, 0.46f, 1f)
        sr.ellipse(cx - 28f * s, cy - 13f * s, 56f * s, 26f * s)

        sr.color = OUTLINE
        sr.rect(cx - 2f * s, cy - 2f * s, 4f * s, 14f * s)
        sr.rect(cx - 9f * s, cy + 9f * s, 18f * s, 12f * s)
        sr.color = Color(0.95f, 0.92f, 0.80f, 1f)
        sr.rect(cx - 7.5f * s, cy + 10.5f * s, 15f * s, 9f * s)
    }

    private fun drawMiniArcherTower(sr: ShapeRenderer, cx: Float, cy: Float, scale: Float, tier: Int = 1) {
        val s = scale
        sr.color = Color(0.45f, 0.60f, 0.22f, 1f)
        sr.ellipse(cx - 32f * s, cy - 22f * s, 64f * s, 24f * s)

        // Gai nhọn ở 2 bên mép tháp nếu là Cấp 3 (Sharpshooter)
        if (tier >= 3) {
            sr.color = OUTLINE
            sr.triangle(cx - 22f * s, cy + 16f * s, cx - 32f * s, cy + 22f * s, cx - 22f * s, cy + 8f * s)
            sr.triangle(cx + 22f * s, cy + 16f * s, cx + 32f * s, cy + 22f * s, cx + 22f * s, cy + 8f * s)
        }

        sr.color = OUTLINE
        sr.rect(cx - 24f * s, cy - 16f * s, 48f * s, 36f * s)
        sr.color = if (tier >= 2) Color(0.58f, 0.60f, 0.64f, 1f) else Color(0.68f, 0.66f, 0.62f, 1f)
        sr.rect(cx - 21f * s, cy - 14f * s, 42f * s, 32f * s)

        sr.color = OUTLINE
        sr.rect(cx - 7f * s, cy - 14f * s, 14f * s, 14f * s)
        sr.color = Color(0.48f, 0.32f, 0.16f, 1f)
        sr.rect(cx - 5f * s, cy - 14f * s, 10f * s, 12f * s)

        sr.color = Color(0.85f, 0.22f, 0.16f, 1f)
        sr.circle(cx, cy + 8f * s, 5.5f * s)
        sr.color = Color.WHITE
        sr.circle(cx, cy + 8f * s, 3.2f * s)
        sr.color = Color(0.85f, 0.22f, 0.16f, 1f)
        sr.circle(cx, cy + 8f * s, 1.4f * s)

        sr.color = OUTLINE
        sr.rect(cx - 26f * s, cy + 18f * s, 52f * s, 8f * s)
        sr.color = if (tier >= 2) Color(0.28f, 0.30f, 0.34f, 1f) else Color(0.55f, 0.36f, 0.18f, 1f)
        sr.rect(cx - 24f * s, cy + 19.5f * s, 48f * s, 5f * s)

        for (ax in listOf(-8f * s, 8f * s)) {
            sr.color = OUTLINE
            sr.circle(cx + ax, cy + 29f * s, 5.5f * s)
            sr.color = if (tier == 2) Color(0.95f, 0.78f, 0.20f, 1f) else if (tier >= 3) Color(0.25f, 0.28f, 0.32f, 1f) else Color(0.95f, 0.78f, 0.56f, 1f)
            sr.circle(cx + ax, cy + 29f * s, 4f * s)
        }
    }

    private fun drawMiniBarracksTower(sr: ShapeRenderer, cx: Float, cy: Float, scale: Float, tier: Int = 1) {
        val s = scale
        sr.color = OUTLINE
        sr.rect(cx - 26f * s, cy - 16f * s, 52f * s, 38f * s)
        sr.color = if (tier >= 3) Color(0.62f, 0.65f, 0.70f, 1f) else Color(0.72f, 0.70f, 0.65f, 1f)
        sr.rect(cx - 23f * s, cy - 14f * s, 46f * s, 34f * s)

        if (tier >= 3) {
            // Chóp pháo đài Knights Barracks
            sr.color = OUTLINE
            sr.rect(cx - 27f * s, cy + 20f * s, 12f * s, 7f * s)
            sr.rect(cx + 15f * s, cy + 20f * s, 12f * s, 7f * s)
        }

        sr.color = OUTLINE
        sr.rect(cx - 7f * s, cy + 4f * s, 14f * s, 12f * s)
        sr.color = Color(0.22f, 0.56f, 0.90f, 1f)
        sr.rect(cx - 5f * s, cy + 6f * s, 10f * s, 8f * s)
        sr.color = OUTLINE
        sr.rect(cx - 7f * s, cy - 14f * s, 14f * s, 12f * s)
    }

    private fun drawMiniMageTower(sr: ShapeRenderer, cx: Float, cy: Float, scale: Float, tier: Int = 1) {
        val s = scale
        sr.color = OUTLINE
        sr.rect(cx - 22f * s, cy - 16f * s, 44f * s, 32f * s)
        sr.color = Color(0.76f, 0.72f, 0.62f, 1f)
        sr.rect(cx - 19f * s, cy - 14f * s, 38f * s, 28f * s)

        // Trụ đá phát sáng xanh băng ở Cấp 2 & Cấp 3
        sr.color = Color(0.20f, 0.82f, 1f, 1f)
        if (tier >= 2) {
            sr.rect(cx - 18f * s, cy - 10f * s, 5f * s, 20f * s)
            sr.rect(cx + 13f * s, cy - 10f * s, 5f * s, 20f * s)
        }
        sr.circle(cx, cy, 6f * s)
        sr.circle(cx - 18f * s, cy + 8f * s, 5f * s)
        sr.circle(cx + 18f * s, cy + 2f * s, 5f * s)

        sr.color = OUTLINE
        sr.triangle(cx - 8f * s, cy + 16f * s, cx + 8f * s, cy + 16f * s, cx, cy + 34f * s)
        sr.color = Color(0.22f, 0.55f, 0.92f, 1f)
        sr.triangle(cx - 6f * s, cy + 16f * s, cx + 6f * s, cy + 16f * s, cx, cy + 31f * s)

        if (tier >= 2) {
            sr.color = Color(0.45f, 0.92f, 1f, 0.85f)
            sr.circle(cx - 8f * s, cy + 32f * s, 7f * s)
            sr.color = Color.WHITE
            sr.circle(cx - 8f * s, cy + 32f * s, 3.5f * s)
        }
    }

    private fun drawMiniArtilleryTower(sr: ShapeRenderer, cx: Float, cy: Float, scale: Float, tier: Int = 1) {
        val s = scale
        sr.color = OUTLINE
        sr.rect(cx - 24f * s, cy - 16f * s, 48f * s, 26f * s)
        sr.color = if (tier >= 3) Color(0.52f, 0.55f, 0.60f, 1f) else Color(0.55f, 0.36f, 0.20f, 1f)
        sr.rect(cx - 21f * s, cy - 14f * s, 42f * s, 22f * s)
        sr.color = OUTLINE
        sr.circle(cx, cy + 12f * s, 15f * s)
        sr.color = Color(0.38f, 0.40f, 0.44f, 1f)
        sr.circle(cx, cy + 12f * s, 12f * s)
        sr.color = OUTLINE
        sr.ellipse(cx - 8f * s, cy + 14f * s, 16f * s, 8f * s)
    }

    private fun drawMiniGoblin(sr: ShapeRenderer, cx: Float, cy: Float, scale: Float) {
        val s = scale
        sr.color = OUTLINE
        sr.circle(cx, cy + 3f * s, 6f * s)
        sr.rect(cx - 4.5f * s, cy - 6f * s, 9f * s, 7f * s)
        sr.color = Color(0.38f, 0.64f, 0.20f, 1f)
        sr.circle(cx, cy + 3f * s, 4.5f * s)
        sr.color = Color(0.45f, 0.28f, 0.14f, 1f)
        sr.rect(cx - 3.2f * s, cy - 4.5f * s, 6.4f * s, 4.5f * s)
    }

    private fun drawMiniSoldier(sr: ShapeRenderer, cx: Float, cy: Float, tier: Int = 1) {
        sr.color = OUTLINE
        sr.circle(cx, cy + 4f, 5.5f)
        sr.rect(cx - 4.5f, cy - 6f, 9f, 8f)
        sr.color = when (tier) {
            2 -> Color(0.58f, 0.62f, 0.68f, 1f) // Mũ sắt Footman
            3 -> Color(0.82f, 0.86f, 0.90f, 1f) // Mũ giáp sắt Knight
            else -> Color(0.95f, 0.80f, 0.22f, 1f) // Tóc vàng Militia
        }
        sr.circle(cx, cy + 5f, 4f)
        sr.color = if (tier >= 3) Color(0.75f, 0.78f, 0.84f, 1f) else Color(0.22f, 0.52f, 0.88f, 1f)
        sr.rect(cx - 3.2f, cy - 4.5f, 6.4f, 6f)
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
