package com.game.td.renderers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Align
import com.game.td.Config
import com.game.td.ecs.components.GameDifficulty
import com.game.td.ecs.components.TowerType
import com.game.td.managers.GameMode
import com.game.td.managers.SaveManager
import com.game.td.managers.SoundManager
import com.game.td.map.LevelData

object MissionPopupRenderer {

    private val OUTLINE = Color(0.12f, 0.09f, 0.07f, 1f)

    private const val PANEL_W = 720f
    private const val PANEL_H = 490f

    // XỬ LÝ SỰ KIỆN CHẠM TRONG BẢNG CHI TIẾT MÀN CHƠI
    fun handleTouch(
        x: Float,
        y: Float,
        selectedLevelNumber: Int,
        currentDifficulty: GameDifficulty,
        currentGameMode: GameMode,
        onUpdateDifficulty: (GameDifficulty) -> Unit,
        onUpdateGameMode: (GameMode) -> Unit,
        onClosePopup: () -> Unit,
        onStartBattle: () -> Unit
    ): Boolean {
        val cX = Config.WORLD_WIDTH / 2f
        val cY = Config.WORLD_HEIGHT / 2f
        val panelX = cX - PANEL_W / 2f
        val panelY = cY - PANEL_H / 2f

        // 1. Nút X đóng bảng ở góc trên phải
        val closeX = panelX + PANEL_W - 36f
        val closeY = panelY + PANEL_H - 36f
        if (Vector2(closeX, closeY).dst(x, y) <= 26f) {
            SoundManager.playClick()
            onClosePopup()
            return true
        }

        // 2. Bấm vào hộp DIFFICULTY để đổi độ khó (EASY / NORMAL / HARD)
        val diffX = panelX + 38f
        val diffY = panelY + 106f
        val diffW = 210f
        val diffH = 34f
        if (x in diffX..(diffX + diffW) && y in diffY..(diffY + diffH)) {
            SoundManager.playClick()
            val nextDiff = when (currentDifficulty) {
                GameDifficulty.CASUAL -> GameDifficulty.NORMAL
                GameDifficulty.NORMAL -> GameDifficulty.VETERAN
                GameDifficulty.VETERAN -> GameDifficulty.CASUAL
            }
            onUpdateDifficulty(nextDiff)
            return true
        }

        // 3. Bấm chọn 1 trong 3 chế độ GAME MODE (CAMPAIGN / HEROIC / IRON)
        val levelStars = SaveManager.getLevelStars(selectedLevelNumber)
        val isChallengeUnlocked = levelStars >= 3 || selectedLevelNumber == 0
        val modeY = panelY + 46f
        val campBtnX = panelX + 68f
        val heroicBtnX = panelX + 142f
        val ironBtnX = panelX + 216f

        if (Math.abs(x - campBtnX) <= 30f && Math.abs(y - modeY) <= 30f) {
            SoundManager.playTowerPreview()
            onUpdateGameMode(GameMode.CAMPAIGN)
            return true
        }

        if (Math.abs(x - heroicBtnX) <= 30f && Math.abs(y - modeY) <= 30f) {
            if (isChallengeUnlocked) {
                SoundManager.playTowerPreview()
                onUpdateGameMode(GameMode.HEROIC)
            } else {
                SoundManager.playClick()
            }
            return true
        }

        if (Math.abs(x - ironBtnX) <= 30f && Math.abs(y - modeY) <= 30f) {
            if (isChallengeUnlocked) {
                SoundManager.playTowerPreview()
                onUpdateGameMode(GameMode.IRON)
            } else {
                SoundManager.playClick()
            }
            return true
        }

        // 4. Bấm nút TO BATTLE ở góc dưới phải
        val battleBtnX = panelX + PANEL_W - 205f
        val battleBtnY = panelY + 20f
        val battleBtnW = 168f
        val battleBtnH = 62f
        if (x in battleBtnX..(battleBtnX + battleBtnW) && y in battleBtnY..(battleBtnY + battleBtnH)) {
            SoundManager.playBattleStart()
            onStartBattle()
            return true
        }

        return true
    }

    // VẼ TOÀN BỘ BẢNG CHI TIẾT MÀN CHƠI
    fun render(
        sr: ShapeRenderer,
        batch: SpriteBatch,
        titleFont: BitmapFont,
        font: BitmapFont,
        smallFont: BitmapFont,
        selectedLevelData: LevelData?,
        selectedLevelName: String,
        selectedLevelNumber: Int,
        currentDifficulty: GameDifficulty,
        currentGameMode: GameMode
    ) {
        val cX = Config.WORLD_WIDTH / 2f
        val cY = Config.WORLD_HEIGHT / 2f
        val panelX = cX - PANEL_W / 2f
        val panelY = cY - PANEL_H / 2f

        val mapBoxX = panelX + 38f
        val mapBoxY = panelY + 175f
        val mapBoxW = 210f
        val mapBoxH = 195f

        val diffX = panelX + 38f
        val diffY = panelY + 106f
        val diffW = 210f
        val diffH = 34f

        val modeY = panelY + 46f
        val campBtnX = panelX + 68f
        val heroicBtnX = panelX + 142f
        val ironBtnX = panelX + 216f

        val battleBtnX = panelX + PANEL_W - 205f
        val battleBtnY = panelY + 20f
        val battleBtnW = 168f
        val battleBtnH = 62f

        val rulesBoxX = panelX + 275f
        val rulesBoxY = panelY + 125f
        val rulesBoxW = 408f
        val rulesBoxH = 135f

        val levelStars = SaveManager.getLevelStars(selectedLevelNumber)
        val isHeroicDone = SaveManager.isHeroicCompleted(selectedLevelNumber)
        val isIronDone = SaveManager.isIronCompleted(selectedLevelNumber)
        val isChallengeUnlocked = levelStars >= 3 || selectedLevelNumber == 0

        // Lấy độ khó cao nhất mà người chơi đã hoàn thành ở chế độ đang xem (EASY / NORMAL / HARD)
        val completedDifficultyLabel = SaveManager.getCompletedDifficultyLabel(selectedLevelNumber, currentGameMode)

        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

        // 1. VẼ KHUNG GỖ BỌC THÉP, BĂNG RÔN VÀ BẢN ĐỒ THU NHỎ
        sr.begin(ShapeRenderer.ShapeType.Filled)
        sr.color = Color(0f, 0f, 0f, 0.55f)
        sr.rect(0f, 0f, Config.WORLD_WIDTH, Config.WORLD_HEIGHT)

        drawWoodenBoardWithIronTrim(sr, panelX, panelY, PANEL_W, PANEL_H)

        // Vẽ huy hiệu Heroic & Iron nhỏ ở hàng tiến độ góc trên trái (cạnh 3 ngôi sao)
        val badgeRowY = mapBoxY + mapBoxH + 24f
        if (isHeroicDone) {
            MainMenuRenderer.drawHeroicEmblem(sr, mapBoxX + 135f, badgeRowY + 1f, 0.85f)
        } else {
            drawSilhouetteEmblem(sr, mapBoxX + 135f, badgeRowY, isHeroic = true)
        }
        if (isIronDone) {
            MainMenuRenderer.drawIronGauntletEmblem(sr, mapBoxX + 174f, badgeRowY, 0.85f)
        } else {
            drawSilhouetteEmblem(sr, mapBoxX + 174f, badgeRowY, isHeroic = false)
        }

        // Vẽ bản đồ thu nhỏ (Map Thumbnail)
        drawRichMapPreview(sr, selectedLevelData, selectedLevelNumber, mapBoxX, mapBoxY, mapBoxW, mapBoxH)

        // Vẽ hộp chọn DIFFICULTY kiểu Dropdown
        sr.color = OUTLINE
        sr.rect(diffX - 2f, diffY - 2f, diffW + 4f, diffH + 4f)
        sr.color = Color(0.20f, 0.15f, 0.11f, 1f)
        sr.rect(diffX, diffY, diffW, diffH)
        sr.color = Color(0.32f, 0.25f, 0.18f, 1f)
        sr.rect(diffX + diffW - 32f, diffY + 2f, 30f, diffH - 4f)
        sr.color = Color(0.88f, 0.80f, 0.58f, 1f)
        sr.triangle(
            diffX + diffW - 23f, diffY + 20f,
            diffX + diffW - 11f, diffY + 20f,
            diffX + diffW - 17f, diffY + 12f
        )

        // Vẽ 3 nút chọn GAME MODE
        drawModeOptionButton(sr, campBtnX, modeY, currentGameMode == GameMode.CAMPAIGN, isUnlocked = true, mode = GameMode.CAMPAIGN)
        drawModeOptionButton(sr, heroicBtnX, modeY, currentGameMode == GameMode.HEROIC, isUnlocked = isChallengeUnlocked, mode = GameMode.HEROIC)
        drawModeOptionButton(sr, ironBtnX, modeY, currentGameMode == GameMode.IRON, isUnlocked = isChallengeUnlocked, mode = GameMode.IRON)

        // Nếu đang ở chế độ HEROIC hoặc IRON -> Vẽ khung CHALLENGE RULES
        if (currentGameMode != GameMode.CAMPAIGN) {
            sr.color = Color(0.36f, 0.28f, 0.20f, 1f)
            sr.rect(rulesBoxX - 2f, rulesBoxY - 2f, rulesBoxW + 4f, rulesBoxH + 4f)
            sr.color = Color(0.22f, 0.17f, 0.12f, 1f)
            sr.rect(rulesBoxX, rulesBoxY, rulesBoxW, rulesBoxH)

            val iconY = rulesBoxY + 62f
            if (currentGameMode == GameMode.HEROIC) {
                drawRuleSkullIcon(sr, rulesBoxX + 95f, iconY)
                drawRuleHeartIcon(sr, rulesBoxX + 205f, iconY)
                drawRuleHammerIcon(sr, rulesBoxX + 315f, iconY)
            } else {
                drawRuleSkullIcon(sr, rulesBoxX + 58f, iconY)
                drawRuleHeartIcon(sr, rulesBoxX + 148f, iconY)
                drawRuleHammerIcon(sr, rulesBoxX + 245f, iconY)
                drawRuleTowerSpotIcon(sr, rulesBoxX + 345f, iconY)
            }
        }

        // Vẽ nút TO BATTLE kèm biểu tượng của chế độ đang chọn
        drawToBattleButton(sr, battleBtnX, battleBtnY, battleBtnW, battleBtnH, currentGameMode)

        // Vẽ dấu tích xanh lá (✔) nếu chế độ đang xem đã hoàn thành
        if (completedDifficultyLabel != null) {
            val checkX = panelX + PANEL_W - 46f
            val checkY = mapBoxY + mapBoxH + 17f
            sr.color = OUTLINE
            sr.rectLine(checkX - 7f, checkY + 1f, checkX - 2f, checkY - 5f, 5.5f)
            sr.rectLine(checkX - 2f, checkY - 5f, checkX + 8f, checkY + 7f, 5.5f)
            sr.color = Color(0.45f, 0.92f, 0.20f, 1f)
            sr.rectLine(checkX - 6f, checkY + 1f, checkX - 2f, checkY - 4f, 3.2f)
            sr.rectLine(checkX - 2f, checkY - 4f, checkX + 7f, checkY + 6f, 3.2f)
        }
        sr.end()

        // 2. VẼ 3 NGÔI SAO CAMPAIGN Ở GÓC TRÊN TRÁI
        for (s in 0 until 3) {
            val starX = mapBoxX + 22f + s * 34f
            GameUIRenderer.drawStar(sr, starX, badgeRowY, 12f, s < levelStars)
        }

        // 3. VẼ TOÀN BỘ CHỮ, CỐT TRUYỆN & LUẬT CHƠI (CHALLENGE RULES)
        batch.begin()
        val oldTitleScaleX = titleFont.data.scaleX
        val oldTitleScaleY = titleFont.data.scaleY
        titleFont.data.setScale(1.85f)

        // CĂN GIỮA TUYỆT ĐỐI TÊN MÀN CHƠI VÀO TÂM BĂNG RÔN VÀNG
        val ribbonBoxX = cX - 168f
        val ribbonBoxY = panelY + PANEL_H - 20f
        val ribbonBoxW = 336f
        val ribbonBoxH = 38f
        GameUIRenderer.drawBadgeCenteredText(
            batch, titleFont, selectedLevelName,
            ribbonBoxX, ribbonBoxY, ribbonBoxW, ribbonBoxH,
            Color(0.26f, 0.16f, 0.08f, 1f)
        )
        titleFont.data.setScale(oldTitleScaleX, oldTitleScaleY)

        // Tiêu đề chế độ (CAMPAIGN / HEROIC CHALLENGE / IRON CHALLENGE)
        val rightColX = panelX + 275f
        val headerTextY = mapBoxY + mapBoxH + 26f
        val modeHeaderTitle = when (currentGameMode) {
            GameMode.CAMPAIGN -> "CAMPAIGN"
            GameMode.HEROIC -> "HEROIC CHALLENGE"
            GameMode.IRON -> "IRON CHALLENGE"
        }
        GameUIRenderer.drawOutlinedText(batch, font, modeHeaderTitle, rightColX, headerTextY, Color(0.98f, 0.88f, 0.52f, 1f))

        // Hiển thị độ khó cao nhất đã hoàn thành (COMPLETED EASY / COMPLETED NORMAL / COMPLETED HARD)
        val selectedDiffLabel = when (currentDifficulty) {
            GameDifficulty.CASUAL -> "EASY"
            GameDifficulty.NORMAL -> "NORMAL"
            GameDifficulty.VETERAN -> "HARD"
        }
        val oldSmallScaleX = smallFont.data.scaleX
        val oldSmallScaleY = smallFont.data.scaleY
        smallFont.data.setScale(0.92f)

        if (completedDifficultyLabel != null) {
            val statusText = "COMPLETED $completedDifficultyLabel"
            val statusBoxW = 200f
            val statusRightX = panelX + PANEL_W - 60f - statusBoxW
            font.color = Color.WHITE
            smallFont.color = Color(0f, 0f, 0f, 0.88f)
            smallFont.draw(batch, statusText, statusRightX + 1f, headerTextY - 3f, statusBoxW, Align.right, false)
            smallFont.color = Color(0.52f, 0.92f, 0.25f, 1f)
            smallFont.draw(batch, statusText, statusRightX, headerTextY - 2f, statusBoxW, Align.right, false)
        }

        // Nhãn DIFFICULTY & GAME MODE bên cột trái
        GameUIRenderer.drawShadowedText(batch, smallFont, "DIFFICULTY", diffX, diffY + diffH + 18f, Color(0.92f, 0.86f, 0.72f, 1f))
        GameUIRenderer.drawOutlinedText(batch, smallFont, selectedDiffLabel, diffX + 12f, diffY + 23f, Color.WHITE)
        GameUIRenderer.drawShadowedText(batch, smallFont, "GAME MODE", diffX, modeY + 45f, Color(0.92f, 0.86f, 0.72f, 1f))

        // Nội dung văn bản và CHALLENGE RULES bên cột phải
        when (currentGameMode) {
            GameMode.CAMPAIGN -> {
                smallFont.data.setScale(0.88f)
                val storyParagraphs = getCampaignStoryParagraphs(selectedLevelNumber)
                var textY = mapBoxY + mapBoxH - 8f
                for (paragraph in storyParagraphs) {
                    val lines = wrapTextLines(paragraph, 43)
                    for (line in lines) {
                        GameUIRenderer.drawShadowedText(batch, smallFont, line.uppercase(), rightColX, textY, Color(0.92f, 0.88f, 0.78f, 1f))
                        textY -= 17.5f
                    }
                    textY -= 10f
                }
                GameUIRenderer.drawShadowedText(batch, smallFont, "GOOD LUCK, GENERAL!", rightColX, textY - 2f, Color(0.98f, 0.90f, 0.65f, 1f))
            }

            GameMode.HEROIC -> {
                smallFont.data.setScale(0.90f)
                val heroicDesc = "Test your tactical skills against an elite enemy force in this challenge meant for the most heroic defenders!"
                val lines = wrapTextLines(heroicDesc, 42)
                var textY = mapBoxY + mapBoxH - 10f
                for (line in lines) {
                    GameUIRenderer.drawShadowedText(batch, smallFont, line.uppercase(), rightColX, textY, Color(0.92f, 0.88f, 0.78f, 1f))
                    textY -= 18f
                }

                GameUIRenderer.drawShadowedText(batch, smallFont, "CHALLENGE RULES", rulesBoxX + 14f, rulesBoxY + rulesBoxH - 14f, Color(0.95f, 0.85f, 0.55f, 1f))

                smallFont.data.setScale(0.82f)
                val maxCap = getChallengeMaxUpgradeLevel(selectedLevelNumber)
                val (capLine1, capLine2) = if (maxCap < 5) Pair("LVL $maxCap", "MAX") else Pair("NO", "LIMIT")

                drawCenteredTwoLineRule(batch, smallFont, "6 ELITE", "WAVES", rulesBoxX + 95f, rulesBoxY + 32f, Color(0.92f, 0.88f, 0.78f, 1f))
                drawCenteredTwoLineRule(batch, smallFont, "1 LIFE", "TOTAL", rulesBoxX + 205f, rulesBoxY + 32f, Color(0.92f, 0.88f, 0.78f, 1f))
                drawCenteredTwoLineRule(batch, smallFont, capLine1, capLine2, rulesBoxX + 315f, rulesBoxY + 32f, Color(0.95f, 0.35f, 0.25f, 1f))
            }

            GameMode.IRON -> {
                smallFont.data.setScale(0.90f)
                val ironDesc = "A test for the ultimate defender, the iron challenge will take your tactical skills to the limit."
                val lines = wrapTextLines(ironDesc, 42)
                var textY = mapBoxY + mapBoxH - 10f
                for (line in lines) {
                    GameUIRenderer.drawShadowedText(batch, smallFont, line.uppercase(), rightColX, textY, Color(0.92f, 0.88f, 0.78f, 1f))
                    textY -= 18f
                }

                GameUIRenderer.drawShadowedText(batch, smallFont, "CHALLENGE RULES", rulesBoxX + 14f, rulesBoxY + rulesBoxH - 14f, Color(0.95f, 0.85f, 0.55f, 1f))

                smallFont.data.setScale(0.80f)
                val maxCap = getChallengeMaxUpgradeLevel(selectedLevelNumber)
                val (capLine1, capLine2) = if (maxCap < 5) Pair("LVL $maxCap", "MAX") else Pair("NO", "LIMIT")
                val (towerRule1, towerRule2) = getIronTowerRestrictionText(selectedLevelNumber)

                drawCenteredTwoLineRule(batch, smallFont, "1 SUPER", "WAVE", rulesBoxX + 58f, rulesBoxY + 32f, Color(0.92f, 0.88f, 0.78f, 1f))
                drawCenteredTwoLineRule(batch, smallFont, "1 LIFE", "TOTAL", rulesBoxX + 148f, rulesBoxY + 32f, Color(0.92f, 0.88f, 0.78f, 1f))
                drawCenteredTwoLineRule(batch, smallFont, capLine1, capLine2, rulesBoxX + 245f, rulesBoxY + 32f, Color(0.95f, 0.35f, 0.25f, 1f))
                drawCenteredTwoLineRule(batch, smallFont, towerRule1, towerRule2, rulesBoxX + 345f, rulesBoxY + 32f, Color(0.52f, 0.94f, 0.22f, 1f))
            }
        }

        smallFont.data.setScale(oldSmallScaleX, oldSmallScaleY)

        // Chữ TO BATTLE trong nút góc dưới phải
        GameUIRenderer.drawBadgeCenteredText(batch, smallFont, "TO", battleBtnX + 52f, battleBtnY + 28f, battleBtnW - 56f, 28f, Color.WHITE)
        GameUIRenderer.drawBadgeCenteredText(batch, font, "BATTLE", battleBtnX + 52f, battleBtnY + 4f, battleBtnW - 56f, 30f, Color.WHITE)
        batch.end()
    }

    private fun getChallengeMaxUpgradeLevel(levelNumber: Int): Int = when (levelNumber) {
        1 -> 1
        2, 3, 4 -> 2
        5, 6 -> 3
        7, 8, 9, 10 -> 4
        else -> 5
    }

    private fun getIronTowerRestrictionText(levelNumber: Int): Pair<String, String> {
        val allowed = when (levelNumber) {
            1 -> listOf(TowerType.BARRACKS, TowerType.ARTILLERY)
            2 -> listOf(TowerType.ARCHER, TowerType.MAGE, TowerType.ARTILLERY)
            3 -> listOf(TowerType.ARCHER, TowerType.BARRACKS, TowerType.MAGE)
            4 -> listOf(TowerType.ARCHER, TowerType.BARRACKS)
            5 -> listOf(TowerType.ARCHER, TowerType.MAGE, TowerType.ARTILLERY)
            6 -> listOf(TowerType.BARRACKS)
            7 -> listOf(TowerType.ARCHER, TowerType.MAGE)
            8 -> listOf(TowerType.BARRACKS, TowerType.ARTILLERY)
            9 -> listOf(TowerType.MAGE)
            10 -> listOf(TowerType.ARCHER, TowerType.MAGE, TowerType.ARTILLERY)
            11 -> listOf(TowerType.MAGE, TowerType.BARRACKS)
            12 -> listOf(TowerType.ARCHER, TowerType.MAGE, TowerType.BARRACKS)
            else -> listOf(TowerType.ARCHER, TowerType.BARRACKS, TowerType.MAGE, TowerType.ARTILLERY)
        }

        val allFour = listOf(TowerType.ARCHER, TowerType.BARRACKS, TowerType.MAGE, TowerType.ARTILLERY)
        val banned = allFour.filter { it !in allowed }

        return when (banned.size) {
            1 -> Pair("NO ${banned[0].name}", "")
            2 -> Pair("NO ${banned[0].name}", "NO ${banned[1].name}")
            3 -> Pair("ONLY", allowed[0].name)
            else -> Pair("ALL TOWERS", "ALLOWED")
        }
    }

    private fun getCampaignStoryParagraphs(levelNumber: Int): List<String> = when (levelNumber) {
        1 -> listOf(
            "Greetings, noble and heroic warrior! As a General of His Majesty's forces, King Denas requires your services.",
            "The inhabitants of the city of Southport have reported attacks by highwaymen and other roaming outlaws. You must take a battalion to Southport to bolster the city's defenses."
        )
        2 -> listOf(
            "Good job strengthening the perimeter at Southport! Now you must march toward the Outskirts, where brigands and goblin raiders have been pillaging the farmlands.",
            "Establish a defensive line and protect the local farmers from the incoming goblin hordes!"
        )
        3 -> listOf(
            "While our men celebrate your victory over the goblin and orcish troops, a garrison messenger brings bad news.",
            "The town of Pagras is under siege by an overwhelming force. This is clearly an open attack on the Kingdom: war is upon us!"
        )
        4 -> listOf(
            "Enemy troops are marching from the east toward the capital through the Twin Rivers Pass! King Denas commands you to hold the pass at all costs.",
            "If the enemy crosses the twin bridges, the road to Linirea will lie wide open!"
        )
        5 -> listOf(
            "Bad news, General! The pass at Twin Rivers has fallen and the enemy vanguard is already marching through Silveroak Forest.",
            "Gather the Sylvan Elves to aid our cause and ambush the invaders beneath the ancient canopy!"
        )
        6 -> listOf(
            "After several days of marching, we have arrived at the capital city of Linirea! The time has come to make our stand and defend the southern walls.",
            "In addition to your experienced units, the King adds two units of Imperial Guards to your forces. Crush the Juggernaut!"
        )
        7 -> listOf(
            "The capital is safe, General! Now the King has ordered us to march through the mountains to strike at Vez'nan's domain.",
            "To cross the mountains we must pass through the Coldstep Mines, where savage Trolls and dark forces lie in wait."
        )
        8 -> listOf(
            "There is no place colder than the Icewind Pass, yet we must advance! Our men have been besieged by Trolls for days.",
            "Deploy your heavy artillery and clear a path through the frozen peaks before the Yetis overwhelm us!"
        )
        9 -> listOf(
            "Reaching the Stormcloud Temple has been the best thing to happen to our men in days!",
            "The Stormcloud sorcerers will now channel the power of the Sunray Tower to help us open a direct path to Valardul!"
        )
        10 -> listOf(
            "We have arrived at the dreaded Valardul Waste. This barren, cursed land is where the spirits of the fallen find no rest.",
            "Beware, General: any warrior who perishes upon this soil may rise again to swell the ranks of the undead!"
        )
        11 -> listOf(
            "Crossing the Forsaken Valley will be no easy task! We are nearing the end of our journey, and although our men are tired, they are filled with noble purpose.",
            "However, beware... this is a land plagued by fiery magma Titans and winged demons!"
        )
        12 -> listOf(
            "Our troops have finally reached the Dark Tower! Vez'nan's lair stands before us, and the dark wizard himself awaits atop his balcony.",
            "It is time to give it everything you've got, General... the fate of the Kingdom rests in your hands!"
        )
        else -> listOf(
            "Welcome to the tactical Sandbox testing ground!",
            "All tower specializations and abundant gold are available to test your strategies against endless waves."
        )
    }

    private fun drawWoodenBoardWithIronTrim(sr: ShapeRenderer, x: Float, y: Float, w: Float, h: Float) {
        sr.color = OUTLINE
        sr.rect(x - 6f, y - 6f, w + 12f, h + 12f)
        sr.color = Color(0.58f, 0.57f, 0.53f, 1f)
        sr.rect(x - 3f, y - 3f, w + 6f, h + 6f)
        sr.color = OUTLINE
        sr.rect(x + 3f, y + 3f, w - 6f, h - 6f)

        sr.color = Color(0.28f, 0.22f, 0.16f, 1f)
        sr.rect(x + 6f, y + 6f, w - 12f, h - 12f)

        sr.color = Color(0.23f, 0.18f, 0.13f, 1f)
        for (gy in (y + 38f).toInt() until (y + h - 25f).toInt() step 44) {
            sr.rect(x + 16f, gy.toFloat(), w - 32f, 3.5f)
        }

        val corners = listOf(
            Vector2(x - 4f, y + h - 20f),
            Vector2(x + w - 20f, y + h - 20f),
            Vector2(x - 4f, y - 4f),
            Vector2(x + w - 20f, y - 4f)
        )
        for (c in corners) {
            sr.color = OUTLINE
            sr.rect(c.x - 1.5f, c.y - 1.5f, 27f, 27f)
            sr.color = Color(0.65f, 0.67f, 0.70f, 1f)
            sr.rect(c.x, c.y, 24f, 24f)
            sr.color = OUTLINE
            sr.circle(c.x + 6f, c.y + 6f, 2f)
            sr.circle(c.x + 18f, c.y + 18f, 2f)
        }

        val cx = x + w / 2f
        val topY = y + h
        sr.color = OUTLINE
        sr.rect(cx - 224f, topY - 40f, 64f, 42f)
        sr.rect(cx + 160f, topY - 40f, 64f, 42f)
        sr.color = Color(0.72f, 0.56f, 0.22f, 1f)
        sr.rect(cx - 220f, topY - 37f, 58f, 36f)
        sr.rect(cx + 162f, topY - 37f, 58f, 36f)

        sr.color = OUTLINE
        sr.rect(cx - 178f, topY - 30f, 356f, 58f)
        sr.color = Color(0.90f, 0.75f, 0.36f, 1f)
        sr.rect(cx - 174f, topY - 26f, 348f, 50f)
        sr.color = Color(0.98f, 0.85f, 0.48f, 1f)
        sr.rect(cx - 168f, topY - 20f, 336f, 38f)

        val closeX = x + w - 36f
        val closeY = y + h - 36f
        sr.color = OUTLINE
        sr.rectLine(closeX - 10f, closeY - 10f, closeX + 10f, closeY + 10f, 9f)
        sr.rectLine(closeX - 10f, closeY + 10f, closeX + 10f, closeY - 10f, 9f)
        sr.color = Color(0.78f, 0.80f, 0.84f, 1f)
        sr.rectLine(closeX - 8f, closeY - 8f, closeX + 8f, closeY + 8f, 5f)
        sr.rectLine(closeX - 8f, closeY + 8f, closeX + 8f, closeY - 8f, 5f)
    }

    private fun drawRichMapPreview(
        sr: ShapeRenderer,
        levelData: LevelData?,
        levelNumber: Int,
        boxX: Float,
        boxY: Float,
        boxW: Float,
        boxH: Float
    ) {
        val isSnow = levelNumber in 7..9
        val isWastes = levelNumber in 10..12

        sr.color = Color(0.18f, 0.14f, 0.10f, 1f)
        sr.rect(boxX - 4f, boxY - 4f, boxW + 8f, boxH + 8f)

        sr.color = when {
            isSnow -> Color(0.84f, 0.90f, 0.96f, 1f)
            isWastes -> Color(0.28f, 0.25f, 0.28f, 1f)
            else -> Color(0.46f, 0.66f, 0.18f, 1f)
        }
        sr.rect(boxX, boxY, boxW, boxH)

        val treeColorDark = when {
            isSnow -> Color(0.58f, 0.68f, 0.78f, 1f)
            isWastes -> Color(0.20f, 0.18f, 0.20f, 1f)
            else -> Color(0.24f, 0.44f, 0.10f, 1f)
        }
        val treeColorLight = when {
            isSnow -> Color(0.72f, 0.82f, 0.90f, 1f)
            isWastes -> Color(0.36f, 0.22f, 0.16f, 1f)
            else -> Color(0.32f, 0.54f, 0.14f, 1f)
        }

        val clusters = listOf(
            Vector2(boxX + 26f, boxY + boxH - 24f),
            Vector2(boxX + 52f, boxY + boxH - 18f),
            Vector2(boxX + boxW - 28f, boxY + boxH - 26f),
            Vector2(boxX + boxW - 55f, boxY + boxH - 20f),
            Vector2(boxX + 28f, boxY + 22f),
            Vector2(boxX + boxW - 32f, boxY + 26f)
        )
        for (c in clusters) {
            sr.color = treeColorDark
            sr.circle(c.x, c.y, 20f)
            sr.color = treeColorLight
            sr.circle(c.x - 2f, c.y + 3f, 14f)
        }

        levelData?.let { level ->
            fun mapToPreview(worldPos: Vector2): Vector2 {
                val px = (boxX + (worldPos.x / Config.WORLD_WIDTH) * boxW).coerceIn(boxX + 6f, boxX + boxW - 6f)
                val py = (boxY + (worldPos.y / Config.WORLD_HEIGHT) * boxH).coerceIn(boxY + 6f, boxY + boxH - 6f)
                return Vector2(px, py)
            }

            sr.color = when {
                isSnow -> Color(0.52f, 0.58f, 0.66f, 1f)
                isWastes -> Color(0.18f, 0.16f, 0.18f, 1f)
                else -> Color(0.60f, 0.50f, 0.28f, 1f)
            }
            for (path in level.allPaths) {
                for (i in 0 until path.size - 1) {
                    sr.rectLine(mapToPreview(path[i]), mapToPreview(path[i + 1]), 15f)
                }
                for (w in path) {
                    val p = mapToPreview(w)
                    sr.circle(p.x, p.y, 7.5f)
                }
            }

            sr.color = when {
                isSnow -> Color(0.70f, 0.76f, 0.84f, 1f)
                isWastes -> Color(0.44f, 0.40f, 0.42f, 1f)
                else -> Color(0.84f, 0.74f, 0.46f, 1f)
            }
            for (path in level.allPaths) {
                for (i in 0 until path.size - 1) {
                    sr.rectLine(mapToPreview(path[i]), mapToPreview(path[i + 1]), 11f)
                }
                for (w in path) {
                    val p = mapToPreview(w)
                    sr.circle(p.x, p.y, 5.5f)
                }
            }

            for (path in level.allPaths) {
                if (path.isNotEmpty()) {
                    val endP = mapToPreview(path.last())
                    sr.color = OUTLINE
                    sr.rect(endP.x - 1f, endP.y, 2f, 10f)
                    sr.color = Color(0.20f, 0.55f, 0.95f, 1f)
                    sr.triangle(endP.x, endP.y + 10f, endP.x, endP.y + 4f, endP.x + 7f, endP.y + 7f)
                }
            }
        }
    }

    private fun drawModeOptionButton(
        sr: ShapeRenderer,
        cx: Float,
        cy: Float,
        isSelected: Boolean,
        isUnlocked: Boolean,
        mode: GameMode
    ) {
        val s = 28f
        sr.color = OUTLINE
        sr.rect(cx - s - 2f, cy - s - 2f, (s + 2f) * 2f, (s + 2f) * 2f)
        sr.color = if (isSelected) Color(1f, 0.88f, 0.12f, 1f) else Color(0.58f, 0.56f, 0.52f, 1f)
        sr.rect(cx - s, cy - s, s * 2f, s * 2f)
        sr.color = Color(0.24f, 0.21f, 0.18f, 1f)
        sr.rect(cx - s + 4f, cy - s + 4f, (s - 4f) * 2f, (s - 4f) * 2f)

        if (!isUnlocked) {
            sr.color = Color(0.65f, 0.68f, 0.72f, 1f)
            sr.circle(cx, cy + 6f, 6.5f)
            sr.color = Color(0.24f, 0.21f, 0.18f, 1f)
            sr.circle(cx, cy + 6f, 4f)
            sr.color = Color(0.82f, 0.68f, 0.25f, 1f)
            sr.rect(cx - 8f, cy - 9f, 16f, 12f)
            sr.color = OUTLINE
            sr.circle(cx, cy - 3f, 2f)
            return
        }

        when (mode) {
            GameMode.CAMPAIGN -> drawBlueCampaignBannerEmblem(sr, cx, cy, 1.0f)
            GameMode.HEROIC -> MainMenuRenderer.drawHeroicEmblem(sr, cx, cy + 2f, 1.05f)
            GameMode.IRON -> MainMenuRenderer.drawIronGauntletEmblem(sr, cx, cy, 1.05f)
        }
    }

    // VẼ CỜ HIỆU CHIẾN DỊCH MÀU XANH DƯƠNG CÂN ĐỐI, THẲNG TRỤC CHUẨN KINGDOM RUSH
    private fun drawBlueCampaignBannerEmblem(sr: ShapeRenderer, cx: Float, cy: Float, scale: Float) {
        val s = scale

        // 1. Trục cán cờ dọc ở chính giữa (nhô lên trên đỉnh và nhô xuống dưới đáy cờ)
        sr.color = OUTLINE
        sr.rect(cx - 2.2f * s, cy - 17f * s, 4.4f * s, 33f * s)
        sr.circle(cx, cy + 15.5f * s, 3.2f * s)
        sr.color = Color(0.68f, 0.44f, 0.18f, 1f)
        sr.rect(cx - 1.2f * s, cy - 15.5f * s, 2.4f * s, 30f * s)
        sr.color = Color(0.96f, 0.78f, 0.20f, 1f)
        sr.circle(cx, cy + 15.5f * s, 2.0f * s)

        // 2. Thanh ngang treo cờ nằm ngang cân đối + 2 đầu tròn vàng
        val barY = cy + 10.5f * s
        sr.color = OUTLINE
        sr.rect(cx - 13f * s, barY - 2f * s, 26f * s, 4.2f * s)
        sr.circle(cx - 13f * s, barY, 2.8f * s)
        sr.circle(cx + 13f * s, barY, 2.8f * s)

        sr.color = Color(0.75f, 0.50f, 0.20f, 1f)
        sr.rect(cx - 12f * s, barY - 1f * s, 24f * s, 2.2f * s)
        sr.color = Color(0.98f, 0.80f, 0.22f, 1f)
        sr.circle(cx - 13f * s, barY, 1.7f * s)
        sr.circle(cx + 13f * s, barY, 1.7f * s)

        // 3. Viền đen của thân lá cờ xanh dương (đuôi cờ nhọn cân xứng)
        sr.color = OUTLINE
        sr.rect(cx - 10.5f * s, cy - 5.5f * s, 21f * s, 15.5f * s)
        sr.triangle(cx - 10.5f * s, cy - 5.5f * s, cx + 10.5f * s, cy - 5.5f * s, cx, cy - 15.5f * s)

        // 4. Nền vải cờ xanh dương đậm & mảng sáng trung tâm
        sr.color = Color(0.18f, 0.50f, 0.86f, 1f)
        sr.rect(cx - 9f * s, cy - 4.8f * s, 18f * s, 13.5f * s)
        sr.triangle(cx - 9f * s, cy - 4.8f * s, cx + 9f * s, cy - 4.8f * s, cx, cy - 13.2f * s)

        sr.color = Color(0.36f, 0.68f, 0.96f, 1f)
        sr.rect(cx - 6.5f * s, cy - 3.5f * s, 13f * s, 10.5f * s)
        sr.triangle(cx - 6.5f * s, cy - 3.5f * s, cx + 6.5f * s, cy - 3.5f * s, cx, cy - 10f * s)

        // Huy hiệu sọc sáng giữa lá cờ
        sr.color = Color(0.68f, 0.88f, 1f, 1f)
        sr.rect(cx - 2f * s, cy - 5f * s, 4f * s, 10f * s)
        sr.triangle(cx - 2f * s, cy - 5f * s, cx + 2f * s, cy - 5f * s, cx, cy - 8f * s)

        // 5. Ba khuyên vải treo cờ nối lên thanh ngang
        sr.color = OUTLINE
        for (lx in listOf(-6.5f, 0f, 6.5f)) {
            sr.rect(cx + (lx - 1.2f) * s, barY - 2.5f * s, 2.4f * s, 4.5f * s)
        }
    }

    private fun drawToBattleButton(
        sr: ShapeRenderer,
        x: Float,
        y: Float,
        w: Float,
        h: Float,
        mode: GameMode
    ) {
        sr.color = OUTLINE
        sr.rect(x - 3f, y - 3f, w + 6f, h + 6f)
        sr.color = Color(0.68f, 0.66f, 0.62f, 1f)
        sr.rect(x, y, w, h)
        sr.color = Color(0.26f, 0.21f, 0.16f, 1f)
        sr.rect(x + 4f, y + 4f, w - 8f, h - 8f)

        val iconCx = x + 32f
        val iconCy = y + h / 2f
        when (mode) {
            GameMode.CAMPAIGN -> drawBlueCampaignBannerEmblem(sr, iconCx, iconCy, 1.30f)
            GameMode.HEROIC -> MainMenuRenderer.drawHeroicEmblem(sr, iconCx, iconCy + 2f, 1.35f)
            GameMode.IRON -> MainMenuRenderer.drawIronGauntletEmblem(sr, iconCx, iconCy, 1.35f)
        }
    }

    private fun drawSilhouetteEmblem(sr: ShapeRenderer, cx: Float, cy: Float, isHeroic: Boolean) {
        sr.color = Color(0.18f, 0.14f, 0.10f, 0.85f)
        if (isHeroic) {
            sr.circle(cx, cy, 9f)
            sr.triangle(cx - 8f, cy, cx + 8f, cy, cx, cy - 10f)
        } else {
            sr.rect(cx - 6f, cy - 9f, 12f, 17f)
        }
    }

    private fun drawRuleSkullIcon(sr: ShapeRenderer, cx: Float, cy: Float) {
        sr.color = OUTLINE
        sr.circle(cx, cy + 4f, 15.5f)
        sr.rect(cx - 9.5f, cy - 11f, 19f, 10f)

        sr.color = Color(0.86f, 0.84f, 0.78f, 1f)
        sr.circle(cx, cy + 4f, 13.5f)
        sr.rect(cx - 7.5f, cy - 9f, 15f, 9f)

        sr.color = OUTLINE
        sr.circle(cx - 5f, cy + 3f, 4.2f)
        sr.circle(cx + 5f, cy + 3f, 4.2f)
        sr.triangle(cx - 2f, cy - 3f, cx + 2f, cy - 3f, cx, cy + 0.5f)
        sr.rect(cx - 4f, cy - 9f, 1.8f, 4.5f)
        sr.rect(cx, cy - 9f, 1.8f, 4.5f)
        sr.rect(cx + 4f, cy - 9f, 1.8f, 4.5f)
    }

    private fun drawRuleHeartIcon(sr: ShapeRenderer, cx: Float, cy: Float) {
        sr.color = OUTLINE
        sr.circle(cx - 7f, cy + 5f, 10.5f)
        sr.circle(cx + 7f, cy + 5f, 10.5f)
        sr.triangle(cx - 16.5f, cy + 2f, cx + 16.5f, cy + 2f, cx, cy - 14f)

        sr.color = Color(0.86f, 0.18f, 0.12f, 1f)
        sr.circle(cx - 7f, cy + 5f, 8.5f)
        sr.circle(cx + 7f, cy + 5f, 8.5f)
        sr.triangle(cx - 14.5f, cy + 2f, cx + 14.5f, cy + 2f, cx, cy - 11.5f)

        sr.color = Color(1f, 0.65f, 0.45f, 1f)
        sr.circle(cx + 6f, cy + 7f, 3.2f)
    }

    private fun drawRuleHammerIcon(sr: ShapeRenderer, cx: Float, cy: Float) {
        sr.color = OUTLINE
        sr.rectLine(cx - 4f, cy - 13f, cx, cy + 8f, 8f)
        sr.color = Color(0.52f, 0.32f, 0.14f, 1f)
        sr.rectLine(cx - 4f, cy - 12f, cx, cy + 8f, 5f)

        sr.color = OUTLINE
        sr.rect(cx - 18f, cy + 2f, 32f, 16f)
        sr.color = Color(0.52f, 0.55f, 0.60f, 1f)
        sr.rect(cx - 16f, cy + 4f, 28f, 12f)
        sr.color = Color(0.72f, 0.75f, 0.80f, 1f)
        sr.rect(cx - 14f, cy + 12f, 24f, 3f)

        sr.color = OUTLINE
        sr.rectLine(cx + 9f, cy - 12f, cx + 14f, cy - 1f, 6f)
        sr.circle(cx + 14f, cy, 5.5f)
        sr.color = Color(0.65f, 0.70f, 0.76f, 1f)
        sr.circle(cx + 14f, cy, 3.8f)
    }

    private fun drawRuleTowerSpotIcon(sr: ShapeRenderer, cx: Float, cy: Float) {
        sr.color = Color(0.32f, 0.52f, 0.14f, 1f)
        sr.ellipse(cx - 26f, cy - 12f, 52f, 20f)
        sr.color = Color(0.76f, 0.66f, 0.38f, 1f)
        sr.ellipse(cx - 21f, cy - 9f, 42f, 15f)

        sr.color = OUTLINE
        sr.rect(cx - 2f, cy - 3f, 4f, 12f)
        sr.rect(cx - 8f, cy + 6f, 16f, 11f)
        sr.color = Color(0.92f, 0.86f, 0.68f, 1f)
        sr.rect(cx - 6.5f, cy + 7.5f, 13f, 8f)
        sr.color = OUTLINE
        sr.rect(cx - 1f, cy + 9.5f, 2f, 4.5f)
    }

    private fun drawCenteredTwoLineRule(
        batch: SpriteBatch,
        font: BitmapFont,
        line1: String,
        line2: String,
        centerX: Float,
        topY: Float,
        color: Color
    ) {
        val boxW = 110f
        val startX = centerX - boxW / 2f
        font.color = Color(0f, 0f, 0f, 0.85f)
        font.draw(batch, line1, startX + 1f, topY - 1f, boxW, Align.center, false)
        font.color = color
        font.draw(batch, line1, startX, topY, boxW, Align.center, false)

        if (line2.isNotEmpty()) {
            font.color = Color(0f, 0f, 0f, 0.85f)
            font.draw(batch, line2, startX + 1f, topY - 15f, boxW, Align.center, false)
            font.color = color
            font.draw(batch, line2, startX, topY - 14f, boxW, Align.center, false)
        }
    }

    private fun wrapTextLines(text: String, maxCharsPerLine: Int): List<String> {
        val words = text.split(" ")
        val lines = mutableListOf<String>()
        var current = ""
        for (w in words) {
            if (current.isEmpty()) current = w
            else if (current.length + 1 + w.length <= maxCharsPerLine) current += " $w"
            else {
                lines.add(current)
                current = w
            }
        }
        if (current.isNotEmpty()) lines.add(current)
        return lines
    }
}
