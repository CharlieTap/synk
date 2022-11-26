package com.tap.synk.config

import okio.FileSystem
import okio.Path

data class StorageConfiguration(
    val fileSystem: FileSystem,
    val filePath: Path,
    val metaStoreFileName: String = FILENAME_METASTORE,
    val clockFileName: String = FILENAME_CLOCK
) {
    companion object {
        const val FILENAME_METASTORE = "meta.bin"
        const val FILENAME_CLOCK = "clock.hlc"
    }
}
