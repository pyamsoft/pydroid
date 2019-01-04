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

package com.pyamsoft.pydroid.ui.app.fragment

import android.os.Bundle
import android.view.View
import androidx.annotation.CheckResult
import androidx.fragment.app.Fragment
import com.pyamsoft.pydroid.ui.app.activity.ToolbarActivity

val Fragment.toolbarActivity: ToolbarActivity?
  @get:CheckResult get() {
    val a = activity
    if (a is ToolbarActivity) {
      return a
    } else {
      return null
    }
  }

@CheckResult
fun Fragment.requireToolbarActivity(): ToolbarActivity {
  return requireNotNull(toolbarActivity) { "ToolbarActivity is required and cannot be null." }
}

@CheckResult
fun Fragment.requireView(): View {
  return checkNotNull(view) { "View is required and cannot be null." }
}

@CheckResult
fun Fragment.requireArguments(): Bundle {
  return checkNotNull(arguments) { "Arguments are required and cannot be null." }
}
