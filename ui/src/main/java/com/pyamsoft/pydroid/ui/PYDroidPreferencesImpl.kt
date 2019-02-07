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

package com.pyamsoft.pydroid.ui

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.pyamsoft.pydroid.bootstrap.rating.RatingPreferences

internal class PYDroidPreferencesImpl internal constructor(
  context: Context
) : PYDroidPreferences {

  private val prefs by lazy {
    PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
  }

  override var ratingAcceptedVersion: Int
    get() = prefs.getInt(
        RATING_ACCEPTED_VERSION, RatingPreferences.DEFAULT_RATING_ACCEPTED_VERSION
    )
    set(value) {
      prefs.edit {
        putInt(RATING_ACCEPTED_VERSION, value)
      }
    }

  companion object {

    private const val RATING_ACCEPTED_VERSION = "rating_dialog_accepted_version"
  }
}
