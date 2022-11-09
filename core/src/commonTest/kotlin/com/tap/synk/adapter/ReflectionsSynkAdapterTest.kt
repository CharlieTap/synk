package com.tap.synk.adapter

import com.tap.synk.IDCRDT
import com.tap.synk.cache.ReflectionCacheEntry
import com.tap.synk.cache.ReflectionsCache
import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.assertEquals

class ReflectionsSynkAdapterTest {

    @Test
    fun `can resolve id`() {
        val cache = HashMap<KClass<*>, ReflectionCacheEntry<Any>>()
        val reflectionsCache = ReflectionsCache(cache)
        val adapter = ReflectionsSynkAdapter(reflectionsCache)

        val crdt = IDCRDT("123", "", "", 1234)
        val id = adapter.resolveId(crdt)

        assertEquals("123", id)
        assertEquals(1, cache.size)
    }

    @Test
    fun `can encode crdt to hashmap`() {
        val cache = HashMap<KClass<*>, ReflectionCacheEntry<Any>>()
        val reflectionsCache = ReflectionsCache(cache)
        val adapter = ReflectionsSynkAdapter(reflectionsCache)

        val crdt = IDCRDT("123", "John", "Smith", 1234)

        val map = adapter.encode(crdt)
        val expectedMap = HashMap<String, String>().apply {
            put("name", "John")
            put("last_name", "Smith")
            put("phone", "1234")
        }

        assertEquals(map, expectedMap)
        assertEquals(1, cache.size)
    }

    @Test
    fun `can encode crdt to hashmap and ignore keys`() {
        val cache = HashMap<KClass<*>, ReflectionCacheEntry<Any>>()
        val reflectionsCache = ReflectionsCache(cache)
        val adapter = ReflectionsSynkAdapter(reflectionsCache, ignoredKeys = setOf("id", "name"))

        val crdt = IDCRDT("123", "John", "Smith", 1234)

        val map = adapter.encode(crdt)
        val expectedMap = HashMap<String, String>().apply {
            put("last_name", "Smith")
            put("phone", "1234")
        }

        assertEquals(map, expectedMap)
        assertEquals(1, cache.size)
    }
}
