package io.github.reactivecircus.cache4k

import org.jetbrains.kotlinx.lincheck.annotations.Operation
import org.jetbrains.kotlinx.lincheck.check
import org.jetbrains.kotlinx.lincheck.strategy.managed.modelchecking.ModelCheckingOptions
import org.jetbrains.kotlinx.lincheck.strategy.stress.StressOptions
import org.junit.jupiter.api.Test

class ReorderingIsoMutableSetLincheckTest {

    private val reorderingIsoMutableSet = ReorderingIsoMutableSet<Int>()

    @Operation
    fun add(element: Int) = reorderingIsoMutableSet.add(element)

    @Operation
    fun remove(element: Int) = reorderingIsoMutableSet.remove(element)

    @Operation
    fun clear() = reorderingIsoMutableSet.clear()

    @Test
    fun stressTest() = StressOptions().check(this::class)

    @Test
    fun modelCheckingTest() = ModelCheckingOptions().check(this::class)
}
