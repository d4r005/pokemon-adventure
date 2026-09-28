package com.dario.pokemonadventure.game

import android.content.Context
import android.graphics.RectF
import android.view.MotionEvent
import com.dario.pokemonadventure.battle.Battle
import com.dario.pokemonadventure.data.Dex
import com.dario.pokemonadventure.data.Items
import com.dario.pokemonadventure.data.Mon
import com.dario.pokemonadventure.data.TypeColors
import com.dario.pokemonadventure.save.SaveManager
import com.dario.pokemonadventure.ui.UiButton
import com.dario.pokemonadventure.world.Npc
import com.dario.pokemonadventure.world.Player
import com.dario.pokemonadventure.world.Regions
import com.dario.pokemonadventure.world.Tile
import com.dario.pokemonadventure.world.TileMap
import com.dario.pokemonadventure.world.TrainerFight
import kotlin.math.abs
import kotlin.math.max
import kotlin.random.Random

enum class Screen { TITLE, WORLD, BATTLE, MENU, TEAM, SHOP, REGION, DEX }

class GameEngine(val context: Context) {

    var screen = Screen.TITLE
    var player = Player()
    var battle: Battle? = null

    private val maps = arrayOfNulls<TileMap>(Regions.ALL.size)
    var map: TileMap? = null

    val dialog = ArrayDeque<String>()
    var buttons = ArrayList<UiButton>()

    var time = 0f
    var dpadDir = -1
    // Joystick flotante estilo Pokémon GO
    var joyActive = false
    var joyBaseX = 0f
    var joyBaseY = 0f
    var joyVecX = 0f
    var joyVecY = 0f
    var encounterCooldown = 2f
    var menuMsg = ""
    var menuMsgT = 0f
    var shopMsg = ""
    var shopMsgT = 0f
    var dexPage = 0
    var lastSaveExists = SaveManager.exists(context)
    private var onPortal = false
    private var pendingTrainer = -1

    private var lastW = 1
    private var lastH = 1

    companion object {
        const val STARTER_ID = 10 // Pikachu, como en Pokémon Amarillo
    }

    // ---------------------------------------------------------
    // Mapas
    // ---------------------------------------------------------

    private fun mapFor(index: Int): TileMap {
        if (maps[index] == null) maps[index] = TileMap(Regions.ALL[index])
        return maps[index]!!
    }

    private fun enterRegion(index: Int) {
        player.regionIndex = index
        val m = mapFor(index)
        map = m
        player.x = m.spawnX + 0.5f
        player.y = m.spawnY + 0.5f
        onPortal = false
        encounterCooldown = 2f
    }

    // ---------------------------------------------------------
    // Pokédex
    // ---------------------------------------------------------

    fun dexSee(id: Int) {
        player.dexSeen.add(id)
    }

    fun dexCatch(id: Int) {
        player.dexSeen.add(id)
        player.dexCaught.add(id)
    }

    // ---------------------------------------------------------
    // Ciclo de vida del juego
    // ---------------------------------------------------------

    fun newGame() {
        player = Player()
        // Pokémon Amarillo: el Prof. Oak te entrega a Pikachu
        val pikachu = Mon(Dex.byId(STARTER_ID), 5)
        player.party.add(pikachu)
        player.activeIndex = 0
        dexCatch(STARTER_ID)
        enterRegion(0) // Kanto
        screen = Screen.WORLD
        dialog.clear()
        dialog.addLast("Prof. Oak: ¡Toma este Pikachu! Será tu compañero, igual que en Pokémon Amarillo.")
        dialog.addLast("¡Tu aventura comienza en Kanto!")
        dialog.addLast("En el pueblo hay gente que regala Bulbasaur, Charmander y Squirtle. ¡Búscalos!")
        dialog.addLast("Arrastra el joystick en pantalla para caminar. El botón A interactúa.")
        dialog.addLast("Gana la Liga de Kanto: el Prof. Oak te dará el Ticket Johto.")
    }

    fun loadGame() {
        val p = SaveManager.load(context) ?: return
        player = p
        map = mapFor(p.regionIndex)
        screen = Screen.WORLD
        dialog.clear()
        dialog.addLast("¡Bienvenido de nuevo, entrenador!")
        onPortal = false
        encounterCooldown = 2f
    }

    fun hasTicket(index: Int): Boolean =
        index == 0 || player.tickets.contains("Ticket ${Regions.ALL[index].name}")

    fun travel(index: Int) {
        if (!hasTicket(index)) {
            screen = Screen.WORLD
            player.y += 1.2f
            onPortal = false
            dialog.clear()
            dialog.addLast("El barco no zarpa sin boleto... Necesitas el Ticket ${Regions.ALL[index].name}.")
            return
        }
        dialog.clear()
        dialog.addLast("El portal de distorsión te envuelve en una luz brillante...")
        enterRegion(index)
        dialog.addLast("¡Bienvenido a la región de ${Regions.ALL[index].name}!")
        screen = Screen.WORLD
    }

    // ---------------------------------------------------------
    // Actualización
    // ---------------------------------------------------------

    fun update(dt: Float) {
        time += dt
        if (menuMsgT > 0f) menuMsgT -= dt
        if (shopMsgT > 0f) shopMsgT -= dt
        encounterCooldown = max(0f, encounterCooldown - dt)
        when (screen) {
            Screen.WORLD -> updateWorld(dt)
            Screen.BATTLE -> battle?.update(dt)
            else -> {}
        }
    }

    private fun updateWorld(dt: Float) {
        if (dialog.isNotEmpty()) return
        if (pendingTrainer >= 0) {
            val idx = pendingTrainer
            pendingTrainer = -1
            startTrainerBattle(idx)
            return
        }
        val p = player
        val m = map ?: return
        var dx = 0f
        var dy = 0f
        if (joyActive) {
            dx = joyVecX
            dy = joyVecY
        }
        if (dx == 0f && dy == 0f) {
            when (dpadDir) {
                0 -> dy = -1f
                1 -> dx = 1f
                2 -> dy = 1f
                3 -> dx = -1f
            }
        }
        if (dx == 0f && dy == 0f) return

        if (abs(dx) > abs(dy)) p.facing = if (dx > 0) 1 else 3
        else p.facing = if (dy > 0) 2 else 0

        val speed = 3.4f * dt
        tryMove(dx * speed, 0f)
        tryMove(0f, dy * speed)

        val tile = m.tileAt(p.x.toInt(), p.y.toInt())

        if (tile == Tile.PORTAL) {
            if (!onPortal) {
                onPortal = true
                screen = Screen.REGION
                dialog.clear()
                dialog.addLast("El portal de distorsión vibra con energía...")
            }
        } else {
            onPortal = false
        }

        if (tile == Tile.TALL && encounterCooldown <= 0f && p.anyAlive()) {
            if (Random.nextDouble() < dt * 0.30) startWildEncounter()
        }
    }

    private fun tryMove(dx: Float, dy: Float) {
        val m = map ?: return
        val p = player
        val nx = p.x + dx
        val ny = p.y + dy
        val r = 0.30f
        val corners = listOf(
            nx - r to ny - r, nx + r to ny - r,
            nx - r to ny + r, nx + r to ny + r
        )
        for ((cx, cy) in corners) {
            if (m.blocked(cx.toInt(), cy.toInt())) return
        }
        p.x = nx
        p.y = ny
    }

    private fun startWildEncounter() {
        val m = map ?: return
        if (!player.anyAlive()) return
        if ((player.party.getOrNull(player.activeIndex)?.hp ?: 0) <= 0) {
            player.activeIndex = player.party.indexOfFirst { it.hp > 0 }
        }
        val def = m.def
        val total = def.encounters.sumOf { it.weight }
        var roll = Random.nextInt(total)
        var pick = def.encounters.last()
        for (e in def.encounters) {
            roll -= e.weight
            if (roll < 0) {
                pick = e
                break
            }
        }
        val lvl = Random.nextInt(pick.minLevel, pick.maxLevel + 1)
        val mon = Mon(Dex.byId(pick.speciesId), lvl)
        dexSee(mon.species.id)
        battle = Battle(this, listOf(mon), null)
        screen = Screen.BATTLE
    }

    private fun startTrainerBattle(idx: Int) {
        val m = map ?: return
        if (!player.anyAlive()) return
        if ((player.party.getOrNull(player.activeIndex)?.hp ?: 0) <= 0) {
            player.activeIndex = player.party.indexOfFirst { it.hp > 0 }
        }
        val def = m.trainers.getOrNull(idx) ?: return
        val team = def.team.map { Mon(Dex.byId(it.first), it.second) }
        for (t in team) dexSee(t.species.id)
        battle = Battle(this, team, TrainerFight(def, "${m.def.id}:$idx"))
        screen = Screen.BATTLE
    }

    fun endBattle(b: Battle) {
        screen = Screen.WORLD
        encounterCooldown = 2.5f
        when (b.result) {
            Battle.Result.CATCH -> {
                val mon = b.caughtMon
                if (mon != null) {
                    dexCatch(mon.species.id)
                    if (player.party.size < 6) {
                        player.party.add(mon)
                        dialog.addLast("¡${mon.nickname} se unió a tu equipo!")
                    } else {
                        player.box++
                        dialog.addLast("¡Tu equipo está lleno! ${mon.nickname} fue enviado a la caja.")
                    }
                }
            }
            Battle.Result.WIN -> {
                b.trainer?.let { t ->
                    player.beatenTrainers.add(t.key)
                    if (t.key == "0:4" && !player.tickets.contains("Ticket Johto")) {
                        player.tickets.add("Ticket Johto")
                        dialog.addLast("Prof. Oak: ¡Ganaste la Liga de Kanto! ¡Enhorabuena, campeón!")
                        dialog.addLast("Prof. Oak: Toma este TICKET JOHTO. El barco te espera en el portal del norte.")
                    }
                    if (t.key == "1:4") {
                        for (i in 2 until Regions.ALL.size) {
                            val tn = "Ticket ${Regions.ALL[i].name}"
                            if (!player.tickets.contains(tn)) player.tickets.add(tn)
                        }
                        dialog.addLast("Prof. Oak: ¡Ganaste la Liga de Johto! Eres todo un campeón.")
                        dialog.addLast("¡Recibiste los tickets de Hoenn, Sinnoh, Unova, Kalos, Alola, Galar y Paldea!")
                    }
                }
            }
            Battle.Result.LOSE -> {
                enterRegion(player.regionIndex)
                player.healAll()
                dialog.addLast("Corriste de vuelta al Centro Pokémon...")
                dialog.addLast("Enfermera Joy: ¡Tus Pokémon están como nuevos! Ten más cuidado.")
            }
            else -> {}
        }
        battle = null
    }

    // ---------------------------------------------------------
    // Interacciones del mundo
    // ---------------------------------------------------------

    private fun interact() {
        val m = map ?: return
        val npc = m.npcNear(player.x, player.y, 2.2f) ?: return
        when (npc.kind) {
            Npc.Kind.NURSE -> {
                player.healAll()
                dialog.addLast("${npc.name}: ¡Tus Pokémon están como nuevos!")
            }
            Npc.Kind.SHOP -> screen = Screen.SHOP
            Npc.Kind.GUIDE -> dialog.addLast("${npc.name}: ${guideTip()}")
            Npc.Kind.TRAINER -> {
                val idx = npc.trainerIndex
                val t = m.trainers.getOrNull(idx) ?: return
                val key = "${m.def.id}:$idx"
                val tLabel = if (t.title.isEmpty()) t.name else "${t.title} ${t.name}"
                if (player.beatenTrainers.contains(key)) {
                    dialog.addLast("$tLabel: ¡Buen combate! Vuelve cuando quieras la revancha.")
                } else if (player.anyAlive()) {
                    dialog.addLast("¡$tLabel te desafía a un combate!")
                    pendingTrainer = idx
                } else {
                    dialog.addLast("Tu equipo está debilitado... Ve primero al Centro Pokémon.")
                }
            }
            Npc.Kind.GIFT -> {
                val giftId = npc.giftId
                if (giftId <= 0) return
                val giftKey = "gift:$giftId"
                if (player.giftsReceived.contains(giftKey)) {
                    dialog.addLast("${npc.name}: ¡Cuídalo mucho! Adiós.")
                    return
                }
                if (player.party.size >= 6) {
                    dialog.addLast("${npc.name}: Tu equipo está lleno... Vuelve con espacio libre.")
                    return
                }
                val mon = Mon(Dex.byId(giftId), 5)
                player.party.add(mon)
                player.giftsReceived.add(giftKey)
                dexCatch(giftId)
                dialog.addLast("${npc.name}: ¡Este ${mon.nickname} busca un buen entrenador! ¡Es tuyo!")
            }
        }
    }

    private fun guideTip(): String {
        val tips = listOf(
            "La hierba alta esconde Pokémon salvajes. ¡Lleva tus Balls listas!",
            "Los portales de distorsión están al norte de cada pueblo.",
            "El estilo Ágil te deja actuar primero, pero golpea más suave.",
            "El estilo Fuerte pega con todo, pero actuarás al final del turno.",
            "Los entrenadores rivales no perdonan: ¡no puedes huir de ellos!",
            "Cada Pokémon solo aparece en su región de origen. ¡Visítalas todas!",
            "Aquí en Kanto hay gente que regala Bulbasaur, Charmander y Squirtle.",
            "Gana la Liga de Kanto y el Prof. Oak te dará el Ticket Johto.",
            "Registra tu progreso en la Pokédex desde el menú de pausa.",
            "Se dice que Arceus aparece en las distorsiones de Sinnoh..."
        )
        return tips[Random.nextInt(tips.size)]
    }

    private fun buy(name: String) {
        val item = Items.byName(name)
        if (player.money >= item.price) {
            player.money -= item.price
            player.items[name] = player.itemCount(name) + 1
            shopMsg = "¡Compraste ${item.name}!"
        } else {
            shopMsg = "No tienes suficiente dinero..."
        }
        shopMsgT = 2f
    }

    // ---------------------------------------------------------
    // Entrada
    // ---------------------------------------------------------

    fun handleTouch(action: Int, x: Float, y: Float) {
        when (action) {
            MotionEvent.ACTION_DOWN -> onDown(x, y)
            MotionEvent.ACTION_MOVE -> if (joyActive && screen == Screen.WORLD && dialog.isEmpty()) updateJoystick(x, y)
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> endJoystick()
        }
    }

    private fun buttonAt(x: Float, y: Float): UiButton? {
        return buttons.firstOrNull { it.rect.contains(x, y) }
    }

    private fun onDown(x: Float, y: Float) {
        when (screen) {
            Screen.WORLD -> {
                if (dialog.isNotEmpty()) {
                    dialog.removeFirst()
                    return
                }
                val b = buttonAt(x, y)
                if (b != null) {
                    if (b.enabled) onUi(b.id)
                    return
                }
                if (dialog.isEmpty()) startJoystick(x, y)
            }
            Screen.BATTLE -> {
                val b = battle ?: return
                if (b.phase == Battle.Phase.MESSAGE || b.phase == Battle.Phase.INTRO) {
                    b.tapAdvance()
                    return
                }
                val btn = buttonAt(x, y)
                if (btn != null && btn.enabled) onUi(btn.id)
            }
            else -> {
                val b = buttonAt(x, y)
                if (b != null && b.enabled) onUi(b.id)
            }
        }
    }

    // Teclado / mando (útil en emulador)
    fun uiConfirm() {
        when (screen) {
            Screen.WORLD -> {
                if (dialog.isNotEmpty()) dialog.removeFirst()
                else interact()
            }
            Screen.BATTLE -> {
                val b = battle ?: return
                if (b.phase == Battle.Phase.MESSAGE || b.phase == Battle.Phase.INTRO) b.tapAdvance()
            }
            Screen.MENU -> screen = Screen.WORLD
            else -> {}
        }
    }

    fun uiMenu() {
        if (screen == Screen.WORLD) {
            if (dialog.isNotEmpty()) dialog.removeFirst()
            else screen = Screen.MENU
        } else if (screen == Screen.MENU) {
            screen = Screen.WORLD
        }
    }

    fun setDpad(dir: Int) {
        dpadDir = dir
    }

    fun isMoving(): Boolean =
        (joyActive && (joyVecX != 0f || joyVecY != 0f)) || dpadDir >= 0

    private fun startJoystick(x: Float, y: Float) {
        joyActive = true
        joyBaseX = x
        joyBaseY = y
        joyVecX = 0f
        joyVecY = 0f
    }

    private fun updateJoystick(x: Float, y: Float) {
        val r = lastH * 0.13f
        val dx = x - joyBaseX
        val dy = y - joyBaseY
        val len = kotlin.math.sqrt(dx * dx + dy * dy)
        if (len < r * 0.12f) {
            joyVecX = 0f
            joyVecY = 0f
            return
        }
        joyVecX = dx / len
        joyVecY = dy / len
    }

    private fun endJoystick() {
        joyActive = false
        joyVecX = 0f
        joyVecY = 0f
    }

    fun screenW(): Int = lastW
    fun screenH(): Int = lastH

    // ---------------------------------------------------------
    // Acciones de botones
    // ---------------------------------------------------------

    private fun onUi(id: String) {
        when (screen) {
            Screen.WORLD -> when (id) {
                "A" -> interact()
                "MENU" -> screen = Screen.MENU
            }
            Screen.TITLE -> when (id) {
                "NEW" -> newGame()
                "CONT" -> loadGame()
            }
            Screen.REGION -> when {
                id.startsWith("REGION:") -> travel(id.substringAfter(":").toInt())
                id == "REGION_CANCEL" -> {
                    screen = Screen.WORLD
                    player.y += 1.2f
                    onPortal = false
                    dialog.removeFirstOrNull()
                }
            }
            Screen.MENU -> when (id) {
                "RESUME" -> screen = Screen.WORLD
                "SAVE" -> {
                    SaveManager.save(context, player)
                    lastSaveExists = true
                    menuMsg = "¡Partida guardada!"
                    menuMsgT = 2f
                }
                "TEAMVIEW" -> screen = Screen.TEAM
                "POKEDEX" -> {
                    dexPage = 0
                    screen = Screen.DEX
                }
                "QUIT" -> {
                    screen = Screen.TITLE
                    battle = null
                    lastSaveExists = SaveManager.exists(context)
                }
            }
            Screen.TEAM -> when {
                id.startsWith("MON:") -> {
                    val i = id.substringAfter(":").toInt()
                    val m = player.party.getOrNull(i)
                    if (m != null && m.hp > 0) {
                        player.activeIndex = i
                        menuMsg = "¡${m.nickname} ahora está al frente!"
                        menuMsgT = 2f
                    }
                }
                id == "VOLVER" -> screen = Screen.MENU
            }
            Screen.SHOP -> when {
                id.startsWith("BUY:") -> buy(id.substringAfter("BUY:"))
                id == "SHOP_CLOSE" -> screen = Screen.WORLD
            }
            Screen.DEX -> when (id) {
                "DEX_PREV" -> dexPage--
                "DEX_NEXT" -> dexPage++
                "VOLVER" -> screen = Screen.MENU
            }
            Screen.BATTLE -> {
                val b = battle ?: return
                when {
                    id == "LUCHAR" -> b.toMoves()
                    id == "BOLSA" -> b.phase = Battle.Phase.BAG
                    id == "EQUIPO" -> b.phase = Battle.Phase.TEAM
                    id == "HUIR" -> b.run()
                    id == "STYLE" -> b.cycleStyle()
                    id.startsWith("MOVE:") -> {
                        val i = id.substringAfter(":").toInt()
                        b.chooseMove(b.active.moves.getOrNull(i) ?: return)
                    }
                    id.startsWith("ITEM:") -> {
                        val name = id.substringAfter("ITEM:")
                        val item = Items.byName(name)
                        if (item.kind == Items.Kind.BALL) b.useBall(name)
                        else b.usePotion(name)
                    }
                    id.startsWith("MON:") -> b.switchTo(id.substringAfter(":").toInt())
                    id == "VOLVER" -> b.back()
                }
            }
        }
    }

    // ---------------------------------------------------------
    // Construcción de botones (geometría compartida con el render)
    // ---------------------------------------------------------

    private fun add(
        list: MutableList<UiButton>, id: String, x: Float, y: Float,
        w: Float, h: Float, label: String, sub: String = "",
        enabled: Boolean = true, icon: Int = 0, style: Int = 0
    ) {
        list.add(UiButton(id, RectF(x, y, x + w, y + h), label, sub, enabled, icon, style))
    }

    fun rebuildButtons(w: Int, h: Int) {
        lastW = w
        lastH = h
        val list = ArrayList<UiButton>()
        val fw = w.toFloat()
        val fh = h.toFloat()

        when (screen) {
            Screen.WORLD -> {
                add(list, "A", fw - fh * 0.30f, fh - fh * 0.40f, fh * 0.20f, fh * 0.20f, "A")
                add(list, "MENU", fw - fh * 0.30f, fh - fh * 0.155f, fh * 0.20f, fh * 0.10f, "MENÚ")
            }
            Screen.TITLE -> {
                val bw = fw * 0.34f
                val bh = fh * 0.10f
                add(list, "NEW", (fw - bw) / 2, fh * 0.40f, bw, bh, "Nueva partida")
                add(list, "CONT", (fw - bw) / 2, fh * 0.56f, bw, bh, "Continuar", enabled = lastSaveExists)
            }
            Screen.REGION -> {
                val bw = fw * 0.27f
                val bh = fh * 0.17f
                val gapY = fh * 0.025f
                val gapX = fw * 0.02f
                Regions.ALL.forEachIndexed { i, r ->
                    val col = i % 3
                    val row = i / 3
                    val x = fw * 0.04f + col * (bw + gapX)
                    val y = fh * 0.12f + row * (bh + gapY)
                    val unlocked = hasTicket(r.id)
                    val sub = if (unlocked)
                        "Nv. ${r.encounters.minOf { it.minLevel }}-${r.encounters.maxOf { it.maxLevel }}"
                    else
                        "Necesitas el Ticket ${r.name}"
                    add(list, "REGION:${r.id}", x, y, bw, bh, r.name, sub,
                        enabled = unlocked, icon = r.palette.accent, style = 1)
                }
                add(list, "REGION_CANCEL", fw * 0.70f, fh * 0.86f, fw * 0.25f, fh * 0.10f, "Cancelar")
            }
            Screen.MENU -> {
                val bw = fw * 0.30f
                val bh = fh * 0.11f
                val x = fw * 0.35f
                add(list, "RESUME", x, fh * 0.08f, bw, bh, "Reanudar")
                add(list, "POKEDEX", x, fh * 0.22f, bw, bh, "Pokédex")
                add(list, "TEAMVIEW", x, fh * 0.36f, bw, bh, "Ver equipo")
                add(list, "SAVE", x, fh * 0.50f, bw, bh, "Guardar partida")
                add(list, "QUIT", x, fh * 0.64f, bw, bh, "Volver al título")
            }
            Screen.TEAM -> {
                val bw = fw * 0.44f
                val bh = fh * 0.10f
                val x = fw * 0.05f
                player.party.forEachIndexed { i, m ->
                    add(list, "MON:$i", x, fh * 0.06f + i * (bh + fh * 0.02f), bw, bh,
                        "${m.nickname}  Nv.${m.level}",
                        "PS ${m.hp}/${m.maxHp}${if (i == player.activeIndex) "  (activo)" else ""}",
                        enabled = m.hp > 0, icon = TypeColors.color(m.species.types[0]))
                }
                add(list, "VOLVER", fw * 0.60f, fh * 0.85f, fw * 0.25f, fh * 0.10f, "Volver")
            }
            Screen.SHOP -> {
                val bw = fw * 0.40f
                val bh = fh * 0.10f
                val x = fw * 0.05f
                Items.SHOP.forEachIndexed { i, item ->
                    val owned = player.itemCount(item.name)
                    add(list, "BUY:${item.name}", x, fh * 0.06f + i * (bh + fh * 0.02f), bw, bh,
                        item.name, "$${item.price}  -  tienes $owned",
                        enabled = player.money >= item.price, icon = 0xFF9E9E9E.toInt())
                }
                add(list, "SHOP_CLOSE", fw * 0.60f, fh * 0.85f, fw * 0.25f, fh * 0.10f, "Salir")
            }
            Screen.DEX -> {
                val perPage = 6
                val total = Dex.ALL.size
                val pages = (total + perPage - 1) / perPage
                dexPage = dexPage.coerceIn(0, pages - 1)
                for (i in 0 until perPage) {
                    val idx = dexPage * perPage + i
                    if (idx >= total) break
                    val sp = Dex.ALL[idx]
                    val seen = player.dexSeen.contains(sp.id)
                    val caught = player.dexCaught.contains(sp.id)
                    val label = if (seen) String.format("%03d  %s", idx + 1, sp.name)
                                else String.format("%03d  ???", idx + 1)
                    val sub = when {
                        caught -> "Capturado"
                        seen -> "Visto"
                        else -> "Sin datos"
                    }
                    val icon = if (seen) TypeColors.color(sp.types[0]) else 0xFF555555.toInt()
                    add(list, "DEX:$idx", fw * 0.05f, fh * 0.12f + i * fh * 0.125f, fw * 0.40f, fh * 0.11f,
                        label, sub, enabled = true, icon = icon)
                }
                add(list, "DEX_PREV", fw * 0.05f, fh * 0.86f, fw * 0.15f, fh * 0.09f, "◀ Anterior")
                add(list, "DEX_NEXT", fw * 0.22f, fh * 0.86f, fw * 0.15f, fh * 0.09f, "Siguiente ▶")
                add(list, "VOLVER", fw * 0.60f, fh * 0.86f, fw * 0.25f, fh * 0.10f, "Volver")
            }
            Screen.BATTLE -> {
                val b = battle ?: return
                val bw = fw * 0.19f
                val bh = fh * 0.105f
                val bx = fw * 0.53f
                when (b.phase) {
                    Battle.Phase.ACTION -> {
                        add(list, "STYLE", bx, fh * 0.14f, bw * 1.55f, bh * 0.7f, "Estilo: ${b.label()}")
                        add(list, "LUCHAR", bx, fh * 0.28f, bw, bh, "Luchar")
                        add(list, "BOLSA", bx + bw * 1.25f, fh * 0.28f, bw, bh, "Bolsa")
                        add(list, "EQUIPO", bx, fh * 0.42f, bw, bh, "Equipo")
                        add(list, "HUIR", bx + bw * 1.25f, fh * 0.42f, bw, bh, "Huir", enabled = b.wild)
                    }
                    Battle.Phase.MOVES -> {
                        b.active.moves.forEachIndexed { i, mv ->
                            add(list, "MOVE:$i", fw * 0.03f, fh * 0.20f + i * fh * 0.135f, fw * 0.30f, fh * 0.115f,
                                mv.name, "${mv.type}  •  Pot. ${mv.power}  •  ${mv.accuracy}%",
                                icon = TypeColors.color(mv.type))
                        }
                        add(list, "VOLVER", bx, fh * 0.60f, bw, bh, "Volver")
                    }
                    Battle.Phase.BAG -> {
                        Items.SHOP.filter { player.itemCount(it.name) > 0 }.forEachIndexed { i, item ->
                            val ballBlocked = b.trainer != null && item.kind == Items.Kind.BALL
                            add(list, "ITEM:${item.name}", fw * 0.03f, fh * 0.12f + i * fh * 0.13f, fw * 0.32f, fh * 0.11f,
                                "${item.name} x${player.itemCount(item.name)}",
                                if (ballBlocked) "No sirve contra entrenadores" else item.desc,
                                enabled = !ballBlocked, icon = 0xFF9E9E9E.toInt())
                        }
                        add(list, "VOLVER", bx, fh * 0.60f, bw, bh, "Volver")
                    }
                    Battle.Phase.TEAM -> {
                        player.party.forEachIndexed { i, m ->
                            add(list, "MON:$i", fw * 0.03f, fh * 0.12f + i * fh * 0.13f, fw * 0.34f, fh * 0.11f,
                                "${m.nickname}  Nv.${m.level}", "PS ${m.hp}/${m.maxHp}",
                                enabled = m.hp > 0 && (i != player.activeIndex || b.mustSwitch),
                                icon = TypeColors.color(m.species.types[0]))
                        }
                        add(list, "VOLVER", bx, fh * 0.60f, bw, bh, "Volver", enabled = !b.mustSwitch)
                    }
                    Battle.Phase.MESSAGE, Battle.Phase.INTRO -> {}
                }
            }
        }
        buttons = list
    }

    // ---------------------------------------------------------
    // Render
    // ---------------------------------------------------------

    fun render(canvas: android.graphics.Canvas) {
        canvas.drawColor(0xFF10141C.toInt())
        rebuildButtons(canvas.width, canvas.height)
        when (screen) {
            Screen.WORLD -> com.dario.pokemonadventure.ui.WorldRenderer.render(canvas, this)
            Screen.BATTLE -> com.dario.pokemonadventure.ui.BattleRenderer.render(canvas, this)
            else -> com.dario.pokemonadventure.ui.MenuRenderer.dispatch(canvas, this)
        }
    }
}
