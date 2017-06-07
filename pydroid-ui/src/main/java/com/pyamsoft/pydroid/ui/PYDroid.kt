/*
 * Copyright 2017 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.ui

import android.content.Context
import android.support.annotation.CheckResult
import android.support.annotation.RestrictTo
import com.pyamsoft.pydroid.PYDroidModule

object PYDroid {

  @RestrictTo(RestrictTo.Scope.LIBRARY) private var component: PYDroidComponent? = null
  @RestrictTo(RestrictTo.Scope.LIBRARY) private var debugMode = false

  @RestrictTo(
      RestrictTo.Scope.LIBRARY) @JvmStatic private fun guaranteeNonNull(): PYDroidComponent {
    val obj = component
    if (obj == null) {
      throw IllegalStateException("Component must undergo initialize(Context, Boolean) before use")
    } else {
      return obj
    }
  }

  /**
   * Return the DEBUG state of the library
   */
  @JvmStatic @CheckResult fun isDebugMode(): Boolean {
    guaranteeNonNull()
    return debugMode
  }

  /**
   * Initialize the library
   */
  @JvmStatic fun initialize(context: Context, debug: Boolean) {
    debugMode = debug
    component = PYDroidComponentImpl(PYDroidModule(context.applicationContext, debug))
  }

  /**
   * For use internally in the library
   */
  @RestrictTo(RestrictTo.Scope.LIBRARY) @JvmStatic internal fun with(
      func: (PYDroidComponent) -> Unit) {
    func(guaranteeNonNull())
  }
}
