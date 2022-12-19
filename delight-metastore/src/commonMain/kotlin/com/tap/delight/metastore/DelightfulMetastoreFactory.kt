package com.tap.delight.metastore

import com.goncalossilva.murmurhash.MurmurHash3
import com.squareup.sqldelight.db.SqlDriver
import com.tap.delight.metastore.hash.Hasher
import com.tap.delight.metastore.hash.MurmurHasher
import com.tap.synk.meta.store.MetaStore
import com.tap.synk.meta.store.MetaStoreFactory
import kotlin.reflect.KClass

/**
 * Factory class responsible for construction of a Metastore
 */
class DelightfulMetastoreFactory(
    private val driver: SqlDriver,
    private val warmCaches: Boolean = false,
    private val storeCache: HashMap<String, MetaStore> = HashMap(),
    private val hasher: Hasher = MurmurHasher(MurmurHash3())
) : MetaStoreFactory {

    companion object {
        fun clazzNameToString(clazz: KClass<*>, hasher: Hasher): String {
            val clazzName = clazz.qualifiedName ?: clazz.simpleName ?: throw IllegalStateException("Unable to generate metastore, given class has no name")
            return hasher.hash(clazzName)
        }
    }

    private fun createDatabase(driver: SqlDriver): DelightfulDatabase {
        DelightfulDatabase.Schema.create(driver)
        return DelightfulDatabase(driver)
    }

    private fun createMetastore(key: String): MetaStore {
        return DelightfulMetastore(createDatabase(driver), key, hasher).apply {
            if (warmCaches) {
                warm()
            }
        }
    }

    override fun getStore(clazz: KClass<*>): MetaStore {
        val key = clazzNameToString(clazz, hasher)
        return storeCache[key] ?: createMetastore(key)
    }
}
