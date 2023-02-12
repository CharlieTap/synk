package com.tap.synk.serialize

import kotlin.test.Test
import kotlin.test.assertEquals

class StringSerializerTest {

    @Test
    fun `can serialize and deserialize a Bool`() {
        val bool = true
        val serialized = serialize(bool)
        val deserialized = deserialize(Boolean::class, serialized)

        assertEquals(bool, deserialized)
    }

    @Test
    fun `can serialize and deserialize a Byte`() {
        val byte: Byte = 30
        val serialized = serialize(byte)
        val deserialized = deserialize(Byte::class, serialized)

        assertEquals(byte, deserialized)
    }

    @Test
    fun `can serialize and deserialize a Short`() {
        val short: Short = 4
        val serialized = serialize(short)
        val deserialized = deserialize(Short::class, serialized)

        assertEquals(short, deserialized)
    }

    @Test
    fun `can serialize and deserialize a Double`() {
        val double = 22.8e5
        val serialized = serialize(double)
        val deserialized = deserialize(Double::class, serialized)

        assertEquals(double, deserialized)
    }

    @Test
    fun `can serialize and deserialize a Char`() {
        val char = 'c'
        val serialized = serialize(char)
        val deserialized = deserialize(Char::class, serialized)

        assertEquals(char, deserialized)
    }

    @Test
    fun `can serialize and deserialize a null`() {
        val nully = null
        val serialized = serialize(nully)
        val deserialized = deserialize(String::class, serialized)

        assertEquals(nully, deserialized)
    }
}
