package dev.craftwarestudios.tema.inventory.slot.builder

import dev.craftwarestudios.tema.ContainerClickContext
import dev.craftwarestudios.tema.inventory.InventoryContainer
import dev.craftwarestudios.tema.inventory.slot.ContainerButton
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class ContainerButtonBuilder {
    private var icon: ItemStack = ItemStack.of(Material.AIR)
    private var displayName: Component? = null
    private val descriptionLines: MutableList<Component> = mutableListOf()

    private val clickActions: MutableList<(ContainerClickContext) -> Unit> = mutableListOf()

    fun setIcon(material: Material): ContainerButtonBuilder = apply {
        this.icon = ItemStack.of(material)
    }

    fun setIcon(icon: ItemStack): ContainerButtonBuilder = apply {
        this.icon = icon
    }

    fun setTitle(c: Component): ContainerButtonBuilder = apply {
        this.displayName = c
    }

    fun addDescriptionLine(c: Component): ContainerButtonBuilder = apply {
        this.descriptionLines += c
    }

    fun editItem(action: (ItemStack) -> Unit): ContainerButtonBuilder = apply {
        this.icon.apply(action)
    }

    fun clickAction(action: (ContainerClickContext) -> Unit): ContainerButtonBuilder = apply {
        this.clickActions += action
    }

    fun build(container: InventoryContainer): ContainerButton {
        return ContainerButton(
            this.icon,
            this.displayName ?: this.icon.displayName(),
            this.descriptionLines,
            this.clickActions
        )
    }
 }