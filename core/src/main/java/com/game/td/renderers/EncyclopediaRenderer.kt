package com.game.td.renderers

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector2
import com.game.td.Config
import com.game.td.ecs.components.*
import com.game.td.managers.EncyclopediaManager
import com.game.td.managers.EncyclopediaTowerId
import com.game.td.managers.SoundManager

enum class EncyclopediaTab {
    HOME,
    TOWERS,
    ENEMIES
}

object EncyclopediaRenderer {

    var currentTab: EncyclopediaTab = EncyclopediaTab.HOME
    var selectedTower: EncyclopediaTowerId = EncyclopediaTowerId.ARCHER_1
    var selectedEnemy: EnemyType = EnemyType.GOBLIN

    private val oldMatrix = Matrix4()
    private val tempMatrix = Matrix4()

    fun open() {
        currentTab = EncyclopediaTab.HOME
        selectedTower = EncyclopediaTowerId.ARCHER_1
        selectedEnemy = EnemyType.GOBLIN
    }

    fun handleTouch(x: Float, y: Float): Boolean {
        val boardX = 90f
        val boardY = 40f
        val boardW = 1100f
        val boardH = 630f

        val closeBtnX = boardX + boardW - 155f
        val closeBtnY = boardY + 24f
        if (x in closeBtnX..(closeBtnX + 125f) && y in closeBtnY..(closeBtnY + 46f)) {
            SoundManager.playClick()
            return false
        }

        if (x !in boardX..(boardX + boardW) || y !in boardY..(boardY + boardH)) {
            SoundManager.playClick()
            return false
        }

        if (currentTab != EncyclopediaTab.HOME) {
            val backX = boardX + boardXOffset()
            val backY = boardY + boardH - 92f
            if (x in backX..(backX + 95f) && y in backY..(backY + 55f)) {
                SoundManager.playClick()
                currentTab = EncyclopediaTab.HOME
                return true
            }
        }

        when (currentTab) {
            EncyclopediaTab.HOME -> {
                if (x in 310f..610f && y in 140f..485f) {
                    SoundManager.playTowerPreview()
                    currentTab = EncyclopediaTab.TOWERS
                    selectedTower = EncyclopediaTowerId.ARCHER_1
                    return true
                }
                if (x in 670f..970f && y in 140f..485f) {
                    SoundManager.playTowerPreview()
                    currentTab = EncyclopediaTab.ENEMIES
                    selectedEnemy = EnemyType.GOBLIN
                    return true
                }
            }

            EncyclopediaTab.TOWERS -> {
                val gridStartX = 205f
                val basicTopY = 455f
                val cellW = 68f
                val cellH = 66f
                val boxSize = 54f

                for (i in EncyclopediaManager.basicTowers.indices) {
                    val col = i % 4
                    val row = i / 4
                    val cx = gridStartX + col * cellW
                    val cy = basicTopY - row * cellH
                    if (x in cx..(cx + boxSize) && y in cy..(cy + boxSize)) {
                        val tId = EncyclopediaManager.basicTowers[i]
                        if (EncyclopediaManager.isTowerUnlocked(tId)) {
                            SoundManager.playTowerPreview()
                            selectedTower = tId
                        } else {
                            SoundManager.playClick()
                        }
                        return true
                    }
                }

                val advTopY = 175f
                for (i in EncyclopediaManager.advancedTowers.indices) {
                    val col = i % 4
                    val row = i / 4
                    val cx = gridStartX + col * cellW
                    val cy = advTopY - row * cellH
                    if (x in cx..(cx + boxSize) && y in cy..(cy + boxSize)) {
                        val tId = EncyclopediaManager.advancedTowers[i]
                        if (EncyclopediaManager.isTowerUnlocked(tId)) {
                            SoundManager.playTowerPreview()
                            selectedTower = tId
                        } else {
                            SoundManager.playClick()
                        }
                        return true
                    }
                }
            }

            EncyclopediaTab.ENEMIES -> {
                val gridStartX = 165f
                val gridTopY = 462f
                val cellW = 62f
                val cellH = 62f
                val boxSize = 50f

                val enemies = EncyclopediaManager.allEnemiesOrdered
                for (i in enemies.indices) {
                    val col = i % 6
                    val row = i / 6
                    val cx = gridStartX + col * cellW
                    val cy = gridTopY - row * cellH
                    if (x in cx..(cx + boxSize) && y in cy..(cy + boxSize)) {
                        val eType = enemies[i]
                        if (EncyclopediaManager.isEnemyUnlocked(eType)) {
                            SoundManager.playTowerPreview()
                            selectedEnemy = eType
                        } else {
                            SoundManager.playClick()
                        }
                        return true
                    }
                }
            }
        }

        return true
    }

    private fun boardXOffset() = 42f

    fun render(
        shapeRenderer: ShapeRenderer,
        batch: SpriteBatch,
        titleFont: BitmapFont,
        font: BitmapFont,
        smallFont: BitmapFont
    ) {
        val boardX = 90f
        val boardY = 40f
        val boardW = 1100f
        val boardH = 630f

        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

        drawWoodenBoardFrame(shapeRenderer, boardX, boardY, boardW, boardH)

        when (currentTab) {
            EncyclopediaTab.HOME -> renderHomeTab(shapeRenderer, batch, titleFont, font, boardX, boardY, boardW, boardH)
            EncyclopediaTab.TOWERS -> renderTowersTab(shapeRenderer, batch, titleFont, font, smallFont, boardX, boardY, boardW, boardH)
            EncyclopediaTab.ENEMIES -> renderEnemiesTab(shapeRenderer, batch, titleFont, font, smallFont, boardX, boardY, boardW, boardH)
        }
    }

    private fun drawWoodenBoardFrame(
        sr: ShapeRenderer,
        x: Float,
        y: Float,
        w: Float,
        h: Float
    ) {
        sr.begin(ShapeRenderer.ShapeType.Filled)
        sr.color = Color(0f, 0f, 0f, 0.55f)
        sr.rect(0f, 0f, Config.WORLD_WIDTH, Config.WORLD_HEIGHT)

        for (chainX in listOf(x + 65f, x + w - 75f)) {
            sr.color = Color(0.25f, 0.26f, 0.28f, 1f)
            sr.rect(chainX, y + h - 5f, 12f, 55f)
            sr.color = Color(0.58f, 0.60f, 0.65f, 1f)
            for (cy in (y + h).toInt()..715 step 14) {
                sr.rect(chainX + 2f, cy.toFloat(), 8f, 9f)
            }
        }

        sr.color = Color(0.55f, 0.54f, 0.50f, 1f)
        sr.rect(x - 6f, y - 6f, w + 12f, h + 12f)
        sr.color = Color(0.22f, 0.18f, 0.14f, 1f)
        sr.rect(x - 2f, y - 2f, w + 4f, h + 4f)

        sr.color = Color(0.28f, 0.22f, 0.16f, 1f)
        sr.rect(x, y, w, h)

        sr.color = Color(0.24f, 0.18f, 0.13f, 1f)
        for (gy in (y + 35f).toInt() until (y + h - 25f).toInt() step 42) {
            sr.rect(x + 18f, gy.toFloat(), w - 36f, 4f)
            sr.rect(x + 60f, gy + 18f, w - 140f, 2.5f)
        }

        val corners = listOf(
            Vector2(x - 4f, y + h - 22f),
            Vector2(x + w - 22f, y + h - 22f),
            Vector2(x - 4f, y - 4f),
            Vector2(x + w - 22f, y - 4f)
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

        val cx = x + w / 2f
        val topY = y + h
        sr.color = Color(0.72f, 0.56f, 0.22f, 1f)
        sr.rect(cx - 235f, topY - 46f, 65f, 44f)
        sr.rect(cx + 170f, topY - 46f, 65f, 44f)
        sr.color = Color(0.28f, 0.22f, 0.16f, 1f)
        sr.triangle(cx - 235f, topY - 24f, cx - 235f, topY - 46f, cx - 215f, topY - 35f)
        sr.triangle(cx + 235f, topY - 24f, cx + 235f, topY - 46f, cx + 215f, topY - 35f)

        sr.color = Color(0.52f, 0.38f, 0.12f, 1f)
        sr.rect(cx - 184f, topY - 36f, 368f, 64f)
        sr.color = Color(0.92f, 0.76f, 0.36f, 1f)
        sr.rect(cx - 180f, topY - 32f, 360f, 56f)
        sr.color = Color(0.98f, 0.85f, 0.48f, 1f)
        sr.rect(cx - 174f, topY - 26f, 348f, 44f)

        val closeX = x + w - 155f
        val closeY = y + 24f
        sr.color = Color(0.52f, 0.48f, 0.40f, 1f)
        sr.rect(closeX - 3f, closeY - 3f, 131f, 52f)
        sr.color = Color(0.28f, 0.24f, 0.18f, 1f)
        sr.rect(closeX, closeY, 125f, 46f)
        sr.color = Color(0.36f, 0.31f, 0.24f, 1f)
        sr.rect(closeX + 4f, closeY + 4f, 117f, 38f)

        if (currentTab != EncyclopediaTab.HOME) {
            val backX = x + 48f
            val backY = y + h - 82f
            sr.color = Color(0.68f, 0.66f, 0.60f, 1f)
            sr.rect(backX + 18f, backY + 18f, 34f, 18f)
            sr.triangle(backX, backY + 27f, backX + 22f, backY + 42f, backX + 22f, backY + 12f)
        }
        sr.end()
    }

    private fun renderHomeTab(
        sr: ShapeRenderer,
        batch: SpriteBatch,
        titleFont: BitmapFont,
        font: BitmapFont,
        boardX: Float,
        boardY: Float,
        boardW: Float,
        boardH: Float
    ) {
        val cx = boardX + boardW / 2f
        val topY = boardY + boardH

        val leftCardX = 460f
        val rightCardX = 820f
        val cardY = 325f

        sr.begin(ShapeRenderer.ShapeType.Filled)
        sr.color = Color(0.21f, 0.16f, 0.12f, 1f)
        sr.rect(leftCardX - 145f, cardY - 175f, 290f, 340f)
        sr.rect(rightCardX - 145f, cardY - 175f, 290f, 340f)

        for (cardCx in listOf(leftCardX, rightCardX)) {
            sr.color = Color(0.65f, 0.66f, 0.68f, 1f)
            sr.circle(cardCx, cardY + 10f, 108f)
            sr.color = Color(0.16f, 0.13f, 0.10f, 1f)
            sr.circle(cardCx, cardY + 10f, 96f)
            sr.color = Color(0.26f, 0.21f, 0.16f, 1f)
            sr.circle(cardCx, cardY + 10f, 88f)
        }

        val kx = leftCardX
        val ky = cardY + 12f
        sr.color = Color(0.32f, 0.36f, 0.40f, 1f)
        sr.rect(kx - 36f, ky - 65f, 72f, 65f)
        sr.color = Color(0.75f, 0.55f, 0.20f, 1f)
        sr.rectLine(kx - 35f, ky + 12f, kx - 52f, ky + 22f, 10f)
        sr.color = Color(0.85f, 0.92f, 0.98f, 1f)
        sr.rectLine(kx - 50f, ky + 20f, kx - 115f, ky + 48f, 16f)
        sr.triangle(kx - 115f, ky + 56f, kx - 115f, ky + 40f, kx - 132f, ky + 52f)
        sr.color = Color(0.42f, 0.46f, 0.52f, 1f)
        sr.circle(kx - 4f, ky + 24f, 44f)
        sr.color = Color(0.88f, 0.72f, 0.48f, 1f)
        sr.rect(kx - 28f, ky + 4f, 48f, 18f)
        sr.color = Color(0.58f, 0.64f, 0.70f, 1f)
        sr.circle(kx + 8f, ky + 36f, 14f)
        sr.color = Color(0.28f, 0.32f, 0.36f, 1f)
        sr.circle(kx + 42f, ky - 22f, 46f)
        sr.color = Color(0.46f, 0.50f, 0.56f, 1f)
        sr.circle(kx + 42f, ky - 22f, 38f)
        sr.color = Color(0.72f, 0.76f, 0.80f, 1f)
        sr.circle(kx + 42f, ky - 22f, 12f)

        val gx = rightCardX
        val gy = cardY + 12f
        sr.color = Color(0.32f, 0.58f, 0.16f, 1f)
        sr.rect(gx - 32f, gy - 65f, 64f, 62f)
        sr.color = Color(0.48f, 0.32f, 0.16f, 1f)
        sr.rectLine(gx + 52f, gy - 35f, gx + 68f, gy + 58f, 22f)
        sr.color = Color(0.62f, 0.42f, 0.22f, 1f)
        sr.circle(gx + 68f, gy + 58f, 13f)
        sr.color = Color(0.38f, 0.68f, 0.18f, 1f)
        sr.triangle(gx - 38f, gy + 28f, gx - 38f, gy + 8f, gx - 88f, gy + 45f)
        sr.triangle(gx + 28f, gy + 28f, gx + 28f, gy + 8f, gx + 78f, gy + 52f)
        sr.circle(gx - 5f, gy + 22f, 42f)
        sr.color = Color(0.98f, 0.88f, 0.15f, 1f)
        sr.circle(gx - 18f, gy + 26f, 7f)
        sr.circle(gx + 8f, gy + 26f, 7f)
        sr.color = Color.WHITE
        sr.triangle(gx - 22f, gy - 2f, gx - 12f, gy - 2f, gx - 17f, gy - 12f)
        sr.triangle(gx + 2f, gy - 2f, gx + 12f, gy - 2f, gx + 7f, gy - 12f)
        sr.end()

        batch.begin()
        val oldScaleX = titleFont.data.scaleX
        val oldScaleY = titleFont.data.scaleY
        titleFont.data.setScale(2.0f)

        GameUIRenderer.drawOutlinedText(batch, titleFont, "ENCYCLOPEDIA", cx - 115f, topY + 6f, Color(0.28f, 0.18f, 0.08f, 1f))

        titleFont.data.setScale(2.3f)
        GameUIRenderer.drawOutlinedText(batch, titleFont, "TOWERS", leftCardX - 78f, cardY - 102f, Color(1f, 0.86f, 0.12f, 1f))
        GameUIRenderer.drawOutlinedText(batch, titleFont, "ENEMIES", rightCardX - 86f, cardY - 102f, Color(1f, 0.86f, 0.12f, 1f))

        titleFont.data.setScale(oldScaleX, oldScaleY)

        val closeX = boardX + boardW - 155f
        val closeY = boardY + 24f
        GameUIRenderer.drawBadgeCenteredText(batch, font, "CLOSE", closeX, closeY, 125f, 46f, Color.WHITE)
        batch.end()
    }

    private fun renderTowersTab(
        sr: ShapeRenderer,
        batch: SpriteBatch,
        titleFont: BitmapFont,
        font: BitmapFont,
        smallFont: BitmapFont,
        boardX: Float,
        boardY: Float,
        boardW: Float,
        boardH: Float
    ) {
        val cx = boardX + boardW / 2f
        val topY = boardY + boardH

        val gridStartX = 205f
        val basicTopY = 455f
        val advTopY = 175f
        val cellW = 68f
        val cellH = 66f
        val boxSize = 54f

        val previewX = 680f
        val previewY = 345f
        val previewW = 280f
        val previewH = 195f
        val isBarracksSelected = (selectedTower.towerType == TowerType.BARRACKS)

        sr.begin(ShapeRenderer.ShapeType.Filled)
        for (i in EncyclopediaManager.basicTowers.indices) {
            val col = i % 4
            val row = i / 4
            val bx = gridStartX + col * cellW
            val by = basicTopY - row * cellH
            val tId = EncyclopediaManager.basicTowers[i]
            val unlocked = EncyclopediaManager.isTowerUnlocked(tId)
            val isSel = (selectedTower == tId)
            drawEncyclopediaCell(sr, bx, by, boxSize, unlocked, isSel, isGrass = true)
        }

        for (i in EncyclopediaManager.advancedTowers.indices) {
            val col = i % 4
            val row = i / 4
            val bx = gridStartX + col * cellW
            val by = advTopY - row * cellH
            val tId = EncyclopediaManager.advancedTowers[i]
            val unlocked = EncyclopediaManager.isTowerUnlocked(tId)
            val isSel = (selectedTower == tId)
            drawEncyclopediaCell(sr, bx, by, boxSize, unlocked, isSel, isGrass = true)
        }

        sr.color = Color(0.86f, 0.78f, 0.56f, 1f)
        sr.rect(previewX - 6f, previewY - 6f, previewW + 12f, previewH + 12f)
        sr.color = Color(0.34f, 0.58f, 0.20f, 1f)
        sr.rect(previewX, previewY, previewW, previewH)

        // Nếu là tháp Barracks -> Vẽ thêm con đường đất phía trước trại lính để 3 người lính đứng dàn hàng như ảnh gốc!
        if (isBarracksSelected) {
            sr.color = Color(0.84f, 0.74f, 0.46f, 1f)
            sr.rect(previewX, previewY, previewW, 64f)
            sr.color = Color(0.26f, 0.48f, 0.14f, 1f)
            for (gx in 18..265 step 36) {
                sr.circle(previewX + gx, previewY + 66f, 14f)
            }
        }

        val towerCenterY = if (isBarracksSelected) previewY + previewH / 2f + 16f else previewY + previewH / 2f - 14f

        sr.color = Color(0.24f, 0.48f, 0.14f, 1f)
        sr.circle(previewX + 45f, previewY + 155f, 36f)
        sr.circle(previewX + 235f, previewY + 158f, 40f)
        sr.color = Color(0.48f, 0.45f, 0.38f, 1f)
        sr.circle(previewX + previewW / 2f, towerCenterY, 50f)
        sr.color = Color(0.62f, 0.58f, 0.48f, 1f)
        sr.circle(previewX + previewW / 2f, towerCenterY, 40f)

        sr.color = Color(0.20f, 0.15f, 0.11f, 0.92f)
        sr.rect(previewX, 200f, 280f, 26f)
        sr.rect(previewX, 165f, 135f, 26f)
        sr.rect(previewX + 145f, 165f, 135f, 26f)

        sr.color = Color(0.85f, 0.88f, 0.92f, 1f)
        sr.rect(previewX + 8f, 206f, 5f, 14f)
        sr.color = Color.GOLD
        sr.rect(previewX + 5f, 206f, 11f, 4f)

        sr.color = Color(0.90f, 0.75f, 0.30f, 1f)
        sr.triangle(previewX + 6f, 186f, previewX + 16f, 186f, previewX + 11f, 178f)
        sr.triangle(previewX + 6f, 170f, previewX + 16f, 170f, previewX + 11f, 178f)

        sr.color = Color(0.90f, 0.30f, 0.18f, 1f)
        sr.circle(previewX + 156f, 178f, 6.5f)
        sr.color = Color.WHITE
        sr.circle(previewX + 156f, 178f, 3.2f)
        sr.end()

        for (i in EncyclopediaManager.basicTowers.indices) {
            val tId = EncyclopediaManager.basicTowers[i]
            if (EncyclopediaManager.isTowerUnlocked(tId)) {
                val col = i % 4
                val row = i / 4
                val bx = gridStartX + col * cellW + boxSize / 2f
                val by = basicTopY - row * cellH + boxSize / 2f - 3f
                drawScaledTower(sr, tId, bx, by, 0.80f)
            }
        }

        for (i in EncyclopediaManager.advancedTowers.indices) {
            val tId = EncyclopediaManager.advancedTowers[i]
            if (EncyclopediaManager.isTowerUnlocked(tId)) {
                val col = i % 4
                val row = i / 4
                val bx = gridStartX + col * cellW + boxSize / 2f
                val by = advTopY - row * cellH + boxSize / 2f - 3f
                drawScaledTower(sr, tId, bx, by, 0.80f)
            }
        }

        // Vẽ tòa tháp trong khung tranh lớn
        drawScaledTower(
            sr,
            selectedTower,
            previewX + previewW / 2f,
            if (isBarracksSelected) towerCenterY + 4f else previewY + previewH / 2f - 10f,
            if (isBarracksSelected) 1.75f else 2.0f
        )

        // Nếu đang chọn tháp Barracks -> Vẽ thêm 3 người lính đứng dàn hàng trước cửa trại lính!
        if (isBarracksSelected) {
            val rankName = when (selectedTower) {
                EncyclopediaTowerId.BARRACKS_1 -> "Militia"
                EncyclopediaTowerId.BARRACKS_2 -> "Footman"
                EncyclopediaTowerId.BARRACKS_3 -> "Knight"
                EncyclopediaTowerId.HOLY_ORDER -> "Paladin"
                EncyclopediaTowerId.BARBARIAN_MEAD_HALL -> "Barbarian"
                else -> "Militia"
            }
            val soldierPositions = listOf(
                Vector2(previewX + 90f, previewY + 38f),
                Vector2(previewX + 140f, previewY + 22f),
                Vector2(previewX + 192f, previewY + 40f)
            )
            for (sp in soldierPositions) {
                drawScaledSoldier(sr, rankName, sp.x, sp.y, 1.45f)
            }
        }

        batch.begin()
        val oldScaleX = titleFont.data.scaleX
        val oldScaleY = titleFont.data.scaleY
        titleFont.data.setScale(2.0f)
        GameUIRenderer.drawOutlinedText(batch, titleFont, "TOWERS", cx - 72f, topY + 6f, Color(0.28f, 0.18f, 0.08f, 1f))
        titleFont.data.setScale(oldScaleX, oldScaleY)

        GameUIRenderer.drawOutlinedText(batch, font, "BACK", boardX + 46f, topY - 68f, Color.LIGHT_GRAY)
        GameUIRenderer.drawOutlinedText(batch, smallFont, "BASIC TOWERS", gridStartX + 68f, basicTopY + 74f, Color(0.95f, 0.85f, 0.55f, 1f))
        GameUIRenderer.drawOutlinedText(batch, smallFont, "ADVANCED TOWERS", gridStartX + 52f, advTopY + 74f, Color(0.95f, 0.85f, 0.55f, 1f))

        GameUIRenderer.drawOutlinedText(batch, font, selectedTower.title, previewX, 322f, Color(0.98f, 0.94f, 0.76f, 1f))

        val oldSmallScaleX = smallFont.data.scaleX
        val oldSmallScaleY = smallFont.data.scaleY
        smallFont.data.setScale(0.92f)
        val descLines = wrapLines(selectedTower.description, 34)
        var descY = 294f
        for (line in descLines) {
            GameUIRenderer.drawShadowedText(batch, smallFont, line.uppercase(), previewX, descY, Color(0.88f, 0.84f, 0.74f, 1f))
            descY -= 16f
        }

        GameUIRenderer.drawOutlinedText(batch, smallFont, selectedTower.damageText, previewX + 24f, 218f, Color.WHITE)
        GameUIRenderer.drawOutlinedText(batch, smallFont, selectedTower.rateOrArmorLabel, previewX + 24f, 183f, Color.WHITE)
        GameUIRenderer.drawOutlinedText(batch, smallFont, selectedTower.rangeOrRespawnLabel, previewX + 170f, 183f, Color.WHITE)

        GameUIRenderer.drawShadowedText(batch, smallFont, "SPECIALS", previewX, 145f, Color(0.96f, 0.82f, 0.35f, 1f))
        val specLines = wrapLines(selectedTower.specialsText, 34)
        var specY = 126f
        for (sLine in specLines) {
            GameUIRenderer.drawShadowedText(batch, smallFont, sLine.uppercase(), previewX, specY, Color(0.85f, 0.82f, 0.75f, 1f))
            specY -= 15f
        }
        smallFont.data.setScale(oldSmallScaleX, oldSmallScaleY)

        val closeX = boardX + boardW - 155f
        val closeY = boardY + 24f
        GameUIRenderer.drawBadgeCenteredText(batch, font, "CLOSE", closeX, closeY, 125f, 46f, Color.WHITE)
        batch.end()
    }

    private fun renderEnemiesTab(
        sr: ShapeRenderer,
        batch: SpriteBatch,
        titleFont: BitmapFont,
        font: BitmapFont,
        smallFont: BitmapFont,
        boardX: Float,
        boardY: Float,
        boardW: Float,
        boardH: Float
    ) {
        val cx = boardX + boardW / 2f
        val topY = boardY + boardH

        val gridStartX = 165f
        val gridTopY = 462f
        val cellW = 62f
        val cellH = 62f
        val boxSize = 50f

        val enemies = EncyclopediaManager.allEnemiesOrdered

        sr.begin(ShapeRenderer.ShapeType.Filled)
        for (i in enemies.indices) {
            val col = i % 6
            val row = i / 6
            val bx = gridStartX + col * cellW
            val by = gridTopY - row * cellH
            val eType = enemies[i]
            val unlocked = EncyclopediaManager.isEnemyUnlocked(eType)
            val isSel = (selectedEnemy == eType)
            val isSnow = eType in listOf(
                EnemyType.TROLL, EnemyType.TROLL_CHAMPION, EnemyType.TROLL_CHIEFTAIN,
                EnemyType.WINTER_WOLF, EnemyType.YETI, EnemyType.ROCKET_RIDER, EnemyType.JT
            )
            drawEncyclopediaCell(sr, bx, by, boxSize, unlocked, isSel, isGrass = !isSnow)
        }

        val previewX = 680f
        val previewY = 350f
        val previewW = 280f
        val previewH = 190f

        sr.color = Color(0.86f, 0.78f, 0.56f, 1f)
        sr.rect(previewX - 6f, previewY - 6f, previewW + 12f, previewH + 12f)

        val isSnowEnemy = selectedEnemy in listOf(
            EnemyType.TROLL, EnemyType.TROLL_CHAMPION, EnemyType.TROLL_CHIEFTAIN,
            EnemyType.WINTER_WOLF, EnemyType.YETI, EnemyType.ROCKET_RIDER, EnemyType.JT
        )
        val isWastesEnemy = selectedEnemy in listOf(
            EnemyType.SKELETON, EnemyType.SKELETON_KNIGHT, EnemyType.NECROMANCER,
            EnemyType.DEMON_SPAWN, EnemyType.DEMON_HOUND, EnemyType.DEMON_IMP,
            EnemyType.DEMON_LORD, EnemyType.MAGMA_ELEMENTAL, EnemyType.VEZNAN, EnemyType.VEZNAN_DEMON
        )

        sr.color = when {
            isSnowEnemy -> Color(0.85f, 0.90f, 0.95f, 1f)
            isWastesEnemy -> Color(0.28f, 0.26f, 0.30f, 1f)
            else -> Color(0.34f, 0.56f, 0.18f, 1f)
        }
        sr.rect(previewX, previewY, previewW, previewH)

        sr.color = when {
            isSnowEnemy -> Color(0.74f, 0.78f, 0.84f, 1f)
            isWastesEnemy -> Color(0.45f, 0.44f, 0.48f, 1f)
            else -> Color(0.82f, 0.72f, 0.46f, 1f)
        }
        sr.rect(previewX, previewY + 18f, previewW, 108f)

        if (!isSnowEnemy && !isWastesEnemy) {
            sr.color = Color(0.26f, 0.46f, 0.14f, 1f)
            for (gx in 15..265 step 35) {
                sr.circle(previewX + gx, previewY + 132f, 18f)
                sr.circle(previewX + gx, previewY + 12f, 14f)
            }
        }

        val statY1 = 198f
        val statY2 = 165f
        val statY3 = 132f
        val col1X = previewX
        val col2X = previewX + 145f

        sr.color = Color(0.20f, 0.15f, 0.11f, 0.92f)
        for (sy in listOf(statY1, statY2, statY3)) {
            sr.rect(col1X, sy, 135f, 25f)
            sr.rect(col2X, sy, 135f, 25f)
        }

        sr.color = Color(0.90f, 0.18f, 0.18f, 1f)
        sr.circle(col1X + 11f, statY1 + 13f, 6f)
        sr.color = Color(0.88f, 0.90f, 0.95f, 1f)
        sr.rect(col2X + 9f, statY1 + 6f, 4f, 14f)
        sr.color = Color.GOLD
        sr.rect(col2X + 6f, statY1 + 7f, 10f, 3.5f)
        sr.color = Color(0.75f, 0.78f, 0.84f, 1f)
        sr.rect(col1X + 6f, statY2 + 6f, 10f, 13f)
        sr.color = Color(0.25f, 0.78f, 0.98f, 1f)
        sr.rect(col2X + 6f, statY2 + 6f, 10f, 13f)
        sr.color = Color(0.95f, 0.78f, 0.25f, 1f)
        sr.rect(col1X + 5f, statY3 + 6f, 12f, 9f)
        sr.color = Color(0.90f, 0.90f, 0.92f, 1f)
        sr.circle(col2X + 11f, statY3 + 13f, 6f)
        sr.end()

        for (i in enemies.indices) {
            val eType = enemies[i]
            if (EncyclopediaManager.isEnemyUnlocked(eType)) {
                val col = i % 6
                val row = i / 6
                val bx = gridStartX + col * cellW + boxSize / 2f
                val by = gridTopY - row * cellH + boxSize / 2f - 2f
                drawScaledEnemy(sr, eType, bx, by, getEnemyVisualScale(eType) * 1.18f)
            }
        }

        val isSwarm = selectedEnemy in listOf(
            EnemyType.GOBLIN, EnemyType.ORC, EnemyType.WULF, EnemyType.SPIDER_HATCHLING,
            EnemyType.SKELETON, EnemyType.BANDIT, EnemyType.BRIGAND
        )
        if (isSwarm) {
            val swarmPositions = listOf(
                Vector2(previewX + 65f, previewY + 86f),
                Vector2(previewX + 130f, previewY + 98f),
                Vector2(previewX + 98f, previewY + 50f),
                Vector2(previewX + 172f, previewY + 46f),
                Vector2(previewX + 222f, previewY + 78f)
            )
            val sScale = getEnemyVisualScale(selectedEnemy) * 1.65f
            for (sp in swarmPositions) {
                drawScaledEnemy(sr, selectedEnemy, sp.x, sp.y, sScale)
            }
        } else {
            val bigScale = getEnemyVisualScale(selectedEnemy) * 2.40f
            drawScaledEnemy(sr, selectedEnemy, previewX + previewW / 2f, previewY + 68f, bigScale)
        }

        val info = EncyclopediaManager.getEnemyInfo(selectedEnemy)
        batch.begin()
        val oldScaleX = titleFont.data.scaleX
        val oldScaleY = titleFont.data.scaleY
        titleFont.data.setScale(2.0f)
        GameUIRenderer.drawOutlinedText(batch, titleFont, "ENEMIES", cx - 78f, topY + 6f, Color(0.28f, 0.18f, 0.08f, 1f))
        titleFont.data.setScale(oldScaleX, oldScaleY)

        GameUIRenderer.drawOutlinedText(batch, font, "BACK", boardX + 46f, topY - 68f, Color.LIGHT_GRAY)
        GameUIRenderer.drawOutlinedText(batch, smallFont, "ENEMIES", gridStartX + 138f, gridTopY + 68f, Color(0.95f, 0.85f, 0.55f, 1f))

        GameUIRenderer.drawOutlinedText(batch, font, selectedEnemy.title.uppercase(), previewX, 326f, Color(0.98f, 0.94f, 0.76f, 1f))

        val oldSmallScaleX = smallFont.data.scaleX
        val oldSmallScaleY = smallFont.data.scaleY
        smallFont.data.setScale(0.90f)
        val descLines = wrapLines("${info.description} (${info.specials})", 35)
        var descY = 298f
        for (line in descLines.take(4)) {
            GameUIRenderer.drawShadowedText(batch, smallFont, line.uppercase(), previewX, descY, Color(0.88f, 0.84f, 0.74f, 1f))
            descY -= 15.5f
        }

        val atkStr = if (selectedEnemy.damageMax <= 0f) "NONE" else "${selectedEnemy.damageMin.toInt()}-${selectedEnemy.damageMax.toInt()}"
        val lifeStr = if (selectedEnemy.livesTaken == 1) "1 LIFE" else "${selectedEnemy.livesTaken} LIVES"

        GameUIRenderer.drawOutlinedText(batch, smallFont, "${selectedEnemy.hpNormal.toInt()}", col1X + 24f, statY1 + 17f, Color.WHITE)
        GameUIRenderer.drawOutlinedText(batch, smallFont, atkStr, col2X + 24f, statY1 + 17f, Color.WHITE)
        GameUIRenderer.drawOutlinedText(batch, smallFont, info.armorLabel, col1X + 24f, statY2 + 17f, Color.WHITE)
        GameUIRenderer.drawOutlinedText(batch, smallFont, info.magicResistLabel, col2X + 24f, statY2 + 17f, Color.WHITE)
        GameUIRenderer.drawOutlinedText(batch, smallFont, info.speedLabel, col1X + 24f, statY3 + 17f, Color.WHITE)
        GameUIRenderer.drawOutlinedText(batch, smallFont, lifeStr, col2X + 24f, statY3 + 17f, Color.WHITE)

        smallFont.data.setScale(oldSmallScaleX, oldSmallScaleY)

        val closeX = boardX + boardW - 155f
        val closeY = boardY + 24f
        GameUIRenderer.drawBadgeCenteredText(batch, font, "CLOSE", closeX, closeY, 125f, 46f, Color.WHITE)
        batch.end()
    }

    private fun drawEncyclopediaCell(
        sr: ShapeRenderer,
        x: Float,
        y: Float,
        size: Float,
        isUnlocked: Boolean,
        isSelected: Boolean,
        isGrass: Boolean
    ) {
        sr.color = if (isSelected && isUnlocked) Color(1f, 0.92f, 0.15f, 1f) else Color(0.76f, 0.68f, 0.46f, 1f)
        val border = if (isSelected && isUnlocked) 3.5f else 2.2f
        sr.rect(x - border, y - border, size + border * 2f, size + border * 2f)

        if (isUnlocked) {
            sr.color = if (isGrass) Color(0.42f, 0.62f, 0.20f, 1f) else Color(0.84f, 0.90f, 0.96f, 1f)
            sr.rect(x, y, size, size)
            sr.color = if (isGrass) Color(0.30f, 0.48f, 0.14f, 1f) else Color(0.70f, 0.78f, 0.86f, 1f)
            sr.rect(x, y + size - 12f, size, 12f)
            sr.circle(x + 12f, y + size - 12f, 7f)
            sr.circle(x + size - 12f, y + size - 12f, 8f)
        } else {
            sr.color = Color(0.46f, 0.38f, 0.24f, 1f)
            sr.rect(x, y, size, size)

            val lcx = x + size / 2f
            val lcy = y + size / 2f - 3f
            sr.color = Color(0.84f, 0.76f, 0.52f, 1f)
            sr.circle(lcx, lcy + 8f, 7.5f)
            sr.color = Color(0.46f, 0.38f, 0.24f, 1f)
            sr.circle(lcx, lcy + 8f, 4.5f)
            sr.color = Color(0.84f, 0.76f, 0.52f, 1f)
            sr.rect(lcx - 9.5f, lcy - 9f, 19f, 14f)
        }
    }

    private fun getEnemyVisualScale(type: EnemyType): Float = when (type) {
        EnemyType.VEZNAN_DEMON -> 0.52f
        EnemyType.JT, EnemyType.JUGGERNAUT, EnemyType.SPIDER_MATRIARCH, EnemyType.VEZNAN -> 0.65f
        EnemyType.MAGMA_ELEMENTAL, EnemyType.YETI, EnemyType.DEMON_LORD, EnemyType.OGRE -> 0.78f
        EnemyType.MARAUDER, EnemyType.TROLL_CHIEFTAIN, EnemyType.DARK_SLAYER, EnemyType.GIANT_SPIDER, EnemyType.GARGOYLE, EnemyType.ROCKET_RIDER -> 0.90f
        EnemyType.GOBLIN, EnemyType.SPIDER_HATCHLING -> 1.25f
        else -> 1.05f
    }

    private fun drawScaledEnemy(
        sr: ShapeRenderer,
        type: EnemyType,
        cx: Float,
        cy: Float,
        scale: Float
    ) {
        val adjustedScale = when (type) {
            EnemyType.JUGGERNAUT, EnemyType.JT, EnemyType.VEZNAN_DEMON -> scale * 0.70f
            EnemyType.VEZNAN, EnemyType.YETI, EnemyType.MAGMA_ELEMENTAL, EnemyType.SPIDER_MATRIARCH -> scale * 0.80f
            EnemyType.OGRE, EnemyType.DEMON_LORD, EnemyType.TROLL_CHIEFTAIN -> scale * 0.88f
            else -> scale
        }

        oldMatrix.set(sr.transformMatrix)
        tempMatrix.idt().translate(cx, cy, 0f).scale(adjustedScale, adjustedScale, 1f)
        sr.transformMatrix = tempMatrix

        val dummyEnemy = Entity().apply {
            add(TransformComponent(position = Vector2(0f, 0f)))
            add(HealthComponent(maxHealth = type.hpNormal, currentHealth = type.hpNormal))
            add(EnemyComponent(enemyType = type))
        }

        sr.begin(ShapeRenderer.ShapeType.Filled)
        EnemyRenderer.render(sr, dummyEnemy, showHpBar = false)
        sr.end()

        sr.transformMatrix = oldMatrix
    }

    private fun drawScaledTower(
        sr: ShapeRenderer,
        towerId: EncyclopediaTowerId,
        cx: Float,
        cy: Float,
        scale: Float
    ) {
        oldMatrix.set(sr.transformMatrix)
        tempMatrix.idt().translate(cx, cy, 0f).scale(scale, scale, 1f)
        sr.transformMatrix = tempMatrix

        val dummyTower = Entity().apply {
            add(TransformComponent(position = Vector2(0f, 0f)))
            add(TowerComponent(
                towerType = towerId.towerType,
                level = towerId.level,
                specialization = towerId.specialization
            ))
        }

        sr.begin(ShapeRenderer.ShapeType.Filled)
        TowerRenderer.render(sr, dummyTower)
        sr.end()

        sr.transformMatrix = oldMatrix
    }

    private fun drawScaledSoldier(
        sr: ShapeRenderer,
        rankName: String,
        cx: Float,
        cy: Float,
        scale: Float
    ) {
        oldMatrix.set(sr.transformMatrix)
        tempMatrix.idt().translate(cx, cy, 0f).scale(scale, scale, 1f)
        sr.transformMatrix = tempMatrix

        val dummySoldier = Entity().apply {
            add(TransformComponent(position = Vector2(0f, 0f)))
            add(HealthComponent(maxHealth = 100f, currentHealth = 100f))
            add(SoldierComponent(soldierRankName = rankName, targetPosition = Vector2(0f, 0f)))
        }

        sr.begin(ShapeRenderer.ShapeType.Filled)
        SoldierRenderer.render(sr, dummySoldier, showHpBar = false)
        sr.end()

        sr.transformMatrix = oldMatrix
    }

    private fun wrapLines(text: String, maxChars: Int): List<String> {
        val words = text.split(" ")
        val lines = mutableListOf<String>()
        var current = ""
        for (w in words) {
            if (current.isEmpty()) current = w
            else if (current.length + 1 + w.length <= maxChars) current += " $w"
            else {
                lines.add(current)
                current = w
            }
        }
        if (current.isNotEmpty()) lines.add(current)
        return lines
    }
}
