package com.game.td.renderers.maps

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.game.td.map.Level1Southport
import com.game.td.renderers.MapRenderer

object Level1MapRenderer {
    fun render(shapeRenderer: ShapeRenderer) {
        // 1. VẼ ĐƯỜNG ĐI TRƯỚC ĐỂ CÁC CHI TIẾT & BIỂU TƯỢNG PHÒNG THỦ NỔI LÊN TRÊN
        MapRenderer.drawStandardDirtPaths(shapeRenderer, Level1Southport.data.allPaths)

        // 2. VÒNG TRÒN PHÒNG THỦ DƯỚI ĐẤT & 2 CỜ HIỆU LINIREA TẠI CỔNG ĐÔNG (PHẢI)
        MapRenderer.drawDefensePoint(
            sr = shapeRenderer,
            crestX = 1220f, crestY = 255f,
            flag1X = 1220f, flag1Y = 296f,
            flag2X = 1220f, flag2Y = 202f
        )

        // A. CỤM RỪNG LỚN PHÍA TÂY
        val westForest = listOf(
            Vector2(20f, 700f), Vector2(80f, 690f), Vector2(140f, 680f), Vector2(200f, 670f), Vector2(260f, 650f), Vector2(320f, 620f),
            Vector2(20f, 620f), Vector2(80f, 610f), Vector2(140f, 590f), Vector2(200f, 570f), Vector2(250f, 540f),
            Vector2(20f, 530f), Vector2(70f, 520f), Vector2(130f, 490f), Vector2(180f, 460f), Vector2(220f, 420f),
            Vector2(20f, 440f), Vector2(70f, 420f), Vector2(120f, 390f), Vector2(170f, 360f),
            Vector2(20f, 350f), Vector2(70f, 330f), Vector2(120f, 300f), Vector2(170f, 260f), Vector2(210f, 210f),
            Vector2(20f, 250f), Vector2(60f, 230f), Vector2(110f, 200f), Vector2(160f, 160f), Vector2(220f, 120f),
            Vector2(20f, 150f), Vector2(60f, 130f), Vector2(110f, 100f), Vector2(170f, 70f),
            Vector2(20f, 50f),  Vector2(70f, 40f),  Vector2(130f, 30f)
        )

        shapeRenderer.color = Color(0.10f, 0.22f, 0.06f, 1f)
        for (p in westForest) shapeRenderer.circle(p.x, p.y - 4f, 44f)
        shapeRenderer.color = Color(0.14f, 0.32f, 0.09f, 1f)
        for (p in westForest) shapeRenderer.circle(p.x, p.y, 40f)
        shapeRenderer.color = Color(0.20f, 0.44f, 0.14f, 1f)
        for (p in westForest) shapeRenderer.circle(p.x - 3f, p.y + 4f, 28f)

        // B. CỤM RỪNG ĐÔNG BẮC & ĐÔNG NAM
        val eastForest = listOf(
            Vector2(650f, 700f), Vector2(720f, 690f), Vector2(800f, 670f), Vector2(890f, 650f), Vector2(980f, 630f), Vector2(1070f, 620f), Vector2(1160f, 610f), Vector2(1250f, 610f),
            Vector2(750f, 610f), Vector2(830f, 580f), Vector2(920f, 560f), Vector2(1010f, 550f), Vector2(1100f, 540f), Vector2(1200f, 540f),
            Vector2(950f, 490f), Vector2(1040f, 480f), Vector2(1140f, 470f), Vector2(1240f, 470f),
            Vector2(900f, 140f), Vector2(980f, 150f), Vector2(1070f, 160f), Vector2(1160f, 160f), Vector2(1250f, 160f),
            Vector2(850f, 70f),  Vector2(930f, 80f),  Vector2(1020f, 90f),  Vector2(1110f, 90f),  Vector2(1200f, 90f),
            Vector2(850f, 0f),   Vector2(930f, 10f),  Vector2(1010f, 15f),  Vector2(1100f, 15f),  Vector2(1190f, 15f)
        )

        shapeRenderer.color = Color(0.10f, 0.22f, 0.06f, 1f)
        for (p in eastForest) shapeRenderer.circle(p.x, p.y - 4f, 44f)
        shapeRenderer.color = Color(0.14f, 0.32f, 0.09f, 1f)
        for (p in eastForest) shapeRenderer.circle(p.x, p.y, 40f)
        shapeRenderer.color = Color(0.20f, 0.44f, 0.14f, 1f)
        for (p in eastForest) shapeRenderer.circle(p.x - 3f, p.y + 4f, 28f)

        // C. CÁC CÂY ĐƠN LẺ Ở BÃI CỎ
        val singleTrees = listOf(
            Vector2(420f, 650f), Vector2(660f, 520f), Vector2(610f, 440f)
        )
        for (p in singleTrees) {
            shapeRenderer.color = Color(0.12f, 0.26f, 0.08f, 1f)
            shapeRenderer.circle(p.x, p.y - 3f, 32f)
            shapeRenderer.color = Color(0.18f, 0.40f, 0.12f, 1f)
            shapeRenderer.circle(p.x, p.y, 28f)
            shapeRenderer.color = Color(0.26f, 0.52f, 0.18f, 1f)
            shapeRenderer.circle(p.x - 3f, p.y + 3f, 20f)
        }

        // D. NGÔI NHÀ NÔNG DÂN BỜ NAM
        shapeRenderer.color = Color(0.55f, 0.38f, 0.20f, 1f)
        shapeRenderer.rect(480f, 75f, 42f, 32f)
        shapeRenderer.color = Color(0.85f, 0.72f, 0.30f, 1f)
        shapeRenderer.triangle(470f, 107f, 532f, 107f, 501f, 132f)
    }
}
