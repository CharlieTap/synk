package com.tap.synk.meta.store

internal class InMemoryMetaStore(
    private val store: HashMap<String, String> = HashMap()
) : MetaStore {

    override fun getMeta(id: String): HashMap<String, String>? {
        return store[id]?.let { serial ->
            serial.decodeToHashmap()
        }
    }

    override fun putMeta(id: String, meta: HashMap<String, String>) {
        val serial = meta.encodeToString()
        store[id] = serial
    }
}

internal fun HashMap<String, String>.encodeToString() : String {
    return entries.foldIndexed("") { idx, acc, entry ->
        val postFix = if (idx < entries.size - 1) {
            "|"
        } else ""
        acc + entry.key + ":" + entry.value + postFix
    }
}

internal fun String.decodeToHashmap() : HashMap<String, String> {
    return HashMap<String, String>().apply {
        split("|").map { pairSerial ->
            val key = pairSerial.substringBefore(":")
            val value = pairSerial.substringAfter(":")
            put(key, value)
        }
    }
}
