package dev.craftwarestudios.tema.inventory.slot.builder

import dev.craftwarestudios.tema.Anchor
import dev.craftwarestudios.tema.ContainerClickContext
import dev.craftwarestudios.tema.inventory.InventoryContainer
import dev.craftwarestudios.tema.inventory.slot.ContainerToggle
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class ContainerToggleBuilder {
    private var slot: Int = -1
    private var anchor: Anchor? = null

    private var enabledIcon: ItemStack = ItemStack.of(Material.AIR)
    private var disabledIcon: ItemStack = ItemStack.of(Material.AIR)

    private var enabledTitle: Component? = null
    private var disabledTitle: Component? = null

    private val enabledDescriptionLines: MutableList<Component> = mutableListOf()
    private val disabledDescriptionLines: MutableList<Component> = mutableListOf()

    private val changeActions: MutableList<(ContainerClickContext, Boolean) -> Unit> = mutableListOf()

    fun setSlot(slot: Int): ContainerToggleBuilder = apply {
        this.slot = slot
    }

    fun setSlot(anchor: Anchor): ContainerToggleBuilder = apply {
        this.anchor = anchor
    }

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

    fun onToggle(action: (ContainerClickContext, Boolean) -> Unit): ContainerToggleBuilder = apply {
        this.changeActions += action
    }

    fun build(container: InventoryContainer): ContainerToggle {
        if (this.slot == -1 && this.anchor == null) {
            throw IllegalArgumentException("Toggle button slot or anchor should be set first.")
        }

        val slot = this.anchor?.let { container.resolveAnchorPosition(it) } ?: this.slot
        return ContainerToggle(
            slot,
            this.enabledIcon, this.disabledIcon,
            this.enabledTitle ?: this.enabledIcon.displayName(),
            this.disabledTitle ?: this.disabledIcon.displayName(),
            this.enabledDescriptionLines, this.disabledDescriptionLines,
            this.changeActions
        )
    }
}