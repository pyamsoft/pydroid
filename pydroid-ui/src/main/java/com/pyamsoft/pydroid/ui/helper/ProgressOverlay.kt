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

import android.app.Activity
import android.support.annotation.CheckResult
import android.support.annotation.ColorInt
import android.support.annotation.StyleRes
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.util.TypedValue
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.util.AppUtil
import timber.log.Timber

/**
 * Spinner view which takes over the screen and displays indeterminate progress

 * Since the progress dialog class is deprecated in Android O, this overlay will serve as a shim
 * replacement.
 * The overlay is a full view which will take over the screen and eat input, but will not disrupt
 * the user experience as much as a dialog would
 */
abstract class ProgressOverlay private constructor() {

  abstract fun dispose()

  @get:CheckResult abstract val isDisposed: Boolean

  private class Empty internal constructor() : ProgressOverlay() {

    override fun dispose() {
    }

    override val isDisposed: Boolean
      get() = false
  }

  class Builder internal constructor() {

    @ColorInt private var backgroundColor: Int = 0
    private var rootViewGroup: ViewGroup? = null
    private var alphaPercent: Int = 0
    private var elevation: Int = 0
    @StyleRes private var theme: Int = 0

    init {
      alphaPercent = 50
      theme = 0
      backgroundColor = 0
      elevation = 16
      rootViewGroup = null
    }

    @CheckResult fun setElevation(elevation: Int): Builder {
      if (elevation < 0) {
        throw IllegalArgumentException("Cannot set negative elevation")
      }
      this.elevation = elevation
      return this
    }

    @CheckResult fun setBackgroundColor(@ColorInt backgroundColor: Int): Builder {
      this.backgroundColor = backgroundColor
      return this
    }

    @CheckResult fun setAlphaPercent(alphaPercent: Int): Builder {
      this.alphaPercent = alphaPercent
      return this
    }

    @CheckResult fun setRootViewGroup(rootViewGroup: ViewGroup): Builder {
      this.rootViewGroup = rootViewGroup
      return this
    }

    @CheckResult fun setTheme(@StyleRes theme: Int): Builder {
      this.theme = theme
      return this
    }

    @CheckResult private fun inflateOverlay(activity: Activity,
        rootView: ViewGroup): ProgressOverlay {
      val inflater: LayoutInflater
      if (theme == 0) {
        inflater = LayoutInflater.from(activity)
      } else {
        inflater = LayoutInflater.from(activity).cloneInContext(
            ContextThemeWrapper(activity, theme))
      }
      val binding = inflater.inflate(R.layout.view_progress_overlay, rootView, false)

      // Set elevation to above basically everything
      // Make sure elevation cannot be negative
      elevation = Math.max(0, elevation)
      ViewCompat.setElevation(binding, AppUtil.convertToDP(activity, elevation.toFloat()))

      // Set alpha
      binding.alpha = alphaPercent.toFloat() / 100.0f

      if (backgroundColor == 0) {
        // Get color from theme
        val themeValue = TypedValue()
        activity.theme.resolveAttribute(android.R.attr.windowBackground, themeValue, true)
        if (themeValue.type >= TypedValue.TYPE_FIRST_COLOR_INT && themeValue.type <= TypedValue.TYPE_LAST_COLOR_INT) {
          // windowBackground is a color
          binding.setBackgroundColor(themeValue.data)
        } else {
          val drawable = ContextCompat.getDrawable(activity, themeValue.resourceId)
          if (drawable != null) {
            // windowBackground is not a color, probably a drawable
            binding.background = drawable
          } else {
            // Default to white
            binding.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.white))
          }
        }
      } else {
        // Set custom defined color
        binding.setBackgroundColor(backgroundColor)
      }

      // Eat any click attempts while the overlay is showing
      binding.setOnClickListener({ Timber.w("Eat click attempt with Overlay") })

      return Impl(binding, rootView)
    }

    @CheckResult fun build(activity: Activity): ProgressOverlay {
      val rootView: View
      if (rootViewGroup == null) {
        // Use the default Android content view as Overlay root
        Timber.d("Using Android content view as root")
        rootView = activity.window.decorView.findViewById(android.R.id.content)
      } else {
        // Locate a view in the given activity and use it as the root view
        Timber.d("Using builder-defined view as root")
        rootView = rootViewGroup as ViewGroup
      }
      if (rootView is ViewGroup) {
        return inflateOverlay(activity, rootView)
      } else {
        throw IllegalStateException("Root view is not a ViewGroup")
      }
    }
  }

  private class Impl internal constructor(private val binding: View,
      private val root: ViewGroup) : ProgressOverlay() {

    override var isDisposed: Boolean = false
      private set

    init {
      isDisposed = false
      root.addView(binding)
    }

    override fun dispose() {
      if (!isDisposed) {
        root.removeView(binding)
        isDisposed = true
      }
    }
  }

  companion object {

    @JvmStatic @CheckResult fun empty(): ProgressOverlay {
      return Empty()
    }

    @JvmStatic @CheckResult fun builder(): Builder {
      return Builder()
    }
  }
}
