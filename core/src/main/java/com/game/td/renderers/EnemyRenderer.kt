package com.game.td.renderers

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.game.td.ecs.components.*

object EnemyRenderer {

    // =========================================================================
    // HÀM VẼ TÚI TRỨNG NHỆN (SPIDER EGG)
    // =========================================================================
    fun renderEgg(shapeRenderer: ShapeRenderer, egg: Entity) {
        val trans = egg.getComponent(TransformComponent::class.java) ?: return
        val p = trans.position

        shapeRenderer.color = Color(0.85f, 0.85f, 0.80f, 1f)
        shapeRenderer.circle(p.x, p.y + 2f, 9.5f)
        shapeRenderer.circle(p.x, p.y - 2f, 11f)

        shapeRenderer.color = Color(0.40f, 0.65f, 0.30f, 0.85f)
        shapeRenderer.circle(p.x - 3f, p.y + 1f, 3.5f)
        shapeRenderer.circle(p.x + 3f, p.y - 2f, 4f)
        shapeRenderer.circle(p.x, p.y + 5f, 2.5f)
    }

    // =========================================================================
    // HÀM VẼ TẤT CẢ 35 LOẠI QUÁI VẬT & BOSS (ĐÁNH SỐ THỨ TỰ TỪ 01 ĐẾN 35)
    // =========================================================================
    fun render(shapeRenderer: ShapeRenderer, enemy: Entity) {
        val trans = enemy.getComponent(TransformComponent::class.java) ?: return
        val hp = enemy.getComponent(HealthComponent::class.java) ?: return
        val eComp = enemy.getComponent(EnemyComponent::class.java) ?: return
        val p = trans.position

        // ---------------------------------------------------------------------
        // DẠNG BIẾN HÌNH ĐẶC BIỆT: CỪU (POLYMORPH CỦA SORCERER MAGE)
        // ---------------------------------------------------------------------
        if (eComp.isSheep) {
            shapeRenderer.color = Color.WHITE
            shapeRenderer.circle(p.x, p.y, 11f)
            shapeRenderer.circle(p.x + 6f, p.y + 4f, 6.5f)
            shapeRenderer.color = Color(0.95f, 0.85f, 0.75f, 1f)
            shapeRenderer.circle(p.x + 8f, p.y + 5f, 4.5f)
            shapeRenderer.color = Color.BLACK
            shapeRenderer.circle(p.x + 9.5f, p.y + 6f, 1.2f)
            renderHpBar(shapeRenderer, p.x, p.y + 14f, 20f, hp)
            return
        }

        // ---------------------------------------------------------------------
        // VẼ TỪNG LOẠI QUÁI VẬT & BOSS CHUẨN WIKI KINGDOM RUSH
        // ---------------------------------------------------------------------
        when (eComp.enemyType) {

            // 01. GOBLIN: Da xanh lá sáng, hai tai nhọn vểnh ngang, khố da nâu, dao găm
            EnemyType.GOBLIN -> {
                shapeRenderer.color = Color(0.42f, 0.72f, 0.22f, 1f)
                shapeRenderer.circle(p.x, p.y + 4f, 6.5f)
                shapeRenderer.rect(p.x - 4.5f, p.y - 6f, 9f, 8f)
                // Đôi tai nhọn vểnh
                shapeRenderer.triangle(p.x - 6f, p.y + 5f, p.x - 11f, p.y + 8f, p.x - 4f, p.y + 2f)
                shapeRenderer.triangle(p.x + 6f, p.y + 5f, p.x + 11f, p.y + 8f, p.x + 4f, p.y + 2f)
                // Mắt đen
                shapeRenderer.color = Color.BLACK
                shapeRenderer.circle(p.x - 2f, p.y + 5f, 1.4f)
                shapeRenderer.circle(p.x + 2f, p.y + 5f, 1.4f)
                // Khăn khố da nâu
                shapeRenderer.color = Color(0.45f, 0.30f, 0.15f, 1f)
                shapeRenderer.rect(p.x - 5f, p.y - 6f, 10f, 4f)
            }

            // 02. ORC: Da xanh rêu sẫm, mũ sắt che trán, hai răng nanh dưới nhô ngược
            EnemyType.ORC -> {
                shapeRenderer.color = Color(0.28f, 0.50f, 0.20f, 1f)
                shapeRenderer.circle(p.x, p.y + 5f, 8.5f)
                shapeRenderer.rect(p.x - 7f, p.y - 8f, 14f, 12f)
                // Mũ giáp sắt xám
                shapeRenderer.color = Color(0.45f, 0.48f, 0.52f, 1f)
                shapeRenderer.rect(p.x - 7.5f, p.y + 6f, 15f, 5f)
                // Hai răng nanh trắng nhô lên
                shapeRenderer.color = Color.WHITE
                shapeRenderer.triangle(p.x - 3f, p.y + 3f, p.x - 2f, p.y + 7f, p.x - 1f, p.y + 3f)
                shapeRenderer.triangle(p.x + 1f, p.y + 3f, p.x + 2f, p.y + 7f, p.x + 3f, p.y + 3f)
            }

            // 03. WULF: Sói xám rừng 4 chân chạy nhanh, tai nhọn dựng đứng
            EnemyType.WULF -> {
                shapeRenderer.color = Color(0.65f, 0.65f, 0.68f, 1f)
                shapeRenderer.ellipse(p.x - 11f, p.y - 5f, 22f, 11f)
                shapeRenderer.circle(p.x + 7f, p.y + 2f, 5.5f)
                shapeRenderer.triangle(p.x + 6f, p.y + 6f, p.x + 8f, p.y + 11f, p.x + 10f, p.y + 6f)
                shapeRenderer.color = Color.BLACK
                shapeRenderer.circle(p.x + 8.5f, p.y + 3f, 1.3f)
            }

            // 04. WORG: Sói đen hung dữ, sống lưng mọc bờm gai nhọn dựng đứng, mắt đỏ ngầu
            EnemyType.WORG -> {
                shapeRenderer.color = Color(0.28f, 0.26f, 0.28f, 1f)
                shapeRenderer.ellipse(p.x - 14f, p.y - 7f, 28f, 15f)
                // Bờm gai nhọn đen trên lưng
                shapeRenderer.color = Color(0.18f, 0.16f, 0.18f, 1f)
                shapeRenderer.triangle(p.x - 8f, p.y + 5f, p.x - 4f, p.y + 14f, p.x, p.y + 5f)
                shapeRenderer.triangle(p.x - 1f, p.y + 5f, p.x + 3f, p.y + 15f, p.x + 7f, p.y + 5f)
                // Đầu sói mắt đỏ
                shapeRenderer.color = Color(0.35f, 0.32f, 0.35f, 1f)
                shapeRenderer.circle(p.x + 9f, p.y + 3f, 7.5f)
                shapeRenderer.color = Color.RED
                shapeRenderer.circle(p.x + 11f, p.y + 5f, 2f)
            }

            // 05. SHAMAN: Pháp sư Orc đeo mặt nạ xương trắng, bờm lông đỏ, trượng ngọc xanh
            EnemyType.SHAMAN -> {
                shapeRenderer.color = Color(0.35f, 0.58f, 0.22f, 1f)
                shapeRenderer.circle(p.x, p.y + 5f, 7.5f)
                shapeRenderer.rect(p.x - 6f, p.y - 7f, 12f, 10f)
                // Bờm lông vũ đỏ cao vút
                shapeRenderer.color = Color(0.92f, 0.30f, 0.15f, 1f)
                shapeRenderer.triangle(p.x - 5f, p.y + 9f, p.x, p.y + 17f, p.x + 5f, p.y + 9f)
                // Mặt nạ xương trắng
                shapeRenderer.color = Color(0.90f, 0.90f, 0.85f, 1f)
                shapeRenderer.circle(p.x, p.y + 4f, 4.5f)
                // Trượng phép ngọc xanh lam
                shapeRenderer.color = Color(0.40f, 0.28f, 0.16f, 1f)
                shapeRenderer.rectLine(p.x + 7f, p.y - 8f, p.x + 11f, p.y + 12f, 2f)
                shapeRenderer.color = Color.CYAN
                shapeRenderer.circle(p.x + 11f, p.y + 13f, 3f)
            }

            // 06. BANDIT: Thổ phỉ áo da nâu, khăn đỏ bịt kín nửa mặt, cầm dao găm
            EnemyType.BANDIT -> {
                shapeRenderer.color = Color(0.48f, 0.32f, 0.18f, 1f)
                shapeRenderer.circle(p.x, p.y, 9.5f)
                shapeRenderer.rect(p.x - 6f, p.y - 8f, 12f, 8f)
                shapeRenderer.color = Color(0.40f, 0.25f, 0.15f, 1f)
                shapeRenderer.circle(p.x, p.y + 6f, 7.5f)
                // Khăn đỏ bịt mặt đặc trưng
                shapeRenderer.color = Color(0.85f, 0.18f, 0.18f, 1f)
                shapeRenderer.rect(p.x - 6.5f, p.y + 2f, 13f, 5.5f)
                shapeRenderer.triangle(p.x - 2f, p.y + 2f, p.x + 2f, p.y + 2f, p.x, p.y - 2f)
                // Mắt
                shapeRenderer.color = Color(0.1f, 0.1f, 0.1f, 1f)
                shapeRenderer.circle(p.x - 2.5f, p.y + 8f, 1.5f)
                shapeRenderer.circle(p.x + 2.5f, p.y + 8f, 1.5f)
                // Dao găm
                shapeRenderer.color = Color.LIGHT_GRAY
                shapeRenderer.rectLine(p.x + 6f, p.y - 4f, p.x + 12f, p.y + 4f, 2f)
            }

            // 07. BRIGAND: Sơn tặc áo da đinh tán, mũ sắt tròn viền đai, khiên gỗ tròn
            EnemyType.BRIGAND -> {
                shapeRenderer.color = Color(0.55f, 0.40f, 0.25f, 1f)
                shapeRenderer.circle(p.x, p.y, 11f)
                shapeRenderer.rect(p.x - 8f, p.y - 9f, 16f, 10f)
                // Mũ sắt tròn
                shapeRenderer.color = Color(0.50f, 0.52f, 0.58f, 1f)
                shapeRenderer.circle(p.x, p.y + 7f, 8f)
                shapeRenderer.color = Color(0.35f, 0.38f, 0.42f, 1f)
                shapeRenderer.rect(p.x - 8f, p.y + 5f, 16f, 3.5f)
                // Mặt có ria mép
                shapeRenderer.color = Color(0.85f, 0.70f, 0.55f, 1f)
                shapeRenderer.circle(p.x, p.y + 4f, 4.5f)
                shapeRenderer.color = Color.BLACK
                shapeRenderer.circle(p.x - 2.5f, p.y + 6f, 1.4f)
                shapeRenderer.circle(p.x + 2.5f, p.y + 6f, 1.4f)
                // Khiên gỗ tròn
                shapeRenderer.color = Color(0.60f, 0.35f, 0.15f, 1f)
                shapeRenderer.circle(p.x - 9f, p.y, 6f)
            }

            // 08. MARAUDER: Tướng cướp hộ pháp lực lưỡng, mũ sừng trâu cong, đai da chéo, đại rìu
            EnemyType.MARAUDER -> {
                shapeRenderer.color = Color(0.40f, 0.38f, 0.42f, 1f)
                shapeRenderer.circle(p.x, p.y, 14f)
                shapeRenderer.rect(p.x - 10f, p.y - 12f, 20f, 14f)
                // Đai da chéo ngực
                shapeRenderer.color = Color(0.65f, 0.30f, 0.15f, 1f)
                shapeRenderer.rectLine(p.x - 9f, p.y + 8f, p.x + 9f, p.y - 8f, 3.5f)
                // Mũ giáp sắt và sừng trâu cong
                shapeRenderer.color = Color(0.30f, 0.32f, 0.36f, 1f)
                shapeRenderer.circle(p.x, p.y + 9f, 9f)
                shapeRenderer.color = Color(0.85f, 0.85f, 0.80f, 1f)
                shapeRenderer.triangle(p.x - 7f, p.y + 11f, p.x - 16f, p.y + 20f, p.x - 3f, p.y + 14f)
                shapeRenderer.triangle(p.x + 7f, p.y + 11f, p.x + 16f, p.y + 20f, p.x + 3f, p.y + 14f)
                // Khe mắt đỏ
                shapeRenderer.color = Color.RED
                shapeRenderer.circle(p.x - 3f, p.y + 9f, 1.8f)
                shapeRenderer.circle(p.x + 3f, p.y + 9f, 1.8f)
                // Đại rìu hai lưỡi
                shapeRenderer.color = Color(0.45f, 0.30f, 0.15f, 1f)
                shapeRenderer.rectLine(p.x + 8f, p.y - 12f, p.x + 15f, p.y + 14f, 3f)
                shapeRenderer.color = Color.LIGHT_GRAY
                shapeRenderer.triangle(p.x + 13f, p.y + 8f, p.x + 22f, p.y + 16f, p.x + 17f, p.y + 3f)
            }

            // 09. OGRE: Khổng lồ xanh bụng phệ, răng nanh ngược, vác thân cây chùy gỗ gai
            EnemyType.OGRE -> {
                shapeRenderer.color = Color(0.58f, 0.55f, 0.35f, 1f)
                shapeRenderer.circle(p.x, p.y, 18f)
                shapeRenderer.rect(p.x - 12f, p.y - 14f, 24f, 16f)
                shapeRenderer.circle(p.x, p.y + 10f, 10f)
                // Răng nanh ngược
                shapeRenderer.color = Color.WHITE
                shapeRenderer.triangle(p.x - 4f, p.y + 7f, p.x - 3f, p.y + 12f, p.x - 2f, p.y + 7f)
                shapeRenderer.triangle(p.x + 2f, p.y + 7f, p.x + 3f, p.y + 12f, p.x + 4f, p.y + 7f)
                // Chùy gỗ gai
                shapeRenderer.color = Color(0.45f, 0.30f, 0.15f, 1f)
                shapeRenderer.rectLine(p.x + 10f, p.y - 12f, p.x + 18f, p.y + 16f, 3.5f)
                shapeRenderer.circle(p.x + 18f, p.y + 16f, 6.5f)
            }

            // 10. GIANT_SPIDER: Nhện xanh rêu bụng đốm vàng, 8 chân phân đốt xòe đều hai bên
            EnemyType.GIANT_SPIDER -> {
                // 8 chân nhện phân đốt xòe rộng
                shapeRenderer.color = Color(0.12f, 0.16f, 0.10f, 1f)
                // 4 chân trái
                shapeRenderer.rectLine(p.x - 4f, p.y + 4f, p.x - 16f, p.y + 12f, 2f)
                shapeRenderer.rectLine(p.x - 16f, p.y + 12f, p.x - 20f, p.y + 5f, 1.8f)
                shapeRenderer.rectLine(p.x - 5f, p.y + 1f, p.x - 18f, p.y + 6f, 2f)
                shapeRenderer.rectLine(p.x - 18f, p.y + 6f, p.x - 22f, p.y - 1f, 1.8f)
                shapeRenderer.rectLine(p.x - 5f, p.y - 2f, p.x - 17f, p.y - 4f, 2f)
                shapeRenderer.rectLine(p.x - 17f, p.y - 4f, p.x - 21f, p.y - 10f, 1.8f)
                shapeRenderer.rectLine(p.x - 4f, p.y - 5f, p.x - 14f, p.y - 12f, 2f)
                shapeRenderer.rectLine(p.x - 14f, p.y - 12f, p.x - 16f, p.y - 18f, 1.8f)
                // 4 chân phải
                shapeRenderer.rectLine(p.x + 4f, p.y + 4f, p.x + 16f, p.y + 12f, 2f)
                shapeRenderer.rectLine(p.x + 16f, p.y + 12f, p.x + 20f, p.y + 5f, 1.8f)
                shapeRenderer.rectLine(p.x + 5f, p.y + 1f, p.x + 18f, p.y + 6f, 2f)
                shapeRenderer.rectLine(p.x + 18f, p.y + 6f, p.x + 22f, p.y - 1f, 1.8f)
                shapeRenderer.rectLine(p.x + 5f, p.y - 2f, p.x + 17f, p.y - 4f, 2f)
                shapeRenderer.rectLine(p.x + 17f, p.y - 4f, p.x + 21f, p.y - 10f, 1.8f)
                shapeRenderer.rectLine(p.x + 4f, p.y - 5f, p.x + 14f, p.y - 12f, 2f)
                shapeRenderer.rectLine(p.x + 14f, p.y - 12f, p.x + 16f, p.y - 18f, 1.8f)

                // Bụng nhện xanh rêu
                shapeRenderer.color = Color(0.24f, 0.36f, 0.18f, 1f)
                shapeRenderer.circle(p.x, p.y - 2f, 10.5f)
                // Đốm vàng trên lưng
                shapeRenderer.color = Color(0.88f, 0.85f, 0.20f, 1f)
                shapeRenderer.circle(p.x - 3f, p.y - 1f, 2.5f)
                shapeRenderer.circle(p.x + 3f, p.y - 1f, 2.5f)
                shapeRenderer.circle(p.x, p.y - 5f, 2.2f)

                // Đầu và mắt đỏ
                shapeRenderer.color = Color(0.18f, 0.26f, 0.12f, 1f)
                shapeRenderer.circle(p.x, p.y + 6f, 6f)
                shapeRenderer.color = Color.RED
                shapeRenderer.circle(p.x - 2f, p.y + 7f, 1.6f)
                shapeRenderer.circle(p.x + 2f, p.y + 7f, 1.6f)
            }

            // 11. SPIDER_MATRIARCH: Nhện chúa khổng lồ 8 chân to lông lá, bụng tròn vân đỏ/cam độc
            EnemyType.SPIDER_MATRIARCH -> {
                // 8 chân lớn
                shapeRenderer.color = Color(0.12f, 0.10f, 0.12f, 1f)
                for (side in listOf(-1f, 1f)) {
                    shapeRenderer.rectLine(p.x + side * 6f, p.y + 6f, p.x + side * 22f, p.y + 18f, 3f)
                    shapeRenderer.rectLine(p.x + side * 22f, p.y + 18f, p.x + side * 28f, p.y + 8f, 2.5f)
                    shapeRenderer.rectLine(p.x + side * 8f, p.y + 2f, p.x + side * 25f, p.y + 9f, 3f)
                    shapeRenderer.rectLine(p.x + side * 25f, p.y + 9f, p.x + side * 30f, p.y - 2f, 2.5f)
                    shapeRenderer.rectLine(p.x + side * 8f, p.y - 3f, p.x + side * 24f, p.y - 6f, 3f)
                    shapeRenderer.rectLine(p.x + side * 24f, p.y - 6f, p.x + side * 29f, p.y - 16f, 2.5f)
                    shapeRenderer.rectLine(p.x + side * 6f, p.y - 8f, p.x + side * 20f, p.y - 18f, 3f)
                    shapeRenderer.rectLine(p.x + side * 20f, p.y - 18f, p.x + side * 24f, p.y - 26f, 2.5f)
                }

                // Bụng tròn to sẫm màu
                shapeRenderer.color = Color(0.16f, 0.18f, 0.15f, 1f)
                shapeRenderer.circle(p.x, p.y - 3f, 16f)

                // Vân đỏ cam độc tố
                shapeRenderer.color = Color(0.85f, 0.20f, 0.15f, 1f)
                shapeRenderer.circle(p.x, p.y - 3f, 10f)
                shapeRenderer.color = Color(1f, 0.75f, 0.15f, 1f)
                shapeRenderer.circle(p.x, p.y - 3f, 5f)

                // Đầu mắt đỏ
                shapeRenderer.color = Color(0.12f, 0.14f, 0.12f, 1f)
                shapeRenderer.circle(p.x, p.y + 9f, 8.5f)
                shapeRenderer.color = Color.RED
                shapeRenderer.circle(p.x - 3.5f, p.y + 10f, 2.2f)
                shapeRenderer.circle(p.x + 3.5f, p.y + 10f, 2.2f)
            }

            // 12. SPIDER_HATCHLING: Nhện con tí hon 6 chân bò nhanh
            EnemyType.SPIDER_HATCHLING -> {
                shapeRenderer.color = Color(0.15f, 0.18f, 0.12f, 1f)
                shapeRenderer.rectLine(p.x - 3f, p.y + 2f, p.x - 9f, p.y + 6f, 1.5f)
                shapeRenderer.rectLine(p.x - 3f, p.y - 1f, p.x - 10f, p.y - 1f, 1.5f)
                shapeRenderer.rectLine(p.x - 3f, p.y - 3f, p.x - 8f, p.y - 7f, 1.5f)
                shapeRenderer.rectLine(p.x + 3f, p.y + 2f, p.x + 9f, p.y + 6f, 1.5f)
                shapeRenderer.rectLine(p.x + 3f, p.y - 1f, p.x + 10f, p.y - 1f, 1.5f)
                shapeRenderer.rectLine(p.x + 3f, p.y - 3f, p.x + 8f, p.y - 7f, 1.5f)

                shapeRenderer.color = Color(0.28f, 0.38f, 0.20f, 1f)
                shapeRenderer.circle(p.x, p.y, 5.5f)
                shapeRenderer.color = Color.YELLOW
                shapeRenderer.circle(p.x + 2f, p.y + 1f, 1.3f)
            }

            // 13. DARK_KNIGHT: Kị sĩ giáp đen Gothic, khiên tam giác, khe mắt đỏ rực
            EnemyType.DARK_KNIGHT -> {
                shapeRenderer.color = Color(0.18f, 0.18f, 0.22f, 1f)
                shapeRenderer.circle(p.x, p.y, 14f)
                shapeRenderer.rect(p.x - 9f, p.y - 12f, 18f, 14f)
                // Khiên tam giác đen
                shapeRenderer.color = Color(0.10f, 0.10f, 0.12f, 1f)
                shapeRenderer.triangle(p.x - 15f, p.y + 7f, p.x - 5f, p.y + 7f, p.x - 10f, p.y - 8f)
                // Mũ giáp sắt khe mắt đỏ
                shapeRenderer.color = Color(0.24f, 0.24f, 0.28f, 1f)
                shapeRenderer.circle(p.x, p.y + 8f, 8f)
                shapeRenderer.color = Color.RED
                shapeRenderer.rect(p.x - 4.5f, p.y + 8f, 9f, 2.5f)
            }

            // 14. GARGOYLE: Quỷ đá bay cánh dơi xám tro, sừng đá nhọn, mắt đỏ
            EnemyType.GARGOYLE -> {
                shapeRenderer.color = Color(0.36f, 0.38f, 0.44f, 1f)
                shapeRenderer.triangle(p.x - 5f, p.y + 2f, p.x - 24f, p.y + 18f, p.x - 14f, p.y - 6f)
                shapeRenderer.triangle(p.x + 5f, p.y + 2f, p.x + 24f, p.y + 18f, p.x + 14f, p.y - 6f)
                shapeRenderer.color = Color(0.24f, 0.26f, 0.30f, 1f)
                shapeRenderer.triangle(p.x - 4f, p.y, p.x - 18f, p.y + 12f, p.x - 10f, p.y - 4f)
                shapeRenderer.triangle(p.x + 4f, p.y, p.x + 18f, p.y + 12f, p.x + 10f, p.y - 4f)

                shapeRenderer.color = Color(0.50f, 0.52f, 0.58f, 1f)
                shapeRenderer.circle(p.x, p.y, 9.5f)

                shapeRenderer.color = Color(0.42f, 0.44f, 0.48f, 1f)
                shapeRenderer.circle(p.x, p.y + 6f, 6.5f)
                shapeRenderer.triangle(p.x - 5f, p.y + 8f, p.x - 9f, p.y + 17f, p.x - 2f, p.y + 10f)
                shapeRenderer.triangle(p.x + 5f, p.y + 8f, p.x + 9f, p.y + 17f, p.x + 2f, p.y + 10f)

                shapeRenderer.color = Color.RED
                shapeRenderer.circle(p.x - 2.5f, p.y + 7f, 1.6f)
                shapeRenderer.circle(p.x + 2.5f, p.y + 7f, 1.6f)
            }

            // 15. SHADOW_ARCHER: Cung thủ bóng đêm áo choàng tím than, mắt vàng ma mị, cung đen
            EnemyType.SHADOW_ARCHER -> {
                shapeRenderer.color = Color(0.25f, 0.18f, 0.32f, 1f)
                shapeRenderer.rect(p.x - 7f, p.y - 9f, 14f, 15f)
                shapeRenderer.circle(p.x, p.y + 6f, 7.5f)
                shapeRenderer.color = Color.YELLOW
                shapeRenderer.circle(p.x - 2.5f, p.y + 6f, 1.8f)
                shapeRenderer.circle(p.x + 2.5f, p.y + 6f, 1.8f)
                shapeRenderer.color = Color(0.15f, 0.15f, 0.18f, 1f)
                shapeRenderer.rectLine(p.x + 7f, p.y - 10f, p.x + 12f, p.y + 10f, 2.2f)
            }

            // 16. GOLEM_HEAD: Đầu golem đá sắt 2 sừng nhọn lăn tròn, khe mắt cam
            EnemyType.GOLEM_HEAD -> {
                shapeRenderer.color = Color(0.42f, 0.40f, 0.38f, 1f)
                shapeRenderer.circle(p.x, p.y, 11f)
                shapeRenderer.color = Color(0.28f, 0.26f, 0.25f, 1f)
                shapeRenderer.triangle(p.x - 7f, p.y + 6f, p.x - 14f, p.y + 15f, p.x - 3f, p.y + 9f)
                shapeRenderer.triangle(p.x + 7f, p.y + 6f, p.x + 14f, p.y + 15f, p.x + 3f, p.y + 9f)
                shapeRenderer.color = Color(0.95f, 0.45f, 0.15f, 1f)
                shapeRenderer.rect(p.x - 4f, p.y + 1f, 8f, 2.5f)
            }

            // 17. JUGGERNAUT: Boss người máy đá rỉ sét 1 sừng sắt cong, tay đại bác tròn
            EnemyType.JUGGERNAUT -> {
                shapeRenderer.color = Color(0.42f, 0.36f, 0.32f, 1f)
                shapeRenderer.circle(p.x, p.y, 25f)
                shapeRenderer.rect(p.x - 18f, p.y - 18f, 36f, 22f)
                // Đầu golem 1 sừng
                shapeRenderer.circle(p.x, p.y + 14f, 12f)
                shapeRenderer.color = Color(0.30f, 0.25f, 0.22f, 1f)
                shapeRenderer.triangle(p.x - 8f, p.y + 16f, p.x - 22f, p.y + 32f, p.x - 3f, p.y + 22f)
                shapeRenderer.color = Color(1f, 0.65f, 0.10f, 1f)
                shapeRenderer.rect(p.x - 6f, p.y + 13f, 12f, 3.5f)
                // Nòng súng đại bác
                shapeRenderer.color = Color(0.25f, 0.22f, 0.20f, 1f)
                shapeRenderer.circle(p.x + 22f, p.y + 6f, 8f)
                shapeRenderer.color = Color.BLACK
                shapeRenderer.circle(p.x + 22f, p.y + 6f, 4.5f)
            }

            // 18. TROLL: Người rừng Troll xanh biển, tóc Mohawk đỏ dựng đứng, răng nanh dưới
            EnemyType.TROLL -> {
                shapeRenderer.color = Color(0.38f, 0.52f, 0.65f, 1f)
                shapeRenderer.circle(p.x, p.y, 12f)
                shapeRenderer.rect(p.x - 8f, p.y - 10f, 16f, 11f)
                shapeRenderer.circle(p.x, p.y + 7f, 8f)
                // Tóc mohawk đỏ
                shapeRenderer.color = Color(0.92f, 0.25f, 0.15f, 1f)
                shapeRenderer.triangle(p.x - 4f, p.y + 11f, p.x, p.y + 20f, p.x + 4f, p.y + 11f)
                // Răng nanh dưới
                shapeRenderer.color = Color.WHITE
                shapeRenderer.triangle(p.x - 3f, p.y + 5f, p.x - 2f, p.y + 9f, p.x - 1f, p.y + 5f)
                shapeRenderer.triangle(p.x + 1f, p.y + 5f, p.x + 2f, p.y + 9f, p.x + 3f, p.y + 5f)
            }

            // 19. WINTER_WOLF: Sói tuyết lông trắng muốt, mắt xanh băng giá
            EnemyType.WINTER_WOLF -> {
                shapeRenderer.color = Color(0.88f, 0.94f, 0.98f, 1f)
                shapeRenderer.ellipse(p.x - 13f, p.y - 6f, 26f, 13f)
                shapeRenderer.circle(p.x + 8f, p.y + 2f, 6.5f)
                shapeRenderer.triangle(p.x + 6f, p.y + 6f, p.x + 9f, p.y + 12f, p.x + 11f, p.y + 6f)
                shapeRenderer.color = Color(0.20f, 0.75f, 1f, 1f)
                shapeRenderer.circle(p.x + 9f, p.y + 4f, 1.6f)
            }

            // 20. TROLL_CHAMPION: Troll chiến binh giáp trụ, mũ sắt 2 sừng nhọn, tóc đỏ
            EnemyType.TROLL_CHAMPION -> {
                shapeRenderer.color = Color(0.35f, 0.48f, 0.58f, 1f)
                shapeRenderer.circle(p.x, p.y, 14f)
                shapeRenderer.rect(p.x - 10f, p.y - 12f, 20f, 13f)
                shapeRenderer.color = Color(0.50f, 0.52f, 0.58f, 1f)
                shapeRenderer.circle(p.x, p.y + 8f, 9f)
                shapeRenderer.color = Color(0.85f, 0.85f, 0.80f, 1f)
                shapeRenderer.triangle(p.x - 6f, p.y + 10f, p.x - 13f, p.y + 18f, p.x - 2f, p.y + 12f)
                shapeRenderer.triangle(p.x + 6f, p.y + 10f, p.x + 13f, p.y + 18f, p.x + 2f, p.y + 12f)
                shapeRenderer.color = Color(0.92f, 0.25f, 0.15f, 1f)
                shapeRenderer.circle(p.x, p.y + 14f, 3.5f)
            }

            // 21. YETI: Vượn tuyết khổng lồ lông trắng, mặt xanh xám dữ tợn, răng đá
            EnemyType.YETI -> {
                shapeRenderer.color = Color(0.92f, 0.94f, 0.98f, 1f)
                shapeRenderer.circle(p.x, p.y, 19f)
                shapeRenderer.rect(p.x - 14f, p.y - 15f, 28f, 18f)
                shapeRenderer.color = Color(0.55f, 0.60f, 0.68f, 1f)
                shapeRenderer.circle(p.x, p.y + 8f, 9.5f)
                shapeRenderer.color = Color.YELLOW
                shapeRenderer.circle(p.x - 3.5f, p.y + 10f, 2f)
                shapeRenderer.circle(p.x + 3.5f, p.y + 10f, 2f)
                shapeRenderer.color = Color(0.15f, 0.15f, 0.18f, 1f)
                shapeRenderer.rect(p.x - 4f, p.y + 4f, 8f, 3.5f)
            }

            // 22. TROLL_CHIEFTAIN: Thủ lĩnh Troll mặt nạ sọ thú có cặp sừng cừu khổng lồ cong
            EnemyType.TROLL_CHIEFTAIN -> {
                shapeRenderer.color = Color(0.30f, 0.42f, 0.52f, 1f)
                shapeRenderer.circle(p.x, p.y, 16f)
                shapeRenderer.rect(p.x - 12f, p.y - 14f, 24f, 15f)
                shapeRenderer.color = Color(0.92f, 0.90f, 0.82f, 1f)
                shapeRenderer.circle(p.x, p.y + 9f, 9.5f)
                shapeRenderer.triangle(p.x - 8f, p.y + 11f, p.x - 18f, p.y + 22f, p.x - 3f, p.y + 14f)
                shapeRenderer.triangle(p.x + 8f, p.y + 11f, p.x + 18f, p.y + 22f, p.x + 3f, p.y + 14f)
                shapeRenderer.color = Color.BLACK
                shapeRenderer.circle(p.x - 3.5f, p.y + 9f, 2f)
                shapeRenderer.circle(p.x + 3.5f, p.y + 9f, 2f)
            }

            // 23. ROCKET_RIDER: Goblin phi công đội mũ da kính bảo hộ cưỡi tên lửa phản lực
            EnemyType.ROCKET_RIDER -> {
                shapeRenderer.color = Color(1f, 0.40f, 0.05f, 0.95f)
                shapeRenderer.triangle(p.x - 14f, p.y + 4f, p.x - 14f, p.y - 4f, p.x - 26f, p.y)
                shapeRenderer.color = Color(1f, 0.90f, 0.25f, 1f)
                shapeRenderer.triangle(p.x - 14f, p.y + 2f, p.x - 14f, p.y - 2f, p.x - 21f, p.y)

                shapeRenderer.color = Color(0.45f, 0.48f, 0.52f, 1f)
                shapeRenderer.rect(p.x - 13f, p.y - 5f, 12f, 10f)
                shapeRenderer.color = Color(0.68f, 0.70f, 0.75f, 1f)
                shapeRenderer.circle(p.x - 1f, p.y, 5f)

                shapeRenderer.color = Color(0.28f, 0.65f, 0.25f, 1f)
                shapeRenderer.circle(p.x + 4f, p.y - 2f, 8f)
                shapeRenderer.color = Color(0.42f, 0.25f, 0.15f, 1f)
                shapeRenderer.circle(p.x + 6f, p.y + 5f, 6.5f)

                shapeRenderer.color = Color(0.85f, 0.75f, 0.20f, 1f)
                shapeRenderer.circle(p.x + 8f, p.y + 6f, 3.2f)
                shapeRenderer.color = Color(0.40f, 0.85f, 1f, 1f)
                shapeRenderer.circle(p.x + 8f, p.y + 6f, 2f)
            }

            // 24. DARK_SLAYER: Đại sát thủ giáp đen viền vàng hoàng kim, sừng trâu, đại kiếm đỏ
            EnemyType.DARK_SLAYER -> {
                shapeRenderer.color = Color(0.12f, 0.12f, 0.14f, 1f)
                shapeRenderer.circle(p.x, p.y, 16.5f)
                shapeRenderer.rect(p.x - 12f, p.y - 14f, 24f, 16f)

                shapeRenderer.color = Color.GOLD
                shapeRenderer.rect(p.x - 10f, p.y - 2f, 20f, 3f)

                shapeRenderer.color = Color(0.20f, 0.20f, 0.25f, 1f)
                shapeRenderer.circle(p.x, p.y + 10f, 9.5f)
                shapeRenderer.color = Color(0.10f, 0.10f, 0.12f, 1f)
                shapeRenderer.triangle(p.x - 8f, p.y + 12f, p.x - 16f, p.y + 22f, p.x - 3f, p.y + 16f)
                shapeRenderer.triangle(p.x + 8f, p.y + 12f, p.x + 16f, p.y + 22f, p.x + 3f, p.y + 16f)

                shapeRenderer.color = Color.RED
                shapeRenderer.circle(p.x - 3.5f, p.y + 10f, 2.2f)
                shapeRenderer.circle(p.x + 3.5f, p.y + 10f, 2.2f)
            }

            // 25. JT: Boss J.T. đại quỷ tuyết đầu khối đá, răng đá nhọn, mắt vàng
            EnemyType.JT -> {
                shapeRenderer.color = Color(0.88f, 0.92f, 0.96f, 1f)
                shapeRenderer.circle(p.x, p.y, 26f)
                shapeRenderer.rect(p.x - 20f, p.y - 20f, 40f, 24f)
                // Đầu đá khối lớn
                shapeRenderer.color = Color(0.50f, 0.55f, 0.62f, 1f)
                shapeRenderer.circle(p.x, p.y + 12f, 14f)
                shapeRenderer.color = Color.YELLOW
                shapeRenderer.circle(p.x - 5f, p.y + 14f, 2.8f)
                shapeRenderer.circle(p.x + 5f, p.y + 14f, 2.8f)
                shapeRenderer.color = Color(0.12f, 0.12f, 0.15f, 1f)
                shapeRenderer.rect(p.x - 7f, p.y + 5f, 14f, 5f)
                shapeRenderer.color = Color.WHITE
                shapeRenderer.rect(p.x - 5f, p.y + 7f, 2.5f, 3f)
                shapeRenderer.rect(p.x + 2.5f, p.y + 7f, 2.5f, 3f)
            }

            // 26. SKELETON: Bộ xương trắng, khiên gỗ tròn viền đồng, kiếm cong Scimitar
            EnemyType.SKELETON -> {
                shapeRenderer.color = Color(0.92f, 0.92f, 0.88f, 1f)
                shapeRenderer.rect(p.x - 2f, p.y - 7f, 4f, 10f)
                shapeRenderer.rectLine(p.x - 5f, p.y - 1f, p.x + 5f, p.y - 1f, 2f)
                shapeRenderer.rectLine(p.x - 4f, p.y - 4f, p.x + 4f, p.y - 4f, 2f)

                shapeRenderer.circle(p.x, p.y + 6f, 6.8f)
                shapeRenderer.color = Color.BLACK
                shapeRenderer.circle(p.x - 2.5f, p.y + 6f, 1.8f)
                shapeRenderer.circle(p.x + 2.5f, p.y + 6f, 1.8f)

                shapeRenderer.color = Color(0.72f, 0.45f, 0.20f, 1f)
                shapeRenderer.circle(p.x - 8f, p.y - 1f, 5.5f)
                shapeRenderer.color = Color(0.45f, 0.28f, 0.12f, 1f)
                shapeRenderer.circle(p.x - 8f, p.y - 1f, 3.5f)

                shapeRenderer.color = Color(0.75f, 0.78f, 0.82f, 1f)
                shapeRenderer.rectLine(p.x + 5f, p.y - 6f, p.x + 11f, p.y + 7f, 2f)
            }

            // 27. SKELETON_KNIGHT: Chiến binh xương giáp sắt, mũ thép thanh che mũi, khiên sắt tròn
            EnemyType.SKELETON_KNIGHT -> {
                shapeRenderer.color = Color(0.45f, 0.48f, 0.55f, 1f)
                shapeRenderer.rect(p.x - 8f, p.y - 8f, 16f, 13f)

                shapeRenderer.color = Color(0.92f, 0.92f, 0.88f, 1f)
                shapeRenderer.circle(p.x, p.y + 6f, 7.5f)

                shapeRenderer.color = Color(0.35f, 0.38f, 0.44f, 1f)
                shapeRenderer.rect(p.x - 7.5f, p.y + 6f, 15f, 6.5f)
                shapeRenderer.rect(p.x - 1.5f, p.y + 2f, 3f, 6f)

                shapeRenderer.color = Color.BLACK
                shapeRenderer.circle(p.x - 3.5f, p.y + 4.5f, 2f)
                shapeRenderer.circle(p.x + 3.5f, p.y + 4.5f, 2f)

                shapeRenderer.color = Color(0.55f, 0.58f, 0.65f, 1f)
                shapeRenderer.circle(p.x - 10f, p.y - 1f, 7f)
                shapeRenderer.color = Color(0.30f, 0.32f, 0.36f, 1f)
                shapeRenderer.circle(p.x - 10f, p.y - 1f, 5f)

                shapeRenderer.color = Color(0.85f, 0.88f, 0.92f, 1f)
                shapeRenderer.rectLine(p.x + 6f, p.y - 8f, p.x + 14f, p.y + 8f, 2.5f)
            }

            // 28. DEMON_SPAWN: Quỷ đỏ địa ngục sừng đen, đuôi nhọn mũi tên, mắt vàng
            EnemyType.DEMON_SPAWN -> {
                shapeRenderer.color = Color(0.55f, 0.12f, 0.10f, 1f)
                shapeRenderer.rectLine(p.x, p.y - 4f, p.x - 14f, p.y + 4f, 2.5f)
                shapeRenderer.triangle(p.x - 14f, p.y + 4f, p.x - 18f, p.y + 8f, p.x - 18f, p.y)

                shapeRenderer.color = Color(0.85f, 0.22f, 0.18f, 1f)
                shapeRenderer.circle(p.x, p.y - 1f, 10f)

                shapeRenderer.color = Color(0.92f, 0.28f, 0.22f, 1f)
                shapeRenderer.circle(p.x, p.y + 6f, 8f)

                shapeRenderer.color = Color(0.20f, 0.15f, 0.15f, 1f)
                shapeRenderer.triangle(p.x - 6f, p.y + 8f, p.x - 11f, p.y + 17f, p.x - 2f, p.y + 11f)
                shapeRenderer.triangle(p.x + 6f, p.y + 8f, p.x + 11f, p.y + 17f, p.x + 2f, p.y + 11f)

                shapeRenderer.color = Color(1f, 0.95f, 0.2f, 1f)
                shapeRenderer.circle(p.x - 3f, p.y + 7f, 1.8f)
                shapeRenderer.circle(p.x + 3f, p.y + 7f, 1.8f)
            }

            // 29. DEMON_HOUND: Chó săn địa ngục 4 chân, lưng mọc gai nham thạch lửa
            EnemyType.DEMON_HOUND -> {
                shapeRenderer.color = Color(0.68f, 0.18f, 0.14f, 1f)
                shapeRenderer.ellipse(p.x - 12f, p.y - 6f, 24f, 14f)

                shapeRenderer.color = Color(0.45f, 0.12f, 0.10f, 1f)
                shapeRenderer.rect(p.x - 9f, p.y - 11f, 3.5f, 7f)
                shapeRenderer.rect(p.x - 3f, p.y - 11f, 3.5f, 7f)
                shapeRenderer.rect(p.x + 3f, p.y - 11f, 3.5f, 7f)
                shapeRenderer.rect(p.x + 8f, p.y - 11f, 3.5f, 7f)

                shapeRenderer.color = Color(1f, 0.55f, 0.10f, 1f)
                shapeRenderer.triangle(p.x - 8f, p.y + 4f, p.x - 5f, p.y + 12f, p.x - 2f, p.y + 4f)
                shapeRenderer.triangle(p.x - 2f, p.y + 4f, p.x + 1f, p.y + 14f, p.x + 4f, p.y + 4f)
                shapeRenderer.triangle(p.x + 4f, p.y + 4f, p.x + 7f, p.y + 12f, p.x + 10f, p.y + 4f)

                shapeRenderer.color = Color(0.78f, 0.22f, 0.16f, 1f)
                shapeRenderer.circle(p.x + 8f, p.y + 2f, 7f)

                shapeRenderer.color = Color.RED
                shapeRenderer.circle(p.x + 10f, p.y + 4f, 2f)
            }

            // 30. DEMON_LORD: Chúa quỷ cơ bắp, giáp ngực nứt vân dung nham, cặp sừng đại quỷ
            EnemyType.DEMON_LORD -> {
                shapeRenderer.color = Color(0.55f, 0.15f, 0.12f, 1f)
                shapeRenderer.circle(p.x, p.y, 18f)
                shapeRenderer.rect(p.x - 14f, p.y - 15f, 28f, 18f)

                shapeRenderer.color = Color(0.20f, 0.16f, 0.16f, 1f)
                shapeRenderer.rect(p.x - 10f, p.y - 8f, 20f, 16f)
                shapeRenderer.color = Color(1f, 0.50f, 0.05f, 1f)
                shapeRenderer.rectLine(p.x - 8f, p.y + 4f, p.x + 8f, p.y - 2f, 2f)
                shapeRenderer.rectLine(p.x - 6f, p.y - 4f, p.x + 6f, p.y + 4f, 2f)

                shapeRenderer.color = Color(0.70f, 0.18f, 0.14f, 1f)
                shapeRenderer.circle(p.x, p.y + 12f, 11f)

                shapeRenderer.color = Color(0.18f, 0.15f, 0.18f, 1f)
                shapeRenderer.triangle(p.x - 8f, p.y + 14f, p.x - 22f, p.y + 28f, p.x - 3f, p.y + 20f)
                shapeRenderer.triangle(p.x + 8f, p.y + 14f, p.x + 22f, p.y + 28f, p.x + 3f, p.y + 20f)

                shapeRenderer.color = Color(1f, 0.90f, 0.15f, 1f)
                shapeRenderer.circle(p.x - 4f, p.y + 13f, 2.5f)
                shapeRenderer.circle(p.x + 4f, p.y + 13f, 2.5f)
            }

            // 31. DEMON_IMP: Tiểu quỷ bay cánh dơi đỏ cam, sừng đen, mắt vàng
            EnemyType.DEMON_IMP -> {
                shapeRenderer.color = Color(0.85f, 0.32f, 0.10f, 1f)
                shapeRenderer.triangle(p.x - 4f, p.y, p.x - 18f, p.y + 14f, p.x - 10f, p.y - 5f)
                shapeRenderer.triangle(p.x + 4f, p.y, p.x + 18f, p.y + 14f, p.x + 10f, p.y - 5f)

                shapeRenderer.color = Color(0.92f, 0.22f, 0.16f, 1f)
                shapeRenderer.circle(p.x, p.y - 1f, 8f)
                shapeRenderer.circle(p.x, p.y + 5f, 6f)

                shapeRenderer.color = Color(0.20f, 0.12f, 0.12f, 1f)
                shapeRenderer.triangle(p.x - 4f, p.y + 7f, p.x - 8f, p.y + 15f, p.x - 1f, p.y + 9f)
                shapeRenderer.triangle(p.x + 4f, p.y + 7f, p.x + 8f, p.y + 15f, p.x + 1f, p.y + 9f)

                shapeRenderer.color = Color.YELLOW
                shapeRenderer.circle(p.x - 2.2f, p.y + 6f, 1.5f)
                shapeRenderer.circle(p.x + 2.2f, p.y + 6f, 1.5f)
            }

            // 32. MAGMA_ELEMENTAL: Người đá dung nham nứt nẻ, lõi nham thạch rực cháy
            EnemyType.MAGMA_ELEMENTAL -> {
                shapeRenderer.color = Color(0.25f, 0.20f, 0.20f, 1f)
                shapeRenderer.circle(p.x, p.y, 22f)

                shapeRenderer.color = Color(0.95f, 0.35f, 0.05f, 1f)
                shapeRenderer.circle(p.x, p.y + 1f, 14f)
                shapeRenderer.color = Color(1f, 0.85f, 0.20f, 1f)
                shapeRenderer.circle(p.x, p.y + 1f, 8f)

                shapeRenderer.color = Color(0.18f, 0.15f, 0.16f, 1f)
                shapeRenderer.rect(p.x - 20f, p.y - 12f, 10f, 12f)
                shapeRenderer.rect(p.x + 10f, p.y - 12f, 10f, 12f)
                shapeRenderer.rect(p.x - 8f, p.y + 12f, 16f, 10f)

                shapeRenderer.color = Color.WHITE
                shapeRenderer.circle(p.x - 5f, p.y + 14f, 2.8f)
                shapeRenderer.circle(p.x + 5f, p.y + 14f, 2.8f)
            }

            // 33. NECROMANCER: Pháp sư gọi hồn áo đen, mắt xanh lá ma mị, trượng đầu lâu
            EnemyType.NECROMANCER -> {
                shapeRenderer.color = Color(0.18f, 0.20f, 0.24f, 1f)
                shapeRenderer.rect(p.x - 9f, p.y - 10f, 18f, 18f)
                shapeRenderer.circle(p.x, p.y + 6f, 8f)

                shapeRenderer.color = Color(0.40f, 0.28f, 0.16f, 1f)
                shapeRenderer.rectLine(p.x - 12f, p.y - 10f, p.x - 12f, p.y + 16f, 2.5f)
                shapeRenderer.color = Color(0.25f, 0.95f, 0.40f, 1f)
                shapeRenderer.circle(p.x - 12f, p.y + 17f, 4.5f)

                shapeRenderer.color = Color(0.35f, 0.95f, 0.45f, 1f)
                shapeRenderer.circle(p.x - 2.5f, p.y + 7f, 1.8f)
                shapeRenderer.circle(p.x + 2.5f, p.y + 7f, 1.8f)
            }

            // 34. VEZNAN: Boss Vez'nan dạng 1 (Pháp sư)
            EnemyType.VEZNAN -> {
                shapeRenderer.color = Color(0.28f, 0.16f, 0.38f, 1f)
                shapeRenderer.rect(p.x - 14f, p.y - 14f, 28f, 26f)
                shapeRenderer.circle(p.x, p.y - 2f, 16f)

                shapeRenderer.color = Color(0.15f, 0.12f, 0.18f, 1f)
                shapeRenderer.rect(p.x - 12f, p.y - 14f, 24f, 8f)

                shapeRenderer.color = Color(0.12f, 0.10f, 0.14f, 1f)
                shapeRenderer.circle(p.x, p.y + 12f, 10f)

                shapeRenderer.color = Color(0.95f, 0.78f, 0.15f, 1f)
                shapeRenderer.rect(p.x - 10f, p.y + 16f, 20f, 6f)
                shapeRenderer.triangle(p.x - 10f, p.y + 22f, p.x - 6f, p.y + 28f, p.x - 2f, p.y + 22f)
                shapeRenderer.triangle(p.x - 4f, p.y + 22f, p.x, p.y + 31f, p.x + 4f, p.y + 22f)
                shapeRenderer.triangle(p.x + 2f, p.y + 22f, p.x + 6f, p.y + 28f, p.x + 10f, p.y + 22f)

                shapeRenderer.color = Color.RED
                shapeRenderer.circle(p.x - 3.5f, p.y + 13f, 2f)
                shapeRenderer.circle(p.x + 3.5f, p.y + 13f, 2f)

                shapeRenderer.color = Color(0.25f, 0.95f, 0.25f, 0.9f)
                shapeRenderer.circle(p.x - 18f, p.y + 2f, 6f)
                shapeRenderer.color = Color.WHITE
                shapeRenderer.circle(p.x - 18f, p.y + 2f, 2.5f)
            }

            // 35. VEZNAN_DEMON: Boss Vez'nan dạng 2 (Đại quỷ đỏ có cánh)
            EnemyType.VEZNAN_DEMON -> {
                shapeRenderer.color = Color(0.38f, 0.20f, 0.12f, 1f)
                shapeRenderer.triangle(p.x - 12f, p.y + 5f, p.x - 36f, p.y + 36f, p.x - 28f, p.y - 12f)
                shapeRenderer.triangle(p.x + 12f, p.y + 5f, p.x + 36f, p.y + 36f, p.x + 28f, p.y - 12f)
                shapeRenderer.color = Color(0.50f, 0.24f, 0.14f, 1f)
                shapeRenderer.circle(p.x - 34f, p.y + 32f, 6f)
                shapeRenderer.circle(p.x + 34f, p.y + 32f, 6f)

                shapeRenderer.color = Color(0.78f, 0.26f, 0.12f, 1f)
                shapeRenderer.circle(p.x, p.y, 22f)
                shapeRenderer.rect(p.x - 16f, p.y - 20f, 32f, 24f)

                shapeRenderer.color = Color(0.92f, 0.78f, 0.45f, 1f)
                shapeRenderer.circle(p.x, p.y - 2f, 13f)
                shapeRenderer.rect(p.x - 8f, p.y - 16f, 16f, 14f)

                shapeRenderer.color = Color.RED
                shapeRenderer.circle(p.x, p.y + 2f, 4.5f)
                shapeRenderer.color = Color(1f, 0.85f, 0.85f, 1f)
                shapeRenderer.circle(p.x, p.y + 2f, 2f)

                shapeRenderer.color = Color(0.78f, 0.26f, 0.12f, 1f)
                shapeRenderer.circle(p.x, p.y + 20f, 14f)

                shapeRenderer.color = Color(0.90f, 0.88f, 0.80f, 1f)
                shapeRenderer.triangle(p.x - 12f, p.y + 22f, p.x - 22f, p.y + 38f, p.x - 6f, p.y + 28f)
                shapeRenderer.triangle(p.x + 12f, p.y + 22f, p.x + 22f, p.y + 38f, p.x + 6f, p.y + 28f)

                shapeRenderer.color = Color(1f, 0.95f, 0.2f, 1f)
                shapeRenderer.circle(p.x - 5f, p.y + 21f, 3f)
                shapeRenderer.circle(p.x + 5f, p.y + 21f, 3f)
            }

            // -----------------------------------------------------------------
            // DỰ PHÒNG CHO QUÁI MỚI (TỪ SỐ 36 TRỞ ĐI)
            // -----------------------------------------------------------------
            else -> {
                shapeRenderer.color = Color.RED
                shapeRenderer.circle(p.x, p.y, 12f)
            }
        }

        // 3. VẼ THANH MÁU (HP BAR)
        val barW = when (eComp.enemyType) {
            EnemyType.VEZNAN, EnemyType.VEZNAN_DEMON -> 52f
            EnemyType.JT, EnemyType.JUGGERNAUT -> 48f
            EnemyType.YETI, EnemyType.MAGMA_ELEMENTAL -> 36f
            EnemyType.OGRE, EnemyType.DEMON_LORD, EnemyType.TROLL_CHIEFTAIN -> 28f
            else -> 20f
        }
        val barYOffset = when (eComp.enemyType) {
            EnemyType.VEZNAN_DEMON -> 38f
            EnemyType.VEZNAN -> 32f
            EnemyType.JT, EnemyType.JUGGERNAUT -> 32f
            else -> 17f
        }
        renderHpBar(shapeRenderer, p.x, p.y + barYOffset, barW, hp)

        // 4. HIỆU ỨNG KHIÊN LỬA INFERNAL SHIELD
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
