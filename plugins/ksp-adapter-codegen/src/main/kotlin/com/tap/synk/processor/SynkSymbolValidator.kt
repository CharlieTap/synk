package com.tap.synk.processor

import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.validate

internal class SynkSymbolValidator(
    private val synkSymbols: SynkSymbols,
    private val logger: KSPLogger
) {
    companion object {
        internal fun invariantMustHaveQualifiedName(classDeclaration: KSClassDeclaration) : Boolean {
            return classDeclaration.qualifiedName?.let { true } ?: false
        }
        internal fun invariantMustImplementIdResolver(classDeclaration: KSClassDeclaration, idResolverType: KSType) : Boolean {
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
            && testInvariant(::invariantMustHaveQualifiedName, logger, "@SynkAdapter must target classes with qualified names")(classDeclaration)
            && testInvariant({ invariantMustImplementIdResolver(it, synkSymbols.idResolver) }, logger,"@SynkAdapter annotated class ${classDeclaration.qualifiedName?.asString()} must implement IDResolver interface")(classDeclaration)
    }
}