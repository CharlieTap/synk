package com.tap.synk.resolver

import com.tap.synk.CMap
import com.tap.synk.IDCRDT
import com.tap.synk.cache.ReflectionCacheEntry
import com.tap.synk.cache.ReflectionsCache
import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.assertEquals

class ReflectionsIDResolverTest {

    @Test
    fun `id is resolved and cache is populated after call`() {
        val cache = CMap<KClass<*>, ReflectionCacheEntry<Any>>()
        val reflectionsCache = ReflectionsCache(cache)

        val reflectionsIDResolver = ReflectionsIDResolver(reflectionsCache)

        val crdt = IDCRDT("5", "", "", 1)

        val id = reflectionsIDResolver.resolveId(crdt)

        assertEquals("5", id)
        assertEquals(1, cache.size())
    }

//    @Test
//    fun `crdt is populated and cache is populated after call`() {
//        val cache = HashMap<KClass<*>, ReflectionCacheEntry<Any>>()
//        val reflectionsCache = ReflectionsCache(cache)
//
//        val reflectionsIDResolver = ReflectionsIDResolver(reflectionsCache)
//
//
//        val crdt = IDCRDT(5, "", "", 1)
//
//        val id = reflectionsIDResolver.resolveID(crdt)
//
//        assertEquals("5", id)
//        assertEquals(1, cache.entries.size)
//    }
}
