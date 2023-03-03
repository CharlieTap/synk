package com.tap.synk.processor.visitor

import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.innerArguments
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo
import com.tap.synk.processor.context.ProcessorContext
import com.tap.synk.processor.context.SynkPoetTypes
import com.tap.synk.processor.context.SynkSymbols
import com.tap.synk.processor.filespec.adapter.synkAdapterFileSpec

internal class IDResolverVisitor(
    private val synkSymbols: SynkSymbols,
    private val synkPoetTypes: SynkPoetTypes,
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : KSVisitorVoid() {

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {

        val customIdResolverType = classDeclaration.getAllSuperTypes().first { it.declaration == synkSymbols.idResolver.declaration }
        val crdtType = customIdResolverType.innerArguments.first().type?.resolve() ?: return

        val synkPackageName = synkSymbols.synkAdapter.declaration.packageName.asString()
        val mapEncoderPackageName = synkSymbols.mapEncoder.declaration.packageName.asString()
        val customPackageName = crdtType.declaration.packageName.asString()

        val crdtClassName = crdtType.declaration.simpleName.asString()
        val idResolverClassName = classDeclaration.simpleName.asString()
        val mapEncoderFileName = crdtClassName + "MapEncoder"
        val synkAdapterClassName = crdtClassName + "SynkAdapter"

        val customMapEncoderTypeName = ClassName(mapEncoderPackageName, "MapEncoder").parameterizedBy(crdtType.toTypeName())
        val customSynkAdapterTypeName = ClassName(synkPackageName, "SynkAdapter").parameterizedBy(crdtType.toTypeName())

        with(ProcessorContext(synkSymbols, synkPoetTypes, logger)) {

            val synkAdapterFileSpec = synkAdapterFileSpec(
                customPackageName,
                idResolverClassName,
                mapEncoderFileName,
                synkAdapterClassName,
                customIdResolverType.toTypeName(),
                customMapEncoderTypeName,
                customSynkAdapterTypeName,
                classDeclaration.containingFile ?: return
            )

            synkAdapterFileSpec.writeTo(codeGenerator = codeGenerator, aggregating = false)
        }
    }
}