package dev.tuffstudios.tema.inventory.impl

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

@ApiStatus.AvailableSince("1.0")
class PagedInventoryMenu(
    init: AbstractPagedInventoryMenu.() -> Unit
) : AbstractPagedInventoryMenu(init) {
    private var built: Boolean = false
    private var sharedView: SharedMenuViewImpl = getSharedView()
    private var sharedMenuViewPage: Int = 0

    private val views: Object2ObjectLinkedOpenHashMap<UUID, InstancedInventoryViewImpl> = objectToObjectMap()

    override fun open(viewer: HumanEntity): Boolean {
        return this.open(viewer, 0)
    }

    fun open(viewer: HumanEntity, page: Int): Boolean {
        if (!this.built) this.build(); this.built = true
        val page = getPage(page) ?: return false
        if (viewer !is Player || !viewer.isOnline) return false
        this.views[viewer.uniqueId] = InstancedInventoryViewImpl(page, this, viewer)
        this.views[viewer.uniqueId]!!.open()
        return true
    }

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

    fun navigateTo(page: Int, viewer: HumanEntity) {
        this.open(viewer, page)
    }

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

    override fun forget(viewer: UUID): Boolean {
        return this.views.remove(viewer) != null
    }

    override fun isViewing(player: HumanEntity): Boolean {
        this.views[player.uniqueId]?.let {
            return it.inventoryView == player.openInventory
        }

        return false
    }

    private fun getSharedView(): SharedMenuViewImpl {
        return SharedMenuViewImpl(getPage(this.sharedMenuViewPage)
            ?: throw IllegalStateException("PagedInventoryMenu has no pages."), this)
    }
}