package com.dario.pokemonadventure

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.dario.pokemonadventure.game.Screen

class MainActivity : Activity() {

    lateinit var gameView: GameView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        gameView = GameView(this)
        setContentView(gameView)
        hideSystemBars()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemBars()
    }

    private fun hideSystemBars() {
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            )
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val screen = gameView.engine.screen
        when (screen) {
            Screen.WORLD -> gameView.engine.uiMenu()
            Screen.MENU, Screen.TEAM, Screen.SHOP -> gameView.engine.screen = Screen.WORLD
            Screen.DEX -> gameView.engine.screen = Screen.MENU
            Screen.BATTLE -> { /* no se puede huir con atrás */ }
            Screen.REGION -> gameView.engine.screen = Screen.TITLE
            else -> super.onBackPressed()
        }
    }
}
