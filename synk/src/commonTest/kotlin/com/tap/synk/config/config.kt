package com.tap.synk.config

import com.tap.hlc.HybridLogicalClock
import com.tap.synk.CMap
import com.tap.synk.CRDT
import com.tap.synk.CRDTAdapter
import com.tap.synk.Synk
import com.tap.synk.adapter.store.SynkAdapterStore
import com.tap.synk.meta.store.InMemoryMetaStore
import com.tap.synk.meta.store.InMemoryMetaStoreFactory
import com.tap.synk.meta.store.MetaStore
import okio.Path.Companion.toPath
import okio.fakefilesystem.FakeFileSystem

internal fun storageConfig() =
    CustomClockStorageConfiguration(
        filePath = "/test".toPath(),
        fileSystem = FakeFileSystem(),
    )

internal fun setupSynk(
    storageConfiguration: ClockStorageConfiguration,
    metaStoreMap: CMap<String, String>,
    hlc: HybridLogicalClock = HybridLogicalClock(),
): Synk {
    HybridLogicalClock.store(hlc, storageConfiguration.filePath, storageConfiguration.fileSystem, storageConfiguration.clockFileName)

    val metaStore = InMemoryMetaStore(metaStoreMap)
    val metaStoreFactoryMap = HashMap<String, MetaStore>().apply {
        put(CRDT::class.qualifiedName.toString(), metaStore)
    }
    val metaStoreFactory = InMemoryMetaStoreFactory(metaStoreFactoryMap)
    val synkAdapterStore = SynkAdapterStore().apply {
        register(CRDT::class, CRDTAdapter())
    }
    return Synk(factory = metaStoreFactory, clockStorageConfiguration = storageConfiguration, synkAdapterStore = synkAdapterStore)
}
