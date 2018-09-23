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

package com.pyamsoft.pydroid.ui.util

import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.google.android.material.snackbar.Snackbar
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.util.toDp

object Snackbreak {

  private fun setMargin(snackbar: Snackbar) {
    val params = snackbar.view.layoutParams as? ViewGroup.MarginLayoutParams
    if (params != null) {
      val margin = 8.toDp(snackbar.context)
      params.setMargins(margin, margin, margin, margin)
      snackbar.view.layoutParams = params
    }
  }

  private fun setBackground(snackbar: Snackbar) {
    val drawable = GradientDrawable().mutate() as GradientDrawable

    val background = drawable.apply {
      shape = GradientDrawable.RECTANGLE
      setColor(ContextCompat.getColor(snackbar.context, R.color.snackbar))

      cornerRadius = 4.toDp(snackbar.context)
          .toFloat()
    }

    snackbar.view.background = background
  }

  private fun Snackbar.materialDesign() {
    setMargin(this)
    setBackground(this)
    ViewCompat.setElevation(view, 6.toDp(context).toFloat())
  }

  @JvmStatic
  @CheckResult
  private fun make(
    view: View,
    @StringRes resId: Int,
    duration: Int
  ): Snackbar {
    return Snackbar.make(view, resId, duration)
        .also { it.materialDesign() }
  }

  @JvmStatic
  @CheckResult
  private fun make(
    view: View,
    message: CharSequence,
    duration: Int
  ): Snackbar {
    return Snackbar.make(view, message, duration)
        .also { it.materialDesign() }
  }

  @JvmStatic
  @CheckResult
  fun short(
    view: View,
    message: CharSequence
  ): Snackbar = make(view, message, Snackbar.LENGTH_SHORT)

  @JvmStatic
  @CheckResult
  fun short(
    view: View,
    @StringRes message: Int
  ): Snackbar = make(view, message, Snackbar.LENGTH_SHORT)

  @JvmStatic
  @CheckResult
  fun long(
    view: View,
    message: CharSequence
  ): Snackbar = make(view, message, Snackbar.LENGTH_LONG)

  @JvmStatic
  @CheckResult
  fun long(
    view: View,
    @StringRes message: Int
  ): Snackbar = make(view, message, Snackbar.LENGTH_LONG)

  @JvmStatic
  @CheckResult
  fun indefinite(
    view: View,
    message: CharSequence
  ): Snackbar = make(view, message, Snackbar.LENGTH_INDEFINITE)

  @JvmStatic
  @CheckResult
  fun indefinite(
    view: View,
    @StringRes message: Int
  ): Snackbar = make(view, message, Snackbar.LENGTH_INDEFINITE)

}
