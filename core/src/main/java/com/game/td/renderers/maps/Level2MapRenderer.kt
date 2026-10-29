package com.game.td.renderers.maps

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.game.td.map.Level2Farmlands
import com.game.td.renderers.MapRenderer

object Level2MapRenderer {

    fun render(shapeRenderer: ShapeRenderer) {
        // 1. CỤM RỪNG RẬM BAO QUANH
        drawDenseForest(
            shapeRenderer,
            listOf(
                Vector2(30f, 700f), Vector2(100f, 690f), Vector2(180f, 680f), Vector2(260f, 670f), Vector2(340f, 660f),
                Vector2(420f, 660f), Vector2(480f, 680f),
                Vector2(30f, 620f), Vector2(110f, 610f), Vector2(190f, 590f), Vector2(270f, 580f), Vector2(350f, 570f),
                Vector2(30f, 530f), Vector2(90f, 520f), Vector2(160f, 490f), Vector2(220f, 460f),
                Vector2(20f, 440f), Vector2(70f, 420f), Vector2(120f, 380f)
            )
        )

        drawDenseForest(
            shapeRenderer,
            listOf(
                Vector2(950f, 700f), Vector2(1040f, 690f), Vector2(1130f, 680f), Vector2(1220f, 680f),
                Vector2(880f, 640f), Vector2(960f, 620f), Vector2(1050f, 600f), Vector2(1140f, 590f), Vector2(1230f, 590f),
                Vector2(920f, 540f), Vector2(1000f, 530f), Vector2(1080f, 520f), Vector2(1170f, 510f), Vector2(1250f, 510f)
            )
        )

        drawDenseForest(
            shapeRenderer,
            listOf(
                Vector2(980f, 400f), Vector2(1070f, 410f), Vector2(1160f, 420f), Vector2(1250f, 420f),
                Vector2(930f, 340f), Vector2(1020f, 330f), Vector2(1110f, 330f), Vector2(1200f, 330f),
                Vector2(890f, 260f), Vector2(970f, 250f), Vector2(1060f, 240f), Vector2(1150f, 240f), Vector2(1240f, 240f),
                Vector2(850f, 180f), Vector2(940f, 170f), Vector2(1030f, 160f), Vector2(1120f, 150f), Vector2(1210f, 140f),
                Vector2(820f, 90f),  Vector2(900f, 80f),  Vector2(990f, 70f),   Vector2(1080f, 60f),  Vector2(1170f, 50f),
                Vector2(840f, 10f),  Vector2(920f, 0f),   Vector2(1010f, -10f), Vector2(1100f, -10f)
            )
        )

        // 2. KHU ĐỒNG RUỘNG & NÔNG TRẠI TÂY NAM
        val farmOriginX = 45f
        val farmOriginY = 90f

        shapeRenderer.color = Color(0.48f, 0.36f, 0.22f, 1f)
        shapeRenderer.rect(farmOriginX - 10f, farmOriginY - 10f, 280f, 190f)

        drawFieldPlot(shapeRenderer, farmOriginX, farmOriginY + 95f, 125f, 80f, isWheat = true)
        drawFieldPlot(shapeRenderer, farmOriginX + 135f, farmOriginY + 95f, 125f, 80f, isWheat = false)
        drawFieldPlot(shapeRenderer, farmOriginX, farmOriginY, 125f, 80f, isWheat = false)
        drawFieldPlot(shapeRenderer, farmOriginX + 135f, farmOriginY, 125f, 80f, isWheat = true)

        drawFarmHouse(shapeRenderer, farmOriginX + 140f, farmOriginY + 195f)
        drawFarmHouse(shapeRenderer, farmOriginX + 205f, farmOriginY + 205f)

        drawHaystack(shapeRenderer, farmOriginX + 310f, farmOriginY + 20f)
        drawHaystack(shapeRenderer, farmOriginX + 345f, farmOriginY + 55f)
        drawHaystack(shapeRenderer, 860f, 210f)

        drawWindmill(shapeRenderer, 840f, 320f)
        drawWindmill(shapeRenderer, 880f, 290f)

        // 3. CÂY ĐỘC LẬP TRÊN DẢI CỎ GIỮA
        drawSingleTree(shapeRenderer, 810f, 410f)
        drawSingleTree(shapeRenderer, 220f, 410f)
        drawSingleTree(shapeRenderer, 1020f, 190f)

        // 4. VẼ ĐƯỜNG ĐI TRƯỚC, SAU ĐÓ VẼ BIỂU TƯỢNG PHÒNG THỦ & CỜ HIỆU ĐÈ LÊN TRÊN ĐƯỜNG
        MapRenderer.drawStandardDirtPaths(shapeRenderer, Level2Farmlands.data.allPaths)
        MapRenderer.drawDefensePointOnPath(shapeRenderer, Level2Farmlands.data.waypoints)
    }

    private fun drawFieldPlot(sr: ShapeRenderer, x: Float, y: Float, w: Float, h: Float, isWheat: Boolean) {
        if (isWheat) {
            sr.color = Color(0.78f, 0.65f, 0.25f, 1f)
            sr.rect(x, y, w, h)
            sr.color = Color(0.92f, 0.82f, 0.35f, 1f)
            for (col in 0 until 6) {
                sr.rect(x + 5f + col * 20f, y + 4f, 8f, h - 8f)
            }
        } else {
            sr.color = Color(0.38f, 0.55f, 0.22f, 1f)
            sr.rect(x, y, w, h)
            sr.color = Color(0.30f, 0.45f, 0.18f, 1f)
            for (row in 0 until 4) {
                sr.rect(x + 4f, y + 6f + row * 18f, w - 8f, 7f)
            }
        }
    }

    private fun drawFarmHouse(sr: ShapeRenderer, x: Float, y: Float) {
        sr.color = Color(0.45f, 0.32f, 0.18f, 1f)
        sr.rect(x - 18f, y - 12f, 36f, 24f)
        sr.color = Color(0.25f, 0.18f, 0.10f, 1f)
        sr.rect(x - 5f, y - 12f, 10f, 15f)
        sr.color = Color(0.85f, 0.72f, 0.28f, 1f)
        sr.triangle(x - 24f, y + 12f, x + 24f, y + 12f, x, y + 32f)
    }

    private fun drawHaystack(sr: ShapeRenderer, x: Float, y: Float) {
        sr.color = Color(0.85f, 0.75f, 0.25f, 1f)
        sr.circle(x, y, 16f)
        sr.color = Color(0.95f, 0.86f, 0.38f, 1f)
        sr.circle(x - 2f, y + 2f, 11f)
    }

    private fun drawWindmill(sr: ShapeRenderer, x: Float, y: Float) {
        sr.color = Color(0.72f, 0.70f, 0.65f, 1f)
        sr.triangle(x - 10f, y, x + 10f, y, x, y + 36f)
        sr.color = Color(0.45f, 0.30f, 0.15f, 1f)
        sr.circle(x, y + 26f, 3.5f)
        sr.rectLine(x - 16f, y + 26f, x + 16f, y + 26f, 2f)
        sr.rectLine(x, y + 10f, x, y + 42f, 2f)
    }

    private fun drawSingleTree(sr: ShapeRenderer, x: Float, y: Float) {
        sr.color = Color(0.12f, 0.28f, 0.08f, 1f)
        sr.circle(x, y, 22f)
        sr.color = Color(0.20f, 0.48f, 0.14f, 1f)
        sr.circle(x - 3f, y + 3f, 16f)
    }

    private fun drawDenseForest(sr: ShapeRenderer, trees: List<Vector2>) {
        sr.color = Color(0.10f, 0.24f, 0.06f, 1f)
        for (tr in trees) sr.circle(tr.x, tr.y - 4f, 38f)
        sr.color = Color(0.15f, 0.35f, 0.10f, 1f)
        for (tr in trees) sr.circle(tr.x, tr.y, 34f)
        sr.color = Color(0.22f, 0.50f, 0.16f, 1f)
        for (tr in trees) sr.circle(tr.x - 3f, tr.y + 4f, 24f)
    }
}
