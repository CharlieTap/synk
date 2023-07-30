package com.tap.delight.metastore.schema

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlDriver

class SqliteSchemaInitializer(
    private val createSchema: (SqlDriver) -> Unit,
) : SchemaInitializer {

    companion object {
        private fun getSchemaVersion(driver: SqlDriver): Long {
            val mapper = { cursor: SqlCursor ->
                cursor.next()
                QueryResult.Value(cursor.getLong(0) ?: 0)
            }

            return kotlin.runCatching {
                driver.executeQuery(null, "PRAGMA user_version;", mapper, 0, null).value
            }.getOrDefault(0)
        }

        private fun setSchemaVersion(driver: SqlDriver, version: Long) {
            kotlin.runCatching {
                driver.execute(null, String.format("PRAGMA user_version = %d;", version), 0, null)
            }
        }

        private fun schemaHasBeenCreated(driver: SqlDriver): Boolean {
            return getSchemaVersion(driver) > 0
        }

        private fun markSchemaAsCreated(driver: SqlDriver) {
            setSchemaVersion(driver, 1)
        }
    }

    override fun init(sqlDriver: SqlDriver) {
        if (!schemaHasBeenCreated(sqlDriver)) {
            createSchema(sqlDriver)
            markSchemaAsCreated(sqlDriver)
        }
    }
}
