package io.github.reactivecircus.cache4k

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.minutes

private fun eventListOf(vararg elements: CacheEvent<Long, String>) = listOf(elements = elements)

private class TestEventListener : CacheEventListener<Long, String> {
    val events = mutableMapOf<CacheEventType, List<CacheEvent<Long, String>>>()

    override fun onEvent(event: CacheEvent<Long, String>) {
        events[event.type] = events[event.type]?.let { it + event } ?: listOf(event)
    }
}

class CacheListenerTest {
    private val fakeTimeSource = FakeTimeSource()

    @Test
    fun create() {
        val eventListener = TestEventListener()
        val cache = Cache.Builder<Long, String>()
            .timeSource(fakeTimeSource)
            .eventListener(eventListener)
            .build()

        cache.put(1, "dog")
        cache.put(2, "cat")

        assertEquals(
            mapOf(
                CacheEventType.Created to eventListOf(
                    CacheEvent(CacheEventType.Created, 1, null, "dog"),
                    CacheEvent(CacheEventType.Created, 2, null, "cat"),
                ),
            ),
            eventListener.events,
        )
    }

    @Test
    fun update() {
        val eventListener = TestEventListener()
        val cache = Cache.Builder<Long, String>()
            .timeSource(fakeTimeSource)
            .eventListener(eventListener)
            .build()

        cache.put(1, "dog")
        cache.put(1, "cat")

        assertEquals(
            mapOf(
                CacheEventType.Created to eventListOf(
                    CacheEvent(CacheEventType.Created, 1, null, "dog"),
                ),
                CacheEventType.Updated to eventListOf(
                    CacheEvent(CacheEventType.Updated, 1, "dog", "cat"),
                ),
            ),
            eventListener.events,
        )
    }

    @Test
    fun delete() {
        val eventListener = TestEventListener()
        val cache = Cache.Builder<Long, String>()
            .timeSource(fakeTimeSource)
            .eventListener(eventListener)
            .build()

        cache.put(1, "dog")
        cache.invalidate(1)

        assertEquals(
            mapOf(
                CacheEventType.Created to eventListOf(
                    CacheEvent(CacheEventType.Created, 1, null, "dog"),
                ),
                CacheEventType.Removed to eventListOf(
                    CacheEvent(CacheEventType.Removed, 1, "dog", null),
                ),
            ),
            eventListener.events,
        )
    }

    @Test
    fun invalidateAll() {
        val eventListener = TestEventListener()
        val cache = Cache.Builder<Long, String>()
            .timeSource(fakeTimeSource)
            .eventListener(eventListener)
            .build()

        cache.put(1, "dog")
        cache.put(2, "cat")
        cache.invalidateAll()

        assertEquals(
            mapOf(
                CacheEventType.Created to eventListOf(
                    CacheEvent(CacheEventType.Created, 1, null, "dog"),
                    CacheEvent(CacheEventType.Created, 2, null, "cat"),
                ),
                CacheEventType.Removed to eventListOf(
                    CacheEvent(CacheEventType.Removed, 1, "dog", null),
                    CacheEvent(CacheEventType.Removed, 2, "cat", null),
                ),
            ),
            eventListener.events,
        )
    }

    @Test
    fun evict() {
        val eventListener = TestEventListener()
        val cache = Cache.Builder<Long, String>()
            .timeSource(fakeTimeSource)
            .maximumCacheSize(1)
            .eventListener(eventListener)
            .build()

        cache.put(1, "dog")
        cache.put(2, "cat")

        assertEquals(
            mapOf(
                CacheEventType.Created to eventListOf(
                    CacheEvent(CacheEventType.Created, 1, null, "dog"),
                    CacheEvent(CacheEventType.Created, 2, null, "cat"),
                ),
                CacheEventType.Evicted to eventListOf(
                    CacheEvent(CacheEventType.Evicted, 1, "dog", null),
                ),
            ),
            eventListener.events,
        )
    }

    @Test
    fun expiry() {
        val eventListener = TestEventListener()
        val cache = Cache.Builder<Long, String>()
            .timeSource(fakeTimeSource)
            .expireAfterWrite(1.minutes)
            .eventListener(eventListener)
            .build()

        cache.put(1, "dog")
        fakeTimeSource += 1.minutes
        assertNull(cache.get(1))

        assertEquals(
            mapOf(
                CacheEventType.Created to eventListOf(
                    CacheEvent(CacheEventType.Created, 1, null, "dog"),
                ),
                CacheEventType.Expired to eventListOf(
                    CacheEvent(CacheEventType.Expired, 1, "dog", null),
                ),
            ),
            eventListener.events,
        )
    }
}
