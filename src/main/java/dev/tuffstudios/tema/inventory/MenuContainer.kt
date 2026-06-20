package dev.tuffstudios.tema.inventory

import org.bukkit.entity.HumanEntity
import org.jetbrains.annotations.ApiStatus
import java.util.UUID

/**
 * Представляет собой экземпляр графического интерфейса на основе [org.bukkit.inventory.Inventory].
 *
 * [MenuContainer] - это пример того, как должен отображаться интерфейс.
 *
 * Именно он должен отвечать за открытие и закрытие этого интерфейса.
 *
 * Различные его реализации могут предоставлять разный набор функций. Некоторые поддерживают страницы,
 * некоторые являются standalone, однако [MenuContainer] описывает базовый минимум того, что должен уметь класс.
 *
 * Стоит учесть, что реализация [asSharedView] является исключительно опциональной.
 * [SharedMenuView] не должен присутствовать в абсолютно каждой реализации интерфейса, это
 * исключительно решение самого разработчика.
 *
 * @author Egor Morozov
 * @since 1.0
 */
@ApiStatus.AvailableSince("1.0")
interface MenuContainer {
    /**
     * Открывает этот интерфейс [viewer]'у.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    fun open(viewer: HumanEntity): Boolean

    /**
     * Закрывает этот интерфейс для [viewer].
     *
     * @author Egor Morozov
     * @since 1.0
     */
    fun close(viewer: HumanEntity): Boolean

    /**
     * Удаляет [viewer] из списка активных просматривающих.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    fun forget(viewer: UUID): Boolean

    /**
     * Открывает shared версию этого интерфейса для [viewer].
     *
     * @author Egor Morozov
     * @since 1.0
     */
    fun asSharedView(viewer: HumanEntity): SharedMenuView?

    /**
     * Проверяет, просматривает ли [player] этот [MenuContainer].
     *
     * @author Egor Morozov
     * @since 1.0
     */
    fun isViewing(player: HumanEntity): Boolean
}