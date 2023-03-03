package com.tap.synk.encode

class PrimitiveEncoder<T: Any>(
    private val toString: (T) -> String,
    private val fromString: (String) -> T
) : MapEncoder<T> {
    companion object {
        private const val NO_KEY = ""
    }
    override fun encode(crdt: T): Map<String, String> {
        return mapOf(NO_KEY to toString(crdt))
    }

    override fun decode(map: Map<String, String>): T {
        return fromString(map[NO_KEY]!!)
    }
}

object BooleanEncoder : MapEncoder<Boolean> by PrimitiveEncoder(Boolean::toString, String::toBoolean)
object IntEncoder : MapEncoder<Int> by PrimitiveEncoder(Int::toString, String::toInt)
object ShortEncoder : MapEncoder<Short> by PrimitiveEncoder(Short::toString, String::toShort)
object FloatEncoder : MapEncoder<Float> by PrimitiveEncoder(Float::toString, String::toFloat)
object DoubleEncoder : MapEncoder<Double> by PrimitiveEncoder(Double::toString, String::toDouble)
object LongEncoder : MapEncoder<Long> by PrimitiveEncoder(Long::toString, String::toLong)
object ByteEncoder : MapEncoder<Byte> by PrimitiveEncoder(Byte::toString, String::toByte)
object CharEncoder : MapEncoder<Char> by PrimitiveEncoder(Char::toString, { str -> str.toCharArray().first()})


