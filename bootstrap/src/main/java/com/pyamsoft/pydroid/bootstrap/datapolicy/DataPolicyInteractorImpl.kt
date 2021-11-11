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

package com.pyamsoft.pydroid.bootstrap.datapolicy

import android.content.Context
import com.pyamsoft.pydroid.bootstrap.app.AppInteractorImpl
import com.pyamsoft.pydroid.core.Enforcer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class DataPolicyInteractorImpl
internal constructor(context: Context, private val preferences: DataPolicyPreferences) :
    AppInteractorImpl(context), DataPolicyInteractor {

  override suspend fun isPolicyAccepted(): Boolean =
      withContext(context = Dispatchers.IO) {
        Enforcer.assertOffMainThread()

        return@withContext preferences.isPolicyAccepted()
      }

  override suspend fun acceptPolicy() =
      withContext(context = Dispatchers.IO) {
        Enforcer.assertOffMainThread()

        preferences.respondToPolicy(true)
      }

  override suspend fun rejectPolicy() =
      withContext(context = Dispatchers.IO) {
        Enforcer.assertOffMainThread()

        preferences.respondToPolicy(false)
      }
}
