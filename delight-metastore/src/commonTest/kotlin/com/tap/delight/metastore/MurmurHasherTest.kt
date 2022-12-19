package com.tap.delight.metastore

import com.goncalossilva.murmurhash.MurmurHash3
import com.tap.delight.metastore.hash.MurmurHasher
import kotlin.test.Test
import kotlin.test.assertEquals

class MurmurHasherTest {

    @Test
    fun `murmur hash correctly hashes key`() {
        val hasher = MurmurHasher(MurmurHash3())
        val key = "test"
        val result = hasher.hash(key)

        assertEquals("ac7d28cc74bde19d9a128231f9bd4d82", result)
    }
}
