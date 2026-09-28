package com.dario.pokemonadventure.battle

import com.dario.pokemonadventure.data.Items
import com.dario.pokemonadventure.data.Mon
import com.dario.pokemonadventure.data.Move
import com.dario.pokemonadventure.data.TypeChart
import com.dario.pokemonadventure.game.GameEngine
import com.dario.pokemonadventure.world.TrainerFight
import kotlin.math.min
import kotlin.random.Random

class Battle(
    val engine: GameEngine,
    val enemyTeam: List<Mon>,
    val trainer: TrainerFight? = null
) {

    enum class Phase { INTRO, ACTION, MOVES, BAG, TEAM, MESSAGE }
    enum class Style { NORMAL, AGIL, FUERTE }
    enum class Result { WIN, LOSE, RUN, CATCH }

    var phase = Phase.MESSAGE
    var style = Style.NORMAL
    val messages = ArrayDeque<String>()
    var over = false
    var result: Result? = null
    var caughtMon: Mon? = null
    var mustSwitch = false
    var time = 0f

    var enemyIndex = 0
    val enemy: Mon get() = enemyTeam[enemyIndex]
    val wild: Boolean get() = trainer == null

    private val player get() = engine.player
    val active: Mon get() = player.party[player.activeIndex]

    init {
        if (trainer != null) {
            queue("¡${trainer.def.title} ${trainer.def.name} quiere luchar!")
            queue("¡${trainer.def.name} saca a ${enemy.nickname}!")
        } else {
            queue("¡Un ${enemy.nickname} salvaje apareció!")
            if (enemy.species.id == 42) {
                queue("¡El guardián de la distorsión te observa con serenidad!")
            }
        }
    }

    fun update(dt: Float) {
        time += dt
    }

    fun label(): String = when (style) {
        Style.NORMAL -> "Normal"
        Style.AGIL -> "Ágil"
        Style.FUERTE -> "Fuerte"
    }

    fun queue(text: String) {
        messages.addLast(text)
    }

    fun queueAll(texts: List<String>) {
        for (t in texts) messages.addLast(t)
    }

    fun tapAdvance() {
        if (messages.isNotEmpty()) messages.removeFirst()
        if (messages.isEmpty()) syncPhase()
    }

    private fun syncPhase() {
        if (over) {
            engine.endBattle(this)
            return
        }
        if (active.hp <= 0) {
            if (player.party.any { it.hp > 0 }) {
                mustSwitch = true
                queue("¡Elige a tu siguiente Pokémon!")
                phase = Phase.TEAM
                return
            }
            result = Result.LOSE
            over = true
            engine.endBattle(this)
            return
        }
        phase = Phase.ACTION
    }

    fun toMoves() {
        phase = Phase.MOVES
    }

    fun back() {
        if (!mustSwitch) phase = Phase.ACTION
    }

    fun cycleStyle() {
        style = when (style) {
            Style.NORMAL -> Style.AGIL
            Style.AGIL -> Style.FUERTE
            Style.FUERTE -> Style.NORMAL
        }
    }

    private fun enemyMove(): Move = enemy.moves.random()

    private fun attack(att: Mon, def: Mon, move: Move, mult: Double) {
        if (att.hp <= 0) return
        queue("¡${att.nickname} usó ${move.name}!")
        if (Random.nextInt(100) >= move.accuracy) {
            queue("¡Pero falló!")
            return
        }
        val eff = TypeChart.effectiveness(move.type, def.species.types)
        if (eff <= 0.0) {
            queue("No afecta al rival...")
            return
        }
        val stab = if (move.type in att.species.types) 1.5 else 1.0
        var dmg = (((2 * att.level / 5 + 2) * move.power * att.atk / def.def.coerceAtLeast(1)) / 50 + 2).toDouble()
        dmg *= eff * stab * mult * (0.85 + Random.nextDouble() * 0.15)
        val d = dmg.toInt().coerceAtLeast(1)
        def.hp = (def.hp - d).coerceAtLeast(0)
        if (eff >= 2.0) queue("¡Es súper eficaz!")
        else if (eff < 1.0) queue("No es muy eficaz...")
        if (def.hp <= 0) queue("¡${def.nickname} se debilitó!")
    }

    fun chooseMove(move: Move) {
        val em = enemyMove()
        val mult = when (style) {
            Style.AGIL -> 0.8
            Style.FUERTE -> 1.3
            Style.NORMAL -> 1.0
        }
        val playerFirst = when (style) {
            Style.AGIL -> true
            Style.FUERTE -> false
            Style.NORMAL -> active.spd >= enemy.spd
        }
        phase = Phase.MESSAGE
        if (playerFirst) {
            attack(active, enemy, move, mult)
            attack(enemy, active, em, 1.0)
        } else {
            attack(enemy, active, em, 1.0)
            attack(active, enemy, move, mult)
        }
        finishTurn()
    }

    private fun finishTurn() {
        if (enemy.hp <= 0) {
            // Experiencia por cada rival debilitado
            val exp = enemy.level * 8 + 20
            queue("¡El ${enemy.nickname} rival se debilitó!")
            queue("¡${active.nickname} ganó $exp EXP!")
            queueAll(active.gainExp(exp))
            engine.dexSee(active.species.id) // registra la forma evolucionada, si cambió
            if (trainer != null && enemyIndex < enemyTeam.size - 1) {
                enemyIndex++
                engine.dexSee(enemy.species.id)
                queue("¡${trainer.def.name} saca a ${enemy.nickname}!")
                return
            }
            if (trainer != null) {
                val reward = trainer.def.reward
                player.money += reward
                queue("¡Ganaste el combate contra ${trainer.def.title} ${trainer.def.name}!")
                queue("Recibiste $$reward por la victoria.")
            } else {
                player.money += enemy.level * 10 + 30
            }
            result = Result.WIN
            over = true
            return
        }
        if (active.hp <= 0) {
            queue("¡Tu ${active.nickname} se debilitó!")
            if (player.party.any { it.hp > 0 }) {
                mustSwitch = true
            } else {
                result = Result.LOSE
                over = true
            }
        }
    }

    fun useBall(ballName: String) {
        if (!wild) {
            queue("¡No puedes capturar el Pokémon de un entrenador!")
            phase = Phase.MESSAGE
            return
        }
        val count = player.itemCount(ballName)
        if (count <= 0) {
            queue("¡No te quedan $ballName!")
            phase = Phase.MESSAGE
            return
        }
        player.items[ballName] = count - 1
        phase = Phase.MESSAGE
        queue("¡Usaste una $ballName!")
        val bonus = Items.ballBonus(ballName)
        val a = (3 * enemy.maxHp - 2 * enemy.hp) * enemy.species.catchRate * bonus / (3.0 * enemy.maxHp)
        val chance = Math.pow(a / 255.0, 0.75).coerceIn(0.05, 0.95)
        if (Random.nextDouble() < chance) {
            queue("¡Excelente! ¡${enemy.nickname} fue capturado!")
            caughtMon = enemy
            result = Result.CATCH
            over = true
        } else {
            queue("¡Oh, no! ¡Se escapó de la Ball!")
            attack(enemy, active, enemyMove(), 1.0)
            finishTurn()
        }
    }

    fun usePotion(potionName: String) {
        val count = player.itemCount(potionName)
        if (count <= 0) {
            queue("¡No te quedan más pociones!")
            phase = Phase.MESSAGE
            return
        }
        if (active.hp >= active.maxHp) {
            queue("¡${active.nickname} ya tiene todos sus PS!")
            phase = Phase.MESSAGE
            return
        }
        player.items[potionName] = count - 1
        phase = Phase.MESSAGE
        val healed = min(Items.potionHeal(potionName), active.maxHp - active.hp)
        active.hp += healed
        queue("¡${active.nickname} recuperó $healed PS!")
        attack(enemy, active, enemyMove(), 1.0)
        finishTurn()
    }

    fun switchTo(index: Int) {
        val m = player.party.getOrNull(index) ?: return
        if (m.hp <= 0) return
        if (index == player.activeIndex) return
        val forced = mustSwitch
        player.activeIndex = index
        queue("¡Adelante, ${m.nickname}!")
        phase = Phase.MESSAGE
        mustSwitch = false
        if (!forced) {
            attack(enemy, active, enemyMove(), 1.0)
            finishTurn()
        }
    }

    fun run() {
        if (!wild) {
            queue("¡No puedes escapar de un combate contra un entrenador!")
            phase = Phase.MESSAGE
            return
        }
        phase = Phase.MESSAGE
        val chance = (0.55 + (active.spd - enemy.spd) / 200.0).coerceIn(0.3, 0.95)
        if (Random.nextDouble() < chance) {
            queue("¡Escapaste sin problemas!")
            result = Result.RUN
            over = true
        } else {
            queue("¡No has podido escapar!")
            attack(enemy, active, enemyMove(), 1.0)
            finishTurn()
        }
    }
}
