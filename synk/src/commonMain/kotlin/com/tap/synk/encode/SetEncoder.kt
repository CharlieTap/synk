package com.tap.synk.encode

class SetEncoder<T: Any>(
    private val key: String,
    private val encoder: MapEncoder<T>,
    private val collectionEncoder: MapEncoder<Collection<T>> =  CollectionEncoder(key, encoder, mutableSetOf())
) : MapEncoder<Set<T>> {
    override fun encode(crdt: Set<T>): Map<String, String> {
        return collectionEncoder.encode(crdt)
    }

    override fun decode(map: Map<String, String>): Set<T> {
        return collectionEncoder.decode(map) as Set<T>
    }
}