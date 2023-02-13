package com.tap.synk.relay

import com.tap.synk.adapter.SynkAdapter
import com.tap.synk.meta.Meta
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

data class Message<out T>(
    val crdt: T,
    val meta: Meta
)

internal fun <T : Any> Message<T>.encodeAsJsonObject(synkAdapter: SynkAdapter<T>): JsonObject {
    return JsonObject(
        mapOf(
            "crdt" to synkAdapter.encode(crdt).encodeAsJsonObject(),
            "meta" to JsonObject(
                mapOf(
                    "clazz" to JsonPrimitive(meta.namespace),
                    "timestamp_meta" to meta.timestampMeta.encodeAsJsonObject()
                )
            )
        )
    )
}

internal fun <T : Any> List<Message<T>>.encodeAsJsonObject(synkAdapter: SynkAdapter<T>): JsonArray {
    return JsonArray(map { message -> message.encodeAsJsonObject(synkAdapter) })
}

internal fun String.decodeToJsonObject(): JsonObject {
    return Json.decodeFromString(JsonObject.serializer(), this)
}

internal fun String.decodeToJsonArray(): JsonArray {
    return Json.decodeFromString(JsonArray.serializer(), this)
}

internal fun <T : Any> JsonObject.decodeToMessage(synkAdapter: SynkAdapter<T>): Message<T> {
    val crdt = this["crdt"]?.jsonObject
    val meta = this["meta"]?.jsonObject
    return Message(
        synkAdapter.decode(crdt?.decodeToMap() ?: emptyMap()),
        Meta(
            meta?.get("clazz")?.jsonPrimitive?.content ?: "",
            meta?.get("timestamp_meta")?.jsonObject?.decodeToMap() ?: throw IllegalStateException("Failed to find timestamp meta in Message")
        )
    )
}

internal fun Map<String, String>.encodeAsJsonObject(): JsonObject {
    return JsonObject(
        this.map { entry ->
            entry.key to JsonPrimitive(entry.value)
        }.toMap()
    )
}

internal fun JsonObject.decodeToMap(): Map<String, String> {
    val map = mutableMapOf<String, String>()
    map { entry ->
        map.put(entry.key, entry.value.jsonPrimitive.content)
    }
    return map
}

fun <T : Any> Message<T>.encodeAsString(synkAdapter: SynkAdapter<T>): String {
    return encodeAsJsonObject(synkAdapter).toString()
}

fun <T : Any> List<Message<T>>.encodeAsString(synkAdapter: SynkAdapter<T>): String {
    return encodeAsJsonObject(synkAdapter).toString()
}

fun <T : Any> String.decodeToMessage(synkAdapter: SynkAdapter<T>): Message<T> {
    return decodeToJsonObject().decodeToMessage(synkAdapter)
}

fun <T : Any> String.decodeToMessages(synkAdapter: SynkAdapter<T>): List<Message<T>> {
    return decodeToJsonArray().map { element -> element.jsonObject.decodeToMessage(synkAdapter) }
}
