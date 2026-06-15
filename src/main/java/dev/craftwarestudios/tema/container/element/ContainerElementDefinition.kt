package dev.craftwarestudios.tema.container.element

import dev.craftwarestudios.tema.core.ElementDefinition
import org.bukkit.inventory.ItemStack

interface ContainerElementDefinition : ElementDefinition {
    var slot: Int?

    fun render(): ItemStack
    fun copyDefinition(): ContainerElementDefinition
}
