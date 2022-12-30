package com.tap.synk

import com.github.michaelbull.result.getOr
import com.tap.hlc.HybridLogicalClock
import com.tap.synk.adapter.diff
import com.tap.synk.adapter.store.SynkAdapterStore
import com.tap.synk.meta.Meta
import com.tap.synk.meta.meta
import com.tap.synk.meta.store.MetaStoreFactory
import com.tap.synk.relay.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

/**
 * Outbound is designed for creating Messages intended to be propagated to other nodes in the system
 *
 * This function should be called after locally persisting data on the current node
 * It's expected that both @param new and @param old contain populated id properties
 */
fun <T : Any> Synk.outbound(new: T, old: T? = null): Message<T> {
    return synkOutbound(new, old, hlc, synkAdapterStore, factory)
}

internal fun <T : Any> synkOutbound(
    new: T,
    old: T?,
    hlc: MutableStateFlow<HybridLogicalClock>,
    adapterStore: SynkAdapterStore,
    factory: MetaStoreFactory
): Message<T> {
    val synkAdapter = adapterStore.resolve(new::class)
    val metaStore = factory.getStore(new::class)
    val id = synkAdapter.resolveId(old ?: new)

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
    } ?: Message(new, meta(new, synkAdapter, hlc.value))

    metaStore.putMeta(id, newMessage.meta.timestampMeta)

    return newMessage
}
