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

package com.pyamsoft.pydroid.bootstrap.rating

import androidx.annotation.CheckResult

/** Preferences related to in-app ratings */
public interface RatingPreferences {

  /** Decide whether to show the in-app rating interface */
  @CheckResult public suspend fun showRating(): Boolean

  /** Mark the in-app rating interface as shown in the current application state */
  public suspend fun markRatingShown()
}
