package com.tap.synk.processor

import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.innerArguments
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Modifier
import com.tap.synk.annotation.SynkAdapter
import com.tap.synk.processor.context.ProcessorContext
import com.tap.synk.processor.context.SynkPoetTypes
import com.tap.synk.processor.context.SynkSymbols
import com.tap.synk.processor.ext.containsNestedClasses
import com.tap.synk.processor.ext.isSealed
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

        val idResolverDeclarations = annotatedSymbols
            .filterIsInstance<KSClassDeclaration>()
            .filter { symbolValidator.validate(it) }

        val mapEncoderDeclarations = idResolverDeclarations.mapNotNull { classDeclaration ->
            val idResolverType = classDeclaration.getAllSuperTypes().first { it.declaration == synkSymbols.idResolver.declaration }
            val crdtType = idResolverType.innerArguments.first().type?.resolve()
            (crdtType?.declaration as? KSClassDeclaration)
        }.flatMap(::classDeclarationExpansion).toSet()

        idResolverDeclarations.forEach { it.accept(IDResolverVisitor(processorContext), Unit) }
        mapEncoderDeclarations.forEach { it.accept(MapEncoderVisitor(processorContext), Unit) }

        return emptyList()
    }

    private fun classDeclarationExpansion(classDeclaration: KSClassDeclaration): List<KSClassDeclaration> {
        return when {
            classDeclaration.isSealed() || classDeclaration.containsNestedClasses() -> {
               recursiveClassDeclarationExpansion(setOf(classDeclaration)).toList()
            }
            else -> listOf(classDeclaration)
        }
    }

    private fun childSubClassDeclarations(classDeclaration: KSClassDeclaration) : Set<KSClassDeclaration> {
        // When the class declaration is a sealed class
        val sealedChildDeclarations = if (classDeclaration.isSealed()) {
            classDeclaration.getSealedSubclasses().toSet()
        } else emptySet()

        val childClassDeclarations = classDeclaration.primaryConstructor?.parameters?.map { param ->
            param.type.resolve().declaration
        }?.filterIsInstance<KSClassDeclaration>() ?: emptySet()

        // When the class declaration contains a constructor param which is a date class
        val childDataClassDeclarations = childClassDeclarations.filter { it.modifiers.contains(Modifier.DATA) }.toSet()
        // When the class declaration contains a constructor param which is a sealed class
        val childSealedClassDeclarations = childClassDeclarations.filter { it.modifiers.contains(Modifier.SEALED) }.toSet()

        return sealedChildDeclarations + childDataClassDeclarations + childSealedClassDeclarations
    }

    private tailrec fun recursiveClassDeclarationExpansion(classDeclarations: Set<KSClassDeclaration>, originClassDeclarations: Set<KSClassDeclaration> = emptySet()) : Set<KSClassDeclaration> {
        val subClassDeclarations = classDeclarations.flatMap { childSubClassDeclarations(it) }.toSet()

        return if(subClassDeclarations.isEmpty()) {
            classDeclarations + originClassDeclarations
        } else {
            recursiveClassDeclarationExpansion(subClassDeclarations, originClassDeclarations + classDeclarations)
        }
    }
}
