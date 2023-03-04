package com.tap.synk.processor.context

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.ksp.toClassName
import com.tap.synk.processor.ext.asType

/**
 * MapEncoder FileSpecs are derived from one class declaration
 * EncoderContext propagates ProcessorContext and offers class declaration derived values which are common throughout the FileSpec
 */
internal data class EncoderContext(
    private val processorContext: ProcessorContext,
    private val classDeclaration: KSClassDeclaration
) {
    val symbols = processorContext.symbols
    val poetTypes = processorContext.poetTypes
    val logger = processorContext.logger

    val type by lazy { classDeclaration.asType() }
    val typeName by lazy { type.toClassName() }

    val packageName by lazy { classDeclaration.packageName.asString() }
    val declarationName by lazy { classDeclaration.simpleName.asString() }
    val fileName by lazy { declarationName + "MapEncoder" }

    val isSealed by lazy { classDeclaration.modifiers.contains(Modifier.SEALED) }
    val sealedSubClasses by lazy { classDeclaration.getSealedSubclasses().toList() }
    val parameters by lazy { classDeclaration.primaryConstructor?.parameters ?: emptyList() }
}
