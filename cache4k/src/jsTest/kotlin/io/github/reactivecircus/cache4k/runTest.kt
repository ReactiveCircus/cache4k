package io.github.reactivecircus.cache4k

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise

@OptIn(DelicateCoroutinesApi::class)
actual fun runTest(block: suspend () -> Unit): dynamic = GlobalScope.promise { block() }
