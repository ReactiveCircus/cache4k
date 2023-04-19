package io.github.reactivecircus.cache4k

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DefaultCacheTest {

    @Test
    fun noEntryWithAssociatedKeyExists_get_returnsNull() {
        val cache = Cache.Builder<Long, String>().build()

        assertNull(cache.get(1))
    }

    @Test
    fun entryWithAssociatedKeyExists_get_returnsValue() {
        val cache = Cache.Builder<Long, String>().build()

        cache.put(1, "dog")
        cache.put(2, "cat")

        assertEquals("dog", cache.get(1))
        assertEquals("cat", cache.get(2))
    }

    @Test
    fun getMultipleTimesWithSameKey_returnsSameValue() {
        val cache = Cache.Builder<Long, String>().build()

        cache.put(1, "dog")

        val value1 = cache.get(1)
        val value2 = cache.get(1)

        assertEquals("dog", value1)
        assertEquals("dog", value2)
    }

    @Test
    fun valuesReplacedForSameKey_get_returnsLatestValue() {
        val cache = Cache.Builder<Long, String>().build()

        cache.put(1, "dog")
        cache.put(2, "cat")
        cache.put(1, "bird")

        assertEquals("bird", cache.get(1))
        assertEquals("cat", cache.get(2))
    }

    @Test
    fun cacheComplexTypeValueWithHashCode() {
        val cache = Cache.Builder<Long, TypeWithHashCode>().build()

        cache.put(1, TypeWithHashCode("dog", 10))
        cache.put(2, TypeWithHashCode("cat", 15))

        assertEquals(TypeWithHashCode("dog", 10), cache.get(1))
        assertEquals(TypeWithHashCode("cat", 15), cache.get(2))
    }

    @Test
    fun cacheComplexTypeValueWithoutHashCode() {
        val cache = Cache.Builder<Long, TypeWithoutHashCode>().build()

        cache.put(1, TypeWithoutHashCode("dog", 10))

        val value = cache.get(1)

        assertEquals("dog", value?.x)
        assertEquals(10, value?.y)
    }

    @Test
    fun cacheWithComplexTypeKeyWithHashcode() {
        val cache = Cache.Builder<TypeWithHashCode, String>().build()

        cache.put(TypeWithHashCode("a", 1), "dog")

        val value = cache.get(TypeWithHashCode("a", 1))

        assertEquals("dog", value)
    }

    @Test
    fun cacheWithComplexTypeKeyWithoutHashcode() {
        val cache = Cache.Builder<TypeWithoutHashCode, String>().build()

        val key = TypeWithoutHashCode("a", 1)

        cache.put(key, "dog")

        val value1 = cache.get(TypeWithoutHashCode("a", 1))
        val value2 = cache.get(key)

        assertNull(value1)
        assertEquals("dog", value2)
    }

    @Test
    fun cacheWithSameValueAndDifferentKeys() {
        val cache = Cache.Builder<Long, TypeWithoutHashCode>().build()

        val valueToCache = TypeWithoutHashCode("dog", 10)

        cache.put(1, valueToCache)
        cache.put(2, valueToCache)

        assertEquals(valueToCache, cache.get(1))
        assertEquals(valueToCache, cache.get(2))
    }

    @Test
    fun cacheUsingUnitAsKey() {
        val cache = Cache.Builder<Unit, String>().build()

        cache.put(Unit, "dog")
        cache.put(Unit, "cat")

        assertEquals("cat", cache.get(Unit))
    }

    @Test
    fun asMap_returnsAllEntries() {
        val cache = Cache.Builder<Long, String>().build()

        cache.put(1, "dog")
        cache.put(2, "cat")

        assertEquals<Map<in Long, String>>(mapOf(1L to "dog", 2L to "cat"), cache.asMap())
    }

    @Test
    fun asMap_createsDefensiveCopy() {
        val cache = Cache.Builder<Long, String>().build()

        cache.put(1, "dog")
        cache.put(2, "cat")

        val map = cache.asMap() as MutableMap
        map[3] = "bird"

        assertNull(cache.get(3))
    }
}
