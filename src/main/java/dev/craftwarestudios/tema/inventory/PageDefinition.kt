package dev.craftwarestudios.tema.inventory

import dev.craftwarestudios.tema.inventory.slot.AbstractContainerButton
import net.kyori.adventure.text.Component
import org.bukkit.Material

class PageDefinition
    (val container: InventoryContainer, var title: Component? = null)
{
    var fill: Material = Material.AIR
    val elements: MutableMap<Int, AbstractContainerButton> = mutableMapOf()

    fun addButton(button: AbstractContainerButton): PageDefinition = apply {
        if (this.elements.keys.contains(button.slot)) {
            throw IllegalArgumentException("Button with the same id is already present.")
        }

        this.elements.put(button.slot, button)
    }

    fun setBackground(material: Material): PageDefinition = apply {
        this.fill = material
    }

    fun clear(): PageDefinition = apply {
        this.elements.clear()
        this.fill = Material.AIR
    }

    companion object {
        fun builder(container: InventoryContainer, title: Component? = null): PageDefinition = PageDefinition(container, title)
    }
}