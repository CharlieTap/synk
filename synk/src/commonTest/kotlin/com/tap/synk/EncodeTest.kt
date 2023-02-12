package com.tap.synk

import com.tap.hlc.HybridLogicalClock
import com.tap.hlc.Timestamp
import com.tap.synk.meta.meta
import com.tap.synk.relay.Message
import com.tap.synk.relay.MessageEncodingTest
import com.tap.synk.utils.setupSynk
import com.tap.synk.utils.storageConfig
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.datetime.Clock

class EncodeTest {

    @Test
    fun `can encode a list of messages to a json string using a provided synk adapter`() {
        val storageConfig = storageConfig()
        val metaStoreMap = CMap<String, String>()
        val now = Timestamp.now(Clock.System)
        val currentHlc = HybridLogicalClock(now)
        val synk = setupSynk(storageConfig, metaStoreMap, currentHlc)

        val crdt1 = IDCRDT(
            "123",
            "Chest",
            "Prah",
            1234567
        )
        val crdt2 = IDCRDT(
            "234",
            "Yaboy",
            "Dave",
            1234567
        )
        val adapter = IDCRDTAdapter()
        val hlc = HybridLogicalClock()
        val meta1 = meta(crdt1, adapter, hlc)
        val meta2 = meta(crdt2, adapter, hlc)

        val message1 = Message(crdt1, meta1)
        val message2 = Message(crdt2, meta2)


        val result = synk.encode(listOf(message1, message2))
        val expected = "[" + MessageEncodingTest.json(crdt1, hlc) + "," + MessageEncodingTest.json(crdt2, hlc) + "]"

        assertEquals(expected, result)
    }

    @Test
    fun `can encode a message to a json string using a provided synk adapter`() {
        val storageConfig = storageConfig()
        val metaStoreMap = CMap<String, String>()
        val now = Timestamp.now(Clock.System)
        val currentHlc = HybridLogicalClock(now)
        val synk = setupSynk(storageConfig, metaStoreMap, currentHlc)

        val crdt1 = IDCRDT(
            "123",
            "Chest",
            "Prah",
            1234567
        )

        val adapter = IDCRDTAdapter()
        val hlc = HybridLogicalClock()
        val meta1 = meta(crdt1, adapter, hlc)
        val message1 = Message(crdt1, meta1)


        val result = synk.encodeOne(message1)
        val expected = MessageEncodingTest.json(crdt1, hlc)

        assertEquals(expected, result)
    }
}
