package com.tap.synk.meta.transformer

import com.tap.hlc.HybridLogicalClock
import com.tap.synk.meta.Meta
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.declaredMemberProperties

typealias ReflectionsMetaCache = HashMap<KClass<*>, Pair<String, Set<String>>>

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
    private val hlcFactory: () -> HybridLogicalClock = {HybridLogicalClock()},
    private val metaCache: ReflectionsMetaCache = HashMap(),
    private val ignoredKeys: Set<String> = setOf("id")
) : MetaTransformer<Any> {

    override fun toMeta(crdt: Any): Meta {
        val clazz = crdt::class
        val cacheEntry = metaCache[clazz]

        val clazzName = cacheEntry?.first ?: clazz::qualifiedName.get() ?: clazz.simpleName ?: ""
        val properties = cacheEntry?.second ?: clazz::declaredMemberProperties.get().map(KProperty<*>::name).filter {
            ignoredKeys.contains(it).not()
        }.toSet()

        val hlc = hlcFactory().toString()
        val timestamps = properties.fold(HashMap<String, String>(properties.size)) { acc, name ->
            acc.apply {
                put(name, hlc)
            }
        }

        if (cacheEntry == null) {
            metaCache[clazz] = clazzName to properties
        }

        return Meta(
            clazzName,
            timestamps
        )
    }
}
