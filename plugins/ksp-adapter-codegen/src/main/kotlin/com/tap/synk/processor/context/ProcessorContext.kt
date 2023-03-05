package com.tap.synk.processor.context

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger

internal interface ProcessorContext {
    val codeGenerator: CodeGenerator
    val logger: KSPLogger
    val poetTypes: SynkPoetTypes
    val symbols: SynkSymbols
}
