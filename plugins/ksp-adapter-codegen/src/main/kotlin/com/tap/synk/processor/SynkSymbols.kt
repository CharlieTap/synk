package com.tap.synk.processor

import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeArgument
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

    val idResolver = resolver.getClassDeclarationByName<IDResolver<*>>()!!.asType()
    val mapEncoder = resolver.getClassDeclarationByName<MapEncoder<*>>()!!.asType()
    val synkAdapter = resolver.getClassDeclarationByName<SynkAdapter<*>>()!!.asType()

    val stringMap = resolver.getClassDeclarationByName<Map<String, String>>()!!.asType()

    fun isString(type: KSType) : Boolean {
        return type == stringType
    }

    fun isComposite(type: KSType) : Boolean {
        return setOf(
            charType,
            stringType,
            boolType,
            intType,
            shortType,
            floatType,
            doubleType,
            longType,
            byteType
        ).contains(type).not()
    }


}
