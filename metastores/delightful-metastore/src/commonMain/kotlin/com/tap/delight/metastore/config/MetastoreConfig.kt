package com.tap.delight.metastore.config

data class MetastoreConfig(
    val cacheSize: Int,
    val warmCaches: Boolean,
)
