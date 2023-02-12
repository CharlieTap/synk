package com.tap.synk.meta.store

/**
 A storage interface for metadata pertaining to a particular CRDT type
 */
interface MetaStore {

    /**
     * Hydrates cache from disk
     * The number of records pulled is equal to the maximum cache suze
     * This method will block
     */
    fun warm()

    /**
     * Retrieve CRDT metadata
     * This method may block is metastore has not been warmed
     */
    fun getMeta(id: String): Map<String, String>?

    /**
     * Persist CRDT metadata
     * This method will block
     */
    fun putMeta(id: String, meta: Map<String, String>)
}
