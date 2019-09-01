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

package com.pyamsoft.pydroid.ui.privacy

import android.os.Bundle
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModelProvider
import com.pyamsoft.pydroid.arch.createComponent
import com.pyamsoft.pydroid.ui.Injector
import com.pyamsoft.pydroid.ui.PYDroidComponent
import com.pyamsoft.pydroid.ui.app.ActivityBase
import com.pyamsoft.pydroid.ui.arch.factory
import com.pyamsoft.pydroid.ui.privacy.PrivacyControllerEvent.ViewExternalPolicy
import com.pyamsoft.pydroid.util.HyperlinkIntent

abstract class PrivacyActivity : ActivityBase() {

    internal var privacyFactory: ViewModelProvider.Factory? = null
    internal var privacyView: PrivacyView? = null
    private val viewModel by factory<PrivacyViewModel> { privacyFactory }

    /**
     * Used for Activity level snackbars
     */
    protected abstract val snackbarRoot: ViewGroup

    @CallSuper
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Need to do this in onPostCreate because the snackbarRoot will not be available until
        // after subclass onCreate
        Injector.obtain<PYDroidComponent>(applicationContext)
            .plusPrivacy()
            .create(this) { snackbarRoot }
            .inject(this)

        createComponent(
            savedInstanceState, this,
            viewModel,
            requireNotNull(privacyView)
        ) {
            return@createComponent when (it) {
                is ViewExternalPolicy -> openExternalPolicyPage(it.link)
            }
        }
    }

    private fun openExternalPolicyPage(link: HyperlinkIntent) {
        val error = link.navigate()
        if (error == null) {
            viewModel.navigationSuccess()
        } else {
            viewModel.navigationFailed(error)
        }
    }
}
