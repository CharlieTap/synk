package com.tap.synk.processor.filespec.encoder

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName
import com.tap.synk.processor.ext.decapitalise

internal data class MapEncoder(
    val className: ClassName,
    val enum: EncoderEnum?,
    val parameters: List<EncoderParameter>,
    val extends: EncoderInterface,
    val encodeFunction: EncoderFunction,
    val decodeFunction: EncoderFunction
)

@JvmInline
internal value class EncoderInterface(val typeName: ParameterizedTypeName)

internal sealed interface EncoderParameter {

    // Sealed Class Encoder
    data class CompositeSubEncoder(
        val name: String,
        val genericType: ParameterizedTypeName,
        val encoderType: TypeName
    ) : EncoderParameter

    // Collections Encoder
    data class ParameterizedCollectionEncoder(
        val parameterName: String,
        val collectionEncoderVariableName: String,
        val collectionEncoderTypeName: ParameterizedTypeName,
        val genericMapEncoderTypeName: ParameterizedTypeName,
        val genericTypeName: TypeName,
        val genericEncoderTypeName: TypeName,
        val instantiateNestedEncoder: Boolean = false
    ) : EncoderParameter

    // Data class Encoder
    data class SubEncoder(
        val parameterName: String,
        val customEncoderVariableName: String,
        val genericTypeName: ParameterizedTypeName,
        val concreteTypeName: TypeName,
        val nullableMapEncoder: TypeName
    ) : EncoderParameter

    // Value class and Custom Class serializer
    data class CustomSerializer(
        val parameterName: String,
        val serializerVariableName: String,
        val genericTypeName: ParameterizedTypeName,
        val concreteTypeName: TypeName,
        val instantiateSerializer: Boolean = false
    ) : EncoderParameter

    data class EnumSerializer(
        val parameterName: String,
        val serializerVariableName: String,
        val enumType: TypeName,
        val parameterizedStringSerializer: ParameterizedTypeName
    ) : EncoderParameter

    fun variableName(): String = when (this) {
        is CompositeSubEncoder -> name
        is ParameterizedCollectionEncoder -> collectionEncoderVariableName
        is SubEncoder -> customEncoderVariableName
        is CustomSerializer -> serializerVariableName
        is EnumSerializer -> serializerVariableName
    }

    fun variableType(): TypeName = when (this) {
        is CompositeSubEncoder -> genericType
        is ParameterizedCollectionEncoder -> genericMapEncoderTypeName
        is SubEncoder -> genericTypeName
        is CustomSerializer -> genericTypeName
        is EnumSerializer -> parameterizedStringSerializer
    }
}

internal data class EncoderFunction(
    val type: Type,
    val functionParameterName: String,
    val functionParameterTypeName: TypeName,
    val functionReturnTypeName: TypeName,
    val encoderFunctionCodeBlock: EncoderFunctionCodeBlock
) {
    enum class Type {
        Encode,
        Decode
    }
}

internal fun EncoderFunction.Type.name(): String {
    return name.decapitalise()
}

internal sealed interface EncoderFunctionCodeBlock {

    data class Standard(
        val encodables: List<EncoderFunctionCodeBlockStandardEncodable>,
        val type: TypeName? = null
    ) : EncoderFunctionCodeBlock

    data class Delegate(
        val subEncoders: List<EncoderFunctionCodeBlockDelegateEncoder>
    ) : EncoderFunctionCodeBlock
}

internal class EncoderFunctionCodeBlockDelegateEncoder(
    val typeName: TypeName,
    val variableName: String,
    val enumName: String
)

internal sealed interface EncoderFunctionCodeBlockStandardEncodable {

    data class Primitive(
        val encodedKey: String,
        val conversion: String = "",
        val nullable: Boolean
    ) : EncoderFunctionCodeBlockStandardEncodable

    data class Serializable(
        val encodedKey: String,
        val serializerVariableName: String,
        val nullable: Boolean
    ) : EncoderFunctionCodeBlockStandardEncodable

    data class NestedClass(
        val encodedKey: String,
        val encoderVariableName: String
    ) : EncoderFunctionCodeBlockStandardEncodable
}

internal data class EncoderEnum(
    val name: String,
    val options: List<String>
)
