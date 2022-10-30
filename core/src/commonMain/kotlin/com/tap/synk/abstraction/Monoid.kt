package com.tap.synk.abstraction

interface Monoid<T> {

    val neutral: T
    fun combine(a: T, b: T): T

    fun Collection<T>.fold() = this.reduce(::combine)
}
