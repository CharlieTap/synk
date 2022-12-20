package com.tap.delight.metastore.schema

import com.squareup.sqldelight.db.SqlDriver

class SqliteSchemaInitializer(
    private val createSchema: (SqlDriver) -> Unit,
): SchemaInitializer {

    companion object {
        private fun getSchemaVersion(driver: SqlDriver) : Long {
            val cursor = driver.executeQuery(null, "PRAGMA user_version;", 0, null)
            return cursor.use { it.getLong(0) ?: 0 }
        }

        private fun setSchemaVersion(driver: SqlDriver, version: Long) {
            driver.execute(null, String.format("PRAGMA user_version = %d;", version), 0, null)
        }

        private fun schemaHasBeenCreated(driver: SqlDriver) : Boolean {
            return getSchemaVersion(driver) > 0
        }

        private fun markSchemaAsCreated(driver: SqlDriver) {
            setSchemaVersion(driver, 1)
        }
    }

    override fun init(driver: SqlDriver) {
        if(!schemaHasBeenCreated(driver)) {
            createSchema(driver)
            markSchemaAsCreated(driver)
        }
    }

}