package dev.tuffstudios.tema.inventory

import dev.tuffstudios.tema.Anchor
import org.bukkit.inventory.MenuType
import org.jetbrains.annotations.ApiStatus

/**
 * Класс с дополнительными методами для работы с лейаутом контейнеров.
 *
 * Он может быть использован как самими [MenuContainer], так и страницами или отдельными [InventoryMenuView].
 *
 * @author Egor Morozov
 * @since 1.0
 */
@Suppress("UnstableApiUsage")
@ApiStatus.AvailableSince("1.0")
interface MenuLayout {
    /**
     * Определяет размер инвентаря по заданному [MenuType].
     *
     * @author Egor Morozov
     * @since 1.0
     */
    fun resolveSize(menu: MenuType): Pair<Int, Int> = when (menu) {
        MenuType.GENERIC_9X1 -> 1 to 9
        MenuType.GENERIC_3X3 -> 3 to 3
        MenuType.GENERIC_9X2 -> 2 to 9
        MenuType.GENERIC_9X3 -> 3 to 9
        MenuType.GENERIC_9X4 -> 4 to 9
        MenuType.GENERIC_9X5 -> 5 to 9
        MenuType.GENERIC_9X6 -> 6 to 9
        else -> throw IllegalArgumentException("MenuType ${menu.key().asString()} is not supported.")
    }

    /**
     * Определяет номер слота, на котором расположен данный [anchor].
     *
     * Для [Anchor.LEFT], [Anchor.RIGHT], и других, расположенных на левой и правой стороне,
     * результат может быть не совсем точным, поскольку высота в [MenuType] не всегда является
     * нечётной.
     *
     * Для [MenuType.GENERIC_9X1], [MenuType.GENERIC_9X2] невозможно обработать якори [Anchor.TOP_LEFT], [Anchor.BOTTOM_LEFT],
     * [Anchor.TOP_RIGHT], [Anchor.BOTTOM_RIGHT], [Anchor.LEFT], [Anchor.RIGHT] "полноценно" - так как высота
     * составляет всего лишь один слот.
     *
     * @author Egor Morozov
     * @since 1.0
     */
    fun resolveAnchorPosition(anchor: Anchor): Int

    /**
     * Определяет номера слотов соответствующих якорям [Anchor].
     *
     * Для [Anchor.LEFT], [Anchor.RIGHT], и других, расположенных на левой и правой стороне,
     * результат может быть не совсем точным, поскольку высота в [MenuType] не всегда является
     * нечётной.
     *
     * Для [MenuType.GENERIC_9X1], [MenuType.GENERIC_9X2] невозможно обработать якори [Anchor.TOP_LEFT], [Anchor.BOTTOM_LEFT],
     * [Anchor.TOP_RIGHT], [Anchor.BOTTOM_RIGHT], [Anchor.LEFT], [Anchor.RIGHT] "полноценно" - так как высота
     * составляет всего лишь один слот.
     *
     * @return [Map] с соответствующими друг другу [Anchor] и номеру слота [Int]
     *
     * @author Egor Morozov
     * @since 1.0
     */
    fun resolveAnchorPositions(type: MenuType): Map<Anchor, Int> {
        val (rows, columns) = this.resolveSize(type)
        val result = mutableMapOf<Anchor, Int>()

        when {
            rows == 1 -> {
                result[Anchor.LEFT] = 0
                result[Anchor.CENTER] = columns / 2
                result[Anchor.RIGHT] = columns - 1
            }
            rows % 2 == 0 -> {
                result[Anchor.TOP_LEFT] = 0
                result[Anchor.TOP] = columns / 2
                result[Anchor.TOP_RIGHT] = columns - 1
                result[Anchor.BOTTOM_LEFT] = (rows - 1) * columns
                result[Anchor.BOTTOM] = (rows - 1) * columns + columns / 2
                result[Anchor.BOTTOM_RIGHT] = rows * columns - 1
            }
            else -> {
                val middleRow = rows / 2
                val lastRow = rows - 1

                result[Anchor.TOP_LEFT] = 0
                result[Anchor.TOP] = columns / 2
                result[Anchor.TOP_RIGHT] = columns - 1
                result[Anchor.LEFT] = middleRow * columns
                result[Anchor.CENTER] = middleRow * columns + columns / 2
                result[Anchor.RIGHT] = middleRow * columns + (columns - 1)
                result[Anchor.BOTTOM_LEFT] = lastRow * columns
                result[Anchor.BOTTOM] = lastRow * columns + columns / 2
                result[Anchor.BOTTOM_RIGHT] = rows * columns - 1
            }
        }

        return result
    }

    /**
     * Поддерживаемые типы [MenuType].
     *
     * @author Egor Morozov
     * @since 1.0
     */
    val supportedMenuTypes: List<MenuType>
        get() = listOf(
            MenuType.GENERIC_9X1,
            MenuType.GENERIC_9X2,
            MenuType.GENERIC_9X3,
            MenuType.GENERIC_9X4,
            MenuType.GENERIC_9X5,
            MenuType.GENERIC_9X6,
            MenuType.GENERIC_3X3
        )
}