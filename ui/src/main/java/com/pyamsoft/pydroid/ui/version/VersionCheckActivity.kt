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
import androidx.annotation.CallSuper
import com.pyamsoft.pydroid.ui.Injector
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.PYDroidComponent
import com.pyamsoft.pydroid.ui.app.ActivityBase
import com.pyamsoft.pydroid.ui.util.show
import com.pyamsoft.pydroid.ui.version.upgrade.VersionUpgradeDialog
import javax.inject.Inject

abstract class VersionCheckActivity : ActivityBase(), VersionCheckUiComponent.Callback {

  protected abstract val snackbarRoot: View

  @field:Inject internal lateinit var versionComponent: VersionCheckUiComponent

  @CallSuper
  override fun onPostCreate(savedInstanceState: Bundle?) {
    super.onPostCreate(savedInstanceState)

    // Need to do this in onPostCreate because the snackbarRoot will not be available until
    // after subclass onCreate
    Injector.obtain<PYDroidComponent>(applicationContext)
        .plusVersion()
        .create(this, snackbarRoot)
        .inject(this)

    versionComponent.bind(this, savedInstanceState, this)
  }

  @CallSuper
  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    versionComponent.saveState(outState)
  }

  final override fun onShowVersionUpgrade(newVersion: Int) {
    VersionUpgradeDialog.newInstance(newVersion)
        .show(this, VersionUpgradeDialog.TAG)
  }
}
