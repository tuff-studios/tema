package dev.craftwarestudios.tema.container

import dev.craftwarestudios.tema.core.ViewDefinition
import dev.craftwarestudios.tema.core.ViewSession
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.inventory.MenuType
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class ContainerView
    internal constructor(val title: Component, val menuType: MenuType) : ViewDefinition
{
    private val pages: ConcurrentHashMap<Int, PageDefinition> = ConcurrentHashMap()
    private val views: ConcurrentHashMap<UUID, ContainerViewInstance> = ConcurrentHashMap()

    private val sharedState: ContainerViewInstance = ContainerViewInstance(parent = this, isShared = true)

    private val size: Int
    private val rows: Int

    val topLeft: Int
    val topRight: Int
    val downLeft: Int
    val downRight: Int
    val top: Int
    val down: Int
    val left: Int
    val right: Int
    val center: Int

    val mainPage: PageDefinition get() {
        return pageDefinition(0)
    }

    override fun pageDefinition(index: Int): PageDefinition {
        return this.pages.getOrPut(index) {
            PageDefinition.builder(this.title)
        }
    }

    override fun setPage(index: Int, page: PageDefinition) {
        this.pages[index] = page
    }

    fun resolvePageIndex(index: Int): Int {
        if (this.pages.isEmpty()) {
            this.pages[0] = PageDefinition.builder(this.title)
        }
        return if (this.pages.containsKey(index)) index else 0
    }

    override fun open(player: Player, page: Int): ContainerViewInstance {
        return this.sharedState.open(player, page)
    }

    override fun openInstanced(player: Player, page: Int): ContainerViewInstance {
        val state = this.views.getOrPut(player.uniqueId) {
            ContainerViewInstance(parent = this, isShared = false)
        }
        return state.open(player, page)
    }

    override fun getView(player: Player): ContainerViewInstance? {
        this.views[player.uniqueId]?.let { return it }
        if (this.sharedState.isOpenFor(player)) {
            return this.sharedState
        }
        return ViewSession.find(player)
    }

    override fun close(player: Player): Boolean {
        this.views[player.uniqueId]?.let { return it.close(player, true) }
        return this.sharedState.close(player, true)
    }

    // пожалуй это я спрячу
    init {
        this.pages[0] = PageDefinition.builder(this.title)
        this.size = when(menuType) {
            MenuType.GENERIC_9X1 -> 9
            MenuType.GENERIC_9X2 -> 18
            MenuType.GENERIC_9X3 -> 27
            MenuType.GENERIC_9X4 -> 36
            MenuType.GENERIC_9X5 -> 45
            MenuType.GENERIC_9X6 -> 54
            else -> 27
        }

        this.rows = this.size / 9

        this.topLeft = 0
        this.topRight = this.size - 1
        this.downLeft = this.size - 9
        this.downRight = this.size - 1
        this.top = 4
        this.down = this.size - 5
        this.left = ((this.rows / 2) * 9)
        this.right = ((this.rows / 2) * 9) + 8
        this.center = this.size / 2
    }
}
