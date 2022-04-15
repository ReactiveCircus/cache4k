package io.github.reactivecircus.cache4k

import co.touchlab.stately.collections.IsoMutableSet

/**
 * Adds element and reorders insertion order to have inserted element be placed last,
 * regardless of whether the element was already in the set.
 */
public fun<T> MutableSet<T>.addLastOrReorder(element: T): Boolean {
    val exists = remove(element)
    add(element)

    // respect the contract "true if this set did not already contain the specified element"
    return !exists
}

/**
 * Thread safe access to mutable collection. No-op when running using the new memory model.
 */
public fun <T, R> MutableSet<T>.safeAccess(block: (MutableCollection<T>) -> R): R {
    // if we're of type IsoMutableSet call internal access method
    if (this is IsoMutableSet<T>) {
        return this.access(block)
    }

    return block(this)
}
