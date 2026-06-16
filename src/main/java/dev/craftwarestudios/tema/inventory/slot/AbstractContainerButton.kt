package dev.craftwarestudios.tema.inventory.slot

import org.bukkit.inventory.ItemStack

interface AbstractContainerButton {
    val slot: Int

    fun render(): ItemStack
}