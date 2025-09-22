package io.github.reactivecircus.cache4k

internal expect class ConcurrentMutableMap<Key : Any, Value : Any>() {
    val size: Int

    val values: Collection<Value>

    operator fun get(key: Key): Value?

    fun put(key: Key, value: Value): Value?

    fun remove(key: Key): Value?

    fun clear()
}
