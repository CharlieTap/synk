package com.tap.synk.transformer

import com.tap.hlc.HybridLogicalClock
import kotlin.test.Test
import com.tap.synk.meta.transformer.ReflectionsMetaTransformer
import kotlin.test.assertEquals


class ReflectionsMetaTransformerTest {

    @Test
    fun `can turn object into meta`() {

        val hlc = HybridLogicalClock()
        val factory = {
            hlc
        }

        val transformer = ReflectionsMetaTransformer(factory)

        val crdt = CRDT("", "", "")
        val meta = transformer.toMeta(crdt)


        val expected = HashMap<String, String>().apply {
            val hlcs = hlc.toString()
            put("name", hlcs)
            put("secondName", hlcs)
            put("thirdName", hlcs)
        }

        assertEquals("com.tap.synk.transformer.CRDT", meta.clazz)
        assertEquals(expected, meta.timestampMeta)
    }


}

private data class CRDT(
    private val name: String,
    private val secondName : String,
    private val thirdName : String,
)