package com.tap.synk.extension

import com.tap.synk.meta.Meta
import com.tap.synk.relay.Message
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class MessageSerializerTest {

    @Test
    fun `serializer correctly serializes and deserializes message`() {
        val foo = Foo("bar", 30)
        val meta = Meta("foo", mapOf("foo" to "bar"))
        val message = Message(foo, meta)

        val serializer = MessageSerializer(Foo.serializer())

        val json = Json.encodeToString(serializer, message)
        val result = Json.decodeFromString(serializer, json)

        assertEquals(message, result)
    }
}
