/*
 * Copyright 2025 pyamsoft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
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
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.flowOn

internal class ChangeLogInteractorImpl
internal constructor(
    context: Context,
    private val preferences: ChangeLogPreferences,
    private val isFakeChangeLogAvailable: Flow<Boolean>?,
) : ChangeLogInteractor, AppInteractorImpl(context) {

  override fun listenShowChangeLogChanges(): Flow<Boolean> {
    val faked = isFakeChangeLogAvailable
    return if (faked != null) {
      combineTransform(
              faked,
              preferences.listenForShowChangelogChanges(),
          ) { isFaked, show ->
            // If this is showing normally, we return it
            // Or if it's faked
            emit(isFaked || show)
          }
          .flowOn(context = Dispatchers.IO)
    } else {
      preferences.listenForShowChangelogChanges()
    }
  }

  override fun markChangeLogShown() {
    preferences.markChangeLogShown()
  }
}
