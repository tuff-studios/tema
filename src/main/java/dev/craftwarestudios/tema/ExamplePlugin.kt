package dev.craftwarestudios.tema

import dev.craftwarestudios.tema.container.ContainerView
import dev.craftwarestudios.tema.container.GuiListener
import dev.craftwarestudios.tema.container.PageDefinition
import dev.craftwarestudios.tema.container.element.ContainerButtonDefinition
import dev.craftwarestudios.tema.container.element.ContainerToggleDefinition
import dev.craftwarestudios.tema.core.ViewDefinition
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.MenuType
import org.bukkit.plugin.java.JavaPlugin

class ExamplePlugin : JavaPlugin() {
    private val listener = GuiListener()

    private lateinit var gui: ContainerView

    override fun onEnable() {
        server.pluginManager.registerEvents(listener, this)
        buildExampleGui()
        logger.info("GuiLibraryExample enabled")
    }

    override fun onDisable() {
        HandlerList.unregisterAll(listener)
    }

    private fun buildExampleGui() {
        gui = ViewDefinition.container().build(
            Component.text("GUI TITLE"),
            MenuType.GENERIC_9X3
        )

        val infoButton = ContainerButtonDefinition()
            .setSlot(gui.center)
            .setIcon(ItemStack(Material.PAPER))
            .setName(Component.text("info"))
            .addDescriptionLine(Component.text("эта кнопка должна быть в центре"))
            .clickAction { ctx ->
                ctx.player.sendMessage(
                    Component.text("${if (ctx.instance.isShared) "shared" else "personal"} container")
                )
            }

        val toggle = ContainerToggleDefinition()
            .setSlot(11)
            .setState(false)
            .setEnabledIcon(ItemStack(Material.LIME_WOOL))
            .setDisabledIcon(ItemStack(Material.RED_WOOL))
            .setEnabledName(Component.text("sIGMA", NamedTextColor.GREEN))
            .setDisabledName(Component.text("Not sigma", NamedTextColor.RED))
            .addEnabledDescriptionLine(Component.text("Click to disable"))
            .addDisabledDescriptionLine(Component.text("Click to enable"))
            .onToggle { ctx, enabled ->
                ctx.player.sendMessage(
                    Component.text("Toggle=$enabled", NamedTextColor.GRAY)
                )
            }

        val nextPage = ContainerButtonDefinition()
            .setSlot(gui.downRight)
            .setIcon(ItemStack(Material.ARROW))
            .setName(Component.text("next page", NamedTextColor.GREEN))
            .addDescriptionLine(Component.text("open page 1"))
            .clickAction { ctx ->
                ctx.instance.navigate(ctx.player, 1)
            }

        val exampleNotAttached = ContainerButtonDefinition()
            .setIcon(Material.BAKED_POTATO)
            .setName(Component.text("Zalupka"))

        val page0 = PageDefinition.Companion
            .builder(title = Component.text("Page 1", NamedTextColor.GOLD)).apply {
                addButton(infoButton)
                addButton(toggle)
                addButton(nextPage)
                addButton(exampleNotAttached)
                addElement(
                    ContainerButtonDefinition()
                        .setSlot(4)
                        .setIcon(ItemStack(Material.BOOK))
                        .setName(Component.text("Header"))
                )
            }

        val backButton = ContainerButtonDefinition()
            .setSlot(22)
            .setIcon(ItemStack(Material.ARROW))
            .setName(Component.text("back"))
            .addDescriptionLine(Component.text("goto page 0"))
            .clickAction { ctx ->
                ctx.instance.navigate(ctx.player, 0)
            }

        val sharedButton = ContainerButtonDefinition()
            .setSlot(13)
            .setIcon(ItemStack(Material.MAP))
            .setName(Component.text("broadcast"))
            .clickAction { ctx ->
                Bukkit.broadcast(Component.text("sharedButton ${ctx.player.name}"))
            }

        val page1 = PageDefinition.Companion.builder(title = Component.text("page 1")).apply {
            addButton(backButton)
            addButton(sharedButton)
            addElement(
                ContainerButtonDefinition()
                    .setSlot(11)
                    .setIcon(ItemStack(Material.CHEST))
                    .setName(Component.text("info"))
            )
        }

        gui.setPage(0, page0)
        gui.setPage(1, page1)
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (command.name != "gui-debug") return false
        val player = sender as? Player ?: return true

        when (args.firstOrNull()?.lowercase()) {
            "instance" -> {
                gui.openInstanced(player, 0)
            }
            "shared" -> {
                gui.open(player, 0)
            }
        }
        return true
    }
}