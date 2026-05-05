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

package com.pyamsoft.pydroid.ui.internal.datapolicy.dialog

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.app.rememberDialogProperties
import com.pyamsoft.pydroid.ui.inject.rememberComposableInjector
import com.pyamsoft.pydroid.ui.internal.util.rememberResolvedActivity
import com.pyamsoft.pydroid.ui.util.rememberNotNull

@Composable
private fun MountHooks(
    viewModel: DataPolicyDialogViewModeler,
) {
  LaunchedEffect(
      viewModel,
  ) {
    viewModel.bind(scope = this)
  }
}

@Composable
internal fun DataPolicyDisclosureDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
) {
  val component = rememberComposableInjector { DataPolicyInjector() }

  val viewModel = rememberNotNull(component.viewModel)
  val imageLoader = rememberNotNull(component.imageLoader)

  // Required to force finish
  val activity = rememberResolvedActivity()
  val handleRejected by rememberUpdatedState { activity.finishAndRemoveTask() }

  val handleHideNavigationError by rememberUpdatedState { viewModel.handleHideNavigationError() }

  MountHooks(
      viewModel = viewModel,
  )

  Dialog(
      properties = rememberDialogProperties(),
      onDismissRequest = onDismiss,
  ) {
    DataPolicyDisclosureScreen(
        modifier = modifier.padding(MaterialTheme.keylines.content),
        state = viewModel,
        imageLoader = imageLoader,
        onNavigationErrorDismissed = { handleHideNavigationError() },
        onAccept = {
          viewModel.handleAccept(
              onAccepted = onDismiss,
          )
        },
        onReject = {
          viewModel.handleReject(
              onRejected = { handleRejected() },
          )
        },
    )
  }
}
