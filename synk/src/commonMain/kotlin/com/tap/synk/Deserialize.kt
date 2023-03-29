package com.tap.synk

import com.tap.synk.relay.Message
import com.tap.synk.relay.decodeToMessage
import com.tap.synk.relay.decodeToMessages

inline fun <reified T : Any> Synk.deserialize(encoded: String): List<Message<T>> {
    val adapter = synkAdapterStore.resolve(T::class)
    return encoded.decodeToMessages(adapter) as List<Message<T>>
}

inline fun <reified T : Any> Synk.deserializeOne(encoded: String): Message<T> {
    val adapter = synkAdapterStore.resolve(T::class)
    return encoded.decodeToMessage(adapter) as Message<T>
}
