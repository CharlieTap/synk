package com.tap.delight.metastore.cache

import androidx.collection.LruCache

internal class DelightfulMemCache(
    private val cacheSize: Int,
    private val cache: LruCache<String, String> = LruCache(cacheSize)
): MemCache<String, String> {
    override fun get(k: String): String? {
        return cache[k]
    }

    override fun put(k: String, v: String) {
        cache.put(k, v)
    }

    override fun size(): Int {
       return cache.size()
    }

    override fun maxSize(): Int {
       return cache.maxSize()
    }
}