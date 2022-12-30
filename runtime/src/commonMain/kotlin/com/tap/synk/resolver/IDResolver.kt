package com.tap.synk.resolver

interface IDResolver<T> {
    fun resolveId(crdt: T): String
}
