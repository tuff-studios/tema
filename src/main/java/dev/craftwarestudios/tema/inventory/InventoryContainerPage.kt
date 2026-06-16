package dev.craftwarestudios.tema.inventory

import dev.craftwarestudios.tema.Anchor
import dev.craftwarestudios.tema.inventory.slot.SlotDefinition
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class InventoryContainerPage(val container: InventoryContainer) {
    var title: Component? = null

    var fill: ItemStack = ItemStack.of(Material.AIR)
    val elements: Int2ObjectLinkedOpenHashMap<SlotDefinition> = Int2ObjectLinkedOpenHashMap()

    fun setTitle(c: Component?): InventoryContainerPage = apply {
        this.title = c
    }

    fun setSlot(slot: Int, element: SlotDefinition?): InventoryContainerPage = apply {
        if (this.elements.keys.contains(slot)) {
            throw IllegalArgumentException("Button with the same id is already present.")
        }

        this.elements.put(slot, element)
    }

    fun setSlot(anchor: Anchor, element: SlotDefinition?): InventoryContainerPage = apply {
        val slot = this.container.resolveAnchorPosition(anchor)

        if (this.elements.keys.contains(slot)) {
            throw IllegalArgumentException("Button with the same id is already present.")
        }

        this.elements.put(slot, element)
    }

    fun setBackgroundFill(item: ItemStack?): InventoryContainerPage = apply {
        this.fill = item?.clone() ?: this.fill
    }

    fun setBackgroundFill(material: Material?): InventoryContainerPage = apply {
        this.fill = ItemStack.of(material ?: Material.AIR)
    }

    internal fun resolveSlotId(slotDefinition: SlotDefinition): Int? {
        this.elements.forEach {
            if (it.value === slotDefinition) return it.key
        }

        return null
    }

    companion object {
        fun builder(container: InventoryContainer, title: Component? = null, fill: Material? = null): InventoryContainerPage {
            return InventoryContainerPage(container).apply {
                setTitle(title)
                setBackgroundFill(fill)
            }
        }
    }
}