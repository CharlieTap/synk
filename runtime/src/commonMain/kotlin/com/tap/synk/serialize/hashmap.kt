package com.tap.synk.serialize

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
