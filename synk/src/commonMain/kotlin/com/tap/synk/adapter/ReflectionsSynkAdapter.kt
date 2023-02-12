package com.tap.synk.adapter

import com.tap.synk.cache.ReflectionsCache
import com.tap.synk.resolver.IDResolver
import com.tap.synk.resolver.ReflectionsIDResolver
import com.tap.synk.serialize.deserialize
import com.tap.synk.serialize.serialize
import kotlin.reflect.KClass

internal class ReflectionsSynkAdapter(
    private val reflectionCache: ReflectionsCache,
    private val idResolver: IDResolver<Any> = ReflectionsIDResolver(reflectionCache)
) : SynkAdapter<Any> {

    companion object {
        private const val CLASS_KEY = "__clazz__"

        internal fun createMap(crdt: KClass<*>): HashMap<String, String> {
            return HashMap<String, String>().apply {
                put(CLASS_KEY, crdt.toString())
            }
        }
    }

    override fun resolveId(crdt: Any): String {
        return idResolver.resolveId(crdt)
    }

    override fun encode(crdt: Any): Map<String, String> {
        val properties = reflectionCache.getProps(crdt::class).toSet()

        return properties.fold(createMap(crdt::class)) { acc, prop ->
            acc.apply {
                put(prop.name, serialize(prop.getter.call(crdt)))
            }
        }
    }

    override fun decode(map: Map<String, String>): Any {
        val clazz = Class.forName(map[CLASS_KEY]).kotlin

        val constructor = reflectionCache.getConstructor(clazz)
        val paramsProps = reflectionCache.getParamsAndProps(clazz)

        return paramsProps.map { paramProp ->
            val serialClass = paramProp.first.type.classifier as KClass<*>
            deserialize(serialClass, map[paramProp.first.name] ?: "null")
        }.let { params ->
            constructor.call(*params.toTypedArray())
        }
    }
}
