package com.game.td.renderers.maps

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.game.td.map.Level4TwinRivers

object Level4MapRenderer {

    fun render(shapeRenderer: ShapeRenderer) {
        // =====================================================================
        // 1. DÒNG SÔNG CHẢY CHÉO 38 ĐỘ QUA GIỮA ĐẢO
        // =====================================================================
        val riverPoints = listOf(
            Vector2(40f, -40f),
            Vector2(140f, 25f),
            Vector2(260f, 115f),
            Vector2(420f, 232f), // Ngay dưới chân Cầu Tây Nam
            Vector2(590f, 365f), // Tâm đảo
            Vector2(760f, 498f), // Ngay dưới chân Cầu Đông Bắc
            Vector2(920f, 620f),
            Vector2(1080f, 690f),
            Vector2(1280f, 730f),
            Vector2(1320f, 740f)
        )

        // Bờ phù sa xanh rêu đậm ven sông
        shapeRenderer.color = Color(0.38f, 0.58f, 0.28f, 1f)
        for (i in 0 until riverPoints.size - 1) {
            shapeRenderer.rectLine(riverPoints[i], riverPoints[i + 1], 96f)
            shapeRenderer.circle(riverPoints[i].x, riverPoints[i].y, 48f)
        }

        // Lòng sông nước xanh biếc
        shapeRenderer.color = Color(0.24f, 0.52f, 0.82f, 1f)
        for (i in 0 until riverPoints.size - 1) {
            shapeRenderer.rectLine(riverPoints[i], riverPoints[i + 1], 72f)
            shapeRenderer.circle(riverPoints[i].x, riverPoints[i].y, 36f)
        }

        // Gợn sóng nước trắng sáng
        shapeRenderer.color = Color(0.48f, 0.72f, 0.95f, 0.85f)
        shapeRenderer.rectLine(200f, 70f, 240f, 100f, 3f)
        shapeRenderer.rectLine(540f, 325f, 580f, 355f, 3.5f)
        shapeRenderer.rectLine(990f, 650f, 1040f, 680f, 3f)

        // =====================================================================
        // 2. VẼ ĐƯỜNG ĐI UỐN LƯỢN HOÀN TOÀN MƯỢT MÀ VÀ CÂN ĐỐI
        // =====================================================================
        val paths = listOf(Level4TwinRivers.westWaypoints, Level4TwinRivers.eastWaypoints)

        // Lớp 1: Viền đất đậm (Rộng 78px)
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

        // Làm mịn hai nút giao ngã ba
        shapeRenderer.circle(590f, 150f, 36f)
        shapeRenderer.circle(590f, 580f, 36f)

        // =====================================================================
        // 3. VẼ HAI CÂY CẦU GỖ ĐÈ LÊN TRÊN ĐƯỜNG VÀ SÔNG (TUYỆT ĐỐI KHÔNG BỊ CHE)
        // =====================================================================
        // Cầu Tây Nam tại (420, 232)
        drawWoodenBridge(shapeRenderer, Vector2(420f, 232f))

        // Cầu Đông Bắc tại (760, 498)
        drawWoodenBridge(shapeRenderer, Vector2(760f, 498f))

        // =====================================================================
        // 4. RỪNG RẬM BAO QUANH
        // =====================================================================
        val westForest = listOf(
            Vector2(30f, 700f), Vector2(100f, 690f), Vector2(170f, 680f), Vector2(240f, 660f),
            Vector2(30f, 620f), Vector2(90f, 600f),  Vector2(150f, 580f), Vector2(210f, 550f),
            Vector2(20f, 520f), Vector2(70f, 500f),  Vector2(130f, 470f), Vector2(180f, 430f),
            Vector2(20f, 420f), Vector2(70f, 390f),  Vector2(120f, 360f), Vector2(170f, 310f),
            Vector2(20f, 310f), Vector2(60f, 280f),  Vector2(110f, 240f),
            Vector2(20f, 200f), Vector2(60f, 160f)
        )
        drawDenseForest(shapeRenderer, westForest)

        val eastForest = listOf(
            Vector2(1040f, 520f), Vector2(1120f, 530f), Vector2(1200f, 540f), Vector2(1260f, 550f),
            Vector2(1020f, 440f), Vector2(1100f, 440f), Vector2(1180f, 440f), Vector2(1260f, 440f),
            Vector2(1000f, 350f), Vector2(1080f, 350f), Vector2(1160f, 350f), Vector2(1240f, 350f),
            Vector2(980f,  260f), Vector2(1060f, 250f), Vector2(1140f, 250f), Vector2(1220f, 240f),
            Vector2(950f,  170f), Vector2(1030f, 160f), Vector2(1110f, 150f), Vector2(1190f, 140f),
            Vector2(850f,  40f),  Vector2(930f,  30f),  Vector2(1010f, 20f),  Vector2(1090f, 10f)
        )
        drawDenseForest(shapeRenderer, eastForest)

        val northForest = listOf(
            Vector2(320f, 705f), Vector2(390f, 705f), Vector2(460f, 705f), Vector2(530f, 705f),
            Vector2(650f, 705f), Vector2(720f, 705f), Vector2(790f, 705f), Vector2(860f, 705f)
        )
        drawDenseForest(shapeRenderer, northForest)

        // =====================================================================
        // 5. CẢNH QUAN TRÊN ĐẢO: TÚP LỀU, GIÀN CÁ, ĐÀN CỪU, ĐỤN RƠM, THÙNG GỖ
        // =====================================================================
        val shackX = 590f
        val shackY = 460f

        // Túp lều gỗ mái rơm sẫm
        shapeRenderer.color = Color(0.42f, 0.28f, 0.15f, 1f)
        shapeRenderer.rect(shackX - 22f, shackY - 14f, 44f, 28f)
        shapeRenderer.color = Color(0.72f, 0.50f, 0.22f, 1f)
        shapeRenderer.triangle(shackX - 28f, shackY + 14f, shackX + 28f, shackY + 14f, shackX, shackY + 36f)

        // Cửa lều
        shapeRenderer.color = Color(0.20f, 0.14f, 0.08f, 1f)
        shapeRenderer.rect(shackX - 6f, shackY - 14f, 12f, 18f)

        // Giàn phơi cá
        shapeRenderer.color = Color(0.35f, 0.22f, 0.12f, 1f)
        shapeRenderer.rectLine(shackX - 45f, shackY - 14f, shackX - 45f, shackY + 12f, 2.5f)
        shapeRenderer.rectLine(shackX - 25f, shackY - 14f, shackX - 25f, shackY + 12f, 2.5f)
        shapeRenderer.rectLine(shackX - 48f, shackY + 8f, shackX - 22f, shackY + 8f, 2.5f)

        // Con cá phơi
        shapeRenderer.color = Color(0.65f, 0.75f, 0.85f, 1f)
        shapeRenderer.circle(shackX - 40f, shackY + 2f, 3.5f)
        shapeRenderer.circle(shackX - 32f, shackY + 2f, 3.5f)

        // Cần câu cá vươn ra sông
        shapeRenderer.color = Color(0.45f, 0.32f, 0.16f, 1f)
        shapeRenderer.rectLine(shackX + 18f, shackY - 4f, shackX + 46f, shackY - 26f, 2f)
        shapeRenderer.color = Color(0.9f, 0.9f, 0.9f, 0.8f)
        shapeRenderer.rectLine(shackX + 46f, shackY - 26f, shackX + 46f, shackY - 44f, 1.2f)

        // Đàn cừu trên đảo Tây Bắc
        drawGrazingSheep(shapeRenderer, 500f, 490f)
        drawGrazingSheep(shapeRenderer, 535f, 470f)
        drawGrazingSheep(shapeRenderer, 475f, 435f)

        // Đụn rơm và thùng gỗ ở đảo Đông Nam
        drawHaystack(shapeRenderer, 660f, 335f)
        shapeRenderer.color = Color(0.65f, 0.45f, 0.22f, 1f)
        shapeRenderer.rect(700f, 330f, 15f, 15f)
        shapeRenderer.rect(718f, 330f, 13f, 13f)
        shapeRenderer.color = Color(0.45f, 0.28f, 0.14f, 1f)
        shapeRenderer.circle(692f, 342f, 6.5f)

        drawGrazingSheep(shapeRenderer, 685f, 275f)

        // Hai lá cờ Linirea ở cổng Bắc
        shapeRenderer.color = Color(0.35f, 0.25f, 0.18f, 1f)
        shapeRenderer.rect(555f, 640f, 3.5f, 28f)
        shapeRenderer.rect(625f, 640f, 3.5f, 28f)
        shapeRenderer.color = Color(0.20f, 0.55f, 0.90f, 1f)
        shapeRenderer.triangle(558f, 668f, 558f, 650f, 576f, 659f)
        shapeRenderer.triangle(628f, 668f, 628f, 650f, 646f, 659f)
    }

    // HÀM VẼ CẦU GỖ NGHIÊNG GÓC 38 ĐỘ THEO DÒNG SÔNG
    private fun drawWoodenBridge(sr: ShapeRenderer, center: Vector2) {
        val uRiver = Vector2(0.788f, 0.616f) // Vector đơn vị dọc theo sông (38 độ)
        val uRoad = Vector2(-0.616f, 0.788f) // Vector đơn vị dọc theo đường (128 độ)

        // 1. Trụ dầm cầu màu gỗ sẫm
        sr.color = Color(0.30f, 0.18f, 0.10f, 1f)
        for (dr in listOf(-26f, 26f)) {
            val p1 = Vector2(center).add(Vector2(uRoad).scl(-35f)).add(Vector2(uRiver).scl(dr))
            val p2 = Vector2(center).add(Vector2(uRoad).scl(35f)).add(Vector2(uRiver).scl(dr))
            sr.rectLine(p1, p2, 8f)
        }

        // 2. Các tấm ván sàn cầu (Planks) bắc ngang sông
        val plankOffsets = listOf(-28f, -14f, 0f, 14f, 28f)
        for (i in plankOffsets.indices) {
            val dRoad = plankOffsets[i]
            val p1 = Vector2(center).add(Vector2(uRoad).scl(dRoad)).add(Vector2(uRiver).scl(-27f))
            val p2 = Vector2(center).add(Vector2(uRoad).scl(dRoad)).add(Vector2(uRiver).scl(27f))

            // Màu ván gỗ sáng nổi bật
            sr.color = if (i % 2 == 0) Color(0.66f, 0.46f, 0.25f, 1f) else Color(0.58f, 0.40f, 0.22f, 1f)
            sr.rectLine(p1, p2, 11f)

            // Khe rãnh tối giữa các tấm ván
            sr.color = Color(0.24f, 0.15f, 0.08f, 1f)
            sr.rectLine(p1, p2, 1.5f)
        }

        // 3. Hai thanh lan can gỗ (Railings) nằm ở hai mép sông
        sr.color = Color(0.38f, 0.24f, 0.12f, 1f)
        for (dr in listOf(-27f, 27f)) {
            val p1 = Vector2(center).add(Vector2(uRoad).scl(-36f)).add(Vector2(uRiver).scl(dr))
            val p2 = Vector2(center).add(Vector2(uRoad).scl(36f)).add(Vector2(uRiver).scl(dr))
            sr.rectLine(p1, p2, 4f)

            // 3 cọc trụ lan can
            for (dRoad in listOf(-32f, 0f, 32f)) {
                val postPos = Vector2(center).add(Vector2(uRoad).scl(dRoad)).add(Vector2(uRiver).scl(dr))
                sr.color = Color(0.25f, 0.14f, 0.08f, 1f)
                sr.circle(postPos.x, postPos.y, 3.5f)
            }
        }
    }

    private fun drawHaystack(sr: ShapeRenderer, x: Float, y: Float) {
        sr.color = Color(0.85f, 0.75f, 0.25f, 1f)
        sr.circle(x, y, 16f)
        sr.color = Color(0.95f, 0.86f, 0.38f, 1f)
        sr.circle(x - 2f, y + 2f, 11f)
    }

    private fun drawGrazingSheep(sr: ShapeRenderer, x: Float, y: Float) {
        sr.color = Color.WHITE
        sr.circle(x, y, 6.5f)
        sr.circle(x + 4f, y + 2f, 4f)
        sr.color = Color.BLACK
        sr.circle(x + 5.5f, y + 3f, 1f)
    }

    private fun drawDenseForest(sr: ShapeRenderer, trees: List<Vector2>) {
        sr.color = Color(0.10f, 0.22f, 0.06f, 1f)
        for (tr in trees) sr.circle(tr.x, tr.y - 4f, 40f)
        sr.color = Color(0.15f, 0.32f, 0.09f, 1f)
        for (tr in trees) sr.circle(tr.x, tr.y, 36f)
        sr.color = Color(0.22f, 0.46f, 0.14f, 1f)
        for (tr in trees) sr.circle(tr.x - 3f, tr.y + 4f, 25f)
    }
}
