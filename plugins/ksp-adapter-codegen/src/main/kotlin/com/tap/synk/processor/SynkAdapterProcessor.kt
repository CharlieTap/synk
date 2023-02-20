package com.tap.synk.processor

import com.google.auto.service.AutoService
import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.toTypeParameterResolver
import com.squareup.kotlinpoet.ksp.writeTo
import com.tap.synk.annotation.SynkAdapter

class SynkAdapterProcessor(
    private val options: Map<String, String>,
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {


    private lateinit var synkSymbols: SynkSymbols
    private lateinit var intType: KSType
    private lateinit var stringType: KSType

    override fun process(resolver: Resolver): List<KSAnnotated> {
        synkSymbols = SynkSymbols(resolver)
        intType = resolver.builtIns.intType
        stringType = resolver.builtIns.stringType
        val symbols = resolver.getSymbolsWithAnnotation(SynkAdapter::class.qualifiedName!!)
        val unableToProcess = symbols.filterNot { it.validate() }

        symbols
            .filter { it is KSClassDeclaration && it.validate() }
            .forEach { it.accept(SynkAnnotationVisitor(synkSymbols, codeGenerator, logger), Unit) }

        return unableToProcess.toList()
    }

    private data class ClassDetails(
        val type: KSType,
        val simpleName: String,
        val packageName: String
    )

    private fun KSClassDeclaration.isDataClass() =
        modifiers.contains(Modifier.DATA)
}