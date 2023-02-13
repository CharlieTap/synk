package com.tap.delight.metastore.schema

import com.squareup.sqldelight.db.SqlDriver

interface SchemaInitializer {

    fun init(sqlDriver: SqlDriver)
}
