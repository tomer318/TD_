package com.game.td.renderers

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.game.td.Config
import com.game.td.managers.SoundManager
import com.game.td.managers.UpgradeManager

data class UpgradeSkillInfo(
    val title: String,
    val description: String,
    val cost: Int
)

object UpgradeRenderer {

    private var selectedBranch: Int = 0
    private var selectedTier: Int = 1 // 1..5
    private val undoStack = mutableListOf<Int>()

    private val branchData: List<List<UpgradeSkillInfo>> = listOf(
        // 0: ARCHER
        listOf(
            UpgradeSkillInfo("SALVAGE", "Archers towers return 90% of value when sold.", 1),
            UpgradeSkillInfo("EAGLE EYE", "Increases archer attack range by 10%.", 1),
            UpgradeSkillInfo("PIERCING SHOTS", "Piercing shots ignore 10% of physical armor & reduce build cost.", 2),
            UpgradeSkillInfo("FAR SHOTS", "Further increases archer attack range by 10%.", 2),
            UpgradeSkillInfo("PRECISION", "Attacks have a 10% chance of dealing double damage.", 3)
        ),
        // 1: BARRACKS
        listOf(
            UpgradeSkillInfo("TOUGHNESS", "Trains soldiers with 10% more health.", 1),
            UpgradeSkillInfo("BETTER ARMOR", "Soldiers equip better armor & barracks cost less to build.", 1),
            UpgradeSkillInfo("IMPROVED DEPLOYMENT", "Increases rally point range & reduces soldier training time.", 2),
            UpgradeSkillInfo("ENDURANCE TRAINING", "Soldiers gain an additional 10% health bonus.", 2),
            UpgradeSkillInfo("SPIKED ARMOR", "When attacked, soldiers return 10% damage to the enemy.", 3)
        ),
        // 2: MAGE
        listOf(
            UpgradeSkillInfo("SPELL REACH", "Increases mage tower attack range by 10%.", 1),
            UpgradeSkillInfo("ARCANE SHATTER", "Magic attacks slowly destroy enemy physical armor on every hit.", 2),
            UpgradeSkillInfo("HERMETIC STUDY", "Mage towers construction and upgrading costs are reduced by 10%.", 2),
            UpgradeSkillInfo("EMPOWERED MAGIC", "Wizard attacks deal 15% increased magic damage.", 2),
            UpgradeSkillInfo("SLOW CURSE", "Magic attacks slow enemies' speed by half for a moment.", 3)
        ),
        // 3: ARTILLERY
        listOf(
            UpgradeSkillInfo("CONCENTRATED FIRE", "Increases artillery attack damage by 10%.", 1),
            UpgradeSkillInfo("RANGE FINDER", "Increases artillery attack range by 10%.", 1),
            UpgradeSkillInfo("FIELD LOGISTICS", "Artillery construction and upgrading costs are reduced by 10%.", 2),
            UpgradeSkillInfo("INDUSTRIALIZATION", "Artillery special abilities upgrade costs are reduced by 25%.", 3),
            UpgradeSkillInfo("SMART TARGETING", "Artillery suffers no splash reduction and deals max grape damage.", 3)
        ),
        // 4: RAIN OF FIRE
        listOf(
            UpgradeSkillInfo("BLAZING SKIES", "Adds 2 additional meteors and increases meteor damage.", 2),
            UpgradeSkillInfo("SCORCHED EARTH", "Meteors set the ground on fire for 5 seconds.", 2),
            UpgradeSkillInfo("THE FAST & FURIOUS", "Increases blast radius, damage and reduces cooldown by 10s.", 3),
            UpgradeSkillInfo("BLAZING EARTH", "Doubles damage and duration of scorched earth, reduces cooldown by 10s.", 3),
            UpgradeSkillInfo("CATACLYSM", "Increases meteor damage and drops extra meteors across the battlefield.", 3)
        ),
        // 5: REINFORCEMENTS
        listOf(
            UpgradeSkillInfo("WELL FED", "Well-fed farmers have additional health and deal a bit more damage.", 2),
            UpgradeSkillInfo("CONSCRIPTS", "Conscripts have more health, light armor and better gear.", 3),
            UpgradeSkillInfo("WARRIORS", "Warriors have even more health and are better equipped.", 3),
            UpgradeSkillInfo("LEGIONNAIRES", "Legionnaires have the most health and wear medium armor.", 3),
            UpgradeSkillInfo("SPEAR THROW", "Gives Legionnaires a ranged spear attack that can target flying enemies.", 4)
        )
    )

    fun open() {
        selectedBranch = 0
        selectedTier = (UpgradeManager.archerLevel + 1).coerceIn(1, 5)
        undoStack.clear()
    }

    private fun getBranchLevel(branch: Int): Int = when (branch) {
        0 -> UpgradeManager.archerLevel
        1 -> UpgradeManager.barracksLevel
        2 -> UpgradeManager.mageLevel
        3 -> UpgradeManager.artilleryLevel
        4 -> UpgradeManager.rainLevel
        else -> UpgradeManager.reinfLevel
    }

    private fun decrementBranch(branch: Int) {
        when (branch) {
            0 -> if (UpgradeManager.archerLevel > 0) UpgradeManager.archerLevel--
            1 -> if (UpgradeManager.barracksLevel > 0) UpgradeManager.barracksLevel--
            2 -> if (UpgradeManager.mageLevel > 0) UpgradeManager.mageLevel--
            3 -> if (UpgradeManager.artilleryLevel > 0) UpgradeManager.artilleryLevel--
            4 -> if (UpgradeManager.rainLevel > 0) UpgradeManager.rainLevel--
            5 -> if (UpgradeManager.reinfLevel > 0) UpgradeManager.reinfLevel--
        }
        // Lưu lại bằng cách cộng trừ qua prefs của UpgradeManager
        val a = UpgradeManager.archerLevel
        val b = UpgradeManager.barracksLevel
        val m = UpgradeManager.mageLevel
        val ar = UpgradeManager.artilleryLevel
        val r = UpgradeManager.rainLevel
        val re = UpgradeManager.reinfLevel
        UpgradeManager.resetAllUpgrades()
        repeat(a) { UpgradeManager.upgradeBranch(0) }
        repeat(b) { UpgradeManager.upgradeBranch(1) }
        repeat(m) { UpgradeManager.upgradeBranch(2) }
        repeat(ar) { UpgradeManager.upgradeBranch(3) }
        repeat(r) { UpgradeManager.upgradeBranch(4) }
        repeat(re) { UpgradeManager.upgradeBranch(5) }
    }

    // TRẢ VỀ FALSE NẾU BẤM NÚT DONE ĐỂ ĐÓNG BẢNG UPGRADES
    fun handleTouch(x: Float, y: Float): Boolean {
        val boardX = 110f
        val boardY = 30f
        val boardW = 1060f
        val boardH = 645f

        // Bấm ra ngoài bảng -> đóng
        if (x !in boardX..(boardX + boardW) || y !in boardY..(boardY + boardH)) {
            SoundManager.playClick()
            return false
        }

        // 1. Nút RESET (x: 345..475, y: 46..92)
        if (x in 345f..475f && y in 46f..92f) {
            SoundManager.playClick()
            UpgradeManager.resetAllUpgrades()
            undoStack.clear()
            return true
        }

        // 2. Nút UNDO (x: 825..945, y: 46..92)
        if (x in 825f..945f && y in 46f..92f) {
            if (undoStack.isNotEmpty()) {
                val lastBranch = undoStack.removeAt(undoStack.lastIndex)
                SoundManager.playClick()
                decrementBranch(lastBranch)
            }
            return true
        }

        // 3. Nút DONE (x: 960..1085, y: 46..92)
        if (x in 960f..1085f && y in 46f..92f) {
            SoundManager.playClick()
            return false
        }

        // 4. Nút BUY ở thanh mô tả dưới (x: 705..805, y: 46..92)
        val currentLvl = getBranchLevel(selectedBranch)
        if (selectedTier == currentLvl + 1 && UpgradeManager.canUpgrade(selectedBranch)) {
            if (x in 705f..805f && y in 46f..92f) {
                if (UpgradeManager.upgradeBranch(selectedBranch)) {
                    SoundManager.playTowerUpgrade()
                    undoStack.add(selectedBranch)
                    selectedTier = (getBranchLevel(selectedBranch) + 1).coerceAtMost(5)
                }
                return true
            }
        }

        // 5. Kiểm tra chạm vào 30 Ô KỸ NĂNG (6 cột x 5 hàng)
        val startX = 225f
        val colSpacing = 148f
        val tier1Y = 235f
        val rowSpacing = 74f

        for (col in 0..5) {
            val cx = startX + col * colSpacing
            // Bấm vào huy hiệu gốc ở đáy cột -> chọn tầng tiếp theo của nhánh đó
            if (Vector2(cx, 152f).dst(x, y) <= 36f) {
                SoundManager.playTowerPreview()
                selectedBranch = col
                selectedTier = (getBranchLevel(col) + 1).coerceIn(1, 5)
                return true
            }

            for (tier in 1..5) {
                val cy = tier1Y + (tier - 1) * rowSpacing
                if (x in (cx - 30f)..(cx + 30f) && y in (cy - 30f)..(cy + 30f)) {
                    val branchLvl = getBranchLevel(col)
                    if (selectedBranch == col && selectedTier == tier && tier == branchLvl + 1) {
                        // Bấm lần 2 vào ô kế tiếp đang chọn -> Nâng cấp ngay lập tức!
                        if (UpgradeManager.upgradeBranch(col)) {
                            SoundManager.playTowerUpgrade()
                            undoStack.add(col)
                            selectedTier = (getBranchLevel(col) + 1).coerceAtMost(5)
                        } else {
                            SoundManager.playClick()
                        }
                    } else {
                        SoundManager.playTowerPreview()
                        selectedBranch = col
                        selectedTier = tier
                    }
                    return true
                }
            }
        }

        return true
    }

    fun render(
        sr: ShapeRenderer,
        batch: SpriteBatch,
        titleFont: BitmapFont,
        font: BitmapFont,
        smallFont: BitmapFont
    ) {
        val boardX = 110f
        val boardY = 30f
        val boardW = 1060f
        val boardH = 645f

        val startX = 225f
        val colSpacing = 148f
        val tier1Y = 235f
        val rowSpacing = 74f

        sr.begin(ShapeRenderer.ShapeType.Filled)
        // 1. NỀN MỜ & BẢNG GỖ TREO XÍCH CHUẨN HÌNH MẪU
        sr.color = Color(0f, 0f, 0f, 0.60f)
        sr.rect(0f, 0f, Config.WORLD_WIDTH, Config.WORLD_HEIGHT)

        for (chainX in listOf(boardX + 65f, boardX + boardW - 75f)) {
            sr.color = Color(0.25f, 0.26f, 0.28f, 1f)
            sr.rect(chainX, boardY + boardH - 5f, 12f, 50f)
            sr.color = Color(0.58f, 0.60f, 0.65f, 1f)
            for (cy in (boardY + boardH).toInt()..715 step 14) {
                sr.rect(chainX + 2f, cy.toFloat(), 8f, 9f)
            }
        }

        // Khung viền sắt & mặt bảng gỗ
        sr.color = Color(0.55f, 0.54f, 0.50f, 1f)
        sr.rect(boardX - 6f, boardY - 6f, boardW + 12f, boardH + 12f)
        sr.color = Color(0.20f, 0.16f, 0.12f, 1f)
        sr.rect(boardX - 2f, boardY - 2f, boardW + 4f, boardH + 4f)
        sr.color = Color(0.28f, 0.22f, 0.16f, 1f)
        sr.rect(boardX, boardY, boardW, boardH)

        // Vân gỗ ngang
        sr.color = Color(0.24f, 0.18f, 0.13f, 1f)
        for (gy in (boardY + 115f).toInt() until (boardY + boardH - 30f).toInt() step 44) {
            sr.rect(boardX + 20f, gy.toFloat(), boardW - 40f, 4f)
        }

        // 4 góc sắt đinh tán
        val corners = listOf(
            Vector2(boardX - 4f, boardY + boardH - 22f),
            Vector2(boardX + boardW - 22f, boardY + boardH - 22f),
            Vector2(boardX - 4f, boardY - 4f),
            Vector2(boardX + boardW - 22f, boardY - 4f)
        )
        for (c in corners) {
            sr.color = Color(0.62f, 0.64f, 0.66f, 1f)
            sr.rect(c.x, c.y, 26f, 26f)
            sr.color = Color(0.28f, 0.28f, 0.30f, 1f)
            sr.circle(c.x + 6f, c.y + 6f, 2.2f)
            sr.circle(c.x + 20f, c.y + 6f, 2.2f)
            sr.circle(c.x + 6f, c.y + 20f, 2.2f)
            sr.circle(c.x + 20f, c.y + 20f, 2.2f)
        }

        // Băng rôn vàng UPGRADES trên đỉnh
        val bannerCx = boardX + boardW / 2f
        val topY = boardY + boardH
        sr.color = Color(0.72f, 0.56f, 0.22f, 1f)
        sr.rect(bannerCx - 225f, topY - 44f, 60f, 42f)
        sr.rect(bannerCx + 165f, topY - 44f, 60f, 42f)
        sr.color = Color(0.52f, 0.38f, 0.12f, 1f)
        sr.rect(bannerCx - 175f, topY - 34f, 350f, 60f)
        sr.color = Color(0.94f, 0.78f, 0.36f, 1f)
        sr.rect(bannerCx - 170f, topY - 30f, 340f, 52f)
        sr.color = Color(0.98f, 0.86f, 0.48f, 1f)
        sr.rect(bannerCx - 164f, topY - 24f, 328f, 40f)

        // 2. 6 RÃNH DỌC TỐI & ĐƯỜNG KẺ VÀNG NỐI CÁC CẤP ĐÃ MỞ KHÓA
        for (col in 0..5) {
            val cx = startX + col * colSpacing
            val lvl = getBranchLevel(col)

            // Rãnh gỗ trũng tối phía sau cột
            sr.color = Color(0.21f, 0.16f, 0.12f, 1f)
            sr.rect(cx - 38f, 125f, 76f, 452f)
            sr.circle(cx, 577f, 38f)

            // Đường kẻ nối từ Huy hiệu đáy lên các tầng đã nâng cấp
            for (t in 1..5) {
                val fromY = if (t == 1) 180f else tier1Y + (t - 2) * rowSpacing + 28f
                val toY = tier1Y + (t - 1) * rowSpacing - 28f
                sr.color = if (lvl >= t) Color(1f, 0.86f, 0.15f, 1f) else Color(0.14f, 0.11f, 0.08f, 1f)
                sr.rect(cx - 2.5f, fromY, 5f, toY - fromY)
            }

            // VẼ HUY HIỆU KHIÊN SẮT ĐÍNH NGỌC XANH Ở ĐÁY MỖI CỘT (CHUẨN HÌNH MẪU)
            drawColumnBaseCrest(sr, cx, 152f, col)

            // VẼ 5 Ô KỸ NĂNG TỪ TIER 1 (DƯỚI) LÊN TIER 5 (TRÊN)
            for (tier in 1..5) {
                val cy = tier1Y + (tier - 1) * rowSpacing
                val isUnlocked = (tier <= lvl)
                val isSelected = (selectedBranch == col && selectedTier == tier)
                val cost = branchData[col][tier - 1].cost
                drawUpgradeNode(sr, cx, cy, col, tier, isUnlocked, isSelected, cost)
            }
        }

        // 3. THANH ĐIỀU KHIỂN DƯỚI CÙNG: Ô SAO + NÚT RESET + HỘP THÔNG TIN + UNDO + DONE
        // Hộp hiển thị số Sao còn lại bên trái
        sr.color = Color(0.20f, 0.15f, 0.11f, 1f)
        sr.rect(195f, 52f, 115f, 34f)
        sr.circle(210f, 69f, 20f)

        // Nút RESET
        drawBeveledButton(sr, 345f, 46f, 130f, 46f, isEnabled = true, isPrimary = false)

        // Thanh mô tả kỹ năng đang chọn ở giữa (x: 488..812)
        sr.color = Color(0.20f, 0.15f, 0.11f, 0.95f)
        sr.rect(488f, 42f, 324f, 56f)
        sr.color = Color(0.48f, 0.40f, 0.28f, 1f)
        sr.rect(488f, 96f, 324f, 2f)
        sr.rect(488f, 42f, 324f, 2f)

        val canBuySelected = (selectedTier == getBranchLevel(selectedBranch) + 1) && UpgradeManager.canUpgrade(selectedBranch)
        if (canBuySelected) {
            // Nút BUY màu vàng xanh nổi bật bên phải hộp mô tả
            sr.color = Color(0.18f, 0.55f, 0.22f, 1f)
            sr.rect(712f, 49f, 92f, 42f)
            sr.color = Color(0.28f, 0.75f, 0.32f, 1f)
            sr.rect(715f, 52f, 86f, 36f)
        }

        // Nút UNDO
        drawBeveledButton(sr, 825f, 46f, 120f, 46f, isEnabled = undoStack.isNotEmpty(), isPrimary = false)

        // Nút DONE
        drawBeveledButton(sr, 960f, 46f, 125f, 46f, isEnabled = true, isPrimary = true)
        sr.end()

        // Vẽ Ngôi Sao Vàng 5 cánh lớn ở góc dưới trái
        GameUIRenderer.drawStar(sr, 212f, 69f, 22f, true)

        // Vẽ các Ngôi Sao nhỏ trong nhãn giá của từng ô chưa mở khóa
        for (col in 0..5) {
            val cx = startX + col * colSpacing
            val lvl = getBranchLevel(col)
            for (tier in 1..5) {
                if (tier > lvl) {
                    val cy = tier1Y + (tier - 1) * rowSpacing
                    GameUIRenderer.drawStar(sr, cx + 6f, cy - 24f, 5.2f, true)
                }
            }
        }

        // 4. VẼ CHỮ TIÊU ĐỀ, GIÁ SAO & THÔNG TIN CHI TIẾT
        batch.begin()
        val oldTitleScale = titleFont.data.scaleX
        titleFont.data.setScale(2.05f)
        GameUIRenderer.drawOutlinedText(batch, titleFont, "UPGRADES", bannerCx - 92f, topY + 6f, Color(0.28f, 0.18f, 0.08f, 1f))
        titleFont.data.setScale(oldTitleScale)

        // Số sao hiện có
        GameUIRenderer.drawOutlinedText(batch, font, "${UpgradeManager.getAvailableStars()}", 254f, 76f, Color(1f, 0.88f, 0.22f, 1f))

        // Chữ trên các nút bấm
        GameUIRenderer.drawBadgeCenteredText(batch, font, "RESET", 345f, 46f, 130f, 46f, Color.WHITE)
        val undoColor = if (undoStack.isNotEmpty()) Color.WHITE else Color(0.55f, 0.52f, 0.48f, 1f)
        GameUIRenderer.drawBadgeCenteredText(batch, font, "UNDO", 825f, 46f, 120f, 46f, undoColor)
        GameUIRenderer.drawBadgeCenteredText(batch, font, "DONE", 960f, 46f, 125f, 46f, Color.WHITE)

        // Giá sao nhỏ ở góc dưới phải của từng ô chưa mở khóa
        val oldSmallScale = smallFont.data.scaleX
        smallFont.data.setScale(0.85f)
        for (col in 0..5) {
            val cx = startX + col * colSpacing
            val lvl = getBranchLevel(col)
            for (tier in 1..5) {
                if (tier > lvl) {
                    val cy = tier1Y + (tier - 1) * rowSpacing
                    val cost = branchData[col][tier - 1].cost
                    GameUIRenderer.drawOutlinedText(batch, smallFont, "$cost", cx + 14f, cy - 19f, Color.WHITE)
                }
            }
        }

        // Thông tin kỹ năng đang chọn ở thanh giữa dưới
        val selInfo = branchData[selectedBranch][selectedTier - 1]
        val isSelOwned = selectedTier <= getBranchLevel(selectedBranch)
        val statusTag = when {
            isSelOwned -> " (UNLOCKED)"
            canBuySelected -> " (TAP AGAIN OR BUY)"
            else -> " (COST: ${selInfo.cost} STARS)"
        }
        GameUIRenderer.drawOutlinedText(batch, smallFont, selInfo.title + statusTag, 496f, 90f, Color(1f, 0.88f, 0.25f, 1f))

        smallFont.data.setScale(0.76f)
        val maxChars = if (canBuySelected) 32 else 46
        val words = selInfo.description.split(" ")
        var line1 = ""
        var line2 = ""
        for (w in words) {
            if ((line1.length + w.length + 1) <= maxChars) {
                line1 = if (line1.isEmpty()) w else "$line1 $w"
            } else {
                line2 = if (line2.isEmpty()) w else "$line2 $w"
            }
        }
        GameUIRenderer.drawShadowedText(batch, smallFont, line1, 496f, 71f, Color(0.92f, 0.90f, 0.84f, 1f))
        if (line2.isNotEmpty()) {
            GameUIRenderer.drawShadowedText(batch, smallFont, line2, 496f, 55f, Color(0.92f, 0.90f, 0.84f, 1f))
        }

        if (canBuySelected) {
            smallFont.data.setScale(0.95f)
            GameUIRenderer.drawBadgeCenteredText(batch, smallFont, "BUY (${selInfo.cost} )", 712f, 49f, 92f, 42f, Color.YELLOW)
        }
        smallFont.data.setScale(oldSmallScale)
        batch.end()
        if (canBuySelected) {
            GameUIRenderer.drawStar(sr, 779f, 70f, 5.5f, true)
        }
    }

    private fun drawBeveledButton(
        sr: ShapeRenderer,
        x: Float,
        y: Float,
        w: Float,
        h: Float,
        isEnabled: Boolean,
        isPrimary: Boolean
    ) {
        sr.color = if (isEnabled) Color(0.55f, 0.50f, 0.42f, 1f) else Color(0.35f, 0.32f, 0.28f, 1f)
        sr.rect(x - 2f, y - 2f, w + 4f, h + 4f)
        sr.color = when {
            !isEnabled -> Color(0.24f, 0.20f, 0.16f, 1f)
            isPrimary -> Color(0.32f, 0.26f, 0.18f, 1f)
            else -> Color(0.28f, 0.23f, 0.17f, 1f)
        }
        sr.rect(x, y, w, h)
        sr.color = when {
            !isEnabled -> Color(0.28f, 0.24f, 0.19f, 1f)
            isPrimary -> Color(0.42f, 0.34f, 0.24f, 1f)
            else -> Color(0.36f, 0.30f, 0.22f, 1f)
        }
        sr.rect(x + 4f, y + 4f, w - 8f, h - 8f)
    }

    // VẼ HUY HIỆU KHIÊN SẮT ĐÍNH NGỌC XANH Ở ĐÁY CỘT (CHUẨN HÌNH MẪU)
    private fun drawColumnBaseCrest(sr: ShapeRenderer, cx: Float, cy: Float, col: Int) {
        // Khung khiên sắt ngoài
        sr.color = Color(0.58f, 0.60f, 0.64f, 1f)
        sr.rect(cx - 32f, cy - 14f, 64f, 44f)
        sr.triangle(cx - 32f, cy - 14f, cx + 32f, cy - 14f, cx, cy - 34f)

        // Lòng khiên tối
        sr.color = Color(0.22f, 0.20f, 0.18f, 1f)
        sr.rect(cx - 26f, cy - 11f, 52f, 36f)
        sr.triangle(cx - 26f, cy - 11f, cx + 26f, cy - 11f, cx, cy - 27f)

        // Viền răng cưa trên đỉnh khiên & viên ngọc xanh ở mũi khiên dưới
        sr.color = Color(0.75f, 0.78f, 0.82f, 1f)
        for (rx in listOf(-28f, -14f, 0f, 14f, 24f)) {
            sr.rect(cx + rx, cy + 27f, 6f, 5f)
        }
        sr.color = Color(0.20f, 0.75f, 1f, 1f)
        sr.circle(cx, cy - 23f, 4.2f)
        sr.color = Color.WHITE
        sr.circle(cx - 1f, cy - 22f, 1.6f)

        // Biểu tượng đặc trưng của từng nhánh bên trong huy hiệu đáy
        val icY = cy + 6f
        when (col) {
            0 -> { // ARCHER: 3 mũi tên chéo
                for (off in listOf(-7f, 0f, 7f)) {
                    sr.color = Color(0.72f, 0.52f, 0.24f, 1f)
                    sr.rectLine(cx + off - 5f, icY - 8f, cx + off + 5f, icY + 6f, 2.2f)
                    sr.color = Color(0.90f, 0.92f, 0.96f, 1f)
                    sr.triangle(cx + off + 7f, icY + 9f, cx + off + 1f, icY + 6f, cx + off + 6f, icY + 1f)
                }
            }
            1 -> { // BARRACKS: Khiên sắt Hiệp Sĩ
                sr.color = Color(0.85f, 0.88f, 0.92f, 1f)
                sr.rect(cx - 10f, icY - 4f, 20f, 14f)
                sr.triangle(cx - 10f, icY - 4f, cx + 10f, icY - 4f, cx, icY - 14f)
                sr.color = Color(0.45f, 0.50f, 0.58f, 1f)
                sr.rect(cx - 7f, icY - 2f, 14f, 10f)
                sr.triangle(cx - 7f, icY - 2f, cx + 7f, icY - 2f, cx, icY - 10f)
            }
            2 -> { // MAGE: Trượng phép đính ngọc xanh
                sr.color = Color(0.85f, 0.65f, 0.20f, 1f)
                sr.rectLine(cx - 8f, icY - 11f, cx + 4f, icY + 5f, 4f)
                sr.color = Color(0.30f, 0.88f, 1f, 1f)
                sr.circle(cx + 5f, icY + 6f, 7.5f)
                sr.color = Color.WHITE
                sr.circle(cx + 3f, icY + 8f, 3f)
            }
            3 -> { // ARTILLERY: Quả bom đen cháy dây cháy chậm
                sr.color = Color(0.32f, 0.35f, 0.40f, 1f)
                sr.circle(cx, icY - 3f, 11f)
                sr.color = Color(0.65f, 0.70f, 0.76f, 1f)
                sr.circle(cx - 3.5f, icY + 1f, 3f)
                sr.color = Color(1f, 0.65f, 0.12f, 1f)
                sr.circle(cx + 5f, icY + 10f, 4f)
            }
            4 -> { // RAIN OF FIRE: Thiên thạch lửa
                sr.color = Color(1f, 0.45f, 0.08f, 1f)
                sr.triangle(cx - 4f, icY - 6f, cx + 6f, icY + 2f, cx + 11f, icY + 12f)
                sr.circle(cx - 3f, icY - 3f, 9f)
                sr.color = Color(1f, 0.88f, 0.20f, 1f)
                sr.circle(cx - 4f, icY - 4f, 5.5f)
            }
            5 -> { // REINFORCEMENTS: Song kiếm chéo phát sáng xanh
                sr.color = Color(0.25f, 0.78f, 1f, 0.55f)
                sr.circle(cx, icY, 13f)
                sr.color = Color(0.90f, 0.94f, 0.98f, 1f)
                sr.rectLine(cx - 10f, icY - 9f, cx + 10f, icY + 10f, 3.5f)
                sr.rectLine(cx + 10f, icY - 9f, cx - 10f, icY + 10f, 3.5f)
                sr.color = Color.GOLD
                sr.circle(cx - 7f, icY - 6f, 3f)
                sr.circle(cx + 7f, icY - 6f, 3f)
            }
        }
    }

    // VẼ TỪNG Ô NÂNG CẤP (CÓ BIỂU TƯỢNG RIÊNG CHO CẢ 30 KỸ NĂNG & NHÃN GIÁ SAO)
    private fun drawUpgradeNode(
        sr: ShapeRenderer,
        cx: Float,
        cy: Float,
        col: Int,
        tier: Int,
        isUnlocked: Boolean,
        isSelected: Boolean,
        cost: Int
    ) {
        val half = 28f

        // Hào quang vàng khi đang được chọn
        if (isSelected) {
            sr.color = Color(1f, 0.95f, 0.25f, 0.85f)
            sr.rect(cx - half - 4f, cy - half - 4f, (half + 4f) * 2f, (half + 4f) * 2f)
        }

        // Khung bát giác vát góc (Vàng cam nếu đã mở khóa, Bạc xám nếu chưa mở)
        sr.color = if (isUnlocked) Color(0.98f, 0.86f, 0.35f, 1f) else Color(0.68f, 0.70f, 0.74f, 1f)
        sr.rect(cx - half, cy - half, half * 2f, half * 2f)

        sr.color = if (isUnlocked) Color(0.88f, 0.58f, 0.12f, 1f) else Color(0.42f, 0.44f, 0.48f, 1f)
        sr.rect(cx - half + 3f, cy - half + 3f, (half - 3f) * 2f, (half - 3f) * 2f)

        // Màu nền bên trong ô
        val bgColor = if (!isUnlocked) {
            Color(0.55f, 0.57f, 0.60f, 1f)
        } else {
            when {
                col == 0 && tier == 3 -> Color(0.62f, 0.38f, 0.85f, 1f) // Piercing Shots: Tím
                col == 0 && tier == 2 -> Color(0.92f, 0.55f, 0.18f, 1f) // Eagle Eye: Cam
                col == 1 && tier == 2 -> Color(0.32f, 0.72f, 0.95f, 1f) // Better Armor: Xanh lam
                col == 2 && tier in 1..2 -> Color(0.35f, 0.78f, 0.98f, 1f) // Mage: Xanh phép
                col == 3 && tier == 2 -> Color(0.45f, 0.78f, 0.15f, 1f) // Range Finder: Xanh radar
                col == 4 -> Color(0.95f, 0.52f, 0.12f, 1f)              // Rain of Fire: Cam rực
                else -> Color(0.96f, 0.80f, 0.22f, 1f)                  // Mặc định: Vàng kim
            }
        }
        sr.color = bgColor
        sr.rect(cx - half + 6f, cy - half + 6f, (half - 6f) * 2f, (half - 6f) * 2f)

        // Vẽ biểu tượng riêng biệt cho từng kỹ năng trong 30 ô
        drawSpecificUpgradeIcon(sr, cx, cy, col, tier, isUnlocked)

        // Nhãn giá Sao (★ Cost) ở góc dưới bên phải cho các ô chưa mở khóa
        if (!isUnlocked) {
            sr.color = Color(0.16f, 0.16f, 0.18f, 0.96f)
            sr.rect(cx - 2f, cy - 31f, 32f, 15f)
            sr.color = Color(0.58f, 0.60f, 0.64f, 1f)
            sr.rect(cx - 2f, cy - 17f, 32f, 1.5f)
        }
    }

    private fun tint(c: Color, isUnlocked: Boolean): Color {
        if (isUnlocked) return c
        val gray = (c.r * 0.3f + c.g * 0.59f + c.b * 0.11f).coerceIn(0.22f, 0.85f)
        return Color(gray, gray, gray, c.a)
    }

    private fun drawSpecificUpgradeIcon(
        sr: ShapeRenderer,
        cx: Float,
        cy: Float,
        col: Int,
        tier: Int,
        u: Boolean
    ) {
        when (col) {
            // CỘT 0: ARCHER
            0 -> when (tier) {
                1 -> { // Salvage: Chồng tiền vàng
                    sr.color = tint(Color(0.85f, 0.62f, 0.10f, 1f), u)
                    sr.circle(cx - 5f, cy - 4f, 8f)
                    sr.circle(cx - 5f, cy + 2f, 8f)
                    sr.color = tint(Color(1f, 0.92f, 0.25f, 1f), u)
                    sr.circle(cx + 6f, cy - 2f, 8.5f)
                }
                2, 4 -> { // Eagle Eye / Far Shots: Bia hồng tâm
                    sr.color = tint(Color.WHITE, u)
                    sr.circle(cx, cy, 15f)
                    sr.color = tint(Color(0.90f, 0.22f, 0.15f, 1f), u)
                    sr.circle(cx, cy, 11f)
                    sr.color = tint(Color.WHITE, u)
                    sr.circle(cx, cy, 7f)
                    sr.color = tint(Color(0.90f, 0.22f, 0.15f, 1f), u)
                    sr.circle(cx, cy, 3.5f)
                }
                3 -> { // Piercing Shots: Mũi tên xuyên phá
                    sr.color = tint(Color(0.55f, 0.35f, 0.18f, 1f), u)
                    sr.rectLine(cx + 10f, cy - 10f, cx - 4f, cy + 4f, 4.5f)
                    sr.color = tint(Color(0.88f, 0.90f, 0.96f, 1f), u)
                    sr.triangle(cx - 12f, cy + 12f, cx - 10f, cy - 2f, cx + 2f, cy + 10f)
                }
                5 -> { // Precision: Táo cắm mũi tên
                    sr.color = tint(Color(0.85f, 0.18f, 0.15f, 1f), u)
                    sr.circle(cx, cy - 2f, 10f)
                    sr.color = tint(Color(0.92f, 0.95f, 0.98f, 1f), u)
                    sr.rectLine(cx - 14f, cy + 8f, cx + 14f, cy - 6f, 3f)
                }
            }

            // CỘT 1: BARRACKS
            1 -> when (tier) {
                1, 4 -> { // Toughness / Endurance: Cánh tay cơ bắp
                    sr.color = tint(Color(0.88f, 0.65f, 0.38f, 1f), u)
                    sr.circle(cx + 3f, cy - 4f, 9f)
                    sr.rect(cx - 10f, cy - 8f, 14f, 9f)
                    sr.circle(cx - 8f, cy + 5f, 7f)
                }
                2 -> { // Better Armor: Khiên sắt lớn
                    sr.color = tint(Color(0.88f, 0.92f, 0.96f, 1f), u)
                    sr.rect(cx - 11f, cy - 3f, 22f, 15f)
                    sr.triangle(cx - 11f, cy - 3f, cx + 11f, cy - 3f, cx, cy - 16f)
                    sr.color = tint(Color(0.42f, 0.48f, 0.56f, 1f), u)
                    sr.rect(cx - 8f, cy - 1f, 16f, 11f)
                    sr.triangle(cx - 8f, cy - 1f, cx + 8f, cy - 1f, cx, cy - 12f)
                }
                3 -> { // Improved Deployment: Lính + Vòng xoay
                    sr.color = tint(Color(0.30f, 0.34f, 0.38f, 1f), u)
                    sr.circle(cx, cy + 5f, 5.5f)
                    sr.rect(cx - 6f, cy - 9f, 12f, 10f)
                }
                5 -> { // Spiked Armor: Giáp vai gai nhọn
                    sr.color = tint(Color(0.45f, 0.48f, 0.54f, 1f), u)
                    sr.circle(cx, cy - 2f, 12f)
                    sr.color = tint(Color(0.92f, 0.94f, 0.98f, 1f), u)
                    sr.triangle(cx - 10f, cy + 4f, cx - 4f, cy + 8f, cx - 14f, cy + 14f)
                    sr.triangle(cx - 2f, cy + 8f, cx + 4f, cy + 8f, cx + 1f, cy + 17f)
                    sr.triangle(cx + 6f, cy + 4f, cx + 11f, cy, cx + 16f, cy + 11f)
                }
            }

            // CỘT 2: MAGE
            2 -> when (tier) {
                1 -> { // Spell Reach: Bàn tay phóng sét phép
                    sr.color = tint(Color.WHITE, u)
                    sr.circle(cx - 2f, cy + 2f, 9f)
                    sr.color = tint(Color(0.25f, 0.30f, 0.38f, 1f), u)
                    sr.rectLine(cx + 10f, cy - 12f, cx, cy - 2f, 7f)
                }
                2 -> { // Arcane Shatter: Mảnh giáp vỡ
                    sr.color = tint(Color.WHITE, u)
                    sr.circle(cx, cy, 8f)
                    sr.color = tint(Color(0.38f, 0.44f, 0.56f, 1f), u)
                    for (off in listOf(Vector2(-9f, 7f), Vector2(9f, 8f), Vector2(-8f, -8f), Vector2(8f, -7f))) {
                        sr.rect(cx + off.x - 3.5f, cy + off.y - 3.5f, 7f, 7f)
                    }
                }
                3 -> { // Hermetic Study: Cuốn sách phép mở
                    sr.color = tint(Color(0.65f, 0.32f, 0.14f, 1f), u)
                    sr.rect(cx - 14f, cy - 9f, 28f, 18f)
                    sr.color = tint(Color(0.98f, 0.92f, 0.72f, 1f), u)
                    sr.rect(cx - 12f, cy - 6f, 11f, 14f)
                    sr.rect(cx + 1f, cy - 6f, 11f, 14f)
                }
                4 -> { // Empowered Magic: Tia nổ ma thuật
                    sr.color = tint(Color.WHITE, u)
                    sr.circle(cx, cy, 9f)
                    sr.triangle(cx - 15f, cy, cx, cy + 5f, cx, cy - 5f)
                    sr.triangle(cx + 15f, cy, cx, cy + 5f, cx, cy - 5f)
                    sr.triangle(cx, cy + 15f, cx - 5f, cy, cx + 5f, cy)
                    sr.triangle(cx, cy - 15f, cx - 5f, cy, cx + 5f, cy)
                }
                5 -> { // Slow Curse: Đồng hồ cát
                    sr.color = tint(Color(0.45f, 0.32f, 0.18f, 1f), u)
                    sr.rect(cx - 9f, cy + 10f, 18f, 3.5f)
                    sr.rect(cx - 9f, cy - 13.5f, 18f, 3.5f)
                    sr.color = tint(Color(0.82f, 0.92f, 0.98f, 1f), u)
                    sr.triangle(cx - 8f, cy + 10f, cx + 8f, cy + 10f, cx, cy)
                    sr.triangle(cx - 8f, cy - 10f, cx + 8f, cy - 10f, cx, cy)
                }
            }

            // CỘT 3: ARTILLERY
            3 -> when (tier) {
                1 -> { // Concentrated Fire: Vụ nổ mảnh đạn
                    sr.color = tint(Color(0.95f, 0.38f, 0.08f, 1f), u)
                    sr.circle(cx, cy, 13f)
                    sr.color = tint(Color(1f, 0.92f, 0.25f, 1f), u)
                    sr.circle(cx, cy, 7f)
                    sr.color = tint(Color(0.28f, 0.30f, 0.34f, 1f), u)
                    sr.rect(cx - 10f, cy + 4f, 5f, 5f)
                    sr.rect(cx + 5f, cy + 3f, 5f, 5f)
                    sr.rect(cx - 3f, cy - 10f, 5f, 5f)
                }
                2 -> { // Range Finder: Vòng ngắm Radar xanh lá
                    sr.color = tint(Color(0.75f, 0.98f, 0.35f, 1f), u)
                    sr.circle(cx, cy, 14f)
                    sr.color = tint(Color(0.38f, 0.68f, 0.12f, 1f), u)
                    sr.circle(cx, cy, 11f)
                    sr.color = tint(Color(0.75f, 0.98f, 0.35f, 1f), u)
                    sr.rectLine(cx - 15f, cy, cx + 15f, cy, 1.8f)
                    sr.rectLine(cx, cy - 15f, cx, cy + 15f, 1.8f)
                }
                3 -> { // Field Logistics: Thùng gỗ tiếp tế
                    sr.color = tint(Color(0.58f, 0.38f, 0.18f, 1f), u)
                    sr.rect(cx - 12f, cy - 10f, 15f, 15f)
                    sr.color = tint(Color(0.72f, 0.48f, 0.24f, 1f), u)
                    sr.rect(cx - 1f, cy - 6f, 13f, 13f)
                }
                4 -> { // Industrialization: Nhà máy 2 ống khói
                    sr.color = tint(Color(0.32f, 0.34f, 0.38f, 1f), u)
                    sr.rect(cx - 12f, cy - 12f, 24f, 12f)
                    sr.rect(cx - 9f, cy, 6f, 11f)
                    sr.rect(cx + 2f, cy, 6f, 11f)
                }
                5 -> { // Smart Targeting: Màn hình máy tính ngắm mục tiêu
                    sr.color = tint(Color(0.28f, 0.30f, 0.34f, 1f), u)
                    sr.rect(cx - 12f, cy - 8f, 24f, 18f)
                    sr.rect(cx - 5f, cy - 13f, 10f, 5f)
                    sr.color = tint(Color(0.35f, 0.85f, 0.45f, 1f), u)
                    sr.rect(cx - 9f, cy - 5f, 18f, 12f)
                }
            }

            // CỘT 4: RAIN OF FIRE
            4 -> when (tier) {
                1, 5 -> { // Blazing Skies / Cataclysm: Cặp thiên thạch lửa
                    for (off in listOf(Vector2(-5f, 4f), Vector2(5f, -4f))) {
                        sr.color = tint(Color(0.92f, 0.25f, 0.08f, 1f), u)
                        sr.triangle(cx + off.x - 2f, cy + off.y - 2f, cx + off.x + 4f, cy + off.y + 2f, cx + off.x + 10f, cy + off.y + 10f)
                        sr.color = tint(Color(0.28f, 0.24f, 0.22f, 1f), u)
                        sr.circle(cx + off.x - 2f, cy + off.y - 2f, 5.5f)
                    }
                }
                2, 4 -> { // Scorched Earth / Blazing Earth: Ngọn lửa mặt đất
                    sr.color = tint(Color(0.92f, 0.22f, 0.06f, 1f), u)
                    sr.triangle(cx - 12f, cy - 10f, cx + 12f, cy - 10f, cx, cy + 14f)
                    sr.triangle(cx - 14f, cy - 10f, cx - 2f, cy - 10f, cx - 9f, cy + 8f)
                    sr.triangle(cx + 2f, cy - 10f, cx + 14f, cy - 10f, cx + 9f, cy + 8f)
                    sr.color = tint(Color(1f, 0.90f, 0.22f, 1f), u)
                    sr.triangle(cx - 7f, cy - 10f, cx + 7f, cy - 10f, cx, cy + 7f)
                }
                3 -> { // The Fast & The Furious: Hố thiên thạch nổ
                    sr.color = tint(Color(0.88f, 0.22f, 0.06f, 1f), u)
                    sr.circle(cx, cy - 4f, 13f)
                    sr.color = tint(Color(1f, 0.92f, 0.28f, 1f), u)
                    sr.circle(cx, cy - 4f, 8f)
                    sr.rect(cx - 3f, cy - 4f, 6f, 16f)
                }
            }

            // CỘT 5: REINFORCEMENTS
            5 -> when (tier) {
                1 -> { // Well Fed: Đùi gà nướng vàng
                    sr.color = tint(Color.WHITE, u)
                    sr.rectLine(cx - 9f, cy - 9f, cx - 2f, cy - 2f, 4f)
                    sr.circle(cx - 10f, cy - 7f, 2.5f)
                    sr.circle(cx - 7f, cy - 10f, 2.5f)
                    sr.color = tint(Color(0.78f, 0.42f, 0.14f, 1f), u)
                    sr.circle(cx + 4f, cy + 4f, 8.5f)
                }
                2 -> { // Conscripts: Áo giáp da nâu
                    sr.color = tint(Color(0.55f, 0.34f, 0.16f, 1f), u)
                    sr.rect(cx - 10f, cy - 11f, 20f, 20f)
                    sr.color = tint(Color(0.35f, 0.22f, 0.10f, 1f), u)
                    sr.circle(cx, cy + 9f, 5f)
                }
                3, 4 -> { // Warriors / Legionnaires: Giáp ngực thép bạc
                    sr.color = tint(Color(0.75f, 0.80f, 0.86f, 1f), u)
                    sr.rect(cx - 11f, cy - 11f, 22f, 20f)
                    sr.circle(cx - 11f, cy + 5f, 5.5f)
                    sr.circle(cx + 11f, cy + 5f, 5.5f)
                    sr.color = tint(Color(0.35f, 0.38f, 0.42f, 1f), u)
                    sr.circle(cx, cy + 9f, 5f)
                }
                5 -> { // Spear Throw: Ngọn giáo phóng
                    sr.color = tint(Color(0.58f, 0.36f, 0.16f, 1f), u)
                    sr.rectLine(cx + 12f, cy - 12f, cx - 4f, cy + 4f, 3.5f)
                    sr.color = tint(Color(0.88f, 0.92f, 0.96f, 1f), u)
                    sr.triangle(cx - 12f, cy + 12f, cx - 8f, cy, cx, cy + 8f)
                }
            }
        }
    }
}
