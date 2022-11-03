package com.tap.synk.meta.store

import com.tap.synk.meta.store.MetaStore
import kotlin.reflect.KClass

interface MetaStoreFactory {
    fun getStore(clazz: KClass<*>) : MetaStore
}