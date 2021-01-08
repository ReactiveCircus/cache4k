package io.github.reactivecircus.cache4k

import kotlinx.coroutines.runBlocking

actual fun runTest(block: suspend () -> Unit) = runBlocking { block() }
