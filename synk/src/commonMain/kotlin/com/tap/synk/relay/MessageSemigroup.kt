package com.tap.synk.relay

import com.tap.synk.abstraction.Semigroup
import com.tap.synk.adapter.store.SynkAdapterStore

internal class MessageSemigroup<T: Any>(
    private val synkAdapterStore: SynkAdapterStore
) : Semigroup<Message<T>> {
    override fun combine(a: Message<T>, b: Message<T>): Message<T> {
        val synkAdapter = synkAdapterStore.resolve(a.crdt::class)
        val aEncoded = synkAdapter.encode(a.crdt)
        val bEncoded = synkAdapter.encode(b.crdt)
        val meta = a.meta + b.meta

        val newMap = meta.timestampMeta.entries.fold(HashMap<String, String>()) { acc, entry ->

            val value = if (a.meta.timestampMeta[entry.key] == entry.value) {
                aEncoded[entry.key]
            } else {
                bEncoded[entry.key]
            } ?: throw Exception("Failed to find key: " + entry.key + " in encoded hashmap, please check that you have implemented the encode function correctly")

            acc.apply {
                put(entry.key, value)
            }
        }

        val crdt = synkAdapter.decode(newMap) as T

        return Message(crdt, meta)
    }
}