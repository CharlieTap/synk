package com.tap.synk

import com.github.michaelbull.result.getOr
import com.tap.hlc.HybridLogicalClock
import com.tap.synk.meta.Meta
import com.tap.synk.relay.Message
import kotlinx.atomicfu.update

/**
 * Outbound is designed for creating Messages intended to be propagated to other nodes in the system
 *
 * This function should be called after locally persisting data on the current node
 * It's expected that both @param new and @param old contain populated id properties
 */
fun <T : Any> SynkContract.outbound(new: T, old: T? = null): Message<T> {
    val metaStore = factory.getStore(new::class)
    val id = idResolver(old ?: new) ?: throw Exception("Unable to find id for CRDT")

    hlc.update { atomicHlc ->
        HybridLogicalClock.localTick(atomicHlc).getOr(atomicHlc)
    }

    val newMessage = old?.let {
        val oldMetaMap = metaStore.getMeta(id) ?: throw Exception("Failed to find meta for provided old value")
        val diff = differ.diff(old, new)

        val newMetaMap = HashMap<String, String>()
        oldMetaMap.entries.forEach { entry ->

            val value = if (diff.contains(entry.key)) {
                hlc.toString()
            } else entry.value

            newMetaMap[entry.key] = value
        }
        val newMeta = Meta(new::class.qualifiedName!!, newMetaMap)

        Message(new, newMeta)
    } ?: Message(new, metaTransformer.toMeta(new, hlc.value))

    metaStore.putMeta(id, newMessage.meta.timestampMeta)

    return newMessage
}
