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
public interface ResultWrapper<T> {

  /**
   * Run an action only when successful result
   *
   * No @CheckResult since this can be the end of the call.
   */
  public fun onSuccess(action: (T) -> Unit): ResultWrapper<T>

  /**
   * Run an action only when failed result
   *
   * No @CheckResult since this can be the end of the call.
   */
  public fun onFailure(action: (Throwable) -> Unit): ResultWrapper<T>

  /** Transform success results, does not change error results */
  @CheckResult public fun <R> map(transform: (T) -> R): ResultWrapper<R>

  public companion object {

    /** Create a success state instance */
    @JvmStatic
    @CheckResult
    public fun <T : Any> success(success: T): ResultWrapper<T> {
      return ResultWrapperImpl(success = success, error = null)
    }

    /** Create a failure state instance */
    @JvmStatic
    @CheckResult
    public fun <T : Any> failure(error: Throwable): ResultWrapper<T> {
      return ResultWrapperImpl(success = null, error = error)
    }
  }
}

private data class ResultWrapperImpl<T>(private val success: T?, private val error: Throwable?) :
    ResultWrapper<T> {

  override fun onSuccess(action: (T) -> Unit): ResultWrapper<T> {
    return this.apply { success?.also(action) }
  }

  override fun onFailure(action: (Throwable) -> Unit): ResultWrapper<T> {
    return this.apply { error?.also(action) }
  }

  override fun <R> map(transform: (T) -> R): ResultWrapper<R> {
    return ResultWrapperImpl(success = success?.let { transform(it) }, error = error)
  }
}
