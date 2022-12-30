package com.tap.synk.relay

import com.tap.hlc.HybridLogicalClock
import com.tap.synk.IDCRDT
import com.tap.synk.IDCRDTAdapter
import com.tap.synk.meta.Meta
import com.tap.synk.meta.meta
import kotlin.test.Test
import kotlin.test.assertEquals

class MessageEncodingTest {

    companion object {
        private fun json(crdt: IDCRDT, hlc: HybridLogicalClock): String {
            return """
            {
                "crdt":{
                    "phone":"${crdt.phone}",
                    "name":"${crdt.name}",
                    "last_name":"${crdt.last_name}",
                    "id":"${crdt.id}"
                },
                "meta":{
                    "clazz":"com.tap.synk.IDCRDT",
                    "timestamp_meta":{
                        "phone":"$hlc",
                        "name":"$hlc",
                        "last_name":"$hlc",
                        "id":"$hlc"
                    }
                }
            }
            """.replace("[\\s\\n\\r]".toRegex(), "")
        }
    }

    @Test
    fun `can encode message to string`() {
        val crdt = IDCRDT(
            "123",
            "Chest",
            "Prah",
            1234567
        )
        val adapter = IDCRDTAdapter()
        val hlc = HybridLogicalClock()
        val meta = meta(crdt, adapter, hlc)

        val message = Message(crdt, meta)
        val result = message.encodeAsString(adapter)

        val expected = json(crdt, hlc)

        assertEquals(expected, result)
    }

    @Test
    fun `can decode message from a string`() {
        val crdt = IDCRDT(
            "123",
            "Chest",
            "Prah",
            1234567
        )
        val hlc = HybridLogicalClock()
        val adapter = IDCRDTAdapter()

        val result = json(crdt, hlc).decodeToMessage(adapter)
        val expectedMeta = Meta(
            IDCRDT::class.qualifiedName ?: "",
            mapOf(
                "id" to hlc.toString(),
                "name" to hlc.toString(),
                "last_name" to hlc.toString(),
                "phone" to hlc.toString()
            )
        )

        assertEquals(crdt, result.crdt)
        assertEquals(expectedMeta, result.meta)
    }

    @Test
    fun `can encode a list of messages to string`() {
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
        val result = listOf(message1, message2).encodeAsString(adapter)

        val expected = "[" + json(crdt1, hlc) + "," + json(crdt2, hlc) + "]"

        assertEquals(expected, result)
    }

    @Test
    fun `can decode a list of messages from a string`() {
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
        val hlc = HybridLogicalClock()
        val adapter = IDCRDTAdapter()

        val json = "[" + json(crdt1, hlc) + "," + json(crdt2, hlc) + "]"

        val result = json.decodeToMessages(adapter)
        val expectedMeta = Meta(
            IDCRDT::class.qualifiedName ?: "",
            mapOf(
                "id" to hlc.toString(),
                "name" to hlc.toString(),
                "last_name" to hlc.toString(),
                "phone" to hlc.toString()
            )
        )

        assertEquals(crdt1, result[0].crdt)
        assertEquals(expectedMeta, result[0].meta)
        assertEquals(crdt2, result[1].crdt)
        assertEquals(expectedMeta, result[1].meta)
    }
}
