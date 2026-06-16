package dev.craftwarestudios.tema.inventory.slot

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

interface SlotDefinition {
    fun renderToItem(): ItemStack

    companion object {
        fun button() = ContainerButton.builder()
        fun toggle() = ContainerToggle.builder()
        fun simple(material: Material, init: (ItemStack) -> Unit): SlotDefinition {
            return object : SlotDefinition {
                private val item: ItemStack = ItemStack.of(material).apply(init)

                fun editItem(action: (ItemStack) -> Unit) {
                    this.item.apply(action)
                }

                override fun renderToItem() = this.item.clone()
            }
        }
    }
}