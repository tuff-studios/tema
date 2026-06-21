package dev.tuffstudios.tema.example

import dev.tuffstudios.tema.Anchor
import dev.tuffstudios.tema.inventory.InstancedMenuView
import dev.tuffstudios.tema.inventory.SharedMenuView
import dev.tuffstudios.tema.inventory.impl.AbstractPagedInventoryMenu
import dev.tuffstudios.tema.inventory.impl.InventoryMenuPage
import dev.tuffstudios.tema.inventory.impl.view.InstancedInventoryViewImpl
import dev.tuffstudios.tema.inventory.slot.SlotDefinition
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.MenuType
import org.bukkit.inventory.meta.SkullMeta
import java.util.UUID

@Suppress("UnstableApiUsage")
class ExampleNestedMenuMp() : AbstractPagedInventoryMenu() {
    private val views: MutableMap<UUID, InstancedMenuView> = mutableMapOf()

    override fun createUI() {
        setPage(0, InventoryMenuPage(MenuType.GENERIC_9X5, this).apply {
            setTitle(Component.text("Профиль."))
            setSlot(Anchor.CENTER, SlotDefinition.button()
                .setOnRender { viewer ->
                    val item = ItemStack.of(Material.PLAYER_HEAD)
                    val meta = item.itemMeta as SkullMeta
                    meta.displayName(Component.text("NAME"))
                    meta.lore(listOf(Component.text("UUID")))
                    item.itemMeta = meta
                    item
                }
            )

            setSlot(Anchor.BOTTOM_RIGHT, SlotDefinition.icon()
                .setTitle(Component.text("Профиль", NamedTextColor.GOLD))
                .addDescriptionLine(Component.text(":|"))
            )

            setSlot(0 /* = Anchor.TOP_LEFT */, SlotDefinition.toggle()
                .setOnRender { viewer, isToggled ->
                    val item = ItemStack.of(if (isToggled) Material.GRAY_CONCRETE else Material.RED_CONCRETE)
                    item.lore(listOf(
                        Component.text(if (isToggled) "Разблокировать" else "Заблокировать")
                    ))
                    item
                }
                .onToggle { context, bool ->
                    parent.navigateTo(1)
                    /* BlackList.update(list = context.player, target = player, state = bool) */
                }
            )
        })

        setPage(1, InventoryMenuPage(MenuType.GENERIC_9X3, this).apply {
            setTitle(Component.text("Вы уверены?"))
            setSlot(0, SlotDefinition.icon())
        })
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

    override fun open(viewer: HumanEntity): Boolean {
        return this.open(viewer, 0)
    }

    fun open(viewer: HumanEntity, page: Int): Boolean {
        val page = getPage(page) ?: return false
        if (viewer !is Player || !viewer.isOnline) return false
        this.views[viewer.uniqueId] = InstancedInventoryViewImpl(page, this, viewer)
        this.views[viewer.uniqueId]!!.open()
        return true
    }

    override fun isViewing(player: HumanEntity): Boolean {
        val view = this.views[player.uniqueId] ?: return false
        return view.isViewing(player)
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

    override fun asSharedView(viewer: HumanEntity): SharedMenuView? = null
}