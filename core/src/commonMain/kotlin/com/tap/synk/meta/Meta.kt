package com.tap.synk.meta

import com.tap.synk.merge.Mergeable

/**
 * Metadata needed differentiate between CRDT state updates
 *
 * Meta will track two properties
 *
 * @property clazz The FQCN of the CRDT it belongs to.
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
    val clazz: String,
    val timestampMeta: HashMap<String, String>
) : Mergeable<Meta>
{
    override fun merge(other: Meta): Meta {
        if(clazz != other.clazz) {
            throw IllegalStateException("Cannot merge two logically distinct entities")
        }

        val allKeys = timestampMeta.entries union other.timestampMeta.entries

        val newMap = allKeys.fold(HashMap<String, String>()) { acc, mutableEntry ->

            val firstValue = timestampMeta[mutableEntry.key]
            val secondValue = other.timestampMeta[mutableEntry.key]

            val newValue = if(firstValue != null && secondValue != null) {
               maxOf(firstValue, secondValue)
            } else {
                firstValue ?: secondValue
            }

            acc.apply {
                put(mutableEntry.key, newValue!!)
            }
        }
        
        return Meta(
            clazz,
            newMap
        )
    }

}