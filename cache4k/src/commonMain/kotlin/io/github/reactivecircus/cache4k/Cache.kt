package io.github.reactivecircus.cache4k

import kotlin.time.Duration
import kotlin.time.TimeSource

/**
 * An in-memory key-value cache with support for time-based (expiration) and size-based evictions.
 */
public interface Cache<in Key : Any, Value : Any> {

    /**
     * Returns the value associated with [key] in this cache, or null if there is no
     * cached value for [key].
     */
    public fun get(key: Key): Value?

    /**
     * Returns the value associated with [key] in this cache if exists,
     * otherwise gets the value by invoking [loader], associates the value with [key] in the cache,
     * and returns the cached value.
     *
     * Any exceptions thrown by the [loader] will be propagated to the caller of this function.
     */
    public suspend fun get(key: Key, loader: suspend () -> Value): Value

    /**
     * Associates [value] with [key] in this cache. If the cache previously contained a
     * value associated with [key], the old value is replaced by [value].
     */
    public fun put(key: Key, value: Value)

    /**
     * Discards any cached value for key [key].
     */
    public fun invalidate(key: Key)

    /**
     * Discards all entries in the cache.
     */
    public fun invalidateAll()

    /**
     * Returns a defensive copy of cache entries as [Map].
     */
    public fun asMap(): Map<in Key, Value>

    /**
     * Main entry point for creating a [Cache].
     */
    public interface Builder<K : Any, V : Any> {

        /**
         * Specifies that each entry should be automatically removed from the cache once a fixed duration
         * has elapsed after the entry's creation or the most recent replacement of its value.
         *
         * When [duration] is zero, the cache's max size will be set to 0
         * meaning no values will be cached.
         */
        public fun expireAfterWrite(duration: Duration): Builder<K, V>

        /**
         * Specifies that each entry should be automatically removed from the cache once a fixed duration
         * has elapsed after the entry's creation, the most recent replacement of its value, or its last
         * access.
         *
         * When [duration] is zero, the cache's max size will be set to 0
         * meaning no values will be cached.
         */
        public fun expireAfterAccess(duration: Duration): Builder<K, V>

        /**
         * Specifies the maximum number of entries the cache may contain.
         * Cache eviction policy is based on LRU - i.e. least recently accessed entries get evicted first.
         *
         * When [size] is 0, entries will be discarded immediately and no values will be cached.
         *
         * If not set, cache size will be unlimited.
         */
        public fun maximumCacheSize(size: Long): Builder<K, V>

        /**
         * Specifies a [TimeSource] to be used for expiry checks.
         * If not specified, [TimeSource.Monotonic] will be used.
         */
        public fun timeSource(timeSource: TimeSource): Builder<K, V>

        /**
         * Specifies a [CacheEventListener] to be used to handle cache events.
         */
        public fun eventListener(listener: CacheEventListener<K, V>): Builder<K, V>

        /**
         * Builds a new instance of [Cache] with the specified configurations.
         */
        public fun build(): Cache<K, V>

        public companion object {

            /**
             * Returns a new [Cache.Builder] instance.
             */
            public operator fun <K : Any, V : Any> invoke(): Builder<K, V> = CacheBuilderImpl()
        }
    }
}

/**
 * A default implementation of [Cache.Builder].
 */
internal class CacheBuilderImpl<K : Any, V : Any> : Cache.Builder<K, V> {

    private var expireAfterWriteDuration = Duration.INFINITE

    private var expireAfterAccessDuration = Duration.INFINITE
    private var maxSize = UNSET_LONG
    private var timeSource: TimeSource? = null
    private var eventListener: CacheEventListener<K, V>? = null

    override fun expireAfterWrite(duration: Duration): CacheBuilderImpl<K, V> = apply {
        require(duration.isPositive()) {
            "expireAfterWrite duration must be positive"
        }
        this.expireAfterWriteDuration = duration
    }

    override fun expireAfterAccess(duration: Duration): CacheBuilderImpl<K, V> = apply {
        require(duration.isPositive()) {
            "expireAfterAccess duration must be positive"
        }
        this.expireAfterAccessDuration = duration
    }

    override fun maximumCacheSize(size: Long): CacheBuilderImpl<K, V> = apply {
        require(size >= 0) {
            "maximum size must not be negative"
        }
        this.maxSize = size
    }

    override fun timeSource(timeSource: TimeSource): Cache.Builder<K, V> = apply {
        this.timeSource = timeSource
    }

    override fun eventListener(listener: CacheEventListener<K, V>): Cache.Builder<K, V> = apply {
        eventListener = listener
    }

    override fun build(): Cache<K, V> {
        return RealCache(
            expireAfterWriteDuration,
            expireAfterAccessDuration,
            maxSize,
            timeSource ?: TimeSource.Monotonic,
            eventListener,
        )
    }

    companion object {
        internal const val UNSET_LONG: Long = -1
    }
}
