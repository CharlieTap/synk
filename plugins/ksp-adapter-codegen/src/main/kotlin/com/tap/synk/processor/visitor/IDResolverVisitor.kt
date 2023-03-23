package com.tap.synk.processor.visitor

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.squareup.kotlinpoet.ksp.addOriginatingKSFile
import com.squareup.kotlinpoet.ksp.writeTo
import com.tap.synk.processor.context.AdapterContext
import com.tap.synk.processor.context.ProcessorContext
import com.tap.synk.processor.filespec.adapter.synkAdapter
import com.tap.synk.processor.filespec.adapter.synkAdapterFileSpec

internal class IDResolverVisitor(
    private val processorContext: ProcessorContext
) : KSVisitorVoid() {

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        val adapterContext = AdapterContext(processorContext, classDeclaration)
        val containingFile = classDeclaration.containingFile ?: run {
            processorContext.logger.error("Failed to find annotation containing file")
            return
        }

        val synkAdapter = with(adapterContext) { synkAdapter() }
        val synkAdapterFileSpec = synkAdapterFileSpec(
            synkAdapter
        ) { addOriginatingKSFile(containingFile) }

        synkAdapterFileSpec.writeTo(codeGenerator = processorContext.codeGenerator, aggregating = false)
    }
}
