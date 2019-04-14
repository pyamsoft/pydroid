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

package com.pyamsoft.pydroid.ui.rating

import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.ui.rating.RatingComponent.RatingModule
import dagger.Binds
import dagger.Module
import dagger.Subcomponent

@Subcomponent(modules = [RatingModule::class])
internal interface RatingComponent {

  fun inject(fragment: RatingActivity)

  @Subcomponent.Factory
  interface Factory {

    @CheckResult
    fun create(): RatingComponent

  }

  @Module
  abstract class RatingModule {

    @Binds
    @CheckResult
    internal abstract fun bindUiComponent(impl: RatingUiComponentImpl): RatingUiComponent

  }
}
