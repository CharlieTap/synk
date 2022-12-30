package com.tap.synk.utils

import com.tap.hlc.HybridLogicalClock
import com.tap.synk.CMap
import com.tap.synk.IDCRDT
import com.tap.synk.IDCRDTAdapter
import com.tap.synk.Synk
import com.tap.synk.adapter.store.SynkAdapterStore
import com.tap.synk.config.StorageConfiguration
import com.tap.synk.meta.store.InMemoryMetaStore
import com.tap.synk.meta.store.InMemoryMetaStoreFactory
import com.tap.synk.meta.store.MetaStore
import okio.Path.Companion.toPath
import okio.fakefilesystem.FakeFileSystem

internal fun storageConfig() =
    StorageConfiguration(
        filePath = "/test".toPath(),
        fileSystem = FakeFileSystem()
    )

internal fun setupSynk(
    storageConfiguration: StorageConfiguration,
    metaStoreMap: CMap<String, String>,
    hlc: HybridLogicalClock = HybridLogicalClock()
): Synk {
    HybridLogicalClock.store(hlc, storageConfiguration.filePath, storageConfiguration.fileSystem, storageConfiguration.clockFileName)

    val metaStore = InMemoryMetaStore(metaStoreMap)
    val metaStoreFactoryMap = HashMap<String, MetaStore>().apply {
        put(IDCRDT::class.qualifiedName.toString(), metaStore)
    }
    val metaStoreFactory = InMemoryMetaStoreFactory(metaStoreFactoryMap)
    val synkAdapterStore = SynkAdapterStore().apply {
        register(IDCRDT::class, IDCRDTAdapter())
    }
    return Synk(factory = metaStoreFactory, storageConfiguration = storageConfiguration, synkAdapterStore = synkAdapterStore)
}