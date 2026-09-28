package com.dario.pokemonadventure.world

import com.dario.pokemonadventure.data.Mon

// ============================================================
// PALETAS Y REGIONES
// ============================================================

data class Palette(
    val grass: Int, val grassAlt: Int, val tall: Int, val path: Int,
    val water: Int, val tree: Int, val town: Int, val accent: Int
)

data class Encounter(val speciesId: Int, val minLevel: Int, val maxLevel: Int, val weight: Int)

data class TrainerDef(
    val name: String,
    val title: String,
    val team: List<Pair<Int, Int>>, // (speciesId, nivel)
    val reward: Int
)

/** Envoltorio de combate: definición + clave única para registrar la victoria. */
class TrainerFight(val def: TrainerDef, val key: String)

data class RegionDef(
    val id: Int,
    val name: String,
    val palette: Palette,
    val encounters: List<Encounter>,
    val seed: Long,
    val trainers: List<TrainerDef>
)

object Regions {
    val ALL: List<RegionDef> = listOf(
        RegionDef(0, "Kanto",
            Palette(0xFF5FA845.toInt(), 0xFF6FB84E.toInt(), 0xFF3F7D33.toInt(), 0xFFD8C48A.toInt(), 0xFF3E8ED0.toInt(), 0xFF2F6B2F.toInt(), 0xFFC0504D.toInt(), 0xFFE4572E.toInt()),
            listOf(
                Encounter(10, 2, 4, 25), Encounter(1, 3, 5, 10), Encounter(4, 3, 5, 10),
                Encounter(7, 3, 5, 10), Encounter(11, 2, 5, 10), Encounter(18, 3, 6, 15),
                Encounter(20, 3, 6, 10), Encounter(22, 3, 6, 15), Encounter(24, 2, 3, 5)
            ), 10101L,
            listOf(
                TrainerDef("Benito", "Joven", listOf(10 to 4, 1 to 5), 300),
                TrainerDef("Roberto", "Campeón de Kanto", listOf(6 to 10, 3 to 10, 9 to 10), 1500)
            )),
        RegionDef(1, "Johto",
            Palette(0xFF6FA349.toInt(), 0xFF7FB354.toInt(), 0xFF4E7D2F.toInt(), 0xFFD9C58B.toInt(), 0xFF3E8ED0.toInt(), 0xFF37502E.toInt(), 0xFFB0803C.toInt(), 0xFFE8A33D.toInt()),
            listOf(
                Encounter(11, 6, 10, 15), Encounter(15, 6, 11, 20), Encounter(10, 7, 10, 10),
                Encounter(20, 7, 11, 15), Encounter(22, 7, 11, 15), Encounter(12, 10, 12, 3),
                Encounter(13, 10, 12, 3), Encounter(14, 10, 12, 3)
            ), 20202L,
            listOf(
                TrainerDef("Camila", "Señorita", listOf(13 to 10, 12 to 10), 600),
                TrainerDef("Óscar", "Campeón de Johto", listOf(17 to 14, 15 to 14), 1800)
            )),
        RegionDef(2, "Hoenn",
            Palette(0xFF4FB06A.toInt(), 0xFF5DC07A.toInt(), 0xFF2F8C4E.toInt(), 0xFFE0D090.toInt(), 0xFF2BB3C0.toInt(), 0xFF1F7A4D.toInt(), 0xFFD97B29.toInt(), 0xFF17B0C4.toInt()),
            listOf(
                Encounter(27, 12, 16, 20), Encounter(22, 12, 17, 15), Encounter(20, 12, 17, 15),
                Encounter(29, 12, 15, 10), Encounter(18, 13, 17, 10), Encounter(26, 15, 18, 5),
                Encounter(28, 17, 18, 2)
            ), 30303L,
            listOf(
                TrainerDef("Bruno", "Joven", listOf(22 to 14, 20 to 14), 800),
                TrainerDef("Aixa", "Campeona de Hoenn", listOf(28 to 18, 26 to 18, 25 to 18), 2200)
            )),
        RegionDef(3, "Sinnoh",
            Palette(0xFF8FBF9F.toInt(), 0xFF9FCFAF.toInt(), 0xFF6B9B7B.toInt(), 0xFFD5CBA8.toInt(), 0xFF9BC7E8.toInt(), 0xFF4F7F5F.toInt(), 0xFF8A6BB1.toInt(), 0xFFBFE3F2.toInt()),
            listOf(
                Encounter(31, 18, 22, 25), Encounter(32, 18, 22, 25), Encounter(29, 18, 22, 10),
                Encounter(15, 19, 24, 15), Encounter(27, 18, 22, 10), Encounter(30, 23, 25, 4),
                Encounter(42, 25, 25, 1)
            ), 40404L,
            listOf(
                TrainerDef("Lucas", "Joven", listOf(31 to 20, 32 to 20), 1000),
                TrainerDef("Selene", "Campeona de Sinnoh", listOf(30 to 26, 28 to 26), 3000)
            )),
        RegionDef(4, "Unova",
            Palette(0xFF7FA070.toInt(), 0xFF8BB07C.toInt(), 0xFF5A8050.toInt(), 0xFFC9C2B0.toInt(), 0xFF5F9EA0.toInt(), 0xFF4A6B47.toInt(), 0xFF5F7D8C.toInt(), 0xFF90A4AE.toInt()),
            listOf(
                Encounter(18, 25, 29, 15), Encounter(20, 25, 30, 15), Encounter(27, 25, 30, 15),
                Encounter(11, 25, 29, 15), Encounter(19, 29, 31, 3), Encounter(26, 27, 31, 8),
                Encounter(25, 28, 30, 6)
            ), 50505L,
            listOf(
                TrainerDef("Nero", "Joven", listOf(18 to 27, 11 to 27), 1200),
                TrainerDef("Nico", "Campeón de Unova", listOf(28 to 32, 19 to 31), 2600)
            )),
        RegionDef(5, "Kalos",
            Palette(0xFF7C9E6B.toInt(), 0xFF8AAE79.toInt(), 0xFF58794A.toInt(), 0xFFD9CDB8.toInt(), 0xFF6FA8DC.toInt(), 0xFF4E7A45.toInt(), 0xFFA97BB5.toInt(), 0xFFE8A3D1.toInt()),
            listOf(
                Encounter(27, 3, 7, 20), Encounter(11, 3, 7, 20), Encounter(15, 4, 8, 15),
                Encounter(10, 3, 7, 10), Encounter(18, 4, 8, 10), Encounter(36, 6, 9, 6),
                Encounter(28, 8, 9, 2)
            ), 60606L,
            listOf(
                TrainerDef("Sarah", "Señorita", listOf(27 to 6, 15 to 7), 500),
                TrainerDef("Dafne", "Campeona de Kalos", listOf(3 to 12, 17 to 12, 6 to 12), 2000)
            )),
        RegionDef(6, "Alola",
            Palette(0xFF63B48A.toInt(), 0xFF74C49A.toInt(), 0xFF3F8F66.toInt(), 0xFFEAD9A8.toInt(), 0xFF2196F3.toInt(), 0xFF2F7D5C.toInt(), 0xFFEF8A5A.toInt(), 0xFFFFD166.toInt()),
            listOf(
                Encounter(34, 32, 36, 25), Encounter(10, 32, 36, 10), Encounter(11, 32, 36, 10),
                Encounter(36, 33, 37, 10), Encounter(26, 33, 37, 10), Encounter(35, 36, 38, 5),
                Encounter(33, 35, 37, 5)
            ), 70707L,
            listOf(
                TrainerDef("Ismael", "Capitán de Prueba", listOf(34 to 33, 11 to 33), 1500),
                TrainerDef("Kalani", "Kahuna de Alola", listOf(35 to 38, 33 to 38), 2800)
            )),
        RegionDef(7, "Galar",
            Palette(0xFF6B8F5E.toInt(), 0xFF7B9F6E.toInt(), 0xFF466A47.toInt(), 0xFFB5A98C.toInt(), 0xFF4C7A8C.toInt(), 0xFF3E5A44.toInt(), 0xFF8A4E6B.toInt(), 0xFF9E7BB5.toInt()),
            listOf(
                Encounter(37, 38, 42, 25), Encounter(39, 40, 43, 10), Encounter(31, 38, 42, 10),
                Encounter(29, 38, 42, 10), Encounter(40, 42, 45, 6), Encounter(41, 43, 45, 3),
                Encounter(30, 43, 45, 3)
            ), 80808L,
            listOf(
                TrainerDef("Hugo", "Joven", listOf(37 to 40, 39 to 41), 1800),
                TrainerDef("León", "Campeón de Galar", listOf(41 to 46, 40 to 45, 30 to 45), 3500)
            )),
        RegionDef(8, "Paldea",
            Palette(0xFF7BA85A.toInt(), 0xFF8BB86A.toInt(), 0xFF557F3D.toInt(), 0xFFE3CE96.toInt(), 0xFF3E9BD9.toInt(), 0xFF4E7A35.toInt(), 0xFFD97B4A.toInt(), 0xFFFF9E3D.toInt()),
            listOf(
                Encounter(40, 46, 49, 15), Encounter(36, 46, 49, 15), Encounter(30, 46, 50, 10),
                Encounter(26, 46, 50, 15), Encounter(33, 46, 49, 10), Encounter(42, 50, 52, 1),
                Encounter(28, 48, 50, 5)
            ), 90909L,
            listOf(
                TrainerDef("Naomi", "Estudiante", listOf(36 to 47, 30 to 47), 2200),
                TrainerDef("Gema", "Maestra Superior", listOf(40 to 52, 33 to 50, 28 to 50), 4000)
            ))
    )

    fun kalosIndex(): Int = ALL.indexOfFirst { it.name == "Kalos" }
}

// ============================================================
// JUGADOR
// ============================================================

class Player {
    var regionIndex: Int = Regions.kalosIndex()
    var x = 24.5f
    var y = 44.5f
    var facing = 2 // 0 arriba, 1 derecha, 2 abajo, 3 izquierda
    var money = 3000
    var box = 0
    var activeIndex = 0
    val party = ArrayList<Mon>()
    val items = LinkedHashMap<String, Int>()

    // Pokédex: especies vistas y capturadas
    val dexSeen = LinkedHashSet<Int>()
    val dexCaught = LinkedHashSet<Int>()

    // Claves de entrenadores vencidos ("region:indice")
    val beatenTrainers = LinkedHashSet<String>()

    init {
        items["Poké Ball"] = 10
        items["Poción"] = 5
    }

    fun activeMon(): Mon? = party.getOrNull(activeIndex)

    fun anyAlive(): Boolean = party.any { it.hp > 0 }

    fun healAll() {
        for (m in party) m.hp = m.maxHp
    }

    fun itemCount(name: String): Int = items[name] ?: 0
}
