package com.tap.synk.serialize

interface Serializer<F, T> {
    fun serialize(serializable: F): T
    fun deserialize(serialized: T): F
}