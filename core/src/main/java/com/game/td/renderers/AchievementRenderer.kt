package com.game.td.renderers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.game.td.Config
import com.game.td.managers.AchievementId
import com.game.td.managers.AchievementManager
import com.game.td.managers.SoundManager

object AchievementRenderer {

    var currentPage: Int = 1
    var pressedButton: String? = null

    fun open() {
        AchievementManager.syncWithSaveProgress(triggerToast = false)
        currentPage = 1
        pressedButton = null
    }

    private fun getButtonAt(x: Float, y: Float): String? {
        val boardX = 90f
        val boardY = 40f
        val boardW = 1100f

        val closeX = boardX + boardW - 155f
        val closeY = boardY + 24f
        if (x in closeX..(closeX + 125f) && y in closeY..(closeY + 46f)) {
            return "CLOSE"
        }

        val pageStartX = 225f
        val pageY = boardY + 20f
        val pageBtnSize = 42f
        val pageGap = 56f
        for (p in 1..4) {
            val px = pageStartX + (p - 1) * pageGap
            if (x in px..(px + pageBtnSize) && y in pageY..(pageY + pageBtnSize)) {
                return "PAGE_$p"
            }
        }
        return null
    }

    fun handleTouchDown(x: Float, y: Float) {
        pressedButton = getButtonAt(x, y)
    }

    fun handleTouchDragged(x: Float, y: Float) {
        if (pressedButton != null) {
            pressedButton = getButtonAt(x, y)
        }
    }

    fun handleTouchUp(x: Float, y: Float): Boolean {
        val boardX = 90f
        val boardY = 40f
        val boardW = 1100f
        val boardH = 630f

        val activeBtn = pressedButton
        pressedButton = null

        val releasedBtn = getButtonAt(x, y)
        if (activeBtn != null && activeBtn == releasedBtn) {
            if (activeBtn == "CLOSE") {
                SoundManager.playClick()
                return false
            }
            if (activeBtn.startsWith("PAGE_")) {
                val p = activeBtn.removePrefix("PAGE_").toIntOrNull() ?: 1
                if (currentPage != p) {
                    SoundManager.playClick()
                    currentPage = p
                }
                return true
            }
        }

        if (x !in boardX..(boardX + boardW) || y !in boardY..(boardY + boardH)) {
            SoundManager.playClick()
            return false
        }

        return true
    }

    fun handleTouch(x: Float, y: Float): Boolean {
        handleTouchDown(x, y)
        return handleTouchUp(x, y)
    }

    // =========================================================================
    // 1. VẼ NÚT CÚP BẠC "ACHIEVEMENTS" CÂN ĐỐI VỚI CHỮ COMIC VECTOR
    // =========================================================================
    fun renderMapTrophyButton(
        sr: ShapeRenderer,
        batch: SpriteBatch,
        smallFont: BitmapFont,
        isPressed: Boolean = false
    ) {
        val x = 985f
        val y = 42f
        val w = 175f
        val h = 38f
        val cx = x + w / 2f
        val cupBaseY = y + h - 2f

        sr.begin(ShapeRenderer.ShapeType.Filled)

        if (isPressed) {
            sr.color = Color(0.98f, 0.88f, 0.25f, 1f)
            sr.rect(x - 5f, y - 5f, w + 10f, h + 10f)
            sr.rect(cx - 21f, cupBaseY - 1f, 42f, 10f)
            sr.ellipse(cx - 35f, cupBaseY + 13f, 70f, 30f)
            sr.ellipse(cx - 29f, cupBaseY + 35f, 58f, 14f)
        }

        val outline = Color(0.22f, 0.18f, 0.14f, 1f)

        sr.color = outline
        sr.ellipse(cx - 32f, cupBaseY + 15f, 64f, 26f)
        sr.color = Color(0.74f, 0.77f, 0.82f, 1f)
        sr.ellipse(cx - 29f, cupBaseY + 17.5f, 58f, 21f)
        sr.color = Color(0.84f, 0.73f, 0.52f, 1f)
        sr.ellipse(cx - 24f, cupBaseY + 20.5f, 48f, 15f)

        sr.color = outline
        sr.rect(cx - 18f, cupBaseY, 36f, 7f)
        sr.rect(cx - 12f, cupBaseY + 6f, 24f, 5f)
        sr.rect(cx - 6.5f, cupBaseY + 10f, 13f, 9f)

        sr.color = Color(0.55f, 0.58f, 0.64f, 1f)
        sr.rect(cx - 15.5f, cupBaseY + 1.5f, 31f, 4.5f)
        sr.rect(cx - 9.5f, cupBaseY + 6f, 19f, 4f)
        sr.rect(cx - 4.5f, cupBaseY + 10f, 9f, 8f)

        sr.color = Color(0.78f, 0.82f, 0.88f, 1f)
        sr.rect(cx - 12f, cupBaseY + 2f, 14f, 3.5f)
        sr.rect(cx - 3f, cupBaseY + 10f, 4f, 8f)

        sr.color = outline
        sr.arc(cx, cupBaseY + 32f, 21f, 180f, 180f)
        sr.rect(cx - 21f, cupBaseY + 29f, 42f, 13f)

        sr.color = Color(0.58f, 0.61f, 0.67f, 1f)
        sr.arc(cx, cupBaseY + 32f, 18.5f, 180f, 180f)
        sr.rect(cx - 18.5f, cupBaseY + 29f, 37f, 12f)

        sr.color = Color(0.78f, 0.82f, 0.88f, 1f)
        sr.arc(cx - 2f, cupBaseY + 32f, 15.5f, 180f, 180f)
        sr.rect(cx - 16.5f, cupBaseY + 29f, 24f, 11.5f)

        sr.color = Color(0.92f, 0.95f, 0.98f, 1f)
        sr.rect(cx - 12f, cupBaseY + 22f, 5f, 16f)

        sr.color = outline
        sr.ellipse(cx - 23f, cupBaseY + 37f, 46f, 11f)
        sr.color = Color(0.84f, 0.87f, 0.92f, 1f)
        sr.ellipse(cx - 20.5f, cupBaseY + 38.2f, 41f, 8.6f)
        sr.color = Color(0.42f, 0.45f, 0.50f, 1f)
        sr.ellipse(cx - 17f, cupBaseY + 39.5f, 34f, 6f)

        sr.color = if (isPressed) Color(0.98f, 0.88f, 0.25f, 1f) else Color(0.55f, 0.52f, 0.44f, 1f)
        sr.rect(x - 3f, y - 3f, w + 6f, h + 6f)
        sr.color = Color(0.24f, 0.20f, 0.16f, 1f)
        sr.rect(x, y, w, h)
        sr.color = Color(0.32f, 0.27f, 0.21f, 1f)
        sr.rect(x + 4f, y + 4f, w - 8f, h - 8f)

        val textColor = if (isPressed) Color(1f, 0.92f, 0.28f, 1f) else Color.WHITE
        ComicFontRenderer.drawPlaqueText(sr, "ACHIEVEMENTS", x + w / 2f, y + h / 2f, 12.5f, textColor)
        sr.end()
    }

    // =========================================================================
    // 2. VẼ BANNER THÔNG BÁO "ACHIEVEMENT" TRÊN MÀN HÌNH GAMEPLAY
    // =========================================================================
    fun renderInGameToast(
        sr: ShapeRenderer,
        batch: SpriteBatch,
        font: BitmapFont,
        smallFont: BitmapFont
    ) {
        val toastAch = AchievementManager.activeToast ?: return
        val timer = AchievementManager.toastTimer
        val total = AchievementManager.TOAST_TOTAL_DURATION

        val slideProgress = when {
            timer > total - 0.35f -> (total - timer) / 0.35f
            timer < 0.35f -> (timer / 0.35f).coerceIn(0f, 1f)
            else -> 1f
        }

        val bannerW = 390f
        val bannerH = 92f
        val bannerX = Config.WORLD_WIDTH / 2f - bannerW / 2f
        val targetY = Config.WORLD_HEIGHT - 125f
        val hiddenY = Config.WORLD_HEIGHT + 25f
        val bannerY = MathUtils.lerp(hiddenY, targetY, slideProgress)

        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

        sr.begin(ShapeRenderer.ShapeType.Filled)
        sr.color = Color(0f, 0f, 0f, 0.42f)
        sr.rect(bannerX + 6f, bannerY - 6f, bannerW, bannerH)

        val outline = Color(0.14f, 0.11f, 0.08f, 1f)
        sr.color = outline
        sr.rect(bannerX - 4f, bannerY - 4f, bannerW + 8f, bannerH + 8f)
        sr.color = Color(0.68f, 0.69f, 0.66f, 1f)
        sr.rect(bannerX, bannerY, bannerW, bannerH)
        sr.color = Color(0.84f, 0.85f, 0.82f, 1f)
        sr.rect(bannerX + 2f, bannerY + bannerH - 6f, bannerW - 4f, 4f)

        sr.color = outline
        sr.rect(bannerX + 7f, bannerY + 7f, bannerW - 14f, bannerH - 14f)
        sr.color = Color(0.26f, 0.20f, 0.15f, 1f)
        sr.rect(bannerX + 9f, bannerY + 9f, bannerW - 18f, bannerH - 18f)
        sr.color = Color(0.21f, 0.16f, 0.12f, 1f)
        sr.rect(bannerX + 16f, bannerY + 24f, bannerW - 32f, 3f)
        sr.rect(bannerX + 30f, bannerY + 52f, bannerW - 60f, 2.5f)

        val iconBoxX = bannerX + 18f
        val iconBoxY = bannerY + 14f
        val iconBoxSize = 64f
        sr.color = Color(0.36f, 0.30f, 0.24f, 1f)
        sr.rect(iconBoxX - 2f, iconBoxY - 2f, iconBoxSize + 4f, iconBoxSize + 4f)
        sr.color = Color(0.13f, 0.10f, 0.08f, 1f)
        sr.rect(iconBoxX, iconBoxY, iconBoxSize, iconBoxSize)

        drawAchievementIcon(
            sr = sr,
            id = toastAch,
            cx = iconBoxX + iconBoxSize / 2f,
            cy = iconBoxY + iconBoxSize / 2f,
            size = iconBoxSize,
            unlocked = true
        )

        val cx = bannerX + bannerW / 2f
        val ribY = bannerY + bannerH - 14f
        sr.color = outline
        sr.rect(cx - 142f, ribY - 8f, 44f, 26f)
        sr.rect(cx + 98f, ribY - 8f, 44f, 26f)
        sr.color = Color(0.74f, 0.58f, 0.24f, 1f)
        sr.rect(cx - 140f, ribY - 6f, 40f, 22f)
        sr.rect(cx + 100f, ribY - 6f, 40f, 22f)
        sr.color = Color(0.26f, 0.20f, 0.15f, 1f)
        sr.triangle(cx - 141f, ribY + 5f, cx - 141f, ribY - 7f, cx - 128f, ribY - 1f)
        sr.triangle(cx + 141f, ribY + 5f, cx + 141f, ribY - 7f, cx + 128f, ribY - 1f)

        sr.color = outline
        sr.rect(cx - 112f, ribY - 2f, 224f, 32f)
        sr.color = Color(0.88f, 0.73f, 0.34f, 1f)
        sr.rect(cx - 109f, ribY + 1f, 218f, 26f)
        sr.color = Color(0.96f, 0.84f, 0.45f, 1f)
        sr.rect(cx - 105f, ribY + 4f, 210f, 20f)

        ComicFontRenderer.drawRibbonBannerText(sr, "ACHIEVEMENT", cx, ribY + 14f, 17f)
        ComicFontRenderer.drawPlaqueText(
            sr = sr,
            text = toastAch.title,
            centerX = iconBoxX + iconBoxSize + (bannerW - 95f) / 2f,
            centerY = bannerY + 42f,
            charHeight = 16f,
            fillColor = Color(0.98f, 0.88f, 0.58f, 1f)
        )
        sr.end()
    }

    // =========================================================================
    // 3. VẼ BẢNG GỖ ACHIEVEMENTS 4 TRANG VỚI BĂNG RÔN COMIC VECTOR & CHỮ RÕ NÉT
    // =========================================================================
    fun render(
        shapeRenderer: ShapeRenderer,
        batch: SpriteBatch,
        titleFont: BitmapFont,
        font: BitmapFont,
        smallFont: BitmapFont
    ) {
        val boardX = 90f
        val boardY = 40f
        val boardW = 1100f
        val boardH = 630f
        val boardCenterX = boardX + boardW / 2f
        val boardTopY = boardY + boardH

        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

        drawWoodenBoardFrame(shapeRenderer, boardX, boardY, boardW, boardH)

        val pageAchievements = AchievementManager.getAchievementsForPage(currentPage)
        val col0X = 148f
        val col1X = 652f
        val cardW = 480f
        val cardH = 61f
        val topRowY = 530f
        val rowStep = 67f

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        ComicFontRenderer.drawRibbonBannerText(
            sr = shapeRenderer,
            text = "ACHIEVEMENTS",
            centerX = boardCenterX,
            centerY = boardTopY - 4f,
            charHeight = 30f
        )

        for (i in pageAchievements.indices) {
            val ach = pageAchievements[i]
            val col = i % 2
            val row = i / 2
            val cx = if (col == 0) col0X else col1X
            val cy = topRowY - row * rowStep
            val unlocked = AchievementManager.isUnlocked(ach)
            val progress = AchievementManager.getProgress(ach)

            shapeRenderer.color = if (unlocked) Color(0.52f, 0.42f, 0.28f, 1f) else Color(0.36f, 0.29f, 0.22f, 1f)
            shapeRenderer.rect(cx - 1.5f, cy - 1.5f, cardW + 3f, cardH + 3f)
            shapeRenderer.color = Color(0.22f, 0.17f, 0.13f, 1f)
            shapeRenderer.rect(cx, cy, cardW, cardH)

            val iconBoxSize = 49f
            val iconBoxX = cx + 6f
            val iconBoxY = cy + 6f
            shapeRenderer.color = Color(0.32f, 0.26f, 0.20f, 1f)
            shapeRenderer.rect(iconBoxX - 1.5f, iconBoxY - 1.5f, iconBoxSize + 3f, iconBoxSize + 3f)
            shapeRenderer.color = Color(0.15f, 0.12f, 0.09f, 1f)
            shapeRenderer.rect(iconBoxX, iconBoxY, iconBoxSize, iconBoxSize)

            drawAchievementIcon(
                sr = shapeRenderer,
                id = ach,
                cx = iconBoxX + iconBoxSize / 2f,
                cy = iconBoxY + iconBoxSize / 2f,
                size = iconBoxSize,
                unlocked = unlocked
            )

            if (!unlocked && ach.showProgressBar) {
                val barX = cx + 66f
                val barY = cy + 6f
                val barW = 290f
                val barH = 11f
                val ratio = (progress.toFloat() / ach.maxProgress.toFloat()).coerceIn(0f, 1f)

                shapeRenderer.color = Color(0.10f, 0.08f, 0.06f, 1f)
                shapeRenderer.rect(barX - 1.5f, barY - 1.5f, barW + 3f, barH + 3f)
                shapeRenderer.color = Color(0.17f, 0.13f, 0.10f, 1f)
                shapeRenderer.rect(barX, barY, barW, barH)

                if (ratio > 0f) {
                    shapeRenderer.color = Color(0.95f, 0.74f, 0.18f, 1f)
                    shapeRenderer.rect(barX, barY, barW * ratio, barH)
                    shapeRenderer.color = Color(1f, 0.90f, 0.45f, 0.75f)
                    shapeRenderer.rect(barX, barY + barH - 3f, barW * ratio, 3f)
                }
            }
        }

        val pageStartX = 225f
        val pageY = boardY + 20f
        val pageBtnSize = 42f
        val pageGap = 56f
        for (p in 1..4) {
            val px = pageStartX + (p - 1) * pageGap
            val isSel = (currentPage == p)
            val isPressed = (pressedButton == "PAGE_$p")
            if (isPressed) {
                shapeRenderer.color = Color(0.98f, 0.88f, 0.25f, 1f)
                shapeRenderer.rect(px - 4f, pageY - 4f, pageBtnSize + 8f, pageBtnSize + 8f)
            }
            shapeRenderer.color = when {
                isPressed -> Color(0.98f, 0.88f, 0.25f, 1f)
                isSel -> Color(0.56f, 0.46f, 0.34f, 1f)
                else -> Color(0.14f, 0.11f, 0.08f, 1f)
            }
            shapeRenderer.rect(px - 2f, pageY - 2f, pageBtnSize + 4f, pageBtnSize + 4f)
            shapeRenderer.color = if (isSel) Color(0.34f, 0.27f, 0.20f, 1f) else Color(0.22f, 0.18f, 0.13f, 1f)
            shapeRenderer.rect(px, pageY, pageBtnSize, pageBtnSize)

            val numColor = when {
                isPressed -> Color(1f, 0.95f, 0.35f, 1f)
                isSel -> Color(0.88f, 0.82f, 0.70f, 1f)
                else -> Color(0.98f, 0.86f, 0.42f, 1f)
            }
            ComicFontRenderer.drawPlaqueText(shapeRenderer, "$p", px + pageBtnSize / 2f, pageY + pageBtnSize / 2f, 18f, numColor)
        }

        val closeX = boardX + boardW - 155f
        val closeY = boardY + 24f
        val closeColor = if (pressedButton == "CLOSE") Color(1f, 0.92f, 0.28f, 1f) else Color(0.98f, 0.94f, 0.78f, 1f)
        ComicFontRenderer.drawPlaqueText(shapeRenderer, "CLOSE", closeX + 62.5f, closeY + 23f, 18f, closeColor)
        shapeRenderer.end()

        batch.begin()
        ComicFontRenderer.prepareCrispFont(titleFont, font, smallFont)
        val oldSScaleX = smallFont.data.scaleX
        val oldSScaleY = smallFont.data.scaleY

        for (i in pageAchievements.indices) {
            val ach = pageAchievements[i]
            val col = i % 2
            val row = i / 2
            val cx = if (col == 0) col0X else col1X
            val cy = topRowY - row * rowStep
            val unlocked = AchievementManager.isUnlocked(ach)
            val progress = AchievementManager.getProgress(ach)
            val textX = cx + 66f

            smallFont.data.setScale(1.02f)
            val titleColor = if (unlocked) Color(1f, 0.88f, 0.35f, 1f) else Color(0.88f, 0.80f, 0.66f, 1f)
            ComicFontRenderer.drawReadableText(batch, smallFont, ach.title, textX, cy + 55f, titleColor)

            if (!unlocked && ach.showProgressBar) {
                smallFont.data.setScale(0.94f)
                val descLines = wrapLines(ach.description, 44)
                ComicFontRenderer.drawReadableText(
                    batch, smallFont,
                    descLines.firstOrNull() ?: "",
                    textX, cy + 36f,
                    Color(0.82f, 0.76f, 0.64f, 1f)
                )

                smallFont.data.setScale(0.92f)
                ComicFontRenderer.drawReadableText(
                    batch, smallFont,
                    "$progress/${ach.maxProgress}",
                    textX + 298f, cy + 17f,
                    Color(0.98f, 0.92f, 0.76f, 1f)
                )
            } else {
                smallFont.data.setScale(0.95f)
                val descColor = if (unlocked) Color(0.96f, 0.90f, 0.78f, 1f) else Color(0.82f, 0.76f, 0.64f, 1f)
                val descLines = wrapLines(ach.description, 39)
                var dy = cy + 36f
                for (line in descLines.take(2)) {
                    ComicFontRenderer.drawReadableText(batch, smallFont, line, textX, dy, descColor)
                    dy -= 16f
                }
            }
        }

        smallFont.data.setScale(oldSScaleX, oldSScaleY)
        batch.end()
    }

    // =========================================================================
    // 4. HÀM VẼ CHI TIẾT TẤT CẢ 48 BIỂU TƯỢNG THÀNH TỰU
    // =========================================================================
    private fun ic(r: Float, g: Float, b: Float, unlocked: Boolean, a: Float = 1f): Color {
        if (unlocked) return Color(r, g, b, a)
        val lum = 0.299f * r + 0.587f * g + 0.114f * b
        val gray = 0.18f + lum * 0.25f
        return Color(gray * 1.08f, gray * 0.96f, gray * 0.82f, a * 0.82f)
    }

    private fun drawAchievementIcon(
        sr: ShapeRenderer,
        id: AchievementId,
        cx: Float,
        cy: Float,
        size: Float,
        unlocked: Boolean
    ) {
        val s = size / 50f
        val outline = ic(0.08f, 0.06f, 0.05f, unlocked)

        when (id) {
            AchievementId.FIRST_BLOOD -> {
                sr.color = outline
                sr.circle(cx, cy, 11f * s)
                val lobes = listOf(
                    Vector2(-12f, 2f), Vector2(12f, -2f), Vector2(-3f, 13f),
                    Vector2(6f, 11f), Vector2(-8f, -11f), Vector2(2f, -13f)
                )
                for (l in lobes) {
                    sr.rectLine(cx, cy, cx + l.x * s, cy + l.y * s, 7f * s)
                    sr.circle(cx + l.x * s, cy + l.y * s, 4.2f * s)
                }
                sr.color = ic(0.86f, 0.26f, 0.04f, unlocked)
                sr.circle(cx, cy, 9f * s)
                for (l in lobes) {
                    sr.rectLine(cx, cy, cx + l.x * s, cy + l.y * s, 4.6f * s)
                    sr.circle(cx + l.x * s, cy + l.y * s, 2.8f * s)
                }
                sr.color = ic(0.98f, 0.48f, 0.08f, unlocked)
                sr.circle(cx - 2f * s, cy + 2f * s, 5f * s)
                for (d in listOf(Vector2(-16f, 7f), Vector2(16f, 4f), Vector2(-14f, -7f), Vector2(9f, -15f))) {
                    sr.circle(cx + d.x * s, cy + d.y * s, 2.0f * s)
                }
            }

            AchievementId.DARING -> {
                sr.color = outline
                sr.circle(cx, cy + 3f * s, 14.5f * s)
                sr.rect(cx - 9.5f * s, cy - 13f * s, 19f * s, 12f * s)
                sr.color = ic(0.88f, 0.85f, 0.78f, unlocked)
                sr.circle(cx, cy + 3f * s, 12.2f * s)
                sr.rect(cx - 7.5f * s, cy - 11f * s, 15f * s, 10f * s)
                sr.color = outline
                sr.circle(cx - 5f * s, cy + 2f * s, 3.8f * s)
                sr.circle(cx + 5f * s, cy + 2f * s, 3.8f * s)
                sr.triangle(cx - 2f * s, cy - 4f * s, cx + 2f * s, cy - 4f * s, cx, cy)
                for (tx in listOf(-4f, 0f, 4f)) {
                    sr.rect(cx + tx * s - 0.8f * s, cy - 11f * s, 1.6f * s, 4.5f * s)
                }
            }

            AchievementId.CONSTRUCTOR, AchievementId.ENGINEER, AchievementId.THE_ARCHITECT -> {
                val headColor = when (id) {
                    AchievementId.CONSTRUCTOR -> ic(0.68f, 0.48f, 0.28f, unlocked)
                    AchievementId.ENGINEER -> ic(0.65f, 0.68f, 0.72f, unlocked)
                    else -> ic(0.96f, 0.78f, 0.22f, unlocked)
                }
                sr.color = outline
                sr.rectLine(cx - 4f * s, cy + 4f * s, cx + 12f * s, cy - 14f * s, 7f * s)
                sr.rectLine(cx - 13f * s, cy - 1f * s, cx + 5f * s, cy + 13f * s, 13f * s)
                sr.color = ic(0.58f, 0.38f, 0.20f, unlocked)
                sr.rectLine(cx - 3f * s, cy + 3f * s, cx + 11f * s, cy - 13f * s, 4.2f * s)
                sr.color = headColor
                sr.rectLine(cx - 11.5f * s, cy, cx + 3.5f * s, cy + 12f * s, 9.5f * s)
            }

            AchievementId.BLOODLUST -> {
                sr.color = outline
                sr.rectLine(cx + 8f * s, cy - 14f * s, cx - 7f * s, cy + 13f * s, 13f * s)
                sr.color = ic(0.68f, 0.46f, 0.24f, unlocked)
                sr.rectLine(cx + 7f * s, cy - 12f * s, cx - 6f * s, cy + 11f * s, 9.5f * s)
                sr.color = ic(0.88f, 0.16f, 0.08f, unlocked)
                sr.circle(cx - 5f * s, cy + 9f * s, 5.5f * s)
                sr.circle(cx - 1f * s, cy + 4f * s, 4.5f * s)
                sr.circle(cx - 7f * s, cy + 2f * s, 3.5f * s)
            }

            AchievementId.ARMAGGEDON -> {
                val mPts = listOf(Vector2(-8f, -6f), Vector2(3f, -10f), Vector2(10f, -1f))
                for (mp in mPts) {
                    val mx = cx + mp.x * s; val my = cy + mp.y * s
                    sr.color = ic(0.95f, 0.42f, 0.06f, unlocked)
                    sr.rectLine(mx, my, mx - 8f * s, my + 14f * s, 6f * s)
                    sr.circle(mx, my, 5.5f * s)
                    sr.color = ic(1f, 0.86f, 0.20f, unlocked)
                    sr.circle(mx, my, 3.2f * s)
                }
            }

            AchievementId.HOME_IMPROVEMENT -> {
                sr.color = outline
                sr.rect(cx - 14f * s, cy - 8f * s, 28f * s, 18f * s)
                sr.color = ic(0.82f, 0.20f, 0.14f, unlocked)
                sr.rect(cx - 12f * s, cy - 6f * s, 24f * s, 14f * s)
                sr.color = ic(0.75f, 0.78f, 0.82f, unlocked)
                sr.rect(cx - 4f * s, cy, 8f * s, 4f * s)
                sr.rectLine(cx - 10f * s, cy - 12f * s, cx + 8f * s, cy - 10f * s, 3.5f * s)
            }

            AchievementId.STARRY, AchievementId.SUPERMARIO, AchievementId.SUPERSTAR -> {
                when (id) {
                    AchievementId.STARRY -> drawGoldStar(sr, cx, cy, 15f * s, unlocked)
                    AchievementId.SUPERMARIO -> {
                        drawGoldStar(sr, cx - 6f * s, cy + 2f * s, 12f * s, unlocked)
                        drawGoldStar(sr, cx + 8f * s, cy - 4f * s, 10f * s, unlocked)
                    }
                    else -> {
                        drawGoldStar(sr, cx - 8f * s, cy - 4f * s, 10f * s, unlocked)
                        drawGoldStar(sr, cx + 8f * s, cy - 4f * s, 10f * s, unlocked)
                        drawGoldStar(sr, cx, cy + 6f * s, 12f * s, unlocked)
                    }
                }
            }

            AchievementId.WHATS_THAT -> {
                sr.color = outline
                sr.triangle(cx - 6f * s, cy + 16f * s, cx + 8f * s, cy + 15f * s, cx, cy - 4f * s)
                sr.circle(cx - 1f * s, cy - 12f * s, 5.2f * s)
                sr.color = ic(0.98f, 0.86f, 0.12f, unlocked)
                sr.triangle(cx - 4f * s, cy + 14f * s, cx + 5.5f * s, cy + 13f * s, cx, cy - 2f * s)
                sr.circle(cx - 1f * s, cy - 12f * s, 3.5f * s)
            }

            AchievementId.NUTS_AND_BOLTS -> {
                sr.color = outline
                sr.circle(cx + 5f * s, cy + 4f * s, 10f * s)
                sr.circle(cx - 5f * s, cy - 4f * s, 10.5f * s)
                sr.color = ic(0.58f, 0.62f, 0.66f, unlocked)
                sr.circle(cx + 5f * s, cy + 4f * s, 8f * s)
                sr.color = ic(0.76f, 0.80f, 0.84f, unlocked)
                sr.circle(cx - 5f * s, cy - 4f * s, 8.5f * s)
                sr.color = outline
                sr.circle(cx - 5f * s, cy - 4f * s, 3.8f * s)
            }

            AchievementId.IS_HE_DEAD_YETI -> {
                sr.color = ic(0.78f, 0.88f, 0.96f, unlocked)
                sr.circle(cx - 8f * s, cy + 2f * s, 6f * s)
                sr.circle(cx + 7f * s, cy - 2f * s, 7.5f * s)
                sr.color = outline
                sr.circle(cx - 7f * s, cy + 2f * s, 2.2f * s)
                sr.circle(cx + 6f * s, cy - 2f * s, 2.5f * s)
            }

            AchievementId.SLAYER -> {
                sr.color = outline
                sr.rectLine(cx + 10f * s, cy - 13f * s, cx - 10f * s, cy + 13f * s, 10f * s)
                sr.color = ic(0.92f, 0.72f, 0.18f, unlocked)
                sr.rectLine(cx + 3f * s, cy - 11f * s, cx + 12f * s, cy - 3f * s, 4.5f * s)
                sr.color = ic(0.84f, 0.88f, 0.92f, unlocked)
                sr.rectLine(cx + 5f * s, cy - 6f * s, cx - 9f * s, cy + 12f * s, 6.5f * s)
                sr.color = ic(0.88f, 0.14f, 0.08f, unlocked)
                sr.circle(cx - 7f * s, cy + 9f * s, 4f * s)
                sr.rectLine(cx - 8f * s, cy + 10f * s, cx - 12f * s, cy - 2f * s, 2.4f * s)
            }

            AchievementId.DEATH_FROM_ABOVE -> {
                sr.color = ic(0.92f, 0.34f, 0.06f, unlocked)
                sr.ellipse(cx - 15f * s, cy - 12f * s, 30f * s, 14f * s)
                sr.color = ic(0.98f, 0.58f, 0.10f, unlocked)
                sr.ellipse(cx - 11f * s, cy - 10f * s, 22f * s, 10f * s)
                sr.triangle(cx - 5f * s, cy - 6f * s, cx + 5f * s, cy - 6f * s, cx + 4f * s, cy + 16f * s)
                sr.color = ic(1f, 0.92f, 0.35f, unlocked)
                sr.circle(cx, cy - 5f * s, 4.5f * s)
            }

            AchievementId.TACTICIAN -> {
                sr.color = outline
                sr.rectLine(cx - 8f * s, cy - 14f * s, cx - 3f * s, cy + 14f * s, 4.5f * s)
                sr.triangle(cx - 3f * s, cy + 13f * s, cx - 5f * s, cy - 1f * s, cx + 14f * s, cy + 6f * s)
                sr.color = ic(0.20f, 0.58f, 0.95f, unlocked)
                sr.triangle(cx - 2f * s, cy + 11f * s, cx - 4f * s, cy + 1f * s, cx + 12f * s, cy + 6f * s)
                sr.color = ic(0.78f, 0.80f, 0.84f, unlocked)
                sr.rectLine(cx - 8f * s, cy - 13f * s, cx - 3f * s, cy + 13f * s, 2.4f * s)
            }

            AchievementId.THIS_IS_THE_END -> {
                sr.color = outline
                sr.rectLine(cx - 12f * s, cy - 12f * s, cx + 8f * s, cy + 8f * s, 6f * s)
                sr.color = ic(0.35f, 0.32f, 0.38f, unlocked)
                sr.rectLine(cx - 11f * s, cy - 11f * s, cx + 6f * s, cy + 6f * s, 3.5f * s)
                sr.color = ic(0.88f, 0.18f, 0.52f, unlocked)
                sr.triangle(cx, cy + 12f * s, cx + 12f * s, cy, cx + 13f * s, cy + 13f * s)
                sr.triangle(cx, cy + 12f * s, cx + 12f * s, cy, cx + 2f * s, cy + 2f * s)
            }

            AchievementId.TERMINATOR -> {
                sr.color = outline
                sr.circle(cx, cy, 13f * s)
                sr.color = ic(0.58f, 0.62f, 0.66f, unlocked)
                sr.circle(cx, cy, 11f * s)
                sr.color = outline
                sr.circle(cx, cy, 8f * s)
                sr.color = ic(0.95f, 0.16f, 0.12f, unlocked)
                sr.circle(cx, cy, 4.5f * s)
                sr.color = ic(1f, 0.75f, 0.75f, unlocked)
                sr.circle(cx - 1f * s, cy + 1f * s, 1.8f * s)
            }

            AchievementId.DIE_HARD -> {
                sr.color = outline
                sr.rect(cx - 10f * s, cy - 12f * s, 20f * s, 24f * s)
                sr.color = ic(0.92f, 0.72f, 0.52f, unlocked)
                sr.rect(cx - 8f * s, cy - 10f * s, 16f * s, 20f * s)
                sr.color = ic(0.95f, 0.95f, 0.92f, unlocked)
                sr.rect(cx - 8.5f * s, cy + 5f * s, 17f * s, 4.5f * s)
                sr.color = outline
                sr.circle(cx - 3.5f * s, cy + 1f * s, 1.5f * s)
                sr.circle(cx + 3.5f * s, cy + 1f * s, 1.5f * s)
                sr.rect(cx - 4.5f * s, cy - 6f * s, 9f * s, 2.5f * s)
            }

            AchievementId.GI_JOE -> {
                sr.color = outline
                sr.arc(cx, cy - 4f * s, 15f * s, 0f, 180f)
                sr.rect(cx - 16f * s, cy - 6f * s, 32f * s, 4f * s)
                sr.color = ic(0.32f, 0.48f, 0.24f, unlocked)
                sr.arc(cx, cy - 3f * s, 13f * s, 0f, 180f)
                sr.color = ic(0.45f, 0.62f, 0.34f, unlocked)
                sr.circle(cx - 4f * s, cy + 4f * s, 4f * s)
            }

            AchievementId.CANNON_FODDER -> {
                sr.color = ic(0.78f, 0.14f, 0.08f, unlocked)
                sr.ellipse(cx - 15f * s, cy - 12f * s, 30f * s, 10f * s)
                sr.color = outline
                sr.circle(cx, cy, 11f * s)
                sr.color = ic(0.55f, 0.58f, 0.62f, unlocked)
                sr.circle(cx, cy, 9f * s)
                sr.color = outline
                sr.circle(cx + 3f * s, cy - 2f * s, 5f * s)
            }

            AchievementId.FEARLESS -> {
                sr.color = outline
                sr.rect(cx - 16f * s, cy + 2f * s, 32f * s, 3f * s)
                sr.circle(cx - 8f * s, cy - 2f * s, 7.5f * s)
                sr.circle(cx + 8f * s, cy - 2f * s, 7.5f * s)
                sr.color = ic(0.18f, 0.20f, 0.24f, unlocked)
                sr.circle(cx - 8f * s, cy - 2f * s, 5.5f * s)
                sr.circle(cx + 8f * s, cy - 2f * s, 5.5f * s)
                sr.color = ic(0.85f, 0.90f, 0.95f, unlocked)
                sr.rectLine(cx - 11f * s, cy - 4f * s, cx - 6f * s, cy + 2f * s, 2f * s)
                sr.rectLine(cx + 5f * s, cy - 4f * s, cx + 10f * s, cy + 2f * s, 2f * s)
            }

            AchievementId.REAL_ESTATE -> {
                sr.color = outline
                sr.rect(cx - 13f * s, cy - 10f * s, 16f * s, 12f * s)
                sr.triangle(cx - 15f * s, cy + 2f * s, cx + 5f * s, cy + 2f * s, cx - 5f * s, cy + 12f * s)
                sr.rect(cx + 1f * s, cy - 12f * s, 13f * s, 10f * s)
                sr.color = ic(0.86f, 0.78f, 0.58f, unlocked)
                sr.rect(cx - 11f * s, cy - 8f * s, 12f * s, 9f * s)
                sr.rect(cx + 2f * s, cy - 10f * s, 10f * s, 8f * s)
                sr.color = ic(0.82f, 0.26f, 0.14f, unlocked)
                sr.triangle(cx - 13f * s, cy + 2f * s, cx + 3f * s, cy + 2f * s, cx - 5f * s, cy + 10f * s)
                sr.triangle(cx, cy - 2f * s, cx + 14f * s, cy - 2f * s, cx + 7f * s, cy + 6f * s)
            }

            AchievementId.INDECISIVE -> {
                sr.color = outline
                sr.circle(cx, cy + 6f * s, 9.5f * s)
                sr.rect(cx - 3.5f * s, cy - 6f * s, 7f * s, 8f * s)
                sr.circle(cx, cy - 12f * s, 4.5f * s)
                sr.color = ic(0.96f, 0.76f, 0.16f, unlocked)
                sr.circle(cx, cy + 6f * s, 7f * s)
                sr.rect(cx - 2f * s, cy - 5f * s, 4.5f * s, 7f * s)
                sr.circle(cx, cy - 12f * s, 3f * s)
                sr.color = Color(0.15f, 0.12f, 0.09f, 1f)
                sr.circle(cx - 1f * s, cy + 6f * s, 3.2f * s)
            }

            AchievementId.IMPATIENT -> {
                sr.color = outline
                sr.rect(cx - 10f * s, cy + 11f * s, 20f * s, 4f * s)
                sr.rect(cx - 10f * s, cy - 15f * s, 20f * s, 4f * s)
                sr.triangle(cx - 9f * s, cy + 11f * s, cx + 9f * s, cy + 11f * s, cx, cy - 1f * s)
                sr.triangle(cx - 9f * s, cy - 11f * s, cx + 9f * s, cy - 11f * s, cx, cy + 1f * s)
                sr.color = ic(0.55f, 0.82f, 0.92f, unlocked)
                sr.triangle(cx - 6.5f * s, cy + 10f * s, cx + 6.5f * s, cy + 10f * s, cx, cy)
                sr.triangle(cx - 6.5f * s, cy - 10f * s, cx + 6.5f * s, cy - 10f * s, cx, cy)
                sr.color = ic(0.96f, 0.78f, 0.22f, unlocked)
                sr.rect(cx - 8.5f * s, cy + 11.5f * s, 17f * s, 2.5f * s)
                sr.rect(cx - 8.5f * s, cy - 14f * s, 17f * s, 2.5f * s)
                sr.triangle(cx - 5.5f * s, cy - 10f * s, cx + 5.5f * s, cy - 10f * s, cx, cy - 3f * s)
            }

            AchievementId.FOREST_DIPLOMACY -> {
                sr.color = outline
                sr.ellipse(cx - 13f * s, cy - 8f * s, 26f * s, 16f * s)
                sr.color = ic(0.24f, 0.76f, 0.22f, unlocked)
                sr.ellipse(cx - 11f * s, cy - 6f * s, 22f * s, 12f * s)
                sr.color = ic(0.68f, 0.95f, 0.36f, unlocked)
                sr.rectLine(cx - 10f * s, cy - 2f * s, cx + 10f * s, cy + 2f * s, 2f * s)
            }

            AchievementId.SUNBURNER -> {
                sr.color = outline
                sr.triangle(cx - 11f * s, cy, cx + 11f * s, cy, cx, cy + 15f * s)
                sr.triangle(cx - 11f * s, cy, cx + 11f * s, cy, cx, cy - 15f * s)
                sr.color = ic(0.98f, 0.65f, 0.12f, unlocked)
                sr.triangle(cx - 9f * s, cy, cx + 9f * s, cy, cx, cy + 12.5f * s)
                sr.triangle(cx - 9f * s, cy, cx + 9f * s, cy, cx, cy - 12.5f * s)
                sr.color = ic(1f, 0.94f, 0.38f, unlocked)
                sr.triangle(cx - 5f * s, cy, cx + 5f * s, cy, cx, cy + 8f * s)
            }

            AchievementId.IMPERIAL_SAVIOUR -> {
                sr.color = ic(0.78f, 0.20f, 0.45f, unlocked)
                sr.triangle(cx - 6f * s, cy + 8f * s, cx + 6f * s, cy + 8f * s, cx + 4f * s, cy + 17f * s)
                sr.color = outline
                sr.circle(cx, cy - 1f * s, 11f * s)
                sr.rect(cx - 10f * s, cy - 12f * s, 20f * s, 11f * s)
                sr.color = ic(0.75f, 0.78f, 0.84f, unlocked)
                sr.circle(cx, cy - 1f * s, 9f * s)
                sr.rect(cx - 8f * s, cy - 10f * s, 16f * s, 9f * s)
                sr.color = outline
                sr.rect(cx - 6f * s, cy - 3f * s, 12f * s, 3f * s)
                sr.rect(cx - 1.5f * s, cy - 9f * s, 3f * s, 9f * s)
            }

            AchievementId.SPECIALIST -> {
                for (ry in listOf(-6f, 0f, 6f)) {
                    val y = cy + ry * s
                    sr.color = outline
                    sr.rectLine(cx - 10f * s, y - 3f * s, cx, y + 4f * s, 5f * s)
                    sr.rectLine(cx, y + 4f * s, cx + 10f * s, y - 3f * s, 5f * s)
                    sr.color = ic(0.98f, 0.82f, 0.20f, unlocked)
                    sr.rectLine(cx - 9f * s, y - 2.5f * s, cx, y + 3.5f * s, 3f * s)
                    sr.rectLine(cx, y + 3.5f * s, cx + 9f * s, y - 2.5f * s, 3f * s)
                }
            }

            AchievementId.FIFTY_SHOTS_FIFTY_KILLS -> {
                sr.color = ic(0.92f, 0.18f, 0.12f, unlocked)
                sr.circle(cx, cy, 13f * s)
                sr.color = Color(0.15f, 0.12f, 0.09f, 1f)
                sr.circle(cx, cy, 10f * s)
                sr.color = ic(0.92f, 0.18f, 0.12f, unlocked)
                sr.circle(cx, cy, 3f * s)
                sr.rect(cx - 16f * s, cy - 1f * s, 32f * s, 2f * s)
                sr.rect(cx - 1f * s, cy - 16f * s, 2f * s, 32f * s)
            }

            AchievementId.TOXICITY -> {
                sr.color = outline
                sr.rectLine(cx - 9f * s, cy + 9f * s, cx + 10f * s, cy - 8f * s, 6f * s)
                sr.color = ic(0.78f, 0.82f, 0.86f, unlocked)
                sr.rectLine(cx - 7f * s, cy + 7f * s, cx + 8f * s, cy - 6f * s, 3.6f * s)
                sr.color = ic(0.28f, 0.95f, 0.18f, unlocked)
                sr.circle(cx - 7f * s, cy - 6f * s, 3.2f * s)
                sr.circle(cx + 6f * s, cy - 11f * s, 2.5f * s)
            }

            AchievementId.ENTANGLED -> {
                sr.color = outline
                sr.circle(cx, cy, 12f * s)
                sr.color = ic(0.24f, 0.68f, 0.18f, unlocked)
                sr.circle(cx, cy, 9.5f * s)
                sr.color = Color(0.15f, 0.12f, 0.09f, 1f)
                sr.circle(cx, cy, 5f * s)
                sr.color = ic(0.92f, 0.22f, 0.14f, unlocked)
                for (p in listOf(Vector2(-10f, 4f), Vector2(10f, -3f), Vector2(2f, 11f), Vector2(-3f, -10f))) {
                    sr.circle(cx + p.x * s, cy + p.y * s, 2.2f * s)
                }
            }

            AchievementId.DUST_TO_DUST -> {
                sr.color = ic(0.88f, 0.28f, 0.96f, unlocked)
                for (d in listOf(Vector2(-12f, 8f), Vector2(12f, 10f), Vector2(14f, -4f), Vector2(-13f, -6f))) {
                    sr.circle(cx + d.x * s, cy + d.y * s, 2.5f * s)
                }
                sr.color = ic(0.94f, 0.86f, 0.72f, unlocked)
                sr.circle(cx - 2f * s, cy, 9.5f * s)
                sr.color = outline
                sr.circle(cx - 5f * s, cy + 1f * s, 2.8f * s)
                sr.circle(cx + 1f * s, cy + 1f * s, 2.8f * s)
            }

            AchievementId.BEAM_ME_UP_SCOTTY -> {
                sr.color = ic(0.68f, 0.28f, 0.96f, unlocked, 0.55f)
                sr.circle(cx, cy, 14f * s)
                sr.color = ic(0.35f, 0.88f, 1f, unlocked)
                sr.ellipse(cx - 8f * s, cy - 13f * s, 16f * s, 26f * s)
                sr.color = outline
                sr.circle(cx, cy + 4f * s, 3.2f * s)
                sr.rect(cx - 2.5f * s, cy - 6f * s, 5f * s, 8f * s)
            }

            AchievementId.SHEPHERD -> {
                sr.color = outline
                sr.circle(cx - 2f * s, cy, 11.5f * s)
                sr.circle(cx + 7f * s, cy + 2f * s, 7.5f * s)
                sr.color = ic(0.95f, 0.95f, 0.92f, unlocked)
                sr.circle(cx - 2f * s, cy, 9.5f * s)
                sr.circle(cx - 7f * s, cy + 3f * s, 6f * s)
                sr.color = ic(0.24f, 0.20f, 0.18f, unlocked)
                sr.circle(cx + 7.5f * s, cy + 2f * s, 5.2f * s)
            }

            AchievementId.ELEMENTALIST -> {
                sr.color = outline
                sr.circle(cx, cy + 1f * s, 12.5f * s)
                sr.color = ic(0.56f, 0.50f, 0.42f, unlocked)
                sr.circle(cx, cy + 1f * s, 10.5f * s)
                sr.rect(cx - 9f * s, cy - 11f * s, 18f * s, 10f * s)
                sr.color = ic(1f, 0.90f, 0.18f, unlocked)
                sr.circle(cx - 3.5f * s, cy + 3f * s, 2f * s)
                sr.circle(cx + 3.5f * s, cy + 3f * s, 2f * s)
            }

            AchievementId.AXE_RAIN, AchievementId.ARE_YOU_NOT_ENTERTAINED -> {
                sr.color = outline
                sr.rectLine(cx - 10f * s, cy - 12f * s, cx + 8f * s, cy + 12f * s, 5.5f * s)
                sr.triangle(cx - 4f * s, cy + 14f * s, cx + 14f * s, cy + 2f * s, cx + 2f * s, cy)
                sr.color = ic(0.58f, 0.36f, 0.18f, unlocked)
                sr.rectLine(cx - 9f * s, cy - 11f * s, cx + 7f * s, cy + 11f * s, 3.2f * s)
                sr.color = ic(0.82f, 0.86f, 0.90f, unlocked)
                sr.triangle(cx - 3f * s, cy + 12.5f * s, cx + 12.5f * s, cy + 3f * s, cx + 2f * s, cy + 1f * s)
                if (id == AchievementId.ARE_YOU_NOT_ENTERTAINED) {
                    sr.color = ic(0.88f, 0.16f, 0.10f, unlocked)
                    sr.circle(cx + 6f * s, cy + 7f * s, 3.5f * s)
                }
            }

            AchievementId.MEDIC -> {
                sr.color = outline
                sr.rect(cx - 13f * s, cy - 10f * s, 26f * s, 20f * s)
                sr.color = ic(0.92f, 0.94f, 0.96f, unlocked)
                sr.rect(cx - 11f * s, cy - 8f * s, 22f * s, 16f * s)
                sr.color = ic(0.88f, 0.16f, 0.14f, unlocked)
                sr.rect(cx - 2.2f * s, cy - 5.5f * s, 4.4f * s, 11f * s)
                sr.rect(cx - 5.5f * s, cy - 2.2f * s, 11f * s, 4.4f * s)
            }

            AchievementId.HOLY_CHORUS -> {
                sr.color = outline
                sr.rect(cx - 10f * s, cy - 13f * s, 20f * s, 22f * s)
                sr.circle(cx, cy + 9f * s, 10f * s)
                sr.color = ic(0.96f, 0.82f, 0.24f, unlocked)
                sr.rect(cx - 8f * s, cy - 11f * s, 16f * s, 20f * s)
                sr.circle(cx, cy + 9f * s, 8f * s)
                sr.color = ic(0.32f, 0.82f, 0.98f, unlocked)
                sr.rect(cx - 5f * s, cy - 9f * s, 10f * s, 16f * s)
                sr.circle(cx, cy + 7f * s, 5f * s)
            }

            AchievementId.ROCKETEER -> {
                sr.color = ic(0.98f, 0.48f, 0.10f, unlocked)
                sr.circle(cx + 8f * s, cy - 8f * s, 6f * s)
                sr.color = outline
                sr.rectLine(cx + 6f * s, cy - 6f * s, cx - 9f * s, cy + 9f * s, 9f * s)
                sr.color = ic(0.88f, 0.22f, 0.14f, unlocked)
                sr.rectLine(cx + 5f * s, cy - 5f * s, cx - 8f * s, cy + 8f * s, 6.5f * s)
                sr.color = ic(0.95f, 0.95f, 0.95f, unlocked)
                sr.circle(cx - 3f * s, cy + 3f * s, 3.2f * s)
            }

            AchievementId.CLUSTERED -> {
                sr.color = ic(0.98f, 0.68f, 0.15f, unlocked)
                sr.circle(cx, cy + 2f * s, 7f * s)
                for (bp in listOf(Vector2(-10f, 8f), Vector2(10f, 8f), Vector2(-9f, -9f), Vector2(9f, -9f), Vector2(0f, -12f))) {
                    sr.color = outline
                    sr.circle(cx + bp.x * s, cy + bp.y * s, 5f * s)
                    sr.color = ic(0.32f, 0.35f, 0.40f, unlocked)
                    sr.circle(cx + bp.x * s, cy + bp.y * s, 3.5f * s)
                }
            }

            AchievementId.ENERGY_NETWORK -> {
                sr.color = ic(0.22f, 0.85f, 1f, unlocked)
                for (ep in listOf(Vector2(-12f, 10f), Vector2(12f, 10f), Vector2(-13f, -4f), Vector2(13f, -4f), Vector2(0f, 14f))) {
                    sr.rectLine(cx, cy - 2f * s, cx + ep.x * s, cy + ep.y * s, 2.5f * s)
                }
                sr.circle(cx, cy - 4f * s, 8f * s)
                sr.color = ic(0.92f, 0.98f, 1f, unlocked)
                sr.circle(cx, cy - 4f * s, 4.5f * s)
            }

            AchievementId.AC_DC -> {
                sr.color = outline
                sr.triangle(cx + 3f * s, cy + 16f * s, cx - 10f * s, cy - 1f * s, cx + 2f * s, cy - 1f * s)
                sr.triangle(cx - 2f * s, cy + 3f * s, cx + 9f * s, cy + 3f * s, cx - 4f * s, cy - 16f * s)
                sr.color = ic(0.98f, 0.88f, 0.18f, unlocked)
                sr.triangle(cx + 2f * s, cy + 13.5f * s, cx - 7.5f * s, cy, cx + 1.5f * s, cy)
                sr.triangle(cx - 1.5f * s, cy + 2f * s, cx + 7f * s, cy + 2f * s, cx - 3f * s, cy - 13.5f * s)
            }

            AchievementId.GREAT_DEFENDER -> {
                sr.color = outline
                sr.rect(cx - 12f * s, cy + 10f * s, 24f * s, 3.5f * s)
                sr.rect(cx - 9f * s, cy - 6f * s, 18f * s, 16f * s)
                sr.triangle(cx - 9f * s, cy - 6f * s, cx + 9f * s, cy - 6f * s, cx, cy - 14f * s)
                sr.color = ic(0.86f, 0.18f, 0.14f, unlocked)
                sr.rect(cx - 7f * s, cy - 5f * s, 14f * s, 14f * s)
                sr.triangle(cx - 7f * s, cy - 5f * s, cx + 7f * s, cy - 5f * s, cx, cy - 11.5f * s)
                sr.color = ic(0.98f, 0.82f, 0.20f, unlocked)
                sr.circle(cx, cy + 2f * s, 3.8f * s)
            }

            AchievementId.HEROIC_DEFENDER -> {
                sr.color = ic(0.96f, 0.78f, 0.18f, unlocked)
                sr.circle(cx, cy, 14f * s)
                sr.color = outline
                sr.circle(cx, cy, 10.5f * s)
                sr.color = ic(0.20f, 0.56f, 0.92f, unlocked)
                sr.rect(cx - 7f * s, cy - 4f * s, 14f * s, 11f * s)
                sr.triangle(cx - 7f * s, cy - 4f * s, cx + 7f * s, cy - 4f * s, cx, cy - 12f * s)
                sr.color = ic(0.92f, 0.95f, 0.98f, unlocked)
                sr.rect(cx - 1.5f * s, cy - 8f * s, 3f * s, 13f * s)
                sr.rect(cx - 5f * s, cy - 1f * s, 10f * s, 3f * s)
            }

            AchievementId.IRON_DEFENDER -> {
                sr.color = outline
                sr.rect(cx - 9f * s, cy - 14f * s, 18f * s, 12f * s)
                sr.circle(cx, cy + 3f * s, 10.5f * s)
                sr.color = ic(0.62f, 0.66f, 0.72f, unlocked)
                sr.rect(cx - 7f * s, cy - 12f * s, 14f * s, 10f * s)
                sr.circle(cx, cy + 3f * s, 8.5f * s)
                sr.color = ic(0.82f, 0.86f, 0.90f, unlocked)
                for (fx in listOf(-5f, -1.5f, 2f, 5.5f)) {
                    sr.rect(cx + fx * s - 1.2f * s, cy + 2f * s, 2.4f * s, 5.5f * s)
                }
            }
        }
    }

    private fun drawGoldStar(sr: ShapeRenderer, cx: Float, cy: Float, r: Float, unlocked: Boolean) {
        sr.color = ic(0.10f, 0.08f, 0.06f, unlocked)
        sr.circle(cx, cy, r * 0.92f)
        sr.color = ic(0.98f, 0.80f, 0.14f, unlocked)
        for (i in 0 until 5) {
            val a = Math.toRadians((90 + i * 72).toDouble())
            val aL = Math.toRadians((90 + i * 72 - 36).toDouble())
            val aR = Math.toRadians((90 + i * 72 + 36).toDouble())
            val tx = cx + (r * Math.cos(a)).toFloat()
            val ty = cy + (r * Math.sin(a)).toFloat()
            val lx = cx + (r * 0.42f * Math.cos(aL)).toFloat()
            val ly = cy + (r * 0.42f * Math.sin(aL)).toFloat()
            val rx = cx + (r * 0.42f * Math.cos(aR)).toFloat()
            val ry = cy + (r * 0.42f * Math.sin(aR)).toFloat()
            sr.triangle(cx, cy, tx, ty, lx, ly)
            sr.triangle(cx, cy, tx, ty, rx, ry)
        }
        sr.color = ic(1f, 0.95f, 0.55f, unlocked)
        sr.circle(cx - r * 0.12f, cy + r * 0.12f, r * 0.25f)
    }

    private fun drawWoodenBoardFrame(sr: ShapeRenderer, x: Float, y: Float, w: Float, h: Float) {
        sr.begin(ShapeRenderer.ShapeType.Filled)
        sr.color = Color(0f, 0f, 0f, 0.55f)
        sr.rect(0f, 0f, Config.WORLD_WIDTH, Config.WORLD_HEIGHT)

        for (chainX in listOf(x + 65f, x + w - 75f)) {
            sr.color = Color(0.25f, 0.26f, 0.28f, 1f)
            sr.rect(chainX, y + h - 5f, 12f, 55f)
            sr.color = Color(0.58f, 0.60f, 0.65f, 1f)
            for (cy in (y + h).toInt()..715 step 14) {
                sr.rect(chainX + 2f, cy.toFloat(), 8f, 9f)
            }
        }

        sr.color = Color(0.55f, 0.54f, 0.50f, 1f)
        sr.rect(x - 6f, y - 6f, w + 12f, h + 12f)
        sr.color = Color(0.22f, 0.18f, 0.14f, 1f)
        sr.rect(x - 2f, y - 2f, w + 4f, h + 4f)
        sr.color = Color(0.28f, 0.22f, 0.16f, 1f)
        sr.rect(x, y, w, h)

        sr.color = Color(0.24f, 0.18f, 0.13f, 1f)
        for (gy in (y + 35f).toInt() until (y + h - 25f).toInt() step 42) {
            sr.rect(x + 18f, gy.toFloat(), w - 36f, 4f)
            sr.rect(x + 60f, gy + 18f, w - 140f, 2.5f)
        }

        val corners = listOf(
            Vector2(x - 4f, y + h - 22f),
            Vector2(x + w - 22f, y + h - 22f),
            Vector2(x - 4f, y - 4f),
            Vector2(x + w - 22f, y - 4f)
        )
        for (c in corners) {
            sr.color = Color(0.62f, 0.64f, 0.66f, 1f)
            sr.rect(c.x, c.y, 26f, 26f)
            sr.color = Color(0.28f, 0.28f, 0.30f, 1f)
            sr.circle(c.x + 6f, c.y + 6f, 2.2f)
            sr.circle(c.x + 20f, c.y + 6f, 2.2f)
            sr.circle(c.x + 6f, c.y + 20f, 2.2f)
            sr.circle(c.x + 20f, c.y + 20f, 2.2f)
        }

        val cx = x + w / 2f
        val topY = y + h
        sr.color = Color(0.72f, 0.56f, 0.22f, 1f)
        sr.rect(cx - 235f, topY - 46f, 65f, 44f)
        sr.rect(cx + 170f, topY - 46f, 65f, 44f)
        sr.color = Color(0.28f, 0.22f, 0.16f, 1f)
        sr.triangle(cx - 235f, topY - 24f, cx - 235f, topY - 46f, cx - 215f, topY - 35f)
        sr.triangle(cx + 235f, topY - 24f, cx + 235f, topY - 46f, cx + 215f, topY - 35f)

        sr.color = Color(0.52f, 0.38f, 0.12f, 1f)
        sr.rect(cx - 184f, topY - 36f, 368f, 64f)
        sr.color = Color(0.92f, 0.76f, 0.36f, 1f)
        sr.rect(cx - 180f, topY - 32f, 360f, 56f)
        sr.color = Color(0.98f, 0.85f, 0.48f, 1f)
        sr.rect(cx - 174f, topY - 26f, 348f, 44f)

        val closeX = x + w - 155f
        val closeY = boardYOffset(y)
        val isClosePressed = (pressedButton == "CLOSE")
        if (isClosePressed) {
            sr.color = Color(0.98f, 0.88f, 0.25f, 1f)
            sr.rect(closeX - 5f, closeY - 5f, 135f, 56f)
        }
        sr.color = if (isClosePressed) Color(0.98f, 0.88f, 0.25f, 1f) else Color(0.52f, 0.48f, 0.40f, 1f)
        sr.rect(closeX - 3f, closeY - 3f, 131f, 52f)
        sr.color = Color(0.28f, 0.24f, 0.18f, 1f)
        sr.rect(closeX, closeY, 125f, 46f)
        sr.color = Color(0.36f, 0.31f, 0.24f, 1f)
        sr.rect(closeX + 4f, closeY + 4f, 117f, 38f)
        sr.end()
    }

    private fun boardYOffset(y: Float) = y + 24f

    private fun wrapLines(text: String, maxChars: Int): List<String> {
        val words = text.split(" ")
        val lines = mutableListOf<String>()
        var current = ""
        for (w in words) {
            if (current.isEmpty()) current = w
            else if (current.length + 1 + w.length <= maxChars) current += " $w"
            else {
                lines.add(current)
                current = w
            }
        }
        if (current.isNotEmpty()) lines.add(current)
        return lines
    }
}
