package io.github.reactivecircus.cache4k

public inline fun <K : Any, V : Any> buildCache(init: Cache.Builder.() -> Unit): Cache<K, V> {
    val cacheBuilder = Cache.Builder()
    cacheBuilder.init()
    return cacheBuilder.build()
}

public inline fun <K : Any, V : Any> buildDefaultCache(): Cache<K, V> = buildCache {}
