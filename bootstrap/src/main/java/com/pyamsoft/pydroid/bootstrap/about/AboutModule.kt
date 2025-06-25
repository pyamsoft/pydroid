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

package com.pyamsoft.pydroid.bootstrap.about

import android.content.Context
import androidx.annotation.CheckResult

/** About module */
public class AboutModule(
    params: Parameters,
) {

  private val impl =
      AboutInteractorImpl(
          appContext = params.context,
      )

  /** Provide an instance of an AboutInteractor */
  @CheckResult
  public fun provideInteractor(): AboutInteractor {
    return impl
  }

  /** Module parameters */
  public data class Parameters(internal val context: Context)
}
