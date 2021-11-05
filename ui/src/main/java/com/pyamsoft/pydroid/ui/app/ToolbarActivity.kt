/*
 * Copyright 2020 Peter Kenji Yamanaka
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

import androidx.appcompat.widget.Toolbar

/** An activity which handles a global toolbar */
@Deprecated("Migrate to Jetpack Compose")
public interface ToolbarActivity {

  /**
   * Run the function if the toolbar is set, otherwise do nothing
   *
   * Do not @CheckResult the return value since it can be ignored safely.
   */
  public fun <T> withToolbar(func: (Toolbar) -> T): T?

  /**
   * Run the function if the toolbar is set, otherwise throw
   *
   * Do not @CheckResult the return value since it can be ignored safely.
   */
  public fun <T> requireToolbar(func: (Toolbar) -> T): T
}
