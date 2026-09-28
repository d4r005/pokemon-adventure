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
    val trainerIndex: Int = -1,
    val giftId: Int = -1
) {
    enum class Kind { NURSE, SHOP, GUIDE, TRAINER, GIFT }
}

class TileMap(val def: RegionDef) {

    // Mapas grandes: regiones de verdad para cientos de entrenadores
    val size = 144
    val tiles = IntArray(size * size)
    val townX = size / 2
    val townY = 48
    val spawnX = size / 2
    val spawnY = size - 6
    val npcs = ArrayList<Npc>()

    // Entrenadores: los fijos de la región + los generados de ruta
    private val routeTrainerDefs = ArrayList<TrainerDef>()
    val trainers: List<TrainerDef> get() = def.trainers + routeTrainerDefs

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

    companion object {
        private val CLASSES = listOf(
            "Joven", "Señorita", "Escolar", "Pescador", "Montañero",
            "Campista", "Cazabichos", "Guitarrista", "Escolar", "Runner",
            "Pícnic", "Policía", "Marinero", "Piloto", "Domador",
            "Químico", "Científico", "Pijama", "Bella", "Ornamentista"
        )
        private val NAMES = listOf(
            "Alex", "Marta", "Iván", "Lucía", "Dani", "Rosa", "Pablo", "Elena",
            "Sergio", "Nadia", "Bruno", "Claudia", "Hugo", "Mía", "Óscar", "Vera",
            "Teo", "Alma", "Nico", "Iris", "Chema", "Lola", "Jacobo", "Alba",
            "Iker", "Paola", "Rodri", "Carmen", "Mateo", "Julia", "Axl", "Nora"
        )
    }

    /**
     * Genera un entrenador de ruta. Su nivel escala con la distancia al punto
     * de aparición: cuanto más lejos, más fuerte, como en los juegos clásicos.
     */
    private fun genRouteTrainer(distFactor: Float): TrainerDef {
        val enc = def.encounters
        val count = 2 + rnd.nextInt(3) // 2-4 Pokémon
        val team = ArrayList<Pair<Int, Int>>()
        var lvlSum = 0
        repeat(count) {
            val e = enc[rnd.nextInt(enc.size)]
            val span = (e.maxLevel - e.minLevel).coerceAtLeast(0)
            var lvl = (e.minLevel + span * (0.2f + 0.8f * distFactor)).toInt()
            lvl += rnd.nextInt(3) - 1
            lvl = lvl.coerceIn(e.minLevel, e.maxLevel + 2)
            lvlSum += lvl
            team.add(e.speciesId to lvl)
        }
        val cls = CLASSES[rnd.nextInt(CLASSES.size)]
        val name = NAMES[rnd.nextInt(NAMES.size)]
        val reward = (lvlSum / count.coerceAtLeast(1)) * 12 + 100
        return TrainerDef(name, cls, team, reward)
    }

    private fun generate() {
        // 1. Base de hierba
        for (y in 0 until size) {
            for (x in 0 until size) set(x, y, Tile.GRASS)
        }

        // 2. Flores decorativas
        repeat(size * 2) {
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
        repeat(40) {
            val cx = 4 + rnd.nextInt(size - 8)
            val cy = 4 + rnd.nextInt(size - 8)
            if (abs(cx - townX) < 12 && cy in (townY - 12)..(townY + 12)) return@repeat
            repeat(8) {
                val x = (cx - 3 + rnd.nextInt(7)).coerceIn(2, size - 3)
                val y = (cy - 3 + rnd.nextInt(7)).coerceIn(2, size - 3)
                set(x, y, Tile.TREE)
            }
        }

        // 5. Lagos
        repeat(3) {
            val cx = 8 + rnd.nextInt(size - 16)
            val cy = 8 + rnd.nextInt(size - 16)
            val r = 4 + rnd.nextInt(4)
            for (dy in -r..r) {
                for (dx in -r..r) {
                    if (dx * dx + dy * dy <= r * r) set(cx + dx, cy + dy, Tile.WATER)
                }
            }
        }

        // 6. Parches de hierba alta (encuentros)
        repeat(26) {
            val cx = 3 + rnd.nextInt(size - 6)
            val cy = 3 + rnd.nextInt(size - 6)
            val r = 3 + rnd.nextInt(4)
            for (dy in -r..r) {
                for (dx in -r..r) {
                    if (dx * dx + dy * dy <= r * r) set(cx + dx, cy + dy, Tile.TALL)
                }
            }
        }

        // 7. Rocas
        repeat(60) {
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

        // 15. Entrenadores fijos de la región (índices estables, compatible con partidas):
        //     0-4 célebres (rival, 2 líderes, Alto Mando, campeón)
        //     5 jefe del equipo villano (custodia el portal)
        //     6-7 reclutas villanos en el camino
        val spots = listOf(
            intArrayOf(townX - 1, spawnY - 3),   // 0 rival, junto a la plaza inicial
            intArrayOf(townX, spawnY - 14),      // 1 líder, mitad del camino
            intArrayOf(townX + 1, townY + 6),    // 2 líder, entrada del pueblo
            intArrayOf(townX - 3, townY + 2),    // 3 alto mando, dentro del pueblo
            intArrayOf(townX - 1, townY - 6),    // 4 campeón, corredor norte
            intArrayOf(townX + 1, townY - 6),    // 5 jefe villano, corredor norte
            intArrayOf(townX, spawnY - 6),       // 6 recluta villano
            intArrayOf(townX, spawnY - 24)      // 7 recluta villano
        )
        for (i in def.trainers.indices) {
            val s = spots[i % spots.size]
            set(s[0], s[1], Tile.PATH)
            npcs.add(Npc(s[0], s[1], Npc.Kind.TRAINER, def.trainers[i].name, i))
        }

        // 16. TODOS los entrenadores de ruta, con el conteo de cada entrega:
        //     la densidad real de cada generación (Kanto 450, Sinnoh 692, ...).
        //     Se colocan en terreno transitable, con separación mínima y nivel
        //     creciente según la distancia al inicio.
        val base = def.trainers.size
        val total = def.routeTrainers
        var placed = 0
        var guard = 0
        val maxDist = (size * 1.6f)
        while (placed < total && guard < 40000) {
            guard++
            val x = 3 + rnd.nextInt(size - 6)
            val y = 8 + rnd.nextInt(size - 16)
            val t = tileAt(x, y)
            if (t != Tile.GRASS && t != Tile.TALL && t != Tile.FLOWER && t != Tile.PATH) continue
            // fuera del pueblo, del inicio y del portal
            if (abs(x - townX) < 10 && y in (townY - 11)..(townY + 11)) continue
            if (abs(x - spawnX) + abs(y - spawnY) < 6) continue
            if (abs(x - townX) + abs(y - (townY - 8)) < 5) continue
            // separación mínima con cualquier otro NPC
            var clash = false
            for (n in npcs) {
                if (abs(n.x - x) + abs(n.y - y) < 2) {
                    clash = true
                    break
                }
            }
            if (clash) continue
            val distFactor = ((abs(x - spawnX) + abs(y - spawnY)).toFloat() / maxDist).coerceIn(0f, 1f)
            val td = genRouteTrainer(distFactor)
            routeTrainerDefs.add(td)
            npcs.add(Npc(x, y, Npc.Kind.TRAINER, td.name, base + routeTrainerDefs.size - 1))
            placed++
        }

        // 17. Regalos estilo Pokémon Amarillo (solo en Kanto):
        //     Bulbasaur, Charmander y Squirtle entregados por NPCs del pueblo.
        if (def.id == 0) {
            npcs.add(Npc(townX - 3, townY - 2, Npc.Kind.GIFT, "Chica de las plantas", giftId = 1))
            npcs.add(Npc(townX + 3, townY - 2, Npc.Kind.GIFT, "Chico entusiasta", giftId = 4))
            npcs.add(Npc(townX + 3, townY + 2, Npc.Kind.GIFT, "Marinera", giftId = 7))
        }
    }
}
