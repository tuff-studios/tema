package dev.craftwarestudios.tema.core

import dev.craftwarestudios.tema.container.ContainerViewInstance
import org.bukkit.entity.Player
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

// с этим явно нужно сделать что-нибудь
object ViewSession {
    private val sessions: ConcurrentHashMap<UUID, ContainerViewInstance> = ConcurrentHashMap()

    fun register(player: Player, state: ContainerViewInstance) {
        this.sessions[player.uniqueId] = state
    }

    fun unregister(player: Player, state: ContainerViewInstance? = null) {
        state?.let {
            this.sessions.remove(player.uniqueId, it)
        } ?: {
            this.sessions.remove(player.uniqueId)
        }
    }

    fun find(playerId: UUID): ContainerViewInstance? {
        return this.sessions[playerId]
    }

    fun find(player: Player): ContainerViewInstance? {
        return this.sessions[player.uniqueId]
    }
}
