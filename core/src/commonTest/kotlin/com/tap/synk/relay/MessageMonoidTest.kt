package com.tap.synk.relay

import com.tap.hlc.HybridLogicalClock
import com.tap.hlc.Timestamp
import com.tap.synk.cache.ReflectionsCache
import com.tap.synk.meta.Meta
import com.tap.synk.meta.MetaMonoid
import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertEquals

class MessageMonoidTest {

    @Test
    fun `can combine instances of messages`() {
        val now = Timestamp.now(Clock.System)
        val futureTimestamp = Timestamp(now.epochMillis + (1000 * 60))

        val hlc = HybridLogicalClock()
        val laterHlc = HybridLogicalClock(futureTimestamp)

        val meta1 = Meta(
            "test",
            HashMap<String, String>().apply {
                put("name", hlc.toString())
                put("last_name", hlc.toString())
                put("phone", laterHlc.toString())
            }
        )

        val meta2 = Meta(
            "test",
            HashMap<String, String>().apply {
                put("name", laterHlc.toString())
                put("phone", hlc.toString())
            }
        )

        val crdt1 = CRDT(
            "jim",
            "smith",
            12345678
        )

        val crdt2 = CRDT(
            "bob",
            "jones",
            23456789
        )

        val message1 = Message(crdt1, meta1)
        val message2 = Message(crdt2, meta2)

        val result = MessageMonoid<Any>(ReflectionsCache(), MetaMonoid).combine(message1, message2)
        val expectedCrdt = CRDT(
            "bob",
            "smith",
            12345678
        )
        val expectedMeta = Meta(
            "test",
            HashMap<String, String>().apply {
                put("name", laterHlc.toString())
                put("last_name", hlc.toString())
                put("phone", laterHlc.toString())
            }
        )

        assertEquals(Message(expectedCrdt, expectedMeta), result)
    }
}

private data class CRDT(
    val name: String,
    val last_name: String,
    val phone: Int,
)
