package com.tap.synk.processor

import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.validate
import com.tap.synk.processor.context.SynkSymbols

internal class SynkSymbolValidator(
    private val synkSymbols: SynkSymbols,
    private val logger: KSPLogger
) {
    companion object Invariant {
        internal fun mustHaveQualifiedName(classDeclaration: KSClassDeclaration) : Boolean {
            return classDeclaration.qualifiedName?.let { true } ?: false
        }
        internal fun mustImplementIdResolver(classDeclaration: KSClassDeclaration, idResolverType: KSType) : Boolean {
            return classDeclaration.getAllSuperTypes().any {
                it.declaration == idResolverType.declaration
            }
        }
    }

    private fun testInvariant(invariant: (KSClassDeclaration) -> Boolean, logger: KSPLogger, failureMessage: String) : (KSClassDeclaration) -> Boolean {
        return { declaration ->
            invariant(declaration).let { result ->
                if(!result) {
                    logger.error(failureMessage, declaration)
                }
                result
            }
        }
    }

    fun validate(classDeclaration: KSClassDeclaration) : Boolean {
        return testInvariant(KSClassDeclaration::validate, logger, "Failed to validate class declaration")(classDeclaration)
            && testInvariant(Invariant::mustHaveQualifiedName, logger, "@SynkAdapter must target classes with qualified names")(classDeclaration)
            && testInvariant({ mustImplementIdResolver(it, synkSymbols.idResolver) }, logger,"@SynkAdapter annotated class ${classDeclaration.qualifiedName?.asString()} must implement IDResolver interface")(classDeclaration)
    }
}