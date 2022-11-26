package com.tap.synk.serialize

import java.lang.Exception
import kotlin.reflect.KClass

internal fun serialize(value: Any?): String {
    return when (value) {
        is Boolean -> if (value) "1" else "0"
        is Byte -> value.toString()
        is Short -> value.toString()
        is Int -> value.toString()
        is Long -> value.toString()
        is Float -> value.toString()
        is Double -> value.toString()
        is Char -> value.toString()
        is String -> value
        null -> "null"
        else -> throw Exception("Failed to serialize CRDT for type " + value::class.qualifiedName + ", please provide a synk adapter")
    }
}

internal fun <T : Any> deserialize(clazz: KClass<T>, serialized: String): T? {
    if (serialized == "null") return null

    return when (clazz) {
        Boolean::class -> serialized == "1"
        Byte::class -> serialized.toByte()
        Short::class -> serialized.toShort()
        Int::class -> serialized.toInt()
        Long::class -> serialized.toLong()
        Float::class -> serialized.toFloat()
        Double::class -> serialized.toDouble()
        Char::class -> serialized.toCharArray().first()
        String::class -> serialized
        else -> throw Exception("Failed to deserialize CRDT for type " + clazz.qualifiedName + ", please provide a synk adapter")
    } as T
}
