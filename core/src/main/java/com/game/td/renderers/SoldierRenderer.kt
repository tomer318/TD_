package com.game.td.renderers

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.game.td.ecs.components.*

object SoldierRenderer {

    private val OUTLINE = Color(0.12f, 0.10f, 0.08f, 1f)
    private val SHADOW = Color(0.06f, 0.06f, 0.05f, 0.36f)
    private val SKIN = Color(0.95f, 0.76f, 0.56f, 1f)

    fun render(shapeRenderer: ShapeRenderer, soldier: Entity, showHpBar: Boolean = true) {
        val trans = soldier.getComponent(TransformComponent::class.java) ?: return
        val hp = soldier.getComponent(HealthComponent::class.java) ?: return
        val sComp = soldier.getComponent(SoldierComponent::class.java) ?: return
        val p = trans.position

        // =====================================================================
        // 1. EARTH ELEMENTAL (NGƯỜI ĐÁ KHỔNG LỒ CỦA SORCERER MAGE)
        // =====================================================================
        if (sComp.soldierRankName == "Earth Elemental") {
            shapeRenderer.color = SHADOW
            shapeRenderer.ellipse(p.x - 19f, p.y - 17f, 38f, 10f)

            // Viền đen khối đá
            shapeRenderer.color = OUTLINE
            shapeRenderer.rect(p.x - 12f, p.y - 15f, 9f, 8f)
            shapeRenderer.rect(p.x + 3f, p.y - 15f, 9f, 8f)
            shapeRenderer.circle(p.x, p.y + 1f, 17.5f)
            shapeRenderer.circle(p.x - 14f, p.y + 3f, 9.5f)
            shapeRenderer.circle(p.x + 14f, p.y + 3f, 9.5f)
            shapeRenderer.circle(p.x, p.y + 12f, 11f)

            // Thân đá xám nâu & Rêu xanh
            shapeRenderer.color = Color(0.50f, 0.46f, 0.40f, 1f)
            shapeRenderer.circle(p.x, p.y + 1f, 15.5f)
            shapeRenderer.circle(p.x - 13.5f, p.y + 3f, 7.5f)
            shapeRenderer.circle(p.x + 13.5f, p.y + 3f, 7.5f)
            shapeRenderer.color = Color(0.64f, 0.60f, 0.52f, 1f)
            shapeRenderer.circle(p.x - 3f, p.y + 4f, 10f)
            shapeRenderer.circle(p.x, p.y + 12f, 9f)
            shapeRenderer.color = Color(0.36f, 0.58f, 0.24f, 1f)
            shapeRenderer.circle(p.x - 11f, p.y + 7f, 4.5f)
            shapeRenderer.circle(p.x + 8f, p.y - 3f, 5f)

            // Mắt vàng phát sáng
            shapeRenderer.color = Color(1f, 0.90f, 0.18f, 1f)
            shapeRenderer.circle(p.x - 3.5f, p.y + 12.5f, 2.2f)
            shapeRenderer.circle(p.x + 3.5f, p.y + 12.5f, 2.2f)

            if (showHpBar) {
                renderSoldierHpBar(shapeRenderer, p.x, p.y + 27f, 38f, hp)
            }
            return
        }

        // Bóng đổ dưới chân cho tất cả các loại lính bộ binh
        shapeRenderer.color = SHADOW
        shapeRenderer.ellipse(p.x - 11f, p.y - 12.5f, 22f, 6.5f)

        when (sComp.soldierRankName) {

            // =================================================================
            // 2. MILITIA (BARRACKS CẤP 1 - HÌNH 1):
            // Tóc vàng dựng nhọn, áo nâu, cầm kiếm ngắn chuôi vàng ngang ngực
            // =================================================================
            "Militia" -> {
                // Viền đen chân, thân, đầu và chỏm tóc nhọn
                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 6f, p.y - 11.5f, 5f, 5.5f)
                shapeRenderer.rect(p.x + 1f, p.y - 11.5f, 5f, 5.5f)
                shapeRenderer.rect(p.x - 7.5f, p.y - 8f, 15f, 11.5f)
                shapeRenderer.circle(p.x, p.y + 4.5f, 8.5f)
                // Viền đen chỏm tóc vàng xù
                shapeRenderer.triangle(p.x - 8f, p.y + 7f, p.x - 12f, p.y + 13f, p.x - 2f, p.y + 11f)
                shapeRenderer.triangle(p.x - 5f, p.y + 10f, p.x, p.y + 16.5f, p.x + 5f, p.y + 10f)
                shapeRenderer.triangle(p.x + 2f, p.y + 11f, p.x + 11f, p.y + 13.5f, p.x + 7.5f, p.y + 6.5f)

                // Giày nâu sẫm & Áo vải nâu vàng
                shapeRenderer.color = Color(0.32f, 0.20f, 0.12f, 1f)
                shapeRenderer.rect(p.x - 4.8f, p.y - 10.2f, 3.2f, 4f)
                shapeRenderer.rect(p.x + 1.6f, p.y - 10.2f, 3.2f, 4f)
                shapeRenderer.color = Color(0.64f, 0.42f, 0.20f, 1f)
                shapeRenderer.rect(p.x - 6f, p.y - 6.8f, 12f, 9f)
                shapeRenderer.color = Color(0.42f, 0.26f, 0.12f, 1f)
                shapeRenderer.rect(p.x - 6f, p.y - 6.8f, 12f, 3f)

                // Khuôn mặt da người & Mắt
                shapeRenderer.color = SKIN
                shapeRenderer.circle(p.x, p.y + 4.2f, 6.8f)
                shapeRenderer.color = OUTLINE
                shapeRenderer.circle(p.x - 2.3f, p.y + 4.2f, 1.3f)
                shapeRenderer.circle(p.x + 2.3f, p.y + 4.2f, 1.3f)

                // Mái tóc vàng hoe dựng nhọn đặc trưng (chuẩn Hình 1)
                shapeRenderer.color = Color(0.98f, 0.84f, 0.18f, 1f)
                shapeRenderer.rect(p.x - 6.5f, p.y + 6.5f, 13f, 4f)
                shapeRenderer.triangle(p.x - 7f, p.y + 7.5f, p.x - 10.5f, p.y + 12f, p.x - 2f, p.y + 10.5f)
                shapeRenderer.triangle(p.x - 4f, p.y + 9.5f, p.x, p.y + 15f, p.x + 4f, p.y + 9.5f)
                shapeRenderer.triangle(p.x + 2f, p.y + 10.5f, p.x + 9.5f, p.y + 12.2f, p.x + 6.5f, p.y + 7f)

                // Thanh kiếm sắt ngắn chuôi vàng cầm ngang trước ngực
                drawHorizontalSword(shapeRenderer, p.x - 6f, p.y - 5f, 13f, Color(0.86f, 0.90f, 0.94f, 1f))
            }

            // =================================================================
            // 3. FOOTMAN (BARRACKS CẤP 2 - HÌNH 2):
            // Mũ sắt vòm xám đen che nửa trên đầu, áo giáp lam xám, cầm kiếm ngang
            // =================================================================
            "Footman" -> {
                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 6.5f, p.y - 11.5f, 5f, 5.5f)
                shapeRenderer.rect(p.x + 1.5f, p.y - 11.5f, 5f, 5.5f)
                shapeRenderer.rect(p.x - 8f, p.y - 8f, 16f, 12f)
                shapeRenderer.circle(p.x, p.y + 5f, 9f)

                // Áo giáp xanh xám & Đai lưng
                shapeRenderer.color = Color(0.32f, 0.44f, 0.56f, 1f)
                shapeRenderer.rect(p.x - 6.5f, p.y - 6.8f, 13f, 9.5f)
                shapeRenderer.color = Color(0.24f, 0.22f, 0.20f, 1f)
                shapeRenderer.rect(p.x - 6.5f, p.y - 6.8f, 13f, 3f)

                // Nửa mặt dưới màu da
                shapeRenderer.color = SKIN
                shapeRenderer.circle(p.x, p.y + 3.8f, 7f)
                shapeRenderer.color = OUTLINE
                shapeRenderer.circle(p.x - 2.4f, p.y + 3.6f, 1.3f)
                shapeRenderer.circle(p.x + 2.4f, p.y + 3.6f, 1.3f)

                // Mũ sắt vòm xám đậm che trán (có viền đen ngang trán chuẩn Hình 2)
                shapeRenderer.color = OUTLINE
                shapeRenderer.arc(p.x, p.y + 4.8f, 9f, 0f, 180f)
                shapeRenderer.rect(p.x - 8.8f, p.y + 4.5f, 17.6f, 2.2f)
                shapeRenderer.color = Color(0.45f, 0.48f, 0.52f, 1f)
                shapeRenderer.arc(p.x, p.y + 5.5f, 7.4f, 0f, 180f)
                shapeRenderer.color = Color(0.68f, 0.72f, 0.76f, 1f)
                shapeRenderer.circle(p.x - 2.5f, p.y + 9.5f, 2f)

                // Kiếm bản rộng chuôi vàng cầm ngang trước ngực
                drawHorizontalSword(shapeRenderer, p.x - 6.5f, p.y - 5.2f, 14f, Color(0.88f, 0.92f, 0.96f, 1f))
            }

            // =================================================================
            // 4. KNIGHT (BARRACKS CẤP 3 - HÌNH 3):
            // Giáp sắt toàn thân, mũ sắt kín mặt khe ngang đen, khiên bạc-xanh & kiếm
            // =================================================================
            "Knight" -> {
                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 7f, p.y - 11.5f, 5.5f, 5.5f)
                shapeRenderer.rect(p.x + 1.5f, p.y - 11.5f, 5.5f, 5.5f)
                shapeRenderer.rect(p.x - 8.5f, p.y - 8f, 17f, 12.5f)
                shapeRenderer.circle(p.x, p.y + 5.5f, 9.2f)

                // Thân giáp thép sáng & Đai lưng
                shapeRenderer.color = Color(0.62f, 0.66f, 0.70f, 1f)
                shapeRenderer.rect(p.x - 7f, p.y - 6.8f, 14f, 10f)
                shapeRenderer.color = Color(0.82f, 0.86f, 0.90f, 1f)
                shapeRenderer.rect(p.x - 5f, p.y - 4f, 10f, 6f)
                shapeRenderer.color = Color(0.30f, 0.28f, 0.26f, 1f)
                shapeRenderer.rect(p.x - 7f, p.y - 6.8f, 14f, 2.8f)

                // Mũ trụ Hiệp sĩ kín mặt có khe mắt ngang đen (chuẩn Hình 3)
                shapeRenderer.color = Color(0.68f, 0.72f, 0.76f, 1f)
                shapeRenderer.circle(p.x, p.y + 5.5f, 7.6f)
                shapeRenderer.rect(p.x - 6.5f, p.y + 1f, 13f, 5f)
                shapeRenderer.color = Color(0.88f, 0.92f, 0.96f, 1f)
                shapeRenderer.circle(p.x - 2.5f, p.y + 8.5f, 2.4f)
                // Khe kính ngắm ngang màu đen
                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 5.5f, p.y + 4.2f, 11f, 2.6f)
                shapeRenderer.rect(p.x - 4f, p.y + 1.5f, 8f, 1.5f)

                // Khiên Hiệp Sĩ bên hông phải (viền bạc lòng xanh lam)
                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x + 4.5f, p.y - 6.5f, 6.5f, 9.5f)
                shapeRenderer.color = Color(0.82f, 0.86f, 0.90f, 1f)
                shapeRenderer.rect(p.x + 5.5f, p.y - 5.5f, 4.5f, 7.5f)
                shapeRenderer.color = Color(0.20f, 0.48f, 0.82f, 1f)
                shapeRenderer.rect(p.x + 6.5f, p.y - 4.5f, 2.5f, 5.5f)

                // Kiếm thép chuôi vàng cầm ngang
                drawHorizontalSword(shapeRenderer, p.x - 7.5f, p.y - 5.2f, 14.5f, Color(0.92f, 0.96f, 1f, 1f))
            }

            // =================================================================
            // 5. PALADIN (HOLY ORDER CẤP 4 - HÌNH 4):
            // Mũ Great Helm bạc trắng chữ T, mào xanh lam, khiên chữ thập vàng, Thánh Kiếm
            // =================================================================
            "Paladin" -> {
                shapeRenderer.color = OUTLINE
                // Mào lông xanh lam trên đỉnh mũ
                shapeRenderer.triangle(p.x - 4f, p.y + 12f, p.x + 4f, p.y + 12f, p.x - 2f, p.y + 19f)
                shapeRenderer.rect(p.x - 7.5f, p.y - 11.5f, 5.5f, 5.5f)
                shapeRenderer.rect(p.x + 2f, p.y - 11.5f, 5.5f, 5.5f)
                shapeRenderer.rect(p.x - 9.5f, p.y - 8f, 19f, 13f)
                shapeRenderer.circle(p.x, p.y + 6f, 9.5f)

                // Giáp vai xanh lam & Giáp ngực Bạc Trắng Thánh Kị Sĩ
                shapeRenderer.color = Color(0.18f, 0.52f, 0.88f, 1f)
                shapeRenderer.rect(p.x - 8f, p.y - 6.8f, 16f, 10.5f)
                shapeRenderer.triangle(p.x - 2.8f, p.y + 12f, p.x + 2.8f, p.y + 12f, p.x - 1.5f, p.y + 17.5f)
                shapeRenderer.color = Color(0.86f, 0.90f, 0.95f, 1f)
                shapeRenderer.rect(p.x - 5.5f, p.y - 6.8f, 11f, 9.5f)
                shapeRenderer.color = Color.GOLD
                shapeRenderer.rect(p.x - 1.2f, p.y - 5f, 2.4f, 6.5f)
                shapeRenderer.rect(p.x - 3.5f, p.y - 2f, 7f, 2.2f)

                // Mũ trụ Thập Tự Chinh hình trụ bạc sáng (Crusader Great Helm)
                shapeRenderer.color = Color(0.88f, 0.92f, 0.96f, 1f)
                shapeRenderer.circle(p.x, p.y + 6f, 7.8f)
                shapeRenderer.rect(p.x - 7f, p.y + 1f, 14f, 6.5f)
                // Khe kính chữ T đen viền vàng
                shapeRenderer.color = Color.GOLD
                shapeRenderer.rect(p.x - 5.5f, p.y + 5.2f, 11f, 3.2f)
                shapeRenderer.rect(p.x - 1.8f, p.y + 1.5f, 3.6f, 6.5f)
                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 5f, p.y + 5.8f, 10f, 2f)
                shapeRenderer.rect(p.x - 1f, p.y + 1.8f, 2f, 5.8f)

                // Khiên Thánh lớn bên phải (Nền xanh viền vàng chữ thập sáng)
                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x + 4.5f, p.y - 7f, 7.5f, 11f)
                shapeRenderer.color = Color.GOLD
                shapeRenderer.rect(p.x + 5.5f, p.y - 6f, 5.5f, 9f)
                shapeRenderer.color = Color(0.20f, 0.58f, 0.95f, 1f)
                shapeRenderer.rect(p.x + 6.5f, p.y - 5f, 3.5f, 7f)

                // Thanh Thánh Kiếm phát sáng xanh băng cầm ngang
                drawHorizontalSword(shapeRenderer, p.x - 8.5f, p.y - 5.2f, 15.5f, Color(0.65f, 0.95f, 1f, 1f))
            }

            // =================================================================
            // 6. BARBARIAN (BARBARIAN MEAD HALL CẤP 4 - HÌNH 5):
            // Tóc cam đỏ xù nhọn, ở trần cơ bắp, quần da nâu, cầm đại rìu sắt
            // =================================================================
            "Barbarian" -> {
                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 7f, p.y - 11.5f, 5.5f, 5.5f)
                shapeRenderer.rect(p.x + 1.5f, p.y - 11.5f, 5.5f, 5.5f)
                shapeRenderer.rect(p.x - 8.5f, p.y - 8f, 17f, 12.5f)
                shapeRenderer.circle(p.x, p.y + 5.5f, 8.8f)
                // Tóc cam đỏ dựng nhọn
                shapeRenderer.triangle(p.x - 8.5f, p.y + 7f, p.x - 13.5f, p.y + 14f, p.x - 2f, p.y + 11.5f)
                shapeRenderer.triangle(p.x - 5.5f, p.y + 10f, p.x, p.y + 17.5f, p.x + 5.5f, p.y + 10f)
                shapeRenderer.triangle(p.x + 2f, p.y + 11.5f, p.x + 13f, p.y + 14f, p.x + 8.5f, p.y + 7f)

                // Quần khố da nâu & Thân trên ở trần cơ bắp (chuẩn Hình 5)
                shapeRenderer.color = Color(0.45f, 0.28f, 0.14f, 1f)
                shapeRenderer.rect(p.x - 7f, p.y - 7.5f, 14f, 4.5f)
                shapeRenderer.color = SKIN
                shapeRenderer.rect(p.x - 7f, p.y - 3f, 14f, 6.5f)
                shapeRenderer.circle(p.x, p.y + 4.8f, 7f)

                // Đai da chéo ngực & Mắt dữ
                shapeRenderer.color = Color(0.38f, 0.22f, 0.10f, 1f)
                shapeRenderer.rectLine(p.x - 6.5f, p.y + 2.5f, p.x + 6.5f, p.y - 3f, 2.2f)
                shapeRenderer.color = OUTLINE
                shapeRenderer.circle(p.x - 2.5f, p.y + 4.8f, 1.4f)
                shapeRenderer.circle(p.x + 2.5f, p.y + 4.8f, 1.4f)
                shapeRenderer.rectLine(p.x - 5f, p.y + 6.8f, p.x - 1f, p.y + 5.5f, 1.6f)
                shapeRenderer.rectLine(p.x + 5f, p.y + 6.8f, p.x + 1f, p.y + 5.5f, 1.6f)

                // Mái tóc cam đỏ rực lửa
                shapeRenderer.color = Color(0.92f, 0.36f, 0.10f, 1f)
                shapeRenderer.rect(p.x - 7f, p.y + 7f, 14f, 4f)
                shapeRenderer.triangle(p.x - 7.5f, p.y + 7.5f, p.x - 11.5f, p.y + 13f, p.x - 2f, p.y + 11f)
                shapeRenderer.triangle(p.x - 4.5f, p.y + 10f, p.x, p.y + 16f, p.x + 4.5f, p.y + 10f)
                shapeRenderer.triangle(p.x + 2f, p.y + 11f, p.x + 11f, p.y + 13f, p.x + 7.5f, p.y + 7.5f)

                // Đại Rìu Chiến (Cầm rìu to bản màu bạc trước ngực; nếu nâng More Axes thì thêm rìu thứ 2!)
                shapeRenderer.color = OUTLINE
                shapeRenderer.rectLine(p.x - 8f, p.y - 4f, p.x + 6f, p.y + 2f, 4.2f)
                shapeRenderer.triangle(p.x - 12f, p.y + 6f, p.x - 12f, p.y - 6f, p.x - 3f, p.y - 1f)
                shapeRenderer.color = Color(0.55f, 0.34f, 0.16f, 1f)
                shapeRenderer.rectLine(p.x - 7f, p.y - 3.5f, p.x + 5f, p.y + 1.5f, 2.2f)
                shapeRenderer.color = Color(0.84f, 0.88f, 0.92f, 1f)
                shapeRenderer.triangle(p.x - 10.5f, p.y + 4.5f, p.x - 10.5f, p.y - 4.5f, p.x - 4f, p.y - 1f)

                if (sComp.attackDamageMin > 16f) {
                    // Rìu thứ 2 bên tay phải khi đã nâng More Axes
                    shapeRenderer.color = OUTLINE
                    shapeRenderer.triangle(p.x + 12f, p.y + 6f, p.x + 12f, p.y - 6f, p.x + 3f, p.y - 1f)
                    shapeRenderer.color = Color(0.88f, 0.92f, 0.96f, 1f)
                    shapeRenderer.triangle(p.x + 10.5f, p.y + 4.5f, p.x + 10.5f, p.y - 4.5f, p.x + 4f, p.y - 1f)
                }
            }

            // =================================================================
            // 7. IMPERIAL GUARD (CẬN VỆ HOÀNG GIA MÀN 6 - HÌNH 6):
            // Mũ sắt sống mũi dọc + mào lông tím, áo choàng tím, khiên tím viền bạc, kiếm ngang
            // =================================================================
            "Imperial Guard" -> {
                shapeRenderer.color = OUTLINE
                // Áo choàng tím xòe hai bên vai
                shapeRenderer.triangle(p.x - 11.5f, p.y - 8.5f, p.x + 11.5f, p.y - 8.5f, p.x, p.y + 7f)
                // Mào lông tím trên đỉnh mũ
                shapeRenderer.triangle(p.x - 3.5f, p.y + 12f, p.x + 4.5f, p.y + 12f, p.x + 6f, p.y + 19f)
                shapeRenderer.rect(p.x - 7f, p.y - 11.5f, 5.5f, 5.5f)
                shapeRenderer.rect(p.x + 1.5f, p.y - 11.5f, 5.5f, 5.5f)
                shapeRenderer.rect(p.x - 8.5f, p.y - 8f, 17f, 12.5f)
                shapeRenderer.circle(p.x, p.y + 5.5f, 9.2f)

                // Áo choàng tím Hoàng Gia (chuẩn Hình 6)
                shapeRenderer.color = Color(0.56f, 0.24f, 0.72f, 1f)
                shapeRenderer.triangle(p.x - 10f, p.y - 7.5f, p.x + 10f, p.y - 7.5f, p.x, p.y + 6f)
                shapeRenderer.triangle(p.x - 2.5f, p.y + 12f, p.x + 3.5f, p.y + 12f, p.x + 4.8f, p.y + 17.5f)

                // Thân giáp bạc
                shapeRenderer.color = Color(0.68f, 0.70f, 0.74f, 1f)
                shapeRenderer.rect(p.x - 6.5f, p.y - 6.8f, 13f, 9.5f)

                // Mũ trụ sắt có sống mũi dọc (Nasal Bar Helmet chuẩn Hình 6)
                shapeRenderer.color = Color(0.75f, 0.78f, 0.82f, 1f)
                shapeRenderer.circle(p.x, p.y + 5.5f, 7.6f)
                shapeRenderer.rect(p.x - 6.5f, p.y + 1f, 13f, 5f)
                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 5.5f, p.y + 3.8f, 11f, 3f)
                shapeRenderer.color = Color(0.86f, 0.88f, 0.92f, 1f)
                shapeRenderer.rect(p.x - 1.3f, p.y + 1.5f, 2.6f, 6.5f)

                // Khiên Tím viền Bạc bên hông
                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 10.5f, p.y - 6.5f, 7f, 10f)
                shapeRenderer.color = Color(0.84f, 0.86f, 0.90f, 1f)
                shapeRenderer.rect(p.x - 9.5f, p.y - 5.5f, 5f, 8f)
                shapeRenderer.color = Color(0.56f, 0.24f, 0.72f, 1f)
                shapeRenderer.rect(p.x - 8.5f, p.y - 4.5f, 3f, 6f)

                // Thanh kiếm bạc chuôi vàng cầm ngang trước ngực
                drawHorizontalSword(shapeRenderer, p.x - 6.5f, p.y - 5.2f, 14.5f, Color(0.92f, 0.96f, 1f, 1f))
            }

            // =================================================================
            // 8. SYLVAN ELF (CUNG THỦ TIÊN RỪNG MÀN 5):
            // Áo choàng xanh lá rừng, tóc vàng dài, tai nhọn, cầm cung gỗ
            // =================================================================
            "Sylvan Elf" -> {
                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 6f, p.y - 11.5f, 4.5f, 5.5f)
                shapeRenderer.rect(p.x + 1.5f, p.y - 11.5f, 4.5f, 5.5f)
                shapeRenderer.rect(p.x - 7.5f, p.y - 8f, 15f, 12f)
                shapeRenderer.circle(p.x, p.y + 5f, 8.5f)

                shapeRenderer.color = Color(0.18f, 0.58f, 0.32f, 1f)
                shapeRenderer.rect(p.x - 6f, p.y - 6.8f, 12f, 9.5f)
                shapeRenderer.color = SKIN
                shapeRenderer.circle(p.x, p.y + 4.5f, 6.8f)
                shapeRenderer.color = Color(0.98f, 0.86f, 0.25f, 1f)
                shapeRenderer.arc(p.x, p.y + 5f, 7.2f, 0f, 180f)

                shapeRenderer.color = OUTLINE
                shapeRenderer.rectLine(p.x - 8f, p.y - 3f, p.x + 8f, p.y - 2f, 3.8f)
                shapeRenderer.color = Color(0.68f, 0.44f, 0.18f, 1f)
                shapeRenderer.rectLine(p.x - 7f, p.y - 3f, p.x + 7f, p.y - 2f, 2f)
            }

            // =================================================================
            // 9. LÍNH VIỆN BINH (FARMER / CONSCRIPT / WARRIOR / LEGIONNAIRE)
            // =================================================================
            "Farmer" -> {
                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 6f, p.y - 11f, 4.5f, 5f)
                shapeRenderer.rect(p.x + 1.5f, p.y - 11f, 4.5f, 5f)
                shapeRenderer.rect(p.x - 7f, p.y - 7.5f, 14f, 11f)
                shapeRenderer.circle(p.x, p.y + 4.5f, 8.2f)

                shapeRenderer.color = Color(0.58f, 0.40f, 0.22f, 1f)
                shapeRenderer.rect(p.x - 5.5f, p.y - 6.5f, 11f, 8.5f)
                shapeRenderer.color = SKIN
                shapeRenderer.circle(p.x, p.y + 4.2f, 6.5f)
                shapeRenderer.color = Color(0.85f, 0.68f, 0.32f, 1f)
                shapeRenderer.arc(p.x, p.y + 5f, 6.8f, 0f, 180f)

                // Đinh ba / Chĩa rơm cầm ngang
                shapeRenderer.color = OUTLINE
                shapeRenderer.rectLine(p.x - 8f, p.y - 4f, p.x + 9f, p.y - 3f, 3.6f)
                shapeRenderer.color = Color(0.55f, 0.35f, 0.16f, 1f)
                shapeRenderer.rectLine(p.x - 7f, p.y - 4f, p.x + 8f, p.y - 3f, 2f)
            }

            "Conscript" -> {
                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 6f, p.y - 11f, 4.5f, 5f)
                shapeRenderer.rect(p.x + 1.5f, p.y - 11f, 4.5f, 5f)
                shapeRenderer.rect(p.x - 7.5f, p.y - 7.5f, 15f, 11.5f)
                shapeRenderer.circle(p.x, p.y + 4.8f, 8.5f)

                shapeRenderer.color = Color(0.38f, 0.52f, 0.32f, 1f)
                shapeRenderer.rect(p.x - 6f, p.y - 6.5f, 12f, 9f)
                shapeRenderer.color = SKIN
                shapeRenderer.circle(p.x, p.y + 4f, 6.8f)
                shapeRenderer.color = Color(0.55f, 0.58f, 0.62f, 1f)
                shapeRenderer.arc(p.x, p.y + 5f, 7.2f, 0f, 180f)

                drawHorizontalSword(shapeRenderer, p.x - 6f, p.y - 5f, 13f, Color.LIGHT_GRAY)
            }

            "Warrior" -> {
                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 6.5f, p.y - 11.5f, 5f, 5.5f)
                shapeRenderer.rect(p.x + 1.5f, p.y - 11.5f, 5f, 5.5f)
                shapeRenderer.rect(p.x - 8f, p.y - 8f, 16f, 12f)
                shapeRenderer.circle(p.x, p.y + 5f, 8.8f)

                shapeRenderer.color = Color(0.48f, 0.52f, 0.58f, 1f)
                shapeRenderer.rect(p.x - 6.5f, p.y - 6.8f, 13f, 9.5f)
                shapeRenderer.color = SKIN
                shapeRenderer.circle(p.x, p.y + 4.2f, 6.8f)
                shapeRenderer.color = Color(0.62f, 0.66f, 0.72f, 1f)
                shapeRenderer.arc(p.x, p.y + 5.2f, 7.4f, 0f, 180f)

                drawHorizontalSword(shapeRenderer, p.x - 6.5f, p.y - 5f, 14f, Color.WHITE)
            }

            "Legionnaire" -> {
                shapeRenderer.color = OUTLINE
                shapeRenderer.triangle(p.x - 3.5f, p.y + 11.5f, p.x + 3.5f, p.y + 11.5f, p.x, p.y + 18.5f)
                shapeRenderer.rect(p.x - 7f, p.y - 11.5f, 5.5f, 5.5f)
                shapeRenderer.rect(p.x + 1.5f, p.y - 11.5f, 5.5f, 5.5f)
                shapeRenderer.rect(p.x - 8.5f, p.y - 8f, 17f, 12.5f)
                shapeRenderer.circle(p.x, p.y + 5.5f, 9f)

                shapeRenderer.color = Color(0.86f, 0.18f, 0.16f, 1f)
                shapeRenderer.triangle(p.x - 2.5f, p.y + 11.5f, p.x + 2.5f, p.y + 11.5f, p.x, p.y + 17f)
                shapeRenderer.rect(p.x - 7f, p.y - 6.8f, 14f, 9.5f)
                shapeRenderer.color = Color(0.75f, 0.65f, 0.25f, 1f)
                shapeRenderer.circle(p.x, p.y + 5.5f, 7.4f)
                shapeRenderer.color = SKIN
                shapeRenderer.rect(p.x - 4.5f, p.y + 2f, 9f, 4f)

                // Khiên La Mã đỏ viền vàng & Lao phóng
                shapeRenderer.color = OUTLINE
                shapeRenderer.rect(p.x - 10.5f, p.y - 6.5f, 6.5f, 10.5f)
                shapeRenderer.color = Color.GOLD
                shapeRenderer.rect(p.x - 9.5f, p.y - 5.5f, 4.5f, 8.5f)
                shapeRenderer.color = Color(0.86f, 0.18f, 0.16f, 1f)
                shapeRenderer.rect(p.x - 8.5f, p.y - 4.5f, 2.5f, 6.5f)

                drawHorizontalSword(shapeRenderer, p.x - 5.5f, p.y - 5f, 15f, Color.WHITE)
            }

            else -> {
                shapeRenderer.color = OUTLINE
                shapeRenderer.circle(p.x, p.y, 12f)
                shapeRenderer.color = Color(0.25f, 0.65f, 0.95f, 1f)
                shapeRenderer.circle(p.x, p.y, 10f)
            }
        }

        if (showHpBar) {
            val barW = if (sComp.soldierRankName in listOf("Paladin", "Barbarian", "Imperial Guard", "Legionnaire")) 24f else 20f
            renderSoldierHpBar(shapeRenderer, p.x, p.y + 17.5f, barW, hp)
        }
    }

    // HÀM VẼ THANH KIẾM CHUÔI VÀNG CẦM NGANG TRƯỚC NGỰC (ĐẶC TRƯNG CỦA LÍNH KINGDOM RUSH)
    private fun drawHorizontalSword(
        sr: ShapeRenderer,
        startX: Float,
        y: Float,
        length: Float,
        bladeColor: Color
    ) {
        // Viền đen bao quanh kiếm
        sr.color = OUTLINE
        sr.rect(startX - 1.5f, y - 1.2f, length + 3f, 5.2f)
        // Chuôi kiếm & Thanh chắn bằng vàng (Gold Hilt)
        sr.color = Color(0.96f, 0.78f, 0.16f, 1f)
        sr.rect(startX, y - 0.5f, 3.2f, 3.8f)
        sr.rect(startX + 2.2f, y - 1.5f, 2.2f, 5.8f)
        // Lưỡi kiếm thép sáng
        sr.color = bladeColor
        sr.rect(startX + 4.4f, y, length - 4.4f, 2.8f)
        sr.triangle(
            startX + length, y,
            startX + length, y + 2.8f,
            startX + length + 2.8f, y + 1.4f
        )
    }

    private fun renderSoldierHpBar(
        shapeRenderer: ShapeRenderer,
        cx: Float,
        topY: Float,
        width: Float,
        hp: HealthComponent
    ) {
        val halfW = width / 2f
        val height = 3.2f

        shapeRenderer.color = Color(0.08f, 0.08f, 0.08f, 0.92f)
        shapeRenderer.rect(cx - halfW - 1f, topY - 1f, width + 2f, height + 2f)

        shapeRenderer.color = Color(0.85f, 0.15f, 0.15f, 1f)
        shapeRenderer.rect(cx - halfW, topY, width, height)

        val hpRatio = (hp.currentHealth / hp.maxHealth).coerceIn(0f, 1f)
        shapeRenderer.color = Color(0.22f, 0.90f, 0.35f, 1f)
        shapeRenderer.rect(cx - halfW, topY, width * hpRatio, height)
    }
}
