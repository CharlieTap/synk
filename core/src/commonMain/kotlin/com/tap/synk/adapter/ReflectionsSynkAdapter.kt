package com.tap.synk.adapter

import com.tap.synk.cache.ReflectionsCache
import com.tap.synk.resolver.IDResolver
import com.tap.synk.resolver.ReflectionsIDResolver

/**

 */
class ReflectionsSynkAdapter(
    private val reflectionCache: ReflectionsCache,
    private val ignoredKeys: Set<String> = setOf("id"),
    private val idResolver: IDResolver<Any> = ReflectionsIDResolver(reflectionCache)
    // todo reflections object serializer
) : SynkAdapter<Any> {

    override fun resolveId(crdt: Any): String? {
        return idResolver.resolveId(crdt)
    }

    override fun encode(crdt: Any): HashMap<String, String> {
        val properties = reflectionCache.getProps(crdt::class).filter {
            ignoredKeys.contains(it.name).not()
        }.toSet()

        return properties.fold(HashMap(properties.size)) { acc, prop ->
            acc.apply {
                put(prop.name, prop.getter.call(crdt).toString()) // todo here we assume a property can be serialized to string and back
            }
        }
    }
}
