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

/**
 * Представляет собой кликабельный слот инвентаря, имеющий два состояния.
 *
 * Эта реализация [SlotDefinition] имеет [state] `true` или `false`, и соответствующие
 * поля для двух этих состояний: [iconEnabled], [iconDisabled], [titleEnabled] и т.д.
 *
 * Для изменения первоначального состояния можно использовать [setState].
 *
 * @author Egor Morozov
 * @since 1.0
 */
class InventoryToggle : SlotDefinition {
    /**
     * Состояние этого тоггла.
     *
     * @author Egor Morozov
     * @since 1.0
     */
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

    /**
     * Устанавливает [state] этого тоггла.
     *
     * Имеет значение `false` по умолчанию.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    fun setState(state: Boolean) = apply {
        this.isToggled = state
    }

    /**
     * Устанавливает иконки этого тоггла.
     *
     * @param enabled [Material] для включенного состояния.
     * @param disabled [Material] для выключенного состояния.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    fun setIcons(enabled: Material, disabled: Material) = apply {
        this.iconEnabled = enabled
        this.iconDisabled = disabled
    }

    /**
     * Устанавливает заголовки этого тоггла.
     *
     * @param enabled [Component] для включенного состояния.
     * @param disabled [Component] для выключенного состояния.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    fun setTitles(enabled: Component, disabled: Component) = apply {
        this.titleEnabled = enabled
        this.titleDisabled = disabled
    }

    /**
     * Устанавливает описание для включенного состояния.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    fun setEnabledDescription(list: List<Component>) = apply {
        this.descriptionEnabled.clear()
        this.descriptionEnabled += list
    }

    /**
     * Устанавливает описание для выключенного состояния.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    fun setDisabledDescription(list: List<Component>) = apply {
        this.descriptionDisabled.clear()
        this.descriptionDisabled += list
    }

    /**
     * Добавляет строку описания для включенного состояния.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    fun addEnabledDescriptionLine(component: Component) = apply {
        this.descriptionEnabled += component
    }

    /**
     * Добавляет строку описания для выключенного состояния.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    fun addDisabledDescriptionLine(component: Component) = apply {
        this.descriptionDisabled += component
    }

    /**
     * Добавляет действие при переключении состояния.
     *
     * В контекст передается [dev.tuffstudios.tema.ContainerClickContext] и [Boolean].
     *
     * @author Egor Morozov
     * @since 1.0
     */
    fun onToggle(action: ToggleClickAction) = apply {
        this.toggleActions += action
    }

    /**
     * **EXPERIMENTAL**: Сохраняет действие рендера в список.
     *
     * После окончания рендера тоггла в [ItemStack], это действие будет применены к предмету.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    @ApiStatus.Experimental
    fun setOnRender(action: ToggleRenderAction) = apply {
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
     * Возвращает список [ToggleClickAction], каждое из которых должно быть
     * исполнено при переключении состояния этого тоггла.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    fun listToggleActions(): List<ToggleClickAction> = this.toggleActions

    override fun renderToItem(viewer: HumanEntity): ItemStack {
        val item = this.renderAction.invoke(viewer as Player, this.state)
        this.itemMutations.forEach { it(item) }
        return item
    }
}