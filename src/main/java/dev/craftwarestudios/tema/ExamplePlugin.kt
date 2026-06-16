package dev.craftwarestudios.tema

import dev.craftwarestudios.tema.examples.ExampleMultiPageGui
import dev.craftwarestudios.tema.examples.ExampleThingGui
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin

class ExamplePlugin : JavaPlugin() {
    private val listener = GuiListener()

    private val exampleThingy = ExampleThingGui()
    private val exampleMultipage = ExampleMultiPageGui()

    override fun onEnable() {
        server.pluginManager.registerEvents(listener, this)
    }

    override fun onDisable() {
        HandlerList.unregisterAll(listener)
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (command.name != "tema-debug") return false
        val player = sender as? Player ?: return true

        when (args.firstOrNull()?.lowercase()) {
            "thingy" -> {
                exampleThingy.container.openInstanced(player)
            }
            "multipaging-small" -> {
                exampleMultipage.container9x3.openInstanced(player)
            }
            "multipaging-big" -> {
                exampleMultipage.container9x5.openInstanced(player)
            }
        }
        return true
    }
}