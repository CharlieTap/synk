package com.tap.synk

import com.tap.synk.cache.ReflectionsCache
import com.tap.synk.meta.MetaMonoid
import com.tap.synk.meta.store.InMemoryMetaStoreFactory
import com.tap.synk.meta.store.MetaStoreFactory
import com.tap.synk.relay.MessageMonoid

object Synk {
    val factory = InMemoryMetaStoreFactory()
    val cache = ReflectionsCache()
    val merger = MessageMonoid(cache, MetaMonoid)

}
