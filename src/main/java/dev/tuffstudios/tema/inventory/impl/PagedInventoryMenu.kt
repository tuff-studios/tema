package dev.tuffstudios.tema.inventory.impl

import dev.tuffstudios.tema.inventory.InstancedMenuView
import dev.tuffstudios.tema.inventory.MenuContainer
import dev.tuffstudios.tema.inventory.SharedMenuView
import dev.tuffstudios.tema.inventory.impl.view.InstancedInventoryViewImpl
import dev.tuffstudios.tema.inventory.impl.view.SharedMenuViewImpl
import dev.tuffstudios.tema.objectToObjectMap
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap
import org.bukkit.Bukkit
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import org.jetbrains.annotations.ApiStatus
import java.util.UUID

/**
 * Стандартная реализация [AbstractPagedInventoryMenu].
 *
 * Этот класс реализует функции интерфейса [MenuContainer], такие как:
 * [open], [close], [asSharedView] и другие для воссоздания требуемого поведения класса.
 *
 * Этот класс так же подразумевает использование методов из [AbstractPagedInventoryMenu] для
 * создания страниц, в которых и должно быть расположено наполнение итогового графического интерфейса.
 *
 * @param init блок инициализации, в которых должно происходить заполнение инвентаря страницами и их контентом.
 *
 * @author Egor Morozov
 * @since 1.0
 */
@ApiStatus.AvailableSince("1.0")
class PagedInventoryMenu(
    init: AbstractPagedInventoryMenu.() -> Unit
) : AbstractPagedInventoryMenu(init) {
    /**
     * Определяет, было ли создано наполнение этого [PagedInventoryMenu].
     *
     * @author Egor Morozov
     * @since 1.0
     */
    private var built: Boolean = false

    /**
     * Возвращает [SharedMenuView] для этого [InventoryMenu].
     *
     * @author Egor Morozov
     * @since 1.0
     */
    private var sharedView: SharedMenuViewImpl = getSharedView()

    /**
     * Возвращает текущую страницу [sharedView].
     *
     * Эта страница будет отображаться игроком в открывшемся [sharedView].
     *
     * @author Egor Morozov
     * @since 1.0
     */
    private var sharedMenuViewPage: Int = 0

    /**
     * Список [InstancedMenuView] этого [PagedInventoryMenu].
     *
     * Стоит учесть, что каждый [InstancedMenuView] актуален лишь одну сессию игрока, что значит,
     * что после выхода игрока с сервера, созданный [InstancedMenuView] должен быть забыт, поскольку
     * он, так же как и [org.bukkit.inventory.InventoryView], актуален лишь пока игрок находится онлайн.
     *
     * @see forget
     *
     * @author Egor Morozov
     * @since 1.0
     */
    private val views: Object2ObjectLinkedOpenHashMap<UUID, InstancedInventoryViewImpl> = objectToObjectMap()

    /**
     * Открывает этот [PagedInventoryMenu] для [viewer].
     *
     * @author Egor Morozov
     * @since 1.0
     */
    override fun open(viewer: HumanEntity): Boolean {
        return this.open(viewer, 0)
    }

    /**
     * Открывает этот интерфейс на странице [page] для [viewer].
     *
     * Метод вернет `false`, если страницы [page] не существует в этом [PagedInventoryMenu],
     * [viewer] не является игроком, либо этот игрок сейчас не онлайн.
     *
     * @author Egor Morozov
     * @sicne 1.0
     */
    fun open(viewer: HumanEntity, page: Int): Boolean {
        if (!this.built) this.build(); this.built = true
        val page = getPage(page) ?: return false
        if (viewer !is Player || !viewer.isOnline) return false
        this.views[viewer.uniqueId] = InstancedInventoryViewImpl(page, this, viewer)
        this.views[viewer.uniqueId]!!.open()
        return true
    }

    /**
     * Открывает shared версию этого интерфейса для [viewer].
     *
     * @author Egor Morozov
     * @since 1.0
     */
    override fun asSharedView(viewer: HumanEntity): SharedMenuView {
        this.sharedView.open(viewer)
        return this.sharedView
    }

    fun navigateSharedTo(page: Int) {
        this.sharedMenuViewPage = page
        val viewers = this.sharedView.viewers
        this.sharedView = getSharedView()

        val iterator = viewers.iterator()
        while (iterator.hasNext()) {
            Bukkit.getPlayer(iterator.next())?.let { player ->
                this.sharedView.open(player)
            }
        }
    }

    /**
     * Делает страницу [page] "открытой" в каждом [InstancedMenuView] этого [PagedInventoryMenu].
     *
     * Этот метод не устанавливает номер страницы для [asSharedView], для него стоит использовать [navigateSharedTo].
     *
     * @author Egor Morozov
     * @since 1.0
     */
    override fun navigateTo(page: Int) {
        val iterator = this.views.keys.iterator()
        while (iterator.hasNext()) {
            val uniqueId = iterator.next()
            Bukkit.getPlayer(uniqueId)?.let { player ->
                this.open(player, page)
            } ?: {
                this.views.remove(uniqueId)
            }
        }
    }

    /**
     * Делает страницу [page] "открытой" в [InstancedMenuView] игрока [viewer].
     *
     * Этот метод переключает страницу путём пере-открытия инвентаря, так что метод [open]
     * будет так же вызван.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    fun navigateTo(page: Int, viewer: HumanEntity) {
        this.open(viewer, page)
    }

    /**
     * Закрывает этот интерфейс для [viewer].
     *
     * @author Egor Morozov
     * @since 1.0
     */
    override fun close(viewer: HumanEntity): Boolean {
        val uniqueId = viewer.uniqueId
        if (this.views[uniqueId] != null) {
            if (viewer.openInventory == this.views[uniqueId]) {
                viewer.closeInventory(InventoryCloseEvent.Reason.PLUGIN)
                return true
            } else {
                return false
            }
        }

        return false
    }

    /**
     * Заставляет [PagedInventoryMenu] "забыть" viewer`а с айди [viewer].
     *
     * По сути, метод [forget] удаляет нужный [UUID] из списка [views].
     *
     * @author Egor Morozov
     * @since 1.0
     */
    override fun forget(viewer: UUID): Boolean {
        return this.views.remove(viewer) != null
    }

    /**
     * Проверяет, просматривает ли [player] этот интерфейс.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    override fun isViewing(player: HumanEntity): Boolean {
        this.views[player.uniqueId]?.let {
            return it.inventoryView == player.openInventory
        }

        return this.sharedView.isViewing(player)
    }

    private fun getSharedView(): SharedMenuViewImpl {
        return SharedMenuViewImpl(getPage(this.sharedMenuViewPage)
            ?: throw IllegalStateException("PagedInventoryMenu has no pages."), this)
    }
}