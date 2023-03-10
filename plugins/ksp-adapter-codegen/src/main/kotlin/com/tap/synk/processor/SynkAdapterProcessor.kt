package com.tap.synk.processor

import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.innerArguments
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.tap.synk.annotation.SynkAdapter
import com.tap.synk.processor.context.ProcessorContext
import com.tap.synk.processor.context.SynkPoetTypes
import com.tap.synk.processor.context.SynkSymbols
import com.tap.synk.processor.visitor.IDResolverVisitor
import com.tap.synk.processor.visitor.MapEncoderVisitor

internal class SynkAdapterProcessor(
    private val options: Map<String, String>,
    private val kspCodeGenerator: CodeGenerator,
    private val kspLogger: KSPLogger
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val synkSymbols = SynkSymbols(resolver)
        val synkPoetTypes = SynkPoetTypes(synkSymbols)

        val processorContext = object : ProcessorContext {
            override val codeGenerator: CodeGenerator = kspCodeGenerator
            override val logger: KSPLogger = kspLogger
            override val poetTypes: SynkPoetTypes = synkPoetTypes
            override val symbols: SynkSymbols = synkSymbols
        }

        val annotatedSymbols = resolver.getSymbolsWithAnnotation(SynkAdapter::class.qualifiedName!!)
        val symbolValidator = SynkSymbolValidator(synkSymbols, kspLogger)
        val declarationExpander = ClassDeclarationExpander(synkSymbols)

        val idResolverDeclarations = annotatedSymbols
            .filterIsInstance<KSClassDeclaration>()
            .filter { symbolValidator.validate(it) }

        val mapEncoderDeclarations = idResolverDeclarations.mapNotNull { classDeclaration ->
            val idResolverType = classDeclaration.getAllSuperTypes().first { it.declaration == synkSymbols.idResolver.declaration }
            val crdtType = idResolverType.innerArguments.first().type?.resolve()
            (crdtType?.declaration as? KSClassDeclaration)
        }.flatMap{ declarationExpander.expand(it) }.toSet()

        idResolverDeclarations.forEach { it.accept(IDResolverVisitor(processorContext), Unit) }
        mapEncoderDeclarations.forEach { it.accept(MapEncoderVisitor(processorContext), Unit) }

        return emptyList()
    }

}
