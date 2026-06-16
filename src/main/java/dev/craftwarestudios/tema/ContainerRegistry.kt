package dev.craftwarestudios.tema

import dev.craftwarestudios.tema.inventory.InventoryContainerView
import org.bukkit.entity.Player
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object ContainerRegistry {
    val slf4jLogger: Logger = LoggerFactory.getLogger("TEMA")
    private val sessions: ConcurrentHashMap<UUID, InventoryContainerView> = ConcurrentHashMap()

    fun register(player: Player, state: InventoryContainerView) {
        this.sessions[player.uniqueId] = state
    }

    fun unregister(player: Player, state: InventoryContainerView? = null) {
        state?.let {
            this.sessions.remove(player.uniqueId, it)
        } ?: {
            this.sessions.remove(player.uniqueId)
        }
    }

    fun find(playerId: UUID): InventoryContainerView? {
        return this.sessions[playerId]
    }

    fun find(player: Player): InventoryContainerView? {
        return this.sessions[player.uniqueId]
    }
}