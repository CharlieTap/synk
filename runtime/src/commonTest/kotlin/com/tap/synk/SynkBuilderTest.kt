package com.tap.synk

import com.tap.synk.config.ClockStorageConfiguration
import com.tap.synk.meta.store.InMemoryMetaStoreFactory
import kotlin.test.Test
import kotlin.test.assertEquals
import okio.Path.Companion.toPath
import okio.fakefilesystem.FakeFileSystem

class SynkBuilderTest {

    @Test
    fun `calling build on synk builder produces a valid synk instance`() {

        val storageConfiguration = ClockStorageConfiguration(
            filePath = "/test".toPath(),
            fileSystem = FakeFileSystem()
        )
        val factory = InMemoryMetaStoreFactory()
        val adapter = IDCRDTAdapter()

        val synk = Synk.Builder(storageConfiguration)
            .registerSynkAdapter(IDCRDT::class, adapter)
            .metaStoreFactory(factory)
            .build()

        assertEquals(factory, synk.factory)
        assertEquals(adapter, synk.synkAdapterStore.resolve(IDCRDT::class) as IDCRDTAdapter)
    }
}