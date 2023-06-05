package com.tap.synk.processor.validator

import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.validate
import com.tap.synk.processor.context.SynkSymbols
import com.tap.synk.processor.validator.AnnotatedValidator.Invariant.factory

internal class SynkAdapterValidator(
    private val synkSymbols: SynkSymbols,
    private val logger: KSPLogger,
) : AnnotatedValidator {
    companion object Invariant {
        internal fun mustHaveQualifiedName(classDeclaration: KSClassDeclaration): Boolean {
            return classDeclaration.qualifiedName?.let { true } ?: false
        }
        internal fun mustImplementIdResolver(classDeclaration: KSClassDeclaration, idResolverType: KSType): Boolean {
            return classDeclaration.getAllSuperTypes().any {
                it.declaration == idResolverType.declaration
            }
        }
    }

    override fun invariants(): Set<(KSClassDeclaration) -> Boolean> {
        return setOf(
            factory(KSClassDeclaration::validate, logger, "Failed to validate class declaration"),
            factory(Invariant::mustHaveQualifiedName, logger, "@SynkAdapter must target classes with qualified names"),
            factory({
                mustImplementIdResolver(it, synkSymbols.idResolver)
            }, logger, "@SynkAdapter annotated class must implement IDResolver interface"),
        )
    }
}
