package dev.tuffstudios.tema.inventory

import dev.tuffstudios.tema.inventory.slot.SlotDefinition
import dev.tuffstudios.tema.inventory.impl.AbstractPagedInventoryMenu
import net.kyori.adventure.text.Component
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.MenuType
import org.jetbrains.annotations.ApiStatus

/**
 * Представляет собой контент интерфейса.
 *
 * Такая страница хранит в себе список слотов для отображения, фон, заголовок
 * и базовые методы для манипуляций со страницей.
 *
 * Отдельными реализациями страницы может владеть [PagedMenu], который используется
 * при создании многостраничных интерфейсов, вроде [AbstractPagedInventoryMenu].
 *
 * @author Egor Morozov
 * @since 1.0
 */
@ApiStatus.AvailableSince("1.0")
interface MenuContent {
    /**
     * Размер страницы в виде [MenuType].
     *
     * На данный момент поддерживается только список [supportedMenuTypes].
     *
     * @author Egor Morozov
     * @since 1.0
     */
    @Suppress("UnstableApiUsage") val type: MenuType

    /**
     * Заголовок страницы отображающийся в левом верхнем углу
     * при открытии инвентаря.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    var title: Component

    /**
     * Наполнитель, которым должен быть заполнен задний фон
     * инвентаря, там, где не установлены никакие другие элементы.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    var background: ItemStack

    /**
     * Список элементов, которые должна отобразить страница.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    val elements: Map<Int, SlotDefinition>

    /**
     * Устанавливает заголовок страницы.
     *
     * @param title заголовок, отображающийся в левом верхнем углу инвентаря.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    fun setTitle(title: Component): MenuContent

    /**
     * Устанавливает [ItemStack]-наполнитель, которым должен быть заполнен задний фон
     * инвентаря, там, где не установлены никакие другие элементы.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    fun setBackground(item: ItemStack): MenuContent

    /**
     * Устанавливает [element] в слот [slot].
     *
     * @author Egor Morozov
     * @since 1.0
     */
    fun setSlot(slot: Int, element: SlotDefinition?): MenuContent

    /**
     * Возвращает итератор для слотов этой страницы.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    fun slotsIterator(): Iterator<SlotDefinition> {
        return this.elements.values.iterator()
    }

    /**
     * Возвращает неизменяемую копию списка элементов этой [MenuContent].
     *
     * @author Egor Morozov
     * @since 1.0
     */
    fun listSlotDefinitions(): List<SlotDefinition>
}