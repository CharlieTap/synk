package com.tap.synk

import com.tap.hlc.HybridLogicalClock
import com.tap.synk.adapter.SynkAdapter
import com.tap.synk.cache.ReflectionsCache
import com.tap.synk.meta.store.MetaStoreFactory
import com.tap.synk.relay.MessageMonoid
import kotlinx.atomicfu.AtomicRef

interface SynkContract {
    val hlc: AtomicRef<HybridLogicalClock>
    val factory: MetaStoreFactory
    val cache: ReflectionsCache
    val merger: MessageMonoid<Any>
    val synkAdapter: SynkAdapter<Any>
}
