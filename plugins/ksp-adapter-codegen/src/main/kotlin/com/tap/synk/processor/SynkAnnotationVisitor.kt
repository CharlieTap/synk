package com.tap.synk.processor

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

internal class SynkAnnotationVisitor(
    private val synkSymbols: SynkSymbols,
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : KSVisitorVoid() {

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {

        val customIdResolverType = classDeclaration.getAllSuperTypes().first { it.declaration == synkSymbols.idResolver.declaration }
        val crdtType = customIdResolverType.innerArguments.first().type?.resolve() ?: return
        val crdtClassDeclaration = (crdtType.declaration as? KSClassDeclaration) ?: return

        val synkPackageName = synkSymbols.synkAdapter.declaration.packageName.asString()
        val mapEncoderPackageName = synkSymbols.mapEncoder.declaration.packageName.asString()
        val customPackageName = crdtType.declaration.packageName.asString()

        val crdtClassName = crdtType.declaration.simpleName.asString()
        val idResolverClassName = classDeclaration.simpleName.asString()
        val mapEncoderClassName = crdtClassName + "MapEncoder"
        val synkAdapterClassName = crdtClassName + "SynkAdapter"

        val customMapEncoderTypeName = ClassName(mapEncoderPackageName, "MapEncoder").parameterizedBy(crdtType.toTypeName())
        val customSynkAdapterTypeName = ClassName(synkPackageName, "SynkAdapter").parameterizedBy(crdtType.toTypeName())

        crdtType.declaration

        with(ProcessorContext(synkSymbols, logger)) {

            val synkAdapterFileSpec = synkAdapterFileSpec(
                customPackageName,
                idResolverClassName,
                mapEncoderClassName,
                synkAdapterClassName,
                customIdResolverType.toTypeName(),
                customMapEncoderTypeName,
                customSynkAdapterTypeName,
                classDeclaration.containingFile ?: return
            )

            val mapEncoderFileSpec = mapEncoderFileSpec(
                customPackageName,
                mapEncoderClassName,
                customMapEncoderTypeName,
                crdtType.toTypeName(),
                crdtClassDeclaration,
                classDeclaration.containingFile ?: return
            )

            synkAdapterFileSpec.writeTo(codeGenerator = codeGenerator, aggregating = false)
            mapEncoderFileSpec.writeTo(codeGenerator = codeGenerator, aggregating = false)
        }
    }
}