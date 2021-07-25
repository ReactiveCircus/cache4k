package io.github.reactivecircus.cache4k.sample.shared

import io.github.reactivecircus.cache4k.Cache

val userCache: Cache<String, User> = Cache.Builder().build()
