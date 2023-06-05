package com.tap.delight.metastore

import com.benasher44.uuid.Uuid
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import com.tap.delight.metastore.cache.DelightfulMemCache
import com.tap.delight.metastore.config.MetastoreConfig
import com.tap.delight.metastore.hash.MurmurHasher
import com.tap.delight.metastore.schema.delightfulSchemaInitializer
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DelightfulMetastoreFactoryTest {

    private class CRDT

    private val driver by lazy {
        JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
    }

    private val database by lazy {
        DelightfulMetastoreFactory.createDatabase(driver, delightfulSchemaInitializer())
    }

    @BeforeTest
    fun wipeDatabase() {
        database.multistoreQueries.wipe()
    }

    private fun seedDatabase(range: IntRange, namespace: String) {
        for (i in range) {
            database.multistoreQueries.upsert(
                Uuid.randomUUID().toString(),
                namespace,
                "",
            )
        }
    }

    @Test
    fun `can create metastore with warmed cache`() {
        val maxCacheSize = 10
        val conf = MetastoreConfig(
            maxCacheSize,
            true,
        )
        val hasher = MurmurHasher()
        val namespace = CRDT::class.qualifiedName ?: ""
        seedDatabase(1..100, hasher.hash(namespace))

        val cache = DelightfulMemCache(maxCacheSize) // Rather confusingly the conf cache size is overridden as we replace the whole cache here
        val factory = DelightfulMetastoreFactory(
            driver,
            conf,
            hasher,
            cache,
        )

        val metastore = factory.getStore(CRDT::class)

        assertEquals(maxCacheSize, cache.size())
    }

    @Test
    fun `can create metastore with unwarmed cache`() {
        val maxCacheSize = 10
        val conf = MetastoreConfig(
            maxCacheSize,
            false,
        )
        val hasher = MurmurHasher()
        val namespace = CRDT::class.qualifiedName ?: ""
        seedDatabase(1..100, hasher.hash(namespace))

        val cache = DelightfulMemCache(maxCacheSize) // Rather confusingly the conf cache size is overridden as we replace the whole cache here
        val factory = DelightfulMetastoreFactory(
            driver,
            conf,
            hasher,
            cache,
        )

        val metastore = factory.getStore(CRDT::class)

        assertEquals(0, cache.size())
    }

    @Test
    fun `can init database twice in a row`() {
        val database = DelightfulMetastoreFactory.createDatabase(driver, delightfulSchemaInitializer())
        val database2 = DelightfulMetastoreFactory.createDatabase(driver, delightfulSchemaInitializer())
    }
}
