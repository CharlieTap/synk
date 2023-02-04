package com.tap.synk.config

import android.content.Context
import okio.FileSystem
import okio.Path.Companion.toPath

fun ClockStorageConfiguration.Presets.android(
    context: Context
): ClockStorageConfiguration {
    return CustomClockStorageConfiguration(
        FileSystem.SYSTEM, context.filesDir.absolutePath.toPath()
    )
}
