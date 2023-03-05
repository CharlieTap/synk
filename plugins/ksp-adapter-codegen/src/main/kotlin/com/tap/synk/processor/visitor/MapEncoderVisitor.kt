package com.tap.synk.processor.visitor

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.squareup.kotlinpoet.ksp.addOriginatingKSFile
import com.squareup.kotlinpoet.ksp.writeTo
import com.tap.synk.processor.context.EncoderContext
import com.tap.synk.processor.context.ProcessorContext
import com.tap.synk.processor.filespec.encoder.mapEncoder
import com.tap.synk.processor.filespec.encoder.mapEncoderFileSpec

internal class MapEncoderVisitor(
    private val processorContext: ProcessorContext
) : KSVisitorVoid() {

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {

        val encoderContext = EncoderContext(processorContext, classDeclaration)
        val containingFile = classDeclaration.containingFile ?: run {
            processorContext.logger.error("Failed to find annotation containing file")
            return
        }

        val mapEncoder = with(encoderContext) { mapEncoder() }
        val mapEncoderFileSpec = mapEncoderFileSpec(
            mapEncoder
        ) { addOriginatingKSFile(containingFile) }
        mapEncoderFileSpec.writeTo(codeGenerator = processorContext.codeGenerator, aggregating = false)
    }
}
