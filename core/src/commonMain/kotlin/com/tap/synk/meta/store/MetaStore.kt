package com.tap.synk.meta.store

/**
 * 1 Meta store per CRDT ??
 */
interface MetaStore {

    // Map<property-name, hlc`>
    fun getMeta(id: String): HashMap<String, String>?

    fun putMeta(id: String, meta: HashMap<String, String>)
}
