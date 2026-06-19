package dev.tuffstudios.tema

import dev.tuffstudios.tema.example.ExampleNestedMenu
import dev.tuffstudios.tema.inventory.InventoryMenuView
import dev.tuffstudios.tema.inventory.SharedMenuView
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin
import org.slf4j.Logger
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class TEMA : JavaPlugin(), Listener {
    private val viewingSessions: ConcurrentHashMap<UUID, InventoryMenuView> = ConcurrentHashMap()

    fun register(viewer: HumanEntity, view: InventoryMenuView) {
        this.viewingSessions[viewer.uniqueId] = view
    }

    fun unregister(viewer: HumanEntity) {
        this.viewingSessions.remove(viewer.uniqueId)
    }

    override fun onEnable() {
        this.server.pluginManager.registerEvents(this, this)
    }

    override fun onDisable() {
        HandlerList.unregisterAll(this as JavaPlugin)
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (command.name != "tema-debug") return false
        val player = sender as? Player ?: return true

        when (args.firstOrNull()?.lowercase()) {
            "1" -> {
                val menu = ExampleNestedMenu(player)
                menu.open(player)
            }
            "2" -> {
                val name = args.getOrNull(1) ?: player.name
                val target = Bukkit.getOfflinePlayer(name)
                val menu = ExampleNestedMenu(target)
                menu.open(player)
            }
        }

        return true
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        val view = this.viewingSessions[player.uniqueId] ?: return
        view.handleOnClick(event)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val uniqueId = event.player.uniqueId
        val view = this.viewingSessions[uniqueId] ?: return
        if (view is SharedMenuView) {
            view.onQuit(event)
        }

        view.menu.forget(uniqueId)
        this.viewingSessions.remove(uniqueId)
    }

    companion object {
        fun getLogger(): Logger {
            return getInstance().slF4JLogger
        }

        fun getInstance(): TEMA {
            return getPlugin(TEMA::class.java)
        }
    }
}