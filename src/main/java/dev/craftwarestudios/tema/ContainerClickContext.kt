package dev.craftwarestudios.tema

import dev.craftwarestudios.tema.inventory.InventoryContainerView
import dev.craftwarestudios.tema.inventory.InventoryContainerPage
import dev.craftwarestudios.tema.inventory.slot.SlotDefinition
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType

data class ContainerClickContext(
    val player: Player,
    val instance: InventoryContainerView,
    val page: InventoryContainerPage,
    val element: SlotDefinition,
    val clickType: ClickType,
)