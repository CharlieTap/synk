package com.tap.synk

import com.tap.hlc.HybridLogicalClock
import com.tap.synk.encode.decodeToHashmap
import com.tap.synk.encode.encodeToString
import com.tap.synk.meta.Meta
import com.tap.synk.relay.Message
import com.tap.synk.utils.setupSynk
import com.tap.synk.utils.storageConfig
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OutboundTest {

    @Test
    fun `calling outbound with old as null returns a correctly formed Message`() {
        val storageConfig = storageConfig()
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
            put("id", expectedHLC.toString())
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
        val storageConfig = storageConfig()
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
        val storageConfig = storageConfig()
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
