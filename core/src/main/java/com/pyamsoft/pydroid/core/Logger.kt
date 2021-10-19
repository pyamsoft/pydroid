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

import androidx.annotation.RestrictTo

/** The PYDroid Logger */
public object Logger : PYDroidLogger {

  private var logger: PYDroidLogger? = null

  override fun d(message: String, vararg args: Any) {
    logger?.d(message, args)
  }

  override fun w(message: String, vararg args: Any) {
    logger?.w(message, args)
  }

  override fun e(throwable: Throwable, message: String, vararg args: Any) {
    logger?.e(throwable, message, args)
  }

  /** Set the logger for the rest of the library */
  @RestrictTo(RestrictTo.Scope.LIBRARY)
  public fun setLogger(logger: PYDroidLogger) {
    this.logger = logger
  }
}

/** A simple logging interface, following a Timber like syntax */
public interface PYDroidLogger {

  /** Debug level log */
  public fun d(message: String, vararg args: Any)

  /** Warning level log */
  public fun w(message: String, vararg args: Any)

  /** Error level log */
  public fun e(throwable: Throwable, message: String, vararg args: Any)
}
