package com.game.td.renderers

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.game.td.Config
import com.game.td.ecs.components.*
import com.game.td.managers.CheatManager
import com.game.td.managers.EconomyManager
import com.game.td.managers.UpgradeManager
import com.game.td.managers.WaveManager
import com.game.td.screens.ActiveSpell

object GameUIRenderer {

    fun drawOutlinedText(batch: SpriteBatch, font: BitmapFont, text: String, x: Float, y: Float, textColor: Color) {
        font.color = Color(0f, 0f, 0f, 0.95f)
        font.draw(batch, text, x - 1.2f, y)
        font.draw(batch, text, x + 1.2f, y)
        font.draw(batch, text, x, y - 1.2f)
        font.draw(batch, text, x, y + 1.2f)
        font.draw(batch, text, x - 1f, y - 1f)
        font.draw(batch, text, x + 1f, y + 1f)
        font.color = textColor
        font.draw(batch, text, x, y)
    }

    fun renderHUD(
        shapeRenderer: ShapeRenderer,
        batch: SpriteBatch,
        font: BitmapFont,
        waveManager: WaveManager,
        isSettingRallyPoint: Boolean,
        isPaused: Boolean = false
    ) {
        val backBtnX = 35f
        val backBtnY = Config.WORLD_HEIGHT - 35f
        val pauseBtnX = Config.WORLD_WIDTH - 50f
        val pauseBtnY = Config.WORLD_HEIGHT - 35f

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color(0.7f, 0.2f, 0.2f, 0.9f)
        shapeRenderer.circle(backBtnX, backBtnY, 18f)

        shapeRenderer.color = Color(0.18f, 0.18f, 0.22f, 0.95f)
        shapeRenderer.circle(pauseBtnX, pauseBtnY, 20f)
        shapeRenderer.color = Color.WHITE
        shapeRenderer.rect(pauseBtnX - 7f, pauseBtnY - 8f, 4.5f, 16f)
        shapeRenderer.rect(pauseBtnX + 2.5f, pauseBtnY - 8f, 4.5f, 16f)
        shapeRenderer.end()

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color.GOLD
        shapeRenderer.circle(backBtnX, backBtnY, 18f)
        shapeRenderer.circle(pauseBtnX, pauseBtnY, 20f)
        shapeRenderer.end()

        val hudX = 75f
        val hudY = Config.WORLD_HEIGHT - 54f
        val hudH = 38f
        val leftBoxW = 160f
        val rightBoxW = 135f
        val rightX = hudX + leftBoxW + 6f

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color(0.06f, 0.06f, 0.06f, 0.75f)
        shapeRenderer.rect(hudX - 3f, hudY - 3f, leftBoxW + rightBoxW + 12f, hudH + 6f)

        shapeRenderer.color = Color(0.38f, 0.40f, 0.44f, 1f)
        shapeRenderer.rect(hudX - 2f, hudY - 2f, leftBoxW + 4f, hudH + 4f)
        shapeRenderer.color = Color(0.14f, 0.13f, 0.12f, 0.95f)
        shapeRenderer.rect(hudX + 2f, hudY + 2f, leftBoxW - 4f, hudH - 4f)

        shapeRenderer.color = Color(0.38f, 0.40f, 0.44f, 1f)
        shapeRenderer.rect(rightX - 2f, hudY - 2f, rightBoxW + 4f, hudH + 4f)
        shapeRenderer.color = Color(0.14f, 0.13f, 0.12f, 0.95f)
        shapeRenderer.rect(rightX + 2f, hudY + 2f, rightBoxW - 4f, hudH - 4f)

        shapeRenderer.color = Color(0.55f, 0.58f, 0.62f, 1f)
        shapeRenderer.rect(hudX + leftBoxW, hudY + 6f, 6f, hudH - 12f)

        val heartX = hudX + 18f
        val heartY = hudY + 20f
        shapeRenderer.color = Color(0.88f, 0.12f, 0.12f, 1f)
        shapeRenderer.circle(heartX - 4f, heartY + 3f, 5.5f)
        shapeRenderer.circle(heartX + 4f, heartY + 3f, 5.5f)
        shapeRenderer.triangle(heartX - 8.5f, heartY + 2f, heartX + 8.5f, heartY + 2f, heartX, heartY - 8f)

        val goldX = hudX + 86f
        val goldY = hudY + 19f
        shapeRenderer.color = Color(0.95f, 0.72f, 0.1f, 1f)
        shapeRenderer.circle(goldX - 3f, goldY - 1f, 6f)
        shapeRenderer.color = Color(1f, 0.88f, 0.25f, 1f)
        shapeRenderer.circle(goldX + 3.5f, goldY + 2f, 5.5f)

        val skullX = rightX + 18f
        val skullY = hudY + 20f
        shapeRenderer.color = Color(0.92f, 0.92f, 0.92f, 1f)
        shapeRenderer.circle(skullX, skullY + 2f, 7.5f)
        shapeRenderer.rect(skullX - 4f, skullY - 6.5f, 8f, 5f)
        shapeRenderer.end()

        batch.begin()
        drawOutlinedText(batch, font, "<", backBtnX - 5f, backBtnY + 7f, Color.WHITE)
        drawOutlinedText(batch, font, "${EconomyManager.lives}", hudX + 32f, hudY + 25f, Color.WHITE)
        drawOutlinedText(batch, font, "${EconomyManager.gold}", hudX + 98f, hudY + 25f, Color.GOLD)

        val waveText = if (waveManager.currentWaveDisplay <= 0) "Wave 0/${waveManager.totalWaves}" else "Wave ${waveManager.currentWaveDisplay}/${waveManager.totalWaves}"
        drawOutlinedText(batch, font, waveText, rightX + 32f, hudY + 25f, Color.WHITE)

        if (EconomyManager.isGameOver) {
            drawOutlinedText(batch, font, "GAME OVER - DEFEAT!", Config.WORLD_WIDTH / 2f - 140f, Config.WORLD_HEIGHT / 2f + 40f, Color.RED)
            drawOutlinedText(batch, font, "[ CLICK TO REPLAY ]", Config.WORLD_WIDTH / 2f - 130f, Config.WORLD_HEIGHT / 2f - 50f, Color.YELLOW)
        }

        if (isSettingRallyPoint) {
            drawOutlinedText(batch, font, "TAP ROAD WITHIN YELLOW CIRCLE TO SET RALLY!", Config.WORLD_WIDTH / 2f - 240f, 60f, Color.YELLOW)
        }
        batch.end()
    }

    fun renderPauseMenu(
        shapeRenderer: ShapeRenderer,
        batch: SpriteBatch,
        font: BitmapFont,
        smallFont: BitmapFont
    ) {
        val cX = Config.WORLD_WIDTH / 2f
        val cY = Config.WORLD_HEIGHT / 2f

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color(0f, 0f, 0f, 0.65f)
        shapeRenderer.rect(0f, 0f, Config.WORLD_WIDTH, Config.WORLD_HEIGHT)

        shapeRenderer.color = Color(0.18f, 0.15f, 0.12f, 0.98f)
        shapeRenderer.rect(cX - 180f, cY - 165f, 360f, 330f)

        shapeRenderer.color = Color(0.72f, 0.22f, 0.18f, 1f)
        shapeRenderer.rect(cX - 120f, cY + 115f, 240f, 38f)

        shapeRenderer.color = Color(0.22f, 0.55f, 0.28f, 1f)
        shapeRenderer.rect(cX - 120f, cY + 50f, 240f, 44f)

        shapeRenderer.color = Color(0.85f, 0.55f, 0.15f, 1f)
        shapeRenderer.rect(cX - 120f, cY - 10f, 240f, 44f)

        shapeRenderer.color = Color(0.25f, 0.45f, 0.75f, 1f)
        shapeRenderer.rect(cX - 120f, cY - 70f, 240f, 44f)

        val cheatBtnColor = if (CheatManager.isCheatEnabled) Color(0.60f, 0.20f, 0.85f, 1f) else Color(0.35f, 0.35f, 0.40f, 1f)
        shapeRenderer.color = cheatBtnColor
        shapeRenderer.rect(cX - 120f, cY - 130f, 240f, 44f)
        shapeRenderer.end()

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color.GOLD
        shapeRenderer.rect(cX - 180f, cY - 165f, 360f, 330f)
        shapeRenderer.rect(cX - 120f, cY + 50f, 240f, 44f)
        shapeRenderer.rect(cX - 120f, cY - 10f, 240f, 44f)
        shapeRenderer.rect(cX - 120f, cY - 70f, 240f, 44f)
        shapeRenderer.rect(cX - 120f, cY - 130f, 240f, 44f)
        shapeRenderer.end()

        batch.begin()
        drawOutlinedText(batch, font, "GAME PAUSED", cX - 60f, cY + 140f, Color.WHITE)
        drawOutlinedText(batch, font, "RESUME", cX - 35f, cY + 78f, Color.WHITE)
        drawOutlinedText(batch, font, "RESTART", cX - 38f, cY + 18f, Color.WHITE)
        drawOutlinedText(batch, font, "WORLD MAP", cX - 50f, cY - 42f, Color.WHITE)

        val cheatText = "CHEAT: " + if (CheatManager.isCheatEnabled) "ON" else "OFF"
        val cheatTextColor = if (CheatManager.isCheatEnabled) Color.YELLOW else Color.LIGHT_GRAY
        drawOutlinedText(batch, font, cheatText, cX - 50f, cY - 102f, cheatTextColor)
        batch.end()
    }

    fun renderCheatFloatingButton(
        shapeRenderer: ShapeRenderer,
        batch: SpriteBatch,
        font: BitmapFont
    ) {
        val btnX = Config.WORLD_WIDTH - 50f
        val btnY = Config.WORLD_HEIGHT - 90f

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color(0.60f, 0.20f, 0.85f, 0.95f)
        shapeRenderer.circle(btnX, btnY, 20f)
        shapeRenderer.end()

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color.GOLD
        shapeRenderer.circle(btnX, btnY, 20f)
        shapeRenderer.end()

        batch.begin()
        drawOutlinedText(batch, font, "C", btnX - 6f, btnY + 7f, Color.YELLOW)
        batch.end()
    }

    fun renderCheatMenuPopup(
        shapeRenderer: ShapeRenderer,
        batch: SpriteBatch,
        font: BitmapFont,
        smallFont: BitmapFont
    ) {
        val pX = Config.WORLD_WIDTH / 2f
        val pY = Config.WORLD_HEIGHT / 2f

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color(0f, 0f, 0f, 0.60f)
        shapeRenderer.rect(0f, 0f, Config.WORLD_WIDTH, Config.WORLD_HEIGHT)

        shapeRenderer.color = Color(0.16f, 0.14f, 0.18f, 0.98f)
        shapeRenderer.rect(pX - 150f, pY - 150f, 300f, 300f)

        shapeRenderer.color = Color(0.55f, 0.18f, 0.75f, 1f)
        shapeRenderer.rect(pX - 130f, pY + 98f, 260f, 36f)

        shapeRenderer.color = Color(0.85f, 0.65f, 0.12f, 1f)
        shapeRenderer.rect(pX - 120f, pY + 50f, 240f, 36f)

        val dmgBtnColor = if (CheatManager.isHighDamageEnabled) Color(0.20f, 0.65f, 0.25f, 1f) else Color(0.75f, 0.25f, 0.20f, 1f)
        shapeRenderer.color = dmgBtnColor
        shapeRenderer.rect(pX - 120f, pY + 5f, 240f, 36f)

        shapeRenderer.color = Color(0.80f, 0.30f, 0.15f, 1f)
        shapeRenderer.rect(pX - 120f, pY - 40f, 240f, 36f)

        shapeRenderer.color = Color(0.15f, 0.65f, 0.40f, 1f)
        shapeRenderer.rect(pX - 120f, pY - 85f, 240f, 36f)

        shapeRenderer.color = Color(0.35f, 0.35f, 0.38f, 1f)
        shapeRenderer.rect(pX - 60f, pY - 135f, 120f, 30f)
        shapeRenderer.end()

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color.GOLD
        shapeRenderer.rect(pX - 150f, pY - 150f, 300f, 300f)
        shapeRenderer.rect(pX - 120f, pY + 50f, 240f, 36f)
        shapeRenderer.rect(pX - 120f, pY + 5f, 240f, 36f)
        shapeRenderer.rect(pX - 120f, pY - 40f, 240f, 36f)
        shapeRenderer.rect(pX - 120f, pY - 85f, 240f, 36f)
        shapeRenderer.rect(pX - 60f, pY - 135f, 120f, 30f)
        shapeRenderer.end()

        batch.begin()
        drawOutlinedText(batch, font, "CHEAT MENU", pX - 58f, pY + 122f, Color.YELLOW)
        drawOutlinedText(batch, font, "+1000 GOLD", pX - 60f, pY + 74f, Color.WHITE)

        val dmgText = if (CheatManager.isHighDamageEnabled) "300 DMG: ON" else "300 DMG: OFF"
        drawOutlinedText(batch, font, dmgText, pX - 62f, pY + 29f, Color.WHITE)

        drawOutlinedText(batch, font, "KILL ALL MOBS", pX - 68f, pY - 16f, Color.WHITE)
        drawOutlinedText(batch, font, "WIN LEVEL (3★)", pX - 70f, pY - 61f, Color.GOLD)

        drawOutlinedText(batch, smallFont, "CLOSE", pX - 22f, pY - 114f, Color.LIGHT_GRAY)
        batch.end()
    }

    fun renderVictoryDialog(
        shapeRenderer: ShapeRenderer,
        batch: SpriteBatch,
        font: BitmapFont,
        smallFont: BitmapFont,
        earnedStars: Int,
        isChallengeMode: Boolean = false
    ) {
        val cX = Config.WORLD_WIDTH / 2f
        val cY = Config.WORLD_HEIGHT / 2f

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color(0f, 0f, 0f, 0.65f)
        shapeRenderer.rect(0f, 0f, Config.WORLD_WIDTH, Config.WORLD_HEIGHT)

        shapeRenderer.color = Color(0.18f, 0.14f, 0.10f, 0.98f)
        shapeRenderer.rect(cX - 220f, cY - 140f, 440f, 280f)

        shapeRenderer.color = Color(0.80f, 0.20f, 0.15f, 1f)
        shapeRenderer.rect(cX - 160f, cY + 110f, 320f, 42f)

        shapeRenderer.color = Color(0.20f, 0.50f, 0.85f, 1f)
        shapeRenderer.rect(cX - 120f, cY - 115f, 240f, 46f)
        shapeRenderer.end()

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color.GOLD
        shapeRenderer.rect(cX - 220f, cY - 140f, 440f, 280f)
        shapeRenderer.rect(cX - 160f, cY + 110f, 320f, 42f)
        shapeRenderer.rect(cX - 120f, cY - 115f, 240f, 46f)
        shapeRenderer.end()

        val starCenterY = cY + 25f
        if (isChallengeMode) {
            drawStar(shapeRenderer, cX, starCenterY, 34f, true)
        } else {
            val starSpacing = 68f
            for (i in 0 until 3) {
                val starX = cX + (i - 1) * starSpacing
                val isFilled = (i < earnedStars)
                val radius = if (i == 1) 28f else 23f
                drawStar(shapeRenderer, starX, starCenterY, radius, isFilled)
            }
        }

        batch.begin()
        drawOutlinedText(batch, font, "VICTORY!", cX - 48f, cY + 138f, Color.WHITE)

        val victorySub = if (isChallengeMode) "CHALLENGE CLEARED WITH HONORS!" else "STAGE CLEARED WITH HONORS!"
        drawOutlinedText(batch, smallFont, victorySub, cX - 115f, cY - 20f, Color(0.92f, 0.88f, 0.80f, 1f))

        val starsText = if (isChallengeMode) "EXTRA STAR EARNED: +1 ★" else "STARS EARNED: $earnedStars / 3"
        drawOutlinedText(batch, smallFont, starsText, if (isChallengeMode) cX - 85f else cX - 68f, cY - 45f, Color.GOLD)

        drawOutlinedText(batch, font, "CONTINUE", cX - 44f, cY - 86f, Color.WHITE)
        batch.end()
    }

    fun drawStar(shapeRenderer: ShapeRenderer, centerX: Float, centerY: Float, outerRadius: Float, isFilled: Boolean) {
        val innerRadius = outerRadius * 0.45f
        val points = mutableListOf<Vector2>()
        for (i in 0 until 10) {
            val angle = Math.toRadians((90.0 + i * 36.0))
            val r = if (i % 2 == 0) outerRadius else innerRadius
            val px = centerX + (r * Math.cos(angle)).toFloat()
            val py = centerY + (r * Math.sin(angle)).toFloat()
            points.add(Vector2(px, py))
        }

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color(0.12f, 0.10f, 0.08f, 0.8f)
        for (i in 0 until 10) {
            val next = (i + 1) % 10
            shapeRenderer.triangle(centerX, centerY - 2f, points[i].x, points[i].y - 2f, points[next].x, points[next].y - 2f)
        }

        val bodyColor = if (isFilled) Color(0.98f, 0.82f, 0.15f, 1f) else Color(0.38f, 0.38f, 0.42f, 0.9f)
        shapeRenderer.color = bodyColor
        for (i in 0 until 10) {
            val next = (i + 1) % 10
            shapeRenderer.triangle(centerX, centerY, points[i].x, points[i].y, points[next].x, points[next].y)
        }

        if (isFilled) {
            shapeRenderer.color = Color(1f, 0.95f, 0.65f, 0.75f)
            for (i in listOf(0, 8)) {
                val next = (i + 1) % 10
                shapeRenderer.triangle(centerX, centerY, points[i].x, points[i].y, points[next].x, points[next].y)
            }
        }
        shapeRenderer.end()

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = if (isFilled) Color(0.85f, 0.60f, 0.10f, 1f) else Color(0.25f, 0.25f, 0.28f, 1f)
        for (i in 0 until 10) {
            val next = (i + 1) % 10
            shapeRenderer.line(points[i].x, points[i].y, points[next].x, points[next].y)
        }
        shapeRenderer.end()
    }

    fun renderSpellUI(
        shapeRenderer: ShapeRenderer,
        batch: SpriteBatch,
        font: BitmapFont,
        smallFont: BitmapFont,
        selectedSpell: ActiveSpell,
        rainOfFireCooldown: Float,
        reinforcementCooldown: Float
    ) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = if (selectedSpell == ActiveSpell.RAIN_OF_FIRE) Color.YELLOW else Color.DARK_GRAY
        shapeRenderer.circle(45f, 45f, 27f)
        shapeRenderer.color = if (rainOfFireCooldown <= 0f) Color(0.9f, 0.25f, 0.1f, 1f) else Color.GRAY
        shapeRenderer.circle(45f, 45f, 22f)

        shapeRenderer.color = if (selectedSpell == ActiveSpell.REINFORCEMENTS) Color.YELLOW else Color.DARK_GRAY
        shapeRenderer.circle(115f, 45f, 27f)
        shapeRenderer.color = if (reinforcementCooldown <= 0f) Color(0.2f, 0.6f, 0.9f, 1f) else Color.GRAY
        shapeRenderer.circle(115f, 45f, 22f)
        shapeRenderer.end()

        batch.begin()
        if (rainOfFireCooldown > 0f) {
            drawOutlinedText(batch, smallFont, "${rainOfFireCooldown.toInt()}s", 34f, 50f, Color.WHITE)
        } else {
            drawOutlinedText(batch, smallFont, "FIRE", 32f, 50f, Color.WHITE)
        }

        if (reinforcementCooldown > 0f) {
            drawOutlinedText(batch, smallFont, "${reinforcementCooldown.toInt()}s", 104f, 50f, Color.WHITE)
        } else {
            drawOutlinedText(batch, smallFont, "ARMY", 98f, 50f, Color.WHITE)
        }

        if (selectedSpell == ActiveSpell.RAIN_OF_FIRE) {
            drawOutlinedText(batch, font, "TAP ROAD TO DROP METEORS! (TAP AGAIN TO CANCEL)", Config.WORLD_WIDTH / 2f - 240f, 60f, Color.ORANGE)
        } else if (selectedSpell == ActiveSpell.REINFORCEMENTS) {
            drawOutlinedText(batch, font, "TAP ROAD TO SUMMON 2 FARMERS! (TAP AGAIN TO CANCEL)", Config.WORLD_WIDTH / 2f - 240f, 60f, Color.SKY)
        }
        batch.end()
    }

    fun renderEarlyCallWaveButtons(
        shapeRenderer: ShapeRenderer,
        batch: SpriteBatch,
        font: BitmapFont,
        smallFont: BitmapFont,
        waveManager: WaveManager,
        allPaths: List<List<Vector2>>
    ) {
        val canShow = waveManager.currentWaveIndex == -1 || waveManager.isCountdownActive
        if (!canShow || waveManager.currentWaveDisplay >= waveManager.totalWaves) return

        val activePaths = waveManager.getActiveSpawningPathsForNextWave()
        val spawnPoints = mutableListOf<Vector2>()

        for (pIdx in activePaths) {
            if (pIdx in allPaths.indices && allPaths[pIdx].isNotEmpty()) {
                val startP = allPaths[pIdx][0]
                val safeX = startP.x.coerceIn(60f, Config.WORLD_WIDTH - 60f)
                val safeY = startP.y.coerceIn(60f, Config.WORLD_HEIGHT - 60f)
                spawnPoints.add(Vector2(safeX, safeY))
            }
        }

        if (waveManager.isCountdownActive) {
            val progress = (waveManager.waveCountdownTimer / waveManager.waveMaxCountdown).coerceIn(0f, 1f)
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
            shapeRenderer.color = Color(1f, 0.8f, 0.1f, 0.4f)
            for (p in spawnPoints) {
                shapeRenderer.arc(p.x, p.y, 34f, 90f, 360f * progress)
            }
            shapeRenderer.end()
        }

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        for (p in spawnPoints) {
            shapeRenderer.color = Color(0.2f, 0.2f, 0.22f, 0.95f)
            shapeRenderer.circle(p.x, p.y, 28f)
            shapeRenderer.color = Color(0.85f, 0.2f, 0.2f, 1f)
            shapeRenderer.circle(p.x, p.y, 22f)
        }
        shapeRenderer.end()

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color.GOLD
        for (p in spawnPoints) {
            shapeRenderer.circle(p.x, p.y, 28f)
        }
        shapeRenderer.end()

        batch.begin()
        for (p in spawnPoints) {
            drawOutlinedText(batch, font, "GO!", p.x - 14f, p.y + 8f, Color.WHITE)

            if (waveManager.currentWaveIndex == -1) {
                drawOutlinedText(batch, smallFont, "START", p.x - 18f, p.y - 32f, Color.YELLOW)
            } else {
                val bonusGold = (waveManager.waveCountdownTimer * 1.5f).toInt().coerceAtLeast(5)
                drawOutlinedText(batch, smallFont, "+${bonusGold}G", p.x - 14f, p.y - 32f, Color.YELLOW)
                drawOutlinedText(batch, smallFont, "${waveManager.waveCountdownTimer.toInt()}s", p.x - 10f, p.y + 44f, Color.WHITE)
            }
        }
        batch.end()
    }

    // =========================================================================
    // HÀM VẼ WHEEL MENU & TOOLTIP CHUẨN NGUYÊN BẢN KINGDOM RUSH
    // =========================================================================
    fun renderWheelMenu(
        shapeRenderer: ShapeRenderer,
        batch: SpriteBatch,
        smallFont: BitmapFont,
        selectedSpot: Entity?,
        selectedTower: Entity?,
        previewBuildType: TowerType?,
        previewTowerAction: String?,
        isSettingRallyPoint: Boolean,
        maxRallyRadius: Float,
        buildOptions: List<Pair<TowerType, Vector2>>,
        getBranchSpecs: (TowerType) -> Pair<TowerSpecialization, TowerSpecialization>,
        maxLevelAllowed: Int = 4,
        allowedTowers: List<TowerType> = TowerType.values().toList(),
        currentLevelNumber: Int = 0,
        isPreGame: Boolean = false
    ) {
        // 1. TRƯỜNG HỢP CHỌN CỌC ĐẤT TRỐNG (XÂY THÁP MỚI)
        selectedSpot?.let { spot ->
            val p = spot.getComponent(TransformComponent::class.java).position
            val isHigh = p.y > 540f

            shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
            shapeRenderer.color = Color(1f, 1f, 1f, 0.35f)
            shapeRenderer.circle(p.x, p.y, 75f)
            shapeRenderer.end()

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
            for (opt in buildOptions) {
                val type = opt.first
                val isAllowed = allowedTowers.contains(type)
                val offset = if (isHigh) Vector2(opt.second.x, -opt.second.y) else opt.second
                val btn = Vector2(p).add(offset)

                if (isAllowed) {
                    val isSelected = (previewBuildType == type)
                    shapeRenderer.color = if (isSelected) Color(1f, 0.95f, 0.45f, 1f) else Color(0.25f, 0.25f, 0.28f, 1f)
                    shapeRenderer.circle(btn.x, btn.y, if (isSelected) 28f else 25f)

                    shapeRenderer.color = type.color
                    shapeRenderer.circle(btn.x, btn.y, 20f)
                } else {
                    shapeRenderer.color = Color(0.22f, 0.22f, 0.24f, 0.85f)
                    shapeRenderer.circle(btn.x, btn.y, 22f)
                    shapeRenderer.color = Color(0.85f, 0.2f, 0.2f, 0.9f)
                    shapeRenderer.rectLine(btn.x - 11f, btn.y - 11f, btn.x + 11f, btn.y + 11f, 3.5f)
                    shapeRenderer.rectLine(btn.x - 11f, btn.y + 11f, btn.x + 11f, btn.y - 11f, 3.5f)
                }
            }
            shapeRenderer.end()

            batch.begin()
            for (opt in buildOptions) {
                val type = opt.first
                val isAllowed = allowedTowers.contains(type)
                val offset = if (isHigh) Vector2(opt.second.x, -opt.second.y) else opt.second
                val btn = Vector2(p).add(offset)

                if (isAllowed) {
                    var cost = type.cost
                    if (type == TowerType.ARCHER && UpgradeManager.getEffectiveArcherLevel() >= 3) cost = 60
                    if (type == TowerType.BARRACKS && UpgradeManager.getEffectiveBarracksLevel() >= 2) cost = 60
                    if (type == TowerType.MAGE && UpgradeManager.getEffectiveMageLevel() >= 3) cost = 90
                    if (type == TowerType.ARTILLERY && UpgradeManager.getEffectiveArtilleryLevel() >= 3) cost = (cost * 0.9f).toInt()

                    val canAfford = EconomyManager.gold >= cost
                    drawOutlinedText(batch, smallFont, type.title, btn.x - 24f, btn.y + 40f, Color.WHITE)
                    drawOutlinedText(batch, smallFont, "${cost}G", btn.x - 14f, btn.y - 28f, if (canAfford) Color.GOLD else Color.RED)
                } else {
                    drawOutlinedText(batch, smallFont, "LOCKED", btn.x - 24f, btn.y + 35f, Color.GRAY)
                }
            }
            batch.end()

            // VẼ TOOLTIP RA NGOÀI VÒNG TRÒN, KHÔNG CHE KHUẤT CÁC NÚT KHÁC
            previewBuildType?.let { type ->
                if (allowedTowers.contains(type)) {
                    val opt = buildOptions.first { it.first == type }
                    val offset = if (isHigh) Vector2(opt.second.x, -opt.second.y) else opt.second
                    val btn = Vector2(p).add(offset)
                    renderKingdomRushBuildTooltip(shapeRenderer, batch, smallFont, btn, p, type)
                }
            }
        }

        // 2. TRƯỜNG HỢP CHỌN THÁP ĐÃ XÂY
        selectedTower?.let { tower ->
            val tPos = tower.getComponent(TransformComponent::class.java).position
            val tComp = tower.getComponent(TowerComponent::class.java)
            val isHigh = tPos.y > 540f

            val upgradeY = if (isHigh) tPos.y - 65f else tPos.y + 65f
            val sellY = if (isHigh) tPos.y + 65f else tPos.y - 65f
            val branchY = if (isHigh) tPos.y - 60f else tPos.y + 60f

            shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
            shapeRenderer.color = Color(0.2f, 0.8f, 1f, 0.45f)
            shapeRenderer.circle(tPos.x, tPos.y, tComp.range)
            shapeRenderer.end()

            val canRally = tComp.towerType == TowerType.BARRACKS ||
                (tComp.specialization == TowerSpecialization.SORCERER_MAGE && tComp.skill2Level > 0)
            if (isSettingRallyPoint && canRally) {
                shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
                shapeRenderer.color = Color(1f, 0.85f, 0.2f, 0.85f)
                shapeRenderer.circle(tPos.x, tPos.y, maxRallyRadius)
                shapeRenderer.end()
            }

            val canUpgrade = tComp.level < maxLevelAllowed
            val canUpgradeToSpec = if (tComp.level == 3) {
                when (currentLevelNumber) {
                    0 -> true
                    5 -> tComp.towerType == TowerType.ARCHER && maxLevelAllowed >= 4
                    6 -> (tComp.towerType == TowerType.ARCHER || tComp.towerType == TowerType.MAGE || tComp.towerType == TowerType.BARRACKS)
                    7 -> (tComp.towerType == TowerType.ARCHER || tComp.towerType == TowerType.MAGE || tComp.towerType == TowerType.BARRACKS)
                    8, 9, 10, 11, 12 -> true
                    else -> maxLevelAllowed >= 4
                }
            } else false

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)

            // Nút nâng cấp Level 1 -> 2 -> 3
            if (tComp.level < 3 && canUpgrade) {
                val isSelected = (previewTowerAction == "UPGRADE")
                shapeRenderer.color = if (isSelected) Color(1f, 0.95f, 0.45f, 1f) else Color.DARK_GRAY
                shapeRenderer.circle(tPos.x, upgradeY, if (isSelected) 28f else 25f)
                shapeRenderer.color = Color.GREEN
                shapeRenderer.circle(tPos.x, upgradeY, 20f)
            }

            // Nút chuyên hóa nhánh cấp 4
            if (canUpgradeToSpec) {
                val isSingleBranchOnly = (currentLevelNumber in listOf(5, 6))
                if (isSingleBranchOnly) {
                    val isSelected = (previewTowerAction == "SPEC_1")
                    shapeRenderer.color = if (isSelected) Color(1f, 0.95f, 0.45f, 1f) else Color.DARK_GRAY
                    shapeRenderer.circle(tPos.x, branchY, if (isSelected) 28f else 25f)
                    shapeRenderer.color = Color.GOLD
                    shapeRenderer.circle(tPos.x, branchY, 20f)
                } else {
                    val isSel1 = (previewTowerAction == "SPEC_1")
                    shapeRenderer.color = if (isSel1) Color(1f, 0.95f, 0.45f, 1f) else Color.DARK_GRAY
                    shapeRenderer.circle(tPos.x - 45f, branchY, if (isSel1) 27f else 24f)
                    shapeRenderer.color = Color.GOLD
                    shapeRenderer.circle(tPos.x - 45f, branchY, 18f)

                    val isSel2 = (previewTowerAction == "SPEC_2")
                    shapeRenderer.color = if (isSel2) Color(1f, 0.95f, 0.45f, 1f) else Color.DARK_GRAY
                    shapeRenderer.circle(tPos.x + 45f, branchY, if (isSel2) 27f else 24f)
                    shapeRenderer.color = Color.ORANGE
                    shapeRenderer.circle(tPos.x + 45f, branchY, 18f)
                }
            }

            // Nút kỹ năng cấp 4
            if (tComp.level == 4) {
                val has3Skills = tComp.towerType == TowerType.BARRACKS
                val skillCenterY = if (isHigh) tPos.y - 55f else tPos.y + 55f
                val skillMiddleY = if (isHigh) tPos.y - 75f else tPos.y + 75f

                if (has3Skills) {
                    val isSel1 = (previewTowerAction == "SKILL_1")
                    shapeRenderer.color = if (isSel1) Color(1f, 0.95f, 0.45f, 1f) else Color.DARK_GRAY
                    shapeRenderer.circle(tPos.x - 65f, skillCenterY, if (isSel1) 27f else 24f)
                    shapeRenderer.color = Color.CYAN
                    shapeRenderer.circle(tPos.x - 65f, skillCenterY, 18f)

                    val isSel2 = (previewTowerAction == "SKILL_2")
                    shapeRenderer.color = if (isSel2) Color(1f, 0.95f, 0.45f, 1f) else Color.DARK_GRAY
                    shapeRenderer.circle(tPos.x, skillMiddleY, if (isSel2) 27f else 24f)
                    shapeRenderer.color = Color.GOLD
                    shapeRenderer.circle(tPos.x, skillMiddleY, 18f)

                    val isSel3 = (previewTowerAction == "SKILL_3")
                    shapeRenderer.color = if (isSel3) Color(1f, 0.95f, 0.45f, 1f) else Color.DARK_GRAY
                    shapeRenderer.circle(tPos.x + 65f, skillCenterY, if (isSel3) 27f else 24f)
                    shapeRenderer.color = Color.MAGENTA
                    shapeRenderer.circle(tPos.x + 65f, skillCenterY, 18f)
                } else {
                    val isSel1 = (previewTowerAction == "SKILL_1")
                    shapeRenderer.color = if (isSel1) Color(1f, 0.95f, 0.45f, 1f) else Color.DARK_GRAY
                    shapeRenderer.circle(tPos.x - 55f, skillCenterY, if (isSel1) 27f else 24f)
                    shapeRenderer.color = Color.CYAN
                    shapeRenderer.circle(tPos.x - 55f, skillCenterY, 18f)

                    val isSel2 = (previewTowerAction == "SKILL_2")
                    shapeRenderer.color = if (isSel2) Color(1f, 0.95f, 0.45f, 1f) else Color.DARK_GRAY
                    shapeRenderer.circle(tPos.x + 55f, skillCenterY, if (isSel2) 27f else 24f)
                    shapeRenderer.color = Color.MAGENTA
                    shapeRenderer.circle(tPos.x + 55f, skillCenterY, 18f)
                }
            }

            // Nút bán tháp
            val isSellSelected = (previewTowerAction == "SELL")
            shapeRenderer.color = if (isSellSelected) Color(1f, 0.95f, 0.45f, 1f) else Color.DARK_GRAY
            shapeRenderer.circle(tPos.x, sellY, if (isSellSelected) 28f else 25f)
            shapeRenderer.color = Color.FIREBRICK
            shapeRenderer.circle(tPos.x, sellY, 20f)

            if (canRally) {
                shapeRenderer.color = Color.CYAN
                shapeRenderer.circle(tPos.x - 65f, tPos.y, 22f)
            }
            shapeRenderer.end()

            batch.begin()
            if (tComp.level < 3 && canUpgrade) {
                val labelY = if (isHigh) upgradeY - 26f else upgradeY + 39f
                val costY = if (isHigh) upgradeY + 32f else upgradeY - 9f
                var cost = tComp.upgradeCost
                if (tComp.towerType == TowerType.MAGE && UpgradeManager.getEffectiveMageLevel() >= 3) cost = (cost * 0.9f).toInt()
                if (tComp.towerType == TowerType.ARTILLERY && UpgradeManager.getEffectiveArtilleryLevel() >= 3) cost = (cost * 0.9f).toInt()

                drawOutlinedText(batch, smallFont, "LVL ${tComp.level + 1}", tPos.x - 20f, labelY, Color.WHITE)
                drawOutlinedText(batch, smallFont, "${cost}G", tPos.x - 14f, costY, Color.GOLD)
            } else if (canUpgradeToSpec) {
                val (spec1, spec2) = getBranchSpecs(tComp.towerType)
                val labelY = if (isHigh) branchY - 24f else branchY + 40f
                val costY = if (isHigh) branchY + 30f else branchY - 8f

                val isSingleBranchOnly = (currentLevelNumber in listOf(5, 6))
                if (isSingleBranchOnly) {
                    drawOutlinedText(batch, smallFont, spec1.displayName, tPos.x - 30f, labelY, Color.GOLD)
                    drawOutlinedText(batch, smallFont, "${spec1.branchCost}G", tPos.x - 14f, costY, Color.YELLOW)
                } else {
                    drawOutlinedText(batch, smallFont, spec1.displayName, tPos.x - 75f, labelY, Color.GOLD)
                    drawOutlinedText(batch, smallFont, "${spec1.branchCost}G", tPos.x - 58f, costY, Color.YELLOW)
                    drawOutlinedText(batch, smallFont, spec2.displayName, tPos.x + 18f, labelY, Color.ORANGE)
                    drawOutlinedText(batch, smallFont, "${spec2.branchCost}G", tPos.x + 35f, costY, Color.YELLOW)
                }
            } else if (tComp.level == 4) {
                val spec = tComp.specialization
                val has3Skills = tComp.towerType == TowerType.BARRACKS
                val textBaseY = if (isHigh) tPos.y - 75f else tPos.y + 92f

                if (has3Skills) {
                    drawOutlinedText(batch, smallFont, "S1:${tComp.skill1Level}/3", tPos.x - 105f, textBaseY, Color.CYAN)
                    val maxSkill2 = if (spec == TowerSpecialization.HOLY_ORDER) 1 else 3
                    drawOutlinedText(batch, smallFont, "S2:${tComp.skill2Level}/$maxSkill2", tPos.x - 20f, textBaseY + 20f, Color.YELLOW)
                    drawOutlinedText(batch, smallFont, "S3:${tComp.skill3Level}/3", tPos.x + 40f, textBaseY, Color.MAGENTA)
                } else {
                    drawOutlinedText(batch, smallFont, "${spec.skill1Name} (${tComp.skill1Level}/3)", tPos.x - 110f, textBaseY, Color.CYAN)
                    drawOutlinedText(batch, smallFont, "${spec.skill2Name} (${tComp.skill2Level}/3)", tPos.x + 25f, textBaseY, Color.MAGENTA)
                }
            }

            val sellLabelY = if (isHigh) sellY + 38f else sellY - 29f
            val sellCostY = if (isHigh) sellY - 9f else sellY + 9f

            val refundRatio = when {
                isPreGame -> 1.0f
                tComp.towerType == TowerType.ARCHER && UpgradeManager.getEffectiveArcherLevel() >= 1 -> 0.90f
                else -> 0.70f
            }
            val refundAmount = (tComp.totalInvestedGold * refundRatio).toInt()

            drawOutlinedText(batch, smallFont, "SELL", tPos.x - 16f, sellLabelY, Color.WHITE)
            drawOutlinedText(batch, smallFont, "+${refundAmount}G", tPos.x - 20f, sellCostY, Color.GOLD)

            if (canRally) {
                drawOutlinedText(batch, smallFont, "RALLY", tPos.x - 94f, tPos.y + 8f, Color.WHITE)
            }
            batch.end()

            // VẼ BẢNG TOOLTIP RA NGOÀI VÒNG TRÒN
            previewTowerAction?.let { action ->
                val has3Skills = tComp.towerType == TowerType.BARRACKS
                val skillCenterY = if (isHigh) tPos.y - 55f else tPos.y + 55f
                val skillMiddleY = if (isHigh) tPos.y - 75f else tPos.y + 75f

                val btn = when (action) {
                    "UPGRADE" -> Vector2(tPos.x, upgradeY)
                    "SPEC_1" -> Vector2(tPos.x - 45f, branchY)
                    "SPEC_2" -> Vector2(tPos.x + 45f, branchY)
                    "SKILL_1" -> Vector2(if (has3Skills) tPos.x - 65f else tPos.x - 55f, skillCenterY)
                    "SKILL_2" -> Vector2(if (has3Skills) tPos.x else tPos.x + 55f, if (has3Skills) skillMiddleY else skillCenterY)
                    "SKILL_3" -> Vector2(tPos.x + 65f, skillCenterY)
                    "SELL" -> Vector2(tPos.x, sellY)
                    else -> tPos
                }
                renderKingdomRushActionTooltip(shapeRenderer, batch, smallFont, btn, tPos, tComp, action)
            }
        }
    }

    // HÀM TỰ ĐỘNG XUỐNG DÒNG (NGĂN CHẶN TRIỆT ĐỂ CHỮ TRÀN KHUNG)
    private fun wrapText(text: String, maxCharsPerLine: Int = 26): List<String> {
        val words = text.split(" ")
        val lines = mutableListOf<String>()
        var currentLine = ""
        for (word in words) {
            if (currentLine.isEmpty()) {
                currentLine = word
            } else if (currentLine.length + 1 + word.length <= maxCharsPerLine) {
                currentLine += " $word"
            } else {
                lines.add(currentLine)
                currentLine = word
            }
        }
        if (currentLine.isNotEmpty()) lines.add(currentLine)
        return lines
    }

    // =========================================================================
    // HỘP TOOLTIP XÂY THÁP MỚI CHUẨN NGUYÊN BẢN KINGDOM RUSH
    // =========================================================================
    private fun renderKingdomRushBuildTooltip(
        shapeRenderer: ShapeRenderer,
        batch: SpriteBatch,
        smallFont: BitmapFont,
        btnPos: Vector2,
        spotCenter: Vector2,
        type: TowerType
    ) {
        val (title, desc, stats) = when (type) {
            TowerType.ARCHER -> Triple(
                "Archer Tower",
                "Archers ready to strike at your enemies from a distance.",
                listOf("Damage:" to "4-6", "Reload:" to "Average")
            )
            TowerType.BARRACKS -> Triple(
                "Barracks",
                "Trains militia, tough soldiers that block and damage your enemies.",
                listOf("Life:" to "50", "Damage:" to "1-3", "Physical Armor:" to "None", "Respawn:" to "10 secs")
            )
            TowerType.MAGE -> Triple(
                "Mages",
                "Mages cast armor piercing bolts at your enemies, ignoring any physical protection.",
                listOf("Damage:" to "9-17", "Reload:" to "Slow")
            )
            TowerType.ARTILLERY -> Triple(
                "Dwarven Bombard",
                "Bombards ground enemies dealing area damage.",
                listOf("Damage:" to "8-15", "Reload:" to "Very Slow")
            )
            else -> Triple("Special Tower", "Strategic structure.", emptyList())
        }

        var cost = type.cost
        if (type == TowerType.ARCHER && UpgradeManager.getEffectiveArcherLevel() >= 3) cost = 60
        if (type == TowerType.BARRACKS && UpgradeManager.getEffectiveBarracksLevel() >= 2) cost = 60
        if (type == TowerType.MAGE && UpgradeManager.getEffectiveMageLevel() >= 3) cost = 90
        if (type == TowerType.ARTILLERY && UpgradeManager.getEffectiveArtilleryLevel() >= 3) cost = (cost * 0.9f).toInt()
        val canAfford = EconomyManager.gold >= cost

        val prompt = if (canAfford) "[ Cost: ${cost}G - Tap to confirm ]" else "[ Cost: ${cost}G - Not enough gold ]"
        drawKingdomRushTooltipBox(shapeRenderer, batch, smallFont, btnPos, spotCenter, title, desc, stats, prompt, canAfford)
    }

    // =========================================================================
    // HỘP TOOLTIP NÂNG CẤP, NHÁNH, KỸ NĂNG, BÁN THÁP CHUẨN NGUYÊN BẢN
    // =========================================================================
    private fun renderKingdomRushActionTooltip(
        shapeRenderer: ShapeRenderer,
        batch: SpriteBatch,
        smallFont: BitmapFont,
        btnPos: Vector2,
        spotCenter: Vector2,
        tComp: TowerComponent,
        action: String
    ) {
        when (action) {
            "UPGRADE" -> {
                var upCost = tComp.upgradeCost
                if (tComp.towerType == TowerType.MAGE && UpgradeManager.getEffectiveMageLevel() >= 3) upCost = (upCost * 0.9f).toInt()
                if (tComp.towerType == TowerType.ARTILLERY && UpgradeManager.getEffectiveArtilleryLevel() >= 3) upCost = (upCost * 0.9f).toInt()
                val canAfford = EconomyManager.gold >= upCost

                val title = when (tComp.towerType) {
                    TowerType.ARCHER -> if (tComp.level == 1) "Marksman Tower" else "Sharpshooter Tower"
                    TowerType.BARRACKS -> if (tComp.level == 1) "Footmen Barracks" else "Knights Barracks"
                    TowerType.MAGE -> if (tComp.level == 1) "Adept Tower" else "Wizard Tower"
                    TowerType.ARTILLERY -> if (tComp.level == 1) "Dwarven Artillery" else "Dwarven Howitzer"
                    else -> "Upgraded Tower"
                }

                val desc = "Reinforces defensive structures with enhanced combat capabilities."
                val stats = when (tComp.towerType) {
                    TowerType.ARCHER -> listOf("Damage:" to if (tComp.level == 1) "7-11" else "10-16", "Reload:" to "Fast")
                    TowerType.BARRACKS -> listOf("Life:" to if (tComp.level == 1) "100" else "150", "Damage:" to if (tComp.level == 1) "3-4" else "6-10", "Armor:" to if (tComp.level == 1) "Light" else "Medium", "Respawn:" to "10 secs")
                    TowerType.MAGE -> listOf("Damage:" to if (tComp.level == 1) "23-43" else "40-74", "Reload:" to "Slow")
                    TowerType.ARTILLERY -> listOf("Damage:" to if (tComp.level == 1) "20-40" else "30-60", "Reload:" to "Very Slow")
                    else -> emptyList()
                }

                val prompt = if (canAfford) "[ Cost: ${upCost}G - Tap to upgrade ]" else "[ Cost: ${upCost}G - Not enough gold ]"
                drawKingdomRushTooltipBox(shapeRenderer, batch, smallFont, btnPos, spotCenter, title, desc, stats, prompt, canAfford)
            }

            "SPEC_1", "SPEC_2" -> {
                val spec = if (action == "SPEC_1") {
                    when (tComp.towerType) {
                        TowerType.ARCHER -> TowerSpecialization.RANGERS_HIDEOUT
                        TowerType.BARRACKS -> TowerSpecialization.HOLY_ORDER
                        TowerType.MAGE -> TowerSpecialization.ARCANE_WIZARD
                        TowerType.ARTILLERY -> TowerSpecialization.BIG_BERTHA
                        else -> TowerSpecialization.NONE
                    }
                } else {
                    when (tComp.towerType) {
                        TowerType.ARCHER -> TowerSpecialization.MUSKETEER_GARRISON
                        TowerType.BARRACKS -> TowerSpecialization.BARBARIAN_MEAD_HALL
                        TowerType.MAGE -> TowerSpecialization.SORCERER_MAGE
                        TowerType.ARTILLERY -> TowerSpecialization.TESLA_X104
                        else -> TowerSpecialization.NONE
                    }
                }
                val canAfford = EconomyManager.gold >= spec.branchCost

                val desc = when (spec) {
                    TowerSpecialization.RANGERS_HIDEOUT -> "Expert rangers firing rapid longbows with poison and root magic."
                    TowerSpecialization.MUSKETEER_GARRISON -> "Dwarven sharpshooters with long range sniper shots and shrapnel."
                    TowerSpecialization.HOLY_ORDER -> "Paladins blessed with heavy plate armor, divine healing, and holy smite."
                    TowerSpecialization.BARBARIAN_MEAD_HALL -> "Berserkers wielding dual throwing axes and devastating whirlwind slashes."
                    TowerSpecialization.ARCANE_WIZARD -> "Masters of arcane magic with instant disintegration and teleportation."
                    TowerSpecialization.SORCERER_MAGE -> "Powerful sorcerers that summon Earth Golems and curse enemies into sheep."
                    TowerSpecialization.BIG_BERTHA -> "The ultimate dwarven artillery firing dragon missiles and cluster bombs."
                    TowerSpecialization.TESLA_X104 -> "Electrified marvel chaining supercharged bolts through multiple foes."
                    else -> "Specialized Level 4 tower branch."
                }

                val stats = when (spec) {
                    TowerSpecialization.RANGERS_HIDEOUT -> listOf("Damage:" to "13-19", "Reload:" to "Very Fast", "Range:" to "Extreme")
                    TowerSpecialization.MUSKETEER_GARRISON -> listOf("Damage:" to "35-65", "Reload:" to "Slow", "Range:" to "Extreme")
                    TowerSpecialization.HOLY_ORDER -> listOf("Life:" to "200", "Damage:" to "12-18", "Armor:" to "Heavy")
                    TowerSpecialization.BARBARIAN_MEAD_HALL -> listOf("Life:" to "250", "Damage:" to "16-24", "Armor:" to "None")
                    TowerSpecialization.ARCANE_WIZARD -> listOf("Damage:" to "76-140", "Reload:" to "Slow", "Range:" to "Far")
                    TowerSpecialization.SORCERER_MAGE -> listOf("Damage:" to "42-78", "Reload:" to "Average", "Range:" to "Far")
                    TowerSpecialization.BIG_BERTHA -> listOf("Damage:" to "50-100", "Reload:" to "Very Slow", "Range:" to "Far")
                    TowerSpecialization.TESLA_X104 -> listOf("Damage:" to "60-110", "Reload:" to "Very Slow", "Range:" to "Average")
                    else -> emptyList()
                }

                val prompt = if (canAfford) "[ Cost: ${spec.branchCost}G - Tap to choose branch ]" else "[ Cost: ${spec.branchCost}G - Not enough gold ]"
                drawKingdomRushTooltipBox(shapeRenderer, batch, smallFont, btnPos, spotCenter, spec.displayName, desc, stats, prompt, canAfford)
            }

            "SKILL_1", "SKILL_2", "SKILL_3" -> {
                val spec = tComp.specialization
                val costDiscount = if (tComp.towerType == TowerType.ARTILLERY && UpgradeManager.getEffectiveArtilleryLevel() >= 4) 0.75f else 1.0f

                val (sName, sLvl, sMax, sCost) = when (action) {
                    "SKILL_1" -> Quadruple(spec.skill1Name, tComp.skill1Level, 3, (spec.skill1Costs.getOrElse(tComp.skill1Level) { 0 } * costDiscount).toInt())
                    "SKILL_2" -> {
                        val maxS2 = if (spec == TowerSpecialization.HOLY_ORDER) 1 else 3
                        Quadruple(spec.skill2Name, tComp.skill2Level, maxS2, (spec.skill2Costs.getOrElse(tComp.skill2Level) { 0 } * costDiscount).toInt())
                    }
                    else -> Quadruple(spec.skill3Name, tComp.skill3Level, 3, (spec.skill3Costs.getOrElse(tComp.skill3Level) { 0 } * costDiscount).toInt())
                }

                val desc = when (sName) {
                    "Polymorph" -> "Curses enemies, transforming them into harmless sheep with no armor."
                    "Elemental" -> "Summons a mighty Earth Golem with heavy armor and massive health."
                    "Death Ray" -> "Concentrates arcane power to instantly disintegrate the target."
                    "Teleport" -> "Warps groups of advancing enemies back along the road."
                    "Sniper Shot" -> "Fires a deadly sniper bullet with a chance to instantly kill the foe."
                    "Shrapnel Shot" -> "Fires explosive shrapnel shells causing massive area damage."
                    "Poison Arrows" -> "Coats arrows with deadly poison dealing heavy damage over time."
                    "Forest Wrath" -> "Calls upon thorn vines that entangle and crush groups of enemies."
                    "Healing Light" -> "Blesses paladins with holy light, rapidly restoring health in battle."
                    "Shield Valor" -> "Reinforces paladin shields, upgrading their armor to Heavy."
                    "Holy Strike" -> "Paladin attacks strike with radiant holy damage in an area."
                    "Throwing Axes" -> "Barbarians hurl heavy throwing axes at airborne and distant foes."
                    "Whirlwind" -> "Barbarians spin violently, slicing through all nearby enemies."
                    "More Axes" -> "Sharpens barbarian axes, greatly increasing base attack damage."
                    "Dragon Missile" -> "Fires long-range heat-seeking missiles with colossal blast area."
                    "Cluster Bombs" -> "Scatters secondary explosive cluster bomblets across the path."
                    "Chain Bolt" -> "Empowers lightning bolts to jump across additional targets."
                    "Overcharge" -> "Electrifies the surrounding area with a pulse damage aura."
                    else -> "Special tactical upgrade skill."
                }

                val stats = listOf(
                    "Skill Level:" to "$sLvl / $sMax",
                    "Cooldown:" to if (sName == "Polymorph") "16s - 12s" else "Standard"
                )

                val isMax = sLvl >= sMax
                val canAfford = !isMax && EconomyManager.gold >= sCost
                val prompt = when {
                    isMax -> "[ MAX LEVEL REACHED ]"
                    canAfford -> "[ Cost: ${sCost}G - Tap to learn skill ]"
                    else -> "[ Cost: ${sCost}G - Not enough gold ]"
                }

                drawKingdomRushTooltipBox(shapeRenderer, batch, smallFont, btnPos, spotCenter, sName, desc, stats, prompt, canAfford || isMax)
            }

            "SELL" -> {
                val refundRatio = if (UpgradeManager.getEffectiveArcherLevel() >= 1 && tComp.towerType == TowerType.ARCHER) 0.90f else 0.70f
                val refund = (tComp.totalInvestedGold * refundRatio).toInt()

                val title = "Sell Tower"
                val desc = "Dismantles this tower and refunds 70% of invested gold."
                val stats = listOf("Refund:" to "+${refund}G")
                val prompt = "[ Tap to confirm sell ]"
                drawKingdomRushTooltipBox(shapeRenderer, batch, smallFont, btnPos, spotCenter, title, desc, stats, prompt, true)
            }
        }
    }

    // =========================================================================
    // HỘP VẼ KHUNG CHUẨN GỐC - ĐẢM BẢO KHÔNG BAO GIỜ CHE CÁC NÚT KHÁC CỦA WHEEL
    // =========================================================================
    private fun drawKingdomRushTooltipBox(
        shapeRenderer: ShapeRenderer,
        batch: SpriteBatch,
        smallFont: BitmapFont,
        btnPos: Vector2,
        spotCenter: Vector2,
        title: String,
        desc: String,
        stats: List<Pair<String, String>>,
        promptText: String,
        isPositive: Boolean
    ) {
        val boxWidth = 245f
        val descLines = wrapText(desc, 27) // Chia dòng tối đa 27 ký tự
        val boxHeight = 22f + (descLines.size * 14.5f) + (stats.size * 15f) + (if (stats.isNotEmpty()) 10f else 0f) + 24f

        var boxX = 0f
        var boxY = 0f
        var beakEdge = "NONE"

        // 1. Nếu nút nằm ở nửa trên so với tâm wheel:
        if (btnPos.y > spotCenter.y + 15f) {
            val fitsAbove = (btnPos.y + 30f + boxHeight <= Config.WORLD_HEIGHT - 8f)
            if (fitsAbove) {
                boxY = btnPos.y + 30f
                boxX = (btnPos.x - boxWidth / 2f).coerceIn(12f, Config.WORLD_WIDTH - boxWidth - 12f)
                beakEdge = "BOTTOM"
            } else {
                // Không đủ chỗ phía trên -> Đẩy ra ngoài cánh trái hoặc phải của wheel
                if (spotCenter.x <= Config.WORLD_WIDTH / 2f) {
                    boxX = (spotCenter.x + 95f).coerceAtMost(Config.WORLD_WIDTH - boxWidth - 12f)
                    beakEdge = "LEFT"
                } else {
                    boxX = (spotCenter.x - 95f - boxWidth).coerceAtLeast(12f)
                    beakEdge = "RIGHT"
                }
                boxY = (btnPos.y - boxHeight / 2f).coerceIn(65f, Config.WORLD_HEIGHT - boxHeight - 12f)
            }
        }
        // 2. Nếu nút nằm ở nửa dưới so với tâm wheel:
        else if (btnPos.y < spotCenter.y - 15f) {
            val fitsBelow = (btnPos.y - 30f - boxHeight >= 65f)
            if (fitsBelow) {
                boxY = btnPos.y - 30f - boxHeight
                boxX = (btnPos.x - boxWidth / 2f).coerceIn(12f, Config.WORLD_WIDTH - boxWidth - 12f)
                beakEdge = "TOP"
            } else {
                if (spotCenter.x <= Config.WORLD_WIDTH / 2f) {
                    boxX = (spotCenter.x + 95f).coerceAtMost(Config.WORLD_WIDTH - boxWidth - 12f)
                    beakEdge = "LEFT"
                } else {
                    boxX = (spotCenter.x - 95f - boxWidth).coerceAtLeast(12f)
                    beakEdge = "RIGHT"
                }
                boxY = (btnPos.y - boxHeight / 2f).coerceIn(65f, Config.WORLD_HEIGHT - boxHeight - 12f)
            }
        }
        // 3. Nếu nút nằm bên phải:
        else if (btnPos.x > spotCenter.x + 15f) {
            val fitsRight = (btnPos.x + 30f + boxWidth <= Config.WORLD_WIDTH - 12f)
            if (fitsRight) {
                boxX = btnPos.x + 30f
                boxY = (btnPos.y - boxHeight / 2f).coerceIn(65f, Config.WORLD_HEIGHT - boxHeight - 12f)
                beakEdge = "LEFT"
            } else {
                boxX = (spotCenter.x - 95f - boxWidth).coerceAtLeast(12f)
                boxY = (btnPos.y - boxHeight / 2f).coerceIn(65f, Config.WORLD_HEIGHT - boxHeight - 12f)
                beakEdge = "RIGHT"
            }
        }
        // 4. Nếu nút nằm bên trái:
        else {
            val fitsLeft = (btnPos.x - 30f - boxWidth >= 12f)
            if (fitsLeft) {
                boxX = btnPos.x - 30f - boxWidth
                boxY = (btnPos.y - boxHeight / 2f).coerceIn(65f, Config.WORLD_HEIGHT - boxHeight - 12f)
                beakEdge = "RIGHT"
            } else {
                boxX = (spotCenter.x + 95f).coerceAtMost(Config.WORLD_WIDTH - boxWidth - 12f)
                boxY = (btnPos.y - boxHeight / 2f).coerceIn(65f, Config.WORLD_HEIGHT - boxHeight - 12f)
                beakEdge = "LEFT"
            }
        }

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        // Nền gỗ than sẫm chuẩn Kingdom Rush
        shapeRenderer.color = Color(0.14f, 0.13f, 0.11f, 0.96f)
        shapeRenderer.rect(boxX, boxY, boxWidth, boxHeight)

        // Vẽ mũi tên nhô ra chỉ vào nút
        when (beakEdge) {
            "BOTTOM" -> {
                val clampX = btnPos.x.coerceIn(boxX + 16f, boxX + boxWidth - 16f)
                shapeRenderer.triangle(clampX - 8f, boxY, clampX + 8f, boxY, clampX, btnPos.y + 24f)
            }
            "TOP" -> {
                val clampX = btnPos.x.coerceIn(boxX + 16f, boxX + boxWidth - 16f)
                shapeRenderer.triangle(clampX - 8f, boxY + boxHeight, clampX + 8f, boxY + boxHeight, clampX, btnPos.y - 24f)
            }
            "LEFT" -> {
                val clampY = btnPos.y.coerceIn(boxY + 14f, boxY + boxHeight - 14f)
                shapeRenderer.triangle(boxX, clampY - 7f, boxX, clampY + 7f, btnPos.x + 22f, clampY)
            }
            "RIGHT" -> {
                val clampY = btnPos.y.coerceIn(boxY + 14f, boxY + boxHeight - 14f)
                shapeRenderer.triangle(boxX + boxWidth, clampY - 7f, boxX + boxWidth, clampY + 7f, btnPos.x - 22f, clampY)
            }
        }
        shapeRenderer.end()

        // Viền vàng đồng mỏng tinh tế
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color(0.50f, 0.44f, 0.32f, 1f)
        shapeRenderer.rect(boxX, boxY, boxWidth, boxHeight)
        when (beakEdge) {
            "BOTTOM" -> {
                val clampX = btnPos.x.coerceIn(boxX + 16f, boxX + boxWidth - 16f)
                shapeRenderer.line(clampX - 8f, boxY, clampX, btnPos.y + 24f)
                shapeRenderer.line(clampX + 8f, boxY, clampX, btnPos.y + 24f)
            }
            "TOP" -> {
                val clampX = btnPos.x.coerceIn(boxX + 16f, boxX + boxWidth - 16f)
                shapeRenderer.line(clampX - 8f, boxY + boxHeight, clampX, btnPos.y - 24f)
                shapeRenderer.line(clampX + 8f, boxY + boxHeight, clampX, btnPos.y - 24f)
            }
            "LEFT" -> {
                val clampY = btnPos.y.coerceIn(boxY + 14f, boxY + boxHeight - 14f)
                shapeRenderer.line(boxX, clampY - 7f, btnPos.x + 22f, clampY)
                shapeRenderer.line(boxX, clampY + 7f, btnPos.x + 22f, clampY)
            }
            "RIGHT" -> {
                val clampY = btnPos.y.coerceIn(boxY + 14f, boxY + boxHeight - 14f)
                shapeRenderer.line(boxX + boxWidth, clampY - 7f, btnPos.x - 22f, clampY)
                shapeRenderer.line(boxX + boxWidth, clampY + 7f, btnPos.x - 22f, clampY)
            }
        }
        shapeRenderer.end()

        // Vẽ nội dung với font thu gọn nhẹ
        val oldScale = smallFont.data.scaleX
        smallFont.data.setScale(0.82f)

        batch.begin()
        var curY = boxY + boxHeight - 12f

        // 1. Tên tháp / Kỹ năng (Trắng ngà nổi bật)
        drawOutlinedText(batch, smallFont, title, boxX + 10f, curY, Color(0.98f, 0.98f, 0.94f, 1f))
        curY -= 17f

        // 2. Mô tả (Vàng cát ấm áp như game gốc)
        for (line in descLines) {
            drawOutlinedText(batch, smallFont, line, boxX + 10f, curY, Color(0.88f, 0.80f, 0.55f, 1f))
            curY -= 14f
        }
        if (stats.isNotEmpty()) curY -= 4f

        // 3. Các dòng chỉ số: Tên chỉ số (Vàng cam) | Giá trị (Trắng)
        for (st in stats) {
            drawOutlinedText(batch, smallFont, st.first, boxX + 10f, curY, Color(0.92f, 0.72f, 0.32f, 1f))
            drawOutlinedText(batch, smallFont, st.second, boxX + 115f, curY, Color.WHITE)
            curY -= 14f
        }

        // 4. Dòng trạng thái / xác nhận 2 bước ở đáy hộp
        val promptColor = if (isPositive) Color(1f, 0.90f, 0.30f, 1f) else Color(1f, 0.35f, 0.30f, 1f)
        drawOutlinedText(batch, smallFont, promptText, boxX + 10f, curY - 2f, promptColor)

        batch.end()
        smallFont.data.setScale(oldScale) // Trả lại kích thước font cũ
    }

    private fun drawBottomBarBackground(shapeRenderer: ShapeRenderer, barHeight: Float) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color(0.12f, 0.12f, 0.14f, 0.95f)
        shapeRenderer.rect(0f, 0f, Config.WORLD_WIDTH, barHeight)
        shapeRenderer.end()

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color.DARK_GRAY
        shapeRenderer.line(0f, barHeight, Config.WORLD_WIDTH, barHeight)
        shapeRenderer.end()
    }

    private fun renderShieldWithBars(
        shapeRenderer: ShapeRenderer,
        startX: Float,
        startY: Float,
        activeBars: Int,
        color: Color
    ) {
        val sX = startX
        val sY = startY

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = color
        shapeRenderer.line(sX, sY + 22f, sX + 11f, sY + 18f)
        shapeRenderer.line(sX + 11f, sY + 18f, sX + 22f, sY + 22f)
        shapeRenderer.line(sX + 22f, sY + 22f, sX + 22f, sY + 11f)
        shapeRenderer.line(sX + 22f, sY + 11f, sX + 11f, sY)
        shapeRenderer.line(sX + 11f, sY, sX, sY + 11f)
        shapeRenderer.line(sX, sY + 11f, sX, sY + 22f)
        shapeRenderer.end()

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        for (i in 0 until 3) {
            val barX = startX + 28f + (i * 10f)
            val barY = startY + 2f
            if (i < activeBars) {
                shapeRenderer.color = color
                shapeRenderer.rect(barX, barY, 6.5f, 18f)
            } else {
                shapeRenderer.color = Color(0.22f, 0.22f, 0.25f, 0.8f)
                shapeRenderer.rect(barX, barY, 6.5f, 18f)
            }
        }
        shapeRenderer.end()
    }

    fun renderBottomInfoBar(
        shapeRenderer: ShapeRenderer,
        batch: SpriteBatch,
        font: BitmapFont,
        smallFont: BitmapFont,
        selectedEnemy: Entity?,
        selectedSoldier: Entity?,
        selectedTower: Entity?,
        onClearEnemySelection: () -> Unit,
        onClearSoldierSelection: () -> Unit
    ) {
        val barHeight = 65f

        selectedEnemy?.let { enemy ->
            val hp = enemy.getComponent(HealthComponent::class.java)
            val eComp = enemy.getComponent(EnemyComponent::class.java)
            val armor = enemy.getComponent(ArmorComponent::class.java)

            if (hp == null || hp.isDead || eComp == null) {
                onClearEnemySelection()
                return
            }

            drawBottomBarBackground(shapeRenderer, barHeight)

            val pArmorBars = when (armor?.armorType) {
                ArmorType.LIGHT_ARMOR -> 1
                ArmorType.MEDIUM_ARMOR -> 2
                ArmorType.HEAVY_ARMOR -> 3
                ArmorType.GREAT_ARMOR -> 3
                else -> 0
            }
            renderShieldWithBars(shapeRenderer, 560f, 20f, pArmorBars, Color(0.75f, 0.78f, 0.82f, 1f))

            val mResistBars = when (armor?.magicResistType) {
                MagicResistType.LOW -> 1
                MagicResistType.MEDIUM -> 2
                MagicResistType.HIGH -> 3
                else -> 0
            }
            renderShieldWithBars(shapeRenderer, 700f, 20f, mResistBars, Color(0.35f, 0.75f, 1f, 1f))

            batch.begin()
            val enemyTitle = if (eComp.isSheep) "SHEEP" else eComp.enemyType.title.uppercase()
            drawOutlinedText(batch, font, enemyTitle, 30f, 42f, Color.WHITE)
            drawOutlinedText(batch, smallFont, "HP: ${hp.currentHealth.toInt()} / ${hp.maxHealth.toInt()}", 220f, 40f, Color.GREEN)
            val dmgStr = if (eComp.isSheep) "Dmg: 0" else "Dmg: ${eComp.enemyType.damageMin.toInt()}-${eComp.enemyType.damageMax.toInt()}"
            drawOutlinedText(batch, smallFont, dmgStr, 380f, 40f, Color.ORANGE)

            drawOutlinedText(batch, smallFont, "ARMOR", 565f, 54f, Color(0.75f, 0.78f, 0.82f, 1f))
            drawOutlinedText(batch, smallFont, "MAGIC", 705f, 54f, Color(0.35f, 0.75f, 1f, 1f))

            drawOutlinedText(batch, smallFont, "Skulls: -${eComp.enemyType.livesTaken}", 860f, 40f, Color.CORAL)
            drawOutlinedText(batch, smallFont, "Bounty: +${eComp.enemyType.rewardGold}G", 990f, 40f, Color.GOLD)
            batch.end()
            return
        }

        selectedSoldier?.let { sol ->
            val hp = sol.getComponent(HealthComponent::class.java)
            val sComp = sol.getComponent(SoldierComponent::class.java)
            val armor = sol.getComponent(ArmorComponent::class.java)
            if (hp == null || hp.isDead || sComp == null) {
                onClearSoldierSelection()
                return
            }

            drawBottomBarBackground(shapeRenderer, barHeight)

            val pArmorBars = when (armor?.armorType) {
                ArmorType.LIGHT_ARMOR -> 1
                ArmorType.MEDIUM_ARMOR -> 2
                ArmorType.HEAVY_ARMOR -> 3
                else -> 0
            }
            renderShieldWithBars(shapeRenderer, 650f, 20f, pArmorBars, Color(0.75f, 0.78f, 0.82f, 1f))

            batch.begin()
            drawOutlinedText(batch, font, sComp.soldierRankName.uppercase(), 40f, 42f, Color.CYAN)
            drawOutlinedText(batch, smallFont, "HP: ${hp.currentHealth.toInt()} / ${hp.maxHealth.toInt()}", 250f, 40f, Color.GREEN)
            drawOutlinedText(batch, smallFont, "Damage: ${sComp.attackDamageMin.toInt()}-${sComp.attackDamageMax.toInt()}", 420f, 40f, Color.WHITE)
            drawOutlinedText(batch, smallFont, "ARMOR", 655f, 54f, Color(0.75f, 0.78f, 0.82f, 1f))
            batch.end()
            return
        }

        selectedTower?.let { tower ->
            val tComp = tower.getComponent(TowerComponent::class.java) ?: return
            drawBottomBarBackground(shapeRenderer, barHeight)
            batch.begin()

            if (tComp.specialization == TowerSpecialization.SUNRAY_TOWER) {
                drawOutlinedText(batch, font, "SUNRAY TOWER", 40f, 42f, Color(1f, 0.85f, 0.2f, 1f))
                val dmgText = when (tComp.sunraySorcerers) {
                    1 -> "75-125 (True Dmg)"
                    2 -> "125-175 (True Dmg)"
                    3 -> "175-225 (True Dmg)"
                    4 -> "225-275 (True Dmg)"
                    else -> "0 (Needs Sorcerers)"
                }
                drawOutlinedText(batch, smallFont, "Damage: $dmgText", 300f, 40f, Color.WHITE)
                drawOutlinedText(batch, smallFont, "Sorcerers: ${tComp.sunraySorcerers}/4", 620f, 40f, Color.GOLD)

                val statusText = if (tComp.isSunrayReady && tComp.sunraySorcerers > 0) "READY TO FIRE!" else if (tComp.sunraySorcerers > 0) "CHARGING: ${tComp.sunrayChargeTimer.toInt()}s" else "INACTIVE"
                val statusColor = if (tComp.isSunrayReady && tComp.sunraySorcerers > 0) Color.YELLOW else Color.ORANGE
                drawOutlinedText(batch, smallFont, statusText, 850f, 40f, statusColor)
            } else {
                val title = if (tComp.level < 4) "${tComp.towerType.title} (LVL ${tComp.level})" else tComp.specialization.displayName.uppercase()
                drawOutlinedText(batch, font, title, 40f, 42f, tComp.towerType.color)

                if (tComp.towerType != TowerType.BARRACKS) {
                    drawOutlinedText(batch, smallFont, "Damage: ${tComp.damageMin.toInt()}-${tComp.damageMax.toInt()}", 320f, 40f, Color.WHITE)
                    drawOutlinedText(batch, smallFont, "Fire Rate: ${tComp.fireRateText}", 560f, 40f, Color.WHITE)
                    drawOutlinedText(batch, smallFont, "Range: ${tComp.rangeText}", 780f, 40f, Color.WHITE)
                } else {
                    val rank = when (tComp.level) {
                        1 -> "Militias"
                        2 -> "Footmen"
                        3 -> "Knights"
                        else -> if (tComp.specialization == TowerSpecialization.HOLY_ORDER) "Paladins" else "Barbarians"
                    }
                    drawOutlinedText(batch, smallFont, "Soldiers: 3 $rank", 320f, 40f, Color.WHITE)
                    drawOutlinedText(batch, smallFont, "Respawn: 10s", 680f, 40f, Color.WHITE)
                }
            }
            batch.end()
            return
        }
    }

    private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
}
