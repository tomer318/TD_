package com.game.td.renderers

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.game.td.Config
import com.game.td.ecs.components.*
import com.game.td.managers.CheatManager
import com.game.td.managers.EconomyManager
import com.game.td.managers.GameMode
import com.game.td.managers.WaveManager
import com.game.td.map.Level12TheDarkTower
import com.game.td.map.LevelData
import com.game.td.renderers.GameUIRenderer.drawOutlinedText
import com.game.td.screens.ActiveSpell

data class LockedTowerState(
    var initialTimer: Float = 5.0f,
    var penaltyTimer: Float = 5.0f,
    var isUndispellable: Boolean = false
)

data class JuggernautSummonBomb(
    val startPos: Vector2,
    val targetPos: Vector2,
    val remainingWaypoints: List<Vector2>,
    var elapsed: Float = 0f,
    val duration: Float = 0.85f
)

object PlayScreenRenderer {

    fun render(
        shapeRenderer: ShapeRenderer,
        batch: SpriteBatch,
        font: BitmapFont,
        smallFont: BitmapFont,
        engine: Engine,
        currentLevel: LevelData,
        gameMode: GameMode,
        waveManager: WaveManager,
        effectRenderer: EffectRenderer,
        glowingCircles: List<Pair<Vector2, Float>>,
        warningTowersMap: Map<Entity, Float>,
        lockedTowersMap: Map<Entity, LockedTowerState>,
        activeJuggernautBombs: List<JuggernautSummonBomb>,
        selectedSpot: Entity?,
        selectedTower: Entity?,
        selectedEnemy: Entity?,
        selectedSoldier: Entity?,
        previewBuildType: TowerType?,
        previewTowerAction: String?,
        isSettingRallyPoint: Boolean,
        maxRallyRadius: Float,
        buildOptions: List<Pair<TowerType, Vector2>>,
        getBranchSpecs: (TowerType) -> Pair<TowerSpecialization, TowerSpecialization>,
        allowedTowers: List<TowerType>,
        selectedSpell: ActiveSpell,
        rainOfFireCooldown: Float,
        reinforcementCooldown: Float,
        isAimingSunrayTarget: Boolean,
        isGamePaused: Boolean,
        isVictoryReady: Boolean,
        onClearEnemySelection: () -> Unit,
        onClearSoldierSelection: () -> Unit
    ) {
        // 1. VẼ ĐỊA HÌNH BẢN ĐỒ
        MapRenderer.render(shapeRenderer, currentLevel)

        // Vẽ biển gỗ "FOR SALE" trên ban công Màn 12 khi chơi Heroic / Iron
        if (currentLevel.levelNumber == 12 && gameMode != GameMode.CAMPAIGN) {
            val bPos = Level12TheDarkTower.balconyPosition
            val oldScaleX = smallFont.data.scaleX
            val oldScaleY = smallFont.data.scaleY
            smallFont.data.setScale(0.82f)
            batch.begin()
            drawOutlinedText(batch, smallFont, "FOR SALE", bPos.x - 24f, bPos.y + 26f, Color.WHITE)
            batch.end()
            smallFont.data.setScale(oldScaleX, oldScaleY)
        }

        // Vẽ vòng tròn triệu hồi quỷ của Vez'nan ở Màn 12
        if (glowingCircles.isNotEmpty()) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
            for (circle in glowingCircles) {
                val cPos = circle.first
                shapeRenderer.color = Color(0.75f, 0.20f, 0.95f, 0.45f)
                shapeRenderer.circle(cPos.x, cPos.y, 40f)
                shapeRenderer.color = Color(1f, 0.85f, 0.20f, 0.65f)
                shapeRenderer.circle(cPos.x, cPos.y, 24f)
            }
            shapeRenderer.end()
        }

        // 2. VẼ VỆT CHÁY TRÊN MẶT ĐẤT, CÁC THỰC THỂ & HIỆU ỨNG CHIẾN ĐẤU
        effectRenderer.renderGroundPatches(shapeRenderer)
        renderEntities(
            shapeRenderer = shapeRenderer,
            engine = engine,
            glowingCircles = glowingCircles,
            warningTowersMap = warningTowersMap,
            lockedTowersMap = lockedTowersMap,
            activeJuggernautBombs = activeJuggernautBombs
        )
        effectRenderer.render(shapeRenderer)

        // 3. VẼ NÚT GỌI SỚM WAVE & BÁNH XE XÂY/NÂNG CẤP THÁP
        GameUIRenderer.renderEarlyCallWaveButtons(
            shapeRenderer, batch, font, smallFont, waveManager, currentLevel.allPaths
        )

        val maxLevel = when {
            currentLevel.levelNumber == 0 -> 4
            currentLevel.levelNumber == 5 -> if (waveManager.currentWaveDisplay >= 5 || gameMode != GameMode.CAMPAIGN) 4 else 3
            currentLevel.levelNumber >= 6 -> 4
            else -> currentLevel.maxTowerLevel
        }

        GameUIRenderer.renderWheelMenu(
            shapeRenderer = shapeRenderer,
            batch = batch,
            smallFont = smallFont,
            selectedSpot = selectedSpot,
            selectedTower = selectedTower,
            previewBuildType = previewBuildType,
            previewTowerAction = previewTowerAction,
            isSettingRallyPoint = isSettingRallyPoint,
            maxRallyRadius = maxRallyRadius,
            buildOptions = buildOptions,
            getBranchSpecs = getBranchSpecs,
            maxLevelAllowed = maxLevel,
            allowedTowers = allowedTowers,
            currentLevelNumber = currentLevel.levelNumber,
            isPreGame = (waveManager.currentWaveIndex == -1)
        )

        // 4. VẼ KỸ NĂNG SPELL, THANH THÔNG TIN DƯỚI CÙNG & HUD TRÊN CÙNG
        GameUIRenderer.renderSpellUI(
            shapeRenderer, batch, font, smallFont,
            selectedSpell, rainOfFireCooldown, reinforcementCooldown
        )

        GameUIRenderer.renderBottomInfoBar(
            shapeRenderer = shapeRenderer,
            batch = batch,
            font = font,
            smallFont = smallFont,
            selectedEnemy = selectedEnemy,
            selectedSoldier = selectedSoldier,
            selectedTower = selectedTower,
            onClearEnemySelection = onClearEnemySelection,
            onClearSoldierSelection = onClearSoldierSelection
        )

        GameUIRenderer.renderHUD(
            shapeRenderer, batch, font, waveManager, isSettingRallyPoint, isGamePaused
        )

        if (isAimingSunrayTarget) {
            batch.begin()
            drawOutlinedText(
                batch, font,
                "SELECT AN ENEMY TO FIRE SOLAR BEAM!",
                Config.WORLD_WIDTH / 2f - 190f, 65f, Color.YELLOW
            )
            batch.end()
        }

        // 5. VẼ BẢNG TẠM DỪNG, MENU CHEAT & BẢNG CHIẾN THẮNG
        if (isGamePaused) {
            GameUIRenderer.renderPauseMenu(shapeRenderer, batch, font, smallFont)
        } else if (CheatManager.isCheatEnabled) {
            GameUIRenderer.renderCheatFloatingButton(shapeRenderer, batch, font)
            if (CheatManager.isCheatMenuOpen) {
                GameUIRenderer.renderCheatMenuPopup(shapeRenderer, batch, font, smallFont)
            }
        }

        if (isVictoryReady) {
            val earnedStars = if (gameMode == GameMode.CAMPAIGN) {
                when {
                    EconomyManager.lives >= 18 -> 3
                    EconomyManager.lives >= 6 -> 2
                    EconomyManager.lives >= 1 -> 1
                    else -> 0
                }
            } else {
                1
            }
            GameUIRenderer.renderVictoryDialog(
                shapeRenderer, batch, font, smallFont, earnedStars,
                isChallengeMode = (gameMode != GameMode.CAMPAIGN)
            )
        }
    }

    private fun renderEntities(
        shapeRenderer: ShapeRenderer,
        engine: Engine,
        glowingCircles: List<Pair<Vector2, Float>>,
        warningTowersMap: Map<Entity, Float>,
        lockedTowersMap: Map<Entity, LockedTowerState>,
        activeJuggernautBombs: List<JuggernautSummonBomb>
    ) {
        val spots = engine.getEntitiesFor(Family.all(TowerSpotComponent::class.java, TransformComponent::class.java).get()).toList()
        val towers = engine.getEntitiesFor(Family.all(TowerComponent::class.java, TransformComponent::class.java).get()).toList()
        val enemies = engine.getEntitiesFor(Family.all(EnemyComponent::class.java, TransformComponent::class.java).get()).toList()
        val eggs = engine.getEntitiesFor(Family.all(SpiderEggComponent::class.java, TransformComponent::class.java).get()).toList()
        val soldiers = engine.getEntitiesFor(Family.all(SoldierComponent::class.java, TransformComponent::class.java).get()).toList()
        val projectiles = engine.getEntitiesFor(Family.all(ProjectileComponent::class.java, TransformComponent::class.java).get()).toList()

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        for (s in spots) {
            if (s.getComponent(TowerSpotComponent::class.java)?.isOccupied == false) {
                val p = s.getComponent(TransformComponent::class.java)?.position ?: continue
                shapeRenderer.color = Color(0.48f, 0.45f, 0.38f, 1f)
                shapeRenderer.circle(p.x, p.y, 25f)
                shapeRenderer.color = Color(0.62f, 0.58f, 0.48f, 1f)
                shapeRenderer.circle(p.x, p.y, 19f)
            }
        }

        for (egg in eggs) {
            EnemyRenderer.renderEgg(shapeRenderer, egg)
        }

        for (t in towers) {
            TowerRenderer.render(shapeRenderer, t)

            val tPos = t.getComponent(TransformComponent::class.java)?.position
            val tComp = t.getComponent(TowerComponent::class.java)

            if (warningTowersMap.containsKey(t) && tPos != null) {
                shapeRenderer.color = Color(0.95f, 0.35f, 1.0f, 0.35f)
                shapeRenderer.circle(tPos.x, tPos.y, 38f)
            }

            if (tComp != null && tComp.isFrozen && tPos != null) {
                val lockState = lockedTowersMap[t]
                if (lockState != null && lockState.isUndispellable) {
                    shapeRenderer.color = Color(0.65f, 0.15f, 0.85f, 0.70f)
                } else {
                    shapeRenderer.color = Color(0.85f, 0.25f, 0.95f, 0.50f)
                }
                shapeRenderer.circle(tPos.x, tPos.y, 34f)
                shapeRenderer.color = Color(1f, 0.85f, 0.25f, 0.85f)
                shapeRenderer.circle(tPos.x, tPos.y, 16f)
            }
        }

        for (sol in soldiers) {
            SoldierRenderer.render(shapeRenderer, sol)
        }

        for (e in enemies) {
            val move = e.getComponent(MovementComponent::class.java)
            if (move?.isInsideTunnel == true) continue
            val eComp = e.getComponent(EnemyComponent::class.java)
            val eTrans = e.getComponent(TransformComponent::class.java)

            if (eComp?.enemyType == EnemyType.VEZNAN && eTrans != null) {
                val isBossCasting = glowingCircles.isNotEmpty() || warningTowersMap.isNotEmpty()
                if (isBossCasting) {
                    shapeRenderer.color = Color(0.85f, 0.20f, 0.95f, 0.40f)
                    shapeRenderer.circle(eTrans.position.x, eTrans.position.y, 32f)
                }
            }

            EnemyRenderer.render(shapeRenderer, e)

            if (eComp != null && eComp.curseTimer > 0f && eTrans != null) {
                shapeRenderer.color = Color(0.75f, 0.20f, 0.95f, 0.75f)
                shapeRenderer.circle(eTrans.position.x, eTrans.position.y + 16f, 4.5f)
                shapeRenderer.color = Color(1f, 0.85f, 0.30f, 0.95f)
                shapeRenderer.circle(eTrans.position.x, eTrans.position.y + 16f, 2.2f)
            }
        }

        for (proj in projectiles) {
            ProjectileRenderer.render(shapeRenderer, proj)
        }

        // Vẽ quả bom kén sắt triệu hồi Golem Head bay vòng cung Parabol trên không trung
        for (bomb in activeJuggernautBombs) {
            val progress = (bomb.elapsed / bomb.duration).coerceIn(0f, 1f)
            val groundX = MathUtils.lerp(bomb.startPos.x, bomb.targetPos.x, progress)
            val groundY = MathUtils.lerp(bomb.startPos.y, bomb.targetPos.y, progress)
            val arcHeight = 95f * (4f * progress * (1f - progress))

            shapeRenderer.color = Color(0.08f, 0.08f, 0.10f, 0.38f)
            shapeRenderer.circle(groundX, groundY, 9f)

            val bx = groundX
            val by = groundY + arcHeight

            shapeRenderer.color = Color(0.22f, 0.18f, 0.15f, 1f)
            shapeRenderer.circle(bx, by, 11.5f)
            shapeRenderer.color = Color(0.48f, 0.40f, 0.34f, 1f)
            shapeRenderer.circle(bx, by, 9.5f)
            shapeRenderer.color = Color(1f, 0.55f, 0.10f, 1f)
            shapeRenderer.circle(bx, by + 11f, 3.8f)
            shapeRenderer.color = Color(1f, 0.90f, 0.25f, 1f)
            shapeRenderer.circle(bx, by + 11f, 2.0f)
        }

        shapeRenderer.end()
    }
}
