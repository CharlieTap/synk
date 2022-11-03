package com.tap.synk

import com.benasher44.uuid.Uuid
import com.tap.synk.meta.Meta
import com.tap.synk.relay.Message
import com.tap.synk.relay.MessageMonoid

fun <T: Any> Synk.inbound(message: Message<T>, old: T? = null) : T {

    //todo advance hlc

    // need an abstraction for T
    // Can retrieve an id for T, even if compound (T) -> String (ID),
    // Can retrieve a T given an id (String) -> T

    val metaStore = factory.getStore(message.crdt::class)
    val id = Uuid.randomUUID()

    val newMessage = old?.let {

         val oldMetaMap = metaStore.getMeta(id)
         val oldMeta = Meta(old::class.qualifiedName!!, oldMetaMap!!)

         merger.combine(Message(old, oldMeta), message)
     } ?: message

     //save meta data
    metaStore.putMeta(id, newMessage.meta.timestampMeta)

    return newMessage.crdt
 }

fun <T> Synk.inbound(message: Message<T>) {
}
