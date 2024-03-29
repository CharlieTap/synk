package com.tap.synk.processor.filespec.adapter

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec

internal fun synkAdapterFileSpec(
    synkAdapter: SynkAdapter,
    configuration: TypeSpec.Builder.() -> Unit,
) = FileSpec.builder(
    packageName = synkAdapter.className.packageName,
    fileName = synkAdapter.className.simpleName,
).apply {
    indent("    ")
    addFileComment("Code generated by SynkAdapter plugin. Do not edit this file.")
    addType(
        TypeSpec.classBuilder(synkAdapter.className.simpleName).apply {
            primaryConstructor(
                FunSpec.constructorBuilder().apply {
                    synkAdapter.parameters.forEach { parameter ->
                        addParameter(
                            ParameterSpec
                                .builder(parameter.parameterName, parameter.parameterType)
                                .defaultValue("%T()", parameter.parameterDefaultType)
                                .build(),
                        )
                    }
                }.build(),
            )
            synkAdapter.parameters.forEach { parameter ->
                addProperty(
                    PropertySpec.builder(parameter.parameterName, parameter.parameterType)
                        .initializer(parameter.parameterName)
                        .addModifiers(KModifier.PRIVATE)
                        .build(),
                )
            }
            synkAdapter.interfaces.forEach { adapterInterface ->
                when (adapterInterface) {
                    is Delegated -> {
                        addSuperinterface(adapterInterface.typeName, adapterInterface.delegate)
                    }
                    is Inherited -> {
                        addSuperinterface(adapterInterface.typeName)
                    }
                }
            }
            configuration()
        }.build(),
    )
}.build()
