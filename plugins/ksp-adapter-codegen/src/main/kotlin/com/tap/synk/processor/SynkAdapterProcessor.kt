package com.tap.synk.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.tap.synk.annotation.SynkAdapter

internal class SynkAdapterProcessor(
    private val options: Map<String, String>,
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {

        val synkSymbols = SynkSymbols(resolver)
        val synkPoetTypes = SynkPoetTypes(synkSymbols)

        val annotatedSymbols = resolver.getSymbolsWithAnnotation(SynkAdapter::class.qualifiedName!!)
        val symbolValidator = SynkSymbolValidator(synkSymbols, logger)

        annotatedSymbols
            .filterIsInstance<KSClassDeclaration>()
            .filter { symbolValidator.validate(it) }
            .forEach { it.accept(SynkAnnotationVisitor(synkSymbols, synkPoetTypes, codeGenerator, logger), Unit) }

        return emptyList()
    }
}