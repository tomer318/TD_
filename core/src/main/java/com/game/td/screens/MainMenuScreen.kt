package com.game.td.screens

import com.badlogic.gdx.Game
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
import com.game.td.managers.SaveManager
import com.game.td.managers.SoundManager
import com.game.td.renderers.MainMenuRenderer

class MainMenuScreen(private val game: Game) : ScreenAdapter() {
    private val camera = OrthographicCamera()
    private val viewport = FitViewport(Config.WORLD_WIDTH, Config.WORLD_HEIGHT, camera)
    private val shapeRenderer = ShapeRenderer()
    private val batch = SpriteBatch()

    private val logoMainFont = BitmapFont().apply {
        data.setScale(4.2f)
        region.texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest)
    }
    private val logoSubFont = BitmapFont().apply {
        data.setScale(2.85f)
        region.texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest)
    }
    private val titleFont = BitmapFont().apply {
        data.setScale(2.3f)
        region.texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest)
    }
    private val font = BitmapFont().apply {
        data.setScale(1.45f)
        region.texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest)
    }
    private val italicFont = BitmapFont().apply {
        data.setScale(1.25f)
        region.texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest)
    }
    private val smallFont = BitmapFont().apply {
        data.setScale(1.1f)
        region.texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest)
    }

    private var isSlotPopupOpen = false
    private var isCreditsPopupOpen = false
    private val touchPoint = Vector3()

    override fun show() {
        SoundManager.init()
        SoundManager.playMapMusic()

        Gdx.input.inputProcessor = object : InputAdapter() {
            override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
                touchPoint.set(screenX.toFloat(), screenY.toFloat(), 0f)
                viewport.unproject(touchPoint)
                handleTouch(touchPoint.x, touchPoint.y)
                return true
            }
        }
    }

    private fun handleTouch(x: Float, y: Float) {
        val cX = Config.WORLD_WIDTH / 2f
        val cY = Config.WORLD_HEIGHT / 2f

        // 1. Nút bật/tắt Âm thanh & Nhạc ở góc trên trái
        if (Vector2(52f, Config.WORLD_HEIGHT - 42f).dst(x, y) <= 22f ||
            Vector2(102f, Config.WORLD_HEIGHT - 42f).dst(x, y) <= 22f
        ) {
            SoundManager.toggleAudio()
            SoundManager.playClick()
            return
        }

        // 2. Nếu đang mở bảng CREDITS
        if (isCreditsPopupOpen) {
            if (x in (cX - 80f)..(cX + 80f) && y in (cY - 165f)..(cY - 115f) ||
                x !in (cX - 240f)..(cX + 240f) || y !in (cY - 170f)..(cY + 170f)
            ) {
                SoundManager.playClick()
                isCreditsPopupOpen = false
            }
            return
        }

        // 3. Nếu đang ở màn hình chính (chưa mở bảng Save)
        if (!isSlotPopupOpen) {
            if (x in (cX - 115f)..(cX + 115f) && y in 210f..302f) {
                SoundManager.playTowerPreview()
                isSlotPopupOpen = true
                return
            }
            if (x in (cX - 88f)..(cX + 88f) && y in 132f..188f) {
                SoundManager.playClick()
                isCreditsPopupOpen = true
                return
            }
            return
        }

        // 4. Xử lý tương tác trong bảng LOCAL SAVE
        val panelW = 410f
        val panelH = 420f
        val panelX = cX - panelW / 2f
        val panelY = 58f

        val closeBtnX = cX - 85f
        val closeBtnY = panelY - 36f
        if ((x in closeBtnX..(closeBtnX + 170f) && y in closeBtnY..panelY) ||
            Vector2(panelX + panelW - 18f, panelY + panelH + 14f).dst(x, y) <= 24f ||
            x !in (panelX - 20f)..(panelX + panelW + 20f) ||
            y !in (panelY - 42f)..(panelY + panelH + 55f)
        ) {
            SoundManager.playClick()
            isSlotPopupOpen = false
            return
        }

        val startY = panelY + panelH - 82f
        val cardW = 352f
        val cardH = 92f

        for (i in 1..3) {
            val cardY = startY - (i - 1) * 114f
            val cardX = cX - cardW / 2f
            val hasSave = SaveManager.hasData(i)

            if (hasSave) {
                val delBtnX = cardX + cardW - 30f
                val delBtnY = cardY + 20f
                if (Vector2(delBtnX, delBtnY).dst(x, y) <= 20f) {
                    SoundManager.playClick()
                    SaveManager.deleteSlot(i)
                    return
                }
            }

            if (x in cardX..(cardX + cardW) && y in (cardY - cardH / 2f)..(cardY + cardH / 2f)) {
                SoundManager.playBattleStart()
                SaveManager.activeSlot = i
                if (!hasSave) {
                    SaveManager.resetSlot(i)
                }
                openWorldMap()
                return
            }
        }
    }

    private fun openWorldMap() {
        Gdx.app.postRunnable {
            game.screen = WorldMapScreen(
                onStartLevel = { chosenLevel, chosenDifficulty, chosenMode ->
                    Gdx.app.postRunnable {
                        game.screen = PlayScreen(
                            currentLevel = chosenLevel,
                            difficulty = chosenDifficulty,
                            gameMode = chosenMode,
                            onLevelVictory = {},
                            onReturnToMap = { openWorldMap() }
                        )
                    }
                },
                onOpenUpgrades = {
                    Gdx.app.postRunnable {
                        game.screen = UpgradesScreen(
                            onReturn = { openWorldMap() }
                        )
                    }
                },
                onReturnToMainMenu = {
                    Gdx.app.postRunnable {
                        game.screen = MainMenuScreen(game)
                    }
                }
            )
        }
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.38f, 0.65f, 0.78f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        camera.update()
        shapeRenderer.projectionMatrix = camera.combined
        batch.projectionMatrix = camera.combined

        MainMenuRenderer.render(
            shapeRenderer = shapeRenderer,
            batch = batch,
            logoMainFont = logoMainFont,
            logoSubFont = logoSubFont,
            titleFont = titleFont,
            font = font,
            italicFont = italicFont,
            smallFont = smallFont,
            isSlotPopupOpen = isSlotPopupOpen,
            isCreditsPopupOpen = isCreditsPopupOpen
        )
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun dispose() {
        shapeRenderer.dispose()
        batch.dispose()
        logoMainFont.dispose()
        logoSubFont.dispose()
        titleFont.dispose()
        font.dispose()
        italicFont.dispose()
        smallFont.dispose()
    }
}
