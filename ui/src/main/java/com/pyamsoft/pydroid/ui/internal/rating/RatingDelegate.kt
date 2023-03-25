/*
 * Copyright 2023 pyamsoft
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

package com.pyamsoft.pydroid.ui.internal.rating

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.pyamsoft.pydroid.bootstrap.rating.rate.AppRatingLauncher
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.util.doOnDestroy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class RatingDelegate(
    activity: FragmentActivity,
    viewModel: RatingViewModeler,
    private val disabled: Boolean,
) {

  private var hostingActivity: FragmentActivity? = activity
  private var ratingViewModel: RatingViewModeler? = viewModel

  init {
    if (disabled) {
      Logger.w("Application has disabled the Rating component")
    } else {
      Logger.d("In-App Rating is enabled. Awaiting manual call")
    }

    activity.doOnDestroy {
      ratingViewModel = null
      hostingActivity = null
    }
  }

  private fun showRating(
      activity: FragmentActivity,
      launcher: AppRatingLauncher,
  ) {
    // Enforce that we do this on the Main thread
    activity.lifecycleScope.launch(context = Dispatchers.Main) {
      launcher
          .rate(activity)
          .onSuccess { Logger.d("Call was made for in-app rating request") }
          .onFailure { err -> Logger.e(err, "Unable to launch in-app rating") }
    }
  }

  /**
   * Attempt to call in-app rating dialog. Does not always result in showing the Dialog, that is up
   * to Google
   */
  fun loadInAppRating() {
    if (disabled) {
      Logger.w("Application has disabled the Rating component")
      return
    }

    val act = hostingActivity.requireNotNull()
    ratingViewModel
        .requireNotNull()
        .loadInAppRating(
            scope = act.lifecycleScope,
            onLaunchInAppRating = { showRating(act, it) },
        )
  }
}
