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

import com.pyamsoft.pydroid.base.rating.RatingErrorPublisher
import com.pyamsoft.pydroid.base.rating.RatingModule
import com.pyamsoft.pydroid.bus.EventBus
import com.pyamsoft.pydroid.loader.LoaderModule
import com.pyamsoft.pydroid.ui.UiModule

internal class RatingComponentImpl internal constructor(
  private val uiModule: UiModule,
  private val ratingModule: RatingModule,
  private val loaderModule: LoaderModule,
  ratingErrorBus: EventBus<Throwable>,
  private val version: Int
) : RatingComponent {

  private val ratingErrorPublisher: RatingErrorPublisher = RatingErrorPublisherImpl(ratingErrorBus)

  override fun inject(activity: RatingActivity) {
    activity.ratingPresenter = ratingModule.getPresenter(version)
  }

  override fun inject(dialog: RatingDialog) {
    dialog.presenter = ratingModule.getSavePresenter(version)
    dialog.imageLoader = loaderModule.provideImageLoader()
    dialog.linker = uiModule.provideLinker()
    dialog.errorPublisher = ratingErrorPublisher
  }
}
