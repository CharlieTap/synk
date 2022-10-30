package com.tap.synk.meta.transformer

import com.tap.synk.meta.Meta

/**
 *
 */
interface MetaTransformer<in T> {
    fun toMeta(crdt: T): Meta
}
