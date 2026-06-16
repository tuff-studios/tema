package dev.craftwarestudios.tema.examples

import dev.craftwarestudios.tema.Anchor
import dev.craftwarestudios.tema.inventory.InventoryContainer
import dev.craftwarestudios.tema.inventory.PageDefinition
import dev.craftwarestudios.tema.inventory.slot.ContainerButton
import dev.craftwarestudios.tema.inventory.slot.ContainerToggle
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.MenuType

class ExampleThingGui {
    val container: InventoryContainer = InventoryContainer(MenuType.GENERIC_9X3)

    init {
        val infoButton = ContainerButton.builder()
            .setSlot(Anchor.CENTER)
            .setIcon(ItemStack(Material.PAPER))
            .setTitle(Component.text("info"))
            .addDescriptionLine(Component.text("эта кнопка должна быть в центре"))
            .clickAction { ctx ->
                ctx.player.sendMessage(
                    Component.text("${if (ctx.instance.isShared) "shared" else "personal"} container")
                )
            }
            .build(container)

        val toggle = ContainerToggle.builder()
            .setSlot(11)
            .setEnabledIcon(ItemStack(Material.LIME_WOOL))
            .setDisabledIcon(ItemStack(Material.RED_WOOL))
            .setEnabledTitle(Component.text("On", NamedTextColor.GREEN))
            .setDisabledTitle(Component.text("Not On", NamedTextColor.RED))
            .addEnabledDescriptionLine(Component.text("Click to disable"))
            .addDisabledDescriptionLine(Component.text("Click to enable"))
            .onToggle { ctx, enabled ->
                ctx.player.sendMessage(
                    Component.text("toggle state = $enabled", NamedTextColor.GRAY)
                )
            }
            .build(container)

        val nextPage = ContainerButton.builder()
            .setSlot(Anchor.BOTTOM_RIGHT)
            .setIcon(ItemStack(Material.ARROW))
            .setTitle(Component.text("next page", NamedTextColor.GREEN))
            .addDescriptionLine(Component.text("goto page 1"))
            .clickAction { ctx ->
                ctx.instance.openPage(ctx.player, 1)
            }
            .build(container)

        val example = ContainerButton.builder()
            .setSlot(Anchor.RIGHT)
            .setIcon(Material.BAKED_POTATO)
            .setTitle(Component.text("kartoplya"))
            .build(container)

        val page0 = PageDefinition
            .builder(container, title = Component.text("Page 0", NamedTextColor.GOLD)).apply {
                addButton(infoButton)
                addButton(toggle)
                addButton(nextPage)
                addButton(example)
                addButton(
                    ContainerButton.builder()
                        .setSlot(4)
                        .setIcon(ItemStack(Material.BOOK))
                        .setTitle(Component.text("Header"))
                        .build(this@ExampleThingGui.container)
                )
            }

        val backButton = ContainerButton.builder()
            .setSlot(22)
            .setIcon(ItemStack(Material.ARROW))
            .setTitle(Component.text("prev page"))
            .addDescriptionLine(Component.text("goto page 0"))
            .clickAction { ctx ->
                ctx.instance.open(ctx.player, 0)
            }
            .build(container)

        val sharedButton = ContainerButton.builder()
            .setSlot(13)
            .setIcon(ItemStack(Material.MAP))
            .setTitle(Component.text("broadcast"))
            .clickAction { ctx ->
                Bukkit.broadcast(Component.text("sharedButton ${ctx.player.name}"))
            }
            .build(container)

        val page1 = PageDefinition.builder(container, title = Component.text("Page 1", NamedTextColor.RED)).apply {
            addButton(backButton)
            addButton(sharedButton)
            addButton(
                ContainerButton.builder()
                    .setSlot(11)
                    .setIcon(ItemStack(Material.CHEST))
                    .setTitle(Component.text("info"))
                    .build(this@ExampleThingGui.container)
            )
        }

        container.setPage(0, page0)
        container.setPage(1, page1)
    }
}