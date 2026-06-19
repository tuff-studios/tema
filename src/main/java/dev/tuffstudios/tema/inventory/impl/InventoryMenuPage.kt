package dev.tuffstudios.tema.inventory.impl

import dev.tuffstudios.tema.Anchor
import dev.tuffstudios.tema.TEMA
import dev.tuffstudios.tema.inventory.MenuContent
import dev.tuffstudios.tema.inventory.MenuLayout
import dev.tuffstudios.tema.inventory.slot.SlotDefinition
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.MenuType
import org.jetbrains.annotations.ApiStatus

/**
 * Стандартная реализация [dev.tuffstudios.tema.inventory.MenuContent].
 *
 * Этот класс уже имеет в себе реализацию [dev.tuffstudios.tema.inventory.MenuLayout], что позволяет
 * использовать якори [dev.tuffstudios.tema.Anchor] и закреплять слоты в нужном положении с помощью них,
 * в том числе.
 *
 * @author Egor Morozov
 * @since 1.0
 */
@Suppress("UnstableApiUsage")
@ApiStatus.AvailableSince("1.0")
class InventoryMenuPage(
    /**
     * Размер страницы в виде [org.bukkit.inventory.MenuType].
     *
     * На данный момент поддерживается только список [supportedMenuTypes].
     *
     * @author Egor Morozov
     * @since 1.0
     */
    override val type: MenuType,

    /**
     * Контейнер, владеющий этой страницей.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    val parent: AbstractPagedInventoryMenu
) : MenuContent, MenuLayout, Iterable<MenuContent> {
    /**
     * Заголовок страницы отображающийся в левом верхнем углу
     * при открытии инвентаря.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    override var title: Component = Component.empty()

    /**
     * Наполнитель, которым должен быть заполнен задний фон
     * инвентаря, там, где не установлены никакие другие элементы.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    override var background: ItemStack = ItemStack.of(Material.AIR)

    /**
     * Список элементов, которые должна отобразить страница.
     *
     * На данный момент единственными доступными элементами являются кнопки и тогглы.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    override val elements: Int2ObjectLinkedOpenHashMap<SlotDefinition> = Int2ObjectLinkedOpenHashMap()

    /**
     * Номера слотов, закрепленных за определенными [dev.tuffstudios.tema.Anchor].
     *
     * Якори являются облегченным способом закрепить элемент наверху, в центре или углу инвентаря.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    private val anchorPositions: Map<Anchor, Int> = resolveAnchorPositions(this.type)

    /**
     * Устанавливает заголовок страницы.
     *
     * @param title заголовок, отображающийся в левом верхнем углу инвентаря.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    override fun setTitle(title: Component) = apply {
        this.title = title
    }

    /**
     * Устанавливает [Material]-наполнитель, которым должен быть заполнен задний фон
     * инвентаря, там, где не установлены никакие другие элементы.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    fun setBackground(material: Material) = apply {
        this.background = ItemStack.of(material)
    }

    /**
     * Устанавливает [ItemStack]-наполнитель, которым должен быть заполнен задний фон
     * инвентаря, там, где не установлены никакие другие элементы.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    override fun setBackground(item: ItemStack) = apply {
        this.background = item
    }

    /**
     * Устанавливает [element] в слот, определяемый якорем [anchor].
     *
     * Использование метода с неподдерживаемым типом [Anchor] вызовет ошибку.
     *
     * Эта реализация позволяет совершать жесткую перезапись слотов налету, однако
     * предупреждение всё равно появится в консоли, на всякий случай.
     *
     * @throws IllegalArgumentException если [anchor] не поддерживается типом [MenuType] этой страницы.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    fun setSlot(anchor: Anchor, element: SlotDefinition?) = apply {
        return setSlot(resolveAnchorPosition(anchor), element)
    }

    /**
     * Устанавливает [element] в слот [slot].
     *
     * Эта реализация позволяет совершать жесткую перезапись слотов налету,
     * однако предупреждение всё равно появится в консоли, на всякий случай.
     *
     * Если [element] = `null`, слот под номером [slot] будет считаться незаполненным.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    override fun setSlot(slot: Int, element: SlotDefinition?) = apply {
        if (this.elements.containsKey(slot)) {
            TEMA.Companion.getInstance().slF4JLogger.warn(
                "An item is already attached to slot $slot."
            )
        }

        if (element == null) this.elements.remove(slot)
        else this.elements.put(slot, element)
    }

    /**
     * Возвращает неизменяемую копию списка элементов этой страницы.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    override fun listSlotDefinitions(): List<SlotDefinition> {
        if (this.elements.isEmpty()) return emptyList()
        return this.elements.values.toList()
    }

    /**
     * Возвращает итератор для страниц [parent]'а этой страницы.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    override fun iterator(): Iterator<MenuContent> {
        return this.parent.pageIterator()
    }

    override fun resolveAnchorPosition(anchor: Anchor): Int {
        if (this.anchorPositions.isEmpty()) {
            this.resolveAnchorPositions(this.type)
        }

        return this.anchorPositions[anchor]
            ?: throw IllegalStateException("Anchor $anchor is not supported by MenuType $type")
    }

    /**
     * Рисует весь контент этой страницы данному [viewer].
     *
     * После того как контент будет отображён, метод вернёт полученный [org.bukkit.inventory.InventoryView].
     *
     * @author Egor Morozov
     * @since 1.0
     */
    internal fun renderToView(viewer: Player): InventoryView {
        val view = this.type.create(viewer, this.title)
        val inventory = view.topInventory
        inventory.clear()

        repeat(inventory.size) {
            inventory.setItem(it, this.background)
        }

        this.elements.toList().forEach { (slot, item) ->
            if (slot !in inventory.contents.indices) return@forEach
            inventory.setItem(slot, item.renderToItem(viewer))
        }

        return view
    }
}