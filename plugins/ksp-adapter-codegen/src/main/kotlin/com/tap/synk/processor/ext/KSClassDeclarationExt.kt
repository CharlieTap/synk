package com.tap.synk.processor.ext

import com.google.devtools.ksp.innerArguments
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.Modifier
import com.tap.synk.processor.context.SynkSymbols

internal fun KSClassDeclaration.isSealedClass(): Boolean {
    return modifiers.contains(Modifier.SEALED)
}

internal fun KSClassDeclaration.isDataClass(): Boolean {
    return modifiers.contains(Modifier.DATA)
}

internal fun KSClassDeclaration.isValueClass(): Boolean {
    return modifiers.contains(Modifier.VALUE)
}

internal fun KSClassDeclaration.isObject(): Boolean {
    return classKind == ClassKind.OBJECT
}

internal fun KSClassDeclaration.isEnum(): Boolean {
    return classKind == ClassKind.ENUM_CLASS
}

internal fun KSClassDeclaration.containsNestedClasses(symbols: SynkSymbols) : Boolean {
    return primaryConstructor?.parameters?.any { param ->
        val parameterType = param.type.resolve()
        val parameterDeclaration = parameterType.declaration as? KSDeclaration ?: return@any false

        val includesDirectNestedClasses = parameterDeclaration.modifiers.any { it == Modifier.DATA || it == Modifier.SEALED }
        if (includesDirectNestedClasses) return@any true

        if(symbols.isCollection(parameterType)) {
            parameterType.innerArguments.first().type?.resolve()?.declaration?.modifiers?.any { it == Modifier.DATA || it == Modifier.SEALED } ?: false
        } else false
    } ?: false
}

internal fun KSClassDeclaration.asType() = asType(emptyList())
