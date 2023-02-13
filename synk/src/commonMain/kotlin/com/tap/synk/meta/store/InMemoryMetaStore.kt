package com.tap.synk.meta.store

import com.tap.synk.CMap
import com.tap.synk.serialize.decodeToHashmap
import com.tap.synk.serialize.encodeToString

internal class InMemoryMetaStore(
    private val store: CMap<String, String> = CMap()
) : MetaStore {
    override fun warm() {
        // No op
    }

    override fun getMeta(id: String): Map<String, String>? {
        return store[id]?.let { serial ->
            serial.decodeToHashmap()
        }
    }

    override fun putMeta(id: String, meta: Map<String, String>) {
        val serial = meta.encodeToString()
        store.put(id, serial)
    }
}
