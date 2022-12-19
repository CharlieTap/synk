package com.tap.synk.meta.transform

import com.tap.hlc.HybridLogicalClock
import com.tap.synk.adapter.SynkAdapter
import com.tap.synk.meta.Meta

internal fun <T : Any> SynkAdapter<T>.transformToMeta(crdt: T, hlc: HybridLogicalClock): Meta {
    val encoded = encode(crdt)
    val hlcs = hlc.toString()

    return encoded.keys.fold(HashMap<String, String>()) { acc, key ->
        acc.apply {
            put(key, hlcs)
        }
    }.let { meta ->
        Meta(
            crdt::class.qualifiedName ?: "",
            meta
        )
    }
}
