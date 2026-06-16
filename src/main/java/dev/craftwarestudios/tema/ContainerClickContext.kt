package dev.craftwarestudios.tema

import dev.craftwarestudios.tema.inventory.InventoryContainerView
import dev.craftwarestudios.tema.inventory.PageDefinition
import dev.craftwarestudios.tema.inventory.slot.AbstractContainerButton
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType

data class ContainerClickContext(
    val player: Player,
    val instance: InventoryContainerView,
    val page: PageDefinition,
    val element: AbstractContainerButton,
    val clickType: ClickType,
)