package com.tap.synk.meta.transformer

import com.tap.hlc.HybridLogicalClock
import com.tap.synk.cache.ReflectionsCache
import com.tap.synk.meta.Meta
import kotlin.reflect.KProperty1

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
    private val reflectionCache: ReflectionsCache = ReflectionsCache(),
    private val ignoredKeys: Set<String> = setOf("id")
) : MetaTransformer<Any> {

    override fun toMeta(crdt: Any, hlc: HybridLogicalClock): Meta {
        val clazz = crdt::class

        val clazzName = clazz::qualifiedName.get() ?: clazz.simpleName ?: ""
        val properties = reflectionCache.getProps(clazz).map(KProperty1<out Any, *>::name).filter {
            ignoredKeys.contains(it).not()
        }.toSet()

        val hlcString = hlc.toString()
        val timestamps = properties.fold(HashMap<String, String>(properties.size)) { acc, name ->
            acc.apply {
                put(name, hlcString)
            }
        }

        return Meta(
            clazzName,
            timestamps
        )
    }
}
