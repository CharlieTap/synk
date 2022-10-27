package com.tap.synk.meta.transformer

import com.tap.hlc.HybridLogicalClock
import com.tap.synk.meta.Meta
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.declaredMemberProperties

/**
 * This class transforms a CRDT class into its Meta data
 *
 * We will be given a CRDT data class and be expected to insert it into the hashmap
 * What's stored in the hashmap is not identifiable data except the id, but this is the key ultimately
 * The rest of the data can be derived simply from the class itself, the HLC's are the only things that change
 *
 * On Insert all HLCs are set to the current time
 * HLCS will only ever move forward
 * Updates will move select HLCs forward
 *
 *
 */
class ReflectionsMetaTransformer(
    private val hlcFactory: () -> HybridLogicalClock
) : MetaTransformer<Any> {

    companion object {
        private val CACHE = HashMap<KClass<*>, Pair<String, Set<String>>>()

        private fun getCacheEntry(clazz: KClass<*>) : Pair<String, Set<String>>? {
            return CACHE[clazz]
        }

        private fun putCacheEntry(clazz: KClass<*>, entry: Pair<String, Set<String>>) {
            CACHE[clazz] = entry
        }
    }


    override fun toMeta(crdt: Any) : Meta {
        val clazz = crdt::class
        val cacheEntry =  getCacheEntry(clazz)

        val clazzName = cacheEntry?.first ?: clazz::qualifiedName.get() ?: clazz.simpleName ?: ""
        val properties = cacheEntry?.second ?: clazz::declaredMemberProperties.get().map(KProperty<*>::name).filter {
            it == "id"
        }.toSet()

        val hlc = hlcFactory().toString()
        val timestamps =  properties.fold(HashMap<String, String>(properties.size)) { acc, name ->
            acc.apply {
                put(name, hlc)
            }
        }

        if(cacheEntry == null) {
            putCacheEntry(clazz, clazzName to properties)
        }

        return Meta(
            clazzName,
            timestamps
        )
    }

}