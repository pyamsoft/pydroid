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

package com.pyamsoft.pydroid.bootstrap.rating

import android.app.Activity
import com.pyamsoft.pydroid.core.Enforcer
import com.pyamsoft.pydroid.core.ResultWrapper
import com.pyamsoft.pydroid.util.MarketLinker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

internal class RatingInteractorImpl
internal constructor(
    private val rateMyApp: RateMyApp,
    private val ratingPreferences: RatingPreferences,
) : RatingInteractor {

  override suspend fun askForRating(): ResultWrapper<AppRatingLauncher> =
      withContext(context = Dispatchers.Default) {
        Enforcer.assertOffMainThread()

        return@withContext try {
          ResultWrapper.success(
              if (ratingPreferences.showRating()) {
                rateMyApp.startRating()
              } else {
                AppRatingLauncher.empty()
              })
        } catch (e: Throwable) {
          ResultWrapper.failure(e)
        }
      }

  override suspend fun loadMarketLauncher(): ResultWrapper<AppRatingLauncher> =
      withContext(context = Dispatchers.Default) {
        Enforcer.assertOffMainThread()
        return@withContext try {
          ResultWrapper.success(
              object : AppRatingLauncher {

                override suspend fun rate(activity: Activity): ResultWrapper<Unit> {
                  return MarketLinker.linkToMarketPage(activity)
                      .onSuccess { Timber.d("Opened market page for ${activity.packageName}") }
                      .onFailure {
                        Timber.e(it, "Failed to open market page for ${activity.packageName}")
                      }
                }
              })
        } catch (e: Throwable) {
          ResultWrapper.failure(e)
        }
      }
}
