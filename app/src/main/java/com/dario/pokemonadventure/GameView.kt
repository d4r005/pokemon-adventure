package com.dario.pokemonadventure

import android.content.Context
import android.graphics.Canvas
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.dario.pokemonadventure.game.GameEngine
import java.util.concurrent.ConcurrentLinkedQueue

class GameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback, Runnable {

    val engine = GameEngine(context)

    private var gameThread: Thread? = null

    @Volatile
    private var running = false

    private val touches = ConcurrentLinkedQueue<Triple<Int, Float, Float>>()

    init {
        holder.addCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {}

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        if (gameThread == null) {
            running = true
            gameThread = Thread(this).also { it.start() }
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        running = false
        gameThread?.join()
        gameThread = null
    }

    override fun run() {
        var last = System.nanoTime()
        while (running) {
            val now = System.nanoTime()
            val dt = ((now - last) / 1_000_000_000.0).coerceAtMost(0.05)
            last = now

            while (true) {
                val t = touches.poll() ?: break
                engine.handleTouch(t.first, t.second, t.third)
            }

            engine.update(dt.toFloat())

            val c = holder.lockCanvas() ?: continue
            try {
                engine.render(c)
            } finally {
                holder.unlockCanvasAndPost(c)
            }
            Thread.sleep(16)
        }
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        touches.add(Triple(e.actionMasked, e.x, e.y))
        return true
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_W -> engine.setDpad(0)
            KeyEvent.KEYCODE_DPAD_RIGHT, KeyEvent.KEYCODE_D -> engine.setDpad(1)
            KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_S -> engine.setDpad(2)
            KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_A -> engine.setDpad(3)
            KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_SPACE -> engine.uiConfirm()
            KeyEvent.KEYCODE_ESCAPE, KeyEvent.KEYCODE_M, KeyEvent.KEYCODE_MENU -> engine.uiMenu()
            else -> return super.onKeyDown(keyCode, event)
        }
        return true
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_W -> if (engine.dpadDir == 0) engine.setDpad(-1)
            KeyEvent.KEYCODE_DPAD_RIGHT, KeyEvent.KEYCODE_D -> if (engine.dpadDir == 1) engine.setDpad(-1)
            KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_S -> if (engine.dpadDir == 2) engine.setDpad(-1)
            KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_A -> if (engine.dpadDir == 3) engine.setDpad(-1)
            else -> return super.onKeyUp(keyCode, event)
        }
        return true
    }
}
