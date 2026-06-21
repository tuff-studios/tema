package dev.tuffstudios.tema.inventory

import org.jetbrains.annotations.ApiStatus

/**
 * Интерфейс добавляющий поддержку многостраничности.
 *
 * Этот класс напрямую не является реализацией или подтипом [MenuContainer] или
 * [dev.tuffstudios.tema.inventory.view.InventoryMenuView], но подразумевает наличие многостраничности в этих классах.
 *
 * @author Egor Morozov
 * @since 1.0
 */
@ApiStatus.AvailableSince("1.0")
interface PagedMenu {
    /**
     * Делает страницу [page] "открытой" в текущем элементе.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    fun navigateTo(page: Int)

    /**
     * Возвращает [dev.tuffstudios.tema.inventory.impl.InventoryMenuPage] под указанным [index]ом.
     *
     * @return [dev.tuffstudios.tema.inventory.impl.InventoryMenuPage] под переданным [index], если она зарегистрирована в [MenuContainer], иначе `null`.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    fun getPage(index: Int): MenuContent?

    /**
     * Устанавливает заданный [page] на указанный [index].
     *
     * @param page Если `null`, страница под индексом [index] должна удалиться из списка.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    fun setPage(index: Int, page: MenuContent?)

    /**
     * Возвращает список страниц этого [PagedMenu].
     *
     * @author Egor Morozov
     * @since 1.0
     */
    fun listPages(): List<MenuContent>

    /**
     * Возвращает итератор для страниц этого [PagedMenu].
     *
     * @author Egor Morozov
     * @since 1.0
     */
    fun pageIterator(): Iterator<MenuContent>
}