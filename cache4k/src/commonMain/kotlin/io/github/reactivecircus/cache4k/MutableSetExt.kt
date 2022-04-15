package io.github.reactivecircus.cache4k

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
