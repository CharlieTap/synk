package com.tap.synk.diff

import kotlin.test.Test
import kotlin.test.assertEquals

class ReflectionsDifferTest {

    @Test
    fun `can turn object into meta`() {
        val differ = ReflectionsObjectDiffer()

        val crdt1 = CRDT(
            "test",
            "test",
            "test",
        )

        val crdt2 = CRDT(
            "test",
            "test2",
            "test",
        )

        val diff = differ.diff(crdt1, crdt2)

        assertEquals(setOf("secondName"), diff)
    }

}

private data class CRDT(
    private val name: String,
    private val secondName: String,
    private val thirdName: String
)