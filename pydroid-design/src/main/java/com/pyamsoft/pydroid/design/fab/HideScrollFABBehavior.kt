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

package com.pyamsoft.pydroid.design.fab

import android.support.annotation.CheckResult
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.v4.view.ViewCompat
import android.view.View
import timber.log.Timber

/**
 * Floating Action Button behavior which hides button after scroll distance is passed
 */
class HideScrollFABBehavior(private val distanceNeeded: Int) : FloatingActionButton.Behavior() {

  private var animating = false

  @CheckResult
  fun isAnimating(): Boolean = animating

  constructor() : this(0)

  init {
    animating = false
  }

  fun endAnimation() {
    this.animating = false
  }

  fun onHiddenHook() {
  }

  fun onShownHook() {
  }

  override fun onNestedScroll(
    coordinatorLayout: CoordinatorLayout,
    child: FloatingActionButton,
    target: View,
    dxConsumed: Int,
    dyConsumed: Int,
    dxUnconsumed: Int,
    dyUnconsumed: Int,
    type: Int
  ) {
    if (dyConsumed > distanceNeeded && child.isShown) {
      if (!animating) {
        animating = true
        Timber.w("Hide FAB")
        child.hide(object : FloatingActionButton.OnVisibilityChangedListener() {
          override fun onHidden(fab: FloatingActionButton?) {
            super.onHidden(fab)
            onHiddenHook()

            Timber.w(
                "Support library as on 25.1.0 sets FAB visibility to GONE, making it ignore other scrolling event."
            )
            Timber.w("Set it to invisible to fix this problem")
            fab?.apply {
              visibility = View.INVISIBLE
              animating = false
            }
          }
        })
      }
    } else if (dyConsumed < -distanceNeeded && !child.isShown) {
      if (!animating) {
        animating = true
        Timber.w("Show FAB")
        child.show(object : FloatingActionButton.OnVisibilityChangedListener() {
          override fun onShown(fab: FloatingActionButton?) {
            super.onShown(fab)
            onShownHook()
            fab?.apply {
              visibility = View.VISIBLE
              animating = false
            }
          }
        })
      }
    }
  }

  override fun onStartNestedScroll(
    coordinatorLayout: CoordinatorLayout,
    child: FloatingActionButton,
    directTargetChild: View,
    target: View,
    axes: Int,
    type: Int
  ): Boolean = axes == ViewCompat.SCROLL_AXIS_VERTICAL
}
