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

package com.pyamsoft.pydroid.ui.debug

import android.app.Application
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.ui.internal.debug.InAppDebugLoggerImpl

/** A logger which captures internal log messages and publishes them on a bus to an in-app view */
public interface InAppDebugLogger {

  /** A compatibility layer with any underlying Android logger system */
  public fun log(
      priority: Int,
      tag: String?,
      message: String,
      throwable: Throwable?,
  )

  public companion object {

    /** Create the logger but do not expose the internal implementation */
    @JvmStatic
    @CheckResult
    public fun Application.createInAppDebugLogger(): InAppDebugLogger {
      return InAppDebugLoggerImpl(this)
    }
  }
}
