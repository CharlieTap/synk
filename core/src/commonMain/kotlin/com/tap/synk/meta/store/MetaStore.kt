package com.tap.synk.meta.store

import com.benasher44.uuid.Uuid

/**
 * 1 Meta store per CRDT ??
 */
interface MetaStore {

    // Map<property-name, hlc`>
    fun getMeta(id: Uuid) : Map<String, String>?

    fun putMeta(id: Uuid, meta: Map<String, String>)

}