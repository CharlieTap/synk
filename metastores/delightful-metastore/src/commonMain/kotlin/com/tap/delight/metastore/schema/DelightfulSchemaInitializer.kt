package com.tap.delight.metastore.schema

import com.tap.delight.metastore.DelightfulDatabase

fun delightfulSchemaInitializer(): SchemaInitializer {
    return SqliteSchemaInitializer { sqlDriver ->
        DelightfulDatabase.Schema.create(sqlDriver)
    }
}
