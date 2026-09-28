package com.dario.pokemonadventure.world

import kotlin.math.abs

object Tile {
    const val GRASS = 0
    const val TALL = 1
    const val TREE = 2
    const val WATER = 3
    const val PATH = 4
    const val TOWN = 5
    const val PORTAL = 6
    const val FLOWER = 7
    const val ROCK = 8
    const val PAVEMENT = 9
}

class Npc(
    val x: Int,
    val y: Int,
    val kind: Kind,
    val name: String,
    val trainerIndex: Int = -1
) {
    enum class Kind { NURSE, SHOP, GUIDE, TRAINER }
}

class TileMap(val def: RegionDef) {

    val size = 48
    val tiles = IntArray(size * size)
    val spawnX = 24
    val spawnY = 44
    val townX = 24
    val townY = 20
    val npcs = ArrayList<Npc>()
    val trainers: List<TrainerDef> = def.trainers
    private val rnd = java.util.Random(def.seed)

    init {
        generate()
    }

    private fun set(x: Int, y: Int, t: Int) {
        if (x in 0 until size && y in 0 until size) tiles[y * size + x] = t
    }

    fun tileAt(tx: Int, ty: Int): Int {
        if (tx < 0 || ty < 0 || tx >= size || ty >= size) return Tile.TREE
        return tiles[ty * size + tx]
    }

    fun blocked(tx: Int, ty: Int): Boolean {
        val t = tileAt(tx, ty)
        return t == Tile.TREE || t == Tile.WATER || t == Tile.TOWN || t == Tile.ROCK
    }

    fun npcNear(px: Float, py: Float, maxDist: Float): Npc? {
        return npcs.minByOrNull {
            (abs(it.x + 0.5f - px) + abs(it.y + 0.5f - py))
        }?.takeIf {
            (abs(it.x + 0.5f - px) + abs(it.y + 0.5f - py)) <= maxDist
        }
    }

    private fun generate() {
        // 1. Base de hierba
        for (y in 0 until size) {
            for (x in 0 until size) set(x, y, Tile.GRASS)
        }

        // 2. Flores decorativas
        repeat(45) {
            val x = rnd.nextInt(size)
            val y = rnd.nextInt(size)
            if (rnd.nextInt(100) < 40) set(x, y, Tile.FLOWER)
        }

        // 3. Borde de árboles
        for (i in 0 until size) {
            set(i, 0, Tile.TREE); set(i, 1, Tile.TREE)
            set(0, i, Tile.TREE); set(1, i, Tile.TREE)
            set(i, size - 1, Tile.TREE); set(size - 1, i, Tile.TREE)
        }

        // 4. Bosquecillos
        repeat(10) {
            val cx = 4 + rnd.nextInt(size - 8)
            val cy = 4 + rnd.nextInt(size - 8)
            if (abs(cx - townX) < 9 && cy in 8..30) return@repeat
            repeat(6) {
                val x = (cx - 2 + rnd.nextInt(5)).coerceIn(2, size - 3)
                val y = (cy - 2 + rnd.nextInt(5)).coerceIn(2, size - 3)
                set(x, y, Tile.TREE)
            }
        }

        // 5. Lago
        run {
            val cx = 6 + rnd.nextInt(size - 12)
            val cy = 6 + rnd.nextInt(size - 12)
            val r = 3 + rnd.nextInt(3)
            for (dy in -r..r) {
                for (dx in -r..r) {
                    if (dx * dx + dy * dy <= r * r) set(cx + dx, cy + dy, Tile.WATER)
                }
            }
        }

        // 6. Parches de hierba alta (encuentros)
        repeat(7) {
            val cx = 3 + rnd.nextInt(size - 6)
            val cy = 3 + rnd.nextInt(size - 6)
            val r = 2 + rnd.nextInt(3)
            for (dy in -r..r) {
                for (dx in -r..r) {
                    if (dx * dx + dy * dy <= r * r) set(cx + dx, cy + dy, Tile.TALL)
                }
            }
        }

        // 7. Rocas
        repeat(18) {
            set(2 + rnd.nextInt(size - 4), 2 + rnd.nextInt(size - 4), Tile.ROCK)
        }

        // 8. Plaza del pueblo
        for (y in townY - 4..townY + 4) {
            for (x in townX - 5..townX + 5) set(x, y, Tile.PAVEMENT)
        }

        // 9. Edificios (anillo)
        for (x in townX - 5..townX + 5) {
            set(x, townY - 4, Tile.TOWN)
            set(x, townY + 4, Tile.TOWN)
        }
        for (y in townY - 4..townY + 4) {
            set(townX - 5, y, Tile.TOWN)
            set(townX + 5, y, Tile.TOWN)
        }

        // 10. Puertas
        for (x in townX - 1..townX + 1) {
            set(x, townY - 4, Tile.PAVEMENT)
            set(x, townY + 4, Tile.PAVEMENT)
        }

        // 11. Corredor del pueblo al punto de aparición
        for (y in townY + 4..spawnY) {
            set(townX - 1, y, Tile.PATH)
            set(townX, y, Tile.PATH)
            set(townX + 1, y, Tile.PATH)
        }

        // 12. Plaza inicial
        for (y in spawnY - 2..spawnY) {
            for (x in townX - 2..townX + 2) set(x, y, Tile.PAVEMENT)
        }

        // 13. Corredor norte y portal de distorsión
        for (y in townY - 9..townY - 5) {
            set(townX - 1, y, Tile.PAVEMENT)
            set(townX, y, Tile.PAVEMENT)
            set(townX + 1, y, Tile.PAVEMENT)
        }
        set(townX, townY - 8, Tile.PORTAL)

        // 14. NPCs de servicios
        npcs.add(Npc(townX - 2, townY, Npc.Kind.NURSE, "Enfermera Joy"))
        npcs.add(Npc(townX + 2, townY, Npc.Kind.SHOP, "Tendero"))
        npcs.add(Npc(townX, townY + 2, Npc.Kind.GUIDE, "Guía"))

        // 15. Entrenadores rivales (sobre el corredor, garantizamos suelo firme)
        val t1 = intArrayOf(townX, spawnY - 8)
        val t2 = intArrayOf(townX + 1, townY + 6)
        val spots = listOf(t1, t2)
        for (i in trainers.indices) {
            val s = spots[i % spots.size]
            set(s[0], s[1], Tile.PATH)
            npcs.add(Npc(s[0], s[1], Npc.Kind.TRAINER, trainers[i].name, i))
        }
    }
}
