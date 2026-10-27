package com.game.td.renderers

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Align
import com.game.td.Config
import com.game.td.ecs.components.*
import com.game.td.managers.CheatManager
import com.game.td.managers.EconomyManager
import com.game.td.managers.UpgradeManager
import com.game.td.managers.WaveManager
import com.game.td.screens.ActiveSpell

object GameUIRenderer {

    private val layout = GlyphLayout()

    // BẢNG GIÁ TIỀN KỸ NĂNG CHUẨN WIKI CHO TẤT CẢ 8 NHÁNH THÁP LEVEL 4
    fun getSkillUpgradeCost(spec: TowerSpecialization, skillIndex: Int, currentLevel: Int, towerType: TowerType): Int {
        val baseCost = when (spec) {
            TowerSpecialization.RANGERS_HIDEOUT -> when (skillIndex) {
                1 -> 250 // Poison Arrows: 250 / 250 / 250
                2 -> if (currentLevel == 0) 300 else 150 // Wrath of the Forest: 300 / 150 / 150
                else -> 0
            }
            TowerSpecialization.MUSKETEER_GARRISON -> when (skillIndex) {
                1 -> 250 // Sniper Shot: 250 / 250 / 250
                2 -> 300 // Shrapnel Shot: 300 / 300 / 300
                else -> 0
            }
            TowerSpecialization.HOLY_ORDER -> when (skillIndex) {
                1 -> 150 // Healing Light: 150 / 150 / 150
                2 -> 250 // Shield of Valor: 250 (1 cấp duy nhất)
                3 -> if (currentLevel == 0) 220 else 150 // Holy Strike: 220 / 150 / 150
                else -> 0
            }
            TowerSpecialization.BARBARIAN_MEAD_HALL -> when (skillIndex) {
                1 -> if (currentLevel == 0) 200 else 100 // Throwing Axes: 200 / 100 / 100
                2 -> if (currentLevel == 0) 150 else 100 // Whirlwind Attack: 150 / 100 / 100
                3 -> if (currentLevel == 0) 300 else 100 // More Axes: 300 / 100 / 100
                else -> 0
            }
            else -> when (skillIndex) {
                1 -> spec.skill1Costs.getOrElse(currentLevel) { 250 }
                2 -> spec.skill2Costs.getOrElse(currentLevel) { 250 }
                else -> spec.skill3Costs.getOrElse(currentLevel) { 250 }
            }
        }
        val discount = if (towerType == TowerType.ARTILLERY && UpgradeManager.getEffectiveArtilleryLevel() >= 4) 0.75f else 1.0f
        return (baseCost * discount).toInt()
    }

    // =========================================================================
    // VÙNG 1: CÔNG CỤ VẼ CHỮ & BÓNG ĐỔ ĐANH NÉT (TEXT HELPERS)
    // =========================================================================
    fun drawShadowedText(batch: SpriteBatch, font: BitmapFont, text: String, x: Float, y: Float, textColor: Color) {
        val rx = Math.round(x).toFloat()
        val ry = Math.round(y).toFloat()
        font.color = Color(0f, 0f, 0f, 0.88f)
        font.draw(batch, text, rx + 1f, ry - 1f)
        font.color = textColor
        font.draw(batch, text, rx, ry)
    }

    fun drawOutlinedText(batch: SpriteBatch, font: BitmapFont, text: String, x: Float, y: Float, textColor: Color) {
        val rx = Math.round(x).toFloat()
        val ry = Math.round(y).toFloat()
        font.color = Color(0f, 0f, 0f, 0.90f)
        font.draw(batch, text, rx - 1f, ry)
        font.draw(batch, text, rx + 1f, ry)
        font.draw(batch, text, rx, ry - 1f)
        font.draw(batch, text, rx, ry + 1f)
        font.color = textColor
        font.draw(batch, text, rx, ry)
    }

    fun drawBadgeCenteredText(batch: SpriteBatch, font: BitmapFont, text: String, boxX: Float, boxY: Float, boxW: Float, boxH: Float, textColor: Color) {
        layout.setText(font, text)
        val tx = Math.round(boxX + (boxW - layout.width) / 2f).toFloat()
        val ty = Math.round(boxY + (boxH + layout.height) / 2f - 1f).toFloat()
        font.color = Color(0f, 0f, 0f, 0.88f)
        font.draw(batch, text, tx + 1f, ty - 1f)
        font.color = textColor
        font.draw(batch, text, tx, ty)
    }

    // =========================================================================
    // VÙNG 2: HUD & CÁC BẢNG ĐIỀU KHIỂN (HUD & PAUSE & CHEAT)
    // =========================================================================
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
        drawOutlinedText(batch, font, "<", backBtnX - 5f, backBtnY + 8f, Color.WHITE)
        drawOutlinedText(batch, font, "${EconomyManager.lives}", hudX + 32f, hudY + 26f, Color.WHITE)
        drawOutlinedText(batch, font, "${EconomyManager.gold}", hudX + 98f, hudY + 26f, Color.GOLD)

        val waveText = if (waveManager.currentWaveDisplay <= 0) "Wave 0/${waveManager.totalWaves}" else "Wave ${waveManager.currentWaveDisplay}/${waveManager.totalWaves}"
        drawOutlinedText(batch, font, waveText, rightX + 30f, hudY + 26f, Color.WHITE)

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

    // =========================================================================
    // VÙNG 3: CHIẾN THẮNG, KỸ NĂNG & NÚT GỌI WAVE SỚM
    // =========================================================================
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
            drawStar(shapeRenderer, cX + 92f, cY - 49f, 9.5f, true)
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
        drawOutlinedText(batch, smallFont, victorySub, cX - 130f, cY - 20f, Color(0.95f, 0.95f, 0.90f, 1f))

        val starsText = if (isChallengeMode) "EXTRA STAR EARNED: +1" else "STARS EARNED: $earnedStars / 3"
        drawOutlinedText(batch, smallFont, starsText, if (isChallengeMode) cX - 95f else cX - 75f, cY - 45f, Color.GOLD)

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
        val fireCenter = Vector2(48f, 44f)
        val reinfCenter = Vector2(112f, 44f)
        val isFireReady = rainOfFireCooldown <= 0f
        val isReinfReady = reinforcementCooldown <= 0f
        val isFireSelected = (selectedSpell == ActiveSpell.RAIN_OF_FIRE)
        val isReinfSelected = (selectedSpell == ActiveSpell.REINFORCEMENTS)

        com.badlogic.gdx.Gdx.gl.glEnable(com.badlogic.gdx.graphics.GL20.GL_BLEND)
        com.badlogic.gdx.Gdx.gl.glBlendFunc(
            com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA,
            com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA
        )

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)

        // 1. VẼ BỆ ĐỠ KIM LOẠI ĐÍNH ĐINH TÁN (SPELL DOCK)
        val dockX = 10f
        val dockY = 4f
        val dockW = 140f
        val dockH = 50f

        // Bóng đổ và viền thép ngoài của bệ đỡ
        shapeRenderer.color = Color(0.08f, 0.08f, 0.10f, 0.85f)
        shapeRenderer.rect(dockX - 3f, dockY - 2f, dockW + 6f, dockH + 5f)
        shapeRenderer.color = Color(0.46f, 0.48f, 0.50f, 1f)
        shapeRenderer.rect(dockX, dockY, dockW, dockH)
        shapeRenderer.color = Color(0.18f, 0.17f, 0.16f, 1f)
        shapeRenderer.rect(dockX + 3f, dockY + 3f, dockW - 6f, dockH - 6f)
        shapeRenderer.color = Color(0.28f, 0.25f, 0.22f, 1f)
        shapeRenderer.rect(dockX + 6f, dockY + 6f, dockW - 12f, dockH - 12f)

        // 4 miếng ốp sắt vát góc đính đinh tán ở 4 góc bệ đỡ
        val dockCorners = listOf(
            Vector2(dockX - 2f, dockY + dockH - 12f),
            Vector2(dockX + dockW - 12f, dockY + dockH - 12f),
            Vector2(dockX - 2f, dockY - 2f),
            Vector2(dockX + dockW - 12f, dockY - 2f)
        )
        for (c in dockCorners) {
            shapeRenderer.color = Color(0.65f, 0.68f, 0.72f, 1f)
            shapeRenderer.rect(c.x, c.y, 14f, 14f)
            shapeRenderer.color = Color(0.22f, 0.22f, 0.25f, 1f)
            shapeRenderer.circle(c.x + 4f, c.y + 4f, 1.5f)
            shapeRenderer.circle(c.x + 10f, c.y + 10f, 1.5f)
        }

        // Mũi giáo bạc trang trí ở giữa đáy 2 thẻ
        shapeRenderer.color = Color(0.72f, 0.75f, 0.80f, 1f)
        shapeRenderer.triangle(74f, dockY + 4f, 86f, dockY + 4f, 80f, dockY + 13f)

        // 2. VẼ THẺ SPELL 1: RAIN OF FIRE (MƯA THIÊN THẠCH)
        drawSpellCard(
            sr = shapeRenderer,
            cx = fireCenter.x,
            cy = fireCenter.y,
            isReady = isFireReady,
            isSelected = isFireSelected,
            isFireSpell = true
        )

        // 3. VẼ THẺ SPELL 2: REINFORCEMENTS (GỌI LÍNH TIẾP VIỆN)
        drawSpellCard(
            sr = shapeRenderer,
            cx = reinfCenter.x,
            cy = reinfCenter.y,
            isReady = isReinfReady,
            isSelected = isReinfSelected,
            isFireSpell = false
        )

        shapeRenderer.end()

        // 4. VẼ SỐ GIÂY HỒI CHIÊU & DÒNG HƯỚNG DẪN KHI ĐANG CHỌN SPELL
        batch.begin()
        if (!isFireReady) {
            drawBadgeCenteredText(
                batch, smallFont, "${rainOfFireCooldown.toInt()}s",
                fireCenter.x - 20f, fireCenter.y - 8f, 40f, 20f, Color.WHITE
            )
        }

        if (!isReinfReady) {
            drawBadgeCenteredText(
                batch, smallFont, "${reinforcementCooldown.toInt()}s",
                reinfCenter.x - 20f, reinfCenter.y - 8f, 40f, 20f, Color.WHITE
            )
        }

        if (selectedSpell == ActiveSpell.RAIN_OF_FIRE) {
            drawOutlinedText(
                batch, font,
                "TAP ROAD TO DROP METEORS! (TAP AGAIN TO CANCEL)",
                Config.WORLD_WIDTH / 2f - 240f, 60f, Color.ORANGE
            )
        } else if (selectedSpell == ActiveSpell.REINFORCEMENTS) {
            drawOutlinedText(
                batch, font,
                "TAP ROAD TO SUMMON REINFORCEMENTS! (TAP AGAIN TO CANCEL)",
                Config.WORLD_WIDTH / 2f - 260f, 60f, Color.SKY
            )
        }
        batch.end()
    }

    private fun drawSpellCard(
        sr: ShapeRenderer,
        cx: Float,
        cy: Float,
        isReady: Boolean,
        isSelected: Boolean,
        isFireSpell: Boolean
    ) {
        val cardW = 44f
        val cardH = 54f
        val left = cx - cardW / 2f
        val bottom = cy - cardH / 2f

        // Hào quang vàng rực bên ngoài khi người chơi đang bấm chọn kỹ năng để nhắm
        if (isSelected) {
            sr.color = Color(1f, 0.92f, 0.20f, 0.55f)
            sr.rect(left - 5f, bottom - 5f, cardW + 10f, cardH + 10f)
            sr.circle(cx, bottom - 1f, 12f)
        } else if (isReady) {
            // Hào quang xanh băng dịu tỏa ra khi kỹ năng đã hồi xong sẵn sàng chiến đấu
            sr.color = Color(0.25f, 0.90f, 1f, 0.28f)
            sr.rect(left - 3f, bottom - 3f, cardW + 6f, cardH + 6f)
        }

        // Khung viền kim loại của thẻ Spell
        sr.color = Color(0.14f, 0.13f, 0.12f, 1f)
        sr.rect(left - 1.5f, bottom - 1.5f, cardW + 3f, cardH + 3f)

        sr.color = if (isReady) Color(0.48f, 0.45f, 0.36f, 1f) else Color(0.30f, 0.30f, 0.32f, 1f)
        sr.rect(left, bottom, cardW, cardH)

        // Vòm đầu thẻ và bệ đỡ ngọc dưới đáy thẻ
        sr.color = if (isReady) Color(0.62f, 0.58f, 0.48f, 1f) else Color(0.38f, 0.38f, 0.40f, 1f)
        sr.rect(cx - 16f, bottom + cardH - 3f, 32f, 5f)
        sr.circle(cx, bottom + 3f, 9f)

        // Vùng tranh bên trong thẻ (32x40)
        val artX = cx - 16f
        val artY = cy - 17f
        val artW = 32f
        val artH = 38f

        if (isFireSpell) {
            // --- HÌNH ẢNH SPELL 1: MƯA THIÊN THẠCH (RAIN OF FIRE) ---
            // Nền lửa chuyển sắc cam - đỏ - vàng
            sr.color = Color(0.78f, 0.18f, 0.04f, 1f)
            sr.rect(artX, artY, artW, artH)
            sr.color = Color(0.94f, 0.38f, 0.05f, 1f)
            sr.rect(artX, artY, artW, artH * 0.72f)
            sr.color = Color(0.98f, 0.56f, 0.08f, 1f)
            sr.rect(artX, artY, artW, artH * 0.38f)

            // Các vệt sáng lửa lao chéo từ trên phải xuống dưới trái
            sr.color = Color(1f, 0.85f, 0.25f, 0.85f)
            sr.rectLine(cx + 14f, cy + 18f, cx - 2f, cy - 2f, 3.2f)
            sr.rectLine(cx + 6f, cy + 19f, cx - 12f, cy - 3f, 1.8f)
            sr.rectLine(cx + 15f, cy + 6f, cx + 2f, cy - 12f, 1.8f)

            // Đuôi lửa rực sáng của viên thiên thạch chính
            sr.color = Color(1f, 0.72f, 0.12f, 1f)
            sr.triangle(cx - 9f, cy - 2f, cx + 6f, cy - 11f, cx + 14f, cy + 16f)
            sr.color = Color(1f, 0.94f, 0.38f, 1f)
            sr.triangle(cx - 6f, cy - 2f, cx + 4f, cy - 8f, cx + 11f, cy + 12f)
            sr.circle(cx - 1f, cy - 3f, 9.5f)

            // Lõi đá thiên thạch nâu đen gồ ghề ở giữa
            sr.color = Color(0.28f, 0.16f, 0.08f, 1f)
            sr.circle(cx - 2f, cy - 4f, 7.2f)
            sr.circle(cx + 2f, cy - 2f, 5.2f)
            sr.circle(cx - 5f, cy - 1f, 4.5f)
            sr.color = Color(0.18f, 0.10f, 0.05f, 1f)
            sr.circle(cx - 3f, cy - 5f, 4.5f)

            // Các mảnh thiên thạch nhỏ bay kèm xung quanh
            sr.color = Color(0.25f, 0.14f, 0.06f, 1f)
            sr.circle(cx - 10f, cy + 11f, 2.2f)
            sr.circle(cx + 10f, cy + 2f, 2.0f)
            sr.circle(cx + 9f, cy - 12f, 1.6f)
        } else {
            // --- HÌNH ẢNH SPELL 2: GỌI LÍNH (REINFORCEMENTS - SONG KIẾM CHÉO) ---
            // Nền xanh da trời tỏa sáng ở tâm
            sr.color = Color(0.16f, 0.58f, 0.82f, 1f)
            sr.rect(artX, artY, artW, artH)
            sr.color = Color(0.32f, 0.78f, 0.96f, 1f)
            sr.circle(cx, cy + 2f, 15.5f)
            sr.color = Color(0.75f, 0.96f, 1f, 1f)
            sr.circle(cx, cy + 2f, 11.5f)

            // Viền đen tạo khối cho 2 thanh kiếm bắt chéo
            sr.color = Color(0.12f, 0.16f, 0.20f, 1f)
            sr.rectLine(cx - 12f, cy - 12f, cx + 11f, cy + 14f, 7.5f)
            sr.rectLine(cx + 12f, cy - 12f, cx - 11f, cy + 14f, 7.5f)

            // Lưỡi kiếm thứ 1 (Trái dưới -> Phải trên)
            sr.color = Color(0.88f, 0.92f, 0.96f, 1f)
            sr.rectLine(cx - 4f, cy - 3f, cx + 10f, cy + 13f, 5.2f)
            sr.triangle(cx + 6.5f, cy + 14.5f, cx + 11.5f, cy + 9.5f, cx + 13.5f, cy + 16.5f)
            sr.color = Color(0.62f, 0.68f, 0.75f, 1f)
            sr.rectLine(cx - 4f, cy - 3f, cx + 9f, cy + 12f, 1.6f)

            // Lưỡi kiếm thứ 2 (Phải dưới -> Trái trên)
            sr.color = Color(0.94f, 0.97f, 1f, 1f)
            sr.rectLine(cx + 4f, cy - 3f, cx - 10f, cy + 13f, 5.2f)
            sr.triangle(cx - 6.5f, cy + 14.5f, cx - 11.5f, cy + 9.5f, cx - 13.5f, cy + 16.5f)
            sr.color = Color(0.65f, 0.72f, 0.78f, 1f)
            sr.rectLine(cx + 4f, cy - 3f, cx - 9f, cy + 12f, 1.6f)

            // Tay cầm bọc da nâu & chuôi kiếm vàng của cả 2 thanh kiếm
            sr.color = Color(0.48f, 0.26f, 0.10f, 1f)
            sr.rectLine(cx - 11f, cy - 11f, cx - 5f, cy - 4f, 4.2f)
            sr.rectLine(cx + 11f, cy - 11f, cx + 5f, cy - 4f, 4.2f)

            // Thanh chắn chữ thập (Crossguard) và núm chuôi kiếm bằng vàng
            sr.color = Color(0.96f, 0.74f, 0.14f, 1f)
            sr.rectLine(cx - 9.5f, cy - 1.5f, cx - 1.5f, cy - 8.5f, 3.2f)
            sr.rectLine(cx + 9.5f, cy - 1.5f, cx + 1.5f, cy - 8.5f, 3.2f)
            sr.circle(cx - 11f, cy - 11f, 2.8f)
            sr.circle(cx + 11f, cy - 11f, 2.8f)
        }

        // Nếu đang hồi chiêu -> Phủ lớp tối mờ lên mặt tranh
        if (!isReady) {
            sr.color = Color(0.04f, 0.04f, 0.06f, 0.68f)
            sr.rect(artX, artY, artW, artH)
        }

        // 4. HỆ THỐNG ĐÈN NĂNG LƯỢNG XANH BĂNG (SÁNG RỰC KHI HỒI XONG NHƯ HÌNH 1)
        val gemOuterColor = if (isReady) Color(0.20f, 0.88f, 1f, 1f) else Color(0.20f, 0.22f, 0.24f, 1f)
        val gemInnerColor = if (isReady) Color.WHITE else Color(0.30f, 0.32f, 0.35f, 1f)

        // A. Thanh năng lượng trên đỉnh thẻ
        if (isReady) {
            sr.color = Color(0.20f, 0.90f, 1f, 0.45f)
            sr.rect(cx - 13f, bottom + cardH - 4f, 26f, 7f)
        }
        sr.color = gemOuterColor
        sr.rect(cx - 10f, bottom + cardH - 2.5f, 20f, 4f)
        sr.color = gemInnerColor
        sr.rect(cx - 7f, bottom + cardH - 1.5f, 14f, 2f)

        // B. 4 nút đèn năng lượng ở 2 cạnh bên của khung thẻ
        val sideNodeYOffsets = listOf(cy + 8f, cy - 7f)
        val sideNodeXOffsets = listOf(left + 2f, left + cardW - 6f)
        for (nx in sideNodeXOffsets) {
            for (ny in sideNodeYOffsets) {
                if (isReady) {
                    sr.color = Color(0.20f, 0.90f, 1f, 0.45f)
                    sr.rect(nx - 2f, ny - 2f, 8f, 10f)
                }
                sr.color = gemOuterColor
                sr.rect(nx, ny, 4f, 6f)
                sr.color = gemInnerColor
                sr.rect(nx + 1f, ny + 1f, 2f, 4f)
            }
        }

        // C. Viên ngọc năng lượng tròn ở chính giữa đáy thẻ
        val orbY = bottom + 4f
        if (isReady) {
            sr.color = Color(0.20f, 0.92f, 1f, 0.50f)
            sr.circle(cx, orbY, 9.5f)
        }
        sr.color = gemOuterColor
        sr.circle(cx, orbY, 6.2f)
        sr.color = gemInnerColor
        sr.circle(cx, orbY, 3.8f)
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
        if (!canShow || waveManager.currentWaveDisplay > waveManager.totalWaves) return

        val activePaths = waveManager.getActiveSpawningPathsForNextWave()
        val spawnPoints = mutableListOf<Vector2>()

        val pathsToCheck = if (activePaths.isNotEmpty()) activePaths else listOf(0)
        for (pIdx in pathsToCheck) {
            if (pIdx in allPaths.indices && allPaths[pIdx].isNotEmpty()) {
                val startP = allPaths[pIdx][0]
                val safeX = startP.x.coerceIn(80f, Config.WORLD_WIDTH - 80f)
                val safeY = startP.y.coerceIn(85f, Config.WORLD_HEIGHT - 85f)
                val pt = Vector2(safeX, safeY)
                if (spawnPoints.none { it.dst(pt) < 30f }) {
                    spawnPoints.add(pt)
                }
            }
        }

        if (spawnPoints.isEmpty() && allPaths.isNotEmpty() && allPaths[0].isNotEmpty()) {
            val p0 = allPaths[0][0]
            spawnPoints.add(Vector2(p0.x.coerceIn(80f, Config.WORLD_WIDTH - 80f), p0.y.coerceIn(85f, Config.WORLD_HEIGHT - 85f)))
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
                drawOutlinedText(batch, smallFont, "START", p.x - 22f, p.y - 32f, Color.YELLOW)
            } else {
                val bonusGold = (waveManager.waveCountdownTimer * 1.5f).toInt().coerceAtLeast(5)
                drawOutlinedText(batch, smallFont, "+${bonusGold}G", p.x - 16f, p.y - 32f, Color.YELLOW)
                drawOutlinedText(batch, smallFont, "${waveManager.waveCountdownTimer.toInt()}s", p.x - 12f, p.y + 44f, Color.WHITE)
            }
        }
        batch.end()
    }

    // =========================================================================
    // HÀM VẼ KHUNG SẮT TÁN ĐINH & KHUNG NÚT KỸ NĂNG LEVEL 4 CÓ 3 Ổ NGỌC CẤP ĐỘ
    // =========================================================================
    private fun drawIronActionBox(
        shapeRenderer: ShapeRenderer,
        btnX: Float,
        btnY: Float,
        isSelected: Boolean,
        hasCostBox: Boolean = true
    ) {
        if (isSelected) {
            shapeRenderer.color = Color(1f, 0.90f, 0.20f, 0.38f)
            shapeRenderer.rect(btnX - 26f, btnY - 19f, 52f, 46f)
            if (hasCostBox) {
                shapeRenderer.rect(btnX - 23f, btnY - 35f, 46f, 20f)
                shapeRenderer.circle(btnX - 18f, btnY - 30f, 5f)
                shapeRenderer.circle(btnX + 18f, btnY - 30f, 5f)
            }
            shapeRenderer.circle(btnX - 22f, btnY + 24f, 5.5f)
            shapeRenderer.circle(btnX + 22f, btnY + 24f, 5.5f)

            shapeRenderer.color = Color(1f, 0.96f, 0.35f, 0.75f)
            shapeRenderer.rect(btnX - 24f, btnY - 18.5f, 48f, 45f)
            if (hasCostBox) {
                shapeRenderer.rect(btnX - 21.5f, btnY - 33.5f, 43f, 19f)
            }
        }

        shapeRenderer.color = if (isSelected) Color(0.35f, 0.32f, 0.24f, 1f) else Color(0.24f, 0.24f, 0.26f, 1f)
        shapeRenderer.rect(btnX - 22f, btnY - 18f, 44f, 44f)
        shapeRenderer.color = Color(0.15f, 0.13f, 0.11f, 0.98f)
        shapeRenderer.rect(btnX - 18f, btnY - 14f, 36f, 36f)

        // 4 góc đinh tán kim loại
        shapeRenderer.color = Color(0.68f, 0.72f, 0.76f, 1f)
        shapeRenderer.rect(btnX - 22f, btnY + 20f, 7f, 6f)
        shapeRenderer.rect(btnX + 15f, btnY + 20f, 7f, 6f)
        shapeRenderer.rect(btnX - 22f, btnY - 18f, 7f, 6f)
        shapeRenderer.rect(btnX + 15f, btnY - 18f, 7f, 6f)

        // 2 chốt sắt nhỏ ở cạnh bên khung
        shapeRenderer.color = Color(0.55f, 0.58f, 0.62f, 1f)
        shapeRenderer.rect(btnX - 22f, btnY + 1f, 3.5f, 6f)
        shapeRenderer.rect(btnX + 18.5f, btnY + 1f, 3.5f, 6f)

        if (hasCostBox) {
            val costBoxW = 40f
            val costBoxH = 18f
            val costBoxY = btnY - 32f
            shapeRenderer.color = Color(0.10f, 0.09f, 0.08f, 0.98f)
            shapeRenderer.rect(btnX - costBoxW / 2f, costBoxY, costBoxW, costBoxH)

            // 2 móc khóa bạc nối hộp giá tiền vào khung nút
            shapeRenderer.color = Color(0.72f, 0.75f, 0.80f, 1f)
            shapeRenderer.rect(btnX - 11f, costBoxY + costBoxH - 2f, 6f, 3.5f)
            shapeRenderer.rect(btnX + 5f, costBoxY + costBoxH - 2f, 6f, 3.5f)
        }
    }

    // KHUNG TRÒN KỸ NĂNG LEVEL 4 CÓ 1 HOẶC 3 Ổ NGỌC CẤP ĐỘ & HỘP GIÁ TIỀN
    private fun drawSkillActionCircle(
        shapeRenderer: ShapeRenderer,
        btnX: Float,
        btnY: Float,
        currentLevel: Int,
        maxLevel: Int,
        isSelected: Boolean
    ) {
        val cy = btnY + 4f
        val hasCostBox = currentLevel < maxLevel

        if (isSelected) {
            shapeRenderer.color = Color(1f, 0.90f, 0.20f, 0.38f)
            shapeRenderer.circle(btnX, cy, 27.5f)
            if (hasCostBox) {
                shapeRenderer.rect(btnX - 23f, btnY - 35f, 46f, 20f)
            }
            shapeRenderer.color = Color(1f, 0.96f, 0.35f, 0.75f)
            shapeRenderer.circle(btnX, cy, 25f)
            if (hasCostBox) {
                shapeRenderer.rect(btnX - 21.5f, btnY - 33.5f, 43f, 19f)
            }
        }

        shapeRenderer.color = Color(0.16f, 0.16f, 0.18f, 1f)
        shapeRenderer.circle(btnX, cy, 23.5f)
        shapeRenderer.color = if (isSelected) Color(0.45f, 0.42f, 0.32f, 1f) else Color(0.32f, 0.33f, 0.36f, 1f)
        shapeRenderer.circle(btnX, cy, 22f)

        shapeRenderer.color = Color(0.58f, 0.62f, 0.66f, 1f)
        shapeRenderer.rect(btnX - 23f, cy - 2f, 4f, 4.5f)
        shapeRenderer.rect(btnX - 18f, cy - 16f, 4f, 4f)

        shapeRenderer.color = Color(0.10f, 0.10f, 0.12f, 1f)
        shapeRenderer.circle(btnX, cy, 18f)

        val socketAngles = if (maxLevel == 1) listOf(30.0) else listOf(64.0, 30.0, -4.0)
        for (i in socketAngles.indices) {
            val rad = Math.toRadians(socketAngles[i])
            val sx = btnX + (20.5f * Math.cos(rad)).toFloat()
            val sy = cy + (20.5f * Math.sin(rad)).toFloat()

            shapeRenderer.color = Color(0.12f, 0.12f, 0.14f, 1f)
            shapeRenderer.circle(sx, sy, 5.2f)
            shapeRenderer.color = Color(0.52f, 0.55f, 0.60f, 1f)
            shapeRenderer.circle(sx, sy, 4.2f)

            if (i < currentLevel) {
                shapeRenderer.color = Color(0.98f, 0.78f, 0.12f, 1f)
                shapeRenderer.circle(sx, sy, 3.0f)
                shapeRenderer.color = Color(1f, 0.96f, 0.55f, 1f)
                shapeRenderer.circle(sx - 0.6f, sy + 0.6f, 1.4f)
            } else {
                shapeRenderer.color = Color(0.14f, 0.14f, 0.16f, 1f)
                shapeRenderer.circle(sx, sy, 2.8f)
            }
        }

        if (hasCostBox) {
            val costBoxW = 40f
            val costBoxH = 18f
            val costBoxY = btnY - 32f
            shapeRenderer.color = Color(0.10f, 0.09f, 0.08f, 0.98f)
            shapeRenderer.rect(btnX - costBoxW / 2f, costBoxY, costBoxW, costBoxH)

            shapeRenderer.color = Color(0.72f, 0.75f, 0.80f, 1f)
            shapeRenderer.rect(btnX - 11f, costBoxY + costBoxH - 2f, 6f, 3.5f)
            shapeRenderer.rect(btnX + 5f, costBoxY + costBoxH - 2f, 6f, 3.5f)
        }
    }

    // =========================================================================
    // VẼ BIỂU TƯỢNG CHI TIẾT CHO KỸ NĂNG CỦA TẤT CẢ 8 NHÁNH THÁP CẤP 4
    // =========================================================================
    private fun drawTowerSkillIcon(
        shapeRenderer: ShapeRenderer,
        spec: TowerSpecialization,
        skillIndex: Int,
        cx: Float,
        cy: Float
    ) {
        when (spec) {
            // 1. RANGERS HIDEOUT (Poison Arrows & Wrath of the Forest)
            TowerSpecialization.RANGERS_HIDEOUT -> {
                if (skillIndex == 1) {
                    shapeRenderer.color = Color(0.10f, 0.36f, 0.08f, 1f)
                    shapeRenderer.circle(cx, cy, 16f)
                    shapeRenderer.color = Color(0.22f, 0.68f, 0.14f, 1f)
                    shapeRenderer.circle(cx - 1f, cy + 1f, 12.5f)

                    shapeRenderer.color = Color(0.55f, 0.34f, 0.14f, 1f)
                    shapeRenderer.rectLine(cx + 10f, cy - 9f, cx - 4f, cy + 5f, 4f)
                    shapeRenderer.color = Color(0.78f, 0.54f, 0.26f, 1f)
                    shapeRenderer.rectLine(cx + 9f, cy - 8f, cx - 4f, cy + 5f, 2f)

                    shapeRenderer.color = Color(0.32f, 0.96f, 0.15f, 1f)
                    shapeRenderer.triangle(cx - 9f, cy + 9f, cx - 7f, cy - 2f, cx + 3f, cy + 6f)
                    shapeRenderer.rectLine(cx - 8f, cy + 7f, cx - 8f, cy - 6f, 3.2f)
                    shapeRenderer.circle(cx - 8f, cy - 7f, 2.4f)
                    shapeRenderer.rectLine(cx - 3.5f, cy + 4f, cx - 3.5f, cy - 9f, 3.0f)
                    shapeRenderer.circle(cx - 3.5f, cy - 9.5f, 2.2f)
                    shapeRenderer.rectLine(cx + 1f, cy + 2f, cx + 1f, cy - 5f, 2.5f)
                    shapeRenderer.circle(cx + 1f, cy - 5.5f, 1.8f)

                    shapeRenderer.color = Color(0.82f, 1f, 0.55f, 1f)
                    shapeRenderer.triangle(cx - 7.5f, cy + 7.5f, cx - 6f, cy + 1f, cx + 0.5f, cy + 5f)
                    shapeRenderer.circle(cx - 8f, cy - 6.5f, 1.1f)
                    shapeRenderer.circle(cx - 3.5f, cy - 9f, 1.0f)
                } else {
                    shapeRenderer.color = Color(0.10f, 0.28f, 0.08f, 1f)
                    shapeRenderer.circle(cx, cy, 16f)

                    shapeRenderer.color = Color(0.30f, 0.92f, 0.18f, 0.85f)
                    shapeRenderer.circle(cx - 5f, cy + 5f, 6.5f)
                    shapeRenderer.circle(cx + 2f, cy - 3f, 8.5f)
                    shapeRenderer.circle(cx - 5f, cy - 5f, 7f)

                    shapeRenderer.color = Color(0.48f, 0.30f, 0.14f, 1f)
                    shapeRenderer.rectLine(cx - 12f, cy + 3f, cx - 5f, cy + 7f, 4.2f)
                    shapeRenderer.rectLine(cx - 5f, cy + 7f, cx - 1f, cy + 4f, 3.2f)

                    shapeRenderer.color = Color(0.46f, 0.28f, 0.12f, 1f)
                    shapeRenderer.rectLine(cx - 12f, cy - 8f, cx - 3f, cy - 2f, 5.8f)
                    shapeRenderer.rectLine(cx - 3f, cy - 2f, cx + 6f, cy - 3f, 5.5f)
                    shapeRenderer.rectLine(cx + 6f, cy - 3f, cx + 11f, cy - 9f, 4.5f)

                    shapeRenderer.color = Color(0.72f, 0.52f, 0.28f, 1f)
                    shapeRenderer.rectLine(cx - 10f, cy - 7f, cx - 3f, cy - 2f, 2.8f)
                    shapeRenderer.rectLine(cx - 3f, cy - 2f, cx + 5f, cy - 3f, 2.6f)

                    shapeRenderer.color = Color(0.88f, 0.76f, 0.52f, 1f)
                    shapeRenderer.triangle(cx - 8f, cy - 3f, cx - 5f, cy - 1f, cx - 9f, cy + 2f)
                    shapeRenderer.triangle(cx - 2f, cy + 0.5f, cx + 1f, cy, cx - 0.5f, cy + 5f)
                    shapeRenderer.triangle(cx + 4f, cy - 1f, cx + 7f, cy - 3f, cx + 7.5f, cy + 3f)
                    shapeRenderer.triangle(cx - 4f, cy - 5f, cx - 1f, cy - 5f, cx - 2.5f, cy - 10f)
                }
            }

            // 2. MUSKETEER GARRISON (Sniper Shot & Shrapnel Shot)
            TowerSpecialization.MUSKETEER_GARRISON -> {
                if (skillIndex == 1) {
                    shapeRenderer.color = Color(0.26f, 0.08f, 0.04f, 1f)
                    shapeRenderer.circle(cx, cy, 16f)

                    shapeRenderer.color = Color(0.90f, 0.28f, 0.06f, 1f)
                    shapeRenderer.circle(cx, cy, 13.5f)
                    shapeRenderer.color = Color(0.24f, 0.07f, 0.03f, 1f)
                    shapeRenderer.circle(cx, cy, 10.8f)

                    shapeRenderer.color = Color(0.95f, 0.35f, 0.08f, 1f)
                    shapeRenderer.rectLine(cx - 15.5f, cy, cx - 8f, cy, 2.4f)
                    shapeRenderer.rectLine(cx + 8f, cy, cx + 15.5f, cy, 2.4f)
                    shapeRenderer.rectLine(cx, cy - 15.5f, cx, cy - 8f, 2.4f)
                    shapeRenderer.rectLine(cx, cy + 8f, cx, cy + 15.5f, 2.4f)

                    shapeRenderer.color = Color(0.96f, 0.58f, 0.12f, 1f)
                    shapeRenderer.circle(cx, cy + 1.5f, 5.8f)
                    shapeRenderer.rect(cx - 3.5f, cy - 5.5f, 7f, 4.5f)

                    shapeRenderer.color = Color(0.18f, 0.05f, 0.02f, 1f)
                    shapeRenderer.circle(cx - 2.2f, cy + 1.5f, 1.6f)
                    shapeRenderer.circle(cx + 2.2f, cy + 1.5f, 1.6f)
                    shapeRenderer.rect(cx - 2f, cy - 5.5f, 1.2f, 2.2f)
                    shapeRenderer.rect(cx + 0.8f, cy - 5.5f, 1.2f, 2.2f)
                } else {
                    shapeRenderer.color = Color(0.16f, 0.13f, 0.10f, 1f)
                    shapeRenderer.circle(cx, cy, 16f)

                    shapeRenderer.color = Color(0.95f, 0.42f, 0.06f, 1f)
                    shapeRenderer.circle(cx, cy, 9.5f)
                    shapeRenderer.triangle(cx - 13f, cy + 7f, cx, cy + 4f, cx - 4f, cy - 3f)
                    shapeRenderer.triangle(cx + 13f, cy + 6f, cx, cy + 4f, cx + 4f, cy - 3f)
                    shapeRenderer.triangle(cx, cy - 14f, cx - 5f, cy, cx + 5f, cy)
                    shapeRenderer.triangle(cx - 7f, cy + 13f, cx - 3f, cy, cx + 4f, cy + 2f)

                    shapeRenderer.color = Color(1f, 0.90f, 0.20f, 1f)
                    shapeRenderer.circle(cx, cy, 5.5f)
                    shapeRenderer.color = Color.WHITE
                    shapeRenderer.circle(cx, cy, 2.5f)

                    val pellets = listOf(
                        Vector2(-7f, 6f), Vector2(4f, 8f),
                        Vector2(-8f, -3f), Vector2(0f, -2f),
                        Vector2(8f, -1f), Vector2(4f, -8f)
                    )
                    for (pt in pellets) {
                        val px = cx + pt.x
                        val py = cy + pt.y
                        shapeRenderer.color = Color(0.22f, 0.24f, 0.26f, 1f)
                        shapeRenderer.circle(px, py, 3.0f)
                        shapeRenderer.color = Color(0.62f, 0.66f, 0.72f, 1f)
                        shapeRenderer.circle(px - 0.4f, py + 0.4f, 2.1f)
                        shapeRenderer.color = Color.WHITE
                        shapeRenderer.circle(px - 0.8f, py + 0.8f, 0.9f)
                    }
                }
            }

            // 3. HOLY ORDER (1: Healing Light, 2: Shield of Valor, 3: Holy Strike - CHUẨN HÌNH 3)
            TowerSpecialization.HOLY_ORDER -> {
                when (skillIndex) {
                    1 -> {
                        // HEALING LIGHT: Nền hào quang vàng cam + Thánh giá ánh sáng rực rỡ
                        shapeRenderer.color = Color(0.42f, 0.24f, 0.04f, 1f)
                        shapeRenderer.circle(cx, cy, 16f)
                        shapeRenderer.color = Color(0.88f, 0.58f, 0.10f, 1f)
                        shapeRenderer.circle(cx, cy, 12.5f)
                        shapeRenderer.color = Color(1f, 0.86f, 0.28f, 1f)
                        shapeRenderer.circle(cx, cy, 9f)

                        // Thánh giá ánh sáng trắng vàng ở tâm
                        shapeRenderer.color = Color(1f, 0.95f, 0.55f, 1f)
                        shapeRenderer.rect(cx - 3.2f, cy - 11f, 6.4f, 22f)
                        shapeRenderer.rect(cx - 9.5f, cy + 0.5f, 19f, 5.8f)

                        shapeRenderer.color = Color.WHITE
                        shapeRenderer.rect(cx - 1.8f, cy - 9.5f, 3.6f, 19f)
                        shapeRenderer.rect(cx - 8f, cy + 1.8f, 16f, 3.2f)
                    }
                    2 -> {
                        // SHIELD OF VALOR: Nền lam sáng + Khiên Hiệp Sĩ bạc viền trắng có chữ thập xanh
                        shapeRenderer.color = Color(0.10f, 0.28f, 0.45f, 1f)
                        shapeRenderer.circle(cx, cy, 16f)
                        shapeRenderer.color = Color(0.35f, 0.78f, 0.98f, 1f)
                        shapeRenderer.circle(cx, cy, 13f)

                        // Khung khiên bạc sáng
                        shapeRenderer.color = Color.WHITE
                        shapeRenderer.rect(cx - 9f, cy - 1f, 18f, 11f)
                        shapeRenderer.triangle(cx - 9f, cy - 1f, cx + 9f, cy - 1f, cx, cy - 12f)

                        shapeRenderer.color = Color(0.75f, 0.82f, 0.90f, 1f)
                        shapeRenderer.rect(cx - 7f, cy, 14f, 8.5f)
                        shapeRenderer.triangle(cx - 7f, cy, cx + 7f, cy, cx, cy - 9.5f)

                        // Chữ thập xanh đậm ở tâm khiên
                        shapeRenderer.color = Color(0.16f, 0.38f, 0.65f, 1f)
                        shapeRenderer.rect(cx - 1.8f, cy - 6.5f, 3.6f, 13f)
                        shapeRenderer.rect(cx - 5f, cy + 0.5f, 10f, 3.2f)
                    }
                    else -> {
                        // HOLY STRIKE: Nền xanh thẫm + Thanh đại kiếm ánh sáng cắm thẳng xuống
                        shapeRenderer.color = Color(0.08f, 0.24f, 0.36f, 1f)
                        shapeRenderer.circle(cx, cy, 16f)

                        // Hào quang thánh kiếm màu xanh thiên thanh
                        shapeRenderer.color = Color(0.25f, 0.90f, 1f, 0.85f)
                        shapeRenderer.triangle(cx - 6f, cy + 5f, cx + 6f, cy + 5f, cx, cy - 14f)
                        shapeRenderer.circle(cx, cy, 7.5f)

                        // Chuôi kiếm và thanh chắn chữ thập vàng kim
                        shapeRenderer.color = Color(0.95f, 0.78f, 0.20f, 1f)
                        shapeRenderer.rect(cx - 1.8f, cy + 7f, 3.6f, 6.5f)
                        shapeRenderer.circle(cx, cy + 13.5f, 2.4f)
                        shapeRenderer.rect(cx - 7.5f, cy + 5f, 15f, 3f)

                        // Lưỡi kiếm thép trắng bạc phóng thẳng xuống
                        shapeRenderer.color = Color.WHITE
                        shapeRenderer.rect(cx - 2.4f, cy - 7f, 4.8f, 12f)
                        shapeRenderer.triangle(cx - 2.4f, cy - 7f, cx + 2.4f, cy - 7f, cx, cy - 13f)
                    }
                }
            }

            // 4. BARBARIAN MEAD HALL (1: Throwing Axes, 2: Whirlwind Attack, 3: More Axes - CHUẨN HÌNH 4 & 5)
            TowerSpecialization.BARBARIAN_MEAD_HALL -> {
                when (skillIndex) {
                    1 -> {
                        // THROWING AXES: Nền đỏ rực lửa + Rìu chiến bạc bay chéo kèm vệt gió
                        shapeRenderer.color = Color(0.38f, 0.08f, 0.04f, 1f)
                        shapeRenderer.circle(cx, cy, 16f)
                        shapeRenderer.color = Color(0.85f, 0.22f, 0.08f, 1f)
                        shapeRenderer.circle(cx, cy, 12.5f)

                        // Vệt sáng tốc độ phóng rìu
                        shapeRenderer.color = Color(1f, 0.75f, 0.25f, 0.85f)
                        shapeRenderer.rectLine(cx - 11f, cy - 4f, cx - 1f, cy + 6f, 2f)
                        shapeRenderer.rectLine(cx - 9f, cy - 9f, cx + 1f, cy + 1f, 2f)

                        // Cán rìu gỗ nâu đặt chéo
                        shapeRenderer.color = Color(0.55f, 0.32f, 0.14f, 1f)
                        shapeRenderer.rectLine(cx - 7f, cy - 9f, cx + 6f, cy + 8f, 3.6f)

                        // Lưỡi rìu bản lớn ánh thép bạc
                        shapeRenderer.color = Color(0.75f, 0.80f, 0.86f, 1f)
                        shapeRenderer.triangle(cx, cy + 2f, cx + 10f, cy + 6f, cx + 6f, cy - 6f)
                        shapeRenderer.circle(cx + 6.5f, cy, 5.5f)
                        shapeRenderer.color = Color.WHITE
                        shapeRenderer.arc(cx + 5f, cy, 6.5f, -45f, 110f)
                    }
                    2 -> {
                        // WHIRLWIND ATTACK: Nền đỏ sẫm + Lốc xoáy kiếm khí trắng bạc (Tornado)
                        shapeRenderer.color = Color(0.35f, 0.08f, 0.05f, 1f)
                        shapeRenderer.circle(cx, cy, 16f)
                        shapeRenderer.color = Color(0.72f, 0.16f, 0.08f, 1f)
                        shapeRenderer.circle(cx, cy, 12.5f)

                        // Các tầng lốc xoáy trắng xám từ đỉnh rộng thu hẹp dần xuống đáy
                        shapeRenderer.color = Color(0.78f, 0.82f, 0.86f, 1f)
                        shapeRenderer.circle(cx, cy + 6f, 7.5f)
                        shapeRenderer.rect(cx - 8.5f, cy + 4f, 17f, 5f)
                        shapeRenderer.rect(cx - 6.5f, cy - 1f, 13f, 4.5f)
                        shapeRenderer.rect(cx - 4.5f, cy - 5.5f, 9f, 4f)
                        shapeRenderer.triangle(cx - 3.5f, cy - 5.5f, cx + 3.5f, cy - 5.5f, cx + 1f, cy - 12f)

                        // Lòng xoáy tối ở đỉnh lốc và vệt gió trắng sáng
                        shapeRenderer.color = Color(0.45f, 0.48f, 0.52f, 1f)
                        shapeRenderer.circle(cx, cy + 6.5f, 4.2f)
                        shapeRenderer.color = Color.WHITE
                        shapeRenderer.rectLine(cx - 7f, cy + 4f, cx + 7f, cy + 3f, 2.2f)
                        shapeRenderer.rectLine(cx - 5f, cy - 1f, cx + 5f, cy - 2f, 2.0f)
                        shapeRenderer.rectLine(cx - 3f, cy - 5.5f, cx + 3f, cy - 6.5f, 1.8f)
                    }
                    else -> {
                        // MORE AXES: Nền cam đỏ + Cặp song rìu (2 lưỡi rìu chiến xếp chồng nhau)
                        shapeRenderer.color = Color(0.38f, 0.10f, 0.04f, 1f)
                        shapeRenderer.circle(cx, cy, 16f)
                        shapeRenderer.color = Color(0.90f, 0.35f, 0.08f, 1f)
                        shapeRenderer.circle(cx, cy, 12.5f)

                        // Cán rìu thứ 1 (bên trái) & Lưỡi rìu 1
                        shapeRenderer.color = Color(0.48f, 0.28f, 0.12f, 1f)
                        shapeRenderer.rectLine(cx - 8f, cy - 9f, cx + 1f, cy + 7f, 3.2f)
                        shapeRenderer.color = Color(0.62f, 0.66f, 0.72f, 1f)
                        shapeRenderer.circle(cx - 5f, cy + 2f, 5.2f)

                        // Cán rìu thứ 2 (bên phải) & Lưỡi rìu 2 sáng bóng đè lên
                        shapeRenderer.color = Color(0.62f, 0.38f, 0.16f, 1f)
                        shapeRenderer.rectLine(cx - 2f, cy - 9f, cx + 7f, cy + 7f, 3.4f)
                        shapeRenderer.color = Color(0.85f, 0.90f, 0.96f, 1f)
                        shapeRenderer.triangle(cx + 1f, cy + 2f, cx + 10f, cy + 5f, cx + 7f, cy - 5f)
                        shapeRenderer.circle(cx + 6f, cy, 5.2f)
                        shapeRenderer.color = Color.WHITE
                        shapeRenderer.circle(cx + 6.5f, cy + 0.5f, 2.5f)
                    }
                }
            }

            // 5. ARCANE WIZARD (Death Ray & Teleport)
            TowerSpecialization.ARCANE_WIZARD -> {
                shapeRenderer.color = Color(0.22f, 0.08f, 0.36f, 1f)
                shapeRenderer.circle(cx, cy, 16f)
                if (skillIndex == 1) {
                    // Death Ray: Tia hủy diệt tím hồng chéo qua tâm
                    shapeRenderer.color = Color(0.92f, 0.20f, 1f, 1f)
                    shapeRenderer.rectLine(cx - 11f, cy - 9f, cx + 9f, cy + 9f, 6f)
                    shapeRenderer.circle(cx + 6f, cy + 6f, 6.5f)
                    shapeRenderer.color = Color.WHITE
                    shapeRenderer.rectLine(cx - 10f, cy - 8f, cx + 8f, cy + 8f, 2.4f)
                    shapeRenderer.circle(cx + 6f, cy + 6f, 3.2f)
                } else {
                    // Teleport: Vòng xoáy dịch chuyển không gian tím sáng
                    shapeRenderer.color = Color(0.55f, 0.22f, 0.98f, 1f)
                    shapeRenderer.circle(cx, cy, 12f)
                    shapeRenderer.color = Color(0.22f, 0.08f, 0.36f, 1f)
                    shapeRenderer.circle(cx, cy, 8.5f)
                    shapeRenderer.color = Color(0.88f, 0.65f, 1f, 1f)
                    shapeRenderer.circle(cx, cy, 5.5f)
                    shapeRenderer.color = Color.WHITE
                    shapeRenderer.circle(cx, cy, 2.5f)
                }
            }

            // 6. SORCERER MAGE (Polymorph & Elemental)
            TowerSpecialization.SORCERER_MAGE -> {
                shapeRenderer.color = Color(0.28f, 0.14f, 0.06f, 1f)
                shapeRenderer.circle(cx, cy, 16f)
                if (skillIndex == 1) {
                    // Polymorph: Cừu trắng dễ thương trên nền cam tím
                    shapeRenderer.color = Color(0.68f, 0.25f, 0.85f, 1f)
                    shapeRenderer.circle(cx, cy, 12.5f)
                    shapeRenderer.color = Color.WHITE
                    shapeRenderer.circle(cx - 1f, cy - 1f, 7.5f)
                    shapeRenderer.circle(cx - 6f, cy, 5f)
                    shapeRenderer.circle(cx + 4f, cy + 2f, 4.5f)
                    shapeRenderer.color = Color(0.18f, 0.16f, 0.15f, 1f)
                    shapeRenderer.circle(cx + 4.5f, cy + 0.5f, 3.5f)
                    shapeRenderer.color = Color.WHITE
                    shapeRenderer.circle(cx + 5.2f, cy + 1.2f, 1.1f)
                } else {
                    // Elemental: Người đá Golem khổng lồ mắt vàng rực
                    shapeRenderer.color = Color(0.48f, 0.40f, 0.30f, 1f)
                    shapeRenderer.circle(cx, cy - 2f, 10.5f)
                    shapeRenderer.color = Color(0.68f, 0.60f, 0.48f, 1f)
                    shapeRenderer.circle(cx - 5f, cy - 1f, 5.5f)
                    shapeRenderer.circle(cx + 5f, cy - 1f, 5.5f)
                    shapeRenderer.circle(cx, cy + 4.5f, 6f)
                    shapeRenderer.color = Color(1f, 0.88f, 0.18f, 1f)
                    shapeRenderer.circle(cx - 2.2f, cy + 4.5f, 1.6f)
                    shapeRenderer.circle(cx + 2.2f, cy + 4.5f, 1.6f)
                }
            }

            // 7. BIG BERTHA (Dragon Missile & Cluster Bombs)
            TowerSpecialization.BIG_BERTHA -> {
                shapeRenderer.color = Color(0.24f, 0.14f, 0.08f, 1f)
                shapeRenderer.circle(cx, cy, 16f)
                if (skillIndex == 1) {
                    // Dragon Missile: Tên lửa hạng nặng sơn đầu đỏ-vàng phun lửa đuôi
                    shapeRenderer.color = Color(1f, 0.55f, 0.10f, 1f)
                    shapeRenderer.circle(cx - 7f, cy - 7f, 5f)
                    shapeRenderer.color = Color(0.38f, 0.40f, 0.44f, 1f)
                    shapeRenderer.rectLine(cx - 6f, cy - 6f, cx + 5f, cy + 5f, 7f)
                    shapeRenderer.color = Color(0.88f, 0.18f, 0.12f, 1f)
                    shapeRenderer.triangle(cx + 10f, cy + 10f, cx + 1f, cy + 8f, cx + 8f, cy + 1f)
                    shapeRenderer.color = Color(1f, 0.88f, 0.20f, 1f)
                    shapeRenderer.circle(cx + 3f, cy + 3f, 2.2f)
                } else {
                    // Cluster Bombs: Vụ nổ cam + Chùm 3 quả bom bi đen
                    shapeRenderer.color = Color(0.95f, 0.48f, 0.10f, 1f)
                    shapeRenderer.circle(cx, cy, 11f)
                    shapeRenderer.color = Color(1f, 0.88f, 0.25f, 1f)
                    shapeRenderer.circle(cx, cy, 6.5f)
                    val bombs = listOf(Vector2(-5.5f, -3.5f), Vector2(5.5f, -3.5f), Vector2(0f, 5.5f))
                    for (b in bombs) {
                        shapeRenderer.color = Color(0.18f, 0.20f, 0.22f, 1f)
                        shapeRenderer.circle(cx + b.x, cy + b.y, 4.5f)
                        shapeRenderer.color = Color(0.65f, 0.70f, 0.75f, 1f)
                        shapeRenderer.circle(cx + b.x - 1f, cy + b.y + 1f, 1.4f)
                    }
                }
            }

            // 8. TESLA X104 (Chain Bolt & Overcharge)
            TowerSpecialization.TESLA_X104 -> {
                shapeRenderer.color = Color(0.08f, 0.20f, 0.34f, 1f)
                shapeRenderer.circle(cx, cy, 16f)
                if (skillIndex == 1) {
                    // Chain Bolt: Tia sét phân nhánh xanh trắng
                    shapeRenderer.color = Color(0.20f, 0.82f, 1f, 1f)
                    shapeRenderer.rectLine(cx - 9f, cy + 9f, cx + 2f, cy + 1f, 4.2f)
                    shapeRenderer.rectLine(cx + 2f, cy + 1f, cx - 3f, cy - 2f, 4.2f)
                    shapeRenderer.rectLine(cx - 3f, cy - 2f, cx + 9f, cy - 9f, 4.2f)
                    shapeRenderer.color = Color.WHITE
                    shapeRenderer.rectLine(cx - 9f, cy + 9f, cx + 2f, cy + 1f, 1.8f)
                    shapeRenderer.rectLine(cx + 2f, cy + 1f, cx - 3f, cy - 2f, 1.8f)
                    shapeRenderer.rectLine(cx - 3f, cy - 2f, cx + 9f, cy - 9f, 1.8f)
                } else {
                    // Overcharge: Vòng trường điện từ đồng tâm phát sáng
                    shapeRenderer.color = Color(0.18f, 0.78f, 1f, 1f)
                    shapeRenderer.circle(cx, cy, 12.5f)
                    shapeRenderer.color = Color(0.08f, 0.20f, 0.34f, 1f)
                    shapeRenderer.circle(cx, cy, 9f)
                    shapeRenderer.color = Color(0.55f, 0.94f, 1f, 1f)
                    shapeRenderer.circle(cx, cy, 6f)
                    shapeRenderer.color = Color.WHITE
                    shapeRenderer.circle(cx, cy, 3f)
                }
            }

            else -> {}
        }
    }

    private fun drawSunraySorcererIcon(
        shapeRenderer: ShapeRenderer,
        cx: Float,
        cy: Float
    ) {
        shapeRenderer.color = Color(0.08f, 0.07f, 0.06f, 0.95f)
        shapeRenderer.circle(cx, cy - 8f, 7.5f)

        shapeRenderer.color = Color(0.65f, 0.26f, 0.08f, 1f)
        shapeRenderer.triangle(cx - 8.5f, cy - 9.5f, cx + 8.5f, cy - 9.5f, cx, cy + 5f)
        shapeRenderer.rect(cx - 6.5f, cy - 9.5f, 13f, 8f)

        shapeRenderer.color = Color(0.88f, 0.42f, 0.12f, 1f)
        shapeRenderer.triangle(cx - 6.5f, cy - 8.5f, cx + 6.5f, cy - 8.5f, cx, cy + 4f)
        shapeRenderer.circle(cx - 6f, cy - 2.5f, 3.2f)
        shapeRenderer.circle(cx + 6f, cy - 2.5f, 3.2f)

        shapeRenderer.color = Color(0.96f, 0.78f, 0.22f, 1f)
        shapeRenderer.rect(cx - 4.5f, cy - 4.5f, 9f, 2f)

        shapeRenderer.color = Color(0.58f, 0.22f, 0.06f, 1f)
        shapeRenderer.circle(cx, cy + 4.5f, 7f)
        shapeRenderer.triangle(cx - 7.5f, cy + 4f, cx + 7.5f, cy + 4f, cx, cy + 11.5f)

        shapeRenderer.color = Color(0.85f, 0.38f, 0.10f, 1f)
        shapeRenderer.circle(cx, cy + 4.5f, 5.8f)

        shapeRenderer.color = Color(0.20f, 0.12f, 0.08f, 1f)
        shapeRenderer.circle(cx, cy + 4f, 4.2f)
        shapeRenderer.color = Color(0.98f, 0.85f, 0.58f, 1f)
        shapeRenderer.circle(cx, cy + 3.8f, 3.2f)
    }

    private fun drawSunrayFireButton(
        shapeRenderer: ShapeRenderer,
        btnX: Float,
        btnY: Float,
        isReady: Boolean,
        isSelected: Boolean
    ) {
        if (isReady) {
            shapeRenderer.color = Color(1f, 0.92f, 0.20f, if (isSelected) 0.55f else 0.38f)
            shapeRenderer.circle(btnX, btnY, 27f)
            shapeRenderer.color = Color(1f, 0.96f, 0.38f, if (isSelected) 0.85f else 0.68f)
            shapeRenderer.circle(btnX, btnY, 23.5f)
        }

        shapeRenderer.color = if (isReady) Color(0.32f, 0.32f, 0.35f, 1f) else Color(0.22f, 0.22f, 0.24f, 0.96f)
        shapeRenderer.circle(btnX, btnY, 20.5f)

        shapeRenderer.color = if (isReady) Color(0.58f, 0.60f, 0.65f, 1f) else Color(0.36f, 0.38f, 0.40f, 1f)
        shapeRenderer.rect(btnX - 21f, btnY - 2.5f, 3.5f, 5f)
        shapeRenderer.rect(btnX + 17.5f, btnY - 2.5f, 3.5f, 5f)
        shapeRenderer.rect(btnX - 2.5f, btnY + 17.5f, 5f, 3.5f)
        shapeRenderer.rect(btnX - 2.5f, btnY - 21f, 5f, 3.5f)

        shapeRenderer.color = Color(0.12f, 0.12f, 0.14f, 0.98f)
        shapeRenderer.circle(btnX, btnY, 16.5f)

        val crossColor = if (isReady) Color(0.95f, 0.28f, 0.10f, 1f) else Color(0.30f, 0.31f, 0.33f, 0.95f)
        shapeRenderer.color = crossColor

        shapeRenderer.circle(btnX, btnY, 11f)
        shapeRenderer.color = Color(0.12f, 0.12f, 0.14f, 1f)
        shapeRenderer.circle(btnX, btnY, 8.2f)

        shapeRenderer.color = crossColor
        shapeRenderer.rectLine(btnX - 13f, btnY, btnX + 13f, btnY, 2.6f)
        shapeRenderer.rectLine(btnX, btnY - 13f, btnX, btnY + 13f, 2.6f)

        if (isReady) {
            shapeRenderer.color = Color(1f, 0.65f, 0.20f, 1f)
            shapeRenderer.circle(btnX, btnY, 2.2f)
        }
    }

    private fun drawSpecializationIcon(
        shapeRenderer: ShapeRenderer,
        spec: TowerSpecialization,
        cx: Float,
        cy: Float
    ) {
        when (spec) {
            TowerSpecialization.RANGERS_HIDEOUT -> {
                val offsets = listOf(Vector2(-4f, -3f), Vector2(0f, 3f), Vector2(5f, 1f))
                for (off in offsets) {
                    val ax = cx + off.x
                    val ay = cy + off.y
                    shapeRenderer.color = Color(0.70f, 0.48f, 0.18f, 1f)
                    shapeRenderer.rectLine(ax + 4.5f, ay - 7.5f, ax - 1.5f, ay + 0.5f, 2.5f)
                    shapeRenderer.color = Color(0.92f, 0.95f, 0.98f, 1f)
                    shapeRenderer.triangle(ax - 5.5f, ay + 6.5f, ax - 5f, ay - 2f, ax + 2.5f, ay + 2.5f)
                    shapeRenderer.color = Color(0.68f, 0.74f, 0.80f, 1f)
                    shapeRenderer.triangle(ax - 5.5f, ay + 6.5f, ax - 1f, ay, ax + 2.5f, ay + 2.5f)
                }
            }

            TowerSpecialization.MUSKETEER_GARRISON -> {
                shapeRenderer.color = Color(1f, 0.50f, 0.08f, 1f)
                shapeRenderer.circle(cx - 5f, cy + 5f, 6.5f)
                shapeRenderer.triangle(cx - 11f, cy + 10f, cx - 2f, cy + 9f, cx - 9f, cy + 1f)
                shapeRenderer.triangle(cx - 6f, cy + 12f, cx + 1f, cy + 5f, cx - 8f, cy + 4f)
                shapeRenderer.color = Color(1f, 0.90f, 0.25f, 1f)
                shapeRenderer.circle(cx - 4.5f, cy + 4.5f, 4f)

                shapeRenderer.color = Color(0.45f, 0.46f, 0.48f, 1f)
                shapeRenderer.rectLine(cx + 7f, cy - 8f, cx - 3f, cy + 3f, 7.5f)
                shapeRenderer.color = Color(0.66f, 0.68f, 0.72f, 1f)
                shapeRenderer.rectLine(cx + 5.5f, cy - 8.5f, cx - 4f, cy + 2f, 4f)

                shapeRenderer.color = Color(0.32f, 0.33f, 0.35f, 1f)
                shapeRenderer.rectLine(cx - 5.5f, cy + 0.5f, cx - 0.5f, cy + 5.5f, 3f)
            }

            TowerSpecialization.HOLY_ORDER -> {
                shapeRenderer.color = Color(0.18f, 0.62f, 0.96f, 1f)
                shapeRenderer.circle(cx + 1f, cy + 9.5f, 3.8f)
                shapeRenderer.triangle(cx - 2f, cy + 8f, cx + 6f, cy + 11.5f, cx + 3f, cy + 6.5f)

                shapeRenderer.color = Color(0.58f, 0.63f, 0.70f, 1f)
                shapeRenderer.rect(cx - 7.5f, cy - 8.5f, 15f, 14f)
                shapeRenderer.circle(cx, cy + 4.5f, 7.5f)

                shapeRenderer.color = Color(0.85f, 0.90f, 0.95f, 1f)
                shapeRenderer.rect(cx - 6.5f, cy - 7.5f, 8f, 13f)
                shapeRenderer.circle(cx - 1.5f, cy + 4.5f, 6f)

                shapeRenderer.color = Color(0.10f, 0.10f, 0.12f, 1f)
                shapeRenderer.rect(cx - 5.5f, cy + 0.5f, 11f, 2.5f)
                shapeRenderer.rect(cx - 1.2f, cy - 6.5f, 2.4f, 8f)
            }

            TowerSpecialization.BARBARIAN_MEAD_HALL -> {
                shapeRenderer.color = Color(0.58f, 0.36f, 0.16f, 1f)
                shapeRenderer.rectLine(cx - 6f, cy - 8.5f, cx + 5f, cy + 7.5f, 3.5f)

                shapeRenderer.color = Color(0.52f, 0.56f, 0.60f, 1f)
                shapeRenderer.triangle(cx + 1f, cy + 2f, cx - 9.5f, cy + 5.5f, cx - 3.5f, cy - 6.5f)
                shapeRenderer.circle(cx - 5f, cy - 0.5f, 6.2f)

                shapeRenderer.color = Color(0.86f, 0.90f, 0.94f, 1f)
                shapeRenderer.arc(cx - 3.5f, cy - 0.5f, 7.5f, 125f, 135f)
            }

            TowerSpecialization.ARCANE_WIZARD -> {
                shapeRenderer.color = Color(0.28f, 0.08f, 0.45f, 1f)
                shapeRenderer.rectLine(cx - 6f, cy + 9f, cx + 2.5f, cy + 8f, 5f)
                shapeRenderer.rectLine(cx + 2.5f, cy + 8f, cx - 3f, cy + 2f, 5f)
                shapeRenderer.rectLine(cx - 3f, cy + 2f, cx + 6f, cy + 0.5f, 5f)
                shapeRenderer.rectLine(cx + 6f, cy + 0.5f, cx + 6.5f, cy - 4.5f, 5f)
                shapeRenderer.rectLine(cx + 6.5f, cy - 4.5f, cx, cy - 7.5f, 5f)
                shapeRenderer.rectLine(cx, cy - 7.5f, cx - 6f, cy - 4f, 5f)
                shapeRenderer.circle(cx, cy - 2f, 3.6f)
                shapeRenderer.rectLine(cx - 8f, cy - 7f, cx - 3f, cy - 9.5f, 4.2f)

                shapeRenderer.color = Color(0.68f, 0.22f, 0.98f, 1f)
                shapeRenderer.rectLine(cx - 6f, cy + 9f, cx + 2.5f, cy + 8f, 3.2f)
                shapeRenderer.rectLine(cx + 2.5f, cy + 8f, cx - 3f, cy + 2f, 3.2f)
                shapeRenderer.rectLine(cx - 3f, cy + 2f, cx + 6f, cy + 0.5f, 3.4f)
                shapeRenderer.rectLine(cx + 6f, cy + 0.5f, cx + 6.5f, cy - 4.5f, 3.4f)
                shapeRenderer.rectLine(cx + 6.5f, cy - 4.5f, cx, cy - 7.5f, 3.4f)
                shapeRenderer.rectLine(cx, cy - 7.5f, cx - 6f, cy - 4f, 3.2f)
                shapeRenderer.circle(cx, cy - 2f, 2.3f)
                shapeRenderer.rectLine(cx - 8f, cy - 7f, cx - 3f, cy - 9.5f, 2.5f)

                shapeRenderer.color = Color(0.86f, 0.58f, 1f, 1f)
                shapeRenderer.circle(cx, cy - 2f, 1.2f)
                shapeRenderer.rectLine(cx - 5f, cy + 9f, cx + 1.5f, cy + 8.2f, 1.5f)
            }

            TowerSpecialization.SORCERER_MAGE -> {
                shapeRenderer.color = Color(0.38f, 0.16f, 0.04f, 1f)
                shapeRenderer.rectLine(cx - 5f, cy + 10f, cx - 6.5f, cy - 0.5f, 5.2f)
                shapeRenderer.rectLine(cx - 6.5f, cy - 0.5f, cx + 6f, cy + 3.5f, 5.2f)
                shapeRenderer.circle(cx + 4f, cy + 8.5f, 3.8f)
                shapeRenderer.rectLine(cx - 5.5f, cy - 6.5f, cx + 6.5f, cy - 0.5f, 5.2f)
                shapeRenderer.circle(cx + 4.5f, cy - 7.5f, 3.8f)

                shapeRenderer.color = Color(0.96f, 0.56f, 0.08f, 1f)
                shapeRenderer.rectLine(cx - 5f, cy + 10f, cx - 6.5f, cy - 0.5f, 3.4f)
                shapeRenderer.rectLine(cx - 6.5f, cy - 0.5f, cx + 6f, cy + 3.5f, 3.4f)
                shapeRenderer.circle(cx + 4f, cy + 8.5f, 2.5f)
                shapeRenderer.rectLine(cx - 5.5f, cy - 6.5f, cx + 6.5f, cy - 0.5f, 3.4f)
                shapeRenderer.circle(cx + 4.5f, cy - 7.5f, 2.5f)

                shapeRenderer.color = Color(1f, 0.82f, 0.25f, 1f)
                shapeRenderer.circle(cx + 4f, cy + 8.5f, 1.2f)
                shapeRenderer.circle(cx + 4.5f, cy - 7.5f, 1.2f)
                shapeRenderer.rectLine(cx - 5f, cy + 9f, cx - 6f, cy + 1f, 1.5f)
            }

            TowerSpecialization.BIG_BERTHA -> {
                shapeRenderer.color = Color(0.22f, 0.20f, 0.18f, 1f)
                shapeRenderer.rectLine(cx - 9.5f, cy - 5.5f, cx + 7.5f, cy - 9.5f, 5.5f)
                shapeRenderer.color = Color(0.92f, 0.48f, 0.12f, 1f)
                shapeRenderer.rectLine(cx - 9f, cy - 4f, cx + 8f, cy - 8f, 3.2f)

                shapeRenderer.color = Color(0.54f, 0.56f, 0.60f, 1f)
                shapeRenderer.rectLine(cx - 8.5f, cy - 0.5f, cx + 8.5f, cy - 4.5f, 7.5f)
                shapeRenderer.color = Color(0.82f, 0.85f, 0.90f, 1f)
                shapeRenderer.rectLine(cx - 3f, cy - 1.5f, cx + 7.5f, cy - 4f, 6.5f)

                shapeRenderer.color = Color(0.25f, 0.26f, 0.28f, 1f)
                shapeRenderer.circle(cx - 5.5f, cy - 0.5f, 1.6f)
                shapeRenderer.circle(cx - 0.5f, cy - 1.8f, 1.6f)
                shapeRenderer.circle(cx + 4.5f, cy - 3.0f, 1.6f)

                shapeRenderer.color = Color(0.88f, 0.90f, 0.94f, 1f)
                shapeRenderer.circle(cx - 3.5f, cy + 5.5f, 5f)
                shapeRenderer.circle(cx + 3.5f, cy + 3.8f, 5f)
                shapeRenderer.rectLine(cx - 4.5f, cy + 5.8f, cx + 4.5f, cy + 3.5f, 9.5f)

                shapeRenderer.color = Color(0.14f, 0.14f, 0.16f, 1f)
                shapeRenderer.circle(cx - 2.5f, cy + 5.5f, 3.2f)
                shapeRenderer.circle(cx + 2.5f, cy + 4.2f, 3.2f)
                shapeRenderer.rectLine(cx - 3f, cy + 5.5f, cx + 3f, cy + 4.0f, 6f)
            }

            TowerSpecialization.TESLA_X104 -> {
                shapeRenderer.color = Color(0.12f, 0.65f, 0.98f, 1f)
                shapeRenderer.rectLine(cx - 8.5f, cy + 1f, cx, cy + 3.5f, 4f)
                shapeRenderer.rectLine(cx, cy + 3.5f, cx + 8.5f, cy - 1f, 4f)

                shapeRenderer.color = Color(0.45f, 0.48f, 0.52f, 1f)
                shapeRenderer.rectLine(cx - 1.5f, cy - 10f, cx + 2f, cy + 9f, 5f)
                shapeRenderer.color = Color(0.82f, 0.86f, 0.92f, 1f)
                shapeRenderer.rectLine(cx - 1f, cy - 9.5f, cx + 2f, cy + 8.5f, 2.6f)

                shapeRenderer.color = Color(0.12f, 0.12f, 0.14f, 1f)
                shapeRenderer.circle(cx - 3.5f, cy - 1f, 2.5f)
                shapeRenderer.circle(cx + 3.5f, cy - 2f, 2.5f)

                shapeRenderer.color = Color(0.15f, 0.75f, 1f, 1f)
                shapeRenderer.circle(cx - 8.5f, cy - 1f, 3.2f)
                shapeRenderer.circle(cx + 8.5f, cy - 3f, 3.2f)
                shapeRenderer.rectLine(cx - 8.5f, cy - 1.5f, cx - 2f, cy - 5.5f, 4.8f)
                shapeRenderer.rectLine(cx - 2f, cy - 5.5f, cx + 8.5f, cy - 3f, 4.8f)

                shapeRenderer.color = Color(0.88f, 0.98f, 1f, 1f)
                shapeRenderer.rectLine(cx - 7.5f, cy - 1.5f, cx - 2f, cy - 5f, 2.4f)
                shapeRenderer.rectLine(cx - 2f, cy - 5f, cx + 7.5f, cy - 3f, 2.4f)
                shapeRenderer.circle(cx - 8f, cy - 1f, 1.6f)
                shapeRenderer.circle(cx + 8f, cy - 3f, 1.6f)

                shapeRenderer.color = Color(0.15f, 0.78f, 1f, 1f)
                shapeRenderer.circle(cx + 2.2f, cy + 9.2f, 4.2f)
                shapeRenderer.color = Color.WHITE
                shapeRenderer.circle(cx + 2.2f, cy + 9.2f, 2.2f)
            }

            else -> {}
        }
    }

    // =========================================================================
    // VÙNG 4 & 5: VẼ BÁNH XE THÁP (BUILD WHEEL & PURCHASED TOWER WHEEL)
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
        selectedSpot?.let { spot ->
            val p = spot.getComponent(TransformComponent::class.java).position
            val isHigh = p.y > 540f

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
            shapeRenderer.color = Color(0.24f, 0.24f, 0.26f, 0.95f)
            shapeRenderer.rectLine(p.x - 52f, p.y + 52f, p.x + 52f, p.y + 52f, 5.5f)
            shapeRenderer.rectLine(p.x - 52f, p.y - 52f, p.x + 52f, p.y - 52f, 5.5f)
            shapeRenderer.rectLine(p.x - 52f, p.y + 52f, p.x - 52f, p.y - 52f, 5.5f)
            shapeRenderer.rectLine(p.x + 52f, p.y + 52f, p.x + 52f, p.y - 52f, 5.5f)

            shapeRenderer.color = Color(0.42f, 0.44f, 0.48f, 1f)
            shapeRenderer.rect(p.x - 7f, p.y + 48f, 14f, 8f)
            shapeRenderer.rect(p.x - 7f, p.y - 56f, 14f, 8f)
            shapeRenderer.rect(p.x - 56f, p.y - 4f, 8f, 14f)
            shapeRenderer.rect(p.x + 48f, p.y - 4f, 8f, 14f)

            for (opt in buildOptions) {
                val type = opt.first
                val isAllowed = allowedTowers.contains(type)
                val offset = if (isHigh) Vector2(opt.second.x, -opt.second.y) else opt.second
                val btn = Vector2(p).add(offset)
                val isSelected = (previewBuildType == type)

                drawIronActionBox(shapeRenderer, btn.x, btn.y, isSelected, hasCostBox = true)

                val cx = btn.x
                val cy = btn.y + 4f
                if (isAllowed) {
                    when (type) {
                        TowerType.ARCHER -> {
                            val arrowOffsets = listOf(Vector2(-6f, -3f), Vector2(0f, 5f), Vector2(6f, -3f))
                            for (ao in arrowOffsets) {
                                val ax = cx + ao.x; val ay = cy + ao.y
                                shapeRenderer.color = Color(0.62f, 0.44f, 0.22f, 1f)
                                shapeRenderer.rectLine(ax - 2f, ay - 6f, ax + 2f, ay + 1f, 2f)
                                shapeRenderer.color = Color(0.88f, 0.90f, 0.94f, 1f)
                                shapeRenderer.triangle(ax, ay + 6.5f, ax - 4.5f, ay + 1f, ax + 4.5f, ay + 1f)
                            }
                        }
                        TowerType.BARRACKS -> {
                            shapeRenderer.color = Color(0.80f, 0.82f, 0.86f, 1f)
                            shapeRenderer.triangle(cx, cy - 10f, cx - 11f, cy + 9f, cx + 11f, cy + 9f)
                            shapeRenderer.rect(cx - 11f, cy + 3f, 22f, 6f)
                            shapeRenderer.color = Color(0.40f, 0.44f, 0.50f, 1f)
                            shapeRenderer.triangle(cx, cy - 8f, cx - 8.5f, cy + 7.5f, cx + 8.5f, cy + 7.5f)
                            shapeRenderer.rect(cx - 8.5f, cy + 3f, 17f, 4.5f)
                            shapeRenderer.color = Color(0.92f, 0.95f, 0.98f, 0.8f)
                            shapeRenderer.rectLine(cx - 4f, cy + 7f, cx - 1f, cy - 4f, 1.8f)
                        }
                        TowerType.MAGE -> {
                            shapeRenderer.color = Color(0.85f, 0.68f, 0.20f, 1f)
                            shapeRenderer.rectLine(cx - 7f, cy - 9f, cx + 2f, cy + 1f, 3.2f)
                            shapeRenderer.color = Color(1f, 0.85f, 0.28f, 1f)
                            shapeRenderer.triangle(cx - 3f, cy + 2f, cx + 5f, cy + 2f, cx + 1f, cy - 2f)
                            shapeRenderer.color = Color(0.45f, 0.85f, 1f, 1f)
                            shapeRenderer.circle(cx + 1f, cy + 6.5f, 6.5f)
                            shapeRenderer.color = Color.WHITE
                            shapeRenderer.circle(cx - 0.5f, cy + 8f, 2.2f)
                        }
                        TowerType.ARTILLERY -> {
                            shapeRenderer.color = Color(0.35f, 0.38f, 0.42f, 1f)
                            shapeRenderer.circle(cx - 1f, cy - 2f, 9.5f)
                            shapeRenderer.color = Color(0.68f, 0.74f, 0.80f, 1f)
                            shapeRenderer.circle(cx - 4f, cy + 2f, 2.8f)
                            shapeRenderer.color = Color(0.24f, 0.24f, 0.26f, 1f)
                            shapeRenderer.rect(cx - 2f, cy + 6.5f, 4f, 3f)
                            shapeRenderer.color = Color(1f, 0.45f, 0.10f, 1f)
                            shapeRenderer.circle(cx + 1f, cy + 11.5f, 2.8f)
                            shapeRenderer.color = Color(1f, 0.95f, 0.30f, 1f)
                            shapeRenderer.circle(cx + 1f, cy + 11.5f, 1.5f)
                        }
                        else -> {}
                    }
                } else {
                    shapeRenderer.color = Color(0.85f, 0.2f, 0.2f, 0.9f)
                    shapeRenderer.rectLine(cx - 10f, cy - 10f, cx + 10f, cy + 10f, 3.5f)
                    shapeRenderer.rectLine(cx - 10f, cy + 10f, cx + 10f, cy - 10f, 3.5f)
                }
            }
            shapeRenderer.end()

            shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
            for (opt in buildOptions) {
                val offset = if (isHigh) Vector2(opt.second.x, -opt.second.y) else opt.second
                val btn = Vector2(p).add(offset)
                val isSelected = (previewBuildType == opt.first)
                val costBoxW = 40f
                val costBoxH = 18f
                val costBoxY = btn.y - 32f

                shapeRenderer.color = if (isSelected) Color.GOLD else Color(0.48f, 0.45f, 0.40f, 1f)
                shapeRenderer.rect(btn.x - 22f, btn.y - 18f, 44f, 44f)
                shapeRenderer.rect(btn.x - costBoxW / 2f, costBoxY, costBoxW, costBoxH)
            }
            shapeRenderer.end()

            batch.begin()
            for (opt in buildOptions) {
                val type = opt.first
                val isAllowed = allowedTowers.contains(type)
                val offset = if (isHigh) Vector2(opt.second.x, -opt.second.y) else opt.second
                val btn = Vector2(p).add(offset)
                val costBoxW = 40f
                val costBoxH = 18f
                val costBoxY = btn.y - 32f

                if (isAllowed) {
                    var cost = type.cost
                    if (type == TowerType.ARCHER && UpgradeManager.getEffectiveArcherLevel() >= 3) cost = 60
                    if (type == TowerType.BARRACKS && UpgradeManager.getEffectiveBarracksLevel() >= 2) cost = 60
                    if (type == TowerType.MAGE && UpgradeManager.getEffectiveMageLevel() >= 3) cost = 90
                    if (type == TowerType.ARTILLERY && UpgradeManager.getEffectiveArtilleryLevel() >= 3) cost = (cost * 0.9f).toInt()

                    val canAfford = EconomyManager.gold >= cost
                    val costColor = if (canAfford) Color(1f, 0.90f, 0.28f, 1f) else Color(1f, 0.35f, 0.35f, 1f)
                    drawBadgeCenteredText(batch, smallFont, "$cost", btn.x - costBoxW / 2f, costBoxY, costBoxW, costBoxH, costColor)
                } else {
                    drawBadgeCenteredText(batch, smallFont, "X", btn.x - costBoxW / 2f, costBoxY, costBoxW, costBoxH, Color.RED)
                }
            }
            batch.end()

            previewBuildType?.let { type ->
                if (allowedTowers.contains(type)) {
                    val opt = buildOptions.first { it.first == type }
                    val offset = if (isHigh) Vector2(opt.second.x, -opt.second.y) else opt.second
                    val btn = Vector2(p).add(offset)
                    renderKingdomRushBuildTooltip(shapeRenderer, batch, smallFont, btn, p, type)
                }
            }
        }

        selectedTower?.let { tower ->
            val tPos = tower.getComponent(TransformComponent::class.java).position
            val tComp = tower.getComponent(TowerComponent::class.java)
            val isHigh = tPos.y > 540f

            if (tComp.specialization == TowerSpecialization.SYLVAN_ELF_HALL) {
                val bComp = tower.getComponent(BarracksComponent::class.java)
                val currentElves = bComp?.soldiers?.size ?: 0

                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
                if (tComp.isRuined) {
                    val isSelected = (previewTowerAction == "REPAIR")
                    shapeRenderer.color = if (isSelected) Color(1f, 0.95f, 0.45f, 1f) else Color.DARK_GRAY
                    shapeRenderer.circle(tPos.x, tPos.y - 60f, if (isSelected) 28f else 25f)
                    shapeRenderer.color = Color(0.2f, 0.75f, 0.3f, 1f)
                    shapeRenderer.circle(tPos.x, tPos.y - 60f, 20f)
                } else {
                    if (currentElves < 4) {
                        val isSelected = (previewTowerAction == "HIRE_ELF")
                        shapeRenderer.color = if (isSelected) Color(1f, 0.95f, 0.45f, 1f) else Color.DARK_GRAY
                        shapeRenderer.circle(tPos.x + 55f, tPos.y, if (isSelected) 28f else 25f)
                        shapeRenderer.color = Color(0.2f, 0.65f, 0.9f, 1f)
                        shapeRenderer.circle(tPos.x + 55f, tPos.y, 19f)
                    }
                    shapeRenderer.color = Color.CYAN
                    shapeRenderer.circle(tPos.x - 55f, tPos.y, 23f)
                }
                shapeRenderer.end()

                batch.begin()
                if (tComp.isRuined) {
                    drawOutlinedText(batch, smallFont, "REPAIR", tPos.x - 26f, tPos.y - 28f, Color.WHITE)
                    drawOutlinedText(batch, smallFont, "100G", tPos.x - 16f, tPos.y - 90f, Color.GOLD)
                } else {
                    if (currentElves < 4) {
                        drawOutlinedText(batch, smallFont, "HIRE", tPos.x + 36f, tPos.y + 36f, Color.WHITE)
                        drawOutlinedText(batch, smallFont, "100G", tPos.x + 38f, tPos.y - 28f, Color.GOLD)
                    }
                    drawOutlinedText(batch, smallFont, "RALLY", tPos.x - 90f, tPos.y + 8f, Color.WHITE)
                    drawOutlinedText(batch, smallFont, "Elves: $currentElves/4", tPos.x - 34f, tPos.y + 55f, Color.YELLOW)
                }
                batch.end()

                previewTowerAction?.let { action ->
                    val btn = if (action == "REPAIR") Vector2(tPos.x, tPos.y - 60f) else Vector2(tPos.x + 55f, tPos.y)
                    renderKingdomRushActionTooltip(shapeRenderer, batch, smallFont, btn, tPos, tComp, action)
                }
                return
            }

            if (tComp.specialization == TowerSpecialization.SUNRAY_TOWER) {
                val ringRadius = 56f
                val ringSegments = 36
                val topBtnX = tPos.x
                val topBtnY = tPos.y + 58f
                val fireBtnX = tPos.x + 45f
                val fireBtnY = tPos.y - 31f

                val canHireMore = tComp.sunraySorcerers < 4
                val isReadyToFire = tComp.isSunrayReady && tComp.sunraySorcerers > 0
                val isHireSelected = (previewTowerAction == "UPGRADE_SUNRAY")
                val isFireSelected = (previewTowerAction == "FIRE_SUNRAY")

                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
                shapeRenderer.color = Color(0.25f, 0.25f, 0.28f, 0.96f)
                for (i in 0 until ringSegments) {
                    val a1 = Math.toRadians((i * 360.0 / ringSegments))
                    val a2 = Math.toRadians(((i + 1) * 360.0 / ringSegments))
                    val x1 = tPos.x + (ringRadius * Math.cos(a1)).toFloat()
                    val y1 = tPos.y + (ringRadius * Math.sin(a1)).toFloat()
                    val x2 = tPos.x + (ringRadius * Math.cos(a2)).toFloat()
                    val y2 = tPos.y + (ringRadius * Math.sin(a2)).toFloat()
                    shapeRenderer.rectLine(x1, y1, x2, y2, 6.5f)
                }

                shapeRenderer.color = Color(0.55f, 0.58f, 0.62f, 0.9f)
                for (i in 0 until 16) {
                    val ang = Math.toRadians((i * 22.5))
                    val dotX = tPos.x + (ringRadius * Math.cos(ang)).toFloat()
                    val dotY = tPos.y + (ringRadius * Math.sin(ang)).toFloat()
                    shapeRenderer.circle(dotX, dotY, 1.7f)
                }

                shapeRenderer.color = Color(0.60f, 0.64f, 0.68f, 1f)
                shapeRenderer.rect(tPos.x - ringRadius - 3.5f, tPos.y - 8f, 7f, 16f)
                shapeRenderer.triangle(tPos.x - ringRadius + 2f, tPos.y + 7f, tPos.x - ringRadius + 2f, tPos.y - 7f, tPos.x - ringRadius + 9f, tPos.y)

                shapeRenderer.rect(tPos.x + ringRadius - 3.5f, tPos.y - 8f, 7f, 16f)
                shapeRenderer.triangle(tPos.x + ringRadius - 2f, tPos.y + 7f, tPos.x + ringRadius - 2f, tPos.y - 7f, tPos.x + ringRadius - 9f, tPos.y)

                shapeRenderer.rect(tPos.x - 8f, tPos.y - ringRadius - 3.5f, 16f, 7f)
                shapeRenderer.triangle(tPos.x - 7f, tPos.y - ringRadius + 2f, tPos.x + 7f, tPos.y - ringRadius + 2f, tPos.x, tPos.y - ringRadius + 9f)

                drawIronActionBox(shapeRenderer, topBtnX, topBtnY, isHireSelected && canHireMore, hasCostBox = canHireMore)
                drawSunraySorcererIcon(shapeRenderer, topBtnX, topBtnY + 4f)
                drawSunrayFireButton(shapeRenderer, fireBtnX, fireBtnY, isReadyToFire, isFireSelected)
                shapeRenderer.end()

                shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
                shapeRenderer.color = if (isHireSelected && canHireMore) Color.GOLD else Color(0.48f, 0.45f, 0.40f, 1f)
                shapeRenderer.rect(topBtnX - 22f, topBtnY - 18f, 44f, 44f)
                if (canHireMore) {
                    shapeRenderer.rect(topBtnX - 20f, topBtnY - 32f, 40f, 18f)
                }

                shapeRenderer.color = if (isReadyToFire) Color.GOLD else Color(0.38f, 0.38f, 0.40f, 1f)
                shapeRenderer.circle(fireBtnX, fireBtnY, 20.5f)
                shapeRenderer.end()

                batch.begin()
                if (canHireMore) {
                    val costColor = if (EconomyManager.gold >= 100) Color(1f, 0.90f, 0.28f, 1f) else Color(1f, 0.35f, 0.35f, 1f)
                    drawBadgeCenteredText(batch, smallFont, "100", topBtnX - 20f, topBtnY - 32f, 40f, 18f, costColor)
                }
                batch.end()

                if (isHireSelected && canHireMore) {
                    renderKingdomRushActionTooltip(
                        shapeRenderer, batch, smallFont,
                        Vector2(topBtnX, topBtnY), tPos, tComp, "UPGRADE_SUNRAY"
                    )
                } else if (isFireSelected && isReadyToFire) {
                    renderKingdomRushActionTooltip(
                        shapeRenderer, batch, smallFont,
                        Vector2(fireBtnX, fireBtnY), tPos, tComp, "FIRE_SUNRAY"
                    )
                }
                return
            }

            val upgradeY = if (isHigh) tPos.y - 55f else tPos.y + 55f
            val sellY = if (isHigh) tPos.y + 55f else tPos.y - 55f
            val branchY = if (isHigh) tPos.y - 55f else tPos.y + 55f
            val rallyX = tPos.x + 45f
            val rallyY = if (isHigh) tPos.y + 31f else tPos.y - 31f

            val topBtn = Vector2(tPos.x, upgradeY)
            val sellBtn = Vector2(tPos.x, sellY)
            val rallyBtn = Vector2(rallyX, rallyY)

            shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
            shapeRenderer.color = Color(0.2f, 0.8f, 1f, 0.40f)
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

            val ringRadius = 55f
            val ringSegments = 36

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
            shapeRenderer.color = Color(0.25f, 0.25f, 0.28f, 0.96f)
            for (i in 0 until ringSegments) {
                val a1 = Math.toRadians((i * 360.0 / ringSegments))
                val a2 = Math.toRadians(((i + 1) * 360.0 / ringSegments))
                val x1 = tPos.x + (ringRadius * Math.cos(a1)).toFloat()
                val y1 = tPos.y + (ringRadius * Math.sin(a1)).toFloat()
                val x2 = tPos.x + (ringRadius * Math.cos(a2)).toFloat()
                val y2 = tPos.y + (ringRadius * Math.sin(a2)).toFloat()
                shapeRenderer.rectLine(x1, y1, x2, y2, 6.5f)
            }

            shapeRenderer.color = Color(0.55f, 0.58f, 0.62f, 0.9f)
            for (i in 0 until 12) {
                val ang = Math.toRadians((i * 30.0))
                val dotX = tPos.x + (ringRadius * Math.cos(ang)).toFloat()
                val dotY = tPos.y + (ringRadius * Math.sin(ang)).toFloat()
                shapeRenderer.circle(dotX, dotY, 1.8f)
            }

            shapeRenderer.color = Color(0.60f, 0.64f, 0.68f, 1f)
            shapeRenderer.triangle(tPos.x - ringRadius + 2f, tPos.y + 7f, tPos.x - ringRadius + 2f, tPos.y - 7f, tPos.x - ringRadius - 9f, tPos.y)
            shapeRenderer.triangle(tPos.x + ringRadius - 2f, tPos.y + 7f, tPos.x + ringRadius - 2f, tPos.y - 7f, tPos.x + ringRadius + 9f, tPos.y)

            val canUpgrade = tComp.level < maxLevelAllowed && tComp.level < 3
            val isLocked = tComp.level >= maxLevelAllowed && tComp.level < 4

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

            val isSingleBranch = (currentLevelNumber in listOf(5, 6))
            val has3Skills = tComp.towerType == TowerType.BARRACKS

            if ((tComp.level == 3 && canUpgradeToSpec && !isSingleBranch) || (tComp.level == 4 && !has3Skills)) {
                val topClaspY = if (isHigh) tPos.y - ringRadius else tPos.y + ringRadius
                val tipDir = if (isHigh) 8f else -8f
                shapeRenderer.color = Color(0.60f, 0.64f, 0.68f, 1f)
                shapeRenderer.rect(tPos.x - 8f, topClaspY - 3.5f, 16f, 7f)
                shapeRenderer.triangle(tPos.x - 7f, topClaspY, tPos.x + 7f, topClaspY, tPos.x, topClaspY + tipDir)
            }

            if (tComp.level < 3) {
                val isTopSelected = (previewTowerAction == "UPGRADE" || previewTowerAction == "LOCKED")
                drawIronActionBox(shapeRenderer, topBtn.x, topBtn.y, isTopSelected, hasCostBox = canUpgrade)

                val cx = topBtn.x; val cy = topBtn.y + 4f
                if (canUpgrade) {
                    shapeRenderer.color = Color(1f, 0.88f, 0.20f, 1f)
                    shapeRenderer.triangle(cx, cy + 9.5f, cx - 9.5f, cy + 1f, cx + 9.5f, cy + 1f)
                    shapeRenderer.rect(cx - 3.5f, cy - 6f, 7f, 7f)
                    shapeRenderer.rect(cx - 6f, cy - 9f, 12f, 2.5f)
                } else if (isLocked) {
                    val lcx = topBtn.x; val lcy = topBtn.y + 2f
                    shapeRenderer.color = Color(0.75f, 0.78f, 0.82f, 1f)
                    shapeRenderer.rectLine(lcx - 6f, lcy - 2f, lcx - 6f, lcy + 8f, 3f)
                    shapeRenderer.rectLine(lcx + 6f, lcy - 2f, lcx + 6f, lcy + 8f, 3f)
                    shapeRenderer.rectLine(lcx - 6f, lcy + 8f, lcx + 6f, lcy + 8f, 3f)

                    shapeRenderer.color = Color(0.55f, 0.58f, 0.62f, 1f)
                    shapeRenderer.rect(lcx - 8.5f, lcy - 10f, 17f, 13f)
                    shapeRenderer.color = Color(0.10f, 0.10f, 0.12f, 1f)
                    shapeRenderer.circle(lcx, lcy - 3f, 2.2f)
                    shapeRenderer.rect(lcx - 1f, lcy - 7.5f, 2f, 4.5f)
                }
            } else if (tComp.level == 3) {
                if (canUpgradeToSpec) {
                    val (spec1, spec2) = getBranchSpecs(tComp.towerType)

                    if (isSingleBranch) {
                        val isSel = (previewTowerAction == "SPEC_1")
                        drawIronActionBox(shapeRenderer, topBtn.x, branchY, isSel, hasCostBox = true)
                        drawSpecializationIcon(shapeRenderer, spec1, topBtn.x, branchY + 4f)
                    } else {
                        val isSel1 = (previewTowerAction == "SPEC_1")
                        val b1X = tPos.x - 45f
                        drawIronActionBox(shapeRenderer, b1X, branchY, isSel1, hasCostBox = true)
                        drawSpecializationIcon(shapeRenderer, spec1, b1X, branchY + 4f)

                        val isSel2 = (previewTowerAction == "SPEC_2")
                        val b2X = tPos.x + 45f
                        drawIronActionBox(shapeRenderer, b2X, branchY, isSel2, hasCostBox = true)
                        drawSpecializationIcon(shapeRenderer, spec2, b2X, branchY + 4f)
                    }
                } else {
                    val isTopSelected = (previewTowerAction == "LOCKED")
                    drawIronActionBox(shapeRenderer, topBtn.x, topBtn.y, isTopSelected, hasCostBox = false)
                    val lcx = topBtn.x; val lcy = topBtn.y + 2f
                    shapeRenderer.color = Color(0.75f, 0.78f, 0.82f, 1f)
                    shapeRenderer.rectLine(lcx - 6f, lcy - 2f, lcx - 6f, lcy + 8f, 3f)
                    shapeRenderer.rectLine(lcx + 6f, lcy - 2f, lcx + 6f, lcy + 8f, 3f)
                    shapeRenderer.rectLine(lcx - 6f, lcy + 8f, lcx + 6f, lcy + 8f, 3f)
                    shapeRenderer.color = Color(0.55f, 0.58f, 0.62f, 1f)
                    shapeRenderer.rect(lcx - 8.5f, lcy - 10f, 17f, 13f)
                    shapeRenderer.color = Color(0.10f, 0.10f, 0.12f, 1f)
                    shapeRenderer.circle(lcx, lcy - 3f, 2.2f)
                    shapeRenderer.rect(lcx - 1f, lcy - 7.5f, 2f, 4.5f)
                }
            } else if (tComp.level == 4) {
                val spec = tComp.specialization
                val skillCenterY = if (isHigh) tPos.y - 55f else tPos.y + 55f
                val skillMiddleY = if (isHigh) tPos.y - 75f else tPos.y + 75f

                if (has3Skills) {
                    val s1X = tPos.x - 65f
                    val isSel1 = (previewTowerAction == "SKILL_1")
                    drawSkillActionCircle(shapeRenderer, s1X, skillCenterY, tComp.skill1Level, 3, isSel1)
                    drawTowerSkillIcon(shapeRenderer, spec, 1, s1X, skillCenterY + 4f)

                    val s2X = tPos.x
                    val maxSkill2 = if (spec == TowerSpecialization.HOLY_ORDER) 1 else 3
                    val isSel2 = (previewTowerAction == "SKILL_2")
                    drawSkillActionCircle(shapeRenderer, s2X, skillMiddleY, tComp.skill2Level, maxSkill2, isSel2)
                    drawTowerSkillIcon(shapeRenderer, spec, 2, s2X, skillMiddleY + 4f)

                    val s3X = tPos.x + 65f
                    val isSel3 = (previewTowerAction == "SKILL_3")
                    drawSkillActionCircle(shapeRenderer, s3X, skillCenterY, tComp.skill3Level, 3, isSel3)
                    drawTowerSkillIcon(shapeRenderer, spec, 3, s3X, skillCenterY + 4f)
                } else {
                    val s1X = tPos.x - 45f
                    val isSel1 = (previewTowerAction == "SKILL_1")
                    drawSkillActionCircle(shapeRenderer, s1X, skillCenterY, tComp.skill1Level, 3, isSel1)
                    drawTowerSkillIcon(shapeRenderer, spec, 1, s1X, skillCenterY + 4f)

                    val s2X = tPos.x + 45f
                    val isSel2 = (previewTowerAction == "SKILL_2")
                    drawSkillActionCircle(shapeRenderer, s2X, skillCenterY, tComp.skill2Level, 3, isSel2)
                    drawTowerSkillIcon(shapeRenderer, spec, 2, s2X, skillCenterY + 4f)
                }
            }

            val isSellSelected = (previewTowerAction == "SELL")
            drawIronActionBox(shapeRenderer, sellBtn.x, sellBtn.y, isSellSelected, hasCostBox = false)

            if (canRally) {
                if (isSettingRallyPoint) {
                    shapeRenderer.color = Color(0.3f, 0.8f, 1f, 0.40f)
                    shapeRenderer.circle(rallyBtn.x, rallyBtn.y, 25f)
                }
                shapeRenderer.color = Color(0.24f, 0.24f, 0.26f, 1f)
                shapeRenderer.circle(rallyBtn.x, rallyBtn.y, 20f)
                shapeRenderer.color = Color(0.12f, 0.11f, 0.10f, 0.98f)
                shapeRenderer.circle(rallyBtn.x, rallyBtn.y, 16f)

                shapeRenderer.color = Color(0.80f, 0.82f, 0.86f, 1f)
                shapeRenderer.rectLine(rallyBtn.x - 5f, rallyBtn.y - 9f, rallyBtn.x - 5f, rallyBtn.y + 9f, 2.5f)
                shapeRenderer.color = Color.GOLD
                shapeRenderer.circle(rallyBtn.x - 5f, rallyBtn.y + 9.5f, 2f)
                shapeRenderer.color = Color(0.20f, 0.60f, 0.95f, 1f)
                shapeRenderer.triangle(rallyBtn.x - 4f, rallyBtn.y + 9f, rallyBtn.x + 8f, rallyBtn.y + 4.5f, rallyBtn.x - 4f, rallyBtn.y)
            }
            shapeRenderer.end()

            shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
            if (tComp.level < 3) {
                val isTopSelected = (previewTowerAction == "UPGRADE" || previewTowerAction == "LOCKED")
                shapeRenderer.color = if (isTopSelected) Color.GOLD else Color(0.48f, 0.45f, 0.40f, 1f)
                shapeRenderer.rect(topBtn.x - 22f, topBtn.y - 18f, 44f, 44f)
                if (canUpgrade) {
                    shapeRenderer.rect(topBtn.x - 20f, topBtn.y - 32f, 40f, 18f)
                }
            } else if (tComp.level == 3) {
                if (canUpgradeToSpec) {
                    if (isSingleBranch) {
                        val isSel = (previewTowerAction == "SPEC_1")
                        shapeRenderer.color = if (isSel) Color.GOLD else Color(0.48f, 0.45f, 0.40f, 1f)
                        shapeRenderer.rect(topBtn.x - 22f, branchY - 18f, 44f, 44f)
                        shapeRenderer.rect(topBtn.x - 20f, branchY - 32f, 40f, 18f)
                    } else {
                        val b1X = tPos.x - 45f
                        val isSel1 = (previewTowerAction == "SPEC_1")
                        shapeRenderer.color = if (isSel1) Color.GOLD else Color(0.48f, 0.45f, 0.40f, 1f)
                        shapeRenderer.rect(b1X - 22f, branchY - 18f, 44f, 44f)
                        shapeRenderer.rect(b1X - 20f, branchY - 32f, 40f, 18f)

                        val b2X = tPos.x + 45f
                        val isSel2 = (previewTowerAction == "SPEC_2")
                        shapeRenderer.color = if (isSel2) Color.GOLD else Color(0.48f, 0.45f, 0.40f, 1f)
                        shapeRenderer.rect(b2X - 22f, branchY - 18f, 44f, 44f)
                        shapeRenderer.rect(b2X - 20f, branchY - 32f, 40f, 18f)
                    }
                } else {
                    val isTopSelected = (previewTowerAction == "LOCKED")
                    shapeRenderer.color = if (isTopSelected) Color.GOLD else Color(0.48f, 0.45f, 0.40f, 1f)
                    shapeRenderer.rect(topBtn.x - 22f, topBtn.y - 18f, 44f, 44f)
                }
            } else if (tComp.level == 4) {
                val spec = tComp.specialization
                val skillCenterY = if (isHigh) tPos.y - 55f else tPos.y + 55f
                val skillMiddleY = if (isHigh) tPos.y - 75f else tPos.y + 75f

                if (has3Skills) {
                    val s1X = tPos.x - 65f
                    if (tComp.skill1Level < 3) {
                        shapeRenderer.color = if (previewTowerAction == "SKILL_1") Color.GOLD else Color(0.48f, 0.45f, 0.40f, 1f)
                        shapeRenderer.rect(s1X - 20f, skillCenterY - 32f, 40f, 18f)
                    }
                    val s2X = tPos.x
                    val maxSkill2 = if (spec == TowerSpecialization.HOLY_ORDER) 1 else 3
                    if (tComp.skill2Level < maxSkill2) {
                        shapeRenderer.color = if (previewTowerAction == "SKILL_2") Color.GOLD else Color(0.48f, 0.45f, 0.40f, 1f)
                        shapeRenderer.rect(s2X - 20f, skillMiddleY - 32f, 40f, 18f)
                    }
                    val s3X = tPos.x + 65f
                    if (tComp.skill3Level < 3) {
                        shapeRenderer.color = if (previewTowerAction == "SKILL_3") Color.GOLD else Color(0.48f, 0.45f, 0.40f, 1f)
                        shapeRenderer.rect(s3X - 20f, skillCenterY - 32f, 40f, 18f)
                    }
                } else {
                    val s1X = tPos.x - 45f
                    if (tComp.skill1Level < 3) {
                        shapeRenderer.color = if (previewTowerAction == "SKILL_1") Color.GOLD else Color(0.48f, 0.45f, 0.40f, 1f)
                        shapeRenderer.rect(s1X - 20f, skillCenterY - 32f, 40f, 18f)
                    }
                    val s2X = tPos.x + 45f
                    if (tComp.skill2Level < 3) {
                        shapeRenderer.color = if (previewTowerAction == "SKILL_2") Color.GOLD else Color(0.48f, 0.45f, 0.40f, 1f)
                        shapeRenderer.rect(s2X - 20f, skillCenterY - 32f, 40f, 18f)
                    }
                }
            }

            shapeRenderer.color = if (isSellSelected) Color.GOLD else Color(0.48f, 0.45f, 0.40f, 1f)
            shapeRenderer.rect(sellBtn.x - 22f, sellBtn.y - 18f, 44f, 44f)

            if (canRally) {
                shapeRenderer.color = if (isSettingRallyPoint) Color.CYAN else Color(0.48f, 0.45f, 0.40f, 1f)
                shapeRenderer.circle(rallyBtn.x, rallyBtn.y, 20f)
            }
            shapeRenderer.end()

            batch.begin()
            if (tComp.level < 3 && canUpgrade) {
                var upCost = tComp.upgradeCost
                if (tComp.towerType == TowerType.MAGE && UpgradeManager.getEffectiveMageLevel() >= 3) upCost = (upCost * 0.9f).toInt()
                if (tComp.towerType == TowerType.ARTILLERY && UpgradeManager.getEffectiveArtilleryLevel() >= 3) upCost = (upCost * 0.9f).toInt()
                val canAfford = EconomyManager.gold >= upCost
                val costColor = if (canAfford) Color(1f, 0.90f, 0.28f, 1f) else Color(1f, 0.35f, 0.35f, 1f)
                drawBadgeCenteredText(batch, smallFont, "$upCost", topBtn.x - 20f, topBtn.y - 32f, 40f, 18f, costColor)
            } else if (tComp.level == 3 && canUpgradeToSpec) {
                val (spec1, spec2) = getBranchSpecs(tComp.towerType)
                var cost1 = spec1.branchCost
                var cost2 = spec2.branchCost
                if (tComp.towerType == TowerType.MAGE && UpgradeManager.getEffectiveMageLevel() >= 3) {
                    cost1 = (cost1 * 0.9f).toInt()
                    cost2 = (cost2 * 0.9f).toInt()
                }
                if (tComp.towerType == TowerType.ARTILLERY && UpgradeManager.getEffectiveArtilleryLevel() >= 3) {
                    cost1 = (cost1 * 0.9f).toInt()
                    cost2 = (cost2 * 0.9f).toInt()
                }

                if (isSingleBranch) {
                    val color1 = if (EconomyManager.gold >= cost1) Color(1f, 0.90f, 0.28f, 1f) else Color(1f, 0.35f, 0.35f, 1f)
                    drawBadgeCenteredText(batch, smallFont, "$cost1", topBtn.x - 20f, branchY - 32f, 40f, 18f, color1)
                } else {
                    val b1X = tPos.x - 45f
                    val color1 = if (EconomyManager.gold >= cost1) Color(1f, 0.90f, 0.28f, 1f) else Color(1f, 0.35f, 0.35f, 1f)
                    drawBadgeCenteredText(batch, smallFont, "$cost1", b1X - 20f, branchY - 32f, 40f, 18f, color1)

                    val b2X = tPos.x + 45f
                    val color2 = if (EconomyManager.gold >= cost2) Color(1f, 0.90f, 0.28f, 1f) else Color(1f, 0.35f, 0.35f, 1f)
                    drawBadgeCenteredText(batch, smallFont, "$cost2", b2X - 20f, branchY - 32f, 40f, 18f, color2)
                }
            } else if (tComp.level == 4) {
                val spec = tComp.specialization
                val skillCenterY = if (isHigh) tPos.y - 55f else tPos.y + 55f
                val skillMiddleY = if (isHigh) tPos.y - 75f else tPos.y + 75f

                if (has3Skills) {
                    if (tComp.skill1Level < 3) {
                        val s1X = tPos.x - 65f
                        val c1 = getSkillUpgradeCost(spec, 1, tComp.skill1Level, tComp.towerType)
                        val col1 = if (EconomyManager.gold >= c1) Color(1f, 0.90f, 0.28f, 1f) else Color(1f, 0.35f, 0.35f, 1f)
                        drawBadgeCenteredText(batch, smallFont, "$c1", s1X - 20f, skillCenterY - 32f, 40f, 18f, col1)
                    }
                    val maxSkill2 = if (spec == TowerSpecialization.HOLY_ORDER) 1 else 3
                    if (tComp.skill2Level < maxSkill2) {
                        val s2X = tPos.x
                        val c2 = getSkillUpgradeCost(spec, 2, tComp.skill2Level, tComp.towerType)
                        val col2 = if (EconomyManager.gold >= c2) Color(1f, 0.90f, 0.28f, 1f) else Color(1f, 0.35f, 0.35f, 1f)
                        drawBadgeCenteredText(batch, smallFont, "$c2", s2X - 20f, skillMiddleY - 32f, 40f, 18f, col2)
                    }
                    if (tComp.skill3Level < 3) {
                        val s3X = tPos.x + 65f
                        val c3 = getSkillUpgradeCost(spec, 3, tComp.skill3Level, tComp.towerType)
                        val col3 = if (EconomyManager.gold >= c3) Color(1f, 0.90f, 0.28f, 1f) else Color(1f, 0.35f, 0.35f, 1f)
                        drawBadgeCenteredText(batch, smallFont, "$c3", s3X - 20f, skillCenterY - 32f, 40f, 18f, col3)
                    }
                } else {
                    if (tComp.skill1Level < 3) {
                        val s1X = tPos.x - 45f
                        val c1 = getSkillUpgradeCost(spec, 1, tComp.skill1Level, tComp.towerType)
                        val col1 = if (EconomyManager.gold >= c1) Color(1f, 0.90f, 0.28f, 1f) else Color(1f, 0.35f, 0.35f, 1f)
                        drawBadgeCenteredText(batch, smallFont, "$c1", s1X - 20f, skillCenterY - 32f, 40f, 18f, col1)
                    }
                    if (tComp.skill2Level < 3) {
                        val s2X = tPos.x + 45f
                        val c2 = getSkillUpgradeCost(spec, 2, tComp.skill2Level, tComp.towerType)
                        val col2 = if (EconomyManager.gold >= c2) Color(1f, 0.90f, 0.28f, 1f) else Color(1f, 0.35f, 0.35f, 1f)
                        drawBadgeCenteredText(batch, smallFont, "$c2", s2X - 20f, skillCenterY - 32f, 40f, 18f, col2)
                    }
                }
            }

            drawBadgeCenteredText(batch, smallFont, "$", sellBtn.x - 20f, sellBtn.y - 16f, 40f, 40f, Color.GOLD)
            batch.end()

            previewTowerAction?.let { action ->
                val skillCenterY = if (isHigh) tPos.y - 55f else tPos.y + 55f
                val skillMiddleY = if (isHigh) tPos.y - 75f else tPos.y + 75f

                val btn = when (action) {
                    "UPGRADE", "LOCKED" -> topBtn
                    "SPEC_1" -> if (isSingleBranch) Vector2(topBtn.x, branchY) else Vector2(tPos.x - 45f, branchY)
                    "SPEC_2" -> Vector2(tPos.x + 45f, branchY)
                    "SKILL_1" -> Vector2(if (has3Skills) tPos.x - 65f else tPos.x - 45f, skillCenterY)
                    "SKILL_2" -> Vector2(if (has3Skills) tPos.x else tPos.x + 45f, if (has3Skills) skillMiddleY else skillCenterY)
                    "SKILL_3" -> Vector2(tPos.x + 65f, skillCenterY)
                    "SELL" -> sellBtn
                    else -> topBtn
                }
                renderKingdomRushActionTooltip(shapeRenderer, batch, smallFont, btn, tPos, tComp, action)
            }
        }
    }

    private fun wrapText(text: String, maxCharsPerLine: Int = 24): List<String> {
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
    // VÙNG 6: TOOLTIP ENGINE CHUẨN WIKI CHO CÁC THÁP & 18 KỸ NĂNG LEVEL 4
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
                listOf("Damage:" to "4-6", "Type:" to "Physical", "Reload:" to "Average")
            )
            TowerType.BARRACKS -> Triple(
                "Militia Barracks",
                "Trains militia, tough soldiers that block and damage your enemies.",
                listOf("Life:" to "50", "Damage:" to "1-3", "Physical Armor:" to "None", "Respawn:" to "10 secs")
            )
            TowerType.MAGE -> Triple(
                "Mages Guild",
                "Mages cast armor piercing bolts at your enemies, ignoring any physical protection.",
                listOf("Damage:" to "9-17", "Type:" to "Magic", "Reload:" to "Slow")
            )
            TowerType.ARTILLERY -> Triple(
                "Dwarven Bombard",
                "Bombards ground enemies dealing area damage.",
                listOf("Damage:" to "8-15", "Type:" to "Explosive", "Reload:" to "Very Slow")
            )
            else -> Triple("Special Tower", "Strategic structure.", emptyList())
        }

        drawKingdomRushTooltipBox(shapeRenderer, batch, smallFont, btnPos, spotCenter, title, desc, stats)
    }

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
            "LOCKED" -> {
                drawKingdomRushTooltipBox(
                    shapeRenderer, batch, smallFont, btnPos, spotCenter,
                    "Locked!", "This item is locked.", emptyList()
                )
            }
            "SELL" -> {
                val refundRatio = if (UpgradeManager.getEffectiveArcherLevel() >= 1 && tComp.towerType == TowerType.ARCHER) 0.90f else 0.70f
                val refund = (tComp.totalInvestedGold * refundRatio).toInt()
                drawKingdomRushTooltipBox(
                    shapeRenderer, batch, smallFont, btnPos, spotCenter,
                    "Sell Tower", "Sell this tower and get a ${refund}GP refund.", emptyList()
                )
            }
            "UPGRADE_SUNRAY" -> {
                val nextCount = (tComp.sunraySorcerers + 1).coerceAtMost(4)
                val nextRecharge = when (nextCount) { 1 -> "19 secs"; 2 -> "16 secs"; 3 -> "13 secs"; else -> "10 secs" }
                drawKingdomRushTooltipBox(
                    shapeRenderer, batch, smallFont, btnPos, spotCenter,
                    "SUNRAY SORCERER",
                    "Call a sorcerer to charge the Sunray Tower.",
                    listOf("Sorcerers:" to "$nextCount / 4", "Charge:" to nextRecharge, "Ashes to" to "ashes!")
                )
            }
            "FIRE_SUNRAY" -> {
                val dmgText = when (tComp.sunraySorcerers) { 1 -> "75-125"; 2 -> "125-175"; 3 -> "175-225"; else -> "225-275" }
                drawKingdomRushTooltipBox(
                    shapeRenderer, batch, smallFont, btnPos, spotCenter,
                    "SUNRAY BEAM",
                    "Unleash a devastating beam of solar energy upon a target.",
                    listOf("Damage:" to dmgText, "Type:" to "True Damage")
                )
            }
            "UPGRADE" -> {
                val title = when (tComp.towerType) {
                    TowerType.ARCHER -> if (tComp.level == 1) "Marksmen Tower" else "Sharpshooter Tower"
                    TowerType.BARRACKS -> if (tComp.level == 1) "Footmen Barracks" else "Knights Barracks"
                    TowerType.MAGE -> if (tComp.level == 1) "Adept Tower" else "Wizard Tower"
                    TowerType.ARTILLERY -> if (tComp.level == 1) "Dwarven Artillery" else "Dwarven Howitzer"
                    else -> "Upgraded Tower"
                }

                val desc = when (tComp.towerType) {
                    TowerType.ARCHER -> "Marksmen shoot broadhead arrows, dealing more damage. Their longbows have longer attack range."
                    TowerType.BARRACKS -> "Trains better equipped soldiers with improved armor and health."
                    TowerType.MAGE -> "Upgrades adept spellcasters, intensifying magic piercing bolts."
                    TowerType.ARTILLERY -> "Upgrades heavy dwarven artillery, expanding blast radius and explosive damage."
                    else -> "Upgrades defensive structure."
                }

                val stats = when (tComp.towerType) {
                    TowerType.ARCHER -> listOf("Damage:" to if (tComp.level == 1) "7-11" else "10-16", "Type:" to "Physical", "Reload:" to "Fast")
                    TowerType.BARRACKS -> listOf("Life:" to if (tComp.level == 1) "100" else "150", "Damage:" to if (tComp.level == 1) "3-4" else "6-10", "Armor:" to if (tComp.level == 1) "Light" else "Medium", "Respawn:" to "10 secs")
                    TowerType.MAGE -> listOf("Damage:" to if (tComp.level == 1) "23-43" else "40-74", "Type:" to "Magic", "Reload:" to "Slow")
                    TowerType.ARTILLERY -> listOf("Damage:" to if (tComp.level == 1) "20-40" else "30-60", "Type:" to "Explosive", "Reload:" to "Very Slow")
                    else -> emptyList()
                }

                drawKingdomRushTooltipBox(shapeRenderer, batch, smallFont, btnPos, spotCenter, title, desc, stats)
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

                val fullTitle = when (spec) {
                    TowerSpecialization.RANGERS_HIDEOUT -> "Rangers Hideout"
                    TowerSpecialization.MUSKETEER_GARRISON -> "Musketeer Garrison"
                    TowerSpecialization.HOLY_ORDER -> "Holy Order"
                    TowerSpecialization.BARBARIAN_MEAD_HALL -> "Barbarian Mead Hall"
                    TowerSpecialization.ARCANE_WIZARD -> "Arcane Wizard"
                    TowerSpecialization.SORCERER_MAGE -> "Sorcerer Mage"
                    TowerSpecialization.BIG_BERTHA -> "Big Bertha"
                    TowerSpecialization.TESLA_X104 -> "Tesla x104"
                    else -> spec.displayName
                }

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
                    TowerSpecialization.RANGERS_HIDEOUT -> listOf("Damage:" to "13-19", "Type:" to "Physical", "Reload:" to "Very Fast", "Range:" to "Extreme")
                    TowerSpecialization.MUSKETEER_GARRISON -> listOf("Damage:" to "35-65", "Type:" to "Physical", "Reload:" to "Slow", "Range:" to "Extreme")
                    TowerSpecialization.HOLY_ORDER -> listOf("Life:" to "200", "Damage:" to "12-18", "Armor:" to "Heavy")
                    TowerSpecialization.BARBARIAN_MEAD_HALL -> listOf("Life:" to "250", "Damage:" to "16-24", "Armor:" to "None")
                    TowerSpecialization.ARCANE_WIZARD -> listOf("Damage:" to "76-140", "Type:" to "Magic", "Reload:" to "Slow", "Range:" to "Far")
                    TowerSpecialization.SORCERER_MAGE -> listOf("Damage:" to "42-78", "Type:" to "Magic", "Reload:" to "Average", "Range:" to "Far")
                    TowerSpecialization.BIG_BERTHA -> listOf("Damage:" to "50-100", "Type:" to "Explosive", "Reload:" to "Very Slow", "Range:" to "Far")
                    TowerSpecialization.TESLA_X104 -> listOf("Damage:" to "60-110", "Type:" to "Explosive", "Reload:" to "Very Slow", "Range:" to "Average")
                    else -> emptyList()
                }

                drawKingdomRushTooltipBox(shapeRenderer, batch, smallFont, btnPos, spotCenter, fullTitle, desc, stats)
            }

            "SKILL_1", "SKILL_2", "SKILL_3" -> {
                val spec = tComp.specialization
                val (sName, sLvl, sMax) = when (action) {
                    "SKILL_1" -> Triple(spec.skill1Name, tComp.skill1Level, 3)
                    "SKILL_2" -> Triple(spec.skill2Name, tComp.skill2Level, if (spec == TowerSpecialization.HOLY_ORDER) 1 else 3)
                    else -> Triple(spec.skill3Name, tComp.skill3Level, 3)
                }
                val nextLvl = (sLvl + 1).coerceAtMost(sMax)

                // 1. RANGERS HIDEOUT
                if (spec == TowerSpecialization.RANGERS_HIDEOUT && action == "SKILL_1") {
                    val dps = nextLvl * 5
                    drawKingdomRushTooltipBox(
                        shapeRenderer, batch, smallFont, btnPos, spotCenter,
                        "Poison Arrows",
                        "Poisons enemies, causing them to take $dps true damage per second over 3 seconds.",
                        listOf("Level:" to "$sLvl / $sMax", "Damage:" to "${dps * 3} (3 secs)", "Quote:" to "It won't hurt...")
                    )
                    return
                }
                if (spec == TowerSpecialization.RANGERS_HIDEOUT && action == "SKILL_2") {
                    val trapped = when (nextLvl) { 1 -> 4; 2 -> 6; else -> 8 }
                    drawKingdomRushTooltipBox(
                        shapeRenderer, batch, smallFont, btnPos, spotCenter,
                        "Wrath of the Forest",
                        "Summons thorns and vines that trap up to $trapped enemies, dealing 40 damage per second.",
                        listOf("Level:" to "$sLvl / $sMax", "Duration:" to "${nextLvl}s (CD: 9s)", "Quote:" to "It's a trap!")
                    )
                    return
                }

                // 2. MUSKETEER GARRISON
                if (spec == TowerSpecialization.MUSKETEER_GARRISON && action == "SKILL_1") {
                    val chance = nextLvl * 20
                    drawKingdomRushTooltipBox(
                        shapeRenderer, batch, smallFont, btnPos, spotCenter,
                        "Sniper Shot",
                        "1.5x range shot with a $chance% chance of instantly killing an enemy or dealing heavy damage.",
                        listOf("Level:" to "$sLvl / $sMax", "Cooldown:" to "14 secs", "One shot..." to "one kill...")
                    )
                    return
                }
                if (spec == TowerSpecialization.MUSKETEER_GARRISON && action == "SKILL_2") {
                    val minD = 60 * nextLvl
                    val maxD = 240 * nextLvl
                    drawKingdomRushTooltipBox(
                        shapeRenderer, batch, smallFont, btnPos, spotCenter,
                        "Shrapnel Shot",
                        "Short range shot that blasts an area with 6 grapeshot, dealing $minD-$maxD area damage.",
                        listOf("Level:" to "$sLvl / $sMax", "Cooldown:" to "9 secs", "Quote:" to "Eat grapeshot!")
                    )
                    return
                }

                // 3. HOLY ORDER (CHUẨN WIKI HÌNH 3)
                if (spec == TowerSpecialization.HOLY_ORDER && action == "SKILL_1") {
                    val healStr = when (nextLvl) { 1 -> "40-60"; 2 -> "80-120"; else -> "120-180" }
                    drawKingdomRushTooltipBox(
                        shapeRenderer, batch, smallFont, btnPos, spotCenter,
                        "Healing Light",
                        "Paladins can heal themselves for $healStr HP every 10 seconds.",
                        listOf("Level:" to "$sLvl / $sMax", "Healing:" to "$healStr HP", "There shall" to "be light!")
                    )
                    return
                }
                if (spec == TowerSpecialization.HOLY_ORDER && action == "SKILL_2") {
                    drawKingdomRushTooltipBox(
                        shapeRenderer, batch, smallFont, btnPos, spotCenter,
                        "Shield of Valor",
                        "Enhances Paladins' protection, improving their armor by 15%.",
                        listOf("Level:" to "$sLvl / $sMax", "Extra Armor:" to "+15%", "No retreat," to "No surrender!")
                    )
                    return
                }
                if (spec == TowerSpecialization.HOLY_ORDER && action == "SKILL_3") {
                    val strikeDmg = when (nextLvl) { 1 -> "25-45"; 2 -> "50-90"; else -> "75-135" }
                    drawKingdomRushTooltipBox(
                        shapeRenderer, batch, smallFont, btnPos, spotCenter,
                        "Holy Strike",
                        "Paladins' attacks have a chance to become a Holy Strike dealing $strikeDmg holy area damage.",
                        listOf("Level:" to "$sLvl / $sMax", "Area Dmg:" to strikeDmg, "By Holy fire" to "be Purged!")
                    )
                    return
                }

                // 4. BARBARIAN MEAD HALL (CHUẨN WIKI HÌNH 4 & 5)
                if (spec == TowerSpecialization.BARBARIAN_MEAD_HALL && action == "SKILL_1") {
                    val axeDmg = when (nextLvl) { 1 -> "34-42"; 2 -> "44-52"; else -> "54-62" }
                    val axeRng = when (nextLvl) { 1 -> "260"; 2 -> "280"; else -> "300" }
                    drawKingdomRushTooltipBox(
                        shapeRenderer, batch, smallFont, btnPos, spotCenter,
                        "Throwing Axes",
                        "Barbarians can throw great axes at ground and flying enemies in range.",
                        listOf("Level:" to "$sLvl / $sMax", "Damage:" to "$axeDmg (Rng $axeRng)", "It's raining" to "axes!")
                    )
                    return
                }
                if (spec == TowerSpecialization.BARBARIAN_MEAD_HALL && action == "SKILL_2") {
                    val wwDmg = when (nextLvl) { 1 -> "25-45"; 2 -> "40-60"; else -> "55-75" }
                    val wwChance = when (nextLvl) { 1 -> "15%"; 2 -> "20%"; else -> "25%" }
                    drawKingdomRushTooltipBox(
                        shapeRenderer, batch, smallFont, btnPos, spotCenter,
                        "Whirlwind Attack",
                        "Gives Barbarians a $wwChance chance on attack to deal $wwDmg area physical damage.",
                        listOf("Level:" to "$sLvl / $sMax", "Damage:" to "$wwDmg ($wwChance)", "I got something" to "to axe you..")
                    )
                    return
                }
                if (spec == TowerSpecialization.BARBARIAN_MEAD_HALL && action == "SKILL_3") {
                    val bonusDmg = nextLvl * 10
                    drawKingdomRushTooltipBox(
                        shapeRenderer, batch, smallFont, btnPos, spotCenter,
                        "More Axes",
                        "Barbarians equip an additional axe to deliver +$bonusDmg extra damage on every attack.",
                        listOf("Level:" to "$sLvl / $sMax", "Damage Bonus:" to "+$bonusDmg", "Double the Axes." to "Double the fun!")
                    )
                    return
                }

                val desc = when (sName) {
                    "Polymorph" -> "Curses enemies, transforming them into harmless sheep with no armor."
                    "Elemental" -> "Summons a mighty Earth Golem with heavy armor and massive health."
                    "Death Ray" -> "Concentrates arcane power to instantly disintegrate the target."
                    "Teleport" -> "Warps groups of advancing enemies back along the road."
                    "Dragon Missile" -> "Fires long-range heat-seeking missiles with colossal blast area."
                    "Cluster Bombs" -> "Scatters secondary explosive cluster bomblets across the path."
                    "Chain Bolt" -> "Empowers lightning bolts to jump across additional targets."
                    "Overcharge" -> "Electrifies the surrounding area with a pulse damage aura."
                    else -> "Special tactical upgrade skill."
                }

                val stats = listOf(
                    "Skill Level:" to "$sLvl / $sMax",
                    "Cooldown:" to if (sName == "Polymorph") "20s - 16s" else "Standard"
                )

                drawKingdomRushTooltipBox(shapeRenderer, batch, smallFont, btnPos, spotCenter, sName, desc, stats)
            }

            "REPAIR" -> {
                drawKingdomRushTooltipBox(
                    shapeRenderer, batch, smallFont, btnPos, spotCenter,
                    "Repair Sylvan Elf Hall",
                    "Restores the ancient hall to recruit agile elven archers.",
                    listOf("Cost:" to "100G")
                )
            }

            "HIRE_ELF" -> {
                drawKingdomRushTooltipBox(
                    shapeRenderer, batch, smallFont, btnPos, spotCenter,
                    "Recruit Sylvan Elf",
                    "Trains a master Sylvan Elf adept in ranged arrows and melee blades.",
                    listOf("Life:" to "180", "Damage:" to "15-25", "Armor:" to "Medium")
                )
            }
        }
    }

    private enum class BeakSide { LEFT, RIGHT, TOP, BOTTOM }

    private fun drawKingdomRushTooltipBox(
        shapeRenderer: ShapeRenderer,
        batch: SpriteBatch,
        smallFont: BitmapFont,
        btnPos: Vector2,
        spotCenter: Vector2,
        title: String,
        desc: String,
        stats: List<Pair<String, String>>
    ) {
        val oldScale = smallFont.data.scaleX
        smallFont.data.setScale(0.96f)

        val boxWidth = 205f
        val descLines = wrapText(desc, 24)
        val boxHeight = 18f + 18f + (descLines.size * 15f) + (if (stats.isNotEmpty()) 8f else 0f) + (stats.size * 16f) + 10f

        var boxX: Float
        var boxY: Float
        val beakSide: BeakSide

        if (btnPos.x < spotCenter.x - 10f) {
            if (btnPos.x - 26f - boxWidth >= 12f) {
                beakSide = BeakSide.RIGHT
                boxX = btnPos.x - 26f - boxWidth
                boxY = (btnPos.y - boxHeight * 0.45f).coerceIn(40f, Config.WORLD_HEIGHT - boxHeight - 20f)
            } else if (btnPos.y + 30f + boxHeight <= Config.WORLD_HEIGHT - 12f) {
                beakSide = BeakSide.BOTTOM
                boxY = btnPos.y + 30f
                boxX = (btnPos.x - boxWidth / 2f).coerceIn(12f, Config.WORLD_WIDTH - boxWidth - 12f)
            } else {
                beakSide = BeakSide.TOP
                boxY = btnPos.y - 30f - boxHeight
                boxX = (btnPos.x - boxWidth / 2f).coerceIn(12f, Config.WORLD_WIDTH - boxWidth - 12f)
            }
        } else if (btnPos.x > spotCenter.x + 10f) {
            if (btnPos.x + 26f + boxWidth <= Config.WORLD_WIDTH - 12f) {
                beakSide = BeakSide.LEFT
                boxX = btnPos.x + 26f
                boxY = (btnPos.y - boxHeight * 0.45f).coerceIn(40f, Config.WORLD_HEIGHT - boxHeight - 20f)
            } else if (btnPos.y + 30f + boxHeight <= Config.WORLD_HEIGHT - 12f) {
                beakSide = BeakSide.BOTTOM
                boxY = btnPos.y + 30f
                boxX = (btnPos.x - boxWidth / 2f).coerceIn(12f, Config.WORLD_WIDTH - boxWidth - 12f)
            } else {
                beakSide = BeakSide.TOP
                boxY = btnPos.y - 30f - boxHeight
                boxX = (btnPos.x - boxWidth / 2f).coerceIn(12f, Config.WORLD_WIDTH - boxWidth - 12f)
            }
        } else {
            if (btnPos.y > spotCenter.y + 20f && btnPos.y + 30f + boxHeight <= Config.WORLD_HEIGHT - 12f) {
                beakSide = BeakSide.BOTTOM
                boxY = btnPos.y + 30f
                boxX = (btnPos.x - boxWidth / 2f).coerceIn(12f, Config.WORLD_WIDTH - boxWidth - 12f)
            } else if (spotCenter.x + 36f + boxWidth <= Config.WORLD_WIDTH - 12f) {
                beakSide = BeakSide.LEFT
                boxX = spotCenter.x + 36f
                boxY = (btnPos.y - boxHeight * 0.45f).coerceIn(40f, Config.WORLD_HEIGHT - boxHeight - 20f)
            } else {
                beakSide = BeakSide.RIGHT
                boxX = spotCenter.x - 36f - boxWidth
                boxY = (btnPos.y - boxHeight * 0.45f).coerceIn(40f, Config.WORLD_HEIGHT - boxHeight - 20f)
            }
        }

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color(0.14f, 0.13f, 0.10f, 0.98f)
        shapeRenderer.rect(boxX, boxY, boxWidth, boxHeight)

        when (beakSide) {
            BeakSide.LEFT -> {
                val beakY = btnPos.y.coerceIn(boxY + 12f, boxY + boxHeight - 12f)
                shapeRenderer.triangle(boxX, beakY - 6f, boxX, beakY + 6f, boxX - 9f, beakY)
            }
            BeakSide.RIGHT -> {
                val beakY = btnPos.y.coerceIn(boxY + 12f, boxY + boxHeight - 12f)
                shapeRenderer.triangle(boxX + boxWidth, beakY - 6f, boxX + boxWidth, beakY + 6f, boxX + boxWidth + 9f, beakY)
            }
            BeakSide.BOTTOM -> {
                val beakX = btnPos.x.coerceIn(boxX + 16f, boxX + boxWidth - 16f)
                shapeRenderer.triangle(beakX - 6f, boxY, beakX + 6f, boxY, beakX, boxY - 9f)
            }
            BeakSide.TOP -> {
                val beakX = btnPos.x.coerceIn(boxX + 16f, boxX + boxWidth - 16f)
                shapeRenderer.triangle(beakX - 6f, boxY + boxHeight, beakX + 6f, boxY + boxHeight, beakX, boxY + boxHeight + 9f)
            }
        }
        shapeRenderer.end()

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color(0.68f, 0.60f, 0.38f, 1f)
        shapeRenderer.rect(boxX, boxY, boxWidth, boxHeight)

        when (beakSide) {
            BeakSide.LEFT -> {
                val beakY = btnPos.y.coerceIn(boxY + 12f, boxY + boxHeight - 12f)
                shapeRenderer.line(boxX, beakY - 6f, boxX - 9f, beakY)
                shapeRenderer.line(boxX, beakY + 6f, boxX - 9f, beakY)
            }
            BeakSide.RIGHT -> {
                val beakY = btnPos.y.coerceIn(boxY + 12f, boxY + boxHeight - 12f)
                shapeRenderer.line(boxX + boxWidth, beakY - 6f, boxX + boxWidth + 9f, beakY)
                shapeRenderer.line(boxX + boxWidth, beakY + 6f, boxX + boxWidth + 9f, beakY)
            }
            BeakSide.BOTTOM -> {
                val beakX = btnPos.x.coerceIn(boxX + 16f, boxX + boxWidth - 16f)
                shapeRenderer.line(beakX - 6f, boxY, beakX, boxY - 9f)
                shapeRenderer.line(beakX + 6f, boxY, beakX, boxY - 9f)
            }
            BeakSide.TOP -> {
                val beakX = btnPos.x.coerceIn(boxX + 16f, boxX + boxWidth - 16f)
                shapeRenderer.line(beakX - 6f, boxY + boxHeight, beakX, boxY + boxHeight + 9f)
                shapeRenderer.line(beakX + 6f, boxY + boxHeight, beakX, boxY + boxHeight + 9f)
            }
        }
        shapeRenderer.end()

        batch.begin()
        var curY = boxY + boxHeight - 12f

        val titleColor = if (title == "SUNRAY SORCERER" || title == "SUNRAY BEAM") Color(0.95f, 0.92f, 0.25f, 1f) else Color.WHITE
        drawShadowedText(batch, smallFont, title, boxX + 10f, curY, titleColor)
        curY -= 18f

        for (line in descLines) {
            drawShadowedText(batch, smallFont, line, boxX + 10f, curY, Color(1f, 0.85f, 0.42f, 1f))
            curY -= 15f
        }
        if (stats.isNotEmpty()) curY -= 6f

        for (st in stats) {
            drawShadowedText(batch, smallFont, st.first, boxX + 10f, curY, Color(1f, 0.82f, 0.35f, 1f))
            drawShadowedText(batch, smallFont, st.second, boxX + 105f, curY, Color.WHITE)
            curY -= 16f
        }

        batch.end()
        smallFont.data.setScale(oldScale)
    }

    // =========================================================================
    // VÙNG 7: THANH THÔNG TIN DƯỚI CÙNG (BOTTOM INFO BAR)
    // =========================================================================
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

            if (tComp.specialization == TowerSpecialization.SYLVAN_ELF_HALL) {
                val bComp = tower.getComponent(BarracksComponent::class.java)
                val currentElves = bComp?.soldiers?.size ?: 0
                drawOutlinedText(batch, font, "SYLVAN ELF HALL", 40f, 42f, Color(0.2f, 0.85f, 0.4f, 1f))
                drawOutlinedText(batch, smallFont, "Elves: $currentElves / 4", 320f, 40f, Color.GOLD)
                drawOutlinedText(batch, smallFont, "Damage: 15-25 (Physical)", 520f, 40f, Color.WHITE)
                drawOutlinedText(batch, smallFont, "Armor: Medium", 820f, 40f, Color.SKY)
                batch.end()
                return
            }

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
                    val dmgTypeLabel = when {
                        tComp.specialization == TowerSpecialization.TESLA_X104 -> "(Explosive)"
                        tComp.towerType == TowerType.MAGE -> "(Magic)"
                        tComp.towerType == TowerType.ARTILLERY -> "(Explosive)"
                        else -> "(Physical)"
                    }
                    drawOutlinedText(batch, smallFont, "Damage: ${tComp.damageMin.toInt()}-${tComp.damageMax.toInt()} $dmgTypeLabel", 320f, 40f, Color.WHITE)
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
}
