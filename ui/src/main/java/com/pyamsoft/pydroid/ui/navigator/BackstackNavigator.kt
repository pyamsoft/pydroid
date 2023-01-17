/*
 * Copyright 2022 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.ui.navigator

import androidx.annotation.CheckResult

/** A navigator which understands backstacks */
@Deprecated("Start migrating over to Compose and a different Navigation method")
public interface BackstackNavigator<S : Any> : Navigator<S> {

  /** Go back a page */
  public fun goBack()

  /** Get the size of the back stack */
  @CheckResult public fun backStackSize(): Int
}
