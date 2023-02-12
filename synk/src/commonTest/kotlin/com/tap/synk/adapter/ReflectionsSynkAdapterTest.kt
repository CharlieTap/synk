package com.tap.synk.adapter

import com.tap.synk.CMap
import com.tap.synk.IDCRDT
import com.tap.synk.cache.ReflectionCacheEntry
import com.tap.synk.cache.ReflectionsCache
import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.assertEquals

class ReflectionsSynkAdapterTest {

    @Test
    fun `can resolve id`() {
        val cache = CMap<KClass<*>, ReflectionCacheEntry<Any>>()
        val reflectionsCache = ReflectionsCache(cache)
        val adapter = ReflectionsSynkAdapter(reflectionsCache)

        val crdt = IDCRDT("123", "", "", 1234)
        val id = adapter.resolveId(crdt)

        assertEquals("123", id)
        assertEquals(1, cache.size())
    }

    @Test
    fun `can encode crdt to hashmap`() {
        val cache = CMap<KClass<*>, ReflectionCacheEntry<Any>>()
        val reflectionsCache = ReflectionsCache(cache)
        val adapter = ReflectionsSynkAdapter(reflectionsCache)

        val crdt = IDCRDT("123", "John", "Smith", 1234)

        val result = adapter.encode(crdt)
        val expectedMap = ReflectionsSynkAdapter.createMap(crdt::class).apply {
            put("id", "123")
            put("name", "John")
            put("last_name", "Smith")
            put("phone", "1234")
        }

        assertEquals(expectedMap, result)
        assertEquals(1, cache.size())
    }

//    @Test
//    fun `can decode crdt from hashmap`() {
//        val cache = CMap<KClass<*>, ReflectionCacheEntry<Any>>()
//        val reflectionsCache = ReflectionsCache(cache)
//        val adapter = ReflectionsSynkAdapter(reflectionsCache)
//
//        val input = ReflectionsSynkAdapter.createMap(IDCRDT::class).apply {
//            put("name", "John")
//            put("last_name", "Smith")
//            put("phone", "1234")
//        }
//
//        val result = adapter.decode(input)
//        val expected = IDCRDT("123", "John", "Smith", 1234)
//
//        assertEquals(expected, result)
//        assertEquals(1, cache.size())
//    }
}
