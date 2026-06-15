package dev.craftwarestudios.tema

import net.kyori.adventure.text.Component
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

internal fun ItemStack.withMeta(block: ItemMeta.() -> Unit): ItemStack = apply {
    itemMeta = itemMeta?.apply(block)
}

internal fun ItemStack.withNameAndLore(
    name: Component?,
    lore: List<Component>
): ItemStack = withMeta {
    if (name != null) {
        displayName(name)
    }
    if (lore.isNotEmpty()) {
        lore(lore)
    }
}
