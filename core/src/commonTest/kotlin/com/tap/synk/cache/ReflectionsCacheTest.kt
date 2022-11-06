package com.tap.synk.cache

import com.tap.synk.CRDT
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.primaryConstructor
import kotlin.test.Test
import kotlin.test.assertEquals

class ReflectionsCacheTest {

    @Test
    fun `cache returns class constructor and cache is populated after call`() {
        val cache = HashMap<KClass<*>, ReflectionCacheEntry<Any>>()
        val reflectionsCache = ReflectionsCache(cache)

        val crdt = CRDT("", "", 1)

        val constructor = reflectionsCache.getConstructor(crdt::class)

        assertEquals(crdt::class.primaryConstructor!!, constructor)
        assertEquals(1, cache.entries.size)
    }

    @Test
    fun `cache returns properties and cache is populated after call`() {
        val cache = HashMap<KClass<*>, ReflectionCacheEntry<Any>>()
        val reflectionsCache = ReflectionsCache(cache)

        val crdt = CRDT("", "", 1)

        val props = reflectionsCache.getProps(crdt::class)

        assertEquals(3, props.size)
        assertEquals(listOf("name", "last_name", "phone"), props.map(KProperty1<out Any, *>::name))
        assertEquals(1, cache.entries.size)
    }

    @Test
    fun `cache returns params properties combo and cache is populated after call`() {
        val cache = HashMap<KClass<*>, ReflectionCacheEntry<Any>>()
        val reflectionsCache = ReflectionsCache(cache)

        val crdt = CRDT("", "", 1)

        val props = reflectionsCache.getParamsAndProps(crdt::class)

        assertEquals(3, props.size)
        assertEquals(1, cache.entries.size)
    }

}