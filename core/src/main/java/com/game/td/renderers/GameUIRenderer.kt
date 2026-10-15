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

        // Khung nền bảng Cheat
        shapeRenderer.color = Color(0.16f, 0.14f, 0.18f, 0.98f)
        shapeRenderer.rect(pX - 150f, pY - 150f, 300f, 300f)

        // Header
        shapeRenderer.color = Color(0.55f, 0.18f, 0.75f, 1f)
        shapeRenderer.rect(pX - 130f, pY + 98f, 260f, 36f)

        // Nút 1: +1000 GOLD
        shapeRenderer.color = Color(0.85f, 0.65f, 0.12f, 1f)
        shapeRenderer.rect(pX - 120f, pY + 50f, 240f, 36f)

        // Nút 2: 300 DMG ON/OFF
        val dmgBtnColor = if (CheatManager.isHighDamageEnabled) Color(0.20f, 0.65f, 0.25f, 1f) else Color(0.75f, 0.25f, 0.20f, 1f)
        shapeRenderer.color = dmgBtnColor
        shapeRenderer.rect(pX - 120f, pY + 5f, 240f, 36f)

        // Nút 3: KILL ALL MOBS
        shapeRenderer.color = Color(0.80f, 0.30f, 0.15f, 1f)
        shapeRenderer.rect(pX - 120f, pY - 40f, 240f, 36f)

        // Nút 4: WIN LEVEL (3★) - MÀU XANH LỤC RỰC RỠ
        shapeRenderer.color = Color(0.15f, 0.65f, 0.40f, 1f)
        shapeRenderer.rect(pX - 120f, pY - 85f, 240f, 36f)

        // Nút 5: CLOSE
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
                    shapeRenderer.color = if (previewBuildType == type) Color.WHITE else Color.DARK_GRAY
                    shapeRenderer.circle(btn.x, btn.y, 26f)
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
                    if (type == TowerType.ARTILLERY && UpgradeManager.getEffectiveArtilleryLevel() >= 1) cost = 110

                    drawOutlinedText(batch, smallFont, type.title, btn.x - 24f, btn.y + 40f, Color.WHITE)
                    drawOutlinedText(batch, smallFont, "${cost}G", btn.x - 14f, btn.y - 28f, Color.GOLD)
                } else {
                    drawOutlinedText(batch, smallFont, "LOCKED", btn.x - 24f, btn.y + 35f, Color.GRAY)
                }
            }
            batch.end()

            previewBuildType?.let { type ->
                if (allowedTowers.contains(type)) {
                    renderBuildTooltip(shapeRenderer, batch, smallFont, p, type)
                }
            }
        }

        selectedTower?.let { tower ->
            val tPos = tower.getComponent(TransformComponent::class.java).position
            val tComp = tower.getComponent(TowerComponent::class.java)
            val isHigh = tPos.y > 540f

            if (tComp.specialization == TowerSpecialization.SUNRAY_TOWER) {
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)

                if (tComp.sunraySorcerers < 4) {
                    val upgradeBtnX = if (tComp.isSunrayReady && tComp.sunraySorcerers > 0) tPos.x - 55f else tPos.x
                    val upgradeBtnY = tPos.y + 60f
                    shapeRenderer.color = if (previewTowerAction == "UPGRADE_SUNRAY") Color.WHITE else Color.DARK_GRAY
                    shapeRenderer.circle(upgradeBtnX, upgradeBtnY, 26f)
                    shapeRenderer.color = Color(0.2f, 0.75f, 0.35f, 1f)
                    shapeRenderer.circle(upgradeBtnX, upgradeBtnY, 20f)
                }

                if (tComp.isSunrayReady && tComp.sunraySorcerers > 0) {
                    val fireBtnX = if (tComp.sunraySorcerers < 4) tPos.x + 55f else tPos.x
                    val fireBtnY = tPos.y + 60f
                    shapeRenderer.color = if (previewTowerAction == "FIRE_SUNRAY") Color.WHITE else Color.DARK_GRAY
                    shapeRenderer.circle(fireBtnX, fireBtnY, 26f)
                    shapeRenderer.color = Color(1f, 0.85f, 0.15f, 1f)
                    shapeRenderer.circle(fireBtnX, fireBtnY, 20f)
                }
                shapeRenderer.end()

                batch.begin()
                if (tComp.sunraySorcerers < 4) {
                    val upgradeBtnX = if (tComp.isSunrayReady && tComp.sunraySorcerers > 0) tPos.x - 55f else tPos.x
                    val upgradeBtnY = tPos.y + 60f
                    val hireText = if (tComp.sunraySorcerers == 0) "HIRE" else "UPGRADE"
                    drawOutlinedText(batch, smallFont, hireText, upgradeBtnX - 20f, upgradeBtnY + 40f, Color.WHITE)
                    drawOutlinedText(batch, smallFont, "100G", upgradeBtnX - 14f, upgradeBtnY - 28f, Color.GOLD)
                }

                if (tComp.isSunrayReady && tComp.sunraySorcerers > 0) {
                    val fireBtnX = if (tComp.sunraySorcerers < 4) tPos.x + 55f else tPos.x
                    val fireBtnY = tPos.y + 60f
                    drawOutlinedText(batch, smallFont, "FIRE", fireBtnX - 16f, fireBtnY + 40f, Color.WHITE)
                    drawOutlinedText(batch, smallFont, "READY", fireBtnX - 22f, fireBtnY - 28f, Color.YELLOW)
                }

                if (tComp.sunraySorcerers > 0 && !tComp.isSunrayReady) {
                    drawOutlinedText(batch, smallFont, "CHARGING: ${tComp.sunrayChargeTimer.toInt()}s", tPos.x - 42f, tPos.y - 55f, Color.ORANGE)
                }
                batch.end()
                return
            }

            if (tComp.specialization == TowerSpecialization.SYLVAN_ELF_HALL) {
                val bComp = tower.getComponent(BarracksComponent::class.java)
                val currentElves = bComp?.soldiers?.size ?: 0

                if (isSettingRallyPoint) {
                    shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
                    shapeRenderer.color = Color(1f, 0.85f, 0.2f, 0.85f)
                    shapeRenderer.circle(tPos.x, tPos.y, maxRallyRadius)
                    shapeRenderer.end()
                }

                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
                if (tComp.isRuined) {
                    shapeRenderer.color = if (previewTowerAction == "REPAIR") Color.WHITE else Color.DARK_GRAY
                    shapeRenderer.circle(tPos.x, tPos.y - 60f, 26f)
                    shapeRenderer.color = Color(0.2f, 0.75f, 0.3f, 1f)
                    shapeRenderer.circle(tPos.x, tPos.y - 60f, 20f)
                } else {
                    if (currentElves < 4) {
                        shapeRenderer.color = if (previewTowerAction == "HIRE_ELF") Color.WHITE else Color.DARK_GRAY
                        shapeRenderer.circle(tPos.x + 55f, tPos.y, 25f)
                        shapeRenderer.color = Color(0.2f, 0.65f, 0.9f, 1f)
                        shapeRenderer.circle(tPos.x + 55f, tPos.y, 19f)
                    }

                    shapeRenderer.color = Color.CYAN
                    shapeRenderer.circle(tPos.x - 55f, tPos.y, 23f)
                }
                shapeRenderer.end()

                batch.begin()
                if (tComp.isRuined) {
                    drawOutlinedText(batch, smallFont, "REPAIR", tPos.x - 22f, tPos.y - 28f, Color.WHITE)
                    drawOutlinedText(batch, smallFont, "100G", tPos.x - 14f, tPos.y - 90f, Color.GOLD)
                } else {
                    if (currentElves < 4) {
                        drawOutlinedText(batch, smallFont, "HIRE", tPos.x + 38f, tPos.y + 35f, Color.WHITE)
                        drawOutlinedText(batch, smallFont, "100G", tPos.x + 42f, tPos.y - 28f, Color.GOLD)
                    }
                    drawOutlinedText(batch, smallFont, "RALLY", tPos.x - 85f, tPos.y + 8f, Color.WHITE)
                    drawOutlinedText(batch, smallFont, "Elves: $currentElves/4", tPos.x - 30f, tPos.y + 55f, Color.YELLOW)
                }
                batch.end()
                return
            }

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
            if (tComp.level < 3 && canUpgrade) {
                shapeRenderer.color = if (previewTowerAction == "UPGRADE") Color.WHITE else Color.DARK_GRAY
                shapeRenderer.circle(tPos.x, upgradeY, 26f)
                shapeRenderer.color = Color.GREEN
                shapeRenderer.circle(tPos.x, upgradeY, 20f)
            }

            if (canUpgradeToSpec) {
                val (spec1, spec2) = getBranchSpecs(tComp.towerType)

                val isSingleBranchOnly = when (currentLevelNumber) {
                    0 -> false
                    5 -> true
                    6 -> true
                    7 -> tComp.towerType != TowerType.ARCHER
                    8 -> (tComp.towerType == TowerType.MAGE || tComp.towerType == TowerType.ARTILLERY)
                    9 -> (tComp.towerType == TowerType.ARTILLERY)
                    else -> false
                }

                if (isSingleBranchOnly) {
                    shapeRenderer.color = if (previewTowerAction == "SPEC_1") Color.WHITE else Color.DARK_GRAY
                    shapeRenderer.circle(tPos.x, branchY, 26f)
                    shapeRenderer.color = Color.GOLD
                    shapeRenderer.circle(tPos.x, branchY, 20f)
                } else {
                    shapeRenderer.color = if (previewTowerAction == "SPEC_1") Color.WHITE else Color.DARK_GRAY
                    shapeRenderer.circle(tPos.x - 45f, branchY, 24f)
                    shapeRenderer.color = Color.GOLD
                    shapeRenderer.circle(tPos.x - 45f, branchY, 18f)

                    shapeRenderer.color = if (previewTowerAction == "SPEC_2") Color.WHITE else Color.DARK_GRAY
                    shapeRenderer.circle(tPos.x + 45f, branchY, 24f)
                    shapeRenderer.color = Color.ORANGE
                    shapeRenderer.circle(tPos.x + 45f, branchY, 18f)
                }
            }

            if (tComp.level == 4) {
                val spec = tComp.specialization
                val has3Skills = tComp.towerType == TowerType.BARRACKS
                val skillCenterY = if (isHigh) tPos.y - 55f else tPos.y + 55f
                val skillMiddleY = if (isHigh) tPos.y - 75f else tPos.y + 75f

                if (has3Skills) {
                    shapeRenderer.color = Color.DARK_GRAY
                    shapeRenderer.circle(tPos.x - 65f, skillCenterY, 24f)
                    shapeRenderer.color = Color.CYAN
                    shapeRenderer.circle(tPos.x - 65f, skillCenterY, 18f)

                    shapeRenderer.color = Color.DARK_GRAY
                    shapeRenderer.circle(tPos.x, skillMiddleY, 24f)
                    shapeRenderer.color = Color.GOLD
                    shapeRenderer.circle(tPos.x, skillMiddleY, 18f)

                    shapeRenderer.color = Color.DARK_GRAY
                    shapeRenderer.circle(tPos.x + 65f, skillCenterY, 24f)
                    shapeRenderer.color = Color.MAGENTA
                    shapeRenderer.circle(tPos.x + 65f, skillCenterY, 18f)
                } else {
                    shapeRenderer.color = Color.DARK_GRAY
                    shapeRenderer.circle(tPos.x - 55f, skillCenterY, 24f)
                    shapeRenderer.color = Color.CYAN
                    shapeRenderer.circle(tPos.x - 55f, skillCenterY, 18f)

                    shapeRenderer.color = Color.DARK_GRAY
                    shapeRenderer.circle(tPos.x + 55f, skillCenterY, 24f)
                    shapeRenderer.color = Color.MAGENTA
                    shapeRenderer.circle(tPos.x + 55f, skillCenterY, 18f)
                }
            }

            shapeRenderer.color = if (previewTowerAction == "SELL") Color.WHITE else Color.DARK_GRAY
            shapeRenderer.circle(tPos.x, sellY, 26f)
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

                val isSingleBranchOnly = when (currentLevelNumber) {
                    0 -> false
                    5 -> true
                    6 -> true
                    7 -> tComp.towerType != TowerType.ARCHER
                    8 -> (tComp.towerType == TowerType.MAGE || tComp.towerType == TowerType.ARTILLERY)
                    9 -> (tComp.towerType == TowerType.ARTILLERY)
                    else -> false
                }

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
                val costDiscount = if (tComp.towerType == TowerType.ARTILLERY && UpgradeManager.getEffectiveArtilleryLevel() >= 4) 0.75f else 1.0f

                if (has3Skills) {
                    drawOutlinedText(batch, smallFont, "S1:${tComp.skill1Level}/3", tPos.x - 105f, textBaseY, Color.CYAN)
                    if (tComp.skill1Level < 3) {
                        val s1Cost = (spec.skill1Costs[tComp.skill1Level] * costDiscount).toInt()
                        drawOutlinedText(batch, smallFont, "${s1Cost}G", tPos.x - 80f, textBaseY - 45f, Color.GOLD)
                    }

                    val maxSkill2 = if (spec == TowerSpecialization.HOLY_ORDER) 1 else 3
                    drawOutlinedText(batch, smallFont, "S2:${tComp.skill2Level}/$maxSkill2", tPos.x - 20f, textBaseY + 20f, Color.YELLOW)
                    if (tComp.skill2Level < maxSkill2) {
                        val s2Cost = (spec.skill2Costs[tComp.skill2Level] * costDiscount).toInt()
                        drawOutlinedText(batch, smallFont, "${s2Cost}G", tPos.x - 15f, textBaseY - 25f, Color.GOLD)
                    }

                    drawOutlinedText(batch, smallFont, "S3:${tComp.skill3Level}/3", tPos.x + 40f, textBaseY, Color.MAGENTA)
                    if (tComp.skill3Level < 3 && spec.skill3Costs.isNotEmpty()) {
                        val s3Cost = (spec.skill3Costs[tComp.skill3Level] * costDiscount).toInt()
                        drawOutlinedText(batch, smallFont, "${s3Cost}G", tPos.x + 50f, textBaseY - 45f, Color.GOLD)
                    }
                } else {
                    drawOutlinedText(batch, smallFont, "${spec.skill1Name} (${tComp.skill1Level}/3)", tPos.x - 110f, textBaseY, Color.CYAN)
                    if (tComp.skill1Level < 3) {
                        val s1Cost = (spec.skill1Costs[tComp.skill1Level] * costDiscount).toInt()
                        drawOutlinedText(batch, smallFont, "${s1Cost}G", tPos.x - 65f, textBaseY - 44f, Color.GOLD)
                    }

                    drawOutlinedText(batch, smallFont, "${spec.skill2Name} (${tComp.skill2Level}/3)", tPos.x + 25f, textBaseY, Color.MAGENTA)
                    if (tComp.skill2Level < 3) {
                        val s2Cost = (spec.skill2Costs[tComp.skill2Level] * costDiscount).toInt()
                        drawOutlinedText(batch, smallFont, "${s2Cost}G", tPos.x + 45f, textBaseY - 44f, Color.GOLD)
                    }
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
        }
    }

    private fun renderBuildTooltip(
        shapeRenderer: ShapeRenderer,
        batch: SpriteBatch,
        smallFont: BitmapFont,
        pos: Vector2,
        type: TowerType
    ) {
        val isHigh = pos.y > 540f
        val boxX = pos.x + 95f
        val boxY = if (isHigh) pos.y - 120f else pos.y - 45f

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color(0.1f, 0.1f, 0.1f, 0.92f)
        shapeRenderer.rect(boxX, boxY, 220f, 95f)
        shapeRenderer.end()

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color.GOLD
        shapeRenderer.rect(boxX, boxY, 220f, 95f)
        shapeRenderer.end()

        batch.begin()
        when (type) {
            TowerType.ARCHER -> {
                drawOutlinedText(batch, smallFont, "Archer Tower", boxX + 10f, boxY + 80f, Color.WHITE)
                drawOutlinedText(batch, smallFont, "Damage: 4-6 (Physical)", boxX + 10f, boxY + 60f, Color.LIGHT_GRAY)
                drawOutlinedText(batch, smallFont, "Speed: Average | Range: Short", boxX + 10f, boxY + 40f, Color.LIGHT_GRAY)
            }
            TowerType.BARRACKS -> {
                drawOutlinedText(batch, smallFont, "Militia Barracks", boxX + 10f, boxY + 80f, Color.WHITE)
                drawOutlinedText(batch, smallFont, "Soldiers: 3 Militias (50 HP)", boxX + 10f, boxY + 60f, Color.LIGHT_GRAY)
                drawOutlinedText(batch, smallFont, "Role: Block & Attack enemies", boxX + 10f, boxY + 40f, Color.LIGHT_GRAY)
            }
            TowerType.MAGE -> {
                drawOutlinedText(batch, smallFont, "Mage Tower", boxX + 10f, boxY + 80f, Color.WHITE)
                val dmgDesc = if (UpgradeManager.getEffectiveMageLevel() >= 4) "Damage: 10-20 (Magical +15%)" else "Damage: 9-17 (Magical)"
                drawOutlinedText(batch, smallFont, dmgDesc, boxX + 10f, boxY + 60f, Color.LIGHT_GRAY)
                drawOutlinedText(batch, smallFont, "Speed: Slow | Range: Med", boxX + 10f, boxY + 40f, Color.LIGHT_GRAY)
            }
            TowerType.ARTILLERY -> {
                drawOutlinedText(batch, smallFont, "Bombard Artillery", boxX + 10f, boxY + 80f, Color.WHITE)
                drawOutlinedText(batch, smallFont, "Damage: 8-15 (Area AoE)", boxX + 10f, boxY + 60f, Color.LIGHT_GRAY)
                drawOutlinedText(batch, smallFont, "Speed: Very Slow | Range: Short", boxX + 10f, boxY + 40f, Color.LIGHT_GRAY)
            }
            else -> {
                drawOutlinedText(batch, smallFont, "Special Tower", boxX + 10f, boxY + 80f, Color.WHITE)
                drawOutlinedText(batch, smallFont, "Unique shrine support", boxX + 10f, boxY + 60f, Color.LIGHT_GRAY)
                drawOutlinedText(batch, smallFont, "Strategic reinforcement", boxX + 10f, boxY + 40f, Color.LIGHT_GRAY)
            }
        }
        drawOutlinedText(batch, smallFont, "[ Tap again to build ]", boxX + 10f, boxY + 20f, Color.YELLOW)
        batch.end()
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
}
