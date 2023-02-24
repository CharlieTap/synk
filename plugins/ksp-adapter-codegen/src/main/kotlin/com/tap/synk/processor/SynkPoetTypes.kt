package com.tap.synk.processor

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.ksp.toTypeName

internal class SynkPoetTypes(
    private val symbols: SynkSymbols
) {
    val stringTypeName = symbols.stringType.toTypeName()
    val stringMapTypeName = Map::class.asTypeName().parameterizedBy(stringTypeName, stringTypeName)

    val mapEncoderClassName = ClassName(symbols.mapEncoder.declaration.packageName.asString(), "MapEncoder")

    fun parameterizedMapEncoder(genericTypeName: TypeName) : ParameterizedTypeName {
        return mapEncoderClassName.parameterizedBy(genericTypeName)
    }
}