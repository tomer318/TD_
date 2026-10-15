package com.game.td.renderers.maps

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2

object Level4MapRenderer {
    fun render(shapeRenderer: ShapeRenderer) {
        val riverPoints = listOf(
            Vector2(1280f, 665f), Vector2(1150f, 645f), Vector2(1000f, 590f),
            Vector2(850f, 545f),  Vector2(740f, 470f),  Vector2(640f, 420f),
            Vector2(530f, 340f),  Vector2(430f, 245f),  Vector2(320f, 120f),
            Vector2(250f, 10f)
        )

        shapeRenderer.color = Color(0.40f, 0.55f, 0.30f, 1f)
        for (i in 0 until riverPoints.size - 1) shapeRenderer.rectLine(riverPoints[i], riverPoints[i + 1], 110f)
        for (p in riverPoints) shapeRenderer.circle(p.x, p.y, 55f)

        shapeRenderer.color = Color(0.24f, 0.52f, 0.80f, 1f)
        for (i in 0 until riverPoints.size - 1) shapeRenderer.rectLine(riverPoints[i], riverPoints[i + 1], 85f)
        for (p in riverPoints) shapeRenderer.circle(p.x, p.y, 42.5f)

        // Rừng Tây Bắc
        val nwForest = listOf(
            Vector2(10f, 700f), Vector2(60f, 690f), Vector2(110f, 680f), Vector2(160f, 670f), Vector2(210f, 660f), Vector2(260f, 640f), Vector2(300f, 610f),
            Vector2(10f, 620f), Vector2(60f, 610f), Vector2(110f, 590f), Vector2(160f, 570f), Vector2(210f, 550f), Vector2(250f, 520f),
            Vector2(10f, 530f), Vector2(50f, 520f), Vector2(100f, 490f), Vector2(150f, 460f), Vector2(200f, 420f),
            Vector2(10f, 440f), Vector2(50f, 420f), Vector2(100f, 390f), Vector2(150f, 350f),
            Vector2(10f, 350f), Vector2(50f, 320f), Vector2(95f,  290f), Vector2(140f, 250f)
        )
        shapeRenderer.color = Color(0.10f, 0.22f, 0.06f, 1f)
        for (p in nwForest) shapeRenderer.circle(p.x, p.y - 4f, 44f)
        shapeRenderer.color = Color(0.14f, 0.32f, 0.09f, 1f)
        for (p in nwForest) shapeRenderer.circle(p.x, p.y, 40f)
        shapeRenderer.color = Color(0.20f, 0.44f, 0.14f, 1f)
        for (p in nwForest) shapeRenderer.circle(p.x - 3f, p.y + 4f, 28f)

        // Rừng Đông & Đông Nam
        val eastForest = listOf(
            Vector2(1040f, 700f), Vector2(1110f, 690f), Vector2(1180f, 680f), Vector2(1250f, 670f),
            Vector2(1060f, 620f), Vector2(1130f, 610f), Vector2(1200f, 600f), Vector2(1270f, 590f),
            Vector2(1050f, 470f), Vector2(1120f, 460f), Vector2(1190f, 450f), Vector2(1260f, 440f),
            Vector2(1040f, 390f), Vector2(1110f, 380f), Vector2(1180f, 370f), Vector2(1250f, 360f),
            Vector2(1020f, 300f), Vector2(1090f, 290f), Vector2(1160f, 280f), Vector2(1230f, 270f),
            Vector2(990f,  200f), Vector2(1060f, 190f), Vector2(1130f, 180f), Vector2(1200f, 170f),
            Vector2(970f,  100f), Vector2(1040f,  90f), Vector2(1110f,  80f), Vector2(1180f,  70f)
        )
        shapeRenderer.color = Color(0.10f, 0.22f, 0.06f, 1f)
        for (p in eastForest) shapeRenderer.circle(p.x, p.y - 4f, 44f)
        shapeRenderer.color = Color(0.14f, 0.32f, 0.09f, 1f)
        for (p in eastForest) shapeRenderer.circle(p.x, p.y, 40f)
        shapeRenderer.color = Color(0.20f, 0.44f, 0.14f, 1f)
        for (p in eastForest) shapeRenderer.circle(p.x - 3f, p.y + 4f, 28f)

        // Nhà gỗ trên đảo
        shapeRenderer.color = Color(0.52f, 0.35f, 0.18f, 1f)
        shapeRenderer.rect(515f, 445f, 46f, 34f)
        shapeRenderer.color = Color(0.75f, 0.55f, 0.25f, 1f)
        shapeRenderer.triangle(505f, 479f, 571f, 479f, 538f, 507f)

        // 2 cây cầu gỗ
        shapeRenderer.color = Color(0.42f, 0.26f, 0.12f, 1f)
        shapeRenderer.rect(750f, 575f, 75f, 46f)
        shapeRenderer.rect(405f, 210f, 75f, 46f)
        shapeRenderer.color = Color(0.65f, 0.45f, 0.22f, 1f)
        for (i in 0 until 5) {
            shapeRenderer.rect(754f + i * 14f, 578f, 9f, 40f)
            shapeRenderer.rect(409f + i * 14f, 213f, 9f, 40f)
        }
    }
}
