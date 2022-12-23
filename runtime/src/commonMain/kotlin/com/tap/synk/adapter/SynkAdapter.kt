package com.tap.synk.adapter

import com.tap.synk.resolver.IDResolver

interface SynkAdapter<T : Any> : IDResolver<T> {

    /**
     * Only mutable values need to be tracked
     * Should return a HashMap where the key is the property name and the value is the property value
     * Only values that are mutable need to be present in the Hashmap, identifiers and immutable properties should be omitted
     *
     */
    fun encode(crdt: T): HashMap<String, String>

    fun decode(crdt: T, map: HashMap<String, String>): T
}
