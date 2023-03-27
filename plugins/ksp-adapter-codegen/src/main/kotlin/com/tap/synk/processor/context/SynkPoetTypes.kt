package com.tap.synk.processor.context

import com.google.devtools.ksp.innerArguments
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.tap.synk.encode.BooleanEncoder
import com.tap.synk.encode.IntEncoder
import com.tap.synk.encode.ListEncoder
import com.tap.synk.encode.NullableMapEncoder
import com.tap.synk.encode.SetEncoder
import com.tap.synk.encode.StringEncoder

internal class SynkPoetTypes(
    private val symbols: SynkSymbols
) {
    val stringTypeName by lazy {
        symbols.stringType.toTypeName()
    }

    val stringMapTypeName by lazy {
        Map::class.asTypeName().parameterizedBy(stringTypeName, stringTypeName)
    }

    val nullableMapEncoder by lazy {
        NullableMapEncoder::class.asTypeName()
    }

    val stringEncoderTypeName by lazy {
        StringEncoder::class.asTypeName()
    }

    val intEncoderTypeName by lazy {
        IntEncoder::class.asTypeName()
    }

    val booleanEncoderTypeName by lazy {
        BooleanEncoder::class.asTypeName()
    }

    val listEncoderTypeName by lazy {
        ListEncoder::class.asTypeName()
    }

    val setEncoderTypeName by lazy {
        SetEncoder::class.asTypeName()
    }

    val idResolverTypeName by lazy {
        symbols.idResolver.toClassName()
    }

    val mapEncoderTypeName by lazy {
        symbols.mapEncoder.toClassName()
    }

    val synkAdapterTypeName by lazy {
        symbols.synkAdapter.toClassName()
    }

    val stringSerializer by lazy {
        symbols.stringSerializer.toClassName()
    }

    val enumSerializer by lazy {
        symbols.enumStringSerializer.toClassName()
    }

    fun parameterizedMapEncoder(genericTypeName: TypeName): ParameterizedTypeName {
        return mapEncoderTypeName.parameterizedBy(genericTypeName)
    }

    fun parameterizedStringSerializer(genericTypeName: TypeName): ParameterizedTypeName {
        return stringSerializer.parameterizedBy(genericTypeName)
    }

    fun parameterizedEnumStringSerializer(genericTypeName: TypeName): ParameterizedTypeName {
        return enumSerializer.parameterizedBy(genericTypeName)
    }

    fun primitiveEncoder(type: KSType): TypeName {
        return when (type) {
            symbols.boolType -> booleanEncoderTypeName
            symbols.intType -> intEncoderTypeName
            symbols.stringType -> stringEncoderTypeName
            else -> stringEncoderTypeName
        }
    }

    fun collectionEncoder(type: KSType): ParameterizedTypeName? {
        return when (type.declaration) {
            symbols.setType.declaration -> {
                setEncoderTypeName.parameterizedBy(type.innerArguments.first().toTypeName())
            }
            symbols.listType.declaration -> {
                listEncoderTypeName.parameterizedBy(type.innerArguments.first().toTypeName())
            }
            else -> null
        }
    }
}
