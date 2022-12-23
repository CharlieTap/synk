package com.tap.synk

import kotlinx.coroutines.runBlocking

class CMap<K, V>(
    private val map: MutableMap<K, V> = HashMap()
) {
    private val rwLock : ReadWriteMutex = ReadWriteMutex()

    operator fun get(k: K): V? {
        return runBlocking {
            rwLock.read.lock()
            val value = map[k]
            rwLock.read.unlock()
            value
        }
    }

    fun get(keys: Set<K>) : Set<Pair<K, V?>> {
        return runBlocking {
            rwLock.read.lock()
            val pairs = keys.map { k ->
                k to map[k]
            }
            rwLock.read.unlock()
            pairs.toSet()
        }
    }

    fun put(k: K, v: V) {
        runBlocking {
            rwLock.write.lock()
            map[k] = v
            rwLock.write.unlock()
        }
    }

    fun put(entries: Set<Pair<K, V>>) {
        runBlocking {
            rwLock.write.lock()
            entries.forEach { pair ->
                map[pair.first] = pair.second
            }
            rwLock.write.unlock()
        }
    }

    fun size(): Int {
        return map.size
    }

}