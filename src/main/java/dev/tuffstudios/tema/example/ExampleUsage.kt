package dev.tuffstudios.tema.example

import dev.tuffstudios.tema.Anchor
import dev.tuffstudios.tema.inventory.impl.InventoryMenu
import dev.tuffstudios.tema.inventory.slot.SlotDefinition
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.inventory.MenuType

class ExampleUsage {
    val someMenu: InventoryMenu = createGui()

    fun createGui(): InventoryMenu {
        return InventoryMenu(MenuType.GENERIC_9X5) {
            setTitle(Component.text("Какое-то меню"))
            setSlot(Anchor.CENTER, SlotDefinition.button()
                .setTitle(Component.text("Click me!"))
                .onClick { ctx ->
                    ctx.player.sendMessage(Component.text("+1"))
                }
            )
        }
    }

    fun openFor(player: Player) {
        this.someMenu.open(player)
    }
}