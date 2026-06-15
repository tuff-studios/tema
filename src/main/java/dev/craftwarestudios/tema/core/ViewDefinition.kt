package dev.craftwarestudios.tema.core

import dev.craftwarestudios.tema.container.ContainerViewBuilder
import dev.craftwarestudios.tema.container.PageDefinition
import org.bukkit.entity.Player

interface ViewDefinition {
    fun pageDefinition(index: Int = 0): PageDefinition
    fun setPage(index: Int, page: PageDefinition)

    fun open(player: Player, page: Int = 0): ViewInstance
    fun openInstanced(player: Player, page: Int = 0): ViewInstance

    fun getView(player: Player): ViewInstance?
    fun close(player: Player): Boolean

    companion object {
        fun container(): ContainerViewBuilder = ContainerViewBuilder()
    }
}
