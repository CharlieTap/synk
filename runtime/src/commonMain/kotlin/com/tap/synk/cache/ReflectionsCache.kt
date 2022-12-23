package com.tap.synk.cache

import com.tap.synk.CMap
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible

typealias CRDTConstructor<T> = KFunction<T>
typealias ParamPropPair = Pair<KParameter, KProperty1<out Any, *>>
typealias ReflectionCacheEntry<T> = Pair<CRDTConstructor<T>, Set<ParamPropPair>>

internal class ReflectionsCache(
    private val cache: CMap<KClass<*>, ReflectionCacheEntry<Any>> = CMap()
) {

    private fun reflect(clazz: KClass<*>): ReflectionCacheEntry<Any> {
        val constructor = clazz::primaryConstructor.get()?.apply {
            isAccessible = true
        } ?: throw IllegalStateException("Failed to find primary constructor on object")

        val properties = clazz::declaredMemberProperties.get()
        val paramPropSet = constructor.parameters.map { param ->
            param to properties.first {
                it.name == param.name
            }.apply {
                isAccessible = true
            }
        }.toSet()

        return (constructor to paramPropSet).apply {
            cache.put(clazz, this)
        }
    }
    fun <T : Any> getConstructor(clazz: KClass<T>): CRDTConstructor<T> {
        val entry = cache[clazz] ?: reflect(clazz)

        return entry.first as CRDTConstructor<T>
    }

    fun getProps(clazz: KClass<*>): Set<KProperty1<out Any, *>> {
        val entry = cache[clazz] ?: reflect(clazz)

        return entry.second.map { it.second }.toSet()
    }

    fun getParamsAndProps(clazz: KClass<*>): Set<ParamPropPair> {
        val entry = cache[clazz] ?: reflect(clazz)

        return entry.second
    }
}
