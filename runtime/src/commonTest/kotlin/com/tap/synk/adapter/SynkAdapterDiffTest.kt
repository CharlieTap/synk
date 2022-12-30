package com.tap.synk.adapter

import com.tap.synk.IDCRDT
import com.tap.synk.IDCRDTAdapter
import kotlin.test.Test
import kotlin.test.assertEquals

class SynkAdapterDiffTest {

    @Test
    fun `can turn object into meta`() {
        val synkAdapter = IDCRDTAdapter()

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
