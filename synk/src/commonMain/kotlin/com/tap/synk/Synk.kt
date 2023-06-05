package com.tap.synk

import com.tap.hlc.HybridLogicalClock
import com.tap.synk.adapter.SynkAdapter
import com.tap.synk.adapter.store.SynkAdapterStore
import com.tap.synk.config.ClockStorageConfiguration
import com.tap.synk.meta.store.InMemoryMetaStoreFactory
import com.tap.synk.meta.store.MetaStoreFactory
import com.tap.synk.relay.MessageSemigroup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlin.reflect.KClass
import kotlin.time.Duration.Companion.milliseconds

class Synk internal constructor(
    val clockStorageConfiguration: ClockStorageConfiguration,
    val factory: MetaStoreFactory = InMemoryMetaStoreFactory(),
    val synkAdapterStore: SynkAdapterStore = SynkAdapterStore(),
) {
    internal val hlc: MutableStateFlow<HybridLogicalClock> = MutableStateFlow(loadClock())
    internal val merger: MessageSemigroup<Any> = MessageSemigroup(synkAdapterStore)

    private val hlcSynk: Flow<HybridLogicalClock> = hlc.debounce(200.milliseconds)
    private val synkScope = CoroutineScope(Dispatchers.IO)

    init {
        synkScope.launch(Dispatchers.IO) {
            hlcSynk.collectLatest { hlc ->
                storeClock(hlc)
            }
        }
    }

    fun finish() {
        synkScope.cancel()
    }

    data class Builder(private val storageConfiguration: ClockStorageConfiguration) {
        companion object Presets {}

        private var factory: MetaStoreFactory? = null

        @PublishedApi
        internal var synkAdapterStore = SynkAdapterStore()

        inline fun <reified T : Any> registerSynkAdapter(synkAdapter: SynkAdapter<T>) = apply {
            val clazz = T::class
            if (clazz.isSealed) {
                clazz.sealedSubclasses.forEach { sealedClazz ->
                    synkAdapterStore.register(sealedClazz as KClass<T>, synkAdapter)
                }
            }

            synkAdapterStore.register(clazz, synkAdapter)
        }

        fun metaStoreFactory(metaStoreFactory: MetaStoreFactory) = apply {
            factory = metaStoreFactory
        }

        fun build(): Synk {
            return Synk(
                storageConfiguration,
                factory ?: InMemoryMetaStoreFactory(),
                synkAdapterStore,
            )
        }
    }
}

internal fun Synk.loadClock(): HybridLogicalClock {
    return HybridLogicalClock.load(
        clockStorageConfiguration.filePath,
        clockStorageConfiguration.fileSystem,
        clockStorageConfiguration.clockFileName,
    ) ?: HybridLogicalClock()
}

internal fun Synk.storeClock(hybridLogicalClock: HybridLogicalClock) {
    return HybridLogicalClock.store(
        hybridLogicalClock,
        clockStorageConfiguration.filePath,
        clockStorageConfiguration.fileSystem,
        clockStorageConfiguration.clockFileName,
    )
}
