package com.tap.synk.meta

import com.tap.hlc.HybridLogicalClock
import com.tap.synk.adapter.SynkAdapter

/**
 * Metadata needed differentiate between CRDT state updates
 *
 * Meta will track two properties
 *
 * @property namespace The FQCN of the CRDT it belongs to.
 * @property timestampMeta A HashMap<String, String> where:
 * The key is a property belonging to the CRDT
 * The value is a HLC representing the "updated_at" for the property inside the key
 *
 *
 * CRDTs can change their shape over time, For example: add fields, remove fields, change field names even
 * Meta should track all fields always, even as CRDTs change shape because we are never sure if all
 * nodes in the system are operating the same version.
 */
data class Meta(
    val namespace: String,
    val timestampMeta: Map<String, String>
) {
    operator fun plus(meta: Meta): Meta {
        return MetaSemigroup.combine(this, meta)
    }
}

internal fun <T : Any> meta(crdt: T, adapter: SynkAdapter<T>, hlc: HybridLogicalClock): Meta {
    val encoded = adapter.encode(crdt)
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
