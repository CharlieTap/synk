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
    private val classDeclaration: KSClassDeclaration,
) {
    val symbols = processorContext.symbols
    val poetTypes = processorContext.poetTypes
    val logger = processorContext.logger

    val type = classDeclaration.asType()
    val typeName = type.toClassName()

    val packageName = classDeclaration.packageName.asString()
    val simpleName = classDeclaration.simpleName.asString()

    val isSealed = classDeclaration.modifiers.contains(Modifier.SEALED)
    val sealedSubClasses = classDeclaration.getSealedSubclasses().toList()
    val parameters = classDeclaration.primaryConstructor?.parameters ?: emptyList()

}
