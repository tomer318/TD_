package com.game.td.renderers.maps

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2

object Level6MapRenderer {

    fun render(shapeRenderer: ShapeRenderer) {
        // 1. TƯỜNG THÀNH ĐÁ XÁM PHÍA BẮC (THE CITADEL WALLS)
        shapeRenderer.color = Color(0.48f, 0.50f, 0.54f, 1f)
        shapeRenderer.rect(0f, 640f, 1280f, 80f)

        // Các gờ đá tường thành
        shapeRenderer.color = Color(0.36f, 0.38f, 0.42f, 1f)
        for (x in 0..1280 step 42) {
            shapeRenderer.rect(x.toFloat(), 695f, 24f, 25f)
        }

        // Cổng vòm lối vào lâu đài
        shapeRenderer.color = Color(0.24f, 0.20f, 0.16f, 1f)
        shapeRenderer.rect(590f, 640f, 100f, 75f)
        shapeRenderer.circle(640f, 715f, 50f)

        // Đường lát đá hoa cương vào thành
        shapeRenderer.color = Color(0.60f, 0.58f, 0.52f, 1f)
        shapeRenderer.rect(595f, 640f, 90f, 40f)

        // 2. KHU VỰC RỪNG RẬM HAI BÊN CÁNH
        shapeRenderer.color = Color(0.18f, 0.46f, 0.16f, 1f)
        shapeRenderer.circle(100f, 500f, 95f)
        shapeRenderer.circle(180f, 430f, 85f)
        shapeRenderer.circle(90f, 360f, 90f)
        shapeRenderer.circle(130f, 300f, 65f)
        shapeRenderer.circle(60f, 100f, 85f)

        shapeRenderer.circle(1180f, 500f, 95f)
        shapeRenderer.circle(1100f, 430f, 85f)
        shapeRenderer.circle(1190f, 360f, 90f)
        shapeRenderer.circle(1150f, 300f, 65f)
        shapeRenderer.circle(1220f, 100f, 85f)

        shapeRenderer.circle(360f, 40f, 75f)
        shapeRenderer.circle(920f, 40f, 75f)

        shapeRenderer.color = Color(0.25f, 0.56f, 0.20f, 1f)
        shapeRenderer.circle(100f, 510f, 70f)
        shapeRenderer.circle(180f, 440f, 65f)
        shapeRenderer.circle(1180f, 510f, 70f)
        shapeRenderer.circle(1100f, 440f, 65f)

        // 3. CÁC ĐOẠN ĐƯỜNG THẲNG ĐÃ DỊCH XUỐNG 50PX
        val straightSegments = listOf(
            // Trục đáy nối 2 cổng Tây - Đông (ở y = 210f)
            Pair(Vector2(-40f, 210f), Vector2(1320f, 210f)),

            // Trục Nam đâm lên đáy (ở y = 210f)
            Pair(Vector2(640f, -40f), Vector2(640f, 210f)),

            // Trục giữa (ở y = 380f)
            Pair(Vector2(330f, 380f), Vector2(950f, 380f)),

            // Trục trên trước cổng (ở y = 525f)
            Pair(Vector2(370f, 525f), Vector2(910f, 525f)),

            // Đường dẫn vào cổng thành kéo dài thông suốt
            Pair(Vector2(640f, 380f), Vector2(640f, 650f)),

            // Đoạn nối góc đáy Tây
            Pair(Vector2(350f, 210f), Vector2(340f, 270f)),

            // Đoạn nối góc đáy Đông
            Pair(Vector2(930f, 210f), Vector2(940f, 270f))
        )

        // Viền đất đậm
        shapeRenderer.color = Color(0.55f, 0.44f, 0.26f, 1f)
        for (seg in straightSegments) {
            shapeRenderer.rectLine(seg.first, seg.second, 78f)
            shapeRenderer.circle(seg.first.x, seg.first.y, 39f)
            shapeRenderer.circle(seg.second.x, seg.second.y, 39f)
        }

        // Viền đất sườn cong
        drawCurvedFlank(shapeRenderer, isWest = true, isBorder = true)
        drawCurvedFlank(shapeRenderer, isWest = false, isBorder = true)

        // Lòng đường cát vàng
        shapeRenderer.color = Color(0.82f, 0.72f, 0.46f, 1f)
        for (seg in straightSegments) {
            shapeRenderer.rectLine(seg.first, seg.second, 64f)
            shapeRenderer.circle(seg.first.x, seg.first.y, 32f)
            shapeRenderer.circle(seg.second.x, seg.second.y, 32f)
        }

        drawCurvedFlank(shapeRenderer, isWest = true, isBorder = false)
        drawCurvedFlank(shapeRenderer, isWest = false, isBorder = false)

        shapeRenderer.circle(640f, 525f, 46f)
        shapeRenderer.circle(640f, 380f, 38f)

        // 4. BA ĐỒI CỎ NẰM GỌN TRONG CÁC Ô ĐƯỜNG (DỊCH XUỐNG 50PX)
        drawGrassHill(shapeRenderer, 515f, 455f, 120f, 48f)
        drawGrassHill(shapeRenderer, 765f, 455f, 120f, 48f)
        drawGrassHill(shapeRenderer, 640f, 305f, 230f, 48f)

        // 5. BỜ ĐẤT 4 CỌC TRÊN Ở Y=590F (NẰM THOÁNG TRÊN THẢM CỎ KHÔNG ĐÈ TƯỜNG THÀNH)
        shapeRenderer.color = Color(0.32f, 0.56f, 0.18f, 1f)
        shapeRenderer.rect(440f, 574f, 145f, 28f)
        shapeRenderer.circle(440f, 588f, 14f)
        shapeRenderer.circle(585f, 588f, 14f)

        shapeRenderer.rect(695f, 574f, 145f, 28f)
        shapeRenderer.circle(695f, 588f, 14f)
        shapeRenderer.circle(840f, 588f, 14f)

        // 6. VÒNG TRÒN PHÒNG THỦ DƯỚI ĐẤT & 2 CỜ HIỆU LINIREA TRƯỚC CỔNG THÀNH CITADEL
        drawGroundCrest(shapeRenderer, 640f, 616f)
        drawLinireaBanner(shapeRenderer, 594f, 622f)
        drawLinireaBanner(shapeRenderer, 686f, 622f)
    }

    private fun drawGroundCrest(sr: ShapeRenderer, cx: Float, cy: Float) {
        sr.color = Color(0.18f, 0.45f, 0.78f, 0.55f)
        sr.ellipse(cx - 28f, cy - 19f, 56f, 38f)
        sr.color = Color(0.52f, 0.84f, 1f, 0.65f)
        sr.ellipse(cx - 22f, cy - 15f, 44f, 30f)
        sr.color = Color(0.82f, 0.72f, 0.46f, 0.90f)
        sr.ellipse(cx - 16f, cy - 11f, 32f, 22f)
        sr.color = Color(0.22f, 0.56f, 0.88f, 0.65f)
        sr.rect(cx - 7f, cy - 2f, 14f, 9f)
        sr.triangle(cx - 7f, cy - 2f, cx + 7f, cy - 2f, cx, cy - 9f)
    }

    private fun drawLinireaBanner(sr: ShapeRenderer, x: Float, y: Float) {
        val outline = Color(0.12f, 0.09f, 0.07f, 1f)
        sr.color = Color(0f, 0f, 0f, 0.28f)
        sr.ellipse(x - 10f, y - 5f, 20f, 8f)

        sr.color = outline
        sr.rect(x - 2.2f, y - 2f, 4.4f, 40f)
        sr.circle(x, y + 39f, 3.2f)
        sr.rect(x - 12f, y + 31f, 24f, 4f)
        sr.color = Color(0.55f, 0.36f, 0.18f, 1f)
        sr.rect(x - 1.2f, y, 2.4f, 37f)
        sr.color = Color(0.95f, 0.78f, 0.20f, 1f)
        sr.circle(x, y + 39f, 2f)

        sr.color = outline
        sr.rect(x - 10f, y + 15f, 20f, 17f)
        sr.triangle(x - 10f, y + 15f, x + 10f, y + 15f, x, y + 6f)
        sr.color = Color(0.18f, 0.52f, 0.88f, 1f)
        sr.rect(x - 8.5f, y + 16f, 17f, 15f)
        sr.triangle(x - 8.5f, y + 16f, x + 8.5f, y + 16f, x, y + 8f)

        sr.color = Color(0.82f, 0.94f, 1f, 1f)
        sr.rect(x - 4.5f, y + 19f, 9f, 8f)
        sr.triangle(x - 4.5f, y + 19f, x + 4.5f, y + 19f, x, y + 14f)
        sr.color = Color(0.18f, 0.52f, 0.88f, 1f)
        sr.circle(x, y + 23f, 2f)
    }
    private fun drawCurvedFlank(sr: ShapeRenderer, isWest: Boolean, isBorder: Boolean) {
        val width = if (isBorder) 78f else 64f
        val color = if (isBorder) Color(0.55f, 0.44f, 0.26f, 1f) else Color(0.82f, 0.72f, 0.46f, 1f)
        sr.color = color

        val points = if (isWest) {
            listOf(
                Vector2(340f, 270f),
                Vector2(320f, 330f),
                Vector2(315f, 390f),
                Vector2(330f, 460f),
                Vector2(370f, 525f)
            )
        } else {
            listOf(
                Vector2(940f, 270f),
                Vector2(960f, 330f),
                Vector2(965f, 390f),
                Vector2(950f, 460f),
                Vector2(910f, 525f)
            )
        }

        for (i in 0 until points.size - 1) {
            sr.rectLine(points[i], points[i + 1], width)
            sr.circle(points[i].x, points[i].y, width / 2f)
            sr.circle(points[i + 1].x, points[i + 1].y, width / 2f)
        }
    }

    private fun drawGrassHill(sr: ShapeRenderer, cx: Float, cy: Float, width: Float, height: Float) {
        val halfW = width / 2f
        val radius = height / 2f

        sr.color = Color(0.24f, 0.45f, 0.14f, 1f)
        sr.rect(cx - halfW, cy - radius - 3f, width, height + 6f)
        sr.circle(cx - halfW, cy, radius + 3f)
        sr.circle(cx + halfW, cy, radius + 3f)

        sr.color = Color(0.38f, 0.65f, 0.22f, 1f)
        sr.rect(cx - halfW, cy - radius, width, height)
        sr.circle(cx - halfW, cy, radius)
        sr.circle(cx + halfW, cy, radius)
    }
}
