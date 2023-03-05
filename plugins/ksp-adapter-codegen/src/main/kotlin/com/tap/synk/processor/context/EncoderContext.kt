package com.tap.synk.processor.context

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ksp.toClassName
import com.tap.synk.processor.ext.asType

internal data class EncoderContext(
    private val processorContext: ProcessorContext,
    private val classDeclaration: KSClassDeclaration
) : ProcessorContext by processorContext {

    val type by lazy { classDeclaration.asType() }
    val typeName by lazy { type.toClassName() }

    val packageName by lazy { classDeclaration.packageName.asString() }
    val declarationName by lazy { classDeclaration.simpleName.asString() }

    // FooMapEncoder
    val className by lazy { ClassName(classDeclaration.packageName.asString(), classDeclaration.simpleName.asString() + "MapEncoder") }

    val isSealed by lazy { classDeclaration.modifiers.contains(Modifier.SEALED) }
    val sealedSubClasses by lazy { classDeclaration.getSealedSubclasses().toList() }
    val parameters by lazy { classDeclaration.primaryConstructor?.parameters ?: emptyList() }
}
