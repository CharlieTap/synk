package com.tap.synk.encode

import com.tap.synk.encode.MapEncoder.Key.NO_KEY
import com.tap.synk.serialize.BooleanStringSerializer
import com.tap.synk.serialize.ByteStringSerializer
import com.tap.synk.serialize.CharStringSerializer
import com.tap.synk.serialize.DoubleStringSerializer
import com.tap.synk.serialize.FloatStringSerializer
import com.tap.synk.serialize.IntStringSerializer
import com.tap.synk.serialize.LongStringSerializer
import com.tap.synk.serialize.ShortStringSerializer
import com.tap.synk.serialize.StringSerializer

class PrimitiveEncoder<T>(
    private val serializer: StringSerializer<T>
) : MapEncoder<T> {
    override fun encode(crdt: T): Map<String, String> {
        return mapOf(NO_KEY to serializer.serialize(crdt))
    }

    override fun decode(map: Map<String, String>): T {
        return serializer.deserialize(map[NO_KEY]!!)
    }
}

object BooleanEncoder : MapEncoder<Boolean> by PrimitiveEncoder(BooleanStringSerializer)
object IntEncoder : MapEncoder<Int> by PrimitiveEncoder(IntStringSerializer)
object ShortEncoder : MapEncoder<Short> by PrimitiveEncoder(ShortStringSerializer)
object FloatEncoder : MapEncoder<Float> by PrimitiveEncoder(FloatStringSerializer)
object DoubleEncoder : MapEncoder<Double> by PrimitiveEncoder(DoubleStringSerializer)
object LongEncoder : MapEncoder<Long> by PrimitiveEncoder(LongStringSerializer)
object ByteEncoder : MapEncoder<Byte> by PrimitiveEncoder(ByteStringSerializer)
object CharEncoder : MapEncoder<Char> by PrimitiveEncoder(CharStringSerializer)
