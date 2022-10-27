package com.tap.synk.merge

interface Mergeable<T> {
    fun merge(other: T): T
}