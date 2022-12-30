package com.tap.synk

import com.tap.synk.relay.Message
import com.tap.synk.relay.decodeToMessages

inline fun <reified T : Any> Synk.decode(encoded: String): List<Message<T>> {
    val adapter = synkAdapterStore.resolve(T::class)
    return encoded.decodeToMessages(adapter) as List<Message<T>>
}
