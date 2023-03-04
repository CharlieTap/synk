package com.tap.synk.processor.filespec.encoder

import com.google.devtools.ksp.innerArguments
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
        deriveEnums(),
        deriveParameters(),
        deriveEncoderInterface(),
        deriveEncodeFunction(),
        deriveDecodeFunction()
    )
}

context(EncoderContext)
private fun deriveParameters(): List<EncoderParameter> {
    val paramEncoders = parameters.mapNotNull { param ->

        // List<String> or Any
        val parameterType = param.type.resolve()

        if (symbols.isCollection(parameterType)) {
            val paramName = param.name?.asString() ?: run {
                logger.error("Failed to derive name from parameter", param)
                return emptyList()
            }
            // Inner type of collection, String or Int or Custom
            val genericType = parameterType.innerArguments.first().type?.resolve() ?: run {
                logger.error("Synk Adapter Plugin can only encode collections of primitive types", param)
                return emptyList()
            }
            // StringEncoder or IntEncoder
            val primitiveEncoder = if (symbols.isPrimitive(genericType)) { poetTypes.primitiveEncoder(genericType) } else {
                logger.error("Synk Adapter Plugin can only encode collections of primitive types", param)
                return emptyList()
            }
            // SetEncoder or ListEncoder
            val collectionEncoderTypeName = poetTypes.collectionEncoder(parameterType) ?: run {
                logger.error("Synk Adapter Plugin can only encode collections of primitive types", param)
                return emptyList()
            }

            val genericTypeName = genericType.toTypeName()
            // MapEncoder<List<T>> or MapEncoder<Set<T>>
            val genericMapEncoderTypeName = poetTypes.mapEncoderTypeName.parameterizedBy(parameterType.toClassName().parameterizedBy(genericTypeName))
            val collectionEncoderVariableName = paramName + collectionEncoderTypeName.rawType.simpleNames.first() + genericType.declaration.simpleName.asString()

            EncoderParameter.ParameterizedCollectionEncoder(
                paramName,
                collectionEncoderVariableName,
                collectionEncoderTypeName,
                genericMapEncoderTypeName,
                genericTypeName,
                primitiveEncoder
            )
        } else null
    }

    val subClassEncoders = if (isSealed) {
        sealedSubClasses.map { subClassDeclaration ->
            val name = subClassDeclaration.simpleName.asString()
            val encoderVariableName = "${subClassDeclaration.simpleName.asString()}Encoder".decapitalise()
            val encoderType = name + "MapEncoder"
            val concreteType = ClassName(packageName, encoderType)
            val type = poetTypes.parameterizedMapEncoder(subClassDeclaration.asType().toTypeName())
            println(type)
            EncoderParameter.CompositeSubEncoder(encoderVariableName, type, concreteType)
        }
    } else emptyList()

    return paramEncoders + subClassEncoders
}

context(EncoderContext)
private fun deriveEncoderInterface(): EncoderInterface {
    return EncoderInterface(poetTypes.mapEncoderTypeName.parameterizedBy(typeName))
}

context(EncoderContext)
private fun deriveEncodeFunction(): EncoderFunction {
    val codeBlock = if (isSealed) {
        val delegates = sealedSubClasses.map { declaration ->

            val sealedClassName = declaration.simpleName.asString()
            val sealedClassType = declaration.toClassName()
            val sealedClassEncoderVariableName = "${sealedClassName}Encoder".decapitalise()
            val enumName = declarationName + "MapEncoderType." + sealedClassName

            EncoderFunctionCodeBlockDelegateEncoder(sealedClassType, sealedClassEncoderVariableName, enumName)
        }

        EncoderFunctionCodeBlock.Delegate(delegates)
    } else {
        val encodables = parameters.map { param ->

            val parameterName = param.name?.asString() ?: ""
            val parameterType = param.type.resolve()

            if (symbols.isCollection(parameterType)) {
                val collectionEncoderTypeName = poetTypes.collectionEncoder(parameterType) ?: run {
                    logger.error("Synk Adapter Plugin can only encode collections of primitive types", param)
                    throw IllegalStateException()
                }
                // Inner type of collection, String or Int or Custom
                val genericType = parameterType.innerArguments.first().type?.resolve() ?: run {
                    logger.error("Synk Adapter Plugin can only encode collections of primitive types", param)
                    throw IllegalStateException()
                }
                val collectionEncoderVariableName = parameterName + collectionEncoderTypeName.rawType.simpleNames.first() + genericType.declaration.simpleName.asString()

                EncoderFunctionCodeBlockStandardEncodable.ParameterizedCollection(parameterName, collectionEncoderVariableName)
            } else {
                val conversion = if (!symbols.isString(parameterType)) {
                    ".toString()"
                } else { "" }
                EncoderFunctionCodeBlockStandardEncodable.Primitive(parameterName, conversion)
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
        val delegates = sealedSubClasses.map { declaration ->

            val sealedClassName = declaration.simpleName.asString()
            val sealedClassType = declaration.toClassName()
            val sealedClassEncoderVariableName = "${sealedClassName}Encoder".decapitalise()
            val enumName = declarationName + "MapEncoderType." + sealedClassName

            EncoderFunctionCodeBlockDelegateEncoder(sealedClassType, sealedClassEncoderVariableName, enumName)
        }

        EncoderFunctionCodeBlock.Delegate(delegates)
    } else {
        val encodables = parameters.map { param ->

            val parameterName = param.name?.asString() ?: ""
            val parameterType = param.type.resolve()

            if (symbols.isCollection(parameterType)) {
                val collectionEncoderTypeName = poetTypes.collectionEncoder(parameterType) ?: run {
                    logger.error("Synk Adapter Plugin can only encode collections of primitive types", param)
                    throw IllegalStateException()
                }
                // Inner type of collection, String or Int or Custom
                val genericType = parameterType.innerArguments.first().type?.resolve() ?: run {
                    logger.error("Synk Adapter Plugin can only encode collections of primitive types", param)
                    throw IllegalStateException()
                }
                val collectionEncoderVariableName = parameterName + collectionEncoderTypeName.rawType.simpleNames.first() + genericType.declaration.simpleName.asString()

                EncoderFunctionCodeBlockStandardEncodable.ParameterizedCollection(parameterName, collectionEncoderVariableName)
            } else {
                val conversion = if (!symbols.isString(parameterType)) {
                    symbols.stringDecodeFunction(parameterType)
                } else { "" }
                EncoderFunctionCodeBlockStandardEncodable.Primitive(parameterName, conversion)
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
