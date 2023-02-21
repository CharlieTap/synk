package com.tap.synk.encode

interface MapEncoder<T : Any> {
    fun encode(crdt: T): Map<String, String>

    fun decode(map: Map<String, String>): T
}
