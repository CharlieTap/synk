package com.tap.synk.ext

internal fun Map<String, String>.encodedItemCount(): Int? {
    return keys.maxOfOrNull { it.substringBefore('|').toInt() }
}

internal fun Map<String, String>.groupedByEncodedIndex(): Map<Int, Map<String, String>> {
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

fun Map<String, String>.encodeToString(): String {
    return entries.foldIndexed("") { idx, acc, entry ->
        val postFix = if (idx < entries.size - 1) {
            "|"
        } else ""
        acc + entry.key + ":" + entry.value + postFix
    }
}

fun String.decodeToHashmap(): Map<String, String> {
    return HashMap<String, String>().apply {
        split("|").map { pairSerial ->
            val key = pairSerial.substringBefore(":")
            val value = pairSerial.substringAfter(":")
            put(key, value)
        }
    }
}
