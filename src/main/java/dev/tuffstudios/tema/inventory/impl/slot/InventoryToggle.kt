package dev.tuffstudios.tema.inventory.impl.slot

import dev.tuffstudios.tema.inventory.slot.SlotDefinition
import dev.tuffstudios.tema.inventory.slot.beans.ToggleClickAction
import dev.tuffstudios.tema.inventory.slot.beans.ToggleRenderAction
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.jetbrains.annotations.ApiStatus

class InventoryToggle : SlotDefinition {
    val state: Boolean get() = isToggled

    private var isToggled: Boolean = false
    private var iconEnabled: Material = Material.GREEN_CONCRETE
    private var iconDisabled: Material = Material.RED_CONCRETE

    private var titleEnabled: Component = Component.text("Enabled")
    private var titleDisabled: Component = Component.text("Disabled")

    private val descriptionEnabled: MutableList<Component> = mutableListOf()
    private val descriptionDisabled: MutableList<Component> = mutableListOf()

    private val toggleActions: MutableList<ToggleClickAction> = mutableListOf()
    private val itemMutations: MutableList<(ItemStack) -> Unit> = mutableListOf()

    @ApiStatus.Experimental
    private var renderAction: ToggleRenderAction = { viewer, state ->
        val item = ItemStack.of(if (state) iconEnabled else iconDisabled)
        item.itemMeta.displayName(if (state) titleEnabled else titleDisabled)
        item.lore(if (state) descriptionEnabled.toList() else descriptionDisabled.toList())
        item
    }

    fun setState(state: Boolean) = apply {
        this.isToggled = state
    }

    fun setIcons(enabled: Material, disabled: Material) = apply {
        this.iconEnabled = enabled
        this.iconDisabled = disabled
    }

    fun setTitles(enabled: Component, disabled: Component) = apply {
        this.titleEnabled = enabled
        this.titleDisabled = disabled
    }

    fun setEnabledDescription(list: List<Component>) = apply {
        this.descriptionEnabled.clear()
        this.descriptionEnabled += list
    }

    fun setDisabledDescription(list: List<Component>) = apply {
        this.descriptionDisabled.clear()
        this.descriptionDisabled += list
    }

    fun addEnabledDescriptionLine(component: Component) = apply {
        this.descriptionEnabled += component
    }

    fun addDisabledDescriptionLine(component: Component) = apply {
        this.descriptionDisabled += component
    }

    fun onToggle(action: ToggleClickAction) = apply {
        this.toggleActions += action
    }

    @ApiStatus.Experimental
    fun setOnRender(action: ToggleRenderAction) = apply {
        this.renderAction = action
    }

    override fun editItem(action: (ItemStack) -> Unit) = apply {
        this.itemMutations += action
    }

    fun listToggleActions(): List<ToggleClickAction> = this.toggleActions

    override fun renderToItem(viewer: HumanEntity): ItemStack {
        val item = this.renderAction.invoke(viewer as Player, this.state)
        this.itemMutations.forEach { it(item) }
        return item
    }
}