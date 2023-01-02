package com.tap.synk.adapter.store

import com.tap.synk.adapter.SynkAdapter
import kotlin.reflect.KClass

class SynkAdapterStore(
    private val lookup: HashMap<KClass<*>, SynkAdapter<Any>> = HashMap()
) {

    fun <T : Any> register(clazz: KClass<T>, adapter: SynkAdapter<T>) {
        lookup[clazz] = (adapter as SynkAdapter<Any>)
    }

    fun <T : Any> resolve(clazz: KClass<T>): SynkAdapter<Any> {
        return lookup[clazz] ?: throw IllegalStateException("No synk adapter found for given class " + clazz.qualifiedName)
    }
}
