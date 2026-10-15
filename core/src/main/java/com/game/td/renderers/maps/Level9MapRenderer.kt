package com.game.td.renderers.maps

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.game.td.map.Level9StormcloudTemple

object Level9MapRenderer {

    fun render(shapeRenderer: ShapeRenderer) {
        // 1. NỀN TUYẾT TRẮNG
        shapeRenderer.color = Color(0.91f, 0.94f, 0.97f, 1f)
        shapeRenderer.rect(0f, 0f, 1280f, 720f)

        // 2. KHỐI ĐỀN THỜ STORMCLOUD BÊN TÂY
        shapeRenderer.color = Color(0.42f, 0.44f, 0.48f, 1f)
        shapeRenderer.rect(0f, 150f, 260f, 340f)
        shapeRenderer.color = Color(0.28f, 0.30f, 0.34f, 1f)
        shapeRenderer.rect(0f, 260f, 260f, 120f)

        // Các cột đá của đền
        shapeRenderer.color = Color(0.60f, 0.58f, 0.48f, 1f)
        shapeRenderer.rect(20f, 170f, 28f, 300f)
        shapeRenderer.rect(85f, 170f, 28f, 300f)
        shapeRenderer.rect(150f, 170f, 28f, 300f)

        // Cây cầu bắc ngang qua vực vào đền (tâm y = 320f)
        shapeRenderer.color = Color(0.48f, 0.42f, 0.35f, 1f)
        shapeRenderer.rect(220f, 270f, 120f, 100f)

        // Vực thẳm sâu tối hai bên cầu
        shapeRenderer.color = Color(0.18f, 0.20f, 0.24f, 1f)
        shapeRenderer.rect(200f, 370f, 30f, 350f)
        shapeRenderer.rect(200f, 0f, 30f, 270f)

        // 3. VÁCH NÚI ĐÁ TUYẾT ĐÔNG BẮC
        shapeRenderer.color = Color(0.50f, 0.54f, 0.58f, 1f)
        shapeRenderer.triangle(1280f, 720f, 850f, 720f, 1280f, 250f)
        shapeRenderer.color = Color(0.95f, 0.97f, 1.0f, 1f)
        shapeRenderer.triangle(1280f, 720f, 1050f, 720f, 1280f, 480f)

        // 4. HỆ THỐNG 3 ĐƯỜNG ĐI
        val pathN = Level9StormcloudTemple.pathNorth
        val pathC = Level9StormcloudTemple.pathCave
        val pathS = Level9StormcloudTemple.pathSouth

        // Viền đường đậm (72px)
        shapeRenderer.color = Color(0.74f, 0.77f, 0.82f, 1f)
        drawPath(shapeRenderer, pathN, 72f)
        drawPath(shapeRenderer, pathC, 72f)
        drawPath(shapeRenderer, pathS, 72f)

        // Lòng đường tuyết sáng (56px)
        shapeRenderer.color = Color(0.86f, 0.89f, 0.93f, 1f)
        drawPath(shapeRenderer, pathN, 56f)
        drawPath(shapeRenderer, pathC, 56f)
        drawPath(shapeRenderer, pathS, 56f)

        // 5. CÁC GỜ ĐẤT ĐẶT CỌC THÁP
        // Dải 1 trên cùng (3 cọc)
        drawPlateau(shapeRenderer, 645f, 515f, 230f, 40f)

        // Dải 2 giữa trên: cọc lẻ trái (410, 375) và dải ngang ĐÃ MỞ RỘNG SANG PHẢI (tâm 625f, dài 285f)
        drawPlateau(shapeRenderer, 625f, 375f, 285f, 40f)

        // Dải 4 đáy Nam (3 cọc)
        drawPlateau(shapeRenderer, 660f, 140f, 230f, 40f)

        // 6. CỬA HANG MỎ ĐÔNG BẮC
        drawCave(shapeRenderer, 930f, 510f)

        // 7. RỪNG THÔNG
        for (x in 20..190 step 42) drawPine(shapeRenderer, x.toFloat(), 60f)
        for (x in 360..500 step 42) drawPine(shapeRenderer, x.toFloat(), 40f)
        for (x in 780..1240 step 42) drawPine(shapeRenderer, x.toFloat(), 30f)

        // Hàng thông viền trên chừa lối đi cổng Bắc
        for (x in 380..590 step 42) drawPine(shapeRenderer, x.toFloat(), 675f)
        for (x in 710..840 step 42) drawPine(shapeRenderer, x.toFloat(), 675f)
    }

    private fun drawPath(sr: ShapeRenderer, pts: List<Vector2>, width: Float) {
        val r = width / 2f
        for (i in 0 until pts.size - 1) {
            sr.rectLine(pts[i], pts[i + 1], width)
            sr.circle(pts[i].x, pts[i].y, r)
        }
        if (pts.isNotEmpty()) sr.circle(pts.last().x, pts.last().y, r)
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

    private fun drawCave(sr: ShapeRenderer, x: Float, y: Float) {
        sr.color = Color(0.12f, 0.12f, 0.14f, 1f)
        sr.rect(x - 24f, y - 20f, 48f, 36f)
        sr.circle(x, y + 16f, 24f)
        sr.color = Color(0.42f, 0.28f, 0.16f, 1f)
        sr.rect(x - 26f, y - 22f, 8f, 42f)
        sr.rect(x + 18f, y - 22f, 8f, 42f)
        sr.rect(x - 26f, y + 16f, 52f, 8f)
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
