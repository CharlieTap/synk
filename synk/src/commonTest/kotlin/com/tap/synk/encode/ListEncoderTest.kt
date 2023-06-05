package com.tap.synk.encode

import com.tap.synk.CRDT
import kotlin.test.Test
import kotlin.test.assertEquals

internal class ListEncoderTest {

    @Test
    fun `can encode a list of strings to a map and back again`() {
        val list = listOf(
            "Jim",
            "Bob",
            "Jake",
        )

        val encoder = ListEncoder("list", StringEncoder)
        val encoded = encoder.encode(list)
        val decoded = encoder.decode(encoded)

        val expected = mapOf(
            "0|list|" to "Jim",
            "1|list|" to "Bob",
            "2|list|" to "Jake",
        )

        assertEquals(expected, encoded)
        assertEquals(list, decoded)
    }

    @Test
    fun `can encode a list of a user defined type to a map and back again`() {
        val list = listOf(
            CRDT("12323", "Jim", "Bob", 123456),
            CRDT("12325", "Jake", "Smith", 123444),
        )

        val encoder = ListEncoder("list", IDCRDTMapEncoder())
        val encoded = encoder.encode(list)
        val decoded = encoder.decode(encoded)

        val expected = mapOf(
            "0|list|id" to "12323",
            "0|list|name" to "Jim",
            "0|list|last_name" to "Bob",
            "0|list|phone" to "123456",
            "1|list|id" to "12325",
            "1|list|name" to "Jake",
            "1|list|last_name" to "Smith",
            "1|list|phone" to "123444",
        )

        assertEquals(expected, encoded)
        assertEquals(list, decoded)
    }
}
