/*
 * Copyright 2023 pyamsoft
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

package com.pyamsoft.pydroid.bootstrap.changelog

import android.content.Context
import com.pyamsoft.pydroid.bootstrap.app.AppInteractorImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class ChangeLogInteractorImpl
internal constructor(
    context: Context,
    private val preferences: ChangeLogPreferences,
    private val isFakeChangeLogAvailable: Boolean,
) : AppInteractorImpl(context), ChangeLogInteractor {

  override suspend fun listenShowChangeLogChanges(): Flow<Boolean> =
      withContext(context = Dispatchers.Default) {
        return@withContext preferences.listenForShowChangelogChanges().map { show ->
          // Force to show if this is faked
          return@map show || isFakeChangeLogAvailable
        }
      }

  override suspend fun markChangeLogShown() =
      withContext(context = Dispatchers.Default) { preferences.markChangeLogShown() }
}
