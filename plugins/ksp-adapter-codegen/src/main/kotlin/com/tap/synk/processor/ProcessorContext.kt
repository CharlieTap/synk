package com.tap.synk.processor

import com.google.devtools.ksp.processing.KSPLogger

internal data class ProcessorContext(
    val symbols: SynkSymbols,
    val logger: KSPLogger
)
