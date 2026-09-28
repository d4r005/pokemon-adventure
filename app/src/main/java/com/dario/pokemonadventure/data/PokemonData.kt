package com.dario.pokemonadventure.data

import kotlin.math.min

// ============================================================
// TIPOS
// ============================================================

object TypeChart {
    private val chart: Map<String, Map<String, Double>> = mapOf(
        "Normal" to mapOf("Rock" to 0.5, "Ghost" to 0.0, "Steel" to 0.5),
        "Fire" to mapOf("Fire" to 0.5, "Water" to 0.5, "Rock" to 0.5, "Dragon" to 0.5, "Grass" to 2.0, "Ice" to 2.0, "Bug" to 2.0, "Steel" to 2.0),
        "Water" to mapOf("Water" to 0.5, "Grass" to 0.5, "Dragon" to 0.5, "Fire" to 2.0, "Ground" to 2.0, "Rock" to 2.0),
        "Grass" to mapOf("Fire" to 0.5, "Grass" to 0.5, "Poison" to 0.5, "Flying" to 0.5, "Bug" to 0.5, "Steel" to 0.5, "Dragon" to 0.5, "Water" to 2.0, "Ground" to 2.0, "Rock" to 2.0),
        "Electric" to mapOf("Electric" to 0.5, "Grass" to 0.5, "Ground" to 0.0, "Dragon" to 0.5, "Water" to 2.0, "Flying" to 2.0),
        "Ice" to mapOf("Fire" to 0.5, "Water" to 0.5, "Ice" to 0.5, "Steel" to 0.5, "Grass" to 2.0, "Ground" to 2.0, "Flying" to 2.0, "Dragon" to 2.0),
        "Fighting" to mapOf("Flying" to 0.5, "Poison" to 0.5, "Bug" to 0.5, "Psychic" to 0.5, "Fairy" to 0.5, "Ghost" to 0.0, "Normal" to 2.0, "Ice" to 2.0, "Rock" to 2.0, "Dark" to 2.0, "Steel" to 2.0),
        "Poison" to mapOf("Poison" to 0.5, "Ground" to 0.5, "Rock" to 0.5, "Ghost" to 0.5, "Steel" to 0.0, "Grass" to 2.0, "Fairy" to 2.0),
        "Ground" to mapOf("Grass" to 0.5, "Bug" to 0.5, "Flying" to 0.0, "Poison" to 2.0, "Rock" to 2.0, "Fire" to 2.0, "Electric" to 2.0, "Steel" to 2.0),
        "Flying" to mapOf("Electric" to 0.5, "Rock" to 0.5, "Steel" to 0.5, "Grass" to 2.0, "Fighting" to 2.0, "Bug" to 2.0),
        "Psychic" to mapOf("Psychic" to 0.5, "Dark" to 0.0, "Steel" to 0.5, "Fighting" to 2.0, "Poison" to 2.0),
        "Bug" to mapOf("Fire" to 0.5, "Fighting" to 0.5, "Flying" to 0.5, "Poison" to 0.5, "Ghost" to 0.5, "Steel" to 0.5, "Fairy" to 0.5, "Grass" to 2.0, "Psychic" to 2.0, "Dark" to 2.0),
        "Rock" to mapOf("Fighting" to 0.5, "Ground" to 0.5, "Steel" to 0.5, "Fire" to 2.0, "Ice" to 2.0, "Flying" to 2.0, "Bug" to 2.0),
        "Ghost" to mapOf("Dark" to 0.5, "Normal" to 0.0, "Psychic" to 2.0, "Ghost" to 2.0),
        "Dragon" to mapOf("Steel" to 0.5, "Fairy" to 0.0, "Dragon" to 2.0),
        "Dark" to mapOf("Fighting" to 0.5, "Dark" to 0.5, "Fairy" to 0.5, "Psychic" to 2.0, "Ghost" to 2.0),
        "Steel" to mapOf("Fire" to 0.5, "Water" to 0.5, "Electric" to 0.5, "Steel" to 0.5, "Ice" to 2.0, "Rock" to 2.0, "Fairy" to 2.0),
        "Fairy" to mapOf("Fire" to 0.5, "Poison" to 0.5, "Steel" to 0.5, "Fighting" to 2.0, "Dragon" to 2.0, "Dark" to 2.0)
    )

    fun effectiveness(moveType: String, defTypes: List<String>): Double {
        var mult = 1.0
        for (t in defTypes) mult *= (chart[moveType]?.get(t) ?: 1.0)
        return mult
    }
}

object TypeColors {
    private val map = mapOf(
        "Normal" to 0xFF9E9E9E, "Fire" to 0xFFE8510D, "Water" to 0xFF2E7DD1,
        "Grass" to 0xFF3E9B3E, "Electric" to 0xFFE8C20D, "Ice" to 0xFF6FD5E8,
        "Fighting" to 0xFFC03028, "Poison" to 0xFF8A3E8A, "Ground" to 0xFFC2A83C,
        "Flying" to 0xFF7C8FD9, "Psychic" to 0xFFE8538B, "Bug" to 0xFF8FA82E,
        "Rock" to 0xFFA69048, "Ghost" to 0xFF5A4E8C, "Dragon" to 0xFF5A4ED9,
        "Dark" to 0xFF5A4E42, "Steel" to 0xFF8A90A8, "Fairy" to 0xFFE88BC0
    )
    fun color(type: String): Int = ((map[type] ?: 0xFF9E9E9E).toInt())
}

// ============================================================
// MOVIMIENTOS
// ============================================================

data class Move(val name: String, val type: String, val power: Int, val accuracy: Int)

object Moves {
    val PLACAJE = Move("Placaje", "Normal", 40, 100)
    val ARANIAZO = Move("Arañazo", "Normal", 40, 100)
    val AT_RAPIDO = Move("Ataque Rápido", "Normal", 40, 100)
    val GOLPE_CUERPO = Move("Golpe Cuerpo", "Normal", 85, 100)
    val MORDISCO = Move("Mordisco", "Dark", 60, 100)
    val GOLPE_KARATE = Move("Golpe Kárate", "Fighting", 50, 100)
    val PUNO_TRUENO = Move("Puño Trueno", "Electric", 75, 100)
    val PUNO_FUEGO = Move("Puño Fuego", "Fire", 75, 100)
    val PUNO_HIELO = Move("Puño Hielo", "Ice", 75, 100)
    val ASCUAS = Move("Ascuas", "Fire", 40, 100)
    val LANZALLAMAS = Move("Lanzallamas", "Fire", 90, 100)
    val LATIGO_CEPA = Move("Látigo Cepa", "Grass", 45, 100)
    val HOJA_AFILADA = Move("Hoja Afilada", "Grass", 55, 95)
    val RAYO_SOLAR = Move("Rayo Solar", "Grass", 120, 100)
    val ASPERSOR = Move("Aspersor", "Water", 40, 100)
    val SURF = Move("Surf", "Water", 90, 100)
    val HIDROBOMBA = Move("Hidrobomba", "Water", 110, 80)
    val IMPACTRUENO = Move("Impactrueno", "Electric", 40, 100)
    val RAYO = Move("Rayo", "Electric", 90, 100)
    val TRUENO = Move("Trueno", "Electric", 110, 70)
    val CONFUSION = Move("Confusión", "Psychic", 50, 100)
    val PSIQUICO = Move("Psíquico", "Psychic", 90, 100)
    val LENGUETAZO = Move("Lengüetazo", "Ghost", 30, 100)
    val BOLA_SOMBRA = Move("Bola Sombra", "Ghost", 80, 100)
    val LANZA_ROCAS = Move("Lanza Rocas", "Rock", 50, 90)
    val AVALANCHA = Move("Avalancha", "Rock", 75, 90)
    val BOMBA_LODO = Move("Bomba Lodo", "Poison", 90, 100)
    val TERREMOTO = Move("Terremoto", "Ground", 100, 100)
    val PICOTAZO = Move("Picotazo", "Flying", 35, 100)
    val ATAQUE_ALA = Move("Ataque Ala", "Flying", 60, 100)
    val VIENTO_HIELO = Move("Viento Hielo", "Ice", 55, 95)
    val RAYO_HIELO = Move("Rayo Hielo", "Ice", 90, 100)
    val ALIENTO_DRAGON = Move("Aliento Dragón", "Dragon", 60, 100)
    val GARRA_METAL = Move("Garra Metal", "Steel", 50, 95)
    val BRILLO_MAGICO = Move("Brillo Mágico", "Fairy", 80, 100)
    val SENTENCIA = Move("Sentencia", "Normal", 100, 100)
}

// ============================================================
// ESPECIES (Pokédex)
// ============================================================

data class Species(
    val id: Int,
    val name: String,
    val types: List<String>,
    val baseHp: Int,
    val baseAtk: Int,
    val baseDef: Int,
    val baseSpd: Int,
    val catchRate: Int,
    val moves: List<Pair<Int, Move>> // nivel -> movimiento
)

object Dex {
    val ALL: List<Species> = listOf(
        Species(1, "Bulbasaur", listOf("Grass", "Poison"), 45, 49, 49, 45, 45, listOf(1 to Moves.LATIGO_CEPA, 1 to Moves.PLACAJE, 7 to Moves.HOJA_AFILADA, 13 to Moves.BOMBA_LODO, 20 to Moves.RAYO_SOLAR)),
        Species(2, "Ivysaur", listOf("Grass", "Poison"), 60, 62, 63, 60, 45, listOf(1 to Moves.LATIGO_CEPA, 1 to Moves.PLACAJE, 7 to Moves.HOJA_AFILADA, 15 to Moves.BOMBA_LODO, 24 to Moves.RAYO_SOLAR)),
        Species(3, "Venusaur", listOf("Grass", "Poison"), 80, 82, 83, 80, 45, listOf(1 to Moves.LATIGO_CEPA, 7 to Moves.HOJA_AFILADA, 15 to Moves.BOMBA_LODO, 24 to Moves.RAYO_SOLAR, 30 to Moves.GOLPE_CUERPO)),
        Species(4, "Charmander", listOf("Fire"), 39, 52, 43, 65, 45, listOf(1 to Moves.ARANIAZO, 1 to Moves.ASCUAS, 13 to Moves.LANZALLAMAS, 24 to Moves.GOLPE_CUERPO)),
        Species(5, "Charmeleon", listOf("Fire"), 58, 64, 58, 80, 45, listOf(1 to Moves.ASCUAS, 13 to Moves.LANZALLAMAS, 24 to Moves.GOLPE_CUERPO)),
        Species(6, "Charizard", listOf("Fire", "Flying"), 78, 84, 78, 100, 45, listOf(1 to Moves.ASCUAS, 13 to Moves.LANZALLAMAS, 24 to Moves.ATAQUE_ALA, 30 to Moves.GOLPE_CUERPO)),
        Species(7, "Squirtle", listOf("Water"), 44, 48, 65, 43, 45, listOf(1 to Moves.PLACAJE, 1 to Moves.ASPERSOR, 13 to Moves.SURF, 24 to Moves.HIDROBOMBA)),
        Species(8, "Wartortle", listOf("Water"), 59, 63, 80, 58, 45, listOf(1 to Moves.ASPERSOR, 13 to Moves.SURF, 24 to Moves.HIDROBOMBA)),
        Species(9, "Blastoise", listOf("Water"), 79, 83, 100, 78, 45, listOf(1 to Moves.ASPERSOR, 13 to Moves.SURF, 24 to Moves.HIDROBOMBA)),
        Species(10, "Pikachu", listOf("Electric"), 35, 55, 40, 90, 190, listOf(1 to Moves.IMPACTRUENO, 1 to Moves.PLACAJE, 10 to Moves.AT_RAPIDO, 16 to Moves.RAYO, 26 to Moves.TRUENO)),
        Species(11, "Eevee", listOf("Normal"), 55, 55, 50, 55, 45, listOf(1 to Moves.PLACAJE, 1 to Moves.AT_RAPIDO, 8 to Moves.MORDISCO, 15 to Moves.GOLPE_CUERPO)),
        Species(12, "Jolteon", listOf("Electric"), 65, 65, 60, 130, 45, listOf(1 to Moves.IMPACTRUENO, 13 to Moves.RAYO, 26 to Moves.TRUENO)),
        Species(13, "Vaporeon", listOf("Water"), 130, 65, 60, 65, 45, listOf(1 to Moves.ASPERSOR, 13 to Moves.SURF, 26 to Moves.HIDROBOMBA)),
        Species(14, "Flareon", listOf("Fire"), 65, 130, 60, 65, 45, listOf(1 to Moves.ASCUAS, 13 to Moves.LANZALLAMAS, 26 to Moves.GOLPE_CUERPO)),
        Species(15, "Gastly", listOf("Ghost", "Poison"), 30, 35, 30, 80, 190, listOf(1 to Moves.LENGUETAZO, 5 to Moves.BOLA_SOMBRA, 12 to Moves.CONFUSION)),
        Species(16, "Haunter", listOf("Ghost", "Poison"), 45, 50, 45, 95, 90, listOf(1 to Moves.LENGUETAZO, 5 to Moves.BOLA_SOMBRA, 20 to Moves.PSIQUICO)),
        Species(17, "Gengar", listOf("Ghost", "Poison"), 60, 65, 60, 110, 45, listOf(1 to Moves.LENGUETAZO, 5 to Moves.BOLA_SOMBRA, 20 to Moves.PSIQUICO, 30 to Moves.BOMBA_LODO)),
        Species(18, "Abra", listOf("Psychic"), 25, 20, 15, 90, 200, listOf(1 to Moves.CONFUSION, 16 to Moves.PSIQUICO)),
        Species(19, "Alakazam", listOf("Psychic"), 55, 50, 45, 120, 50, listOf(1 to Moves.CONFUSION, 16 to Moves.PSIQUICO)),
        Species(20, "Machop", listOf("Fighting"), 70, 80, 50, 35, 180, listOf(1 to Moves.GOLPE_KARATE, 7 to Moves.AT_RAPIDO, 19 to Moves.GOLPE_CUERPO)),
        Species(21, "Machamp", listOf("Fighting"), 90, 130, 80, 55, 45, listOf(1 to Moves.GOLPE_KARATE, 7 to Moves.AT_RAPIDO, 19 to Moves.GOLPE_CUERPO)),
        Species(22, "Geodude", listOf("Rock", "Ground"), 40, 80, 100, 20, 255, listOf(1 to Moves.PLACAJE, 6 to Moves.LANZA_ROCAS, 11 to Moves.TERREMOTO)),
        Species(23, "Golem", listOf("Rock", "Ground"), 80, 120, 130, 45, 45, listOf(1 to Moves.LANZA_ROCAS, 11 to Moves.TERREMOTO, 20 to Moves.GOLPE_CUERPO)),
        Species(24, "Magikarp", listOf("Water"), 20, 10, 55, 80, 255, listOf(1 to Moves.PLACAJE)),
        Species(25, "Gyarados", listOf("Water", "Flying"), 95, 125, 79, 81, 45, listOf(1 to Moves.MORDISCO, 20 to Moves.HIDROBOMBA, 25 to Moves.GOLPE_CUERPO, 30 to Moves.ATAQUE_ALA)),
        Species(26, "Lapras", listOf("Water", "Ice"), 130, 85, 80, 60, 45, listOf(1 to Moves.ASPERSOR, 1 to Moves.VIENTO_HIELO, 20 to Moves.SURF, 28 to Moves.RAYO_HIELO)),
        Species(27, "Ralts", listOf("Psychic", "Fairy"), 28, 25, 25, 40, 235, listOf(1 to Moves.CONFUSION, 23 to Moves.BRILLO_MAGICO, 30 to Moves.PSIQUICO)),
        Species(28, "Gardevoir", listOf("Psychic", "Fairy"), 68, 65, 65, 80, 45, listOf(1 to Moves.CONFUSION, 23 to Moves.BRILLO_MAGICO, 30 to Moves.PSIQUICO)),
        Species(29, "Riolu", listOf("Fighting"), 40, 70, 40, 60, 75, listOf(1 to Moves.GOLPE_KARATE, 15 to Moves.AT_RAPIDO)),
        Species(30, "Lucario", listOf("Fighting", "Steel"), 70, 110, 70, 90, 45, listOf(1 to Moves.GOLPE_KARATE, 15 to Moves.PUNO_HIELO, 19 to Moves.GARRA_METAL, 25 to Moves.GOLPE_CUERPO)),
        Species(31, "Starly", listOf("Normal", "Flying"), 40, 55, 30, 60, 255, listOf(1 to Moves.PICOTAZO, 9 to Moves.ATAQUE_ALA)),
        Species(32, "Shinx", listOf("Electric"), 45, 65, 34, 45, 235, listOf(1 to Moves.IMPACTRUENO, 9 to Moves.AT_RAPIDO, 18 to Moves.RAYO)),
        Species(33, "Luxray", listOf("Electric"), 80, 120, 79, 70, 45, listOf(1 to Moves.IMPACTRUENO, 18 to Moves.RAYO, 28 to Moves.TRUENO)),
        Species(34, "Rockruff", listOf("Rock"), 45, 65, 40, 60, 190, listOf(1 to Moves.ARANIAZO, 8 to Moves.AT_RAPIDO, 16 to Moves.MORDISCO)),
        Species(35, "Lycanroc", listOf("Rock"), 75, 115, 65, 112, 90, listOf(1 to Moves.ARANIAZO, 16 to Moves.AVALANCHA, 24 to Moves.GOLPE_CUERPO)),
        Species(36, "Mimikyu", listOf("Ghost", "Fairy"), 55, 90, 80, 96, 45, listOf(1 to Moves.LENGUETAZO, 14 to Moves.BOLA_SOMBRA, 22 to Moves.BRILLO_MAGICO)),
        Species(37, "Wooloo", listOf("Normal"), 42, 40, 55, 48, 255, listOf(1 to Moves.PLACAJE, 8 to Moves.AT_RAPIDO)),
        Species(38, "Dubwool", listOf("Normal"), 72, 80, 100, 88, 127, listOf(1 to Moves.PLACAJE, 12 to Moves.GOLPE_CUERPO, 20 to Moves.AT_RAPIDO)),
        Species(39, "Dreepy", listOf("Dragon", "Ghost"), 28, 60, 30, 82, 45, listOf(1 to Moves.ALIENTO_DRAGON, 16 to Moves.BOLA_SOMBRA)),
        Species(40, "Dragapult", listOf("Dragon", "Ghost"), 88, 120, 75, 142, 45, listOf(1 to Moves.ALIENTO_DRAGON, 16 to Moves.BOLA_SOMBRA, 28 to Moves.ATAQUE_ALA)),
        Species(41, "Corviknight", listOf("Flying", "Steel"), 98, 87, 105, 67, 45, listOf(1 to Moves.PICOTAZO, 12 to Moves.ATAQUE_ALA, 24 to Moves.GARRA_METAL)),
        Species(42, "Arceus", listOf("Normal"), 120, 120, 120, 120, 3, listOf(1 to Moves.SENTENCIA, 10 to Moves.RAYO_SOLAR, 20 to Moves.PSIQUICO, 30 to Moves.TERREMOTO))
    )

    fun byId(id: Int): Species = ALL.first { it.id == id }
}

// ============================================================
// POKÉMON INDIVIDUAL
// ============================================================

class Mon(
    val species: Species,
    var level: Int,
    var exp: Int = 0,
    var nickname: String = species.name
) {
    var maxHp = 1
    var hp = 1
    var atk = 1
    var def = 1
    var spd = 1
    val moves = ArrayList<Move>()

    init {
        recalcStats(true)
    }

    fun recalcStats(full: Boolean) {
        maxHp = species.baseHp * 2 * level / 100 + level + 10
        atk = species.baseAtk * 2 * level / 100 + 5
        def = species.baseDef * 2 * level / 100 + 5
        spd = species.baseSpd * 2 * level / 100 + 5
        if (full) hp = maxHp
        if (hp > maxHp) hp = maxHp
        moves.clear()
        moves.addAll(species.moves.filter { it.first <= level }.map { it.second }.takeLast(4))
        if (moves.isEmpty()) moves.add(Moves.PLACAJE)
    }

    fun expToNext(): Int = 5 * level * level

    fun gainExp(amount: Int): List<String> {
        val msgs = ArrayList<String>()
        exp += amount
        while (level < 70 && exp >= expToNext()) {
            exp -= expToNext()
            level++
            recalcStats(false)
            hp = min(hp + maxHp / 8, maxHp)
            msgs.add("¡$nickname subió al nivel $level!")
            for ((lvl, mv) in species.moves) {
                if (lvl == level) msgs.add("¡$nickname aprendió ${mv.name}!")
            }
        }
        return msgs
    }
}

// ============================================================
// OBJETOS
// ============================================================

object Items {
    enum class Kind { BALL, POTION }

    data class Item(val name: String, val price: Int, val desc: String, val kind: Kind, val power: Int = 0)

    val SHOP = listOf(
        Item("Poké Ball", 200, "Captura Pokémon salvajes.", Kind.BALL, 1),
        Item("Super Ball", 600, "Mejor tasa de captura.", Kind.BALL, 15),
        Item("Ultra Ball", 1200, "La mejor tasa de captura.", Kind.BALL, 2),
        Item("Poción", 300, "Restaura 20 PS.", Kind.POTION, 20),
        Item("Superpoción", 700, "Restaura 60 PS.", Kind.POTION, 60)
    )

    fun byName(name: String): Item = SHOP.first { it.name == name }

    fun ballBonus(name: String): Double = when (name) {
        "Poké Ball" -> 1.0
        "Super Ball" -> 1.5
        "Ultra Ball" -> 2.0
        else -> 1.0
    }

    fun potionHeal(name: String): Int = when (name) {
        "Poción" -> 20
        "Superpoción" -> 60
        else -> 0
    }
}
