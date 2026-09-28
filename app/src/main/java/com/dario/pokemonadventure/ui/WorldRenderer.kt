package com.dario.pokemonadventure.ui

import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import com.dario.pokemonadventure.data.Mon
import com.dario.pokemonadventure.data.TypeColors
import com.dario.pokemonadventure.game.GameEngine
import com.dario.pokemonadventure.world.Tile
import kotlin.math.min
import kotlin.math.sin

object WorldRenderer {

    fun render(canvas: Canvas, engine: GameEngine) {
        val w = canvas.width.toFloat()
        val h = canvas.height.toFloat()
        val map = engine.map
        if (map == null) {
            canvas.drawColor(0xFF1B2A1B.toInt())
            return
        }
        val ts = min(w / 16f, h / 10f)
        val p = engine.player
        val pal = map.def.palette

        val camX = (p.x * ts - w / 2).coerceIn(0f, (map.size * ts - w).coerceAtLeast(0f))
        val camY = (p.y * ts - h / 2).coerceIn(0f, (map.size * ts - h).coerceAtLeast(0f))

        val x0 = (camX / ts).toInt()
        val y0 = (camY / ts).toInt()
        val x1 = x0 + (w / ts).toInt() + 2
        val y1 = y0 + (h / ts).toInt() + 2

        // Suelo de respaldo
        canvas.drawColor(Paints.darken(pal.grass, 0.2f))

        for (ty in y0..y1) {
            for (tx in x0..x1) {
                val sx = tx * ts - camX
                val sy = ty * ts - camY
                drawTile(canvas, engine, map.tileAt(tx, ty), tx, ty, sx, sy, ts, pal)
            }
        }

        // NPCs
        for (npc in map.npcs) {
            val sx = npc.x * ts - camX + ts / 2
            val sy = npc.y * ts - camY + ts / 2
            if (sx < -ts || sx > w + ts || sy < -ts || sy > h + ts) continue
            drawNpc(canvas, npc.kind, sx, sy, ts)
        }

        drawPlayer(canvas, engine, p.x * ts - camX, p.y * ts - camY, ts, engine.time)

        drawHud(canvas, engine, w, h)
        Paints.drawButtons(canvas, engine.buttons, h)

        if (engine.dialog.isNotEmpty()) {
            Paints.dialogBox(canvas, engine.dialog.first(), w, h)
        }
    }

    private fun drawTile(canvas: Canvas, engine: GameEngine, tile: Int, tx: Int, ty: Int, sx: Float, sy: Float, ts: Float, pal: com.dario.pokemonadventure.world.Palette) {
        val rect = RectF(sx, sy, sx + ts, sy + ts)
        val v = ((tx * 7 + ty * 13) % 5)
        when (tile) {
            Tile.GRASS -> {
                canvas.drawRoundRect(rect, ts * 0.10f, ts * 0.10f, Paints.fill(if (v == 0) pal.grassAlt else pal.grass))
                if (v == 3) canvas.drawCircle(sx + ts * 0.5f, sy + ts * 0.5f, ts * 0.06f, Paints.fill(Paints.darken(pal.grass, 0.2f)))
            }
            Tile.TALL -> {
                canvas.drawRoundRect(rect, ts * 0.10f, ts * 0.10f, Paints.fill(pal.grass))
                canvas.drawRoundRect(RectF(sx + ts * 0.15f, sy + ts * 0.25f, sx + ts * 0.85f, sy + ts * 0.95f), ts * 0.18f, ts * 0.18f, Paints.fill(pal.tall))
                canvas.drawCircle(sx + ts * 0.38f, sy + ts * 0.30f, ts * 0.07f, Paints.fill(Paints.darken(pal.tall, 0.15f)))
                canvas.drawCircle(sx + ts * 0.62f, sy + ts * 0.45f, ts * 0.07f, Paints.fill(Paints.darken(pal.tall, 0.15f)))
            }
            Tile.TREE -> {
                canvas.drawRoundRect(rect, ts * 0.10f, ts * 0.10f, Paints.fill(pal.grass))
                canvas.drawRect(sx + ts * 0.42f, sy + ts * 0.45f, sx + ts * 0.58f, sy + ts * 0.95f, Paints.fill(0xFF6B4A2E.toInt()))
                canvas.drawCircle(sx + ts * 0.5f, sy + ts * 0.38f, ts * 0.34f, Paints.fill(pal.tree))
                canvas.drawCircle(sx + ts * 0.42f, sy + ts * 0.30f, ts * 0.16f, Paints.fill(Paints.lighten(pal.tree, 0.2f)))
            }
            Tile.WATER -> {
                canvas.drawRoundRect(rect, ts * 0.10f, ts * 0.10f, Paints.fill(pal.water))
                val off = sin(engine.time * 2f + tx * 0.8f + ty) * ts * 0.05f
                canvas.drawCircle(sx + ts * 0.35f + off, sy + ts * 0.40f, ts * 0.05f, Paints.fill(Paints.lighten(pal.water, 0.4f)))
                canvas.drawCircle(sx + ts * 0.70f - off, sy + ts * 0.65f, ts * 0.05f, Paints.fill(Paints.lighten(pal.water, 0.4f)))
            }
            Tile.PATH -> {
                canvas.drawRoundRect(rect, ts * 0.08f, ts * 0.08f, Paints.fill(pal.path))
                if (v == 2) canvas.drawCircle(sx + ts * 0.5f, sy + ts * 0.5f, ts * 0.05f, Paints.fill(Paints.darken(pal.path, 0.2f)))
            }
            Tile.TOWN -> {
                canvas.drawRoundRect(rect, ts * 0.08f, ts * 0.08f, Paints.fill(pal.town))
                canvas.drawRect(RectF(sx + ts * 0.05f, sy + ts * 0.08f, sx + ts * 0.95f, sy + ts * 0.35f), Paints.fill(Paints.darken(pal.town, 0.25f)))
                canvas.drawCircle(sx + ts * 0.25f, sy + ts * 0.62f, ts * 0.07f, Paints.fill(0xFFE8F4FF.toInt()))
                canvas.drawCircle(sx + ts * 0.75f, sy + ts * 0.62f, ts * 0.07f, Paints.fill(0xFFE8F4FF.toInt()))
                canvas.drawRoundRect(RectF(sx + ts * 0.40f, sy + ts * 0.55f, sx + ts * 0.60f, sy + ts * 0.85f), ts * 0.05f, ts * 0.05f, Paints.fill(0xFF6B4A2E.toInt()))
            }
            Tile.PORTAL -> {
                canvas.drawRoundRect(rect, ts * 0.10f, ts * 0.10f, Paints.fill(pal.path))
                val pulse = 0.30f + 0.05f * sin(engine.time * 3f)
                canvas.drawCircle(sx + ts * 0.5f, sy + ts * 0.5f, ts * pulse, Paints.fill(0x88E8A3D1.toInt()))
                canvas.drawCircle(sx + ts * 0.5f, sy + ts * 0.5f, ts * pulse * 0.5f, Paints.fill(0xCCFFFFFF.toInt()))
            }
            Tile.FLOWER -> {
                canvas.drawRoundRect(rect, ts * 0.10f, ts * 0.10f, Paints.fill(pal.grass))
                canvas.drawCircle(sx + ts * 0.35f, sy + ts * 0.4f, ts * 0.07f, Paints.fill(pal.accent))
                canvas.drawCircle(sx + ts * 0.65f, sy + ts * 0.65f, ts * 0.07f, Paints.fill(0xFFFFF3B0.toInt()))
            }
            Tile.ROCK -> {
                canvas.drawRoundRect(rect, ts * 0.10f, ts * 0.10f, Paints.fill(pal.grass))
                canvas.drawCircle(sx + ts * 0.5f, sy + ts * 0.6f, ts * 0.28f, Paints.fill(0xFF8A90A8.toInt()))
                canvas.drawCircle(sx + ts * 0.42f, sy + ts * 0.52f, ts * 0.10f, Paints.fill(0xFFA8AEC0.toInt()))
            }
            Tile.PAVEMENT -> {
                canvas.drawRoundRect(rect, ts * 0.05f, ts * 0.05f, Paints.fill(0xFFBFC5CF.toInt()))
                canvas.drawRect(RectF(sx, sy, sx + ts, sy + ts * 0.06f), Paints.fill(0xFFAAB0BC.toInt()))
            }
        }
    }

    private fun drawNpc(canvas: Canvas, kind: com.dario.pokemonadventure.world.Npc.Kind, cx: Float, cy: Float, ts: Float) {
        val body = when (kind) {
            com.dario.pokemonadventure.world.Npc.Kind.NURSE -> 0xFFE88BB0.toInt()
            com.dario.pokemonadventure.world.Npc.Kind.SHOP -> 0xFF5F8FBF.toInt()
            com.dario.pokemonadventure.world.Npc.Kind.GUIDE -> 0xFF6FA85F.toInt()
            com.dario.pokemonadventure.world.Npc.Kind.TRAINER -> 0xFFB04A5A.toInt()
        }
        canvas.drawOval(RectF(cx - ts * 0.30f, cy + ts * 0.18f, cx + ts * 0.30f, cy + ts * 0.30f), Paints.fill(0x44000000.toInt()))
        canvas.drawCircle(cx, cy - ts * 0.05f, ts * 0.30f, Paints.fill(body))
        canvas.drawCircle(cx, cy - ts * 0.38f, ts * 0.19f, Paints.fill(0xFFF2C9A0.toInt()))
        canvas.drawCircle(cx - ts * 0.06f, cy - ts * 0.40f, ts * 0.03f, Paints.fill(0xFF1B1B24.toInt()))
        canvas.drawCircle(cx + ts * 0.06f, cy - ts * 0.40f, ts * 0.03f, Paints.fill(0xFF1B1B24.toInt()))
        if (kind == com.dario.pokemonadventure.world.Npc.Kind.TRAINER) {
            // Gorra y marcador de duelo
            canvas.drawCircle(cx, cy - ts * 0.48f, ts * 0.16f, Paints.fill(0xFF1B1B24.toInt()))
            Paints.centerText(canvas, "!", cx, cy - ts * 0.80f, 0xFFFFD54F.toInt(), ts * 0.35f)
        }
    }

    private fun drawPlayer(canvas: Canvas, engine: GameEngine, cx: Float, cy: Float, ts: Float, time: Float) {
        val moving = engine.dpadDir >= 0 && engine.dialog.isEmpty()
        val bob = if (moving) sin(time * 12f) * ts * 0.03f else 0f
        val y = cy + bob
        canvas.drawOval(RectF(cx - ts * 0.28f, cy + ts * 0.18f, cx + ts * 0.28f, cy + ts * 0.30f), Paints.fill(0x44000000.toInt()))
        // cuerpo (chaqueta roja estilo leyendas)
        canvas.drawCircle(cx, y - ts * 0.02f, ts * 0.30f, Paints.fill(0xFFD94530.toInt()))
        // cabeza
        canvas.drawCircle(cx, y - ts * 0.38f, ts * 0.19f, Paints.fill(0xFFF2C9A0.toInt()))
        // gorra
        canvas.drawCircle(cx, y - ts * 0.46f, ts * 0.19f, Paints.fill(0xFF2E2E48.toInt()))
        // ojos según dirección
        val f = engine.player.facing
        if (f != 0) {
            val ex = if (f == 1) ts * 0.06f else if (f == 3) -ts * 0.06f else 0f
            canvas.drawCircle(cx - ts * 0.07f + ex, y - ts * 0.38f, ts * 0.03f, Paints.fill(0xFF1B1B24.toInt()))
            canvas.drawCircle(cx + ts * 0.07f + ex, y - ts * 0.38f, ts * 0.03f, Paints.fill(0xFF1B1B24.toInt()))
        } else {
            canvas.drawCircle(cx - ts * 0.07f, y - ts * 0.44f, ts * 0.03f, Paints.fill(0xFF1B1B24.toInt()))
            canvas.drawCircle(cx + ts * 0.07f, y - ts * 0.44f, ts * 0.03f, Paints.fill(0xFF1B1B24.toInt()))
        }
    }

    private fun drawHud(canvas: Canvas, engine: GameEngine, w: Float, h: Float) {
        val p = engine.player
        val region = engine.map?.def ?: return

        // Chip región + dinero
        val chipW = w * 0.24f
        val chip = RectF(w * 0.015f, h * 0.02f, w * 0.015f + chipW, h * 0.02f + h * 0.06f)
        canvas.drawRoundRect(chip, h * 0.015f, h * 0.015f, Paints.fill(0xBB1B2430.toInt()))
        Paints.leftText(canvas, "${region.name}  •  $${p.money}", chip.left + w * 0.012f, chip.top + h * 0.043f, 0xFFFFFFFF.toInt(), h * 0.028f)

        // Barras del equipo
        var by = chip.bottom + h * 0.015f
        for ((i, m) in p.party.withIndex()) {
            val barW = w * 0.14f
            val barH = h * 0.014f
            val rect = RectF(chip.left, by, chip.left + barW + h * 0.05f, by + barH + h * 0.012f)
            canvas.drawRoundRect(rect, h * 0.01f, h * 0.01f, Paints.fill(if (i == p.activeIndex) 0xBB26303F.toInt() else 0x771B2430.toInt()))
            canvas.drawCircle(rect.left + h * 0.018f, rect.centerY(), h * 0.009f, Paints.fill(TypeColors.color(m.species.types[0])))
            Paints.hpBar(canvas, rect.left + h * 0.04f, rect.top + h * 0.007f, barW, barH, m.hp.toFloat() / m.maxHp)
            by += h * 0.030f
        }
        if (p.box > 0) {
            Paints.leftText(canvas, "Caja: ${p.box}", chip.left, by + h * 0.012f, 0xCCFFFFFF.toInt(), h * 0.022f)
        }

        // D-pad
        val dp = engine.dpadRect()
        val cx = dp.centerX()
        val cy = dp.centerY()
        val r = dp.width() / 2
        canvas.drawCircle(cx, cy, r, Paints.fill(0x331B2430.toInt()))
        val dirs = listOf(0, 1, 2, 3)
        for (d in dirs) {
            val dx = when (d) { 1 -> r * 0.52f; 3 -> -r * 0.52f; else -> 0f }
            val dy = when (d) { 0 -> -r * 0.52f; 2 -> r * 0.52f; else -> 0f }
            val active = engine.dpadDir == d
            canvas.drawCircle(cx + dx, cy + dy, r * 0.30f, Paints.fill(if (active) 0xAAFFFFFF.toInt() else 0x55FFFFFF.toInt()))
            val path = Path()
            val a = r * 0.14f
            when (d) {
                0 -> { path.moveTo(cx + dx, cy + dy - a); path.lineTo(cx + dx - a, cy + dy + a * 0.6f); path.lineTo(cx + dx + a, cy + dy + a * 0.6f) }
                2 -> { path.moveTo(cx + dx, cy + dy + a); path.lineTo(cx + dx - a, cy + dy - a * 0.6f); path.lineTo(cx + dx + a, cy + dy - a * 0.6f) }
                1 -> { path.moveTo(cx + dx + a, cy + dy); path.lineTo(cx + dx - a * 0.6f, cy + dy - a); path.lineTo(cx + dx - a * 0.6f, cy + dy + a) }
                3 -> { path.moveTo(cx + dx - a, cy + dy); path.lineTo(cx + dx + a * 0.6f, cy + dy - a); path.lineTo(cx + dx + a * 0.6f, cy + dy + a) }
            }
            path.close()
            canvas.drawPath(path, Paints.fill(if (active) 0xFF1B2430.toInt() else 0xFFDDDDDD.toInt()))
        }
    }
}
