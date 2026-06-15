package dev.craftwarestudios.tema.container

import dev.craftwarestudios.tema.container.element.ContainerElementDefinition
import org.bukkit.entity.Player

data class ContainerClickContext(
    val player: Player,
    val instance: ContainerViewInstance,
    val page: PageDefinition,
    val element: ContainerElementDefinition,
    val clickType: org.bukkit.event.inventory.ClickType,
)
