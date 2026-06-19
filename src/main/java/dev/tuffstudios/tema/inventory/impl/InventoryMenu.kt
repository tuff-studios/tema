package dev.tuffstudios.tema.inventory.impl

import dev.tuffstudios.tema.inventory.SharedMenuView
import dev.tuffstudios.tema.inventory.impl.view.InstancedInventoryViewImpl
import dev.tuffstudios.tema.inventory.impl.view.SharedMenuViewImpl
import dev.tuffstudios.tema.objectToObjectMap
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.MenuType
import org.jetbrains.annotations.ApiStatus
import java.util.*

@Suppress("UnstableApiUsage")
@ApiStatus.AvailableSince("1.0")
class InventoryMenu(
    type: MenuType,
    init: AbstractInventoryMenu.() -> Unit
) : AbstractInventoryMenu(type, init) {
    private var built: Boolean = false
    private var sharedView: SharedMenuView = SharedMenuViewImpl(this, this)
    private val views: Object2ObjectLinkedOpenHashMap<UUID, InstancedInventoryViewImpl> = objectToObjectMap()

    override fun open(viewer: HumanEntity): Boolean {
        if (!this.built) this.build(); this.built = true
        if (viewer !is Player || !viewer.isOnline) return false
        if (!this.views.containsKey(viewer.uniqueId)) {
            this.views[viewer.uniqueId] = InstancedInventoryViewImpl(this, this, viewer)
        }

        this.views[viewer.uniqueId]!!.open()
        return true
    }

    override fun asSharedView(viewer: HumanEntity): SharedMenuView {
        this.sharedView.open(viewer)
        return this.sharedView
    }

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

    override fun forget(viewer: UUID): Boolean {
        return this.views.remove(viewer) != null
    }

    override fun isViewing(player: HumanEntity): Boolean {
        val view = this.views[player.uniqueId] ?: return false
        return view.isViewing(player)
    }
}