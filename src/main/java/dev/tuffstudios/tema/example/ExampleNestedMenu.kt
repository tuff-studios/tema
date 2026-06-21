package dev.tuffstudios.tema.example

import dev.tuffstudios.tema.Anchor
import dev.tuffstudios.tema.inventory.view.InstancedMenuView
import dev.tuffstudios.tema.inventory.view.SharedMenuView
import dev.tuffstudios.tema.inventory.impl.AbstractInventoryMenu
import dev.tuffstudios.tema.inventory.impl.view.InstancedInventoryViewImpl
import dev.tuffstudios.tema.inventory.slot.SlotDefinition
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.MenuType
import org.bukkit.inventory.meta.SkullMeta
import java.util.UUID

@Suppress("UnstableApiUsage")
class ExampleNestedMenu(val target: OfflinePlayer) : AbstractInventoryMenu(MenuType.GENERIC_9X3) {
    private var built: Boolean = false
    private val views: MutableMap<UUID, InstancedMenuView> = mutableMapOf()

    override fun createUI() {
        setTitle(Component.text("Профиль ${target.name}."))
        setSlot(Anchor.CENTER, SlotDefinition.button()
            .setOnRender { viewer ->
                val item = ItemStack.of(Material.PLAYER_HEAD)
                val meta = item.itemMeta as SkullMeta
                meta.playerProfile = target.playerProfile
                meta.displayName(Component.text(target.name ?: "Unknown"))
                meta.lore(listOf(Component.text(target.uniqueId.toString())))
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
                val player = getProfileOwner() ?: return@onToggle
                /* BlackList.update(list = context.player, target = player, state = bool) */
            }
        )
    }

    fun getProfileOwner(): Player? {
        return Bukkit.getPlayer(target.uniqueId)
    }

    override fun open(viewer: HumanEntity): Boolean {
        if (!this.built) this.build(); this.built = true
        //TODO придумать что-нибудь с build(), без этого контент гуи тупо не инициализируется
        if (viewer !is Player || !viewer.isOnline) return false
        if (!this.views.containsKey(viewer.uniqueId)) {
            this.views[viewer.uniqueId] = InstancedInventoryViewImpl(this, this, viewer)
        }

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