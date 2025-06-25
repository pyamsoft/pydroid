/*
 * Copyright 2025 pyamsoft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pydroid.util

import androidx.annotation.CheckResult

/** A simple Result style data wrapper */
@ConsistentCopyVisibility
public data class ResultWrapper<T : Any>
@PublishedApi
internal constructor(
    @PublishedApi internal val data: T?,
    @PublishedApi internal val error: Throwable?
) {

  /** Check that this ResultWrapper is valid */
  @PublishedApi
  internal fun validateWrapper() {
    if (data == null && error == null) {
      throw IllegalStateException("ResultWrapper is missing both a data and an error value.")
    }
  }

  /** Return the success object or throws an exception */
  @CheckResult
  public fun getOrThrow(): T {
    return getOrNull() ?: throw IllegalStateException("ResultWrapper is not Success type")
  }

  /** Return the success object or null */
  @CheckResult
  public fun getOrNull(): T? {
    validateWrapper()
    return data
  }

  /** Return the error object or null */
  @CheckResult
  public fun exceptionOrNull(): Throwable? {
    validateWrapper()

    return error
  }

  /** Return the error object or throws an exception */
  @CheckResult
  public fun exceptionOrThrow(): Throwable {
    return exceptionOrNull() ?: throw IllegalStateException("ResultWrapper is not Error type")
  }

  /**
   * Wrap an operation in a try-catch, and if it throws an exception, return an error ResultWrapper
   */
  @CheckResult
  @PublishedApi
  internal inline fun internallyWrap(block: () -> ResultWrapper<T>): ResultWrapper<T> {
    return try {
      block()
    } catch (e: Throwable) {
      ResultWrapper(data = null, error = e)
    }
  }

  /**
   * Run an action regardless of result success or failure
   *
   * No @CheckResult since this can be the end of the call.
   *
   * If the transform function throws an error, it will be caught and morph the ResultWrapper into
   * an error ResultWrapper
   */
  public inline fun onFinally(action: () -> Unit): ResultWrapper<T> {
    validateWrapper()

    return internallyWrap { this.apply { action() } }
  }

  /**
   * Run an action only when successful result
   *
   * No @CheckResult since this can be the end of the call.
   *
   * If the transform function throws an error, it will be caught and morph the ResultWrapper into
   * an error ResultWrapper
   */
  public inline fun onSuccess(action: (T) -> Unit): ResultWrapper<T> {
    validateWrapper()

    return internallyWrap { this.apply { data?.also(action) } }
  }

  /**
   * Run an action only when failed result
   *
   * No @CheckResult since this can be the end of the call.
   *
   * If the transform function throws an error, it will be caught and morph the ResultWrapper into
   * an error ResultWrapper
   */
  public inline fun onFailure(action: (Throwable) -> Unit): ResultWrapper<T> {
    validateWrapper()

    return internallyWrap { this.apply { error?.also(action) } }
  }

  /**
   * Run an action mapping an error object to a success object and continue the stream
   *
   * If this is a Success, this operation is a no-op
   *
   * If the transform function throws an error, it will be caught and morph the ResultWrapper into
   * an error ResultWrapper
   */
  @CheckResult
  public inline fun recover(transform: (Throwable) -> T): ResultWrapper<T> {
    validateWrapper()

    return try {
      error.let { if (it == null) this else ResultWrapper(data = transform(it), error = null) }
    } catch (e: Throwable) {
      ResultWrapper(data = null, error = e)
    }
  }

  /**
   * Transform success results, does not change error results
   *
   * If the transform function throws an error, it will be caught and morph the ResultWrapper into
   * an error ResultWrapper
   */
  @CheckResult
  public inline fun <R : Any> map(transform: (T) -> R): ResultWrapper<R> {
    validateWrapper()

    return try {
      ResultWrapper(data = data?.let(transform), error = error)
    } catch (e: Throwable) {
      ResultWrapper(data = null, error = e)
    }
  }

  public companion object {

    /** Create a success state instance */
    @JvmStatic
    @CheckResult
    public fun <T : Any> success(success: T): ResultWrapper<T> {
      return ResultWrapper(data = success, error = null)
    }

    /** Create a failure state instance */
    @JvmStatic
    @CheckResult
    public fun <T : Any> failure(error: Throwable): ResultWrapper<T> {
      return ResultWrapper(data = null, error = error)
    }
  }
}
