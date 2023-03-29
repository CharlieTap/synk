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

class DeserializeTest {

    @Test
    fun `can deserialize a json encoded list of messages`() {
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

        val encoded = "[" + MessageEncodingTest.json(crdt1, hlc) + "," + MessageEncodingTest.json(crdt2, hlc) + "]"
        val result = synk.deserialize<CRDT>(encoded)

        assertEquals(listOf(message1, message2), result)
    }

    @Test
    fun `can deserialize a json encoded object to a message`() {
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

        val encoded = MessageEncodingTest.json(crdt1, hlc)
        val result = synk.deserializeOne<CRDT>(encoded)

        assertEquals(message1, result)
    }
}
