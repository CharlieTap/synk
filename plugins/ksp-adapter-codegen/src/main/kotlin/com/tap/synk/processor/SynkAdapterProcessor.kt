package com.tap.synk.processor

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.tap.synk.annotation.SynkAdapter

internal class SynkAdapterProcessor(
    private val options: Map<String, String>,
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {

        val synkSymbols = SynkSymbols(resolver)

        val annotatedSymbols = resolver.getSymbolsWithAnnotation(SynkAdapter::class.qualifiedName!!)
        val symbolValidator = SynkSymbolValidator(synkSymbols, logger)

        annotatedSymbols
            .filterIsInstance<KSClassDeclaration>()
            .filter { symbolValidator.validate(it) }
            .forEach { it.accept(SynkAnnotationVisitor(synkSymbols, codeGenerator, logger), Unit) }

        return emptyList()
    }
}