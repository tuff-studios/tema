package dev.tuffstudios.tema.inventory.impl.slot

import dev.tuffstudios.tema.inventory.slot.SlotDefinition
import dev.tuffstudios.tema.inventory.slot.beans.ButtonClickAction
import dev.tuffstudios.tema.inventory.slot.beans.ButtonRenderAction
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.jetbrains.annotations.ApiStatus

/**
 * Представляет собой кликабельный слот инвентаря.
 *
 * @author Egor Morozov
 * @since 1.0
 */
class InventoryButton : SlotDefinition {
    private var icon: Material = Material.GRAY_CONCRETE
    private var title: Component = Component.text("Button")
    private val description: MutableList<Component> = mutableListOf()

    private val clickActions: MutableList<ButtonClickAction> = mutableListOf()
    private val itemMutations: MutableList<(ItemStack) -> Unit> = mutableListOf()

    @ApiStatus.Experimental
    private var renderAction: ButtonRenderAction = { viewer ->
        val item = ItemStack.of(icon)
        item.itemMeta.displayName(title)
        item.lore(this.description.toList())
        item
    }

    /**
     * Устанавливает иконку этой кнопки.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    fun setIcon(material: Material) = apply {
        this.icon = material
    }

    /**
     * Устанавливает заголовок этой кнопки.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    fun setTitle(component: Component) = apply {
        this.title = component
    }

    /**
     * Устанавливает описание этой кнопки.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    fun setDescription(list: List<Component>) = apply {
        this.description.clear()
        this.description += list
    }

    /**
     * Добавляет строку описания этой кнопки.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    fun addDescriptionLine(component: Component) = apply {
        this.description += component
    }

    /**
     * Добавляет действие при клике на кнопку.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    fun onClick(action: ButtonClickAction) = apply {
        this.clickActions += action
    }

    /**
     * **EXPERIMENTAL**: Сохраняет действие рендера в список.
     *
     * После окончания рендера кнопки в [ItemStack], это действие будет применены к предмету.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    @ApiStatus.Experimental
    fun setOnRender(action: ButtonRenderAction) = apply {
        this.renderAction = action
    }

    /**
     * Применяет данное [action] к [ItemStack],
     * который возвращается методом [renderToItem].
     *
     * @author Egor Morozov
     * @since 1.0
     */
    override fun editItem(action: (ItemStack) -> Unit) = apply {
        this.itemMutations += action
    }

    /**
     * Возвращает список [ButtonClickAction], каждое из которых должно быть
     * исполнено при нажатии на эту кнопку.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    fun listClickActions(): List<ButtonClickAction> = this.clickActions

    override fun renderToItem(viewer: HumanEntity): ItemStack {
        val item = this.renderAction.invoke(viewer as Player)
        this.itemMutations.forEach { it(item) }
        return item
    }
}