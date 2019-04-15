/*
 * Copyright 2019 Peter Kenji Yamanaka
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
 *
 */

package com.pyamsoft.pydroid.ui

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.ui.theme.Theming

internal object Injector {

  internal const val NAME = "com.pyamsoft.pydroid.INJECTOR"
  internal const val THEMING = "com.pyamsoft.pydroid.THEMING_INJECTOR"

  @JvmStatic
  @CheckResult
  @SuppressLint("WrongConstant")
  private fun <T : Any> obtain(
    context: Context,
    name: String
  ): T {
    val service: Any? = context.getSystemService(name)
    if (service == null) {
      throw IllegalStateException("Unable to locate service: $name")
    } else {
      @Suppress("UNCHECKED_CAST")
      return service as T
    }
  }

  @JvmStatic
  @CheckResult
  fun <T : Any> obtain(context: Context): T {
    return obtain(context, NAME)
  }

  @JvmStatic
  @CheckResult
  fun obtainTheming(context: Context): Theming {
    return obtain(context, THEMING)
  }
}