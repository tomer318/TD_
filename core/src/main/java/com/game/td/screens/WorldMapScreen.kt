package com.game.td.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
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
import com.game.td.managers.SoundManager
import com.game.td.map.*
import com.game.td.renderers.EncyclopediaRenderer
import com.game.td.renderers.UpgradeRenderer
import com.game.td.renderers.WorldMapRenderer

class WorldMapScreen(
    private val onStartLevel: (LevelData, GameDifficulty, GameMode) -> Unit = { _, _, _ -> },
    private val onOpenUpgrades: () -> Unit = {}
) : ScreenAdapter() {

    private val camera = OrthographicCamera()
    private val viewport = FitViewport(Config.WORLD_WIDTH, Config.WORLD_HEIGHT, camera)
    private val shapeRenderer = ShapeRenderer()
    private val batch = SpriteBatch()

    private val titleFont = BitmapFont().apply {
        data.setScale(1.6f)
        region.texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest)
    }
    private val font = BitmapFont().apply {
        data.setScale(1.35f)
        region.texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest)
    }
    private val smallFont = BitmapFont().apply {
        data.setScale(1.15f)
        region.texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest)
    }

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
        Pair("12. The Dark Tower", Vector2(765f, 480f))
    )

    private val sandboxNode = Vector2(Config.WORLD_WIDTH - 95f, 85f)

    private var selectedLevelData: LevelData? = null
    private var selectedLevelName: String = ""
    private var selectedLevelNumber: Int = 1
    private var isPopupOpen: Boolean = false
    private var isUpgradesOpen: Boolean = false
    private var isEncyclopediaOpen: Boolean = false
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
        SoundManager.init()
        SoundManager.playMapMusic()
        activeFlaggedIndex = -1
        isPopupOpen = false
        isUpgradesOpen = false
        isEncyclopediaOpen = false

        Gdx.input.inputProcessor = object : InputAdapter() {
            override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
                touchPoint.set(screenX.toFloat(), screenY.toFloat(), 0f)
                viewport.unproject(touchPoint)
                val x = touchPoint.x
                val y = touchPoint.y

                if (isUpgradesOpen) {
                    isUpgradesOpen = UpgradeRenderer.handleTouch(x, y)
                    return true
                }

                if (isEncyclopediaOpen) {
                    isEncyclopediaOpen = EncyclopediaRenderer.handleTouch(x, y)
                    return true
                }

                if (isPopupOpen) {
                    val cX = Config.WORLD_WIDTH / 2f
                    val cY = Config.WORLD_HEIGHT / 2f

                    if (Vector2(cX + 245f, cY + 165f).dst(x, y) <= 25f) {
                        SoundManager.playClick()
                        isPopupOpen = false
                        activeFlaggedIndex = -1
                        return true
                    }

                    if (x in (cX - 230f)..(cX - 70f) && y in (cY - 120f)..(cY - 80f)) {
                        SoundManager.playClick()
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
                        SoundManager.playTowerPreview()
                        currentGameMode = GameMode.CAMPAIGN
                        return true
                    }

                    if (Vector2(cX - 120f, cY - 145f).dst(x, y) <= 24f) {
                        if (isChallengeUnlocked) {
                            SoundManager.playTowerPreview()
                            currentGameMode = GameMode.HEROIC
                        } else {
                            SoundManager.playClick()
                        }
                        return true
                    }

                    if (Vector2(cX - 60f, cY - 145f).dst(x, y) <= 24f) {
                        if (isChallengeUnlocked) {
                            SoundManager.playTowerPreview()
                            currentGameMode = GameMode.IRON
                        } else {
                            SoundManager.playClick()
                        }
                        return true
                    }

                    if (x in (cX + 110f)..(cX + 240f) && y in (cY - 150f)..(cY - 95f)) {
                        SoundManager.playBattleStart()
                        selectedLevelData?.let { baseData ->
                            val finalData = when (currentGameMode) {
                                GameMode.CAMPAIGN -> baseData
                                GameMode.HEROIC -> {
                                    val (hWaves, startG) = when (selectedLevelNumber) {
                                        1 -> Pair(Level1Southport.heroicWaves, 170)
                                        2 -> Pair(Level2Farmlands.heroicWaves, 250)
                                        3 -> Pair(Level3Pagras.heroicWaves, 310)
                                        4 -> Pair(Level4TwinRivers.heroicWaves, Level4TwinRivers.heroicStartingGold)
                                        5 -> Pair(Level5Silveroak.heroicWaves, Level5Silveroak.heroicStartingGold)
                                        6 -> Pair(Level6Citadel.heroicWaves, Level6Citadel.heroicStartingGold)
                                        7 -> Pair(Level7ColdstepMines.heroicWaves, 1000)
                                        8 -> Pair(Level8IcewindPass.heroicWaves, Level8IcewindPass.heroicStartingGold)
                                        9 -> Pair(Level9StormcloudTemple.heroicWaves, Level9StormcloudTemple.heroicStartingGold)
                                        10 -> Pair(Level10TheWastes.heroicWaves, Level10TheWastes.heroicStartingGold)
                                        11 -> Pair(Level11ForsakenValley.heroicWaves, Level11ForsakenValley.heroicStartingGold)
                                        12 -> Pair(Level12TheDarkTower.heroicWaves, Level12TheDarkTower.heroicStartingGold)
                                        else -> Pair(baseData.waves, baseData.startingGold)
                                    }
                                    baseData.copy(startingLives = 1, startingGold = startG, waves = hWaves)
                                }
                                GameMode.IRON -> {
                                    val (iWaves, startG) = when (selectedLevelNumber) {
                                        1 -> Pair(Level1Southport.ironWaves, 265)
                                        2 -> Pair(Level2Farmlands.ironWaves, 220)
                                        3 -> Pair(Level3Pagras.ironWaves, 500)
                                        4 -> Pair(Level4TwinRivers.ironWaves, Level4TwinRivers.ironStartingGold)
                                        5 -> Pair(Level5Silveroak.ironWaves, Level5Silveroak.ironStartingGold)
                                        6 -> Pair(Level6Citadel.ironWaves, Level6Citadel.ironStartingGold)
                                        7 -> Pair(Level7ColdstepMines.ironWaves, 1200)
                                        8 -> Pair(Level8IcewindPass.ironWaves, Level8IcewindPass.ironStartingGold)
                                        9 -> Pair(Level9StormcloudTemple.ironWaves, Level9StormcloudTemple.ironStartingGold)
                                        10 -> Pair(Level10TheWastes.ironWaves, Level10TheWastes.ironStartingGold)
                                        11 -> Pair(Level11ForsakenValley.ironWaves, Level11ForsakenValley.ironStartingGold)
                                        12 -> Pair(Level12TheDarkTower.ironWaves, Level12TheDarkTower.ironStartingGold)
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

                if (x in 55f..150f && y in (Config.WORLD_HEIGHT - 85f)..(Config.WORLD_HEIGHT - 35f)) {
                    SoundManager.toggleAudio()
                    SoundManager.playClick()
                    return true
                }

                if (x in 610f..770f && y in 36f..130f) {
                    SoundManager.playTowerPreview()
                    UpgradeRenderer.open()
                    isUpgradesOpen = true
                    return true
                }

                if (x in 790f..965f && y in 36f..130f) {
                    SoundManager.playTowerPreview()
                    EncyclopediaRenderer.open()
                    isEncyclopediaOpen = true
                    return true
                }

                val maxLevelAllowed = getHighestAccessibleLevel()

                for (i in 0 until campaignNodes.size) {
                    val lvlNum = i + 1
                    if (lvlNum <= maxLevelAllowed && campaignNodes[i].second.dst(x, y) <= 32f) {
                        SoundManager.playMapNodeSelect()
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
                            12 -> Level12TheDarkTower.data
                            else -> Level1Southport.data
                        }
                        selectedLevelName = campaignNodes[i].first.substringAfter(". ").uppercase()
                        currentGameMode = GameMode.CAMPAIGN
                        isPopupOpen = true
                        return true
                    }
                }

                if (sandboxNode.dst(x, y) <= 32f) {
                    SoundManager.playMapNodeSelect()
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
        Gdx.gl.glClearColor(0.68f, 0.56f, 0.36f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        camera.update()
        shapeRenderer.projectionMatrix = camera.combined
        batch.projectionMatrix = camera.combined

        WorldMapRenderer.render(
            shapeRenderer = shapeRenderer,
            batch = batch,
            titleFont = titleFont,
            font = font,
            smallFont = smallFont,
            campaignNodes = campaignNodes,
            sandboxNode = sandboxNode,
            nextLevelToPlay = getHighestAccessibleLevel(),
            isPopupOpen = isPopupOpen,
            isUpgradesOpen = isUpgradesOpen,
            isEncyclopediaOpen = isEncyclopediaOpen,
            selectedLevelData = selectedLevelData,
            selectedLevelName = selectedLevelName,
            selectedLevelNumber = selectedLevelNumber,
            currentDifficulty = currentDifficulty,
            currentGameMode = currentGameMode
        )
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
