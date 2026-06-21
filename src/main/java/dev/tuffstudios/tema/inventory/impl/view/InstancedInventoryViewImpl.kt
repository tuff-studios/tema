package dev.tuffstudios.tema.inventory.impl.view

import dev.tuffstudios.tema.ContainerClickContext
import dev.tuffstudios.tema.TEMA
import dev.tuffstudios.tema.inventory.view.InstancedMenuView
import dev.tuffstudios.tema.inventory.MenuContainer
import dev.tuffstudios.tema.inventory.MenuContent
import dev.tuffstudios.tema.inventory.impl.slot.InventoryButton
import dev.tuffstudios.tema.inventory.impl.slot.InventoryToggle
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.InventoryView
import org.jetbrains.annotations.ApiStatus

/**
 * Реализация класса [InstancedMenuView].
 *
 * Каждый [InstancedInventoryViewImpl] уникален для игрока и одной его сессии игры на сервере.
 *
 * После выхода [viewer] с сервера каждый его [InstancedInventoryViewImpl] должен быть утилизирован.
 *
 * @author Egor Morozov
 * @since 1.0
 */
@Suppress("UnstableApiUsage")
@ApiStatus.AvailableSince("1.0")
class InstancedInventoryViewImpl(
    val content: MenuContent,
    override val menu: MenuContainer,
    override val viewer: HumanEntity
) : InstancedMenuView {
    /**
     * Текущий [InventoryView], в котором находится как инвентарь [menu],
     * так и инвентарь самого игрока [viewer].
     *
     * @author Egor Morozov
     * @since 1.0
     */
    override var inventoryView: InventoryView? = null

    /**
     * Открывает этот [InstancedInventoryViewImpl] для [viewer].
     *
     * @author Egor Morozov
     * @since 1.0
     */
    override fun open() {
        val view = this.content.type.create(viewer, this.content.title)
        this.renderToView(view, this.viewer)
        view.open()

        this.inventoryView = view
        TEMA.getInstance().register(this.viewer as Player, this)
    }

    /**
     * Закрывает этот [InstancedInventoryViewImpl] для [viewer].
     *
     * @author Egor Morozov
     * @since 1.0
     */
    override fun close(): Boolean {
        if (this.inventoryView != null) {
            if (this.viewer.openInventory == this.inventoryView) {
                this.viewer.closeInventory(InventoryCloseEvent.Reason.PLUGIN)
                TEMA.getInstance().unregister(this.viewer)
                return true
            } else {
                return false
            }
        }

        return false
    }

    /**
     * Перерисовывает этот [InstancedInventoryViewImpl] для [viewer].
     *
     * @author Egor Morozov
     * @since 1.0
     */
    override fun refresh(): Boolean {
        val view = this.inventoryView ?: return false
        this.renderToView(view, viewer)
        return true
    }

    /**
     * Проверяет, просматривается ли этот [InstancedInventoryViewImpl].
     *
     * @author Egor Morozov
     * @since 1.0
     */
    fun isViewing(): Boolean {
        return this.isViewing(this.viewer)
    }

    /**
     * Проверяет, просматривается ли этот [InstancedInventoryViewImpl] игроком [player].
     *
     * @author Egor Morozov
     * @since 1.0
     */
    override fun isViewing(player: HumanEntity): Boolean {
        return player.openInventory == this.inventoryView
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