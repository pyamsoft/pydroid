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

package com.pyamsoft.pydroid.bootstrap.settings

import android.app.ActivityManager
import android.content.Context
import androidx.core.content.getSystemService
import timber.log.Timber

internal class SettingsInteractorImpl internal constructor(context: Context) : SettingsInteractor {

  private val activityManager by lazy {
    requireNotNull(context.applicationContext.getSystemService<ActivityManager>())
  }

  override suspend fun wipeData() {
    Timber.d("Resetting all application user data")
    activityManager.clearApplicationUserData()
  }
}
