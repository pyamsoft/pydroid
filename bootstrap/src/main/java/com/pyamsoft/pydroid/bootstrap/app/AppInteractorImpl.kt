/*
 * Copyright 2024 pyamsoft
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

package com.pyamsoft.pydroid.bootstrap.app

import android.content.Context
import com.pyamsoft.pydroid.util.applicationDisplayName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal abstract class AppInteractorImpl
protected constructor(
    private val context: Context,
) : AppInteractor {

  final override suspend fun getDisplayName(): String =
      withContext(context = Dispatchers.Default) { context.applicationDisplayName }
}
