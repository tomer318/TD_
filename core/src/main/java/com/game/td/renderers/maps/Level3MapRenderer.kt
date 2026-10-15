package com.game.td.renderers.maps

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2

object Level3MapRenderer {
    fun render(shapeRenderer: ShapeRenderer) {
        // A. CỤM RỪNG TÂY BẮC
        val nwForest = listOf(
            Vector2(20f, 700f), Vector2(85f, 690f), Vector2(150f, 680f), Vector2(215f, 675f), Vector2(280f, 670f),
            Vector2(20f, 630f), Vector2(85f, 620f), Vector2(150f, 610f), Vector2(215f, 600f), Vector2(275f, 580f),
            Vector2(20f, 560f), Vector2(80f, 550f), Vector2(145f, 540f), Vector2(205f, 520f), Vector2(260f, 490f),
            Vector2(15f, 480f), Vector2(75f, 470f), Vector2(135f, 450f), Vector2(195f, 430f), Vector2(245f, 400f),
            Vector2(15f, 400f), Vector2(70f, 390f), Vector2(125f, 370f), Vector2(180f, 350f),
            Vector2(15f, 320f), Vector2(65f, 310f), Vector2(115f, 290f), Vector2(165f, 270f),
            Vector2(15f, 250f), Vector2(60f, 240f)
        )

        shapeRenderer.color = Color(0.10f, 0.22f, 0.06f, 1f)
        for (p in nwForest) shapeRenderer.circle(p.x, p.y - 4f, 42f)
        shapeRenderer.color = Color(0.14f, 0.32f, 0.09f, 1f)
        for (p in nwForest) shapeRenderer.circle(p.x, p.y, 38f)
        shapeRenderer.color = Color(0.20f, 0.44f, 0.14f, 1f)
        for (p in nwForest) shapeRenderer.circle(p.x - 3f, p.y + 4f, 27f)

        // B. CỤM RỪNG ĐÔNG NAM
        val seForest = listOf(
            Vector2(930f, 210f), Vector2(1010f, 220f), Vector2(1090f, 225f), Vector2(1170f, 225f), Vector2(1250f, 225f),
            Vector2(890f, 140f), Vector2(960f, 150f), Vector2(1040f, 160f), Vector2(1120f, 160f), Vector2(1200f, 160f),
            Vector2(880f, 70f),  Vector2(950f, 80f),  Vector2(1020f, 90f),  Vector2(1100f, 90f),  Vector2(1180f, 90f),
            Vector2(890f, 0f),   Vector2(960f, 10f),  Vector2(1030f, 15f),  Vector2(1100f, 15f),  Vector2(1180f, 15f)
        )

        shapeRenderer.color = Color(0.10f, 0.22f, 0.06f, 1f)
        for (p in seForest) shapeRenderer.circle(p.x, p.y - 4f, 42f)
        shapeRenderer.color = Color(0.14f, 0.32f, 0.09f, 1f)
        for (p in seForest) shapeRenderer.circle(p.x, p.y, 38f)
        shapeRenderer.color = Color(0.20f, 0.44f, 0.14f, 1f)
        for (p in seForest) shapeRenderer.circle(p.x - 3f, p.y + 4f, 27f)

        // C. BỜ CÁT VÀ HỒ NƯỚC TÂY NAM
        shapeRenderer.color = Color(0.85f, 0.76f, 0.48f, 1f)
        shapeRenderer.circle(270f, -5f, 140f)
        shapeRenderer.circle(150f, 5f, 115f)

        shapeRenderer.color = Color(0.24f, 0.52f, 0.80f, 1f)
        shapeRenderer.circle(270f, -5f, 120f)
        shapeRenderer.circle(150f, 5f, 98f)

        // Cầu cảng và túp lều ngư dân
        shapeRenderer.color = Color(0.48f, 0.32f, 0.16f, 1f)
        shapeRenderer.rect(280f, 45f, 45f, 20f)
        shapeRenderer.color = Color(0.55f, 0.38f, 0.20f, 1f)
        shapeRenderer.rect(480f, 25f, 38f, 28f)
        shapeRenderer.color = Color(0.85f, 0.72f, 0.30f, 1f)
        shapeRenderer.triangle(470f, 53f, 528f, 53f, 499f, 76f)
    }
}
