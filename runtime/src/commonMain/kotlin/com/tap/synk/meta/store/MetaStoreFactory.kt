package com.tap.synk.meta.store

import kotlin.reflect.KClass

interface MetaStoreFactory {
    fun getStore(clazz: KClass<*>): MetaStore
}
