package com.game.td.renderers

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.game.td.ecs.components.TowerComponent
import com.game.td.ecs.components.TowerSpecialization
import com.game.td.ecs.components.TowerType
import com.game.td.ecs.components.TransformComponent

object TowerRenderer {

    fun render(shapeRenderer: ShapeRenderer, tower: Entity) {
        val trans = tower.getComponent(TransformComponent::class.java) ?: return
        val tComp = tower.getComponent(TowerComponent::class.java) ?: return
        val p = trans.position

        // =====================================================================
        // 1. NHÀ ĐẶC BIỆT SYLVAN ELF HALL (MÀN 5)
        // =====================================================================
        if (tComp.specialization == TowerSpecialization.SYLVAN_ELF_HALL) {
            if (tComp.isRuined) {
                shapeRenderer.color = Color(0.35f, 0.35f, 0.35f, 1f)
                shapeRenderer.circle(p.x, p.y, 25f)
                shapeRenderer.color = Color(0.40f, 0.30f, 0.20f, 1f)
                shapeRenderer.rect(p.x - 18f, p.y - 15f, 36f, 26f)
                shapeRenderer.color = Color(0.28f, 0.20f, 0.32f, 1f)
                shapeRenderer.triangle(p.x - 22f, p.y + 8f, p.x + 12f, p.y + 8f, p.x - 5f, p.y + 24f)
                shapeRenderer.color = Color(0.85f, 0.2f, 0.2f, 0.85f)
                shapeRenderer.rectLine(p.x - 12f, p.y - 10f, p.x + 12f, p.y + 14f, 3.5f)
                shapeRenderer.rectLine(p.x - 12f, p.y + 14f, p.x + 12f, p.y - 10f, 3.5f)
            } else {
                shapeRenderer.color = Color(0.38f, 0.40f, 0.42f, 1f)
                shapeRenderer.circle(p.x, p.y, 28f)
                shapeRenderer.color = Color(0.48f, 0.35f, 0.22f, 1f)
                shapeRenderer.rect(p.x - 20f, p.y - 18f, 40f, 34f)
                shapeRenderer.color = Color(0.48f, 0.25f, 0.60f, 1f)
                shapeRenderer.triangle(p.x - 25f, p.y + 12f, p.x + 25f, p.y + 12f, p.x, p.y + 34f)
                shapeRenderer.color = Color(0.95f, 0.85f, 0.35f, 1f)
                shapeRenderer.circle(p.x, p.y - 4f, 8f)
                shapeRenderer.color = Color(0.25f, 0.18f, 0.10f, 1f)
                shapeRenderer.circle(p.x, p.y - 5f, 6.5f)
            }
            return
        }

        // =====================================================================
        // 2. THÁP ĐẶC BIỆT SUNRAY TOWER (MÀN 9)
        // =====================================================================
        if (tComp.specialization == TowerSpecialization.SUNRAY_TOWER) {
            val isReady = tComp.isSunrayReady && tComp.sunraySorcerers > 0

            if (tComp.sunraySorcerers > 0) {
                shapeRenderer.color = Color(1f, 0.90f, 0.28f, if (isReady) 0.35f else 0.18f)
                shapeRenderer.circle(p.x, p.y, 46f)
            }

            shapeRenderer.color = Color(0.42f, 0.40f, 0.32f, 1f)
            shapeRenderer.circle(p.x, p.y, 42f)
            shapeRenderer.color = Color(0.58f, 0.55f, 0.43f, 1f)
            shapeRenderer.circle(p.x, p.y, 36f)
            shapeRenderer.color = Color(0.48f, 0.45f, 0.36f, 1f)
            shapeRenderer.circle(p.x, p.y, 26f)

            shapeRenderer.color = Color(0.64f, 0.60f, 0.48f, 1f)
            shapeRenderer.rect(p.x - 42f, p.y - 6f, 12f, 10f)
            shapeRenderer.rect(p.x + 30f, p.y - 6f, 12f, 10f)
            shapeRenderer.rect(p.x - 6f, p.y - 42f, 12f, 14f)

            val pedestalOffsets = listOf(
                Vector2(-22f, 9f), Vector2(22f, 9f),
                Vector2(-20f, -16f), Vector2(20f, -16f)
            )
            for (pod in pedestalOffsets) {
                shapeRenderer.color = Color(0.36f, 0.34f, 0.28f, 1f)
                shapeRenderer.circle(p.x + pod.x, p.y + pod.y - 1.5f, 8.5f)
                shapeRenderer.color = Color(0.66f, 0.63f, 0.52f, 1f)
                shapeRenderer.circle(p.x + pod.x, p.y + pod.y, 7.2f)
            }

            shapeRenderer.color = Color(0.38f, 0.36f, 0.30f, 1f)
            shapeRenderer.triangle(p.x - 16f, p.y - 14f, p.x + 16f, p.y - 14f, p.x, p.y + 42f)
            shapeRenderer.rect(p.x - 12f, p.y - 14f, 24f, 28f)
            shapeRenderer.color = Color(0.56f, 0.53f, 0.42f, 1f)
            shapeRenderer.rect(p.x - 9f, p.y - 12f, 18f, 38f)

            if (tComp.sunraySorcerers > 0) {
                shapeRenderer.color = if (isReady) Color(1f, 0.85f, 0.15f, 1f) else Color(0.95f, 0.55f, 0.10f, 0.9f)
                shapeRenderer.rect(p.x - 3f, p.y - 8f, 6f, 10f)
                shapeRenderer.rect(p.x - 2.5f, p.y + 10f, 5f, 9f)
                for (ry in listOf(-8f, -3f, 2f, 10f, 15f, 20f)) {
                    shapeRenderer.circle(p.x - 6.5f, p.y + ry, 1.3f)
                    shapeRenderer.circle(p.x + 6.5f, p.y + ry, 1.3f)
                }
            }

            val crystalColor = when {
                tComp.sunraySorcerers == 0 -> Color(0.72f, 0.72f, 0.75f, 1f)
                !isReady -> Color(0.95f, 0.55f, 0.15f, 1f)
                else -> Color(1f, 0.96f, 0.28f, 1f)
            }
            if (isReady) {
                shapeRenderer.color = Color(1f, 0.88f, 0.20f, 0.38f)
                shapeRenderer.circle(p.x, p.y + 38f, 18f)
            }
            shapeRenderer.color = crystalColor
            shapeRenderer.triangle(p.x - 9f, p.y + 32f, p.x + 9f, p.y + 32f, p.x, p.y + 48f)
            shapeRenderer.triangle(p.x - 9f, p.y + 32f, p.x + 9f, p.y + 32f, p.x, p.y + 23f)

            for (idx in 0 until tComp.sunraySorcerers.coerceAtMost(4)) {
                val off = pedestalOffsets[idx]
                val sx = p.x + off.x
                val sy = p.y + off.y + 2f

                shapeRenderer.color = Color(0.68f, 0.28f, 0.08f, 1f)
                shapeRenderer.triangle(sx - 6f, sy - 3f, sx + 6f, sy - 3f, sx, sy + 9f)
                shapeRenderer.rect(sx - 4.5f, sy - 3f, 9f, 7f)

                shapeRenderer.color = Color(0.88f, 0.42f, 0.12f, 1f)
                shapeRenderer.rect(sx - 3f, sy - 2f, 6f, 6f)

                shapeRenderer.color = Color(0.62f, 0.24f, 0.06f, 1f)
                shapeRenderer.circle(sx, sy + 7.5f, 4.8f)
                shapeRenderer.color = Color(0.96f, 0.82f, 0.55f, 1f)
                shapeRenderer.circle(sx, sy + 7.2f, 2.3f)
            }
            return
        }

        // =====================================================================
        // 3. CÁC LOẠI THÁP CHIẾN ĐẤU THƯỜNG (CẤP 1 -> CẤP 4 CHUẨN HÌNH MẪU)
        // =====================================================================
        when (tComp.towerType) {

            // -----------------------------------------------------------------
            // A. NHÁNH THÁP CUNG (ARCHER)
            // -----------------------------------------------------------------
            TowerType.ARCHER -> {
                when (tComp.specialization) {
                    // Lv4 Nhánh 1: RANGERS HIDEOUT (Gốc cây cổ thụ + Cầu thang gỗ + Cờ xanh + 2 Ranger)
                    TowerSpecialization.RANGERS_HIDEOUT -> {
                        shapeRenderer.color = Color(0.22f, 0.42f, 0.14f, 1f)
                        shapeRenderer.circle(p.x, p.y - 4f, 29f)

                        shapeRenderer.color = Color(0.42f, 0.27f, 0.14f, 1f)
                        shapeRenderer.triangle(p.x - 28f, p.y - 18f, p.x - 12f, p.y - 18f, p.x - 16f, p.y)
                        shapeRenderer.triangle(p.x + 28f, p.y - 18f, p.x + 12f, p.y - 18f, p.x + 16f, p.y)
                        shapeRenderer.rect(p.x - 20f, p.y - 18f, 40f, 28f)

                        shapeRenderer.color = Color(0.55f, 0.36f, 0.18f, 1f)
                        shapeRenderer.rect(p.x - 16f, p.y - 17f, 32f, 26f)
                        shapeRenderer.color = Color(0.36f, 0.22f, 0.10f, 1f)
                        shapeRenderer.rect(p.x - 12f, p.y - 14f, 2.5f, 18f)
                        shapeRenderer.rect(p.x + 9.5f, p.y - 14f, 2.5f, 18f)

                        shapeRenderer.color = Color(0.20f, 0.46f, 0.14f, 1f)
                        shapeRenderer.circle(p.x - 20f, p.y + 4f, 11f)
                        shapeRenderer.circle(p.x + 20f, p.y + 4f, 11f)
                        shapeRenderer.color = Color(0.32f, 0.62f, 0.20f, 1f)
                        shapeRenderer.circle(p.x - 18f, p.y + 6f, 8.5f)
                        shapeRenderer.circle(p.x + 18f, p.y + 6f, 8.5f)

                        shapeRenderer.color = Color(0.18f, 0.52f, 0.16f, 1f)
                        shapeRenderer.rect(p.x - 16f, p.y - 8f, 7f, 12f)
                        shapeRenderer.rect(p.x + 9f, p.y - 8f, 7f, 12f)
                        shapeRenderer.color = Color(0.95f, 0.82f, 0.22f, 1f)
                        shapeRenderer.circle(p.x - 12.5f, p.y - 2f, 2f)
                        shapeRenderer.circle(p.x + 12.5f, p.y - 2f, 2f)

                        shapeRenderer.color = Color(0.24f, 0.15f, 0.07f, 1f)
                        shapeRenderer.rect(p.x - 6f, p.y - 20f, 12f, 24f)
                        shapeRenderer.color = Color(0.65f, 0.44f, 0.22f, 1f)
                        for (stepY in listOf(-18f, -13f, -8f, -3f, 2f)) {
                            shapeRenderer.rect(p.x - 5f, p.y + stepY, 10f, 2.8f)
                        }

                        shapeRenderer.color = Color(0.38f, 0.24f, 0.12f, 1f)
                        shapeRenderer.circle(p.x, p.y + 12f, 18f)
                        shapeRenderer.color = Color(0.58f, 0.38f, 0.20f, 1f)
                        shapeRenderer.circle(p.x, p.y + 13f, 14.5f)
                        shapeRenderer.color = Color(0.30f, 0.18f, 0.08f, 1f)
                        for (rx in listOf(-13f, -7f, 0f, 7f, 13f)) {
                            shapeRenderer.rect(p.x + rx - 1.2f, p.y + 4f, 2.4f, 7f)
                        }

                        for (side in listOf(-6.5f, 6.5f)) {
                            val rx = p.x + side
                            val ry = p.y + 14f
                            shapeRenderer.color = Color(0.18f, 0.48f, 0.15f, 1f)
                            shapeRenderer.circle(rx, ry, 5f)
                            shapeRenderer.color = Color(0.95f, 0.80f, 0.58f, 1f)
                            shapeRenderer.circle(rx, ry + 3.5f, 3f)
                            shapeRenderer.color = Color(0.25f, 0.62f, 0.18f, 1f)
                            shapeRenderer.triangle(rx - 4f, ry + 3.5f, rx + 4f, ry + 3.5f, rx, ry + 8.5f)
                            shapeRenderer.color = Color(0.75f, 0.52f, 0.24f, 1f)
                            shapeRenderer.rectLine(rx + (if (side < 0) -4f else 4f), ry - 2f, rx + (if (side < 0) -4f else 4f), ry + 6f, 1.8f)
                        }
                    }

                    // Lv4 Nhánh 2: MUSKETEER GARRISON (Pháo đài đá mái xanh + Phù hiệu tròn + 2 lính súng hỏa mai)
                    TowerSpecialization.MUSKETEER_GARRISON -> {
                        shapeRenderer.color = Color(0.28f, 0.30f, 0.34f, 1f)
                        shapeRenderer.circle(p.x, p.y - 3f, 28f)
                        shapeRenderer.color = Color(0.52f, 0.56f, 0.62f, 1f)
                        shapeRenderer.circle(p.x, p.y - 1f, 25f)
                        shapeRenderer.rect(p.x - 22f, p.y - 18f, 44f, 28f)

                        shapeRenderer.color = Color(0.68f, 0.72f, 0.78f, 1f)
                        shapeRenderer.rect(p.x - 19f, p.y - 16f, 38f, 24f)
                        shapeRenderer.color = Color(0.32f, 0.35f, 0.40f, 1f)
                        for (dotX in listOf(-15f, -10f, 10f, 15f)) {
                            shapeRenderer.circle(p.x + dotX, p.y - 9f, 1.6f)
                        }

                        shapeRenderer.color = Color(0.30f, 0.32f, 0.36f, 1f)
                        shapeRenderer.rect(p.x - 7f, p.y - 18f, 14f, 15f)
                        shapeRenderer.color = Color(0.45f, 0.48f, 0.52f, 1f)
                        shapeRenderer.rect(p.x - 4.5f, p.y - 16f, 9f, 11f)

                        shapeRenderer.color = Color(0.16f, 0.38f, 0.62f, 1f)
                        shapeRenderer.triangle(p.x - 24f, p.y + 2f, p.x - 8f, p.y + 2f, p.x - 16f, p.y + 18f)
                        shapeRenderer.triangle(p.x + 24f, p.y + 2f, p.x + 8f, p.y + 2f, p.x + 16f, p.y + 18f)
                        shapeRenderer.rect(p.x - 22f, p.y + 2f, 44f, 11f)

                        shapeRenderer.color = Color(0.24f, 0.52f, 0.82f, 1f)
                        shapeRenderer.rect(p.x - 19f, p.y + 4f, 38f, 8f)

                        shapeRenderer.color = Color(0.25f, 0.28f, 0.32f, 1f)
                        shapeRenderer.circle(p.x, p.y + 3f, 7.5f)
                        shapeRenderer.color = Color(0.92f, 0.95f, 0.98f, 1f)
                        shapeRenderer.circle(p.x, p.y + 3f, 5.8f)
                        shapeRenderer.color = Color(0.42f, 0.46f, 0.52f, 1f)
                        shapeRenderer.circle(p.x, p.y + 3f, 2.8f)

                        shapeRenderer.color = Color(0.35f, 0.38f, 0.42f, 1f)
                        shapeRenderer.circle(p.x, p.y + 15f, 15f)
                        shapeRenderer.color = Color(0.55f, 0.58f, 0.64f, 1f)
                        shapeRenderer.circle(p.x, p.y + 15f, 12f)

                        for (side in listOf(-5.5f, 5.5f)) {
                            val mx = p.x + side
                            val my = p.y + 16f
                            shapeRenderer.color = Color(0.20f, 0.48f, 0.82f, 1f)
                            shapeRenderer.circle(mx, my, 4.5f)
                            shapeRenderer.color = Color(0.96f, 0.82f, 0.60f, 1f)
                            shapeRenderer.circle(mx, my + 3.5f, 2.8f)
                            shapeRenderer.color = Color(0.85f, 0.68f, 0.22f, 1f)
                            shapeRenderer.rect(mx - 5f, my + 5f, 10f, 2.2f)
                            shapeRenderer.circle(mx, my + 6.5f, 3f)
                            shapeRenderer.color = Color(0.30f, 0.32f, 0.35f, 1f)
                            shapeRenderer.rectLine(mx - 2f, my, mx + 6f, my + 5f, 2.4f)
                        }
                    }

                    // Lv1, Lv2, Lv3: ARCHER / MARKSMEN / SHARPSHOOTER TOWER (Chuẩn Hình 1)
                    else -> {
                        // Thân tháp đá hình thang + Cửa vòm gỗ + Bia bắn cung hồng tâm
                        val wallColor = when (tComp.level) {
                            1 -> Color(0.68f, 0.65f, 0.58f, 1f)
                            2 -> Color(0.62f, 0.63f, 0.65f, 1f)
                            else -> Color(0.54f, 0.56f, 0.60f, 1f)
                        }
                        shapeRenderer.color = Color(0.32f, 0.30f, 0.28f, 1f)
                        shapeRenderer.rect(p.x - 22f, p.y - 19f, 44f, 28f)
                        shapeRenderer.color = wallColor
                        shapeRenderer.rect(p.x - 19f, p.y - 17f, 38f, 25f)

                        // Gai sắt hai bên sườn ở Level 3 (Sharpshooter Tower)
                        if (tComp.level >= 3) {
                            shapeRenderer.color = Color(0.78f, 0.82f, 0.86f, 1f)
                            shapeRenderer.triangle(p.x - 20f, p.y + 6f, p.x - 20f, p.y + 12f, p.x - 27f, p.y + 9f)
                            shapeRenderer.triangle(p.x - 20f, p.y + 1f, p.x - 20f, p.y + 7f, p.x - 27f, p.y + 4f)
                            shapeRenderer.triangle(p.x + 20f, p.y + 6f, p.x + 20f, p.y + 12f, p.x + 27f, p.y + 9f)
                            shapeRenderer.triangle(p.x + 20f, p.y + 1f, p.x + 20f, p.y + 7f, p.x + 27f, p.y + 4f)
                        }

                        // Cửa vòm bằng gỗ nâu ở đáy tháp
                        shapeRenderer.color = Color(0.35f, 0.24f, 0.14f, 1f)
                        shapeRenderer.rect(p.x - 6.5f, p.y - 18f, 13f, 12f)
                        shapeRenderer.circle(p.x, p.y - 6f, 6.5f)
                        shapeRenderer.color = Color(0.58f, 0.40f, 0.22f, 1f)
                        shapeRenderer.rect(p.x - 4.5f, p.y - 18f, 9f, 11f)
                        shapeRenderer.circle(p.x, p.y - 7f, 4.5f)

                        // Bia bắn cung hồng tâm (Đỏ - Trắng - Đỏ) treo ở giữa mặt tiền
                        shapeRenderer.color = Color(0.85f, 0.25f, 0.16f, 1f)
                        shapeRenderer.circle(p.x, p.y + 3.5f, 5.2f)
                        shapeRenderer.color = Color.WHITE
                        shapeRenderer.circle(p.x, p.y + 3.5f, 3.4f)
                        shapeRenderer.color = Color(0.85f, 0.25f, 0.16f, 1f)
                        shapeRenderer.circle(p.x, p.y + 3.5f, 1.6f)

                        // Đài gỗ/đá trên đỉnh tháp
                        val topTrimColor = when (tComp.level) {
                            1 -> Color(0.55f, 0.36f, 0.18f, 1f)
                            2 -> Color(0.45f, 0.32f, 0.20f, 1f)
                            else -> Color(0.36f, 0.38f, 0.42f, 1f)
                        }
                        shapeRenderer.color = topTrimColor
                        shapeRenderer.rect(p.x - 21f, p.y + 7f, 42f, 9f)
                        shapeRenderer.color = Color(0.22f, 0.18f, 0.14f, 1f)
                        shapeRenderer.rect(p.x - 17f, p.y + 10f, 34f, 6f)

                        // 2 cung thủ đứng trên đỉnh tháp
                        val archerCloth = when (tComp.level) {
                            1 -> Color(0.65f, 0.48f, 0.25f, 1f)
                            2 -> Color(0.35f, 0.52f, 0.32f, 1f)
                            else -> Color(0.24f, 0.26f, 0.32f, 1f)
                        }
                        for (side in listOf(-6.5f, 6.5f)) {
                            val ax = p.x + side
                            val ay = p.y + 14f
                            shapeRenderer.color = archerCloth
                            shapeRenderer.circle(ax, ay, 4.2f)
                            shapeRenderer.color = Color(0.96f, 0.82f, 0.60f, 1f)
                            shapeRenderer.circle(ax, ay + 3.5f, 2.8f)
                            shapeRenderer.color = Color(0.75f, 0.52f, 0.20f, 1f)
                            shapeRenderer.rectLine(ax + (if (side < 0) -3.5f else 3.5f), ay - 1f, ax + (if (side < 0) -3.5f else 3.5f), ay + 6f, 1.6f)
                        }
                    }
                }
            }

            // -----------------------------------------------------------------
            // B. NHÁNH NHÀ LÍNH (BARRACKS)
            // -----------------------------------------------------------------
            TowerType.BARRACKS -> {
                if (tComp.level < 4) {
                    // Lv1, Lv2, Lv3: MILITIA / FOOTMEN / KNIGHTS BARRACKS (Chuẩn Hình 1)
                    // Pháo đài đá xám + Cổng tối + Khiên biểu tượng chữ U + Tường răng cưa
                    shapeRenderer.color = Color(0.32f, 0.33f, 0.35f, 1f)
                    shapeRenderer.rect(p.x - 22f, p.y - 18f, 44f, 30f)
                    shapeRenderer.color = Color(0.64f, 0.66f, 0.68f, 1f)
                    shapeRenderer.rect(p.x - 19f, p.y - 16f, 38f, 27f)

                    // Cổng ra vào tối ở chính giữa
                    shapeRenderer.color = Color(0.16f, 0.14f, 0.12f, 1f)
                    shapeRenderer.rect(p.x - 6.5f, p.y - 17f, 13f, 14f)

                    // Răng cưa trên đỉnh thành (Cấp 2 & 3 rõ nét hơn, Cấp 3 có 2 tháp canh đôi hai bên)
                    if (tComp.level >= 3) {
                        shapeRenderer.color = Color(0.55f, 0.57f, 0.60f, 1f)
                        shapeRenderer.rect(p.x - 21f, p.y + 10f, 13f, 10f)
                        shapeRenderer.rect(p.x + 8f, p.y + 10f, 13f, 10f)
                        shapeRenderer.color = Color(0.72f, 0.74f, 0.78f, 1f)
                        for (cx in listOf(-21f, -13f, 8f, 16f)) {
                            shapeRenderer.rect(p.x + cx, p.y + 18f, 4.5f, 4f)
                        }
                    } else if (tComp.level == 2) {
                        shapeRenderer.color = Color(0.72f, 0.74f, 0.78f, 1f)
                        for (cx in listOf(-19f, -10f, 5f, 14f)) {
                            shapeRenderer.rect(p.x + cx, p.y + 11f, 5f, 4.5f)
                        }
                    } else {
                        shapeRenderer.color = Color(0.52f, 0.54f, 0.56f, 1f)
                        shapeRenderer.rect(p.x - 20f, p.y + 9f, 40f, 4f)
                    }

                    // Khiên biểu tượng chữ U màu xanh lam viền trắng bạc treo trên cổng
                    val shieldY = p.y + 5f
                    shapeRenderer.color = Color(0.88f, 0.92f, 0.96f, 1f)
                    shapeRenderer.rect(p.x - 6.5f, shieldY - 3f, 13f, 9f)
                    shapeRenderer.triangle(p.x - 6.5f, shieldY - 3f, p.x + 6.5f, shieldY - 3f, p.x, shieldY - 9f)
                    shapeRenderer.color = Color(0.20f, 0.45f, 0.78f, 1f)
                    shapeRenderer.rect(p.x - 4.2f, shieldY - 2f, 8.4f, 6.5f)
                    shapeRenderer.triangle(p.x - 4.2f, shieldY - 2f, p.x + 4.2f, shieldY - 2f, p.x, shieldY - 6.5f)
                } else {
                    if (tComp.specialization == TowerSpecialization.HOLY_ORDER) {
                        // Lv4 Nhánh 1: HOLY ORDER (Lâu đài 2 tháp chóp xanh lam + Khiên chữ thập sáng + Cổng vàng)
                        shapeRenderer.color = Color(0.35f, 0.37f, 0.40f, 1f)
                        shapeRenderer.rect(p.x - 23f, p.y - 18f, 46f, 28f)
                        shapeRenderer.color = Color(0.70f, 0.72f, 0.75f, 1f)
                        shapeRenderer.rect(p.x - 20f, p.y - 16f, 40f, 25f)

                        // 2 tháp tròn hai bên hông
                        shapeRenderer.color = Color(0.62f, 0.65f, 0.70f, 1f)
                        shapeRenderer.rect(p.x - 24f, p.y - 14f, 12f, 26f)
                        shapeRenderer.rect(p.x + 12f, p.y - 14f, 12f, 26f)

                        // 2 mái chóp nón màu xanh lam đậm trên đỉnh 2 tháp
                        shapeRenderer.color = Color(0.18f, 0.38f, 0.68f, 1f)
                        shapeRenderer.triangle(p.x - 26f, p.y + 11f, p.x - 10f, p.y + 11f, p.x - 18f, p.y + 26f)
                        shapeRenderer.triangle(p.x + 10f, p.y + 11f, p.x + 26f, p.y + 11f, p.x + 18f, p.y + 26f)
                        shapeRenderer.color = Color(0.28f, 0.55f, 0.88f, 1f)
                        shapeRenderer.triangle(p.x - 23f, p.y + 11f, p.x - 15f, p.y + 11f, p.x - 18f, p.y + 24f)
                        shapeRenderer.triangle(p.x + 13f, p.y + 11f, p.x + 21f, p.y + 11f, p.x + 18f, p.y + 24f)

                        // Cổng vòm phát ánh sáng vàng ấm áp
                        shapeRenderer.color = Color(0.32f, 0.25f, 0.16f, 1f)
                        shapeRenderer.rect(p.x - 7f, p.y - 17f, 14f, 13f)
                        shapeRenderer.color = Color(0.95f, 0.78f, 0.28f, 0.9f)
                        shapeRenderer.rect(p.x - 4.5f, p.y - 16f, 9f, 10f)

                        // Khiên Paladin lớn màu xanh-trắng có chữ thập ở chính giữa mặt tiền
                        val sy = p.y + 5f
                        shapeRenderer.color = Color(0.92f, 0.95f, 1f, 1f)
                        shapeRenderer.rect(p.x - 7.5f, sy - 3f, 15f, 11f)
                        shapeRenderer.triangle(p.x - 7.5f, sy - 3f, p.x + 7.5f, sy - 3f, p.x, sy - 10f)
                        shapeRenderer.color = Color(0.18f, 0.52f, 0.92f, 1f)
                        shapeRenderer.rect(p.x - 5.5f, sy - 2f, 11f, 8.5f)
                        shapeRenderer.triangle(p.x - 5.5f, sy - 2f, p.x + 5.5f, sy - 2f, p.x, sy - 7.5f)
                        // Chữ thập trắng giữa khiên
                        shapeRenderer.color = Color.WHITE
                        shapeRenderer.rect(p.x - 1.2f, sy - 6f, 2.4f, 11f)
                        shapeRenderer.rect(p.x - 4.5f, sy + 1f, 9f, 2.2f)
                    } else {
                        // Lv4 Nhánh 2: BARBARIAN MEAD HALL (Nhà mái rơm vàng + Cặp sừng khổng lồ + Khiên gỗ tròn)
                        shapeRenderer.color = Color(0.38f, 0.34f, 0.28f, 1f)
                        shapeRenderer.rect(p.x - 22f, p.y - 18f, 44f, 26f)
                        shapeRenderer.color = Color(0.56f, 0.52f, 0.44f, 1f)
                        shapeRenderer.rect(p.x - 19f, p.y - 16f, 38f, 22f)

                        // Cổng song sắt tối ở giữa
                        shapeRenderer.color = Color(0.16f, 0.14f, 0.12f, 1f)
                        shapeRenderer.rect(p.x - 7.5f, p.y - 17f, 15f, 14f)
                        shapeRenderer.circle(p.x, p.y - 4f, 7.5f)
                        shapeRenderer.color = Color(0.45f, 0.45f, 0.48f, 1f)
                        for (gx in listOf(-4f, 0f, 4f)) {
                            shapeRenderer.rect(p.x + gx - 0.7f, p.y - 17f, 1.4f, 15f)
                        }

                        // Mái rơm vàng dày phía trên
                        shapeRenderer.color = Color(0.75f, 0.62f, 0.20f, 1f)
                        shapeRenderer.triangle(p.x - 26f, p.y + 4f, p.x + 26f, p.y + 4f, p.x, p.y + 22f)
                        shapeRenderer.rect(p.x - 23f, p.y + 3f, 46f, 8f)
                        shapeRenderer.color = Color(0.92f, 0.80f, 0.32f, 1f)
                        shapeRenderer.triangle(p.x - 22f, p.y + 6f, p.x + 22f, p.y + 6f, p.x, p.y + 20f)

                        // Cặp sừng thú khổng lồ màu trắng ngà vươn lên trên mái
                        shapeRenderer.color = Color(0.92f, 0.90f, 0.82f, 1f)
                        shapeRenderer.triangle(p.x - 8f, p.y + 12f, p.x - 3f, p.y + 16f, p.x - 19f, p.y + 26f)
                        shapeRenderer.triangle(p.x + 8f, p.y + 12f, p.x + 3f, p.y + 16f, p.x + 19f, p.y + 26f)

                        // Khiên tròn gỗ viền sắt treo giữa trán mái
                        shapeRenderer.color = Color(0.35f, 0.22f, 0.12f, 1f)
                        shapeRenderer.circle(p.x, p.y + 9f, 7.5f)
                        shapeRenderer.color = Color(0.65f, 0.32f, 0.16f, 1f)
                        shapeRenderer.circle(p.x, p.y + 9f, 5.8f)
                        shapeRenderer.color = Color(0.78f, 0.80f, 0.84f, 1f)
                        shapeRenderer.circle(p.x, p.y + 9f, 2.2f)
                    }
                }
            }

            // -----------------------------------------------------------------
            // C. NHÁNH THÁP PHÉP (MAGE)
            // -----------------------------------------------------------------
            TowerType.MAGE -> {
                when (tComp.specialization) {
                    // Lv4 Nhánh 1: ARCANE WIZARD (Tháp đá đen hắc diện + Trụ pha lê tím hồng + Tia sét tím trên đỉnh)
                    TowerSpecialization.ARCANE_WIZARD -> {
                        // Bệ đá đen hắc diện thạch
                        shapeRenderer.color = Color(0.18f, 0.16f, 0.22f, 1f)
                        shapeRenderer.circle(p.x, p.y - 3f, 28f)
                        shapeRenderer.color = Color(0.28f, 0.26f, 0.34f, 1f)
                        shapeRenderer.rect(p.x - 20f, p.y - 18f, 40f, 28f)
                        shapeRenderer.color = Color(0.38f, 0.36f, 0.44f, 1f)
                        shapeRenderer.rect(p.x - 14f, p.y - 16f, 28f, 25f)

                        // 2 trụ đá nhọn viền pha lê tím hồng hai bên sườn
                        shapeRenderer.color = Color(0.24f, 0.22f, 0.30f, 1f)
                        shapeRenderer.triangle(p.x - 24f, p.y - 16f, p.x - 13f, p.y - 16f, p.x - 16f, p.y + 18f)
                        shapeRenderer.triangle(p.x + 24f, p.y - 16f, p.x + 13f, p.y - 16f, p.x + 16f, p.y + 18f)
                        shapeRenderer.color = Color(0.92f, 0.32f, 1f, 1f)
                        shapeRenderer.rectLine(p.x - 20f, p.y - 14f, p.x - 15f, p.y + 15f, 2.6f)
                        shapeRenderer.rectLine(p.x + 20f, p.y - 14f, p.x + 15f, p.y + 15f, 2.6f)

                        // Cổ tự Rune tím hồng phát sáng ở mặt trước
                        shapeRenderer.color = Color(0.95f, 0.38f, 1f, 1f)
                        shapeRenderer.rectLine(p.x - 3f, p.y + 2f, p.x + 3f, p.y - 2f, 2.4f)
                        shapeRenderer.rectLine(p.x + 3f, p.y - 2f, p.x - 3f, p.y - 6f, 2.4f)
                        shapeRenderer.circle(p.x, p.y - 9f, 1.8f)

                        // Bục đá tròn trên đỉnh và Pháp sư Arcane mặc áo choàng tím
                        shapeRenderer.color = Color(0.45f, 0.42f, 0.52f, 1f)
                        shapeRenderer.circle(p.x, p.y + 10f, 11f)
                        shapeRenderer.color = Color(0.55f, 0.16f, 0.68f, 1f)
                        shapeRenderer.triangle(p.x - 5.5f, p.y + 9f, p.x + 5.5f, p.y + 9f, p.x, p.y + 21f)
                        shapeRenderer.color = Color(0.96f, 0.80f, 0.60f, 1f)
                        shapeRenderer.circle(p.x, p.y + 18f, 3f)

                        // Vòng cung tia sét tím hồng phóng qua lại giữa 2 đỉnh trụ
                        shapeRenderer.color = Color(0.95f, 0.45f, 1f, 0.9f)
                        shapeRenderer.rectLine(p.x - 15f, p.y + 16f, p.x - 6f, p.y + 24f, 2.5f)
                        shapeRenderer.rectLine(p.x - 6f, p.y + 24f, p.x + 6f, p.y + 24f, 2.5f)
                        shapeRenderer.rectLine(p.x + 6f, p.y + 24f, p.x + 15f, p.y + 16f, 2.5f)
                    }

                    // Lv4 Nhánh 2: SORCERER MAGE (Gò đá nâu + Bậc thang giữa + Pha lê cam vàng + Pháp sư áo cam)
                    TowerSpecialization.SORCERER_MAGE -> {
                        // Gò đá nâu sẫm
                        shapeRenderer.color = Color(0.38f, 0.32f, 0.24f, 1f)
                        shapeRenderer.circle(p.x, p.y - 3f, 28f)
                        shapeRenderer.color = Color(0.52f, 0.45f, 0.35f, 1f)
                        shapeRenderer.rect(p.x - 21f, p.y - 18f, 42f, 26f)
                        shapeRenderer.circle(p.x, p.y + 6f, 18f)

                        // Cầu thang bậc đá xám ở chính giữa dẫn lên đỉnh gò
                        shapeRenderer.color = Color(0.35f, 0.34f, 0.32f, 1f)
                        shapeRenderer.rect(p.x - 6f, p.y - 19f, 12f, 25f)
                        shapeRenderer.color = Color(0.64f, 0.62f, 0.58f, 1f)
                        for (sy in listOf(-17f, -12f, -7f, -2f, 3f)) {
                            shapeRenderer.rect(p.x - 5f, p.y + sy, 10f, 3f)
                        }

                        // Các khối pha lê và cổ tự màu cam vàng rực sáng hai bên sườn đá
                        shapeRenderer.color = Color(1f, 0.62f, 0.12f, 1f)
                        shapeRenderer.triangle(p.x - 16f, p.y + 8f, p.x - 9f, p.y + 8f, p.x - 12.5f, p.y + 18f)
                        shapeRenderer.triangle(p.x + 9f, p.y + 8f, p.x + 16f, p.y + 8f, p.x + 12.5f, p.y + 18f)
                        shapeRenderer.circle(p.x - 14f, p.y - 6f, 2.5f)
                        shapeRenderer.circle(p.x + 14f, p.y - 6f, 2.5f)

                        shapeRenderer.color = Color(1f, 0.88f, 0.28f, 1f)
                        shapeRenderer.circle(p.x - 12.5f, p.y + 11f, 2.2f)
                        shapeRenderer.circle(p.x + 12.5f, p.y + 11f, 2.2f)

                        // Pháp sư Sorcerer mặc áo choàng cam đứng trên đỉnh bậc thang
                        shapeRenderer.color = Color(0.78f, 0.36f, 0.10f, 1f)
                        shapeRenderer.triangle(p.x - 6f, p.y + 8f, p.x + 6f, p.y + 8f, p.x, p.y + 21f)
                        shapeRenderer.color = Color(0.96f, 0.80f, 0.58f, 1f)
                        shapeRenderer.circle(p.x, p.y + 17.5f, 3f)
                        // Quả cầu lửa cam trên đầu trượng
                        shapeRenderer.color = Color(1f, 0.75f, 0.15f, 1f)
                        shapeRenderer.circle(p.x - 6.5f, p.y + 20f, 3.2f)
                    }

                    // Lv1, Lv2, Lv3: MAGES GUILD / ADEPT TOWER / WIZARD TOWER (Chuẩn Hình 1)
                    else -> {
                        // Bệ đá vàng cát bát giác
                        shapeRenderer.color = Color(0.45f, 0.40f, 0.30f, 1f)
                        shapeRenderer.circle(p.x, p.y - 3f, 26f)
                        shapeRenderer.color = Color(0.72f, 0.66f, 0.52f, 1f)
                        shapeRenderer.rect(p.x - 19f, p.y - 17f, 38f, 25f)
                        shapeRenderer.color = Color(0.82f, 0.76f, 0.62f, 1f)
                        shapeRenderer.rect(p.x - 12f, p.y - 16f, 24f, 24f)

                        // Các trụ đá gắn tinh thể xanh thiên thanh (Cyan) ở hai bên sườn (Cấp 3 cao nhọn hơn)
                        val pillarTop = if (tComp.level >= 3) 15f else 10f
                        shapeRenderer.color = Color(0.55f, 0.50f, 0.40f, 1f)
                        shapeRenderer.rect(p.x - 22f, p.y - 14f, 6f, pillarTop + 14f)
                        shapeRenderer.rect(p.x + 16f, p.y - 14f, 6f, pillarTop + 14f)
                        shapeRenderer.color = Color(0.25f, 0.82f, 0.98f, 1f)
                        shapeRenderer.rect(p.x - 21f, p.y - 10f, 4f, pillarTop + 8f)
                        shapeRenderer.rect(p.x + 17f, p.y - 10f, 4f, pillarTop + 8f)

                        // Cổ tự Rune xanh dương phát sáng ở chính giữa mặt đá trước
                        shapeRenderer.color = Color(0.15f, 0.72f, 0.95f, 1f)
                        shapeRenderer.circle(p.x, p.y - 4f, 4.5f)
                        shapeRenderer.color = Color(0.82f, 0.76f, 0.62f, 1f)
                        shapeRenderer.circle(p.x, p.y - 4f, 2.2f)
                        shapeRenderer.color = Color(0.15f, 0.72f, 0.95f, 1f)
                        shapeRenderer.rect(p.x - 1.2f, p.y - 10f, 2.4f, 11f)

                        // Bục đá tròn trên đỉnh & Pháp sư đội mũ chóp nhọn màu xanh lam
                        shapeRenderer.color = Color(0.76f, 0.72f, 0.60f, 1f)
                        shapeRenderer.circle(p.x, p.y + 9f, 12f)
                        shapeRenderer.color = Color(0.22f, 0.42f, 0.78f, 1f)
                        shapeRenderer.triangle(p.x - 5.5f, p.y + 8f, p.x + 5.5f, p.y + 8f, p.x, p.y + 19f)
                        shapeRenderer.color = Color(0.96f, 0.82f, 0.60f, 1f)
                        shapeRenderer.circle(p.x, p.y + 16f, 2.8f)
                        shapeRenderer.color = Color(0.18f, 0.35f, 0.68f, 1f)
                        shapeRenderer.triangle(p.x - 4.5f, p.y + 18f, p.x + 4.5f, p.y + 18f, p.x, p.y + 25f)

                        // Cây trượng phép phát sáng xanh ngọc bên tay trái pháp sư
                        shapeRenderer.color = Color(0.45f, 0.30f, 0.15f, 1f)
                        shapeRenderer.rectLine(p.x - 6.5f, p.y + 8f, p.x - 6.5f, p.y + 19f, 1.8f)
                        shapeRenderer.color = Color(0.35f, 0.95f, 1f, 1f)
                        shapeRenderer.circle(p.x - 6.5f, p.y + 20f, if (tComp.level >= 2) 3.5f else 2.2f)
                    }
                }
            }

            // -----------------------------------------------------------------
            // D. NHÁNH PHÁO CỐI (ARTILLERY)
            // -----------------------------------------------------------------
            TowerType.ARTILLERY -> {
                when (tComp.specialization) {
                    // Lv4 Nhánh 1: BIG BERTHA (Đại pháo đồng sẫm hoa văn vàng + Hộp tên lửa + Chảo radar)
                    TowerSpecialization.BIG_BERTHA -> {
                        // Bệ đá và 2 chân đế xích sắt phía trước
                        shapeRenderer.color = Color(0.32f, 0.32f, 0.35f, 1f)
                        shapeRenderer.circle(p.x, p.y - 3f, 29f)
                        shapeRenderer.rect(p.x - 20f, p.y - 20f, 11f, 10f)
                        shapeRenderer.rect(p.x + 9f, p.y - 20f, 11f, 10f)

                        // Thân pháo cối khổng lồ màu đồng nâu sẫm
                        shapeRenderer.color = Color(0.42f, 0.32f, 0.22f, 1f)
                        shapeRenderer.circle(p.x, p.y, 22f)
                        shapeRenderer.color = Color(0.58f, 0.46f, 0.34f, 1f)
                        shapeRenderer.circle(p.x, p.y + 4f, 18f)

                        // Hoa văn vàng kim chạm khắc quanh bụng pháo
                        shapeRenderer.color = Color(0.95f, 0.75f, 0.20f, 1f)
                        shapeRenderer.rect(p.x - 12f, p.y - 9f, 24f, 3f)
                        shapeRenderer.circle(p.x - 6f, p.y - 7.5f, 2.5f)
                        shapeRenderer.circle(p.x + 6f, p.y - 7.5f, 2.5f)

                        // Bệ phóng tên lửa vuông bên vai trái và Chảo radar tròn bên vai phải
                        shapeRenderer.color = Color(0.48f, 0.48f, 0.52f, 1f)
                        shapeRenderer.rect(p.x - 24f, p.y + 8f, 11f, 11f)
                        shapeRenderer.color = Color.BLACK
                        shapeRenderer.circle(p.x - 20f, p.y + 15f, 1.8f)
                        shapeRenderer.circle(p.x - 16f, p.y + 15f, 1.8f)

                        shapeRenderer.color = Color(0.75f, 0.78f, 0.82f, 1f)
                        shapeRenderer.circle(p.x + 18f, p.y + 12f, 6.5f)
                        shapeRenderer.color = Color(0.42f, 0.45f, 0.50f, 1f)
                        shapeRenderer.circle(p.x + 18f, p.y + 12f, 3.5f)

                        // Vành miệng đại pháo và lòng pháo đen sâu ở đỉnh
                        shapeRenderer.color = Color(0.68f, 0.58f, 0.46f, 1f)
                        shapeRenderer.circle(p.x, p.y + 12f, 13f)
                        shapeRenderer.color = Color(0.12f, 0.12f, 0.14f, 1f)
                        shapeRenderer.circle(p.x, p.y + 12f, 8.5f)
                    }

                    // Lv4 Nhánh 2: TESLA X104 (Đế vòm kim loại có đèn xanh + Ống đồng + Trụ cuộn cảm phóng sét)
                    TowerSpecialization.TESLA_X104 -> {
                        // Đế kim loại tròn xám bạc
                        shapeRenderer.color = Color(0.35f, 0.38f, 0.42f, 1f)
                        shapeRenderer.circle(p.x, p.y - 4f, 28f)
                        shapeRenderer.color = Color(0.55f, 0.60f, 0.66f, 1f)
                        shapeRenderer.circle(p.x, p.y - 1f, 23f)

                        // Ống dẫn năng lượng bằng đồng vàng hai bên
                        shapeRenderer.color = Color(0.88f, 0.62f, 0.18f, 1f)
                        shapeRenderer.rect(p.x - 20f, p.y - 6f, 40f, 4f)
                        shapeRenderer.rect(p.x - 16f, p.y + 2f, 32f, 3.5f)

                        // 2 ô cửa tròn phát ánh sáng xanh điện ở mặt trước đế
                        shapeRenderer.color = Color(0.20f, 0.85f, 1f, 1f)
                        shapeRenderer.circle(p.x - 8f, p.y - 9f, 4.2f)
                        shapeRenderer.circle(p.x + 4f, p.y - 10f, 4.2f)
                        shapeRenderer.color = Color.WHITE
                        shapeRenderer.circle(p.x - 8f, p.y - 9f, 2.2f)
                        shapeRenderer.circle(p.x + 4f, p.y - 10f, 2.2f)

                        // Trụ cuộn cảm Tesla vươn cao ở trung tâm và 2 vòng khuyên điện
                        shapeRenderer.color = Color(0.42f, 0.46f, 0.52f, 1f)
                        shapeRenderer.rect(p.x - 4.5f, p.y + 4f, 9f, 19f)
                        shapeRenderer.color = Color(0.78f, 0.84f, 0.90f, 1f)
                        shapeRenderer.rect(p.x - 11f, p.y + 9f, 22f, 3.5f)
                        shapeRenderer.rect(p.x - 9f, p.y + 15f, 18f, 3.2f)

                        // Quả cầu sét xanh trắng trên đỉnh trụ
                        shapeRenderer.color = Color(0.20f, 0.85f, 1f, 0.85f)
                        shapeRenderer.circle(p.x, p.y + 24f, 7.5f)
                        shapeRenderer.color = Color.WHITE
                        shapeRenderer.circle(p.x, p.y + 24f, 4.2f)
                    }

                    // Lv1, Lv2, Lv3: DWARVEN BOMBARD / ARTILLERY / HOWITZER (Chuẩn Hình 1)
                    else -> {
                        // Chân đế đá/thép
                        shapeRenderer.color = Color(0.38f, 0.36f, 0.34f, 1f)
                        shapeRenderer.circle(p.x, p.y - 4f, 26f)
                        shapeRenderer.rect(p.x - 22f, p.y - 18f, 44f, 12f)

                        // Thân pháo gỗ ghép đai sắt (Cấp 3 bọc các tấm thép xám sáng tán đinh)
                        shapeRenderer.color = if (tComp.level >= 3) Color(0.58f, 0.58f, 0.62f, 1f) else Color(0.55f, 0.36f, 0.20f, 1f)
                        shapeRenderer.circle(p.x, p.y, 19f)
                        shapeRenderer.rect(p.x - 16f, p.y - 12f, 32f, 20f)

                        // Các tấm gia cố thép ở Level 2 & 3
                        if (tComp.level >= 2) {
                            shapeRenderer.color = Color(0.45f, 0.46f, 0.50f, 1f)
                            shapeRenderer.rect(p.x - 18f, p.y - 4f, 36f, 4f)
                            if (tComp.level >= 3) {
                                shapeRenderer.color = Color(0.68f, 0.70f, 0.74f, 1f)
                                shapeRenderer.rect(p.x - 14f, p.y - 15f, 7f, 18f)
                                shapeRenderer.rect(p.x - 3.5f, p.y - 16f, 7f, 18f)
                                shapeRenderer.rect(p.x + 7f, p.y - 15f, 7f, 18f)
                            }
                        }

                        // Chồng đạn bom/thuốc nổ xếp bên trái pháo
                        if (tComp.level == 1) {
                            shapeRenderer.color = Color(0.22f, 0.24f, 0.28f, 1f)
                            shapeRenderer.circle(p.x - 20f, p.y - 6f, 4.5f)
                            shapeRenderer.circle(p.x - 24f, p.y - 10f, 4.5f)
                            shapeRenderer.circle(p.x - 18f, p.y - 11f, 4.5f)
                        } else if (tComp.level == 2) {
                            shapeRenderer.color = Color(0.85f, 0.22f, 0.15f, 1f)
                            shapeRenderer.rect(p.x - 25f, p.y - 12f, 5f, 10f)
                            shapeRenderer.rect(p.x - 19f, p.y - 13f, 5f, 10f)
                        } else {
                            shapeRenderer.color = Color(0.28f, 0.30f, 0.34f, 1f)
                            shapeRenderer.rect(p.x - 25f, p.y - 12f, 7f, 13f)
                            shapeRenderer.color = Color(0.90f, 0.45f, 0.12f, 1f)
                            shapeRenderer.rect(p.x - 25f, p.y - 6f, 7f, 3f)
                        }

                        // Miệng nòng pháo cối hướng lên trời ở đỉnh
                        shapeRenderer.color = Color(0.52f, 0.54f, 0.58f, 1f)
                        shapeRenderer.circle(p.x, p.y + 10f, 12.5f)
                        shapeRenderer.color = Color(0.14f, 0.14f, 0.16f, 1f)
                        shapeRenderer.circle(p.x, p.y + 10f, 8f)

                        // Lính lùn Dwarf râu cam đội mũ sắt đứng bên phải pháo
                        val dx = p.x + 21f
                        val dy = p.y - 6f
                        shapeRenderer.color = Color(0.42f, 0.34f, 0.22f, 1f)
                        shapeRenderer.rect(dx - 4f, dy - 5f, 8f, 8f)
                        shapeRenderer.color = Color(0.88f, 0.45f, 0.12f, 1f)
                        shapeRenderer.triangle(dx - 4.5f, dy + 2f, dx + 4.5f, dy + 2f, dx, dy - 3f)
                        shapeRenderer.color = Color(0.50f, 0.54f, 0.60f, 1f)
                        shapeRenderer.circle(dx, dy + 4.5f, 4f)
                    }
                }
            }
            else -> {}
        }

        // 4. KHỐI BĂNG BAO PHỦ KHI THÁP BỊ BOSS J.T. ĐÓNG BĂNG
        if (tComp.isFrozen) {
            shapeRenderer.color = Color(0.4f, 0.8f, 1f, 0.65f)
            shapeRenderer.rect(p.x - 26f, p.y - 20f, 52f, 48f)
            shapeRenderer.color = Color.WHITE
            shapeRenderer.rectLine(p.x - 20f, p.y + 20f, p.x + 20f, p.y - 14f, 2f)
            shapeRenderer.rectLine(p.x - 14f, p.y - 14f, p.x + 16f, p.y + 18f, 2f)
        }
    }
}
