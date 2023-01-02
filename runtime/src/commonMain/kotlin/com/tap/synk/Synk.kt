package com.tap.synk

import com.tap.hlc.HybridLogicalClock
import com.tap.synk.adapter.SynkAdapter
import com.tap.synk.adapter.store.SynkAdapterStore
import com.tap.synk.config.StorageConfiguration
import com.tap.synk.meta.store.InMemoryMetaStoreFactory
import com.tap.synk.meta.store.MetaStoreFactory
import com.tap.synk.relay.MessageSemigroup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlin.reflect.KClass
import kotlin.time.Duration.Companion.milliseconds

class Synk internal constructor(
    val storageConfiguration: StorageConfiguration,
    val factory: MetaStoreFactory = InMemoryMetaStoreFactory(),
    val synkAdapterStore: SynkAdapterStore = SynkAdapterStore()
) {
    internal val hlc: MutableStateFlow<HybridLogicalClock> = MutableStateFlow(loadClock())
    private val hlcSynk: Flow<HybridLogicalClock> = hlc.debounce(200.milliseconds)
    internal val merger: MessageSemigroup<Any> = MessageSemigroup(synkAdapterStore)

    init {
        GlobalScope.launch(Dispatchers.IO) {
            hlcSynk.collectLatest { hlc ->
                storeClock(hlc)
            }
        }
    }

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
