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
import com.tap.synk.annotation.SynkSerializer
import com.tap.synk.processor.context.ProcessorContext
import com.tap.synk.processor.context.SynkPoetTypes
import com.tap.synk.processor.context.SynkSymbols
import com.tap.synk.processor.validator.SynkAdapterValidator
import com.tap.synk.processor.validator.SynkSerializerValidator
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

        val adapterSymbols = resolver.getSymbolsWithAnnotation(SynkAdapter::class.qualifiedName!!)
        val serializerSymbols = resolver.getSymbolsWithAnnotation(SynkSerializer::class.qualifiedName!!)

        val synkAdapterValidator = SynkAdapterValidator(synkSymbols, kspLogger)
        val synkSerializerValidator = SynkSerializerValidator(synkSymbols, kspLogger)
        val declarationExpander = ClassDeclarationExpander(synkSymbols)

        val idResolverDeclarations = adapterSymbols
            .filterIsInstance<KSClassDeclaration>()
            .filter { synkAdapterValidator.validate(it) }
            .toList()

        val stringSerializerDeclarations = serializerSymbols
            .filterIsInstance<KSClassDeclaration>()
            .filter { synkSerializerValidator.validate(it) }
            .toList()

        val mapEncoderDeclarations = idResolverDeclarations.mapNotNull { classDeclaration ->
            val idResolverType = classDeclaration.getAllSuperTypes().first { it.declaration == synkSymbols.idResolver.declaration }
            val crdtType = idResolverType.innerArguments.first().type?.resolve()
            (crdtType?.declaration as? KSClassDeclaration)
        }.flatMap { declarationExpander.expand(it) }.toSet()

        val processorContext = object : ProcessorContext {
            override val codeGenerator: CodeGenerator = kspCodeGenerator
            override val logger: KSPLogger = kspLogger
            override val poetTypes: SynkPoetTypes = synkPoetTypes
            override val symbols: SynkSymbols = synkSymbols
        }

        idResolverDeclarations.forEach { it.accept(IDResolverVisitor(processorContext), Unit) }
        mapEncoderDeclarations.forEach { it.accept(MapEncoderVisitor(processorContext, stringSerializerDeclarations), Unit) }

        return emptyList()
    }
}
