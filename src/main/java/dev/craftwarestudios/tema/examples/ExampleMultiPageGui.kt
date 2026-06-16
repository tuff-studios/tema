package dev.craftwarestudios.tema.examples

import dev.craftwarestudios.tema.Anchor
import dev.craftwarestudios.tema.inventory.InventoryContainer
import dev.craftwarestudios.tema.inventory.PageDefinition
import dev.craftwarestudios.tema.inventory.slot.ContainerButton
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.inventory.MenuType

class ExampleMultiPageGui {
    val container9x3: InventoryContainer = InventoryContainer(MenuType.GENERIC_9X3)
    val container9x6: InventoryContainer = InventoryContainer(MenuType.GENERIC_9X6)

    init {
        val topLeft = ContainerButton.builder()
            .setSlot(Anchor.TOP_LEFT)
            .setIcon(Material.BLACK_CONCRETE)
            .setTitle(Component.text("Anchor.TOP_LEFT"))

        val top = ContainerButton.builder()
            .setSlot(Anchor.TOP)
            .setIcon(Material.BLACK_CONCRETE)
            .setTitle(Component.text("Anchor.TOP"))


        val topRight = ContainerButton.builder()
            .setSlot(Anchor.TOP_RIGHT)
            .setIcon(Material.BLACK_CONCRETE)
            .setTitle(Component.text("Anchor.TOP_RIGHT"))

        val left = ContainerButton.builder()
            .setSlot(Anchor.LEFT)
            .setIcon(Material.BLACK_CONCRETE)
            .setTitle(Component.text("Anchor.LEFT"))
            .addDescriptionLine(Component.text("GOTO 1"))
            .clickAction { ctx ->
                ctx.instance.openPage(ctx.player, 1)
            }

        val center = ContainerButton.builder()
            .setSlot(Anchor.CENTER)
            .setIcon(Material.BLACK_CONCRETE)
            .setTitle(Component.text("Anchor.CENTER"))

        val right = ContainerButton.builder()
            .setSlot(Anchor.RIGHT)
            .setIcon(Material.BLACK_CONCRETE)
            .setTitle(Component.text("Anchor.RIGHT"))
            .addDescriptionLine(Component.text("GOTO 0"))
            .clickAction { ctx ->
                ctx.instance.openPage(ctx.player, 0)
            }

        val bottomLeft = ContainerButton.builder()
            .setSlot(Anchor.BOTTOM_LEFT)
            .setIcon(Material.BLACK_CONCRETE)
            .setTitle(Component.text("Anchor.BOTTOM_LEFT"))

        val bottom = ContainerButton.builder()
            .setSlot(Anchor.BOTTOM)
            .setIcon(Material.BLACK_CONCRETE)
            .setTitle(Component.text("Anchor.BOTTOM"))

        val bottomRight = ContainerButton.builder()
            .setSlot(Anchor.BOTTOM_RIGHT)
            .setIcon(Material.BLACK_CONCRETE)
            .setTitle(Component.text("Anchor.BOTTOM_RIGHT"))

        container9x3.setPage(0, PageDefinition.builder(container9x3, title = Component.text("Multipaging debug/Page 0")).apply {
            addButton(topLeft.build(container9x3)).addButton(topRight.build(container9x3)).addButton(top.build(container9x3))
            addButton(left.build(container9x3)).addButton(center.build(container9x3)).addButton(right.build(container9x3))
            addButton(bottomLeft.build(container9x3)).addButton(bottom.build(container9x3)).addButton(bottomRight.build(container9x3))
        })
        container9x3.setPage(1, PageDefinition.builder(container9x3, title = Component.text("Multipaging debug/Page 1")).apply {
            addButton(topLeft.build(container9x3)).addButton(topRight.build(container9x3)).addButton(top.build(container9x3))
            addButton(left.build(container9x3)).addButton(center.build(container9x3)).addButton(right.build(container9x3))
            addButton(bottomLeft.build(container9x3)).addButton(bottom.build(container9x3)).addButton(bottomRight.build(container9x3))
        })

        container9x6.setPage(0, PageDefinition.builder(container9x6, title = Component.text("Multipaging debug/Page 0")).apply {
            addButton(topLeft.build(container9x6)).addButton(topRight.build(container9x6)).addButton(top.build(container9x6))
            addButton(left.build(container9x6)).addButton(center.build(container9x6)).addButton(right.build(container9x6))
            addButton(bottomLeft.build(container9x6)).addButton(bottom.build(container9x6)).addButton(bottomRight.build(container9x6))
        })

        container9x6.setPage(1, PageDefinition.builder(container9x6, title = Component.text("Multipaging debug/Page 1")).apply {
            addButton(topLeft.build(container9x6)).addButton(topRight.build(container9x6)).addButton(top.build(container9x6))
            addButton(left.build(container9x6)).addButton(center.build(container9x6)).addButton(right.build(container9x6))
            addButton(bottomLeft.build(container9x6)).addButton(bottom.build(container9x6)).addButton(bottomRight.build(container9x6))
        })
    }
}