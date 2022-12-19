package com.tap.synk.meta.store

import com.tap.synk.encode.decodeToHashmap
import com.tap.synk.encode.encodeToString

internal class InMemoryMetaStore(
    private val store: HashMap<String, String> = HashMap()
) : MetaStore {
    override fun warm() {
        // No op
    }

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
