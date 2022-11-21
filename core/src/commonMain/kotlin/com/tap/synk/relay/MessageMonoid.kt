package com.tap.synk.relay

import com.tap.synk.abstraction.Monoid
import com.tap.synk.adapter.SynkAdapter
import com.tap.synk.meta.Meta
import com.tap.synk.meta.MetaMonoid

class MessageMonoid<T : Any>(
    private val synkAdapter: SynkAdapter<Any>,
    private val metaMonoid: Monoid<Meta>
) : Monoid<Message<T>> {

    override val neutral: Message<T>
        get() = Message(Unit as T, MetaMonoid.neutral)

    override fun combine(a: Message<T>, b: Message<T>): Message<T> {

        val aEncoded = synkAdapter.encode(a.crdt)
        val bEncoded = synkAdapter.encode(b.crdt)
        val meta = metaMonoid.combine(a.meta, b.meta)

        val newMap = meta.timestampMeta.entries.fold(HashMap<String, String>()) { acc, entry ->

            val value = if(a.meta.timestampMeta[entry.key] == entry.value) {
                aEncoded[entry.key]
            } else {
                bEncoded[entry.key]
            } ?: throw Exception("Failed to find key: " + entry.key + " in encoded hashmap, please check that you have implemented the encode function correctly")

            acc.apply {
                put(entry.key, value)
            }
        }

        // here we need to set all the values back onto the T
        val crdt = synkAdapter.decode(a.crdt, newMap) as T

        return Message(crdt, meta)
    }
}
