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
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import com.pyamsoft.pydroid.bootstrap.rating.RatingModule
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.loader.LoaderModule
import com.pyamsoft.pydroid.ui.navigation.FailedNavigationEvent
import com.pyamsoft.pydroid.ui.navigation.NavigationViewModel
import com.pyamsoft.pydroid.ui.rating.dialog.RatingDialogHandler.RatingEvent

internal interface RatingDialogComponent {

  fun inject(dialog: RatingDialog)

  interface Factory {

    @CheckResult
    fun create(
      rateLink: String,
      changeLogIcon: Int,
      changeLog: SpannedString,
      parent: ViewGroup
    ): RatingDialogComponent

  }

  class Impl private constructor(
    private val changeLogIcon: Int,
    private val rateLink: String,
    private val changeLog: SpannedString,
    private val parent: ViewGroup,
    private val schedulerProvider: SchedulerProvider,
    private val bus: EventBus<RatingEvent>,
    private val navigationBus: EventBus<FailedNavigationEvent>,
    private val loaderModule: LoaderModule,
    private val ratingModule: RatingModule
  ) : RatingDialogComponent {

    override fun inject(dialog: RatingDialog) {
      val handler = RatingDialogHandler(schedulerProvider, bus)
      val viewModel =
        RatingDialogViewModel(handler, ratingModule.provideInteractor(), schedulerProvider)
      val icon = RatingIconView(changeLogIcon, loaderModule.provideLoader(), parent)
      val changelog = RatingChangelogView(changeLog, parent)
      val controls = RatingControlsView(rateLink, parent, handler)
      val navigationViewModel = NavigationViewModel(schedulerProvider, navigationBus)
      val component = RatingDialogUiComponentImpl(
          viewModel, icon, changelog,
          controls, navigationViewModel
      )
      dialog.component = component
    }

    internal class FactoryImpl internal constructor(
      private val schedulerProvider: SchedulerProvider,
      private val bus: EventBus<RatingEvent>,
      private val navigationBus: EventBus<FailedNavigationEvent>,
      private val loaderModule: LoaderModule,
      private val ratingModule: RatingModule
    ) : Factory {

      override fun create(
        rateLink: String,
        changeLogIcon: Int,
        changeLog: SpannedString,
        parent: ViewGroup
      ): RatingDialogComponent {
        return Impl(
            changeLogIcon, rateLink, changeLog,
            parent, schedulerProvider, bus,
            navigationBus, loaderModule, ratingModule
        )
      }

    }

  }
}
