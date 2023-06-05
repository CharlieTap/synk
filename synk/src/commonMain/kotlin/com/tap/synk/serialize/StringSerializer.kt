package com.tap.synk.serialize

import com.tap.synk.ext.decodeToHashmap
import com.tap.synk.ext.encodeToString

interface StringSerializer<T> : Serializer<T, String> {
    companion object {
        fun <T> factory(serialize: (T) -> String, deserialize: (String) -> T): StringSerializer<T> {
            return object : StringSerializer<T> {
                override fun serialize(serializable: T): String {
                    return serialize(serializable)
                }

                override fun deserialize(serialized: String): T {
                    return deserialize(serialized)
                }
            }
        }
    }
}

object BooleanStringSerializer : StringSerializer<Boolean> by StringSerializer.factory(Boolean::toString, String::toBoolean)
object ByteStringSerializer : StringSerializer<Byte> by StringSerializer.factory(Byte::toString, String::toByte)
object IntStringSerializer : StringSerializer<Int> by StringSerializer.factory(Int::toString, String::toInt)
object ShortStringSerializer : StringSerializer<Short> by StringSerializer.factory(Short::toString, String::toShort)
object FloatStringSerializer : StringSerializer<Float> by StringSerializer.factory(Float::toString, String::toFloat)
object DoubleStringSerializer : StringSerializer<Double> by StringSerializer.factory(Double::toString, String::toDouble)
object LongStringSerializer : StringSerializer<Long> by StringSerializer.factory(Long::toString, String::toLong)
object CharStringSerializer : StringSerializer<Char> by StringSerializer.factory(Char::toString, { str -> str.toCharArray().first() })

object StringMapStringSerializer : StringSerializer<Map<String, String>> by StringSerializer.factory(
    Map<String, String>::encodeToString,
    String::decodeToHashmap,
)
