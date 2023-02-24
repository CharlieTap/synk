package com.tap.synk.processor

import com.google.devtools.ksp.processing.KSPLogger

internal data class ProcessorContext(
    val symbols: SynkSymbols,
    val poetTypes: SynkPoetTypes,
    val logger: KSPLogger
)
//
//
sealed interface Foo {

    data class Bar(
        val bar: String,
        val baz: Int,
        val bim: Boolean,
    ): Foo

    data class Baz(
        val bing: String,
        val bam: String,
    ): Foo
}
//
//class FooResolver : IDResolver<Foo> {
//    override fun resolveId(crdt: Foo): String {
//        val id = when(crdt) {
//            is Foo.Bar -> crdt.bar
//            is Foo.Baz -> crdt.bam
//        }
//        return id
//    }
//}
//
//public class FooBazMapEncoder : MapEncoder<Foo.Baz> {
//    public override fun encode(crdt: Foo.Baz): Map<String, String> {
//        val map = mutableMapOf<String, String>()
//        map["bing"] = crdt.bing
//        map["bam"] = crdt.bam
//        return map
//    }
//
//    public override fun decode(map: Map<String, String>): Foo.Baz {
//        val crdt = Foo.Baz(map["bing"]!!, map["bam"]!!)
//        return crdt
//    }
//}
//
//private enum class FooType {
//    Bar,
//    Baz
//}
//
//public class FooMapEncoder(
//    private val barMapEncoder: FooBarMapEncoder,
//    private val bazMapEncoder: FooBazMapEncoder,
//) : MapEncoder<Foo> {
//    override fun encode(crdt: Foo): Map<String, String> {
//        val map = when(crdt) {
//            is Foo.Baz -> bazMapEncoder.encode(crdt)
//            is Foo.Bar -> barMapEncoder.encode(crdt)
//        }
//        val type = when(crdt) {
//            is Foo.Baz -> FooType.Bar.ordinal
//            is Foo.Bar -> FooType.Baz.ordinal
//        }.toString()
//
//        return map + mutableMapOf("*type" to type)
//    }
//
//    override fun decode(map: Map<String, String>): Foo {
//        val type = map["*type"]?.toIntOrNull() ?: 0
//        val foo = when(type) {
//            FooType.Bar.ordinal -> barMapEncoder.decode(map)
//            FooType.Baz.ordinal -> bazMapEncoder.decode(map)
//        }
//        return foo
//    }
//
//}

//public enum class FooMapEncoderType {
//    Bar,
//    Baz,
//}
//
//public class FooMapEncoder(
//    barEncoder: MapEncoder<Foo.Bar> = FooBarMapEncoder,
//    bazEncoder: MapEncoder<Foo.Baz> = FooBazMapEncoder,
//) : MapEncoder<Foo> {
//    public override fun encode(crdt: Foo): Map<String, String> {
//        val map = when(crdt) {
//            is Foo.Bar -> barEncoder.encode(crdt)
//            is Foo.Baz -> bazEncoder.encode(crdt)
//        }
//        val type = when(crdt) {
//            is Foo.Bar -> FooMapEncoderType.Bar.ordinal
//            is Foo.Baz -> FooMapEncoderType.Baz.ordinal
//        }
//        return map + mutableMapOf("*type" to type.toString())
//    }
//
//    public override fun decode(map: Map<String, String>): Foo {
//        val type = map["*type"]?.toIntOrNull() ?: 0
//        val crdt = when(type) {
//            FooMapEncoderType.Bar.ordinal -> Foo.Bar("", 0, true)
//            FooMapEncoderType.Baz.ordinal -> Foo.Baz("", "")
//            else -> throw Exception("Unknown encoded sealed class type")
//        }
//        return crdt
//    }
//}
