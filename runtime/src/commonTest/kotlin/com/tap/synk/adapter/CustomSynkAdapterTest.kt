package com.tap.synk.adapter

import com.tap.synk.adapter.store.SynkAdapterStore
import com.tap.synk.cache.ReflectionsCache
import kotlin.test.Test
import kotlin.test.assertEquals

class CustomSynkAdapterTest {

    private data class CRDT1(val id: String)
    private data class CRDT2(val id: String)
    private data class CRDT3(val id: String)

    private class CRDT1Adapter() : SynkAdapter<CRDT1> {
        override fun encode(crdt: CRDT1): Map<String, String> {
            TODO("Not yet implemented")
        }

        override fun decode(map: Map<String, String>): CRDT1 {
            TODO("Not yet implemented")
        }

        override fun resolveId(crdt: CRDT1): String {
            TODO("Not yet implemented")
        }
    }

    private class CRDT2Adapter : SynkAdapter<CRDT2> {
        override fun encode(crdt: CRDT2): Map<String, String> {
            TODO("Not yet implemented")
        }

        override fun decode(map: Map<String, String>): CRDT2 {
            TODO("Not yet implemented")
        }

        override fun resolveId(crdt: CRDT2): String {
            TODO("Not yet implemented")
        }
    }

    @Test
    fun `can resolve correct adapter by generic type`() {
//        val reflectionsSynkAdapter = ReflectionsSynkAdapter(ReflectionsCache())
//        val adapterStore = SynkAdapterStore(reflectionsSynkAdapter)
        val adapterStore = SynkAdapterStore()
        val adapter1 = CRDT1Adapter()
        val adapter2 = CRDT2Adapter()
        adapterStore.register(CRDT1::class, adapter1)
        adapterStore.register(CRDT2::class, adapter2)

        val resultAdapter = adapterStore.resolve(CRDT1::class)
        assertEquals(resultAdapter.hashCode(), adapter1.hashCode())

        val resultAdapter2 = adapterStore.resolve(CRDT2::class)
        assertEquals(resultAdapter2.hashCode(), adapter2.hashCode())

//        val resultAdapter3 = adapterStore.resolve(CRDT3::class)
//        assertEquals(resultAdapter3.hashCode(), reflectionsSynkAdapter.hashCode())
    }
}
