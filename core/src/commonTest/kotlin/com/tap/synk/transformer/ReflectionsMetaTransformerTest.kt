package com.tap.synk.transformer

import com.tap.hlc.HybridLogicalClock
import com.tap.synk.cache.ReflectionCacheEntry
import com.tap.synk.cache.ReflectionsCache
import com.tap.synk.meta.transformer.ReflectionsMetaTransformer
import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.assertEquals

class ReflectionsMetaTransformerTest {

    @Test
    fun `can turn object into meta`() {
        val hlc = HybridLogicalClock()
        val transformer = ReflectionsMetaTransformer()

        val crdt = CRDT("", "", "")
        val meta = transformer.toMeta(crdt, hlc)

        val expected = HashMap<String, String>().apply {
            val hlcs = hlc.toString()
            put("name", hlcs)
            put("secondName", hlcs)
            put("thirdName", hlcs)
        }

        assertEquals("com.tap.synk.transformer.CRDT", meta.clazz)
        assertEquals(expected, meta.timestampMeta)
    }

    @Test
    fun `meta cache is populated after first run`() {
        val hlc = HybridLogicalClock()

        val cache = HashMap<KClass<*>, ReflectionCacheEntry<Any>>()
        val reflectionsCache = ReflectionsCache(cache)

        val transformer = ReflectionsMetaTransformer(reflectionsCache)

        val crdt = CRDT("", "", "")
        val meta = transformer.toMeta(crdt, hlc)

        assertEquals(1, cache.size)
    }

    @Test
    fun `meta transformer ignores given keys`() {
        val hlc = HybridLogicalClock()
        val transformer = ReflectionsMetaTransformer(ignoredKeys = setOf("name"))

        val crdt = CRDT("", "", "")
        val meta = transformer.toMeta(crdt, hlc)

        val expected = HashMap<String, String>().apply {
            val hlcs = hlc.toString()
            put("secondName", hlcs)
            put("thirdName", hlcs)
        }

        assertEquals("com.tap.synk.transformer.CRDT", meta.clazz)
        assertEquals(expected, meta.timestampMeta)
    }
}

private data class CRDT(
    private val name: String,
    private val secondName: String,
    private val thirdName: String
)
