package io.github.reactivecircus.cache4k

public fun <K : Any, V : Any> buildCache(init: Cache.Builder.() -> Unit): Cache<K, V> {
    val cacheBuilder = Cache.Builder()
    cacheBuilder.init()
    return cacheBuilder.build()
}

public fun <K : Any, V : Any> defaultBuildCache(): Cache<K, V> = buildCache {}

