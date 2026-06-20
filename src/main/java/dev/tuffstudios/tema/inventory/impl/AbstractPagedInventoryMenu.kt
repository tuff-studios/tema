package dev.tuffstudios.tema.inventory.impl

import dev.tuffstudios.tema.TEMA
import dev.tuffstudios.tema.intToObjectMap
import dev.tuffstudios.tema.inventory.MenuContainer
import dev.tuffstudios.tema.inventory.MenuContent
import dev.tuffstudios.tema.inventory.PagedMenu
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap
import org.jetbrains.annotations.ApiStatus

/**
 * Абстрактная реализация [MenuContainer] с поддержкой страниц.
 *
 * Этот класс реализует методы интерфейса [PagedMenu], что позволяет ему не наследовать
 * [MenuContent] напрямую, а вместо этого использовать отдельные классы страниц, например, [InventoryMenuPage].
 *
 * Весь код создания интерфейса должен быть передан в параметр конструктора [createUI], либо через
 * реализацию абстрактной функции [AbstractPagedInventoryMenu.createUI].
 *
 * При создании [createUI] нужно учитывать, что в первую очередь все содержимое должно быть передано
 * через страницы, так как стандартной реализации [MenuContent] этот класс не содержит.
 *
 * @author Egor Morozov
 * @since 1.0
 */
@ApiStatus.AvailableSince("1.0")
abstract class AbstractPagedInventoryMenu(
    private val createUI: (AbstractPagedInventoryMenu.() -> Unit)? = null
) : MenuContainer, PagedMenu {
    /**
     * Список страниц, существующих в рамках этого [AbstractPagedInventoryMenu].
     *
     * @author Egor Morozov
     * @since 1.0
     */
    private val pages: Int2ObjectLinkedOpenHashMap<MenuContent> = intToObjectMap()

    /**
     * Вызывает [createUI], что инициализирует контейнер и страницы меню.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    internal fun build() {
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
    open fun createUI() {
        this.createUI?.invoke(this)
    }

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