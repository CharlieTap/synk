package com.tap.synk.meta

import com.benasher44.uuid.Uuid
import com.tap.synk.meta.store.InMemoryMetaStore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class InMemoryMetaStoreTest {

    @Test
    fun `can save and retrieve meta maps from store`() {
        val metaStore = InMemoryMetaStore(HashMap())

        val key = Uuid.randomUUID().toString()
        val meta = HashMap<String, String>().apply {
            put("name", "123456789")
            put("phone", "234567890")
        }

        metaStore.putMeta(key, meta)
        val result = metaStore.getMeta(key)

        assertNotNull(result)
        assertEquals(meta, result)
    }
}
