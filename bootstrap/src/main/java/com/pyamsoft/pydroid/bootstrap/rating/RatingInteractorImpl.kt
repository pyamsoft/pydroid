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

package com.pyamsoft.pydroid.bootstrap.rating

import com.pyamsoft.pydroid.core.threads.Enforcer
import timber.log.Timber

internal class RatingInteractorImpl internal constructor(
  private val currentVersion: Int,
  private val enforcer: Enforcer,
  private val preferences: RatingPreferences
) : RatingInteractor {

  override suspend fun needsToViewRating(force: Boolean): Boolean {
    enforcer.assertNotOnMainThread()
    if (force) {
      Timber.d("Force view rating")
      return true
    } else {
      // If the version code is 1, it's the first app version, don't show a changelog
      if (currentVersion <= 1) {
        Timber.w("Version code is invalid: $currentVersion")
        return false
      } else {
        // If the preference is default, the app may be installed for the first time
        // regardless of the current version. Don't show change log, else show it
        val lastSeenVersion: Int = preferences.ratingAcceptedVersion
        if (lastSeenVersion == RatingPreferences.DEFAULT_RATING_ACCEPTED_VERSION) {
          Timber.i("Last seen version is default, app is installed for the first time or reset")
          preferences.ratingAcceptedVersion = currentVersion
          return false
        } else {
          Timber.d("Compare version code to last seen: $currentVersion <-> $lastSeenVersion")
          return lastSeenVersion < currentVersion
        }
      }
    }
  }

  override suspend fun saveRating() {
    enforcer.assertNotOnMainThread()
    preferences.ratingAcceptedVersion = currentVersion
  }
}
