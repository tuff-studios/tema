package dev.tuffstudios.tema.inventory

import org.bukkit.entity.HumanEntity
import org.bukkit.event.inventory.InventoryClickEvent
import org.jetbrains.annotations.ApiStatus

/**
 * Представляет собой активную сессию просмотра [MenuContainer].
 *
 * Такая сессия разделена на два типа: [InstancedMenuView] и [SharedMenuView].
 *
 * Разные типы сессий имеют немного различную структуру и предназначение.
 *
 * Каждый класс имеет поле [menu] типа [MenuContainer], слепком которого он является.
 *
 * @author Egor Morozov
 * @since 1.0
 */
@ApiStatus.AvailableSince("1.0")
interface InventoryMenuView {
    /**
     * Контейнер, на котором основан этот [InventoryMenuView].
     *
     * @author Egor Morozov
     * @since 1.0
     */
    val menu: MenuContainer

    /**
     * Закрывает этот [InventoryMenuView]
     *
     * В зависимости от реализации [InventoryMenuView] этот метод может либо закрывать
     * интерфейс целиком, либо закрывать его для определенного игрока.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    fun close(): Boolean

    /**
     * Проверяет, просматривает ли [player] этот конкретный [InventoryMenuView].
     *
     * @author Egor Morozov
     * @since 1.0
     */
    fun isViewing(player: HumanEntity): Boolean

    /**
     * Перерисовывает этот [InventoryMenuView].
     *
     * В зависимости от реализации [InventoryMenuView] этот метод может либо обновлять
     * интерфейс для всех, либо только для определенного игрока.
     * @author Egor Morozov
     * @since 1.0
     */
    fun refresh(): Boolean

    /**
     * Обрабатывает ивент клика по инвентарю, переданный от слушателя.
     *
     * В задачи метода входит отмена ивента, определение кликнутого элемента и
     * обновление контента при необходимости.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    fun handleOnClick(event: InventoryClickEvent): Boolean
}