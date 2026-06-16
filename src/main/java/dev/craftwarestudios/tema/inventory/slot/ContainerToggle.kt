package dev.craftwarestudios.tema.inventory.slot

import dev.craftwarestudios.tema.ContainerClickContext
import dev.craftwarestudios.tema.inventory.slot.builder.ContainerToggleBuilder
import dev.craftwarestudios.tema.withNameAndLore
import net.kyori.adventure.text.Component
import org.bukkit.inventory.ItemStack

open class ContainerToggle
internal constructor(
    private val enabledIcon: ItemStack,
    private val disabledIcon: ItemStack,
    private val enabledTitle: Component,
    private val disabledTitle: Component,
    private val enabledDescription: MutableList<Component>,
    private val disabledDescription: MutableList<Component>,
    private val changeActions: MutableList<(ContainerClickContext, Boolean) -> Unit>
) : SlotDefinition {
    private var _isToggled: Boolean = false

    fun isToggled(): Boolean {
        return _isToggled
    }

    fun toggle(context: ContainerClickContext) {
        _isToggled = !_isToggled
        changeActions.forEach { it(context, _isToggled) }
    }

    override fun renderToItem(): ItemStack {
        val item = if (isToggled()) enabledIcon.clone() else disabledIcon.clone()
        return if (isToggled()) {
            item.withNameAndLore(enabledTitle, enabledDescription)
        } else {
            item.withNameAndLore(disabledTitle, disabledDescription)
        }
    }

    companion object {
        fun builder() = ContainerToggleBuilder()
    }
}