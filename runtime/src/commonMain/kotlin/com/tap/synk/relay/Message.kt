package com.tap.synk.relay

import com.tap.synk.meta.Meta

data class Message<out T>(
    val crdt: T,
    val meta: Meta
)
