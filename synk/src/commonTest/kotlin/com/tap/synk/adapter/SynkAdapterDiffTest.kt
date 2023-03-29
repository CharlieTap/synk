package com.tap.synk.adapter

import com.tap.synk.CRDT
import com.tap.synk.CRDTAdapter
import kotlin.test.Test
import kotlin.test.assertEquals

class SynkAdapterDiffTest {

    @Test
    fun `can turn object into meta`() {
        val synkAdapter = CRDTAdapter()

        val crdt1 = CRDT(
            "123",
            "test",
            "test",
            1234
        )

        val crdt2 = CRDT(
            "123",
            "test",
            "test2",
            1234
        )

        val diff = synkAdapter.diff(crdt1, crdt2)

        assertEquals(setOf("last_name"), diff)
    }
}
