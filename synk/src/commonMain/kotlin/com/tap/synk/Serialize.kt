package com.tap.synk

import com.tap.synk.relay.Message
import com.tap.synk.relay.encodeAsString

inline fun <reified T : Any> Synk.serialize(messages: List<Message<T>>): String {
    val adapter = synkAdapterStore.resolve(T::class)
    return messages.encodeAsString(adapter)
}

inline fun <reified T : Any> Synk.serializeOne(messages: Message<T>): String {
    val adapter = synkAdapterStore.resolve(T::class)
    return messages.encodeAsString(adapter)
}
