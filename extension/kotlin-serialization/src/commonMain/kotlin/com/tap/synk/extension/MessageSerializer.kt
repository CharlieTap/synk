package com.tap.synk.extension

import com.tap.synk.meta.Meta
import com.tap.synk.relay.Message
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure

class MessageSerializer<T>(
    private val innerSerializer: KSerializer<T>,
) : KSerializer<Message<T>> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("message") {
        element("crdt", innerSerializer.descriptor)
        element("meta", MetaSerializer.descriptor)
    }
    override fun serialize(encoder: Encoder, value: Message<T>) = encoder.encodeStructure(descriptor) {
        encodeSerializableElement(descriptor, 0, innerSerializer, value.crdt)
        encodeSerializableElement(descriptor, 1, MetaSerializer, value.meta)
    }

    override fun deserialize(decoder: Decoder): Message<T> = decoder.decodeStructure(descriptor) {
        var crdt: T? = null
        var meta: Meta? = null
        while (true) {
            when (val index = decodeElementIndex(descriptor)) {
                0 -> crdt = decodeSerializableElement(descriptor, 0, innerSerializer)
                1 -> meta = decodeSerializableElement(descriptor, 1, MetaSerializer)
                CompositeDecoder.DECODE_DONE -> break
                else -> error("Unexpected index: $index")
            }
        }
        Message(crdt!!, meta!!)
    }
}
