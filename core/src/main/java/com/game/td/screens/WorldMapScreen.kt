package com.game.td.screens

import com.badlogic.ashley.core.Entity
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
import com.game.td.ecs.components.GameDifficulty
import com.game.td.managers.GameMode
import com.game.td.managers.SaveManager
import com.game.td.map.*

class WorldMapScreen(
    private val onStartLevel: (LevelData, GameDifficulty, GameMode) -> Unit = { _, _, _ -> },
    private val onOpenUpgrades: () -> Unit = {}
) : ScreenAdapter() {

    private val camera = OrthographicCamera()
    private val viewport = FitViewport(Config.WORLD_WIDTH, Config.WORLD_HEIGHT, camera)
    private val shapeRenderer = ShapeRenderer()
    private val batch = SpriteBatch()
    private val titleFont = BitmapFont().apply { data.setScale(1.5f) }
    private val font = BitmapFont().apply { data.setScale(1.2f) }
    private val smallFont = BitmapFont().apply { data.setScale(0.9f) }

    private var currentDifficulty = GameDifficulty.NORMAL
    private var currentGameMode = GameMode.CAMPAIGN
    private val touchPoint = Vector3()

    private val campaignNodes = listOf(
        Pair("1. Southport", Vector2(195f, 155f)),
        Pair("2. The Farmlands", Vector2(185f, 255f)),
        Pair("3. Pagras", Vector2(255f, 310f)),
        Pair("4. Twin River Pass", Vector2(330f, 350f)),
        Pair("5. Silveroak Forest", Vector2(265f, 435f)),
        Pair("6. The Citadel", Vector2(250f, 550f)),
        Pair("7. Coldstep Mines", Vector2(420f, 580f)),
        Pair("8. Icewind Pass", Vector2(525f, 600f)),
        Pair("9. Stormcloud Temple", Vector2(620f, 545f)),
        Pair("10. The Wastes", Vector2(605f, 440f)),
        Pair("11. Forsaken Valley", Vector2(735f, 370f)),
        Pair("12. The Dark Tower", Vector2(765f, 480f)) // MÀN 12: THE DARK TOWER
    )

    private val sandboxNode = Vector2(Config.WORLD_WIDTH - 95f, 85f)

    private var selectedLevelData: LevelData? = null
    private var selectedLevelName: String = ""
    private var selectedLevelNumber: Int = 1
    private var isPopupOpen: Boolean = false
    private var activeFlaggedIndex: Int = -1

    private fun getHighestAccessibleLevel(): Int {
        for (i in 1..12) {
            if (SaveManager.getLevelStars(i) == 0) {
                return i
            }
        }
        return 12
    }

    override fun show() {
        activeFlaggedIndex = -1
        isPopupOpen = false

        Gdx.input.inputProcessor = object : InputAdapter() {
            override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
                touchPoint.set(screenX.toFloat(), screenY.toFloat(), 0f)
                viewport.unproject(touchPoint)
                val x = touchPoint.x
                val y = touchPoint.y

                if (isPopupOpen) {
                    val cX = Config.WORLD_WIDTH / 2f
                    val cY = Config.WORLD_HEIGHT / 2f

                    if (Vector2(cX + 240f, cY + 160f).dst(x, y) <= 25f) {
                        isPopupOpen = false
                        activeFlaggedIndex = -1
                        return true
                    }

                    if (x in (cX - 230f)..(cX - 70f) && y in (cY - 120f)..(cY - 80f)) {
                        currentDifficulty = when (currentDifficulty) {
                            GameDifficulty.CASUAL -> GameDifficulty.NORMAL
                            GameDifficulty.NORMAL -> GameDifficulty.VETERAN
                            GameDifficulty.VETERAN -> GameDifficulty.CASUAL
                        }
                        return true
                    }

                    val levelStars = SaveManager.getLevelStars(selectedLevelNumber)
                    val isChallengeUnlocked = levelStars >= 3

                    if (Vector2(cX - 180f, cY - 145f).dst(x, y) <= 24f) {
                        currentGameMode = GameMode.CAMPAIGN
                        return true
                    }

                    if (isChallengeUnlocked && Vector2(cX - 120f, cY - 145f).dst(x, y) <= 24f) {
                        currentGameMode = GameMode.HEROIC
                        return true
                    }

                    if (isChallengeUnlocked && Vector2(cX - 60f, cY - 145f).dst(x, y) <= 24f) {
                        currentGameMode = GameMode.IRON
                        return true
                    }

                    if (x in (cX + 110f)..(cX + 240f) && y in (cY - 150f)..(cY - 95f)) {
                        selectedLevelData?.let { baseData ->
                            val finalData = when (currentGameMode) {
                                GameMode.CAMPAIGN -> baseData
                                GameMode.HEROIC -> {
                                    val (hWaves, startG) = when (selectedLevelNumber) {
                                        1 -> Pair(Level1Southport.heroicWaves, 170)
                                        2 -> Pair(Level2Farmlands.heroicWaves, 250)
                                        3 -> Pair(Level3Pagras.heroicWaves, 310)
                                        4 -> Pair(Level4TwinRivers.data.waves, 520)
                                        5 -> Pair(Level5Silveroak.heroicWaves, 1000)
                                        6 -> Pair(Level6Citadel.heroicWaves, 1200)
                                        7 -> Pair(Level7ColdstepMines.heroicWaves, 1000)
                                        8 -> Pair(Level8IcewindPass.campaignWaves.take(6), 1100)
                                        9 -> Pair(Level9StormcloudTemple.campaignWaves.take(6), 2000)
                                        10 -> Pair(Level10TheWastes.campaignWaves.take(6), 2000)
                                        11 -> Pair(Level11ForsakenValley.campaignWaves.take(6), 2100)
                                        12 -> Pair(Level12TheDarkTower.campaignWaves.take(6), 2200) // HEROIC MÀN 12
                                        else -> Pair(baseData.waves, baseData.startingGold)
                                    }
                                    baseData.copy(startingLives = 1, startingGold = startG, waves = hWaves)
                                }
                                GameMode.IRON -> {
                                    val (iWaves, startG) = when (selectedLevelNumber) {
                                        1 -> Pair(Level1Southport.ironWaves, 265)
                                        2 -> Pair(Level2Farmlands.ironWaves, 220)
                                        3 -> Pair(Level3Pagras.ironWaves, 500)
                                        6 -> Pair(Level6Citadel.ironWaves, 800)
                                        7 -> Pair(Level7ColdstepMines.ironWaves, 1200)
                                        8 -> Pair(Level8IcewindPass.campaignWaves.take(1), 1200)
                                        9 -> Pair(Level9StormcloudTemple.campaignWaves.take(1), 2400)
                                        10 -> Pair(Level10TheWastes.campaignWaves.take(1), 2400)
                                        11 -> Pair(Level11ForsakenValley.campaignWaves.take(1), 2600)
                                        12 -> Pair(Level12TheDarkTower.campaignWaves.take(1), 2800) // IRON MÀN 12
                                        else -> Pair(baseData.waves, baseData.startingGold)
                                    }
                                    baseData.copy(startingLives = 1, startingGold = startG, waves = iWaves)
                                }
                            }
                            onStartLevel(finalData, currentDifficulty, currentGameMode)
                        }
                        isPopupOpen = false
                        activeFlaggedIndex = -1
                        return true
                    }
                    return true
                }

                if (x in 50f..210f && y in 40f..105f) {
                    onOpenUpgrades()
                    return true
                }

                val maxLevelAllowed = getHighestAccessibleLevel()

                for (i in 0 until campaignNodes.size) {
                    val lvlNum = i + 1
                    if (lvlNum <= maxLevelAllowed && campaignNodes[i].second.dst(x, y) <= 28f) {
                        activeFlaggedIndex = i
                        selectedLevelNumber = lvlNum
                        selectedLevelData = when (selectedLevelNumber) {
                            1 -> Level1Southport.data
                            2 -> Level2Farmlands.data
                            3 -> Level3Pagras.data
                            4 -> Level4TwinRivers.data
                            5 -> Level5Silveroak.data
                            6 -> Level6Citadel.data
                            7 -> Level7ColdstepMines.data
                            8 -> Level8IcewindPass.data
                            9 -> Level9StormcloudTemple.data
                            10 -> Level10TheWastes.data
                            11 -> Level11ForsakenValley.data
                            12 -> Level12TheDarkTower.data // ĐÃ KẾT NỐI MÀN 12
                            else -> Level1Southport.data
                        }
                        selectedLevelName = campaignNodes[i].first.substringAfter(". ").uppercase()
                        currentGameMode = GameMode.CAMPAIGN
                        isPopupOpen = true
                        return true
                    }
                }

                if (sandboxNode.dst(x, y) <= 30f) {
                    selectedLevelData = LevelSandboxTest.data
                    selectedLevelName = "SANDBOX TEST"
                    selectedLevelNumber = 0
                    currentGameMode = GameMode.CAMPAIGN
                    isPopupOpen = true
                    return true
                }

                return true
            }
        }
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.70f, 0.58f, 0.38f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        camera.update()
        shapeRenderer.projectionMatrix = camera.combined
        batch.projectionMatrix = camera.combined

        val nextLevelToPlay = getHighestAccessibleLevel()
        val totalStarsX = Config.WORLD_WIDTH - 290f
        val totalStarsY = Config.WORLD_HEIGHT - 50f

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color(0.85f, 0.74f, 0.54f, 1f)
        shapeRenderer.rect(25f, 25f, Config.WORLD_WIDTH - 50f, Config.WORLD_HEIGHT - 50f)

        shapeRenderer.color = Color(0.60f, 0.48f, 0.32f, 1f)
        shapeRenderer.rect(25f, 25f, Config.WORLD_WIDTH - 50f, 6f)
        shapeRenderer.rect(25f, Config.WORLD_HEIGHT - 31f, Config.WORLD_WIDTH - 50f, 6f)
        shapeRenderer.rect(25f, 25f, 6f, Config.WORLD_HEIGHT - 50f)
        shapeRenderer.rect(Config.WORLD_WIDTH - 31f, 25f, 6f, Config.WORLD_HEIGHT - 50f)

        for (i in 0 until campaignNodes.size - 1) {
            val isPathUnlocked = (i + 1 < nextLevelToPlay)
            val lineColor = if (isPathUnlocked) Color(0.35f, 0.20f, 0.10f, 1f) else Color(0.60f, 0.52f, 0.44f, 0.35f)
            drawDottedLine(shapeRenderer, campaignNodes[i].second, campaignNodes[i + 1].second, lineColor, 5f)
        }

        for (i in campaignNodes.indices) {
            val lvlNum = i + 1
            val pos = campaignNodes[i].second
            val stars = SaveManager.getLevelStars(lvlNum)

            val isPassed = (stars > 0)
            val isCurrentNext = (lvlNum == nextLevelToPlay)

            if (isPassed || isCurrentNext) {
                shapeRenderer.color = Color(0.92f, 0.70f, 0.15f, 1f)
                shapeRenderer.circle(pos.x, pos.y, 14f)
                shapeRenderer.color = Color(0.85f, 0.20f, 0.15f, 1f)
                shapeRenderer.circle(pos.x, pos.y, 8.5f)

                if (isPopupOpen && i == activeFlaggedIndex) {
                    drawBannerFlag(shapeRenderer, pos.x, pos.y)
                }
            } else {
                shapeRenderer.color = Color(0.70f, 0.70f, 0.74f, 1f)
                shapeRenderer.circle(pos.x, pos.y, 13f)
                shapeRenderer.color = Color(0.40f, 0.40f, 0.44f, 1f)
                shapeRenderer.circle(pos.x, pos.y, 7.5f)
            }
        }

        shapeRenderer.color = Color(0.25f, 0.15f, 0.35f, 1f)
        shapeRenderer.circle(sandboxNode.x, sandboxNode.y, 26f)
        shapeRenderer.color = Color(0.65f, 0.25f, 0.95f, 1f)
        shapeRenderer.circle(sandboxNode.x, sandboxNode.y, 20f)
        shapeRenderer.color = Color(0.95f, 0.70f, 1f, 1f)
        shapeRenderer.circle(sandboxNode.x, sandboxNode.y, 10f)

        shapeRenderer.color = Color(0.72f, 0.22f, 0.16f, 1f)
        shapeRenderer.rect(60f, 50f, 140f, 45f)
        shapeRenderer.end()

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color.GOLD
        shapeRenderer.rect(60f, 50f, 140f, 45f)
        shapeRenderer.end()

        for (i in campaignNodes.indices) {
            val lvlNum = i + 1
            val pos = campaignNodes[i].second
            val stars = SaveManager.getLevelStars(lvlNum)
            if (stars > 0) {
                val starOffset = if (isPopupOpen && i == activeFlaggedIndex) 46f else 22f
                drawStarsOnMap(shapeRenderer, pos.x, pos.y + starOffset, stars)
            }
        }

        drawStarDirect(shapeRenderer, totalStarsX + 225f, totalStarsY + 7f, 10f, true)

        batch.begin()
        font.color = Color(0.35f, 0.20f, 0.10f, 1f)
        font.draw(batch, "KINGDOM RUSH - LINIREA CAMPAIGN", Config.WORLD_WIDTH / 2f - 210f, Config.WORLD_HEIGHT - 45f)

        font.color = Color.GOLD
        font.draw(batch, "TOTAL STARS: ${SaveManager.getTotalStars()}", totalStarsX + 15f, totalStarsY + 12f)

        font.color = Color.WHITE
        font.draw(batch, "UPGRADES", 74f, 80f)

        smallFont.color = Color.WHITE
        smallFont.draw(batch, "SANDBOX", sandboxNode.x - 36f, sandboxNode.y + 42f)
        batch.end()

        if (isPopupOpen) {
            renderMissionPopup()
        }
    }

    private fun drawBannerFlag(sr: ShapeRenderer, x: Float, y: Float) {
        sr.color = Color(0.35f, 0.22f, 0.12f, 1f)
        sr.rect(x - 2f, y, 4f, 38f)
        sr.color = Color.GOLD
        sr.circle(x, y + 38f, 4.5f)

        val flagBaseY = y + 16f
        val flagTopY = y + 36f
        sr.color = Color(0.85f, 0.15f, 0.15f, 1f)
        sr.rect(x + 2f, flagBaseY, 26f, 20f)
        sr.triangle(x + 28f, flagTopY, x + 28f, flagBaseY, x + 38f, flagBaseY + 10f)

        sr.color = Color.GOLD
        sr.rect(x + 2f, flagTopY - 2f, 26f, 2f)
        sr.rect(x + 2f, flagBaseY, 26f, 2f)
    }

    private fun drawStarDirect(sr: ShapeRenderer, centerX: Float, centerY: Float, outerRadius: Float, isFilled: Boolean) {
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
        sr.color = Color(0.12f, 0.10f, 0.08f, 0.8f)
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

    private fun drawDottedLine(sr: ShapeRenderer, from: Vector2, to: Vector2, color: Color, width: Float) {
        sr.color = color
        val dist = from.dst(to)
        val step = 14f
        var current = 0f
        val dir = Vector2(to).sub(from).nor()

        while (current < dist) {
            val startP = Vector2(from).add(Vector2(dir).scl(current))
            val segmentEnd = (current + 7f).coerceAtMost(dist)
            val endP = Vector2(from).add(Vector2(dir).scl(segmentEnd))
            sr.rectLine(startP, endP, width)
            current += step
        }
    }

    private fun drawStarsOnMap(sr: ShapeRenderer, x: Float, y: Float, count: Int) {
        val offsets = when (count) {
            1 -> listOf(0f)
            2 -> listOf(-8f, 8f)
            else -> listOf(-14f, 0f, 14f)
        }
        for (off in offsets) {
            drawStarDirect(sr, x + off, y, 6.5f, true)
        }
    }

    private fun renderMissionPopup() {
        val cX = Config.WORLD_WIDTH / 2f
        val cY = Config.WORLD_HEIGHT / 2f
        val pW = 420f
        val pH = 330f

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color(0.18f, 0.15f, 0.12f, 0.96f)
        shapeRenderer.rect(cX - 260f, cY - 170f, 520f, 340f)

        shapeRenderer.color = Color(0.78f, 0.65f, 0.38f, 1f)
        shapeRenderer.rect(cX - 140f, cY + 140f, 280f, 38f)

        val boxX = cX - 235f
        val boxY = cY - 70f
        val boxW = 160f
        val boxH = 150f

        shapeRenderer.color = Color(0.24f, 0.23f, 0.25f, 1f)
        shapeRenderer.rect(boxX, boxY, boxW, boxH)

        selectedLevelData?.let { level ->
            val allPaths = level.allPaths

            fun mapToPreview(worldPos: Vector2): Vector2 {
                val px = boxX + (worldPos.x / Config.WORLD_WIDTH) * boxW
                val py = boxY + (worldPos.y / Config.WORLD_HEIGHT) * boxH
                return Vector2(px, py)
            }

            shapeRenderer.color = Color(0.35f, 0.34f, 0.36f, 1f)
            for (path in allPaths) {
                for (i in 0 until path.size - 1) {
                    shapeRenderer.rectLine(mapToPreview(path[i]), mapToPreview(path[i + 1]), 10f)
                }
                for (w in path) shapeRenderer.circle(mapToPreview(w).x, mapToPreview(w).y, 5f)
            }

            shapeRenderer.color = Color(0.65f, 0.62f, 0.55f, 1f)
            for (path in allPaths) {
                for (i in 0 until path.size - 1) {
                    shapeRenderer.rectLine(mapToPreview(path[i]), mapToPreview(path[i + 1]), 7f)
                }
                for (w in path) shapeRenderer.circle(mapToPreview(w).x, mapToPreview(w).y, 3.5f)
            }

            for (spot in level.towerSpots) {
                val sp = mapToPreview(spot)
                shapeRenderer.color = Color(0.48f, 0.45f, 0.38f, 1f)
                shapeRenderer.circle(sp.x, sp.y, 5f)
                shapeRenderer.color = Color(0.68f, 0.62f, 0.50f, 1f)
                shapeRenderer.circle(sp.x, sp.y, 3.5f)
            }
        }

        if (currentGameMode == GameMode.CAMPAIGN) {
            val diffBg = when (currentDifficulty) {
                GameDifficulty.CASUAL -> Color(0.20f, 0.65f, 0.25f, 1f)
                GameDifficulty.NORMAL -> Color(0.85f, 0.58f, 0.15f, 1f)
                GameDifficulty.VETERAN -> Color(0.80f, 0.20f, 0.15f, 1f)
            }
            shapeRenderer.color = diffBg
            shapeRenderer.rect(cX - 230f, cY - 110f, 150f, 28f)
        }

        val levelStars = SaveManager.getLevelStars(selectedLevelNumber)
        val isChallengeUnlocked = levelStars >= 3

        shapeRenderer.color = if (currentGameMode == GameMode.CAMPAIGN) Color.GOLD else Color.DARK_GRAY
        shapeRenderer.circle(cX - 180f, cY - 145f, 20f)
        shapeRenderer.color = Color(0.2f, 0.5f, 0.85f, 1f)
        shapeRenderer.circle(cX - 180f, cY - 145f, 16f)

        shapeRenderer.color = if (currentGameMode == GameMode.HEROIC) Color.GOLD else Color.DARK_GRAY
        shapeRenderer.circle(cX - 120f, cY - 145f, 20f)
        shapeRenderer.color = if (isChallengeUnlocked) Color(0.85f, 0.35f, 0.15f, 1f) else Color(0.3f, 0.3f, 0.35f, 1f)
        shapeRenderer.circle(cX - 120f, cY - 145f, 16f)

        shapeRenderer.color = if (currentGameMode == GameMode.IRON) Color.GOLD else Color.DARK_GRAY
        shapeRenderer.circle(cX - 60f, cY - 145f, 20f)
        shapeRenderer.color = if (isChallengeUnlocked) Color(0.55f, 0.58f, 0.62f, 1f) else Color(0.3f, 0.3f, 0.35f, 1f)
        shapeRenderer.circle(cX - 60f, cY - 145f, 16f)

        shapeRenderer.color = Color(0.20f, 0.45f, 0.75f, 1f)
        shapeRenderer.rect(cX + 110f, cY - 150f, 130f, 48f)

        shapeRenderer.color = Color(0.75f, 0.20f, 0.15f, 1f)
        shapeRenderer.circle(cX + 240f, cY + 160f, 16f)
        shapeRenderer.end()

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color.GOLD
        shapeRenderer.rect(cX - 260f, cY - 170f, 520f, 340f)
        shapeRenderer.rect(boxX, boxY, boxW, boxH)
        shapeRenderer.rect(cX + 110f, cY - 150f, 130f, 48f)
        shapeRenderer.end()

        batch.begin()
        titleFont.color = Color(0.25f, 0.15f, 0.05f, 1f)
        titleFont.draw(batch, selectedLevelName, cX - 110f, cY + 166f)
        titleFont.color = Color.WHITE
        titleFont.draw(batch, "X", cX + 233f, cY + 168f)

        if (currentGameMode == GameMode.CAMPAIGN) {
            smallFont.color = Color.WHITE
            smallFont.draw(batch, "DIFF: ${currentDifficulty.name}", cX - 215f, cY - 90f)
        }

        smallFont.color = Color.WHITE
        smallFont.draw(batch, "C", cX - 185f, cY - 140f)
        smallFont.draw(batch, if (isChallengeUnlocked) "H" else "X", cX - 125f, cY - 140f)
        smallFont.draw(batch, if (isChallengeUnlocked) "I" else "X", cX - 63f, cY - 140f)

        font.color = Color.GOLD
        val modeTitle = when (currentGameMode) {
            GameMode.CAMPAIGN -> "CAMPAIGN"
            GameMode.HEROIC -> "HEROIC CHALLENGE (1 LIFE)"
            GameMode.IRON -> "IRON CHALLENGE (1 LIFE)"
        }
        font.draw(batch, modeTitle, cX - 55f, cY + 120f)

        smallFont.color = Color.WHITE
        when (currentGameMode) {
            GameMode.CAMPAIGN -> {
                smallFont.draw(batch, "Classic multi-wave invasion.", cX - 55f, cY + 80f)
                smallFont.draw(batch, "Hold the path with standard rules.", cX - 55f, cY + 55f)
                val stars = SaveManager.getLevelStars(selectedLevelNumber)
                smallFont.color = Color.YELLOW
                smallFont.draw(batch, "RECORD: $stars / 3 STARS", cX - 55f, cY + 20f)
            }
            GameMode.HEROIC -> {
                smallFont.draw(batch, "Elite waves. Only 1 life point!", cX - 55f, cY + 80f)
                smallFont.draw(batch, "Victory rewards +1 Extra Star.", cX - 55f, cY + 55f)
                val isDone = SaveManager.isHeroicCompleted(selectedLevelNumber)
                smallFont.color = if (isDone) Color.GREEN else Color.YELLOW
                smallFont.draw(batch, if (isDone) "COMPLETED (+1 STAR)" else "NOT COMPLETED", cX - 55f, cY + 20f)
            }
            GameMode.IRON -> {
                smallFont.draw(batch, "Massive wave! Only 1 life point!", cX - 55f, cY + 80f)
                smallFont.draw(batch, "Victory rewards +1 Extra Star.", cX - 55f, cY + 55f)
                val isDone = SaveManager.isIronCompleted(selectedLevelNumber)
                smallFont.color = if (isDone) Color.GREEN else Color.YELLOW
                smallFont.draw(batch, if (isDone) "COMPLETED (+1 STAR)" else "NOT COMPLETED", cX - 55f, cY + 20f)
            }
        }

        font.color = Color.WHITE
        font.draw(batch, "TO BATTLE!", cX + 118f, cY - 118f)
        batch.end()
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
