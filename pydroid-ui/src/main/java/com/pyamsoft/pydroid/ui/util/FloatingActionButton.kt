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

import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton

fun FloatingActionButton.withBehavior(behavior: FloatingActionButton.Behavior = FloatingActionButton.Behavior()): FloatingActionButton {
  return this.also {
    val params = it.layoutParams
    if (params is CoordinatorLayout.LayoutParams) {
      params.behavior = behavior
    }
  }
}

fun FloatingActionButton.hide(func: FloatingActionButton.() -> Unit) {
  this.hide(object : FloatingActionButton.OnVisibilityChangedListener() {

    override fun onHidden(fab: FloatingActionButton?) {
      super.onHidden(fab)
      if (fab != null) {
        func(fab)
      }
    }
  })
}

fun FloatingActionButton.show(func: FloatingActionButton.() -> Unit) {
  this.hide(object : FloatingActionButton.OnVisibilityChangedListener() {

    override fun onShown(fab: FloatingActionButton?) {
      super.onShown(fab)
      if (fab != null) {
        func(fab)
      }
    }
  })
}
