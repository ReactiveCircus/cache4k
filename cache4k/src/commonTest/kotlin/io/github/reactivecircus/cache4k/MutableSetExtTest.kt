package io.github.reactivecircus.cache4k

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MutableSetExtTest {

    private val set: MutableSet<String> = mutableSetOf()

    @Test
    fun maintainsOriginalInsertionOrder() {
        set.addLastOrReorder("a")
        set.addLastOrReorder("b")

        assertEquals(listOf("a", "b"), set.toList())
    }

    @Test
    fun reInsertedElementMovedToTheEnd() {
        set.addLastOrReorder("a")
        set.addLastOrReorder("b")
        set.addLastOrReorder("a")

        assertEquals(listOf("b", "a"), set.toList())
    }

    @Test
    fun add_elementExists() {
        set.addLastOrReorder("a")
        assertFalse(set.add("a"))
    }

    @Test
    fun add_elementNotExists() {
        assertTrue(set.addLastOrReorder("a"))
        assertTrue(set.addLastOrReorder("b"))
    }
}
