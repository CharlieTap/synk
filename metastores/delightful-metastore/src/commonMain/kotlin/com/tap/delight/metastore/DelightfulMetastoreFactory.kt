package com.tap.delight.metastore

import app.cash.sqldelight.db.SqlDriver
import com.goncalossilva.murmurhash.MurmurHash3
import com.tap.delight.metastore.cache.DelightfulMemCache
import com.tap.delight.metastore.cache.MemCache
import com.tap.delight.metastore.config.MetastoreConfig
import com.tap.delight.metastore.hash.Hasher
import com.tap.delight.metastore.hash.MurmurHasher
import com.tap.delight.metastore.schema.SchemaInitializer
import com.tap.delight.metastore.schema.delightfulSchemaInitializer
import com.tap.synk.meta.store.MetaStore
import com.tap.synk.meta.store.MetaStoreFactory
import kotlin.reflect.KClass

/**
 * Factory class responsible for construction of a Metastore
 */
class DelightfulMetastoreFactory(
    private val driver: SqlDriver,
    private val config: MetastoreConfig? = null,
    private val hasher: Hasher = MurmurHasher(MurmurHash3()),
    private val memCache: MemCache<String, String> = DelightfulMemCache(config?.cacheSize ?: DEFAULT_CACHE_SIZE),
    private val schemaInitializer: SchemaInitializer = delightfulSchemaInitializer(),
    private val storeCache: HashMap<String, MetaStore> = HashMap(),
) : MetaStoreFactory {

    companion object {

        private const val DEFAULT_CACHE_SIZE = 1000

        fun clazzNameToString(clazz: KClass<*>, hasher: Hasher): String {
            val clazzName = clazz.qualifiedName ?: clazz.simpleName ?: throw IllegalStateException(
                "Unable to generate metastore, given class has no name",
            )
            return hasher.hash(clazzName)
        }

        internal fun createDatabase(driver: SqlDriver, schemaInitializer: SchemaInitializer): DelightfulDatabase {
            schemaInitializer.init(driver)

            return DelightfulDatabase(driver)
        }
    }

    private val database: DelightfulDatabase = createDatabase(driver, schemaInitializer)

    private fun createMetastore(key: String): MetaStore {
        return DelightfulMetastore(database, key, hasher, memCache).apply {
            if (config?.warmCaches == true) {
                warm()
            }
        }
    }

    override fun getStore(clazz: KClass<*>): MetaStore {
        val key = clazzNameToString(clazz, hasher)
        return storeCache[key] ?: createMetastore(key)
    }
}
