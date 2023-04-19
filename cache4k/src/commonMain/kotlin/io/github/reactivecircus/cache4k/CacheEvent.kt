package io.github.reactivecircus.cache4k

/**
 * An event resulting from a mutative [Cache] operation.
 */
public data class CacheEvent<Key : Any, Value : Any>(
    val type: CacheEventType,
    val key: Key,
    val oldValue: Value?,
    val newValue: Value?,
)

public enum class CacheEventType {
    Created,
    Updated,
    Removed,
    Expired,
    Evicted,
}

/**
 * Definition of the contract for implementing listeners to receive [CacheEvent]s from a [Cache].
 */
public fun interface CacheEventListener<Key : Any, Value : Any> {
    /**
     * Invoked on [CacheEvent] firing.
     *
     * Cache entry event firing behaviors for mutative methods:
     *
     * | Initial value      | Operation                 | New value | Event (type, key, oldValue, newValue)            |
     * |:-------------------|:--------------------------|:----------|:-------------------------------------------------|
     * | {}                 | put(K, V)                 | {K: V}    | (Created, K, null, V)                            |
     * | {K: V1}            | put(K, V2)                | {K: V2}   | (Updated, K, V1, V2)                             |
     * | {K: V}             | invalidate(K)             | {}        | (Removed, K, V, null)                            |
     * | {K1: V1, K2: V2}   | invalidateAll()           | {}        | (Removed, K1, V1, null), (Removed, K2, V2, null) |
     * | {K: V}             | any operation, K expired  | {}        | (Expired, K, V, null)                            |
     * | {K1: V1}           | put(K2, V2), K1 evicted   | {K2: V2}  | (Evicted, K1, V1, null), (Created, K2, null, V2) |
     *
     */
    public fun onEvent(event: CacheEvent<Key, Value>)
}
