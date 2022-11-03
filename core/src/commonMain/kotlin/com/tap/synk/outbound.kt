package com.tap.synk

import com.tap.synk.abstraction.Monoid
import com.tap.synk.meta.Meta
import com.tap.synk.relay.Message

fun <T : Monoid<T>> Synk.outbound(new: T, old: T? = null): Message<T> {
    // diff what properties changed or were added, return them as a set
    // update meta for these properties to now
    // return message
    return Message(new, Meta("", HashMap()))
}

fun <T> Synk.outbound(crdt: T): Message<T> {
    // Save crdt locally
    // Translate crdt into message
    // return message and optionally pump to relay if one is registered

    return Message(crdt, Meta("", HashMap()))
}
