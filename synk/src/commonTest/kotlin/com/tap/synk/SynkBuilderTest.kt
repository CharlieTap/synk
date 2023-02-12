package com.tap.synk

import com.tap.synk.adapter.SynkAdapter
import com.tap.synk.config.ClockStorageConfiguration
import com.tap.synk.config.CustomClockStorageConfiguration
import com.tap.synk.meta.store.InMemoryMetaStoreFactory
import kotlin.test.Test
import kotlin.test.assertEquals
import okio.Path.Companion.toPath
import okio.fakefilesystem.FakeFileSystem

class SynkBuilderTest {

    @Test
    fun `calling build on synk builder produces a valid synk instance`() {

        val storageConfiguration = CustomClockStorageConfiguration(
            filePath = "/test".toPath(),
            fileSystem = FakeFileSystem()
        )
        val factory = InMemoryMetaStoreFactory()
        val adapter = IDCRDTAdapter()

        val synk = Synk.Builder(storageConfiguration)
            .registerSynkAdapter(adapter)
            .metaStoreFactory(factory)
            .build()

        assertEquals(factory, synk.factory)
        assertEquals(adapter, synk.synkAdapterStore.resolve(IDCRDT::class) as IDCRDTAdapter)
    }

    private sealed interface SealedTest {
        object Test1 : SealedTest
        object Test2 : SealedTest
    }

    private class SealedTestAdapter : SynkAdapter<SealedTest> {
        override fun encode(crdt: SealedTest): Map<String, String> {
            TODO("Not yet implemented")
        }

        override fun decode(map: Map<String, String>): SealedTest {
            TODO("Not yet implemented")
        }

        override fun resolveId(crdt: SealedTest): String {
            TODO("Not yet implemented")
        }
    }


    @Test
    fun `registering an adapter for a sealed class registers the adapter for all the nested subclasses`() {

        val storageConfiguration = CustomClockStorageConfiguration(
            filePath = "/test".toPath(),
            fileSystem = FakeFileSystem()
        )
        val factory = InMemoryMetaStoreFactory()
        val adapter = SealedTestAdapter()

        val synk = Synk.Builder(storageConfiguration)
            .registerSynkAdapter(adapter)
            .metaStoreFactory(factory)
            .build()

        assertEquals(factory, synk.factory)
        assertEquals(adapter, synk.synkAdapterStore.resolve(SealedTest::class) as SealedTestAdapter)
        assertEquals(adapter, synk.synkAdapterStore.resolve(SealedTest.Test1::class) as SealedTestAdapter)
        assertEquals(adapter, synk.synkAdapterStore.resolve(SealedTest.Test2::class) as SealedTestAdapter)
    }

}