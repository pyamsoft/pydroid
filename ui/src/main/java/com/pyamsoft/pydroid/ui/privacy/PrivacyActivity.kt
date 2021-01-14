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

package com.pyamsoft.pydroid.ui.privacy

import android.os.Bundle
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModelProvider
import com.pyamsoft.pydroid.arch.StateSaver
import com.pyamsoft.pydroid.arch.createComponent
import com.pyamsoft.pydroid.ui.Injector
import com.pyamsoft.pydroid.ui.PYDroidComponent
import com.pyamsoft.pydroid.ui.app.ActivityBase
import com.pyamsoft.pydroid.ui.arch.fromViewModelFactory
import com.pyamsoft.pydroid.ui.internal.privacy.PrivacyControllerEvent.ViewExternalPolicy
import com.pyamsoft.pydroid.ui.internal.privacy.PrivacyView
import com.pyamsoft.pydroid.ui.internal.privacy.PrivacyViewModel
import com.pyamsoft.pydroid.util.HyperlinkIntent
import com.pyamsoft.pydroid.util.hyperlink

/**
 * Activity which handles displaying a privacy policy via menu items
 */
public abstract class PrivacyActivity : ActivityBase() {

    private var stateSaver: StateSaver? = null

    internal var privacyView: PrivacyView? = null

    internal var privacyFactory: ViewModelProvider.Factory? = null
    private val viewModel by fromViewModelFactory<PrivacyViewModel> { privacyFactory }

    /**
     * Used for Activity level snackbars
     */
    protected abstract val snackbarRoot: ViewGroup

    /**
     * On post create before start
     */
    @CallSuper
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Need to do this in onPostCreate because the snackbarRoot will not be available until
        // after subclass onCreate
        Injector.obtainFromApplication<PYDroidComponent>(this)
            .plusPrivacy()
            .create(this) { snackbarRoot }
            .inject(this)

        stateSaver = createComponent(
            savedInstanceState, this,
            viewModel,
            requireNotNull(privacyView)
        ) {
            return@createComponent when (it) {
                is ViewExternalPolicy -> openExternalPolicyPage(it.url.hyperlink(this))
            }
        }
    }

    private fun openExternalPolicyPage(link: HyperlinkIntent) {
        link.navigate()
            .onSuccess { viewModel.navigationSuccess() }
            .onFailure { viewModel.navigationFailed(it) }
    }

    /**
     * On destroy
     */
    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        privacyFactory = null
        privacyView = null
        stateSaver = null
    }

    /**
     * On save instance state
     */
    @CallSuper
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        stateSaver?.saveState(outState)
    }
}
