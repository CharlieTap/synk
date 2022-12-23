package com.tap.synk

import com.tap.synk.relay.Message

fun <T : Any> Synk.Compress(messages: List<Message<T>>): List<Message<T>> {
    if (messages.isEmpty()) return emptyList()
    val adapter = synkAdapterStore.resolve(messages.first().crdt::class)
    return messages
        .groupBy { message ->
            adapter.resolveId(message.crdt)
        }
        .map { entry ->
            entry.value.reduce { acc, message ->
                merger.combine(acc, message) as Message<T>
            }
        }
}
