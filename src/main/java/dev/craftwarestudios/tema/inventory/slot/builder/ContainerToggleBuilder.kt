package dev.craftwarestudios.tema.inventory.slot.builder

import dev.craftwarestudios.tema.ContainerClickContext
import dev.craftwarestudios.tema.inventory.InventoryContainer
import dev.craftwarestudios.tema.inventory.slot.ContainerToggle
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class ContainerToggleBuilder {
    private var enabledIcon: ItemStack = ItemStack.of(Material.AIR)
    private var disabledIcon: ItemStack = ItemStack.of(Material.AIR)

    private var enabledTitle: Component? = null
    private var disabledTitle: Component? = null

    private val enabledDescriptionLines: MutableList<Component> = mutableListOf()
    private val disabledDescriptionLines: MutableList<Component> = mutableListOf()

    private val changeActions: MutableList<(ContainerClickContext, Boolean) -> Unit> = mutableListOf()

    fun setEnabledIcon(icon: ItemStack): ContainerToggleBuilder = apply {
        this.enabledIcon = icon
    }

    fun setDisabledIcon(icon: ItemStack): ContainerToggleBuilder = apply {
        this.disabledIcon = icon
    }

    fun setEnabledTitle(c: Component): ContainerToggleBuilder = apply {
        this.enabledTitle = c
    }

    fun setDisabledTitle(c: Component): ContainerToggleBuilder = apply {
        this.disabledTitle = c
    }

    fun addEnabledDescriptionLine(c: Component): ContainerToggleBuilder = apply {
        this.enabledDescriptionLines += c
    }

    fun addDisabledDescriptionLine(c: Component): ContainerToggleBuilder = apply {
        this.disabledDescriptionLines += c
    }

    fun editEnabledItem(action: (ItemStack) -> Unit): ContainerToggleBuilder = apply {
        this.enabledIcon.apply(action)
    }

    fun editDisabledItem(action: (ItemStack) -> Unit): ContainerToggleBuilder = apply {
        this.disabledIcon.apply(action)
    }

    fun onToggle(action: (ContainerClickContext, Boolean) -> Unit): ContainerToggleBuilder = apply {
        this.changeActions += action
    }

    fun build(container: InventoryContainer): ContainerToggle {
        return ContainerToggle(
            this.enabledIcon, this.disabledIcon,
            this.enabledTitle ?: this.enabledIcon.displayName(),
            this.disabledTitle ?: this.disabledIcon.displayName(),
            this.enabledDescriptionLines, this.disabledDescriptionLines,
            this.changeActions
        )
    }
}