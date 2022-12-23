package com.tap.synk.meta.transform

import com.tap.hlc.HybridLogicalClock
import com.tap.synk.CMap
import com.tap.synk.IDCRDT
import com.tap.synk.adapter.ReflectionsSynkAdapter
import com.tap.synk.cache.ReflectionCacheEntry
import com.tap.synk.cache.ReflectionsCache
import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.assertEquals

class TransformTest {

    @Test
    fun `can turn object into meta`() {
        val cache = CMap<KClass<*>, ReflectionCacheEntry<Any>>()
        val reflectionsCache = ReflectionsCache(cache)
        val synkAdapter = ReflectionsSynkAdapter(reflectionsCache)
        val hlc = HybridLogicalClock()

        val crdt = IDCRDT(
            "123",
            "test",
            "test",
            1234
        )

        val result = synkAdapter.transformToMeta(crdt, hlc)

        val expected = HashMap<String, String>().apply {
            val hlcs = hlc.toString()
            put("name", hlcs)
            put("last_name", hlcs)
            put("phone", hlcs)
        }

        assertEquals("com.tap.synk.IDCRDT", result.clazz)
        assertEquals(expected, result.timestampMeta)
    }
}
