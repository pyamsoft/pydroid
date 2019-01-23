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

package com.pyamsoft.pydroid.ui.version

import android.os.Bundle
import android.view.View
import com.pyamsoft.pydroid.core.singleDisposable
import com.pyamsoft.pydroid.core.tryDispose
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.app.activity.ActivityBase
import com.pyamsoft.pydroid.ui.arch.destroy
import com.pyamsoft.pydroid.ui.util.show
import com.pyamsoft.pydroid.ui.version.VersionStateEvent.UpdateError
import com.pyamsoft.pydroid.ui.version.VersionStateEvent.UpdateFound
import com.pyamsoft.pydroid.ui.version.upgrade.VersionUpgradeDialog
import timber.log.Timber

abstract class VersionCheckActivity : ActivityBase() {

  internal lateinit var versionWorker: VersionCheckWorker
  internal lateinit var versionUiComponent: VersionUiComponent

  private var checkUpdatesDisposable by singleDisposable()

  override fun onPostCreate(savedInstanceState: Bundle?) {
    super.onPostCreate(savedInstanceState)
    PYDroid.obtain(this)
        .plusVersionComponent(this, snackbarRoot)
        .inject(this)

    versionWorker.onUpdateEvent {
      return@onUpdateEvent when (it) {
        is UpdateFound -> onUpdatedVersionFound(it.currentVersion, it.newVersion)
        is UpdateError -> onUpdatedVersionError(it.error)
        else -> Timber.d("Unhandled event: $it")
      }
    }
        .destroy(this)

    versionUiComponent.create(savedInstanceState)
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    versionUiComponent.saveState(outState)
  }

  override fun onDestroy() {
    super.onDestroy()
    checkUpdatesDisposable.tryDispose()
  }

  // Start in post resume in case dialog launches before resume() is complete for fragments
  override fun onPostResume() {
    super.onPostResume()
    checkUpdatesDisposable = versionWorker.checkForUpdates(false)
  }

  private fun onUpdatedVersionFound(
    current: Int,
    updated: Int
  ) {
    Timber.d("Updated version found. %d => %d", current, updated)
    VersionUpgradeDialog.newInstance(updated)
        .show(this, VersionUpgradeDialog.TAG)
  }

  private fun onUpdatedVersionError(throwable: Throwable) {
    // Silently drop version check errors
    Timber.e(throwable, "Error checking for latest version")
  }

  protected abstract val snackbarRoot: View
}
