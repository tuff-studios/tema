package dev.craftwarestudios.tema.container.element

import dev.craftwarestudios.tema.container.ContainerClickContext
import dev.craftwarestudios.tema.withNameAndLore
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.util.*

class ContainerButtonDefinition : ContainerElementDefinition {
    override var slot: Int? = null
    override val elementId: UUID = UUID.randomUUID()

    private var iconDefinition: ItemStack = ItemStack(Material.BARRIER)
    private var iconDisplayName: Component? = null
    private val descriptionLines: MutableList<Component> = mutableListOf()

    private val clickActions: MutableList<(ContainerClickContext) -> Unit> = mutableListOf()

    fun setSlot(slot: Int?): ContainerButtonDefinition = apply {
        this.slot = slot
    }

    fun setIcon(material: Material): ContainerButtonDefinition = apply {
        this.iconDefinition = ItemStack.of(material)
    }

    fun setIcon(stack: ItemStack): ContainerButtonDefinition = apply {
        this.iconDefinition = stack
    }

    fun setName(name: Component): ContainerButtonDefinition = apply {
        this.iconDisplayName = name
    }

    fun addDescriptionLine(line: Component): ContainerButtonDefinition = apply {
        descriptionLines += line
    }

    fun clickAction(action: (ContainerClickContext) -> Unit): ContainerButtonDefinition = apply {
        clickActions += action
    }

    fun handleClick(context: ContainerClickContext) {
        clickActions.forEach { action -> action(context) }
    }

    override fun render(): ItemStack {
        val effectiveName = if (iconDisplayName == null) iconDefinition.displayName() else iconDisplayName
        return iconDefinition.clone().withNameAndLore(effectiveName, descriptionLines)
    }

    override fun copyDefinition(): ContainerElementDefinition = ContainerButtonDefinition().apply {
        slot = this@ContainerButtonDefinition.slot
        setIcon(this@ContainerButtonDefinition.iconDefinition)
        this@ContainerButtonDefinition.iconDisplayName?.let { setName(it) }
        this@ContainerButtonDefinition.descriptionLines.forEach { addDescriptionLine(it) }
        this@ContainerButtonDefinition.clickActions.forEach { action ->
            clickAction(action)
        }
    }
}
