package com.tap.synk

import com.tap.synk.abstraction.Monoid
import com.tap.synk.meta.Meta
import com.tap.synk.relay.Message

fun <T : Monoid<T>> Synk.outbound(old: T, new: T): Message<T> {
    return Message(new, Meta("", HashMap()))
}

fun <T> Synk.outbound(crdt: T): Message<T> {
    // Save crdt locally
    // Translate crdt into message
    // return message and optionally pump to relay if one is registered

    return Message(crdt, Meta("", HashMap()))
}
