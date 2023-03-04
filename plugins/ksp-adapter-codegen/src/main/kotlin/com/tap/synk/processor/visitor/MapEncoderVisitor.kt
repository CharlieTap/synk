package com.tap.synk.processor.visitor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.squareup.kotlinpoet.ksp.addOriginatingKSFile
import com.squareup.kotlinpoet.ksp.writeTo
import com.tap.synk.processor.context.EncoderContext
import com.tap.synk.processor.context.ProcessorContext
import com.tap.synk.processor.context.SynkPoetTypes
import com.tap.synk.processor.context.SynkSymbols
import com.tap.synk.processor.filespec.encoder.mapEncoder
import com.tap.synk.processor.filespec.encoder.mapEncoderFileSpec

internal class MapEncoderVisitor(
    private val synkSymbols: SynkSymbols,
    private val synkPoetTypes: SynkPoetTypes,
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : KSVisitorVoid() {

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        val processorContext = ProcessorContext(synkSymbols, synkPoetTypes, logger)
        val encoderContext = EncoderContext(processorContext, classDeclaration)

        with(encoderContext) {
            val containingFile = classDeclaration.containingFile ?: return
            val mapEncoder = mapEncoder()

            val mapEncoderFileSpec = mapEncoderFileSpec(
                packageName,
                fileName,
                mapEncoder
            ) { addOriginatingKSFile(containingFile) }
            mapEncoderFileSpec.writeTo(codeGenerator = codeGenerator, aggregating = false)
        }
    }
}
