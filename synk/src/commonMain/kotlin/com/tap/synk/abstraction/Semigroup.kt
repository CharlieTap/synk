package com.tap.synk.abstraction

internal interface Semigroup<A> {
    fun combine(a: A, b: A): A
}
