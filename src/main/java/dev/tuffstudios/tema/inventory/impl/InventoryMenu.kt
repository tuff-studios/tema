package dev.tuffstudios.tema.inventory.impl

import dev.tuffstudios.tema.inventory.view.SharedMenuView
import dev.tuffstudios.tema.inventory.impl.view.InstancedInventoryViewImpl
import dev.tuffstudios.tema.inventory.impl.view.SharedMenuViewImpl
import dev.tuffstudios.tema.inventory.view.InstancedMenuView
import dev.tuffstudios.tema.inventory.MenuContainer
import dev.tuffstudios.tema.objectToObjectMap
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.MenuType
import org.jetbrains.annotations.ApiStatus
import java.util.*

/**
 * Стандартная реализация [AbstractInventoryMenu].
 *
 * Этот класс реализует функции интерфейса [MenuContainer], такие как:
 * [open], [close], [asSharedView] и другие для воссоздания требуемого поведения класса.
 *
 * Этот класс так же подразумевает использование методов из [AbstractInventoryMenu] для
 * создания самого графического интерфейса.
 *
 * @param type тип меню, используемый при создании интерфейса.
 * @param init блок инициализации, в котором должно происходить заполнение инвентаря контентом.
 *
 * @author Egor Morozov
 * @since 1.0
 */
@Suppress("UnstableApiUsage")
@ApiStatus.AvailableSince("1.0")
class InventoryMenu(
    type: MenuType,
    init: AbstractInventoryMenu.() -> Unit
) : AbstractInventoryMenu(type, init) {
    /**
     * Определяет, было ли создано наполнение этого [InventoryMenu].
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
    private var sharedView: SharedMenuView = SharedMenuViewImpl(this, this)

    /**
     * Список [InstancedMenuView] этого [InventoryMenu].
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
     * Открывает этот интерфейс для [viewer].
     *
     * Помимо открытия этот метод так же создает наполнение инвентаря,
     * если оно не было создано од этого.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    override fun open(viewer: HumanEntity): Boolean {
        if (!this.built) this.build(); this.built = true
        if (viewer !is Player || !viewer.isOnline) return false
        if (!this.views.containsKey(viewer.uniqueId)) {
            this.views[viewer.uniqueId] = InstancedInventoryViewImpl(this, this, viewer)
        }

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

    /**
     * Закрывает этот интерфейс для [viewer].
     *
     * @author Egor Morozov
     * @since 1.0
     */
    override fun close(viewer: HumanEntity): Boolean {
        if (!this.views.containsKey(viewer.uniqueId)) {
            return false
        }

        if (viewer is Player && !viewer.isOnline) {
            this.views.remove(viewer.uniqueId)
            return true
        }

        (this.views[viewer.uniqueId] ?: return false).close()
        return true
    }

    /**
     * Заставляет [InventoryMenu] "забыть" viewer`а с айди [viewer].
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
        val view = this.views[player.uniqueId] ?: return this.sharedView.isViewing(player)
        return view.isViewing(player)
    }
}