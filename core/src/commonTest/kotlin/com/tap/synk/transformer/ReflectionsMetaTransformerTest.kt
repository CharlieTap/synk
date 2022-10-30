package com.tap.synk.transformer

import com.tap.hlc.HybridLogicalClock
import com.tap.synk.meta.transformer.ReflectionsMetaCache
import com.tap.synk.meta.transformer.ReflectionsMetaTransformer
import kotlin.test.Test
import kotlin.test.assertEquals

class ReflectionsMetaTransformerTest {

    @Test
    fun `can turn object into meta`() {
        val hlc = HybridLogicalClock()
        val factory = {
            hlc
        }

        val transformer = ReflectionsMetaTransformer(factory)

        val crdt = CRDT("", "", "")
        val meta = transformer.toMeta(crdt)

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
        val factory = {
            hlc
        }
        val cache = ReflectionsMetaCache()

        val transformer = ReflectionsMetaTransformer(factory, cache)

        val crdt = CRDT("", "", "")
        val meta = transformer.toMeta(crdt)

        val expected = CRDT::class.qualifiedName!! to setOf("name", "secondName", "thirdName")

        assertEquals(1, cache.size)
        assertEquals(expected, cache[CRDT::class])
    }

    @Test
    fun `meta cache is used if available`() {
        val hlc = HybridLogicalClock()
        val factory = {
            hlc
        }
        val cache = ReflectionsMetaCache().apply {
            put(CRDT::class, "testclass" to setOf("fakename", "fakename2", "fakename3"))
        }

        val transformer = ReflectionsMetaTransformer(factory, cache)

        val crdt = CRDT("", "", "")
        val meta = transformer.toMeta(crdt)

        val expected = HashMap<String, String>().apply {
            val hlcs = hlc.toString()
            put("fakename", hlcs)
            put("fakename2", hlcs)
            put("fakename3", hlcs)
        }

        assertEquals("testclass", meta.clazz)
        assertEquals(expected, meta.timestampMeta)
    }


    @Test
    fun `meta transformer ignores given keys`() {
        val hlc = HybridLogicalClock()
        val factory = {
            hlc
        }

        val transformer = ReflectionsMetaTransformer(factory, ignoredKeys = setOf("name"))

        val crdt = CRDT("", "", "")
        val meta = transformer.toMeta(crdt)

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
