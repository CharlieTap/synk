package com.tap.synk.encode

import com.tap.synk.encode.MapEncoder.Key.NO_KEY

object StringEncoder : MapEncoder<String> {
    override fun encode(crdt: String): Map<String, String> {
        return mapOf(NO_KEY to crdt)
    }

    override fun decode(map: Map<String, String>): String {
        return map[NO_KEY]!!
    }
}