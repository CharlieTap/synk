package com.tap.synk

import com.tap.hlc.HybridLogicalClock
import com.tap.synk.adapter.SynkAdapter
import com.tap.synk.adapter.store.SynkAdapterStore
import com.tap.synk.config.StorageConfiguration
import com.tap.synk.meta.MetaMonoid
import com.tap.synk.meta.store.InMemoryMetaStoreFactory
import com.tap.synk.meta.store.MetaStoreFactory
import com.tap.synk.relay.MessageMonoid
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlin.reflect.KClass

class Synk internal constructor(
    val storageConfiguration: StorageConfiguration,
    val factory: MetaStoreFactory = InMemoryMetaStoreFactory(),
    val synkAdapterStore: SynkAdapterStore = SynkAdapterStore()
) {
    internal val hlc: AtomicRef<HybridLogicalClock> = atomic(loadClock())
    internal val merger: MessageMonoid<Any> = MessageMonoid(synkAdapterStore, MetaMonoid)

    data class Builder(private val storageConfiguration: StorageConfiguration) {
        private var factory: MetaStoreFactory? = null
        private var synkAdapterStore = SynkAdapterStore()

        fun <T : Any> registerSynkAdapter(clazz: KClass<T>, synkAdapter: SynkAdapter<T>) {
            synkAdapterStore.register(clazz, synkAdapter)
        }

        fun metaStoreFactory(metaStoreFactory: MetaStoreFactory) {
            factory = metaStoreFactory
        }

        fun build(): Synk {
            return Synk(
                storageConfiguration,
                factory ?: InMemoryMetaStoreFactory(),
                synkAdapterStore
            )
        }
    }
}

internal fun Synk.loadClock(): HybridLogicalClock {
    return HybridLogicalClock.load(storageConfiguration.filePath, storageConfiguration.fileSystem, storageConfiguration.clockFileName) ?: HybridLogicalClock()
}

internal fun Synk.storeClock(hybridLogicalClock: HybridLogicalClock) {
    return HybridLogicalClock.store(hybridLogicalClock, storageConfiguration.filePath, storageConfiguration.fileSystem, storageConfiguration.clockFileName)
}
