package com.tap.synk.processor

import com.tschuchort.compiletesting.SourceFile

internal val FOO_DATA_CLASS = SourceFile.kotlin(
    "Foo.kt", """
        package com.test.processor
        
        data class Foo(
            private val bar: Int,
            private val baz: Int
        )
    """
)


internal val FOO_NOT_IMPLEMENTED_ID_RESOLVER = SourceFile.kotlin(
    "FooResolver.kt", """
        package com.test.processor

        import com.tap.synk.annotation.SynkAdapter
        import com.tap.synk.resolver.IDResolver
        
        @SynkAdapter
        class FooResolver
    """
)

internal val FOO_ID_RESOLVER = SourceFile.kotlin(
    "FooResolver.kt", """
        package com.test.processor

        import com.tap.synk.annotation.SynkAdapter
        import com.tap.synk.resolver.IDResolver
        
        @SynkAdapter
        class FooResolver : IDResolver<Foo> {
    
            override fun resolveId(crdt: Foo): String {
                return crdt.bar.toString()
            }
        }
    """
)