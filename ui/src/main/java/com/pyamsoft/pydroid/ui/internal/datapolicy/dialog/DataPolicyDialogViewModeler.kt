/*
 * Copyright 2024 pyamsoft
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

package com.pyamsoft.pydroid.ui.internal.datapolicy.dialog

import com.pyamsoft.pydroid.arch.AbstractViewModeler
import com.pyamsoft.pydroid.bootstrap.datapolicy.DataPolicyInteractor
import com.pyamsoft.pydroid.ui.app.AppProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class DataPolicyDialogViewModeler
internal constructor(
    override val state: MutableDataPolicyDialogViewState,
    private val privacyPolicyUrl: String,
    private val termsConditionsUrl: String,
    private val provider: AppProvider,
    private val interactor: DataPolicyInteractor,
) : DataPolicyDialogViewState by state, AbstractViewModeler<DataPolicyDialogViewState>(state) {

  internal fun bind(scope: CoroutineScope) {
    scope.launch(context = Dispatchers.Default) {
      val displayName = interactor.getDisplayName()
      state.apply {
        name.value = displayName
        icon.value = provider.applicationIcon
      }
    }
  }

  internal fun handleAccept(onAccepted: () -> Unit) {
    interactor.acceptPolicy()
    onAccepted()
  }

  internal fun handleReject(onRejected: () -> Unit) {
    interactor.rejectPolicy()
    onRejected()
  }

  internal fun handleNavigationFailed(throwable: Throwable) {
    state.navigationError.value = throwable
  }

  internal fun handleHideNavigationError() {
    state.navigationError.value = null
  }

  internal fun handleViewTermsOfService(onOpenUrl: (String) -> Unit) {
    onOpenUrl(termsConditionsUrl)
  }

  internal fun handleViewPrivacyPolicy(onOpenUrl: (String) -> Unit) {
    onOpenUrl(privacyPolicyUrl)
  }
}
