package com.game.td.renderers

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.game.td.map.LevelData
import com.game.td.renderers.maps.*

object MapRenderer {

    fun render(shapeRenderer: ShapeRenderer, levelData: LevelData) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color(0.36f, 0.62f, 0.22f, 1f)
        shapeRenderer.rect(0f, 0f, 1280f, 720f)

        when (levelData.levelNumber) {
            1 -> Level1MapRenderer.render(shapeRenderer)
            2 -> Level2MapRenderer.render(shapeRenderer)
            3 -> Level3MapRenderer.render(shapeRenderer)
            4 -> Level4MapRenderer.render(shapeRenderer)
            5 -> Level5MapRenderer.render(shapeRenderer)
            6 -> Level6MapRenderer.render(shapeRenderer)
            7 -> Level7MapRenderer.render(shapeRenderer)
            8 -> Level8MapRenderer.render(shapeRenderer)
            9 -> Level9MapRenderer.render(shapeRenderer)
            10 -> Level10MapRenderer.render(shapeRenderer)
            11 -> Level11MapRenderer.render(shapeRenderer)
            12 -> Level12MapRenderer.render(shapeRenderer)
        }

        // BỔ SUNG MÀN 4 VÀ MÀN 5 VÀO DANH SÁCH TỰ VẼ ĐƯỜNG ĐỂ KHÔNG BỊ ĐÈ LÊN CÔNG TRÌNH & RỪNG NHỆN
        if (levelData.levelNumber !in listOf(4, 5, 6, 7, 8, 9, 10, 11, 12)) {
            val allPaths = levelData.allPaths

            shapeRenderer.color = Color(0.55f, 0.44f, 0.26f, 1f)
            for (path in allPaths) {
                for (i in 0 until path.size - 1) {
                    shapeRenderer.rectLine(path[i], path[i + 1], 80f)
                }
                for (w in path) {
                    shapeRenderer.circle(w.x, w.y, 40f)
                }
            }

            shapeRenderer.color = Color(0.82f, 0.72f, 0.46f, 1f)
            for (path in allPaths) {
                for (i in 0 until path.size - 1) {
                    shapeRenderer.rectLine(path[i], path[i + 1], 66f)
                }
                for (w in path) {
                    shapeRenderer.circle(w.x, w.y, 33f)
                }
            }
        }

        shapeRenderer.end()
    }
}
