package dev.craftwarestudios.tema.examples

import dev.craftwarestudios.tema.Anchor
import dev.craftwarestudios.tema.inventory.InventoryContainer
import dev.craftwarestudios.tema.inventory.InventoryContainerPage
import dev.craftwarestudios.tema.inventory.slot.ContainerButton
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.inventory.MenuType

class ExampleMultiPageGui {
    val container9x3: InventoryContainer = InventoryContainer(MenuType.GENERIC_9X3)
    val container9x5: InventoryContainer = InventoryContainer(MenuType.GENERIC_9X5)

    init {
        val topLeft = ContainerButton.builder()
            .setIcon(Material.BLACK_CONCRETE)
            .setTitle(Component.text("Anchor.TOP_LEFT"))

        val top = ContainerButton.builder()
            .setIcon(Material.BLACK_CONCRETE)
            .setTitle(Component.text("Anchor.TOP"))


        val topRight = ContainerButton.builder()
            .setIcon(Material.BLACK_CONCRETE)
            .setTitle(Component.text("Anchor.TOP_RIGHT"))

        val left = ContainerButton.builder()
            .setIcon(Material.BLACK_CONCRETE)
            .setTitle(Component.text("Anchor.LEFT"))
            .addDescriptionLine(Component.text("GOTO 1"))
            .clickAction { ctx ->
                ctx.instance.openPage(ctx.player, 1)
            }

        val center = ContainerButton.builder()
            .setIcon(Material.BLACK_CONCRETE)
            .setTitle(Component.text("Anchor.CENTER"))

        val right = ContainerButton.builder()
            .setIcon(Material.BLACK_CONCRETE)
            .setTitle(Component.text("Anchor.RIGHT"))
            .addDescriptionLine(Component.text("GOTO 0"))
            .clickAction { ctx ->
                ctx.instance.openPage(ctx.player, 0)
            }

        val bottomLeft = ContainerButton.builder()
            .setIcon(Material.BLACK_CONCRETE)
            .setTitle(Component.text("Anchor.BOTTOM_LEFT"))

        val bottom = ContainerButton.builder()
            .setIcon(Material.BLACK_CONCRETE)
            .setTitle(Component.text("Anchor.BOTTOM"))

        val bottomRight = ContainerButton.builder()
            .setIcon(Material.BLACK_CONCRETE)
            .setTitle(Component.text("Anchor.BOTTOM_RIGHT"))

        container9x3.setPage(0, InventoryContainerPage.builder(container9x3, title = Component.text("Multipaging debug/Page 0")).apply {
            setSlot(Anchor.TOP_LEFT, topLeft.build(container9x3))
                .setSlot(Anchor.TOP_RIGHT, topRight.build(container9x3))
                .setSlot(Anchor.TOP, top.build(container9x3))
            setSlot(Anchor.LEFT, left.build(container9x3))
                .setSlot(Anchor.CENTER, center.build(container9x3))
                .setSlot(Anchor.RIGHT, right.build(container9x3))
            setSlot(Anchor.BOTTOM_LEFT, bottomLeft.build(container9x3))
                .setSlot(Anchor.BOTTOM, bottom.build(container9x3))
                .setSlot(Anchor.BOTTOM_RIGHT, bottomRight.build(container9x3))
        })
        container9x3.setPage(1, InventoryContainerPage.builder(container9x3, title = Component.text("Multipaging debug/Page 1")).apply {
            setSlot(Anchor.TOP_LEFT, topLeft.build(container9x3))
                .setSlot(Anchor.TOP_RIGHT, topRight.build(container9x3))
                .setSlot(Anchor.TOP, top.build(container9x3))
            setSlot(Anchor.LEFT, left.build(container9x3))
                .setSlot(Anchor.CENTER, center.build(container9x3))
                .setSlot(Anchor.RIGHT, right.build(container9x3))
            setSlot(Anchor.BOTTOM_LEFT, bottomLeft.build(container9x3))
                .setSlot(Anchor.BOTTOM, bottom.build(container9x3))
                .setSlot(Anchor.BOTTOM_RIGHT, bottomRight.build(container9x3))
        })

        container9x5.setPage(0, InventoryContainerPage.builder(container9x5, title = Component.text("Multipaging debug/Page 0")).apply {
            setSlot(Anchor.TOP_LEFT, topLeft.build(container9x5))
                .setSlot(Anchor.TOP_RIGHT, topRight.build(container9x5))
                .setSlot(Anchor.TOP, top.build(container9x5))
            setSlot(Anchor.LEFT, left.build(container9x5))
                .setSlot(Anchor.CENTER, center.build(container9x5))
                .setSlot(Anchor.RIGHT, right.build(container9x5))
            setSlot(Anchor.BOTTOM_LEFT, bottomLeft.build(container9x5))
                .setSlot(Anchor.BOTTOM, bottom.build(container9x5))
                .setSlot(Anchor.BOTTOM_RIGHT, bottomRight.build(container9x5))
        })

        container9x5.setPage(1, InventoryContainerPage.builder(container9x5, title = Component.text("Multipaging debug/Page 1")).apply {
            setSlot(Anchor.TOP_LEFT, topLeft.build(container9x5))
                .setSlot(Anchor.TOP_RIGHT, topRight.build(container9x5))
                .setSlot(Anchor.TOP, top.build(container9x5))
            setSlot(Anchor.LEFT, left.build(container9x5))
                .setSlot(Anchor.CENTER, center.build(container9x5))
                .setSlot(Anchor.RIGHT, right.build(container9x5))
            setSlot(Anchor.BOTTOM_LEFT, bottomLeft.build(container9x5))
                .setSlot(Anchor.BOTTOM, bottom.build(container9x5))
                .setSlot(Anchor.BOTTOM_RIGHT, bottomRight.build(container9x5))
        })
    }
}