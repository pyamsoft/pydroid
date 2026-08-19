/*
 * Copyright 2026 pyamsoft
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

import android.content.Context
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.bootstrap.rating.rate.RateMyApp
import com.pyamsoft.pydroid.core.ThreadEnforcer
import com.pyamsoft.pydroid.util.AppDispatchers

/** Rating module */
public abstract class RatingModule(params: Parameters) {

  private val impl: RatingInteractor

  init {
    val rateMyApp = newRater(params)
    impl =
        RatingInteractorImpl(
            rateMyApp = rateMyApp,
            dispatchers = params.dispatchers,
        )
  }

  /** Provide a rating interactor */
  @CheckResult
  public fun provideInteractor(): RatingInteractor {
    return impl
  }

  /** Is this a "live" client, or is it no-op */
  @CheckResult public abstract fun isLive(): Boolean

  @CheckResult protected abstract fun newRater(params: Parameters): RateMyApp

  /** Module parameters */
  public data class Parameters(
      val context: Context,
      val enforcer: ThreadEnforcer,
      val dispatchers: AppDispatchers,
  )
}
