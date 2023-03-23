package com.tap.synk.processor.context

import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.Modifier
import com.tap.synk.adapter.SynkAdapter
import com.tap.synk.encode.MapEncoder
import com.tap.synk.processor.ext.asType
import com.tap.synk.resolver.IDResolver
import com.tap.synk.serialize.EnumStringSerializer
import com.tap.synk.serialize.StringSerializer

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

    private val primitiveTypes by lazy {
        setOf(charType, stringType, boolType, intType, shortType, floatType, doubleType, longType, byteType)
    }

    val setType by lazy { resolver.getClassDeclarationByName<Set<*>>()!!.asType() }
    val listType by lazy { resolver.getClassDeclarationByName<List<*>>()!!.asType() }

    val idResolver by lazy { resolver.getClassDeclarationByName<IDResolver<*>>()!!.asType() }
    val mapEncoder by lazy { resolver.getClassDeclarationByName<MapEncoder<*>>()!!.asType() }
    val synkAdapter by lazy { resolver.getClassDeclarationByName<SynkAdapter<*>>()!!.asType() }
    val stringSerializer by lazy { resolver.getClassDeclarationByName<StringSerializer<*>>()!!.asType() }
    val enumStringSerializer by lazy { resolver.getClassDeclarationByName<EnumStringSerializer<*>>()!!.asType() }

    fun isString(type: KSType): Boolean {
        return type == stringType
    }

    fun isPrimitive(type: KSType): Boolean {
        return primitiveTypes.contains(type)
    }

    fun isUserDefinedType(type: KSType): Boolean {
        return type is KSClassDeclaration && type.declaration.packageName != boolType.declaration.packageName
    }

    fun isDataClass(type: KSType): Boolean {
        return type.declaration.modifiers.any { modifier -> modifier == Modifier.DATA }
    }

    fun isSealedClass(type: KSType): Boolean {
        return type is KSClassDeclaration && type.declaration.modifiers.any { modifier -> modifier == Modifier.SEALED }
    }

    fun isCollection(type: KSType): Boolean {
        return type.declaration.packageName == iterableType.declaration.packageName
    }

    fun isArray(type: KSType): Boolean {
        return type == arrayType
    }

    fun stringDecodeFunction(type: KSType): String {
        return when (type) {
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
