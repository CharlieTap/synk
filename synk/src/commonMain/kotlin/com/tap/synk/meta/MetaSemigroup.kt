package com.tap.synk.meta

import com.tap.synk.abstraction.Semigroup

internal object MetaSemigroup : Semigroup<Meta> {
    override fun combine(a: Meta, b: Meta): Meta {
        val allKeys = a.timestampMeta.entries union b.timestampMeta.entries

        val newMap = allKeys.fold(HashMap<String, String>()) { acc, mutableEntry ->

            val aValue = a.timestampMeta[mutableEntry.key]
            val bValue = b.timestampMeta[mutableEntry.key]

            val newValue = if (aValue != null && bValue != null) {
                maxOf(aValue, bValue)
            } else {
                aValue ?: bValue
            }

            acc.apply {
                put(mutableEntry.key, newValue!!)
            }
        }

        return Meta(
            a.namespace.ifEmpty { b.namespace },
            newMap
        )
    }
}