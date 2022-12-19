package com.tap.synk.diff

import com.tap.synk.IDCRDT
import com.tap.synk.adapter.ReflectionsSynkAdapter
import com.tap.synk.cache.ReflectionCacheEntry
import com.tap.synk.cache.ReflectionsCache
import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.assertEquals

class DiffTest {

    @Test
    fun `can turn object into meta`() {
        val cache = HashMap<KClass<*>, ReflectionCacheEntry<Any>>()
        val reflectionsCache = ReflectionsCache(cache)
        val synkAdapter = ReflectionsSynkAdapter(reflectionsCache)

        val crdt1 = IDCRDT(
            "123",
            "test",
            "test",
            1234
        )

        val crdt2 = IDCRDT(
            "123",
            "test",
            "test2",
            1234
        )

        val diff = synkAdapter.diff(crdt1, crdt2)

        assertEquals(setOf("last_name"), diff)
    }
}
