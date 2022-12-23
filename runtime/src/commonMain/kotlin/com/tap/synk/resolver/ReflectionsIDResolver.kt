package com.tap.synk.resolver

import com.tap.synk.cache.ReflectionsCache

internal class ReflectionsIDResolver(
    private val reflectionsCache: ReflectionsCache = ReflectionsCache()
) : IDResolver<Any> {

    override fun resolveId(crdt: Any): String? {
        val idProp = reflectionsCache.getProps(crdt::class).firstOrNull {
            it.name.lowercase() == "id"
        } ?: return null

        return idProp.getter.call(crdt)?.toString()
    }
}
