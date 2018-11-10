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

package com.pyamsoft.pydroid.ui.widget

import android.view.View
import androidx.annotation.CheckResult
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView

/**
 * Floating Action Button behavior which hides button after scroll distance is passed
 */
class HideOnScrollListener private constructor(
  private val view: View,
  private val distanceThreshold: Int,
  private val onVisibilityChanged: (Boolean) -> Unit
) : RecyclerView.OnScrollListener() {

  private var visible: Boolean = view.isVisible
  private var distanceScrolled: Int = 0

  fun syncVisibilityState() {
    visible = view.isVisible
  }

  override fun onScrolled(
    recyclerView: RecyclerView,
    dx: Int,
    dy: Int
  ) {
    super.onScrolled(recyclerView, dx, dy)
    if (distanceScrolled > distanceThreshold && visible) {
      // Once we pass minimum distance threshold, and we are currently visible
      // Then we hide
      onVisibilityChanged(false)
      visible = false
      distanceScrolled = 0
    } else if (distanceScrolled < -distanceThreshold && !visible) {
      // Once we pass minimum distance threshold, and we are currently invisible
      // Then we show
      onVisibilityChanged(true)
      visible = true
      distanceScrolled = 0
    } else {
      // Otherwise, we track how much we have scrolled until we cross the threshold
      if ((visible && dy > 0) || (!visible && dy < 0)) {
        distanceScrolled += dy
      }
    }

  }

  companion object {

    private const val DEFAULT_DISTANCE = 12

    @JvmStatic
    @CheckResult
    fun withView(
      view: View,
      onVisibilityChanged: (Boolean) -> Unit
    ): HideOnScrollListener {
      return withView(view, DEFAULT_DISTANCE, onVisibilityChanged)
    }

    @JvmStatic
    @CheckResult
    fun withView(
      view: View,
      distance: Int,
      onVisibilityChanged: (Boolean) -> Unit
    ): HideOnScrollListener {
      return HideOnScrollListener(view, distance, onVisibilityChanged)
    }
  }

}
