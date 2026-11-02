package com.game.td.renderers

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.game.td.Config
import com.game.td.ecs.components.*
import com.game.td.managers.CheatManager
import com.game.td.managers.EconomyManager
import com.game.td.managers.GameMode
import com.game.td.managers.WaveManager
import com.game.td.map.Level12TheDarkTower
import com.game.td.map.LevelData
import com.game.td.renderers.GameUIRenderer.drawOutlinedText
import com.game.td.screens.ActiveSpell

data class LockedTowerState(
    var initialTimer: Float = 5.0f,
    var penaltyTimer: Float = 5.0f,
    var isUndispellable: Boolean = false
)

data class JuggernautSummonBomb(
    val startPos: Vector2,
    val targetPos: Vector2,
    val remainingWaypoints: List<Vector2>,
    var elapsed: Float = 0f,
    val duration: Float = 0.85f
)

object PlayScreenRenderer {

    private val OUTLINE = Color(0.12f, 0.09f, 0.07f, 1f)

    fun render(
        shapeRenderer: ShapeRenderer,
        batch: SpriteBatch,
        font: BitmapFont,
        smallFont: BitmapFont,
        engine: Engine,
        currentLevel: LevelData,
        gameMode: GameMode,
        waveManager: WaveManager,
        effectRenderer: EffectRenderer,
        glowingCircles: List<Pair<Vector2, Float>>,
        warningTowersMap: Map<Entity, Float>,
        lockedTowersMap: Map<Entity, LockedTowerState>,
        activeJuggernautBombs: List<JuggernautSummonBomb>,
        selectedSpot: Entity?,
        selectedTower: Entity?,
        selectedEnemy: Entity?,
        selectedSoldier: Entity?,
        previewBuildType: TowerType?,
        previewTowerAction: String?,
        isSettingRallyPoint: Boolean,
        maxRallyRadius: Float,
        buildOptions: List<Pair<TowerType, Vector2>>,
        getBranchSpecs: (TowerType) -> Pair<TowerSpecialization, TowerSpecialization>,
        allowedTowers: List<TowerType>,
        selectedSpell: ActiveSpell,
        rainOfFireCooldown: Float,
        reinforcementCooldown: Float,
        isAimingSunrayTarget: Boolean,
        isGamePaused: Boolean,
        isVictoryReady: Boolean,
        onClearEnemySelection: () -> Unit,
        onClearSoldierSelection: () -> Unit,
        pressedDefeatButton: String? = null
    ) {
        ComicFontRenderer.prepareCrispFont(font, smallFont)

        // 1. VẼ ĐỊA HÌNH BẢN ĐỒ
        MapRenderer.render(shapeRenderer, currentLevel)

        if (currentLevel.levelNumber == 12 && gameMode != GameMode.CAMPAIGN) {
            val bPos = Level12TheDarkTower.balconyPosition
            val oldScaleX = smallFont.data.scaleX
            val oldScaleY = smallFont.data.scaleY
            smallFont.data.setScale(0.82f)
            batch.begin()
            drawOutlinedText(batch, smallFont, "FOR SALE", bPos.x - 24f, bPos.y + 26f, Color.WHITE)
            batch.end()
            smallFont.data.setScale(oldScaleX, oldScaleY)
        }

        if (glowingCircles.isNotEmpty()) {
            Gdx.gl.glEnable(GL20.GL_BLEND)
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
            for (circle in glowingCircles) {
                val cPos = circle.first
                shapeRenderer.color = Color(0.75f, 0.20f, 0.95f, 0.45f)
                shapeRenderer.circle(cPos.x, cPos.y, 40f)
                shapeRenderer.color = Color(1f, 0.85f, 0.20f, 0.65f)
                shapeRenderer.circle(cPos.x, cPos.y, 24f)
            }
            shapeRenderer.end()
        }

        // 2. VẼ VỆT CHÁY TRÊN MẶT ĐẤT, CÁC THỰC THỂ & HIỆU ỨNG CHIẾN ĐẤU
        effectRenderer.renderGroundPatches(shapeRenderer)
        renderEntities(
            shapeRenderer = shapeRenderer,
            engine = engine,
            levelNumber = currentLevel.levelNumber,
            glowingCircles = glowingCircles,
            warningTowersMap = warningTowersMap,
            lockedTowersMap = lockedTowersMap,
            activeJuggernautBombs = activeJuggernautBombs
        )
        effectRenderer.render(shapeRenderer)

        // 3. VẼ NÚT GỌI SỚM WAVE & BÁNH XE XÂY/NÂNG CẤP THÁP
        GameUIRenderer.renderEarlyCallWaveButtons(
            shapeRenderer, batch, font, smallFont, waveManager, currentLevel.allPaths
        )

        val hasAnyTowerBuilt = engine.getEntitiesFor(Family.all(TowerComponent::class.java).get()).size() > 0
        TutorialRenderer.renderMapHints(
            sr = shapeRenderer,
            batch = batch,
            smallFont = smallFont,
            hasAnyTowerBuilt = hasAnyTowerBuilt,
            waveManager = waveManager
        )

        val maxLevel = when (currentLevel.levelNumber) {
            0 -> 4
            1 -> 1
            2, 3 -> 2
            4 -> 3
            5 -> if (waveManager.currentWaveDisplay >= 5 || gameMode != GameMode.CAMPAIGN) 4 else 3
            else -> 4
        }

        GameUIRenderer.renderWheelMenu(
            shapeRenderer = shapeRenderer,
            batch = batch,
            smallFont = smallFont,
            selectedSpot = selectedSpot,
            selectedTower = selectedTower,
            previewBuildType = previewBuildType,
            previewTowerAction = previewTowerAction,
            isSettingRallyPoint = isSettingRallyPoint,
            maxRallyRadius = maxRallyRadius,
            buildOptions = buildOptions,
            getBranchSpecs = getBranchSpecs,
            maxLevelAllowed = maxLevel,
            allowedTowers = allowedTowers,
            currentLevelNumber = currentLevel.levelNumber,
            isPreGame = (waveManager.currentWaveIndex == -1)
        )

        // 4. VẼ KỸ NĂNG SPELL, THANH THÔNG TIN DƯỚI CÙNG & HUD TRÊN CÙNG
        GameUIRenderer.renderSpellUI(
            shapeRenderer, batch, font, smallFont,
            selectedSpell, rainOfFireCooldown, reinforcementCooldown
        )

        GameUIRenderer.renderBottomInfoBar(
            shapeRenderer = shapeRenderer,
            batch = batch,
            font = font,
            smallFont = smallFont,
            selectedEnemy = selectedEnemy,
            selectedSoldier = selectedSoldier,
            selectedTower = selectedTower,
            onClearEnemySelection = onClearEnemySelection,
            onClearSoldierSelection = onClearSoldierSelection
        )

        GameUIRenderer.renderHUD(
            shapeRenderer, batch, font, waveManager, isSettingRallyPoint, isGamePaused
        )

        if (isAimingSunrayTarget) {
            batch.begin()
            drawOutlinedText(
                batch, font,
                "SELECT AN ENEMY TO FIRE SOLAR BEAM!",
                Config.WORLD_WIDTH / 2f - 190f, 65f, Color.YELLOW
            )
            batch.end()
        }

        // BẬT LẠI GL_BLEND SAU KHI RENDERHUD KẾT THÚC BATCH ĐỂ KHÔNG BỊ ĐEN MÀN HÌNH KHI PAUSE
        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

        // 5. VẼ BẢNG THUA CUỘC (DEFEAT), BẢNG TẠM DỪNG (OPTIONS), MENU CHEAT & BẢNG CHIẾN THẮNG (VICTORY)
        if (EconomyManager.isGameOver) {
            renderDefeatDialog(shapeRenderer, pressedDefeatButton)
        } else if (isGamePaused) {
            renderPauseDialog(shapeRenderer)
        } else if (CheatManager.isCheatEnabled) {
            renderCheatFloatingButton(shapeRenderer)
            if (CheatManager.isCheatMenuOpen) {
                renderCheatDialog(shapeRenderer)
            }
        }

        if (isVictoryReady && !EconomyManager.isGameOver) {
            val earnedStars = if (gameMode == GameMode.CAMPAIGN) {
                when {
                    EconomyManager.lives >= 18 -> 3
                    EconomyManager.lives >= 6 -> 2
                    EconomyManager.lives >= 1 -> 1
                    else -> 0
                }
            } else {
                1
            }
            GameUIRenderer.renderVictoryDialog(
                shapeRenderer = shapeRenderer,
                batch = batch,
                font = font,
                smallFont = smallFont,
                earnedStars = earnedStars,
                isChallengeMode = (gameMode != GameMode.CAMPAIGN),
                gameMode = gameMode
            )
        }

        // 6. VẼ BẢNG HƯỚNG DẪN CÁCH CHƠI (INSTRUCTIONS), NÂNG CẤP THÁP, QUÁI MỚI & HINT
        TutorialRenderer.renderPopups(
            sr = shapeRenderer,
            batch = batch,
            font = font,
            smallFont = smallFont
        )

        // 7. VẼ BANNER THÔNG BÁO THÀNH TỰU (ACHIEVEMENT TOAST) TRÊN ĐỈNH MÀN HÌNH
        AchievementRenderer.renderInGameToast(
            sr = shapeRenderer,
            batch = batch,
            font = font,
            smallFont = smallFont
        )
    }

    // =========================================================================
    // HỘP THOẠI TẠM DỪNG "OPTIONS" (ĐỒNG BỘ BẢNG GỖ TREO XÍCH & BĂNG RÔN VÀNG)
    // =========================================================================
    private fun renderPauseDialog(sr: ShapeRenderer) {
        val cX = Config.WORLD_WIDTH / 2f
        val cY = Config.WORLD_HEIGHT / 2f
        val boardW = 370f
        val boardH = 290f
        val boardX = cX - boardW / 2f
        val boardY = cY - 155f

        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

        sr.begin(ShapeRenderer.ShapeType.Filled)
        drawHangingWoodenBoardWithRibbon(
            sr = sr,
            boardX = boardX,
            boardY = boardY,
            boardW = boardW,
            boardH = boardH,
            ribbonTitle = "OPTIONS",
            ribbonWidth = 250f,
            titleCharHeight = 28f
        )

        val btnW = 240f
        val btnH = 44f
        val btnX = cX - 120f

        // 1. Nút RESUME (y = cY + 50f..cY + 94f)
        MainMenuRenderer.drawIronWoodPlaqueButton(sr, btnX, cY + 50f, btnW, btnH)
        ComicFontRenderer.drawPlaqueText(sr, "RESUME", cX, cY + 72f, 17f, Color(0.98f, 0.94f, 0.82f, 1f))

        // 2. Nút RESTART (y = cY - 10f..cY + 34f)
        MainMenuRenderer.drawIronWoodPlaqueButton(sr, btnX, cY - 10f, btnW, btnH)
        ComicFontRenderer.drawPlaqueText(sr, "RESTART", cX, cY + 12f, 17f, Color(0.98f, 0.94f, 0.82f, 1f))

        // 3. Nút QUIT TO MAP (y = cY - 70f..cY - 26f)
        MainMenuRenderer.drawIronWoodPlaqueButton(sr, btnX, cY - 70f, btnW, btnH)
        ComicFontRenderer.drawPlaqueText(sr, "QUIT TO MAP", cX, cY - 48f, 16f, Color(0.98f, 0.94f, 0.82f, 1f))

        // 4. Nút CHEAT: ON/OFF (y = cY - 130f..cY - 86f)
        val isCheatOn = CheatManager.isCheatEnabled
        MainMenuRenderer.drawIronWoodPlaqueButton(sr, btnX, cY - 130f, btnW, btnH, isHighlighted = isCheatOn)
        val cheatLabel = if (isCheatOn) "CHEAT: ON" else "CHEAT: OFF"
        val cheatColor = if (isCheatOn) Color(0.55f, 0.98f, 0.35f, 1f) else Color(0.85f, 0.82f, 0.76f, 1f)
        ComicFontRenderer.drawPlaqueText(sr, cheatLabel, cX, cY - 108f, 16f, cheatColor)

        sr.end()
    }

    // =========================================================================
    // NÚT NỔI & BẢNG "CHEAT MENU" ĐỒNG BỘ BẢNG GỖ TREO XÍCH & BĂNG RÔN VÀNG
    // =========================================================================
    private fun renderCheatFloatingButton(sr: ShapeRenderer) {
        val bx = Config.WORLD_WIDTH - 50f
        val by = Config.WORLD_HEIGHT - 90f

        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

        sr.begin(ShapeRenderer.ShapeType.Filled)
        sr.color = Color(0f, 0f, 0f, 0.35f)
        sr.circle(bx + 2f, by - 3f, 22f)
        sr.color = OUTLINE
        sr.circle(bx, by, 22f)
        sr.color = Color(0.96f, 0.78f, 0.18f, 1f)
        sr.circle(bx, by, 19f)
        sr.color = Color(0.32f, 0.24f, 0.16f, 1f)
        sr.circle(bx, by, 15.5f)
        ComicFontRenderer.drawPlaqueText(sr, "C", bx, by, 15f, Color(1f, 0.90f, 0.25f, 1f))
        sr.end()
    }

    private fun renderCheatDialog(sr: ShapeRenderer) {
        val pX = Config.WORLD_WIDTH / 2f
        val pY = Config.WORLD_HEIGHT / 2f
        val boardW = 360f
        val boardH = 275f
        val boardX = pX - boardW / 2f
        val boardY = pY - 150f

        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

        sr.begin(ShapeRenderer.ShapeType.Filled)
        drawHangingWoodenBoardWithRibbon(
            sr = sr,
            boardX = boardX,
            boardY = boardY,
            boardW = boardW,
            boardH = boardH,
            ribbonTitle = "CHEAT MENU",
            ribbonWidth = 270f,
            titleCharHeight = 24f
        )

        val btnW = 240f
        val btnH = 36f
        val btnX = pX - 120f

        // 1. +1000 GOLD (y = pY + 50f..pY + 86f)
        MainMenuRenderer.drawIronWoodPlaqueButton(sr, btnX, pY + 50f, btnW, btnH)
        ComicFontRenderer.drawPlaqueText(sr, "+1000 GOLD", pX, pY + 68f, 14.5f, Color(1f, 0.90f, 0.25f, 1f))

        // 2. HIGH DAMAGE (y = pY + 5f..pY + 41f)
        val hiDmg = CheatManager.isHighDamageEnabled
        MainMenuRenderer.drawIronWoodPlaqueButton(sr, btnX, pY + 5f, btnW, btnH, isHighlighted = hiDmg)
        val dmgLabel = if (hiDmg) "300 DMG: ON" else "300 DMG: OFF"
        val dmgColor = if (hiDmg) Color(0.55f, 0.98f, 0.35f, 1f) else Color.WHITE
        ComicFontRenderer.drawPlaqueText(sr, dmgLabel, pX, pY + 23f, 14.5f, dmgColor)

        // 3. KILL ALL ENEMIES (y = pY - 40f..pY - 4f)
        MainMenuRenderer.drawIronWoodPlaqueButton(sr, btnX, pY - 40f, btnW, btnH)
        ComicFontRenderer.drawPlaqueText(sr, "WIPE ENEMIES", pX, pY - 22f, 14.5f, Color(1f, 0.55f, 0.45f, 1f))

        // 4. INSTANT WIN (y = pY - 85f..pY - 49f)
        MainMenuRenderer.drawIronWoodPlaqueButton(sr, btnX, pY - 85f, btnW, btnH)
        ComicFontRenderer.drawPlaqueText(sr, "INSTANT WIN", pX, pY - 67f, 14.5f, Color(0.55f, 0.96f, 1f, 1f))

        // 5. CLOSE (y = pY - 135f..pY - 105f)
        MainMenuRenderer.drawIronWoodPlaqueButton(sr, pX - 65f, pY - 135f, 130f, 32f)
        ComicFontRenderer.drawPlaqueText(sr, "CLOSE", pX, pY - 119f, 14f, Color.WHITE)

        sr.end()
    }

    // =========================================================================
    // BẢNG THUA CUỘC "DEFEAT" TREO DÂY XÍCH CHUẨN HÌNH 5 (BỎ TIP, CHỈ TRY AGAIN & QUIT)
    // =========================================================================
    private fun renderDefeatDialog(sr: ShapeRenderer, pressedDefeatButton: String?) {
        val cX = Config.WORLD_WIDTH / 2f
        val cY = Config.WORLD_HEIGHT / 2f - 10f
        val boardW = 540f
        val boardH = 210f
        val boardX = cX - boardW / 2f
        val boardY = cY - boardH / 2f

        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

        sr.begin(ShapeRenderer.ShapeType.Filled)
        drawHangingWoodenBoardWithRibbon(
            sr = sr,
            boardX = boardX,
            boardY = boardY,
            boardW = boardW,
            boardH = boardH,
            ribbonTitle = "DEFEAT",
            ribbonWidth = 310f,
            titleCharHeight = 36f
        )

        val btnW = 212f
        val btnH = 64f
        val tryAgainX = cX - 224f
        val quitX = cX + 12f
        val btnY = boardY + 42f

        val isTryPressed = (pressedDefeatButton == "TRY_AGAIN")
        val isQuitPressed = (pressedDefeatButton == "QUIT")

        MainMenuRenderer.drawIronWoodPlaqueButton(sr, tryAgainX, btnY, btnW, btnH, isHighlighted = isTryPressed)
        MainMenuRenderer.drawIronWoodPlaqueButton(sr, quitX, btnY, btnW, btnH, isHighlighted = isQuitPressed)

        ComicFontRenderer.drawPlaqueText(
            sr = sr,
            text = "TRY AGAIN",
            centerX = tryAgainX + btnW / 2f,
            centerY = btnY + btnH / 2f,
            charHeight = 20f,
            fillColor = if (isTryPressed) Color(1f, 0.94f, 0.35f, 1f) else Color(0.94f, 0.92f, 0.86f, 1f)
        )
        ComicFontRenderer.drawPlaqueText(
            sr = sr,
            text = "QUIT",
            centerX = quitX + btnW / 2f,
            centerY = btnY + btnH / 2f,
            charHeight = 22f,
            fillColor = if (isQuitPressed) Color(1f, 0.94f, 0.35f, 1f) else Color(0.94f, 0.92f, 0.86f, 1f)
        )

        sr.end()
    }

    private fun drawHangingWoodenBoardWithRibbon(
        sr: ShapeRenderer,
        boardX: Float,
        boardY: Float,
        boardW: Float,
        boardH: Float,
        ribbonTitle: String,
        ribbonWidth: Float,
        titleCharHeight: Float
    ) {
        val cX = boardX + boardW / 2f

        sr.color = Color(0f, 0f, 0f, 0.62f)
        sr.rect(0f, 0f, Config.WORLD_WIDTH, Config.WORLD_HEIGHT)

        for (chainX in listOf(boardX + 52f, boardX + boardW - 66f)) {
            sr.color = OUTLINE
            sr.rect(chainX - 3f, boardY + boardH - 5f, 20f, Config.WORLD_HEIGHT - (boardY + boardH) + 10f)
            sr.color = Color(0.32f, 0.34f, 0.38f, 1f)
            sr.rect(chainX, boardY + boardH - 5f, 14f, Config.WORLD_HEIGHT - (boardY + boardH) + 10f)
            sr.color = Color(0.68f, 0.70f, 0.74f, 1f)
            for (cy in (boardY + boardH).toInt()..720 step 16) {
                sr.rect(chainX + 2.5f, cy.toFloat(), 9f, 10f)
            }
        }

        sr.color = Color(0f, 0f, 0f, 0.45f)
        sr.rect(boardX + 8f, boardY - 8f, boardW, boardH)

        sr.color = OUTLINE
        sr.rect(boardX - 5f, boardY - 5f, boardW + 10f, boardH + 10f)
        sr.color = Color(0.62f, 0.63f, 0.60f, 1f)
        sr.rect(boardX, boardY, boardW, boardH)
        sr.color = OUTLINE
        sr.rect(boardX + 7f, boardY + 7f, boardW - 14f, boardH - 14f)

        sr.color = Color(0.28f, 0.22f, 0.16f, 1f)
        sr.rect(boardX + 10f, boardY + 10f, boardW - 20f, boardH - 20f)
        sr.color = Color(0.22f, 0.17f, 0.12f, 1f)
        for (gy in (boardY + 34f).toInt() until (boardY + boardH - 22f).toInt() step 38) {
            sr.rect(boardX + 20f, gy.toFloat(), boardW - 40f, 3.5f)
        }

        val corners = listOf(
            Vector2(boardX - 2f, boardY + boardH - 24f),
            Vector2(boardX + boardW - 24f, boardY + boardH - 24f),
            Vector2(boardX - 2f, boardY - 2f),
            Vector2(boardX + boardW - 24f, boardY - 2f)
        )
        for (c in corners) {
            sr.color = OUTLINE
            sr.rect(c.x - 1.5f, c.y - 1.5f, 29f, 29f)
            sr.color = Color(0.72f, 0.74f, 0.76f, 1f)
            sr.rect(c.x, c.y, 26f, 26f)
            sr.color = OUTLINE
            sr.circle(c.x + 6f, c.y + 6f, 2.2f)
            sr.circle(c.x + 20f, c.y + 6f, 2.2f)
            sr.circle(c.x + 6f, c.y + 20f, 2.2f)
            sr.circle(c.x + 20f, c.y + 20f, 2.2f)
        }

        val topY = boardY + boardH
        val tailSpread = ribbonWidth / 2f + 42f
        sr.color = OUTLINE
        sr.rect(cX - tailSpread, topY - 38f, 58f, 40f)
        sr.rect(cX + tailSpread - 58f, topY - 38f, 58f, 40f)
        sr.color = Color(0.74f, 0.58f, 0.24f, 1f)
        sr.rect(cX - tailSpread + 3f, topY - 34f, 52f, 32f)
        sr.rect(cX + tailSpread - 55f, topY - 34f, 52f, 32f)
        sr.color = Color(0.28f, 0.22f, 0.16f, 1f)
        sr.triangle(cX - tailSpread + 2f, topY - 8f, cX - tailSpread + 2f, topY - 28f, cX - tailSpread + 18f, topY - 18f)
        sr.triangle(cX + tailSpread - 2f, topY - 8f, cX + tailSpread - 2f, topY - 28f, cX + tailSpread - 18f, topY - 18f)

        val ribH = 60f
        val ribX = cX - ribbonWidth / 2f
        val ribY = topY - 24f
        sr.color = OUTLINE
        sr.rect(ribX - 4f, ribY - 4f, ribbonWidth + 8f, ribH + 8f)
        sr.color = Color(0.88f, 0.73f, 0.34f, 1f)
        sr.rect(ribX, ribY, ribbonWidth, ribH)
        sr.color = Color(0.96f, 0.84f, 0.46f, 1f)
        sr.rect(ribX + 6f, ribY + 6f, ribbonWidth - 12f, ribH - 12f)

        ComicFontRenderer.drawRibbonBannerText(
            sr = sr,
            text = ribbonTitle,
            centerX = cX,
            centerY = ribY + ribH / 2f,
            charHeight = titleCharHeight
        )
    }

    private fun renderEntities(
        shapeRenderer: ShapeRenderer,
        engine: Engine,
        levelNumber: Int,
        glowingCircles: List<Pair<Vector2, Float>>,
        warningTowersMap: Map<Entity, Float>,
        lockedTowersMap: Map<Entity, LockedTowerState>,
        activeJuggernautBombs: List<JuggernautSummonBomb>
    ) {
        val spots = engine.getEntitiesFor(Family.all(TowerSpotComponent::class.java, TransformComponent::class.java).get()).toList()
        val towers = engine.getEntitiesFor(Family.all(TowerComponent::class.java, TransformComponent::class.java).get()).toList()
        val enemies = engine.getEntitiesFor(Family.all(EnemyComponent::class.java, TransformComponent::class.java).get()).toList()
        val eggs = engine.getEntitiesFor(Family.all(SpiderEggComponent::class.java, TransformComponent::class.java).get()).toList()
        val soldiers = engine.getEntitiesFor(Family.all(SoldierComponent::class.java, TransformComponent::class.java).get()).toList()
        val projectiles = engine.getEntitiesFor(Family.all(ProjectileComponent::class.java, TransformComponent::class.java).get()).toList()

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        for (s in spots) {
            if (s.getComponent(TowerSpotComponent::class.java)?.isOccupied == false) {
                val p = s.getComponent(TransformComponent::class.java)?.position ?: continue
                TowerRenderer.renderStrategicPoint(shapeRenderer, p.x, p.y, levelNumber)
            }
        }

        for (egg in eggs) {
            EnemyRenderer.renderEgg(shapeRenderer, egg)
        }

        for (t in towers) {
            TowerRenderer.render(shapeRenderer, t)

            val tPos = t.getComponent(TransformComponent::class.java)?.position
            val tComp = t.getComponent(TowerComponent::class.java)

            if (warningTowersMap.containsKey(t) && tPos != null) {
                shapeRenderer.color = Color(0.95f, 0.35f, 1.0f, 0.35f)
                shapeRenderer.circle(tPos.x, tPos.y, 38f)
            }

            if (tComp != null && tComp.isFrozen && tPos != null) {
                val lockState = lockedTowersMap[t]
                if (lockState != null && lockState.isUndispellable) {
                    shapeRenderer.color = Color(0.65f, 0.15f, 0.85f, 0.70f)
                } else {
                    shapeRenderer.color = Color(0.85f, 0.25f, 0.95f, 0.50f)
                }
                shapeRenderer.circle(tPos.x, tPos.y, 34f)
                shapeRenderer.color = Color(1f, 0.85f, 0.25f, 0.85f)
                shapeRenderer.circle(tPos.x, tPos.y, 16f)
            }
        }

        for (sol in soldiers) {
            SoldierRenderer.render(shapeRenderer, sol)
        }

        for (e in enemies) {
            val move = e.getComponent(MovementComponent::class.java)
            if (move?.isInsideTunnel == true) continue
            val eComp = e.getComponent(EnemyComponent::class.java)
            val eTrans = e.getComponent(TransformComponent::class.java)

            if (eComp?.enemyType == EnemyType.VEZNAN && eTrans != null) {
                val isBossCasting = glowingCircles.isNotEmpty() || warningTowersMap.isNotEmpty()
                if (isBossCasting) {
                    shapeRenderer.color = Color(0.85f, 0.20f, 0.95f, 0.40f)
                    shapeRenderer.circle(eTrans.position.x, eTrans.position.y, 32f)
                }
            }

            EnemyRenderer.render(shapeRenderer, e)

            if (eComp != null && eComp.curseTimer > 0f && eTrans != null) {
                shapeRenderer.color = Color(0.75f, 0.20f, 0.95f, 0.75f)
                shapeRenderer.circle(eTrans.position.x, eTrans.position.y + 16f, 4.5f)
                shapeRenderer.color = Color(1f, 0.85f, 0.30f, 0.95f)
                shapeRenderer.circle(eTrans.position.x, eTrans.position.y + 16f, 2.2f)
            }
        }

        for (proj in projectiles) {
            ProjectileRenderer.render(shapeRenderer, proj)
        }

        for (bomb in activeJuggernautBombs) {
            val progress = (bomb.elapsed / bomb.duration).coerceIn(0f, 1f)
            val groundX = MathUtils.lerp(bomb.startPos.x, bomb.targetPos.x, progress)
            val groundY = MathUtils.lerp(bomb.startPos.y, bomb.targetPos.y, progress)
            val arcHeight = 95f * (4f * progress * (1f - progress))

            shapeRenderer.color = Color(0.08f, 0.08f, 0.10f, 0.38f)
            shapeRenderer.circle(groundX, groundY, 9f)

            val bx = groundX
            val by = groundY + arcHeight

            shapeRenderer.color = Color(0.22f, 0.18f, 0.15f, 1f)
            shapeRenderer.circle(bx, by, 11.5f)
            shapeRenderer.color = Color(0.48f, 0.40f, 0.34f, 1f)
            shapeRenderer.circle(bx, by, 9.5f)
            shapeRenderer.color = Color(1f, 0.55f, 0.10f, 1f)
            shapeRenderer.circle(bx, by + 11f, 3.8f)
            shapeRenderer.color = Color(1f, 0.90f, 0.25f, 1f)
            shapeRenderer.circle(bx, by + 11f, 2.0f)
        }

        shapeRenderer.end()
    }
}
