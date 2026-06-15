package dev.craftwarestudios.tema.container

import dev.craftwarestudios.tema.container.element.ContainerElementDefinition
import dev.craftwarestudios.tema.core.ViewDefinition
import net.kyori.adventure.text.Component
import org.bukkit.Material

open class PageDefinition(var title: Component? = null) {
    var fill: Material = Material.AIR
    val elements: MutableList<ContainerElementDefinition> = mutableListOf()

    fun addElement(element: ContainerElementDefinition): PageDefinition = apply {
        this.elements += element
    }

    fun addButton(button: ContainerElementDefinition): PageDefinition = apply {
        this.elements += button
    }

    fun setBackground(material: Material) {
        this.fill = material
    }

    fun clear(): PageDefinition = apply {
        this.elements.clear()
        this.fill = Material.AIR
    }

    companion object {
        fun builder(title: Component? = null): PageDefinition = PageDefinition(title)
    }
}