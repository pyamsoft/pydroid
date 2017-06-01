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

package com.pyamsoft.pydroid.design.util

import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton

object FABUtil {

  @JvmStatic fun setupFABBehavior(fab: FloatingActionButton,
      behavior: FloatingActionButton.Behavior?) {
    val params = fab.layoutParams
    if (params is CoordinatorLayout.LayoutParams) {
      val coordParams = params
      if (behavior == null) {
        coordParams.behavior = FloatingActionButton.Behavior()
      } else {
        coordParams.behavior = behavior
      }
    }
  }
}
