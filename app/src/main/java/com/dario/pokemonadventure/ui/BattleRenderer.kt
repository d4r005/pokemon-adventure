package com.dario.pokemonadventure.ui

import android.graphics.Canvas
import android.graphics.RectF
import com.dario.pokemonadventure.battle.Battle
import com.dario.pokemonadventure.data.Mon
import com.dario.pokemonadventure.data.TypeColors
import com.dario.pokemonadventure.game.GameEngine
import com.dario.pokemonadventure.world.Regions
import kotlin.math.min
import kotlin.math.sin

object BattleRenderer {

    fun render(canvas: Canvas, engine: GameEngine) {
        val b = engine.battle ?: return
        val w = canvas.width.toFloat()
        val h = canvas.height.toFloat()
        val pal = Regions.ALL[engine.player.regionIndex].palette

        // Cielo y suelo
        canvas.drawColor(Paints.lighten(pal.water, 0.45f))
        val ground = RectF(0f, h * 0.55f, w, h)
        canvas.drawRect(ground, Paints.fill(pal.grass))

        // Plataformas
        canvas.drawOval(RectF(w * 0.62f, h * 0.44f, w * 0.86f, h * 0.50f), Paints.fill(Paints.darken(pal.grass, 0.15f)))
        canvas.drawOval(RectF(w * 0.12f, h * 0.66f, w * 0.44f, h * 0.76f), Paints.fill(Paints.darken(pal.grass, 0.15f)))

        // Pokémon
        drawMon(canvas, b.enemy, w * 0.74f, h * 0.36f, h * 0.13f, true, b.time)
        drawMon(canvas, b.active, w * 0.26f, h * 0.64f, h * 0.17f, false, b.time)

        // Panel del rival
        val ePanel = RectF(w * 0.03f, h * 0.04f, w * 0.33f, h * 0.16f)
        canvas.drawRoundRect(ePanel, h * 0.02f, h * 0.02f, Paints.fill(0xCC26303F.toInt()))
        Paints.leftText(canvas, "${b.enemy.nickname}  Nv.${b.enemy.level}", ePanel.left + w * 0.015f, ePanel.top + h * 0.05f, 0xFFFFFFFF.toInt(), h * 0.030f)
        Paints.hpBar(canvas, ePanel.left + w * 0.015f, ePanel.top + h * 0.07f, ePanel.width() - w * 0.03f, h * 0.016f, b.enemy.hp.toFloat() / b.enemy.maxHp)
        if (b.trainer != null) {
            for (i in b.enemyTeam.indices) {
                val alive = b.enemyTeam[i].hp > 0
                val bx = ePanel.right + w * 0.015f + i * h * 0.034f
                canvas.drawCircle(bx, ePanel.top + h * 0.035f, h * 0.014f, Paints.fill(if (alive) 0xFFE84C3D.toInt() else 0xFF666666.toInt()))
                canvas.drawCircle(bx, ePanel.top + h * 0.035f, h * 0.006f, Paints.fill(if (alive) 0xFFF5F5F5.toInt() else 0xFF333333.toInt()))
            }
        }

        // Panel del jugador
        val pPanel = RectF(w * 0.60f, h * 0.58f, w * 0.97f, h * 0.73f)
        canvas.drawRoundRect(pPanel, h * 0.02f, h * 0.02f, Paints.fill(0xCC26303F.toInt()))
        val mine = b.active
        Paints.leftText(canvas, "${mine.nickname}  Nv.${mine.level}", pPanel.left + w * 0.015f, pPanel.top + h * 0.04f, 0xFFFFFFFF.toInt(), h * 0.030f)
        Paints.leftText(canvas, "PS ${mine.hp}/${mine.maxHp}", pPanel.left + w * 0.015f, pPanel.bottom - h * 0.015f, 0xFFBBBBBB.toInt(), h * 0.024f)
        Paints.hpBar(canvas, pPanel.left + w * 0.015f, pPanel.top + h * 0.055f, pPanel.width() - w * 0.03f, h * 0.016f, mine.hp.toFloat() / mine.maxHp)
        val expRatio = mine.exp.toFloat() / mine.expToNext().coerceAtLeast(1)
        canvas.drawRect(RectF(pPanel.left + w * 0.015f, pPanel.bottom - h * 0.008f, pPanel.left + w * 0.015f + (pPanel.width() - w * 0.03f) * expRatio.coerceIn(0f, 1f), pPanel.bottom), Paints.fill(0xFF4C8BD9.toInt()))

        // Botones
        Paints.drawButtons(canvas, engine.buttons, h)

        // Caja de mensajes
        val msg = if (b.messages.isNotEmpty()) b.messages.first() else when (b.phase) {
            Battle.Phase.ACTION -> "¿Qué hará ${mine.nickname}?   (Estilo ${b.label()}: Ágil pega primero con menos fuerza, Fuerte pega con todo pero al final)"
            Battle.Phase.MOVES -> "Elige un movimiento."
            Battle.Phase.BAG -> "Elige un objeto de tu bolsa."
            Battle.Phase.TEAM -> if (b.mustSwitch) "¡Tu Pokémon está debilitado! Elige a otro." else "Elige un Pokémon."
            else -> ""
        }
        if (msg.isNotEmpty()) {
            Paints.dialogBox(canvas, msg, w, h)
        }
    }

    fun drawMon(canvas: Canvas, mon: Mon, cx: Float, cy: Float, r: Float, faceLeft: Boolean, time: Float) {
        canvas.drawOval(RectF(cx - r * 1.05f, cy + r * 0.85f, cx + r * 1.05f, cy + r * 1.15f), Paints.fill(0x44000000.toInt()))
        val bob = sin(time * 2.4f) * r * 0.04f
        val y = cy + bob
        val body = TypeColors.color(mon.species.types[0])
        val dir = if (faceLeft) -1f else 1f
        // Orejas / formas secundarias
        canvas.drawCircle(cx - dir * r * 0.55f, y - r * 0.72f, r * 0.22f, Paints.fill(body))
        canvas.drawCircle(cx + dir * r * 0.05f, y - r * 0.85f, r * 0.22f, Paints.fill(body))
        // Cuerpo
        canvas.drawCircle(cx, y, r, Paints.fill(body))
        // Vientre
        canvas.drawCircle(cx - dir * r * 0.15f, y + r * 0.30f, r * 0.52f, Paints.fill(Paints.lighten(body, 0.4f)))
        // Ojos
        canvas.drawCircle(cx + dir * r * 0.32f, y - r * 0.18f, r * 0.15f, Paints.fill(0xFF1B1B24.toInt()))
        canvas.drawCircle(cx + dir * r * 0.36f, y - r * 0.22f, r * 0.055f, Paints.fill(0xFFFFFFFF.toInt()))
        // Boca
        canvas.drawCircle(cx + dir * r * 0.15f, y + r * 0.05f, r * 0.05f, Paints.fill(0xFF1B1B24.toInt()))
        // Brillo de estado
        if (mon.hp <= mon.maxHp / 4) {
            val p = Paints.stroke(0x88E84C3D.toInt())
            canvas.drawCircle(cx, y, r * 1.12f, p)
        }
    }
}
