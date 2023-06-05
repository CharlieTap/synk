package com.tap.synk.processor

import com.tschuchort.compiletesting.SourceFile

internal val FOO_DATA_CLASS = SourceFile.kotlin(
    "Foo.kt",
    """
        package com.test.processor
        
        data class Foo(
            private val bar: String,
            private val baz: Int?,
            private val bim: Boolean,
        )
    """,
)

internal val FOO_NOT_IMPLEMENTED_ID_RESOLVER = SourceFile.kotlin(
    "FooResolver.kt",
    """
        package com.test.processor

        import com.tap.synk.annotation.SynkAdapter
        import com.tap.synk.resolver.IDResolver
        
        @SynkAdapter
        class FooResolver
    """,
)

internal val FOO_ID_RESOLVER = SourceFile.kotlin(
    "FooResolver.kt",
    """
        package com.test.processor

        import com.tap.synk.annotation.SynkAdapter
        import com.tap.synk.resolver.IDResolver
        
        @SynkAdapter
        class FooResolver : IDResolver<Foo> {
            override fun resolveId(crdt: Foo): String {
                return crdt.bar
            }
        }
    """,
)

internal val FOO_SEALED_CLASS = SourceFile.kotlin(
    "Foo.kt",
    """
        package com.test.processor
        
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
    """,
)

internal val FOO_SEALED_ID_RESOLVER = SourceFile.kotlin(
    "FooResolver.kt",
    """
        package com.test.processor

        import com.tap.synk.annotation.SynkAdapter
        import com.tap.synk.resolver.IDResolver
        
        @SynkAdapter
        class FooResolver : IDResolver<Foo> {
            override fun resolveId(crdt: Foo): String {
                val id = when(crdt) {
                    is Foo.Bar -> crdt.bar
                    is Foo.Baz -> crdt.bam
                } 
                return id
            }
        }
    """,
)

internal val FOO_COLLECTION_CLASS = SourceFile.kotlin(
    "Foo.kt",
    """
        package com.test.processor
        data class Foo(
            private val bar: List<String>,
            private val baz: String,
            private val bim: Set<Boolean>,
        )
    """,
)

internal val FOO_COLLECTION_ID_RESOLVER = SourceFile.kotlin(
    "FooResolver.kt",
    """
        package com.test.processor

        import com.tap.synk.annotation.SynkAdapter
        import com.tap.synk.resolver.IDResolver
        
        @SynkAdapter
        class FooResolver : IDResolver<Foo> {
            override fun resolveId(crdt: Foo): String {
                return crdt.baz
            }
        }
    """,
)

internal val FOO_DATA_SUB_CLASS = SourceFile.kotlin(
    "Foo.kt",
    """
        package com.test.processor
        data class Foo(
            private val bar: Bar,
            private val baz: String,
        )
    """,
)

internal val FOO_BAR_SUB_CLASS = SourceFile.kotlin(
    "Bar.kt",
    """
        package com.test.processor
        data class Bar(
            private val bim: Bim?,
            private val second: String,
        )
    """,
)

internal val FOO_BIM_SUB_CLASS = SourceFile.kotlin(
    "Bim.kt",
    """
        package com.test.processor
        data class Bim(
            private val first: String,
            private val second: String,
        )
    """,
)

internal val FOO_BAR_SUB_CLASS_RESOLVER = SourceFile.kotlin(
    "FooResolver.kt",
    """
        package com.test.processor

        import com.tap.synk.annotation.SynkAdapter
        import com.tap.synk.resolver.IDResolver
        
        @SynkAdapter
        class FooResolver : IDResolver<Foo> {
            override fun resolveId(crdt: Foo): String {
                return crdt.baz
            }
        }
    """,
)

internal val FOO_COLLECTION_DATA_CLASS = SourceFile.kotlin(
    "Foo.kt",
    """
        package com.test.processor
        data class Foo(
            private val bar: List<Bar>,
        )
    """,
)

internal val BAR_COLLECTION_DATA_CLASS = SourceFile.kotlin(
    "Bar.kt",
    """
        package com.test.processor
        data class Bar(
            private val bim: String,
        )
    """,
)

internal val FOO_COLLECTION_DATA_CLASS_RESOLVER = SourceFile.kotlin(
    "FooResolver.kt",
    """
        package com.test.processor

        import com.tap.synk.annotation.SynkAdapter
        import com.tap.synk.resolver.IDResolver
        
        @SynkAdapter
        class FooResolver : IDResolver<Foo> {
            override fun resolveId(crdt: Foo): String {
                return crdt.baz
            }
        }
    """,
)

internal val BAR_VALUE_CLASS = SourceFile.kotlin(
    "Bar.kt",
    """
        package com.test.processor
        @JvmInline
        value class Bar(val test: Int)
    """,
)

internal val BAR_VALUE_CLASS_SERIALIZER = SourceFile.kotlin(
    "BarSerializer.kt",
    """
        package com.test.processor
        
        import com.tap.synk.annotation.SynkSerializer
        import com.tap.synk.serialize.StringSerializer

        @SynkSerializer
        object BarSerializer: StringSerializer<Bar> {
            override fun serialize(serializable: Bar): String {
                return serializable.test.toString() 
            }

            override fun deserialize(serialized: String): Bar {
                return Bar(serialized.toInt())
            }
        }
    """,
)

internal val BAZ_ENUM_CLASS = SourceFile.kotlin(
    "Baz.kt",
    """
        package com.test.processor
        enum class Baz {
            BIM,
            BAM,
        }
    """,
)

internal val FOO_VALUE_CLASS = SourceFile.kotlin(
    "Foo.kt",
    """
        package com.test.processor
        data class Foo(
            private val bar: Bar,
            private val barNull: Bar?,
            private val baz: Baz,
        )
    """,
)

internal val FOO_RESOLVER = SourceFile.kotlin(
    "FooResolver.kt",
    """
        package com.test.processor

        import com.tap.synk.annotation.SynkAdapter
        import com.tap.synk.resolver.IDResolver
        
        @SynkAdapter
        class FooResolver : IDResolver<Foo> {
            override fun resolveId(crdt: Foo): String {
                return crdt.baz
            }
        }
    """,
)
