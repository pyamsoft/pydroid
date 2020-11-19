/*
 * Copyright 2020 Peter Kenji Yamanaka
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

import android.content.Intent
import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModelProvider
import com.pyamsoft.pydroid.arch.StateSaver
import com.pyamsoft.pydroid.arch.createComponent
import com.pyamsoft.pydroid.bootstrap.version.AppUpdateLauncher
import com.pyamsoft.pydroid.ui.Injector
import com.pyamsoft.pydroid.ui.PYDroidComponent
import com.pyamsoft.pydroid.ui.arch.viewModelFactory
import com.pyamsoft.pydroid.ui.privacy.PrivacyActivity
import com.pyamsoft.pydroid.ui.internal.version.VersionCheckControllerEvent.LaunchUpdate
import com.pyamsoft.pydroid.ui.internal.version.VersionCheckControllerEvent.ShowUpgrade
import com.pyamsoft.pydroid.ui.internal.version.VersionCheckView
import com.pyamsoft.pydroid.ui.internal.version.VersionCheckViewModel
import com.pyamsoft.pydroid.ui.internal.version.upgrade.VersionUpgradeDialog
import com.pyamsoft.pydroid.util.doOnStart
import timber.log.Timber

abstract class VersionCheckActivity : PrivacyActivity() {

    protected open val checkForUpdates: Boolean = true

    private var stateSaver: StateSaver? = null

    internal var versionCheckView: VersionCheckView? = null

    internal var versionFactory: ViewModelProvider.Factory? = null
    private val versionViewModel by viewModelFactory<VersionCheckViewModel> { versionFactory }

    @CallSuper
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Need to do this in onPostCreate because the snackbarRoot will not be available until
        // after subclass onCreate
        Injector.obtain<PYDroidComponent>(applicationContext)
            .plusVersionCheck()
            .create(this) { snackbarRoot }
            .inject(this)

        stateSaver = createComponent(
            savedInstanceState, this,
            versionViewModel,
            requireNotNull(versionCheckView)
        ) {
            return@createComponent when (it) {
                is LaunchUpdate -> showVersionUpgrade(it.launcher)
                ShowUpgrade -> VersionUpgradeDialog.show(this)
            }
        }
    }

    @CallSuper
    override fun onResume() {
        super.onResume()

        if (checkForUpdates) {
            doCheckForUpdate()
        }
    }

    private fun doCheckForUpdate() {
        Timber.d("Queue check for update onStart")
        doOnStart {
            Timber.d("Activity started, check for updates")
            versionViewModel.checkForUpdates(false)
        }
    }

    // Keep public for app consumers
    fun checkForUpdate() {
        // In case somebody calls this when the auto update check is still enabled
        check(!checkForUpdates) { "Do not call this method manually, updates are automatically checked onResume" }

        doCheckForUpdate()
    }

    @CallSuper
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        stateSaver?.saveState(outState)
    }

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        versionCheckView = null
        versionFactory = null
        stateSaver = null
    }

    @CallSuper
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_APP_UPDATE) {
            if (resultCode == RESULT_OK) {
                Timber.d("Update flow succeeded!")
            } else {
                Timber.d("User has cancelled or denied the update")
            }
        }
    }

    // Used by AppSettingsPreferenceFragment too
    internal fun showVersionUpgrade(launcher: AppUpdateLauncher) {
        launcher.update(this, RC_APP_UPDATE)
    }

    companion object {

        private const val RC_APP_UPDATE = 176923
    }
}
