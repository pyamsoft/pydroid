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

import com.google.android.material.appbar.AppBarLayout

/** An activity which handles a global AppBar */
@Deprecated("Migrate to Jetpack Compose")
public interface AppBarActivity : ToolbarActivity {

  /**
   * Run the function if the AppBar is set, otherwise do nothing
   *
   * Do not @CheckResult the return value since it can be ignored safely.
   */
  public fun <T> withAppBar(func: (AppBarLayout) -> T): T?

  /**
   * Run the function if the AppBar is set, otherwise throw
   *
   * Do not @CheckResult the return value since it can be ignored safely.
   */
  public fun <T> requireAppBar(func: (AppBarLayout) -> T): T
}
