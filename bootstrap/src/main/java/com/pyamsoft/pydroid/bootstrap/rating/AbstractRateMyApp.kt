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

package com.pyamsoft.pydroid.bootstrap.rating

import androidx.compose.runtime.CheckResult
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.pyamsoft.pydroid.bootstrap.rating.rate.AppRatingLauncher
import com.pyamsoft.pydroid.bootstrap.rating.rate.RateMyApp
import com.pyamsoft.pydroid.core.ThreadEnforcer
import com.pyamsoft.pydroid.util.Logger
import kotlin.coroutines.resume
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext

internal abstract class AbstractRateMyApp<T : ReviewManager>
protected constructor(
  enforcer: ThreadEnforcer,
  resolveReviewManager: () -> T,
) : RateMyApp {

  protected val manager by lazy {
    enforcer.assertOffMainThread()
    resolveReviewManager()
  }

  final override suspend fun startRating(): AppRatingLauncher =
    withContext(context = Dispatchers.IO) {
      onBeforeStartRating()
      return@withContext suspendCancellableCoroutine { continuation ->
        manager
          .requestReviewFlow()
          .addOnCanceledListener {
            Logger.w { "Review task has been cancelled" }
            continuation.cancel()
          }
          .addOnFailureListener { error ->
            Logger.e(error) { "Failed to resolve app review info task" }
            continuation.resume(AppRatingLauncher.empty())
          }
          .addOnSuccessListener { info ->
            // Always false in play-core 1.10.3, but nullable in rating 2.0.0
            if (info == null) {
              Logger.w { "Successful request had NULL review info" }
              continuation.resume(AppRatingLauncher.empty())
            } else {
              continuation.resume(createRatingLauncher(info))
            }
          }
      }
    }

  @CheckResult
  protected abstract fun createRatingLauncher(info: ReviewInfo): AppRatingLauncher

  protected open suspend fun onBeforeStartRating() {
  }
}
