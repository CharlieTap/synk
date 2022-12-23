package com.tap.synk.abstraction

internal interface Monoid<T> : Semigroup<T> {

    val neutral: T

    fun Collection<T>.fold() = this.reduce(::combine)
}
