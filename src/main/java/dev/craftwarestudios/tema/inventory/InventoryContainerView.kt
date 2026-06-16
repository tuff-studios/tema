package dev.craftwarestudios.tema.inventory

import dev.craftwarestudios.tema.ContainerClickContext
import dev.craftwarestudios.tema.ContainerRegistry
import dev.craftwarestudios.tema.inventory.slot.ContainerButton
import dev.craftwarestudios.tema.inventory.slot.ContainerToggle
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import java.util.*

class InventoryContainerView
    internal constructor(val parent: InventoryContainer, val isShared: Boolean)
{
    private var _sharedPageId: Int = 0
    private val _playerPageId: Object2IntLinkedOpenHashMap<UUID> = Object2IntLinkedOpenHashMap()
    private val _playerOpenedView: Object2ObjectLinkedOpenHashMap<UUID, InventoryView> = Object2ObjectLinkedOpenHashMap()

    private var _defaultPage: Int = _sharedPageId

    init {
        _playerPageId.defaultReturnValue(0)
    }

    fun currentPage(): Int {
        if (isShared) {
            return _sharedPageId
        } else {
            ContainerRegistry.slf4jLogger.warn("currentPage() is called inside a non-shared InventoryContainer.")
            return _sharedPageId
        }
    }

    fun currentPage(playerId: UUID?): Int {
        return _playerPageId.getInt(playerId)
    }

    fun defaultPageId(index: Int) {
        _defaultPage = index
        _playerPageId.defaultReturnValue(index)
    }

    fun defaultSharedPageId(index: Int) {
        _sharedPageId = index
    }

    fun openPage(target: Player, page: Int = _defaultPage) {
        this.open(target, page)
    }

    fun open(target: Player, page: Int = _defaultPage): InventoryContainerView {
        val pageIndex = parent.resolvePageIndex(page)

        _playerPageId[target.uniqueId] = pageIndex
        _playerOpenedView.remove(target.uniqueId)

        val pageDefinition = parent.getPage(pageIndex)
        val inventoryView = parent.type.create(target, pageDefinition.title)
        this.render(inventoryView, pageDefinition)
        inventoryView.open()

        _playerOpenedView[target.uniqueId] = inventoryView
        ContainerRegistry.register(target, this)

        return this
    }

    fun close(target: Player, force: Boolean): Boolean {
        if (!_playerOpenedView.containsKey(target.uniqueId)) return false

        if (force) target.closeInventory()

        _playerOpenedView.remove(target.uniqueId)
        if (isShared) {
            ContainerRegistry.unregister(target, this)
            return true
        }

        _playerPageId.removeInt(target.uniqueId)
        ContainerRegistry.unregister(target, this)
        return true
    }

    fun refresh(target: Player): Boolean {
        val inventoryView = _playerOpenedView[target.uniqueId] ?: return false
        val pageIndex = currentPage(target.uniqueId)
        val page = parent.getPage(pageIndex)
        render(inventoryView, page)
        return true
    }

    fun refreshAll(): Boolean {
        var changed = false
        _playerOpenedView.keys.toList().forEach { playerId ->
            Bukkit.getPlayer(playerId)?.let {
                changed = this.refresh(it) || changed
            }
        }
        return changed
    }

    fun handleClick(event: org.bukkit.event.inventory.InventoryClickEvent): Boolean {
        val player = event.whoClicked as? Player ?: return false
        val page = parent.getPage(currentPage(player.uniqueId))

        if (event.rawSlot < 0 || event.rawSlot >= event.view.topInventory.size) {
            return false
        }

        val element = page.elements.toList().firstOrNull {
            candidate -> candidate.first == event.rawSlot
        }?.second ?: return false

        event.isCancelled = true

        val context = ContainerClickContext(
            player = player,
            instance = this,
            page = page,
            element = element,
            clickType = event.click,
        )

        when (element) {
            is ContainerButton -> element.handleClick(context)
            is ContainerToggle -> element.toggle(context)
            else -> Unit
        }

        this.refresh(player)
        return true
    }

    private fun render(inventoryView: InventoryView, page: PageDefinition) {
        val inventory = inventoryView.topInventory
        inventory.clear()

        repeat(inventory.size) {
            inventory.setItem(it, ItemStack.of(page.fill))
        }

        page.elements.forEach { element ->
            if (element.key !in inventory.contents.indices) {
                return@forEach
            }

            inventory.setItem(element.key, element.value.render())
        }
    }
}