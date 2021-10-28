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
import androidx.annotation.RestrictTo

private val IGNORE_NAMES =
    listOf(
        Logger::class.java.name,
        PYDroidLogger::class.java.name,
    )

/** The PYDroid Logger */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public object Logger {

  private var logger: PYDroidLogger? = null

  @CheckResult
  private fun Any.createStackTraceTag(): String {
    return this.run {
      Throwable().stackTrace.first { it.className !in IGNORE_NAMES }.run {
        "($fileName:$lineNumber)"
      }
    }
  }

  /** Debug level log */
  @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
  public fun d(
      message: String,
      vararg args: Any,
  ) {
    val tag = this.createStackTraceTag()
    logger?.d(tag, message, args)
  }

  /** Warning level log */
  @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
  public fun w(
      message: String,
      vararg args: Any,
  ) {
    val tag = this.createStackTraceTag()
    logger?.w(tag, message, args)
  }

  /** Error level log */
  @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
  public fun e(
      throwable: Throwable,
      message: String,
      vararg args: Any,
  ) {
    val tag = this.createStackTraceTag()
    logger?.e(tag, throwable, message, args)
  }

  /** Set the logger for the rest of the library */
  @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
  public fun setLogger(logger: PYDroidLogger) {
    this.logger = logger
  }
}

/** A simple logging interface, following a Timber like syntax */
public interface PYDroidLogger {

  /** Debug level log */
  public fun d(
      tag: String,
      message: String,
      vararg args: Any,
  )

  /** Warning level log */
  public fun w(
      tag: String,
      message: String,
      vararg args: Any,
  )

  /** Error level log */
  public fun e(
      tag: String,
      throwable: Throwable,
      message: String,
      vararg args: Any,
  )
}
