package com.tap.synk.adapter


internal fun <T : Any> SynkAdapter<T>.diff(old: T, new: T): Set<String> {
    val encodedOld = encode(old)
    val encodedNew = encode(new)

    return encodedNew.entries.fold(mutableSetOf()) { acc, newEntry ->

        val oldEntry = encodedOld.entries.firstOrNull {
            newEntry.key == it.key
        } ?: return@fold acc.apply { add(newEntry.key) }

        if (newEntry.value != oldEntry.value) {
            acc.apply { add(newEntry.key) }
        } else acc
    }
}