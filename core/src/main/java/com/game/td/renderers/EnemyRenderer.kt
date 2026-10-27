package com.game.td.renderers

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.game.td.ecs.components.*

object EnemyRenderer {

    private val OUTLINE = Color(0.13f, 0.10f, 0.08f, 1f)
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
        shapeRenderer.circle(p.x + 3.5f, p.y - 2f, 3.8f)
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

                // Chân & Thân có viền đen
                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 5.5f, p.y - 11f, 4.5f, 5f)
                shapeRenderer.rect(p.x + 1f, p.y - 11f, 4.5f, 5f)
                shapeRenderer.rect(p.x - 7.5f, p.y - 8f, 15f, 12f)
                // Tai nhọn dài & Đầu
                shapeRenderer.triangle(p.x - 5f, p.y + 6f, p.x - 16f, p.y + 10f, p.x - 5f, p.y + 1f)
                shapeRenderer.triangle(p.x + 5f, p.y + 6f, p.x + 16f, p.y + 10f, p.x + 5f, p.y + 1f)
                shapeRenderer.circle(p.x, p.y + 4.5f, 9f)

                // Màu da xanh Goblin & Áo khố nâu
                shapeRenderer.color = Color(0.36f, 0.58f, 0.18f, 1f)
                shapeRenderer.rect(p.x - 4f, p.y - 9.5f, 3f, 4f)
                shapeRenderer.rect(p.x + 1f, p.y - 9.5f, 3f, 4f)
                shapeRenderer.rect(p.x - 6f, p.y - 6.5f, 12f, 9f)
                shapeRenderer.color = Color(0.42f, 0.28f, 0.14f, 1f)
                shapeRenderer.rect(p.x - 6f, p.y - 6.5f, 12f, 5.5f)
                shapeRenderer.color = Color(0.62f, 0.44f, 0.22f, 1f)
                shapeRenderer.rect(p.x - 6f, p.y - 2.5f, 12f, 2f)

                // Đầu & Tai xanh sáng
                shapeRenderer.color = Color(0.46f, 0.70f, 0.22f, 1f)
                shapeRenderer.triangle(p.x - 5f, p.y + 5.5f, p.x - 14f, p.y + 9f, p.x - 5f, p.y + 2f)
                shapeRenderer.triangle(p.x + 5f, p.y + 5.5f, p.x + 14f, p.y + 9f, p.x + 5f, p.y + 2f)
                shapeRenderer.circle(p.x, p.y + 4.5f, 7.3f)

                // Mắt vàng ác & Lông mày chữ V
                shapeRenderer.color = Color(0.98f, 0.90f, 0.18f, 1f)
                shapeRenderer.circle(p.x - 2.6f, p.y + 4.5f, 1.7f)
                shapeRenderer.circle(p.x + 2.6f, p.y + 4.5f, 1.7f)
                shapeRenderer.color = OUTLINE
                shapeRenderer.rectLine(p.x - 5.5f, p.y + 7f, p.x - 1f, p.y + 5.2f, 1.8f)
                shapeRenderer.rectLine(p.x + 5.5f, p.y + 7f, p.x + 1f, p.y + 5.2f, 1.8f)

                // Chùy gỗ cầm ngang trước bụng
                shapeRenderer.color = OUTLINE
                shapeRenderer.rectLine(p.x - 6f, p.y - 3.5f, p.x + 8f, p.y - 2f, 5f)
                shapeRenderer.color = Color(0.65f, 0.45f, 0.22f, 1f)
                shapeRenderer.rectLine(p.x - 5f, p.y - 3.5f, p.x + 7f, p.y - 2f, 3f)
            }

            // 02. ORC: Chiến binh Orc vạm vỡ, hàm dưới bạnh với 2 răng nanh trắng, cầm dao phay lớn dính máu đỏ
            EnemyType.ORC -> {
                shapeRenderer.color = SHADOW
                shapeRenderer.ellipse(p.x - 13f, p.y - 14f, 26f, 7f)

                // Viền đen tổng thể
                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 7.5f, p.y - 13f, 6f, 6f)
                shapeRenderer.rect(p.x + 1.5f, p.y - 13f, 6f, 6f)
                shapeRenderer.circle(p.x, p.y - 1f, 11f)
                shapeRenderer.rect(p.x - 9.5f, p.y - 9f, 19f, 12f)
                shapeRenderer.triangle(p.x - 7f, p.y + 8f, p.x - 15f, p.y + 11f, p.x - 6f, p.y + 4f)
                shapeRenderer.triangle(p.x + 7f, p.y + 8f, p.x + 15f, p.y + 11f, p.x + 6f, p.y + 4f)
                shapeRenderer.circle(p.x, p.y + 6.5f, 9.8f)

                // Thân & Giáp đai da
                shapeRenderer.color = Color(0.28f, 0.48f, 0.16f, 1f)
                shapeRenderer.rect(p.x - 6f, p.y - 11.5f, 4f, 5f)
                shapeRenderer.rect(p.x + 2f, p.y - 11.5f, 4f, 5f)
                shapeRenderer.circle(p.x, p.y - 1f, 9.2f)
                shapeRenderer.color = Color(0.38f, 0.25f, 0.14f, 1f)
                shapeRenderer.rect(p.x - 8f, p.y - 8f, 16f, 5.5f)

                // Đầu Orc & Chỏm mũ sắt đỉnh đầu
                shapeRenderer.color = Color(0.36f, 0.58f, 0.20f, 1f)
                shapeRenderer.triangle(p.x - 6f, p.y + 7.5f, p.x - 13f, p.y + 10f, p.x - 6f, p.y + 4.5f)
                shapeRenderer.triangle(p.x + 6f, p.y + 7.5f, p.x + 13f, p.y + 10f, p.x + 6f, p.y + 4.5f)
                shapeRenderer.circle(p.x, p.y + 6.5f, 8f)
                shapeRenderer.color = Color(0.25f, 0.38f, 0.14f, 1f)
                shapeRenderer.triangle(p.x - 4f, p.y + 13f, p.x + 4f, p.y + 13f, p.x, p.y + 18f)

                // Hàm dưới & 2 Răng nanh trắng nhô lên
                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 5.5f, p.y + 2.5f, 11f, 2f)
                shapeRenderer.color = Color.WHITE
                shapeRenderer.triangle(p.x - 4.5f, p.y + 3.5f, p.x - 3f, p.y + 7.5f, p.x - 1.5f, p.y + 3.5f)
                shapeRenderer.triangle(p.x + 1.5f, p.y + 3.5f, p.x + 3f, p.y + 7.5f, p.x + 4.5f, p.y + 3.5f)

                // Mắt vàng & Mày dữ
                shapeRenderer.color = Color(0.98f, 0.88f, 0.18f, 1f)
                shapeRenderer.circle(p.x - 2.8f, p.y + 8.5f, 1.6f)
                shapeRenderer.circle(p.x + 2.8f, p.y + 8.5f, 1.6f)
                shapeRenderer.color = OUTLINE
                shapeRenderer.rectLine(p.x - 6f, p.y + 10.5f, p.x - 1f, p.y + 9f, 1.8f)
                shapeRenderer.rectLine(p.x + 6f, p.y + 10.5f, p.x + 1f, p.y + 9f, 1.8f)

                // Dao phay bản rộng cầm ngang có vệt máu đỏ (chuẩn hình 2)
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

                // Viền đen 4 chân, thân, đuôi và bờm gai
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

                // Lông sói nâu xám
                shapeRenderer.color = Color(0.46f, 0.42f, 0.36f, 1f)
                shapeRenderer.rect(p.x - 7.8f, p.y - 7.5f, 2f, 4.5f)
                shapeRenderer.rect(p.x + 3f, p.y - 7.5f, 2f, 4.5f)
                shapeRenderer.ellipse(p.x - 10.5f, p.y - 4.5f, 19f, 9f)
                shapeRenderer.triangle(p.x - 7.5f, p.y + 2f, p.x - 5f, p.y + 7.5f, p.x - 2.5f, p.y + 2f)
                shapeRenderer.triangle(p.x - 3f, p.y + 3f, p.x, p.y + 8.5f, p.x + 2.5f, p.y + 3f)
                shapeRenderer.circle(p.x + 7.5f, p.y + 1f, 5.2f)
                shapeRenderer.triangle(p.x + 7f, p.y + 3f, p.x + 14f, p.y + 1f, p.x + 7f, p.y - 2.5f)
                // Tai nhọn
                shapeRenderer.triangle(p.x + 4f, p.y + 5f, p.x + 6f, p.y + 10f, p.x + 8.5f, p.y + 5f)

                // Nanh trắng & Mắt đỏ cam
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

                // Lông đen ánh xanh thép
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

            // 05. SHAMAN: Pháp sư bộ tộc đeo mặt nạ sọ xương trắng, mũ bờm lông vũ cam rực, cầm trượng sọ (chuẩn hình 3)
            EnemyType.SHAMAN -> {
                shapeRenderer.color = SHADOW
                shapeRenderer.ellipse(p.x - 11f, p.y - 12f, 22f, 6.5f)

                // Bờm lông vũ cam rực xòe trên đỉnh đầu (5 nhánh lông vũ có viền đen)
                val feathers = listOf(-8f, -4f, 0f, 4f, 8f)
                shapeRenderer.color = OUTLINE
                for (fx in feathers) {
                    shapeRenderer.triangle(p.x + fx - 3.5f, p.y + 8f, p.x + fx + 3.5f, p.y + 8f, p.x + fx * 1.35f, p.y + 19f)
                }
                shapeRenderer.rect(p.x - 7.5f, p.y - 10.5f, 15f, 13f)
                shapeRenderer.circle(p.x, p.y + 4.5f, 9.5f)

                // Màu lông vũ cam & vàng lửa
                for (fx in feathers) {
                    shapeRenderer.color = Color(0.92f, 0.36f, 0.08f, 1f)
                    shapeRenderer.triangle(p.x + fx - 2.4f, p.y + 8f, p.x + fx + 2.4f, p.y + 8f, p.x + fx * 1.3f, p.y + 17.2f)
                    shapeRenderer.color = Color(0.98f, 0.68f, 0.15f, 1f)
                    shapeRenderer.triangle(p.x + fx - 1.2f, p.y + 8.5f, p.x + fx + 1.2f, p.y + 8.5f, p.x + fx * 1.15f, p.y + 14f)
                }

                // Áo choàng thổ dân màu nâu cam
                shapeRenderer.color = Color(0.68f, 0.34f, 0.12f, 1f)
                shapeRenderer.rect(p.x - 6f, p.y - 9f, 12f, 10f)
                shapeRenderer.color = Color(0.92f, 0.56f, 0.16f, 1f)
                shapeRenderer.rect(p.x - 6f, p.y - 5f, 12f, 2.5f)

                // Mặt nạ hộp sọ trắng ngà (Skull Mask)
                shapeRenderer.color = Color(0.94f, 0.90f, 0.78f, 1f)
                shapeRenderer.circle(p.x, p.y + 4.5f, 7.6f)
                shapeRenderer.rect(p.x - 4.5f, p.y - 1f, 9f, 5f)
                // Hốc mắt sọ đen & Mũi sọ tam giác
                shapeRenderer.color = OUTLINE
                shapeRenderer.circle(p.x - 2.8f, p.y + 4.5f, 2.1f)
                shapeRenderer.circle(p.x + 2.8f, p.y + 4.5f, 2.1f)
                shapeRenderer.triangle(p.x - 1.2f, p.y + 1.5f, p.x + 1.2f, p.y + 1.5f, p.x, p.y + 3.2f)
                // Đốm mắt sáng nhỏ trong hốc sọ
                shapeRenderer.color = Color(1f, 0.75f, 0.2f, 1f)
                shapeRenderer.circle(p.x - 2.8f, p.y + 4.5f, 0.8f)
                shapeRenderer.circle(p.x + 2.8f, p.y + 4.5f, 0.8f)

                // Trượng phép xương bên tay phải
                shapeRenderer.color = OUTLINE
                shapeRenderer.rectLine(p.x + 8.5f, p.y - 9f, p.x + 10.5f, p.y + 9f, 4f)
                shapeRenderer.circle(p.x + 10.5f, p.y + 10f, 4.5f)
                shapeRenderer.color = Color(0.58f, 0.36f, 0.16f, 1f)
                shapeRenderer.rectLine(p.x + 8.5f, p.y - 8f, p.x + 10.5f, p.y + 8f, 2.2f)
                shapeRenderer.color = Color(1f, 0.62f, 0.15f, 1f)
                shapeRenderer.circle(p.x + 10.5f, p.y + 10f, 3.2f)
            }

            // 06. BANDIT: Thổ phỉ nhanh nhẹn, trán da người, khăn cam đỏ bịt kín mũi miệng, cầm dao găm bạc (chuẩn hình 5)
            EnemyType.BANDIT -> {
                shapeRenderer.color = SHADOW
                shapeRenderer.ellipse(p.x - 10f, p.y - 12f, 20f, 6f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 6f, p.y - 11f, 4.5f, 5f)
                shapeRenderer.rect(p.x + 1.5f, p.y - 11f, 4.5f, 5f)
                shapeRenderer.rect(p.x - 7.5f, p.y - 8f, 15f, 12f)
                shapeRenderer.circle(p.x, p.y + 4.5f, 8.8f)

                // Quần áo tối & Áo nâu
                shapeRenderer.color = Color(0.24f, 0.22f, 0.20f, 1f)
                shapeRenderer.rect(p.x - 6f, p.y - 7f, 12f, 9f)
                shapeRenderer.color = Color(0.52f, 0.34f, 0.18f, 1f)
                shapeRenderer.rect(p.x - 6f, p.y - 3f, 12f, 5f)

                // Đầu da người & Mắt dữ
                shapeRenderer.color = Color(0.94f, 0.76f, 0.56f, 1f)
                shapeRenderer.circle(p.x, p.y + 5f, 7f)
                shapeRenderer.color = OUTLINE
                shapeRenderer.circle(p.x - 2.5f, p.y + 6.5f, 1.5f)
                shapeRenderer.circle(p.x + 2.5f, p.y + 6.5f, 1.5f)
                shapeRenderer.rectLine(p.x - 5f, p.y + 8.5f, p.x - 1f, p.y + 7.2f, 1.6f)
                shapeRenderer.rectLine(p.x + 5f, p.y + 8.5f, p.x + 1f, p.y + 7.2f, 1.6f)

                // Khăn cam-đỏ bịt mặt chéo xuống cổ
                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 7.8f, p.y + 0.5f, 15.6f, 5f)
                shapeRenderer.color = Color(0.88f, 0.36f, 0.14f, 1f)
                shapeRenderer.rect(p.x - 6.8f, p.y + 1.2f, 13.6f, 3.8f)
                shapeRenderer.triangle(p.x - 5.5f, p.y + 1.2f, p.x + 5.5f, p.y + 1.2f, p.x, p.y - 3.5f)

                // Dao găm bạc cầm ngang trước bụng
                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 5f, p.y - 5.5f, 13f, 4.5f)
                shapeRenderer.color = Color(0.32f, 0.24f, 0.16f, 1f)
                shapeRenderer.rect(p.x - 4f, p.y - 4.5f, 3.5f, 2.5f)
                shapeRenderer.color = Color(0.90f, 0.94f, 0.98f, 1f)
                shapeRenderer.rect(p.x - 0.5f, p.y - 4.5f, 6.5f, 2.5f)
                shapeRenderer.triangle(p.x + 6f, p.y - 4.5f, p.x + 6f, p.y - 2f, p.x + 9.5f, p.y - 3.2f)
            }

            // 07. BRIGAND: Sơn tặc mặc giáp da nâu, đội mũ sắt chóp nhọn có khe mắt đen, cầm kiếm bản rộng (chuẩn hình 6)
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

                // Áo giáp da nâu & Tay áo vàng cát
                shapeRenderer.color = Color(0.58f, 0.38f, 0.18f, 1f)
                shapeRenderer.rect(p.x - 7f, p.y - 8f, 14f, 10f)
                shapeRenderer.color = Color(0.84f, 0.68f, 0.42f, 1f)
                shapeRenderer.circle(p.x - 7.5f, p.y - 2f, 3.5f)
                shapeRenderer.circle(p.x + 7.5f, p.y - 2f, 3.5f)

                // Mũ sắt chóp nhọn (Conical Iron Helmet)
                shapeRenderer.color = Color(0.56f, 0.60f, 0.64f, 1f)
                shapeRenderer.circle(p.x, p.y + 5.5f, 7.4f)
                shapeRenderer.triangle(p.x - 2f, p.y + 12f, p.x + 2f, p.y + 12f, p.x, p.y + 16f)
                shapeRenderer.color = Color(0.76f, 0.80f, 0.84f, 1f)
                shapeRenderer.circle(p.x - 2.5f, p.y + 8f, 2.2f)

                // Khe kính mũ sắt đen & Phần cằm da người
                shapeRenderer.color = Color(0.92f, 0.74f, 0.54f, 1f)
                shapeRenderer.rect(p.x - 5.5f, p.y + 1f, 11f, 3.5f)
                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 6f, p.y + 4.5f, 12f, 3f)
                shapeRenderer.rect(p.x + 0.5f, p.y + 3.5f, 4.5f, 4.5f)

                // Thanh kiếm bản rộng cầm ngang trước người
                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 5f, p.y - 6.5f, 16f, 5.5f)
                shapeRenderer.color = Color(0.88f, 0.92f, 0.96f, 1f)
                shapeRenderer.rect(p.x - 3.5f, p.y - 5.2f, 13f, 3f)
            }

            // 08. MARAUDER: Tướng cướp khổng lồ mặc giáp đồng, mũ sắt song sắt dọc, cầm đinh ba bạc (chuẩn hàng 2 cột 1)
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

                // Giáp tấm màu nâu đồng & cầu vai lớn
                shapeRenderer.color = Color(0.68f, 0.48f, 0.26f, 1f)
                shapeRenderer.circle(p.x, p.y - 1f, 11.5f)
                shapeRenderer.rect(p.x - 10f, p.y - 8.5f, 20f, 12f)
                shapeRenderer.color = Color(0.82f, 0.62f, 0.36f, 1f)
                shapeRenderer.circle(p.x - 11.5f, p.y + 3f, 4.8f)
                shapeRenderer.circle(p.x + 11.5f, p.y + 3f, 4.8f)
                shapeRenderer.color = Color(0.42f, 0.28f, 0.14f, 1f)
                shapeRenderer.rect(p.x - 10f, p.y - 8.5f, 20f, 4f)

                // Mũ sắt kín mặt có khe song sắt dọc
                shapeRenderer.color = Color(0.58f, 0.56f, 0.52f, 1f)
                shapeRenderer.circle(p.x, p.y + 9f, 7.8f)
                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 5.5f, p.y + 6f, 11f, 5.5f)
                shapeRenderer.color = Color(0.75f, 0.74f, 0.70f, 1f)
                shapeRenderer.rect(p.x - 3f, p.y + 6f, 1.5f, 5.5f)
                shapeRenderer.rect(p.x, p.y + 6f, 1.5f, 5.5f)
                shapeRenderer.rect(p.x + 3f, p.y + 6f, 1.5f, 5.5f)

                // Cây Đinh Ba / Kích bạc dài cầm ngang trước bụng
                shapeRenderer.color = OUTLINE
                shapeRenderer.rectLine(p.x - 14f, p.y - 4f, p.x + 12f, p.y - 2f, 4.5f)
                shapeRenderer.color = Color(0.48f, 0.30f, 0.14f, 1f)
                shapeRenderer.rectLine(p.x - 13f, p.y - 4f, p.x + 8f, p.y - 2.3f, 2.5f)
                shapeRenderer.color = Color(0.88f, 0.92f, 0.96f, 1f)
                shapeRenderer.triangle(p.x + 8f, p.y + 2.5f, p.x + 8f, p.y - 6.5f, p.x + 17f, p.y - 2f)
            }

            // 09. OGRE: Gã khổng lồ Ogre cơ bắp xanh rêu, giáp vai sắt, cầm chùy gỗ đỏ đóng đinh trắng (chuẩn hình 4)
            EnemyType.OGRE -> {
                shapeRenderer.color = SHADOW
                shapeRenderer.ellipse(p.x - 16f, p.y - 16f, 32f, 8.5f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 10f, p.y - 15f, 8f, 7f)
                shapeRenderer.rect(p.x + 2f, p.y - 15f, 8f, 7f)
                shapeRenderer.circle(p.x, p.y, 15.5f)
                shapeRenderer.rect(p.x - 13.5f, p.y - 11f, 27f, 16f)
                shapeRenderer.circle(p.x, p.y + 11f, 10f)

                // Thân cơ bắp xanh Ogre & Đai chéo ngực
                shapeRenderer.color = Color(0.46f, 0.56f, 0.28f, 1f)
                shapeRenderer.circle(p.x, p.y, 13.5f)
                shapeRenderer.rect(p.x - 11.5f, p.y - 9f, 23f, 14f)
                shapeRenderer.color = Color(0.38f, 0.24f, 0.12f, 1f)
                shapeRenderer.rect(p.x - 11.5f, p.y - 9.5f, 23f, 4.5f)
                shapeRenderer.rectLine(p.x - 10f, p.y + 7f, p.x + 9f, p.y - 6f, 3.5f)
                // Giáp vai sắt bên trái
                shapeRenderer.color = Color(0.55f, 0.58f, 0.62f, 1f)
                shapeRenderer.circle(p.x - 11f, p.y + 6f, 5.5f)

                // Đầu Ogre, răng nanh dưới & Mắt dữ
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

                // Chùy gỗ đỏ nạm đinh trắng cầm ngang
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

            // 10. GIANT_SPIDER: Nhện độc xanh đen lưng vằn đỏ, 8 chân đốt cong quặp xuống đất (chuẩn hàng 2 cột 2)
            EnemyType.GIANT_SPIDER -> {
                shapeRenderer.color = SHADOW
                shapeRenderer.ellipse(p.x - 13f, p.y - 10f, 26f, 7f)

                // 8 chân nhện đốt gấp khúc quặp xuống dưới (giống hệt ảnh gốc)
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

                // Bụng & Đầu nhện có viền đen
                shapeRenderer.color = OUTLINE
                shapeRenderer.circle(p.x, p.y + 2f, 8.8f)
                shapeRenderer.circle(p.x, p.y - 3.5f, 6.8f)

                shapeRenderer.color = Color(0.24f, 0.28f, 0.36f, 1f)
                shapeRenderer.circle(p.x, p.y + 2f, 7.2f)
                shapeRenderer.circle(p.x, p.y - 3.5f, 5.2f)

                // Vằn đỏ trên lưng & Mắt đỏ rực
                shapeRenderer.color = Color(0.86f, 0.18f, 0.14f, 1f)
                shapeRenderer.rect(p.x - 4f, p.y + 3f, 8f, 2f)
                shapeRenderer.rect(p.x - 3f, p.y + 6f, 6f, 1.8f)
                shapeRenderer.circle(p.x - 2.2f, p.y - 4f, 1.6f)
                shapeRenderer.circle(p.x + 2.2f, p.y - 4f, 1.6f)
            }

            // 11. SPIDER_MATRIARCH: Nhện Chúa khổng lồ chân to vạm vỡ, lưng vằn xanh độc vàng (chuẩn hàng 2 cột 3)
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

                // Vằn xanh lá mạ / vàng độc tố trên bụng nhện chúa
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

            // 13. DARK_KNIGHT: Hiệp sĩ bóng đêm mũ sừng sắt, cầm kiếm lửa cam và khiên sắt (chuẩn hàng 2 cột 6)
            EnemyType.DARK_KNIGHT -> {
                shapeRenderer.color = SHADOW
                shapeRenderer.ellipse(p.x - 12f, p.y - 13f, 24f, 7f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 7f, p.y - 12f, 5.5f, 5f)
                shapeRenderer.rect(p.x + 1.5f, p.y - 12f, 5.5f, 5f)
                shapeRenderer.rect(p.x - 9f, p.y - 8.5f, 18f, 12f)
                shapeRenderer.circle(p.x, p.y + 6f, 9f)
                // 2 Sừng nhọn vểnh lên
                shapeRenderer.triangle(p.x - 6f, p.y + 8f, p.x - 13f, p.y + 18f, p.x - 2f, p.y + 12f)
                shapeRenderer.triangle(p.x + 6f, p.y + 8f, p.x + 13f, p.y + 18f, p.x + 2f, p.y + 12f)

                // Giáp thép đen & Sừng xám sáng
                shapeRenderer.color = Color(0.24f, 0.25f, 0.28f, 1f)
                shapeRenderer.rect(p.x - 7.5f, p.y - 7f, 15f, 10f)
                shapeRenderer.color = Color(0.38f, 0.40f, 0.44f, 1f)
                shapeRenderer.circle(p.x, p.y + 6f, 7.3f)
                shapeRenderer.color = Color(0.75f, 0.76f, 0.78f, 1f)
                shapeRenderer.triangle(p.x - 6f, p.y + 9f, p.x - 11.5f, p.y + 16.5f, p.x - 3f, p.y + 11.5f)
                shapeRenderer.triangle(p.x + 6f, p.y + 9f, p.x + 11.5f, p.y + 16.5f, p.x + 3f, p.y + 11.5f)

                // Khe mắt chữ T đen & mắt đỏ
                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 5f, p.y + 5f, 10f, 2.5f)
                shapeRenderer.rect(p.x - 1.2f, p.y + 2f, 2.4f, 5f)
                shapeRenderer.color = Color.RED
                shapeRenderer.circle(p.x - 2.5f, p.y + 6.2f, 1.1f)
                shapeRenderer.circle(p.x + 2.5f, p.y + 6.2f, 1.1f)

                // Khiên bạc bên phải & Thanh kiếm lửa cam-đỏ ngang ngực (chuẩn hình mẫu)
                shapeRenderer.color = OUTLINE
                shapeRenderer.circle(p.x + 6.5f, p.y - 2f, 6.2f)
                shapeRenderer.color = Color(0.78f, 0.80f, 0.84f, 1f)
                shapeRenderer.circle(p.x + 6.5f, p.y - 2f, 4.6f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 9.5f, p.y - 5.5f, 15f, 4.8f)
                shapeRenderer.color = Color(0.95f, 0.38f, 0.12f, 1f)
                shapeRenderer.rect(p.x - 8f, p.y - 4.3f, 12f, 2.5f)
            }

            // 14. GARGOYLE: Quỷ dơi đá cánh rộng, tai dơi nhọn, mắt đỏ (chuẩn hàng 2 cột 4)
            EnemyType.GARGOYLE -> {
                shapeRenderer.color = SHADOW
                shapeRenderer.ellipse(p.x - 10f, p.y - 14f, 20f, 6f)

                // Đôi cánh dơi xòe rộng có viền đen
                shapeRenderer.color = OUTLINE
                shapeRenderer.triangle(p.x - 3f, p.y + 3f, p.x - 22f, p.y + 13f, p.x - 13f, p.y - 7f)
                shapeRenderer.triangle(p.x + 3f, p.y + 3f, p.x + 22f, p.y + 13f, p.x + 13f, p.y - 7f)
                shapeRenderer.circle(p.x, p.y - 1f, 8.5f)
                shapeRenderer.circle(p.x, p.y + 6f, 7f)
                shapeRenderer.triangle(p.x - 5f, p.y + 8f, p.x - 9f, p.y + 15f, p.x - 1f, p.y + 10f)
                shapeRenderer.triangle(p.x + 5f, p.y + 8f, p.x + 9f, p.y + 15f, p.x + 1f, p.y + 10f)

                // Màng cánh nâu xám
                shapeRenderer.color = Color(0.46f, 0.40f, 0.38f, 1f)
                shapeRenderer.triangle(p.x - 3f, p.y + 2f, p.x - 20f, p.y + 11.5f, p.x - 12f, p.y - 5.5f)
                shapeRenderer.triangle(p.x + 3f, p.y + 2f, p.x + 20f, p.y + 11.5f, p.x + 12f, p.y - 5.5f)

                // Thân & Đầu Gargoyle
                shapeRenderer.color = Color(0.56f, 0.50f, 0.48f, 1f)
                shapeRenderer.circle(p.x, p.y - 1f, 6.8f)
                shapeRenderer.circle(p.x, p.y + 6f, 5.5f)
                shapeRenderer.triangle(p.x - 4.5f, p.y + 8f, p.x - 7.5f, p.y + 13.5f, p.x - 1.5f, p.y + 9.5f)
                shapeRenderer.triangle(p.x + 4.5f, p.y + 8f, p.x + 7.5f, p.y + 13.5f, p.x + 1.5f, p.y + 9.5f)

                shapeRenderer.color = Color(0.95f, 0.25f, 0.15f, 1f)
                shapeRenderer.circle(p.x - 2.2f, p.y + 6.5f, 1.4f)
                shapeRenderer.circle(p.x + 2.2f, p.y + 6.5f, 1.4f)
            }

            // 15. SHADOW_ARCHER: Cung thủ áo choàng trùm đầu đen kịt, 2 mắt vàng sáng, cầm cung gỗ ngang ngực (chuẩn hàng 2 cột 5)
            EnemyType.SHADOW_ARCHER -> {
                shapeRenderer.color = SHADOW
                shapeRenderer.ellipse(p.x - 10f, p.y - 12f, 20f, 6f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 6f, p.y - 11f, 4.5f, 5f)
                shapeRenderer.rect(p.x + 1.5f, p.y - 11f, 4.5f, 5f)
                shapeRenderer.rect(p.x - 8f, p.y - 8f, 16f, 12f)
                shapeRenderer.circle(p.x, p.y + 5f, 8.5f)
                shapeRenderer.triangle(p.x - 8f, p.y + 5f, p.x - 13f, p.y - 2f, p.x - 5f, p.y - 4f)

                // Áo trùm đầu đen thẫm
                shapeRenderer.color = Color(0.16f, 0.15f, 0.20f, 1f)
                shapeRenderer.rect(p.x - 6.5f, p.y - 7f, 13f, 10f)
                shapeRenderer.circle(p.x, p.y + 5f, 6.8f)

                // Đôi mắt vàng rực trong bóng tối
                shapeRenderer.color = Color(1f, 0.90f, 0.18f, 1f)
                shapeRenderer.circle(p.x - 2.3f, p.y + 5.2f, 1.6f)
                shapeRenderer.circle(p.x + 2.3f, p.y + 5.2f, 1.6f)

                // Cánh cung gỗ nâu cầm ngang trước ngực
                shapeRenderer.color = OUTLINE
                shapeRenderer.rectLine(p.x - 9f, p.y - 2.5f, p.x + 9f, p.y - 1.5f, 4.2f)
                shapeRenderer.color = Color(0.62f, 0.36f, 0.16f, 1f)
                shapeRenderer.rectLine(p.x - 8f, p.y - 2.5f, p.x + 8f, p.y - 1.5f, 2.4f)
            }

            // 16. GOLEM_HEAD: Đầu người máy sắt 2 sừng trâu có chân nhỏ chạy lạch bạch (chuẩn hàng 3 cột 3)
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

            // 17. JUGGERNAUT: BOSS CỖ MÁY HỦY DIỆT KHỔNG LỒ (GIỮ NGUYÊN TẠO HÌNH ĐẸP NHẤT)
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

            // 18. TROLL: Chiến binh Troll da xanh băng, bờm tóc Mohawk đỏ rực, nanh trắng, cầm đao/giáo đá (chuẩn hàng 3 cột 5)
            EnemyType.TROLL -> {
                shapeRenderer.color = SHADOW
                shapeRenderer.ellipse(p.x - 12f, p.y - 13f, 24f, 6.5f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 6.5f, p.y - 12f, 5f, 5f)
                shapeRenderer.rect(p.x + 1.5f, p.y - 12f, 5f, 5f)
                shapeRenderer.rect(p.x - 8.5f, p.y - 8.5f, 17f, 12f)
                shapeRenderer.triangle(p.x - 6f, p.y + 7f, p.x - 14f, p.y + 10f, p.x - 5f, p.y + 3f)
                shapeRenderer.triangle(p.x + 6f, p.y + 7f, p.x + 14f, p.y + 10f, p.x + 5f, p.y + 3f)
                shapeRenderer.circle(p.x, p.y + 6f, 9.2f)
                // Bờm tóc Mohawk đỏ
                shapeRenderer.triangle(p.x - 5.5f, p.y + 12f, p.x + 5.5f, p.y + 12f, p.x, p.y + 21f)

                shapeRenderer.color = Color(0.32f, 0.62f, 0.72f, 1f)
                shapeRenderer.rect(p.x - 7f, p.y - 7f, 14f, 10f)
                shapeRenderer.color = Color(0.45f, 0.28f, 0.14f, 1f)
                shapeRenderer.rect(p.x - 7f, p.y - 7.5f, 14f, 5f)

                shapeRenderer.color = Color(0.38f, 0.70f, 0.80f, 1f)
                shapeRenderer.triangle(p.x - 5f, p.y + 6.5f, p.x - 12.5f, p.y + 9f, p.x - 5f, p.y + 3.5f)
                shapeRenderer.triangle(p.x + 5f, p.y + 6.5f, p.x + 12.5f, p.y + 9f, p.x + 5f, p.y + 3.5f)
                shapeRenderer.circle(p.x, p.y + 6f, 7.5f)

                shapeRenderer.color = Color(0.90f, 0.20f, 0.15f, 1f)
                shapeRenderer.triangle(p.x - 4f, p.y + 12f, p.x + 4f, p.y + 12f, p.x, p.y + 19f)

                shapeRenderer.color = Color.WHITE
                shapeRenderer.triangle(p.x - 4.5f, p.y + 3f, p.x - 3f, p.y + 6.5f, p.x - 1.5f, p.y + 3f)
                shapeRenderer.triangle(p.x + 1.5f, p.y + 3f, p.x + 3f, p.y + 6.5f, p.x + 4.5f, p.y + 3f)
                shapeRenderer.color = Color(1f, 0.90f, 0.20f, 1f)
                shapeRenderer.circle(p.x - 2.6f, p.y + 7.5f, 1.5f)
                shapeRenderer.circle(p.x + 2.6f, p.y + 7.5f, 1.5f)

                // Giáo đá cầm ngang trước bụng
                shapeRenderer.color = OUTLINE
                shapeRenderer.rectLine(p.x - 9f, p.y - 4f, p.x + 8f, p.y - 3f, 4f)
                shapeRenderer.color = Color(0.55f, 0.35f, 0.16f, 1f)
                shapeRenderer.rectLine(p.x - 8f, p.y - 4f, p.x + 6f, p.y - 3f, 2.2f)
                shapeRenderer.color = Color(0.82f, 0.86f, 0.90f, 1f)
                shapeRenderer.triangle(p.x + 6f, p.y, p.x + 6f, p.y - 6f, p.x + 13.5f, p.y - 3f)
            }

            // 19. WINTER_WOLF: Sói băng tuyết trắng xanh bờm nhọn, mắt đỏ rực (chuẩn hàng 3 cột 4)
            EnemyType.WINTER_WOLF -> {
                shapeRenderer.color = SHADOW
                shapeRenderer.ellipse(p.x - 14f, p.y - 10f, 28f, 6.5f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 10f, p.y - 9.5f, 4f, 6.5f)
                shapeRenderer.rect(p.x - 4.5f, p.y - 9.5f, 4f, 6.5f)
                shapeRenderer.rect(p.x + 2f, p.y - 9.5f, 4f, 6.5f)
                shapeRenderer.rect(p.x + 7f, p.y - 9.5f, 4f, 6.5f)
                shapeRenderer.ellipse(p.x - 13f, p.y - 6f, 24f, 13f)
                shapeRenderer.triangle(p.x - 9f, p.y + 3f, p.x - 5f, p.y + 11.5f, p.x - 1f, p.y + 3f)
                shapeRenderer.triangle(p.x - 3f, p.y + 4f, p.x + 1f, p.y + 13f, p.x + 5f, p.y + 4f)
                shapeRenderer.circle(p.x + 8.5f, p.y + 2f, 7.5f)
                shapeRenderer.triangle(p.x + 7.5f, p.y + 5.5f, p.x + 17f, p.y + 1.5f, p.x + 7.5f, p.y - 3.5f)

                // Lông trắng băng giá ánh xanh
                shapeRenderer.color = Color(0.86f, 0.92f, 0.98f, 1f)
                shapeRenderer.rect(p.x - 8.8f, p.y - 8f, 2.2f, 5f)
                shapeRenderer.rect(p.x + 3.2f, p.y - 8f, 2.2f, 5f)
                shapeRenderer.ellipse(p.x - 11.5f, p.y - 4.5f, 21f, 10f)
                shapeRenderer.color = Color.WHITE
                shapeRenderer.triangle(p.x - 8f, p.y + 3f, p.x - 5f, p.y + 10f, p.x - 2f, p.y + 3f)
                shapeRenderer.triangle(p.x - 2f, p.y + 4f, p.x + 1f, p.y + 11f, p.x + 4f, p.y + 4f)
                shapeRenderer.circle(p.x + 8.5f, p.y + 2f, 5.8f)
                shapeRenderer.triangle(p.x + 7.5f, p.y + 4f, p.x + 15.2f, p.y + 1.5f, p.x + 7.5f, p.y - 2.2f)

                shapeRenderer.color = Color.RED
                shapeRenderer.circle(p.x + 10f, p.y + 3f, 1.6f)
            }

            // 20. TROLL_CHAMPION: Troll tinh nhuệ đội mũ sừng sắt, bờm đỏ, cầm cặp rìu phóng
            EnemyType.TROLL_CHAMPION -> {
                shapeRenderer.color = SHADOW
                shapeRenderer.ellipse(p.x - 14f, p.y - 14f, 28f, 7.5f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 8f, p.y - 13f, 6f, 6f)
                shapeRenderer.rect(p.x + 2f, p.y - 13f, 6f, 6f)
                shapeRenderer.circle(p.x, p.y, 13f)
                shapeRenderer.rect(p.x - 11f, p.y - 10f, 22f, 14f)
                shapeRenderer.circle(p.x, p.y + 8f, 10f)
                shapeRenderer.triangle(p.x - 7f, p.y + 10f, p.x - 15f, p.y + 19f, p.x - 2f, p.y + 13f)
                shapeRenderer.triangle(p.x + 7f, p.y + 10f, p.x + 15f, p.y + 19f, p.x + 2f, p.y + 13f)

                shapeRenderer.color = Color(0.28f, 0.56f, 0.68f, 1f)
                shapeRenderer.circle(p.x, p.y, 11f)
                shapeRenderer.rect(p.x - 9f, p.y - 8.5f, 18f, 12f)
                shapeRenderer.color = Color(0.52f, 0.55f, 0.60f, 1f)
                shapeRenderer.circle(p.x, p.y + 8f, 8.2f)
                shapeRenderer.color = Color(0.88f, 0.88f, 0.82f, 1f)
                shapeRenderer.triangle(p.x - 6.5f, p.y + 10f, p.x - 13.5f, p.y + 17.5f, p.x - 2.5f, p.y + 12.5f)
                shapeRenderer.triangle(p.x + 6.5f, p.y + 10f, p.x + 13.5f, p.y + 17.5f, p.x + 2.5f, p.y + 12.5f)
                shapeRenderer.color = Color(0.92f, 0.22f, 0.15f, 1f)
                shapeRenderer.triangle(p.x - 4f, p.y + 14f, p.x + 4f, p.y + 14f, p.x, p.y + 22f)

                // Rìu chiến cầm ngang
                shapeRenderer.color = OUTLINE
                shapeRenderer.rectLine(p.x - 10f, p.y - 3f, p.x + 10f, p.y - 2f, 4.2f)
                shapeRenderer.color = Color(0.85f, 0.88f, 0.92f, 1f)
                shapeRenderer.triangle(p.x + 5f, p.y + 3f, p.x + 13f, p.y + 2f, p.x + 9f, p.y - 7f)
            }

            // 21. YETI: Quái thú người tuyết khổng lồ lông trắng dày, mặt xanh xám, mắt vàng dữ tợn
            EnemyType.YETI -> {
                shapeRenderer.color = SHADOW
                shapeRenderer.ellipse(p.x - 19f, p.y - 17f, 38f, 10f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 13f, p.y - 16f, 10f, 8f)
                shapeRenderer.rect(p.x + 3f, p.y - 16f, 10f, 8f)
                shapeRenderer.circle(p.x, p.y + 1f, 18.5f)
                shapeRenderer.circle(p.x - 14f, p.y + 3f, 9f)
                shapeRenderer.circle(p.x + 14f, p.y + 3f, 9f)
                shapeRenderer.circle(p.x, p.y + 11f, 12f)

                shapeRenderer.color = Color(0.90f, 0.94f, 0.98f, 1f)
                shapeRenderer.circle(p.x, p.y + 1f, 16.5f)
                shapeRenderer.circle(p.x - 13.5f, p.y + 3f, 7.2f)
                shapeRenderer.circle(p.x + 13.5f, p.y + 3f, 7.2f)
                shapeRenderer.circle(p.x, p.y + 11f, 10.2f)

                shapeRenderer.color = Color(0.52f, 0.60f, 0.70f, 1f)
                shapeRenderer.circle(p.x, p.y + 9f, 7f)
                shapeRenderer.color = Color(1f, 0.88f, 0.15f, 1f)
                shapeRenderer.circle(p.x - 3f, p.y + 11f, 1.8f)
                shapeRenderer.circle(p.x + 3f, p.y + 11f, 1.8f)
                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 4.5f, p.y + 4.5f, 9f, 3.2f)
                shapeRenderer.color = Color.WHITE
                shapeRenderer.triangle(p.x - 3.5f, p.y + 5f, p.x - 2f, p.y + 7.5f, p.x - 0.5f, p.y + 5f)
                shapeRenderer.triangle(p.x + 0.5f, p.y + 5f, p.x + 2f, p.y + 7.5f, p.x + 3.5f, p.y + 5f)
            }

            // 22. TROLL_CHIEFTAIN: Thủ lĩnh Troll đội mũ sọ sừng cừu đại, vác trống trận bộ lạc
            EnemyType.TROLL_CHIEFTAIN -> {
                shapeRenderer.color = SHADOW
                shapeRenderer.ellipse(p.x - 16f, p.y - 15f, 32f, 8.5f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.circle(p.x, p.y, 15.5f)
                shapeRenderer.rect(p.x - 12f, p.y - 12f, 24f, 15f)
                shapeRenderer.circle(p.x, p.y + 10f, 10.5f)
                shapeRenderer.triangle(p.x - 8f, p.y + 11f, p.x - 19f, p.y + 22f, p.x - 3f, p.y + 15f)
                shapeRenderer.triangle(p.x + 8f, p.y + 11f, p.x + 19f, p.y + 22f, p.x + 3f, p.y + 15f)

                shapeRenderer.color = Color(0.26f, 0.48f, 0.58f, 1f)
                shapeRenderer.circle(p.x, p.y, 13.5f)
                shapeRenderer.rect(p.x - 10f, p.y - 10f, 20f, 13f)

                shapeRenderer.color = Color(0.94f, 0.90f, 0.80f, 1f)
                shapeRenderer.circle(p.x, p.y + 10f, 8.8f)
                shapeRenderer.triangle(p.x - 7.5f, p.y + 11f, p.x - 17f, p.y + 20.5f, p.x - 3f, p.y + 14f)
                shapeRenderer.triangle(p.x + 7.5f, p.y + 11f, p.x + 17f, p.y + 20.5f, p.x + 3f, p.y + 14f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.circle(p.x - 3.2f, p.y + 10f, 2f)
                shapeRenderer.circle(p.x + 3.2f, p.y + 10f, 2f)
            }

            // 23. ROCKET_RIDER: Pháo thủ cưỡi tên lửa sắt phun lửa đuôi
            EnemyType.ROCKET_RIDER -> {
                shapeRenderer.color = SHADOW
                shapeRenderer.ellipse(p.x - 12f, p.y - 15f, 24f, 6f)

                shapeRenderer.color = Color(1f, 0.42f, 0.05f, 0.95f)
                shapeRenderer.triangle(p.x - 13f, p.y + 4.5f, p.x - 13f, p.y - 4.5f, p.x - 26f, p.y)
                shapeRenderer.color = Color(1f, 0.90f, 0.25f, 1f)
                shapeRenderer.triangle(p.x - 13f, p.y + 2.2f, p.x - 13f, p.y - 2.2f, p.x - 20f, p.y)

                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 14f, p.y - 6.5f, 22f, 13f)
                shapeRenderer.triangle(p.x + 8f, p.y + 6.5f, p.x + 8f, p.y - 6.5f, p.x + 16f, p.y)

                shapeRenderer.color = Color(0.48f, 0.50f, 0.55f, 1f)
                shapeRenderer.rect(p.x - 12.5f, p.y - 5f, 19f, 10f)
                shapeRenderer.color = Color(0.85f, 0.18f, 0.15f, 1f)
                shapeRenderer.triangle(p.x + 6.5f, p.y + 5f, p.x + 6.5f, p.y - 5f, p.x + 14f, p.y)

                shapeRenderer.color = OUTLINE
                shapeRenderer.circle(p.x + 1f, p.y + 6.5f, 7.2f)
                shapeRenderer.color = Color(0.45f, 0.28f, 0.15f, 1f)
                shapeRenderer.circle(p.x + 1f, p.y + 6.5f, 5.8f)
                shapeRenderer.color = Color(0.35f, 0.88f, 1f, 1f)
                shapeRenderer.circle(p.x + 3.5f, p.y + 7f, 2.4f)
            }

            // 24. DARK_SLAYER: Đại kị sĩ hắc ám giáp đen viền vàng, mũ sừng lớn, đại kiếm
            EnemyType.DARK_SLAYER -> {
                shapeRenderer.color = SHADOW
                shapeRenderer.ellipse(p.x - 16f, p.y - 15f, 32f, 8.5f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.circle(p.x, p.y, 16f)
                shapeRenderer.rect(p.x - 13f, p.y - 12f, 26f, 16f)
                shapeRenderer.circle(p.x, p.y + 10f, 10.5f)
                shapeRenderer.triangle(p.x - 8f, p.y + 12f, p.x - 18f, p.y + 23f, p.x - 3f, p.y + 16f)
                shapeRenderer.triangle(p.x + 8f, p.y + 12f, p.x + 18f, p.y + 23f, p.x + 3f, p.y + 16f)

                shapeRenderer.color = Color(0.18f, 0.18f, 0.22f, 1f)
                shapeRenderer.circle(p.x, p.y, 14f)
                shapeRenderer.rect(p.x - 11f, p.y - 10f, 22f, 14f)
                shapeRenderer.color = Color.GOLD
                shapeRenderer.rect(p.x - 11f, p.y - 3f, 22f, 3f)

                shapeRenderer.color = Color(0.24f, 0.24f, 0.28f, 1f)
                shapeRenderer.circle(p.x, p.y + 10f, 8.8f)
                shapeRenderer.color = Color.RED
                shapeRenderer.rect(p.x - 5f, p.y + 9f, 10f, 2.5f)
            }

            // 25. JT: Boss người tuyết khổng lồ J.T.
            EnemyType.JT -> {
                shapeRenderer.color = SHADOW
                shapeRenderer.ellipse(p.x - 26f, p.y - 22f, 52f, 14f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.circle(p.x, p.y, 26f)
                shapeRenderer.rect(p.x - 21f, p.y - 19f, 42f, 24f)
                shapeRenderer.circle(p.x, p.y + 13f, 15.5f)

                shapeRenderer.color = Color(0.88f, 0.93f, 0.98f, 1f)
                shapeRenderer.circle(p.x, p.y, 23.5f)
                shapeRenderer.rect(p.x - 18.5f, p.y - 17f, 37f, 22f)

                shapeRenderer.color = Color(0.52f, 0.58f, 0.66f, 1f)
                shapeRenderer.circle(p.x, p.y + 13f, 13.5f)
                shapeRenderer.color = Color.YELLOW
                shapeRenderer.circle(p.x - 5f, p.y + 15f, 2.8f)
                shapeRenderer.circle(p.x + 5f, p.y + 15f, 2.8f)
                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 7.5f, p.y + 6f, 15f, 5f)
                shapeRenderer.color = Color.WHITE
                shapeRenderer.rect(p.x - 5.5f, p.y + 7.5f, 3f, 3.2f)
                shapeRenderer.rect(p.x + 2.5f, p.y + 7.5f, 3f, 3.2f)
            }

            // 26. SKELETON: Chiến binh bộ xương trắng có viền đen, khiên đồng, kiếm cong
            EnemyType.SKELETON -> {
                shapeRenderer.color = SHADOW
                shapeRenderer.ellipse(p.x - 10f, p.y - 12f, 20f, 6f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 5f, p.y - 10f, 10f, 12f)
                shapeRenderer.circle(p.x, p.y + 6f, 8.2f)

                shapeRenderer.color = Color(0.94f, 0.92f, 0.86f, 1f)
                shapeRenderer.rect(p.x - 2f, p.y - 8f, 4f, 10f)
                shapeRenderer.rectLine(p.x - 5f, p.y - 2f, p.x + 5f, p.y - 2f, 2.2f)
                shapeRenderer.rectLine(p.x - 4.5f, p.y - 5f, p.x + 4.5f, p.y - 5f, 2.2f)
                shapeRenderer.circle(p.x, p.y + 6f, 6.6f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.circle(p.x - 2.5f, p.y + 6f, 1.9f)
                shapeRenderer.circle(p.x + 2.5f, p.y + 6f, 1.9f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.circle(p.x - 7.5f, p.y - 2f, 6.2f)
                shapeRenderer.color = Color(0.75f, 0.48f, 0.20f, 1f)
                shapeRenderer.circle(p.x - 7.5f, p.y - 2f, 4.6f)
            }

            // 27. SKELETON_KNIGHT: Kị sĩ xương đội mũ sắt, khiên sắt, kiếm dài
            EnemyType.SKELETON_KNIGHT -> {
                shapeRenderer.color = SHADOW
                shapeRenderer.ellipse(p.x - 11f, p.y - 12f, 22f, 6.5f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 7.5f, p.y - 10f, 15f, 13f)
                shapeRenderer.circle(p.x, p.y + 6.5f, 8.8f)

                shapeRenderer.color = Color(0.45f, 0.48f, 0.54f, 1f)
                shapeRenderer.rect(p.x - 6f, p.y - 8.5f, 12f, 10.5f)
                shapeRenderer.color = Color(0.94f, 0.92f, 0.86f, 1f)
                shapeRenderer.circle(p.x, p.y + 6f, 7f)
                shapeRenderer.color = Color(0.36f, 0.40f, 0.46f, 1f)
                shapeRenderer.rect(p.x - 7f, p.y + 6.5f, 14f, 6f)
                shapeRenderer.rect(p.x - 1.5f, p.y + 3f, 3f, 5f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.circle(p.x - 3.2f, p.y + 5f, 1.8f)
                shapeRenderer.circle(p.x + 3.2f, p.y + 5f, 1.8f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.circle(p.x - 8.5f, p.y - 2f, 6.8f)
                shapeRenderer.color = Color(0.62f, 0.66f, 0.72f, 1f)
                shapeRenderer.circle(p.x - 8.5f, p.y - 2f, 5.2f)
            }

            // 28. DEMON_SPAWN: Quỷ đỏ địa ngục có sừng cong, đuôi mũi tên, mắt vàng
            EnemyType.DEMON_SPAWN -> {
                shapeRenderer.color = SHADOW
                shapeRenderer.ellipse(p.x - 11f, p.y - 12f, 22f, 6.5f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 8f, p.y - 9f, 16f, 12f)
                shapeRenderer.circle(p.x, p.y + 5.5f, 9f)
                shapeRenderer.triangle(p.x - 6f, p.y + 8f, p.x - 12f, p.y + 17f, p.x - 2f, p.y + 11f)
                shapeRenderer.triangle(p.x + 6f, p.y + 8f, p.x + 12f, p.y + 17f, p.x + 2f, p.y + 11f)

                shapeRenderer.color = Color(0.86f, 0.20f, 0.16f, 1f)
                shapeRenderer.rect(p.x - 6.5f, p.y - 7.5f, 13f, 10f)
                shapeRenderer.circle(p.x, p.y + 5.5f, 7.4f)

                shapeRenderer.color = Color(0.24f, 0.18f, 0.18f, 1f)
                shapeRenderer.triangle(p.x - 5.5f, p.y + 8.5f, p.x - 10.5f, p.y + 15.5f, p.x - 2.5f, p.y + 11f)
                shapeRenderer.triangle(p.x + 5.5f, p.y + 8.5f, p.x + 10.5f, p.y + 15.5f, p.x + 2.5f, p.y + 11f)

                shapeRenderer.color = Color(1f, 0.92f, 0.18f, 1f)
                shapeRenderer.circle(p.x - 2.8f, p.y + 6f, 1.7f)
                shapeRenderer.circle(p.x + 2.8f, p.y + 6f, 1.7f)
            }

            // 29. DEMON_HOUND: Chó săn địa ngục đỏ rực, gai lửa vàng cam trên lưng
            EnemyType.DEMON_HOUND -> {
                shapeRenderer.color = SHADOW
                shapeRenderer.ellipse(p.x - 14f, p.y - 11f, 28f, 6.5f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.ellipse(p.x - 13f, p.y - 6.5f, 25f, 13.5f)
                shapeRenderer.circle(p.x + 8.5f, p.y + 2f, 7.8f)

                shapeRenderer.color = Color(0.76f, 0.18f, 0.14f, 1f)
                shapeRenderer.ellipse(p.x - 11.5f, p.y - 5f, 22f, 10.5f)
                shapeRenderer.circle(p.x + 8.5f, p.y + 2f, 6.2f)

                shapeRenderer.color = Color(1f, 0.65f, 0.12f, 1f)
                shapeRenderer.triangle(p.x - 8f, p.y + 3.5f, p.x - 5f, p.y + 11.5f, p.x - 2f, p.y + 3.5f)
                shapeRenderer.triangle(p.x - 2f, p.y + 4f, p.x + 1f, p.y + 13f, p.x + 4f, p.y + 4f)
                shapeRenderer.color = Color.YELLOW
                shapeRenderer.circle(p.x + 10f, p.y + 3.2f, 1.7f)
            }

            // 30. DEMON_LORD: Chúa quỷ khổng lồ giáp dung nham, sừng đại quỷ
            EnemyType.DEMON_LORD -> {
                shapeRenderer.color = SHADOW
                shapeRenderer.ellipse(p.x - 17f, p.y - 16f, 34f, 9f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.circle(p.x, p.y, 17f)
                shapeRenderer.rect(p.x - 14f, p.y - 13f, 28f, 16f)
                shapeRenderer.circle(p.x, p.y + 11f, 11f)
                shapeRenderer.triangle(p.x - 8f, p.y + 13f, p.x - 21f, p.y + 26f, p.x - 3f, p.y + 18f)
                shapeRenderer.triangle(p.x + 8f, p.y + 13f, p.x + 21f, p.y + 26f, p.x + 3f, p.y + 18f)

                shapeRenderer.color = Color(0.68f, 0.16f, 0.12f, 1f)
                shapeRenderer.circle(p.x, p.y, 15f)
                shapeRenderer.rect(p.x - 12f, p.y - 11f, 24f, 14f)
                shapeRenderer.circle(p.x, p.y + 11f, 9.2f)

                shapeRenderer.color = Color(0.22f, 0.18f, 0.18f, 1f)
                shapeRenderer.rect(p.x - 9.5f, p.y - 7f, 19f, 12f)
                shapeRenderer.color = Color(1f, 0.55f, 0.08f, 1f)
                shapeRenderer.rectLine(p.x - 7f, p.y + 3f, p.x + 7f, p.y - 3f, 2.2f)
                shapeRenderer.rectLine(p.x - 6f, p.y - 3f, p.x + 6f, p.y + 3f, 2.2f)

                shapeRenderer.color = Color(1f, 0.92f, 0.18f, 1f)
                shapeRenderer.circle(p.x - 3.5f, p.y + 12f, 2.2f)
                shapeRenderer.circle(p.x + 3.5f, p.y + 12f, 2.2f)
            }

            // 31. DEMON_IMP: Tiểu quỷ đỏ có cánh dơi
            EnemyType.DEMON_IMP -> {
                shapeRenderer.color = SHADOW
                shapeRenderer.ellipse(p.x - 10f, p.y - 14f, 20f, 6f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.triangle(p.x - 3f, p.y + 2f, p.x - 19f, p.y + 14f, p.x - 11f, p.y - 5f)
                shapeRenderer.triangle(p.x + 3f, p.y + 2f, p.x + 19f, p.y + 14f, p.x + 11f, p.y - 5f)
                shapeRenderer.circle(p.x, p.y, 8.5f)
                shapeRenderer.circle(p.x, p.y + 5.5f, 7f)

                shapeRenderer.color = Color(0.85f, 0.30f, 0.10f, 1f)
                shapeRenderer.triangle(p.x - 3f, p.y + 1.5f, p.x - 17f, p.y + 12.5f, p.x - 10f, p.y - 4f)
                shapeRenderer.triangle(p.x + 3f, p.y + 1.5f, p.x + 17f, p.y + 12.5f, p.x + 10f, p.y - 4f)

                shapeRenderer.color = Color(0.92f, 0.22f, 0.16f, 1f)
                shapeRenderer.circle(p.x, p.y, 6.8f)
                shapeRenderer.circle(p.x, p.y + 5.5f, 5.5f)
                shapeRenderer.color = Color.YELLOW
                shapeRenderer.circle(p.x - 2.2f, p.y + 6f, 1.4f)
                shapeRenderer.circle(p.x + 2.2f, p.y + 6f, 1.4f)
            }

            // 32. MAGMA_ELEMENTAL: Khổng lồ đá núi lửa rực cháy dung nham
            EnemyType.MAGMA_ELEMENTAL -> {
                shapeRenderer.color = SHADOW
                shapeRenderer.ellipse(p.x - 20f, p.y - 18f, 40f, 10f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.circle(p.x, p.y + 1f, 22f)
                shapeRenderer.color = Color(0.24f, 0.19f, 0.18f, 1f)
                shapeRenderer.circle(p.x, p.y + 1f, 19.5f)

                shapeRenderer.color = Color(0.96f, 0.38f, 0.05f, 1f)
                shapeRenderer.circle(p.x, p.y + 1f, 13.5f)
                shapeRenderer.color = Color(1f, 0.86f, 0.20f, 1f)
                shapeRenderer.circle(p.x, p.y + 1f, 8f)

                shapeRenderer.color = Color(0.18f, 0.15f, 0.16f, 1f)
                shapeRenderer.rect(p.x - 18f, p.y - 11f, 9f, 11f)
                shapeRenderer.rect(p.x + 9f, p.y - 11f, 9f, 11f)
                shapeRenderer.rect(p.x - 8f, p.y + 11f, 16f, 9f)

                shapeRenderer.color = Color.YELLOW
                shapeRenderer.circle(p.x - 4.5f, p.y + 14f, 2.4f)
                shapeRenderer.circle(p.x + 4.5f, p.y + 14f, 2.4f)
            }

            // 33. NECROMANCER: Pháp sư chiêu hồn áo choàng đen, trượng đầu lâu lửa xanh
            EnemyType.NECROMANCER -> {
                shapeRenderer.color = SHADOW
                shapeRenderer.ellipse(p.x - 11f, p.y - 12f, 22f, 6.5f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 9f, p.y - 10.5f, 18f, 16f)
                shapeRenderer.circle(p.x, p.y + 6.5f, 9f)

                shapeRenderer.color = Color(0.18f, 0.20f, 0.25f, 1f)
                shapeRenderer.rect(p.x - 7.5f, p.y - 9f, 15f, 14f)
                shapeRenderer.circle(p.x, p.y + 6.5f, 7.2f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.rectLine(p.x - 11.5f, p.y - 9.5f, p.x - 11.5f, p.y + 15f, 4f)
                shapeRenderer.color = Color(0.45f, 0.30f, 0.16f, 1f)
                shapeRenderer.rectLine(p.x - 11.5f, p.y - 9f, p.x - 11.5f, p.y + 14f, 2.2f)
                shapeRenderer.color = Color(0.25f, 0.96f, 0.42f, 1f)
                shapeRenderer.circle(p.x - 11.5f, p.y + 16f, 4.5f)
                shapeRenderer.circle(p.x - 2.5f, p.y + 7f, 1.7f)
                shapeRenderer.circle(p.x + 2.5f, p.y + 7f, 1.7f)
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

            // 35. VEZNAN_DEMON: Boss Vez'nan dạng 2 (Đại quỷ đỏ có cánh)
            EnemyType.VEZNAN_DEMON -> {
                shapeRenderer.color = SHADOW
                shapeRenderer.ellipse(p.x - 26f, p.y - 22f, 52f, 14f)

                shapeRenderer.color = Color(0.38f, 0.20f, 0.12f, 1f)
                shapeRenderer.triangle(p.x - 12f, p.y + 5f, p.x - 36f, p.y + 36f, p.x - 28f, p.y - 12f)
                shapeRenderer.triangle(p.x + 12f, p.y + 5f, p.x + 36f, p.y + 36f, p.x + 28f, p.y - 12f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.circle(p.x, p.y, 23.5f)
                shapeRenderer.rect(p.x - 17.5f, p.y - 21f, 35f, 26f)
                shapeRenderer.circle(p.x, p.y + 20f, 15.5f)

                shapeRenderer.color = Color(0.78f, 0.26f, 0.12f, 1f)
                shapeRenderer.circle(p.x, p.y, 21.5f)
                shapeRenderer.rect(p.x - 15.5f, p.y - 19.5f, 31f, 23.5f)
                shapeRenderer.circle(p.x, p.y + 20f, 13.5f)

                shapeRenderer.color = Color(0.90f, 0.88f, 0.80f, 1f)
                shapeRenderer.triangle(p.x - 12f, p.y + 22f, p.x - 22f, p.y + 38f, p.x - 6f, p.y + 28f)
                shapeRenderer.triangle(p.x + 12f, p.y + 22f, p.x + 22f, p.y + 38f, p.x + 6f, p.y + 28f)

                shapeRenderer.color = Color(1f, 0.95f, 0.2f, 1f)
                shapeRenderer.circle(p.x - 5f, p.y + 21f, 3f)
                shapeRenderer.circle(p.x + 5f, p.y + 21f, 3f)
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
