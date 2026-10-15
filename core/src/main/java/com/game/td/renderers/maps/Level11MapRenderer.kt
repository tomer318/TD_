package com.game.td.renderers.maps

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.game.td.map.Level11ForsakenValley

object Level11MapRenderer {

    fun render(shapeRenderer: ShapeRenderer) {
        // 1. NỀN TRO NÚI LỬA XÁM SẪM
        shapeRenderer.color = Color(0.24f, 0.23f, 0.25f, 1f)
        shapeRenderer.rect(0f, 0f, 1280f, 720f)

        // 2. CÁC DÃY NÚI ĐÁ BAZAN & KHE DUNG NHAM VIỀN NGOÀI
        shapeRenderer.color = Color(0.16f, 0.15f, 0.17f, 1f)
        // Núi viền Tây
        shapeRenderer.triangle(0f, 0f, 220f, 0f, 0f, 720f)
        shapeRenderer.triangle(0f, 720f, 320f, 720f, 160f, 400f)

        // Núi viền Đông
        shapeRenderer.triangle(1280f, 0f, 1060f, 0f, 1280f, 720f)
        shapeRenderer.triangle(1280f, 720f, 960f, 720f, 1120f, 400f)

        // Các khe nứt dung nham rực lửa ngoài sườn núi
        shapeRenderer.color = Color(0.95f, 0.40f, 0.05f, 0.95f)
        shapeRenderer.rectLine(40f, 650f, 140f, 480f, 8f)
        shapeRenderer.rectLine(120f, 490f, 60f, 280f, 7f)
        shapeRenderer.rectLine(1240f, 650f, 1140f, 480f, 8f)
        shapeRenderer.rectLine(1160f, 490f, 1220f, 280f, 7f)
        shapeRenderer.color = Color(1f, 0.85f, 0.20f, 1f)
        shapeRenderer.rectLine(40f, 650f, 140f, 480f, 3f)
        shapeRenderer.rectLine(1240f, 650f, 1140f, 480f, 3f)

        // Gờ đất đồi giữa 2 lối vào phía Nam (ngăn cách 2 đường tím)
        shapeRenderer.color = Color(0.18f, 0.17f, 0.19f, 1f)
        shapeRenderer.circle(640f, 130f, 85f)
        shapeRenderer.rect(560f, 0f, 160f, 130f)

        // 3. VẼ HỆ THỐNG ĐƯỜNG ĐI CHÍNH
        val pathSW = Level11ForsakenValley.pathSouthwest
        val pathSE = Level11ForsakenValley.pathSoutheast
        val pathMagmaL = Level11ForsakenValley.pathMagmaLeft
        val pathMagmaR = Level11ForsakenValley.pathMagmaRight

        // A. Lớp viền đường tối sẫm (rộng 64px)
        shapeRenderer.color = Color(0.35f, 0.34f, 0.36f, 1f)
        drawPath(shapeRenderer, pathSW, 64f)
        drawPath(shapeRenderer, pathSE, 64f)

        // B. Lòng đường sỏi đá xám sáng (rộng 48px)
        shapeRenderer.color = Color(0.55f, 0.54f, 0.56f, 1f)
        drawPath(shapeRenderer, pathSW, 48f)
        drawPath(shapeRenderer, pathSE, 48f)

        // C. Cổng thành phía Bắc (Đích đến)
        shapeRenderer.color = Color(0.20f, 0.22f, 0.26f, 1f)
        shapeRenderer.rect(590f, 700f, 100f, 20f)
        shapeRenderer.color = Color(0.2f, 0.5f, 0.9f, 1f)
        shapeRenderer.circle(605f, 695f, 6f)
        shapeRenderer.circle(675f, 695f, 6f)

        // D. Lối dẫn nham thạch nối từ Magma Field ra 2 đường sườn bên ngoài (vẽ dạng rãnh đá nứt lửa)
        val mPos = Level11ForsakenValley.magmaFieldPosition
        shapeRenderer.color = Color(0.14f, 0.13f, 0.15f, 1f)
        // Rãnh thoát xuống phía Nam rồi rẽ 2 bên
        shapeRenderer.rectLine(mPos.x, mPos.y - 20f, mPos.x, mPos.y - 80f, 32f)
        shapeRenderer.rectLine(mPos.x, mPos.y - 80f, 490f, 220f, 26f)
        shapeRenderer.rectLine(mPos.x, mPos.y - 80f, 790f, 220f, 26f)

        // Dòng chảy dung nham trong rãnh
        shapeRenderer.color = Color(0.92f, 0.35f, 0.05f, 0.9f)
        shapeRenderer.rectLine(mPos.x, mPos.y - 20f, mPos.x, mPos.y - 80f, 16f)
        shapeRenderer.rectLine(mPos.x, mPos.y - 80f, 490f, 220f, 12f)
        shapeRenderer.rectLine(mPos.x, mPos.y - 80f, 790f, 220f, 12f)

        shapeRenderer.color = Color(1f, 0.85f, 0.22f, 0.9f)
        shapeRenderer.rectLine(mPos.x, mPos.y - 20f, mPos.x, mPos.y - 80f, 6f)
        shapeRenderer.rectLine(mPos.x, mPos.y - 80f, 490f, 220f, 4f)
        shapeRenderer.rectLine(mPos.x, mPos.y - 80f, 790f, 220f, 4f)

        // 4. KHU VỰC ĐẶC BIỆT: MAGMA FIELD (VẼ SAU ĐỂ NỔI LÊN TRÊN KHÔNG BỊ ĐƯỜNG CHE KHUẤT)
        // Vành đá núi lửa đen xếp tầng bao quanh miệng núi lửa
        shapeRenderer.color = Color(0.14f, 0.13f, 0.15f, 1f)
        shapeRenderer.circle(mPos.x, mPos.y, 82f)
        shapeRenderer.circle(mPos.x - 42f, mPos.y + 30f, 48f)
        shapeRenderer.circle(mPos.x + 42f, mPos.y + 30f, 48f)
        shapeRenderer.circle(mPos.x - 52f, mPos.y - 18f, 44f)
        shapeRenderer.circle(mPos.x + 52f, mPos.y - 18f, 44f)
        shapeRenderer.circle(mPos.x, mPos.y + 50f, 46f)

        // Vành đá sẫm gồ ghề lớp trong
        shapeRenderer.color = Color(0.19f, 0.18f, 0.20f, 1f)
        shapeRenderer.circle(mPos.x, mPos.y, 68f)
        shapeRenderer.circle(mPos.x - 30f, mPos.y + 20f, 38f)
        shapeRenderer.circle(mPos.x + 30f, mPos.y + 20f, 38f)
        shapeRenderer.circle(mPos.x - 36f, mPos.y - 14f, 34f)
        shapeRenderer.circle(mPos.x + 36f, mPos.y - 14f, 34f)

        // Lòng chảo dung nham đỏ rực
        shapeRenderer.color = Color(0.35f, 0.10f, 0.05f, 1f)
        shapeRenderer.circle(mPos.x, mPos.y, 52f)

        // Lớp dung nham cam cháy sủi bọt
        shapeRenderer.color = Color(0.95f, 0.38f, 0.05f, 1f)
        shapeRenderer.circle(mPos.x, mPos.y, 44f)

        // Lõi dung nham nóng vàng phát sáng
        shapeRenderer.color = Color(1f, 0.88f, 0.20f, 1f)
        shapeRenderer.circle(mPos.x, mPos.y, 28f)

        shapeRenderer.color = Color.WHITE
        shapeRenderer.circle(mPos.x + 5f, mPos.y + 3f, 12f)
        shapeRenderer.circle(mPos.x - 8f, mPos.y - 6f, 8f)

        // Các tảng đá bazan vụn nổi trên bề mặt dung nham
        shapeRenderer.color = Color(0.18f, 0.16f, 0.18f, 1f)
        shapeRenderer.circle(mPos.x - 18f, mPos.y + 8f, 8f)
        shapeRenderer.circle(mPos.x + 16f, mPos.y - 12f, 7f)
        shapeRenderer.circle(mPos.x - 6f, mPos.y - 18f, 6f)
        shapeRenderer.circle(mPos.x + 18f, mPos.y + 12f, 8f)
        shapeRenderer.circle(mPos.x, mPos.y + 20f, 7f)

        // Miệng dốc phía Nam thoát nham thạch
        shapeRenderer.color = Color(0.90f, 0.32f, 0.05f, 0.95f)
        shapeRenderer.rect(mPos.x - 12f, mPos.y - 64f, 24f, 28f)
        shapeRenderer.color = Color(1f, 0.86f, 0.22f, 1f)
        shapeRenderer.rect(mPos.x - 5f, mPos.y - 60f, 10f, 20f)
    }

    private fun drawPath(sr: ShapeRenderer, pts: List<Vector2>, width: Float) {
        val r = width / 2f
        for (i in 0 until pts.size - 1) {
            sr.rectLine(pts[i], pts[i + 1], width)
            sr.circle(pts[i].x, pts[i].y, r)
        }
        if (pts.isNotEmpty()) sr.circle(pts.last().x, pts.last().y, r)
    }
}
