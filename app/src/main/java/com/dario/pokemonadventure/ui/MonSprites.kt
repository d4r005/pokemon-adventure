package com.dario.pokemonadventure.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.dario.pokemonadventure.R

/**
 * Sprites de monstruos generados con IA (uno por tipo elemental).
 * Cada Pokémon usa el sprite de su tipo primario en las batallas.
 */
object MonSprites {

    private val cache = HashMap<String, Bitmap>()

    private val resIds = mapOf(
        "Normal" to R.drawable.mon_normal,
        "Fire" to R.drawable.mon_fire,
        "Water" to R.drawable.mon_water,
        "Grass" to R.drawable.mon_grass,
        "Electric" to R.drawable.mon_electric,
        "Ice" to R.drawable.mon_ice,
        "Fighting" to R.drawable.mon_fighting,
        "Poison" to R.drawable.mon_poison,
        "Ground" to R.drawable.mon_ground,
        "Flying" to R.drawable.mon_flying,
        "Psychic" to R.drawable.mon_psychic,
        "Bug" to R.drawable.mon_bug,
        "Rock" to R.drawable.mon_rock,
        "Ghost" to R.drawable.mon_ghost,
        "Dragon" to R.drawable.mon_dragon,
        "Dark" to R.drawable.mon_dark,
        "Steel" to R.drawable.mon_steel,
        "Fairy" to R.drawable.mon_fairy
    )

    fun get(context: Context, type: String): Bitmap? {
        val id = resIds[type] ?: return null
        return cache.getOrPut(type) {
            BitmapFactory.decodeResource(context.resources, id) ?: error("sprite no encontrado: $type")
        }
    }
}
