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

import android.app.Activity
import android.text.SpannedString
import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.loader.LoaderModule
import com.pyamsoft.pydroid.ui.PYDroidViewModelFactory

internal interface RatingDialogComponent {

  fun inject(dialog: RatingDialog)

  interface Factory {

    @CheckResult
    fun create(
      activity: Activity,
      parent: ViewGroup,
      owner: LifecycleOwner,
      rateLink: String,
      changeLogIcon: Int,
      changeLog: SpannedString
    ): RatingDialogComponent

  }

  class Impl private constructor(
    private val activity: Activity,
    private val parent: ViewGroup,
    private val changeLogIcon: Int,
    private val rateLink: String,
    private val changeLog: SpannedString,
    private val owner: LifecycleOwner,
    private val loaderModule: LoaderModule,
    private val factoryProvider: (activity: Activity) -> PYDroidViewModelFactory
  ) : RatingDialogComponent {

    override fun inject(dialog: RatingDialog) {
      val icon = RatingIconView(changeLogIcon, loaderModule.provideLoader(), parent)
      val changelog = RatingChangelogView(changeLog, parent)
      val controls = RatingControlsView(rateLink, owner, parent)

      dialog.viewModelFactory = factoryProvider(activity)
      dialog.iconView = icon
      dialog.changelogView = changelog
      dialog.controlsView = controls
    }

    internal class FactoryImpl internal constructor(
      private val loaderModule: LoaderModule,
      private val factoryProvider: (activity: Activity) -> PYDroidViewModelFactory
    ) : Factory {

      override fun create(
        activity: Activity,
        parent: ViewGroup,
        owner: LifecycleOwner,
        rateLink: String,
        changeLogIcon: Int,
        changeLog: SpannedString
      ): RatingDialogComponent {
        return Impl(
            activity, parent, changeLogIcon, rateLink, changeLog,
            owner, loaderModule, factoryProvider
        )
      }

    }

  }
}
