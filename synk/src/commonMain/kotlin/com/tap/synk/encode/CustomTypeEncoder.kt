package com.tap.synk.encode

class CustomTypeEncoder<T: Any>(
    private val key: String,
    private val toString: (T) -> String,
    private val fromString: (String) -> T
) : MapEncoder<T> {
    override fun encode(crdt: T): Map<String, String> {
        return mapOf(key to toString(crdt))
    }

    override fun decode(map: Map<String, String>): T {
        return fromString(map[key]!!)
    }
}
