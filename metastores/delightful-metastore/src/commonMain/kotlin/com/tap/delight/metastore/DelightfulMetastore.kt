package com.tap.delight.metastore

import com.tap.delight.metastore.cache.MemCache
import com.tap.delight.metastore.hash.Hasher
import com.tap.synk.ext.decodeToHashmap
import com.tap.synk.ext.encodeToString
import com.tap.synk.meta.store.MetaStore

class DelightfulMetastore internal constructor(
    private val database: DelightfulDatabase,
    private val namespace: String,
    private val hasher: Hasher,
    private val cache: MemCache<String, String>,
) : MetaStore {

    private fun deriveCacheKey(id: String, namespace: String): String {
        return hasher.hash("$namespace:$id")
    }

    private fun getMetaFromDatabase(id: String, namespace: String): Map<String, String>? {
        return database.multistoreQueries.getById(id, namespace).executeAsOneOrNull()?.data_?.decodeToHashmap()
    }

    private fun getMetaFromCache(id: String, namespace: String): Map<String, String>? {
        val key = deriveCacheKey(id, namespace)
        return cache[key]?.decodeToHashmap()
    }

    private fun putMetaInDatabase(id: String, namespace: String, data: String) {
        return database.multistoreQueries.upsert(id, namespace, data)
    }

    private fun putMetaInCache(id: String, namespace: String, data: String) {
        val key = deriveCacheKey(id, namespace)
        cache.put(key, data)
    }

    override fun warm() {
        val results = database.multistoreQueries.allForNamespace(namespace, cache.maxSize().toLong()).executeAsList()
        val entries = results.map { multistore ->
            deriveCacheKey(multistore.id, multistore.namespace) to multistore.data_
        }.toSet()
        cache.put(entries)
    }

    override fun getMeta(id: String): Map<String, String>? {
        return getMetaFromCache(id, namespace) ?: getMetaFromDatabase(id, namespace)
    }

    override fun putMeta(id: String, meta: Map<String, String>) {
        val data = meta.encodeToString()

        putMetaInDatabase(id, namespace, data)
        putMetaInCache(id, namespace, data)
    }
}
