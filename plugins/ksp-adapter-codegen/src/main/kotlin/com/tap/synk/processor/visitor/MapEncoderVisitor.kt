package com.tap.synk.processor.visitor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.squareup.kotlinpoet.ksp.addOriginatingKSFile
import com.squareup.kotlinpoet.ksp.writeTo
import com.tap.synk.processor.context.ProcessorContext
import com.tap.synk.processor.context.SynkPoetTypes
import com.tap.synk.processor.context.SynkSymbols
import com.tap.synk.processor.filespec.encoder.classDeclarationConverter
import com.tap.synk.processor.filespec.encoder.mapEncoderFileSpec

internal class MapEncoderVisitor(
    private val synkSymbols: SynkSymbols,
    private val synkPoetTypes: SynkPoetTypes,
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : KSVisitorVoid() {

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {

        val customPackageName = classDeclaration.packageName.asString()
        val crdtClassName = classDeclaration.simpleName.asString()
        val mapEncoderFileName = crdtClassName + "MapEncoder"
        val processorContext = ProcessorContext(synkSymbols, synkPoetTypes, logger)


        with(processorContext) {

            val containingFile = classDeclaration.containingFile ?: return
            val mapEncoder = classDeclarationConverter(classDeclaration)

            val mapEncoderFileSpec = mapEncoderFileSpec(
                customPackageName,
                mapEncoderFileName,
                mapEncoder,
            ) { addOriginatingKSFile(containingFile) }
            mapEncoderFileSpec.writeTo(codeGenerator = codeGenerator, aggregating = false)
        }
    }
}