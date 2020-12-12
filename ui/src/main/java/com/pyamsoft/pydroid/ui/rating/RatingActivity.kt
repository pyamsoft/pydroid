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

package com.pyamsoft.pydroid.ui.rating

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.pyamsoft.pydroid.arch.StateSaver
import com.pyamsoft.pydroid.arch.createComponent
import com.pyamsoft.pydroid.bootstrap.rating.AppRatingLauncher
import com.pyamsoft.pydroid.ui.Injector
import com.pyamsoft.pydroid.ui.PYDroidComponent
import com.pyamsoft.pydroid.ui.arch.viewModelFactory
import com.pyamsoft.pydroid.ui.internal.changelog.dialog.ChangeLogDialog
import com.pyamsoft.pydroid.ui.internal.rating.RatingControllerEvent.LoadRating
import com.pyamsoft.pydroid.ui.internal.rating.RatingView
import com.pyamsoft.pydroid.ui.internal.rating.RatingViewModel
import com.pyamsoft.pydroid.ui.internal.util.MarketLinker
import com.pyamsoft.pydroid.ui.util.openAppPage
import com.pyamsoft.pydroid.ui.version.VersionCheckActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class RatingActivity : VersionCheckActivity() {

    private var stateSaver: StateSaver? = null

    internal var ratingView: RatingView? = null

    internal var ratingFactory: ViewModelProvider.Factory? = null
    private val viewModel by viewModelFactory<RatingViewModel> { ratingFactory }

    @CallSuper
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Need to do this in onPostCreate because the snackbarRoot will not be available until
        // after subclass onCreate
        Injector.obtain<PYDroidComponent>(applicationContext)
            .plusRating()
            .create(this)
            .inject(this)

        stateSaver = createComponent(
            savedInstanceState, this,
            viewModel,
            requireNotNull(ratingView)
        ) {
            return@createComponent when (it) {
                is LoadRating -> showRating(it.isFallbackEnabled, it.launcher)
            }
        }

        viewModel.load(false)
    }

    @CallSuper
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        stateSaver?.saveState(outState)
    }

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        ratingFactory = null
        stateSaver = null
    }

    private fun showRating(isFallbackEnabled: Boolean, launcher: AppRatingLauncher) {
        val activity = this

        // Enforce that we do this on the Main thread
        lifecycleScope.launch(context = Dispatchers.Main) {
            if (ChangeLogDialog.isNotShown(activity)) {
                try {
                    launcher.rate(activity)
                } catch (throwable: Throwable) {
                    Timber.e(throwable, "Unable to launch in-app rating")
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
    }
}
