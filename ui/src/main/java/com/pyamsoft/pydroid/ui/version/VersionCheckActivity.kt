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

import android.content.ActivityNotFoundException
import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.app.ActivityBase
import com.pyamsoft.pydroid.ui.navigation.FailedNavigationPresenter
import com.pyamsoft.pydroid.ui.util.show
import com.pyamsoft.pydroid.ui.version.VersionCheckPresenter.Callback
import com.pyamsoft.pydroid.ui.version.upgrade.VersionUpgradeDialog
import timber.log.Timber

abstract class VersionCheckActivity : ActivityBase(), Callback, FailedNavigationPresenter.Callback {

  protected abstract val snackbarRoot: View

  internal lateinit var failedNavigationPresenter: FailedNavigationPresenter
  internal lateinit var versionPresenter: VersionCheckPresenter
  internal lateinit var versionView: VersionView

  @CallSuper
  override fun onPostCreate(savedInstanceState: Bundle?) {
    super.onPostCreate(savedInstanceState)

    // Need to do this in onPostCreate because the snackbarRoot will not be available until
    // after subclass onCreate
    PYDroid.obtain(this)
        .plusVersionComponent(this, snackbarRoot)
        .inject(this)

    versionView.inflate(savedInstanceState)

    failedNavigationPresenter.bind(this)

    versionPresenter.bind(this)
    versionPresenter.checkForUpdates(false)
  }

  @CallSuper
  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    versionView.saveState(outState)
  }

  @CallSuper
  override fun onDestroy() {
    super.onDestroy()
    versionView.teardown()
  }

  final override fun onVersionCheckBegin(forced: Boolean) {
    if (forced) {
      versionView.showUpdating()
    }
  }

  final override fun onVersionCheckFound(
    currentVersion: Int,
    newVersion: Int
  ) {
    Timber.d("Updated version found. %d => %d", currentVersion, newVersion)
    VersionUpgradeDialog.newInstance(newVersion)
        .show(this, VersionUpgradeDialog.TAG)
  }

  final override fun onVersionCheckError(throwable: Throwable) {
    Timber.e(throwable, "Error checking for latest version")
  }

  final override fun onVersionCheckComplete() {
    versionView.dismissUpdating()
  }

  final override fun onFailedNavigation(error: ActivityNotFoundException) {
    versionView.showError(error)
  }
}
