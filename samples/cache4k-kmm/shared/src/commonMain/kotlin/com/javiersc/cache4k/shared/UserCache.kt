package com.javiersc.cache4k.shared

import io.github.reactivecircus.cache4k.Cache

val userCache: Cache<Int, User> = Cache.Builder.newBuilder().build()
