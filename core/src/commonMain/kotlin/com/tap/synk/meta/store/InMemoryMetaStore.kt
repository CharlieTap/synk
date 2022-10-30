package com.tap.synk.meta.store

import com.benasher44.uuid.Uuid

internal class InMemoryMetaStore(
    private val store: HashMap<String, String>
) : MetaStore {

    override fun getMeta(id: Uuid): Map<String, String>? {
        return store[id.toString()]?.let { serial ->
            mutableMapOf<String, String>().apply {
                serial.split("|").map { pairSerial ->
                    val key = pairSerial.substringBefore(":")
                    val value = pairSerial.substringAfter(":")
                    put(key, value)
                }
            }
        }
    }

    override fun putMeta(id: Uuid, meta: Map<String, String>) {
        val serial = meta.entries.foldIndexed("") { idx, acc, entry ->
            val postFix = if (idx < meta.entries.size - 1) {
                "|"
            } else ""
            acc + entry.key + ":" + entry.value + postFix
        }
        store[id.toString()] = serial
    }
}
