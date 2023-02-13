package com.tap.synk.meta

import com.tap.hlc.HybridLogicalClock
import com.tap.synk.IDCRDT
import com.tap.synk.IDCRDTAdapter
import kotlin.test.Test
import kotlin.test.assertEquals

class MetaTest {

    @Test
    fun `can derive meta from crdt`() {
        val synkAdapter = IDCRDTAdapter()
        val hlc = HybridLogicalClock()

        val crdt = IDCRDT(
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
