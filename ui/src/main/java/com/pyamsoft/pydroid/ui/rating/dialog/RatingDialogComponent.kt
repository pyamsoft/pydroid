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
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import com.pyamsoft.pydroid.bootstrap.rating.RatingModule
import com.pyamsoft.pydroid.loader.LoaderModule

internal interface RatingDialogComponent {

  fun inject(dialog: RatingDialog)

  interface Factory {

    @CheckResult
    fun create(
      parent: ViewGroup,
      owner: LifecycleOwner,
      rateLink: String,
      changeLogIcon: Int,
      changeLog: SpannedString
    ): RatingDialogComponent

  }

  class Impl private constructor(
    private val parent: ViewGroup,
    private val changeLogIcon: Int,
    private val rateLink: String,
    private val changeLog: SpannedString,
    private val owner: LifecycleOwner,
    private val schedulerProvider: SchedulerProvider,
    private val loaderModule: LoaderModule,
    private val ratingModule: RatingModule
  ) : RatingDialogComponent {

    override fun inject(dialog: RatingDialog) {
      val viewModel = RatingDialogViewModel(
          changeLog, rateLink, changeLogIcon,
          ratingModule.provideInteractor(), schedulerProvider
      )
      val icon = RatingIconView(loaderModule.provideLoader(), parent)
      val changelog = RatingChangelogView(parent)
      val controls = RatingControlsView(owner, parent)

      dialog.viewModel = viewModel
      dialog.iconView = icon
      dialog.changelogView = changelog
      dialog.controlsView = controls
    }

    internal class FactoryImpl internal constructor(
      private val schedulerProvider: SchedulerProvider,
      private val loaderModule: LoaderModule,
      private val ratingModule: RatingModule
    ) : Factory {

      override fun create(
        parent: ViewGroup,
        owner: LifecycleOwner,
        rateLink: String,
        changeLogIcon: Int,
        changeLog: SpannedString
      ): RatingDialogComponent {
        return Impl(
            parent, changeLogIcon, rateLink, changeLog,
            owner, schedulerProvider,
            loaderModule, ratingModule
        )
      }

    }

  }
}
