package com.tap.synk.processor

import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.Modifier
import com.tap.synk.adapter.SynkAdapter
import com.tap.synk.encode.MapEncoder
import com.tap.synk.resolver.IDResolver

internal class SynkSymbols(resolver: Resolver) {

    val charType = resolver.builtIns.charType
    val stringType = resolver.builtIns.stringType
    val boolType = resolver.builtIns.booleanType
    val intType = resolver.builtIns.intType
    val shortType = resolver.builtIns.shortType
    val floatType = resolver.builtIns.floatType
    val doubleType = resolver.builtIns.doubleType
    val longType = resolver.builtIns.longType
    val byteType = resolver.builtIns.byteType
    val arrayType = resolver.builtIns.arrayType
    val iterableType = resolver.builtIns.iterableType

    val idResolver = resolver.getClassDeclarationByName<IDResolver<*>>()!!.asType()
    val mapEncoder = resolver.getClassDeclarationByName<MapEncoder<*>>()!!.asType()
    val synkAdapter = resolver.getClassDeclarationByName<SynkAdapter<*>>()!!.asType()

    fun isString(type: KSType) : Boolean {
        return type == stringType
    }

    fun isUserDefinedType(type: KSType) : Boolean {
        return type is KSClassDeclaration && type.declaration.packageName != boolType.declaration.packageName
    }

    fun isDataClass(type: KSType) : Boolean {
        return type is KSClassDeclaration && type.declaration.modifiers.any { modifier -> modifier == Modifier.DATA  }
    }

    fun isSealedClass(type: KSType) : Boolean {
        return type is KSClassDeclaration && type.declaration.modifiers.any { modifier -> modifier == Modifier.SEALED  }
    }

    fun isCollection(type: KSType) : Boolean {
        return type.declaration.packageName == iterableType.declaration.packageName
    }

    fun isArray(type: KSType) : Boolean {
        return type == arrayType
    }

    fun isCollectionOrArray(type: KSType) : Boolean {
        return isCollection(type) || isArray(type)
    }

    fun stringDecodeFunction(type: KSType) : String {
        return when(type) {
            intType -> ".toInt()"
            boolType -> ".toBoolean()"
            shortType -> ".toShort()"
            floatType -> ".toFloat()"
            doubleType -> ".toDouble()"
            longType -> ".toLong()"
            byteType -> ".toByte()"
            charType -> ".toCharArray().first()"
            else -> ""
        }
    }


}
