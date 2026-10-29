package com.game.td.renderers

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.game.td.map.LevelData
import com.game.td.renderers.maps.*

object MapRenderer {

    private val OUTLINE = Color(0.12f, 0.09f, 0.07f, 1f)

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
            else -> {
                // Màn Sandbox (Level 0)
                drawStandardDirtPaths(shapeRenderer, levelData.allPaths)
                for (path in levelData.allPaths) {
                    drawDefensePointOnPath(shapeRenderer, path)
                }
            }
        }

        shapeRenderer.end()
    }

    fun drawStandardDirtPaths(sr: ShapeRenderer, allPaths: List<List<Vector2>>) {
        sr.color = Color(0.55f, 0.44f, 0.26f, 1f)
        for (path in allPaths) {
            for (i in 0 until path.size - 1) {
                sr.rectLine(path[i], path[i + 1], 80f)
            }
            for (w in path) {
                sr.circle(w.x, w.y, 40f)
            }
        }

        sr.color = Color(0.82f, 0.72f, 0.46f, 1f)
        for (path in allPaths) {
            for (i in 0 until path.size - 1) {
                sr.rectLine(path[i], path[i + 1], 66f)
            }
            for (w in path) {
                sr.circle(w.x, w.y, 33f)
            }
        }
    }

    // VẼ VÒNG TRÒN PHÒNG THỦ DƯỚI ĐẤT & 2 CỜ HIỆU LINIREA TẠI TỌA ĐỘ CỐ ĐỊNH
    fun drawDefensePoint(
        sr: ShapeRenderer,
        crestX: Float,
        crestY: Float,
        flag1X: Float,
        flag1Y: Float,
        flag2X: Float,
        flag2Y: Float
    ) {
        drawGroundCrest(sr, crestX, crestY)
        if (flag1Y >= flag2Y) {
            drawLinireaBanner(sr, flag1X, flag1Y)
            drawLinireaBanner(sr, flag2X, flag2Y)
        } else {
            drawLinireaBanner(sr, flag2X, flag2Y)
            drawLinireaBanner(sr, flag1X, flag1Y)
        }
    }

    // TỰ ĐỘNG XÁC ĐỊNH ĐOẠN CUỐI ĐƯỜNG ĐI ĐỂ ĐẶT BIỂU TƯỢNG GIỮA ĐƯỜNG & 2 CỜ HAI BÊN MÉP ĐƯỜNG
    fun drawDefensePointOnPath(sr: ShapeRenderer, path: List<Vector2>) {
        if (path.size < 2) return
        var idx = path.size - 1
        while (idx > 1 && (path[idx].x !in 48f..1232f || path[idx].y !in 45f..672f)) {
            idx--
        }
        val pOut = path[(idx + 1).coerceAtMost(path.size - 1)]
        val pIn = path[idx]
        val center = Vector2(pIn).lerp(pOut, 0.35f)
        center.x = center.x.coerceIn(55f, 1225f)
        center.y = center.y.coerceIn(46f, 668f)

        val prev = path[(idx - 2).coerceAtLeast(0)]
        val dir = Vector2(pOut).sub(prev)
        if (dir.isZero) dir.set(0f, -1f) else dir.nor()
        val normal = Vector2(-dir.y, dir.x)

        val f1 = Vector2(center).add(Vector2(normal).scl(44f))
        val f2 = Vector2(center).add(Vector2(normal).scl(-44f))
        drawDefensePoint(sr, center.x, center.y, f1.x, f1.y - 6f, f2.x, f2.y - 6f)
    }

    // VÒNG TRÒN BIỂU TƯỢNG PHÒNG THỦ DƯỚI MẶT ĐẤT (GIỐNG ẢNH GỐC KINGDOM RUSH)
    fun drawGroundCrest(sr: ShapeRenderer, cx: Float, cy: Float) {
        // Vành tròn xanh lam đậm ngoài cùng
        sr.color = Color(0.14f, 0.42f, 0.76f, 0.75f)
        sr.ellipse(cx - 28f, cy - 19f, 56f, 38f)

        // Vành tròn xanh thiên thanh sáng rực
        sr.color = Color(0.38f, 0.80f, 1f, 0.90f)
        sr.ellipse(cx - 22f, cy - 15f, 44f, 30f)

        // Lõi tròn bên trong và hình khiên bảo vệ ở tâm
        sr.color = Color(0.18f, 0.48f, 0.82f, 0.45f)
        sr.ellipse(cx - 14f, cy - 9.5f, 28f, 19f)

        sr.color = Color(0.65f, 0.90f, 1f, 0.85f)
        sr.rect(cx - 7f, cy - 1f, 14f, 8f)
        sr.triangle(cx - 7f, cy - 1f, cx + 7f, cy - 1f, cx, cy - 8f)
    }

    // CỜ HIỆU HIỆP SĨ LINIREA CẮM HAI BÊN ĐƯỜNG
    fun drawLinireaBanner(sr: ShapeRenderer, x: Float, y: Float) {
        sr.color = Color(0f, 0f, 0f, 0.28f)
        sr.ellipse(x - 10f, y - 5f, 20f, 8f)

        // Cán cờ gỗ & Thanh treo ngang
        sr.color = OUTLINE
        sr.rect(x - 2.2f, y - 2f, 4.4f, 40f)
        sr.circle(x, y + 39f, 3.2f)
        sr.rect(x - 12f, y + 31f, 24f, 4f)
        sr.color = Color(0.55f, 0.36f, 0.18f, 1f)
        sr.rect(x - 1.2f, y, 2.4f, 37f)
        sr.color = Color(0.95f, 0.78f, 0.20f, 1f)
        sr.circle(x, y + 39f, 2f)

        // Thân lá cờ xanh dương
        sr.color = OUTLINE
        sr.rect(x - 10f, y + 15f, 20f, 17f)
        sr.triangle(x - 10f, y + 15f, x + 10f, y + 15f, x, y + 6f)
        sr.color = Color(0.18f, 0.52f, 0.88f, 1f)
        sr.rect(x - 8.5f, y + 16f, 17f, 15f)
        sr.triangle(x - 8.5f, y + 16f, x + 8.5f, y + 16f, x, y + 8f)

        // Huy hiệu khiên trắng sáng giữa cờ
        sr.color = Color(0.85f, 0.95f, 1f, 1f)
        sr.rect(x - 4.5f, y + 19f, 9f, 8f)
        sr.triangle(x - 4.5f, y + 19f, x + 4.5f, y + 19f, x, y + 14f)
        sr.color = Color(0.18f, 0.52f, 0.88f, 1f)
        sr.circle(x, y + 23f, 2f)
    }
}
