package com.game.td.renderers

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.game.td.ecs.components.*

object ProjectileRenderer {

    fun render(shapeRenderer: ShapeRenderer, projectile: Entity) {
        val trans = projectile.getComponent(TransformComponent::class.java) ?: return
        val proj = projectile.getComponent(ProjectileComponent::class.java) ?: return
        val p = trans.position

        when (proj.type) {
            // =================================================================
            // 1. MŨI TÊN CHÂN THỰC (XOAY HƯỚNG THEO GÓC BAY, ĐẦU NHỌN, LÔNG ĐUÔI)
            // =================================================================
            ProjectileType.ARROW -> {
                val u = if (proj.velocity.isZero) Vector2(1f, 0f) else Vector2(proj.velocity).nor()
                val v = Vector2(-u.y, u.x) // Vector vuông góc với hướng bay

                // Thân mũi tên bằng gỗ nâu mảnh
                shapeRenderer.color = Color(0.52f, 0.36f, 0.20f, 1f)
                val shaftStart = Vector2(p).sub(Vector2(u).scl(8f))
                val shaftEnd = Vector2(p).add(Vector2(u).scl(4f))
                shapeRenderer.rectLine(shaftStart, shaftEnd, 2.2f)

                // Đầu mũi tên tam giác kim loại nhọn (hoặc xanh độc nếu là cung độc)
                val tip = Vector2(p).add(Vector2(u).scl(9f))
                val baseL = Vector2(p).add(Vector2(u).scl(3.5f)).add(Vector2(v).scl(3.5f))
                val baseR = Vector2(p).add(Vector2(u).scl(3.5f)).sub(Vector2(v).scl(3.5f))

                shapeRenderer.color = proj.color
                shapeRenderer.triangle(tip.x, tip.y, baseL.x, baseL.y, baseR.x, baseR.y)

                // Lông vũ đuôi tên màu trắng ngà
                shapeRenderer.color = Color(0.95f, 0.95f, 0.92f, 0.95f)
                val featherBase = Vector2(p).sub(Vector2(u).scl(5f))
                val featherEnd = Vector2(p).sub(Vector2(u).scl(8.5f))
                shapeRenderer.rectLine(featherBase, featherEnd.add(Vector2(v).scl(2.8f)), 1.5f)
                shapeRenderer.rectLine(featherBase, featherEnd.sub(Vector2(v).scl(2.8f)), 1.5f)
            }

            // =================================================================
            // 2. CẦU PHÉP THUẬT HUYỀN ẢO (LÕI SÁNG, HÀO QUANG VÀ VỆT ĐUÔI MA THUẬT)
            // =================================================================
            ProjectileType.MAGIC_BOLT -> {
                val u = if (proj.velocity.isZero) Vector2(1f, 0f) else Vector2(proj.velocity).nor()

                // Vệt đuôi năng lượng ma thuật kéo dài phía sau
                val tail1 = Vector2(p).sub(Vector2(u).scl(4.5f))
                val tail2 = Vector2(p).sub(Vector2(u).scl(8.5f))

                shapeRenderer.color = Color(proj.color.r, proj.color.g, proj.color.b, 0.45f)
                shapeRenderer.circle(tail2.x, tail2.y, 3.2f)
                shapeRenderer.color = Color(proj.color.r, proj.color.g, proj.color.b, 0.75f)
                shapeRenderer.circle(tail1.x, tail1.y, 4.5f)

                // Hào quang ma thuật tỏa sáng
                shapeRenderer.color = Color(proj.color.r, proj.color.g, proj.color.b, 0.35f)
                shapeRenderer.circle(p.x, p.y, 8.5f)

                // Quả cầu năng lượng chính
                shapeRenderer.color = proj.color
                shapeRenderer.circle(p.x, p.y, 5.5f)

                // Lõi sáng trắng rực rỡ ở tâm
                shapeRenderer.color = Color(1f, 1f, 1f, 0.95f)
                shapeRenderer.circle(p.x, p.y, 2.5f)
            }

            // =================================================================
            // 3. QUẢ BOM PHÁO CỐI DWARVEN (QUỸ ĐẠO CẦU VỒNG 3D, BÓNG RÂM, NGÒI NỔ TÓE LỬA)
            // =================================================================
            ProjectileType.BOMB -> {
                // Tính toán độ cao bay bổng theo quỹ đạo hình parabol
                val progress = (proj.traveledDistance / proj.totalDistance).coerceIn(0f, 1f)
                val arcHeight = proj.maxArcHeight * (4f * progress * (1f - progress))

                // A. Bóng râm của quả bom trên mặt đất (lướt theo mặt đường)
                shapeRenderer.color = Color(0.10f, 0.10f, 0.12f, 0.38f)
                shapeRenderer.circle(p.x, p.y, 5.5f)

                // B. Vị trí thực tế của quả bom trên không trung
                val bx = p.x
                val by = p.y + arcHeight

                // Thân bom kim loại màu đen sắt
                shapeRenderer.color = Color(0.18f, 0.18f, 0.20f, 1f)
                shapeRenderer.circle(bx, by, 7.5f)

                // Vết phản quang kim loại màu xám sáng
                shapeRenderer.color = Color(0.50f, 0.52f, 0.56f, 1f)
                shapeRenderer.circle(bx - 2f, by + 2f, 2.4f)

                // Cổ kíp nổ bằng đồng
                shapeRenderer.color = Color(0.65f, 0.50f, 0.25f, 1f)
                shapeRenderer.rect(bx - 2f, by + 6.5f, 4f, 2.8f)

                // Ngòi nổ đang cháy xì khói tóe lửa cam/vàng
                shapeRenderer.color = Color(1f, 0.45f, 0.10f, 1f)
                shapeRenderer.circle(bx, by + 11f, 2.5f)
                shapeRenderer.color = Color(1f, 0.92f, 0.25f, 1f)
                shapeRenderer.circle(bx, by + 11f, 1.4f)
            }
        }
    }
}
