package com.tap.synk


import com.tap.hlc.HybridLogicalClock
import com.tap.hlc.Timestamp
import com.tap.synk.cache.ReflectionCacheEntry
import com.tap.synk.cache.ReflectionsCache
import com.tap.synk.meta.Meta
import com.tap.synk.meta.store.InMemoryMetaStore
import com.tap.synk.meta.store.InMemoryMetaStoreFactory
import com.tap.synk.meta.store.MetaStore
import com.tap.synk.meta.store.decodeToHashmap
import com.tap.synk.meta.store.encodeToString
import com.tap.synk.relay.Message
import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.datetime.Clock

class InboundTest {

    private fun setupSynk(rCache: HashMap<KClass<*>, ReflectionCacheEntry<Any>>, metaStoreMap: HashMap<String, String>, hlc : HybridLogicalClock = HybridLogicalClock()) : SynkContract {
        val reflectionsCache = ReflectionsCache(rCache)
        val metaStore = InMemoryMetaStore(metaStoreMap)
        val metaStoreFactoryMap = HashMap<String, MetaStore>().apply {
            put(IDCRDT::class.qualifiedName.toString(), metaStore)
        }
        val metaStoreFactory = InMemoryMetaStoreFactory(metaStoreFactoryMap)
        return Synk(cache = reflectionsCache, factory = metaStoreFactory, hlc = hlc)
    }

    @Test
    fun `calling inbound with old as null uses the timestamps`() {
        val cache = HashMap<KClass<*>, ReflectionCacheEntry<Any>>()
        val metaStoreMap = HashMap<String, String>()
        val synk = setupSynk(cache, metaStoreMap)

        val currentHlc = HybridLogicalClock()
        val newCRDT = IDCRDT(
            "123",
            "Jim",
            "Jones",
            123344433
        )
        val newMetaMap = HashMap<String, String>().apply {
            put("name", currentHlc.toString())
            put("last_name", currentHlc.toString())
            put("phone", currentHlc.toString())
        }
        val newMeta = Meta(
            IDCRDT::class.qualifiedName.toString(),
            newMetaMap
        )
        val newMessage = Message(newCRDT, newMeta)

        val syncedCRDT = synk.inbound(newMessage)

        assertEquals(newCRDT, syncedCRDT)
        assertEquals(1, cache.entries.size)
        assertEquals(newMetaMap, metaStoreMap[newCRDT.id]?.decodeToHashmap())
    }

    @Test
    fun `calling inbound with old as previous value correctly diffs`() {
        val cache = HashMap<KClass<*>, ReflectionCacheEntry<Any>>()
        val metaStoreMap = HashMap<String, String>()
        val now = Timestamp.now(Clock.System)
        val currentHlc = HybridLogicalClock(now)
        val synk = setupSynk(cache, metaStoreMap, currentHlc)


        val futureHlc = HybridLogicalClock(Timestamp(now.epochMillis + (1000 * 60)))
        val oldCRDT = IDCRDT(
            "123",
            "Jimmy",
            "Jones",
            123344438
        )
        val oldMetaMap = HashMap<String, String>().apply {
            put("name", currentHlc.toString())
            put("last_name", currentHlc.toString())
            put("phone", futureHlc.toString())
        }
        val newCRDT = IDCRDT(
            "123",
            "Jim",
            "Jones",
            123344433
        )
        val newMetaMap = HashMap<String, String>().apply {
            put("name", futureHlc.toString())
            put("last_name", currentHlc.toString())
            put("phone", currentHlc.toString())
        }
        val newMeta = Meta(
            IDCRDT::class.qualifiedName.toString(),
            newMetaMap
        )
        val newMessage = Message(newCRDT, newMeta)

        metaStoreMap[oldCRDT.id] = oldMetaMap.encodeToString()
        val syncedCRDT = synk.inbound(newMessage, oldCRDT)

        val expectedCRDT = IDCRDT(
            "123",
            "Jim",
            "Jones",
            123344438
        )
        val expectedMeta = HashMap<String, String>().apply {
            put("name", futureHlc.toString())
            put("last_name", currentHlc.toString())
            put("phone", futureHlc.toString())
        }

        assertEquals(expectedCRDT, syncedCRDT)
        assertEquals(1, cache.entries.size)
        assertEquals(expectedMeta, metaStoreMap[newCRDT.id]?.decodeToHashmap())
        assertTrue(synk.hlc > futureHlc)
    }


}