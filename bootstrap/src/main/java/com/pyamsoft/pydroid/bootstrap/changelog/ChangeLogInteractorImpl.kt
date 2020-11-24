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

package com.pyamsoft.pydroid.bootstrap.changelog

import android.content.Context
import com.pyamsoft.pydroid.core.Enforcer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class ChangeLogInteractorImpl internal constructor(
    context: Context,
    private val preferences: ChangeLogPreferences
) : ChangeLogInteractor {

    private val packageManager by lazy { context.applicationContext.packageManager }

    override suspend fun getDisplayName(packageName: String): CharSequence =
        withContext(context = Dispatchers.Default) {
            Enforcer.assertOffMainThread()
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            return@withContext applicationInfo.loadLabel(packageManager)
        }

    override suspend fun showChangelog(force: Boolean): Boolean =
        withContext(context = Dispatchers.Default) {
            Enforcer.assertOffMainThread()
            return@withContext (force || preferences.showChangelog()).also { show ->
                if (show) {
                    preferences.markChangeLogShown()
                }
            }
        }
}

