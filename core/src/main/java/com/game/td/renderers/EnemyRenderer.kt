package com.game.td.renderers

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.game.td.ecs.components.*

object EnemyRenderer {

    private val OUTLINE = Color(0.12f, 0.09f, 0.07f, 1f)
    private val SHADOW = Color(0.06f, 0.06f, 0.05f, 0.36f)

    // =========================================================================
    // HÀM VẼ TÚI TRỨNG NHỆN (SPIDER EGG)
    // =========================================================================
    fun renderEgg(shapeRenderer: ShapeRenderer, egg: Entity) {
        val trans = egg.getComponent(TransformComponent::class.java) ?: return
        val p = trans.position

        shapeRenderer.color = SHADOW
        shapeRenderer.ellipse(p.x - 11f, p.y - 9f, 22f, 8f)

        shapeRenderer.color = OUTLINE
        shapeRenderer.circle(p.x, p.y + 2f, 11f)
        shapeRenderer.circle(p.x, p.y - 2f, 12.5f)

        shapeRenderer.color = Color(0.88f, 0.88f, 0.82f, 1f)
        shapeRenderer.circle(p.x, p.y + 2f, 9.2f)
        shapeRenderer.circle(p.x, p.y - 2f, 10.5f)

        shapeRenderer.color = Color(0.45f, 0.72f, 0.28f, 0.9f)
        shapeRenderer.circle(p.x - 3f, p.y + 1f, 3.2f)
        shapeRenderer.circle(p.x + 3f, p.y - 2f, 3.8f)
        shapeRenderer.circle(p.x, p.y + 5f, 2.4f)
    }

    // =========================================================================
    // HÀM VẼ TẤT CẢ 35 LOẠI QUÁI VẬT & BOSS CHUẨN PHONG CÁCH KINGDOM RUSH
    // =========================================================================
    fun render(shapeRenderer: ShapeRenderer, enemy: Entity, showHpBar: Boolean = true) {
        val trans = enemy.getComponent(TransformComponent::class.java) ?: return
        val hp = enemy.getComponent(HealthComponent::class.java) ?: return
        val eComp = enemy.getComponent(EnemyComponent::class.java) ?: return
        val p = trans.position

        // DẠNG BIẾN HÌNH ĐẶC BIỆT: CỪU (POLYMORPH CỦA SORCERER MAGE)
        if (eComp.isSheep) {
            shapeRenderer.color = SHADOW
            shapeRenderer.ellipse(p.x - 11f, p.y - 10f, 22f, 7f)
            shapeRenderer.color = OUTLINE
            shapeRenderer.circle(p.x, p.y, 12.5f)
            shapeRenderer.circle(p.x + 6f, p.y + 4f, 8f)
            shapeRenderer.color = Color.WHITE
            shapeRenderer.circle(p.x, p.y, 10.5f)
            shapeRenderer.circle(p.x - 5f, p.y + 3f, 6f)
            shapeRenderer.circle(p.x + 6f, p.y + 4f, 6.2f)
            shapeRenderer.color = Color(0.22f, 0.18f, 0.16f, 1f)
            shapeRenderer.circle(p.x + 7.5f, p.y + 3.5f, 4.2f)
            shapeRenderer.color = Color.WHITE
            shapeRenderer.circle(p.x + 8.5f, p.y + 4.5f, 1.3f)
            if (showHpBar) {
                renderHpBar(shapeRenderer, p.x, p.y + 15f, 20f, hp)
            }
            return
        }

        when (eComp.enemyType) {

            // 01. GOBLIN: Da xanh lá, tai nhọn dài vểnh ngang, mày chau chữ V, đóng khố nâu, cầm chùy gỗ
            EnemyType.GOBLIN -> {
                shapeRenderer.color = SHADOW
                shapeRenderer.ellipse(p.x - 10f, p.y - 12f, 20f, 6f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 5.5f, p.y - 11f, 4.5f, 5f)
                shapeRenderer.rect(p.x + 1f, p.y - 11f, 4.5f, 5f)
                shapeRenderer.rect(p.x - 7.5f, p.y - 8f, 15f, 12f)
                shapeRenderer.triangle(p.x - 5f, p.y + 6f, p.x - 16f, p.y + 10f, p.x - 5f, p.y + 1f)
                shapeRenderer.triangle(p.x + 5f, p.y + 6f, p.x + 16f, p.y + 10f, p.x + 5f, p.y + 1f)
                shapeRenderer.circle(p.x, p.y + 4.5f, 9f)

                shapeRenderer.color = Color(0.36f, 0.58f, 0.18f, 1f)
                shapeRenderer.rect(p.x - 4f, p.y - 9.5f, 3f, 4f)
                shapeRenderer.rect(p.x + 1f, p.y - 9.5f, 3f, 4f)
                shapeRenderer.rect(p.x - 6f, p.y - 6.5f, 12f, 9f)
                shapeRenderer.color = Color(0.42f, 0.28f, 0.14f, 1f)
                shapeRenderer.rect(p.x - 6f, p.y - 6.5f, 12f, 5.5f)
                shapeRenderer.color = Color(0.62f, 0.44f, 0.22f, 1f)
                shapeRenderer.rect(p.x - 6f, p.y - 2.5f, 12f, 2f)

                shapeRenderer.color = Color(0.46f, 0.70f, 0.22f, 1f)
                shapeRenderer.triangle(p.x - 5f, p.y + 5.5f, p.x - 14f, p.y + 9f, p.x - 5f, p.y + 2f)
                shapeRenderer.triangle(p.x + 5f, p.y + 5.5f, p.x + 14f, p.y + 9f, p.x + 5f, p.y + 2f)
                shapeRenderer.circle(p.x, p.y + 4.5f, 7.3f)

                shapeRenderer.color = Color(0.98f, 0.90f, 0.18f, 1f)
                shapeRenderer.circle(p.x - 2.6f, p.y + 4.5f, 1.7f)
                shapeRenderer.circle(p.x + 2.6f, p.y + 4.5f, 1.7f)
                shapeRenderer.color = OUTLINE
                shapeRenderer.rectLine(p.x - 5.5f, p.y + 7f, p.x - 1f, p.y + 5.2f, 1.8f)
                shapeRenderer.rectLine(p.x + 5.5f, p.y + 7f, p.x + 1f, p.y + 5.2f, 1.8f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.rectLine(p.x - 6f, p.y - 3.5f, p.x + 8f, p.y - 2f, 5f)
                shapeRenderer.color = Color(0.65f, 0.45f, 0.22f, 1f)
                shapeRenderer.rectLine(p.x - 5f, p.y - 3.5f, p.x + 7f, p.y - 2f, 3f)
            }

            // 02. ORC: Chiến binh Orc vạm vỡ, hàm dưới bạnh với 2 răng nanh trắng, cầm dao phay lớn dính máu đỏ
            EnemyType.ORC -> {
                shapeRenderer.color = SHADOW
                shapeRenderer.ellipse(p.x - 13f, p.y - 14f, 26f, 7f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 7.5f, p.y - 13f, 6f, 6f)
                shapeRenderer.rect(p.x + 1.5f, p.y - 13f, 6f, 6f)
                shapeRenderer.circle(p.x, p.y - 1f, 11f)
                shapeRenderer.rect(p.x - 9.5f, p.y - 9f, 19f, 12f)
                shapeRenderer.triangle(p.x - 7f, p.y + 8f, p.x - 15f, p.y + 11f, p.x - 6f, p.y + 4f)
                shapeRenderer.triangle(p.x + 7f, p.y + 8f, p.x + 15f, p.y + 11f, p.x + 6f, p.y + 4f)
                shapeRenderer.circle(p.x, p.y + 6.5f, 9.8f)

                shapeRenderer.color = Color(0.28f, 0.48f, 0.16f, 1f)
                shapeRenderer.rect(p.x - 6f, p.y - 11.5f, 4f, 5f)
                shapeRenderer.rect(p.x + 2f, p.y - 11.5f, 4f, 5f)
                shapeRenderer.circle(p.x, p.y - 1f, 9.2f)
                shapeRenderer.color = Color(0.38f, 0.25f, 0.14f, 1f)
                shapeRenderer.rect(p.x - 8f, p.y - 8f, 16f, 5.5f)

                shapeRenderer.color = Color(0.36f, 0.58f, 0.20f, 1f)
                shapeRenderer.triangle(p.x - 6f, p.y + 7.5f, p.x - 13f, p.y + 10f, p.x - 6f, p.y + 4.5f)
                shapeRenderer.triangle(p.x + 6f, p.y + 7.5f, p.x + 13f, p.y + 10f, p.x + 6f, p.y + 4.5f)
                shapeRenderer.circle(p.x, p.y + 6.5f, 8f)
                shapeRenderer.color = Color(0.25f, 0.38f, 0.14f, 1f)
                shapeRenderer.triangle(p.x - 4f, p.y + 13f, p.x + 4f, p.y + 13f, p.x, p.y + 18f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 5.5f, p.y + 2.5f, 11f, 2f)
                shapeRenderer.color = Color.WHITE
                shapeRenderer.triangle(p.x - 4.5f, p.y + 3.5f, p.x - 3f, p.y + 7.5f, p.x - 1.5f, p.y + 3.5f)
                shapeRenderer.triangle(p.x + 1.5f, p.y + 3.5f, p.x + 3f, p.y + 7.5f, p.x + 4.5f, p.y + 3.5f)

                shapeRenderer.color = Color(0.98f, 0.88f, 0.18f, 1f)
                shapeRenderer.circle(p.x - 2.8f, p.y + 8.5f, 1.6f)
                shapeRenderer.circle(p.x + 2.8f, p.y + 8.5f, 1.6f)
                shapeRenderer.color = OUTLINE
                shapeRenderer.rectLine(p.x - 6f, p.y + 10.5f, p.x - 1f, p.y + 9f, 1.8f)
                shapeRenderer.rectLine(p.x + 6f, p.y + 10.5f, p.x + 1f, p.y + 9f, 1.8f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 8.5f, p.y - 5.5f, 18f, 7f)
                shapeRenderer.color = Color(0.82f, 0.86f, 0.90f, 1f)
                shapeRenderer.rect(p.x - 7f, p.y - 4f, 14f, 4.5f)
                shapeRenderer.color = Color(0.85f, 0.16f, 0.14f, 1f)
                shapeRenderer.rect(p.x - 7f, p.y - 4f, 11f, 2.2f)
                shapeRenderer.color = Color(0.48f, 0.30f, 0.14f, 1f)
                shapeRenderer.rect(p.x + 6f, p.y - 3.5f, 3.5f, 3f)
            }

            // 03. WULF: Sói nâu xám 4 chân, lông lưng dựng nhọn, mõm nhe răng trắng, mắt đỏ
            EnemyType.WULF -> {
                shapeRenderer.color = SHADOW
                shapeRenderer.ellipse(p.x - 13f, p.y - 10f, 26f, 6f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 9f, p.y - 9f, 3.5f, 6f)
                shapeRenderer.rect(p.x - 4f, p.y - 9f, 3.5f, 6f)
                shapeRenderer.rect(p.x + 2f, p.y - 9f, 3.5f, 6f)
                shapeRenderer.rect(p.x + 6.5f, p.y - 9f, 3.5f, 6f)
                shapeRenderer.ellipse(p.x - 12f, p.y - 6f, 22f, 12f)
                shapeRenderer.triangle(p.x - 9f, p.y + 2f, p.x - 5f, p.y + 9f, p.x - 2f, p.y + 2f)
                shapeRenderer.triangle(p.x - 4f, p.y + 3f, p.x, p.y + 10f, p.x + 3f, p.y + 3f)
                shapeRenderer.circle(p.x + 7.5f, p.y + 1f, 6.8f)
                shapeRenderer.triangle(p.x + 7f, p.y + 4f, p.x + 15.5f, p.y + 1f, p.x + 7f, p.y - 3.5f)

                shapeRenderer.color = Color(0.46f, 0.42f, 0.36f, 1f)
                shapeRenderer.rect(p.x - 7.8f, p.y - 7.5f, 2f, 4.5f)
                shapeRenderer.rect(p.x + 3f, p.y - 7.5f, 2f, 4.5f)
                shapeRenderer.ellipse(p.x - 10.5f, p.y - 4.5f, 19f, 9f)
                shapeRenderer.triangle(p.x - 7.5f, p.y + 2f, p.x - 5f, p.y + 7.5f, p.x - 2.5f, p.y + 2f)
                shapeRenderer.triangle(p.x - 3f, p.y + 3f, p.x, p.y + 8.5f, p.x + 2.5f, p.y + 3f)
                shapeRenderer.circle(p.x + 7.5f, p.y + 1f, 5.2f)
                shapeRenderer.triangle(p.x + 7f, p.y + 3f, p.x + 14f, p.y + 1f, p.x + 7f, p.y - 2.5f)
                shapeRenderer.triangle(p.x + 4f, p.y + 5f, p.x + 6f, p.y + 10f, p.x + 8.5f, p.y + 5f)

                shapeRenderer.color = Color.WHITE
                shapeRenderer.triangle(p.x + 9.5f, p.y - 0.5f, p.x + 10.5f, p.y - 2.5f, p.x + 11.5f, p.y - 0.5f)
                shapeRenderer.color = Color(0.95f, 0.25f, 0.12f, 1f)
                shapeRenderer.circle(p.x + 9f, p.y + 2.2f, 1.4f)
            }

            // 04. WORG: Sói bóng đêm khổng lồ lông đen xanh, bờm lưng nhọn hoắt, nhe nanh trắng, mắt đỏ rực
            EnemyType.WORG -> {
                shapeRenderer.color = SHADOW
                shapeRenderer.ellipse(p.x - 15f, p.y - 11f, 30f, 7f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 11f, p.y - 10f, 4.5f, 7f)
                shapeRenderer.rect(p.x - 5f, p.y - 10f, 4.5f, 7f)
                shapeRenderer.rect(p.x + 2f, p.y - 10f, 4.5f, 7f)
                shapeRenderer.rect(p.x + 7.5f, p.y - 10f, 4.5f, 7f)
                shapeRenderer.ellipse(p.x - 14f, p.y - 6.5f, 26f, 14f)
                shapeRenderer.triangle(p.x - 10f, p.y + 3f, p.x - 6f, p.y + 12f, p.x - 2f, p.y + 3f)
                shapeRenderer.triangle(p.x - 4f, p.y + 4f, p.x + 1f, p.y + 13.5f, p.x + 5f, p.y + 4f)
                shapeRenderer.circle(p.x + 9f, p.y + 2f, 8f)
                shapeRenderer.triangle(p.x + 8f, p.y + 6f, p.x + 18f, p.y + 1.5f, p.x + 8f, p.y - 4f)

                shapeRenderer.color = Color(0.22f, 0.26f, 0.30f, 1f)
                shapeRenderer.rect(p.x - 9.5f, p.y - 8.5f, 2.5f, 5f)
                shapeRenderer.rect(p.x + 3.5f, p.y - 8.5f, 2.5f, 5f)
                shapeRenderer.ellipse(p.x - 12f, p.y - 5f, 22f, 11f)
                shapeRenderer.color = Color(0.32f, 0.37f, 0.42f, 1f)
                shapeRenderer.triangle(p.x - 8.5f, p.y + 3f, p.x - 6f, p.y + 10.5f, p.x - 3f, p.y + 3f)
                shapeRenderer.triangle(p.x - 3f, p.y + 4f, p.x + 1f, p.y + 11.5f, p.x + 4f, p.y + 4f)
                shapeRenderer.circle(p.x + 9f, p.y + 2f, 6.2f)
                shapeRenderer.triangle(p.x + 8f, p.y + 4.5f, p.x + 16f, p.y + 1.5f, p.x + 8f, p.y - 2.5f)
                shapeRenderer.triangle(p.x + 5f, p.y + 6.5f, p.x + 7.5f, p.y + 12.5f, p.x + 10.5f, p.y + 6.5f)

                shapeRenderer.color = Color.WHITE
                shapeRenderer.triangle(p.x + 11f, p.y, p.x + 12.2f, p.y - 2.8f, p.x + 13.5f, p.y)
                shapeRenderer.triangle(p.x + 13.5f, p.y + 0.5f, p.x + 14.5f, p.y - 2f, p.x + 15.5f, p.y + 0.5f)
                shapeRenderer.color = Color.RED
                shapeRenderer.circle(p.x + 10.5f, p.y + 3.2f, 1.8f)
            }

            // 05. SHAMAN: Pháp sư bộ tộc đeo mặt nạ sọ xương trắng, mũ bờm lông vũ cam rực, cầm trượng sọ
            EnemyType.SHAMAN -> {
                shapeRenderer.color = SHADOW
                shapeRenderer.ellipse(p.x - 11f, p.y - 12f, 22f, 6.5f)

                val feathers = listOf(-8f, -4f, 0f, 4f, 8f)
                shapeRenderer.color = OUTLINE
                for (fx in feathers) {
                    shapeRenderer.triangle(p.x + fx - 3.5f, p.y + 8f, p.x + fx + 3.5f, p.y + 8f, p.x + fx * 1.35f, p.y + 19f)
                }
                shapeRenderer.rect(p.x - 7.5f, p.y - 10.5f, 15f, 13f)
                shapeRenderer.circle(p.x, p.y + 4.5f, 9.5f)

                for (fx in feathers) {
                    shapeRenderer.color = Color(0.92f, 0.36f, 0.08f, 1f)
                    shapeRenderer.triangle(p.x + fx - 2.4f, p.y + 8f, p.x + fx + 2.4f, p.y + 8f, p.x + fx * 1.3f, p.y + 17.2f)
                    shapeRenderer.color = Color(0.98f, 0.68f, 0.15f, 1f)
                    shapeRenderer.triangle(p.x + fx - 1.2f, p.y + 8.5f, p.x + fx + 1.2f, p.y + 8.5f, p.x + fx * 1.15f, p.y + 14f)
                }

                shapeRenderer.color = Color(0.68f, 0.34f, 0.12f, 1f)
                shapeRenderer.rect(p.x - 6f, p.y - 9f, 12f, 10f)
                shapeRenderer.color = Color(0.92f, 0.56f, 0.16f, 1f)
                shapeRenderer.rect(p.x - 6f, p.y - 5f, 12f, 2.5f)

                shapeRenderer.color = Color(0.94f, 0.90f, 0.78f, 1f)
                shapeRenderer.circle(p.x, p.y + 4.5f, 7.6f)
                shapeRenderer.rect(p.x - 4.5f, p.y - 1f, 9f, 5f)
                shapeRenderer.color = OUTLINE
                shapeRenderer.circle(p.x - 2.8f, p.y + 4.5f, 2.1f)
                shapeRenderer.circle(p.x + 2.8f, p.y + 4.5f, 2.1f)
                shapeRenderer.triangle(p.x - 1.2f, p.y + 1.5f, p.x + 1.2f, p.y + 1.5f, p.x, p.y + 3.2f)
                shapeRenderer.color = Color(1f, 0.75f, 0.2f, 1f)
                shapeRenderer.circle(p.x - 2.8f, p.y + 4.5f, 0.8f)
                shapeRenderer.circle(p.x + 2.8f, p.y + 4.5f, 0.8f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.rectLine(p.x + 8.5f, p.y - 9f, p.x + 10.5f, p.y + 9f, 4f)
                shapeRenderer.circle(p.x + 10.5f, p.y + 10f, 4.5f)
                shapeRenderer.color = Color(0.58f, 0.36f, 0.16f, 1f)
                shapeRenderer.rectLine(p.x + 8.5f, p.y - 8f, p.x + 10.5f, p.y + 8f, 2.2f)
                shapeRenderer.color = Color(1f, 0.62f, 0.15f, 1f)
                shapeRenderer.circle(p.x + 10.5f, p.y + 10f, 3.2f)
            }

            // 06. BANDIT: Thổ phỉ nhanh nhẹn, trán da người, khăn cam đỏ bịt kín mũi miệng, cầm dao găm bạc
            EnemyType.BANDIT -> {
                shapeRenderer.color = SHADOW
                shapeRenderer.ellipse(p.x - 10f, p.y - 12f, 20f, 6f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 6f, p.y - 11f, 4.5f, 5f)
                shapeRenderer.rect(p.x + 1.5f, p.y - 11f, 4.5f, 5f)
                shapeRenderer.rect(p.x - 7.5f, p.y - 8f, 15f, 12f)
                shapeRenderer.circle(p.x, p.y + 4.5f, 8.8f)

                shapeRenderer.color = Color(0.24f, 0.22f, 0.20f, 1f)
                shapeRenderer.rect(p.x - 6f, p.y - 7f, 12f, 9f)
                shapeRenderer.color = Color(0.52f, 0.34f, 0.18f, 1f)
                shapeRenderer.rect(p.x - 6f, p.y - 3f, 12f, 5f)

                shapeRenderer.color = Color(0.94f, 0.76f, 0.56f, 1f)
                shapeRenderer.circle(p.x, p.y + 5f, 7f)
                shapeRenderer.color = OUTLINE
                shapeRenderer.circle(p.x - 2.5f, p.y + 6.5f, 1.5f)
                shapeRenderer.circle(p.x + 2.5f, p.y + 6.5f, 1.5f)
                shapeRenderer.rectLine(p.x - 5f, p.y + 8.5f, p.x - 1f, p.y + 7.2f, 1.6f)
                shapeRenderer.rectLine(p.x + 5f, p.y + 8.5f, p.x + 1f, p.y + 7.2f, 1.6f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 7.8f, p.y + 0.5f, 15.6f, 5f)
                shapeRenderer.color = Color(0.88f, 0.36f, 0.14f, 1f)
                shapeRenderer.rect(p.x - 6.8f, p.y + 1.2f, 13.6f, 3.8f)
                shapeRenderer.triangle(p.x - 5.5f, p.y + 1.2f, p.x + 5.5f, p.y + 1.2f, p.x, p.y - 3.5f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 5f, p.y - 5.5f, 13f, 4.5f)
                shapeRenderer.color = Color(0.32f, 0.24f, 0.16f, 1f)
                shapeRenderer.rect(p.x - 4f, p.y - 4.5f, 3.5f, 2.5f)
                shapeRenderer.color = Color(0.90f, 0.94f, 0.98f, 1f)
                shapeRenderer.rect(p.x - 0.5f, p.y - 4.5f, 6.5f, 2.5f)
                shapeRenderer.triangle(p.x + 6f, p.y - 4.5f, p.x + 6f, p.y - 2f, p.x + 9.5f, p.y - 3.2f)
            }

            // 07. BRIGAND: Sơn tặc mặc giáp da nâu, đội mũ sắt chóp nhọn có khe mắt đen, cầm kiếm bản rộng
            EnemyType.BRIGAND -> {
                shapeRenderer.color = SHADOW
                shapeRenderer.ellipse(p.x - 11f, p.y - 13f, 22f, 6.5f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 6.5f, p.y - 12f, 5f, 5f)
                shapeRenderer.rect(p.x + 1.5f, p.y - 12f, 5f, 5f)
                shapeRenderer.circle(p.x, p.y - 2f, 10f)
                shapeRenderer.rect(p.x - 8.5f, p.y - 9f, 17f, 11f)
                shapeRenderer.circle(p.x, p.y + 5.5f, 9f)
                shapeRenderer.triangle(p.x - 3f, p.y + 13f, p.x + 3f, p.y + 13f, p.x, p.y + 18f)

                shapeRenderer.color = Color(0.58f, 0.38f, 0.18f, 1f)
                shapeRenderer.rect(p.x - 7f, p.y - 8f, 14f, 10f)
                shapeRenderer.color = Color(0.84f, 0.68f, 0.42f, 1f)
                shapeRenderer.circle(p.x - 7.5f, p.y - 2f, 3.5f)
                shapeRenderer.circle(p.x + 7.5f, p.y - 2f, 3.5f)

                shapeRenderer.color = Color(0.56f, 0.60f, 0.64f, 1f)
                shapeRenderer.circle(p.x, p.y + 5.5f, 7.4f)
                shapeRenderer.triangle(p.x - 2f, p.y + 12f, p.x + 2f, p.y + 12f, p.x, p.y + 16f)
                shapeRenderer.color = Color(0.76f, 0.80f, 0.84f, 1f)
                shapeRenderer.circle(p.x - 2.5f, p.y + 8f, 2.2f)

                shapeRenderer.color = Color(0.92f, 0.74f, 0.54f, 1f)
                shapeRenderer.rect(p.x - 5.5f, p.y + 1f, 11f, 3.5f)
                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 6f, p.y + 4.5f, 12f, 3f)
                shapeRenderer.rect(p.x + 0.5f, p.y + 3.5f, 4.5f, 4.5f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 5f, p.y - 6.5f, 16f, 5.5f)
                shapeRenderer.color = Color(0.88f, 0.92f, 0.96f, 1f)
                shapeRenderer.rect(p.x - 3.5f, p.y - 5.2f, 13f, 3f)
            }

            // 08. MARAUDER: Tướng cướp khổng lồ mặc giáp đồng, mũ sắt song sắt dọc, cầm đinh ba bạc
            EnemyType.MARAUDER -> {
                shapeRenderer.color = SHADOW
                shapeRenderer.ellipse(p.x - 15f, p.y - 15f, 30f, 8f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 9f, p.y - 14f, 7f, 6f)
                shapeRenderer.rect(p.x + 2f, p.y - 14f, 7f, 6f)
                shapeRenderer.circle(p.x, p.y - 1f, 13.5f)
                shapeRenderer.rect(p.x - 12f, p.y - 10f, 24f, 14f)
                shapeRenderer.circle(p.x - 12f, p.y + 3f, 6.5f)
                shapeRenderer.circle(p.x + 12f, p.y + 3f, 6.5f)
                shapeRenderer.circle(p.x, p.y + 9f, 9.5f)

                shapeRenderer.color = Color(0.68f, 0.48f, 0.26f, 1f)
                shapeRenderer.circle(p.x, p.y - 1f, 11.5f)
                shapeRenderer.rect(p.x - 10f, p.y - 8.5f, 20f, 12f)
                shapeRenderer.color = Color(0.82f, 0.62f, 0.36f, 1f)
                shapeRenderer.circle(p.x - 11.5f, p.y + 3f, 4.8f)
                shapeRenderer.circle(p.x + 11.5f, p.y + 3f, 4.8f)
                shapeRenderer.color = Color(0.42f, 0.28f, 0.14f, 1f)
                shapeRenderer.rect(p.x - 10f, p.y - 8.5f, 20f, 4f)

                shapeRenderer.color = Color(0.58f, 0.56f, 0.52f, 1f)
                shapeRenderer.circle(p.x, p.y + 9f, 7.8f)
                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 5.5f, p.y + 6f, 11f, 5.5f)
                shapeRenderer.color = Color(0.75f, 0.74f, 0.70f, 1f)
                shapeRenderer.rect(p.x - 3f, p.y + 6f, 1.5f, 5.5f)
                shapeRenderer.rect(p.x, p.y + 6f, 1.5f, 5.5f)
                shapeRenderer.rect(p.x + 3f, p.y + 6f, 1.5f, 5.5f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.rectLine(p.x - 14f, p.y - 4f, p.x + 12f, p.y - 2f, 4.5f)
                shapeRenderer.color = Color(0.48f, 0.30f, 0.14f, 1f)
                shapeRenderer.rectLine(p.x - 13f, p.y - 4f, p.x + 8f, p.y - 2.3f, 2.5f)
                shapeRenderer.color = Color(0.88f, 0.92f, 0.96f, 1f)
                shapeRenderer.triangle(p.x + 8f, p.y + 2.5f, p.x + 8f, p.y - 6.5f, p.x + 17f, p.y - 2f)
            }

            // 09. OGRE: Gã khổng lồ Ogre cơ bắp xanh rêu, giáp vai sắt, cầm chùy gỗ đỏ đóng đinh trắng
            EnemyType.OGRE -> {
                shapeRenderer.color = SHADOW
                shapeRenderer.ellipse(p.x - 16f, p.y - 16f, 32f, 8.5f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 10f, p.y - 15f, 8f, 7f)
                shapeRenderer.rect(p.x + 2f, p.y - 15f, 8f, 7f)
                shapeRenderer.circle(p.x, p.y, 15.5f)
                shapeRenderer.rect(p.x - 13.5f, p.y - 11f, 27f, 16f)
                shapeRenderer.circle(p.x, p.y + 11f, 10f)

                shapeRenderer.color = Color(0.46f, 0.56f, 0.28f, 1f)
                shapeRenderer.circle(p.x, p.y, 13.5f)
                shapeRenderer.rect(p.x - 11.5f, p.y - 9f, 23f, 14f)
                shapeRenderer.color = Color(0.38f, 0.24f, 0.12f, 1f)
                shapeRenderer.rect(p.x - 11.5f, p.y - 9.5f, 23f, 4.5f)
                shapeRenderer.rectLine(p.x - 10f, p.y + 7f, p.x + 9f, p.y - 6f, 3.5f)
                shapeRenderer.color = Color(0.55f, 0.58f, 0.62f, 1f)
                shapeRenderer.circle(p.x - 11f, p.y + 6f, 5.5f)

                shapeRenderer.color = Color(0.52f, 0.62f, 0.32f, 1f)
                shapeRenderer.circle(p.x, p.y + 11f, 8.2f)
                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 5.5f, p.y + 7.5f, 11f, 2f)
                shapeRenderer.color = Color.WHITE
                shapeRenderer.triangle(p.x - 4.5f, p.y + 8.5f, p.x - 3f, p.y + 12f, p.x - 1.5f, p.y + 8.5f)
                shapeRenderer.triangle(p.x + 1.5f, p.y + 8.5f, p.x + 3f, p.y + 12f, p.x + 4.5f, p.y + 8.5f)
                shapeRenderer.color = Color(0.98f, 0.88f, 0.20f, 1f)
                shapeRenderer.circle(p.x - 2.8f, p.y + 13f, 1.6f)
                shapeRenderer.circle(p.x + 2.8f, p.y + 13f, 1.6f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.rectLine(p.x - 7f, p.y - 4f, p.x + 14f, p.y - 2f, 9f)
                shapeRenderer.color = Color(0.75f, 0.28f, 0.14f, 1f)
                shapeRenderer.rectLine(p.x - 6f, p.y - 4f, p.x + 13f, p.y - 2f, 6.2f)
                shapeRenderer.color = Color.WHITE
                shapeRenderer.circle(p.x + 4f, p.y - 2.5f, 1.8f)
                shapeRenderer.circle(p.x + 9f, p.y - 2f, 1.8f)
                shapeRenderer.triangle(p.x + 11f, p.y + 1f, p.x + 13f, p.y + 1f, p.x + 12f, p.y + 5f)
                shapeRenderer.triangle(p.x + 11f, p.y - 5f, p.x + 13f, p.y - 5f, p.x + 12f, p.y - 9f)
            }

            // 10. GIANT_SPIDER: Nhện độc xanh đen lưng vằn đỏ, 8 chân đốt cong quặp xuống đất
            EnemyType.GIANT_SPIDER -> {
                shapeRenderer.color = SHADOW
                shapeRenderer.ellipse(p.x - 13f, p.y - 10f, 26f, 7f)

                for (side in listOf(-1f, 1f)) {
                    shapeRenderer.color = OUTLINE
                    shapeRenderer.rectLine(p.x + side * 4f, p.y + 1f, p.x + side * 12f, p.y + 6f, 3.6f)
                    shapeRenderer.rectLine(p.x + side * 12f, p.y + 6f, p.x + side * 13f, p.y - 6f, 3.2f)

                    shapeRenderer.rectLine(p.x + side * 5f, p.y - 1f, p.x + side * 15f, p.y + 2f, 3.6f)
                    shapeRenderer.rectLine(p.x + side * 15f, p.y + 2f, p.x + side * 16f, p.y - 8f, 3.2f)

                    shapeRenderer.rectLine(p.x + side * 4f, p.y - 3f, p.x + side * 11f, p.y - 2f, 3.4f)
                    shapeRenderer.rectLine(p.x + side * 11f, p.y - 2f, p.x + side * 10f, p.y - 9.5f, 3.0f)

                    shapeRenderer.color = Color(0.26f, 0.30f, 0.38f, 1f)
                    shapeRenderer.rectLine(p.x + side * 4f, p.y + 1f, p.x + side * 12f, p.y + 6f, 1.8f)
                    shapeRenderer.rectLine(p.x + side * 12f, p.y + 6f, p.x + side * 13f, p.y - 5.5f, 1.6f)
                    shapeRenderer.rectLine(p.x + side * 5f, p.y - 1f, p.x + side * 15f, p.y + 2f, 1.8f)
                    shapeRenderer.rectLine(p.x + side * 15f, p.y + 2f, p.x + side * 16f, p.y - 7.5f, 1.6f)
                }

                shapeRenderer.color = OUTLINE
                shapeRenderer.circle(p.x, p.y + 2f, 8.8f)
                shapeRenderer.circle(p.x, p.y - 3.5f, 6.8f)

                shapeRenderer.color = Color(0.24f, 0.28f, 0.36f, 1f)
                shapeRenderer.circle(p.x, p.y + 2f, 7.2f)
                shapeRenderer.circle(p.x, p.y - 3.5f, 5.2f)

                shapeRenderer.color = Color(0.86f, 0.18f, 0.14f, 1f)
                shapeRenderer.rect(p.x - 4f, p.y + 3f, 8f, 2f)
                shapeRenderer.rect(p.x - 3f, p.y + 6f, 6f, 1.8f)
                shapeRenderer.circle(p.x - 2.2f, p.y - 4f, 1.6f)
                shapeRenderer.circle(p.x + 2.2f, p.y - 4f, 1.6f)
            }

            // 11. SPIDER_MATRIARCH: Nhện Chúa khổng lồ chân to vạm vỡ, lưng vằn xanh độc vàng
            EnemyType.SPIDER_MATRIARCH -> {
                shapeRenderer.color = SHADOW
                shapeRenderer.ellipse(p.x - 16f, p.y - 13f, 32f, 8f)

                for (side in listOf(-1f, 1f)) {
                    shapeRenderer.color = OUTLINE
                    shapeRenderer.rectLine(p.x + side * 5f, p.y + 3f, p.x + side * 16f, p.y + 9f, 4.8f)
                    shapeRenderer.rectLine(p.x + side * 16f, p.y + 9f, p.x + side * 18f, p.y - 8f, 4.2f)

                    shapeRenderer.rectLine(p.x + side * 6f, p.y, p.x + side * 19f, p.y + 4f, 4.8f)
                    shapeRenderer.rectLine(p.x + side * 19f, p.y + 4f, p.x + side * 20f, p.y - 11f, 4.2f)

                    shapeRenderer.rectLine(p.x + side * 5f, p.y - 3f, p.x + side * 14f, p.y - 2f, 4.5f)
                    shapeRenderer.rectLine(p.x + side * 14f, p.y - 2f, p.x + side * 13f, p.y - 12f, 4.0f)

                    shapeRenderer.color = Color(0.28f, 0.24f, 0.36f, 1f)
                    shapeRenderer.rectLine(p.x + side * 5f, p.y + 3f, p.x + side * 16f, p.y + 9f, 2.4f)
                    shapeRenderer.rectLine(p.x + side * 16f, p.y + 9f, p.x + side * 18f, p.y - 7.5f, 2.2f)
                    shapeRenderer.rectLine(p.x + side * 6f, p.y, p.x + side * 19f, p.y + 4f, 2.4f)
                    shapeRenderer.rectLine(p.x + side * 19f, p.y + 4f, p.x + side * 20f, p.y - 10.5f, 2.2f)
                }

                shapeRenderer.color = OUTLINE
                shapeRenderer.circle(p.x, p.y + 3.5f, 11.5f)
                shapeRenderer.circle(p.x, p.y - 4.5f, 8.5f)

                shapeRenderer.color = Color(0.22f, 0.20f, 0.30f, 1f)
                shapeRenderer.circle(p.x, p.y + 3.5f, 9.6f)
                shapeRenderer.circle(p.x, p.y - 4.5f, 6.8f)

                shapeRenderer.color = Color(0.68f, 0.85f, 0.16f, 1f)
                shapeRenderer.rect(p.x - 6f, p.y + 5f, 12f, 2.5f)
                shapeRenderer.rect(p.x - 4.5f, p.y + 8.5f, 9f, 2.2f)
                shapeRenderer.circle(p.x, p.y + 2f, 2.5f)

                shapeRenderer.color = Color.RED
                shapeRenderer.circle(p.x - 2.8f, p.y - 5.5f, 1.9f)
                shapeRenderer.circle(p.x + 2.8f, p.y - 5.5f, 1.9f)
            }

            // 12. SPIDER_HATCHLING: Nhện con chân cong
            EnemyType.SPIDER_HATCHLING -> {
                shapeRenderer.color = SHADOW
                shapeRenderer.ellipse(p.x - 8f, p.y - 7f, 16f, 5f)

                shapeRenderer.color = OUTLINE
                for (side in listOf(-1f, 1f)) {
                    shapeRenderer.rectLine(p.x + side * 2f, p.y, p.x + side * 8f, p.y + 3f, 2.4f)
                    shapeRenderer.rectLine(p.x + side * 8f, p.y + 3f, p.x + side * 9f, p.y - 4f, 2.0f)
                    shapeRenderer.rectLine(p.x + side * 2f, p.y - 2f, p.x + side * 7f, p.y - 6f, 2.2f)
                }
                shapeRenderer.circle(p.x, p.y, 5.5f)

                shapeRenderer.color = Color(0.26f, 0.32f, 0.22f, 1f)
                shapeRenderer.circle(p.x, p.y, 4.2f)
                shapeRenderer.color = Color.RED
                shapeRenderer.circle(p.x - 1.5f, p.y - 1f, 1.2f)
                shapeRenderer.circle(p.x + 1.5f, p.y - 1f, 1.2f)
            }

            // 13. DARK_KNIGHT: Hiệp sĩ bóng đêm mũ sừng sắt, cầm kiếm lửa cam và khiên sắt
            EnemyType.DARK_KNIGHT -> {
                shapeRenderer.color = SHADOW
                shapeRenderer.ellipse(p.x - 12f, p.y - 13f, 24f, 7f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 7f, p.y - 12f, 5.5f, 5f)
                shapeRenderer.rect(p.x + 1.5f, p.y - 12f, 5.5f, 5f)
                shapeRenderer.rect(p.x - 9f, p.y - 8.5f, 18f, 12f)
                shapeRenderer.circle(p.x, p.y + 6f, 9f)
                shapeRenderer.triangle(p.x - 6f, p.y + 8f, p.x - 13f, p.y + 18f, p.x - 2f, p.y + 12f)
                shapeRenderer.triangle(p.x + 6f, p.y + 8f, p.x + 13f, p.y + 18f, p.x + 2f, p.y + 12f)

                shapeRenderer.color = Color(0.24f, 0.25f, 0.28f, 1f)
                shapeRenderer.rect(p.x - 7.5f, p.y - 7f, 15f, 10f)
                shapeRenderer.color = Color(0.38f, 0.40f, 0.44f, 1f)
                shapeRenderer.circle(p.x, p.y + 6f, 7.3f)
                shapeRenderer.color = Color(0.75f, 0.76f, 0.78f, 1f)
                shapeRenderer.triangle(p.x - 6f, p.y + 9f, p.x - 11.5f, p.y + 16.5f, p.x - 3f, p.y + 11.5f)
                shapeRenderer.triangle(p.x + 6f, p.y + 9f, p.x + 11.5f, p.y + 16.5f, p.x + 3f, p.y + 11.5f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 5f, p.y + 5f, 10f, 2.5f)
                shapeRenderer.rect(p.x - 1.2f, p.y + 2f, 2.4f, 5f)
                shapeRenderer.color = Color.RED
                shapeRenderer.circle(p.x - 2.5f, p.y + 6.2f, 1.1f)
                shapeRenderer.circle(p.x + 2.5f, p.y + 6.2f, 1.1f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.circle(p.x + 6.5f, p.y - 2f, 6.2f)
                shapeRenderer.color = Color(0.78f, 0.80f, 0.84f, 1f)
                shapeRenderer.circle(p.x + 6.5f, p.y - 2f, 4.6f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 9.5f, p.y - 5.5f, 15f, 4.8f)
                shapeRenderer.color = Color(0.95f, 0.38f, 0.12f, 1f)
                shapeRenderer.rect(p.x - 8f, p.y - 4.3f, 12f, 2.5f)
            }

            // 14. GARGOYLE: Quỷ dơi đá cánh rộng, tai dơi nhọn, mắt đỏ
            EnemyType.GARGOYLE -> {
                shapeRenderer.color = SHADOW
                shapeRenderer.ellipse(p.x - 10f, p.y - 14f, 20f, 6f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.triangle(p.x - 3f, p.y + 3f, p.x - 22f, p.y + 13f, p.x - 13f, p.y - 7f)
                shapeRenderer.triangle(p.x + 3f, p.y + 3f, p.x + 22f, p.y + 13f, p.x + 13f, p.y - 7f)
                shapeRenderer.circle(p.x, p.y - 1f, 8.5f)
                shapeRenderer.circle(p.x, p.y + 6f, 7f)
                shapeRenderer.triangle(p.x - 5f, p.y + 8f, p.x - 9f, p.y + 15f, p.x - 1f, p.y + 10f)
                shapeRenderer.triangle(p.x + 5f, p.y + 8f, p.x + 9f, p.y + 15f, p.x + 1f, p.y + 10f)

                shapeRenderer.color = Color(0.46f, 0.40f, 0.38f, 1f)
                shapeRenderer.triangle(p.x - 3f, p.y + 2f, p.x - 20f, p.y + 11.5f, p.x - 12f, p.y - 5.5f)
                shapeRenderer.triangle(p.x + 3f, p.y + 2f, p.x + 20f, p.y + 11.5f, p.x + 12f, p.y - 5.5f)

                shapeRenderer.color = Color(0.56f, 0.50f, 0.48f, 1f)
                shapeRenderer.circle(p.x, p.y - 1f, 6.8f)
                shapeRenderer.circle(p.x, p.y + 6f, 5.5f)
                shapeRenderer.triangle(p.x - 4.5f, p.y + 8f, p.x - 7.5f, p.y + 13.5f, p.x - 1.5f, p.y + 9.5f)
                shapeRenderer.triangle(p.x + 4.5f, p.y + 8f, p.x + 7.5f, p.y + 13.5f, p.x + 1.5f, p.y + 9.5f)

                shapeRenderer.color = Color(0.95f, 0.25f, 0.15f, 1f)
                shapeRenderer.circle(p.x - 2.2f, p.y + 6.5f, 1.4f)
                shapeRenderer.circle(p.x + 2.2f, p.y + 6.5f, 1.4f)
            }

            // 15. SHADOW_ARCHER: Cung thủ áo choàng trùm đầu đen kịt, 2 mắt vàng sáng, cầm cung gỗ ngang ngực
            EnemyType.SHADOW_ARCHER -> {
                shapeRenderer.color = SHADOW
                shapeRenderer.ellipse(p.x - 10f, p.y - 12f, 20f, 6f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 6f, p.y - 11f, 4.5f, 5f)
                shapeRenderer.rect(p.x + 1.5f, p.y - 11f, 4.5f, 5f)
                shapeRenderer.rect(p.x - 8f, p.y - 8f, 16f, 12f)
                shapeRenderer.circle(p.x, p.y + 5f, 8.5f)
                shapeRenderer.triangle(p.x - 8f, p.y + 5f, p.x - 13f, p.y - 2f, p.x - 5f, p.y - 4f)

                shapeRenderer.color = Color(0.16f, 0.15f, 0.20f, 1f)
                shapeRenderer.rect(p.x - 6.5f, p.y - 7f, 13f, 10f)
                shapeRenderer.circle(p.x, p.y + 5f, 6.8f)

                shapeRenderer.color = Color(1f, 0.90f, 0.18f, 1f)
                shapeRenderer.circle(p.x - 2.3f, p.y + 5.2f, 1.6f)
                shapeRenderer.circle(p.x + 2.3f, p.y + 5.2f, 1.6f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.rectLine(p.x - 9f, p.y - 2.5f, p.x + 9f, p.y - 1.5f, 4.2f)
                shapeRenderer.color = Color(0.62f, 0.36f, 0.16f, 1f)
                shapeRenderer.rectLine(p.x - 8f, p.y - 2.5f, p.x + 8f, p.y - 1.5f, 2.4f)
            }

            // 16. GOLEM_HEAD: Đầu người máy sắt 2 sừng trâu có chân nhỏ chạy lạch bạch
            EnemyType.GOLEM_HEAD -> {
                shapeRenderer.color = SHADOW
                shapeRenderer.ellipse(p.x - 10f, p.y - 10f, 20f, 6f)
                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 6.5f, p.y - 9.5f, 5f, 4.5f)
                shapeRenderer.rect(p.x + 1.5f, p.y - 9.5f, 5f, 4.5f)

                shapeRenderer.triangle(p.x - 6f, p.y + 4f, p.x - 15f, p.y + 13f, p.x - 2f, p.y + 9f)
                shapeRenderer.triangle(p.x + 6f, p.y + 4f, p.x + 15f, p.y + 13f, p.x + 2f, p.y + 9f)
                shapeRenderer.color = Color(0.58f, 0.50f, 0.42f, 1f)
                shapeRenderer.triangle(p.x - 6f, p.y + 5f, p.x - 13f, p.y + 12f, p.x - 3f, p.y + 8f)
                shapeRenderer.triangle(p.x + 6f, p.y + 5f, p.x + 13f, p.y + 12f, p.x + 3f, p.y + 8f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.circle(p.x, p.y, 9.5f)
                shapeRenderer.color = Color(0.46f, 0.38f, 0.32f, 1f)
                shapeRenderer.circle(p.x, p.y, 8f)
                shapeRenderer.color = Color(0.62f, 0.54f, 0.46f, 1f)
                shapeRenderer.circle(p.x - 2.5f, p.y + 2.5f, 2.2f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 5.5f, p.y - 0.5f, 11f, 3.2f)
                shapeRenderer.color = Color(1f, 0.55f, 0.12f, 1f)
                shapeRenderer.circle(p.x - 2.5f, p.y + 1f, 1.2f)
                shapeRenderer.circle(p.x + 2.5f, p.y + 1f, 1.2f)
            }

            // 17. JUGGERNAUT: BOSS CỖ MÁY HỦY DIỆT KHỔNG LỒ
            EnemyType.JUGGERNAUT -> {
                shapeRenderer.color = SHADOW
                shapeRenderer.ellipse(p.x - 28f, p.y - 26f, 56f, 16f)

                shapeRenderer.color = Color(0.20f, 0.16f, 0.13f, 1f)
                shapeRenderer.rect(p.x - 17f, p.y - 22f, 14f, 16f)
                shapeRenderer.rect(p.x + 3f, p.y - 22f, 14f, 16f)
                shapeRenderer.color = Color(0.38f, 0.31f, 0.25f, 1f)
                shapeRenderer.rect(p.x - 15f, p.y - 20f, 10f, 13f)
                shapeRenderer.rect(p.x + 5f, p.y - 20f, 10f, 13f)
                shapeRenderer.color = Color(0.28f, 0.22f, 0.18f, 1f)
                shapeRenderer.ellipse(p.x - 18f, p.y - 24f, 16f, 7f)
                shapeRenderer.ellipse(p.x + 2f, p.y - 24f, 16f, 7f)

                shapeRenderer.color = Color(0.22f, 0.17f, 0.14f, 1f)
                shapeRenderer.circle(p.x, p.y + 2f, 22f)
                shapeRenderer.rect(p.x - 18f, p.y - 10f, 36f, 22f)

                shapeRenderer.color = Color(0.45f, 0.37f, 0.30f, 1f)
                shapeRenderer.circle(p.x, p.y + 2f, 19.5f)
                shapeRenderer.rect(p.x - 15.5f, p.y - 8f, 31f, 19f)

                shapeRenderer.color = Color(0.28f, 0.22f, 0.18f, 1f)
                shapeRenderer.rect(p.x - 16f, p.y - 10f, 32f, 6f)
                shapeRenderer.color = Color(0.58f, 0.50f, 0.42f, 1f)
                shapeRenderer.rect(p.x - 5f, p.y - 9.5f, 10f, 5f)

                shapeRenderer.color = Color(0.65f, 0.57f, 0.48f, 1f)
                shapeRenderer.circle(p.x - 7f, p.y + 4f, 3.5f)
                shapeRenderer.circle(p.x - 2f, p.y - 1f, 2.5f)
                shapeRenderer.circle(p.x - 9f, p.y - 2f, 2.2f)
                shapeRenderer.circle(p.x + 6f, p.y + 5f, 2.8f)
                shapeRenderer.color = Color(0.32f, 0.25f, 0.20f, 1f)
                shapeRenderer.circle(p.x + 5f, p.y - 2f, 2.5f)
                shapeRenderer.circle(p.x - 3f, p.y + 7f, 2.0f)

                shapeRenderer.color = Color(0.22f, 0.17f, 0.14f, 1f)
                shapeRenderer.circle(p.x - 19f, p.y + 9f, 11.5f)
                shapeRenderer.circle(p.x + 19f, p.y + 9f, 11.5f)
                shapeRenderer.color = Color(0.50f, 0.42f, 0.34f, 1f)
                shapeRenderer.circle(p.x - 19f, p.y + 9f, 9.5f)
                shapeRenderer.circle(p.x + 19f, p.y + 9f, 9.5f)
                shapeRenderer.color = Color(0.68f, 0.60f, 0.50f, 1f)
                shapeRenderer.circle(p.x - 21f, p.y + 12f, 3f)

                shapeRenderer.color = Color(0.22f, 0.17f, 0.14f, 1f)
                shapeRenderer.rectLine(p.x - 21f, p.y + 6f, p.x - 25f, p.y - 8f, 11f)
                shapeRenderer.circle(p.x - 25f, p.y - 10f, 8.5f)
                shapeRenderer.color = Color(0.46f, 0.38f, 0.31f, 1f)
                shapeRenderer.rectLine(p.x - 21f, p.y + 6f, p.x - 25f, p.y - 8f, 8f)
                shapeRenderer.circle(p.x - 25f, p.y - 10f, 6.8f)

                shapeRenderer.color = Color(0.20f, 0.15f, 0.12f, 1f)
                shapeRenderer.rectLine(p.x + 18f, p.y + 5f, p.x + 28f, p.y + 16f, 15f)
                shapeRenderer.ellipse(p.x + 19f, p.y + 11f, 18f, 11f)

                shapeRenderer.color = Color(0.44f, 0.36f, 0.29f, 1f)
                shapeRenderer.rectLine(p.x + 18f, p.y + 5f, p.x + 27f, p.y + 15f, 11.5f)
                shapeRenderer.color = Color(0.58f, 0.50f, 0.42f, 1f)
                shapeRenderer.ellipse(p.x + 20f, p.y + 12f, 16f, 9.5f)
                shapeRenderer.color = Color(0.10f, 0.08f, 0.07f, 1f)
                shapeRenderer.ellipse(p.x + 22.5f, p.y + 13.5f, 11f, 6.5f)

                shapeRenderer.color = Color(0.22f, 0.17f, 0.14f, 1f)
                shapeRenderer.triangle(p.x - 9f, p.y + 20f, p.x - 25f, p.y + 33f, p.x - 5f, p.y + 27f)
                shapeRenderer.triangle(p.x - 25f, p.y + 33f, p.x - 19f, p.y + 41f, p.x - 13f, p.y + 28f)
                shapeRenderer.color = Color(0.65f, 0.57f, 0.48f, 1f)
                shapeRenderer.triangle(p.x - 9f, p.y + 21f, p.x - 23f, p.y + 32f, p.x - 6f, p.y + 26f)
                shapeRenderer.triangle(p.x - 23f, p.y + 32f, p.x - 18f, p.y + 39f, p.x - 14f, p.y + 28f)

                shapeRenderer.color = Color(0.22f, 0.17f, 0.14f, 1f)
                shapeRenderer.triangle(p.x + 9f, p.y + 20f, p.x + 25f, p.y + 33f, p.x + 5f, p.y + 27f)
                shapeRenderer.triangle(p.x + 25f, p.y + 33f, p.x + 19f, p.y + 41f, p.x + 13f, p.y + 28f)
                shapeRenderer.color = Color(0.65f, 0.57f, 0.48f, 1f)
                shapeRenderer.triangle(p.x + 9f, p.y + 21f, p.x + 23f, p.y + 32f, p.x + 6f, p.y + 26f)
                shapeRenderer.triangle(p.x + 23f, p.y + 32f, p.x + 18f, p.y + 39f, p.x + 14f, p.y + 28f)

                shapeRenderer.color = Color(0.22f, 0.17f, 0.14f, 1f)
                shapeRenderer.circle(p.x, p.y + 18f, 12.5f)
                shapeRenderer.rect(p.x - 11.5f, p.y + 10f, 23f, 10f)

                shapeRenderer.color = Color(0.48f, 0.40f, 0.33f, 1f)
                shapeRenderer.circle(p.x, p.y + 18f, 10.5f)
                shapeRenderer.rect(p.x - 9.5f, p.y + 11f, 19f, 9f)

                shapeRenderer.color = Color(0.10f, 0.08f, 0.07f, 1f)
                shapeRenderer.rect(p.x - 7.5f, p.y + 16f, 15f, 3.8f)
                shapeRenderer.rect(p.x - 6f, p.y + 12f, 12f, 2f)
                shapeRenderer.color = Color(1f, 0.55f, 0.12f, 1f)
                shapeRenderer.circle(p.x - 3.5f, p.y + 17.8f, 1.5f)
                shapeRenderer.circle(p.x + 3.5f, p.y + 17.8f, 1.5f)
            }

            // =================================================================
            // 18. TROLL (Hàng 1 Cột 3 trong ảnh mẫu):
            // Da xanh lam, tóc Mohawk đỏ xòe 3 chỏm, tai nhọn ngang, răng nanh trắng, cầm giáo/dao đá
            // =================================================================
            EnemyType.TROLL -> {
                shapeRenderer.color = SHADOW
                shapeRenderer.ellipse(p.x - 12f, p.y - 13f, 24f, 6.5f)

                // Viền đen chân, thân, tai nhọn và chỏm tóc Mohawk đỏ
                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 7f, p.y - 12f, 5.5f, 5.5f)
                shapeRenderer.rect(p.x + 1.5f, p.y - 12f, 5.5f, 5.5f)
                shapeRenderer.rect(p.x - 8.5f, p.y - 8.5f, 17f, 12f)
                shapeRenderer.triangle(p.x - 6f, p.y + 7f, p.x - 15f, p.y + 10.5f, p.x - 5f, p.y + 2.5f)
                shapeRenderer.triangle(p.x + 6f, p.y + 7f, p.x + 15f, p.y + 10.5f, p.x + 5f, p.y + 2.5f)
                shapeRenderer.circle(p.x, p.y + 5.5f, 9.2f)
                // 3 chỏm tóc Mohawk đỏ dựng nhọn
                shapeRenderer.triangle(p.x - 6.5f, p.y + 10f, p.x - 10f, p.y + 17f, p.x - 1f, p.y + 13f)
                shapeRenderer.triangle(p.x - 4.5f, p.y + 11.5f, p.x, p.y + 20f, p.x + 4.5f, p.y + 11.5f)
                shapeRenderer.triangle(p.x + 1f, p.y + 13f, p.x + 10f, p.y + 17f, p.x + 6.5f, p.y + 10f)

                // Thân xanh lam & Khố nâu
                shapeRenderer.color = Color(0.28f, 0.60f, 0.70f, 1f)
                shapeRenderer.rect(p.x - 5.5f, p.y - 10.5f, 3.5f, 4f)
                shapeRenderer.rect(p.x + 2f, p.y - 10.5f, 3.5f, 4f)
                shapeRenderer.rect(p.x - 7f, p.y - 7f, 14f, 9.5f)
                shapeRenderer.color = Color(0.45f, 0.28f, 0.14f, 1f)
                shapeRenderer.rect(p.x - 7f, p.y - 7.5f, 14f, 4.5f)

                // Đầu & Tai xanh sáng
                shapeRenderer.color = Color(0.38f, 0.72f, 0.82f, 1f)
                shapeRenderer.triangle(p.x - 5f, p.y + 6.5f, p.x - 13f, p.y + 9.5f, p.x - 5f, p.y + 3.5f)
                shapeRenderer.triangle(p.x + 5f, p.y + 6.5f, p.x + 13f, p.y + 9.5f, p.x + 5f, p.y + 3.5f)
                shapeRenderer.circle(p.x, p.y + 5.5f, 7.5f)

                // Bờm tóc đỏ tươi
                shapeRenderer.color = Color(0.88f, 0.22f, 0.16f, 1f)
                shapeRenderer.triangle(p.x - 5.5f, p.y + 10.5f, p.x - 8.5f, p.y + 15.5f, p.x - 1f, p.y + 12.5f)
                shapeRenderer.triangle(p.x - 3.5f, p.y + 11.5f, p.x, p.y + 18.2f, p.x + 3.5f, p.y + 11.5f)
                shapeRenderer.triangle(p.x + 1f, p.y + 12.5f, p.x + 8.5f, p.y + 15.5f, p.x + 5.5f, p.y + 10.5f)

                // 2 Răng nanh trắng nhô lên & Mắt vàng hung dữ
                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 5f, p.y + 2f, 10f, 1.8f)
                shapeRenderer.color = Color.WHITE
                shapeRenderer.triangle(p.x - 4.5f, p.y + 2.8f, p.x - 3.2f, p.y + 6.5f, p.x - 1.8f, p.y + 2.8f)
                shapeRenderer.triangle(p.x + 1.8f, p.y + 2.8f, p.x + 3.2f, p.y + 6.5f, p.x + 4.5f, p.y + 2.8f)
                shapeRenderer.color = Color(1f, 0.88f, 0.18f, 1f)
                shapeRenderer.circle(p.x - 2.5f, p.y + 7f, 1.5f)
                shapeRenderer.circle(p.x + 2.5f, p.y + 7f, 1.5f)

                // Vũ khí cán gỗ mũi giáo đá nhọn cầm ngang trước bụng
                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 9.5f, p.y - 5.5f, 16f, 4.8f)
                shapeRenderer.triangle(p.x + 6f, p.y - 0.5f, p.x + 6f, p.y - 6.5f, p.x + 14.5f, p.y - 3.5f)
                shapeRenderer.color = Color(0.68f, 0.36f, 0.16f, 1f)
                shapeRenderer.rect(p.x - 8f, p.y - 4.3f, 14f, 2.5f)
                shapeRenderer.color = Color(0.84f, 0.86f, 0.88f, 1f)
                shapeRenderer.triangle(p.x + 6.5f, p.y - 1.5f, p.x + 6.5f, p.y - 5.5f, p.x + 13f, p.y - 3.5f)
            }

            // =================================================================
            // 19. WINTER_WOLF (Hàng 1 Cột 2 trong ảnh mẫu):
            // Sói tuyết lông trắng xù gai nhọn trên lưng, chân và bụng xám tro, mắt đỏ, nhe nanh
            // =================================================================
            EnemyType.WINTER_WOLF -> {
                shapeRenderer.color = SHADOW
                shapeRenderer.ellipse(p.x - 15f, p.y - 11f, 30f, 7f)

                // Viền đen toàn thân sói tuyết
                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 10.5f, p.y - 10f, 4.2f, 7f)
                shapeRenderer.rect(p.x - 5f, p.y - 10f, 4.2f, 7f)
                shapeRenderer.rect(p.x + 2f, p.y - 10f, 4.2f, 7f)
                shapeRenderer.rect(p.x + 7f, p.y - 10f, 4.2f, 7f)
                shapeRenderer.ellipse(p.x - 13.5f, p.y - 6.5f, 25f, 13.5f)
                // Bờm lông nhọn trắng dọc lưng
                shapeRenderer.triangle(p.x - 12f, p.y + 1f, p.x - 16f, p.y + 8f, p.x - 6f, p.y + 4f)
                shapeRenderer.triangle(p.x - 9f, p.y + 3f, p.x - 7f, p.y + 12f, p.x - 1f, p.y + 4f)
                shapeRenderer.triangle(p.x - 4f, p.y + 4f, p.x, p.y + 13.5f, p.x + 5f, p.y + 4f)
                shapeRenderer.circle(p.x + 8.5f, p.y + 2f, 7.8f)
                shapeRenderer.triangle(p.x + 7.5f, p.y + 5.5f, p.x + 17.5f, p.y + 1f, p.x + 7.5f, p.y - 4f)

                // Bụng & 4 chân màu xám tro (giống ảnh mẫu)
                shapeRenderer.color = Color(0.55f, 0.60f, 0.66f, 1f)
                shapeRenderer.rect(p.x - 9.3f, p.y - 8.5f, 2.2f, 5.5f)
                shapeRenderer.rect(p.x - 3.8f, p.y - 8.5f, 2.2f, 5.5f)
                shapeRenderer.rect(p.x + 3.2f, p.y - 8.5f, 2.2f, 5.5f)
                shapeRenderer.rect(p.x + 8.2f, p.y - 8.5f, 2.2f, 5.5f)
                shapeRenderer.ellipse(p.x - 11f, p.y - 5.2f, 20f, 7f)
                shapeRenderer.triangle(p.x + 7.5f, p.y + 2f, p.x + 15.8f, p.y + 0.5f, p.x + 7.5f, p.y - 2.8f)

                // Lớp lông tuyết trắng muốt phủ trên lưng và đầu
                shapeRenderer.color = Color(0.92f, 0.96f, 1f, 1f)
                shapeRenderer.ellipse(p.x - 11.5f, p.y - 2.5f, 21f, 8.5f)
                shapeRenderer.triangle(p.x - 11f, p.y + 1.5f, p.x - 14.5f, p.y + 7f, p.x - 6.5f, p.y + 3.8f)
                shapeRenderer.triangle(p.x - 8f, p.y + 3f, p.x - 6.5f, p.y + 10.5f, p.x - 1.5f, p.y + 4f)
                shapeRenderer.triangle(p.x - 3f, p.y + 4f, p.x, p.y + 11.8f, p.x + 4.5f, p.y + 4f)
                shapeRenderer.circle(p.x + 8f, p.y + 2.8f, 5.8f)
                shapeRenderer.triangle(p.x + 5f, p.y + 6.5f, p.x + 7.5f, p.y + 12f, p.x + 10.5f, p.y + 6.5f)

                // Nanh trắng & Mắt đỏ rực
                shapeRenderer.color = Color.WHITE
                shapeRenderer.triangle(p.x + 10.5f, p.y - 0.5f, p.x + 11.5f, p.y - 2.8f, p.x + 12.8f, p.y - 0.5f)
                shapeRenderer.color = Color(0.95f, 0.20f, 0.12f, 1f)
                shapeRenderer.circle(p.x + 10.2f, p.y + 3.2f, 1.6f)
            }

            // =================================================================
            // 20. TROLL_CHAMPION (Hàng 1 Cột 4 trong ảnh mẫu):
            // Troll chiến binh cơ bắp xanh lam, bờm tóc đỏ sẫm dài, mặc giáp đai đen, cầm 2 rìu phóng
            // =================================================================
            EnemyType.TROLL_CHAMPION -> {
                shapeRenderer.color = SHADOW
                shapeRenderer.ellipse(p.x - 14f, p.y - 14f, 28f, 7.5f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 8f, p.y - 13f, 6f, 6f)
                shapeRenderer.rect(p.x + 2f, p.y - 13f, 6f, 6f)
                shapeRenderer.circle(p.x, p.y - 1f, 12.5f)
                shapeRenderer.rect(p.x - 10.5f, p.y - 9.5f, 21f, 13f)
                shapeRenderer.triangle(p.x - 7f, p.y + 8f, p.x - 16f, p.y + 11.5f, p.x - 6f, p.y + 3.5f)
                shapeRenderer.triangle(p.x + 7f, p.y + 8f, p.x + 16f, p.y + 11.5f, p.x + 6f, p.y + 3.5f)
                shapeRenderer.circle(p.x, p.y + 7f, 9.8f)
                // Bờm tóc đỏ sẫm vuốt ngược
                shapeRenderer.triangle(p.x - 7.5f, p.y + 11f, p.x - 12f, p.y + 19f, p.x, p.y + 14f)
                shapeRenderer.triangle(p.x - 5f, p.y + 13f, p.x - 2f, p.y + 22f, p.x + 5f, p.y + 13f)

                // Thân cơ bắp xanh lam & Đai giáp đen chéo ngực
                shapeRenderer.color = Color(0.26f, 0.56f, 0.66f, 1f)
                shapeRenderer.circle(p.x, p.y - 1f, 10.5f)
                shapeRenderer.rect(p.x - 8.5f, p.y - 8f, 17f, 11f)
                shapeRenderer.color = Color(0.20f, 0.22f, 0.25f, 1f)
                shapeRenderer.rect(p.x - 8.5f, p.y - 8.5f, 17f, 4.5f)
                shapeRenderer.rectLine(p.x - 7.5f, p.y + 4f, p.x + 7.5f, p.y - 5f, 3.5f)

                // Đầu, tai & bờm tóc đỏ sẫm
                shapeRenderer.color = Color(0.34f, 0.66f, 0.76f, 1f)
                shapeRenderer.triangle(p.x - 6f, p.y + 7.5f, p.x - 14f, p.y + 10.5f, p.x - 6f, p.y + 4.5f)
                shapeRenderer.triangle(p.x + 6f, p.y + 7.5f, p.x + 14f, p.y + 10.5f, p.x + 6f, p.y + 4.5f)
                shapeRenderer.circle(p.x, p.y + 7f, 8f)

                shapeRenderer.color = Color(0.78f, 0.16f, 0.16f, 1f)
                shapeRenderer.triangle(p.x - 6.5f, p.y + 11f, p.x - 10.5f, p.y + 17.5f, p.x - 0.5f, p.y + 13.5f)
                shapeRenderer.triangle(p.x - 4f, p.y + 12.5f, p.x - 1.5f, p.y + 20f, p.x + 4f, p.y + 12.5f)

                // Răng nanh trắng & Mắt vàng
                shapeRenderer.color = Color.WHITE
                shapeRenderer.triangle(p.x - 5f, p.y + 3.5f, p.x - 3.5f, p.y + 8f, p.x - 2f, p.y + 3.5f)
                shapeRenderer.triangle(p.x + 2f, p.y + 3.5f, p.x + 3.5f, p.y + 8f, p.x + 5f, p.y + 3.5f)
                shapeRenderer.color = Color(1f, 0.88f, 0.18f, 1f)
                shapeRenderer.circle(p.x - 2.6f, p.y + 8.5f, 1.5f)
                shapeRenderer.circle(p.x + 2.6f, p.y + 8.5f, 1.5f)

                // Rìu chiến cầm trước ngực
                shapeRenderer.color = OUTLINE
                shapeRenderer.rectLine(p.x - 9f, p.y - 4f, p.x + 9f, p.y - 2f, 4.5f)
                shapeRenderer.triangle(p.x - 9f, p.y + 2f, p.x - 9f, p.y - 8f, p.x - 2f, p.y - 3f)
                shapeRenderer.color = Color(0.78f, 0.82f, 0.86f, 1f)
                shapeRenderer.triangle(p.x - 8f, p.y + 0.8f, p.x - 8f, p.y - 6.8f, p.x - 3f, p.y - 3f)
            }

            // =================================================================
            // 21. YETI (Hàng 2 Cột 2 trong ảnh mẫu):
            // Vượn tuyết khổng lồ lông trắng dày khom lưng, 2 tay dài chạm đất, mặt & ngực xám tro
            // =================================================================
            EnemyType.YETI -> {
                shapeRenderer.color = SHADOW
                shapeRenderer.ellipse(p.x - 20f, p.y - 17f, 40f, 10f)

                // Viền đen thân hình đồ sộ và 2 cánh tay khổng lồ chống đất
                shapeRenderer.color = OUTLINE
                shapeRenderer.circle(p.x, p.y + 2f, 18.5f)
                shapeRenderer.circle(p.x - 14f, p.y - 3f, 10.5f)
                shapeRenderer.circle(p.x + 14f, p.y - 3f, 10.5f)
                shapeRenderer.rect(p.x - 20f, p.y - 14f, 11f, 12f)
                shapeRenderer.rect(p.x + 9f, p.y - 14f, 11f, 12f)
                shapeRenderer.rect(p.x - 10f, p.y - 15f, 8f, 8f)
                shapeRenderer.rect(p.x + 2f, p.y - 15f, 8f, 8f)
                shapeRenderer.circle(p.x, p.y + 11.5f, 11.5f)

                // Bàn chân & Bàn tay xám đá
                shapeRenderer.color = Color(0.58f, 0.62f, 0.66f, 1f)
                shapeRenderer.rect(p.x - 18.5f, p.y - 12.5f, 8f, 6f)
                shapeRenderer.rect(p.x + 10.5f, p.y - 12.5f, 8f, 6f)
                shapeRenderer.rect(p.x - 8.5f, p.y - 13.5f, 5.5f, 5f)
                shapeRenderer.rect(p.x + 3f, p.y - 13.5f, 5.5f, 5f)

                // Bộ lông trắng xù khổng lồ có lớp bóng xám băng
                shapeRenderer.color = Color(0.80f, 0.86f, 0.90f, 1f)
                shapeRenderer.circle(p.x, p.y + 1f, 16.5f)
                shapeRenderer.circle(p.x - 13.5f, p.y - 2f, 8.8f)
                shapeRenderer.circle(p.x + 13.5f, p.y - 2f, 8.8f)

                shapeRenderer.color = Color(0.95f, 0.97f, 1f, 1f)
                shapeRenderer.circle(p.x, p.y + 3f, 15f)
                shapeRenderer.circle(p.x - 13f, p.y, 7.5f)
                shapeRenderer.circle(p.x + 13f, p.y, 7.5f)
                shapeRenderer.circle(p.x, p.y + 11.5f, 9.8f)

                // Khuôn mặt & Ngực xám tro của Yeti (chuẩn ảnh mẫu)
                shapeRenderer.color = Color(0.55f, 0.60f, 0.65f, 1f)
                shapeRenderer.ellipse(p.x - 7f, p.y - 6f, 14f, 10f)
                shapeRenderer.circle(p.x, p.y + 9.5f, 6.2f)

                // Mắt tối sâu & Miệng rộng
                shapeRenderer.color = OUTLINE
                shapeRenderer.circle(p.x - 2.6f, p.y + 11f, 1.5f)
                shapeRenderer.circle(p.x + 2.6f, p.y + 11f, 1.5f)
                shapeRenderer.rectLine(p.x - 3.8f, p.y + 6.8f, p.x + 3.8f, p.y + 6.8f, 2f)
            }

            // =================================================================
            // 22. TROLL_CHIEFTAIN (Hàng 2 Cột 1 trong ảnh mẫu):
            // Thủ lĩnh Troll đeo mặt nạ sọ xương trắng, bờm đỏ trên đỉnh, đeo 2 trống trận gỗ bên hông
            // =================================================================
            EnemyType.TROLL_CHIEFTAIN -> {
                shapeRenderer.color = SHADOW
                shapeRenderer.ellipse(p.x - 16f, p.y - 15f, 32f, 8.5f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 8.5f, p.y - 13.5f, 6.5f, 6f)
                shapeRenderer.rect(p.x + 2f, p.y - 13.5f, 6.5f, 6f)
                shapeRenderer.circle(p.x, p.y, 14.5f)
                shapeRenderer.circle(p.x, p.y + 9.5f, 10.5f)
                // Sừng sọ & Bờm tóc đỏ trên đỉnh sọ
                shapeRenderer.triangle(p.x - 8f, p.y + 10f, p.x - 18f, p.y + 19f, p.x - 4f, p.y + 14f)
                shapeRenderer.triangle(p.x + 8f, p.y + 10f, p.x + 18f, p.y + 19f, p.x + 4f, p.y + 14f)
                shapeRenderer.triangle(p.x - 5f, p.y + 15f, p.x, p.y + 24f, p.x + 5f, p.y + 15f)
                // Viền 2 chiếc trống trận đeo trước bụng/hông
                shapeRenderer.circle(p.x - 9.5f, p.y - 3.5f, 7.5f)
                shapeRenderer.circle(p.x + 9.5f, p.y - 3.5f, 7.5f)

                // Thân da xanh lam
                shapeRenderer.color = Color(0.28f, 0.56f, 0.66f, 1f)
                shapeRenderer.circle(p.x, p.y, 12.5f)
                shapeRenderer.rect(p.x - 7f, p.y - 12f, 5f, 6f)
                shapeRenderer.rect(p.x + 2f, p.y - 12f, 5f, 6f)

                // 2 Chiếc trống trận bằng gỗ nâu mặt da vàng kem (chuẩn ảnh mẫu)
                for (dx in listOf(-9.5f, 9.5f)) {
                    shapeRenderer.color = Color(0.55f, 0.34f, 0.14f, 1f)
                    shapeRenderer.circle(p.x + dx, p.y - 3.5f, 6f)
                    shapeRenderer.color = Color(0.92f, 0.84f, 0.62f, 1f)
                    shapeRenderer.circle(p.x + dx, p.y - 2.5f, 4.2f)
                }

                // Bờm tóc đỏ trên đỉnh đầu
                shapeRenderer.color = Color(0.86f, 0.20f, 0.14f, 1f)
                shapeRenderer.triangle(p.x - 4f, p.y + 15f, p.x, p.y + 22f, p.x + 4f, p.y + 15f)

                // Mặt nạ sọ xương trắng ngà & Cặp sừng
                shapeRenderer.color = Color(0.92f, 0.88f, 0.76f, 1f)
                shapeRenderer.circle(p.x, p.y + 9.5f, 8.8f)
                shapeRenderer.triangle(p.x - 7.5f, p.y + 10.5f, p.x - 16f, p.y + 17.5f, p.x - 4f, p.y + 13.5f)
                shapeRenderer.triangle(p.x + 7.5f, p.y + 10.5f, p.x + 16f, p.y + 17.5f, p.x + 4f, p.y + 13.5f)

                // Hốc mắt đen có đốm sáng đỏ
                shapeRenderer.color = OUTLINE
                shapeRenderer.circle(p.x - 3.2f, p.y + 9.5f, 2.2f)
                shapeRenderer.circle(p.x + 3.2f, p.y + 9.5f, 2.2f)
                shapeRenderer.color = Color(0.95f, 0.25f, 0.15f, 1f)
                shapeRenderer.circle(p.x - 3.2f, p.y + 9.5f, 0.9f)
                shapeRenderer.circle(p.x + 3.2f, p.y + 9.5f, 0.9f)
            }

            // =================================================================
            // 23. ROCKET_RIDER (Hàng 2 Cột 3 trong ảnh mẫu):
            // Kị sĩ mặc giáp sắt, mũ trụ có kính ngắm cam, đeo tên lửa lớn phun lửa cam-vàng phía sau
            // =================================================================
            EnemyType.ROCKET_RIDER -> {
                shapeRenderer.color = SHADOW
                shapeRenderer.ellipse(p.x - 12f, p.y - 15f, 24f, 6f)

                // Luồng lửa phản lực cam-vàng phụt ra phía sau tên lửa
                shapeRenderer.color = OUTLINE
                shapeRenderer.triangle(p.x - 8f, p.y + 8f, p.x - 15f, p.y - 3f, p.x - 24f, p.y - 6f)
                shapeRenderer.color = Color(0.98f, 0.42f, 0.08f, 1f)
                shapeRenderer.triangle(p.x - 8f, p.y + 6.5f, p.x - 14f, p.y - 2.5f, p.x - 22f, p.y - 5f)
                shapeRenderer.color = Color(1f, 0.88f, 0.20f, 1f)
                shapeRenderer.triangle(p.x - 9f, p.y + 4.5f, p.x - 13f, p.y - 1.5f, p.x - 18f, p.y - 3.5f)

                // Thân tên lửa sắt xám bạc đặt chéo trên lưng
                shapeRenderer.color = OUTLINE
                shapeRenderer.rectLine(p.x - 12f, p.y + 1f, p.x + 8f, p.y + 11f, 13f)
                shapeRenderer.color = Color(0.55f, 0.58f, 0.64f, 1f)
                shapeRenderer.rectLine(p.x - 11f, p.y + 1.5f, p.x + 7f, p.y + 10.5f, 9.5f)
                shapeRenderer.color = Color(0.78f, 0.82f, 0.88f, 1f)
                shapeRenderer.rectLine(p.x - 10f, p.y + 3f, p.x + 6f, p.y + 11f, 4f)

                // Thân giáp sắt và Mũ trụ của Rocket Rider
                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 6f, p.y - 9f, 14f, 12f)
                shapeRenderer.circle(p.x + 3f, p.y + 3f, 8.8f)

                shapeRenderer.color = Color(0.45f, 0.48f, 0.52f, 1f)
                shapeRenderer.rect(p.x - 4.5f, p.y - 7.5f, 11f, 9.5f)
                shapeRenderer.color = Color(0.65f, 0.68f, 0.74f, 1f)
                shapeRenderer.circle(p.x + 3f, p.y + 3f, 7.2f)

                // Khe kính bảo hộ màu cam sáng trên mũ trụ (chuẩn ảnh mẫu)
                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 0.5f, p.y + 1.5f, 8.5f, 4.5f)
                shapeRenderer.color = Color(0.98f, 0.52f, 0.12f, 1f)
                shapeRenderer.rect(p.x + 0.5f, p.y + 2.3f, 6.5f, 2.8f)
            }

            // =================================================================
            // 24. DARK_SLAYER (Hàng 2 Cột 4 trong ảnh mẫu):
            // Kị sĩ giáp đen hạng nặng, mũ trụ 2 sừng lớn, mắt đỏ, cầm đại kiếm răng cưa ánh cam ngang bụng
            // =================================================================
            EnemyType.DARK_SLAYER -> {
                shapeRenderer.color = SHADOW
                shapeRenderer.ellipse(p.x - 15f, p.y - 15f, 30f, 8f)

                // Viền đen tổng thể
                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 8.5f, p.y - 14f, 6.5f, 6f)
                shapeRenderer.rect(p.x + 2f, p.y - 14f, 6.5f, 6f)
                shapeRenderer.rect(p.x - 11f, p.y - 10f, 22f, 14f)
                shapeRenderer.circle(p.x, p.y + 7f, 9.8f)
                // Cặp sừng trâu lớn vểnh lên
                shapeRenderer.triangle(p.x - 7f, p.y + 9f, p.x - 16f, p.y + 20f, p.x - 2.5f, p.y + 14f)
                shapeRenderer.triangle(p.x + 7f, p.y + 9f, p.x + 16f, p.y + 20f, p.x + 2.5f, p.y + 14f)

                // Giáp thép đen xám & Sừng xám sáng
                shapeRenderer.color = Color(0.22f, 0.23f, 0.26f, 1f)
                shapeRenderer.rect(p.x - 7f, p.y - 12.5f, 4.5f, 5f)
                shapeRenderer.rect(p.x + 2.5f, p.y - 12.5f, 4.5f, 5f)
                shapeRenderer.rect(p.x - 9f, p.y - 8.5f, 18f, 11.5f)
                shapeRenderer.color = Color(0.34f, 0.36f, 0.40f, 1f)
                shapeRenderer.circle(p.x, p.y + 7f, 8f)

                shapeRenderer.color = Color(0.68f, 0.66f, 0.62f, 1f)
                shapeRenderer.triangle(p.x - 6.5f, p.y + 9.5f, p.x - 14f, p.y + 18.2f, p.x - 3f, p.y + 13.5f)
                shapeRenderer.triangle(p.x + 6.5f, p.y + 9.5f, p.x + 14f, p.y + 18.2f, p.x + 3f, p.y + 13.5f)

                // Khe mũ chữ T đen & Đôi mắt đỏ rực
                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 5.5f, p.y + 5.5f, 11f, 2.8f)
                shapeRenderer.rect(p.x - 1.4f, p.y + 2f, 2.8f, 5.5f)
                shapeRenderer.color = Color.RED
                shapeRenderer.circle(p.x - 2.6f, p.y + 6.8f, 1.3f)
                shapeRenderer.circle(p.x + 2.6f, p.y + 6.8f, 1.3f)

                // Thanh đại kiếm răng cưa phát sáng cam-trắng cầm ngang bụng (chuẩn ảnh mẫu)
                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 9f, p.y - 6.5f, 19f, 5.8f)
                shapeRenderer.triangle(p.x + 10f, p.y - 0.7f, p.x + 10f, p.y - 6.5f, p.x + 15.5f, p.y - 3.6f)
                shapeRenderer.color = Color(0.96f, 0.48f, 0.14f, 1f)
                shapeRenderer.rect(p.x - 7.5f, p.y - 5.2f, 17f, 3.4f)
                shapeRenderer.color = Color(1f, 0.90f, 0.75f, 1f)
                shapeRenderer.rect(p.x - 5f, p.y - 4.2f, 13f, 1.6f)
            }

            // =================================================================
            // 25. JT: BOSS ĐẠI QUÁI TUYẾT J.T. (CHUẨN WIKI KINGDOM RUSH)
            // Dáng khom lưng khổng lồ, bờm lông trắng nhọn, 2 cánh tay to chống nắm đấm đá,
            // mặt đá xám vuông góc cạnh, mắt vàng xếch và miệng há rộng nhe răng vàng
            // =================================================================
            EnemyType.JT -> {
                // 1. Bóng đổ khổng lồ dưới mặt đất
                shapeRenderer.color = SHADOW
                shapeRenderer.ellipse(p.x - 28f, p.y - 24f, 56f, 15f)

                val furShadow = Color(0.75f, 0.80f, 0.82f, 1f)
                val furMain = Color(0.92f, 0.95f, 0.94f, 1f)
                val skinStone = Color(0.46f, 0.50f, 0.53f, 1f)
                val skinStoneDark = Color(0.34f, 0.38f, 0.40f, 1f)

                // 2. CHÂN SAU & THÂN LƯNG GỒ KHỔNG LỒ (Lớp viền đen)
                shapeRenderer.color = OUTLINE
                // Chân trụ phía sau
                shapeRenderer.rect(p.x + 6f, p.y - 21f, 16f, 16f)
                shapeRenderer.ellipse(p.x + 4f, p.y - 23f, 20f, 9f)
                // Khối lưng và bả vai gồ cao kiểu Gorilla khổng lồ
                shapeRenderer.circle(p.x + 5f, p.y + 4f, 23f)
                shapeRenderer.circle(p.x - 6f, p.y + 6f, 20f)
                shapeRenderer.ellipse(p.x - 20f, p.y - 14f, 46f, 32f)

                // Cánh tay trái (phía trước) & Cánh tay phải (vòng qua ngực)
                shapeRenderer.circle(p.x - 18f, p.y - 3f, 13f)
                shapeRenderer.circle(p.x + 14f, p.y - 4f, 14f)
                shapeRenderer.rect(p.x - 25f, p.y - 18f, 16f, 16f)
                shapeRenderer.rect(p.x - 10f, p.y - 20f, 24f, 15f)

                // Gai lông nhọn trên đỉnh đầu và vai
                shapeRenderer.triangle(p.x - 14f, p.y + 20f, p.x - 4f, p.y + 20f, p.x - 11f, p.y + 29f)
                shapeRenderer.triangle(p.x - 8f, p.y + 21f, p.x + 2f, p.y + 21f, p.x - 3f, p.y + 31f)
                shapeRenderer.triangle(p.x - 1f, p.y + 20f, p.x + 8f, p.y + 20f, p.x + 4f, p.y + 29f)

                // 3. TÔ MÀU LÔNG TRẮNG XÁM CHO THÂN, LƯNG & BẮP TAY
                // Bàn chân đá phía sau
                shapeRenderer.color = skinStoneDark
                shapeRenderer.ellipse(p.x + 6f, p.y - 21.5f, 16f, 7f)

                // Lớp lông bóng râm (furShadow) tạo khối 3D
                shapeRenderer.color = furShadow
                shapeRenderer.circle(p.x + 5f, p.y + 3f, 20.5f)
                shapeRenderer.ellipse(p.x - 18f, p.y - 12f, 42f, 28f)
                shapeRenderer.circle(p.x - 17.5f, p.y - 3f, 10.8f)
                shapeRenderer.circle(p.x + 13.5f, p.y - 4f, 11.8f)

                // Lớp lông trắng sáng chủ đạo (furMain)
                shapeRenderer.color = furMain
                shapeRenderer.circle(p.x + 4f, p.y + 5f, 19f)
                shapeRenderer.circle(p.x - 6f, p.y + 6.5f, 17.5f)
                shapeRenderer.circle(p.x - 16.5f, p.y - 2f, 9.5f)
                shapeRenderer.circle(p.x + 12.5f, p.y - 3f, 10.5f)
                // Bờm lông nhọn trên đỉnh đầu
                shapeRenderer.triangle(p.x - 12.5f, p.y + 19f, p.x - 5f, p.y + 19f, p.x - 10f, p.y + 27f)
                shapeRenderer.triangle(p.x - 7f, p.y + 20f, p.x + 1f, p.y + 20f, p.x - 3f, p.y + 29f)
                shapeRenderer.triangle(p.x, p.y + 19f, p.x + 6.5f, p.y + 19f, p.x + 3.5f, p.y + 27f)

                // 4. HAI NẮM ĐẤM ĐÁ XÁM ĐEN KHỔNG LỒ CHỐNG TRƯỚC NGỰC (Chuẩn ảnh mẫu)
                // Nắm đấm tay phải (bên trái hình)
                shapeRenderer.color = OUTLINE
                shapeRenderer.circle(p.x - 18f, p.y - 11f, 9.5f)
                shapeRenderer.color = skinStoneDark
                shapeRenderer.circle(p.x - 18f, p.y - 11f, 7.8f)
                shapeRenderer.color = skinStone
                shapeRenderer.circle(p.x - 17f, p.y - 10f, 5.5f)

                // Cánh tay trái vắt ngang & Nắm đấm khổng lồ có lông cổ tay nhọn
                shapeRenderer.color = OUTLINE
                shapeRenderer.circle(p.x - 4f, p.y - 12f, 10.5f)
                shapeRenderer.rect(p.x - 5f, p.y - 19f, 20f, 14f)
                shapeRenderer.color = skinStoneDark
                shapeRenderer.circle(p.x - 4f, p.y - 12f, 8.8f)
                shapeRenderer.color = skinStone
                shapeRenderer.circle(p.x - 3.5f, p.y - 11.5f, 6.8f)

                // Lớp lông trắng xù phủ dọc cẳng tay trước có gai lông nhọn ở cổ tay
                shapeRenderer.color = furShadow
                shapeRenderer.rect(p.x - 1f, p.y - 17.5f, 15f, 11f)
                shapeRenderer.color = furMain
                shapeRenderer.rect(p.x, p.y - 16f, 14f, 9.5f)
                shapeRenderer.triangle(p.x + 1f, p.y - 6.5f, p.x + 1f, p.y - 16f, p.x - 5.5f, p.y - 11f)
                shapeRenderer.triangle(p.x + 2f, p.y - 6.5f, p.x + 2f, p.y - 12f, p.x - 3.5f, p.y - 8f)

                // 5. KHUÔN MẶT ĐÁ XÁM GÓC CẠNH DỮ TỢN CỦA J.T.
                val faceX = p.x - 5f
                val faceY = p.y + 8f

                // Viền đen khung mặt vuông vức
                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(faceX - 11.5f, faceY - 9.5f, 22f, 21f)
                shapeRenderer.circle(faceX - 0.5f, faceY + 7f, 11f)

                // Nền da mặt đá xám
                shapeRenderer.color = skinStone
                shapeRenderer.rect(faceX - 9.8f, faceY - 8f, 18.6f, 18f)
                shapeRenderer.circle(faceX - 0.5f, faceY + 6.5f, 9.2f)

                // Nếp nhăn trán & Gờ chân mày đá nhíu chặt
                shapeRenderer.color = skinStoneDark
                shapeRenderer.rect(faceX - 8.5f, faceY + 4.5f, 16f, 4.5f)
                shapeRenderer.color = OUTLINE
                shapeRenderer.rectLine(faceX - 9f, faceY + 7f, faceX - 1f, faceY + 4.2f, 2.4f)
                shapeRenderer.rectLine(faceX + 8f, faceY + 7f, faceX, faceY + 4.2f, 2.4f)
                shapeRenderer.rectLine(faceX - 5f, faceY + 9.5f, faceX + 4f, faceY + 9.5f, 1.6f)

                // Đôi mắt vàng rực xếch ngược hung tợn
                shapeRenderer.color = OUTLINE
                shapeRenderer.circle(faceX - 5f, faceY + 3.2f, 2.6f)
                shapeRenderer.circle(faceX + 4f, faceY + 3.2f, 2.6f)
                shapeRenderer.color = Color(1f, 0.88f, 0.12f, 1f)
                shapeRenderer.circle(faceX - 5f, faceY + 3.2f, 1.8f)
                shapeRenderer.circle(faceX + 4f, faceY + 3.2f, 1.8f)

                // 6. CÁI MIỆNG VUÔNG HÁ TO GẦM THÉT, LƯỠI HỒNG XÁM & RĂNG VÀNG XỈN
                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(faceX - 8.5f, faceY - 7.5f, 16f, 9.5f)
                // Hốc miệng tối thẫm
                shapeRenderer.color = Color(0.22f, 0.15f, 0.17f, 1f)
                shapeRenderer.rect(faceX - 7f, faceY - 6.2f, 13f, 7f)
                // Lưỡi bên trong miệng
                shapeRenderer.color = Color(0.58f, 0.42f, 0.45f, 1f)
                shapeRenderer.ellipse(faceX - 4.5f, faceY - 6f, 9f, 4.5f)

                // 4 Chiếc răng đá vàng xỉn ở 2 góc hàm trên và hàm dưới (giống hệt ảnh Wiki)
                val toothColor = Color(0.92f, 0.84f, 0.42f, 1f)
                shapeRenderer.color = toothColor
                // Răng nanh hàm trên
                shapeRenderer.rect(faceX - 6.8f, faceY - 1.8f, 2.8f, 2.6f)
                shapeRenderer.rect(faceX + 3.0f, faceY - 1.8f, 2.8f, 2.6f)
                // Răng nanh hàm dưới
                shapeRenderer.rect(faceX - 6.8f, faceY - 6.2f, 3.0f, 3.0f)
                shapeRenderer.rect(faceX + 2.8f, faceY - 6.2f, 3.0f, 3.0f)
            }

            // =================================================================
            // 26. SKELETON
            // Bộ xương trắng ngà có viền đen rõ nét, hốc mắt đen, xương sườn ngang, cầm kiếm cong Scimitar
            // =================================================================
            EnemyType.SKELETON -> {
                shapeRenderer.color = SHADOW
                shapeRenderer.ellipse(p.x - 10f, p.y - 12f, 20f, 6f)

                // Viền đen hộp sọ, xương sườn và chân xương
                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 5.5f, p.y - 11.5f, 4f, 5.5f)
                shapeRenderer.rect(p.x + 1.5f, p.y - 11.5f, 4f, 5.5f)
                shapeRenderer.rect(p.x - 6.5f, p.y - 7.5f, 13f, 10.5f)
                shapeRenderer.circle(p.x, p.y + 6f, 8.8f)

                // Xương trắng ngà (Ivory Bone)
                val boneColor = Color(0.94f, 0.90f, 0.78f, 1f)
                shapeRenderer.color = boneColor
                shapeRenderer.rect(p.x - 4.2f, p.y - 10.5f, 2f, 4f)
                shapeRenderer.rect(p.x + 2.2f, p.y - 10.5f, 2f, 4f)
                // Cột sống & 2 dải xương sườn ngang
                shapeRenderer.rect(p.x - 1.5f, p.y - 7f, 3f, 9f)
                shapeRenderer.rect(p.x - 5f, p.y - 1.5f, 10f, 2f)
                shapeRenderer.rect(p.x - 4.5f, p.y - 4.5f, 9f, 2f)

                // Hộp sọ lớn & Hốc mắt đen sâu
                shapeRenderer.circle(p.x, p.y + 6f, 7.2f)
                shapeRenderer.rect(p.x - 4f, p.y + 0.5f, 8f, 3.5f)
                shapeRenderer.color = OUTLINE
                shapeRenderer.circle(p.x - 2.6f, p.y + 5.8f, 2.0f)
                shapeRenderer.circle(p.x + 2.6f, p.y + 5.8f, 2.0f)
                shapeRenderer.rectLine(p.x - 5.5f, p.y + 8f, p.x - 1f, p.y + 7f, 1.5f)
                shapeRenderer.rectLine(p.x + 5.5f, p.y + 8f, p.x + 1f, p.y + 7f, 1.5f)

                // Thanh kiếm cong Scimitar bản rộng cầm ngang trước bụng (chuẩn ảnh mẫu)
                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 7f, p.y - 6.5f, 16f, 5f)
                shapeRenderer.triangle(p.x + 9f, p.y - 1.5f, p.x + 9f, p.y - 6.5f, p.x + 14.5f, p.y - 0.5f)
                shapeRenderer.color = Color(0.85f, 0.65f, 0.20f, 1f)
                shapeRenderer.rect(p.x - 5.8f, p.y - 5.2f, 3f, 2.6f)
                shapeRenderer.color = Color.WHITE
                shapeRenderer.rect(p.x - 2.8f, p.y - 5.2f, 11.5f, 2.6f)
                shapeRenderer.triangle(p.x + 8.7f, p.y - 2.6f, p.x + 8.7f, p.y - 5.2f, p.x + 13f, p.y - 1.5f)
            }

            // =================================================================
            // 27. SKELETON_KNIGHT
            // Chiến binh xương đội mũ sắt chóp nhọn có thanh che mũi, đeo khiên tròn đen viền bạc, cầm kiếm cong
            // =================================================================
            EnemyType.SKELETON_KNIGHT -> {
                shapeRenderer.color = SHADOW
                shapeRenderer.ellipse(p.x - 11f, p.y - 12.5f, 22f, 6.5f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 6f, p.y - 11.5f, 4.5f, 5.5f)
                shapeRenderer.rect(p.x + 1.5f, p.y - 11.5f, 4.5f, 5.5f)
                shapeRenderer.rect(p.x - 7.5f, p.y - 8f, 15f, 11f)
                shapeRenderer.circle(p.x, p.y + 6f, 9.2f)
                shapeRenderer.triangle(p.x - 3f, p.y + 13.5f, p.x + 3f, p.y + 13.5f, p.x, p.y + 18.5f)

                // Xương trắng ngà
                val boneColor = Color(0.94f, 0.90f, 0.78f, 1f)
                shapeRenderer.color = boneColor
                shapeRenderer.rect(p.x - 4.5f, p.y - 10.5f, 2.2f, 4f)
                shapeRenderer.rect(p.x + 2.3f, p.y - 10.5f, 2.2f, 4f)
                shapeRenderer.rect(p.x - 5f, p.y - 2f, 10f, 2f)
                shapeRenderer.rect(p.x - 4.5f, p.y - 5f, 9f, 2f)
                shapeRenderer.circle(p.x, p.y + 4.8f, 7.2f)

                // Hốc mắt đen
                shapeRenderer.color = OUTLINE
                shapeRenderer.circle(p.x - 2.8f, p.y + 4.8f, 2.0f)
                shapeRenderer.circle(p.x + 2.8f, p.y + 4.8f, 2.0f)

                // Mũ sắt chóp nhọn có thanh che mũi dọc
                shapeRenderer.color = OUTLINE
                shapeRenderer.arc(p.x, p.y + 6f, 9.2f, 0f, 180f)
                shapeRenderer.rect(p.x - 9f, p.y + 5.8f, 18f, 2f)
                shapeRenderer.color = Color(0.58f, 0.62f, 0.68f, 1f)
                shapeRenderer.arc(p.x, p.y + 6.5f, 7.6f, 0f, 180f)
                shapeRenderer.triangle(p.x - 2f, p.y + 13.5f, p.x + 2f, p.y + 13.5f, p.x, p.y + 17f)
                shapeRenderer.rect(p.x - 1.2f, p.y + 2.5f, 2.4f, 5f)

                // Khiên tròn đen viền bạc bên hông phải
                shapeRenderer.color = OUTLINE
                shapeRenderer.circle(p.x + 6.5f, p.y - 3f, 6.8f)
                shapeRenderer.color = Color(0.75f, 0.78f, 0.82f, 1f)
                shapeRenderer.circle(p.x + 6.5f, p.y - 3f, 5.4f)
                shapeRenderer.color = Color(0.25f, 0.26f, 0.28f, 1f)
                shapeRenderer.circle(p.x + 6.5f, p.y - 3f, 3.8f)

                // Kiếm cong Scimitar trắng cầm ngang
                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 8f, p.y - 6.5f, 15f, 4.8f)
                shapeRenderer.color = Color.WHITE
                shapeRenderer.rect(p.x - 5.5f, p.y - 5.3f, 11.5f, 2.5f)
            }

            // =================================================================
            // 28. DEMON_SPAWN
            // Quỷ đỏ cam có 2 sừng trắng ngắn, mắt vàng giận dữ, cầm đoản đao bạc ngang bụng
            // =================================================================
            EnemyType.DEMON_SPAWN -> {
                shapeRenderer.color = SHADOW
                shapeRenderer.ellipse(p.x - 10f, p.y - 12f, 20f, 6f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 6f, p.y - 11f, 4.5f, 5f)
                shapeRenderer.rect(p.x + 1.5f, p.y - 11f, 4.5f, 5f)
                shapeRenderer.rect(p.x - 7.5f, p.y - 8f, 15f, 11.5f)
                shapeRenderer.circle(p.x, p.y + 4.5f, 8.8f)
                // 2 Sừng trắng ngắn trên đầu
                shapeRenderer.triangle(p.x - 6f, p.y + 8f, p.x - 10.5f, p.y + 15.5f, p.x - 2f, p.y + 11f)
                shapeRenderer.triangle(p.x + 6f, p.y + 8f, p.x + 10.5f, p.y + 15.5f, p.x + 2f, p.y + 11f)

                // Da quỷ đỏ cam rực
                shapeRenderer.color = Color(0.76f, 0.22f, 0.08f, 1f)
                shapeRenderer.rect(p.x - 6f, p.y - 7f, 12f, 9f)
                shapeRenderer.color = Color(0.88f, 0.32f, 0.10f, 1f)
                shapeRenderer.circle(p.x, p.y + 4.5f, 7.2f)

                // Sừng trắng ngà & Mắt vàng sáng
                shapeRenderer.color = Color(0.96f, 0.92f, 0.80f, 1f)
                shapeRenderer.triangle(p.x - 5.5f, p.y + 8.5f, p.x - 9.2f, p.y + 14.2f, p.x - 2.5f, p.y + 10.8f)
                shapeRenderer.triangle(p.x + 5.5f, p.y + 8.5f, p.x + 9.2f, p.y + 14.2f, p.x + 2.5f, p.y + 10.8f)

                shapeRenderer.color = Color(1f, 0.92f, 0.20f, 1f)
                shapeRenderer.circle(p.x - 2.6f, p.y + 5f, 1.7f)
                shapeRenderer.circle(p.x + 2.6f, p.y + 5f, 1.7f)
                shapeRenderer.color = OUTLINE
                shapeRenderer.rectLine(p.x - 5.5f, p.y + 7.2f, p.x - 1f, p.y + 5.8f, 1.6f)
                shapeRenderer.rectLine(p.x + 5.5f, p.y + 7.2f, p.x + 1f, p.y + 5.8f, 1.6f)

                // Đoản đao bạc cầm ngang trước bụng (chuẩn ảnh mẫu)
                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 6f, p.y - 5.5f, 13f, 4.5f)
                shapeRenderer.color = Color(0.88f, 0.92f, 0.96f, 1f)
                shapeRenderer.rect(p.x - 4.5f, p.y - 4.3f, 10f, 2.3f)
            }

            // =================================================================
            // 29. DEMON_HOUND
            // Chó săn địa ngục 4 chân màu đỏ cam, lưng gai nhọn, cặp sừng trắng lớn chúc về phía trước
            // =================================================================
            EnemyType.DEMON_HOUND -> {
                shapeRenderer.color = SHADOW
                shapeRenderer.ellipse(p.x - 14f, p.y - 11f, 28f, 6.5f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 10.5f, p.y - 9.5f, 4f, 6.5f)
                shapeRenderer.rect(p.x - 5f, p.y - 9.5f, 4f, 6.5f)
                shapeRenderer.rect(p.x + 2f, p.y - 9.5f, 4f, 6.5f)
                shapeRenderer.rect(p.x + 7f, p.y - 9.5f, 4f, 6.5f)
                shapeRenderer.ellipse(p.x - 13f, p.y - 6f, 24f, 13f)
                shapeRenderer.triangle(p.x - 9f, p.y + 3f, p.x - 6f, p.y + 11f, p.x - 2f, p.y + 3f)
                shapeRenderer.triangle(p.x - 4f, p.y + 4f, p.x, p.y + 12f, p.x + 4f, p.y + 4f)
                shapeRenderer.circle(p.x + 8f, p.y + 1.5f, 7.5f)
                // Sừng trắng lớn trên đầu chó săn địa ngục
                shapeRenderer.triangle(p.x + 5f, p.y + 6f, p.x + 15f, p.y + 11f, p.x + 9f, p.y + 2f)

                // Thân đỏ cam sẫm & Gai lưng
                shapeRenderer.color = Color(0.78f, 0.26f, 0.08f, 1f)
                shapeRenderer.rect(p.x - 9.3f, p.y - 8f, 2.2f, 5f)
                shapeRenderer.rect(p.x + 3.2f, p.y - 8f, 2.2f, 5f)
                shapeRenderer.ellipse(p.x - 11.5f, p.y - 4.5f, 21f, 10f)
                shapeRenderer.color = Color(0.90f, 0.36f, 0.12f, 1f)
                shapeRenderer.triangle(p.x - 8f, p.y + 3f, p.x - 6f, p.y + 9.5f, p.x - 3f, p.y + 3f)
                shapeRenderer.triangle(p.x - 3f, p.y + 4f, p.x, p.y + 10.5f, p.x + 3f, p.y + 4f)
                shapeRenderer.circle(p.x + 8f, p.y + 1.5f, 5.8f)

                // Sừng trắng ngà & Mắt vàng sáng
                shapeRenderer.color = Color(0.96f, 0.92f, 0.78f, 1f)
                shapeRenderer.triangle(p.x + 5.5f, p.y + 5.5f, p.x + 13.5f, p.y + 9.8f, p.x + 8.8f, p.y + 2.5f)
                shapeRenderer.color = Color(1f, 0.92f, 0.20f, 1f)
                shapeRenderer.circle(p.x + 10f, p.y + 2f, 1.6f)
            }

            // =================================================================
            // 30. DEMON_LORD
            // Chúa quỷ đỏ cam vạm vỡ, sừng trắng lớn, đeo cầu vai vàng, đai vàng và huy chương vàng trước ngực
            // =================================================================
            EnemyType.DEMON_LORD -> {
                shapeRenderer.color = SHADOW
                shapeRenderer.ellipse(p.x - 15f, p.y - 15f, 30f, 8f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 8.5f, p.y - 14f, 6.5f, 6f)
                shapeRenderer.rect(p.x + 2f, p.y - 14f, 6.5f, 6f)
                shapeRenderer.circle(p.x, p.y - 1f, 13.5f)
                shapeRenderer.rect(p.x - 11.5f, p.y - 10f, 23f, 14f)
                shapeRenderer.circle(p.x - 11.5f, p.y + 2f, 6.5f)
                shapeRenderer.circle(p.x + 11.5f, p.y + 2f, 6.5f)
                shapeRenderer.circle(p.x, p.y + 8f, 9.8f)
                // Cặp sừng trắng lớn trên đỉnh đầu
                shapeRenderer.triangle(p.x - 7f, p.y + 11f, p.x - 15f, p.y + 21f, p.x - 2f, p.y + 14.5f)
                shapeRenderer.triangle(p.x + 7f, p.y + 11f, p.x + 15f, p.y + 21f, p.x + 2f, p.y + 14.5f)

                // Thân cơ bắp đỏ cam & Quần sẫm
                shapeRenderer.color = Color(0.45f, 0.16f, 0.10f, 1f)
                shapeRenderer.rect(p.x - 9.5f, p.y - 9f, 19f, 5f)
                shapeRenderer.color = Color(0.86f, 0.28f, 0.08f, 1f)
                shapeRenderer.rect(p.x - 9.5f, p.y - 4f, 19f, 8f)
                shapeRenderer.circle(p.x, p.y + 8f, 8f)

                // Giáp vai vàng cam, đai ngực và Huy chương vàng giữa ngực (chuẩn ảnh mẫu)
                shapeRenderer.color = Color(0.96f, 0.68f, 0.14f, 1f)
                shapeRenderer.circle(p.x - 11f, p.y + 2f, 4.8f)
                shapeRenderer.circle(p.x + 11f, p.y + 2f, 4.8f)
                shapeRenderer.rect(p.x - 9.5f, p.y - 4.5f, 19f, 2.2f)
                shapeRenderer.circle(p.x, p.y - 0.5f, 3.2f)

                // Sừng trắng ngà & Mắt vàng dữ tợn
                shapeRenderer.color = Color(0.96f, 0.92f, 0.80f, 1f)
                shapeRenderer.triangle(p.x - 6.5f, p.y + 11.5f, p.x - 13.5f, p.y + 19.5f, p.x - 2.5f, p.y + 14f)
                shapeRenderer.triangle(p.x + 6.5f, p.y + 11.5f, p.x + 13.5f, p.y + 19.5f, p.x + 2.5f, p.y + 14f)

                shapeRenderer.color = Color(1f, 0.92f, 0.20f, 1f)
                shapeRenderer.circle(p.x - 2.8f, p.y + 8.5f, 1.8f)
                shapeRenderer.circle(p.x + 2.8f, p.y + 8.5f, 1.8f)
                shapeRenderer.color = OUTLINE
                shapeRenderer.rectLine(p.x - 6f, p.y + 11f, p.x - 1f, p.y + 9.2f, 1.8f)
                shapeRenderer.rectLine(p.x + 6f, p.y + 11f, p.x + 1f, p.y + 9.2f, 1.8f)
            }

            // =================================================================
            // 31. DEMON_IMP
            // Tiểu quỷ bay màu đỏ cam, xòe đôi cánh dơi lớn có xương cánh sáng, sừng trắng, chân vuốt
            // =================================================================
            EnemyType.DEMON_IMP -> {
                shapeRenderer.color = SHADOW
                shapeRenderer.ellipse(p.x - 10f, p.y - 14f, 20f, 6f)

                // Viền đen đôi cánh dơi xòe rộng, thân, đầu và sừng
                shapeRenderer.color = OUTLINE
                shapeRenderer.triangle(p.x - 2f, p.y + 3f, p.x - 21f, p.y + 16f, p.x - 12f, p.y - 4f)
                shapeRenderer.triangle(p.x + 2f, p.y + 3f, p.x + 21f, p.y + 16f, p.x + 12f, p.y - 4f)
                shapeRenderer.rectLine(p.x - 3f, p.y - 5f, p.x - 5f, p.y - 11f, 3.2f)
                shapeRenderer.rectLine(p.x + 3f, p.y - 5f, p.x + 5f, p.y - 11f, 3.2f)
                shapeRenderer.circle(p.x, p.y - 1f, 7.5f)
                shapeRenderer.circle(p.x, p.y + 5.5f, 7.2f)
                shapeRenderer.triangle(p.x - 5f, p.y + 8f, p.x - 9f, p.y + 15f, p.x - 1.5f, p.y + 10f)
                shapeRenderer.triangle(p.x + 5f, p.y + 8f, p.x + 9f, p.y + 15f, p.x + 1.5f, p.y + 10f)

                // Màng cánh dơi đỏ cam
                shapeRenderer.color = Color(0.76f, 0.24f, 0.08f, 1f)
                shapeRenderer.triangle(p.x - 2f, p.y + 2.5f, p.x - 19f, p.y + 14.5f, p.x - 11f, p.y - 2.5f)
                shapeRenderer.triangle(p.x + 2f, p.y + 2.5f, p.x + 19f, p.y + 14.5f, p.x + 11f, p.y - 2.5f)
                shapeRenderer.color = Color(0.95f, 0.46f, 0.14f, 1f)
                shapeRenderer.rectLine(p.x - 3f, p.y + 3f, p.x - 18f, p.y + 14f, 1.8f)
                shapeRenderer.rectLine(p.x + 3f, p.y + 3f, p.x + 18f, p.y + 14f, 1.8f)

                // Thân & Đầu tiểu quỷ
                shapeRenderer.color = Color(0.88f, 0.32f, 0.10f, 1f)
                shapeRenderer.circle(p.x, p.y - 1f, 5.8f)
                shapeRenderer.circle(p.x, p.y + 5.5f, 5.6f)

                // Sừng trắng & Mắt vàng
                shapeRenderer.color = Color(0.96f, 0.92f, 0.80f, 1f)
                shapeRenderer.triangle(p.x - 4.5f, p.y + 8.5f, p.x - 7.8f, p.y + 13.8f, p.x - 2f, p.y + 10f)
                shapeRenderer.triangle(p.x + 4.5f, p.y + 8.5f, p.x + 7.8f, p.y + 13.8f, p.x + 2f, p.y + 10f)
                shapeRenderer.color = Color(1f, 0.92f, 0.20f, 1f)
                shapeRenderer.circle(p.x - 2f, p.y + 5.8f, 1.4f)
                shapeRenderer.circle(p.x + 2f, p.y + 5.8f, 1.4f)
            }

            // =================================================================
            // 32. MAGMA_ELEMENTAL
            // Người đá núi lửa đen xám góc cạnh, các đường nứt dung nham cam-vàng rực cháy khắp người
            // =================================================================
            EnemyType.MAGMA_ELEMENTAL -> {
                shapeRenderer.color = SHADOW
                shapeRenderer.ellipse(p.x - 19f, p.y - 17f, 38f, 10f)

                // Viền đen khối đá góc cạnh
                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 13f, p.y - 15f, 10f, 9f)
                shapeRenderer.rect(p.x + 3f, p.y - 15f, 10f, 9f)
                shapeRenderer.circle(p.x, p.y + 1f, 18f)
                shapeRenderer.circle(p.x - 14f, p.y + 3f, 10f)
                shapeRenderer.circle(p.x + 14f, p.y + 3f, 10f)
                shapeRenderer.circle(p.x, p.y + 12f, 11f)

                // Lớp nền dung nham cam vàng rực cháy bên trong
                shapeRenderer.color = Color(0.98f, 0.45f, 0.06f, 1f)
                shapeRenderer.circle(p.x, p.y + 1f, 16f)
                shapeRenderer.circle(p.x - 13.5f, p.y + 3f, 8.2f)
                shapeRenderer.circle(p.x + 13.5f, p.y + 3f, 8.2f)
                shapeRenderer.circle(p.x, p.y + 12f, 9.2f)

                // Các tảng đá núi lửa đen xám xếp chồng lên nhau để lộ khe nứt dung nham (chuẩn ảnh mẫu)
                val rockDark = Color(0.24f, 0.22f, 0.22f, 1f)
                val rockMid = Color(0.34f, 0.31f, 0.30f, 1f)
                shapeRenderer.color = rockDark
                shapeRenderer.rect(p.x - 11.5f, p.y - 13.5f, 7f, 7f)
                shapeRenderer.rect(p.x + 4.5f, p.y - 13.5f, 7f, 7f)
                shapeRenderer.circle(p.x - 6f, p.y + 4f, 7.5f)
                shapeRenderer.circle(p.x + 6f, p.y + 4f, 7.5f)
                shapeRenderer.circle(p.x, p.y - 5f, 7f)
                shapeRenderer.circle(p.x - 14f, p.y + 2f, 6.8f)
                shapeRenderer.circle(p.x + 14f, p.y + 2f, 6.8f)
                shapeRenderer.circle(p.x, p.y + 12.5f, 7.8f)

                shapeRenderer.color = rockMid
                shapeRenderer.circle(p.x - 6f, p.y + 5f, 4.5f)
                shapeRenderer.circle(p.x + 5f, p.y + 5f, 4.5f)

                // Mạch dung nham vàng rực chạy giữa ngực & Đôi mắt lửa vàng
                shapeRenderer.color = Color(1f, 0.78f, 0.15f, 1f)
                shapeRenderer.rectLine(p.x - 7f, p.y + 7f, p.x, p.y + 1f, 2.2f)
                shapeRenderer.rectLine(p.x + 7f, p.y + 7f, p.x, p.y + 1f, 2.2f)
                shapeRenderer.rectLine(p.x, p.y + 1f, p.x - 3f, p.y - 8f, 2.0f)
                shapeRenderer.rectLine(p.x, p.y + 1f, p.x + 4f, p.y - 7f, 2.0f)

                shapeRenderer.color = Color(1f, 0.92f, 0.20f, 1f)
                shapeRenderer.circle(p.x - 3.2f, p.y + 12f, 1.8f)
                shapeRenderer.circle(p.x + 3.2f, p.y + 12f, 1.8f)
            }

            // =================================================================
            // 33. NECROMANCER
            // Áo choàng trùm đầu màu xanh đen thẫm có đường viền vàng ở gấu áo và vành mũ, mắt xanh lá phát sáng
            // =================================================================
            EnemyType.NECROMANCER -> {
                shapeRenderer.color = SHADOW
                shapeRenderer.ellipse(p.x - 11f, p.y - 12.5f, 22f, 6.5f)

                // Viền đen áo choàng trùm đầu
                shapeRenderer.color = OUTLINE
                shapeRenderer.triangle(p.x - 10.5f, p.y - 11f, p.x + 10.5f, p.y - 11f, p.x, p.y + 12f)
                shapeRenderer.rect(p.x - 8.5f, p.y - 11f, 17f, 14f)
                shapeRenderer.circle(p.x, p.y + 5.5f, 8.8f)

                // Áo choàng xanh đen thẫm
                shapeRenderer.color = Color(0.20f, 0.23f, 0.30f, 1f)
                shapeRenderer.triangle(p.x - 9f, p.y - 9.5f, p.x + 9f, p.y - 9.5f, p.x, p.y + 10f)
                shapeRenderer.rect(p.x - 7f, p.y - 9.5f, 14f, 12.5f)
                shapeRenderer.circle(p.x, p.y + 5.5f, 7.2f)

                // Đường viền vàng dọc theo gấu áo và vành mũ trùm đầu (chuẩn ảnh mẫu)
                shapeRenderer.color = Color(0.92f, 0.78f, 0.20f, 1f)
                shapeRenderer.rect(p.x - 8.5f, p.y - 9.8f, 17f, 2.2f)
                shapeRenderer.circle(p.x, p.y + 5f, 5.8f)

                // Hốc mũ tối thẫm & Đôi mắt xanh lá ma mị phát sáng
                shapeRenderer.color = OUTLINE
                shapeRenderer.circle(p.x, p.y + 4.8f, 4.5f)
                shapeRenderer.color = Color(0.35f, 0.98f, 0.25f, 1f)
                shapeRenderer.circle(p.x - 1.8f, p.y + 5.2f, 1.5f)
                shapeRenderer.circle(p.x + 1.8f, p.y + 5.2f, 1.5f)
            }

            // 34. VEZNAN: Boss Vez'nan dạng 1
            EnemyType.VEZNAN -> {
                shapeRenderer.color = SHADOW
                shapeRenderer.ellipse(p.x - 16f, p.y - 16f, 32f, 9f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 15.5f, p.y - 15f, 31f, 28f)
                shapeRenderer.circle(p.x, p.y + 12f, 11.5f)

                shapeRenderer.color = Color(0.28f, 0.16f, 0.38f, 1f)
                shapeRenderer.rect(p.x - 13.5f, p.y - 13.5f, 27f, 25f)
                shapeRenderer.circle(p.x, p.y - 2f, 15f)

                shapeRenderer.color = Color(0.12f, 0.10f, 0.14f, 1f)
                shapeRenderer.circle(p.x, p.y + 12f, 9.8f)

                shapeRenderer.color = Color(0.95f, 0.78f, 0.15f, 1f)
                shapeRenderer.rect(p.x - 9.5f, p.y + 16f, 19f, 5.5f)
                shapeRenderer.triangle(p.x - 9.5f, p.y + 21.5f, p.x - 5.5f, p.y + 28f, p.x - 1.5f, p.y + 21.5f)
                shapeRenderer.triangle(p.x - 3.5f, p.y + 21.5f, p.x, p.y + 30f, p.x + 3.5f, p.y + 21.5f)
                shapeRenderer.triangle(p.x + 1.5f, p.y + 21.5f, p.x + 5.5f, p.y + 28f, p.x + 9.5f, p.y + 21.5f)

                shapeRenderer.color = Color.RED
                shapeRenderer.circle(p.x - 3.2f, p.y + 13f, 2f)
                shapeRenderer.circle(p.x + 3.2f, p.y + 13f, 2f)
            }

            // =================================================================
            // 35. VEZNAN_DEMON: BOSS VEZ'NAN DẠNG 2
            // Cánh dơi nâu sẫm khổng lồ, tay cơ bắp, ngực vàng kem đính ngọc đỏ hình thoi,
            // sừng ngà cong vút, mắt vàng xếch và miệng nhe 2 nanh trắng lớn
            // =================================================================
            EnemyType.VEZNAN_DEMON -> {
                // 1. Bóng đổ lớn dưới chân
                shapeRenderer.color = SHADOW
                shapeRenderer.ellipse(p.x - 26f, p.y - 25f, 52f, 14f)

                // 2. ĐÔI CÁNH ĐẠI QUỶ NÂU SẪM PHÍA SAU (Vươn cao có móc nhọn & rủ xuống chân)
                for (side in listOf(-1f, 1f)) {
                    // Viền đen của cánh
                    shapeRenderer.color = OUTLINE
                    shapeRenderer.triangle(
                        p.x + side * 8f, p.y + 14f,
                        p.x + side * 34f, p.y + 26f,
                        p.x + side * 38f, p.y - 18f
                    )
                    shapeRenderer.triangle(
                        p.x + side * 8f, p.y + 14f,
                        p.x + side * 38f, p.y - 18f,
                        p.x + side * 16f, p.y - 16f
                    )
                    // Đầu móc nhọn trên đỉnh cánh
                    shapeRenderer.circle(p.x + side * 31f, p.y + 21f, 6.5f)

                    // Xương cánh nâu đậm
                    shapeRenderer.color = Color(0.32f, 0.15f, 0.07f, 1f)
                    shapeRenderer.triangle(
                        p.x + side * 9f, p.y + 13f,
                        p.x + side * 32f, p.y + 24f,
                        p.x + side * 35f, p.y - 15f
                    )
                    shapeRenderer.circle(p.x + side * 30f, p.y + 20f, 4.8f)

                    // Màng cánh trong nâu đỏ sẫm
                    shapeRenderer.color = Color(0.44f, 0.20f, 0.09f, 1f)
                    shapeRenderer.triangle(
                        p.x + side * 10f, p.y + 10f,
                        p.x + side * 31f, p.y + 17f,
                        p.x + side * 33f, p.y - 14f
                    )
                    shapeRenderer.triangle(
                        p.x + side * 10f, p.y + 10f,
                        p.x + side * 33f, p.y - 14f,
                        p.x + side * 17f, p.y - 14f
                    )
                }

                // 3. HAI CHÂN TRỤ & HAI CÁNH TAY CƠ BẮP KHỔNG LỒ (Lớp viền đen)
                shapeRenderer.color = OUTLINE
                // Chân trái & phải
                shapeRenderer.rect(p.x - 13.5f, p.y - 23f, 11.5f, 15f)
                shapeRenderer.rect(p.x + 2f, p.y - 23f, 11.5f, 15f)
                shapeRenderer.ellipse(p.x - 14.5f, p.y - 24.5f, 13f, 6.5f)
                shapeRenderer.ellipse(p.x + 1.5f, p.y - 24.5f, 13f, 6.5f)

                // Thân giữa
                shapeRenderer.circle(p.x, p.y + 2f, 19.5f)
                shapeRenderer.rect(p.x - 15f, p.y - 14f, 30f, 22f)

                // Vai & Hai cánh tay cơ bắp cuồn cuộn hai bên
                shapeRenderer.circle(p.x - 17f, p.y + 5f, 11.5f)
                shapeRenderer.circle(p.x + 17f, p.y + 5f, 11.5f)
                shapeRenderer.circle(p.x - 20f, p.y - 5f, 11f)
                shapeRenderer.circle(p.x + 20f, p.y - 5f, 11f)
                shapeRenderer.rect(p.x - 28f, p.y - 12f, 15f, 16f)
                shapeRenderer.rect(p.x + 13f, p.y - 12f, 15f, 16f)

                // 4. TÔ MÀU DA ĐỎ CAM ĐỊA NGỤC CHO CHÂN, THÂN & CÁNH TAY
                val skinDark = Color(0.62f, 0.20f, 0.06f, 1f)
                val skinBase = Color(0.76f, 0.26f, 0.08f, 1f)
                val skinLight = Color(0.88f, 0.36f, 0.11f, 1f)

                // Chân
                shapeRenderer.color = skinDark
                shapeRenderer.rect(p.x - 11.5f, p.y - 21.5f, 8f, 13f)
                shapeRenderer.rect(p.x + 3.5f, p.y - 21.5f, 8f, 13f)

                // Thân
                shapeRenderer.color = skinBase
                shapeRenderer.circle(p.x, p.y + 2f, 17f)
                shapeRenderer.rect(p.x - 13f, p.y - 12.5f, 26f, 19f)

                // Bắp vai & Cánh tay + Nắm đấm lớn
                shapeRenderer.color = skinLight
                shapeRenderer.circle(p.x - 16.5f, p.y + 5.5f, 9.2f)
                shapeRenderer.circle(p.x + 16.5f, p.y + 5.5f, 9.2f)
                shapeRenderer.color = skinBase
                shapeRenderer.circle(p.x - 19.5f, p.y - 5f, 8.8f)
                shapeRenderer.circle(p.x + 19.5f, p.y - 5f, 8.8f)
                shapeRenderer.rect(p.x - 25.5f, p.y - 10f, 11.5f, 12f)
                shapeRenderer.rect(p.x + 14f, p.y - 10f, 11.5f, 12f)

                // Đốm cơ bắp trên vai
                shapeRenderer.color = skinDark
                shapeRenderer.circle(p.x - 19f, p.y + 4f, 2.2f)
                shapeRenderer.circle(p.x + 19f, p.y + 4f, 2.2f)
                shapeRenderer.circle(p.x + 21f, p.y - 2f, 2.5f)

                // 5. MẢNG NGỰC VÀNG KEM RĂNG CƯA (CHEST PATCH CHUẨN ẢNH GỐC)
                shapeRenderer.color = OUTLINE
                shapeRenderer.triangle(p.x - 14f, p.y + 8f, p.x + 14f, p.y + 8f, p.x, p.y - 13.5f)
                shapeRenderer.triangle(p.x - 15f, p.y + 3f, p.x + 15f, p.y + 3f, p.x, p.y - 8f)
                shapeRenderer.triangle(p.x - 11f, p.y - 2f, p.x + 11f, p.y - 2f, p.x, p.y - 13.5f)

                shapeRenderer.color = Color(0.84f, 0.72f, 0.38f, 1f)
                shapeRenderer.triangle(p.x - 12f, p.y + 7f, p.x + 12f, p.y + 7f, p.x, p.y - 11.5f)
                shapeRenderer.triangle(p.x - 13f, p.y + 3f, p.x + 13f, p.y + 3f, p.x, p.y - 6.5f)
                shapeRenderer.triangle(p.x - 9f, p.y - 2f, p.x + 9f, p.y - 2f, p.x, p.y - 11.5f)

                shapeRenderer.color = Color(0.95f, 0.88f, 0.55f, 1f)
                shapeRenderer.triangle(p.x - 10f, p.y + 6.5f, p.x + 10f, p.y + 6.5f, p.x, p.y - 7f)

                // 6. VIÊN NGỌC ĐỎ CAM HÌNH THOI ĐÍNH GIỮA NGỰC
                val gemY = p.y + 1.5f
                shapeRenderer.color = OUTLINE
                shapeRenderer.triangle(p.x - 5.8f, gemY, p.x + 5.8f, gemY, p.x, gemY + 7.2f)
                shapeRenderer.triangle(p.x - 5.8f, gemY, p.x + 5.8f, gemY, p.x, gemY - 7.2f)

                shapeRenderer.color = Color(0.86f, 0.20f, 0.05f, 1f)
                shapeRenderer.triangle(p.x - 4.2f, gemY, p.x + 4.2f, gemY, p.x, gemY + 5.4f)
                shapeRenderer.triangle(p.x - 4.2f, gemY, p.x + 4.2f, gemY, p.x, gemY - 5.4f)

                shapeRenderer.color = Color(1f, 0.58f, 0.15f, 1f)
                shapeRenderer.triangle(p.x - 2.8f, gemY + 0.5f, p.x + 1.5f, gemY + 0.5f, p.x - 0.5f, gemY + 4.2f)
                shapeRenderer.color = Color(1f, 0.90f, 0.65f, 1f)
                shapeRenderer.circle(p.x - 1.2f, gemY + 2.0f, 1.1f)

                // 7. CẶP SỪNG ĐẠI QUỶ NGÀ VÀNG CONG VÚT TRÊN ĐỈNH ĐẦU
                for (side in listOf(-1f, 1f)) {
                    shapeRenderer.color = OUTLINE
                    shapeRenderer.triangle(
                        p.x + side * 7f, p.y + 18f,
                        p.x + side * 19f, p.y + 24f,
                        p.x + side * 15f, p.y + 36f
                    )
                    shapeRenderer.circle(p.x + side * 12f, p.y + 22f, 5.5f)

                    shapeRenderer.color = Color(0.94f, 0.88f, 0.62f, 1f)
                    shapeRenderer.triangle(
                        p.x + side * 8f, p.y + 19f,
                        p.x + side * 17f, p.y + 24f,
                        p.x + side * 14.5f, p.y + 34f
                    )
                    shapeRenderer.circle(p.x + side * 11.5f, p.y + 22f, 3.8f)

                    // Đốm sẫm ở gốc sừng giống ảnh gốc
                    shapeRenderer.color = Color(0.45f, 0.32f, 0.16f, 1f)
                    shapeRenderer.circle(p.x + side * 11.5f, p.y + 21f, 1.3f)
                    shapeRenderer.circle(p.x + side * 13.5f, p.y + 23.5f, 1.1f)
                }

                // 8. ĐẦU ĐẠI QUỶ, MẮT VÀNG XẾCH & MIỆNG NHE 2 RĂNG NANH LỚN
                val headY = p.y + 15.5f
                shapeRenderer.color = OUTLINE
                shapeRenderer.circle(p.x, headY, 12.5f)
                shapeRenderer.rect(p.x - 10.5f, headY - 7f, 21f, 10f)

                shapeRenderer.color = skinBase
                shapeRenderer.circle(p.x, headY, 10.5f)
                shapeRenderer.rect(p.x - 8.8f, headY - 5.5f, 17.6f, 8.5f)
                shapeRenderer.color = skinLight
                shapeRenderer.circle(p.x, headY + 2f, 8f)

                // Nếp nhăn dữ tợn trên trán
                shapeRenderer.color = OUTLINE
                shapeRenderer.rectLine(p.x - 4.5f, headY + 6.5f, p.x + 4.5f, headY + 6.5f, 1.6f)
                shapeRenderer.rectLine(p.x - 3f, headY + 8.8f, p.x + 3f, headY + 8.8f, 1.4f)

                // Đôi mắt vàng xếch ngược giận dữ & Chân mày chữ V đậm
                shapeRenderer.color = OUTLINE
                shapeRenderer.circle(p.x - 4.5f, headY + 1.5f, 2.8f)
                shapeRenderer.circle(p.x + 4.5f, headY + 1.5f, 2.8f)
                shapeRenderer.color = Color(1f, 0.92f, 0.15f, 1f)
                shapeRenderer.circle(p.x - 4.5f, headY + 1.5f, 1.9f)
                shapeRenderer.circle(p.x + 4.5f, headY + 1.5f, 1.9f)
                shapeRenderer.color = OUTLINE
                shapeRenderer.rectLine(p.x - 8.5f, headY + 4.2f, p.x - 1.5f, headY + 2.2f, 2.2f)
                shapeRenderer.rectLine(p.x + 8.5f, headY + 4.2f, p.x + 1.5f, headY + 2.2f, 2.2f)

                // Hốc miệng gầm gừ & 2 Răng nanh trắng lớn cắm xuống hai bên mép
                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 6.5f, headY - 6.5f, 13f, 5.2f)
                shapeRenderer.color = Color(0.28f, 0.08f, 0.05f, 1f)
                shapeRenderer.rect(p.x - 5.2f, headY - 5.5f, 10.4f, 3.5f)

                // Răng nhỏ ở giữa hàm
                shapeRenderer.color = Color(0.92f, 0.88f, 0.72f, 1f)
                shapeRenderer.triangle(p.x - 2.5f, headY - 2f, p.x - 0.5f, headY - 2f, p.x - 1.5f, headY - 4.5f)
                shapeRenderer.triangle(p.x + 0.5f, headY - 2f, p.x + 2.5f, headY - 2f, p.x + 1.5f, headY - 4.5f)

                // 2 Răng nanh đại (Fangs) dài nhọn hai bên mép
                shapeRenderer.color = OUTLINE
                shapeRenderer.triangle(p.x - 8f, headY - 1.5f, p.x - 3.5f, headY - 1.5f, p.x - 6f, headY - 8.5f)
                shapeRenderer.triangle(p.x + 3.5f, headY - 1.5f, p.x + 8f, headY - 1.5f, p.x + 6f, headY - 8.5f)
                shapeRenderer.color = Color(0.98f, 0.95f, 0.82f, 1f)
                shapeRenderer.triangle(p.x - 7f, headY - 1.8f, p.x - 4.2f, headY - 1.8f, p.x - 5.8f, headY - 7.2f)
                shapeRenderer.triangle(p.x + 4.2f, headY - 1.8f, p.x + 7f, headY - 1.8f, p.x + 5.8f, headY - 7.2f)
            }
        }

        if (showHpBar) {
            val barW = when (eComp.enemyType) {
                EnemyType.VEZNAN, EnemyType.VEZNAN_DEMON -> 52f
                EnemyType.JT, EnemyType.JUGGERNAUT -> 48f
                EnemyType.YETI, EnemyType.MAGMA_ELEMENTAL -> 36f
                EnemyType.OGRE, EnemyType.DEMON_LORD, EnemyType.TROLL_CHIEFTAIN -> 28f
                else -> 20f
            }
            val barYOffset = when (eComp.enemyType) {
                EnemyType.VEZNAN_DEMON -> 40f
                EnemyType.JUGGERNAUT -> 42f
                EnemyType.VEZNAN, EnemyType.JT -> 32f
                else -> 19f
            }
            renderHpBar(shapeRenderer, p.x, p.y + barYOffset, barW, hp)
        }

        if (eComp.infernalShieldHitsRemaining > 0) {
            shapeRenderer.color = Color(0.95f, 0.45f, 0.10f, 0.65f)
            shapeRenderer.circle(p.x, p.y, 18f)
            shapeRenderer.color = Color(1f, 0.85f, 0.20f, 0.85f)
            shapeRenderer.circle(p.x, p.y, 14f)
        }
    }

    private fun renderHpBar(shapeRenderer: ShapeRenderer, cx: Float, topY: Float, width: Float, hp: HealthComponent) {
        val halfW = width / 2f
        val height = if (width >= 40f) 5.5f else 3.5f

        shapeRenderer.color = Color(0.1f, 0.1f, 0.1f, 0.9f)
        shapeRenderer.rect(cx - halfW - 1f, topY - 1f, width + 2f, height + 2f)

        shapeRenderer.color = Color.RED
        shapeRenderer.rect(cx - halfW, topY, width, height)

        val hpRatio = (hp.currentHealth / hp.maxHealth).coerceIn(0f, 1f)
        shapeRenderer.color = if (width >= 40f) Color.PURPLE else Color.GREEN
        shapeRenderer.rect(cx - halfW, topY, width * hpRatio, height)
    }
}
