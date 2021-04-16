package io.github.reactivecircus.cache4k

import io.github.reactivecircus.cache4k.CacheBuilderImpl.Companion.UNSET_LONG
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Duration
import kotlin.time.TimeSource
import kotlin.time.hours

class CacheBuilderTest {

    @Test
    fun expireAfterWrite_zeroDuration() {
        val exception = assertFailsWith<IllegalArgumentException> {
            Cache.Builder.newBuilder()
                .expireAfterWrite(Duration.nanoseconds(0))
                .build<Any, Any>()
        }

        assertEquals("expireAfterWrite duration must be positive", exception.message)
    }

    @Test
    fun expireAfterWrite_positiveDuration() {
        val cache = Cache.Builder.newBuilder()
            .expireAfterWrite(Duration.hours(24))
            .build<Any, Any>() as RealCache

        assertEquals(Duration.hours(24), cache.expireAfterWriteDuration)
    }

    @Test
    fun expireAfterWrite_negativeDuration() {
        val exception = assertFailsWith<IllegalArgumentException> {
            Cache.Builder.newBuilder()
                .expireAfterWrite(Duration.nanoseconds((-1)))
                .build<Any, Any>() as RealCache
        }

        assertEquals("expireAfterWrite duration must be positive", exception.message)
    }

    @Test
    fun expireAfterAccess_zeroDuration() {
        val exception = assertFailsWith<IllegalArgumentException> {
            Cache.Builder.newBuilder()
                .expireAfterAccess(Duration.nanoseconds(0))
                .build<Any, Any>() as RealCache
        }

        assertEquals("expireAfterAccess duration must be positive", exception.message)
    }

    @Test
    fun expireAfterAccess_positiveDuration() {
        val cache = Cache.Builder.newBuilder()
            .expireAfterAccess(Duration.hours(24))
            .build<Any, Any>() as RealCache

        assertEquals(Duration.hours(24), cache.expireAfterAccessDuration)
    }

    @Test
    fun expireAfterAccess_negativeDuration() {
        val exception = assertFailsWith<IllegalArgumentException> {
            Cache.Builder.newBuilder()
                .expireAfterAccess(Duration.nanoseconds(-1))
                .build<Any, Any>() as RealCache
        }

        assertEquals("expireAfterAccess duration must be positive", exception.message)
    }

    @Test
    fun maximumCacheSize_zero() {
        val cache = Cache.Builder.newBuilder()
            .maximumCacheSize(0)
            .build<Any, Any>() as RealCache

        assertEquals(0, cache.maxSize)
    }

    @Test
    fun maximumCacheSize_positiveValue() {
        val cache = Cache.Builder.newBuilder()
            .maximumCacheSize(10)
            .build<Any, Any>() as RealCache

        assertEquals(10, cache.maxSize)
    }

    @Test
    fun maximumCacheSize_negativeValue() {
        val exception = assertFailsWith<IllegalArgumentException> {
            Cache.Builder.newBuilder()
                .maximumCacheSize(-1)
                .build<Any, Any>() as RealCache
        }

        assertEquals("maximum size must not be negative", exception.message)
    }

    @Test
    fun fakeTimeSource() {
        val fakeTimeSource = FakeTimeSource()
        val cache = Cache.Builder.newBuilder()
            .fakeTimeSource(fakeTimeSource)
            .build<Any, Any>() as RealCache

        assertEquals(fakeTimeSource, cache.timeSource)
    }

    @Test
    fun buildWithDefaults() {
        val cache = Cache.Builder.newBuilder().build<Any, Any>() as RealCache

        assertEquals(Duration.INFINITE, cache.expireAfterWriteDuration)
        assertEquals(Duration.INFINITE, cache.expireAfterAccessDuration)
        assertEquals(UNSET_LONG, cache.maxSize)
        assertEquals(TimeSource.Monotonic, cache.timeSource)
    }
}
