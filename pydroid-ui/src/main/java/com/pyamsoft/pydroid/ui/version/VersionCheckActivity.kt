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
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.app.activity.BackPressConfirmActivity
import com.pyamsoft.pydroid.ui.util.DialogUtil
import com.pyamsoft.pydroid.version.VersionCheckPresenter
import com.pyamsoft.pydroid.version.VersionCheckPresenter.Callback
import com.pyamsoft.pydroid.version.VersionCheckProvider
import timber.log.Timber

abstract class VersionCheckActivity : BackPressConfirmActivity(), VersionCheckProvider, Callback {

  internal lateinit var presenter: VersionCheckPresenter

  @CallSuper override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    PYDroid.with {
      it.plusVersionCheckComponent(packageName, currentApplicationVersion).inject(this)
    }
  }

  @CallSuper override fun onStart() {
    super.onStart()
    presenter.start(this)
  }

  override fun onUpdatedVersionFound(current: Int, updated: Int) {
    Timber.d("Updated version found. %d => %d", current, updated)
    DialogUtil.guaranteeSingleDialogFragment(this,
        VersionUpgradeDialog.newInstance(applicationName, current, updated),
        VersionUpgradeDialog.TAG)
  }

  @CallSuper override fun onStop() {
    super.onStop()
    presenter.stop()
  }
}
