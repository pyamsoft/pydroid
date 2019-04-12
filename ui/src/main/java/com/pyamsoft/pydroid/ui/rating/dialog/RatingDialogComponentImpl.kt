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

import android.text.SpannedString
import android.view.ViewGroup
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import com.pyamsoft.pydroid.bootstrap.rating.RatingInteractor
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.loader.ImageLoader
import com.pyamsoft.pydroid.ui.navigation.FailedNavigationEvent
import com.pyamsoft.pydroid.ui.navigation.NavigationViewModel

internal class RatingDialogComponentImpl internal constructor(
  private val interactor: RatingInteractor,
  private val imageLoader: ImageLoader,
  private val schedulerProvider: SchedulerProvider,
  private val parent: ViewGroup,
  private val rateLink: String,
  private val changelogIcon: Int,
  private val changelog: SpannedString,
  private val failedNavBus: EventBus<FailedNavigationEvent>
) : RatingDialogComponent {

  override fun inject(dialog: RatingDialog) {
    val failedPresenter = NavigationViewModel(schedulerProvider, failedNavBus)
    val presenter = RatingDialogViewModel(interactor, schedulerProvider)
    val controlsView = RatingControlsView(rateLink, parent, presenter)
    val iconView = RatingIconView(changelogIcon, imageLoader, parent)
    val changelogView = RatingChangelogView(changelog, parent)

    dialog.apply {
      this.component = RatingDialogUiComponentImpl(
          presenter, iconView, changelogView, controlsView, failedPresenter
      )
    }
  }

}
