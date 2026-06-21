package dev.tuffstudios.tema

import dev.tuffstudios.tema.inventory.view.InventoryMenuView
import dev.tuffstudios.tema.inventory.MenuContent
import dev.tuffstudios.tema.inventory.slot.SlotDefinition
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType

data class ContainerClickContext(
    val player: Player,
    val view: InventoryMenuView,
    val page: MenuContent,
    val element: SlotDefinition,
    val clickType: ClickType,
)