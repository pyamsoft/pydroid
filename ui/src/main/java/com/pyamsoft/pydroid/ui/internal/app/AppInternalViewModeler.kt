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

import com.pyamsoft.pydroid.arch.AbstractViewModeler
import com.pyamsoft.pydroid.bootstrap.changelog.ChangeLogInteractor
import com.pyamsoft.pydroid.bootstrap.datapolicy.DataPolicyInteractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class AppInternalViewModeler
internal constructor(
    state: MutableAppInternalViewState,
    private val disableDataPolicy: Boolean,
    private val disableChangeLog: Boolean,
    private val dataPolicyInteractor: DataPolicyInteractor,
    private val changeLogInteractor: ChangeLogInteractor,
) : AbstractViewModeler<AppInternalViewState>(state) {

  /**
   * Decides the correct dialog to show so we don't spam dialogs
   *
   * This does not guarantee that a dialog will be shown, but simply notifies a given dialog that it
   * should be shown if possible
   */
  internal fun handleShowCorrectDialog(
      scope: CoroutineScope,
      onShowDataPolicy: () -> Unit,
      onShowChangeLog: () -> Unit,
      onShowVersionCheck: () -> Unit,
  ) {
    scope.launch(context = Dispatchers.Main) {
      if (disableDataPolicy && disableChangeLog) {
        // If data policy and changelog are disabled, show Version
        onShowVersionCheck()
      } else {
        // If data policy is disabled show changelog
        if (disableDataPolicy) {
          onShowChangeLog()
        } else {
          // If data policy is enabled, show it if you can
          if (!dataPolicyInteractor.isPolicyAccepted()) {
            onShowDataPolicy()
          } else {
            // If changelog is disabled, show version check
            if (disableChangeLog) {
              onShowVersionCheck()
            } else {
              // If changelog is enabled, show it if you can
              if (changeLogInteractor.canShowChangeLog()) {
                onShowChangeLog()
              } else {
                // Else fallback to update check
                onShowVersionCheck()
              }
            }
          }
        }
      }
    }
  }
}
