package com.tap.delight.metastore.cache

interface MemCache<K, V> {

    operator fun get(k: K): V?

    fun put(k: K, v: V)

    fun put(entries: Set<Pair<K, V>>)

    fun size(): Int

    fun maxSize(): Int
}
