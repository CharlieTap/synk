package com.tap.synk.processor.context

import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.innerArguments
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName

internal data class AdapterContext(
    private val processorContext: ProcessorContext,
    private val classDeclaration: KSClassDeclaration,
) : ProcessorContext by processorContext {

    companion object {
        const val VARIABLE_NAME_ID_RESOLVER = "idResolver"
        const val VARIABLE_NAME_MAP_ENCODER = "mapEncoder"
    }

    val superTypes by lazy { classDeclaration.getAllSuperTypes() }

    val customIdResolverType by lazy { superTypes.first { it.declaration == symbols.idResolver.declaration } }

    // Foo
    val crdtType = customIdResolverType.innerArguments.first().type?.resolve() ?: run {
        throw IllegalStateException()
    }
    val crdtTypeName = crdtType.toTypeName()
    val crdtClassName = crdtType.declaration.simpleName.asString()

    // FooIDResolver
    val idResolverClassName by lazy { classDeclaration.toClassName() }

    // FooMapEncoder
    val mapEncoderClassName by lazy { ClassName(classDeclaration.packageName.asString(), crdtClassName + "MapEncoder") }

    // FooSynkAdapter
    val synkAdapterClassName by lazy { ClassName(classDeclaration.packageName.asString(), crdtClassName + "SynkAdapter") }

    // IDResolver<Foo>
    val customIdResolverTypeName by lazy { poetTypes.idResolverTypeName.parameterizedBy(crdtTypeName) }

    // MapEncoder<Foo>
    val customMapEncoderTypeName by lazy { poetTypes.mapEncoderTypeName.parameterizedBy(crdtTypeName) }

    // SynkAdapter<Foo>
    val customSynkAdapterTypeName by lazy { poetTypes.synkAdapterTypeName.parameterizedBy(crdtTypeName) }
}
