package com.game.td.renderers.maps

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.game.td.map.Level12TheDarkTower

object Level12MapRenderer {

    // Cờ kiểm soát hiển thị Vez'nan trên ban công (Campaign) hoặc biển "FOR SALE" (Heroic/Iron)
    var isVeznanOnBalcony: Boolean = true
    var showForSaleSign: Boolean = false

    fun render(shapeRenderer: ShapeRenderer) {
        // 1. NỀN TRO NÚI LỬA XÁM SẪM ĐẶC TRƯNG VÙNG WASTES
        shapeRenderer.color = Color(0.20f, 0.19f, 0.22f, 1f)
        shapeRenderer.rect(0f, 0f, 1280f, 720f)

        // 2. KHU RỪNG CÂY CHẾT (SƯỜN TÂY) & DÃY VÁCH NÚI LỬA (SƯỜN ĐÔNG)
        shapeRenderer.color = Color(0.14f, 0.13f, 0.15f, 1f)
        shapeRenderer.triangle(1280f, 0f, 1080f, 0f, 1280f, 520f)
        shapeRenderer.triangle(1280f, 520f, 1140f, 380f, 1280f, 720f)

        // Các khe nứt dung nham sườn Đông
        shapeRenderer.color = Color(0.92f, 0.35f, 0.05f, 0.95f)
        shapeRenderer.rectLine(1180f, 660f, 1120f, 480f, 7f)
        shapeRenderer.rectLine(1140f, 500f, 1220f, 320f, 6f)
        shapeRenderer.rectLine(1190f, 340f, 1110f, 120f, 7f)
        shapeRenderer.color = Color(1f, 0.85f, 0.20f, 1f)
        shapeRenderer.rectLine(1180f, 660f, 1120f, 480f, 2.5f)
        shapeRenderer.rectLine(1190f, 340f, 1110f, 120f, 2.5f)

        // Các cây chết khô sườn Tây
        drawDeadTree(shapeRenderer, 80f, 520f)
        drawDeadTree(shapeRenderer, 60f, 380f)
        drawDeadTree(shapeRenderer, 80f, 230f)
        drawDeadTree(shapeRenderer, 70f, 110f)

        // 3. VẼ HỆ THỐNG ĐƯỜNG ĐI MÀU ĐÁ XÁM TRO
        val pathNW = Level12TheDarkTower.pathNorthwest
        val pathNE = Level12TheDarkTower.pathNortheast
        val pathG = Level12TheDarkTower.pathGraveyard

        // A. Viền đường màu xám đá sẫm
        shapeRenderer.color = Color(0.38f, 0.37f, 0.40f, 1f)
        drawPath(shapeRenderer, pathNW, 62f)
        drawPath(shapeRenderer, pathNE, 62f)
        drawPath(shapeRenderer, pathG, 44f)

        // B. Lòng đường sỏi đá màu xám tro nhạt
        shapeRenderer.color = Color(0.65f, 0.65f, 0.69f, 1f)
        drawPath(shapeRenderer, pathNW, 46f)
        drawPath(shapeRenderer, pathNE, 46f)
        drawPath(shapeRenderer, pathG, 30f)

        // 4. 3 VÒNG TRÒN MA THUẬT PENTAGRAM (TẮT NĂNG LƯỢNG KHI Ở CHẾ ĐỘ HEROIC / IRON)
        val isCircleDormant = showForSaleSign
        drawSummoningPentagram(shapeRenderer, Level12TheDarkTower.summoningCircleTop, 36f, isCircleDormant)
        drawSummoningPentagram(shapeRenderer, Level12TheDarkTower.summoningCircleCenter, 36f, isCircleDormant)
        drawSummoningPentagram(shapeRenderer, Level12TheDarkTower.summoningCircleBottom, 36f, isCircleDormant)

        // 5. KHU VỰC NGHĨA ĐỊA (VẼ ĐÈ LÊN TRÊN ĐƯỜNG ĐI)
        val gPos = Level12TheDarkTower.graveyardPosition
        drawGraveyardArea(shapeRenderer, gPos.x, gPos.y)

        // 6. HAI LÁ CỜ LINIREA XANH TẠI CỔNG THOÁT PHÍA NAM
        shapeRenderer.color = Color(0.28f, 0.20f, 0.15f, 1f)
        shapeRenderer.rect(860f, 25f, 3f, 28f)
        shapeRenderer.rect(920f, 25f, 3f, 28f)
        shapeRenderer.color = Color(0.20f, 0.55f, 0.90f, 1f)
        shapeRenderer.triangle(863f, 53f, 863f, 36f, 880f, 44f)
        shapeRenderer.triangle(923f, 53f, 923f, 36f, 940f, 44f)

        // 7. TÒA PHÁO ĐÀI THE DARK TOWER (PHÍA BẮC)
        shapeRenderer.color = Color(0.12f, 0.11f, 0.14f, 1f)
        shapeRenderer.rect(420f, 580f, 440f, 140f)
        shapeRenderer.circle(640f, 650f, 110f)

        // Hai cổng vòm hắc ám xuất quân
        shapeRenderer.color = Color(0.08f, 0.07f, 0.09f, 1f)
        shapeRenderer.rect(420f, 590f, 60f, 45f)
        shapeRenderer.circle(450f, 635f, 30f)
        shapeRenderer.rect(800f, 590f, 60f, 45f)
        shapeRenderer.circle(830f, 635f, 30f)

        shapeRenderer.color = Color(0.04f, 0.03f, 0.05f, 1f)
        shapeRenderer.circle(450f, 635f, 20f)
        shapeRenderer.circle(830f, 635f, 20f)

        // Đuốc ma thuật hai bên vòm cửa
        drawTorch(shapeRenderer, 395f, 625f)
        drawTorch(shapeRenderer, 885f, 625f)

        // Hàng cọc cắm đầu lâu
        shapeRenderer.color = Color(0.20f, 0.18f, 0.16f, 1f)
        for (i in 0 until 11) {
            val sX = 520f + i * 24f
            shapeRenderer.rectLine(sX, 582f, sX, 600f, 2f)
            shapeRenderer.color = Color(0.82f, 0.82f, 0.78f, 1f)
            shapeRenderer.circle(sX, 603f, 4.5f)
            shapeRenderer.color = Color(0.20f, 0.18f, 0.16f, 1f)
        }

        // Ban công trung tâm
        val bX = Level12TheDarkTower.balconyPosition.x
        val bY = Level12TheDarkTower.balconyPosition.y

        shapeRenderer.color = Color(0.09f, 0.08f, 0.10f, 1f)
        shapeRenderer.circle(bX, bY + 15f, 36f)
        shapeRenderer.rect(bX - 36f, bY - 20f, 72f, 35f)

        shapeRenderer.color = Color(0.18f, 0.17f, 0.20f, 1f)
        shapeRenderer.rect(bX - 34f, bY - 18f, 68f, 16f)
        shapeRenderer.triangle(bX - 34f, bY - 2f, bX - 26f, bY + 6f, bX - 18f, bY - 2f)
        shapeRenderer.triangle(bX - 8f, bY - 2f, bX, bY + 6f, bX + 8f, bY - 2f)
        shapeRenderer.triangle(bX + 18f, bY - 2f, bX + 26f, bY + 6f, bX + 34f, bY - 2f)

        if (showForSaleSign) {
            // VẼ BIỂN GỖ "FOR SALE" TRÊN BAN CÔNG (CHẾ ĐỘ HEROIC & IRON)
            drawForSaleSignOnBalcony(shapeRenderer, bX, bY)
        } else if (isVeznanOnBalcony) {
            // VẼ VEZ'NAN TRÊN BAN CÔNG KHI CHƯA XUỐNG SÂN (CHẾ ĐỘ CAMPAIGN)
            shapeRenderer.color = Color(0.35f, 0.18f, 0.45f, 1f)
            shapeRenderer.rect(bX - 9f, bY - 2f, 18f, 24f)
            shapeRenderer.circle(bX, bY + 12f, 11f)

            shapeRenderer.color = Color(0.10f, 0.08f, 0.12f, 1f)
            shapeRenderer.circle(bX, bY + 24f, 8f)

            shapeRenderer.color = Color(0.95f, 0.78f, 0.15f, 1f)
            shapeRenderer.rect(bX - 7f, bY + 27f, 14f, 4f)
            shapeRenderer.triangle(bX - 7f, bY + 31f, bX - 4f, bY + 36f, bX - 1f, bY + 31f)
            shapeRenderer.triangle(bX - 2f, bY + 31f, bX + 1f, bY + 38f, bX + 4f, bY + 31f)
            shapeRenderer.triangle(bX + 3f, bY + 31f, bX + 6f, bY + 36f, bX + 9f, bY + 31f)

            shapeRenderer.color = Color.RED
            shapeRenderer.circle(bX - 2.5f, bY + 25f, 1.8f)
            shapeRenderer.circle(bX + 2.5f, bY + 25f, 1.8f)

            shapeRenderer.color = Color(0.35f, 0.25f, 0.18f, 1f)
            shapeRenderer.rect(bX - 16f, bY + 4f, 3f, 20f)
            shapeRenderer.color = Color(0.95f, 0.40f, 0.05f, 0.95f)
            shapeRenderer.circle(bX - 14.5f, bY + 25f, 5.5f)
            shapeRenderer.color = Color(1f, 0.85f, 0.20f, 1f)
            shapeRenderer.circle(bX - 14.5f, bY + 25f, 3f)
        }
    }

    // VẼ BIỂN GỖ "FOR SALE" TRÊN BAN CÔNG THÁP ĐEN
    private fun drawForSaleSignOnBalcony(sr: ShapeRenderer, bX: Float, bY: Float) {
        // Cọc gỗ cắm biển
        sr.color = Color(0.36f, 0.22f, 0.11f, 1f)
        sr.rect(bX - 3f, bY - 2f, 6f, 34f)

        // Khung viền biển gỗ đậm
        sr.color = Color(0.32f, 0.19f, 0.09f, 1f)
        sr.rect(bX - 29f, bY + 10f, 58f, 24f)

        // Mặt bảng gỗ sáng màu vàng nâu
        sr.color = Color(0.76f, 0.56f, 0.30f, 1f)
        sr.rect(bX - 27f, bY + 12f, 54f, 20f)

        // Vân ván gỗ ngang và 2 đinh tán
        sr.color = Color(0.62f, 0.44f, 0.22f, 1f)
        sr.rect(bX - 27f, bY + 21f, 54f, 1.5f)
        sr.color = Color(0.25f, 0.25f, 0.28f, 1f)
        sr.circle(bX - 23f, bY + 28f, 1.5f)
        sr.circle(bX + 23f, bY + 28f, 1.5f)
    }

    private fun drawPath(sr: ShapeRenderer, pts: List<Vector2>, width: Float) {
        val r = width / 2f
        for (i in 0 until pts.size - 1) {
            sr.rectLine(pts[i], pts[i + 1], width)
            sr.circle(pts[i].x, pts[i].y, r)
        }
        if (pts.isNotEmpty()) sr.circle(pts.last().x, pts.last().y, r)
    }

    private fun drawGraveyardArea(sr: ShapeRenderer, cx: Float, cy: Float) {
        sr.color = Color(0.15f, 0.14f, 0.17f, 1f)
        sr.circle(cx, cy, 52f)
        sr.color = Color(0.11f, 0.10f, 0.13f, 1f)
        sr.circle(cx, cy, 45f)

        drawDeadTree(sr, cx - 46f, cy + 26f)
        drawDeadTree(sr, cx + 40f, cy + 26f)

        sr.color = Color(0.32f, 0.22f, 0.16f, 1f)
        sr.rectLine(cx - 38f, cy + 18f, cx + 38f, cy + 18f, 3.5f)
        sr.rectLine(cx - 38f, cy - 18f, cx + 38f, cy - 18f, 3.5f)

        for (xOff in listOf(-32f, -20f, -8f, 4f, 16f, 28f)) {
            sr.rect(cx + xOff - 1.5f, cy - 24f, 3f, 48f)
            sr.triangle(cx + xOff - 2.5f, cy + 24f, cx + xOff + 2.5f, cy + 24f, cx + xOff, cy + 29f)
        }

        drawDetailedTombstone(sr, cx - 20f, cy + 5f)
        drawDetailedTombstone(sr, cx, cy + 7f)
        drawDetailedTombstone(sr, cx + 20f, cy + 5f)
        drawDetailedTombstone(sr, cx - 11f, cy - 10f)
        drawDetailedTombstone(sr, cx + 11f, cy - 10f)
    }

    private fun drawDetailedTombstone(sr: ShapeRenderer, x: Float, y: Float) {
        sr.color = Color(0.50f, 0.50f, 0.54f, 1f)
        sr.rect(x - 5.5f, y - 8f, 11f, 14f)
        sr.circle(x, y + 6f, 5.5f)

        sr.color = Color(0.92f, 0.92f, 0.96f, 1f)
        sr.rect(x - 1f, y - 4f, 2f, 10f)
        sr.rect(x - 3.5f, y + 1f, 7f, 2f)
    }

    private fun drawSummoningPentagram(sr: ShapeRenderer, center: Vector2, radius: Float, isDormant: Boolean = false) {
        if (isDormant) {
            // Vòng đá cổ tự đã tắt năng lượng khi chơi Heroic / Iron
            sr.color = Color(0.45f, 0.44f, 0.48f, 1f)
            sr.circle(center.x, center.y, radius)
            sr.color = Color(0.58f, 0.58f, 0.62f, 1f)
            sr.circle(center.x, center.y, radius - 3f)

            val points = mutableListOf<Vector2>()
            for (i in 0 until 5) {
                val angle = Math.toRadians((90.0 + i * 72.0))
                val px = center.x + (radius * 0.70f * Math.cos(angle)).toFloat()
                val py = center.y + (radius * 0.70f * Math.sin(angle)).toFloat()
                points.add(Vector2(px, py))
            }
            sr.color = Color(0.46f, 0.45f, 0.49f, 1f)
            for (i in 0 until 5) {
                val next = (i + 2) % 5
                sr.rectLine(points[i], points[next], 2.5f)
            }
            return
        }

        sr.color = Color(0.28f, 0.14f, 0.35f, 1f)
        sr.circle(center.x, center.y, radius)

        sr.color = Color(0.85f, 0.30f, 1.0f, 1f)
        sr.circle(center.x, center.y, radius)
        sr.color = Color(0.20f, 0.10f, 0.26f, 1f)
        sr.circle(center.x, center.y, radius - 3.5f)

        sr.color = Color(0.92f, 0.45f, 1.0f, 1f)
        sr.circle(center.x, center.y, radius * 0.72f)
        sr.color = Color(0.20f, 0.10f, 0.26f, 1f)
        sr.circle(center.x, center.y, radius * 0.72f - 2.5f)

        val points = mutableListOf<Vector2>()
        for (i in 0 until 5) {
            val angle = Math.toRadians((90.0 + i * 72.0))
            val px = center.x + (radius * 0.70f * Math.cos(angle)).toFloat()
            val py = center.y + (radius * 0.70f * Math.sin(angle)).toFloat()
            points.add(Vector2(px, py))
        }

        sr.color = Color(1.0f, 0.50f, 1.0f, 1f)
        for (i in 0 until 5) {
            val next = (i + 2) % 5
            sr.rectLine(points[i], points[next], 3.5f)
        }

        sr.color = Color(1f, 0.88f, 0.20f, 1f)
        for (p in points) {
            sr.circle(p.x, p.y, 4f)
        }
        sr.color = Color(1f, 0.95f, 0.40f, 1f)
        sr.circle(center.x, center.y, 5f)
        sr.color = Color.WHITE
        sr.circle(center.x, center.y, 2.5f)
    }

    private fun drawDeadTree(sr: ShapeRenderer, x: Float, y: Float) {
        sr.color = Color(0.14f, 0.13f, 0.15f, 1f)
        sr.rectLine(x, y, x, y + 28f, 4f)
        sr.rectLine(x, y + 14f, x - 12f, y + 25f, 2.8f)
        sr.rectLine(x, y + 18f, x + 12f, y + 30f, 2.5f)
        sr.rectLine(x - 12f, y + 25f, x - 16f, y + 33f, 2f)
    }

    private fun drawTorch(sr: ShapeRenderer, x: Float, y: Float) {
        sr.color = Color(0.18f, 0.15f, 0.12f, 1f)
        sr.rect(x - 3f, y - 10f, 6f, 20f)
        sr.color = Color(0.95f, 0.35f, 0.05f, 0.95f)
        sr.circle(x, y + 12f, 6.5f)
        sr.color = Color(1f, 0.85f, 0.20f, 1f)
        sr.circle(x, y + 12f, 3.5f)
    }
}
