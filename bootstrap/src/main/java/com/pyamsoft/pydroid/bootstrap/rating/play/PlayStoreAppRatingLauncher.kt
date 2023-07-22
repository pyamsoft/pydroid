/*
 * Copyright 2023 pyamsoft
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
 */

package com.pyamsoft.pydroid.bootstrap.rating.play

import android.app.Activity
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.pyamsoft.pydroid.bootstrap.rating.rate.AppRatingLauncher
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.core.ResultWrapper
import kotlin.coroutines.resume
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext

internal class PlayStoreAppRatingLauncher
internal constructor(
    private val manager: ReviewManager,
    private val info: ReviewInfo,
) : AppRatingLauncher {

  override suspend fun rate(activity: Activity): ResultWrapper<Unit> =
      withContext(context = Dispatchers.Main) {
        return@withContext suspendCancellableCoroutine { continuation ->
          manager
              .launchReviewFlow(activity, info)
              .addOnCanceledListener {
                Logger.w("In-app review was cancelled")
                continuation.cancel()
              }
              .addOnSuccessListener {
                Logger.d("In-app Review was a success")
                continuation.resume(ResultWrapper.success(Unit))
              }
              .addOnFailureListener { err ->
                Logger.e(err, "In-App review failed!")
                continuation.resume(ResultWrapper.failure(err))
              }
        }
      }
}
