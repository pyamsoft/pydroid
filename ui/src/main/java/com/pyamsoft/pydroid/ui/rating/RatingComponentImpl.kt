/*
 * Copyright (C) 2018 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pydroid.ui.rating

import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.bootstrap.rating.RatingModule
import com.pyamsoft.pydroid.loader.LoaderModule

internal class RatingComponentImpl internal constructor(
  private val owner: LifecycleOwner,
  private val ratingModule: RatingModule,
  private val loaderModule: LoaderModule,
  private val version: Int
) : RatingComponent {

  override fun inject(activity: RatingActivity) {
    activity.ratingViewModel = ratingModule.getViewModel(owner, version)
  }

  override fun inject(dialog: RatingDialog) {
    dialog.viewModel = ratingModule.getViewModel(owner, version)
    dialog.imageLoader = loaderModule.provideImageLoader()
    dialog.errorPublisher = ratingModule.getPublisher()
  }
}
