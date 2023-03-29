package com.tap.synk.relay

import com.tap.hlc.HybridLogicalClock
import com.tap.hlc.Timestamp
import com.tap.synk.CRDT
import com.tap.synk.CRDTAdapter
import com.tap.synk.adapter.store.SynkAdapterStore
import com.tap.synk.meta.Meta
import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertEquals

class MessageSemigroupTest {

    @Test
    fun `can combine instances of messages`() {
        val now = Timestamp.now(Clock.System)
        val futureTimestamp = Timestamp(now.epochMillis + (1000 * 60))

        val hlc = HybridLogicalClock()
        val laterHlc = HybridLogicalClock(futureTimestamp)
        val synkAdapterStore = SynkAdapterStore().apply {
            register(CRDT::class, CRDTAdapter())
        }
        val monoid = MessageSemigroup<Any>(synkAdapterStore)

        val meta1 = Meta(
            "test",
            HashMap<String, String>().apply {
                put("id", hlc.toString())
                put("name", hlc.toString())
                put("last_name", hlc.toString())
                put("phone", laterHlc.toString())
            }
        )

        val meta2 = Meta(
            "test",
            HashMap<String, String>().apply {
                put("id", hlc.toString())
                put("name", laterHlc.toString())
                put("phone", hlc.toString())
            }
        )

        val crdt1 = CRDT(
            "123",
            "jim",
            "smith",
            12345678
        )

        val crdt2 = CRDT(
            "123",
            "bob",
            "jones",
            23456789
        )

        val message1 = Message(crdt1, meta1)
        val message2 = Message(crdt2, meta2)

        val result = monoid.combine(message1, message2)
        val expectedCrdt = CRDT(
            "123",
            "bob",
            "smith",
            12345678
        )
        val expectedMeta = Meta(
            "test",
            HashMap<String, String>().apply {
                put("id", hlc.toString())
                put("name", laterHlc.toString())
                put("last_name", hlc.toString())
                put("phone", laterHlc.toString())
            }
        )

        assertEquals(Message(expectedCrdt, expectedMeta), result)
    }

    @Test
    fun `can combine instances of messages where a nullable parameter is made nullable `() {
        val now = Timestamp.now(Clock.System)
        val futureTimestamp = Timestamp(now.epochMillis + (1000 * 60))

        val hlc = HybridLogicalClock()
        val laterHlc = HybridLogicalClock(futureTimestamp)
        val synkAdapterStore = SynkAdapterStore().apply {
            register(CRDT::class, CRDTAdapter())
        }
        val monoid = MessageSemigroup<Any>(synkAdapterStore)

        val meta1 = Meta(
            "test",
            HashMap<String, String>().apply {
                put("id", hlc.toString())
                put("name", hlc.toString())
                put("last_name", hlc.toString())
                put("phone", laterHlc.toString())
            }
        )

        val meta2 = Meta(
            "test",
            HashMap<String, String>().apply {
                put("id", hlc.toString())
                put("name", laterHlc.toString())
                put("phone", hlc.toString())
            }
        )

        val crdt1 = CRDT(
            "123",
            "jim",
            "smith",
            null
        )

        val crdt2 = CRDT(
            "123",
            "bob",
            "jones",
            23456789
        )

        val message1 = Message(crdt1, meta1)
        val message2 = Message(crdt2, meta2)

        val result = monoid.combine(message1, message2)
        val expectedCrdt = CRDT(
            "123",
            "bob",
            "smith",
            null
        )
        val expectedMeta = Meta(
            "test",
            HashMap<String, String>().apply {
                put("id", hlc.toString())
                put("name", laterHlc.toString())
                put("last_name", hlc.toString())
                put("phone", laterHlc.toString())
            }
        )

        assertEquals(Message(expectedCrdt, expectedMeta), result)
    }
}
