package com.tap.synk.encode

interface MapEncoder<T> {
    companion object Key {
        internal const val NO_KEY = ""
    }
    fun encode(crdt: T): Map<String, String>

    fun decode(map: Map<String, String>): T
}
