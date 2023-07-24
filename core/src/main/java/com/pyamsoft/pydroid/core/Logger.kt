/*
 * Copyright 2023 pyamsoft
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
import androidx.annotation.RestrictTo
import androidx.annotation.VisibleForTesting

private val IGNORE_NAMES =
    listOf(
        Logger::class.java.name,
        PYDroidLogger::class.java.name,
    )

/** The PYDroid Logger */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public object Logger {

  /** PublishedApi so we can inline the logger functions */
  private var logger: PYDroidLogger? = null

  /** PublishedApi so we can inline the logger functions */
  @CheckResult
  private fun Any.createStackTraceTag(): String {
    return this.run {
      Throwable()
          .stackTrace
          .first { it.className !in IGNORE_NAMES }
          .run { "($fileName:$lineNumber)" }
    }
  }

  /** Debug level log */
  @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
  public fun d(message: () -> String) {
    val tag = this.createStackTraceTag()
    logger?.d(tag, message)
  }

  /** Warning level log */
  @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
  public fun w(message: () -> String) {
    val tag = this.createStackTraceTag()
    logger?.w(tag, message)
  }

  /** Error level log */
  @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
  public fun e(
      throwable: Throwable,
      message: () -> String,
  ) {
    val tag = this.createStackTraceTag()
    logger?.e(tag, throwable, message)
  }

  /** Set the logger for the rest of the library */
  @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
  public fun setLogger(logger: PYDroidLogger) {
    this.logger = logger
  }

  /** Clear the logger for the rest of the library */
  @VisibleForTesting
  @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
  internal fun resetLogger() {
    this.logger = null
  }
}

/** A simple logging interface, following a Timber like syntax */
public interface PYDroidLogger {

  /** Debug level log */
  @Deprecated("Use the lazy form of d()")
  public fun d(
      tag: String,
      message: String,
      vararg args: Any,
  )

  /** Warning level log */
  @Deprecated("Use the lazy form of w()")
  public fun w(
      tag: String,
      message: String,
      vararg args: Any,
  )

  /** Error level log */
  @Deprecated("Use the lazy form of e()")
  public fun e(
      tag: String,
      throwable: Throwable,
      message: String,
      vararg args: Any,
  )

  /** Debug level log */
  public fun d(
      tag: String,
      message: () -> String,
  )

  /** Warning level log */
  public fun w(
      tag: String,
      message: () -> String,
  )

  /** Error level log */
  public fun e(
      tag: String,
      throwable: Throwable,
      message: () -> String,
  )
}
