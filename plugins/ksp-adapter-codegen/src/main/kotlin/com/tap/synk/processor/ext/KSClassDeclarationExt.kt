package com.tap.synk.processor.ext

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Modifier

internal fun KSClassDeclaration.isSealed(): Boolean {
    return modifiers.contains(Modifier.SEALED)
}

internal fun KSClassDeclaration.asType() = asType(emptyList())
