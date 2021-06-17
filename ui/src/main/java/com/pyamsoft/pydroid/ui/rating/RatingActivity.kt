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
import com.pyamsoft.pydroid.arch.newUiController
import com.pyamsoft.pydroid.bootstrap.rating.AppRatingLauncher
import com.pyamsoft.pydroid.inject.Injector
import com.pyamsoft.pydroid.ui.PYDroidComponent
import com.pyamsoft.pydroid.ui.arch.fromViewModelFactory
import com.pyamsoft.pydroid.ui.internal.billing.BillingDialog
import com.pyamsoft.pydroid.ui.internal.changelog.dialog.ChangeLogDialog
import com.pyamsoft.pydroid.ui.internal.rating.RatingControllerEvent
import com.pyamsoft.pydroid.ui.internal.rating.RatingView
import com.pyamsoft.pydroid.ui.internal.rating.RatingViewEvent
import com.pyamsoft.pydroid.ui.internal.rating.RatingViewModel
import com.pyamsoft.pydroid.ui.internal.util.MarketLinker
import com.pyamsoft.pydroid.ui.util.openAppPage
import com.pyamsoft.pydroid.ui.version.VersionCheckActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

/** Activity which handles displaying an in-app rating prompt */
public abstract class RatingActivity : VersionCheckActivity() {

  private var stateSaver: StateSaver? = null

  internal var ratingView: RatingView? = null

  internal var ratingFactory: ViewModelProvider.Factory? = null
  private val viewModel by fromViewModelFactory<RatingViewModel> { ratingFactory }

  /** On post create */
  @CallSuper
  override fun onPostCreate(savedInstanceState: Bundle?) {
    super.onPostCreate(savedInstanceState)

    // Need to do this in onPostCreate because the snackbarRoot will not be available until
    // after subclass onCreate
    Injector.obtainFromApplication<PYDroidComponent>(this)
        .plusRating()
        .create(this) { snackbarRoot }
        .inject(this)

    stateSaver =
        createComponent(
            savedInstanceState,
            this,
            viewModel,
            controller =
                newUiController {
                  return@newUiController when (it) {
                    is RatingControllerEvent.LaunchRating ->
                        showRating(it.isFallbackEnabled, it.launcher)
                  }
                },
            requireNotNull(ratingView)) {
          return@createComponent when (it) {
            is RatingViewEvent.HideNavigation -> viewModel.handleClearNavigationError()
          }
        }

    // Attempt to load rating based on a couple various factors - does not always result
    // in a call to showRating
    viewModel.load(false)
  }

  /** On save instance state */
  @CallSuper
  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    stateSaver?.saveState(outState)
  }

  /** On destroy */
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
      if (ChangeLogDialog.isNotShown(activity) && BillingDialog.isNotShown(activity)) {
        try {
          launcher.rate(activity)
        } catch (throwable: Throwable) {
          Timber.e(throwable, "Unable to launch in-app rating")
          if (isFallbackEnabled) {
            MarketLinker.openAppPage(activity)
                .onSuccess { viewModel.handleNavigationSuccess() }
                .onFailure { viewModel.handleNavigationFailed(it) }
          }
        }
      }
    }
  }
}
