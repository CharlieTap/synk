package com.tap.synk.encode

import com.tap.synk.CRDT
import kotlin.test.Test
import kotlin.test.assertEquals

internal class NullableEncoderTest {

    @Test
    fun `can wrap an encoder and correctly return values from nullable inputs`() {
        val crdt = CRDT(
            "123",
            "Tom",
            "Smith",
            12345667,
        )

        val encoder = IDCRDTMapEncoder()
        val nullableEncoder = NullableMapEncoder(encoder)

        val expectedEncoded = mapOf(
            "id" to "123",
            "name" to "Tom",
            "last_name" to "Smith",
            "phone" to "12345667",
        )

        assertEquals(emptyMap(), nullableEncoder.encode(null))
        assertEquals(expectedEncoded, nullableEncoder.encode(crdt))
        assertEquals(null, nullableEncoder.decode(emptyMap()))
        assertEquals(crdt, nullableEncoder.decode(nullableEncoder.encode(crdt)))
    }
}
