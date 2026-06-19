package dev.tuffstudios.tema.inventory.impl

import dev.tuffstudios.tema.Anchor
import dev.tuffstudios.tema.TEMA
import dev.tuffstudios.tema.inventory.MenuContainer
import dev.tuffstudios.tema.inventory.MenuContent
import dev.tuffstudios.tema.inventory.MenuLayout
import dev.tuffstudios.tema.inventory.slot.SlotDefinition
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.MenuType
import org.jetbrains.annotations.ApiStatus

@Suppress("UnstableApiUsage")
@ApiStatus.AvailableSince("1.0")
abstract class AbstractInventoryMenu(
    override val type: MenuType
) : MenuContainer, MenuContent, MenuLayout {
    /**
     * Заголовок, отображающийся в левом верхнем углу при открытии инвентаря.
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
     * Список элементов, которые должны отображаться в меню.
     *
     * На данный момент единственными доступными элементами являются кнопки и тогглы.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    override val elements: Int2ObjectLinkedOpenHashMap<SlotDefinition> = Int2ObjectLinkedOpenHashMap()

    /**
     * Номера слотов, закрепленных за определенными [Anchor].
     *
     * Якори являются облегченным способом закрепить элемент наверху, в центре или углу инвентаря.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    private val anchorPositions: Map<Anchor, Int> = resolveAnchorPositions(this.type)

    init {
        createUI()
    }

    /**
     * Функция для классов - наследователей.
     *
     * Именно здесь может происходить заполнение фона, заголовка, создание кнопок и т.д.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    abstract fun createUI()

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
            TEMA.getInstance().slF4JLogger.warn(
                "An item is already attached to slot $slot."
            )
        }

        if (element == null) this.elements.remove(slot)
        else this.elements.put(slot, element)
    }

    /**
     * Возвращает неизменяемую копию списка элементов этого [AbstractInventoryMenu].
     *
     * @author Egor Morozov
     * @since 1.0
     */
    override fun listSlotDefinitions(): List<SlotDefinition> {
        if (this.elements.isEmpty()) return emptyList()
        return this.elements.values.toList()
    }

    override fun resolveAnchorPosition(anchor: Anchor): Int {
        if (this.anchorPositions.isEmpty()) {
            this.resolveAnchorPositions(this.type)
        }

        return this.anchorPositions[anchor]
            ?: throw IllegalStateException("Anchor $anchor is not supported by MenuType $type")
    }
}