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
import androidx.activity.viewModels
import androidx.annotation.CallSuper
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.pyamsoft.pydroid.bootstrap.rating.AppRatingLauncher
import com.pyamsoft.pydroid.core.Logger

import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.inject.Injector
import com.pyamsoft.pydroid.ui.PYDroidComponent
import com.pyamsoft.pydroid.ui.internal.rating.RatingControllerEvent.LaunchMarketPage
import com.pyamsoft.pydroid.ui.internal.rating.RatingControllerEvent.LaunchRating
import com.pyamsoft.pydroid.ui.internal.rating.RatingScreen
import com.pyamsoft.pydroid.ui.internal.rating.RatingViewModel
import com.pyamsoft.pydroid.ui.version.VersionCheckActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** Activity which handles displaying an in-app rating prompt */
public abstract class RatingActivity : VersionCheckActivity() {

  internal var ratingFactory: ViewModelProvider.Factory? = null
  private val viewModel by viewModels<RatingViewModel> { ratingFactory.requireNotNull() }

  /** On create */
  @CallSuper
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Injector.obtainFromApplication<PYDroidComponent>(this).plusRating().create().inject(this)

    viewModel.bindController(this) { event ->
      return@bindController when (event) {
        is LaunchMarketPage -> showRating(event.launcher)
        is LaunchRating -> showRating(event.launcher)
      }
    }
  }

  /**
   * Rating screen
   *
   * Handles showing an in-app rating dialog and any UI around navigation errors related to ratings
   */
  @Composable
  protected fun RatingScreen(
      scaffoldState: ScaffoldState,
  ) {
    RatingScreen(
        snackbarHostState = scaffoldState.snackbarHostState,
        addSnackbarHost = false,
    )
  }

  /**
   * Rating screen
   *
   * Handles showing an in-app rating dialog and any UI around navigation errors related to ratings
   */
  @Composable
  protected fun RatingScreen(
      snackbarHostState: SnackbarHostState,
  ) {
    RatingScreen(
        snackbarHostState = snackbarHostState,
        addSnackbarHost = true,
    )
  }

  @Composable
  private fun RatingScreen(
      snackbarHostState: SnackbarHostState,
      addSnackbarHost: Boolean,
  ) {
    val state by viewModel.compose()

    RatingScreen(
        state = state,
        addSnackbarHost = addSnackbarHost,
        snackbarHostState = snackbarHostState,
        onNavigationErrorDismissed = { viewModel.handleClearNavigationError() },
    )
  }

  /**
   * Attempt to call in-app rating dialog. Does not always result in showing the Dialog, that is up
   * to Google
   */
  protected fun loadInAppRating() {
    viewModel.loadInAppRating()
  }

  /** On destroy */
  @CallSuper
  override fun onDestroy() {
    super.onDestroy()
    ratingFactory = null
  }

  private fun showRating(launcher: AppRatingLauncher) {
    val activity = this

    // Enforce that we do this on the Main thread
    lifecycleScope.launch(context = Dispatchers.Main) {
      launcher.rate(activity).onFailure { err -> Logger.e(err, "Unable to launch in-app rating") }
    }
  }
}
