package com.tap.synk.config

import android.content.Context
import com.tap.synk.Synk

fun Synk.Builder.Presets.Android(context: Context): Synk.Builder {
    return Synk.Builder(ClockStorageConfiguration.Android(context))
}
