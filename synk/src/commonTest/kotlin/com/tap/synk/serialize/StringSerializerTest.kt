package com.tap.synk.serialize

import kotlin.test.Test
import kotlin.test.assertEquals

// TODO Could compress the the numeric primitives
class StringSerializerTest {


    @Test
    fun `can serialize and deserialize a Bool`() {
        val serializer = BooleanStringSerializer
        val input = false

        val serialized = serializer.serialize(input)
        val deserialized = serializer.deserialize(serialized)

        assertEquals("false", serialized)
        assertEquals(input, deserialized)
    }

    @Test
    fun `can serialize and deserialize a Byte`() {
        val serializer = ByteStringSerializer
        val input : Byte = 30

        val serialized = serializer.serialize(input)
        val deserialized = serializer.deserialize(serialized)

        assertEquals("30", serialized)
        assertEquals(input, deserialized)
    }

    @Test
    fun `can serialize and deserialize an Int`() {
        val serializer = IntStringSerializer
        val input = 4

        val serialized = serializer.serialize(input)
        val deserialized = serializer.deserialize(serialized)

        assertEquals("4", serialized)
        assertEquals(input, deserialized)
    }

    @Test
    fun `can serialize and deserialize a Short`() {
        val serializer = ShortStringSerializer
        val input : Short = 4

        val serialized = serializer.serialize(input)
        val deserialized = serializer.deserialize(serialized)

        assertEquals("4", serialized)
        assertEquals(input, deserialized)
    }

    @Test
    fun `can serialize and deserialize a Double`() {
        val serializer = DoubleStringSerializer
        val input = 22.8e5

        val serialized = serializer.serialize(input)
        val deserialized = serializer.deserialize(serialized)

        assertEquals("2280000.0", serialized)
        assertEquals(input, deserialized)
    }

    @Test
    fun `can serialize and deserialize a Char`() {
        val serializer = CharStringSerializer
        val input = 'c'

        val serialized = serializer.serialize(input)
        val deserialized = serializer.deserialize(serialized)

        assertEquals("c", serialized)
        assertEquals(input, deserialized)
    }

}
