package com.tap.synk.encode

class NullableMapEncoder<T>(
    private val encoder: MapEncoder<T>
) : MapEncoder<T?> {
    override fun encode(crdt: T?): Map<String, String> {
        return crdt?.let {
            encoder.encode(crdt)
        } ?: emptyMap()
    }

    override fun decode(map: Map<String, String>): T? {
        return if (map.isEmpty()) {
            null
        } else encoder.decode(map)
    }
}
