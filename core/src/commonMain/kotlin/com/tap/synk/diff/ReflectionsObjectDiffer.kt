package com.tap.synk.diff

import com.tap.synk.cache.ReflectionsCache
import kotlin.reflect.jvm.isAccessible

class ReflectionsObjectDiffer(
    private val reflectionsCache: ReflectionsCache = ReflectionsCache()
) : ObjectDiffer<Any> {
    override fun diff(old: Any, new: Any): Set<String> {
        return reflectionsCache.getProps(old::class).filter { prop ->

            prop.isAccessible = true

            val oldValue = prop.getter.call(old)
            val newValue = prop.getter.call(new)

            oldValue != newValue
        }.fold(mutableSetOf()) { acc, prop ->
            acc.apply {
                add(prop.name)
            }
        }
    }
}
