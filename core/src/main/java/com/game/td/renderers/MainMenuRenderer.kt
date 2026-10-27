package com.game.td.renderers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Align
import com.game.td.Config
import com.game.td.managers.SaveManager
import com.game.td.managers.SoundManager

object MainMenuRenderer {

    private val OUTLINE = Color(0.12f, 0.09f, 0.07f, 1f)

    fun render(
        shapeRenderer: ShapeRenderer,
        batch: SpriteBatch,
        logoMainFont: BitmapFont,
        logoSubFont: BitmapFont,
        titleFont: BitmapFont,
        font: BitmapFont,
        italicFont: BitmapFont,
        smallFont: BitmapFont,
        isSlotPopupOpen: Boolean,
        isCreditsPopupOpen: Boolean
    ) {
        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

        val cX = Config.WORLD_WIDTH / 2f
        val logoY = 535f

        // 1. VẼ BỐI CẢNH CHIẾN TRƯỜNG NỀN, KHIÊN LOGO & NÚT TREO DÂY XÍCH
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        drawSkyAndClouds(shapeRenderer)
        drawDistantMountainsAndRoad(shapeRenderer)
        drawLeftArcaneTowerAndPaladin(shapeRenderer)
        drawRightRangerTreehouseAndMissile(shapeRenderer)
        drawForegroundBarbarianAndOgreBattle(shapeRenderer)
        drawTopCornerDecorations(shapeRenderer)

        if (!isSlotPopupOpen && !isCreditsPopupOpen) {
            drawHangingStartAndCreditsButtons(shapeRenderer, cX)
        }

        drawKingdomRushCrestShield(shapeRenderer, cX, logoY)
        shapeRenderer.end()

        // 2. VẼ CHỮ LOGO 3D, CHỮ TRÊN NÚT START / CREDITS VÀ ICON GÓC
        batch.begin()
        drawLogoAndMenuTexts(
            batch = batch,
            cX = cX,
            logoY = logoY,
            logoMainFont = logoMainFont,
            logoSubFont = logoSubFont,
            titleFont = titleFont,
            font = font,
            smallFont = smallFont,
            isSlotPopupOpen = isSlotPopupOpen,
            isCreditsPopupOpen = isCreditsPopupOpen
        )
        batch.end()

        // 3. HIỂN THỊ BẢNG LOCAL SAVE HOẶC CREDITS KHI ĐƯỢC MỞ
        if (isSlotPopupOpen) {
            renderLocalSavePopup(shapeRenderer, batch, font, italicFont, smallFont)
        } else if (isCreditsPopupOpen) {
            renderCreditsPopup(shapeRenderer, batch, titleFont, font, smallFont)
        }
    }

    // =========================================================================
    // LỚP 1: BẦU TRỜI CHUYỂN SẮC, MÂY TRẮNG, MƯA TÊN & ĐÀN GARGOYLE BAY LƯỢN
    // =========================================================================
    private fun drawSkyAndClouds(sr: ShapeRenderer) {
        val w = Config.WORLD_WIDTH
        sr.color = Color(0.36f, 0.62f, 0.74f, 1f)
        sr.rect(0f, 580f, w, 140f)
        sr.color = Color(0.50f, 0.73f, 0.80f, 1f)
        sr.rect(0f, 470f, w, 115f)
        sr.color = Color(0.68f, 0.82f, 0.80f, 1f)
        sr.rect(0f, 380f, w, 95f)
        sr.color = Color(0.88f, 0.88f, 0.74f, 1f)
        sr.rect(0f, 280f, w, 105f)

        sr.color = Color(0.95f, 0.94f, 0.86f, 0.85f)
        drawCloud(sr, 230f, 615f, 1.1f)
        drawCloud(sr, 920f, 600f, 1.25f)
        drawCloud(sr, 1080f, 485f, 0.9f)
        drawCloud(sr, 160f, 455f, 0.85f)

        val arrows = listOf(
            Vector2(145f, 520f), Vector2(195f, 495f), Vector2(140f, 460f),
            Vector2(240f, 465f), Vector2(215f, 435f), Vector2(280f, 445f)
        )
        sr.color = Color(0.28f, 0.24f, 0.20f, 0.85f)
        for (a in arrows) {
            sr.rectLine(a.x, a.y, a.x + 22f, a.y + 7f, 2f)
            sr.triangle(a.x + 22f, a.y + 10f, a.x + 22f, a.y + 4f, a.x + 28f, a.y + 9f)
        }

        drawBatSilhouette(sr, 1015f, 505f, 1.15f)
        drawBatSilhouette(sr, 915f, 495f, 0.72f)
        drawBatSilhouette(sr, 1050f, 435f, 0.65f)
        drawBatSilhouette(sr, 835f, 405f, 0.75f)
        drawBatSilhouette(sr, 760f, 385f, 0.48f)
    }

    private fun drawCloud(sr: ShapeRenderer, cx: Float, cy: Float, scale: Float) {
        sr.ellipse(cx - 65f * scale, cy - 12f * scale, 130f * scale, 26f * scale)
        sr.circle(cx - 25f * scale, cy + 8f * scale, 22f * scale)
        sr.circle(cx + 15f * scale, cy + 12f * scale, 26f * scale)
        sr.circle(cx + 45f * scale, cy + 4f * scale, 18f * scale)
    }

    private fun drawBatSilhouette(sr: ShapeRenderer, x: Float, y: Float, s: Float) {
        sr.color = Color(0.26f, 0.24f, 0.20f, 0.9f)
        sr.circle(x, y, 6f * s)
        sr.triangle(x - 3f * s, y + 2f * s, x - 34f * s, y + 16f * s, x - 18f * s, y - 6f * s)
        sr.triangle(x + 3f * s, y + 2f * s, x + 34f * s, y + 16f * s, x + 18f * s, y - 6f * s)
        sr.triangle(x - 4f * s, y - 2f * s, x + 4f * s, y - 2f * s, x, y - 12f * s)
    }

    // =========================================================================
    // LỚP 2: DÃY NÚI XA, ĐỒI CỎ XANH & CON ĐƯỜNG ĐẤT CHÍNH GIỮA
    // =========================================================================
    private fun drawDistantMountainsAndRoad(sr: ShapeRenderer) {
        val w = Config.WORLD_WIDTH
        val cX = w / 2f

        sr.color = Color(0.68f, 0.66f, 0.56f, 1f)
        sr.triangle(20f, 300f, 260f, 300f, 135f, 435f)
        sr.triangle(180f, 300f, 410f, 300f, 295f, 395f)
        sr.triangle(710f, 300f, 940f, 300f, 825f, 405f)
        sr.triangle(860f, 300f, 1140f, 300f, 995f, 425f)
        sr.triangle(1060f, 300f, 1290f, 300f, 1190f, 445f)

        sr.color = Color(0.42f, 0.58f, 0.16f, 1f)
        sr.rect(0f, 0f, w, 335f)
        sr.ellipse(-80f, 180f, 620f, 185f)
        sr.ellipse(740f, 180f, 620f, 185f)

        val leftTrees = listOf(Vector2(45f, 250f), Vector2(280f, 325f), Vector2(325f, 338f), Vector2(365f, 342f))
        val rightTrees = listOf(Vector2(1140f, 265f), Vector2(1185f, 240f), Vector2(1230f, 215f), Vector2(1265f, 185f))
        for (t in leftTrees + rightTrees) {
            sr.color = OUTLINE
            sr.circle(t.x, t.y, 26f)
            sr.color = Color(0.32f, 0.48f, 0.12f, 1f)
            sr.circle(t.x, t.y, 23f)
            sr.color = Color(0.48f, 0.66f, 0.18f, 1f)
            sr.circle(t.x - 4f, t.y + 5f, 16f)
        }

        sr.color = Color(0.56f, 0.48f, 0.16f, 1f)
        sr.triangle(cX - 185f, 355f, cX + 185f, 355f, -140f, 0f)
        sr.triangle(cX + 185f, 355f, -140f, 0f, w + 140f, 0f)

        sr.color = Color(0.58f, 0.42f, 0.24f, 1f)
        sr.triangle(cX - 165f, 355f, cX + 165f, 355f, -90f, 0f)
        sr.triangle(cX + 165f, 355f, -90f, 0f, w + 90f, 0f)

        sr.color = Color(0.50f, 0.35f, 0.19f, 1f)
        sr.ellipse(cX - 240f, 95f, 180f, 45f)
        sr.ellipse(cX + 60f, 140f, 160f, 38f)
        sr.ellipse(cX - 110f, 25f, 210f, 42f)
        sr.ellipse(cX - 95f, 260f, 190f, 35f)

        val ax = cX - 35f
        val ay = 42f
        sr.color = Color(0.08f, 0.06f, 0.04f, 0.4f)
        sr.ellipse(ax - 12f, ay - 6f, 24f, 8f)
        sr.color = OUTLINE
        sr.rectLine(ax, ay, ax - 10f, ay + 48f, 5.5f)
        sr.color = Color(0.65f, 0.45f, 0.22f, 1f)
        sr.rectLine(ax, ay + 2f, ax - 10f, ay + 46f, 3f)
        sr.color = Color(0.92f, 0.90f, 0.82f, 1f)
        sr.triangle(ax - 10f, ay + 48f, ax - 18f, ay + 38f, ax - 7f, ay + 36f)
        sr.triangle(ax - 10f, ay + 48f, ax - 1f, ay + 41f, ax - 7f, ay + 36f)
    }

    // =========================================================================
    // LỚP 3: CÁNH TRÁI – THÁP ARCANE WIZARD TÍM, TIA SÉT & PALADIN TỎA HÀO QUANG
    // =========================================================================
    private fun drawLeftArcaneTowerAndPaladin(sr: ShapeRenderer) {
        val tx = 165f
        val ty = 235f

        sr.color = OUTLINE
        sr.triangle(tx - 82f, ty - 35f, tx + 78f, ty - 55f, tx - 48f, ty + 145f)
        sr.triangle(tx + 78f, ty - 55f, tx - 48f, ty + 145f, tx + 52f, ty + 125f)

        sr.color = Color(0.24f, 0.22f, 0.28f, 1f)
        sr.triangle(tx - 76f, ty - 32f, tx + 72f, ty - 50f, tx - 44f, ty + 138f)
        sr.triangle(tx + 72f, ty - 50f, tx - 44f, ty + 138f, tx + 46f, ty + 120f)

        sr.color = Color(0.92f, 0.22f, 0.98f, 1f)
        sr.rectLine(tx - 58f, ty - 28f, tx - 35f, ty + 115f, 4f)
        sr.rectLine(tx + 48f, ty - 44f, tx + 32f, ty + 105f, 4f)

        sr.circle(tx + 8f, ty + 35f, 16f)
        sr.color = Color(0.24f, 0.22f, 0.28f, 1f)
        sr.circle(tx + 8f, ty + 35f, 11f)
        sr.color = Color(1f, 0.55f, 1f, 1f)
        sr.circle(tx + 8f, ty + 35f, 5.5f)

        val crystalOffsets = listOf(
            Vector2(-42f, 136f), Vector2(-22f, 152f), Vector2(4f, 156f),
            Vector2(28f, 145f), Vector2(45f, 126f), Vector2(-12f, 120f), Vector2(18f, 116f)
        )
        for (c in crystalOffsets) {
            sr.color = Color(0.88f, 0.15f, 0.95f, 0.9f)
            sr.circle(tx + c.x, ty + c.y, 10f)
            sr.color = Color(1f, 0.75f, 1f, 1f)
            sr.circle(tx + c.x, ty + c.y, 5f)
        }

        sr.color = OUTLINE
        sr.circle(tx + 2f, ty + 132f, 12f)
        sr.color = Color(0.58f, 0.16f, 0.68f, 1f)
        sr.circle(tx + 2f, ty + 132f, 9.5f)

        sr.color = Color(0.35f, 0.88f, 1f, 0.9f)
        sr.rectLine(420f, 355f, 385f, 395f, 4.5f)
        sr.rectLine(385f, 395f, 355f, 388f, 4.5f)
        sr.rectLine(355f, 388f, 330f, 425f, 4.5f)
        sr.color = Color.WHITE
        sr.circle(325f, 428f, 16f)

        val px = 365f
        val py = 225f
        sr.color = Color(1f, 0.85f, 0.18f, 0.42f)
        sr.ellipse(px - 46f, py - 24f, 92f, 42f)
        sr.color = Color(1f, 0.94f, 0.45f, 0.75f)
        sr.ellipse(px - 34f, py - 18f, 68f, 30f)

        sr.color = OUTLINE
        sr.rect(px - 18f, py - 12f, 36f, 38f)
        sr.rect(px - 15f, py + 22f, 30f, 28f)
        sr.color = Color(0.86f, 0.90f, 0.95f, 1f)
        sr.rect(px - 15f, py - 9f, 30f, 32f)
        sr.rect(px - 12f, py + 24f, 24f, 23f)
        sr.color = OUTLINE
        sr.rect(px - 8f, py + 34f, 16f, 3.5f)
        sr.rect(px - 2f, py + 26f, 4f, 12f)
        sr.color = Color(0.18f, 0.62f, 0.96f, 1f)
        sr.triangle(px - 6f, py + 47f, px + 6f, py + 47f, px - 10f, py + 62f)
        sr.color = OUTLINE
        sr.rect(px + 8f, py - 8f, 24f, 32f)
        sr.color = Color(0.78f, 0.82f, 0.88f, 1f)
        sr.rect(px + 11f, py - 5f, 18f, 26f)
        sr.color = Color(1f, 0.92f, 0.25f, 0.65f)
        sr.rectLine(px - 16f, py + 18f, px - 36f, py + 76f, 14f)
        sr.color = Color.WHITE
        sr.rectLine(px - 16f, py + 18f, px - 36f, py + 76f, 6f)
    }

    // =========================================================================
    // LỚP 4: CÁNH PHẢI – NHÀ CÂY RANGERS HIDEOUT & TÊN LỬA KHÓI ĐEN
    // =========================================================================
    private fun drawRightRangerTreehouseAndMissile(sr: ShapeRenderer) {
        val smokePts = listOf(
            Vector2(1070f, 330f), Vector2(1078f, 355f), Vector2(1072f, 380f),
            Vector2(1055f, 402f), Vector2(1032f, 418f)
        )
        for (i in smokePts.indices) {
            val pt = smokePts[i]
            val r = 22f - i * 2.2f
            sr.color = OUTLINE
            sr.circle(pt.x, pt.y, r + 3f)
            sr.color = Color(0.32f, 0.28f, 0.26f, 1f)
            sr.circle(pt.x, pt.y, r)
        }
        val mx = 1005f
        val my = 428f
        sr.color = OUTLINE
        sr.circle(mx, my, 13f)
        sr.color = Color(1f, 0.65f, 0.12f, 1f)
        sr.circle(mx + 10f, my - 4f, 8f)
        sr.color = Color(0.24f, 0.24f, 0.26f, 1f)
        sr.circle(mx, my, 10f)

        val rx = 980f
        val ry = 235f
        sr.color = OUTLINE
        sr.triangle(rx - 65f, ry - 45f, rx + 75f, ry - 45f, rx - 35f, ry + 85f)
        sr.triangle(rx + 75f, ry - 45f, rx - 35f, ry + 85f, rx + 45f, ry + 85f)
        sr.color = Color(0.46f, 0.28f, 0.12f, 1f)
        sr.triangle(rx - 58f, ry - 42f, rx + 68f, ry - 42f, rx - 30f, ry + 80f)
        sr.triangle(rx + 68f, ry - 42f, rx - 30f, ry + 80f, rx + 40f, ry + 80f)

        sr.color = OUTLINE
        sr.rectLine(rx - 18f, ry - 38f, rx - 2f, ry + 55f, 3f)
        sr.rectLine(rx + 2f, ry - 38f, rx + 16f, ry + 55f, 3f)
        for (stepY in -25..45 step 14) {
            val t = (stepY + 38f) / 93f
            val lx = MathUtils.lerp(rx - 18f, rx - 2f, t)
            val rxStep = MathUtils.lerp(rx + 2f, rx + 16f, t)
            sr.rectLine(lx, ry + stepY, rxStep, ry + stepY, 2.5f)
        }

        val leafClusters = listOf(
            Vector2(-75f, 95f), Vector2(-45f, 120f), Vector2(-10f, 128f),
            Vector2(35f, 118f), Vector2(68f, 90f), Vector2(-55f, 75f), Vector2(45f, 72f)
        )
        for (lf in leafClusters) {
            sr.color = OUTLINE
            sr.circle(rx + lf.x, ry + lf.y, 32f)
        }
        for (lf in leafClusters) {
            sr.color = Color(0.32f, 0.52f, 0.14f, 1f)
            sr.circle(rx + lf.x, ry + lf.y, 29f)
            sr.color = Color(0.48f, 0.68f, 0.18f, 1f)
            sr.circle(rx + lf.x - 4f, ry + lf.y + 5f, 21f)
        }
    }

    // =========================================================================
    // LỚP 5: TIỀN CẢNH – BARBARIAN VUNG ĐẠI RÌU (TRÁI) & OGRE ĐẤU 2 FOOTMEN (PHẢI)
    // =========================================================================
    private fun drawForegroundBarbarianAndOgreBattle(sr: ShapeRenderer) {
        val bx = 285f
        val by = 65f

        sr.color = OUTLINE
        sr.rectLine(bx - 75f, by + 25f, bx - 105f, by + 135f, 14f)
        sr.circle(bx - 95f, by + 115f, 52f)
        sr.color = Color(0.48f, 0.30f, 0.14f, 1f)
        sr.rectLine(bx - 75f, by + 25f, bx - 105f, by + 135f, 9f)
        sr.color = Color(0.62f, 0.65f, 0.68f, 1f)
        sr.circle(bx - 95f, by + 115f, 47f)
        sr.color = Color(0.78f, 0.82f, 0.86f, 1f)
        sr.circle(bx - 92f, by + 115f, 36f)
        sr.color = Color(0.42f, 0.30f, 0.16f, 1f)
        sr.circle(bx - 95f, by + 115f, 10f)

        sr.color = OUTLINE
        sr.ellipse(bx - 80f, by - 75f, 175f, 135f)
        sr.circle(bx + 68f, by - 5f, 34f)
        sr.circle(bx - 65f, by + 15f, 32f)

        sr.color = Color(0.88f, 0.68f, 0.42f, 1f)
        sr.ellipse(bx - 74f, by - 70f, 163f, 125f)
        sr.circle(bx + 66f, by - 5f, 29f)
        sr.circle(bx - 63f, by + 15f, 27f)

        sr.color = OUTLINE
        sr.rect(bx - 68f, by - 68f, 135f, 42f)
        sr.color = Color(0.38f, 0.24f, 0.12f, 1f)
        sr.rect(bx - 64f, by - 68f, 127f, 36f)

        val headX = bx - 5f
        val headY = by + 68f
        val hairSpikes = listOf(
            Vector2(-58f, 15f), Vector2(-48f, 45f), Vector2(-22f, 62f),
            Vector2(12f, 58f), Vector2(42f, 42f)
        )
        sr.color = OUTLINE
        for (sp in hairSpikes) {
            sr.triangle(headX - 25f, headY + 5f, headX + 25f, headY + 5f, headX + sp.x * 1.25f, headY + sp.y * 1.25f)
        }
        sr.circle(headX, headY, 46f)

        sr.color = Color(0.88f, 0.40f, 0.12f, 1f)
        for (sp in hairSpikes) {
            sr.triangle(headX - 22f, headY + 5f, headX + 22f, headY + 5f, headX + sp.x * 1.15f, headY + sp.y * 1.15f)
        }
        sr.circle(headX - 10f, headY + 12f, 38f)

        sr.color = Color(0.92f, 0.72f, 0.46f, 1f)
        sr.circle(headX + 4f, headY - 4f, 36f)

        sr.color = OUTLINE
        sr.circle(headX - 6f, headY + 12f, 9f)
        sr.circle(headX + 18f, headY + 12f, 9f)
        sr.color = Color.WHITE
        sr.circle(headX - 6f, headY + 12f, 6.5f)
        sr.circle(headX + 18f, headY + 12f, 6.5f)
        sr.color = OUTLINE
        sr.rectLine(headX - 18f, headY + 22f, headX + 2f, headY + 17f, 4.5f)
        sr.rectLine(headX + 30f, headY + 22f, headX + 10f, headY + 17f, 4.5f)

        sr.color = OUTLINE
        sr.rect(headX - 14f, headY - 26f, 40f, 26f)
        sr.color = Color(0.45f, 0.12f, 0.10f, 1f)
        sr.rect(headX - 11f, headY - 23f, 34f, 20f)
        sr.color = Color.WHITE
        sr.rect(headX - 11f, headY - 8f, 34f, 5f)
        sr.rect(headX - 11f, headY - 23f, 34f, 5f)
        sr.color = Color(0.85f, 0.25f, 0.22f, 1f)
        sr.ellipse(headX - 4f, headY - 18f, 20f, 10f)

        val ox = 965f
        val oy = 115f

        sr.color = OUTLINE
        sr.rectLine(ox - 55f, oy + 45f, ox - 145f, oy + 125f, 34f)
        sr.color = Color(0.48f, 0.32f, 0.14f, 1f)
        sr.rectLine(ox - 55f, oy + 45f, ox - 145f, oy + 125f, 27f)
        sr.color = Color(0.62f, 0.44f, 0.20f, 1f)
        sr.rectLine(ox - 58f, oy + 50f, ox - 140f, oy + 122f, 12f)

        sr.color = OUTLINE
        sr.circle(ox + 15f, oy - 15f, 92f)
        sr.circle(ox - 55f, oy - 45f, 44f)
        sr.circle(ox - 12f, oy + 35f, 52f)

        sr.color = Color(0.36f, 0.48f, 0.14f, 1f)
        sr.circle(ox + 15f, oy - 15f, 86f)
        sr.color = Color(0.44f, 0.58f, 0.18f, 1f)
        sr.circle(ox - 52f, oy - 45f, 39f)
        sr.circle(ox - 12f, oy + 35f, 47f)

        sr.color = OUTLINE
        sr.triangle(ox + 25f, oy + 48f, ox + 58f, oy + 62f, ox + 32f, oy + 32f)
        sr.color = Color(0.48f, 0.62f, 0.20f, 1f)
        sr.triangle(ox + 26f, oy + 46f, ox + 52f, oy + 58f, ox + 32f, oy + 35f)

        sr.color = OUTLINE
        sr.rectLine(ox - 38f, oy + 58f, ox + 10f, oy + 48f, 5f)
        sr.color = Color(0.98f, 0.85f, 0.18f, 1f)
        sr.circle(ox - 24f, oy + 48f, 5f)
        sr.circle(ox + 2f, oy + 44f, 5f)

        sr.color = OUTLINE
        sr.ellipse(ox - 42f, oy - 8f, 62f, 44f)
        sr.color = Color(0.48f, 0.12f, 0.10f, 1f)
        sr.ellipse(ox - 38f, oy - 4f, 54f, 36f)
        sr.color = Color(0.88f, 0.32f, 0.25f, 1f)
        sr.ellipse(ox - 28f, oy - 2f, 36f, 20f)
        sr.color = Color(0.96f, 0.94f, 0.85f, 1f)
        sr.triangle(ox - 30f, oy + 26f, ox - 22f, oy + 26f, ox - 26f, oy + 14f)
        sr.triangle(ox - 8f, oy + 26f, ox, oy + 26f, ox - 4f, oy + 14f)
        sr.triangle(ox - 32f, oy + 2f, ox - 22f, oy + 2f, ox - 27f, oy + 16f)
        sr.triangle(ox - 6f, oy + 2f, ox + 4f, oy + 2f, ox - 1f, oy + 16f)

        drawChargingFootman(sr, 800f, 118f)
        drawChargingFootman(sr, 825f, 38f)
    }

    private fun drawChargingFootman(sr: ShapeRenderer, fx: Float, fy: Float) {
        sr.color = Color(0.08f, 0.06f, 0.04f, 0.4f)
        sr.ellipse(fx - 22f, fy - 24f, 44f, 14f)

        sr.color = OUTLINE
        sr.rectLine(fx - 12f, fy + 6f, fx - 54f, fy + 24f, 10f)
        sr.color = Color(0.85f, 0.90f, 0.96f, 1f)
        sr.rectLine(fx - 14f, fy + 7f, fx - 52f, fy + 23f, 5.5f)
        sr.color = Color(0.92f, 0.72f, 0.18f, 1f)
        sr.circle(fx - 14f, fy + 7f, 5f)

        sr.color = OUTLINE
        sr.rect(fx - 14f, fy - 20f, 28f, 28f)
        sr.circle(fx, fy + 16f, 18f)
        sr.color = Color(0.26f, 0.28f, 0.32f, 1f)
        sr.rect(fx - 11f, fy - 17f, 22f, 23f)
        sr.color = Color(0.92f, 0.74f, 0.52f, 1f)
        sr.circle(fx - 3f, fy + 13f, 13f)
        sr.color = Color(0.38f, 0.40f, 0.44f, 1f)
        sr.arc(fx, fy + 14f, 16f, -15f, 195f)

        sr.color = OUTLINE
        sr.circle(fx + 22f, fy - 2f, 19f)
        sr.color = Color(0.42f, 0.45f, 0.50f, 1f)
        sr.circle(fx + 22f, fy - 2f, 15.5f)
        sr.color = Color(0.62f, 0.65f, 0.70f, 1f)
        sr.circle(fx + 22f, fy - 2f, 6f)
    }

    // =========================================================================
    // LỚP 6: BIỂU TƯỢNG LOA/NHẠC, FACEBOOK/TWITTER & CUỘN GIẤY GÓC TRÊN
    // =========================================================================
    private fun drawTopCornerDecorations(sr: ShapeRenderer) {
        val topY = Config.WORLD_HEIGHT - 42f

        val audioOn = SoundManager.isSoundEnabled
        sr.color = OUTLINE
        sr.circle(52f, topY, 18f)
        sr.circle(102f, topY, 18f)
        sr.color = if (audioOn) Color(0.82f, 0.78f, 0.68f, 1f) else Color(0.75f, 0.25f, 0.20f, 1f)
        sr.circle(52f, topY, 14.5f)
        sr.circle(102f, topY, 14.5f)

        drawRoundedBadge(sr, 34f, topY - 82f, 52f, 52f, Color(0.36f, 0.52f, 0.76f, 1f))
        drawRoundedBadge(sr, 34f, topY - 144f, 52f, 52f, Color(0.48f, 0.78f, 0.92f, 1f))

        val sx = Config.WORLD_WIDTH - 115f
        val sy = Config.WORLD_HEIGHT - 65f
        sr.color = OUTLINE
        sr.rect(sx - 48f, sy - 22f, 96f, 44f)
        sr.circle(sx + 38f, sy, 22f)
        sr.color = Color(0.84f, 0.72f, 0.46f, 1f)
        sr.rect(sx - 44f, sy - 18f, 88f, 36f)
        sr.circle(sx + 38f, sy, 18f)
        sr.color = Color(0.45f, 0.30f, 0.14f, 1f)
        sr.circle(sx + 38f, sy, 9f)
        sr.color = Color(0.92f, 0.90f, 0.85f, 1f)
        sr.rect(sx - 12f, sy - 20f, 22f, 40f)
    }

    private fun drawRoundedBadge(sr: ShapeRenderer, x: Float, y: Float, w: Float, h: Float, fill: Color) {
        sr.color = OUTLINE
        sr.rect(x - 2.5f, y - 2.5f, w + 5f, h + 5f)
        sr.color = fill
        sr.rect(x, y, w, h)
        sr.color = Color(1f, 1f, 1f, 0.22f)
        sr.rect(x + 3f, y + h - 10f, w - 6f, 7f)
    }

    // =========================================================================
    // LỚP 7: KHIÊN BIỂU TƯỢNG KINGDOM RUSH & HAI BẢNG GỖ TREO DÂY XÍCH
    // =========================================================================
    private fun drawHangingStartAndCreditsButtons(sr: ShapeRenderer, cX: Float) {
        for (chainX in listOf(cX - 88f, cX + 80f)) {
            sr.color = OUTLINE
            sr.rect(chainX - 3f, 295f, 14f, 95f)
            sr.color = Color(0.62f, 0.65f, 0.70f, 1f)
            for (cy in 300..380 step 16) {
                sr.rect(chainX, cy.toFloat(), 8f, 11f)
            }
        }

        for (chainX in listOf(cX - 66f, cX + 58f)) {
            sr.color = OUTLINE
            sr.rect(chainX - 2f, 182f, 12f, 34f)
            sr.color = Color(0.62f, 0.65f, 0.70f, 1f)
            sr.rect(chainX, 188f, 8f, 10f)
            sr.rect(chainX, 202f, 8f, 10f)
        }

        drawIronWoodPlaqueButton(sr, cX - 115f, 212f, 230f, 88f)
        drawIronWoodPlaqueButton(sr, cX - 88f, 132f, 176f, 56f)
    }

    fun drawIronWoodPlaqueButton(sr: ShapeRenderer, x: Float, y: Float, w: Float, h: Float) {
        sr.color = Color(0f, 0f, 0f, 0.45f)
        sr.rect(x - 6f, y - 8f, w + 12f, h + 10f)
        sr.color = OUTLINE
        sr.rect(x - 4f, y - 4f, w + 8f, h + 8f)

        sr.color = Color(0.58f, 0.60f, 0.64f, 1f)
        sr.rect(x, y, w, h)

        sr.color = Color(0.28f, 0.21f, 0.14f, 1f)
        sr.rect(x + 7f, y + 7f, w - 14f, h - 14f)
        sr.color = Color(0.36f, 0.27f, 0.18f, 1f)
        sr.rect(x + 11f, y + 11f, w - 22f, h - 22f)
        sr.color = Color(0.22f, 0.16f, 0.10f, 1f)
        sr.rect(x + 14f, y + h / 2f - 2f, w - 28f, 3.5f)

        val bracketSize = 18f
        val corners = listOf(
            Vector2(x - 2f, y + h - bracketSize + 2f),
            Vector2(x + w - bracketSize + 2f, y + h - bracketSize + 2f),
            Vector2(x - 2f, y - 2f),
            Vector2(x + w - bracketSize + 2f, y - 2f)
        )
        for (c in corners) {
            sr.color = OUTLINE
            sr.rect(c.x - 1.5f, c.y - 1.5f, bracketSize + 3f, bracketSize + 3f)
            sr.color = Color(0.72f, 0.75f, 0.78f, 1f)
            sr.rect(c.x, c.y, bracketSize, bracketSize)
            sr.color = OUTLINE
            sr.circle(c.x + 5f, c.y + 5f, 2f)
            sr.circle(c.x + bracketSize - 5f, c.y + bracketSize - 5f, 2f)
        }
    }

    private fun drawKingdomRushCrestShield(sr: ShapeRenderer, cX: Float, logoY: Float) {
        for (side in listOf(-1f, 1f)) {
            sr.color = OUTLINE
            sr.circle(cX + side * 125f, logoY + 88f, 32f)
            sr.circle(cX + side * 185f, logoY - 38f, 30f)
            sr.color = Color(0.88f, 0.32f, 0.08f, 1f)
            sr.circle(cX + side * 125f, logoY + 88f, 27f)
            sr.circle(cX + side * 185f, logoY - 38f, 25f)

            val hiltX = cX + side * 68f
            val hiltY = logoY + 112f
            sr.color = OUTLINE
            sr.rectLine(cX + side * 35f, logoY + 65f, hiltX, hiltY, 26f)
            sr.circle(hiltX, hiltY, 16f)
            sr.color = Color(0.55f, 0.26f, 0.12f, 1f)
            sr.rectLine(cX + side * 35f, logoY + 65f, hiltX, hiltY, 19f)
            sr.color = Color(0.98f, 0.78f, 0.15f, 1f)
            sr.circle(hiltX, hiltY, 12f)
        }

        sr.color = OUTLINE
        sr.circle(cX, logoY, 126f)
        sr.triangle(cX - 18f, logoY - 118f, cX + 18f, logoY - 118f, cX, logoY - 144f)
        sr.triangle(cX - 65f, logoY - 95f, cX - 38f, logoY - 112f, cX - 75f, logoY - 130f)
        sr.triangle(cX + 65f, logoY - 95f, cX + 38f, logoY - 112f, cX + 75f, logoY - 130f)

        sr.color = Color(0.76f, 0.78f, 0.82f, 1f)
        sr.circle(cX, logoY, 118f)
        sr.color = Color(0.52f, 0.54f, 0.58f, 1f)
        sr.circle(cX, logoY, 96f)

        sr.color = OUTLINE
        for (a in 0 until 360 step 30) {
            val rad = Math.toRadians(a.toDouble())
            val rx = cX + (107f * Math.cos(rad)).toFloat()
            val ry = logoY + (107f * Math.sin(rad)).toFloat()
            sr.circle(rx, ry, 3.5f)
        }

        val plaqueW = 470f
        val plaqueH = 128f
        sr.color = OUTLINE
        sr.rect(cX - plaqueW / 2f - 6f, logoY - 38f, plaqueW + 12f, plaqueH + 12f)
        sr.color = Color(0.32f, 0.22f, 0.14f, 1f)
        sr.rect(cX - plaqueW / 2f, logoY - 32f, plaqueW, plaqueH)
        sr.color = Color(0.46f, 0.33f, 0.20f, 1f)
        sr.rect(cX - plaqueW / 2f + 6f, logoY - 26f, plaqueW - 12f, plaqueH - 12f)

        val rushW = 250f
        val rushH = 62f
        sr.color = OUTLINE
        sr.rect(cX - rushW / 2f - 5f, logoY - 88f, rushW + 10f, rushH + 10f)
        sr.color = Color(0.58f, 0.18f, 0.08f, 1f)
        sr.rect(cX - rushW / 2f, logoY - 83f, rushW, rushH)
    }

    // =========================================================================
    // LỚP 8: VẼ CHỮ LOGO 3D "KINGDOM RUSH", NÚT START/CREDITS & CÁC NHÃN GÓC
    // =========================================================================
    private fun drawLogoAndMenuTexts(
        batch: SpriteBatch,
        cX: Float,
        logoY: Float,
        logoMainFont: BitmapFont,
        logoSubFont: BitmapFont,
        titleFont: BitmapFont,
        font: BitmapFont,
        smallFont: BitmapFont,
        isSlotPopupOpen: Boolean,
        isCreditsPopupOpen: Boolean
    ) {
        val w = Config.WORLD_WIDTH
        val topY = Config.WORLD_HEIGHT - 42f

        val kY = logoY + 62f
        logoMainFont.color = OUTLINE
        for (dx in listOf(-4f, 0f, 4f)) {
            for (dy in listOf(-6f, -2f, 3f)) {
                logoMainFont.draw(batch, "KINGDOM", dx, kY + dy, w, Align.center, false)
            }
        }
        logoMainFont.color = Color(0.85f, 0.48f, 0.08f, 1f)
        logoMainFont.draw(batch, "KINGDOM", 0f, kY - 3f, w, Align.center, false)
        logoMainFont.color = Color(1f, 0.92f, 0.22f, 1f)
        logoMainFont.draw(batch, "KINGDOM", 0f, kY, w, Align.center, false)

        val rY = logoY - 36f
        logoSubFont.color = OUTLINE
        for (dx in listOf(-3f, 0f, 3f)) {
            for (dy in listOf(-4f, 0f, 3f)) {
                logoSubFont.draw(batch, "RUSH", dx, rY + dy, w, Align.center, false)
            }
        }
        logoSubFont.color = Color(0.95f, 0.36f, 0.12f, 1f)
        logoSubFont.draw(batch, "RUSH", 0f, rY, w, Align.center, false)

        val sText = if (SoundManager.isSoundEnabled) "S" else "X"
        val mText = if (SoundManager.isMusicEnabled) "M" else "X"
        GameUIRenderer.drawOutlinedText(batch, smallFont, sText, 47f, topY + 6f, Color.WHITE)
        GameUIRenderer.drawOutlinedText(batch, smallFont, mText, 96f, topY + 6f, Color.WHITE)

        GameUIRenderer.drawBadgeCenteredText(batch, titleFont, "f", 34f, topY - 82f, 52f, 52f, Color.WHITE)
        GameUIRenderer.drawBadgeCenteredText(batch, titleFont, "t", 34f, topY - 144f, 52f, 52f, Color.WHITE)
        GameUIRenderer.drawOutlinedText(batch, titleFont, "1", Config.WORLD_WIDTH - 122f, Config.WORLD_HEIGHT - 38f, Color(0.92f, 0.22f, 0.15f, 1f))

        if (!isSlotPopupOpen && !isCreditsPopupOpen) {
            GameUIRenderer.drawBadgeCenteredText(
                batch, titleFont, "START",
                cX - 115f, 212f, 230f, 88f,
                Color(1f, 0.86f, 0.18f, 1f)
            )
            GameUIRenderer.drawBadgeCenteredText(
                batch, font, "CREDITS",
                cX - 88f, 132f, 176f, 56f,
                Color(1f, 0.86f, 0.18f, 1f)
            )
        }

        GameUIRenderer.drawShadowedText(batch, smallFont, "v1.072", Config.WORLD_WIDTH - 68f, 22f, Color(0.90f, 0.86f, 0.74f, 1f))
    }

    // =========================================================================
    // LỚP 9: BẢNG CHỌN PHẦN LƯU GAME "LOCAL SAVE"
    // =========================================================================
    private fun renderLocalSavePopup(
        shapeRenderer: ShapeRenderer,
        batch: SpriteBatch,
        font: BitmapFont,
        italicFont: BitmapFont,
        smallFont: BitmapFont
    ) {
        val cX = Config.WORLD_WIDTH / 2f
        val panelW = 410f
        val panelH = 420f
        val panelX = cX - panelW / 2f
        val panelY = 58f

        val startY = panelY + panelH - 82f
        val cardW = 352f
        val cardH = 92f

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color(0f, 0f, 0f, 0.35f)
        shapeRenderer.rect(panelX - 10f, panelY - 44f, panelW + 20f, panelH + 92f)

        val tabW = 240f
        val tabH = 44f
        shapeRenderer.color = OUTLINE
        shapeRenderer.triangle(cX - tabW / 2f - 18f, panelY + panelH, cX + tabW / 2f + 18f, panelY + panelH, cX - tabW / 2f, panelY + panelH + tabH)
        shapeRenderer.triangle(cX + tabW / 2f + 18f, panelY + panelH, cX - tabW / 2f, panelY + panelH + tabH, cX + tabW / 2f, panelY + panelH + tabH)
        shapeRenderer.color = Color(0.62f, 0.64f, 0.68f, 1f)
        shapeRenderer.rect(cX - tabW / 2f, panelY + panelH - 2f, tabW, tabH - 2f)
        shapeRenderer.color = Color(0.32f, 0.25f, 0.18f, 1f)
        shapeRenderer.rect(cX - tabW / 2f + 6f, panelY + panelH, tabW - 12f, tabH - 8f)

        val qX = cX + tabW / 2f + 26f
        val qY = panelY + panelH + 14f
        shapeRenderer.color = OUTLINE
        shapeRenderer.circle(qX, qY, 16f)
        shapeRenderer.color = Color(0.42f, 0.35f, 0.28f, 1f)
        shapeRenderer.circle(qX, qY, 13f)

        val closeW = 170f
        val closeH = 36f
        shapeRenderer.color = OUTLINE
        shapeRenderer.rect(cX - closeW / 2f - 4f, panelY - closeH - 4f, closeW + 8f, closeH + 6f)
        shapeRenderer.color = Color(0.55f, 0.56f, 0.58f, 1f)
        shapeRenderer.rect(cX - closeW / 2f, panelY - closeH, closeW, closeH)
        shapeRenderer.color = Color(0.42f, 0.40f, 0.36f, 1f)
        shapeRenderer.rect(cX - closeW / 2f + 4f, panelY - closeH + 4f, closeW - 8f, closeH - 8f)

        drawIronWoodPlaqueButton(shapeRenderer, panelX, panelY, panelW, panelH)

        for (i in 1..3) {
            val cardY = startY - (i - 1) * 114f
            val cardX = cX - cardW / 2f
            val bottom = cardY - cardH / 2f
            val hasSave = SaveManager.hasData(i)

            shapeRenderer.color = OUTLINE
            shapeRenderer.rect(cardX - 3f, bottom - 3f, cardW + 6f, cardH + 6f)
            shapeRenderer.color = Color(0.68f, 0.66f, 0.60f, 1f)
            shapeRenderer.rect(cardX, bottom, cardW, cardH)
            shapeRenderer.color = if (hasSave) Color(0.24f, 0.18f, 0.13f, 1f) else Color(0.32f, 0.25f, 0.18f, 1f)
            shapeRenderer.rect(cardX + 5f, bottom + 5f, cardW - 10f, cardH - 10f)

            shapeRenderer.color = Color(0.20f, 0.15f, 0.10f, 0.65f)
            shapeRenderer.rect(cardX + 12f, cardY + 8f, cardW - 24f, 3f)
            shapeRenderer.rect(cardX + 12f, cardY - 18f, cardW - 24f, 3f)

            if (hasSave) {
                val delX = cardX + cardW - 30f
                val delY = cardY + 20f
                shapeRenderer.color = OUTLINE
                shapeRenderer.circle(delX, delY, 13f)
                shapeRenderer.color = Color(0.75f, 0.78f, 0.82f, 1f)
                shapeRenderer.rectLine(delX - 6f, delY - 6f, delX + 6f, delY + 6f, 4f)
                shapeRenderer.rectLine(delX - 6f, delY + 6f, delX + 6f, delY - 6f, 4f)
            }
        }
        shapeRenderer.end()

        for (i in 1..3) {
            val cardY = startY - (i - 1) * 114f
            val cardX = cX - cardW / 2f
            if (SaveManager.hasData(i)) {
                GameUIRenderer.drawStar(shapeRenderer, cardX + 32f, cardY - 15f, 11f, true)

                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
                val shX = cardX + 152f
                val shY = cardY - 15f
                shapeRenderer.color = Color(0.95f, 0.75f, 0.15f, 1f)
                shapeRenderer.circle(shX, shY + 3f, 11f)
                shapeRenderer.triangle(shX - 11f, shY + 3f, shX + 11f, shY + 3f, shX, shY - 11f)
                shapeRenderer.color = Color(0.72f, 0.76f, 0.82f, 1f)
                shapeRenderer.circle(shX, shY + 3f, 7.5f)
                shapeRenderer.triangle(shX - 7.5f, shY + 3f, shX + 7.5f, shY + 3f, shX, shY - 7.5f)

                val fistX = cardX + 245f
                val fistY = cardY - 15f
                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(fistX - 9f, fistY - 10f, 18f, 20f)
                shapeRenderer.color = Color(0.68f, 0.72f, 0.78f, 1f)
                shapeRenderer.rect(fistX - 7f, fistY - 8f, 14f, 16f)
                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(fistX - 7f, fistY + 1f, 14f, 2f)
                shapeRenderer.end()
            }
        }

        batch.begin()
        GameUIRenderer.drawBadgeCenteredText(
            batch, font, "LOCAL SAVE",
            cX - 120f, panelY + panelH - 2f, 240f, 40f,
            Color(0.98f, 0.94f, 0.82f, 1f)
        )
        GameUIRenderer.drawOutlinedText(batch, smallFont, "?", cX + 141f, panelY + panelH + 20f, Color(0.95f, 0.90f, 0.75f, 1f))
        GameUIRenderer.drawBadgeCenteredText(
            batch, smallFont, "CLOSE",
            cX - 85f, panelY - 36f, 170f, 36f,
            Color(0.95f, 0.92f, 0.84f, 1f)
        )

        for (i in 1..3) {
            val cardY = startY - (i - 1) * 114f
            val cardX = cX - cardW / 2f
            val hasSave = SaveManager.hasData(i)

            if (hasSave) {
                val (campStars, chalStars, totalStars) = SaveManager.getSlotStats(i)
                GameUIRenderer.drawOutlinedText(batch, italicFont, "SLOT $i", cardX + 20f, cardY + 26f, Color.WHITE)
                GameUIRenderer.drawOutlinedText(batch, font, "$totalStars/60", cardX + 50f, cardY - 8f, Color(1f, 0.90f, 0.25f, 1f))
                GameUIRenderer.drawOutlinedText(batch, font, "$campStars", cardX + 172f, cardY - 8f, Color.WHITE)
                GameUIRenderer.drawOutlinedText(batch, font, "$chalStars", cardX + 265f, cardY - 8f, Color.WHITE)
            } else {
                GameUIRenderer.drawBadgeCenteredText(
                    batch, font, "NEW GAME",
                    cardX, cardY - cardH / 2f, cardW, cardH,
                    Color(0.98f, 0.95f, 0.86f, 1f)
                )
            }
        }
        batch.end()
    }

    // =========================================================================
    // LỚP 10: BẢNG CREDITS KHI BẤM NÚT "CREDITS"
    // =========================================================================
    private fun renderCreditsPopup(
        shapeRenderer: ShapeRenderer,
        batch: SpriteBatch,
        titleFont: BitmapFont,
        font: BitmapFont,
        smallFont: BitmapFont
    ) {
        val cX = Config.WORLD_WIDTH / 2f
        val cY = Config.WORLD_HEIGHT / 2f
        val boxW = 460f
        val boxH = 310f
        val boxX = cX - boxW / 2f
        val boxY = cY - boxH / 2f

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color(0f, 0f, 0f, 0.55f)
        shapeRenderer.rect(0f, 0f, Config.WORLD_WIDTH, Config.WORLD_HEIGHT)
        drawIronWoodPlaqueButton(shapeRenderer, boxX, boxY, boxW, boxH)
        drawIronWoodPlaqueButton(shapeRenderer, cX - 75f, boxY - 26f, 150f, 44f)
        shapeRenderer.end()

        batch.begin()
        GameUIRenderer.drawBadgeCenteredText(batch, titleFont, "CREDITS", boxX, boxY + boxH - 72f, boxW, 50f, Color.GOLD)
        GameUIRenderer.drawBadgeCenteredText(batch, font, "KINGDOM RUSH - LIBGDX EDITION", boxX, cY + 15f, boxW, 30f, Color.WHITE)
        GameUIRenderer.drawBadgeCenteredText(batch, smallFont, "INSPIRED BY IRONHIDE GAME STUDIO", boxX, cY - 25f, boxW, 25f, Color(0.95f, 0.85f, 0.55f, 1f))
        GameUIRenderer.drawBadgeCenteredText(batch, smallFont, "CUSTOM VECTOR ART & ECS ENGINE", boxX, cY - 58f, boxW, 25f, Color.LIGHT_GRAY)
        GameUIRenderer.drawBadgeCenteredText(batch, font, "CLOSE", cX - 75f, boxY - 26f, 150f, 44f, Color.YELLOW)
        batch.end()
    }
}
