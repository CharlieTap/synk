package com.tap.synk

import com.tap.hlc.HybridLogicalClock
import com.tap.synk.cache.ReflectionCacheEntry
import com.tap.synk.config.StorageConfiguration
import com.tap.synk.encode.decodeToHashmap
import com.tap.synk.encode.encodeToString
import com.tap.synk.meta.Meta
import com.tap.synk.meta.store.InMemoryMetaStore
import com.tap.synk.meta.store.InMemoryMetaStoreFactory
import com.tap.synk.meta.store.MetaStore
import com.tap.synk.relay.Message
import okio.Path.Companion.toPath
import okio.fakefilesystem.FakeFileSystem
import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OutboundTest {

    private val storageConfig by lazy {
        StorageConfiguration(
            filePath = "/test".toPath(),
            fileSystem = FakeFileSystem()
        )
    }

    private fun setupSynk(
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
        return Synk(factory = metaStoreFactory, storageConfiguration = storageConfiguration)
    }

    @Test
    fun `calling outbound with old as null returns a correctly formed Message`() {
        val metaStoreMap = CMap<String, String>()
        val currentHlc = HybridLogicalClock()
        val synk = setupSynk(storageConfig, metaStoreMap, currentHlc)

        val newCRDT = IDCRDT(
            "123",
            "Jim",
            "Jones",
            123344433
        )

        val result = synk.outbound(newCRDT)
        val expectedHLC = synk.hlc.value
        val expectedMetaMap = HashMap<String, String>().apply {
            put("name", expectedHLC.toString())
            put("last_name", expectedHLC.toString())
            put("phone", expectedHLC.toString())
        }
        val expectedMeta = Meta(
            IDCRDT::class.qualifiedName.toString(),
            expectedMetaMap
        )
        val expectedMessage = Message(newCRDT, expectedMeta)

        assertEquals(expectedMessage, result)
        assertTrue(expectedHLC > currentHlc)
        assertEquals(expectedHLC.node.toString(), currentHlc.node.toString())
        assertEquals(expectedMetaMap, metaStoreMap[newCRDT.id]?.decodeToHashmap())
        assertTrue(storageConfig.fileSystem.exists(storageConfig.filePath / storageConfig.clockFileName))
    }

    @Test
    fun `calling outbound with old but no meta causes a runtime crash`() {
        val metaStoreMap = CMap<String, String>()
        val currentHlc = HybridLogicalClock()
        val synk = setupSynk(storageConfig, metaStoreMap, currentHlc)

        val oldCRDT = IDCRDT(
            "123",
            "Jim",
            "Jonesss",
            123344477
        )

        val newCRDT = IDCRDT(
            "123",
            "Jim",
            "Jones",
            123344433
        )

        val result = kotlin.runCatching { synk.outbound(newCRDT, oldCRDT) }
        assertTrue(result.isFailure)
    }

    @Test
    fun `calling outbound with old correctly updates timestamps for values which changed`() {
        val cache = HashMap<KClass<*>, ReflectionCacheEntry<Any>>()
        val metaStoreMap = CMap<String, String>()
        val currentHlc = HybridLogicalClock()
        val synk = setupSynk(storageConfig, metaStoreMap, currentHlc)

        val oldCRDT = IDCRDT(
            "123",
            "Jim",
            "Jonesss",
            123344477
        )

        val oldMetaMap = HashMap<String, String>().apply {
            put("name", currentHlc.toString())
            put("last_name", currentHlc.toString())
            put("phone", currentHlc.toString())
        }

        metaStoreMap.put(oldCRDT.id, oldMetaMap.encodeToString())

        val newCRDT = IDCRDT(
            "123",
            "Jim",
            "Jones",
            123344433
        )

        val result = synk.outbound(newCRDT, oldCRDT)

        val expectedHLC = synk.hlc.value
        val expectedMetaMap = HashMap<String, String>().apply {
            put("name", currentHlc.toString())
            put("last_name", expectedHLC.toString())
            put("phone", expectedHLC.toString())
        }
        val expectedMeta = Meta(
            IDCRDT::class.qualifiedName.toString(),
            expectedMetaMap
        )
        val expectedMessage = Message(newCRDT, expectedMeta)

        assertEquals(expectedMessage, result)
        assertTrue(expectedHLC > currentHlc)
        assertEquals(expectedHLC.node.toString(), currentHlc.node.toString())
        assertEquals(expectedMetaMap, metaStoreMap[newCRDT.id]?.decodeToHashmap())
        assertTrue(storageConfig.fileSystem.exists(storageConfig.filePath / storageConfig.clockFileName))
    }
}
