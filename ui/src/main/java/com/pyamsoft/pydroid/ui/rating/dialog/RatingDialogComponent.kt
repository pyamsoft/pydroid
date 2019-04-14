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
import com.pyamsoft.pydroid.ui.rating.dialog.RatingDialogComponent.RatingModule
import dagger.Binds
import dagger.BindsInstance
import dagger.Module
import dagger.Subcomponent
import javax.inject.Named

@Subcomponent(modules = [RatingModule::class])
internal interface RatingDialogComponent {

  fun inject(fragment: RatingDialog)

  @Subcomponent.Factory
  interface Factory {

    @CheckResult
    fun create(
      @BindsInstance @Named("rate_link") rateLink: String,
      @BindsInstance @Named("change_log_icon") changeLogIcon: Int,
      @BindsInstance @Named("change_log") changeLog: SpannedString,
      @BindsInstance parent: ViewGroup
    ): RatingDialogComponent

  }

  @Module
  abstract class RatingModule {

    @Binds
    @CheckResult
    internal abstract fun bindUiComponent(impl: RatingDialogUiComponentImpl): RatingDialogUiComponent

    @Binds
    @CheckResult
    internal abstract fun bindUiCallback(impl: RatingDialogHandler): RatingControlsView.Callback

  }
}
