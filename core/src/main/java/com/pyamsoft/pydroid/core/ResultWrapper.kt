/*
 * Copyright 2021 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pydroid.core

import androidx.annotation.CheckResult

/** A simple Result style data wrapper */
public data class ResultWrapper<T : Any>
@PublishedApi
internal constructor(
    @PublishedApi internal val success: T?,
    @PublishedApi internal val error: Throwable?
) {

  /** Return the success object or null */
  @CheckResult
  public fun getOrNull(): T? {
    return success
  }

  /** Return the error object or null */
  @CheckResult
  public fun exceptionOrNull(): Throwable? {
    return error
  }

  /**
   * Run an action only when successful result
   *
   * No @CheckResult since this can be the end of the call.
   */
  public inline fun onSuccess(action: (T) -> Unit): ResultWrapper<T> {
    return this.apply { success?.also(action) }
  }

  /**
   * Run an action only when failed result
   *
   * No @CheckResult since this can be the end of the call.
   */
  public inline fun onFailure(action: (Throwable) -> Unit): ResultWrapper<T> {
    return this.apply { error?.also(action) }
  }

  /** Run an action mapping an error object to a success object and continue the stream */
  @CheckResult
  public inline fun recover(action: (Throwable) -> T): ResultWrapper<T> {
    return ResultWrapper(success = error?.let(action), error = null)
  }

  /** Transform success results, does not change error results */
  @CheckResult
  public inline fun <R : Any> map(transform: (T) -> R?): ResultWrapper<R> {
    return ResultWrapper(success = success?.let(transform), error = error)
  }

  public companion object {

    /** Create a success state instance */
    @JvmStatic
    @CheckResult
    public fun <T : Any> success(success: T): ResultWrapper<T> {
      return ResultWrapper(success = success, error = null)
    }

    /** Create a failure state instance */
    @JvmStatic
    @CheckResult
    public fun <T : Any> failure(error: Throwable): ResultWrapper<T> {
      return ResultWrapper(success = null, error = error)
    }
  }
}
