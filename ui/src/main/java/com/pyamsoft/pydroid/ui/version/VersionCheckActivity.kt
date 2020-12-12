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
import androidx.lifecycle.lifecycleScope
import com.pyamsoft.pydroid.arch.StateSaver
import com.pyamsoft.pydroid.arch.createComponent
import com.pyamsoft.pydroid.bootstrap.version.AppUpdateLauncher
import com.pyamsoft.pydroid.ui.Injector
import com.pyamsoft.pydroid.ui.PYDroidComponent
import com.pyamsoft.pydroid.ui.arch.viewModelFactory
import com.pyamsoft.pydroid.ui.internal.util.MarketLinker
import com.pyamsoft.pydroid.ui.internal.version.VersionCheckComponent
import com.pyamsoft.pydroid.ui.internal.version.VersionCheckControllerEvent.LaunchUpdate
import com.pyamsoft.pydroid.ui.internal.version.VersionCheckControllerEvent.ShowUpgrade
import com.pyamsoft.pydroid.ui.internal.version.VersionCheckView
import com.pyamsoft.pydroid.ui.internal.version.VersionCheckViewModel
import com.pyamsoft.pydroid.ui.internal.version.upgrade.VersionUpgradeDialog
import com.pyamsoft.pydroid.ui.privacy.PrivacyActivity
import com.pyamsoft.pydroid.ui.util.openAppPage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class VersionCheckActivity : PrivacyActivity() {

    protected open val checkForUpdates: Boolean = true

    private var stateSaver: StateSaver? = null

    internal var versionCheckView: VersionCheckView? = null

    internal var versionFactory: ViewModelProvider.Factory? = null
    private val viewModel by viewModelFactory<VersionCheckViewModel> { versionFactory }

    private var injector: VersionCheckComponent? = null

    @CallSuper
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Need to do this in onPostCreate because the snackbarRoot will not be available until
        // after subclass onCreate
        injector = Injector.obtain<PYDroidComponent>(applicationContext)
            .plusVersionCheck()
            .create(this, this) { snackbarRoot }.also { component ->
                component.inject(this)
            }

        stateSaver = createComponent(
            savedInstanceState, this,
            viewModel,
            requireNotNull(versionCheckView)
        ) {
            return@createComponent when (it) {
                is LaunchUpdate -> showVersionUpgrade(it.isFallbackEnabled, it.launcher)
                ShowUpgrade -> VersionUpgradeDialog.show(this)
            }
        }

        if (checkForUpdates) {
            checkUpdates()
        }
    }

    // Provide this graph as a service injector
    @CallSuper
    override fun getSystemService(name: String): Any? {
        return when (name) {
            VersionCheckComponent::class.java.name -> requireNotNull(injector)
            else -> super.getSystemService(name)
        }
    }

    @CallSuper
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        stateSaver?.saveState(outState)
    }

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        versionFactory = null
        versionCheckView = null
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

    private fun checkUpdates() {
        require(checkForUpdates) { "checkUpdates() will be called automatically, do not call this manually." }
        viewModel.checkForUpdates(false)
    }

    // Expose for applications to call manually
    fun checkForUpdates() {
        require(!checkForUpdates) { "checkForUpdates() must be called manually and cannot be called when checkForUpdates is automatic." }
        viewModel.checkForUpdates(false)
    }

    private fun showVersionUpgrade(isFallbackEnabled: Boolean, launcher: AppUpdateLauncher) {
        val activity = this

        // Enforce that we do this on the Main thread
        lifecycleScope.launch(context = Dispatchers.Main) {
            try {
                launcher.update(activity, RC_APP_UPDATE)
            } catch (throwable: Throwable) {
                Timber.e(throwable, "Unable to launch in-app update flow")
                if (isFallbackEnabled) {
                    val error = MarketLinker.openAppPage(activity)
                    if (error == null) {
                        viewModel.navigationSuccess()
                    } else {
                        viewModel.navigationFailed(error)
                    }
                }
            }
        }
    }

    companion object {

        // Only bottom 16 bits.
        private const val RC_APP_UPDATE = 146
    }
}
