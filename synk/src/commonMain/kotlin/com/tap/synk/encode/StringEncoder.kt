package com.tap.synk.encode

object StringEncoder : MapEncoder<String> {
    override fun encode(crdt: String): Map<String, String> {
        return mapOf("" to crdt)
    }

    override fun decode(map: Map<String, String>): String {
        return map[""]!!
    }
}