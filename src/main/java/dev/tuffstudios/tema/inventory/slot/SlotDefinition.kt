package dev.tuffstudios.tema.inventory.slot

import dev.tuffstudios.tema.inventory.MenuContainer
import dev.tuffstudios.tema.inventory.impl.slot.InventoryButton
import dev.tuffstudios.tema.inventory.impl.slot.InventoryIcon
import dev.tuffstudios.tema.inventory.impl.slot.InventoryToggle
import org.bukkit.entity.HumanEntity
import org.bukkit.inventory.ItemStack
import org.jetbrains.annotations.ApiStatus

/**
 * Представляет собой отображаемый слот [MenuContainer].
 *
 * @author Egor Morozov
 * @since 1.0
 */
@ApiStatus.AvailableSince("1.0")
interface SlotDefinition {
    /**
     * Применяет данное [action] к [ItemStack],
     * который возвращается методом [renderToItem].
     *
     * @author Egor Morozov
     * @since 1.0
     */
    fun editItem(action: (ItemStack) -> Unit): SlotDefinition

    /**
     * Отрисовывает этот [SlotDefinition] в [ItemStack].
     *
     * @author Egor Morozov
     * @since 1.0
     */
    fun renderToItem(viewer: HumanEntity): ItemStack

    companion object {
        fun icon() = InventoryIcon()
        fun button() = InventoryButton()
        fun toggle() = InventoryToggle()
    }
}