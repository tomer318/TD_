package com.game.td.renderers

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils

object ComicFontRenderer {

    // Tự động chuyển BitmapFont sang bộ lọc Linear và tọa độ thực để chữ "T", "1", "I" không bao giờ bị mất nét dọc
    fun prepareCrispFont(vararg fonts: BitmapFont) {
        for (f in fonts) {
            f.region.texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
            f.setUseIntegerPositions(false)
        }
    }

    // Vẽ chữ thông tin / số tiến trình (4/10, 7/100) với bóng đổ mảnh, không làm đen nét chữ "1" và "T"
    fun drawReadableText(
        batch: SpriteBatch,
        font: BitmapFont,
        text: String,
        x: Float,
        y: Float,
        color: Color
    ) {
        prepareCrispFont(font)
        font.color = Color(0f, 0f, 0f, 0.72f)
        font.draw(batch, text, x + 1.1f, y - 1.1f)
        font.color = color
        font.draw(batch, text, x, y)
    }

    // Vẽ chữ trên băng rôn vàng (Chữ nâu đậm + bóng kem vàng lệch phải dưới giống hệt chữ VICTORY)
    fun drawRibbonBannerText(
        sr: ShapeRenderer,
        text: String,
        centerX: Float,
        centerY: Float,
        charHeight: Float = 26f,
        fillColor: Color = Color(0.26f, 0.16f, 0.07f, 1f),
        shadowColor: Color = Color(1f, 0.94f, 0.62f, 1f)
    ) {
        val offset = (charHeight * 0.09f).coerceAtLeast(1.6f)
        drawVectorString(sr, text.uppercase(), centerX + offset, centerY - offset, charHeight, shadowColor, extraStroke = 0f)
        drawVectorString(sr, text.uppercase(), centerX, centerY, charHeight, fillColor, extraStroke = 0f)
    }

    // Vẽ chữ trên nền gỗ tối / nút bấm (Viền đen gọn + lòng chữ sáng rõ, không bị nhòe ở cỡ chữ nhỏ)
    fun drawPlaqueText(
        sr: ShapeRenderer,
        text: String,
        centerX: Float,
        centerY: Float,
        charHeight: Float = 20f,
        fillColor: Color = Color(0.98f, 0.88f, 0.28f, 1f),
        outlineColor: Color = Color(0.10f, 0.07f, 0.05f, 1f)
    ) {
        val uText = text.uppercase()
        val dropOffset = (charHeight * 0.08f).coerceAtLeast(1.2f)
        val borderExpand = (charHeight * 0.12f).coerceAtLeast(1.5f)

        drawVectorString(sr, uText, centerX + dropOffset, centerY - dropOffset, charHeight, outlineColor, extraStroke = borderExpand * 0.65f)
        drawVectorString(sr, uText, centerX, centerY, charHeight, outlineColor, extraStroke = borderExpand)
        drawVectorString(sr, uText, centerX, centerY, charHeight, fillColor, extraStroke = 0f)
    }

    fun drawMultiColorLine(
        batch: SpriteBatch,
        font: BitmapFont,
        segments: List<Pair<String, Color>>,
        startX: Float,
        y: Float
    ) {
        prepareCrispFont(font)
        var curX = startX
        for ((segText, segColor) in segments) {
            font.color = Color(0f, 0f, 0f, 0.72f)
            font.draw(batch, segText, curX + 1.1f, y - 1.1f)
            font.color = segColor
            val drawn = font.draw(batch, segText, curX, y)
            curX += drawn.width
        }
    }

    private fun getCharWidthRatio(ch: Char): Float = when (ch) {
        'I', '!', ':', '.', ',' -> 0.38f
        '1', '(', ')' -> 0.48f
        'J', '?' -> 0.56f
        'L', 'F', 'E', 'Z', 'S', '2', '3', '5', '7' -> 0.66f
        'T' -> 0.72f
        'M', 'W', '%' -> 0.88f
        ' ' -> 0.42f
        else -> 0.72f
    }

    fun drawVectorString(
        sr: ShapeRenderer,
        text: String,
        centerX: Float,
        centerY: Float,
        charHeight: Float,
        color: Color,
        extraStroke: Float = 0f
    ) {
        if (text.isEmpty()) return
        val spacing = charHeight * 0.19f
        var totalW = 0f
        for (i in text.indices) {
            totalW += getCharWidthRatio(text[i]) * charHeight
            if (i < text.lastIndex) totalW += spacing
        }

        var curX = centerX - totalW / 2f
        val bottomY = centerY - charHeight / 2f
        val baseStroke = (charHeight * 0.175f).coerceAtLeast(1.7f)
        val stroke = baseStroke + extraStroke

        sr.color = color
        for (ch in text) {
            val cw = getCharWidthRatio(ch) * charHeight
            drawGlyph(sr, ch, curX, bottomY, cw, charHeight, baseStroke, stroke)
            curX += cw + spacing
        }
    }

    private fun bar(sr: ShapeRenderer, x1: Float, y1: Float, x2: Float, y2: Float, t: Float) {
        sr.rectLine(x1, y1, x2, y2, t)
        val r = t * 0.34f
        sr.circle(x1, y1, r)
        sr.circle(x2, y2, r)
    }

    private fun curve(
        sr: ShapeRenderer,
        cx: Float,
        cy: Float,
        rx: Float,
        ry: Float,
        startDeg: Float,
        sweepDeg: Float,
        t: Float,
        segments: Int = 10
    ) {
        var prevX = cx + rx * MathUtils.cosDeg(startDeg)
        var prevY = cy + ry * MathUtils.sinDeg(startDeg)
        val r = t * 0.36f
        sr.circle(prevX, prevY, r)
        for (i in 1..segments) {
            val deg = startDeg + sweepDeg * (i.toFloat() / segments.toFloat())
            val nx = cx + rx * MathUtils.cosDeg(deg)
            val ny = cy + ry * MathUtils.sinDeg(deg)
            sr.rectLine(prevX, prevY, nx, ny, t)
            sr.circle(nx, ny, r)
            prevX = nx
            prevY = ny
        }
    }

    private fun drawGlyph(
        sr: ShapeRenderer,
        ch: Char,
        x: Float,
        y: Float,
        w: Float,
        h: Float,
        baseT: Float,
        t: Float
    ) {
        val l = x + baseT * 0.32f
        val r = x + w - baseT * 0.32f
        val b = y + baseT * 0.32f
        val top = y + h - baseT * 0.32f
        val mx = x + w / 2f
        val my = y + h / 2f

        when (ch) {
            'A' -> {
                bar(sr, l, b, mx, top, t)
                bar(sr, r, b, mx, top, t)
                bar(sr, l + w * 0.16f, my - h * 0.08f, r - w * 0.16f, my - h * 0.08f, t * 0.85f)
            }
            'B' -> {
                bar(sr, l, b, l, top, t)
                bar(sr, l, top, mx, top, t * 0.92f)
                bar(sr, l, my, mx, my, t * 0.88f)
                bar(sr, l, b, mx, b, t * 0.92f)
                curve(sr, mx, (top + my) / 2f, (r - mx), (top - my) / 2f, -90f, 180f, t * 0.9f, 8)
                curve(sr, mx, (my + b) / 2f, (r - mx) * 1.05f, (my - b) / 2f, -90f, 180f, t * 0.9f, 8)
            }
            'C' -> {
                curve(sr, mx + w * 0.06f, my, (r - l) * 0.52f, (top - b) * 0.50f, 42f, 276f, t, 12)
            }
            'D' -> {
                bar(sr, l, b, l, top, t)
                bar(sr, l, top, mx - w * 0.05f, top, t)
                bar(sr, l, b, mx - w * 0.05f, b, t)
                curve(sr, mx - w * 0.05f, my, r - (mx - w * 0.05f), (top - b) / 2f, -90f, 180f, t, 10)
            }
            'E' -> {
                bar(sr, l, b, l, top, t)
                bar(sr, l, top, r, top, t)
                bar(sr, l, my, r - w * 0.12f, my, t * 0.9f)
                bar(sr, l, b, r, b, t)
            }
            'F' -> {
                bar(sr, l, b, l, top, t)
                bar(sr, l, top, r, top, t)
                bar(sr, l, my, r - w * 0.12f, my, t * 0.9f)
            }
            'G' -> {
                curve(sr, mx + w * 0.04f, my, (r - l) * 0.52f, (top - b) * 0.50f, 45f, 290f, t, 12)
                bar(sr, mx + w * 0.02f, my - h * 0.04f, r, my - h * 0.04f, t * 0.88f)
                bar(sr, r, b + h * 0.14f, r, my - h * 0.04f, t * 0.88f)
            }
            'H' -> {
                bar(sr, l, b, l, top, t)
                bar(sr, r, b, r, top, t)
                bar(sr, l, my, r, my, t * 0.9f)
            }
            'I' -> {
                bar(sr, mx, b, mx, top, t)
            }
            'J' -> {
                bar(sr, r, b + (r - l) * 0.48f, r, top, t)
                curve(sr, mx, b + (r - l) * 0.48f, (r - l) * 0.5f, (r - l) * 0.48f, 0f, -180f, t, 8)
            }
            'K' -> {
                bar(sr, l, b, l, top, t)
                bar(sr, l + baseT * 0.2f, my - h * 0.04f, r, top, t * 0.95f)
                bar(sr, l + w * 0.22f, my + h * 0.04f, r, b, t * 0.95f)
            }
            'L' -> {
                bar(sr, l, b, l, top, t)
                bar(sr, l, b, r, b, t)
            }
            'M' -> {
                bar(sr, l, b, l + w * 0.05f, top, t)
                bar(sr, r, b, r - w * 0.05f, top, t)
                bar(sr, l + w * 0.05f, top, mx, b + h * 0.22f, t * 0.9f)
                bar(sr, r - w * 0.05f, top, mx, b + h * 0.22f, t * 0.9f)
            }
            'N' -> {
                bar(sr, l, b, l, top, t)
                bar(sr, r, b, r, top, t)
                bar(sr, l, top, r, b, t * 0.95f)
            }
            'O', '0' -> {
                curve(sr, mx, my, (r - l) * 0.50f, (top - b) * 0.50f, 0f, 360f, t, 14)
            }
            'P' -> {
                bar(sr, l, b, l, top, t)
                bar(sr, l, top, mx, top, t * 0.95f)
                bar(sr, l, my - h * 0.02f, mx, my - h * 0.02f, t * 0.9f)
                curve(sr, mx, (top + my - h * 0.02f) / 2f, r - mx, (top - (my - h * 0.02f)) / 2f, -90f, 180f, t * 0.95f, 8)
            }
            'Q' -> {
                curve(sr, mx, my, (r - l) * 0.50f, (top - b) * 0.50f, 0f, 360f, t, 14)
                bar(sr, mx + w * 0.05f, my - h * 0.12f, r + baseT * 0.2f, b - baseT * 0.15f, t * 0.9f)
            }
            'R' -> {
                bar(sr, l, b, l, top, t)
                bar(sr, l, top, mx, top, t * 0.95f)
                bar(sr, l, my, mx, my, t * 0.9f)
                curve(sr, mx, (top + my) / 2f, r - mx, (top - my) / 2f, -90f, 180f, t * 0.95f, 8)
                bar(sr, mx - w * 0.02f, my, r, b, t * 0.95f)
            }
            'S' -> {
                curve(sr, mx, my + (top - my) * 0.48f, (r - l) * 0.48f, (top - my) * 0.48f, 35f, 225f, t * 0.92f, 8)
                curve(sr, mx, b + (my - b) * 0.52f, (r - l) * 0.48f, (my - b) * 0.52f, 90f, -235f, t * 0.92f, 8)
            }
            'T' -> {
                // Thanh ngang trên rộng rõ + thân dọc tách gọn không bị phình cục ở ngã ba
                bar(sr, x + baseT * 0.1f, top, x + w - baseT * 0.1f, top, t * 0.96f)
                bar(sr, mx, b, mx, top - baseT * 0.15f, t * 0.96f)
            }
            'U' -> {
                val ry = (r - l) * 0.46f
                bar(sr, l, b + ry, l, top, t)
                bar(sr, r, b + ry, r, top, t)
                curve(sr, mx, b + ry, (r - l) / 2f, ry, 180f, 180f, t, 10)
            }
            'V' -> {
                bar(sr, l, top, mx, b, t)
                bar(sr, r, top, mx, b, t)
            }
            'W' -> {
                bar(sr, l, top, x + w * 0.28f, b, t * 0.92f)
                bar(sr, x + w * 0.28f, b, mx, my + h * 0.12f, t * 0.88f)
                bar(sr, mx, my + h * 0.12f, x + w * 0.72f, b, t * 0.88f)
                bar(sr, r, top, x + w * 0.72f, b, t * 0.92f)
            }
            'X' -> {
                bar(sr, l, b, r, top, t)
                bar(sr, l, top, r, b, t)
            }
            'Y' -> {
                bar(sr, mx, b, mx, my, t)
                bar(sr, l, top, mx, my, t)
                bar(sr, r, top, mx, my, t)
            }
            'Z' -> {
                bar(sr, l, top, r, top, t)
                bar(sr, r, top, l, b, t)
                bar(sr, l, b, r, b, t)
            }
            '1' -> {
                // Số 1 rõ ràng: thân dọc + mũi chéo góc trên trái (bỏ gạch đáy gây nhòe)
                val stemX = mx + w * 0.10f
                bar(sr, stemX, b, stemX, top, t * 0.96f)
                bar(sr, l, top - h * 0.24f, stemX, top, t * 0.88f)
            }
            '2' -> {
                curve(sr, mx, top - (top - my) * 0.48f, (r - l) * 0.48f, (top - my) * 0.48f, 155f, -195f, t * 0.92f, 8)
                bar(sr, r - w * 0.04f, my + h * 0.05f, l, b, t * 0.92f)
                bar(sr, l, b, r, b, t)
            }
            '3' -> {
                curve(sr, mx - w * 0.05f, (top + my) / 2f, (r - l) * 0.5f, (top - my) / 2f, 135f, -225f, t * 0.9f, 8)
                curve(sr, mx - w * 0.05f, (my + b) / 2f, (r - l) * 0.52f, (my - b) / 2f, 90f, -225f, t * 0.9f, 8)
            }
            '4' -> {
                bar(sr, r - w * 0.15f, b, r - w * 0.15f, top, t)
                bar(sr, r - w * 0.15f, top, l, my - h * 0.08f, t * 0.9f)
                bar(sr, l, my - h * 0.08f, r, my - h * 0.08f, t * 0.9f)
            }
            '5' -> {
                bar(sr, l, top, r, top, t * 0.92f)
                bar(sr, l, my, l, top, t * 0.92f)
                bar(sr, l, my + h * 0.04f, mx, my + h * 0.04f, t * 0.9f)
                curve(sr, mx - w * 0.04f, (my + b) / 2f + h * 0.02f, (r - l) * 0.52f, (my - b) * 0.52f, 90f, -230f, t * 0.92f, 9)
            }
            '6' -> {
                curve(sr, mx + w * 0.05f, my, (r - l) * 0.50f, (top - b) * 0.50f, 65f, 135f, t * 0.92f, 7)
                curve(sr, mx, b + (my - b) * 0.55f, (r - l) * 0.48f, (my - b) * 0.55f, 0f, 360f, t * 0.92f, 10)
            }
            '7' -> {
                bar(sr, l, top, r, top, t)
                bar(sr, r, top, mx - w * 0.08f, b, t)
            }
            '8' -> {
                curve(sr, mx, (top + my) / 2f, (r - l) * 0.45f, (top - my) / 2f, 0f, 360f, t * 0.88f, 10)
                curve(sr, mx, (my + b) / 2f, (r - l) * 0.50f, (my - b) / 2f, 0f, 360f, t * 0.88f, 10)
            }
            '9' -> {
                curve(sr, mx, top - (top - my) * 0.55f, (r - l) * 0.48f, (top - my) * 0.55f, 0f, 360f, t * 0.92f, 10)
                bar(sr, r, b, r, top - (top - my) * 0.55f, t * 0.92f)
            }
            '!' -> {
                bar(sr, mx, y + h * 0.36f, mx, top, t)
                sr.circle(mx, b + baseT * 0.1f, t * 0.56f)
            }
            '?' -> {
                curve(sr, mx, top - h * 0.24f, (r - l) * 0.48f, h * 0.24f, 150f, -240f, t * 0.9f, 8)
                bar(sr, mx, y + h * 0.34f, mx, my, t * 0.9f)
                sr.circle(mx, b + baseT * 0.1f, t * 0.56f)
            }
            '-' -> {
                bar(sr, l, my, r, my, t * 0.9f)
            }
            '/' -> {
                bar(sr, l, b, r, top, t * 0.9f)
            }
            '.' -> {
                sr.circle(mx, b + baseT * 0.1f, t * 0.54f)
            }
            ',' -> {
                sr.circle(mx, b + baseT * 0.15f, t * 0.50f)
                bar(sr, mx, b + baseT * 0.1f, mx - w * 0.2f, b - baseT * 0.35f, t * 0.68f)
            }
            '\'' -> {
                bar(sr, mx, top - h * 0.24f, mx + baseT * 0.15f, top, t * 0.78f)
            }
            ':' -> {
                sr.circle(mx, my + h * 0.22f, t * 0.52f)
                sr.circle(mx, b + baseT * 0.15f, t * 0.52f)
            }
            '+' -> {
                bar(sr, l, my, r, my, t * 0.85f)
                bar(sr, mx, my - (r - l) / 2f, mx, my + (r - l) / 2f, t * 0.85f)
            }
            '(' -> {
                curve(sr, r, my, (r - l) * 0.7f, (top - b) * 0.5f, 115f, 130f, t * 0.85f, 8)
            }
            ')' -> {
                curve(sr, l, my, (r - l) * 0.7f, (top - b) * 0.5f, -65f, 130f, t * 0.85f, 8)
            }
            '%' -> {
                bar(sr, l, b, r, top, t * 0.75f)
                sr.circle(l + w * 0.2f, top - h * 0.2f, t * 0.52f)
                sr.circle(r - w * 0.2f, b + h * 0.2f, t * 0.52f)
            }
            '&' -> {
                curve(sr, mx, top - h * 0.24f, (r - l) * 0.38f, h * 0.22f, 0f, 360f, t * 0.82f, 8)
                curve(sr, mx - w * 0.05f, b + h * 0.26f, (r - l) * 0.45f, h * 0.25f, 120f, 210f, t * 0.85f, 8)
                bar(sr, l + w * 0.15f, my + h * 0.05f, r, b, t * 0.85f)
            }
            '$' -> {
                curve(sr, mx, my + (top - my) * 0.45f, (r - l) * 0.45f, (top - my) * 0.45f, 35f, 225f, t * 0.85f, 8)
                curve(sr, mx, b + (my - b) * 0.50f, (r - l) * 0.45f, (my - b) * 0.50f, 90f, -235f, t * 0.85f, 8)
                bar(sr, mx, b - baseT * 0.2f, mx, top + baseT * 0.2f, t * 0.75f)
            }
        }
    }
}
