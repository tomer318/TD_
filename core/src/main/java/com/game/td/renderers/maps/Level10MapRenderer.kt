package com.game.td.renderers.maps

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.game.td.map.Level10TheWastes
import com.game.td.renderers.MapRenderer

object Level10MapRenderer {

    fun render(shapeRenderer: ShapeRenderer) {
        // 1. NỀN ĐẤT ĐÁ TRO XÁM SẪM
        shapeRenderer.color = Color(0.28f, 0.28f, 0.30f, 1f)
        shapeRenderer.rect(0f, 0f, 1280f, 720f)

        // 2. KHỐI NÚI ĐÁ VÀ KHE DUNG NHAM THEO VIỀN BẢN ĐỒ
        // Núi Tây Bắc quanh nghĩa trang tím
        shapeRenderer.color = Color(0.18f, 0.18f, 0.20f, 1f)
        shapeRenderer.triangle(0f, 720f, 220f, 720f, 0f, 650f)

        // Núi Bắc giữa cổng thành và đường bên phải (nâng cao để không lấn đường ngang y=670f)
        shapeRenderer.triangle(460f, 720f, 850f, 720f, 650f, 705f)

        // Dãy núi phía Đông & Đông Nam
        shapeRenderer.triangle(1280f, 720f, 920f, 720f, 1280f, 260f)
        shapeRenderer.triangle(1280f, 0f, 1100f, 0f, 1280f, 280f)

        // Khe nứt dung nham rực lửa
        shapeRenderer.color = Color(0.95f, 0.40f, 0.05f, 0.95f)
        shapeRenderer.rectLine(1160f, 680f, 1260f, 540f, 8f)
        shapeRenderer.rectLine(1190f, 480f, 1260f, 340f, 7f)
        shapeRenderer.rectLine(60f, 680f, 180f, 640f, 6f)
        shapeRenderer.color = Color(1f, 0.85f, 0.2f, 1f)
        shapeRenderer.rectLine(1160f, 680f, 1260f, 540f, 3.5f)

        // Bụi gai đen khô cằn ở chân núi
        shapeRenderer.color = Color(0.20f, 0.20f, 0.22f, 1f)
        shapeRenderer.rect(0f, 0f, 1280f, 85f)
        shapeRenderer.circle(60f, 340f, 45f)

        // 3. VẼ HỆ THỐNG ĐƯỜNG ĐI MƯỢT MÀ
        val pathSW = Level10TheWastes.pathSouthwest
        val pathSE = Level10TheWastes.pathSoutheast
        val pathSkel = Level10TheWastes.pathSkeleton

        // Viền đường tối (66px)
        shapeRenderer.color = Color(0.42f, 0.42f, 0.45f, 1f)
        drawPath(shapeRenderer, pathSW, 66f)
        drawPath(shapeRenderer, pathSE, 66f)
        drawPath(shapeRenderer, pathSkel, 46f)

        // Lòng đường đất xám sáng (50px)
        shapeRenderer.color = Color(0.64f, 0.64f, 0.66f, 1f)
        drawPath(shapeRenderer, pathSW, 50f)
        drawPath(shapeRenderer, pathSE, 50f)
        drawPath(shapeRenderer, pathSkel, 32f)

        // CỔNG THÀNH / NHÀ PHÍA BẮC (ĐÍCH ĐẾN VALARDUL - TỌA ĐỘ x=370f KHỚP ĐƯỜNG THẲNG LÊN)
        shapeRenderer.color = Color(0.20f, 0.22f, 0.26f, 1f)
        shapeRenderer.rect(330f, 700f, 80f, 20f)
        shapeRenderer.color = Color(0.2f, 0.5f, 0.9f, 1f)
        shapeRenderer.circle(345f, 695f, 6f)
        shapeRenderer.circle(395f, 695f, 6f)

        // 4. KHU VỰC NGHĨA TRANG (VÙNG KHOANH TÍM MỚI TẠI GÓC TÂY BẮC)
        val gPos = Level10TheWastes.graveyardPosition
        shapeRenderer.color = Color(0.22f, 0.22f, 0.24f, 1f)
        shapeRenderer.circle(gPos.x, gPos.y, 44f)
        shapeRenderer.color = Color(0.16f, 0.16f, 0.18f, 1f)
        shapeRenderer.circle(gPos.x, gPos.y, 35f)

        // Hàng rào gỗ xiêu vẹo
        shapeRenderer.color = Color(0.35f, 0.26f, 0.18f, 1f)
        shapeRenderer.rectLine(gPos.x - 36f, gPos.y - 22f, gPos.x + 36f, gPos.y - 22f, 3f)
        shapeRenderer.rectLine(gPos.x - 36f, gPos.y + 25f, gPos.x + 36f, gPos.y + 25f, 3f)
        for (i in -28..28 step 14) {
            shapeRenderer.rect(gPos.x + i, gPos.y - 28f, 3f, 12f)
            shapeRenderer.rect(gPos.x + i, gPos.y + 19f, 3f, 12f)
        }

        // Bia mộ đá & chữ thập
        val tombOffsets = listOf(
            Vector2(-15f, 6f), Vector2(15f, 7f),
            Vector2(-10f, -8f), Vector2(12f, -8f),
            Vector2(0f, 12f)
        )
        for (tomb in tombOffsets) {
            val tx = gPos.x + tomb.x
            val ty = gPos.y + tomb.y
            shapeRenderer.color = Color(0.50f, 0.52f, 0.55f, 1f)
            shapeRenderer.rect(tx - 5f, ty - 4f, 10f, 11f)
            shapeRenderer.circle(tx, ty + 7.5f, 5f)
            shapeRenderer.color = Color(0.20f, 0.20f, 0.22f, 1f)
            shapeRenderer.rectLine(tx, ty - 1.5f, tx, ty + 7f, 1.8f)
            shapeRenderer.rectLine(tx - 2.5f, ty + 2.5f, tx + 2.5f, ty + 2.5f, 1.8f)
        }

        // Cây khô khẳng khiu
        drawDeadTree(shapeRenderer, gPos.x - 48f, gPos.y + 18f)
        drawDeadTree(shapeRenderer, gPos.x + 44f, gPos.y + 26f)
        drawDeadTree(shapeRenderer, 700f, 110f)

        // 5. VÒNG TRÒN PHÒNG THỦ & 2 CỜ HIỆU LINIREA TẠI CỔNG BẮC (X = 370F)
        MapRenderer.drawDefensePoint(
            sr = shapeRenderer,
            crestX = 370f, crestY = 650f,
            flag1X = 326f, flag1Y = 646f,
            flag2X = 414f, flag2Y = 646f
        )
    }

    private fun drawPath(sr: ShapeRenderer, pts: List<Vector2>, width: Float) {
        val r = width / 2f
        for (i in 0 until pts.size - 1) {
            sr.rectLine(pts[i], pts[i + 1], width)
            sr.circle(pts[i].x, pts[i].y, r)
        }
        if (pts.isNotEmpty()) sr.circle(pts.last().x, pts.last().y, r)
    }

    private fun drawDeadTree(sr: ShapeRenderer, x: Float, y: Float) {
        sr.color = Color(0.16f, 0.14f, 0.12f, 1f)
        sr.rect(x - 3f, y - 8f, 6f, 22f)
        sr.rectLine(x, y + 8f, x - 10f, y + 22f, 2.5f)
        sr.rectLine(x, y + 10f, x + 12f, y + 24f, 2.5f)
        sr.rectLine(x - 5f, y + 16f, x - 8f, y + 28f, 2f)
    }
}
