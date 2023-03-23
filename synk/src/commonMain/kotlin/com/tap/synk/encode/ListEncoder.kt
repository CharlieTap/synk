package com.tap.synk.encode

class ListEncoder<T : Any>(
    private val key: String,
    private val encoder: MapEncoder<T>,
    private val collectionEncoder: MapEncoder<Collection<T>> = CollectionEncoder(key, encoder, mutableListOf())
) : MapEncoder<List<T>> {
    override fun encode(crdt: List<T>): Map<String, String> {
        return collectionEncoder.encode(crdt)
    }

    override fun decode(map: Map<String, String>): List<T> {
        return collectionEncoder.decode(map) as List<T>
    }
}
