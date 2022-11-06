package com.tap.synk

import com.tap.hlc.HybridLogicalClock
import com.tap.synk.meta.Meta
import com.tap.synk.relay.Message

/**
 * Outbound is designed for creating Messages intended to be propagated to other nodes in the system
 *
 * This function should be called after locally persisting data on the current node
 * It's expected that both @param new and @param old contain populated id properties
 */
fun <T: Any> SynkContract.outbound(new: T, old: T? = null): Message<T> {

    val metaStore = factory.getStore(new!!::class)
    val id = idResolver(old ?: new) ?: throw Exception("Unable to find id for CRDT")

    val newMessage = old?.let {

        val oldMetaMap = metaStore.getMeta(id) ?: throw Exception("Failed to find meta for provided old value")
        val diff = differ.diff(old, new)

        val hlc = HybridLogicalClock()
        val newMetaMap = HashMap<String, String>()
        oldMetaMap.entries.forEach { entry ->

            val value = if (diff.contains(entry.key)) {
               hlc.toString()
            } else entry.value

            newMetaMap[hlc.toString()] = value
        }
        val newMeta = Meta(new::class.qualifiedName!!, newMetaMap)

        Message(new, newMeta)
    } ?: Message(new, metaTransformer.toMeta(new))

    metaStore.putMeta(id, newMessage.meta.timestampMeta)

    return Message(new, Meta("", HashMap()))
}
