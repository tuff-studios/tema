package dev.craftwarestudios.tema.container.element

import dev.craftwarestudios.tema.container.ContainerClickContext
import dev.craftwarestudios.tema.withNameAndLore
import java.util.UUID
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class ContainerToggleDefinition : ContainerElementDefinition {
    override var slot: Int? = null
    override val elementId: UUID = UUID.randomUUID()

    private var enabled: Boolean = false

    private var enabledIconDefinition: ItemStack = ItemStack(Material.LIME_WOOL)
    private var disabledIconDefinition: ItemStack = ItemStack(Material.RED_WOOL)

    private var enabledName: Component? = null
    private var disabledName: Component? = null

    private val enabledLore: MutableList<Component> = mutableListOf()
    private val disabledLore: MutableList<Component> = mutableListOf()

    private val changeActions: MutableList<(ContainerClickContext, Boolean) -> Unit> = mutableListOf()

    fun setSlot(slot: Int?): ContainerToggleDefinition = apply {
        this.slot = slot
    }

    fun setState(enabled: Boolean): ContainerToggleDefinition = apply {
        this.enabled = enabled
    }

    fun setEnabledIcon(itemStack: ItemStack): ContainerToggleDefinition = apply {
        enabledIconDefinition = itemStack.clone()
    }

    fun setDisabledIcon(itemStack: ItemStack): ContainerToggleDefinition = apply {
        disabledIconDefinition = itemStack.clone()
    }

    fun setEnabledName(name: Component): ContainerToggleDefinition = apply {
        enabledName = name
    }

    fun setDisabledName(name: Component): ContainerToggleDefinition = apply {
        disabledName = name
    }

    fun addEnabledDescriptionLine(line: Component): ContainerToggleDefinition = apply {
        enabledLore += line
    }

    fun addDisabledDescriptionLine(line: Component): ContainerToggleDefinition = apply {
        disabledLore += line
    }

    fun onToggle(action: (ContainerClickContext, Boolean) -> Unit): ContainerToggleDefinition = apply {
        changeActions += action
    }

    fun isToggled(): Boolean {
        return this.enabled
    }

    fun toggle(context: ContainerClickContext) {
        enabled = !enabled
        changeActions.forEach { action ->
            action(context, enabled)
        }
    }

    override fun render(): ItemStack {
        val stack = if (enabled) enabledIconDefinition.clone() else disabledIconDefinition.clone()
        return if (enabled) {
            stack.withNameAndLore(enabledName, enabledLore)
        } else {
            stack.withNameAndLore(disabledName, disabledLore)
        }
    }

    override fun copyDefinition(): ContainerElementDefinition = ContainerToggleDefinition().apply {
        slot = this@ContainerToggleDefinition.slot
        setState(this@ContainerToggleDefinition.enabled)
        setEnabledIcon(this@ContainerToggleDefinition.enabledIconDefinition)
        setDisabledIcon(this@ContainerToggleDefinition.disabledIconDefinition)
        this@ContainerToggleDefinition.enabledName?.let { setEnabledName(it) }
        this@ContainerToggleDefinition.disabledName?.let { setDisabledName(it) }
        this@ContainerToggleDefinition.enabledLore.forEach { addEnabledDescriptionLine(it) }
        this@ContainerToggleDefinition.disabledLore.forEach { addDisabledDescriptionLine(it) }
        this@ContainerToggleDefinition.changeActions.forEach { action ->
            onToggle(action)
        }
    }
}
