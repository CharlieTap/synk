package com.tap.synk.extension


import com.tap.synk.meta.Meta
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.serialization.json.Json

class MetaSerializerTest {

    @Test
    fun `serializer correctly serializes and deserializes meta`() {
        val meta = Meta("test", mapOf("foo" to "bar"))
        val json = Json.encodeToString(MetaSerializer, meta)
        val result = Json.decodeFromString(MetaSerializer, json)

        assertEquals(meta, result)
    }
}
