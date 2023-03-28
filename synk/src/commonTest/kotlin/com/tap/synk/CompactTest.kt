package com.tap.synk

import com.tap.hlc.HybridLogicalClock
import com.tap.hlc.Timestamp
import com.tap.synk.fake.crdt
import com.tap.synk.fake.faker
import com.tap.synk.meta.meta
import com.tap.synk.relay.Message
import com.tap.synk.utils.setupSynk
import com.tap.synk.utils.storageConfig
import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertEquals

class CompactTest {

    @Test
    fun `can compress a list of messages to their latest states`() {
        val storageConfig = storageConfig()
        val metaStoreMap = CMap<String, String>()
        val now = Timestamp.now(Clock.System)
        val later = Timestamp(now.epochMillis + (1000 * 60))
        val currentHlc = HybridLogicalClock(now)
        val laterHlc = HybridLogicalClock(later)
        val adapter = IDCRDTAdapter()
        val synk = setupSynk(storageConfig, metaStoreMap, currentHlc)

        val id1 = "123344553343"
        val id2 = "199887853343"

        val range = 0..9
        val crdtList1 = (range).map { crdt(id1) }
        val crdtList2 = (range).map { crdt(id2) }

        var message1Latest: Message<IDCRDT>? = null
        var message2Latest: Message<IDCRDT>? = null

        val randomMessageIndex = faker.random.nextInt(range)

        val messages1 = crdtList1.mapIndexed { idx, crdt ->
            if (randomMessageIndex == idx) {
                message1Latest = Message(
                    crdt,
                    meta(crdt, adapter, laterHlc)
                )
                message1Latest!!
            } else {
                Message(
                    crdt,
                    meta(crdt, adapter, currentHlc)
                )
            }
        }

        val messages2 = crdtList2.mapIndexed { idx, crdt ->
            if (randomMessageIndex == idx) {
                message2Latest = Message(
                    crdt,
                    meta(crdt, adapter, laterHlc)
                )
                message2Latest!!
            } else {
                Message(
                    crdt,
                    meta(crdt, adapter, currentHlc)
                )
            }
        }

        val result = synk.compact(messages1 + messages2)

        assertEquals(2, result.size)
        assertEquals(message1Latest, result[0])
        assertEquals(message2Latest, result[1])
    }
}
