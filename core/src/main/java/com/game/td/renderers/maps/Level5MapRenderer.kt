package com.game.td.renderers.maps

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2

object Level5MapRenderer {
    fun render(shapeRenderer: ShapeRenderer) {
        // Con suối và hồ nước góc Tây Bắc
        val streamPoints = listOf(
            Vector2(160f, 690f), Vector2(230f, 680f), Vector2(320f, 650f),
            Vector2(380f, 620f), Vector2(430f, 630f)
        )
        shapeRenderer.color = Color(0.24f, 0.55f, 0.82f, 1f)
        for (i in 0 until streamPoints.size - 1) {
            shapeRenderer.rectLine(streamPoints[i], streamPoints[i + 1], 18f)
        }
        shapeRenderer.circle(440f, 630f, 26f)

        // Rừng rậm bạt ngàn bờ Tây
        val westForest = listOf(
            Vector2(30f, 680f), Vector2(90f, 670f), Vector2(150f, 650f), Vector2(210f, 620f),
            Vector2(30f, 590f), Vector2(90f, 570f), Vector2(150f, 540f), Vector2(210f, 500f), Vector2(270f, 460f),
            Vector2(30f, 490f), Vector2(90f, 470f), Vector2(150f, 430f), Vector2(210f, 390f), Vector2(270f, 340f),
            Vector2(30f, 390f), Vector2(80f, 370f), Vector2(140f, 330f), Vector2(200f, 280f), Vector2(260f, 230f), Vector2(320f, 170f),
            Vector2(30f, 280f), Vector2(80f, 260f), Vector2(140f, 220f), Vector2(200f, 170f), Vector2(260f, 110f),
            Vector2(30f, 170f), Vector2(80f, 140f), Vector2(140f, 100f), Vector2(200f, 50f),
            Vector2(30f, 60f),  Vector2(80f, 30f)
        )
        shapeRenderer.color = Color(0.08f, 0.20f, 0.06f, 1f)
        for (p in westForest) shapeRenderer.circle(p.x, p.y - 4f, 46f)
        shapeRenderer.color = Color(0.12f, 0.30f, 0.08f, 1f)
        for (p in westForest) shapeRenderer.circle(p.x, p.y, 42f)
        shapeRenderer.color = Color(0.18f, 0.42f, 0.12f, 1f)
        for (p in westForest) shapeRenderer.circle(p.x - 3f, p.y + 4f, 30f)

        // Rừng rậm Đông & Đông Nam
        val eastForest = listOf(
            Vector2(880f, 700f), Vector2(960f, 690f), Vector2(1040f, 670f), Vector2(1120f, 660f), Vector2(1200f, 650f),
            Vector2(940f, 610f), Vector2(1020f, 590f), Vector2(1100f, 580f), Vector2(1180f, 570f), Vector2(1250f, 560f),
            Vector2(980f, 500f), Vector2(1060f, 490f), Vector2(1140f, 480f), Vector2(1220f, 470f),
            Vector2(990f, 390f), Vector2(1070f, 380f), Vector2(1150f, 370f), Vector2(1230f, 360f),
            Vector2(950f, 160f), Vector2(1030f, 150f), Vector2(1110f, 140f), Vector2(1190f, 130f),
            Vector2(920f, 70f),  Vector2(1000f, 60f),  Vector2(1080f, 50f),  Vector2(1160f, 40f),
            Vector2(650f, 30f),  Vector2(600f, 10f),   Vector2(550f, 0f)
        )
        shapeRenderer.color = Color(0.08f, 0.20f, 0.06f, 1f)
        for (p in eastForest) shapeRenderer.circle(p.x, p.y - 4f, 46f)
        shapeRenderer.color = Color(0.12f, 0.30f, 0.08f, 1f)
        for (p in eastForest) shapeRenderer.circle(p.x, p.y, 42f)
        shapeRenderer.color = Color(0.18f, 0.42f, 0.12f, 1f)
        for (p in eastForest) shapeRenderer.circle(p.x - 3f, p.y + 4f, 30f)

        // Cửa hang nhện bờ Đông
        shapeRenderer.color = Color(0.20f, 0.18f, 0.16f, 1f)
        shapeRenderer.circle(1150f, 250f, 38f)
        shapeRenderer.color = Color(0.06f, 0.06f, 0.06f, 1f)
        shapeRenderer.circle(1150f, 250f, 26f)
        shapeRenderer.color = Color(0.9f, 0.9f, 0.95f, 0.6f)
        shapeRenderer.line(1125f, 230f, 1175f, 270f)
        shapeRenderer.line(1125f, 270f, 1175f, 230f)
        shapeRenderer.line(1150f, 224f, 1150f, 276f)
    }
}
