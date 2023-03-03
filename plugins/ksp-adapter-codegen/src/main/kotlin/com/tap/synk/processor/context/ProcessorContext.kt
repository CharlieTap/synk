package com.tap.synk.processor.context

import com.google.devtools.ksp.processing.KSPLogger

internal data class ProcessorContext(
    val symbols: SynkSymbols,
    val poetTypes: SynkPoetTypes,
    val logger: KSPLogger
)
