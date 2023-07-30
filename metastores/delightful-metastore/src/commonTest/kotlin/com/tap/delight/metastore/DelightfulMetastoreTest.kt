package com.tap.delight.metastore

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.benasher44.uuid.Uuid
import com.tap.delight.metastore.cache.DelightfulMemCache
import com.tap.delight.metastore.hash.Hasher
import com.tap.delight.metastore.schema.delightfulSchemaInitializer
import com.tap.synk.ext.encodeToString
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DelightfulMetastoreTest {

    private val database by lazy {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        DelightfulMetastoreFactory.createDatabase(driver, delightfulSchemaInitializer())
    }

    @BeforeTest
    fun wipeDatabase() {
        database.multistoreQueries.wipe()
    }

    @Test
    fun `putting data into metastore populates the cache and the database`() {
        val hasherResult = "result"
        val hasher = object : Hasher {
            override fun hash(plaintext: String): String {
                return hasherResult
            }
        }
        val cache = DelightfulMemCache(100)
        val namespace = "test"
        val metastore = DelightfulMetastore(database, namespace, hasher, cache)
        val id = Uuid.randomUUID().toString()
        val meta = HashMap<String, String>().apply {
            put("property", "value")
            put("property1", "value2")
        }

        metastore.putMeta(id, meta)
        val result = database.multistoreQueries.getById(id, namespace).executeAsOneOrNull()

        assertEquals(id, result?.id)
        assertEquals(namespace, result?.namespace)
        assertEquals(meta.encodeToString(), result?.data_)

        val cacheResult = cache[hasherResult]

        assertEquals(cacheResult, meta.encodeToString())
    }

    @Test
    fun `getting data from the metastore can retrieve from database`() {
        val id = Uuid.randomUUID().toString()
        val namespace = "test"
        val meta = HashMap<String, String>().apply {
            put("property", "value")
            put("property1", "value2")
        }
        val encodedMeta = meta.encodeToString()

        database.multistoreQueries.upsert(id, namespace, encodedMeta)

        val hasherResult = "result"
        val hasher = object : Hasher {
            override fun hash(plaintext: String): String {
                return hasherResult
            }
        }
        val cache = DelightfulMemCache(100)
        val metastore = DelightfulMetastore(database, namespace, hasher, cache)

        val result = metastore.getMeta(id)

        assertEquals(2, result?.size)
        result?.forEach { entry ->
            val expectedValue = meta[entry.key]
            assertEquals(expectedValue, entry.value)
        }
    }

    @Test
    fun `getting data from the metastore can retrieve from the cache`() {
        val id = Uuid.randomUUID().toString()
        val namespace = "test"
        val meta = HashMap<String, String>().apply {
            put("property", "value")
            put("property1", "value2")
        }
        val encodedMeta = meta.encodeToString()

        database.multistoreQueries.upsert(id, namespace, encodedMeta)

        val hasherResult = "result"
        val hasher = object : Hasher {
            override fun hash(plaintext: String): String {
                return hasherResult
            }
        }

        val cacheMeta = HashMap<String, String>().apply {
            put("property", "value124")
            put("property1", "value123")
            put("property2", "value345")
        }
        val cache = DelightfulMemCache(100).apply {
            put(hasherResult, cacheMeta.encodeToString())
        }
        val metastore = DelightfulMetastore(database, namespace, hasher, cache)

        val result = metastore.getMeta(id)

        assertEquals(3, result?.size)
        result?.forEach { entry ->
            val expectedValue = cacheMeta[entry.key]
            assertEquals(expectedValue, entry.value)
        }
    }

    @Test
    fun `updating meta causes both the cache and database to change`() {
        val hasherResult = "result"
        val hasher = object : Hasher {
            override fun hash(plaintext: String): String {
                return hasherResult
            }
        }
        val cache = DelightfulMemCache(100)
        val namespace = "test"
        val metastore = DelightfulMetastore(database, namespace, hasher, cache)
        val id = Uuid.randomUUID().toString()
        val meta = HashMap<String, String>().apply {
            put("property", "value")
            put("property1", "value2")
        }

        metastore.putMeta(id, meta)

        val updatedMeta = HashMap<String, String>().apply {
            put("property", "value124")
            put("property1", "value123")
            put("property2", "value345")
        }

        metastore.putMeta(id, updatedMeta)

        val result = database.multistoreQueries.getById(id, namespace).executeAsOneOrNull()

        assertEquals(id, result?.id)
        assertEquals(namespace, result?.namespace)
        assertEquals(updatedMeta.encodeToString(), result?.data_)

        val cacheResult = cache[hasherResult]

        assertEquals(cacheResult, updatedMeta.encodeToString())
    }
}
