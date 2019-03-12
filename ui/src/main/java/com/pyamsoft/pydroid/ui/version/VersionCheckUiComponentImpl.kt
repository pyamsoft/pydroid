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
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.arch.BaseUiComponent
import com.pyamsoft.pydroid.arch.doOnDestroy
import com.pyamsoft.pydroid.ui.navigation.FailedNavigationPresenter
import com.pyamsoft.pydroid.ui.version.VersionCheckUiComponent.Callback
import timber.log.Timber

internal class VersionCheckUiComponentImpl internal constructor(
  private val failedNavigationPresenter: FailedNavigationPresenter,
  private val versionPresenter: VersionCheckPresenter,
  private val versionView: VersionView
) : BaseUiComponent<VersionCheckUiComponent.Callback>(),
    VersionCheckUiComponent,
    VersionCheckPresenter.Callback,
    FailedNavigationPresenter.Callback {

  override fun onBind(
    owner: LifecycleOwner,
    savedInstanceState: Bundle?,
    callback: Callback
  ) {
    owner.doOnDestroy {
      versionView.teardown()
      failedNavigationPresenter.unbind()
      versionPresenter.unbind()
    }

    versionView.inflate(savedInstanceState)
    failedNavigationPresenter.bind(this)
    versionPresenter.bind(this)
  }

  override fun saveState(outState: Bundle) {
    versionView.saveState(outState)
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
    callback.onShowVersionUpgrade(newVersion)
  }

  override fun onVersionCheckError(throwable: Throwable) {
    Timber.e(throwable, "Error checking for latest version")
  }

  override fun onVersionCheckComplete() {
    versionView.dismissUpdating()
  }

  override fun onFailedNavigation(error: ActivityNotFoundException) {
    versionView.showError(error)
  }

}