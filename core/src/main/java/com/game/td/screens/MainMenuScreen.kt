package com.game.td.screens

import com.badlogic.gdx.Game
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
import com.game.td.managers.SaveManager
import com.game.td.renderers.GameUIRenderer

class MainMenuScreen(private val game: Game) : ScreenAdapter() {
    private val camera = OrthographicCamera()
    private val viewport = FitViewport(Config.WORLD_WIDTH, Config.WORLD_HEIGHT, camera)
    private val shapeRenderer = ShapeRenderer()
    private val batch = SpriteBatch()
    private val titleFont = BitmapFont().apply { data.setScale(2.2f) }
    private val font = BitmapFont().apply { data.setScale(1.2f) }
    private val italicFont = BitmapFont().apply { data.setScale(1.1f) }
    private val smallFont = BitmapFont().apply { data.setScale(0.95f) }

    private var isSlotPopupOpen = false
    private val touchPoint = Vector3()

    override fun show() {
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
        if (!isSlotPopupOpen) {
            val playBtn = Vector2(Config.WORLD_WIDTH / 2f, 260f)
            if (x in (playBtn.x - 120f)..(playBtn.x + 120f) && y in (playBtn.y - 35f)..(playBtn.y + 35f)) {
                isSlotPopupOpen = true
            }
        } else {
            val cX = Config.WORLD_WIDTH / 2f
            val cY = Config.WORLD_HEIGHT / 2f

            // 1. Nút Đóng popup [?] hoặc bấm ra ngoài
            val closeBtn = Vector2(cX + 175f, cY + 235f)
            if (closeBtn.dst(x, y) <= 22f || x !in (cX - 210f)..(cX + 210f) || y !in (cY - 265f)..(cY + 265f)) {
                isSlotPopupOpen = false
                return
            }

            // 2. Chạm vào 3 slot
            val startY = cY + 115f
            val cardW = 340f
            val cardH = 92f

            for (i in 1..3) {
                val cardY = startY - (i - 1) * 122f
                val cardX = cX - cardW / 2f
                val hasSave = SaveManager.hasData(i)

                // Nút Xóa [X]
                if (hasSave) {
                    val delBtnX = cX + 130f
                    val delBtnY = cardY + 26f
                    if (Vector2(delBtnX, delBtnY).dst(x, y) <= 18f) {
                        SaveManager.deleteSlot(i)
                        return
                    }
                }

                // Chạm vào thẻ để Chơi
                if (x in cardX..(cardX + cardW) && y in (cardY - cardH / 2f)..(cardY + cardH / 2f)) {
                    SaveManager.activeSlot = i
                    if (!hasSave) {
                        SaveManager.resetSlot(i)
                    }
                    openWorldMap()
                    return
                }
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
                }
            )
        }
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.14f, 0.22f, 0.12f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        camera.update()
        shapeRenderer.projectionMatrix = camera.combined
        batch.projectionMatrix = camera.combined

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color(0.08f, 0.14f, 0.08f, 0.95f)
        shapeRenderer.rect(0f, 0f, Config.WORLD_WIDTH, Config.WORLD_HEIGHT)

        if (!isSlotPopupOpen) {
            shapeRenderer.color = Color(0.85f, 0.20f, 0.15f, 1f)
            shapeRenderer.rect(Config.WORLD_WIDTH / 2f - 120f, 225f, 240f, 70f)
        }
        shapeRenderer.end()

        if (!isSlotPopupOpen) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
            shapeRenderer.color = Color.GOLD
            shapeRenderer.rect(Config.WORLD_WIDTH / 2f - 120f, 225f, 240f, 70f)
            shapeRenderer.end()
        }

        batch.begin()
        titleFont.color = Color.GOLD
        titleFont.draw(batch, "KINGDOM RUSH", Config.WORLD_WIDTH / 2f - 240f, 540f)
        smallFont.color = Color.LIGHT_GRAY
        smallFont.draw(batch, "LIBGDX RECREATION", Config.WORLD_WIDTH / 2f - 90f, 470f)

        if (!isSlotPopupOpen) {
            font.color = Color.WHITE
            font.draw(batch, "START GAME", Config.WORLD_WIDTH / 2f - 75f, 270f)
        }
        batch.end()

        if (isSlotPopupOpen) {
            renderLocalSavePopup()
        }
    }

    private fun renderLocalSavePopup() {
        val cX = Config.WORLD_WIDTH / 2f
        val cY = Config.WORLD_HEIGHT / 2f
        val panelW = 390f
        val panelH = 500f
        val panelX = cX - panelW / 2f
        val panelY = cY - panelH / 2f

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color(0f, 0f, 0f, 0.65f)
        shapeRenderer.rect(0f, 0f, Config.WORLD_WIDTH, Config.WORLD_HEIGHT)

        // Khung gỗ chính
        shapeRenderer.color = Color(0.20f, 0.17f, 0.14f, 1f)
        shapeRenderer.rect(panelX, panelY, panelW, panelH)

        shapeRenderer.color = Color(0.27f, 0.22f, 0.18f, 1f)
        shapeRenderer.rect(panelX + 16f, panelY + 16f, panelW - 32f, panelH - 32f)

        // Nút tròn trợ giúp (?)
        shapeRenderer.color = Color(0.25f, 0.22f, 0.20f, 1f)
        shapeRenderer.circle(cX + 175f, cY + 235f, 18f)

        val startY = cY + 115f
        val cardW = 340f
        val cardH = 92f

        for (i in 1..3) {
            val cardY = startY - (i - 1) * 122f
            val cardX = cX - cardW / 2f
            val hasSave = SaveManager.hasData(i)

            if (hasSave) {
                shapeRenderer.color = Color(0.12f, 0.10f, 0.08f, 1f)
                shapeRenderer.rect(cardX, cardY - cardH / 2f, cardW, cardH)

                // Nút Xóa (X tròn)
                shapeRenderer.color = Color(0.70f, 0.72f, 0.75f, 1f)
                shapeRenderer.circle(cX + 130f, cardY + 26f, 11f)
                shapeRenderer.color = Color(0.18f, 0.16f, 0.14f, 1f)
                shapeRenderer.circle(cX + 130f, cardY + 26f, 8f)
            } else {
                shapeRenderer.color = Color(0.18f, 0.14f, 0.11f, 1f)
                shapeRenderer.rect(cardX, cardY - cardH / 2f, cardW, cardH)
                shapeRenderer.color = Color(0.25f, 0.20f, 0.16f, 1f)
                shapeRenderer.rect(cardX + 4f, cardY - cardH / 2f + 4f, cardW - 8f, cardH - 8f)
            }
        }
        shapeRenderer.end()

        // VIỀN ĐƯỜNG NÉT
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color(0.75f, 0.78f, 0.82f, 1f)
        shapeRenderer.rect(panelX, panelY, panelW, panelH)
        shapeRenderer.circle(cX + 175f, cY + 235f, 18f)

        for (i in 1..3) {
            val cardY = startY - (i - 1) * 122f
            val cardX = cX - cardW / 2f
            val hasSave = SaveManager.hasData(i)

            if (hasSave) {
                shapeRenderer.color = Color(0.95f, 0.75f, 0.10f, 1f)
                shapeRenderer.rect(cardX, cardY - cardH / 2f, cardW, cardH)
                shapeRenderer.rect(cardX + 2f, cardY - cardH / 2f + 2f, cardW - 4f, cardH - 4f)
            } else {
                shapeRenderer.color = Color(0.50f, 0.45f, 0.40f, 1f)
                shapeRenderer.rect(cardX, cardY - cardH / 2f, cardW, cardH)
            }
        }
        shapeRenderer.end()

        // VẼ 3 ICON VECTOR (NGÔI SAO, KHIÊN, CÂY BÚA/NẮM ĐẤM THÉP) - KHÔNG DÙNG EMOJI NỮA!
        for (i in 1..3) {
            val cardY = startY - (i - 1) * 122f
            val cardX = cX - cardW / 2f
            val hasSave = SaveManager.hasData(i)

            if (hasSave) {
                // 1. Ngôi sao vàng
                GameUIRenderer.drawStar(shapeRenderer, cardX + 26f, cardY - 14f, 9f, true)

                // 2. Chiếc khiên Heroic
                val shieldX = cardX + 115f
                val shieldY = cardY - 14f
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
                shapeRenderer.color = Color(0.65f, 0.68f, 0.72f, 1f)
                shapeRenderer.circle(shieldX, shieldY + 3f, 8.5f)
                shapeRenderer.triangle(shieldX - 8.5f, shieldY + 3f, shieldX + 8.5f, shieldY + 3f, shieldX, shieldY - 8.5f)
                shapeRenderer.color = Color(0.85f, 0.65f, 0.15f, 1f)
                shapeRenderer.circle(shieldX, shieldY + 3f, 5.5f)
                shapeRenderer.triangle(shieldX - 5.5f, shieldY + 3f, shieldX + 5.5f, shieldY + 3f, shieldX, shieldY - 5.5f)
                shapeRenderer.end()

                // 3. Cây búa sắt Iron
                val fistX = cardX + 205f
                val fistY = cardY - 14f
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
                shapeRenderer.color = Color(0.42f, 0.30f, 0.20f, 1f)
                shapeRenderer.rect(fistX - 2f, fistY - 9f, 4f, 16f)
                shapeRenderer.color = Color(0.70f, 0.74f, 0.78f, 1f)
                shapeRenderer.rect(fistX - 7f, fistY + 1f, 14f, 7f)
                shapeRenderer.end()
            }
        }

        // IN CHỮ
        batch.begin()
        titleFont.color = Color(0.98f, 0.95f, 0.88f, 1f)
        titleFont.draw(batch, "LOCAL SAVE", cX - 105f, cY + 242f)

        font.color = Color(0.9f, 0.85f, 0.7f, 1f)
        font.draw(batch, "?", cX + 170f, cY + 242f)

        for (i in 1..3) {
            val cardY = startY - (i - 1) * 122f
            val cardX = cX - cardW / 2f
            val hasSave = SaveManager.hasData(i)

            if (hasSave) {
                val (campStars, chalStars, totalStars) = SaveManager.getSlotStats(i)

                italicFont.color = Color(0.98f, 0.95f, 0.88f, 1f)
                italicFont.draw(batch, "Slot $i", cardX + 16f, cardY + 34f)

                font.color = Color(0.75f, 0.78f, 0.82f, 1f)
                font.draw(batch, "x", cX + 125f, cardY + 31f)

                // Số lượng cạnh các icon vector
                font.color = Color.GOLD
                font.draw(batch, "$totalStars/62", cardX + 40f, cardY - 7f)

                font.color = Color.WHITE
                font.draw(batch, "$campStars", cardX + 130f, cardY - 7f)
                font.draw(batch, "$chalStars", cardX + 220f, cardY - 7f)
            } else {
                font.color = Color(0.96f, 0.92f, 0.82f, 1f)
                font.draw(batch, "NEW GAME", cX - 60f, cardY + 8f)
            }
        }
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
        italicFont.dispose()
        smallFont.dispose()
    }
}
