package com.tap.delight.metastore.hash

import com.goncalossilva.murmurhash.MurmurHash3

internal class MurmurHasher(
    private val murmurHash3: MurmurHash3 = MurmurHash3(),
) : Hasher {
    override fun hash(plaintext: String): String {
        return murmurHash3.hash128x64(plaintext.encodeToByteArray()).joinToString(separator = "") { it.toString(16) }
    }
}
