package dev.tuffstudios.tema.inventory

import org.bukkit.entity.HumanEntity
import org.bukkit.inventory.InventoryView
import org.jetbrains.annotations.ApiStatus

/**
 * Представляет собой активную сессию просмотра [MenuContainer].
 *
 * Описывает [InventoryMenuView] созданный для одного конкретного игрока.
 *
 * Именно такая концепция позволяет надёжно использовать [org.bukkit.entity.Player] внутри класса,
 * поскольку после выхода игрока с сервера, поле [inventoryView] станет не валидным само по себе.
 *
 * В отличие от [SharedMenuView], этот класс имеет строго обозначенного [viewer]`а.
 *
 * @author Egor Morozov
 * @since 1.0
 */
@ApiStatus.AvailableSince("1.0")
interface InstancedMenuView : InventoryMenuView {
    /**
     * Игрок, который может просматривать это меню.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    val viewer: HumanEntity

    /**
     * Конкретный [InventoryView], который был открыт последним.
     *
     * [InventoryView] сохраняет свою актуальность на время всей сессии игры игрока
     * на сервере, однако для полной смены страницы (её `title` и содержимого вместе) требуется
     * полностью обновить [InventoryView], а значит, по сути, открыть новый.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    var inventoryView: InventoryView?

    /**
     * Открывает этот [InstancedMenuView] для [viewer].
     *
     * @author Egor Morozov
     * @since 1.0
     */
    fun open()

    /**
     * Закрывает этот [InstancedMenuView] для [viewer].
     *
     * @author Egor Morozov
     * @since 1.0
     */
    override fun close(): Boolean

    /**
     * Перерисовывает этот [InstancedMenuView] для [viewer].
     *
     * @author Egor Morozov
     * @since 1.0
     */
    override fun refresh(): Boolean
}