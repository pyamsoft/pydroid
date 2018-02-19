/*
 * Copyright (C) 2018 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pydroid.util

import android.content.Context
import android.support.annotation.CheckResult
import android.support.annotation.StringRes
import android.widget.Toast

/**
 * Toasty is a drop in replacement for native Android toasts.
 *
 * It explicitly uses the application context to create the toast. If this is not done it is possible
 * though very unlikely for the Toast to leak the Activity context if it is displayed and then
 * dismissed very quickly
 */
object Toasty {

  enum class Duration {
    LENGTH_SHORT,
    LENGTH_LONG
  }

  @JvmField
  val LENGTH_SHORT = Duration.LENGTH_SHORT

  @JvmField
  val LENGTH_LONG = Duration.LENGTH_LONG

  @JvmStatic
  @CheckResult
  fun makeText(
    c: Context,
    message: CharSequence,
    duration: Duration,
    show: Boolean = true
  ): Toast {
    val length: Int = when (duration) {
      Duration.LENGTH_SHORT -> Toast.LENGTH_SHORT
      Duration.LENGTH_LONG -> Toast.LENGTH_LONG
    }
    return Toast.makeText(c.applicationContext, message, length)
        .also { if (show) it.show() }
  }

  @JvmStatic
  @CheckResult
  fun makeText(
    c: Context,
    @StringRes resId: Int,
    duration: Duration,
    show: Boolean = true
  ): Toast = makeText(c, c.applicationContext.getString(resId), duration, show)

  @JvmStatic
  @CheckResult
  fun makeText(
    c: Context,
    @StringRes resId: Int,
    duration: Int,
    show: Boolean = true
  ): Toast = makeText(c, c.applicationContext.getString(resId), duration, show)

  @JvmStatic
  @CheckResult
  fun makeText(
    c: Context,
    message: CharSequence,
    duration: Int,
    show: Boolean = true
  ): Toast {
    val length: Duration = when (duration) {
      Toast.LENGTH_SHORT -> LENGTH_SHORT
      Toast.LENGTH_LONG -> LENGTH_LONG
      else -> throw IllegalArgumentException(
          "Duration must be either Toast.LENGTH_SHORT or Toast.LENGTH_LONG"
      )
    }
    return makeText(c, message, length, show)
  }
}
