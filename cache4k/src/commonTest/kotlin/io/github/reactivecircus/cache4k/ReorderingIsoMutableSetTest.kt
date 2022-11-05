package io.github.reactivecircus.cache4k

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ReorderingIsoMutableSetTest {

    private val set = ReorderingIsoMutableSet<String>()

    @Test
    fun maintainsOriginalInsertionOrder() {
        set.add("a")
        set.add("b")

        assertEquals(listOf("a", "b"), set.toList())
    }

    @Test
    fun reInsertedElementMovedToTheEnd() {
        set.add("a")
        set.add("b")
        set.add("a")

        assertEquals(listOf("b", "a"), set.toList())
    }

    @Test
    fun add_elementExists() {
        set.add("a")
        assertFalse(set.add("a"))
    }

    @Test
    fun add_elementNotExists() {
        assertTrue(set.add("a"))
        assertTrue(set.add("b"))
    }
}
