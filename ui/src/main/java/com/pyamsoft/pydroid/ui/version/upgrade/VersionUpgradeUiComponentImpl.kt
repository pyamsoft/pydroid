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

package com.pyamsoft.pydroid.ui.version.upgrade

import android.content.ActivityNotFoundException
import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.arch.BaseUiComponent
import com.pyamsoft.pydroid.arch.doOnDestroy
import com.pyamsoft.pydroid.ui.navigation.FailedNavigationPresenter
import com.pyamsoft.pydroid.ui.version.upgrade.VersionUpgradeUiComponent.Callback

internal class VersionUpgradeUiComponentImpl internal constructor(
  private val controlsView: VersionUpgradeControlView,
  private val contentView: VersionUpgradeContentView,
  private val failedNavigationPresenter: FailedNavigationPresenter,
  private val presenter: VersionUpgradePresenter
) : BaseUiComponent<VersionUpgradeUiComponent.Callback>(),
    VersionUpgradeUiComponent,
    VersionUpgradePresenter.Callback {

  override fun onBind(
    owner: LifecycleOwner,
    savedInstanceState: Bundle?,
    callback: Callback
  ) {
    owner.doOnDestroy {
      contentView.teardown()
      controlsView.teardown()
      presenter.unbind()
    }

    contentView.inflate(savedInstanceState)
    controlsView.inflate(savedInstanceState)
    presenter.bind(this)
  }

  override fun saveState(outState: Bundle) {
    contentView.saveState(outState)
    controlsView.saveState(outState)
  }

  override fun onUpgradeBegin() {
    callback.onNavigateToMarket()
  }

  override fun onUpgradeCancel() {
    callback.onCancelUpgrade()
  }

  override fun navigationFailed(error: ActivityNotFoundException) {
    failedNavigationPresenter.failedNavigation(error)
  }

}