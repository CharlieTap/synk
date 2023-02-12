package com.tap.synk.extension

import com.tap.synk.meta.Meta
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure

object MetaSerializer : KSerializer<Meta> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("meta") {
        element<String>("clazz")
        element<Map<String, String>>("timestamp_meta")
    }

    override fun serialize(encoder: Encoder, value: Meta) = encoder.encodeStructure(descriptor) {
        encodeStringElement(descriptor, 0, value.clazz)
        encodeSerializableElement(descriptor, 1, MapSerializer(String.serializer(), String.serializer()), value.timestampMeta)
    }

    override fun deserialize(decoder: Decoder): Meta = decoder.decodeStructure(descriptor) {
        var clazz = ""
        var timestampMeta = emptyMap<String, String>()
        while (true) {
            when (val index = decodeElementIndex(descriptor)) {
                0 -> clazz = decodeStringElement(descriptor, 0)
                1 -> timestampMeta = decodeSerializableElement(descriptor, 1, MapSerializer(String.serializer(), String.serializer()))
                CompositeDecoder.DECODE_DONE -> break
                else -> error("Unexpected index: $index")
            }
        }
        Meta(clazz, timestampMeta)
    }
}