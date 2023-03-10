package com.tap.synk.processor

import com.google.devtools.ksp.innerArguments
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.Modifier
import com.tap.synk.processor.context.SynkSymbols
import com.tap.synk.processor.ext.containsNestedClasses
import com.tap.synk.processor.ext.isSealed

internal class ClassDeclarationExpander(
    private val symbols: SynkSymbols
) {

    fun expand(classDeclaration: KSClassDeclaration): List<KSClassDeclaration> {
        return when {
            classDeclaration.isSealed() || classDeclaration.containsNestedClasses(symbols) -> {
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

        val childClassTypes = classDeclaration.primaryConstructor?.parameters?.map { param ->
            param.type.resolve()
        } ?: emptyList()

        val childClassDeclarations = childClassTypes.map(KSType::declaration).filterIsInstance<KSClassDeclaration>()

        val childCollectionClassDeclarations = childClassTypes.fold(mutableSetOf<KSClassDeclaration>()){ acc, type ->
            if(symbols.isCollection(type)) {
                val innerTypeDeclaration = type.innerArguments.first().type?.resolve()?.declaration as? KSClassDeclaration
                innerTypeDeclaration?.let {
                    acc.add(innerTypeDeclaration)
                }
                acc
            } else acc
        }
        // constructor param declarations and constructor param collection inner type declarations
        val candidateClassDeclarations = childClassDeclarations + childCollectionClassDeclarations
        // When the class declaration contains a constructor param which is a data class
        val childDataClassDeclarations = candidateClassDeclarations.filter { it.modifiers.contains(Modifier.DATA) }.toSet()
        // When the class declaration contains a constructor param which is a sealed class
        val childSealedClassDeclarations = candidateClassDeclarations.filter { it.modifiers.contains(Modifier.SEALED) }.toSet()

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