package io.github.reactivecircus.cache4k

/**
 * An event resulting from a mutative [Cache] operation.
 */
public sealed interface CacheEvent<Key : Any, Value : Any> {
    public val key: Key

    public class Created<Key : Any, Value : Any>(
        override val key: Key,
        public val value: Value,
    ) : CacheEvent<Key, Value> {
        override fun toString(): String {
            return "Created(key=$key, value=$value)"
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as Created<*, *>

            return key == other.key && value == other.value
        }

        override fun hashCode(): Int {
            var result = key.hashCode()
            result = 31 * result + value.hashCode()
            return result
        }
    }

    public class Updated<Key : Any, Value : Any>(
        override val key: Key,
        public val oldValue: Value,
        public val newValue: Value,
    ) : CacheEvent<Key, Value> {
        override fun toString(): String {
            return "Updated(key=$key, oldValue=$oldValue, newValue=$newValue)"
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as Updated<*, *>

            return key == other.key && oldValue == other.oldValue && newValue == other.newValue
        }

        override fun hashCode(): Int {
            var result = key.hashCode()
            result = 31 * result + oldValue.hashCode()
            result = 31 * result + newValue.hashCode()
            return result
        }
    }

    public class Removed<Key : Any, Value : Any>(
        override val key: Key,
        public val value: Value,
    ) : CacheEvent<Key, Value> {
        override fun toString(): String {
            return "Removed(key=$key, value=$value)"
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as Removed<*, *>

            return key == other.key && value == other.value
        }

        override fun hashCode(): Int {
            var result = key.hashCode()
            result = 31 * result + value.hashCode()
            return result
        }
    }

    public class Expired<Key : Any, Value : Any>(
        override val key: Key,
        public val value: Value,
    ) : CacheEvent<Key, Value> {
        override fun toString(): String {
            return "Expired(key=$key, value=$value)"
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as Expired<*, *>

            return key == other.key && value == other.value
        }

        override fun hashCode(): Int {
            var result = key.hashCode()
            result = 31 * result + value.hashCode()
            return result
        }
    }

    public class Evicted<Key : Any, Value : Any>(
        override val key: Key,
        public val value: Value,
    ) : CacheEvent<Key, Value> {
        override fun toString(): String {
            return "Evicted(key=$key, value=$value)"
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as Evicted<*, *>

            return key == other.key && value == other.value
        }

        override fun hashCode(): Int {
            var result = key.hashCode()
            result = 31 * result + value.hashCode()
            return result
        }
    }
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
     * | Initial value    | Operation                | New value | Event                            |
     * |:-----------------|:-------------------------|:----------|:---------------------------------|
     * | {}               | put(K, V)                | {K: V}    | Created(K, V)                    |
     * | {K: V1}          | put(K, V2)               | {K: V2}   | Updated(K, V1, V2)               |
     * | {K: V}           | invalidate(K)            | {}        | Removed(K, V)                    |
     * | {K1: V1, K2: V2} | invalidateAll()          | {}        | Removed(K1, V1), Removed(K2, V2) |
     * | {K: V}           | any operation, K expired | {}        | Expired(K, V)                    |
     * | {K1: V1}         | put(K2, V2), K1 evicted  | {K2: V2}  | Created(K2, V2), Evicted(K1, V1) |
     *
     */
    public fun onEvent(event: CacheEvent<Key, Value>)
}
