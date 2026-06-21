package dev.tuffstudios.tema.inventory.view

import org.bukkit.entity.HumanEntity
import org.bukkit.event.player.PlayerQuitEvent
import org.jetbrains.annotations.ApiStatus

/**
 * Представляет собой активную сессию просмотра [dev.tuffstudios.tema.inventory.MenuContainer].
 *
 * Описывает [InventoryMenuView] созданный для общего просмотра.
 *
 * Такой вариант реализации [InventoryMenuView] не имеет одного общего смотрящего.
 *
 * В отличие от [InstancedMenuView], этот класс должен учитывать тот факт, что
 * [InventoryMenuView] может просматриваться сразу многими игроками.
 *
 * @author Egor Morozov
 * @since 1.0
 */
@ApiStatus.AvailableSince("1.0")
interface SharedMenuView : InventoryMenuView {
    /**
     * Открывает этот [SharedMenuView] для [target].
     *
     * @author Egor Morozov
     * @since 1.0
     */
    fun open(target: HumanEntity)

    /**
     * Закрывает этот [SharedMenuView] для всех просматривающих игроков.
     *
     * @param force Должно ли при этом произойти фактическое закрытие инвентарей, а не только логическое.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    override fun close(): Boolean

    /**
     * Закрывает этот [SharedMenuView] для игрока [target].
     *
     * @param force Должно ли при этом произойти фактическое закрытие инвентаря, а не только логическое.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    fun close(target: HumanEntity): Boolean

    /**
     * По аналогии с [handleOnClick] обрабатывает выход игрока.
     *
     * Поскольку этот [InventoryMenuView] является shared, он содержит
     * сразу несколько [org.bukkit.inventory.InventoryView], и должен
     * заниматься их валидацией.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    fun onQuit(event: PlayerQuitEvent): Boolean

    /**
     * Перерисовывает этот [SharedMenuView] для всех просматривающих игроков.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    override fun refresh(): Boolean

    /**
     * Перерисовывает этот [SharedMenuView] для [target].
     *
     * @author Egor Morozov
     * @since 1.0
     */
    fun refresh(target: HumanEntity): Boolean
}