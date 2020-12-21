package com.pyamsoft.pydroid.util

import androidx.annotation.CheckResult

/**
 * Returns whether a collection contains an item based on a condition
 */
@CheckResult
public inline fun <T> Collection<T>.contains(block: (T) -> Boolean): Boolean {
    return this.find(block) != null
}

