package com.tap.synk.config

import okio.FileSystem
import okio.Path

interface ClockStorageConfiguration {

    val fileSystem: FileSystem
    val filePath: Path
    val clockFileName: String

}
