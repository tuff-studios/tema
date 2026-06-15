package dev.craftwarestudios.tema.container

import net.kyori.adventure.text.Component
import org.bukkit.inventory.MenuType

// кнопочки нужны и заливки цвет
class ContainerViewBuilder {
    fun build(title: Component, menuType: MenuType): ContainerView {
        return ContainerView(title = title, menuType = menuType)
    }
}
