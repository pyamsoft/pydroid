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

package com.pyamsoft.pydroid.bootstrap.version.fake

import android.content.Context
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.testing.FakeAppUpdateManager
import com.pyamsoft.pydroid.bootstrap.version.AbstractAppUpdater
import com.pyamsoft.pydroid.bootstrap.version.update.AppUpdateLauncher
import com.pyamsoft.pydroid.core.ThreadEnforcer
import com.pyamsoft.pydroid.util.Logger
import kotlinx.coroutines.delay

internal class FakeAppUpdater
internal constructor(
  enforcer: ThreadEnforcer,
  context: Context,
  version: Int,
  private val fakeUpgradeRequest: FakeUpgradeRequest,
) :
  AbstractAppUpdater<FakeAppUpdateManager>(
    enforcer = enforcer,
    resolveAppUpdateManager = {
      FakeAppUpdateManager(context.applicationContext).apply { setUpdateAvailable(version + 1) }
    },
  ) {

  override suspend fun onBeforeCheckForUpdate() {
    Logger.d { "In debug mode we fake a delay to mimic real world network turnaround time." }
    delay(2000L)
  }

  override fun createAppUpdateLauncher(info: AppUpdateInfo, updateType: Int): AppUpdateLauncher =
    FakeAppUpdateLauncher(
      manager = manager,
      info = info,
      type = updateType,
      fakeUpgradeRequest = fakeUpgradeRequest,
    )
}
