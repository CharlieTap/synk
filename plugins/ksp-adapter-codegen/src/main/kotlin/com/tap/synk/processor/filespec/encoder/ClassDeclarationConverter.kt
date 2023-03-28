package com.tap.synk.processor.filespec.encoder

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.tap.synk.processor.context.EncoderContext
import com.tap.synk.processor.ext.asType
import com.tap.synk.processor.ext.decapitalise

context(EncoderContext)
internal fun mapEncoder(): MapEncoder {
    return MapEncoder(
        className,
        deriveEnums(),
        deriveParameters(),
        deriveEncoderInterface(),
        deriveEncodeFunction(),
        deriveDecodeFunction()
    )
}

context(EncoderContext)
private fun deriveParameterizedCollectionParameter(parameter: EncoderContext.DerivedParameter): EncoderParameter.ParameterizedCollectionEncoder? {
    // Inner type of collection, String or Int or Custom
    val genericType = parameter.innerType
    // StringEncoder or BarEncoder
    val concreteEncoderPair = if (symbols.isPrimitive(genericType)) {
        poetTypes.primitiveEncoder(genericType) to false
    } else if (symbols.isDataClass(genericType)) {
        ClassName(packageName, genericType.declaration.simpleName.asString() + "MapEncoder") to true
    } else {
        logger.error("Synk Adapter Plugin can only encode collections of primitive types or data classes", parameter.parameter)
        return null
    }
    // SetEncoder or ListEncoder
    val collectionEncoderTypeName = poetTypes.collectionEncoder(parameter.type) ?: run {
        logger.error("Synk Adapter Plugin can only encode collections of primitive types", parameter.parameter)
        return null
    }

    val genericTypeName = parameter.innerTypeName
    // MapEncoder<List<T>> or MapEncoder<Set<T>>
    val genericMapEncoderTypeName = poetTypes.mapEncoderTypeName.parameterizedBy(parameter.type.toClassName().parameterizedBy(genericTypeName))
    val collectionEncoderVariableName = parameter.name + collectionEncoderTypeName.rawType.simpleNames.first() + genericType.declaration.simpleName.asString()

    return EncoderParameter.ParameterizedCollectionEncoder(
        parameter.name,
        collectionEncoderVariableName,
        collectionEncoderTypeName,
        genericMapEncoderTypeName,
        genericTypeName,
        concreteEncoderPair.first,
        concreteEncoderPair.second
    )
}

context(EncoderContext)
private fun deriveSubEncoderParameter(parameter: EncoderContext.DerivedParameter): EncoderParameter.SubEncoder {
    val genericMapEncoderTypeName = poetTypes.mapEncoderTypeName.parameterizedBy(parameter.type.toTypeName())
    val concreteMapEncoderName = parameter.type.toClassName().simpleName + "MapEncoder"
    val concreteMapEncoderTypeName = ClassName(packageName, concreteMapEncoderName)

    return EncoderParameter.SubEncoder(
        parameter.name,
        parameter.name + "MapEncoder",
        genericMapEncoderTypeName,
        concreteMapEncoderTypeName,
        poetTypes.nullableMapEncoder
    )
}

context(EncoderContext)
private fun deriveSerializerParameter(parameter: EncoderContext.DerivedParameter): EncoderParameter.CustomSerializer {
    val genericType = parameter.type.makeNotNullable()
    val parameterizedStringSerializer = poetTypes.parameterizedStringSerializer(genericType.toTypeName())

    val (concreteType, requiresInstantiation) = serializerMap[genericType]!!

    return EncoderParameter.CustomSerializer(
        parameter.name,
        parameter.name + "Serializer",
        parameterizedStringSerializer,
        concreteType,
        requiresInstantiation
    )
}

context(EncoderContext)
private fun deriveEnumSerializerParameter(parameter: EncoderContext.DerivedParameter): EncoderParameter.EnumSerializer {
    val genericType = parameter.type
    val parameterizedEnumSerializer = poetTypes.parameterizedEnumStringSerializer(genericType.toTypeName())

    return EncoderParameter.EnumSerializer(
        parameter.name,
        parameter.name + "Serializer",
        parameter.type.toTypeName(),
        parameterizedEnumSerializer
    )
}

context(EncoderContext)
private fun deriveCompositeSubEncoderParameter(subClassDeclaration: KSClassDeclaration): EncoderParameter.CompositeSubEncoder {
    val name = subClassDeclaration.simpleName.asString()
    val encoderVariableName = "${subClassDeclaration.simpleName.asString()}Encoder".decapitalise()
    val encoderType = name + "MapEncoder"
    val concreteType = ClassName(packageName, encoderType)
    val type = poetTypes.parameterizedMapEncoder(subClassDeclaration.asType().toTypeName())

    return EncoderParameter.CompositeSubEncoder(encoderVariableName, type, concreteType)
}

context(EncoderContext)
private fun deriveParameters(): List<EncoderParameter> {
    val paramEncoders = derivedParameters.mapNotNull { param ->
        if (param.isInstanceOfCollection) {
            deriveParameterizedCollectionParameter(param)
        } else if (param.isInstanceOfDataClass) {
            deriveSubEncoderParameter(param)
        } else if (param.hasProvidedSerializer) {
            deriveSerializerParameter(param)
        } else if (param.isEnum) {
            deriveEnumSerializerParameter(param)
        } else null
    }

    val subClassEncoders = if (isSealed) {
        sealedSubClasses.map { subClassDeclaration ->
            deriveCompositeSubEncoderParameter(subClassDeclaration)
        }
    } else emptyList()

    return paramEncoders + subClassEncoders
}

context(EncoderContext)
private fun deriveEncoderInterface(): EncoderInterface {
    return EncoderInterface(poetTypes.mapEncoderTypeName.parameterizedBy(typeName))
}

context(EncoderContext)
private fun deriveDelegateEncoderFunctionCodeBlock(): EncoderFunctionCodeBlock {
    val delegates = sealedSubClasses.map { declaration ->

        val sealedClassName = declaration.simpleName.asString()
        val sealedClassType = declaration.toClassName()
        val sealedClassEncoderVariableName = "${sealedClassName}Encoder".decapitalise()
        val enumName = declarationName + "MapEncoderType." + sealedClassName

        EncoderFunctionCodeBlockDelegateEncoder(sealedClassType, sealedClassEncoderVariableName, enumName)
    }

    return EncoderFunctionCodeBlock.Delegate(delegates)
}

context(EncoderContext)
private fun deriveStandardEncodablePrimitive(parameter: EncoderContext.DerivedParameter): EncoderFunctionCodeBlockStandardEncodable.Primitive {
    val conversion = if (!symbols.isString(parameter.type)) {
        ".toString()"
    } else { "" }
    return EncoderFunctionCodeBlockStandardEncodable.Primitive(parameter.name, conversion, parameter.type.isMarkedNullable)
}

context(EncoderContext)
private fun deriveStandardEncodableCollectionNestedClass(parameter: EncoderContext.DerivedParameter): EncoderFunctionCodeBlockStandardEncodable.NestedClass {
    val collectionEncoderTypeName = poetTypes.collectionEncoder(parameter.type) ?: run {
        logger.error("Synk Adapter Plugin can only encode collections of primitive types", parameter.parameter)
        throw IllegalStateException()
    }
    // Inner type of collection, String or Int or Custom
    val genericType = parameter.innerType
    val collectionEncoderVariableName = parameter.name + collectionEncoderTypeName.rawType.simpleNames.first() + genericType.declaration.simpleName.asString()

    return EncoderFunctionCodeBlockStandardEncodable.NestedClass(parameter.name, collectionEncoderVariableName)
}

context(EncoderContext)
private fun deriveEncodeFunction(): EncoderFunction {
    val codeBlock = if (isSealed) {
        deriveDelegateEncoderFunctionCodeBlock()
    } else {
        val encodables = derivedParameters.map { param ->

            if (param.isInstanceOfCollection) {
                deriveStandardEncodableCollectionNestedClass(param)
            } else if (param.isInstanceOfDataClass) {
                EncoderFunctionCodeBlockStandardEncodable.NestedClass(param.name, param.name + "MapEncoder")
            } else if (param.hasProvidedSerializer) {
                EncoderFunctionCodeBlockStandardEncodable.Serializable(param.name, param.name + "Serializer", param.type.isMarkedNullable)
            } else if (param.isEnum) {
                EncoderFunctionCodeBlockStandardEncodable.Serializable(param.name, param.name + "Serializer", param.type.isMarkedNullable)
            } else {
                deriveStandardEncodablePrimitive(param)
            }
        }
        EncoderFunctionCodeBlock.Standard(encodables)
    }

    return EncoderFunction(
        EncoderFunction.Type.Encode,
        "crdt",
        typeName,
        poetTypes.stringMapTypeName,
        codeBlock
    )
}

context(EncoderContext)
private fun deriveDecodeFunction(): EncoderFunction {
    val codeBlock = if (isSealed) {
        deriveDelegateEncoderFunctionCodeBlock()
    } else {
        val encodables = derivedParameters.map { param ->
            if (param.isInstanceOfCollection) {
                deriveStandardEncodableCollectionNestedClass(param)
            } else if (param.isInstanceOfDataClass) {
                EncoderFunctionCodeBlockStandardEncodable.NestedClass(param.name, param.name + "MapEncoder")
            } else if (param.hasProvidedSerializer) {
                EncoderFunctionCodeBlockStandardEncodable.Serializable(param.name, param.name + "Serializer", param.type.isMarkedNullable)
            } else if (param.isEnum) {
                EncoderFunctionCodeBlockStandardEncodable.Serializable(param.name, param.name + "Serializer", param.type.isMarkedNullable)
            } else {
                val typeNotNull = param.type.makeNotNullable()
                val conversion = if (!symbols.isString(typeNotNull)) {
                    if (param.type.isMarkedNullable) {
                        "?" + symbols.stringDecodeFunction(typeNotNull)
                    } else symbols.stringDecodeFunction(typeNotNull)
                } else { "" }
                EncoderFunctionCodeBlockStandardEncodable.Primitive(param.name, conversion, param.type.isMarkedNullable)
            }
        }
        EncoderFunctionCodeBlock.Standard(encodables, typeName)
    }

    return EncoderFunction(
        EncoderFunction.Type.Decode,
        "map",
        poetTypes.stringMapTypeName,
        typeName,
        codeBlock
    )
}

context(EncoderContext)
private fun deriveEnums(): EncoderEnum? {
    return if (isSealed) {
        val enumName = declarationName + "MapEncoderType"
        val options = sealedSubClasses.map { sealedSubClass ->
            sealedSubClass.simpleName.asString()
        }.toList()
        EncoderEnum(enumName, options)
    } else null
}
