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
            cacheConfig<Any, Any> { expireAfterWrite(0.nanoseconds) }
        }

        assertEquals("expireAfterWrite duration must be positive", exception.message)
    }

    @Test
    fun expireAfterWrite_positiveDuration() {
        val cache = cacheConfig<Any, Any> {
            expireAfterWrite(24.hours)
        } as RealCache

        assertEquals(24.hours, cache.expireAfterWriteDuration)
    }

    @Test
    fun expireAfterWrite_negativeDuration() {
        val exception = assertFailsWith<IllegalArgumentException> {
            cacheConfig<Any, Any> { expireAfterWrite((-1).nanoseconds) } as RealCache
        }

        assertEquals("expireAfterWrite duration must be positive", exception.message)
    }

    @Test
    fun expireAfterAccess_zeroDuration() {
        val exception = assertFailsWith<IllegalArgumentException> {
            cacheConfig<Any, Any> { expireAfterAccess(0.nanoseconds) } as RealCache
        }
        assertEquals("expireAfterAccess duration must be positive", exception.message)
    }

    @Test
    fun expireAfterAccess_positiveDuration() {
        val cache = cacheConfig<Any, Any> { expireAfterAccess(24.hours) } as RealCache

        assertEquals(24.hours, cache.expireAfterAccessDuration)
    }

    @Test
    fun expireAfterAccess_negativeDuration() {
        val exception = assertFailsWith<IllegalArgumentException> {
            cacheConfig<Any, Any> { expireAfterAccess((-1).nanoseconds) } as RealCache
        }

        assertEquals("expireAfterAccess duration must be positive", exception.message)
    }

    @Test
    fun maximumCacheSize_zero() {
        val cache = cacheConfig<Any, Any> { maximumCacheSize(0) } as RealCache

        assertEquals(0, cache.maxSize)
    }

    @Test
    fun maximumCacheSize_positiveValue() {
        val cache = cacheConfig<Any, Any> { maximumCacheSize(10) } as RealCache

        assertEquals(10, cache.maxSize)
    }

    @Test
    fun maximumCacheSize_negativeValue() {
        val exception = assertFailsWith<IllegalArgumentException> {
            cacheConfig<Any, Any> { maximumCacheSize(-1) } as RealCache
        }

        assertEquals("maximum size must not be negative", exception.message)
    }

    @Test
    fun fakeTimeSource() {
        val fakeTimeSource = FakeTimeSource()
        val cache = cacheConfig<Any, Any> { fakeTimeSource(fakeTimeSource) } as RealCache

        assertEquals(fakeTimeSource, cache.timeSource)
    }

    @Test
    fun buildWithDefaults() {
        val cache = cacheConfig<Any, Any> { } as RealCache

        assertEquals(Duration.INFINITE, cache.expireAfterWriteDuration)
        assertEquals(Duration.INFINITE, cache.expireAfterAccessDuration)
        assertEquals(UNSET_LONG, cache.maxSize)
        assertEquals(TimeSource.Monotonic, cache.timeSource)
    }
}
