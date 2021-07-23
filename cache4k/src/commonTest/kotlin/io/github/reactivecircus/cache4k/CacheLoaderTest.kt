package io.github.reactivecircus.cache4k

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.time.Duration

class CacheLoaderTest {

    private val fakeTimeSource = FakeTimeSource()
    private val expiryDuration = Duration.minutes(1)

    @Test
    fun entryWithAssociatedKeyNotExists_getWithLoader_returnsValueFromLoader() = runTest {
        val cache = Cache.Builder()
            .build<Long, String>()

        var loaderInvokeCount = 0
        val loader = suspend {
            loaderInvokeCount++
            "dog"
        }

        val value = cache.get(1, loader)

        assertEquals(1, loaderInvokeCount)
        assertEquals("dog", value)
    }

    @Test
    fun expiredEntryWithAssociatedKeyExists_getWithLoader_returnsValueFromLoader() = runTest {
        val cache = Cache.Builder()
            .expireAfterWrite(expiryDuration)
            .fakeTimeSource(fakeTimeSource)
            .build<Long, String>()

        cache.put(1, "cat")

        // now expires
        fakeTimeSource += expiryDuration

        var loaderInvokeCount = 0
        val loader = suspend {
            loaderInvokeCount++
            "dog"
        }

        val value = cache.get(1, loader)

        assertEquals(1, loaderInvokeCount)
        assertEquals("dog", value)
    }

    @Test
    fun unexpiredEntryWithAssociatedKeyExists_getWithLoader_returnsExistingValue() = runTest {
        val cache = Cache.Builder()
            .expireAfterAccess(expiryDuration)
            .fakeTimeSource(fakeTimeSource)
            .build<Long, String>()

        cache.put(1, "dog")

        // just before expiry
        fakeTimeSource += expiryDuration - Duration.nanoseconds(1)

        var loaderInvokeCount = 0
        val loader = suspend {
            loaderInvokeCount++
            "cat"
        }

        val value = cache.get(1, loader)

        assertEquals(0, loaderInvokeCount)
        assertEquals("dog", value)
    }

    @Test
    fun loaderThrowsException_getWithLoader_exceptionPropagated() = runTest {
        val cache = Cache.Builder()
            .build<Long, String>()

        var loaderInvokeCount = 0
        val loader = suspend {
            loaderInvokeCount++
            throw IllegalStateException()
        }

        assertFailsWith<IllegalStateException> {
            cache.get(1, loader)
        }

        assertEquals(1, loaderInvokeCount)
        assertNull(cache.get(1))
    }
}
