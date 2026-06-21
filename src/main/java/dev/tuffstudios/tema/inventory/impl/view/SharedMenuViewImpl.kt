package dev.tuffstudios.tema.inventory.impl.view

import dev.tuffstudios.tema.ContainerClickContext
import dev.tuffstudios.tema.TEMA
import dev.tuffstudios.tema.inventory.view.InventoryMenuView
import dev.tuffstudios.tema.inventory.MenuContainer
import dev.tuffstudios.tema.inventory.MenuContent
import dev.tuffstudios.tema.inventory.view.SharedMenuView
import dev.tuffstudios.tema.inventory.impl.slot.InventoryButton
import dev.tuffstudios.tema.inventory.impl.slot.InventoryToggle
import dev.tuffstudios.tema.objectToObjectMap
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap
import org.bukkit.Bukkit
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.InventoryView
import org.jetbrains.annotations.ApiStatus
import java.util.UUID

/**
 * Реализация класса [SharedMenuView].
 *
 * [SharedMenuViewImpl] является слепком [MenuContent], отображающимся одинаково для всех игроков.
 *
 * После выхода игрока с сервера, его [InventoryView] должен быть удален из списка [views].
 *
 * @author Egor Morozov
 * @since 1.0
 */
@Suppress("UnstableApiUsage")
@ApiStatus.AvailableSince("1.0")
class SharedMenuViewImpl(
    val content: MenuContent,
    override val menu: MenuContainer
) : SharedMenuView {
    /**
     * Список просматривающих.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    val viewers: List<UUID> get() { return this.views.keys.toList() }

    /**
     * Список [InventoryView], ассоциированный с [UUID] игроков, являющихся их владельцами.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    private val views: Object2ObjectLinkedOpenHashMap<UUID, InventoryView> = objectToObjectMap()

    /**
     * Открывает этот [SharedMenuViewImpl] для [target].
     *
     * @author Egor Morozov
     * @since 1.0
     */
    override fun open(target: HumanEntity) {
        val view = this.content.type.create(target, this.content.title)
        this.renderToView(view, target)
        view.open()

        this.views[target.uniqueId] = view
        TEMA.getInstance().register(target as Player, this)
    }

    /**
     * Закрывает этот [SharedMenuViewImpl] для всех просматривающих.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    override fun close(): Boolean {
        val iterator = this.views.keys.iterator()
        var resultCount = 0
        while (iterator.hasNext()) {
            Bukkit.getPlayer(iterator.next())?.let { player ->
                this.close(player)
                resultCount++
            }
        }

        return resultCount >= this.views.size
    }

    /**
     * Закрывает этот [SharedMenuViewImpl] для [target].
     *
     * @author Egor Morozov
     * @since 1.0
     */
    override fun close(target: HumanEntity): Boolean {
        if (this.views[target.uniqueId] != null) {
            if (target.openInventory == this.views[target.uniqueId]) {
                target.closeInventory(InventoryCloseEvent.Reason.PLUGIN)
                TEMA.getInstance().unregister(target)
                return true
            } else {
                return false
            }
        }

        return false
    }

    /**
     * Перерисовывает этот [InstancedInventoryViewImpl] для всех просматривающих.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    override fun refresh(): Boolean {
        val iterator = this.views.keys.iterator()
        var resultCount = 0
        while (iterator.hasNext()) {
            Bukkit.getPlayer(iterator.next())?.let { player ->
                this.refresh(player)
                resultCount++
            }
        }

        return resultCount >= this.views.size
    }

    /**
     * Перерисовывает этот [InstancedInventoryViewImpl] для [target].
     *
     * @author Egor Morozov
     * @since 1.0
     */
    override fun refresh(target: HumanEntity): Boolean {
        val view = this.views[target.uniqueId] ?: return false
        this.renderToView(view, target)
        return true
    }

    /**
     * Проверяет, просматривается ли этот [InstancedInventoryViewImpl] игроком [player].
     *
     * @author Egor Morozov
     * @since 1.0
     */
    override fun isViewing(player: HumanEntity): Boolean {
        return this.views.containsKey(player.uniqueId)
                && this.views[player.uniqueId] == player.openInventory
    }

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
    override fun onQuit(event: PlayerQuitEvent): Boolean {
        val uniqueId = event.player.uniqueId
        return if (this.views.containsKey(uniqueId)) {
            this.views.remove(uniqueId) != null
        } else {
            false
        }
    }

    /**
     * Обрабатывает ивент клика по инвентарю, переданный от слушателя.
     *
     * В задачи метода входит отмена ивента, определение кликнутого элемента и
     * обновление контента при необходимости.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    override fun handleOnClick(event: InventoryClickEvent): Boolean {
        val player = event.whoClicked as? Player ?: return false

        if (event.rawSlot < 0 || event.rawSlot >= event.view.topInventory.size) return false

        val element = this.content.elements.toList()
            .firstOrNull { (slot, _) -> slot == event.rawSlot }
            ?.second ?: return false
        event.isCancelled = true

        val context = ContainerClickContext(
            player = player,
            view = this,
            page = content,
            element = element,
            clickType = event.click
        )

        when (element) {
            is InventoryButton -> element.listClickActions().forEach {
                it.invoke(context)
            }
            is InventoryToggle -> element.listToggleActions().forEach {
                element.setState(!element.state)
                it.invoke(context, element.state)
            }
        }

        this.refresh()
        return true
    }

    internal fun renderToView(view: InventoryView, viewer: HumanEntity) {
        val inventory = view.topInventory
        inventory.clear()

        repeat(inventory.size) {
            inventory.setItem(it, this.content.background)
        }

        this.content.elements.toList().forEach { (slot, item) ->
            if (slot !in inventory.contents.indices) return@forEach
            inventory.setItem(slot, item.renderToItem(viewer))
        }
    }
}