package io.github.reactivecircus.cache4k

import co.touchlab.stately.collections.IsoMutableMap

internal actual class ConcurrentMutableMap<Key : Any, Value : Any> {
    private val map = IsoMutableMap<Key, Value>()

    actual val size: Int
        get() = map.size

    actual val values: Collection<Value>
        get() = map.values

    actual operator fun get(key: Key): Value? {
        return map[key]
    }

    actual fun put(key: Key, value: Value): Value? {
        return map.put(key, value)
    }

    actual fun remove(key: Key): Value? {
        return map.remove(key)
    }

    actual fun clear() {
        map.clear()
    }
}
