/*
 * Copyright 2021 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.ui.app

import androidx.annotation.CheckResult
import androidx.fragment.app.Fragment

/**
 * Get the AppBarActivity from a fragment. Null if not present.
 */
public val Fragment.appBarActivity: AppBarActivity?
  @get:CheckResult
  get() {
    val a = activity
    return if (a is AppBarActivity) a else null
  }

/**
 * Get the AppBarActivity from a fragment. Throws if not present
 */
@CheckResult
public fun Fragment.requireAppBarActivity(): AppBarActivity {
  return requireNotNull(appBarActivity) { "AppBarActivity is required and cannot be null." }
}
