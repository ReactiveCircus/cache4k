package io.github.reactivecircus.cache4k

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class KeyedSynchronizerTest {
    private val synchronizer = KeyedSynchronizer<String>()
    private val testDispatcher = StandardTestDispatcher()

    @Test
    fun actionsAssociatedWithTheSameKeyAreMutuallyExclusive() = runTest(testDispatcher) {
        val key = "a"
        var action1Started = false
        var action2Started = false
        var action3Started = false

        val actionTime = 50L

        // run action with synchronizer using the same key on 3 different threads concurrently
        launch {
            synchronizer.synchronizedFor(key) {
                action1Started = true
                delay(actionTime)
            }
        }
        launch {
            synchronizer.synchronizedFor(key) {
                action2Started = true
                delay(actionTime)
            }
        }
        launch {
            synchronizer.synchronizedFor(key) {
                action3Started = true
                delay(actionTime)
            }
        }

        delay(10)


        // action 1 starts immediately
        assertTrue(action1Started)
        assertFalse(action2Started)
        assertFalse(action3Started)

        delay(actionTime + 10)

        // action 1 completes, action 2 starts
        assertTrue(action2Started)
        assertFalse(action3Started)

        delay(actionTime + 10)

        // action 2 completes, action 3 starts
        assertTrue(action3Started)
    }

    @Test
    fun actionsAssociatedWithDifferentKeys_run_concurrently() = runTest(testDispatcher) {
        var action1Started = false
        var action2Started = false
        var action3Started = false

        val actionTime = 50L

        // run action with synchronizer using different keys on 3 different threads concurrently
        launch {
            synchronizer.synchronizedFor("a") {
                action1Started = true
                delay(actionTime)
            }
        }
        launch {
            synchronizer.synchronizedFor("b") {
                action2Started = true
                delay(actionTime)
            }
        }
        launch {
            synchronizer.synchronizedFor("c") {
                action3Started = true
                delay(actionTime)
            }
        }

        delay(10)

        // all 3 actions should have started
        assertTrue(action1Started)
        assertTrue(action2Started)
        assertTrue(action3Started)
    }

    @Test
    fun newActionUsingSameKeyFromDifferentThread_is_queuedAfterExistingBlockedActions() =
        runTest(testDispatcher) {
            val key = "a"
            var action1Started = false
            var action2Started = false
            var action3Started = false

            val actionTime = 50L

            // start running action with synchronizer using the same key on 2 different threads concurrently
            launch {
                // 1st action
                synchronizer.synchronizedFor(key) {
                    action1Started = true
                    delay(actionTime)
                }

                // start running 3rd action once 1st action has finished
                launch {
                    // 3rd action
                    synchronizer.synchronizedFor(key) {
                        action3Started = true
                        delay(actionTime)
                    }
                }
            }
            launch {
                // 2nd action
                synchronizer.synchronizedFor(key) {
                    action2Started = true
                    delay(actionTime)
                }
            }

            delay(10)

            // action 1 starts immediately
            assertTrue(action1Started)
            assertFalse(action2Started)
            assertFalse(action3Started)

            delay(actionTime + 10)

            // action 1 completes, action 2 starts, action 3 is blocked
            assertTrue(action2Started)
            assertFalse(action3Started)

            delay(actionTime + 10)

            // action 2 completes, action 3 starts
            assertTrue(action3Started)
        }

    @Test
    fun whenActionUsingSameKeyFromAnotherThreadThrowsErrorTheNextAction_is_unblocked() =
        runTest(testDispatcher) {
            val key = "a"
            var action1Started = false
            var action2Started = false
            var action3Started = false

            val actionTime = 50L

            // start running action with synchronizer using the same key on 2 different threads concurrently
            launch {
                // 1st action throws an exception
                runCatching {
                    synchronizer.synchronizedFor<String>(key) {
                        action1Started = true
                        delay(actionTime)
                        throw Exception()
                    }
                }

                // start running 3rd action once 1st action has finished
                launch {
                    // 3rd action
                    synchronizer.synchronizedFor(key) {
                        action3Started = true
                        delay(actionTime)
                    }
                }
            }
            launch {
                // 2nd action
                synchronizer.synchronizedFor(key) {
                    action2Started = true
                    delay(actionTime)
                }
            }

            delay(10)

            // action 1 starts immediately
            assertTrue(action1Started)
            assertFalse(action2Started)
            assertFalse(action3Started)

            delay(actionTime + 10)

            // action 1 completes (failed), action 2 starts, action 3 is blocked
            assertTrue(action2Started)
            assertFalse(action3Started)

            delay(actionTime + 10)

            // action 2 completes, action 3 starts
            assertTrue(action3Started)
        }
}
