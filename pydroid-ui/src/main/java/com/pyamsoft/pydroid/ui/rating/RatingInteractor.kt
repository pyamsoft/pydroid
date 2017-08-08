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

package com.pyamsoft.pydroid.ui.rating

import android.support.annotation.CheckResult
import com.pyamsoft.pydroid.ui.RatingPreferences
import io.reactivex.Completable
import io.reactivex.Single

internal class RatingInteractor(private val preferences: RatingPreferences) {

  /**
   * public
   */
  @CheckResult internal fun needsToViewRating(versionCode: Int, force: Boolean): Single<Boolean> {
    return Single.fromCallable { preferences.getRatingAcceptedVersion() < versionCode || force }
  }

  /**
   * public
   */
  @CheckResult internal fun saveRating(versionCode: Int): Completable {
    return Completable.fromAction { preferences.setRatingAcceptedVersion(versionCode) }
  }
}
