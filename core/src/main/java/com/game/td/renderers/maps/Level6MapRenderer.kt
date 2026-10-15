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

        // Cổng vòm lối vào lâu đài Linirea[cite: 58, 59]
        shapeRenderer.color = Color(0.24f, 0.20f, 0.16f, 1f)
        shapeRenderer.rect(590f, 640f, 100f, 75f)
        shapeRenderer.circle(640f, 715f, 50f)

        // Đường lát đá hoa cương vào thành[cite: 58, 59]
        shapeRenderer.color = Color(0.60f, 0.58f, 0.52f, 1f)
        shapeRenderer.rect(595f, 640f, 90f, 40f)

        // 2. KHU VỰC RỪNG RẬM HAI BÊN CÁNH (ĐÃ ĐẨY CAO TRÁNH ĐÈ LÊN ĐƯỜNG VÀO CỦA QUÁI)[cite: 58, 59]
        shapeRenderer.color = Color(0.18f, 0.46f, 0.16f, 1f)
        // Rừng cánh Tây (đẩy lùi tâm cây lên y >= 360f, không đè y=260f)
        shapeRenderer.circle(100f, 550f, 95f)
        shapeRenderer.circle(180f, 480f, 85f)
        shapeRenderer.circle(90f, 410f, 90f)
        shapeRenderer.circle(130f, 350f, 65f) // Đã thu nhỏ và đẩy lên cao[cite: 58]
        shapeRenderer.circle(60f, 150f, 85f)  // Dạt xuống dưới đáy hẳn[cite: 58]

        // Rừng cánh Đông (đẩy lùi tâm cây lên y >= 360f, không đè y=260f)[cite: 58]
        shapeRenderer.circle(1180f, 550f, 95f)
        shapeRenderer.circle(1100f, 480f, 85f)
        shapeRenderer.circle(1190f, 410f, 90f)
        shapeRenderer.circle(1150f, 350f, 65f) // Đã thu nhỏ và đẩy lên cao[cite: 58]
        shapeRenderer.circle(1220f, 150f, 85f) // Dạt xuống dưới đáy hẳn[cite: 58]

        // Rừng chân phía Nam[cite: 58, 59]
        shapeRenderer.circle(360f, 60f, 75f)
        shapeRenderer.circle(920f, 60f, 75f)

        // Tán cây xanh sáng hơn phủ lên
        shapeRenderer.color = Color(0.25f, 0.56f, 0.20f, 1f)
        shapeRenderer.circle(100f, 560f, 70f)
        shapeRenderer.circle(180f, 490f, 65f)
        shapeRenderer.circle(1180f, 560f, 70f)
        shapeRenderer.circle(1100f, 490f, 65f)

        // 3. DANH SÁCH CÁC ĐOẠN ĐƯỜNG THẲNG ĐỊNH HÌNH CÁC NGÃ BA
        val straightSegments = listOf(
            // Trục ngang đáy thông suốt từ Tây sang Đông (được mở thoáng hoàn toàn)[cite: 58]
            Pair(Vector2(-40f, 260f), Vector2(1320f, 260f)),

            // Trục Nam đâm lên gặp trục đáy tại (640f, 260f)
            Pair(Vector2(640f, -40f), Vector2(640f, 260f)),

            // Trục ngang giữa (nơi 2 tốp lính đứng chốt chặn)
            Pair(Vector2(330f, 430f), Vector2(950f, 430f)),

            // Trục ngang trên trước cổng thành
            Pair(Vector2(370f, 575f), Vector2(910f, 575f)),

            // Nhánh dọc giữa nối từ đường giữa (640f, 430f) đâm thẳng thông suốt vào cổng thành (640f, 650f)[cite: 58]
            Pair(Vector2(640f, 430f), Vector2(640f, 650f)),

            // Đoạn nối từ ngã ba đáy Tây (350f, 260f) lên điểm bắt đầu cung Tây
            Pair(Vector2(350f, 260f), Vector2(340f, 320f)),

            // Đoạn nối từ ngã ba đáy Đông (930f, 260f) lên điểm bắt đầu cung Đông
            Pair(Vector2(930f, 260f), Vector2(940f, 320f))
        )

        // A. Lớp viền đất đậm cho các đoạn thẳng (Rộng 78px)
        shapeRenderer.color = Color(0.55f, 0.44f, 0.26f, 1f)
        for (seg in straightSegments) {
            shapeRenderer.rectLine(seg.first, seg.second, 78f)
            shapeRenderer.circle(seg.first.x, seg.first.y, 39f)
            shapeRenderer.circle(seg.second.x, seg.second.y, 39f)
        }

        // B. Lớp viền đất đậm cho 2 sườn cong ngoài
        drawCurvedFlank(shapeRenderer, isWest = true, isBorder = true)
        drawCurvedFlank(shapeRenderer, isWest = false, isBorder = true)

        // C. Lòng đường cát vàng cho các đoạn thẳng (Rộng 64px)
        shapeRenderer.color = Color(0.82f, 0.72f, 0.46f, 1f)
        for (seg in straightSegments) {
            shapeRenderer.rectLine(seg.first, seg.second, 64f)
            shapeRenderer.circle(seg.first.x, seg.first.y, 32f)
            shapeRenderer.circle(seg.second.x, seg.second.y, 32f)
        }

        // D. Lòng đường cát vàng cho 2 sườn cong ngoài
        drawCurvedFlank(shapeRenderer, isWest = true, isBorder = false)
        drawCurvedFlank(shapeRenderer, isWest = false, isBorder = false)

        // Vòm ngã ba cổng thành đón lính
        shapeRenderer.circle(640f, 575f, 46f)
        // Vòm ngã ba chữ T giữa
        shapeRenderer.circle(640f, 430f, 38f)

        // 4. BA ĐỒI CỎ NẰM GỌN TRONG CÁC Ô ĐƯỜNG[cite: 58, 59]
        // Đảo cỏ trên trái
        drawGrassHill(shapeRenderer, 515f, 505f, 120f, 48f)

        // Đảo cỏ trên phải
        drawGrassHill(shapeRenderer, 765f, 505f, 120f, 48f)

        // Đảo cỏ dài bên dưới (nguyên vẹn)
        drawGrassHill(shapeRenderer, 640f, 355f, 230f, 48f)

        // 5. BỜ ĐẤT 4 CỌC TRÊN: TÁCH ĐÔI KHÔNG CẮT NGANG ĐƯỜNG VÀO CỔNG[cite: 58]
        shapeRenderer.color = Color(0.32f, 0.56f, 0.18f, 1f)
        // Bờ đất phía Tây cho 2 cọc trên bên trái (480f và 570f)[cite: 58]
        shapeRenderer.rect(440f, 624f, 145f, 28f)
        shapeRenderer.circle(440f, 638f, 14f)
        shapeRenderer.circle(585f, 638f, 14f)

        // Bờ đất phía Đông cho 2 cọc trên bên phải (710f và 800f)[cite: 58]
        shapeRenderer.rect(695f, 624f, 145f, 28f)
        shapeRenderer.circle(695f, 638f, 14f)
        shapeRenderer.circle(840f, 638f, 14f)
    }

    private fun drawCurvedFlank(sr: ShapeRenderer, isWest: Boolean, isBorder: Boolean) {
        val width = if (isBorder) 78f else 64f
        val color = if (isBorder) Color(0.55f, 0.44f, 0.26f, 1f) else Color(0.82f, 0.72f, 0.46f, 1f)
        sr.color = color

        val points = if (isWest) {
            listOf(
                Vector2(340f, 320f),
                Vector2(320f, 380f),
                Vector2(315f, 440f),
                Vector2(330f, 510f),
                Vector2(370f, 575f)
            )
        } else {
            listOf(
                Vector2(940f, 320f),
                Vector2(960f, 380f),
                Vector2(965f, 440f),
                Vector2(950f, 510f),
                Vector2(910f, 575f)
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
