package com.tap.synk.adapter.store

import com.tap.synk.adapter.ReflectionsSynkAdapter
import com.tap.synk.adapter.SynkAdapter
import com.tap.synk.cache.ReflectionsCache
import kotlin.reflect.KClass

class SynkAdapterStore(
    private val default: SynkAdapter<Any> = ReflectionsSynkAdapter(ReflectionsCache()),
    private val lookup: HashMap<KClass<*>, SynkAdapter<Any>> = HashMap()
) {
    fun <T : Any> register(clazz: KClass<T>, adapter: SynkAdapter<T>) {
        lookup[clazz] = (adapter as SynkAdapter<Any>)
    }

    fun <T : Any> resolve(clazz: KClass<T>): SynkAdapter<Any> {
        return lookup[clazz] ?: default
    }
}
