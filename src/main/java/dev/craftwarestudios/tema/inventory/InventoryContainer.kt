package dev.craftwarestudios.tema.inventory

import dev.craftwarestudios.tema.Anchor
import dev.craftwarestudios.tema.ContainerRegistry
import dev.craftwarestudios.tema.inventory.slot.SlotDefinition
import org.bukkit.entity.Player
import org.bukkit.inventory.MenuType
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class InventoryContainer(val type: MenuType) {
    val size: Int
    private val _anchorPositions: MutableMap<Anchor, Int> = mutableMapOf()

    private val _pages: ConcurrentHashMap<Int, InventoryContainerPage> = ConcurrentHashMap()
    private val _views: ConcurrentHashMap<UUID, InventoryContainerView> = ConcurrentHashMap()
    private val _sharedState: InventoryContainerView = InventoryContainerView(this, true)

    init {
        this.size = getSize(type)
        this.resolveAnchorPositions(type)
    }

    fun getPage(index: Int): InventoryContainerPage? {
        return _pages[index]
    }

    fun setPage(index: Int, pageDefinition: InventoryContainerPage): InventoryContainerPage {
        val current = _pages[index]
        if (current != null) {
            ContainerRegistry.slf4jLogger.warn(
                "InventoryContainer already has ${if (current === pageDefinition) "the same" else "a"} page at the same index."
            )
        }

        _pages[index] = pageDefinition
        return pageDefinition
    }

    fun setDefaultPage(index: Int) {
        if (_pages[index] != null) {
            _views.values.forEach { indexedView ->
                indexedView.defaultPageId(index)
            }
            return
        }

        throw IllegalArgumentException("Page with id $index is not present.")
    }

    fun setDefaultSharedPage(index: Int) {
        if (_pages[index] != null) {
            _sharedState.defaultSharedPageId(index)
            return
        }

        throw IllegalArgumentException("Page with id $index is not present.")
    }

    fun openShared(target: Player, page: Int = 0): InventoryContainerView {
        return _sharedState.open(target, page)
    }

    fun openInstanced(target: Player, page: Int = 0): InventoryContainerView {
        return _views.getOrPut(target.uniqueId) {
            InventoryContainerView(this, false)
        }.open(target, page)
    }

    fun close(target: Player): Boolean {
        _views[target.uniqueId]?.let { return it.close(target, true) }
        return _sharedState.close(target, true)
    }

    internal fun resolvePageIndex(pageDefinition: InventoryContainerPage): Int {
        if (_pages.isEmpty()) {
            _pages[0] = InventoryContainerPage.builder(this)
        }
        return _pages.entries.firstOrNull { it.value === pageDefinition }?.key ?: 0
    }

    internal fun resolveAnchorPosition(anchor: Anchor): Int {
        return _anchorPositions[anchor]!!
    }

    internal fun resolveSlotId(slotDefinition: SlotDefinition): Int? {
        return resolveSlotPage(slotDefinition)?.resolveSlotId(slotDefinition)
    }

    internal fun resolveSlotPage(slotDefinition: SlotDefinition): InventoryContainerPage? {
        return _pages.values.firstOrNull { it.resolveSlotId(slotDefinition) != null }
    }

    internal fun resolveAnchorPositions(menuType: MenuType) {
        val size = getSize(menuType)
        val rows = size / 9
        val middleRow = rows / 2

        _anchorPositions.put(Anchor.TOP_LEFT, 0)
        _anchorPositions.put(Anchor.TOP_RIGHT, 8)
        _anchorPositions.put(Anchor.TOP, 4)
        _anchorPositions.put(Anchor.BOTTOM_LEFT, size - 9)
        _anchorPositions.put(Anchor.BOTTOM_RIGHT, size - 1)
        _anchorPositions.put(Anchor.BOTTOM, size - 5)
        _anchorPositions.put(Anchor.LEFT, middleRow * 9)
        _anchorPositions.put(Anchor.RIGHT, middleRow * 9 + 8)
        _anchorPositions.put(Anchor.CENTER, middleRow * 9 + 4)
    }

    internal fun getSize(menuType: MenuType): Int {
        return when (menuType) {
            MenuType.GENERIC_9X1 -> 9
            MenuType.GENERIC_9X2 -> 18
            MenuType.GENERIC_9X3 -> 27
            MenuType.GENERIC_9X4 -> 36
            MenuType.GENERIC_9X5 -> 45
            MenuType.GENERIC_9X6 -> 54
            else -> 27
        }
    }
}