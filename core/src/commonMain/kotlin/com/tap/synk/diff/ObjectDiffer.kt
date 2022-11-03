package com.tap.synk.diff

interface ObjectDiffer<CRDT> {
    fun diff(old: CRDT, new: CRDT) : Set<String>
}