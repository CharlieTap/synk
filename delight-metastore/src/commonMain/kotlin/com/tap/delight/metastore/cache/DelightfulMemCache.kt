package com.tap.delight.metastore.cache

import androidx.collection.LruCache
import com.tap.synk.CMap

internal class DelightfulMemCache(
    private val cacheSize: Int,
    private val cache: CMap<String, String> = CMap(lruToMap(LruCache(cacheSize)))
) : MemCache<String, String> {

    companion object {
        fun <K : Any, V : Any> lruToMap(lruCache: LruCache<K, V>): MutableMap<K, V> {
            return object : MutableMap<K, V> {
                override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
                    get() = TODO("Not yet implemented")
                override val keys: MutableSet<K>
                    get() = TODO("Not yet implemented")
                override val size: Int
                    get() = lruCache.size()
                override val values: MutableCollection<V>
                    get() = TODO("Not yet implemented")

                override fun clear() {
                    lruCache.evictAll()
                }

                override fun isEmpty(): Boolean {
                    return lruCache.size() == 0
                }

                override fun remove(key: K): V? {
                    return lruCache.remove(key)
                }

                override fun putAll(from: Map<out K, V>) {
                    TODO("Not yet implemented")
                }

                override fun containsValue(value: V): Boolean {
                    TODO("Not yet implemented")
                }

                override fun containsKey(key: K): Boolean {
                    return lruCache[key] != null
                }

                override fun put(key: K, value: V): V? {
                    return lruCache.put(key, value)
                }

                override fun get(key: K): V? {
                    return lruCache[key]
                }
            }
        }
    }
    override fun get(k: String): String? {
        return cache[k]
    }

    override fun put(k: String, v: String) {
        cache.put(k, v)
    }

    override fun put(entries: Set<Pair<String, String>>) {
        cache.put(entries)
    }

    override fun size(): Int {
        return cache.size()
    }

    override fun maxSize(): Int {
        return cacheSize
    }
}
