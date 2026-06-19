package dev.tuffstudios.tema.inventory.impl.slot

import dev.tuffstudios.tema.inventory.slot.SlotDefinition
import dev.tuffstudios.tema.inventory.slot.beans.ButtonRenderAction
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.jetbrains.annotations.ApiStatus

class InventoryIcon : SlotDefinition {
    private var icon: Material = Material.DIAMOND
    private var title: Component = Component.text("Icon")
    private val description: MutableList<Component> = mutableListOf()

    private val itemMutations: MutableList<(ItemStack) -> Unit> = mutableListOf()

    @ApiStatus.Experimental
    private var renderAction: ButtonRenderAction = { viewer ->
        val item = ItemStack.of(icon)
        item.itemMeta.displayName(title)
        item.lore(this.description.toList())
        item
    }

    fun setIcon(material: Material) = apply {
        this.icon = material
    }

    fun setTitle(component: Component) = apply {
        this.title = component
    }

    fun setDescription(list: List<Component>) = apply {
        this.description.clear()
        this.description += list
    }

    fun addDescriptionLine(component: Component) = apply {
        this.description += component
    }

    @ApiStatus.Experimental
    fun setOnRender(action: ButtonRenderAction) = apply {
        this.renderAction = action
    }

    override fun editItem(action: (ItemStack) -> Unit) = apply {
        this.itemMutations += action
    }

    override fun renderToItem(viewer: HumanEntity): ItemStack {
        val item = this.renderAction.invoke(viewer as Player)
        this.itemMutations.forEach { it(item) }
        return item
    }
}