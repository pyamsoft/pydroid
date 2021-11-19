/*
 * Copyright 2021 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.bootstrap.datapolicy

import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.bootstrap.app.AppInteractor

/** Interactor which handles consent of Data policy */
public interface DataPolicyInteractor : AppInteractor {

  /** Has the user consented */
  @CheckResult public suspend fun isPolicyAccepted(): Boolean

  /** User has given consent */
  public suspend fun acceptPolicy()

  /** User has given consent */
  public suspend fun rejectPolicy()
}