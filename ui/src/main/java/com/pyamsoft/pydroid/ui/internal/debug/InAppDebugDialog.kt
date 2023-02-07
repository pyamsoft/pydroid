/*
 * Copyright 2023 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.ui.internal.debug

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.window.Dialog
import com.pyamsoft.pydroid.arch.SaveStateDisposableEffect
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.theme.warning
import com.pyamsoft.pydroid.ui.app.rememberDialogProperties
import com.pyamsoft.pydroid.ui.defaults.DialogDefaults
import com.pyamsoft.pydroid.ui.inject.rememberComposableInjector
import com.pyamsoft.pydroid.ui.internal.app.DialogToolbar
import com.pyamsoft.pydroid.ui.internal.debug.LogLine.Level.DEBUG
import com.pyamsoft.pydroid.ui.internal.debug.LogLine.Level.ERROR
import com.pyamsoft.pydroid.ui.internal.debug.LogLine.Level.WARNING
import com.pyamsoft.pydroid.ui.util.collectAsStateList
import com.pyamsoft.pydroid.ui.util.rememberNotNull

@Composable
internal fun InAppDebugDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
) {
  val component = rememberComposableInjector { DebugInjector() }
  val viewModel = rememberNotNull(component.viewModel)
  SaveStateDisposableEffect(viewModel)

  InAppDebugScreen(
      modifier = modifier,
      state = viewModel.state,
      onDismiss = onDismiss,
  )
}

@Composable
private fun InAppDebugScreen(
    modifier: Modifier = Modifier,
    state: DebugViewState,
    onDismiss: () -> Unit,
) {
  val isEnabled by state.isInAppDebuggingEnabled.collectAsState()
  val lines = state.inAppDebuggingLogLines.collectAsStateList()

  Dialog(
      properties = rememberDialogProperties(),
      onDismissRequest = onDismiss,
  ) {
    Column(
        modifier = modifier,
    ) {
      DialogToolbar(
          modifier = Modifier.fillMaxWidth(),
          title = "Debug Logging",
          onClose = onDismiss,
      )
      Surface(
          modifier = Modifier.fillMaxWidth(),
          elevation = DialogDefaults.Elevation,
          shape =
              MaterialTheme.shapes.medium.copy(
                  topEnd = ZeroCornerSize,
                  topStart = ZeroCornerSize,
              ),
      ) {
        LazyColumn(
            modifier =
                Modifier.padding(
                    MaterialTheme.keylines.content,
                ),
        ) {
          if (isEnabled) {
            item {
              Text(
                  modifier = Modifier.fillMaxWidth(),
                  text = "In-App Debugging is not enabled.",
                  style = MaterialTheme.typography.h5,
              )
            }
          } else {
            items(
                items = lines,
                key = { it.id },
            ) { line ->
              Text(
                  modifier = Modifier.fillMaxWidth(),
                  text = line.line,
                  style =
                      MaterialTheme.typography.body2.copy(
                          fontFamily = FontFamily.Monospace,
                          color =
                              when (line.level) {
                                DEBUG -> MaterialTheme.colors.onSurface
                                WARNING -> MaterialTheme.colors.warning
                                ERROR -> MaterialTheme.colors.error
                              },
                      ),
              )
            }
          }
        }
      }
    }
  }
}
