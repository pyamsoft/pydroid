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

package com.pyamsoft.pydroid.ui.helper

import android.content.Context
import android.support.annotation.StringRes
import android.widget.Toast

/**
 * Toasty is a drop in replacement for native Android toasts.
 *
 * It explicitly uses the application context to create the toast. If this is not done it is possible
 * though very unlikely for the Toast to leak the Activity context if it is displayed and then
 * dismissed very quickly
 */
class Toasty {

  init {
    throw RuntimeException("No instances")
  }

  enum class Duration {
    LENGTH_SHORT, LENGTH_LONG
  }

  companion object {

    @JvmStatic val LENGTH_SHORT = Duration.LENGTH_SHORT
    @JvmStatic val LENGTH_LONG = Duration.LENGTH_LONG

    @JvmStatic fun makeText(c: Context, message: CharSequence, duration: Duration): Toast {
      return Toast.makeText(c.applicationContext, message, when (duration) {
        Duration.LENGTH_SHORT -> Toast.LENGTH_SHORT
        Duration.LENGTH_LONG -> Toast.LENGTH_LONG
      })
    }

    @JvmStatic fun makeText(c: Context, @StringRes resId: Int, duration: Duration): Toast {
      return makeText(c, c.applicationContext.getString(resId), duration)
    }
  }
}
