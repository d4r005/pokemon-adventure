package com.dario.pokemonadventure.ui

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF

class UiButton(
    val id: String,
    val rect: RectF,
    val label: String,
    val sub: String = "",
    val enabled: Boolean = true,
    val icon: Int = 0,
    val style: Int = 0 // 0 = fila, 1 = tarjeta
)

object Paints {

    private val fills = HashMap<Int, Paint>()
    private val strokes = HashMap<Int, Paint>()
    private val texts = HashMap<String, Paint>()

    fun fill(color: Int): Paint = fills.getOrPut(color) {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color
            style = Paint.Style.FILL
        }
    }

    fun stroke(color: Int): Paint = strokes.getOrPut(color) {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color
            style = Paint.Style.STROKE
            strokeWidth = 3f
        }
    }

    fun text(color: Int, size: Float, bold: Boolean = true): Paint {
        val key = "$color|$size|$bold"
        return texts.getOrPut(key) {
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                this.color = color
                textSize = size
                isFakeBoldText = bold
            }
        }
    }

    fun lighten(c: Int, f: Float = 0.35f): Int {
        val a = (c shr 24) and 0xFF
        val r = (c shr 16) and 0xFF
        val g = (c shr 8) and 0xFF
        val b = c and 0xFF
        val nr = r + ((255 - r) * f).toInt()
        val ng = g + ((255 - g) * f).toInt()
        val nb = b + ((255 - b) * f).toInt()
        return (a shl 24) or (nr shl 16) or (ng shl 8) or nb
    }

    fun darken(c: Int, f: Float = 0.35f): Int {
        val a = (c shr 24) and 0xFF
        val r = (c shr 16) and 0xFF
        val g = (c shr 8) and 0xFF
        val b = c and 0xFF
        val nr = (r * (1 - f)).toInt()
        val ng = (g * (1 - f)).toInt()
        val nb = (b * (1 - f)).toInt()
        return (a shl 24) or (nr shl 16) or (ng shl 8) or nb
    }

    fun wrap(paint: Paint, text: String, maxWidth: Float): List<String> {
        val words = text.split(" ")
        val lines = ArrayList<String>()
        var cur = ""
        for (w in words) {
            val t = if (cur.isEmpty()) w else "$cur $w"
            if (paint.measureText(t) <= maxWidth) {
                cur = t
            } else {
                if (cur.isNotEmpty()) lines.add(cur)
                cur = w
            }
        }
        if (cur.isNotEmpty()) lines.add(cur)
        return lines
    }

    fun centerText(canvas: Canvas, text: String, cx: Float, y: Float, color: Int, size: Float) {
        val p = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color
            textSize = size
            textAlign = Paint.Align.CENTER
            isFakeBoldText = true
        }
        canvas.drawText(text, cx, y, p)
    }

    fun leftText(canvas: Canvas, text: String, x: Float, y: Float, color: Int, size: Float) {
        val p = text(color, size)
        canvas.drawText(text, x, y, p)
    }

    fun hpBar(canvas: Canvas, x: Float, y: Float, w: Float, h: Float, ratio: Float) {
        val bg = RectF(x, y, x + w, y + h)
        canvas.drawRoundRect(bg, h / 2, h / 2, fill(0xFF2E2E38.toInt()))
        val r = ratio.coerceIn(0f, 1f)
        if (r > 0f) {
            val color = when {
                r > 0.5f -> 0xFF4CC94C.toInt()
                r > 0.2f -> 0xFFE8C20D.toInt()
                else -> 0xFFE84C3D.toInt()
            }
            val fg = RectF(x, y, x + w * r, y + h)
            canvas.drawRoundRect(fg, h / 2, h / 2, fill(color))
        }
    }

    fun drawButton(canvas: Canvas, b: UiButton, fh: Float) {
        val bg = if (b.enabled) 0xCC26303F.toInt() else 0x6626303F.toInt()
        val r = fh * 0.02f
        canvas.drawRoundRect(b.rect, r, r, fill(bg))
        val border = if (b.enabled) 0x66FFFFFF else 0x33FFFFFF
        canvas.drawRoundRect(b.rect, r, r, stroke(border))
        val labelColor = if (b.enabled) 0xFFFFFFFF.toInt() else 0xFF888888.toInt()

        if (b.style == 1) {
            // Tarjeta: icono centrado arriba, textos centrados abajo
            val cx = b.rect.centerX()
            val iconR = kotlin.math.min(b.rect.width(), b.rect.height()) * 0.20f
            canvas.drawCircle(cx, b.rect.top + b.rect.height() * 0.32f, iconR,
                fill(if (b.icon != 0 && b.enabled) b.icon else 0xFF555555.toInt()))
            centerText(canvas, b.label, cx, b.rect.top + b.rect.height() * 0.68f, labelColor, fh * 0.035f)
            if (b.sub.isNotEmpty()) {
                centerText(canvas, b.sub, cx, b.rect.top + b.rect.height() * 0.86f, 0xFFBBBBBB.toInt(), fh * 0.026f)
            }
        } else {
            var tx = b.rect.left + b.rect.height() * 0.12f
            if (b.icon != 0) {
                val iconR = b.rect.height() * 0.28f
                canvas.drawCircle(b.rect.left + b.rect.height() * 0.55f, b.rect.centerY(), iconR,
                    fill(if (b.enabled) b.icon else 0xFF555555.toInt()))
                tx = b.rect.left + b.rect.height() * 1.05f
            }
            if (b.sub.isEmpty()) {
                val tp = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = labelColor
                    textSize = fh * 0.035f
                    isFakeBoldText = true
                }
                val ty = b.rect.centerY() - (tp.descent() + tp.ascent()) / 2
                canvas.drawText(b.label, tx, ty, tp)
            } else {
                val tp1 = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = labelColor
                    textSize = fh * 0.030f
                    isFakeBoldText = true
                }
                canvas.drawText(b.label, tx, b.rect.top + b.rect.height() * 0.42f, tp1)
                leftText(canvas, b.sub, tx, b.rect.top + b.rect.height() * 0.78f, 0xFFBBBBBB.toInt(), fh * 0.024f)
            }
        }
    }

    fun drawButtons(canvas: Canvas, buttons: List<UiButton>, fh: Float) {
        for (b in buttons) drawButton(canvas, b, fh)
    }

    fun dialogBox(canvas: Canvas, text: String, w: Float, h: Float) {
        val boxH = h * 0.17f
        val rect = RectF(w * 0.02f, h - boxH - h * 0.02f, w * 0.98f, h - h * 0.02f)
        canvas.drawRoundRect(rect, h * 0.02f, h * 0.02f, fill(0xEE26303F.toInt()))
        canvas.drawRoundRect(rect, h * 0.02f, h * 0.02f, stroke(0x66FFFFFF))
        val tp = text(0xFFFFFFFF.toInt(), h * 0.038f)
        val lines = wrap(tp, text, rect.width() - h * 0.06f)
        var y = rect.top + h * 0.055f
        for (line in lines.take(3)) {
            canvas.drawText(line, rect.left + h * 0.03f, y, tp)
            y += h * 0.05f
        }
        // Indicador de continuar
        centerText(canvas, "▼", rect.right - h * 0.05f, rect.bottom - h * 0.025f, 0xFFAAAAAA.toInt(), h * 0.03f)
    }
}
