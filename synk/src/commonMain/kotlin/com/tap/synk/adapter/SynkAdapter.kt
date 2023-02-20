package com.tap.synk.adapter

import com.tap.synk.encode.MapEncoder
import com.tap.synk.resolver.IDResolver

interface SynkAdapter<T : Any> : IDResolver<T>, MapEncoder<T>
//
//
//data class Foo(val test: String)
//
//class FooResolver : IDResolver<Foo> {
//
//    override fun resolveId(crdt: Foo): String {
//        TODO("Not yet implemented")
//    }
//}
//
//class FooMapEncoder : MapEncoder<Foo> {
//    override fun encode(crdt: Foo): Map<String, String> {
//        TODO("Not yet implemented")
//    }
//
//    override fun decode(map: Map<String, String>): Foo {
//        TODO("Not yet implemented")
//    }
//
//}
//
//class FooSynkAdapter(
//    private val fooResolver: IDResolver<Foo> = FooResolver(),
//    private val fooMapEncoder: MapEncoder<Foo> = FooMapEncoder()
//) : SynkAdapter<Foo>, IDResolver<Foo> by fooResolver, MapEncoder<Foo> by fooMapEncoder
//
//fun test(synkAdapter: SynkAdapter<Foo>) {
//
//}
//
//fun another() {
//    test(FooSynkAdapter())
//}
