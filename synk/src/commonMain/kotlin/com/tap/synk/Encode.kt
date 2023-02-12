package com.tap.synk

import com.tap.synk.relay.Message
import com.tap.synk.relay.encodeAsString

inline fun <reified T : Any> Synk.encode(messages: List<Message<T>>): String {
    val adapter = synkAdapterStore.resolve(T::class)
    return messages.encodeAsString(adapter)
}

inline fun <reified T : Any> Synk.encodeOne(messages: Message<T>): String {
    val adapter = synkAdapterStore.resolve(T::class)
    return messages.encodeAsString(adapter)
}