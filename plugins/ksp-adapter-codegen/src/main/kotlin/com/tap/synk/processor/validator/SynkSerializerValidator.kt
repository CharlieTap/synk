package com.tap.synk.processor.validator

import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.validate
import com.tap.synk.processor.context.SynkSymbols
import com.tap.synk.processor.validator.AnnotatedValidator.Invariant.factory

internal class SynkSerializerValidator(
    private val synkSymbols: SynkSymbols,
    private val logger: KSPLogger
) : AnnotatedValidator {

    companion object Invariant {
        internal fun mustHaveQualifiedName(classDeclaration: KSClassDeclaration): Boolean {
            return classDeclaration.qualifiedName?.let { true } ?: false
        }
        internal fun mustImplementStringSerializer(classDeclaration: KSClassDeclaration, stringSerializerType: KSType): Boolean {
            return classDeclaration.getAllSuperTypes().any {
                it.declaration == stringSerializerType.declaration
            }
        }
    }

    override fun invariants(): Set<(KSClassDeclaration) -> Boolean> {
        return setOf(
            factory(KSClassDeclaration::validate, logger, "Failed to validate class declaration"),
            factory(Invariant::mustHaveQualifiedName, logger, "@SynkAdapter must target classes with qualified names"),
            factory({ mustImplementStringSerializer(it, synkSymbols.stringSerializer) }, logger, "@SynkSerializer annotated class must implement StringSerializer interface")
        )
    }
}
