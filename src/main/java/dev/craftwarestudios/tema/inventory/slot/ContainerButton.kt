package dev.craftwarestudios.tema.inventory.slot

import dev.craftwarestudios.tema.ContainerClickContext
import dev.craftwarestudios.tema.inventory.slot.builder.ContainerButtonBuilder
import dev.craftwarestudios.tema.withNameAndLore
import net.kyori.adventure.text.Component
import org.bukkit.inventory.ItemStack

open class ContainerButton
internal constructor(
    private val iconDefinition: ItemStack,
    private val displayName: Component,
    private val description: MutableList<Component>,
    private val clickActions: MutableList<(ContainerClickContext) -> Unit>
) : SlotDefinition {
    fun handleClick(ctx: ContainerClickContext) {
        clickActions.forEach { it(ctx) }
    }

    override fun renderToItem(): ItemStack {
        return iconDefinition.clone().withNameAndLore(displayName, description)
    }

    companion object {
        fun builder() = ContainerButtonBuilder()
    }
}