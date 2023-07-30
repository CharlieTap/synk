package com.tap.delight.metastore.schema

import app.cash.sqldelight.db.SqlDriver

interface SchemaInitializer {

    fun init(sqlDriver: SqlDriver)
}
