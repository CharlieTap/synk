package com.tap.synk.meta.store

import com.benasher44.uuid.Uuid

/**
 * 1 Meta store per CRDT ??
 */
interface MetaStore {

    // Map<property-name, hlc`>
    fun getMeta(id: Uuid): HashMap<String, String>?

    fun putMeta(id: Uuid, meta: HashMap<String, String>)
}
