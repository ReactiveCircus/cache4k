package io.github.reactivecircus.cache4k

import androidx.collection.SimpleArrayMap

internal actual class ConcurrentMutableMap<Key : Any, Value : Any> {
    private val map = SimpleArrayMap<Key, Value>()

    actual val size: Int
        get() = map.size()

    actual val values: Collection<Value>
        get() {
            val values = mutableListOf<Value>()
            for (i in 0 until map.size()) {
                values.add(map.valueAt(i))
            }
            return values
        }

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
