package com.tap.synk.encode

internal fun Map<String, String>.encodedItemCount() : Int? {
    return keys.maxOfOrNull { it.substringBefore('|').toInt() }
}

internal fun Map<String, String>.groupedByEncodedIndex() : Map<Int, Map<String, String>> {
    return asIterable().fold(mutableMapOf<Int, MutableMap<String, String>>()) { acc, entry ->
        val idx = entry.key.substringBefore('|').toInt()
        val key = entry.key.substringAfterLast('|')
        acc.apply {
            val subMap = getOrDefault(idx, mutableMapOf())
            subMap[key] = entry.value
            put(idx, subMap)
        }
    }
}