package com.tap.synk.config

import okio.FileSystem
import okio.Path

data class CustomClockStorageConfiguration(
    override val fileSystem: FileSystem,
    override val filePath: Path,
    override val clockFileName: String = FILENAME_CLOCK,
) : ClockStorageConfiguration {
    companion object {
        const val FILENAME_CLOCK = "clock.hlc"
    }
}
