package io.github.reactivecircus.cache4k

import io.github.reactivecircus.cache4k.CacheBuilderImpl.Companion.UNSET_LONG
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.TimeSource

class CacheBuilderTest {

    @Test
    fun expireAfterWrite_zeroDuration() {
        val exception = assertFailsWith<IllegalArgumentException> {
            Cache.Builder<Any, Any>()
                .expireAfterWrite(0.nanoseconds)
                .build()
        }

        assertEquals("expireAfterWrite duration must be positive", exception.message)
    }

    @Test
    fun expireAfterWrite_positiveDuration() {
        val cache = Cache.Builder<Any, Any>()
            .expireAfterWrite(24.hours)
            .build() as RealCache

        assertEquals(24.hours, cache.expireAfterWriteDuration)
    }

    @Test
    fun expireAfterWrite_negativeDuration() {
        val exception = assertFailsWith<IllegalArgumentException> {
            Cache.Builder<Any, Any>()
                .expireAfterWrite((-1).nanoseconds)
                .build() as RealCache
        }

        assertEquals("expireAfterWrite duration must be positive", exception.message)
    }

    @Test
    fun expireAfterAccess_zeroDuration() {
        val exception = assertFailsWith<IllegalArgumentException> {
            Cache.Builder<Any, Any>()
                .expireAfterAccess(0.nanoseconds)
                .build() as RealCache
        }

        assertEquals("expireAfterAccess duration must be positive", exception.message)
    }

    @Test
    fun expireAfterAccess_positiveDuration() {
        val cache = Cache.Builder<Any, Any>()
            .expireAfterAccess(24.hours)
            .build() as RealCache

        assertEquals(24.hours, cache.expireAfterAccessDuration)
    }

    @Test
    fun expireAfterAccess_negativeDuration() {
        val exception = assertFailsWith<IllegalArgumentException> {
            Cache.Builder<Any, Any>()
                .expireAfterAccess((-1).nanoseconds)
                .build() as RealCache
        }

        assertEquals("expireAfterAccess duration must be positive", exception.message)
    }

    @Test
    fun maximumCacheSize_zero() {
        val cache = Cache.Builder<Any, Any>()
            .maximumCacheSize(0)
            .build() as RealCache

        assertEquals(0, cache.maxSize)
    }

    @Test
    fun maximumCacheSize_positiveValue() {
        val cache = Cache.Builder<Any, Any>()
            .maximumCacheSize(10)
            .build() as RealCache

        assertEquals(10, cache.maxSize)
    }

    @Test
    fun maximumCacheSize_negativeValue() {
        val exception = assertFailsWith<IllegalArgumentException> {
            Cache.Builder<Any, Any>()
                .maximumCacheSize(-1)
                .build() as RealCache
        }

        assertEquals("maximum size must not be negative", exception.message)
    }

    @Test
    fun fakeTimeSource() {
        val fakeTimeSource = FakeTimeSource()
        val cache = Cache.Builder<Any, Any>()
            .timeSource(fakeTimeSource)
            .build() as RealCache

        assertEquals(fakeTimeSource, cache.timeSource)
    }

    @Test
    fun buildWithDefaults() {
        val cache = Cache.Builder<Any, Any>().build() as RealCache

        assertEquals(Duration.INFINITE, cache.expireAfterWriteDuration)
        assertEquals(Duration.INFINITE, cache.expireAfterAccessDuration)
        assertEquals(UNSET_LONG, cache.maxSize)
        assertEquals(TimeSource.Monotonic, cache.timeSource)
    }
}
