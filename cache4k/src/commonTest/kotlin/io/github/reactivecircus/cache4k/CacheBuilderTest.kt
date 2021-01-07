package io.github.reactivecircus.cache4k

import io.github.reactivecircus.cache4k.CacheBuilderImpl.Companion.UNSET_LONG
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Duration
import kotlin.time.TestTimeSource
import kotlin.time.TimeSource
import kotlin.time.hours
import kotlin.time.nanoseconds

class CacheBuilderTest {

    @Test
    fun expireAfterWrite_zeroDuration() {
        val exception = assertFailsWith<IllegalArgumentException> {
            Cache.Builder.newBuilder()
                .expireAfterWrite(0.nanoseconds)
                .build<Any, Any>()
        }

        assertEquals("expireAfterWrite duration must be positive", exception.message)
    }

    @Test
    fun expireAfterWrite_positiveDuration() {
        val cache = Cache.Builder.newBuilder()
            .expireAfterWrite(24.hours)
            .build<Any, Any>() as RealCache

        assertEquals(24.hours, cache.expireAfterWriteDuration)
    }

    @Test
    fun expireAfterWrite_negativeDuration() {
        val exception = assertFailsWith<IllegalArgumentException> {
            Cache.Builder.newBuilder()
                .expireAfterWrite((-1).nanoseconds)
                .build<Any, Any>() as RealCache
        }

        assertEquals("expireAfterWrite duration must be positive", exception.message)
    }

    @Test
    fun expireAfterAccess_zeroDuration() {
        val exception = assertFailsWith<IllegalArgumentException> {
            Cache.Builder.newBuilder()
                .expireAfterAccess(0.nanoseconds)
                .build<Any, Any>() as RealCache
        }

        assertEquals("expireAfterAccess duration must be positive", exception.message)
    }

    @Test
    fun expireAfterAccess_positiveDuration() {
        val cache = Cache.Builder.newBuilder()
            .expireAfterAccess(24.hours)
            .build<Any, Any>() as RealCache

        assertEquals(24.hours, cache.expireAfterAccessDuration)
    }

    @Test
    fun expireAfterAccess_negativeDuration() {
        val exception = assertFailsWith<IllegalArgumentException> {
            Cache.Builder.newBuilder()
                .expireAfterAccess((-1).nanoseconds)
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
    fun timeSource() {
        val testTimeSource = TestTimeSource()
        val cache = Cache.Builder.newBuilder()
            .timeSource(testTimeSource)
            .build<Any, Any>() as RealCache

        assertEquals(testTimeSource, cache.timeSource)
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
