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

import android.app.Activity
import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import com.pyamsoft.pydroid.bootstrap.rating.RatingModule
import com.pyamsoft.pydroid.ui.PYDroidViewModelFactory
import com.pyamsoft.pydroid.ui.rating.RatingComponent

internal interface VersionComponent {

  @CheckResult
  fun plusRating(): RatingComponent.Factory

  fun inject(activity: VersionCheckActivity)

  interface Factory {

    @CheckResult
    fun create(
      owner: LifecycleOwner,
      parent: ViewGroup
    ): VersionComponent

  }

  class Impl private constructor(
    private val parent: ViewGroup,
    private val owner: LifecycleOwner,
    private val schedulerProvider: SchedulerProvider,
    private val ratingModule: RatingModule,
    private val factoryProvider: (activity: Activity) -> PYDroidViewModelFactory
  ) : VersionComponent {

    override fun plusRating(): RatingComponent.Factory {
      return RatingComponent.Impl.FactoryImpl(schedulerProvider, ratingModule)
    }

    override fun inject(activity: VersionCheckActivity) {
      val versionView = VersionView(owner, parent)

      activity.versionViewModelFactory = factoryProvider(activity)
      activity.versionView = versionView
    }

    internal class FactoryImpl internal constructor(
      private val schedulerProvider: SchedulerProvider,
      private val ratingModule: RatingModule,
      private val factoryProvider: (activity: Activity) -> PYDroidViewModelFactory
    ) : Factory {

      override fun create(
        owner: LifecycleOwner,
        parent: ViewGroup
      ): VersionComponent {
        return Impl(parent, owner, schedulerProvider, ratingModule, factoryProvider)
      }

    }

  }
}

