package dev.craftwarestudios.tema.container

import dev.craftwarestudios.tema.container.element.ContainerButtonDefinition
import dev.craftwarestudios.tema.container.element.ContainerToggleDefinition
import dev.craftwarestudios.tema.core.ViewInstance
import dev.craftwarestudios.tema.core.ViewSession
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import java.util.UUID

// реализация ContainerView
// по сути этот класс является контроллером к ContainerView,
// в то время как ContainerView хранит общую дату, ну или по крайней мере должен
// это просто отвечает за показ другим игрокам и страницы
class ContainerViewInstance
    internal constructor(val parent: ContainerView, val isShared: Boolean): ViewInstance
{
    private var sharedPageIndex: Int = 0
    private val playerPageIndex: Object2IntLinkedOpenHashMap<UUID> = Object2IntLinkedOpenHashMap()
    init { this.playerPageIndex.defaultReturnValue(0) }

    private val playerOpenedView: Object2ObjectLinkedOpenHashMap<UUID, InventoryView> = Object2ObjectLinkedOpenHashMap()

    fun isOpenFor(player: Player): Boolean = this.playerOpenedView.containsKey(player.uniqueId)

    fun currentPage(playerId: UUID?): Int {
        return if (this.isShared) this.sharedPageIndex
        else if (playerId == null) 0 else this.playerPageIndex.getInt(playerId)
    }

    // :thinking:
    fun open(player: Player, page: Int): ContainerViewInstance {
        val pageIndex = this.parent.resolvePageIndex(page)

        if (this.isShared) {
            this.sharedPageIndex = pageIndex
        } else {
            this.playerPageIndex[player.uniqueId] = pageIndex
        }

        val pageDefinition = this.parent.pageDefinition(pageIndex)
        val inventoryView = this.parent.menuType.create(player, pageDefinition.title)
        this.render(inventoryView, pageDefinition)

        inventoryView.open()
        this.playerOpenedView[player.uniqueId] = inventoryView
        ViewSession.register(player, this)

        return this
    }

    // метод open отвечает за первоначальное открытие,
    // а вот navigate как бы отвечает за смену страниц,
    // но по сути это и так и так процесс переоткрытия
    // инвентаря, так что я бы очень сильно подумал над
    // тем как это реализовать
    // но в целом что то есть

    // :thinking:
    fun navigate(player: Player, page: Int) {
        val pageIndex = this.parent.resolvePageIndex(page)

        if (this.isShared) {
            this.sharedPageIndex = pageIndex
        } else {
            this.playerPageIndex[player.uniqueId] = pageIndex
        }

        this.playerOpenedView.remove(player.uniqueId)
        val pageDefinition = this.parent.pageDefinition(pageIndex)
        val inventoryView = this.parent.menuType.create(player, pageDefinition.title)
        this.render(inventoryView, pageDefinition)
        inventoryView.open()

        this.playerOpenedView[player.uniqueId] = inventoryView
    }

     override fun close(player: Player, closeInternally: Boolean): Boolean {
        if (!this.playerOpenedView.containsKey(player.uniqueId)) return false

        if (closeInternally) player.closeInventory()

        this.playerOpenedView.remove(player.uniqueId)
        if (this.isShared) {
            ViewSession.unregister(player, this)
            return true
        }

        this.playerPageIndex.removeInt(player.uniqueId)
        ViewSession.unregister(player, this)
        return true
    }

    override fun refresh(player: Player): Boolean {
        val inventoryView = this.playerOpenedView[player.uniqueId] ?: return false
        val pageIndex = currentPage(player.uniqueId)
        val page = this.parent.pageDefinition(pageIndex)
        this.render(inventoryView, page)
        return true
    }

    override fun refreshAll(): Boolean {
        var changed = false
        this.playerOpenedView.keys.toList().forEach { playerId ->
            Bukkit.getPlayer(playerId)?.let {
                changed = this.refresh(it) || changed
            }
        }
        return changed
    }

    fun handleClick(event: org.bukkit.event.inventory.InventoryClickEvent): Boolean {
        val player = event.whoClicked as? Player ?: return false
        val page = parent.pageDefinition(currentPage(player.uniqueId))

        if (event.rawSlot < 0 || event.rawSlot >= event.view.topInventory.size) {
            return false
        }

        val element = page.elements.toList().firstOrNull { candidate -> candidate.slot == event.rawSlot } ?: return false
        event.isCancelled = true

        val context = ContainerClickContext(
            player = player,
            instance = this,
            page = page,
            element = element,
            clickType = event.click,
        )

        when (element) {
            is ContainerButtonDefinition -> element.handleClick(context)
            is ContainerToggleDefinition -> element.toggle(context)
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

        val occupied = page.elements
            .mapNotNull { it.slot }
            .toMutableList()

        page.elements.forEach { element ->
            val slot = element.slot
                ?: (0 until inventory.size).firstOrNull { it !in occupied }
                ?: return@forEach

            if (slot !in inventory.contents.indices) {
                return@forEach
            }

            occupied += slot
            inventory.setItem(slot, element.render())
        }
    }
}
