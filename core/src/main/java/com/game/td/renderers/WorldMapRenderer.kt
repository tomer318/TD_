package com.game.td.renderers

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.game.td.Config
import com.game.td.ecs.components.GameDifficulty
import com.game.td.managers.GameMode
import com.game.td.managers.SaveManager
import com.game.td.managers.SoundManager
import com.game.td.managers.UpgradeManager
import com.game.td.map.LevelData

object WorldMapRenderer {

    private val layout = GlyphLayout()

    fun render(
        shapeRenderer: ShapeRenderer,
        batch: SpriteBatch,
        titleFont: BitmapFont,
        font: BitmapFont,
        smallFont: BitmapFont,
        campaignNodes: List<Pair<String, Vector2>>,
        sandboxNode: Vector2,
        nextLevelToPlay: Int,
        isPopupOpen: Boolean,
        isUpgradesOpen: Boolean,
        isEncyclopediaOpen: Boolean,
        selectedLevelData: LevelData?,
        selectedLevelName: String,
        selectedLevelNumber: Int,
        currentDifficulty: GameDifficulty,
        currentGameMode: GameMode,
        pressedMapButton: String? = null
    ) {
        ComicFontRenderer.prepareCrispFont(titleFont, font, smallFont)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        // 1. NỀN CUỘN BẢN ĐỒ DA DÊ CỔ ĐIỂN
        shapeRenderer.color = Color(0.84f, 0.73f, 0.52f, 1f)
        shapeRenderer.rect(25f, 25f, Config.WORLD_WIDTH - 50f, Config.WORLD_HEIGHT - 50f)

        shapeRenderer.color = Color(0.72f, 0.60f, 0.40f, 1f)
        shapeRenderer.rect(20f, 18f, 24f, Config.WORLD_HEIGHT - 36f)
        shapeRenderer.rect(Config.WORLD_WIDTH - 44f, 18f, 24f, Config.WORLD_HEIGHT - 36f)

        shapeRenderer.color = Color(0.58f, 0.46f, 0.30f, 1f)
        shapeRenderer.rect(25f, 25f, Config.WORLD_WIDTH - 50f, 6f)
        shapeRenderer.rect(25f, Config.WORLD_HEIGHT - 31f, Config.WORLD_WIDTH - 50f, 6f)

        drawMapTerrainDecorations(shapeRenderer)

        // 2. ĐƯỜNG HÀNH QUÂN NÉT ĐỨT GIỮA CÁC MÀN CHƠI
        for (i in 0 until campaignNodes.size - 1) {
            val isPathUnlocked = (i + 1 < nextLevelToPlay)
            val lineColor = if (isPathUnlocked) Color(0.32f, 0.18f, 0.08f, 1f) else Color(0.60f, 0.52f, 0.40f, 0.35f)
            drawDottedLine(shapeRenderer, campaignNodes[i].second, campaignNodes[i + 1].second, lineColor, 5f)
        }

        // 3. VẼ CỜ KHIÊN CHO TỪNG MỐC MÀN CHƠI
        for (i in campaignNodes.indices) {
            val lvlNum = i + 1
            val pos = campaignNodes[i].second
            val stars = SaveManager.getLevelStars(lvlNum)
            val isHeroicDone = SaveManager.isHeroicCompleted(lvlNum)
            val isIronDone = SaveManager.isIronCompleted(lvlNum)

            val isPassed = (stars > 0)
            val isCurrentNext = (lvlNum == nextLevelToPlay)

            when {
                isPassed -> drawCompletedShieldBanner(shapeRenderer, pos.x, pos.y, isHeroicDone, isIronDone)
                isCurrentNext -> drawNextMissionRedBanner(shapeRenderer, pos.x, pos.y)
                else -> {
                    shapeRenderer.color = Color(0.65f, 0.58f, 0.45f, 1f)
                    shapeRenderer.circle(pos.x, pos.y, 11f)
                    shapeRenderer.color = Color(0.45f, 0.38f, 0.28f, 1f)
                    shapeRenderer.circle(pos.x, pos.y, 6f)
                }
            }
        }

        // Cổng Sandbox bên phải
        shapeRenderer.color = Color(0.25f, 0.15f, 0.35f, 1f)
        shapeRenderer.circle(sandboxNode.x, sandboxNode.y, 26f)
        shapeRenderer.color = Color(0.65f, 0.25f, 0.95f, 1f)
        shapeRenderer.circle(sandboxNode.x, sandboxNode.y, 20f)
        shapeRenderer.color = Color(0.95f, 0.70f, 1f, 1f)
        shapeRenderer.circle(sandboxNode.x, sandboxNode.y, 10f)

        // 4. BẢNG TỔNG SỐ SAO TREO DÂY XÍCH Ở GÓC TRÊN PHẢI
        val signX = Config.WORLD_WIDTH - 245f
        val signY = Config.WORLD_HEIGHT - 102f
        val signW = 185f
        val signH = 54f
        for (cx in listOf(signX + 18f, signX + signW - 26f)) {
            shapeRenderer.color = Color(0.30f, 0.32f, 0.35f, 1f)
            shapeRenderer.rect(cx, signY + signH, 8f, 48f)
            shapeRenderer.color = Color(0.65f, 0.68f, 0.72f, 1f)
            shapeRenderer.rect(cx + 1.5f, signY + signH + 6f, 5f, 12f)
            shapeRenderer.rect(cx + 1.5f, signY + signH + 24f, 5f, 12f)
        }
        shapeRenderer.color = Color(0.48f, 0.46f, 0.42f, 1f)
        shapeRenderer.rect(signX - 4f, signY - 4f, signW + 8f, signH + 8f)
        shapeRenderer.color = Color(0.26f, 0.21f, 0.16f, 1f)
        shapeRenderer.rect(signX, signY, signW, signH)
        shapeRenderer.color = Color(0.34f, 0.27f, 0.20f, 1f)
        shapeRenderer.rect(signX + 4f, signY + 4f, signW - 8f, signH - 8f)

        // Nút Loa & Nhạc ở góc trên trái
        val audioOn = SoundManager.isSoundEnabled
        shapeRenderer.color = if (audioOn) Color(0.45f, 0.42f, 0.36f, 1f) else Color(0.65f, 0.22f, 0.18f, 1f)
        shapeRenderer.circle(78f, Config.WORLD_HEIGHT - 58f, 18f)
        shapeRenderer.circle(122f, Config.WORLD_HEIGHT - 58f, 18f)
        shapeRenderer.color = Color(0.24f, 0.20f, 0.16f, 1f)
        shapeRenderer.circle(78f, Config.WORLD_HEIGHT - 58f, 14f)
        shapeRenderer.circle(122f, Config.WORLD_HEIGHT - 58f, 14f)

        // 5. NÚT QUAY VỀ MAIN MENU, UPGRADES & ENCYCLOPEDIA
        drawMainMenuMapButton(shapeRenderer, 55f, 42f, 145f, 38f, isPressed = (pressedMapButton == "MAIN_MENU"))
        drawUpgradesMapButton(shapeRenderer, 615f, 42f, 150f, 38f, isPressed = (pressedMapButton == "UPGRADES"))
        drawEncyclopediaMapButton(shapeRenderer, 795f, 42f, 165f, 38f, isPressed = (pressedMapButton == "ENCYCLOPEDIA"))

        // 6. VẼ CHỮ COMIC VECTOR KIỂU VICTORY CHO TOÀN BỘ WORLD MAP
        ComicFontRenderer.drawRibbonBannerText(
            sr = shapeRenderer,
            text = "KINGDOM RUSH - LINIREA CAMPAIGN",
            centerX = Config.WORLD_WIDTH / 2f,
            centerY = Config.WORLD_HEIGHT - 54f,
            charHeight = 18f,
            fillColor = Color(0.32f, 0.18f, 0.08f, 1f),
            shadowColor = Color(0.96f, 0.88f, 0.65f, 1f)
        )

        ComicFontRenderer.drawPlaqueText(
            sr = shapeRenderer,
            text = "${SaveManager.getTotalStars()}/60",
            centerX = signX + 72f,
            centerY = signY + signH / 2f,
            charHeight = 20f,
            fillColor = Color(1f, 0.88f, 0.22f, 1f)
        )

        val soundLabel = if (SoundManager.isSoundEnabled) "S" else "X"
        val musicLabel = if (SoundManager.isMusicEnabled) "M" else "X"
        ComicFontRenderer.drawPlaqueText(shapeRenderer, soundLabel, 78f, Config.WORLD_HEIGHT - 58f, 14f, Color.WHITE)
        ComicFontRenderer.drawPlaqueText(shapeRenderer, musicLabel, 122f, Config.WORLD_HEIGHT - 58f, 14f, Color.WHITE)

        val mainColor = if (pressedMapButton == "MAIN_MENU") Color(1f, 0.92f, 0.28f, 1f) else Color.WHITE
        val upgColor = if (pressedMapButton == "UPGRADES") Color(1f, 0.92f, 0.28f, 1f) else Color.WHITE
        val encColor = if (pressedMapButton == "ENCYCLOPEDIA") Color(1f, 0.92f, 0.28f, 1f) else Color.WHITE

        ComicFontRenderer.drawPlaqueText(shapeRenderer, "MAIN MENU", 55f + 72.5f, 42f + 19f, 13f, mainColor)
        ComicFontRenderer.drawPlaqueText(shapeRenderer, "UPGRADES", 615f + 75f, 42f + 19f, 13f, upgColor)
        ComicFontRenderer.drawPlaqueText(shapeRenderer, "ENCYCLOPEDIA", 795f + 82.5f, 42f + 19f, 12.5f, encColor)
        ComicFontRenderer.drawPlaqueText(shapeRenderer, "SANDBOX", sandboxNode.x, sandboxNode.y + 38f, 13f, Color.WHITE)

        val availStars = UpgradeManager.getAvailableStars()
        if (availStars > 0) {
            ComicFontRenderer.drawPlaqueText(shapeRenderer, "+$availStars", 734f, 111f, 14f, Color.YELLOW)
        }
        shapeRenderer.end()

        for (i in campaignNodes.indices) {
            val lvlNum = i + 1
            val pos = campaignNodes[i].second
            val stars = SaveManager.getLevelStars(lvlNum)
            if (stars > 0) {
                drawStarsOnMap(shapeRenderer, pos.x, pos.y + 42f, stars)
            }
        }

        drawStarDirect(shapeRenderer, signX + signW - 30f, signY + signH / 2f, 15f, true)

        if (availStars > 0) {
            drawStarDirect(shapeRenderer, 762f, 111f, 7.5f, true)
        }

        if (isPopupOpen) {
            MissionPopupRenderer.render(
                sr = shapeRenderer,
                batch = batch,
                titleFont = titleFont,
                font = font,
                smallFont = smallFont,
                selectedLevelData = selectedLevelData,
                selectedLevelName = selectedLevelName,
                selectedLevelNumber = selectedLevelNumber,
                currentDifficulty = currentDifficulty,
                currentGameMode = currentGameMode
            )
        }

        if (isUpgradesOpen) {
            UpgradeRenderer.render(shapeRenderer, batch, titleFont, font, smallFont)
        }

        if (isEncyclopediaOpen) {
            EncyclopediaRenderer.render(shapeRenderer, batch, titleFont, font, smallFont)
        }
    }

    private fun drawMainMenuMapButton(sr: ShapeRenderer, x: Float, y: Float, w: Float, h: Float, isPressed: Boolean) {
        val cx = x + w / 2f
        val iconY = y + h + 18f

        if (isPressed) {
            sr.color = Color(0.98f, 0.88f, 0.25f, 1f)
            sr.circle(cx, iconY, 22f)
            sr.rect(x - 5f, y - 5f, w + 10f, h + 10f)
        }

        sr.color = Color(0.24f, 0.18f, 0.12f, 1f)
        sr.circle(cx, iconY, 19f)
        sr.color = Color(0.76f, 0.62f, 0.32f, 1f)
        sr.circle(cx, iconY, 16f)
        sr.color = Color(0.32f, 0.24f, 0.16f, 1f)
        sr.circle(cx, iconY, 12.5f)
        sr.color = Color(0.98f, 0.84f, 0.22f, 1f)
        sr.rect(cx - 2f, iconY - 3.5f, 10f, 7f)
        sr.triangle(cx - 10f, iconY, cx - 1f, iconY + 8.5f, cx - 1f, iconY - 8.5f)

        sr.color = if (isPressed) Color(0.98f, 0.88f, 0.25f, 1f) else Color(0.55f, 0.52f, 0.44f, 1f)
        sr.rect(x - 3f, y - 3f, w + 6f, h + 6f)
        sr.color = Color(0.24f, 0.20f, 0.16f, 1f)
        sr.rect(x, y, w, h)
        sr.color = Color(0.32f, 0.27f, 0.21f, 1f)
        sr.rect(x + 4f, y + 4f, w - 8f, h - 8f)
    }

    private fun drawCompletedShieldBanner(
        sr: ShapeRenderer,
        x: Float,
        y: Float,
        isHeroicCompleted: Boolean,
        isIronCompleted: Boolean
    ) {
        sr.color = Color(0.62f, 0.50f, 0.34f, 1f)
        sr.circle(x, y - 4f, 8.5f)
        sr.color = Color(0.45f, 0.34f, 0.22f, 1f)
        sr.circle(x, y - 4f, 5f)

        if (isHeroicCompleted) {
            sr.color = Color(0.78f, 0.55f, 0.08f, 1f)
            sr.triangle(x - 11f, y + 19f, x - 32f, y + 30f, x - 25f, y + 12f)
            sr.triangle(x + 11f, y + 19f, x + 32f, y + 30f, x + 25f, y + 12f)

            sr.color = Color(0.98f, 0.84f, 0.18f, 1f)
            sr.triangle(x - 12f, y + 18f, x - 30f, y + 28f, x - 24f, y + 13f)
            sr.triangle(x - 12f, y + 12f, x - 27f, y + 17f, x - 20f, y + 6f)
            sr.triangle(x - 11f, y + 7f, x - 22f, y + 9f, x - 16f, y + 1f)

            sr.triangle(x + 12f, y + 18f, x + 30f, y + 28f, x + 24f, y + 13f)
            sr.triangle(x + 12f, y + 12f, x + 27f, y + 17f, x + 20f, y + 6f)
            sr.triangle(x + 11f, y + 7f, x + 22f, y + 9f, x + 16f, y + 1f)
        }

        sr.color = Color(0.35f, 0.22f, 0.12f, 1f)
        sr.rect(x - 2.5f, y - 5f, 5f, 36f)

        sr.color = Color(0.86f, 0.89f, 0.94f, 1f)
        sr.rect(x - 14f, y + 6f, 28f, 22f)
        sr.triangle(x - 14f, y + 6f, x + 14f, y + 6f, x, y - 6f)

        if (isIronCompleted) {
            sr.color = Color(0.28f, 0.62f, 0.86f, 1f)
            sr.rect(x - 11f, y + 8f, 22f, 18f)
            sr.triangle(x - 11f, y + 8f, x + 11f, y + 8f, x, y - 2f)

            sr.color = Color(0.65f, 0.86f, 0.98f, 1f)
            sr.rect(x - 2f, y + 3f, 4f, 19f)
            sr.rect(x - 7f, y + 14f, 14f, 4f)
        } else {
            sr.color = Color(0.46f, 0.40f, 0.34f, 1f)
            sr.rect(x - 11f, y + 8f, 22f, 18f)
            sr.triangle(x - 11f, y + 8f, x + 11f, y + 8f, x, y - 2f)

            sr.color = Color(0.35f, 0.30f, 0.25f, 1f)
            sr.circle(x, y + 16f, 4.5f)
        }

        sr.color = Color(0.92f, 0.75f, 0.20f, 1f)
        sr.rect(x - 16f, y + 28f, 32f, 4f)
    }

    private fun drawNextMissionRedBanner(sr: ShapeRenderer, x: Float, y: Float) {
        sr.color = Color(0.62f, 0.50f, 0.34f, 1f)
        sr.circle(x, y - 4f, 9f)

        sr.color = Color(0.35f, 0.22f, 0.12f, 1f)
        sr.rect(x - 2.5f, y - 4f, 5f, 38f)

        sr.color = Color(0.95f, 0.78f, 0.20f, 1f)
        sr.rect(x - 15f, y + 8f, 30f, 24f)
        sr.triangle(x - 15f, y + 8f, x + 15f, y + 8f, x, y - 5f)

        sr.color = Color(0.85f, 0.18f, 0.14f, 1f)
        sr.rect(x - 12f, y + 10f, 24f, 20f)
        sr.triangle(x - 12f, y + 10f, x + 12f, y + 10f, x, y - 1f)

        sr.color = Color(0.98f, 0.90f, 0.65f, 1f)
        sr.rectLine(x - 7f, y + 11f, x + 7f, y + 25f, 2.6f)
        sr.rectLine(x - 7f, y + 25f, x + 7f, y + 11f, 2.6f)
    }

    private fun drawUpgradesMapButton(sr: ShapeRenderer, x: Float, y: Float, w: Float, h: Float, isPressed: Boolean) {
        val cx = x + w / 2f
        if (isPressed) {
            sr.color = Color(0.98f, 0.88f, 0.25f, 1f)
            sr.rect(x - 5f, y - 5f, w + 10f, h + 10f)
            sr.rect(cx - 31f, y + h + 21f, 60f, 34f)
        }

        sr.color = Color(0.45f, 0.28f, 0.12f, 1f)
        sr.rectLine(cx - 4f, y + h - 2f, cx + 10f, y + h + 44f, 9f)
        sr.color = Color(0.62f, 0.40f, 0.18f, 1f)
        sr.rectLine(cx - 5f, y + h, cx + 9f, y + h + 44f, 4.5f)

        sr.color = Color(0.30f, 0.32f, 0.36f, 1f)
        sr.rect(cx - 28f, y + h + 24f, 54f, 28f)
        sr.color = Color(0.55f, 0.58f, 0.64f, 1f)
        sr.rect(cx - 25f, y + h + 27f, 48f, 22f)
        sr.color = Color(0.75f, 0.78f, 0.84f, 1f)
        sr.rect(cx - 23f, y + h + 42f, 44f, 5f)

        sr.color = Color(0.42f, 0.46f, 0.52f, 1f)
        sr.rect(cx + 16f, y + h - 2f, 8f, 20f)
        sr.color = Color(0.68f, 0.72f, 0.78f, 1f)
        sr.circle(cx + 20f, y + h + 18f, 9f)

        sr.color = if (isPressed) Color(0.98f, 0.88f, 0.25f, 1f) else Color(0.55f, 0.52f, 0.44f, 1f)
        sr.rect(x - 3f, y - 3f, w + 6f, h + 6f)
        sr.color = Color(0.24f, 0.20f, 0.16f, 1f)
        sr.rect(x, y, w, h)
        sr.color = Color(0.32f, 0.27f, 0.21f, 1f)
        sr.rect(x + 4f, y + 4f, w - 8f, h - 8f)
    }

    private fun drawEncyclopediaMapButton(sr: ShapeRenderer, x: Float, y: Float, w: Float, h: Float, isPressed: Boolean) {
        val cx = x + w / 2f
        val bookY = y + h - 2f

        if (isPressed) {
            sr.color = Color(0.98f, 0.88f, 0.25f, 1f)
            sr.rect(x - 5f, y - 5f, w + 10f, h + 10f)
            sr.rect(cx - 45f, bookY - 3f, 90f, 54f)
        }

        sr.color = Color(0.42f, 0.25f, 0.10f, 1f)
        sr.rect(cx - 42f, bookY, 84f, 48f)
        sr.color = Color(0.28f, 0.16f, 0.06f, 1f)
        sr.rect(cx - 3f, bookY - 2f, 6f, 50f)

        sr.color = Color(0.95f, 0.85f, 0.45f, 1f)
        sr.rect(cx - 38f, bookY + 4f, 35f, 41f)
        sr.rect(cx + 3f, bookY + 4f, 35f, 41f)
        sr.color = Color(1f, 0.94f, 0.65f, 1f)
        sr.rect(cx - 35f, bookY + 7f, 30f, 36f)
        sr.rect(cx + 5f, bookY + 7f, 30f, 36f)

        sr.color = Color(0.48f, 0.36f, 0.16f, 1f)
        for (ly in listOf(13f, 21f, 29f, 36f)) {
            sr.rect(cx - 31f, bookY + ly, 22f, 2.5f)
            sr.rect(cx + 9f, bookY + ly, 22f, 2.5f)
        }

        sr.color = if (isPressed) Color(0.98f, 0.88f, 0.25f, 1f) else Color(0.55f, 0.52f, 0.44f, 1f)
        sr.rect(x - 3f, y - 3f, w + 6f, h + 6f)
        sr.color = Color(0.24f, 0.20f, 0.16f, 1f)
        sr.rect(x, y, w, h)
        sr.color = Color(0.32f, 0.27f, 0.21f, 1f)
        sr.rect(x + 4f, y + 4f, w - 8f, h - 8f)
    }

    private fun drawMapTerrainDecorations(sr: ShapeRenderer) {
        sr.color = Color(0.74f, 0.62f, 0.42f, 1f)
        val mountains = listOf(
            Vector2(450f, 480f), Vector2(490f, 510f), Vector2(530f, 475f),
            Vector2(680f, 590f), Vector2(730f, 610f), Vector2(780f, 575f),
            Vector2(840f, 490f), Vector2(890f, 520f), Vector2(670f, 310f)
        )
        for (m in mountains) {
            sr.triangle(m.x - 35f, m.y - 20f, m.x + 35f, m.y - 20f, m.x, m.y + 28f)
        }
    }

    private fun drawStarDirect(sr: ShapeRenderer, centerX: Float, centerY: Float, outerRadius: Float, isFilled: Boolean) {
        val innerRadius = outerRadius * 0.45f
        val points = mutableListOf<Vector2>()
        for (i in 0 until 10) {
            val angle = Math.toRadians((90.0 + i * 36.0))
            val r = if (i % 2 == 0) outerRadius else innerRadius
            val px = centerX + (r * Math.cos(angle)).toFloat()
            val py = centerY + (r * Math.sin(angle)).toFloat()
            points.add(Vector2(px, py))
        }

        sr.begin(ShapeRenderer.ShapeType.Filled)
        sr.color = Color(0.12f, 0.10f, 0.08f, 1f)
        for (i in 0 until 10) {
            val next = (i + 1) % 10
            sr.triangle(centerX, centerY - 2f, points[i].x, points[i].y - 2f, points[next].x, points[next].y - 2f)
        }

        val bodyColor = if (isFilled) Color(0.98f, 0.82f, 0.15f, 1f) else Color(0.38f, 0.38f, 0.42f, 1f)
        sr.color = bodyColor
        for (i in 0 until 10) {
            val next = (i + 1) % 10
            sr.triangle(centerX, centerY, points[i].x, points[i].y, points[next].x, points[next].y)
        }

        if (isFilled) {
            sr.color = Color(1f, 0.95f, 0.65f, 1f)
            for (i in listOf(0, 8)) {
                val next = (i + 1) % 10
                sr.triangle(centerX, centerY, points[i].x, points[i].y, points[next].x, points[next].y)
            }
        }
        sr.end()

        sr.begin(ShapeRenderer.ShapeType.Line)
        sr.color = if (isFilled) Color(0.85f, 0.60f, 0.10f, 1f) else Color(0.25f, 0.25f, 0.28f, 1f)
        for (i in 0 until 10) {
            val next = (i + 1) % 10
            sr.line(points[i].x, points[i].y, points[next].x, points[next].y)
        }
        sr.end()
    }

    private fun drawDottedLine(sr: ShapeRenderer, from: Vector2, to: Vector2, color: Color, width: Float) {
        sr.color = color
        val dist = from.dst(to)
        val step = 14f
        var current = 0f
        val dir = Vector2(to).sub(from).nor()

        while (current < dist) {
            val startP = Vector2(from).add(Vector2(dir).scl(current))
            val segmentEnd = (current + 7f).coerceAtMost(dist)
            val endP = Vector2(from).add(Vector2(dir).scl(segmentEnd))
            sr.rectLine(startP, endP, width)
            current += step
        }
    }

    private fun drawStarsOnMap(sr: ShapeRenderer, x: Float, y: Float, count: Int) {
        val offsets = when (count) {
            1 -> listOf(0f)
            2 -> listOf(-8f, 8f)
            else -> listOf(-14f, 0f, 14f)
        }
        for (off in offsets) {
            drawStarDirect(sr, x + off, y, 6.5f, true)
        }
    }
}
