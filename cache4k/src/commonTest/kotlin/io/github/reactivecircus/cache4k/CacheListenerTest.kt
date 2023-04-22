package io.github.reactivecircus.cache4k

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.minutes

class CacheListenerTest {
    private val fakeTimeSource = FakeTimeSource()
    private val eventListener = TestEventListener()

    @Test
    fun create() {
        val cache = Cache.Builder<Long, String>()
            .timeSource(fakeTimeSource)
            .eventListener(eventListener)
            .build()

        cache.put(1, "dog")
        cache.put(2, "cat")

        assertContentEquals(
            eventListOf(
                CacheEvent.Created(1, "dog"),
                CacheEvent.Created(2, "cat"),
            ),
            eventListener.events,
        )
    }

    @Test
    fun update() {
        val cache = Cache.Builder<Long, String>()
            .timeSource(fakeTimeSource)
            .eventListener(eventListener)
            .build()

        cache.put(1, "dog")
        cache.put(1, "cat")

        assertContentEquals(
            eventListOf(
                CacheEvent.Created(1, "dog"),
                CacheEvent.Updated(1, "dog", "cat"),
            ),
            eventListener.events,
        )
    }

    @Test
    fun delete() {
        val cache = Cache.Builder<Long, String>()
            .timeSource(fakeTimeSource)
            .eventListener(eventListener)
            .build()

        cache.put(1, "dog")
        cache.invalidate(1)

        assertContentEquals(
            eventListOf(
                CacheEvent.Created(1, "dog"),
                CacheEvent.Removed(1, "dog"),
            ),
            eventListener.events,
        )
    }

    @Test
    fun invalidateAll() {
        val cache = Cache.Builder<Long, String>()
            .timeSource(fakeTimeSource)
            .eventListener(eventListener)
            .build()

        cache.put(1, "dog")
        cache.put(2, "cat")
        cache.invalidateAll()

        assertContentEquals(
            eventListOf(
                CacheEvent.Created(1, "dog"),
                CacheEvent.Created(2, "cat"),
                CacheEvent.Removed(1, "dog"),
                CacheEvent.Removed(2, "cat"),
            ),
            eventListener.events,
        )
    }

    @Test
    fun evict() {
        val cache = Cache.Builder<Long, String>()
            .timeSource(fakeTimeSource)
            .maximumCacheSize(1)
            .eventListener(eventListener)
            .build()

        cache.put(1, "dog")
        cache.put(2, "cat")

        assertContentEquals(
            eventListOf(
                CacheEvent.Created(1, "dog"),
                CacheEvent.Created(2, "cat"),
                CacheEvent.Evicted(1, "dog"),
            ),
            eventListener.events,
        )
    }

    @Test
    fun expiry() {
        val cache = Cache.Builder<Long, String>()
            .timeSource(fakeTimeSource)
            .expireAfterWrite(1.minutes)
            .eventListener(eventListener)
            .build()

        cache.put(1, "dog")
        fakeTimeSource += 1.minutes
        assertNull(cache.get(1))

        assertContentEquals(
            eventListOf(
                CacheEvent.Created(1, "dog"),
                CacheEvent.Expired(1, "dog"),
            ),
            eventListener.events,
        )
    }

    @Test
    fun equalsTest() {
        assertContentEquals(
            eventListOf(
                CacheEvent.Created(1, "dog"),
                CacheEvent.Updated(1, "dog", "cat"),
                CacheEvent.Removed(1, "cat"),
                CacheEvent.Expired(1, "dog"),
                CacheEvent.Evicted(1, "catdog")
            ),
            eventListOf(
                CacheEvent.Created(1, "dog"),
                CacheEvent.Updated(1, "dog", "cat"),
                CacheEvent.Removed(1, "cat"),
                CacheEvent.Expired(1, "dog"),
                CacheEvent.Evicted(1, "catdog")
            ),
        )
    }

    @Test
    fun hashCodeTest() {
        assertContentEquals(
            listOf(
                CacheEvent.Created(1, "dog"),
                CacheEvent.Updated(1, "dog", "cat"),
                CacheEvent.Removed(1, "cat"),
                CacheEvent.Expired(1, "dog"),
                CacheEvent.Evicted(1, "catdog")
            ).map { it.hashCode() },
            listOf(
                CacheEvent.Created(1, "dog"),
                CacheEvent.Updated(1, "dog", "cat"),
                CacheEvent.Removed(1, "cat"),
                CacheEvent.Expired(1, "dog"),
                CacheEvent.Evicted(1, "catdog")
            ).map { it.hashCode() },
        )
    }

}

private fun eventListOf(vararg elements: CacheEvent<Long, String>) = listOf(elements = elements)

private class TestEventListener : CacheEventListener<Long, String> {
    val events = mutableListOf<CacheEvent<Long, String>>()

    override fun onEvent(event: CacheEvent<Long, String>) {
        events.add(event)
    }
}

