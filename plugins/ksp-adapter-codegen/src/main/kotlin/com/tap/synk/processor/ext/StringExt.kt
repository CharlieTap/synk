package com.tap.synk.processor.ext

import java.util.Locale

internal fun String.capitalise(): String {
    return replaceFirstChar { it.uppercase(Locale.getDefault()) }
}

internal fun String.decapitalise(): String {
    return replaceFirstChar { it.lowercase(Locale.getDefault()) }
}
