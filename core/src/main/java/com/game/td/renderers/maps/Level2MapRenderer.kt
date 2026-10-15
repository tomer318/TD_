package com.game.td.renderers.maps

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2

object Level2MapRenderer {
    fun render(shapeRenderer: ShapeRenderer) {
        // Ruộng lúa vàng
        shapeRenderer.color = Color(0.68f, 0.58f, 0.32f, 1f)
        shapeRenderer.rect(30f, 25f, 185f, 130f)

        shapeRenderer.color = Color(0.85f, 0.75f, 0.25f, 1f)
        for (r in 0 until 4) {
            for (c in 0 until 3) {
                shapeRenderer.rect(40f + c * 58f, 32f + r * 30f, 46f, 18f)
            }
        }

        // Nhà nông trại
        shapeRenderer.color = Color(0.45f, 0.30f, 0.15f, 1f)
        shapeRenderer.rect(80f, 165f, 44f, 34f)
        shapeRenderer.color = Color(0.85f, 0.72f, 0.30f, 1f)
        shapeRenderer.triangle(70f, 199f, 134f, 199f, 102f, 229f)

        // Rừng cây trang trí The Farmlands
        val trees = listOf(
            Vector2(550f, 715f), Vector2(640f, 715f), Vector2(730f, 700f), Vector2(820f, 680f),
            Vector2(920f, 660f), Vector2(1020f, 640f), Vector2(1120f, 630f), Vector2(1220f, 620f),
            Vector2(980f, 330f), Vector2(880f, 310f), Vector2(780f, 300f), Vector2(860f, 230f),
            Vector2(220f, 530f), Vector2(150f, 460f), Vector2(90f, 390f),
            Vector2(780f, 120f), Vector2(850f, 180f), Vector2(920f, 100f)
        )
        for (tr in trees) {
            shapeRenderer.color = Color(0.18f, 0.38f, 0.12f, 1f)
            shapeRenderer.circle(tr.x, tr.y, 32f)
            shapeRenderer.color = Color(0.25f, 0.50f, 0.18f, 1f)
            shapeRenderer.circle(tr.x - 4f, tr.y + 4f, 24f)
        }
    }
}
