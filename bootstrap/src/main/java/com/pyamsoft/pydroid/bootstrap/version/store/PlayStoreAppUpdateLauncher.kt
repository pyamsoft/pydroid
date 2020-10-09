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

package com.pyamsoft.pydroid.bootstrap.version.store

import android.app.Activity
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.model.AppUpdateType
import com.pyamsoft.pydroid.bootstrap.version.AppUpdateLauncher
import com.pyamsoft.pydroid.core.Enforcer
import timber.log.Timber

internal class PlayStoreAppUpdateLauncher internal constructor(
    private val manager: AppUpdateManager,
    private val info: AppUpdateInfo,
    @AppUpdateType private val type: Int,
) : AppUpdateLauncher {

    override fun update(activity: Activity, requestCode: Int) {
        Enforcer.assertOnMainThread()

        Timber.d("Begin update flow $requestCode $info")
        if (manager.startUpdateFlowForResult(info, type, activity, requestCode)) {
            Timber.d("Update flow has started")
        }
    }
}
