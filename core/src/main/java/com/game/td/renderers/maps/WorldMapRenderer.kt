package com.game.td.renderers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Align
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
        currentGameMode: GameMode
    ) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        // 1. NỀN CUỘN BẢN ĐỒ DA DÊ CỔ ĐIỂN
        shapeRenderer.color = Color(0.84f, 0.73f, 0.52f, 1f)
        shapeRenderer.rect(25f, 25f, Config.WORLD_WIDTH - 50f, Config.WORLD_HEIGHT - 50f)

        // Hai trục cuộn giấy ở 2 mép trái phải
        shapeRenderer.color = Color(0.72f, 0.60f, 0.40f, 1f)
        shapeRenderer.rect(20f, 18f, 24f, Config.WORLD_HEIGHT - 36f)
        shapeRenderer.rect(Config.WORLD_WIDTH - 44f, 18f, 24f, Config.WORLD_HEIGHT - 36f)

        shapeRenderer.color = Color(0.58f, 0.46f, 0.30f, 1f)
        shapeRenderer.rect(25f, 25f, Config.WORLD_WIDTH - 50f, 6f)
        shapeRenderer.rect(25f, Config.WORLD_HEIGHT - 31f, Config.WORLD_WIDTH - 50f, 6f)

        // Họa tiết núi non trang trí mờ trên bản đồ cổ
        drawMapTerrainDecorations(shapeRenderer)

        // 2. ĐƯỜNG HÀNH QUÂN NÉT ĐỨT GIỮA CÁC MÀN CHƠI
        for (i in 0 until campaignNodes.size - 1) {
            val isPathUnlocked = (i + 1 < nextLevelToPlay)
            val lineColor = if (isPathUnlocked) Color(0.32f, 0.18f, 0.08f, 1f) else Color(0.60f, 0.52f, 0.40f, 0.35f)
            drawDottedLine(shapeRenderer, campaignNodes[i].second, campaignNodes[i + 1].second, lineColor, 5f)
        }

        // 3. VẼ CỜ KHIÊN CHO TỪNG MỐC MÀN CHƠI (HEROIC = CÁNH VÀNG, IRON = KHIÊN XANH)
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

        // Cổng Sandbox góc dưới phải
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

        // 5. NÚT UPGRADES & NÚT ENCYCLOPEDIA
        drawUpgradesMapButton(shapeRenderer, 615f, 42f, 150f, 38f)
        drawEncyclopediaMapButton(shapeRenderer, 795f, 42f, 165f, 38f)
        shapeRenderer.end()

        // Vẽ các ngôi sao nhỏ trên đỉnh cờ của từng màn chơi đã qua
        for (i in campaignNodes.indices) {
            val lvlNum = i + 1
            val pos = campaignNodes[i].second
            val stars = SaveManager.getLevelStars(lvlNum)
            if (stars > 0) {
                drawStarsOnMap(shapeRenderer, pos.x, pos.y + 42f, stars)
            }
        }

        // Vẽ ngôi sao lớn trong bảng sao góc trên phải
        drawStarDirect(shapeRenderer, signX + signW - 30f, signY + signH / 2f, 15f, true)

        // Vẽ ngôi sao Vector cạnh số sao dư ở nút UPGRADES
        val availStars = UpgradeManager.getAvailableStars()
        val availText = "+$availStars"
        layout.setText(smallFont, availText)
        val badgeTextX = 724f
        val badgeTextY = 116f
        if (availStars > 0) {
            drawStarDirect(shapeRenderer, badgeTextX + layout.width + 11f, badgeTextY - 5f, 7.5f, true)
        }

        batch.begin()
        font.color = Color(0.32f, 0.18f, 0.08f, 1f)
        font.draw(batch, "KINGDOM RUSH - LINIREA CAMPAIGN", 0f, Config.WORLD_HEIGHT - 45f, Config.WORLD_WIDTH, Align.center, false)

        GameUIRenderer.drawOutlinedText(
            batch, titleFont,
            "${SaveManager.getTotalStars()}/60",
            signX + 20f, signY + 36f,
            Color(1f, 0.88f, 0.22f, 1f)
        )

        val soundLabel = if (SoundManager.isSoundEnabled) "S" else "X"
        val musicLabel = if (SoundManager.isMusicEnabled) "M" else "X"
        GameUIRenderer.drawOutlinedText(batch, smallFont, soundLabel, 73f, Config.WORLD_HEIGHT - 52f, Color.WHITE)
        GameUIRenderer.drawOutlinedText(batch, smallFont, musicLabel, 116f, Config.WORLD_HEIGHT - 52f, Color.WHITE)

        GameUIRenderer.drawBadgeCenteredText(batch, smallFont, "UPGRADES", 615f, 42f, 150f, 38f, Color.WHITE)
        GameUIRenderer.drawBadgeCenteredText(batch, smallFont, "ENCYCLOPEDIA", 795f, 42f, 165f, 38f, Color.WHITE)

        if (availStars > 0) {
            GameUIRenderer.drawOutlinedText(batch, smallFont, availText, badgeTextX, badgeTextY, Color.YELLOW)
        }

        smallFont.color = Color.WHITE
        smallFont.draw(batch, "SANDBOX", sandboxNode.x - 45f, sandboxNode.y + 44f)
        batch.end()

        if (isPopupOpen) {
            renderMissionPopup(
                shapeRenderer = shapeRenderer,
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

    private fun drawUpgradesMapButton(sr: ShapeRenderer, x: Float, y: Float, w: Float, h: Float) {
        val cx = x + w / 2f
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

        sr.color = Color(0.55f, 0.52f, 0.44f, 1f)
        sr.rect(x - 3f, y - 3f, w + 6f, h + 6f)
        sr.color = Color(0.24f, 0.20f, 0.16f, 1f)
        sr.rect(x, y, w, h)
        sr.color = Color(0.32f, 0.27f, 0.21f, 1f)
        sr.rect(x + 4f, y + 4f, w - 8f, h - 8f)
    }

    private fun drawEncyclopediaMapButton(sr: ShapeRenderer, x: Float, y: Float, w: Float, h: Float) {
        val cx = x + w / 2f
        val bookY = y + h - 2f

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

        sr.color = Color(0.55f, 0.52f, 0.44f, 1f)
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

    private fun renderMissionPopup(
        shapeRenderer: ShapeRenderer,
        batch: SpriteBatch,
        titleFont: BitmapFont,
        font: BitmapFont,
        smallFont: BitmapFont,
        selectedLevelData: LevelData?,
        selectedLevelName: String,
        selectedLevelNumber: Int,
        currentDifficulty: GameDifficulty,
        currentGameMode: GameMode
    ) {
        val cX = Config.WORLD_WIDTH / 2f
        val cY = Config.WORLD_HEIGHT / 2f

        val boxX = cX - 235f
        val boxY = cY - 70f
        val boxW = 160f
        val boxH = 150f

        val headerW = 340f
        val headerH = 40f
        val headerX = cX - headerW / 2f
        val headerY = cY + 145f

        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color(0f, 0f, 0f, 0.65f)
        shapeRenderer.rect(0f, 0f, Config.WORLD_WIDTH, Config.WORLD_HEIGHT)

        shapeRenderer.color = Color(0.18f, 0.15f, 0.12f, 0.98f)
        shapeRenderer.rect(cX - 260f, cY - 170f, 520f, 355f)

        shapeRenderer.color = Color(0.78f, 0.65f, 0.38f, 1f)
        shapeRenderer.rect(headerX, headerY, headerW, headerH)

        shapeRenderer.color = Color(0.24f, 0.23f, 0.25f, 1f)
        shapeRenderer.rect(boxX, boxY, boxW, boxH)

        selectedLevelData?.let { level ->
            val allPaths = level.allPaths

            fun mapToPreview(worldPos: Vector2): Vector2 {
                val px = boxX + (worldPos.x / Config.WORLD_WIDTH) * boxW
                val py = boxY + (worldPos.y / Config.WORLD_HEIGHT) * boxH
                return Vector2(px, py)
            }

            shapeRenderer.color = Color(0.35f, 0.34f, 0.36f, 1f)
            for (path in allPaths) {
                for (i in 0 until path.size - 1) {
                    shapeRenderer.rectLine(mapToPreview(path[i]), mapToPreview(path[i + 1]), 10f)
                }
                for (w in path) shapeRenderer.circle(mapToPreview(w).x, mapToPreview(w).y, 5f)
            }

            shapeRenderer.color = Color(0.65f, 0.62f, 0.55f, 1f)
            for (path in allPaths) {
                for (i in 0 until path.size - 1) {
                    shapeRenderer.rectLine(mapToPreview(path[i]), mapToPreview(path[i + 1]), 7f)
                }
                for (w in path) shapeRenderer.circle(mapToPreview(w).x, mapToPreview(w).y, 3.5f)
            }

            for (spot in level.towerSpots) {
                val sp = mapToPreview(spot)
                shapeRenderer.color = Color(0.48f, 0.45f, 0.38f, 1f)
                shapeRenderer.circle(sp.x, sp.y, 5f)
                shapeRenderer.color = Color(0.68f, 0.62f, 0.50f, 1f)
                shapeRenderer.circle(sp.x, sp.y, 3.5f)
            }
        }

        val diffBtnX = cX - 230f
        val diffBtnY = cY - 110f
        val diffBtnW = 150f
        val diffBtnH = 32f

        if (currentGameMode == GameMode.CAMPAIGN) {
            val diffBg = when (currentDifficulty) {
                GameDifficulty.CASUAL -> Color(0.20f, 0.65f, 0.25f, 1f)
                GameDifficulty.NORMAL -> Color(0.85f, 0.58f, 0.15f, 1f)
                GameDifficulty.VETERAN -> Color(0.80f, 0.20f, 0.15f, 1f)
            }
            shapeRenderer.color = diffBg
            shapeRenderer.rect(diffBtnX, diffBtnY, diffBtnW, diffBtnH)
        }

        val levelStars = SaveManager.getLevelStars(selectedLevelNumber)
        val isChallengeUnlocked = levelStars >= 3

        shapeRenderer.color = if (currentGameMode == GameMode.CAMPAIGN) Color.GOLD else Color.DARK_GRAY
        shapeRenderer.circle(cX - 180f, cY - 145f, 20f)
        shapeRenderer.color = Color(0.2f, 0.5f, 0.85f, 1f)
        shapeRenderer.circle(cX - 180f, cY - 145f, 16f)

        shapeRenderer.color = if (currentGameMode == GameMode.HEROIC) Color.GOLD else Color.DARK_GRAY
        shapeRenderer.circle(cX - 120f, cY - 145f, 20f)
        shapeRenderer.color = if (isChallengeUnlocked) Color(0.85f, 0.35f, 0.15f, 1f) else Color(0.3f, 0.3f, 0.35f, 1f)
        shapeRenderer.circle(cX - 120f, cY - 145f, 16f)

        shapeRenderer.color = if (currentGameMode == GameMode.IRON) Color.GOLD else Color.DARK_GRAY
        shapeRenderer.circle(cX - 60f, cY - 145f, 20f)
        shapeRenderer.color = if (isChallengeUnlocked) Color(0.55f, 0.58f, 0.62f, 1f) else Color(0.3f, 0.3f, 0.35f, 1f)
        shapeRenderer.circle(cX - 60f, cY - 145f, 16f)

        val battleBtnX = cX + 110f
        val battleBtnY = cY - 150f
        val battleBtnW = 130f
        val battleBtnH = 48f

        shapeRenderer.color = Color(0.20f, 0.45f, 0.75f, 1f)
        shapeRenderer.rect(battleBtnX, battleBtnY, battleBtnW, battleBtnH)

        shapeRenderer.color = Color(0.75f, 0.20f, 0.15f, 1f)
        shapeRenderer.circle(cX + 245f, cY + 165f, 16f)
        shapeRenderer.end()

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color.GOLD
        shapeRenderer.rect(cX - 260f, cY - 170f, 520f, 355f)
        shapeRenderer.rect(headerX, headerY, headerW, headerH)
        shapeRenderer.rect(boxX, boxY, boxW, boxH)
        shapeRenderer.rect(battleBtnX, battleBtnY, battleBtnW, battleBtnH)
        shapeRenderer.circle(cX + 245f, cY + 165f, 16f)
        shapeRenderer.end()

        batch.begin()
        titleFont.color = Color(0.20f, 0.12f, 0.05f, 1f)
        val titleTargetY = headerY + (headerH + titleFont.capHeight) / 2f
        titleFont.draw(batch, selectedLevelName, headerX, titleTargetY, headerW, Align.center, false)

        titleFont.color = Color.WHITE
        titleFont.draw(batch, "X", cX + 245f - 8f, cY + 165f + titleFont.capHeight / 2f)

        if (currentGameMode == GameMode.CAMPAIGN) {
            smallFont.color = Color.WHITE
            val diffTargetY = diffBtnY + (diffBtnH + smallFont.capHeight) / 2f
            smallFont.draw(batch, "DIFF: ${currentDifficulty.name}", diffBtnX, diffTargetY, diffBtnW, Align.center, false)
        }

        font.color = Color.WHITE
        val modeIconY = (cY - 145f) + font.capHeight / 2f
        font.draw(batch, "C", cX - 180f - 20f, modeIconY, 40f, Align.center, false)
        font.draw(batch, if (isChallengeUnlocked) "H" else "X", cX - 120f - 20f, modeIconY, 40f, Align.center, false)
        font.draw(batch, if (isChallengeUnlocked) "I" else "X", cX - 60f - 20f, modeIconY, 40f, Align.center, false)

        font.color = Color(1f, 0.85f, 0.2f, 1f)
        val modeTitle = when (currentGameMode) {
            GameMode.CAMPAIGN -> "CAMPAIGN"
            GameMode.HEROIC -> "HEROIC CHALLENGE (1 LIFE)"
            GameMode.IRON -> "IRON CHALLENGE (1 LIFE)"
        }
        font.draw(batch, modeTitle, cX - 55f, cY + 124f)

        smallFont.color = Color(0.96f, 0.96f, 0.92f, 1f)
        when (currentGameMode) {
            GameMode.CAMPAIGN -> {
                smallFont.draw(batch, "Classic multi-wave invasion.", cX - 55f, cY + 84f)
                smallFont.draw(batch, "Hold the path with standard rules.", cX - 55f, cY + 56f)
                val stars = SaveManager.getLevelStars(selectedLevelNumber)
                smallFont.color = Color(1f, 0.92f, 0.25f, 1f)
                smallFont.draw(batch, "RECORD: $stars / 3 STARS", cX - 55f, cY + 20f)
            }
            GameMode.HEROIC -> {
                smallFont.draw(batch, "Elite waves. Only 1 life point!", cX - 55f, cY + 84f)
                smallFont.draw(batch, "Victory rewards +1 Extra Star.", cX - 55f, cY + 56f)
                val isDone = SaveManager.isHeroicCompleted(selectedLevelNumber)
                smallFont.color = if (isDone) Color(0.3f, 0.95f, 0.4f, 1f) else Color(1f, 0.92f, 0.25f, 1f)
                smallFont.draw(batch, if (isDone) "COMPLETED (+1 STAR)" else "NOT COMPLETED", cX - 55f, cY + 20f)
            }
            GameMode.IRON -> {
                smallFont.draw(batch, "Massive wave! Only 1 life point!", cX - 55f, cY + 84f)
                smallFont.draw(batch, "Victory rewards +1 Extra Star.", cX - 55f, cY + 56f)
                val isDone = SaveManager.isIronCompleted(selectedLevelNumber)
                smallFont.color = if (isDone) Color(0.3f, 0.95f, 0.4f, 1f) else Color(1f, 0.92f, 0.25f, 1f)
                smallFont.draw(batch, if (isDone) "COMPLETED (+1 STAR)" else "NOT COMPLETED", cX - 55f, cY + 20f)
            }
        }

        val battleTargetY = battleBtnY + (battleBtnH + font.capHeight) / 2f
        font.color = Color.WHITE
        font.draw(batch, "TO BATTLE!", battleBtnX, battleTargetY, battleBtnW, Align.center, false)
        batch.end()
    }
}
