package com.tap.synk.processor

import com.google.devtools.ksp.processing.Resolver
import com.tap.synk.adapter.SynkAdapter
import com.tap.synk.encode.MapEncoder

import com.tap.synk.resolver.IDResolver

internal class SynkSymbols(resolver: Resolver) {

    val stringType = resolver.builtIns.stringType
    val intType = resolver.builtIns.intType

    val idResolver = resolver.getClassDeclarationByName<IDResolver<*>>().asType()
    val mapEncoder = resolver.getClassDeclarationByName<MapEncoder<*>>().asType()
    val synkAdapter = resolver.getClassDeclarationByName<SynkAdapter<*>>().asType()
}
