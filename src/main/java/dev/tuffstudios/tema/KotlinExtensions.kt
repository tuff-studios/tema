package dev.tuffstudios.tema

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap

fun <V> intToObjectMap(): Int2ObjectLinkedOpenHashMap<V> = Int2ObjectLinkedOpenHashMap()

fun <K, V> objectToObjectMap(): Object2ObjectLinkedOpenHashMap<K, V> = Object2ObjectLinkedOpenHashMap()