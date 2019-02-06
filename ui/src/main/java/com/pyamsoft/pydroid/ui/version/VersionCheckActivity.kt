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
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.app.activity.ActivityBase
import com.pyamsoft.pydroid.ui.util.show
import com.pyamsoft.pydroid.ui.version.VersionCheckPresenter.Callback
import com.pyamsoft.pydroid.ui.version.upgrade.VersionUpgradeDialog
import timber.log.Timber

abstract class VersionCheckActivity : ActivityBase(), Callback {

  protected abstract val snackbarRoot: View

  internal lateinit var versionPresenter: VersionCheckPresenter
  internal lateinit var versionView: VersionView

  override fun onPostCreate(savedInstanceState: Bundle?) {
    super.onPostCreate(savedInstanceState)

    // Need to do this in onPostCreate because the snackbarRoot will not be available until
    // after subclass onCreate
    PYDroid.obtain(this)
        .plusVersionComponent(this, snackbarRoot)
        .inject(this)

    versionView.inflate(savedInstanceState)
    versionPresenter.bind(this)
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    versionView.saveState(outState)
  }

  override fun onDestroy() {
    super.onDestroy()
    versionView.teardown()
  }

  override fun onVersionCheckBegin(forced: Boolean) {
    if (forced) {
      versionView.showUpdating()
    }
  }

  override fun onVersionCheckFound(
    currentVersion: Int,
    newVersion: Int
  ) {
    Timber.d("Updated version found. %d => %d", currentVersion, newVersion)
    VersionUpgradeDialog.newInstance(newVersion)
        .show(this, VersionUpgradeDialog.TAG)
  }

  override fun onVersionCheckError(throwable: Throwable) {
    if (throwable is ActivityNotFoundException) {
      versionView.showError(throwable)
    } else {
      Timber.e(throwable, "Error checking for latest version")
    }
  }

  override fun onVersionCheckComplete() {
    versionView.dismissUpdating()
  }
}
