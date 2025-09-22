package io.github.reactivecircus.cache4k

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.RepeatedTest
import kotlin.time.Duration.Companion.seconds

class JvmConcurrencyTest {
    private val fakeTimeSource = FakeTimeSource()

    @RepeatedTest(100)
    fun evictEntriesConcurrently() = runTest {
        val cache = Cache.Builder<Long, String>()
            .maximumCacheSize(2)
            .build()

        // should not produce a ConcurrentModificationException
        repeat(2) {
            launch(Dispatchers.IO) {
                cache.put(it.toLong(), "value for $it")
            }
        }
    }

    @RepeatedTest(100)
    fun expireEntriesConcurrently() = runTest {
        val cache = Cache.Builder<Long, String>()
            .timeSource(fakeTimeSource)
            .expireAfterWrite(2.seconds)
            .build()

        repeat(10) {
            cache.put(it.toLong(), "value for $it")
        }

        // should not produce a ConcurrentModificationException
        repeat(2) {
            launch(Dispatchers.IO) {
                cache.get(it.toLong())
                fakeTimeSource += 1.seconds
            }
        }
    }
}
