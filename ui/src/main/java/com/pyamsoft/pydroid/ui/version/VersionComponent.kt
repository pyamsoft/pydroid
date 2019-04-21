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

package com.pyamsoft.pydroid.ui.version

import android.view.View
import androidx.annotation.CheckResult
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import com.pyamsoft.pydroid.bootstrap.rating.RatingModule
import com.pyamsoft.pydroid.bootstrap.version.VersionCheckModule
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.ui.navigation.FailedNavigationEvent
import com.pyamsoft.pydroid.ui.navigation.NavigationViewModel
import com.pyamsoft.pydroid.ui.rating.RatingComponent
import com.pyamsoft.pydroid.ui.rating.ShowRating

internal interface VersionComponent {

  @CheckResult
  fun plusRating(): RatingComponent.Factory

  fun inject(activity: VersionCheckActivity)

  interface Factory {

    @CheckResult
    fun create(
      owner: LifecycleOwner,
      snackbarRoot: View
    ): VersionComponent

  }

  class Impl private constructor(
    private val owner: LifecycleOwner,
    private val snackbarRoot: View,
    private val navigationBus: EventBus<FailedNavigationEvent>,
    private val schedulerProvider: SchedulerProvider,
    private val bus: EventBus<ShowRating>,
    private val networkBus: EventBus<VersionCheckState>,
    private val ratingModule: RatingModule,
    private val versionModule: VersionCheckModule
  ) : VersionComponent {

    override fun plusRating(): RatingComponent.Factory {
      return RatingComponent.Impl.FactoryImpl(schedulerProvider, bus, ratingModule)
    }

    override fun inject(activity: VersionCheckActivity) {
      val navigationViewModel = NavigationViewModel(schedulerProvider, navigationBus)
      val versionViewModel =
        VersionCheckViewModel(versionModule.provideInteractor(), schedulerProvider, networkBus)
      val versionView = VersionView(snackbarRoot, owner)
      val component =
        VersionCheckUiComponentImpl(navigationViewModel, versionViewModel, versionView)
      activity._versionComponent = component
    }

    internal class FactoryImpl internal constructor(
      private val navigationBus: EventBus<FailedNavigationEvent>,
      private val schedulerProvider: SchedulerProvider,
      private val bus: EventBus<ShowRating>,
      private val networkBus: EventBus<VersionCheckState>,
      private val ratingModule: RatingModule,
      private val versionModule: VersionCheckModule
    ) : Factory {

      override fun create(
        owner: LifecycleOwner,
        snackbarRoot: View
      ): VersionComponent {
        return Impl(
            owner, snackbarRoot, navigationBus,
            schedulerProvider, bus, networkBus,
            ratingModule, versionModule
        )
      }

    }

  }
}

