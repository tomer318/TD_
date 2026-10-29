package com.game.td.renderers.maps

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.game.td.map.Level5Silveroak

object Level5MapRenderer {

    fun render(shapeRenderer: ShapeRenderer) {
        // =====================================================================
        // 1. CON SUỐI NƯỚC & BÃI ĐÁ TÂY BẮC (CẠNH NHÀ LÍNH SYLVAN ELF)
        // =====================================================================
        val streamPoints = listOf(
            Vector2(-40f, 650f),
            Vector2(80f, 650f),
            Vector2(170f, 640f),
            Vector2(240f, 630f),
            Vector2(290f, 640f)
        )
        shapeRenderer.color = Color(0.48f, 0.52f, 0.44f, 1f)
        for (i in 0 until streamPoints.size - 1) {
            shapeRenderer.rectLine(streamPoints[i], streamPoints[i + 1], 42f)
            shapeRenderer.circle(streamPoints[i].x, streamPoints[i].y, 21f)
        }
        shapeRenderer.circle(290f, 640f, 26f)

        shapeRenderer.color = Color(0.24f, 0.55f, 0.82f, 1f)
        for (i in 0 until streamPoints.size - 1) {
            shapeRenderer.rectLine(streamPoints[i], streamPoints[i + 1], 24f)
            shapeRenderer.circle(streamPoints[i].x, streamPoints[i].y, 12f)
        }
        shapeRenderer.circle(290f, 640f, 17f)

        // Sỏi đá cuội ven bờ suối
        shapeRenderer.color = Color(0.68f, 0.70f, 0.72f, 1f)
        shapeRenderer.circle(315f, 650f, 5.5f)
        shapeRenderer.circle(325f, 640f, 4.5f)
        shapeRenderer.circle(305f, 625f, 6f)

        // =====================================================================
        // 2. VẼ 3 NHÁNH ĐƯỜNG CÁT UỐN CONG CÂN XỨNG THEO ẢNH GỐC
        // =====================================================================
        val paths = listOf(Level5Silveroak.pathWest, Level5Silveroak.pathMiddle, Level5Silveroak.pathSpider)

        // Lớp 1: Viền đất sẫm (Rộng 78px)
        shapeRenderer.color = Color(0.55f, 0.44f, 0.26f, 1f)
        for (path in paths) {
            for (i in 0 until path.size - 1) {
                shapeRenderer.rectLine(path[i], path[i + 1], 78f)
            }
            for (w in path) {
                shapeRenderer.circle(w.x, w.y, 39f)
            }
        }

        // Lớp 2: Lòng đường cát vàng mịn (Rộng 64px)
        shapeRenderer.color = Color(0.82f, 0.72f, 0.46f, 1f)
        for (path in paths) {
            for (i in 0 until path.size - 1) {
                shapeRenderer.rectLine(path[i], path[i + 1], 64f)
            }
            for (w in path) {
                shapeRenderer.circle(w.x, w.y, 32f)
            }
        }

        // Vòm ngã ba giao nhau
        shapeRenderer.circle(460f, 155f, 36f) // Ngã ba Nam tách đường
        shapeRenderer.circle(600f, 435f, 36f) // Ngã ba dưới (đón đường Rừng Nhện)
        shapeRenderer.circle(605f, 545f, 36f) // Ngã ba trên (đón đường Tây)

        // =====================================================================
        // 3. MẢNH ĐẤT LƯỠI LIỀM NẰM CHÍNH GIỮA CÂN BẰNG TUYỆT ĐỐI (CENTRAL CRESCENT RIDGE)
        // =====================================================================
        val ridgePoints = listOf(
            Vector2(335f, 285f), // Đuôi cọc vàng dưới
            Vector2(350f, 345f),
            Vector2(385f, 395f),
            Vector2(430f, 445f), // Thân cọc vàng giữa[cite: 55, 71]
            Vector2(485f, 475f),
            Vector2(535f, 490f)  // Đỉnh Tháp Cung cấp 3[cite: 55, 71]
        )

        // Viền bóng đồi đất sẫm (Rộng 68px)
        shapeRenderer.color = Color(0.24f, 0.46f, 0.14f, 1f)
        for (i in 0 until ridgePoints.size - 1) {
            shapeRenderer.rectLine(ridgePoints[i], ridgePoints[i + 1], 68f)
            shapeRenderer.circle(ridgePoints[i].x, ridgePoints[i].y, 34f)
        }
        shapeRenderer.circle(ridgePoints.last().x, ridgePoints.last().y, 34f)

        // Mặt gờ đồi cỏ xanh sáng (Rộng 56px - thanh mảnh, cách đều hai đường 40px)
        shapeRenderer.color = Color(0.38f, 0.65f, 0.22f, 1f)
        for (i in 0 until ridgePoints.size - 1) {
            shapeRenderer.rectLine(ridgePoints[i], ridgePoints[i + 1], 56f)
            shapeRenderer.circle(ridgePoints[i].x, ridgePoints[i].y, 28f)
        }
        shapeRenderer.circle(ridgePoints.last().x, ridgePoints.last().y, 28f)

        // Bệ đá pháo đài tại đỉnh đầu dải đồi cho Tháp Cung cấp 3
        val archerSpot = Level5Silveroak.prebuiltArcherSpot
        shapeRenderer.color = Color(0.45f, 0.46f, 0.48f, 1f)
        shapeRenderer.circle(archerSpot.x, archerSpot.y, 34f)
        shapeRenderer.color = Color(0.58f, 0.60f, 0.62f, 1f)
        shapeRenderer.circle(archerSpot.x, archerSpot.y, 26f)
        shapeRenderer.color = Color(0.35f, 0.60f, 0.20f, 1f)
        shapeRenderer.circle(archerSpot.x, archerSpot.y, 18f)

        // Bụi cây nhỏ mọc dọc dải đồi
        drawBush(shapeRenderer, 365f, 365f)
        drawBush(shapeRenderer, 470f, 465f)

        // =====================================================================
        // 4. GÒ ĐỒI TRÒN TRUNG TÂM (CHỨA 5 CỌC THÁP VÀ CÂY Ở GIỮA)
        // =====================================================================
        val hillCenterX = 565f
        val hillCenterY = 250f

        // Viền bóng đồi
        shapeRenderer.color = Color(0.24f, 0.46f, 0.14f, 1f)
        shapeRenderer.circle(hillCenterX, hillCenterY, 78f)

        // Mặt đồi cỏ xanh
        shapeRenderer.color = Color(0.38f, 0.65f, 0.22f, 1f)
        shapeRenderer.circle(hillCenterX, hillCenterY, 70f)

        // Cây xanh đứng sừng sững chính giữa gò đồi
        drawSingleTree(shapeRenderer, hillCenterX, hillCenterY)

        // =====================================================================
        // 5. KHU RỪNG NHỆN ĐÔNG NAM (SPIDER FOREST & COBWEBS)
        // =====================================================================
        val spiderNestX = 1120f
        val spiderNestY = 190f

        // Nền đất ẩm sẫm màu đầm lầy nhện quanh cửa hang
        shapeRenderer.color = Color(0.28f, 0.30f, 0.20f, 1f)
        shapeRenderer.circle(spiderNestX, spiderNestY, 70f)
        shapeRenderer.circle(spiderNestX - 45f, spiderNestY, 50f)

        // Cửa hang nhện tối tăm dưới lòng đất
        shapeRenderer.color = Color(0.18f, 0.16f, 0.14f, 1f)
        shapeRenderer.circle(spiderNestX, spiderNestY, 44f)
        shapeRenderer.color = Color(0.05f, 0.05f, 0.05f, 1f)
        shapeRenderer.circle(spiderNestX, spiderNestY, 30f)

        // =====================================================================
        // 6. RỪNG RẬM BAO QUANH (LÙI SÂU x <= 90F - KHÔNG CHE ĐƯỜNG & KHÔNG CHE CỌC)
        // =====================================================================
        val westForest = listOf(
            Vector2(20f, 700f), Vector2(70f, 690f), Vector2(110f, 680f),
            Vector2(20f, 600f), Vector2(65f, 590f), Vector2(100f, 570f),
            Vector2(15f, 500f), Vector2(50f, 490f), Vector2(85f,  470f),
            Vector2(15f, 410f), Vector2(45f, 400f), Vector2(80f,  380f),
            Vector2(15f, 320f), Vector2(45f, 300f), Vector2(75f,  270f),
            Vector2(15f, 230f), Vector2(45f, 210f), Vector2(75f,  180f),
            Vector2(15f, 140f), Vector2(45f, 120f), Vector2(80f,  80f),
            Vector2(15f, 50f),  Vector2(50f, 20f),  Vector2(170f, 10f)
        )
        drawDenseForest(shapeRenderer, westForest)

        // Rừng rậm Đông Bắc và Rừng Nhện Đông Nam bao bọc kín lối vào
        val eastForest = listOf(
            Vector2(920f, 700f), Vector2(1000f, 690f), Vector2(1080f, 680f), Vector2(1160f, 670f), Vector2(1240f, 660f),
            Vector2(940f, 610f), Vector2(1020f, 600f), Vector2(1110f, 590f), Vector2(1190f, 580f), Vector2(1260f, 570f),
            Vector2(970f, 510f), Vector2(1050f, 500f), Vector2(1130f, 490f), Vector2(1210f, 480f),
            Vector2(970f, 410f), Vector2(1050f, 400f), Vector2(1130f, 390f), Vector2(1210f, 380f),
            Vector2(1040f, 280f), Vector2(1120f, 280f), Vector2(1200f, 280f), Vector2(1260f, 280f),
            Vector2(1190f, 190f), Vector2(1260f, 190f),
            Vector2(1000f, 100f), Vector2(1080f, 90f),  Vector2(1160f, 90f),  Vector2(1240f, 90f),
            Vector2(900f, 30f),   Vector2(980f, 20f),   Vector2(1060f, 20f),  Vector2(1140f, 10f)
        )
        drawDenseForest(shapeRenderer, eastForest)

        // =====================================================================
        // 7. MẠNG NHỆN DÀY ĐẶC GIĂNG KÍN TÁN CÂY RỪNG NHỆN (COBWEBS)
        // =====================================================================
        shapeRenderer.color = Color(0.92f, 0.94f, 0.96f, 0.82f)
        shapeRenderer.rectLine(1030f, 250f, 1110f, 190f, 2.5f)
        shapeRenderer.rectLine(1070f, 270f, 1120f, 200f, 2.5f)
        shapeRenderer.rectLine(1120f, 270f, 1120f, 190f, 2.5f)
        shapeRenderer.rectLine(1180f, 250f, 1120f, 190f, 2.5f)
        shapeRenderer.rectLine(1040f, 120f, 1100f, 180f, 2.5f)
        shapeRenderer.rectLine(1100f, 110f, 1120f, 180f, 2.5f)
        shapeRenderer.rectLine(1170f, 120f, 1130f, 180f, 2.5f)
        shapeRenderer.circle(spiderNestX, spiderNestY, 24f)
        shapeRenderer.circle(spiderNestX, spiderNestY, 38f)

        // Túi bọc trứng nhện trắng treo lủng lẳng trên cành cây
        shapeRenderer.color = Color(0.95f, 0.95f, 0.90f, 0.95f)
        shapeRenderer.circle(1060f, 260f, 5.5f)
        shapeRenderer.circle(1085f, 250f, 4.5f)
        shapeRenderer.circle(1055f, 120f, 5f)

        // Hai lá cờ Linirea ở cổng Bắc
        shapeRenderer.color = Color(0.35f, 0.25f, 0.18f, 1f)
        shapeRenderer.rect(595f, 660f, 3.5f, 28f)
        shapeRenderer.rect(655f, 660f, 3.5f, 28f)
        shapeRenderer.color = Color(0.20f, 0.55f, 0.90f, 1f)
        shapeRenderer.triangle(598f, 688f, 598f, 670f, 616f, 679f)
        shapeRenderer.triangle(658f, 688f, 658f, 670f, 676f, 679f)

        // Vòng tròn phòng thủ dưới đất & Hai lá cờ Hiệp sĩ Linirea ở cổng Bắc
        drawGroundCrest(shapeRenderer, 625f, 650f)
        drawLinireaBanner(shapeRenderer, 582f, 650f)
        drawLinireaBanner(shapeRenderer, 668f, 650f)
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

    private fun drawBush(sr: ShapeRenderer, x: Float, y: Float) {
        sr.color = Color(0.26f, 0.48f, 0.16f, 1f)
        sr.circle(x, y, 9f)
        sr.color = Color(0.34f, 0.58f, 0.20f, 1f)
        sr.circle(x - 2f, y + 2f, 6.5f)
    }

    private fun drawSingleTree(sr: ShapeRenderer, x: Float, y: Float) {
        sr.color = Color(0.12f, 0.28f, 0.08f, 1f)
        sr.circle(x, y, 22f)
        sr.color = Color(0.20f, 0.48f, 0.14f, 1f)
        sr.circle(x - 3f, y + 3f, 16f)
    }

    private fun drawDenseForest(sr: ShapeRenderer, trees: List<Vector2>) {
        sr.color = Color(0.08f, 0.20f, 0.06f, 1f)
        for (tr in trees) sr.circle(tr.x, tr.y - 4f, 44f)
        sr.color = Color(0.12f, 0.30f, 0.08f, 1f)
        for (tr in trees) sr.circle(tr.x, tr.y, 40f)
        sr.color = Color(0.18f, 0.44f, 0.12f, 1f)
        for (tr in trees) sr.circle(tr.x - 3f, tr.y + 4f, 28f)
    }
}
