package io.github.reactivecircus.cache4k

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.minutes

class CacheInvalidationTest {
    @Test
    fun invalidateByKey_associatedEntryEvicted() {
        val cache = Cache.Builder<Long, String>()
            .build()

        cache.put(1, "dog")
        cache.put(2, "cat")

        cache.invalidate(2)

        assertEquals("dog", cache.get(1))
        assertNull(cache.get(2))
    }

    @Test
    fun invalidateByKey_allExpiredEntriesEvicted() {
        val fakeTimeSource = FakeTimeSource()
        val oneMinute = 1.minutes

        val cache = Cache.Builder<Long, String>()
            .timeSource(fakeTimeSource)
            .expireAfterWrite(oneMinute)
            .build()

        cache.put(1, "dog")
        cache.put(2, "cat")

        fakeTimeSource += oneMinute / 2

        cache.put(3, "bird")

        // first 2 entries now expire
        fakeTimeSource += oneMinute / 2

        cache.invalidate(3)

        // all 3 entries should have been evicted
        assertNull(cache.get(1))
        assertNull(cache.get(2))
        assertNull(cache.get(3))
    }

    @Test
    fun invalidateAll_allEntriesEvicted() {
        val cache = Cache.Builder<Long, String>()
            .build()

        cache.put(1, "dog")
        cache.put(2, "cat")
        cache.put(3, "bird")

        assertEquals("dog", cache.get(1))
        assertEquals("cat", cache.get(2))
        assertEquals("bird", cache.get(3))

        cache.invalidateAll()

        assertNull(cache.get(1))
        assertNull(cache.get(2))
        assertNull(cache.get(3))
    }
}
