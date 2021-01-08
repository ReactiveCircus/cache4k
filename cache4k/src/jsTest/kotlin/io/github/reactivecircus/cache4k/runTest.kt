package io.github.reactivecircus.cache4k

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise

actual fun runTest(block: suspend () -> Unit): dynamic = GlobalScope.promise { block() }
