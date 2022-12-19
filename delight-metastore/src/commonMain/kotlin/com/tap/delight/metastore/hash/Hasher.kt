package com.tap.delight.metastore.hash

interface Hasher {

    fun hash(plaintext: String): String
}
