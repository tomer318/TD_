package com.game.td.renderers.maps

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.game.td.map.Level7ColdstepMines

object Level7MapRenderer {

    fun render(shapeRenderer: ShapeRenderer) {
        // 1. NỀN TUYẾT TRẮNG
        shapeRenderer.color = Color(0.92f, 0.94f, 0.96f, 1f)
        shapeRenderer.rect(0f, 0f, 1280f, 720f)

        // 2. KHỐI NÚI ĐÁ TUYẾT XÁM 2 BÊN RÌA
        shapeRenderer.color = Color(0.50f, 0.54f, 0.58f, 1f)
        // Núi Tây
        shapeRenderer.triangle(0f, 720f, 320f, 720f, 0f, 400f)
        shapeRenderer.triangle(0f, 450f, 260f, 320f, 0f, 120f)
        shapeRenderer.triangle(0f, 200f, 220f, 100f, 0f, 0f)

        // Núi Đông
        shapeRenderer.triangle(1280f, 720f, 960f, 720f, 1280f, 400f)
        shapeRenderer.triangle(1280f, 450f, 1020f, 320f, 1280f, 120f)
        shapeRenderer.triangle(1280f, 200f, 1060f, 100f, 1280f, 0f)

        // Mũ tuyết trắng trên đỉnh núi
        shapeRenderer.color = Color(0.96f, 0.98f, 1f, 1f)
        shapeRenderer.triangle(0f, 720f, 180f, 720f, 0f, 520f)
        shapeRenderer.triangle(1280f, 720f, 1100f, 720f, 1280f, 520f)

        // 3. KHỞI TẠO CÁC ĐOẠN ĐƯỜNG VẼ CHUẨN XÁC
        val mainPath = Level7ColdstepMines.pathMain

        // Đoạn nhánh rẽ vào Hang Tây Bắc (nối từ đường chính vào cửa hang, không nối bừa)
        val branchIntoTopCave = listOf(
            Vector2(735f, 570f),
            Vector2(650f, 595f),
            Vector2(550f, 620f),
            Vector2(470f, 665f)
        )

        // Đoạn nhánh từ Hang Đông đi ra (nhập vào đường chính)
        val branchOutEastCave = listOf(
            Vector2(870f, 410f),
            Vector2(855f, 350f)
        )

        // Đoạn nhánh từ Hang Tây Nam đi ra (nhập vào cua tầng dưới)
        val branchOutSouthWestCave = listOf(
            Vector2(370f, 220f),
            Vector2(405f, 175f),
            Vector2(420f, 115f)
        )

        // A. Lớp viền đất tuyết đậm (Rộng 72px)
        shapeRenderer.color = Color(0.74f, 0.77f, 0.82f, 1f)
        drawPathStrip(shapeRenderer, mainPath, 72f)
        drawPathStrip(shapeRenderer, branchIntoTopCave, 72f)
        drawPathStrip(shapeRenderer, branchOutEastCave, 72f)
        drawPathStrip(shapeRenderer, branchOutSouthWestCave, 72f)

        // B. Lòng đường đất tuyết sáng (Rộng 56px)
        shapeRenderer.color = Color(0.86f, 0.89f, 0.93f, 1f)
        drawPathStrip(shapeRenderer, mainPath, 56f)
        drawPathStrip(shapeRenderer, branchIntoTopCave, 56f)
        drawPathStrip(shapeRenderer, branchOutEastCave, 56f)
        drawPathStrip(shapeRenderer, branchOutSouthWestCave, 56f)

        // 4. BA CỬA HANG MỎ GỖ
        drawCaveEntrance(shapeRenderer, 470f, 670f) // Hang Tây Bắc
        drawCaveEntrance(shapeRenderer, 870f, 410f) // Hang Đông
        drawCaveEntrance(shapeRenderer, 370f, 220f) // Hang Tây Nam

        // 5. BA GỜ ĐẤT CẮM THÁP (HẠ TỌA ĐỘ Y NẰM GỌN LỌT LÒNG, KHÔNG ĐÈ LÒNG ĐƯỜNG)
        // Gờ tầng trên (3 cọc): hạ tâm y từ 460f xuống 435f
        drawTowerPlateau(shapeRenderer, 600f, 435f, 260f, 40f)

        // Gờ tầng giữa (4 cọc): hạ tâm y từ 285f xuống 260f
        drawTowerPlateau(shapeRenderer, 615f, 260f, 345f, 40f)

        // Gờ tầng dưới (4 cọc): giữ nguyên tâm y = 115f
        drawTowerPlateau(shapeRenderer, 685f, 115f, 335f, 42f)

        // 6. RỪNG THÔNG TUYẾT DƯỚI ĐÁY
        for (x in 60..420 step 45) {
            drawPineTree(shapeRenderer, x.toFloat(), 40f)
        }
        for (x in 860..1220 step 45) {
            drawPineTree(shapeRenderer, x.toFloat(), 40f)
        }

        // 7. VÒNG TRÒN PHÒNG THỦ DƯỚI ĐẤT & 2 CỜ HIỆU LINIREA TẠI LỐI THOÁT MÀN 7
        drawDefensePointForPath(shapeRenderer, mainPath)
    }

    private fun drawDefensePointForPath(sr: ShapeRenderer, path: List<Vector2>) {
        if (path.size < 2) return
        var idx = path.size - 1
        while (idx > 0 && (path[idx].x !in 65f..1215f || path[idx].y !in 62f..658f)) {
            idx--
        }
        val center = path[idx]
        val prev = path[(idx - 1).coerceAtLeast(0)]
        val dir = Vector2(center).sub(prev)
        if (dir.isZero) dir.set(0f, -1f) else dir.nor()
        val normal = Vector2(-dir.y, dir.x)

        sr.color = Color(0.18f, 0.48f, 0.82f, 0.55f)
        sr.ellipse(center.x - 28f, center.y - 19f, 56f, 38f)
        sr.color = Color(0.52f, 0.84f, 1f, 0.70f)
        sr.ellipse(center.x - 22f, center.y - 15f, 44f, 30f)
        sr.color = Color(0.86f, 0.89f, 0.93f, 0.92f)
        sr.ellipse(center.x - 16f, center.y - 11f, 32f, 22f)
        sr.color = Color(0.22f, 0.56f, 0.88f, 0.70f)
        sr.rect(center.x - 7f, center.y - 2f, 14f, 9f)
        sr.triangle(center.x - 7f, center.y - 2f, center.x + 7f, center.y - 2f, center.x, center.y - 9f)

        val f1 = Vector2(center).add(Vector2(normal).scl(40f))
        val f2 = Vector2(center).add(Vector2(normal).scl(-40f))
        if (f1.y >= f2.y) {
            drawLinireaBanner(sr, f1.x, f1.y)
            drawLinireaBanner(sr, f2.x, f2.y)
        } else {
            drawLinireaBanner(sr, f2.x, f2.y)
            drawLinireaBanner(sr, f1.x, f1.y)
        }
    }

    private fun drawLinireaBanner(sr: ShapeRenderer, x: Float, y: Float) {
        val outline = Color(0.12f, 0.09f, 0.07f, 1f)
        sr.color = Color(0f, 0f, 0f, 0.25f)
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
    private fun drawPathStrip(sr: ShapeRenderer, pts: List<Vector2>, width: Float) {
        val radius = width / 2f
        for (i in 0 until pts.size - 1) {
            sr.rectLine(pts[i], pts[i + 1], width)
            sr.circle(pts[i].x, pts[i].y, radius)
        }
        if (pts.isNotEmpty()) {
            sr.circle(pts.last().x, pts.last().y, radius)
        }
    }

    private fun drawCaveEntrance(sr: ShapeRenderer, x: Float, y: Float) {
        sr.color = Color(0.12f, 0.12f, 0.14f, 1f)
        sr.rect(x - 24f, y - 20f, 48f, 36f)
        sr.circle(x, y + 16f, 24f)

        sr.color = Color(0.42f, 0.28f, 0.16f, 1f)
        sr.rect(x - 26f, y - 22f, 8f, 42f)
        sr.rect(x + 18f, y - 22f, 8f, 42f)
        sr.rect(x - 26f, y + 16f, 52f, 8f)

        sr.color = Color(0.25f, 0.25f, 0.28f, 1f)
        sr.rectLine(Vector2(x - 12f, y - 24f), Vector2(x - 6f, y - 2f), 3f)
        sr.rectLine(Vector2(x + 12f, y - 24f), Vector2(x + 6f, y - 2f), 3f)
    }

    private fun drawTowerPlateau(sr: ShapeRenderer, cx: Float, cy: Float, w: Float, h: Float) {
        val halfW = w / 2f
        val rad = h / 2f
        sr.color = Color(0.72f, 0.75f, 0.80f, 1f)
        sr.rect(cx - halfW, cy - rad - 2f, w, h + 4f)
        sr.circle(cx - halfW, cy, rad + 2f)
        sr.circle(cx + halfW, cy, rad + 2f)

        sr.color = Color(0.86f, 0.89f, 0.93f, 1f)
        sr.rect(cx - halfW, cy - rad, w, h)
        sr.circle(cx - halfW, cy, rad)
        sr.circle(cx + halfW, cy, rad)
    }

    private fun drawPineTree(sr: ShapeRenderer, x: Float, y: Float) {
        sr.color = Color(0.35f, 0.22f, 0.12f, 1f)
        sr.rect(x - 3f, y - 10f, 6f, 14f)

        sr.color = Color(0.20f, 0.40f, 0.28f, 1f)
        sr.triangle(x - 20f, y, x + 20f, y, x, y + 26f)
        sr.triangle(x - 16f, y + 14f, x + 16f, y + 14f, x, y + 38f)

        sr.color = Color(0.92f, 0.95f, 0.98f, 0.9f)
        sr.triangle(x - 8f, y + 26f, x + 8f, y + 26f, x, y + 40f)
    }
}
