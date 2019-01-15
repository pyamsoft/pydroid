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
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import com.pyamsoft.pydroid.bootstrap.rating.RatingModule
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.loader.LoaderModule

internal class RatingDialogComponentImpl internal constructor(
  private val ratingModule: RatingModule,
  private val loaderModule: LoaderModule,
  private val schedulerProvider: SchedulerProvider,
  private val parent: ViewGroup,
  private val owner: LifecycleOwner,
  private val rateLink: String,
  private val changelogIcon: Int,
  private val changelog: SpannedString,
  private val uiBus: EventBus<RatingViewEvent>
) : RatingDialogComponent {

  override fun inject(dialog: RatingDialog) {
    val iconView = RatingIconView(parent, changelogIcon, loaderModule.provideImageLoader(), owner)
    val changelogView = RatingChangelogView(parent, changelog)
    val controlsView = RatingControlsView(parent, rateLink, uiBus)
    dialog.iconComponent = RatingIconUiComponent(iconView)
    dialog.changelogComponent = RatingChangelogUiComponent(changelogView)
    dialog.controlsComponent = RatingControlsUiComponent(controlsView, uiBus, schedulerProvider)
    dialog.worker = RatingDialogWorker(ratingModule.interactor, schedulerProvider)
  }

}