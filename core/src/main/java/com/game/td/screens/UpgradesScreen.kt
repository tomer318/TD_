package com.game.td.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.viewport.FitViewport
import com.game.td.Config
import com.game.td.managers.UpgradeManager

class UpgradesScreen(
    private val onReturn: () -> Unit = {}
) : ScreenAdapter() {

    private val camera = OrthographicCamera()
    private val viewport = FitViewport(Config.WORLD_WIDTH, Config.WORLD_HEIGHT, camera)
    private val shapeRenderer = ShapeRenderer()
    private val batch = SpriteBatch()
    private val titleFont = BitmapFont().apply { data.setScale(1.5f) }
    private val font = BitmapFont().apply { data.setScale(1.15f) }
    private val smallFont = BitmapFont().apply { data.setScale(0.9f) }

    private val touchPoint = Vector3()

    private val panelW = 920f
    private val panelH = 590f
    private val panelX = (Config.WORLD_WIDTH - panelW) / 2f   // 180f
    private val panelY = 85f

    private val colSpacing = 115f
    private val rowSpacing = 68f
    private val startX = panelX + 172f
    private val startY = panelY + 160f

    // Tọa độ hộp sao được đẩy thụt vào trong an toàn
    private val starBoxX = panelX + 48f
    private val starBoxY = panelY + panelH - 52f
    private val starBoxW = 145f
    private val starBoxH = 40f

    private val branchNames = listOf("ARCHER", "BARRACKS", "MAGE", "ARTILLERY", "RAIN", "ARMY")

    private var selectedUpgrade: Pair<Int, Int>? = null

    private val upgradeDetails = mapOf(
        Pair(0, 0) to Pair("Salvage", "Refund 90% of tower value when sold (normally 70%)."),
        Pair(0, 1) to Pair("Eagle Eye", "Increases Archer attack range by +10%."),
        Pair(0, 2) to Pair("Piercing Shots", "Arrows ignore 10% of physical enemy armor."),
        Pair(0, 3) to Pair("Far Shots", "Increases Archer attack range by another +10%."),
        Pair(0, 4) to Pair("Precision", "Archer attacks have a 10% chance to deal double damage."),

        Pair(1, 0) to Pair("Toughness Training", "Soldiers gain +10% maximum health."),
        Pair(1, 1) to Pair("Better Armor", "Soldiers gain +10% physical armor rating."),
        Pair(1, 2) to Pair("Improved Deployment", "Increases rally point range by 20%, reduces respawn by 2s."),
        Pair(1, 3) to Pair("Endurance Training", "Soldiers gain an additional +10% maximum health (+20% total)."),
        Pair(1, 4) to Pair("Spiked Armor", "Soldiers reflect 10% of incoming physical damage as True Damage."),

        Pair(2, 0) to Pair("Spell Reach", "Increases Mage attack range by +10%."),
        Pair(2, 1) to Pair("Arcane Shatter", "Magic attacks destroy 3% physical armor per hit."),
        Pair(2, 2) to Pair("Hermetic Study", "Mage tower build and upgrade costs reduced by 10%."),
        Pair(2, 3) to Pair("Empowered Magic", "Increases Mage attack damage by +15%."),
        Pair(2, 4) to Pair("Slow Curse", "Magic attacks slow enemy movement speed by 50% for 0.5s."),

        Pair(3, 0) to Pair("Concentrated Fire", "Increases Artillery attack damage by +10%."),
        Pair(3, 1) to Pair("Range Finder", "Increases Artillery attack range by +10%."),
        Pair(3, 2) to Pair("Field Logistic", "Artillery build and upgrade costs reduced by 10%."),
        Pair(3, 3) to Pair("Industrialization", "Artillery level 4 special skills cost 25% less gold."),
        Pair(3, 4) to Pair("Smart Targeting", "Explosion splash damage no longer diminishes with distance."),

        Pair(4, 0) to Pair("Blazing Skies", "Calls 5 meteors (50-80 damage each)."),
        Pair(4, 1) to Pair("Scorched Earth", "Meteors set the ground on fire, dealing 10-20 dmg/s for 5s."),
        Pair(4, 2) to Pair("The Fast & Furious", "Meteors deal 90-120 dmg, radius +25%, cooldown reduced to 70s."),
        Pair(4, 3) to Pair("Blazing Earth", "Doubles ground burn duration to 10s (20-30 dmg/s), cooldown reduced to 60s."),
        Pair(4, 4) to Pair("Cataclysm", "Rains 10 meteors dealing 150-180 damage across the battlefield."),

        Pair(5, 0) to Pair("Well Fed", "Farmers have 50 HP and deal 1-3 damage."),
        Pair(5, 1) to Pair("Conscripts", "Trains Conscripts with 70 HP, 10% armor, dealing 2-4 damage."),
        Pair(5, 2) to Pair("Warriors", "Trains Warriors with 90 HP, 20% armor, dealing 3-6 damage."),
        Pair(5, 3) to Pair("Legionnaires", "Trains Legionnaires with 110 HP, 30% armor, dealing 6-10 damage."),
        Pair(5, 4) to Pair("Spear Throw", "Legionnaires throw spears dealing 24-40 ranged damage.")
    )

    override fun show() {
        Gdx.input.inputProcessor = object : InputAdapter() {
            override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
                touchPoint.set(screenX.toFloat(), screenY.toFloat(), 0f)
                viewport.unproject(touchPoint)
                val x = touchPoint.x
                val y = touchPoint.y

                // 1. Nút DONE
                if (x in (panelX + panelW - 140f)..(panelX + panelW - 30f) && y in (panelY + 18f)..(panelY + 58f)) {
                    onReturn()
                    return true
                }

                // 2. Nút RESET
                if (x in (panelX + 30f)..(panelX + 140f) && y in (panelY + 18f)..(panelY + 58f)) {
                    UpgradeManager.resetAllUpgrades()
                    selectedUpgrade = null
                    return true
                }

                // 3. Nút BUY
                selectedUpgrade?.let { (col, row) ->
                    val (currentLvl, _) = UpgradeManager.getBranchInfo(col)
                    if (row == currentLvl && UpgradeManager.canUpgrade(col)) {
                        val buyBtnX = panelX + panelW - 140f
                        val buyBtnY = panelY + 70f
                        if (x in buyBtnX..(buyBtnX + 110f) && y in buyBtnY..(buyBtnY + 36f)) {
                            UpgradeManager.upgradeBranch(col)
                            return true
                        }
                    }
                }

                // 4. Bấm vào các ô nâng cấp
                for (col in 0 until 6) {
                    for (row in 0 until 5) {
                        val cx = startX + col * colSpacing
                        val cy = startY + row * rowSpacing
                        if (Vector2(cx, cy).dst(x, y) <= 26f) {
                            val (currentLvl, _) = UpgradeManager.getBranchInfo(col)

                            if (selectedUpgrade == Pair(col, row) && row == currentLvl && UpgradeManager.canUpgrade(col)) {
                                UpgradeManager.upgradeBranch(col)
                            } else {
                                selectedUpgrade = Pair(col, row)
                            }
                            return true
                        }
                    }
                }

                selectedUpgrade = null
                return true
            }
        }
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.10f, 0.08f, 0.07f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        camera.update()
        shapeRenderer.projectionMatrix = camera.combined
        batch.projectionMatrix = camera.combined

        // 1. VẼ HÌNH KHỐI NỀN
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)

        // Khung gỗ chính
        shapeRenderer.color = Color(0.38f, 0.28f, 0.18f, 1f)
        shapeRenderer.rect(panelX, panelY, panelW, panelH)

        shapeRenderer.color = Color(0.18f, 0.14f, 0.11f, 0.98f)
        shapeRenderer.rect(panelX + 12f, panelY + 12f, panelW - 24f, panelH - 24f)

        // Banner Tiêu đề trên cùng
        shapeRenderer.color = Color(0.85f, 0.65f, 0.22f, 1f)
        shapeRenderer.rect(Config.WORLD_WIDTH / 2f - 140f, panelY + panelH - 52f, 280f, 44f)

        // Khung nền hộp Sao (đã thụt vào trong gọn gàng)
        shapeRenderer.color = Color(0.12f, 0.10f, 0.08f, 0.92f)
        shapeRenderer.rect(starBoxX, starBoxY, starBoxW, starBoxH)

        // Nút RESET & DONE
        shapeRenderer.color = Color(0.50f, 0.22f, 0.18f, 1f)
        shapeRenderer.rect(panelX + 30f, panelY + 18f, 110f, 40f)

        shapeRenderer.color = Color(0.20f, 0.55f, 0.25f, 1f)
        shapeRenderer.rect(panelX + panelW - 140f, panelY + 18f, 110f, 40f)

        // Bảng Tooltip xem thông tin
        shapeRenderer.color = Color(0.12f, 0.10f, 0.08f, 0.95f)
        shapeRenderer.rect(panelX + 155f, panelY + 14f, panelW - 310f, 96f)

        // Vẽ 6 cột nâng cấp
        for (col in 0 until 6) {
            val (currentLvl, _) = UpgradeManager.getBranchInfo(col)
            for (row in 0 until 5) {
                val cx = startX + col * colSpacing
                val cy = startY + row * rowSpacing
                val isPurchased = row < currentLvl
                val isNext = row == currentLvl
                val canAfford = isNext && UpgradeManager.canUpgrade(col)
                val isSelected = selectedUpgrade == Pair(col, row)

                shapeRenderer.color = when {
                    isSelected -> Color.CYAN
                    isPurchased -> Color.GOLD
                    canAfford -> Color(0.3f, 0.85f, 0.3f, 1f)
                    isNext -> Color(0.85f, 0.50f, 0.15f, 1f)
                    else -> Color(0.30f, 0.30f, 0.34f, 1f)
                }
                shapeRenderer.circle(cx, cy, if (isSelected) 28f else 25f)

                shapeRenderer.color = when {
                    isPurchased -> Color(0.95f, 0.75f, 0.20f, 1f)
                    canAfford -> Color(0.18f, 0.45f, 0.18f, 1f)
                    isNext -> Color(0.35f, 0.25f, 0.15f, 1f)
                    else -> Color(0.14f, 0.14f, 0.16f, 1f)
                }
                shapeRenderer.circle(cx, cy, 21f)
            }
        }

        // Nút BUY khi có thể nâng
        selectedUpgrade?.let { (col, row) ->
            val (currentLvl, _) = UpgradeManager.getBranchInfo(col)
            if (row == currentLvl && UpgradeManager.canUpgrade(col)) {
                shapeRenderer.color = Color(0.85f, 0.65f, 0.15f, 1f)
                shapeRenderer.rect(panelX + panelW - 140f, panelY + 70f, 110f, 36f)
            }
        }
        shapeRenderer.end()

        // 2. VẼ NGÔI SAO VÀNG VECTOR TRONG HỘP
        drawStarVector(shapeRenderer, starBoxX + 26f, starBoxY + starBoxH / 2f, 13f, true)

        // 3. VIỀN ĐƯỜNG NÉT
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color.GOLD
        shapeRenderer.rect(panelX + 12f, panelY + 12f, panelW - 24f, panelH - 24f)
        shapeRenderer.rect(starBoxX, starBoxY, starBoxW, starBoxH)
        shapeRenderer.rect(panelX + 155f, panelY + 14f, panelW - 310f, 96f)
        shapeRenderer.rect(panelX + 30f, panelY + 18f, 110f, 40f)
        shapeRenderer.rect(panelX + panelW - 140f, panelY + 18f, 110f, 40f)

        selectedUpgrade?.let { (col, row) ->
            val (currentLvl, _) = UpgradeManager.getBranchInfo(col)
            if (row == currentLvl && UpgradeManager.canUpgrade(col)) {
                shapeRenderer.color = Color.WHITE
                shapeRenderer.rect(panelX + panelW - 140f, panelY + 70f, 110f, 36f)
            }
        }
        shapeRenderer.end()

        // 4. HIỂN THỊ CHỮ VÀ MÔ TẢ
        batch.begin()
        titleFont.color = Color(0.15f, 0.10f, 0.05f, 1f)
        titleFont.draw(batch, "UPGRADES", Config.WORLD_WIDTH / 2f - 70f, panelY + panelH - 20f)

        // Số sao hiển thị trong hộp sao
        font.color = Color.GOLD
        font.draw(batch, "${UpgradeManager.getAvailableStars()}", starBoxX + 52f, starBoxY + 27f)

        font.color = Color.WHITE
        font.draw(batch, "RESET", panelX + 55f, panelY + 44f)
        font.draw(batch, "DONE", panelX + panelW - 115f, panelY + 44f)

        for (col in 0 until 6) {
            val (currentLvl, costs) = UpgradeManager.getBranchInfo(col)
            val cx = startX + col * colSpacing

            smallFont.color = Color.GOLD
            smallFont.draw(batch, branchNames[col], cx - 26f, startY - 32f)

            for (row in 0 until 5) {
                val cy = startY + row * rowSpacing
                val isPurchased = row < currentLvl
                smallFont.color = if (isPurchased) Color.BLACK else Color.WHITE
                val txt = if (isPurchased) "OK" else "+${costs[row]}"
                smallFont.draw(batch, txt, cx - 11f, cy + 6f)
            }
        }

        if (selectedUpgrade != null) {
            val (col, row) = selectedUpgrade!!
            val (currentLvl, costs) = UpgradeManager.getBranchInfo(col)
            val detail = upgradeDetails[Pair(col, row)] ?: Pair("Upgrade", "Increases effectiveness.")
            val cost = costs[row]
            val statusText = when {
                row < currentLvl -> "[ ALREADY PURCHASED ]"
                row == currentLvl && UpgradeManager.canUpgrade(col) -> "[ TAP AGAIN OR CLICK BUY ]"
                row == currentLvl -> "[ NOT ENOUGH STARS (Need $cost) ]"
                else -> "[ LOCKED - UNLOCK PREVIOUS FIRST ]"
            }

            font.color = Color.GOLD
            font.draw(batch, "${detail.first} (${branchNames[col]} Tier ${row + 1})", panelX + 170f, panelY + 96f)

            smallFont.color = Color.WHITE
            smallFont.draw(batch, detail.second, panelX + 170f, panelY + 70f)

            smallFont.color = when {
                row < currentLvl -> Color(0.4f, 0.9f, 0.4f, 1f)
                row == currentLvl && UpgradeManager.canUpgrade(col) -> Color.YELLOW
                else -> Color(0.85f, 0.35f, 0.35f, 1f)
            }
            smallFont.draw(batch, "Cost: $cost Stars  |  $statusText", panelX + 170f, panelY + 42f)

            if (row == currentLvl && UpgradeManager.canUpgrade(col)) {
                font.color = Color.BLACK
                font.draw(batch, "BUY", panelX + panelW - 105f, panelY + 95f)
            }
        } else {
            smallFont.color = Color.LIGHT_GRAY
            smallFont.draw(batch, "TAP ANY UPGRADE ICON TO VIEW ITS DETAILS AND COST.", panelX + 220f, panelY + 68f)
        }

        batch.end()
    }

    private fun drawStarVector(sr: ShapeRenderer, centerX: Float, centerY: Float, outerRadius: Float, isFilled: Boolean) {
        val innerRadius = outerRadius * 0.45f
        val points = mutableListOf<Vector2>()
        for (i in 0 until 10) {
            val angle = Math.toRadians((90.0 + i * 36.0))
            val r = if (i % 2 == 0) outerRadius else innerRadius
            val px = centerX + (r * Math.cos(angle)).toFloat()
            val py = centerY + (r * Math.sin(angle)).toFloat()
            points.add(Vector2(px, py))
        }

        sr.begin(ShapeRenderer.ShapeType.Filled)
        sr.color = Color(0.10f, 0.08f, 0.06f, 0.8f)
        for (i in 0 until 10) {
            val next = (i + 1) % 10
            sr.triangle(centerX, centerY - 2f, points[i].x, points[i].y - 2f, points[next].x, points[next].y - 2f)
        }

        val bodyColor = if (isFilled) Color(0.98f, 0.82f, 0.15f, 1f) else Color(0.38f, 0.38f, 0.42f, 0.9f)
        sr.color = bodyColor
        for (i in 0 until 10) {
            val next = (i + 1) % 10
            sr.triangle(centerX, centerY, points[i].x, points[i].y, points[next].x, points[next].y)
        }

        if (isFilled) {
            sr.color = Color(1f, 0.95f, 0.65f, 0.75f)
            for (i in listOf(0, 8)) {
                val next = (i + 1) % 10
                sr.triangle(centerX, centerY, points[i].x, points[i].y, points[next].x, points[next].y)
            }
        }
        sr.end()

        sr.begin(ShapeRenderer.ShapeType.Line)
        sr.color = if (isFilled) Color(0.85f, 0.60f, 0.10f, 1f) else Color(0.25f, 0.25f, 0.28f, 1f)
        for (i in 0 until 10) {
            val next = (i + 1) % 10
            sr.line(points[i].x, points[i].y, points[next].x, points[next].y)
        }
        sr.end()
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun dispose() {
        shapeRenderer.dispose()
        batch.dispose()
        titleFont.dispose()
        font.dispose()
        smallFont.dispose()
    }
}
