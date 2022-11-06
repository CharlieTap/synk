package com.tap.synk.meta.store

import kotlin.reflect.KClass

class InMemoryMetaStoreFactory(
    private val stores: HashMap<String, MetaStore> = HashMap()
): MetaStoreFactory {

    private fun createStore(clazz: String) : MetaStore {
        val store = InMemoryMetaStore()
        stores[clazz] = store
        return store
    }

    override fun getStore(clazz: KClass<*>): MetaStore {
        val key = clazz.qualifiedName.toString()
        return stores[key] ?: createStore(key)
    }
}