package com.tap.synk

import com.tap.hlc.HybridLogicalClock
import com.tap.synk.adapter.SynkAdapter
import com.tap.synk.config.StorageConfiguration
import com.tap.synk.meta.store.MetaStoreFactory
import com.tap.synk.relay.MessageMonoid
import kotlinx.atomicfu.AtomicRef

interface SynkContract {
    val hlc: AtomicRef<HybridLogicalClock>
    val factory: MetaStoreFactory
    val merger: MessageMonoid<Any>
    val synkAdapter: SynkAdapter<Any>
    val storageConfiguration: StorageConfiguration
}

internal fun SynkContract.loadClock(): HybridLogicalClock {
    return HybridLogicalClock.load(storageConfiguration.filePath, storageConfiguration.fileSystem, storageConfiguration.clockFileName) ?: HybridLogicalClock()
}

internal fun SynkContract.storeClock(hybridLogicalClock: HybridLogicalClock) {
    return HybridLogicalClock.store(hybridLogicalClock, storageConfiguration.filePath, storageConfiguration.fileSystem, storageConfiguration.clockFileName)
}
