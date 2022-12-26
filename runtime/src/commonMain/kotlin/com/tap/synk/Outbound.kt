package com.tap.synk

import com.github.michaelbull.result.getOr
import com.tap.hlc.HybridLogicalClock
import com.tap.synk.diff.diff
import com.tap.synk.meta.Meta
import com.tap.synk.meta.transform.transformToMeta
import com.tap.synk.relay.Message
import kotlinx.coroutines.flow.update

/**
 * Outbound is designed for creating Messages intended to be propagated to other nodes in the system
 *
 * This function should be called after locally persisting data on the current node
 * It's expected that both @param new and @param old contain populated id properties
 */
fun <T : Any> Synk.outbound(new: T, old: T? = null): Message<T> {
    val synkAdapter = synkAdapterStore.resolve(new::class)
    val metaStore = factory.getStore(new::class)
    val id = synkAdapter.resolveId(old ?: new) ?: throw Exception("Unable to find id for CRDT")

    hlc.update { atomicHlc ->
        HybridLogicalClock.localTick(atomicHlc).getOr(atomicHlc)
    }

    val newMessage = old?.let {
        val oldMetaMap = metaStore.getMeta(id) ?: throw Exception("Failed to find meta for provided old value")
        val diff = synkAdapter.diff(old, new)

        val newMetaMap = HashMap<String, String>()
        oldMetaMap.entries.forEach { entry ->

            val value = if (diff.contains(entry.key)) {
                hlc.value.toString()
            } else entry.value

            newMetaMap[entry.key] = value
        }
        val newMeta = Meta(new::class.qualifiedName!!, newMetaMap)

        Message(new, newMeta)
    } ?: Message(new, synkAdapter.transformToMeta(new, hlc.value))

    metaStore.putMeta(id, newMessage.meta.timestampMeta)

    return newMessage
}
