package com.tap.synk

import com.tap.hlc.HybridLogicalClock
import com.tap.hlc.Timestamp
import com.tap.synk.ext.decodeToHashmap
import com.tap.synk.ext.encodeToString
import com.tap.synk.meta.Meta
import com.tap.synk.relay.Message
import com.tap.synk.config.setupSynk
import com.tap.synk.config.storageConfig
import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InboundTest {

    @Test
    fun `calling inbound with old as null uses the timestamps found in the Message`() {
        val storageConfig = storageConfig()
        val metaStoreMap = CMap<String, String>()
        val now = Timestamp.now(Clock.System)
        val currentHlc = HybridLogicalClock(now)
        val synk = setupSynk(storageConfig, metaStoreMap, currentHlc)

        val newCRDT = CRDT(
            "123",
            "Jim",
            "Jones",
            123344433
        )
        val pastHlc = HybridLogicalClock(Timestamp(now.epochMillis - (1000 * 60)))
        val newMetaMap = HashMap<String, String>().apply {
            put("name", pastHlc.toString()) // On inbound meta timestamps are predetermined
            put("last_name", pastHlc.toString())
            put("phone", pastHlc.toString())
        }
        val newMeta = Meta(
            CRDT::class.qualifiedName.toString(),
            newMetaMap
        )
        val newMessage = Message(newCRDT, newMeta)

        val syncedCRDT = synk.inbound(newMessage)

        assertEquals(newCRDT, syncedCRDT)
        assertTrue(synk.hlc.value > currentHlc)
        assertEquals(synk.hlc.value.node.toString(), currentHlc.node.toString())
        assertEquals(newMetaMap, metaStoreMap[newCRDT.id]?.decodeToHashmap())
        assertTrue(storageConfig.fileSystem.exists(storageConfig.filePath / storageConfig.clockFileName))
    }

    @Test
    fun `calling inbound with old as previous value correctly diffs`() {
        val storageConfig = storageConfig()
        val metaStoreMap = CMap<String, String>()
        val now = Timestamp.now(Clock.System)
        val currentHlc = HybridLogicalClock(now)
        val synk = setupSynk(storageConfig, metaStoreMap, currentHlc)

        val futureHlc = HybridLogicalClock(Timestamp(now.epochMillis + (1000 * 60)))
        val oldCRDT = CRDT(
            "123",
            "Jimmy",
            "Jones",
            123344438
        )
        val oldMetaMap = HashMap<String, String>().apply {
            put("id", currentHlc.toString())
            put("name", currentHlc.toString())
            put("last_name", currentHlc.toString())
            put("phone", futureHlc.toString())
        }
        val newCRDT = CRDT(
            "123",
            "Jim",
            "Jones",
            123344433
        )
        val newMetaMap = HashMap<String, String>().apply {
            put("name", futureHlc.toString())
            put("last_name", currentHlc.toString())
            put("phone", currentHlc.toString())
        }
        val newMeta = Meta(
            CRDT::class.qualifiedName.toString(),
            newMetaMap
        )
        val newMessage = Message(newCRDT, newMeta)

        metaStoreMap.put(oldCRDT.id, oldMetaMap.encodeToString())
        val syncedCRDT = synk.inbound(newMessage, oldCRDT)

        val expectedCRDT = CRDT(
            "123",
            "Jim",
            "Jones",
            123344438
        )
        val expectedMeta = HashMap<String, String>().apply {
            put("id", currentHlc.toString())
            put("name", futureHlc.toString())
            put("last_name", currentHlc.toString())
            put("phone", futureHlc.toString())
        }

        val expectedHlc = synk.hlc.value

        assertEquals(expectedCRDT, syncedCRDT)
        assertTrue(expectedHlc > currentHlc)
        assertEquals(expectedHlc.node.toString(), currentHlc.node.toString())
        assertEquals(expectedMeta, metaStoreMap[newCRDT.id]?.decodeToHashmap())
        assertTrue(expectedHlc > futureHlc)
        assertTrue(storageConfig.fileSystem.exists(storageConfig.filePath / storageConfig.clockFileName))
    }
}
