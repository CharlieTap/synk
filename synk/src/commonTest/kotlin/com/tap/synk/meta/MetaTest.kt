package com.tap.synk.meta

import com.tap.hlc.HybridLogicalClock
import com.tap.synk.CRDT
import com.tap.synk.CRDTAdapter
import kotlin.test.Test
import kotlin.test.assertEquals

class MetaTest {

    @Test
    fun `can derive meta from crdt`() {
        val synkAdapter = CRDTAdapter()
        val hlc = HybridLogicalClock()

        val crdt = CRDT(
            "123",
            "test",
            "test",
            1234
        )

        val result = meta(crdt, synkAdapter, hlc)

        val expected = HashMap<String, String>().apply {
            val hlcs = hlc.toString()
            put("id", hlcs)
            put("name", hlcs)
            put("last_name", hlcs)
            put("phone", hlcs)
        }

        assertEquals("com.tap.synk.IDCRDT", result.namespace)
        assertEquals(expected, result.timestampMeta)
    }
}
