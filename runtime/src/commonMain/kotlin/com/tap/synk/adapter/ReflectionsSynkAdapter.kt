package com.tap.synk.adapter

import com.tap.synk.cache.ReflectionsCache
import com.tap.synk.resolver.IDResolver
import com.tap.synk.resolver.ReflectionsIDResolver
import com.tap.synk.serialize.deserialize
import com.tap.synk.serialize.serialize
import kotlin.reflect.KClass

/**

 */
internal class ReflectionsSynkAdapter(
    private val reflectionCache: ReflectionsCache,
    private val ignoredKeys: Set<String> = setOf("id"),
    private val idResolver: IDResolver<Any> = ReflectionsIDResolver(reflectionCache)
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
                put(prop.name, serialize(prop.getter.call(crdt)))
            }
        }
    }

    override fun decode(crdt: Any, map: HashMap<String, String>): Any {
        val clazz = crdt::class

        val constructor = reflectionCache.getConstructor(clazz)
        val paramsProps = reflectionCache.getParamsAndProps(clazz)

        return paramsProps.map { paramProp ->
            val serialClass = paramProp.first.type.classifier as KClass<*>
            deserialize(serialClass, map[paramProp.first.name] ?: "null") ?: paramProp.second.getter.call(crdt)
        }.let { params ->
            println(params)
            constructor.call(*params.toTypedArray())
        }
    }
}
