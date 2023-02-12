package com.tap.synk

import com.github.michaelbull.result.getOr
import com.tap.hlc.HybridLogicalClock
import com.tap.synk.adapter.SynkAdapter
import com.tap.synk.meta.Meta
import com.tap.synk.meta.store.MetaStoreFactory
import com.tap.synk.relay.Message
import com.tap.synk.relay.MessageSemigroup
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

/**
 * Inbound is designed for Messages from other remote nodes in the system
 *
 * It's expected that this function be called prior to persisting the state contained in the Message
 * Given @param message obtained remotely from the network
 * and @param old which is the current persisted value for object contained in the message, if one exists, if it
 * is a new CRDT then null is the correct input
 *
 * This function will produce the new value to be inserted into the database
 */
fun <T : Any> Synk.inbound(message: Message<T>, old: T? = null): T {
    return synkInbound(message, old, hlc, factory, synkAdapterStore.resolve(message.crdt::class), merger) as T
}

internal fun <T : Any> synkInbound(
    message: Message<T>,
    old: T? = null,
    hlc: MutableStateFlow<HybridLogicalClock>,
    factory: MetaStoreFactory,
    synkAdapter: SynkAdapter<Any>,
    messageMerger: MessageSemigroup<T>
): T {
    val remoteHlc = message.meta.timestampMeta.map { HybridLogicalClock.decodeFromString(it.value).getOr(hlc.value) }.reduce { acc, result ->
        maxOf(acc, result)
    }
    hlc.update { atomicHlc ->
        HybridLogicalClock.remoteTock(atomicHlc, remoteHlc).getOr(atomicHlc)
    }

    val metaStore = factory.getStore(message.crdt::class)
    val id = synkAdapter.resolveId(old ?: message.crdt)

    val newMessage = old?.let {
        val oldMetaMap = metaStore.getMeta(id)
        val oldMeta = Meta(old::class.qualifiedName!!, oldMetaMap!!)

        messageMerger.combine(Message(old, oldMeta), message)
    } ?: message

    metaStore.putMeta(id, newMessage.meta.timestampMeta)

    return newMessage.crdt
}
