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
import com.pyamsoft.pydroid.ui.arch.InvalidIdException
import com.pyamsoft.pydroid.ui.navigation.FailedNavigationBinder
import com.pyamsoft.pydroid.ui.version.VersionCheckPresenter.VersionState
import com.pyamsoft.pydroid.ui.version.VersionCheckUiComponent.Callback
import timber.log.Timber

internal class VersionCheckUiComponentImpl internal constructor(
  private val failedNavigationBinder: FailedNavigationBinder,
  private val versionPresenter: VersionCheckPresenter,
  private val versionView: VersionView
) : BaseUiComponent<VersionCheckUiComponent.Callback>(),
    VersionCheckUiComponent,
    VersionCheckPresenter.Callback,
    FailedNavigationBinder.Callback {

  override fun id(): Int {
    throw InvalidIdException
  }

  override fun onBind(
    owner: LifecycleOwner,
    savedInstanceState: Bundle?,
    callback: Callback
  ) {
    owner.doOnDestroy {
      versionView.teardown()
      failedNavigationBinder.unbind()
      versionPresenter.unbind()
    }

    versionView.inflate(savedInstanceState)
    failedNavigationBinder.bind(this)
    versionPresenter.bind(this)
  }

  override fun onSaveState(outState: Bundle) {
    versionView.saveState(outState)
  }

  override fun onRender(
    state: VersionState,
    oldState: VersionState?
  ) {
    renderLoading(state, oldState)
    renderUpgrade(state, oldState)
    renderError(state, oldState)
  }

  private fun renderLoading(
    state: VersionState,
    oldState: VersionState?
  ) {
    state.isLoading.let { loading ->
      if (oldState == null || loading != oldState.isLoading) {
        if (loading == null) {
          versionView.dismissUpdating()
        } else {
          if (loading.forced) {
            versionView.showUpdating()
          }
        }
      }
    }
  }

  private fun renderUpgrade(
    state: VersionState,
    oldState: VersionState?
  ) {
    state.upgrade.let { upgrade ->
      if (oldState == null || upgrade != oldState.upgrade) {
        if (upgrade != null) {
          val (currentVersion, newVersion) = upgrade
          Timber.d("Updated version found. %d => %d", currentVersion, newVersion)
          callback.onShowVersionUpgrade(newVersion)
        }
      }
    }
  }

  private fun renderError(
    state: VersionState,
    oldState: VersionState?
  ) {
    state.throwable.let { throwable ->
      if (oldState == null || throwable != oldState.throwable) {
        if (throwable != null) {
          Timber.e(throwable, "Error checking for latest version")
        }
      }
    }
  }

  override fun handleFailedNavigation(error: ActivityNotFoundException) {
    versionView.showError(error)
  }

}