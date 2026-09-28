package com.dario.pokemonadventure.save

import android.content.Context
import com.dario.pokemonadventure.data.Dex
import com.dario.pokemonadventure.world.Player
import org.json.JSONArray
import org.json.JSONObject

object SaveManager {

    private const val PREFS = "pokemon_adventure"
    private const val KEY = "save"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun exists(context: Context): Boolean = prefs(context).contains(KEY)

    fun save(context: Context, p: Player) {
        val root = JSONObject()
        root.put("region", p.regionIndex)
        root.put("x", p.x.toDouble())
        root.put("y", p.y.toDouble())
        root.put("facing", p.facing)
        root.put("money", p.money)
        root.put("box", p.box)
        root.put("active", p.activeIndex)
        val items = JSONObject()
        for ((k, v) in p.items) items.put(k, v)
        root.put("items", items)
        val party = JSONArray()
        for (m in p.party) {
            val o = JSONObject()
            o.put("id", m.species.id)
            o.put("lvl", m.level)
            o.put("exp", m.exp)
            o.put("hp", m.hp)
            o.put("nick", m.nickname)
            party.put(o)
        }
        root.put("party", party)
        prefs(context).edit().putString(KEY, root.toString()).apply()
    }

    fun load(context: Context): Player? {
        return try {
            val s = prefs(context).getString(KEY, null) ?: return null
            val root = JSONObject(s)
            val p = Player()
            p.regionIndex = root.getInt("region")
            p.x = root.getDouble("x").toFloat()
            p.y = root.getDouble("y").toFloat()
            p.facing = root.getInt("facing")
            p.money = root.getInt("money")
            p.box = root.getInt("box")
            p.activeIndex = root.getInt("active")
            p.items.clear()
            val items = root.getJSONObject("items")
            var it = items.keys()
            while (it.hasNext()) {
                val k = it.next()
                p.items[k] = items.getInt(k)
            }
            val party = root.getJSONArray("party")
            for (i in 0 until party.length()) {
                val o = party.getJSONObject(i)
                val mon = com.dario.pokemonadventure.data.Mon(
                    Dex.byId(o.getInt("id")),
                    o.getInt("lvl"),
                    o.getInt("exp"),
                    o.getString("nick")
                )
                mon.hp = o.getInt("hp")
                p.party.add(mon)
            }
            p
        } catch (e: Exception) {
            null
        }
    }
}
