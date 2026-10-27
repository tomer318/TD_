package com.game.td.renderers.maps

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2

object Level3MapRenderer {

    fun render(shapeRenderer: ShapeRenderer) {
        // =====================================================================
        // 1. CỤM RỪNG PHÍA BẮC (DỌC THEO MÉP TRÊN MÀN HÌNH)
        // =====================================================================
        val northForest = listOf(
            Vector2(30f, 705f), Vector2(100f, 700f), Vector2(170f, 695f), Vector2(240f, 690f),
            Vector2(380f, 680f), Vector2(450f, 680f), Vector2(520f, 680f), Vector2(590f, 680f),
            Vector2(660f, 680f), Vector2(730f, 680f), Vector2(800f, 680f), Vector2(870f, 680f),
            Vector2(940f, 685f), Vector2(1010f, 690f), Vector2(1080f, 695f), Vector2(1150f, 700f), Vector2(1220f, 705f)
        )
        drawDenseForest(shapeRenderer, northForest)

        // Rừng rậm rìa Tây (phía trên cổng thoát)
        val westForest = listOf(
            Vector2(30f, 630f), Vector2(90f, 620f), Vector2(150f, 600f), Vector2(210f, 580f),
            Vector2(30f, 550f), Vector2(80f, 540f), Vector2(140f, 520f), Vector2(190f, 490f),
            Vector2(20f, 470f), Vector2(70f, 450f), Vector2(120f, 430f), Vector2(170f, 400f),
            Vector2(20f, 390f), Vector2(65f, 370f), Vector2(110f, 340f)
        )
        drawDenseForest(shapeRenderer, westForest)

        // Rừng rậm Đông Nam (phía dưới cổng vào)
        val southEastForest = listOf(
            Vector2(980f, 240f), Vector2(1060f, 250f), Vector2(1140f, 250f), Vector2(1220f, 250f),
            Vector2(930f, 170f), Vector2(1010f, 180f), Vector2(1090f, 180f), Vector2(1170f, 180f), Vector2(1250f, 180f),
            Vector2(890f, 100f), Vector2(970f, 100f),  Vector2(1050f, 100f), Vector2(1130f, 100f), Vector2(1210f, 100f),
            Vector2(880f, 20f),  Vector2(960f, 20f),   Vector2(1040f, 20f),  Vector2(1120f, 20f),  Vector2(1200f, 20f)
        )
        drawDenseForest(shapeRenderer, southEastForest)

        // =====================================================================
        // 2. KHU DOANH TRẠI QUÂN ĐỘI TÂY BẮC (MILITARY CAMP)
        // =====================================================================
        val campX = 290f
        val campY = 570f

        // Lều chính tròn lớn sọc xanh lam - trắng
        shapeRenderer.color = Color(0.20f, 0.45f, 0.85f, 1f)
        shapeRenderer.circle(campX, campY, 26f)
        shapeRenderer.color = Color(0.92f, 0.92f, 0.95f, 1f)
        shapeRenderer.triangle(campX - 12f, campY - 22f, campX + 12f, campY - 22f, campX, campY + 22f)
        shapeRenderer.color = Color(0.85f, 0.65f, 0.15f, 1f)
        shapeRenderer.circle(campX, campY + 22f, 3.5f)

        // Lều phụ tam giác nhỏ
        shapeRenderer.color = Color(0.85f, 0.85f, 0.88f, 1f)
        shapeRenderer.triangle(campX + 35f, campY - 20f, campX + 65f, campY - 20f, campX + 50f, campY + 5f)
        shapeRenderer.color = Color(0.25f, 0.18f, 0.12f, 1f)
        shapeRenderer.triangle(campX + 44f, campY - 20f, campX + 56f, campY - 20f, campX + 50f, campY - 5f)

        // Đống củi xẻ
        shapeRenderer.color = Color(0.48f, 0.32f, 0.16f, 1f)
        shapeRenderer.rect(campX - 5f, campY - 48f, 24f, 10f)
        shapeRenderer.rect(campX - 1f, campY - 40f, 16f, 8f)

        // =====================================================================
        // 3. KHU CỐI XAY GIÓ & RUỘNG RƠM ĐÔNG BẮC
        // =====================================================================
        drawWindmill(shapeRenderer, 1080f, 605f)
        drawWindmill(shapeRenderer, 1140f, 605f)

        // Rào gỗ bao quanh đụn rơm
        shapeRenderer.color = Color(0.50f, 0.35f, 0.18f, 1f)
        shapeRenderer.rectLine(1030f, 415f, 1170f, 415f, 2.5f)
        for (i in 0 until 5) {
            shapeRenderer.rect(1035f + i * 30f, 408f, 3f, 14f)
        }
        drawHaystack(shapeRenderer, 1070f, 425f)
        drawHaystack(shapeRenderer, 1130f, 425f)

        // =====================================================================
        // 4. HỒ NƯỚC, BỜ CÁT VÀ CẦU CẢNG GỖ TÂY NAM
        // =====================================================================
        // Bờ cát vàng cong
        shapeRenderer.color = Color(0.88f, 0.78f, 0.48f, 1f)
        shapeRenderer.circle(230f, -20f, 165f)
        shapeRenderer.circle(100f, -10f, 135f)

        // Lòng hồ nước xanh biếc
        shapeRenderer.color = Color(0.24f, 0.52f, 0.82f, 1f)
        shapeRenderer.circle(230f, -20f, 145f)
        shapeRenderer.circle(100f, -10f, 115f)

        // Cầu cảng gỗ
        shapeRenderer.color = Color(0.42f, 0.28f, 0.14f, 1f)
        shapeRenderer.rect(235f, 35f, 50f, 24f)
        shapeRenderer.color = Color(0.55f, 0.38f, 0.20f, 1f)
        for (i in 0 until 4) {
            shapeRenderer.rect(238f + i * 12f, 37f, 9f, 20f)
        }

        // Túp lều ngư dân mái tranh
        shapeRenderer.color = Color(0.45f, 0.30f, 0.15f, 1f)
        shapeRenderer.rect(480f, 50f, 38f, 26f)
        shapeRenderer.color = Color(0.85f, 0.72f, 0.28f, 1f)
        shapeRenderer.triangle(470f, 76f, 528f, 76f, 499f, 102f)

        // Đụn rơm nhỏ & đàn cừu gặm cỏ
        drawHaystack(shapeRenderer, 615f, 75f)
        drawGrazingSheep(shapeRenderer, 550f, 55f)
        drawGrazingSheep(shapeRenderer, 575f, 45f)

        // =====================================================================
        // 5. HAI LÁ CỜ LINIREA XANH TẠI CỔNG THOÁT TÂY
        // =====================================================================
        shapeRenderer.color = Color(0.35f, 0.25f, 0.18f, 1f)
        shapeRenderer.rect(145f, 260f, 3f, 26f)
        shapeRenderer.rect(145f, 190f, 3f, 26f)
        shapeRenderer.color = Color(0.20f, 0.55f, 0.90f, 1f)
        shapeRenderer.triangle(148f, 285f, 148f, 270f, 166f, 277f)
        shapeRenderer.triangle(148f, 215f, 148f, 200f, 166f, 207f)
    }

    private fun drawHaystack(sr: ShapeRenderer, x: Float, y: Float) {
        sr.color = Color(0.85f, 0.75f, 0.25f, 1f)
        sr.circle(x, y, 15f)
        sr.color = Color(0.95f, 0.86f, 0.38f, 1f)
        sr.circle(x - 2f, y + 2f, 10f)
    }

    private fun drawWindmill(sr: ShapeRenderer, x: Float, y: Float) {
        sr.color = Color(0.72f, 0.70f, 0.65f, 1f)
        sr.triangle(x - 10f, y, x + 10f, y, x, y + 36f)
        sr.color = Color(0.45f, 0.30f, 0.15f, 1f)
        sr.circle(x, y + 26f, 3.5f)
        sr.rectLine(x - 16f, y + 26f, x + 16f, y + 26f, 2f)
        sr.rectLine(x, y + 10f, x, y + 42f, 2f)
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
