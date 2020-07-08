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
import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModelProvider
import com.pyamsoft.pydroid.arch.StateSaver
import com.pyamsoft.pydroid.arch.createComponent
import com.pyamsoft.pydroid.ui.Injector
import com.pyamsoft.pydroid.ui.PYDroidComponent
import com.pyamsoft.pydroid.ui.arch.viewModelFactory
import com.pyamsoft.pydroid.ui.privacy.PrivacyActivity
import com.pyamsoft.pydroid.ui.util.show
import com.pyamsoft.pydroid.ui.version.VersionControllerEvent.ShowUpgrade
import com.pyamsoft.pydroid.ui.version.upgrade.VersionUpgradeDialog
import com.pyamsoft.pydroid.util.doOnStart

abstract class VersionCheckActivity : PrivacyActivity() {

    protected open val checkForUpdates: Boolean = true

    private var stateSaver: StateSaver? = null
    internal var versionFactory: ViewModelProvider.Factory? = null
    internal var versionView: VersionView? = null
    private val versionViewModel by viewModelFactory<VersionCheckViewModel> { versionFactory }

    @CallSuper
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Need to do this in onPostCreate because the snackbarRoot will not be available until
        // after subclass onCreate
        Injector.obtain<PYDroidComponent>(applicationContext)
            .plusVersion()
            .create(this) { snackbarRoot }
            .inject(this)

        stateSaver = createComponent(
            savedInstanceState, this,
            versionViewModel,
            requireNotNull(versionView)
        ) {
            return@createComponent when (it) {
                is ShowUpgrade -> showVersionUpgrade(it.payload.newVersion)
            }
        }
    }

    @CallSuper
    override fun onResume() {
        super.onResume()

        if (checkForUpdates) {
            forceCheckForUpdate()
        }
    }

    private fun forceCheckForUpdate() {
        doOnStart { versionViewModel.checkForUpdates(false) }
    }

    // Keep public for app consumers
    fun checkForUpdate() {
        check(!checkForUpdates) { "Do not call this method manually, updates are automatically checked onResume" }
        forceCheckForUpdate()
    }

    @CallSuper
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        stateSaver?.saveState(outState)
    }

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        versionView = null
        versionFactory = null
        stateSaver = null
    }

    private fun showVersionUpgrade(newVersion: Int) {
        VersionUpgradeDialog.newInstance(newVersion)
            .show(this, VersionUpgradeDialog.TAG)
    }
}
