package dev.tuffstudios.tema.inventory.impl

import dev.tuffstudios.tema.TEMA
import dev.tuffstudios.tema.intToObjectMap
import dev.tuffstudios.tema.inventory.MenuContainer
import dev.tuffstudios.tema.inventory.MenuContent
import dev.tuffstudios.tema.inventory.PagedMenu
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap
import org.jetbrains.annotations.ApiStatus

@ApiStatus.AvailableSince("1.0")
abstract class AbstractPagedInventoryMenu : MenuContainer, PagedMenu {
    /**
     * Список страниц, существующих в рамках этого [AbstractPagedInventoryMenu].
     *
     * @author Egor Morozov
     * @since 1.0
     */
    private val pages: Int2ObjectLinkedOpenHashMap<MenuContent> = intToObjectMap()

    init {
        createUI()
    }

    /**
     * Функция для классов - наследователей.
     *
     * Именно здесь может происходить заполнение фона, заголовка, создание кнопок и т.д.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    abstract fun createUI()

    /**
     * Делает страницу с индексом [page] "открытой" в текущем [AbstractPagedInventoryMenu].
     *
     * @author Egor Morozov
     * @since 1.0
     */
    abstract override fun navigateTo(page: Int)

    /**
     * Возвращает [InventoryMenuPage] под указанным [index]ом.
     *
     * @return [InventoryMenuPage] под переданным [index], если она зарегистрирована в [MenuContainer], иначе `null`.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    override fun getPage(index: Int): MenuContent? {
        return this.pages[index]
    }

    /**
     * Устанавливает заданный [page] на указанный [index].
     *
     * @param page Если `null`, страница под индексом [index] должна удалиться из списка.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    override fun setPage(index: Int, page: MenuContent?) {
        if (this.pages[index] != null) {
            TEMA.getInstance().slF4JLogger.warn(
                "Page $index of container is already set, replacing anyway."
            )
        }

        if (page == null) this.pages.remove(index)
        else this.pages.put(index, page)
    }

    /**
     * Возвращает список страниц этого [PagedMenu].
     *
     * @author Egor Morozov
     * @since 1.0
     */
    override fun listPages(): List<MenuContent> {
        return this.pages.values.toList()
    }

    /**
     * Возвращает итератор для страниц этого [PagedMenu].
     *
     * @author Egor Morozov
     * @since 1.0
     */
    override fun pageIterator(): Iterator<MenuContent> {
        return this.pages.values.iterator()
    }
}