package dev.craftwarestudios.tema.inventory

import dev.craftwarestudios.tema.Anchor
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.inventory.MenuType
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class InventoryContainer
    internal constructor(val type: MenuType, val title: Component? = Component.empty())
{
    private val _size: Int
    private val _rows: Int

    private val _anchorPositions: MutableMap<Anchor, Int> = mutableMapOf()

    private val _pages: ConcurrentHashMap<Int, PageDefinition> = ConcurrentHashMap()
    private val _views: ConcurrentHashMap<UUID, InventoryContainerView> = ConcurrentHashMap()
    private val _sharedState: InventoryContainerView = InventoryContainerView(this, true)

    val mainPage: PageDefinition get() = getPage(0)

    fun getPage(index: Int): PageDefinition {
        return _pages.getOrPut(index) {
            PageDefinition.builder(this, this.title)
        }
    }

    fun setPage(index: Int, pageDefinition: PageDefinition) {
        _pages[index] = pageDefinition
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

    internal fun resolvePageIndex(index: Int): Int {
        if (_pages.isEmpty()) {
            _pages[0] = PageDefinition.builder(this, this.title)
        }
        return if (_pages.containsKey(index)) index else 0
    }

    internal fun resolveAnchorPosition(anchor: Anchor): Int {
        return _anchorPositions[anchor]!!
    }

    init {
        _pages[0] = PageDefinition.builder(this, this.title)
        _size = when (this.type) {
            MenuType.GENERIC_9X1 -> 9
            MenuType.GENERIC_9X2 -> 18
            MenuType.GENERIC_9X3 -> 27
            MenuType.GENERIC_9X4 -> 36
            MenuType.GENERIC_9X5 -> 45
            MenuType.GENERIC_9X6 -> 54
            else -> 27
        }
        _rows = _size / 9
        val middleRow = _rows / 2

        _anchorPositions.put(Anchor.TOP_LEFT, 0)
        _anchorPositions.put(Anchor.TOP_RIGHT, 8)
        _anchorPositions.put(Anchor.TOP, 4)
        _anchorPositions.put(Anchor.BOTTOM_LEFT, _size - 9)
        _anchorPositions.put(Anchor.BOTTOM_RIGHT, _size - 1)
        _anchorPositions.put(Anchor.BOTTOM, _size - 5)
        _anchorPositions.put(Anchor.LEFT, middleRow * 9)
        _anchorPositions.put(Anchor.RIGHT, middleRow * 9 + 8)
        _anchorPositions.put(Anchor.CENTER, middleRow * 9 + 4)
    }
}