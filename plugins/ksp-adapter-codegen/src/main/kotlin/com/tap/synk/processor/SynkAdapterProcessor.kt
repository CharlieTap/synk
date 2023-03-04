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
import com.tap.synk.processor.context.SynkPoetTypes
import com.tap.synk.processor.context.SynkSymbols
import com.tap.synk.processor.ext.isSealed
import com.tap.synk.processor.visitor.IDResolverVisitor
import com.tap.synk.processor.visitor.MapEncoderVisitor

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

        val idResolverDeclarations = annotatedSymbols
            .filterIsInstance<KSClassDeclaration>()
            .filter { symbolValidator.validate(it) }

        val mapEncoderDeclarations = idResolverDeclarations.mapNotNull { classDeclaration ->
            val idResolverType = classDeclaration.getAllSuperTypes().first { it.declaration == synkSymbols.idResolver.declaration }
            val crdtType = idResolverType.innerArguments.first().type?.resolve()
            (crdtType?.declaration as? KSClassDeclaration)
        }.flatMap(::classDeclarationExpansion)

        idResolverDeclarations.forEach { it.accept(IDResolverVisitor(synkSymbols, synkPoetTypes, codeGenerator, logger), Unit) }
        mapEncoderDeclarations.forEach { it.accept(MapEncoderVisitor(synkSymbols, synkPoetTypes, codeGenerator, logger), Unit) }

        return emptyList()
    }

    /**
     * One KSClassDeclaration can actually result in the creation of multiple MapEncoders
     * If it's a SealedClass
     * If the KSClassDeclaration properties contains 3rd party types
     */
    private fun classDeclarationExpansion(classDeclaration: KSClassDeclaration): List<KSClassDeclaration> {
        return when {
            classDeclaration.isSealed() -> {
                val childDeclarations = classDeclaration.declarations.filterIsInstance<KSClassDeclaration>().toList()
                listOf(classDeclaration, *childDeclarations.toTypedArray())
            }
            else -> listOf(classDeclaration)
        }
    }
}
