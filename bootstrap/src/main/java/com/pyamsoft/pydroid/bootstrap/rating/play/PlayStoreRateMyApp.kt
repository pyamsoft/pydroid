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

package com.pyamsoft.pydroid.bootstrap.rating.play

import android.content.Context
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.pyamsoft.pydroid.bootstrap.rating.AbstractRateMyApp
import com.pyamsoft.pydroid.bootstrap.rating.rate.AppRatingLauncher
import com.pyamsoft.pydroid.core.ThreadEnforcer

internal class PlayStoreRateMyApp
internal constructor(
    enforcer: ThreadEnforcer,
    context: Context,
) :
    AbstractRateMyApp<ReviewManager>(
        enforcer = enforcer,
        resolveReviewManager = { ReviewManagerFactory.create(context.applicationContext) },
    ) {

  override fun createRatingLauncher(info: ReviewInfo): AppRatingLauncher =
      PlayStoreAppRatingLauncher(
          manager = manager,
          info = info,
      )
}
