/*
 * Copyright 2017 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.ui.version

import android.os.Bundle
import android.support.annotation.CallSuper
import com.pyamsoft.pydroid.presenter.Presenter
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.app.activity.DisposableActivity
import com.pyamsoft.pydroid.ui.util.DialogUtil
import com.pyamsoft.pydroid.version.VersionCheckPresenter
import com.pyamsoft.pydroid.version.VersionCheckProvider
import timber.log.Timber

abstract class VersionCheckActivity : DisposableActivity(), VersionCheckProvider {

  internal lateinit var presenter: VersionCheckPresenter

  @CallSuper
  override fun provideBoundPresenters(): List<Presenter<*>> = listOf(presenter)

  @CallSuper override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    PYDroid.with {
      it.plusVersionCheckComponent(packageName, currentApplicationVersion).inject(this)
    }

    presenter.bind(Unit)
  }

  // Start in post resume in case dialog launches before resume() is complete for fragments
  override fun onPostResume() {
    super.onPostResume()
    presenter.checkForUpdates(false, onUpdatedVersionFound = { current, updated ->
      Timber.d("Updated version found. %d => %d", current, updated)
      DialogUtil.guaranteeSingleDialogFragment(this,
          VersionUpgradeDialog.newInstance(applicationName, current, updated),
          VersionUpgradeDialog.TAG)
    })
  }
}
