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

package com.pyamsoft.pydroid.bootstrap.changelog

import android.content.Context
import androidx.annotation.CheckResult

/** Change log module */
public class ChangeLogModule(params: Parameters) {

  private val impl =
      ChangeLogInteractorImpl(
          context = params.context,
          preferences = params.preferences,
          isFakeChangeLogAvailable = params.isFakeChangeLogAvailable,
      )

  /** Provide a change log interactor */
  @CheckResult
  public fun provideInteractor(): ChangeLogInteractor {
    return impl
  }

  /** ChangeLogModule parameters */
  public data class Parameters
  @JvmOverloads
  public constructor(
      internal val context: Context,
      internal val preferences: ChangeLogPreferences,
      /** If this field is set, the changelog module will always request to show a changelog */
      internal val isFakeChangeLogAvailable: Boolean = false
  )
}
