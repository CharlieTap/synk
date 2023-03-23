package com.tap.synk.encode

internal fun <T : Any> Iterator<T>.encode(key: String, encoder: MapEncoder<T>): Map<String, String> {
    return mutableMapOf<String, String>().apply {
        var idx = 0
        this@encode.forEach { value ->
            val encoded = encoder.encode(value)
            encoded.forEach { (subKey, subValue) ->
                put("$idx|$key|$subKey", subValue)
            }
            idx++
        }
    }
}
