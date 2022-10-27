package com.tap.synk

import com.tap.synk.meta.Meta
import com.tap.synk.relay.Message

fun <T> Synk.outbound(crdt: T) : Message<T> {
    // Save crdt locally
    // Translate crdt into message
    // return message and optionally pump to relay if one is registered

    return Message(crdt, Meta("", HashMap()))
}