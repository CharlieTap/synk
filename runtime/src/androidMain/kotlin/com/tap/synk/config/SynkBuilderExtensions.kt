package com.tap.synk.config

import android.content.Context
import com.tap.synk.Synk

fun Synk.Builder.Android(context: Context) : Synk.Builder {
    return Synk.Builder(ClockStorageConfiguration.Android(context))
}