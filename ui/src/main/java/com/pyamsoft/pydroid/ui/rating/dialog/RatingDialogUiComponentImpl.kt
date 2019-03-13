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

package com.pyamsoft.pydroid.ui.rating.dialog

import android.content.ActivityNotFoundException
import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.arch.BaseUiComponent
import com.pyamsoft.pydroid.arch.doOnDestroy
import com.pyamsoft.pydroid.ui.navigation.FailedNavigationPresenter
import com.pyamsoft.pydroid.ui.rating.dialog.RatingDialogUiComponent.Callback

internal class RatingDialogUiComponentImpl internal constructor(
  private val presenter: RatingDialogPresenter,
  private val iconView: RatingIconView,
  private val changelogView: RatingChangelogView,
  private val controlsView: RatingControlsView,
  private val failedNavigationPresenter: FailedNavigationPresenter
) : BaseUiComponent<RatingDialogUiComponent.Callback>(),
    RatingDialogUiComponent,
    RatingDialogPresenter.Callback {

  override fun onBind(
    owner: LifecycleOwner,
    savedInstanceState: Bundle?,
    callback: Callback
  ) {
    owner.doOnDestroy {
      iconView.teardown()
      changelogView.teardown()
      controlsView.teardown()
      presenter.unbind()
    }

    iconView.inflate(savedInstanceState)
    changelogView.inflate(savedInstanceState)
    controlsView.inflate(savedInstanceState)
    presenter.bind(this)
  }

  override fun saveState(outState: Bundle) {
    iconView.saveState(outState)
    changelogView.saveState(outState)
    controlsView.saveState(outState)
  }

  override fun navigationFailed(error: ActivityNotFoundException) {
    failedNavigationPresenter.failedNavigation(error)
  }

  override fun onVisitApplicationPageToRate(packageName: String) {
    callback.onNavigateToApplicationPage(packageName)
  }

  override fun onDidNotRate() {
    callback.onCancelRating()
  }

}