package io.github.reactivecircus.cache4k

import java.util.concurrent.Executors
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test

@OptIn(ExperimentalTime::class)
class JvmConcurrencyTest {

    private val fakeTimeSource = FakeTimeSource()

    @Test
    fun evictEntriesConcurrently() = runBlocking {
        val cache = Cache.Builder()
            .maximumCacheSize(2)
            .build<Long, String>()

        // should not produce a ConcurrentModificationException
        repeat(10) {
            launch(Executors.newSingleThreadExecutor().asCoroutineDispatcher()) {
                cache.put(it.toLong(), "value for $it")
            }
        }
    }

    @Test
    fun expireEntriesConcurrently() = runBlocking {
        val cache = Cache.Builder()
            .fakeTimeSource(fakeTimeSource)
            .expireAfterWrite(Duration.seconds(2))
            .build<Long, String>()

        repeat(10) {
            cache.put(it.toLong(), "value for $it")
        }

        // should not produce a ConcurrentModificationException
        repeat(10) {
            launch(Executors.newSingleThreadExecutor().asCoroutineDispatcher()) {
                cache.get(it.toLong())
                fakeTimeSource += Duration.seconds(1)
            }
        }
    }
}
