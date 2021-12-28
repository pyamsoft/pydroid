package com.pyamsoft.pydroid.util

import kotlinx.coroutines.CancellationException

/** Rethrow cancellation exceptions to continue Coroutine flow, otherwise we handle the error */
public inline fun <R : Any> Throwable.ifNotCancellation(block: () -> R): R {
  return when (this) {
    is CancellationException -> throw this
    else -> block()
  }
}
