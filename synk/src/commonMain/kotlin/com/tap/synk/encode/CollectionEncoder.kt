package com.tap.synk.encode

class CollectionEncoder<T: Any>(
    private val key: String,
    private val encoder: MapEncoder<T>,
    private val container: MutableCollection<T>
): MapEncoder<Collection<T>> {

    override fun encode(crdt: Collection<T>): Map<String, String> {
        return crdt.iterator().encode(key, encoder)
    }

    override fun decode(map: Map<String, String>): Collection<T> {

        val groupedMap = map.groupedByEncodedIndex()
        val size = map.encodedItemCount() ?: return container

        return container.apply {
            (0 .. size).forEach { idx ->
                val subMap = groupedMap[idx] ?: emptyMap()
                val decoded = encoder.decode(subMap)
                add(decoded)
            }
        }
    }
}
