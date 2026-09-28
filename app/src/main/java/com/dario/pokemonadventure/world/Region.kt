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
    val title: String, // vacío = sin título (p. ej. villanos)
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
    val trainers: List<TrainerDef>, // 5 célebres + jefe villano + 2 reclutas villanos
    val routeTrainers: Int // entrenadores de ruta según la densidad de cada generación
)

object Regions {

    // Cada especie aparece SOLO en su región de origen (9 generaciones).
    // Densidad de entrenadores de ruta: Gens 1-5 alta, 6 media, 7 reducida, 8-9 baja.
    val ALL: List<RegionDef> = listOf(

        // 0. KANTO (1ª generación, FireRed/LeafGreen ~458 combates)
        RegionDef(0, "Kanto",
            Palette(0xFF5FA845.toInt(), 0xFF6FB84E.toInt(), 0xFF3F7D33.toInt(), 0xFFD8C48A.toInt(), 0xFF3E8ED0.toInt(), 0xFF2F6B2F.toInt(), 0xFFC0504D.toInt(), 0xFFE4572E.toInt()),
            listOf(
                Encounter(10, 2, 4, 25),
                Encounter(1, 3, 5, 4), Encounter(2, 5, 7, 2), Encounter(3, 6, 8, 1),
                Encounter(4, 3, 5, 4), Encounter(5, 5, 7, 2), Encounter(6, 6, 8, 1),
                Encounter(7, 3, 5, 4), Encounter(8, 5, 7, 2), Encounter(9, 6, 8, 1),
                Encounter(43, 5, 8, 1),
                Encounter(11, 2, 5, 6), Encounter(12, 5, 7, 2), Encounter(13, 5, 7, 2), Encounter(14, 5, 7, 2),
                Encounter(15, 2, 6, 8), Encounter(16, 5, 7, 3), Encounter(17, 6, 8, 1),
                Encounter(18, 2, 6, 8), Encounter(44, 5, 7, 3), Encounter(19, 6, 8, 1),
                Encounter(20, 2, 6, 6), Encounter(45, 5, 7, 2), Encounter(21, 6, 8, 1),
                Encounter(22, 2, 6, 8), Encounter(46, 5, 7, 2), Encounter(23, 6, 8, 1),
                Encounter(24, 2, 5, 6), Encounter(25, 5, 8, 1), Encounter(26, 4, 7, 3)
            ), 10101L,
            listOf(
                TrainerDef("Benito", "Rival", listOf(10 to 5, 18 to 5), 250),
                TrainerDef("Brock", "Líder de Gimnasio", listOf(22 to 6, 46 to 7), 500),
                TrainerDef("Misty", "Líder de Gimnasio", listOf(7 to 9, 8 to 11), 800),
                TrainerDef("Lorelei", "Alto Mando", listOf(26 to 12, 25 to 12, 17 to 12), 1000),
                TrainerDef("Rojo", "Campeón de Kanto", listOf(6 to 14, 3 to 14, 9 to 14), 1500),
                TrainerDef("Giovanni", "", listOf(22 to 9, 46 to 10, 21 to 11), 2000),
                TrainerDef("Recluta Rocket", "", listOf(24 to 5, 22 to 5), 300),
                TrainerDef("Recluta Rocket", "", listOf(10 to 5, 18 to 5), 300)
            ), 8),

        // 1. JOHTO (2ª generación, HeartGold/SoulSilver ~400 combates)
        RegionDef(1, "Johto",
            Palette(0xFF6FA349.toInt(), 0xFF7FB354.toInt(), 0xFF4E7D2F.toInt(), 0xFFD9C58B.toInt(), 0xFF3E8ED0.toInt(), 0xFF37502E.toInt(), 0xFFB0803C.toInt(), 0xFFE8A33D.toInt()),
            listOf(
                Encounter(51, 8, 10, 5), Encounter(52, 10, 12, 2), Encounter(53, 12, 13, 1),
                Encounter(54, 8, 11, 5), Encounter(55, 11, 12, 2), Encounter(56, 12, 13, 1),
                Encounter(57, 8, 11, 5), Encounter(58, 11, 12, 2), Encounter(59, 12, 13, 1),
                Encounter(60, 10, 13, 2), Encounter(61, 10, 13, 2)
            ), 20202L,
            listOf(
                TrainerDef("Plata", "Rival", listOf(54 to 12, 57 to 13), 700),
                TrainerDef("Falkner", "Líder de Gimnasio", listOf(31 to 12, 48 to 13), 900),
                TrainerDef("Clair", "Líder de Gimnasio", listOf(58 to 16, 59 to 17), 1200),
                TrainerDef("Karen", "Alto Mando", listOf(61 to 17, 60 to 17), 1500),
                TrainerDef("Lance", "Campeón de Johto", listOf(25 to 20, 6 to 20), 2200),
                TrainerDef("Ejecutiva Rocket", "", listOf(61 to 18, 56 to 18), 2500),
                TrainerDef("Recluta Rocket", "", listOf(54 to 12, 57 to 12), 500),
                TrainerDef("Recluta Rocket", "", listOf(51 to 13, 65 to 13), 500)
            ), 8),

        // 2. HOENN (3ª generación, Emerald ~350 combates)
        RegionDef(2, "Hoenn",
            Palette(0xFF4FB06A.toInt(), 0xFF5DC07A.toInt(), 0xFF2F8C4E.toInt(), 0xFFE0D090.toInt(), 0xFF2BB3C0.toInt(), 0xFF1F7A4D.toInt(), 0xFFD97B29.toInt(), 0xFF17B0C4.toInt()),
            listOf(
                Encounter(27, 13, 16, 6), Encounter(47, 16, 18, 3), Encounter(28, 18, 19, 1),
                Encounter(62, 13, 17, 6), Encounter(63, 17, 19, 2), Encounter(64, 18, 19, 1)
            ), 30303L,
            listOf(
                TrainerDef("Aura", "Rival", listOf(27 to 17, 62 to 17), 1200),
                TrainerDef("Roxana", "Líder de Gimnasio", listOf(22 to 18, 62 to 18), 1400),
                TrainerDef("Marcial", "Líder de Gimnasio", listOf(20 to 19, 21 to 20), 1700),
                TrainerDef("Fátima", "Alto Mando", listOf(63 to 21, 28 to 21), 2000),
                TrainerDef("Esteban", "Campeón de Hoenn", listOf(64 to 23, 23 to 23, 3 to 23), 2600),
                TrainerDef("Maxie", "", listOf(63 to 19, 64 to 20, 23 to 18), 2800),
                TrainerDef("Recluta Aqua", "", listOf(24 to 14, 26 to 15), 700),
                TrainerDef("Recluta Magma", "", listOf(62 to 15, 63 to 15), 700)
            ), 8),

        // 3. SINNOH (4ª generación, Platinum ~700 combates con revanchas)
        RegionDef(3, "Sinnoh",
            Palette(0xFF8FBF9F.toInt(), 0xFF9FCFAF.toInt(), 0xFF6B9B7B.toInt(), 0xFFD5CBA8.toInt(), 0xFF9BC7E8.toInt(), 0xFF4F7F5F.toInt(), 0xFF8A6BB1.toInt(), 0xFFBFE3F2.toInt()),
            listOf(
                Encounter(31, 19, 22, 8), Encounter(48, 22, 24, 4), Encounter(49, 25, 27, 1),
                Encounter(32, 19, 22, 8), Encounter(50, 22, 24, 4), Encounter(33, 25, 27, 1),
                Encounter(29, 19, 22, 4), Encounter(30, 24, 27, 1),
                Encounter(42, 26, 26, 1) // leyenda de Hisui, ultra rara
            ), 40404L,
            listOf(
                TrainerDef("Berto", "Rival", listOf(31 to 23, 32 to 23), 1800),
                TrainerDef("Roco", "Líder de Gimnasio", listOf(22 to 24, 62 to 24), 2100),
                TrainerDef("Melisa", "Líder de Gimnasio", listOf(29 to 25, 20 to 25), 2400),
                TrainerDef("Lucio", "Alto Mando", listOf(44 to 26, 28 to 26), 2700),
                TrainerDef("Cintia", "Campeona de Sinnoh", listOf(30 to 28, 3 to 28, 9 to 28), 3200),
                TrainerDef("Cyrus", "", listOf(49 to 26, 33 to 27, 30 to 26), 3200),
                TrainerDef("Recluta Galaxia", "", listOf(32 to 21, 31 to 21), 900),
                TrainerDef("Recluta Galaxia", "", listOf(29 to 22, 50 to 22), 900)
            ), 10),

        // 4. UNOVA (5ª generación, Black/White 412 combates)
        RegionDef(4, "Unova",
            Palette(0xFF7FA070.toInt(), 0xFF8BB07C.toInt(), 0xFF5A8050.toInt(), 0xFFC9C2B0.toInt(), 0xFF5F9EA0.toInt(), 0xFF4A6B47.toInt(), 0xFF5F7D8C.toInt(), 0xFF90A4AE.toInt()),
            listOf(
                Encounter(65, 27, 30, 6), Encounter(66, 31, 34, 1),
                Encounter(67, 27, 31, 5), Encounter(68, 31, 33, 2), Encounter(69, 34, 35, 1)
            ), 50505L,
            listOf(
                TrainerDef("Chema", "Rival", listOf(67 to 30, 65 to 30), 2400),
                TrainerDef("Cirilo", "Líder de Gimnasio", listOf(51 to 31, 52 to 31), 2700),
                TrainerDef("Sila", "Líder de Gimnasio", listOf(31 to 32, 49 to 32), 3000),
                TrainerDef("N", "Alto Mando", listOf(66 to 34, 69 to 34), 3300),
                TrainerDef("Aldo", "Campeón de Unova", listOf(69 to 36, 6 to 36, 9 to 36), 3800),
                TrainerDef("Ghetsis", "", listOf(69 to 34, 66 to 34), 3600),
                TrainerDef("Recluta del Plasma", "", listOf(65 to 29, 67 to 30), 1100),
                TrainerDef("Recluta del Plasma", "", listOf(68 to 31, 66 to 31), 1100)
            ), 10),

        // 5. KALOS (6ª generación, X/Y 377 combates)
        RegionDef(5, "Kalos",
            Palette(0xFF7C9E6B.toInt(), 0xFF8AAE79.toInt(), 0xFF58794A.toInt(), 0xFFD9CDB8.toInt(), 0xFF6FA8DC.toInt(), 0xFF4E7A45.toInt(), 0xFFA97BB5.toInt(), 0xFFE8A3D1.toInt()),
            listOf(
                Encounter(70, 33, 37, 7), Encounter(71, 38, 40, 1),
                Encounter(72, 33, 37, 7), Encounter(73, 38, 40, 1)
            ), 60606L,
            listOf(
                TrainerDef("Sirena", "Rival", listOf(70 to 34, 72 to 34), 3000),
                TrainerDef("Cleto", "Líder de Gimnasio", listOf(32 to 35, 33 to 35), 3300),
                TrainerDef("Karina", "Líder de Gimnasio", listOf(34 to 36, 35 to 37), 3600),
                TrainerDef("Sigfrido", "Alto Mando", listOf(9 to 38, 26 to 38), 3900),
                TrainerDef("Dafne", "Campeona de Kalos", listOf(73 to 40, 3 to 40, 17 to 40), 4400),
                TrainerDef("Lysandre", "", listOf(73 to 39, 71 to 39, 3 to 38), 4000),
                TrainerDef("Recluta Flare", "", listOf(70 to 34, 72 to 35), 1300),
                TrainerDef("Recluta Flare", "", listOf(71 to 37, 73 to 37), 1300)
            ), 8),

        // 6. ALOLA (7ª generación, USUM ~250 combates)
        RegionDef(6, "Alola",
            Palette(0xFF63B48A.toInt(), 0xFF74C49A.toInt(), 0xFF3F8F66.toInt(), 0xFFEAD9A8.toInt(), 0xFF2196F3.toInt(), 0xFF2F7D5C.toInt(), 0xFFEF8A5A.toInt(), 0xFFFFD166.toInt()),
            listOf(
                Encounter(74, 37, 41, 5), Encounter(75, 41, 43, 2), Encounter(76, 44, 45, 1),
                Encounter(34, 37, 41, 6), Encounter(35, 42, 44, 1),
                Encounter(36, 38, 43, 3)
            ), 70707L,
            listOf(
                TrainerDef("Javi", "Rival", listOf(74 to 38, 34 to 39), 3600),
                TrainerDef("Ismael", "Capitán de Prueba", listOf(36 to 40, 11 to 40), 3900),
                TrainerDef("Kalani", "Kahuna", listOf(35 to 42, 76 to 42), 4200),
                TrainerDef("Karla", "Alto Mando", listOf(41 to 44, 49 to 44), 4500),
                TrainerDef("Kaimana", "Campeón de Alola", listOf(76 to 46, 30 to 46, 3 to 46), 5000),
                TrainerDef("Guzma", "", listOf(35 to 43, 36 to 43, 76 to 44), 4400),
                TrainerDef("Matón del Team Skull", "", listOf(34 to 40, 36 to 40), 1500),
                TrainerDef("Matón del Team Skull", "", listOf(11 to 41, 35 to 41), 1500)
            ), 6),

        // 7. GALAR (8ª generación, Sword/Shield ~150 combates)
        RegionDef(7, "Galar",
            Palette(0xFF6B8F5E.toInt(), 0xFF7B9F6E.toInt(), 0xFF466A47.toInt(), 0xFFB5A98C.toInt(), 0xFF4C7A8C.toInt(), 0xFF3E5A44.toInt(), 0xFF8A4E6B.toInt(), 0xFF9E7BB5.toInt()),
            listOf(
                Encounter(37, 41, 44, 7), Encounter(38, 45, 47, 2),
                Encounter(39, 42, 46, 5), Encounter(40, 48, 50, 1),
                Encounter(77, 41, 45, 7), Encounter(78, 46, 48, 2), Encounter(41, 49, 50, 1)
            ), 80808L,
            listOf(
                TrainerDef("Hugo", "Rival", listOf(37 to 42, 39 to 43), 4200),
                TrainerDef("Milo", "Líder de Gimnasio", listOf(51 to 44, 53 to 45), 4500),
                TrainerDef("Beda", "Líder de Gimnasio", listOf(44 to 46, 19 to 46), 4800),
                TrainerDef("Raúl", "Líder de Gimnasio", listOf(39 to 48, 40 to 49, 6 to 48), 5200),
                TrainerDef("León", "Campeón de Galar", listOf(41 to 50, 40 to 50, 30 to 50), 6000),
                TrainerDef("Oleana", "", listOf(78 to 48, 41 to 49, 40 to 49), 5000),
                TrainerDef("Seguidor del Team Yell", "", listOf(38 to 45, 37 to 44), 1700),
                TrainerDef("Seguidor del Team Yell", "", listOf(40 to 46, 77 to 46), 1700)
            ), 5),

        // 8. PALDEA (9ª generación, Scarlet/Violet ~100-150 combates, mundo abierto)
        RegionDef(8, "Paldea",
            Palette(0xFF7BA85A.toInt(), 0xFF8BB86A.toInt(), 0xFF557F3D.toInt(), 0xFFE3CE96.toInt(), 0xFF3E9BD9.toInt(), 0xFF4E7A35.toInt(), 0xFFD97B4A.toInt(), 0xFFFF9E3D.toInt()),
            listOf(
                Encounter(79, 45, 49, 5), Encounter(80, 50, 52, 2), Encounter(81, 54, 56, 1),
                Encounter(82, 45, 52, 8)
            ), 90909L,
            listOf(
                TrainerDef("Naomi", "Rival", listOf(82 to 46, 79 to 47), 5000),
                TrainerDef("Arvin", "Líder de Gimnasio", listOf(81 to 50, 34 to 50), 5400),
                TrainerDef("Rita", "Alto Mando", listOf(81 to 52, 19 to 52), 5800),
                TrainerDef("Larry", "Alto Mando", listOf(26 to 54, 6 to 54), 6200),
                TrainerDef("Gema", "Maestra Superior", listOf(69 to 56, 81 to 56, 66 to 56), 7000),
                TrainerDef("Penny", "", listOf(81 to 54, 82 to 53, 80 to 53), 5500),
                TrainerDef("Recluta del Team Star", "", listOf(82 to 49, 79 to 48), 2000),
                TrainerDef("Recluta del Team Star", "", listOf(80 to 50, 82 to 51), 2000)
            ), 5)
    )

    fun kalosIndex(): Int = ALL.indexOfFirst { it.name == "Kalos" }
}

// ============================================================
// JUGADOR
// ============================================================

class Player {
    var regionIndex: Int = 0 // Kanto, como en Pokémon Amarillo
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

    // Regalos recibidos ("gift:<speciesId>"), estilo Pokémon Amarillo
    val giftsReceived = LinkedHashSet<String>()

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
