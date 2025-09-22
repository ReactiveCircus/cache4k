package io.github.reactivecircus.cache4k

import kotlinx.atomicfu.locks.reentrantLock
import kotlinx.atomicfu.locks.withLock
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Provides a mechanism for performing key-based synchronization.
 */
internal class KeyedSynchronizer<Key : Any> {
    private val keyBasedMutexes = ConcurrentMutableMap<Key, MutexEntry>()

    private val mapLock = reentrantLock()

    /**
     * Executes the given [action] under a mutex associated with the [key].
     * When called concurrently, all actions associated with the same [key] are mutually exclusive.
     */
    suspend fun <T> synchronizedFor(key: Key, action: suspend () -> T): T {
        return getMutex(key).withLock {
            try {
                action()
            } finally {
                removeMutex(key)
            }
        }
    }

    /**
     * Try to get a [MutexEntry] for the given [key] from the map.
     * If one cannot be found, create a new [MutexEntry], save it to the map, and return it.
     */
    private fun getMutex(key: Key): Mutex {
        mapLock.withLock {
            val mutexEntry = keyBasedMutexes[key] ?: MutexEntry(Mutex(), 0)
            // increment the counter to indicate a new thread is using the lock
            mutexEntry.counter++
            // save the lock entry to the map if it has just been created
            if (keyBasedMutexes[key] == null) {
                keyBasedMutexes.put(key, mutexEntry)
            }

            return mutexEntry.mutex
        }
    }

    /**
     * Remove the [MutexEntry] associated with the given [key] from the map
     * if no other thread is using the mutex.
     */
    private fun removeMutex(key: Key) {
        mapLock.withLock {
            // decrement the counter to indicate the lock is no longer needed for this thread,
            // then remove the lock entry from map if no other thread is still holding this lock
            val mutexEntry = keyBasedMutexes[key] ?: return
            mutexEntry.counter--
            if (mutexEntry.counter == 0) {
                keyBasedMutexes.remove(key)
            }
        }
    }
}

private class MutexEntry(
    val mutex: Mutex,
    var counter: Int
)
