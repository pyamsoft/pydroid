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

import com.pyamsoft.pydroid.ui.RatingPreferences
import io.reactivex.Completable
import io.reactivex.Single

internal class RatingInteractorImpl internal constructor(
    private val preferences: RatingPreferences) : RatingInteractor {

  override fun needsToViewRating(versionCode: Int, force: Boolean): Single<Boolean> =
      Single.fromCallable { force || preferences.getRatingAcceptedVersion() < versionCode }

  override fun saveRating(versionCode: Int): Completable =
      Completable.fromAction { preferences.setRatingAcceptedVersion(versionCode) }
}
