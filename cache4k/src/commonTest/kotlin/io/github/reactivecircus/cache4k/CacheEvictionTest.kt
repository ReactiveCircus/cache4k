package io.github.reactivecircus.cache4k

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CacheEvictionTest {

    @Test
    fun maxSizeLimitReached_addNewEntry_oldEntryEvicted() {
        val cache = cacheConfig<Long, String> { maximumCacheSize(2) }

        cache.put(1, "dog")
        cache.put(2, "cat")

        // this exceeds the max size limit
        cache.put(3, "bird")

        assertNull(cache.get(1))
        assertEquals("cat", cache.get(2))
        assertEquals("bird", cache.get(3))

        // this exceeds the max size limit again
        cache.put(4, "dinosaur")

        assertNull(cache.get(1))
        assertNull(cache.get(2))
        assertEquals("bird", cache.get(3))
        assertEquals("dinosaur", cache.get(4))
    }

    @Test
    fun maxSizeLimitReached_replaceCacheEntry_doesNotEvict() {
        val cache = cacheConfig<Long, String> { maximumCacheSize(2) }

        cache.put(1, "dog")
        cache.put(2, "cat")

        // replacing an entry does not change internal cache size
        cache.put(2, "bird")
        cache.put(2, "dinosaur")

        assertEquals("dog", cache.get(1))
        assertEquals("dinosaur", cache.get(2))
    }

    @Test
    fun readCacheEntry_accessOrderChanged() {
        val cache = cacheConfig<Long, String> { maximumCacheSize(3) }
        cache.put(1, "dog")
        cache.put(2, "cat")
        cache.put(3, "bird")

        // read 1st entry - access order becomes 2, 3, 1
        cache.get(1)

        // add a new entry
        cache.put(4, "dinosaur")

        // 2nd entry should be evicted
        assertEquals("dog", cache.get(1))
        assertNull(cache.get(2))
        assertEquals("bird", cache.get(3))
        assertEquals("dinosaur", cache.get(4))
    }

    @Test
    fun replaceCacheValue_accessOrderChanged() {
        val cache = cacheConfig<Long, String> { maximumCacheSize(3) }

        cache.put(1, "dog")
        cache.put(2, "cat")
        cache.put(3, "bird")

        // replace 1st entry - access order becomes 2, 3, 1
        cache.put(1, "rabbit")

        // replace 2nd entry - access order becomes 3, 1, 2
        cache.put(2, "fish")

        // add a new entry
        cache.put(4, "dinosaur")

        // 3rd entry should be evicted
        assertEquals("rabbit", cache.get(1))
        assertEquals("fish", cache.get(2))
        assertNull(cache.get(3))
        assertEquals("dinosaur", cache.get(4))
    }

    @Test
    fun maximumCacheSizeIsOne_addNewEntry_existingEntryEvicted() {
        val cache = cacheConfig<Long, String> { maximumCacheSize(1) }

        cache.put(1, "dog")

        assertEquals("dog", cache.get(1))

        cache.put(2, "cat")

        assertNull(cache.get(1))

        assertEquals("cat", cache.get(2))

        cache.put(1, "dog")

        assertEquals("dog", cache.get(1))
        assertNull(cache.get(2))
    }

    @Test
    fun maximumCacheSizeIsZero_noValuesCached() {
        val cache = cacheConfig<Long, String> { maximumCacheSize(0) }
        cache.put(1, "dog")
        cache.put(2, "cat")

        assertNull(cache.get(1))
        assertNull(cache.get(2))
    }
}
