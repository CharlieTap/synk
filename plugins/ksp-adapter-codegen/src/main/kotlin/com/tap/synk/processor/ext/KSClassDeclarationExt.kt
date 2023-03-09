package com.tap.synk.processor.ext

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.Modifier

internal fun KSClassDeclaration.isSealed(): Boolean {
    return modifiers.contains(Modifier.SEALED)
}

internal fun KSClassDeclaration.containsNestedClasses() : Boolean {
    return primaryConstructor?.parameters?.any { param ->
        val parameterType = param.type.resolve()
        val parameterDeclaration = parameterType.declaration as? KSDeclaration ?: return@any false
        parameterDeclaration.modifiers.any { it == Modifier.DATA || it == Modifier.SEALED }
    } ?: false
}

internal fun KSClassDeclaration.asType() = asType(emptyList())
