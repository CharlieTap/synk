package com.tap.synk.processor.validator

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration

interface AnnotatedValidator {

    companion object Invariant {
        fun factory(
            invariant: (KSClassDeclaration) -> Boolean,
            logger: KSPLogger,
            failureMessage: String
        ): (KSClassDeclaration) -> Boolean {
            return { declaration ->
                invariant(declaration).let { result ->
                    if (!result) {
                        logger.error(failureMessage, declaration)
                    }
                    result
                }
            }
        }
    }

    fun invariants(): Set<(KSClassDeclaration) -> Boolean>

    fun validate(declaration: KSClassDeclaration) = invariants().fold(true) { acc, invariant ->
        invariant(declaration) && acc
    }
}
