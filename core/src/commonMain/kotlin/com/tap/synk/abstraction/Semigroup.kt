package com.tap.synk.abstraction

interface Semigroup<A> {
    fun combine(a: A, b: A) : A
}