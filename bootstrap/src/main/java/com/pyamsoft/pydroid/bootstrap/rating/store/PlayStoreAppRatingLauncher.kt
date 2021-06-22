/*
 * Copyright 2020 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.bootstrap.rating.store

import android.app.Activity
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.pyamsoft.pydroid.bootstrap.rating.AppRatingLauncher
import com.pyamsoft.pydroid.bootstrap.rating.RatingPreferences
import com.pyamsoft.pydroid.core.Enforcer
import com.pyamsoft.pydroid.core.ResultWrapper
import kotlin.coroutines.resume
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import timber.log.Timber

internal class PlayStoreAppRatingLauncher
internal constructor(
    private val preferences: RatingPreferences,
    private val manager: ReviewManager,
    private val info: ReviewInfo
) : AppRatingLauncher {

  override suspend fun rate(activity: Activity): ResultWrapper<Unit> =
      withContext(context = Dispatchers.Main) {
        Enforcer.assertOnMainThread()

        withContext(context = Dispatchers.IO) {
          Enforcer.assertOffMainThread()
          preferences.markRatingShown()
        }

        Enforcer.assertOnMainThread()
        return@withContext suspendCancellableCoroutine { continuation ->
          manager
              .launchReviewFlow(activity, info)
              .addOnSuccessListener {
                Timber.d("In-app Review was a success")
                continuation.resume(ResultWrapper.success(Unit))
              }
              .addOnFailureListener { err ->
                Timber.e(err, "In-App review failed!")
                continuation.resume(ResultWrapper.failure(err))
              }
        }
      }
}
