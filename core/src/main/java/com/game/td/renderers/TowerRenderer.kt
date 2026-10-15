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

        // 1. VẼ NHÀ ĐẶC BIỆT SYLVAN ELF HALL
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

        // 2. VẼ THÁP ĐẶC BIỆT SUNRAY TOWER (MÀN 9)
        if (tComp.specialization == TowerSpecialization.SUNRAY_TOWER) {
            // Đài đá cổ đại lục giác rộng lớn
            shapeRenderer.color = Color(0.48f, 0.46f, 0.40f, 1f)
            shapeRenderer.circle(p.x, p.y, 48f)
            shapeRenderer.color = Color(0.66f, 0.62f, 0.50f, 1f)
            shapeRenderer.circle(p.x, p.y, 40f)
            shapeRenderer.color = Color(0.55f, 0.52f, 0.44f, 1f)
            shapeRenderer.circle(p.x, p.y, 30f)

            // Trụ tháp đá cổ khắc hoa văn
            shapeRenderer.color = Color(0.38f, 0.36f, 0.32f, 1f)
            shapeRenderer.rect(p.x - 14f, p.y - 18f, 28f, 52f)
            shapeRenderer.color = Color(0.85f, 0.72f, 0.25f, 0.85f)
            shapeRenderer.rect(p.x - 4f, p.y - 8f, 8f, 32f)

            // TRẠNG THÁI MÀU SẮC VIÊN NGỌC THEO YÊU CẦU:
            // - Chưa có ai: Xám nhạt
            // - Đang nạp năng lượng (Hình 1): Cam hổ phách
            // - Sẵn sàng khai hoả (Hình 2): Vàng rực phát sáng
            val isReady = tComp.isSunrayReady && tComp.sunraySorcerers > 0
            val crystalColor = when {
                tComp.sunraySorcerers == 0 -> Color(0.72f, 0.72f, 0.75f, 1f) // Xám nhạt khi không có ai
                !isReady -> Color(0.95f, 0.52f, 0.12f, 1f)                   // Hình 1: Cam hổ phách nạp năng lượng
                else -> Color(1f, 0.95f, 0.25f, 1f)                         // Hình 2: Vàng rực sẵn sàng khai hỏa
            }

            if (isReady) {
                // Hào quang tỏa sáng khi sẵn sàng bắn
                shapeRenderer.color = Color(1f, 0.85f, 0.2f, 0.35f)
                shapeRenderer.circle(p.x, p.y + 44f, 26f)
            }

            shapeRenderer.color = crystalColor
            shapeRenderer.triangle(p.x - 12f, p.y + 36f, p.x + 12f, p.y + 36f, p.x, p.y + 60f)
            shapeRenderer.triangle(p.x - 12f, p.y + 36f, p.x + 12f, p.y + 36f, p.x, p.y + 24f)

            // Pháp sư Sunray Sorcerers đứng xung quanh bệ đá (0 đến 4 người)
            val sorcererOffsets = listOf(
                Vector2(-28f, 8f), Vector2(28f, 8f),
                Vector2(-20f, -22f), Vector2(20f, -22f)
            )
            for (idx in 0 until tComp.sunraySorcerers) {
                val off = sorcererOffsets[idx]
                shapeRenderer.color = Color(0.58f, 0.25f, 0.15f, 1f)
                shapeRenderer.circle(p.x + off.x, p.y + off.y, 8.5f)
                shapeRenderer.color = Color(0.88f, 0.72f, 0.52f, 1f)
                shapeRenderer.circle(p.x + off.x, p.y + off.y + 3.5f, 4.5f)
            }
        }

        // 3. CÁC LOẠI THÁP BÌNH THƯỜNG
        when (tComp.towerType) {
            TowerType.ARCHER -> {
                if (tComp.specialization == TowerSpecialization.RANGERS_HIDEOUT) {
                    shapeRenderer.color = Color(0.18f, 0.38f, 0.18f, 1f)
                    shapeRenderer.circle(p.x, p.y, 28f)
                    shapeRenderer.color = Color(0.35f, 0.65f, 0.25f, 1f)
                    shapeRenderer.circle(p.x, p.y, 20f)
                } else if (tComp.specialization == TowerSpecialization.MUSKETEER_GARRISON) {
                    shapeRenderer.color = Color(0.30f, 0.38f, 0.46f, 1f)
                    shapeRenderer.rect(p.x - 18f, p.y - 18f, 36f, 36f)
                    shapeRenderer.color = Color(0.55f, 0.68f, 0.78f, 1f)
                    shapeRenderer.circle(p.x, p.y, 14f)
                    shapeRenderer.color = Color.BLACK
                    shapeRenderer.circle(p.x, p.y, 7f)
                } else {
                    shapeRenderer.color = Color(0.52f, 0.36f, 0.20f, 1f)
                    shapeRenderer.circle(p.x, p.y, 21f + tComp.level * 2f)
                    shapeRenderer.color = Color(0.35f, 0.75f, 0.28f, 1f)
                    shapeRenderer.circle(p.x, p.y, 7f + tComp.level)
                }
            }
            TowerType.BARRACKS -> {
                when (tComp.level) {
                    1 -> {
                        shapeRenderer.color = Color(0.48f, 0.35f, 0.22f, 1f)
                        shapeRenderer.rect(p.x - 19f, p.y - 19f, 38f, 38f)
                        shapeRenderer.color = Color(0.68f, 0.52f, 0.32f, 1f)
                        shapeRenderer.circle(p.x, p.y, 12f)
                    }
                    2 -> {
                        shapeRenderer.color = Color(0.45f, 0.45f, 0.48f, 1f)
                        shapeRenderer.rect(p.x - 20f, p.y - 20f, 40f, 40f)
                        shapeRenderer.color = Color(0.72f, 0.20f, 0.20f, 1f)
                        shapeRenderer.circle(p.x, p.y, 11f)
                    }
                    3 -> {
                        shapeRenderer.color = Color(0.38f, 0.40f, 0.46f, 1f)
                        shapeRenderer.rect(p.x - 22f, p.y - 22f, 44f, 44f)
                        shapeRenderer.color = Color(0.20f, 0.45f, 0.85f, 1f)
                        shapeRenderer.circle(p.x, p.y, 13f)
                    }
                    4 -> {
                        if (tComp.specialization == TowerSpecialization.HOLY_ORDER) {
                            shapeRenderer.color = Color(0.62f, 0.65f, 0.72f, 1f)
                            shapeRenderer.rect(p.x - 24f, p.y - 24f, 48f, 48f)
                            shapeRenderer.color = Color(0.22f, 0.52f, 0.95f, 1f)
                            shapeRenderer.circle(p.x, p.y, 15f)
                            shapeRenderer.color = Color.GOLD
                            shapeRenderer.circle(p.x, p.y, 7f)
                        } else {
                            shapeRenderer.color = Color(0.55f, 0.38f, 0.20f, 1f)
                            shapeRenderer.rect(p.x - 24f, p.y - 24f, 48f, 48f)
                            shapeRenderer.color = Color(0.85f, 0.68f, 0.25f, 1f)
                            shapeRenderer.triangle(p.x - 22f, p.y + 12f, p.x + 22f, p.y + 12f, p.x, p.y + 26f)
                            shapeRenderer.color = Color.WHITE
                            shapeRenderer.circle(p.x - 16f, p.y + 22f, 4f)
                            shapeRenderer.circle(p.x + 16f, p.y + 22f, 4f)
                        }
                    }
                }
            }
            TowerType.MAGE -> {
                if (tComp.specialization == TowerSpecialization.SORCERER_MAGE) {
                    shapeRenderer.color = Color(0.70f, 0.72f, 0.73f, 1f)
                    shapeRenderer.circle(p.x, p.y, 28f)
                    shapeRenderer.color = Color(0.88f, 0.42f, 0.12f, 1f)
                    shapeRenderer.circle(p.x, p.y, 20f)
                    shapeRenderer.color = Color(1f, 0.85f, 0.25f, 1f)
                    shapeRenderer.circle(p.x, p.y, 11f)
                } else if (tComp.specialization == TowerSpecialization.ARCANE_WIZARD) {
                    shapeRenderer.color = Color(0.28f, 0.18f, 0.38f, 1f)
                    shapeRenderer.circle(p.x, p.y, 28f)
                    shapeRenderer.color = Color(0.68f, 0.22f, 0.88f, 1f)
                    shapeRenderer.circle(p.x, p.y, 18f)
                    shapeRenderer.color = Color.MAGENTA
                    shapeRenderer.circle(p.x, p.y, 9f)
                } else {
                    shapeRenderer.color = tComp.towerType.color
                    shapeRenderer.circle(p.x, p.y, 21f + tComp.level * 2f)
                    shapeRenderer.color = Color.CYAN
                    shapeRenderer.circle(p.x, p.y, 8f + tComp.level)
                }
            }
            TowerType.ARTILLERY -> {
                if (tComp.specialization == TowerSpecialization.BIG_BERTHA) {
                    shapeRenderer.color = Color(0.32f, 0.22f, 0.12f, 1f)
                    shapeRenderer.rect(p.x - 24f, p.y - 24f, 48f, 48f)
                    shapeRenderer.color = Color(0.55f, 0.40f, 0.22f, 1f)
                    shapeRenderer.circle(p.x, p.y, 16f)
                    shapeRenderer.color = Color.BLACK
                    shapeRenderer.circle(p.x, p.y, 8f)
                } else if (tComp.specialization == TowerSpecialization.TESLA_X104) {
                    shapeRenderer.color = Color(0.55f, 0.62f, 0.70f, 1f)
                    shapeRenderer.circle(p.x, p.y, 28f)
                    shapeRenderer.color = Color(0.85f, 0.92f, 0.98f, 1f)
                    shapeRenderer.circle(p.x, p.y, 18f)
                    shapeRenderer.color = Color.CYAN
                    shapeRenderer.circle(p.x, p.y, 8f)
                } else {
                    shapeRenderer.color = Color(0.40f, 0.30f, 0.18f, 1f)
                    shapeRenderer.circle(p.x, p.y, 22f + tComp.level * 2f)
                    shapeRenderer.color = Color(0.60f, 0.45f, 0.28f, 1f)
                    shapeRenderer.circle(p.x, p.y, 9f + tComp.level)
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
