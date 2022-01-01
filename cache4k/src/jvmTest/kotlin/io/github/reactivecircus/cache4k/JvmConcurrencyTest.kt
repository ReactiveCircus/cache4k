package io.github.reactivecircus.cache4k

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.util.concurrent.Executors
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class JvmConcurrencyTest {

    private val fakeTimeSource = FakeTimeSource()

    @Test
    fun evictEntriesConcurrently() = runTest {
        val cache = cacheConfig<Long, String> { maximumCacheSize(2) }
        // should not produce a ConcurrentModificationException
        repeat(10) {
            launch(Executors.newSingleThreadExecutor().asCoroutineDispatcher()) {
                cache.put(it.toLong(), "value for $it")
            }
        }
    }

    @Test
    fun expireEntriesConcurrently() = runTest {
        val cache = cacheConfig<Long, String> {
            fakeTimeSource(fakeTimeSource)
            expireAfterWrite(2.seconds)
        }

        repeat(10) {
            cache.put(it.toLong(), "value for $it")
        }

        // should not produce a ConcurrentModificationException
        repeat(10) {
            launch(Executors.newSingleThreadExecutor().asCoroutineDispatcher()) {
                cache.get(it.toLong())
                fakeTimeSource += 1.seconds
            }
        }
    }
}
