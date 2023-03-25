/*
 * Copyright 2022 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.bootstrap.rating

import com.pyamsoft.pydroid.bootstrap.rating.rate.AppRatingLauncher
import com.pyamsoft.pydroid.bootstrap.rating.rate.RateMyApp
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.core.ResultWrapper
import com.pyamsoft.pydroid.util.ifNotCancellation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class RatingInteractorImpl
internal constructor(
    private val rateMyApp: RateMyApp,
) : RatingInteractor {

  override suspend fun askForRating(): ResultWrapper<AppRatingLauncher> =
      withContext(context = Dispatchers.Default) {
        return@withContext try {
          ResultWrapper.success(rateMyApp.startRating())
        } catch (e: Throwable) {
          e.ifNotCancellation {
            Logger.e(e, "Failed to ask for rating")
            ResultWrapper.failure(e)
          }
        }
      }
}
