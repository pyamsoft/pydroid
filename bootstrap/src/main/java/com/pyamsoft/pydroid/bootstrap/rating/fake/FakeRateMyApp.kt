/*
 * Copyright 2025 pyamsoft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pydroid.bootstrap.rating.fake

import android.content.Context
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.testing.FakeReviewManager
import com.pyamsoft.pydroid.bootstrap.rating.AbstractRateMyApp
import com.pyamsoft.pydroid.bootstrap.rating.rate.AppRatingLauncher
import com.pyamsoft.pydroid.core.ThreadEnforcer
import com.pyamsoft.pydroid.util.Logger
import kotlinx.coroutines.delay

internal class FakeRateMyApp
internal constructor(
    enforcer: ThreadEnforcer,
    context: Context,
) :
    AbstractRateMyApp<FakeReviewManager>(
        enforcer = enforcer,
        resolveReviewManager = { FakeReviewManager(context.applicationContext) }) {

  override suspend fun onBeforeStartRating() {
    Logger.d { "In debug mode we fake a delay to mimic real world network turnaround time." }
    delay(2000L)
  }

  override fun createRatingLauncher(info: ReviewInfo): AppRatingLauncher =
      FakeAppRatingLauncher(
          manager = manager,
          info = info,
      )
}
