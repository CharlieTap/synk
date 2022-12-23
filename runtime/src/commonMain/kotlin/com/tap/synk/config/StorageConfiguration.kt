package com.tap.synk.config

import okio.FileSystem
import okio.Path

data class StorageConfiguration(
    val fileSystem: FileSystem,
    val filePath: Path,
    val clockFileName: String = FILENAME_CLOCK
) {
    companion object {
        const val FILENAME_CLOCK = "clock.hlc"
    }
}
