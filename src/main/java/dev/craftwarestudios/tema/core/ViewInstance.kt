package dev.craftwarestudios.tema.core

import org.bukkit.entity.Player

interface ViewInstance {
    fun close(player: Player, closeInternally: Boolean): Boolean
    fun refresh(player: Player): Boolean
    fun refreshAll(): Boolean
}
