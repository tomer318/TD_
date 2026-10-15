package com.game.td.renderers.maps

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.game.td.map.Level8IcewindPass

object Level8MapRenderer {

    fun render(shapeRenderer: ShapeRenderer) {
        // 1. NỀN TUYẾT TRẮNG
        shapeRenderer.color = Color(0.91f, 0.94f, 0.97f, 1f)
        shapeRenderer.rect(0f, 0f, 1280f, 720f)

        // 2. CÁC KHỐI NÚI ĐÁ TUYẾT TRẮNG XÁM
        shapeRenderer.color = Color(0.50f, 0.54f, 0.58f, 1f)
        // Núi lớn mạn Tây Bắc
        shapeRenderer.triangle(0f, 720f, 480f, 720f, 0f, 300f)
        shapeRenderer.triangle(0f, 350f, 250f, 280f, 0f, 180f)
        // Núi lớn mạn Đông (quanh hang mỏ dưới)
        shapeRenderer.triangle(1280f, 480f, 960f, 320f, 1280f, 160f)
        shapeRenderer.triangle(1280f, 720f, 1020f, 720f, 1280f, 450f)

        // Đỉnh núi phủ tuyết sáng
        shapeRenderer.color = Color(0.96f, 0.98f, 1f, 1f)
        shapeRenderer.triangle(0f, 720f, 260f, 720f, 0f, 480f)
        shapeRenderer.triangle(1280f, 720f, 1140f, 720f, 1280f, 560f)

        // 3. VẼ CÁC ĐOẠN ĐƯỜNG KHỚP NÉT VẼ TAY CỦA BẠN[cite: 89]
        val pathMain = Level8IcewindPass.pathMain
        val pathCaveIn = listOf(
            Vector2(900f, 530f),
            Vector2(780f, 550f),
            Vector2(680f, 610f),
            Vector2(620f, 660f)
        )
        val pathCaveOut = listOf(
            Vector2(920f, 370f),
            Vector2(850f, 330f),
            Vector2(780f, 250f),
            Vector2(660f, 220f)
        )
        val pathSouthEast = listOf(
            Vector2(1320f, 130f),
            Vector2(1120f, 130f),
            Vector2(950f, 150f),
            Vector2(800f, 185f),
            Vector2(660f, 220f)
        )

        // A. Lớp viền đất tuyết đậm (Rộng 72px)
        shapeRenderer.color = Color(0.74f, 0.77f, 0.82f, 1f)
        drawPath(shapeRenderer, pathMain, 72f)
        drawPath(shapeRenderer, pathCaveIn, 72f)
        drawPath(shapeRenderer, pathCaveOut, 72f)
        drawPath(shapeRenderer, pathSouthEast, 72f)

        // B. Lòng đường đất tuyết sáng (Rộng 56px)
        shapeRenderer.color = Color(0.86f, 0.89f, 0.93f, 1f)
        drawPath(shapeRenderer, pathMain, 56f)
        drawPath(shapeRenderer, pathCaveIn, 56f)
        drawPath(shapeRenderer, pathCaveOut, 56f)
        drawPath(shapeRenderer, pathSouthEast, 56f)

        // 4. HAI CỬA HANG MỎ (CỬA TRÊN TÂY BẮC & CỬA DƯỚI ĐÔNG NAM)[cite: 89]
        drawCave(shapeRenderer, 620f, 660f) // Hang trên
        drawCave(shapeRenderer, 920f, 370f) // Hang dưới

        // 5. CÁC GỜ ĐẤT NỔI ĐẶT CỌC THÁP KHỚP THEO CÁC VÒNG TÍM BẠN ĐÃ KHOANH[cite: 79]
        // Gờ tầng trên (3 cọc): tâm y=440f[cite: 79]
        drawPlateau(shapeRenderer, 710f, 425f, 250f, 40f)

        // Gờ tầng giữa (3 cọc): tâm y=275f[cite: 79]
        drawPlateau(shapeRenderer, 590f, 288f, 250f, 40f)

        // Gờ cọc lẻ sườn Tây (1 cọc)[cite: 79]
        drawPlateau(shapeRenderer, 350f, 255f, 50f, 40f)

        // Gờ chân mỏ Đông Nam (2 cọc)[cite: 79]
        drawPlateau(shapeRenderer, 923f, 237f, 150f, 40f)

        // 6. RỪNG THÔNG TUYẾT TRÊN VÀ DƯỚI RÌA ĐƯỜNG (THEO ĐƯỜNG XANH LÁ CỦA BẠN)[cite: 79]
        // Dải thông Đông Bắc (phía trên đường đỏ)[cite: 79]
        for (x in 780..1240 step 45) {
            drawPine(shapeRenderer, x.toFloat(), 640f)
        }
        // Dải thông đáy Nam (dưới mép đường)[cite: 79]
        for (x in 50..460 step 42) {
            drawPine(shapeRenderer, x.toFloat(), 38f)
        }
        for (x in 700..1220 step 42) {
            drawPine(shapeRenderer, x.toFloat(), 38f)
        }
    }

    private fun drawPath(sr: ShapeRenderer, pts: List<Vector2>, width: Float) {
        val r = width / 2f
        for (i in 0 until pts.size - 1) {
            sr.rectLine(pts[i], pts[i + 1], width)
            sr.circle(pts[i].x, pts[i].y, r)
        }
        if (pts.isNotEmpty()) {
            sr.circle(pts.last().x, pts.last().y, r)
        }
    }

    private fun drawCave(sr: ShapeRenderer, x: Float, y: Float) {
        sr.color = Color(0.12f, 0.12f, 0.14f, 1f)
        sr.rect(x - 24f, y - 20f, 48f, 36f)
        sr.circle(x, y + 16f, 24f)
        sr.color = Color(0.42f, 0.28f, 0.16f, 1f)
        sr.rect(x - 26f, y - 22f, 8f, 42f)
        sr.rect(x + 18f, y - 22f, 8f, 42f)
        sr.rect(x - 26f, y + 16f, 52f, 8f)
    }

    private fun drawPlateau(sr: ShapeRenderer, cx: Float, cy: Float, w: Float, h: Float) {
        val hw = w / 2f
        val rad = h / 2f
        sr.color = Color(0.72f, 0.75f, 0.80f, 1f)
        sr.rect(cx - hw, cy - rad - 2f, w, h + 4f)
        sr.circle(cx - hw, cy, rad + 2f)
        sr.circle(cx + hw, cy, rad + 2f)

        sr.color = Color(0.86f, 0.89f, 0.93f, 1f)
        sr.rect(cx - hw, cy - rad, w, h)
        sr.circle(cx - hw, cy, rad)
        sr.circle(cx + hw, cy, rad)
    }

    private fun drawPine(sr: ShapeRenderer, x: Float, y: Float) {
        sr.color = Color(0.35f, 0.22f, 0.12f, 1f)
        sr.rect(x - 3f, y - 10f, 6f, 14f)
        sr.color = Color(0.20f, 0.40f, 0.28f, 1f)
        sr.triangle(x - 20f, y, x + 20f, y, x, y + 26f)
        sr.triangle(x - 16f, y + 14f, x + 16f, y + 14f, x, y + 38f)
        sr.color = Color(0.92f, 0.95f, 0.98f, 0.9f)
        sr.triangle(x - 8f, y + 26f, x + 8f, y + 26f, x, y + 40f)
    }
}
