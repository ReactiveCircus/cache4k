package io.github.reactivecircus.cache4k

import org.jetbrains.kotlinx.lincheck.annotations.Operation
import org.jetbrains.kotlinx.lincheck.check
import org.jetbrains.kotlinx.lincheck.strategy.managed.modelchecking.ModelCheckingOptions
import org.jetbrains.kotlinx.lincheck.strategy.stress.StressOptions
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.seconds

class Cache4kLincheckTest {

    private val cache = Cache.Builder<Long, String>()
        .expireAfterWrite(5.seconds)
        .expireAfterAccess(2.seconds)
        .maximumCacheSize(5)
        .build()

    @Operation
    fun get(key: Long) {
        cache.get(key)
    }

//    @Operation
//    fun get(key: Long, loader: suspend () -> String) = runTest {
//        cache.get(key, loader)
//    }

    @Operation
    fun put(key: Long, value: String) {
        cache.put(key, value)
    }

    @Operation
    fun invalidate(key: Long) {
        cache.invalidate(key)
    }

    @Operation
    fun invalidateAll() {
        cache.invalidateAll()
    }

    @Operation
    fun asMap() {
        cache.asMap()
    }

    @Test
    fun stressTest() = StressOptions().check(this::class)

    @Test
    fun modelCheckingTest() = ModelCheckingOptions().check(this::class)
}
