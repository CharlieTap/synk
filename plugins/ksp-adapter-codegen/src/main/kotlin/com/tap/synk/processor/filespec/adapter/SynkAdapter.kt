package com.tap.synk.processor.filespec.adapter

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName

internal data class SynkAdapter(
    val className: ClassName,
    val interfaces: Set<AdapterInterface>,
    val parameters: List<AdapterParameter>,
)

internal sealed interface AdapterInterface
@JvmInline
internal value class Inherited(val typeName: ParameterizedTypeName): AdapterInterface
internal data class Delegated(val typeName: ParameterizedTypeName, val delegate: String): AdapterInterface

internal data class AdapterParameter(
    val parameterName: String,
    val parameterType: ParameterizedTypeName,
    val parameterDefaultType: TypeName
)

