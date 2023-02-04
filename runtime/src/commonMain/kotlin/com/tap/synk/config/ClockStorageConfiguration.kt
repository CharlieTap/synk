package com.tap.synk.config

import okio.FileSystem
import okio.Path

interface ClockStorageConfiguration {

    companion object Presets {}

    val fileSystem: FileSystem
    val filePath: Path
    val clockFileName: String

}
