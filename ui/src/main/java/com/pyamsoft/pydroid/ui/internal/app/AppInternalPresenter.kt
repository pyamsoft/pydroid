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

package com.pyamsoft.pydroid.ui.internal.app

import com.pyamsoft.pydroid.bootstrap.datapolicy.DataPolicyInteractor
import com.pyamsoft.pydroid.core.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class AppInternalPresenter
internal constructor(
    private val disableDataPolicy: Boolean,
    private val dataPolicyInteractor: DataPolicyInteractor,
) {

  /**
   * Decides the correct dialog to show so we don't spam dialogs
   *
   * This does not guarantee that a dialog will be shown, but simply notifies a given dialog that it
   * should be shown if possible
   */
  internal fun handleShowCorrectDialog(
      scope: CoroutineScope,
      onShowDataPolicy: () -> Unit,
  ) {
    if (disableDataPolicy) {
      Logger.w("Data policy is disabled, do not need to show any dialogs")
      return
    }

    scope.launch(context = Dispatchers.Main) {
      // If data policy is enabled, show it if you can
      if (!dataPolicyInteractor.isPolicyAccepted()) {
        onShowDataPolicy()
      }
    }
  }
}
