package com.tap.synk

import com.tap.hlc.HybridLogicalClock
import com.tap.synk.adapter.ReflectionsSynkAdapter
import com.tap.synk.adapter.SynkAdapter
import com.tap.synk.cache.ReflectionsCache
import com.tap.synk.meta.MetaMonoid
import com.tap.synk.meta.store.InMemoryMetaStoreFactory
import com.tap.synk.meta.store.MetaStoreFactory
import com.tap.synk.relay.MessageMonoid
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic

class Synk(
    clock: HybridLogicalClock = HybridLogicalClock(), // TODO load from storage or newest object
    override val factory: MetaStoreFactory = InMemoryMetaStoreFactory(),
    override val synkAdapter: SynkAdapter<Any> = ReflectionsSynkAdapter(ReflectionsCache()),

) : SynkContract {
    override val hlc: AtomicRef<HybridLogicalClock> = atomic(clock)
    override val merger: MessageMonoid<Any> = MessageMonoid(synkAdapter, MetaMonoid)
}
