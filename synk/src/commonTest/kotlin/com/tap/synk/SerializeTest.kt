package com.tap.synk

import com.tap.hlc.HybridLogicalClock
import com.tap.hlc.Timestamp
import com.tap.synk.config.setupSynk
import com.tap.synk.config.storageConfig
import com.tap.synk.meta.meta
import com.tap.synk.relay.Message
import com.tap.synk.relay.MessageEncodingTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.datetime.Clock

class SerializeTest {

    @Test
    fun `can serialize a list of messages to a json string using a provided synk adapter`() {
        val storageConfig = storageConfig()
        val metaStoreMap = CMap<String, String>()
        val now = Timestamp.now(Clock.System)
        val currentHlc = HybridLogicalClock(now)
        val synk = setupSynk(storageConfig, metaStoreMap, currentHlc)

        val crdt1 = CRDT(
            "123",
            "Chest",
            "Prah",
            1234567
        )
        val crdt2 = CRDT(
            "234",
            "Yaboy",
            "Dave",
            1234567
        )
        val adapter = CRDTAdapter()
        val hlc = HybridLogicalClock()
        val meta1 = meta(crdt1, adapter, hlc)
        val meta2 = meta(crdt2, adapter, hlc)

        val message1 = Message(crdt1, meta1)
        val message2 = Message(crdt2, meta2)

        val result = synk.serialize(listOf(message1, message2))
        val expected = "[" + MessageEncodingTest.json(crdt1, hlc) + "," + MessageEncodingTest.json(crdt2, hlc) + "]"

        assertEquals(expected, result)
    }

    @Test
    fun `can serialize a message to a json string using a provided synk adapter`() {
        val storageConfig = storageConfig()
        val metaStoreMap = CMap<String, String>()
        val now = Timestamp.now(Clock.System)
        val currentHlc = HybridLogicalClock(now)
        val synk = setupSynk(storageConfig, metaStoreMap, currentHlc)

        val crdt1 = CRDT(
            "123",
            "Chest",
            "Prah",
            1234567
        )

        val adapter = CRDTAdapter()
        val hlc = HybridLogicalClock()
        val meta1 = meta(crdt1, adapter, hlc)
        val message1 = Message(crdt1, meta1)

        val result = synk.serializeOne(message1)
        val expected = MessageEncodingTest.json(crdt1, hlc)

        assertEquals(expected, result)
    }
}
