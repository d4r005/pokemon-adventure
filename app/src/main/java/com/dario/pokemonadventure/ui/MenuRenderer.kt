package com.dario.pokemonadventure.ui

import android.graphics.Canvas
import android.graphics.RectF
import com.dario.pokemonadventure.data.Dex
import com.dario.pokemonadventure.game.GameEngine
import com.dario.pokemonadventure.game.Screen
import com.dario.pokemonadventure.world.Regions
import java.util.Random

object MenuRenderer {

    fun title(canvas: Canvas, engine: GameEngine) {
        val w = canvas.width.toFloat()
        val h = canvas.height.toFloat()
        canvas.drawColor(0xFF2B1E4E.toInt())

        // Estrellas
        val rnd = Random(7)
        for (i in 0 until 60) {
            val x = rnd.nextFloat() * w
            val y = rnd.nextFloat() * h * 0.6f
            val r = 1.5f + rnd.nextFloat() * 1.5f
            canvas.drawCircle(x, y, r, Paints.fill(0x88FFFFFF.toInt()))
        }

        // Portales de las regiones
        for (i in Regions.ALL.indices) {
            val x = w * (0.10f + i * 0.10f)
            canvas.drawCircle(x, h * 0.14f, h * 0.030f, Paints.fill(0x66FFFFFF.toInt()))
            canvas.drawCircle(x, h * 0.14f, h * 0.018f, Paints.fill(Regions.ALL[i].palette.accent))
        }

        Paints.centerText(canvas, "POKÉMON", w / 2, h * 0.24f, 0xFFFFD54F.toInt(), h * 0.12f)
        Paints.centerText(canvas, "AVENTURA: TODAS LAS REGIONES", w / 2, h * 0.33f, 0xFFFFFFFF.toInt(), h * 0.05f)
        Paints.centerText(canvas, "Edición Leyendas, con portales de distorsión", w / 2, h * 0.38f, 0xFFBBAAFF.toInt(), h * 0.03f)
        Paints.drawButtons(canvas, engine.buttons, h)
        Paints.centerText(canvas, "Fan game no oficial", w / 2, h * 0.92f, 0x66FFFFFF.toInt(), h * 0.025f)
    }

    fun starter(canvas: Canvas, engine: GameEngine) {
        val w = canvas.width.toFloat()
        val h = canvas.height.toFloat()
        canvas.drawColor(0xFF1E3A2E.toInt())
        Paints.centerText(canvas, "¡Elige a tu compañero de aventura!", w / 2, h * 0.10f, 0xFFFFFFFF.toInt(), h * 0.05f)
        Paints.drawButtons(canvas, engine.buttons, h)
        Paints.centerText(canvas, "Cada región tiene sus propios secretos...", w / 2, h * 0.90f, 0x88FFFFFF.toInt(), h * 0.03f)
    }

    fun region(canvas: Canvas, engine: GameEngine) {
        val w = canvas.width.toFloat()
        val h = canvas.height.toFloat()
        canvas.drawColor(0xFF14162E.toInt())
        Paints.centerText(canvas, "Portales de Distorsión", w / 2, h * 0.055f, 0xFFE8A3D1.toInt(), h * 0.055f)
        Paints.centerText(canvas, "Elige tu destino", w / 2, h * 0.09f, 0xFFFFFFFF.toInt(), h * 0.03f)
        Paints.drawButtons(canvas, engine.buttons, h)
    }

    fun pause(canvas: Canvas, engine: GameEngine) {
        val w = canvas.width.toFloat()
        val h = canvas.height.toFloat()
        canvas.drawColor(0x99000000.toInt())
        Paints.centerText(canvas, "PAUSA", w * 0.50f, h * 0.06f, 0xFFFFFFFF.toInt(), h * 0.06f)
        Paints.drawButtons(canvas, engine.buttons, h)
        if (engine.menuMsgT > 0) {
            val box = RectF(w * 0.25f, h * 0.80f, w * 0.75f, h * 0.90f)
            canvas.drawRoundRect(box, h * 0.02f, h * 0.02f, Paints.fill(0xCC26303F.toInt()))
            Paints.centerText(canvas, engine.menuMsg, box.centerX(), box.centerY() + h * 0.015f, 0xFFFFFFFF.toInt(), h * 0.032f)
        }
    }

    fun team(canvas: Canvas, engine: GameEngine) {
        val w = canvas.width.toFloat()
        val h = canvas.height.toFloat()
        canvas.drawColor(0xFF26303F.toInt())
        Paints.centerText(canvas, "Tu Equipo", w * 0.28f, h * 0.05f, 0xFFFFFFFF.toInt(), h * 0.05f)
        Paints.leftText(canvas, "Toca un Pokémon sano para ponerlo al frente.", w * 0.05f, h * 0.10f, 0xFFBBBBBB.toInt(), h * 0.026f)
        Paints.drawButtons(canvas, engine.buttons, h)
    }

    fun shop(canvas: Canvas, engine: GameEngine) {
        val w = canvas.width.toFloat()
        val h = canvas.height.toFloat()
        canvas.drawColor(0xFF2E3440.toInt())
        Paints.centerText(canvas, "Tienda Pokémon", w * 0.25f, h * 0.05f, 0xFFFFFFFF.toInt(), h * 0.05f)
        Paints.centerText(canvas, "$${engine.player.money}", w * 0.85f, h * 0.05f, 0xFFFFD54F.toInt(), h * 0.045f)
        Paints.drawButtons(canvas, engine.buttons, h)
        if (engine.shopMsgT > 0) {
            val box = RectF(w * 0.25f, h * 0.80f, w * 0.75f, h * 0.90f)
            canvas.drawRoundRect(box, h * 0.02f, h * 0.02f, Paints.fill(0xCC26303F.toInt()))
            Paints.centerText(canvas, engine.shopMsg, box.centerX(), box.centerY() + h * 0.015f, 0xFFFFFFFF.toInt(), h * 0.032f)
        }
    }

    fun dex(canvas: Canvas, engine: GameEngine) {
        val w = canvas.width.toFloat()
        val h = canvas.height.toFloat()
        canvas.drawColor(0xFF1B2430.toInt())
        Paints.centerText(canvas, "Pokédex", w * 0.25f, h * 0.045f, 0xFFE8538B.toInt(), h * 0.05f)
        val caught = engine.player.dexCaught.size
        val seen = engine.player.dexSeen.size
        Paints.centerText(canvas, "Capturados: $caught / ${Dex.ALL.size}    Vistos: $seen", w * 0.25f, h * 0.085f, 0xFFFFFFFF.toInt(), h * 0.030f)
        Paints.drawButtons(canvas, engine.buttons, h)
    }

    fun dispatch(canvas: Canvas, engine: GameEngine) {
        when (engine.screen) {
            Screen.TITLE -> title(canvas, engine)
            Screen.STARTER -> starter(canvas, engine)
            Screen.REGION -> region(canvas, engine)
            Screen.MENU -> pause(canvas, engine)
            Screen.TEAM -> team(canvas, engine)
            Screen.SHOP -> shop(canvas, engine)
            Screen.DEX -> dex(canvas, engine)
            else -> {}
        }
    }
}
