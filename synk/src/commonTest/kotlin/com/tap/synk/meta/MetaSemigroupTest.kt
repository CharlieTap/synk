package com.tap.synk.meta

import com.tap.hlc.HybridLogicalClock
import com.tap.hlc.Timestamp
import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertEquals

class MetaSemigroupTest {

    @Test
    fun `can combine instances of meta`() {
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
            },
        )
        val meta2 = Meta(
            "test",
            HashMap<String, String>().apply {
                put("name", laterHlc.toString())
                put("phone", hlc.toString())
            },
        )

        val result = MetaSemigroup.combine(meta1, meta2)
        val expected = Meta(
            "test",
            HashMap<String, String>().apply {
                put("name", laterHlc.toString())
                put("last_name", hlc.toString())
                put("phone", laterHlc.toString())
            },
        )

        assertEquals(expected, result)
    }
}
