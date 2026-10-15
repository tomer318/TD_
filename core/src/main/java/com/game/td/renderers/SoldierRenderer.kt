package com.game.td.renderers

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.game.td.ecs.components.*

object SoldierRenderer {

    fun render(shapeRenderer: ShapeRenderer, soldier: Entity) {
        val trans = soldier.getComponent(TransformComponent::class.java) ?: return
        val hp = soldier.getComponent(HealthComponent::class.java) ?: return
        val sComp = soldier.getComponent(SoldierComponent::class.java) ?: return
        val isReinforcement = soldier.getComponent(ReinforcementComponent::class.java) != null
        val p = trans.position

        if (sComp.soldierRankName == "Earth Elemental") {
            shapeRenderer.color = Color(0.42f, 0.40f, 0.36f, 1f)
            shapeRenderer.circle(p.x, p.y, 20f)
            shapeRenderer.color = Color(0.58f, 0.55f, 0.48f, 1f)
            shapeRenderer.circle(p.x - 3f, p.y + 3f, 13f)
            shapeRenderer.color = Color(0.35f, 0.52f, 0.30f, 0.8f)
            shapeRenderer.circle(p.x + 5f, p.y - 4f, 6f)

            shapeRenderer.color = Color.RED
            shapeRenderer.rect(p.x - 20f, p.y + 26f, 40f, 5f)
            val hpP = (hp.currentHealth / hp.maxHealth).coerceAtLeast(0f)
            shapeRenderer.color = Color.CYAN
            shapeRenderer.rect(p.x - 20f, p.y + 26f, 40f * hpP, 5f)
            return
        }

        // VẼ CÁC LOẠI LÍNH THƯỜNG VÀ VIỆN BINH
        when (sComp.soldierRankName) {
            "Paladin" -> {
                shapeRenderer.color = Color(0.85f, 0.88f, 0.95f, 1f)
                shapeRenderer.circle(p.x, p.y, 13f)
                shapeRenderer.color = Color.GOLD
                shapeRenderer.circle(p.x, p.y, 7f)
            }
            "Barbarian" -> {
                shapeRenderer.color = Color(0.85f, 0.55f, 0.40f, 1f)
                shapeRenderer.circle(p.x, p.y, 12f)
                shapeRenderer.color = Color(0.85f, 0.25f, 0.10f, 1f)
                shapeRenderer.circle(p.x, p.y + 5f, 6f)
            }
            "Knight" -> {
                shapeRenderer.color = Color(0.65f, 0.70f, 0.75f, 1f)
                shapeRenderer.circle(p.x, p.y, 12f)
                shapeRenderer.color = Color(0.20f, 0.45f, 0.85f, 1f)
                shapeRenderer.circle(p.x, p.y, 6f)
            }
            "Footman" -> {
                shapeRenderer.color = Color(0.35f, 0.60f, 0.85f, 1f)
                shapeRenderer.circle(p.x, p.y, 11f)
                shapeRenderer.color = Color.LIGHT_GRAY
                shapeRenderer.circle(p.x, p.y + 4f, 5f)
            }
            "Sylvan Elf" -> {
                shapeRenderer.color = Color(0.20f, 0.65f, 0.50f, 1f)
                shapeRenderer.circle(p.x, p.y, 11f)
                shapeRenderer.color = Color(0.95f, 0.85f, 0.35f, 1f)
                shapeRenderer.circle(p.x, p.y + 4f, 5.5f)
                shapeRenderer.color = Color(0.65f, 0.42f, 0.20f, 1f)
                shapeRenderer.line(p.x + 6f, p.y - 6f, p.x + 10f, p.y + 8f)
            }
            // 4 CẤP BẬC VIỆN BINH REINFORCEMENTS THEO UPGRADES
            "Farmer" -> {
                // Nông dân áo nâu vải thô cầm nĩa
                shapeRenderer.color = Color(0.65f, 0.48f, 0.32f, 1f)
                shapeRenderer.circle(p.x, p.y, 10f)
                shapeRenderer.color = Color(0.85f, 0.70f, 0.50f, 1f)
                shapeRenderer.circle(p.x, p.y + 3f, 4.5f)
            }
            "Conscript" -> {
                // Lính nghĩa vụ có mũ sắt và áo xanh rêu
                shapeRenderer.color = Color(0.42f, 0.52f, 0.38f, 1f)
                shapeRenderer.circle(p.x, p.y, 11f)
                shapeRenderer.color = Color(0.65f, 0.68f, 0.70f, 1f)
                shapeRenderer.circle(p.x, p.y + 4f, 5.5f)
            }
            "Warrior" -> {
                // Chiến binh giáp sắt sáng, khiên tròn nhỏ
                shapeRenderer.color = Color(0.55f, 0.60f, 0.68f, 1f)
                shapeRenderer.circle(p.x, p.y, 12f)
                shapeRenderer.color = Color(0.25f, 0.40f, 0.75f, 1f)
                shapeRenderer.circle(p.x, p.y, 6f)
                shapeRenderer.color = Color(0.85f, 0.75f, 0.30f, 1f)
                shapeRenderer.circle(p.x - 7f, p.y - 2f, 4f)
            }
            "Legionnaire" -> {
                // Quân đoàn tinh nhuệ: Mũ có mào đỏ, khiên lớn, cầm lao phóng
                shapeRenderer.color = Color(0.70f, 0.72f, 0.75f, 1f)
                shapeRenderer.circle(p.x, p.y, 13f)
                shapeRenderer.color = Color(0.85f, 0.20f, 0.20f, 1f) // Mào lông đỏ
                shapeRenderer.rect(p.x - 2f, p.y + 6f, 4f, 7f)
                shapeRenderer.color = Color(0.85f, 0.65f, 0.15f, 1f) // Khiên tháp vàng
                shapeRenderer.rect(p.x - 10f, p.y - 6f, 5f, 12f)
                // Cây lao phóng nhọn
                shapeRenderer.color = Color(0.35f, 0.22f, 0.12f, 1f)
                shapeRenderer.rectLine(p.x + 4f, p.y - 6f, p.x + 10f, p.y + 12f, 2f)
            }
            else -> {
                shapeRenderer.color = if (isReinforcement) Color(0.72f, 0.52f, 0.32f, 1f) else Color(0.2f, 0.65f, 1f, 1f)
                shapeRenderer.circle(p.x, p.y, 11f)
            }
        }

        val barW = if (sComp.soldierRankName == "Legionnaire") 26f else 22f
        shapeRenderer.color = Color.RED
        shapeRenderer.rect(p.x - barW / 2f, p.y + 14f, barW, 3.2f)
        val hpP = (hp.currentHealth / hp.maxHealth).coerceAtLeast(0f)
        shapeRenderer.color = Color.CYAN
        shapeRenderer.rect(p.x - barW / 2f, p.y + 14f, barW * hpP, 3.2f)
    }
}
