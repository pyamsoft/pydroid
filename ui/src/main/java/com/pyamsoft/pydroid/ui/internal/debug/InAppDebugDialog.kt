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

package com.pyamsoft.pydroid.ui.internal.debug

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pyamsoft.pydroid.arch.SaveStateDisposableEffect
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.app.rememberDialogProperties
import com.pyamsoft.pydroid.ui.defaults.TypographyDefaults
import com.pyamsoft.pydroid.ui.inject.rememberComposableInjector
import com.pyamsoft.pydroid.ui.internal.app.DialogToolbar
import com.pyamsoft.pydroid.ui.internal.debug.InAppDebugLogLine.Level.DEBUG
import com.pyamsoft.pydroid.ui.internal.debug.InAppDebugLogLine.Level.ERROR
import com.pyamsoft.pydroid.ui.internal.debug.InAppDebugLogLine.Level.WARNING
import com.pyamsoft.pydroid.ui.util.collectAsStateListWithLifecycle
import com.pyamsoft.pydroid.ui.util.rememberNotNull
import kotlinx.coroutines.flow.MutableStateFlow

private enum class InAppDebugContentTypes {
  LINE,
  TITLE,
  DISABLED,
}

@Composable
private fun MountHooks(
    viewModel: DebugViewModeler,
) {
  SaveStateDisposableEffect(viewModel)

  LaunchedEffect(
      viewModel,
  ) {
    viewModel.bind(scope = this)
  }
}

@Composable
internal fun InAppDebugDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    extraContent: LazyListScope.() -> Unit,
) {
  val component = rememberComposableInjector { DebugInjector() }
  val viewModel = rememberNotNull(component.viewModel)

  val scope = rememberCoroutineScope()

  MountHooks(
      viewModel = viewModel,
  )

  InAppDebugScreen(
      modifier = modifier,
      state = viewModel,
      extraContent = extraContent,
      onDismiss = onDismiss,
      onCopy = { viewModel.handleCopy(scope = scope) },
  )
}

@Composable
private fun InAppDebugScreen(
    modifier: Modifier = Modifier,
    state: DebugViewState,
    onDismiss: () -> Unit,
    onCopy: () -> Unit,
    extraContent: LazyListScope.() -> Unit = {},
) {
  val isEnabled by state.isInAppDebuggingEnabled.collectAsStateWithLifecycle()
  val lines = state.inAppDebuggingLogLines.collectAsStateListWithLifecycle()

  val snackbarHostState = remember { SnackbarHostState() }
  val (copied, setCopied) = remember { mutableStateOf(false) }

  val handleCopied by rememberUpdatedState {
    onCopy()
    setCopied(true)
  }

  val sortedLines = remember(lines) { lines.sortedBy { it.timestamp } }

  Dialog(
      properties = rememberDialogProperties(),
      onDismissRequest = onDismiss,
  ) {
    Column(
        modifier = modifier.padding(MaterialTheme.keylines.content),
    ) {
      DialogToolbar(
          modifier = Modifier.fillMaxWidth(),
          title = "Debug Logging",
          onClose = onDismiss,
      )
      Card(
          modifier = Modifier.fillMaxWidth(),
          elevation = CardDefaults.elevatedCardElevation(),
          colors = CardDefaults.elevatedCardColors(),
          shape =
              MaterialTheme.shapes.medium.copy(
                  topEnd = ZeroCornerSize,
                  topStart = ZeroCornerSize,
              ),
      ) {
        Box(
            modifier =
                Modifier.clickable(enabled = isEnabled) { handleCopied() }
                    .padding(MaterialTheme.keylines.content),
            contentAlignment = Alignment.BottomCenter,
        ) {
          LazyColumn {
            if (isEnabled) {
              extraContent()

              item(
                  contentType = InAppDebugContentTypes.TITLE,
              ) {
                Text(
                    text = "Logs",
                    style =
                        MaterialTheme.typography.bodySmall.copy(
                            color =
                                MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = TypographyDefaults.ALPHA_DISABLED,
                                ),
                        ),
                )
              }
              items(
                  items = sortedLines,
                  key = { it.timestamp.toString() },
                  contentType = { InAppDebugContentTypes.LINE },
              ) { line ->
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text =
                        remember(line) {
                          val errorMessage =
                              if (line.throwable == null) "" else line.throwable.message.orEmpty()
                          return@remember "${line.line} $errorMessage"
                        },
                    style =
                        MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            color =
                                when (line.level) {
                                  DEBUG -> MaterialTheme.colorScheme.onSurface
                                  WARNING -> MaterialTheme.colorScheme.tertiary
                                  ERROR -> MaterialTheme.colorScheme.error
                                },
                        ),
                )
              }
            } else {
              item(
                  contentType = InAppDebugContentTypes.DISABLED,
              ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                  Text(
                      modifier = Modifier.fillMaxWidth().padding(MaterialTheme.keylines.content),
                      text = "In-App Developer Mode is not enabled.",
                      style = MaterialTheme.typography.headlineSmall,
                      textAlign = TextAlign.Center,
                  )
                }
              }
            }
          }

          LogLinesCopied(
              snackbarHostState = snackbarHostState,
              show = copied,
              onSnackbarDismissed = { setCopied(false) },
          )
        }
      }
    }
  }
}

@Composable
private fun LogLinesCopied(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    show: Boolean,
    onSnackbarDismissed: () -> Unit,
) {
  SnackbarHost(
      modifier = modifier,
      hostState = snackbarHostState,
  )

  if (show) {
    LaunchedEffect(Unit) {
      snackbarHostState.showSnackbar(
          message = "Developer Log Copied",
          duration = SnackbarDuration.Short,
      )

      // We ignore the showSnackbar result because we don't care (no actions)
      onSnackbarDismissed()
    }
  }
}

@Preview
@Composable
private fun PreviewInAppDebugScreen() {
  InAppDebugScreen(
      state =
          MutableDebugViewState(
                  logLinesBus = MutableStateFlow(emptyList()),
              )
              .apply {},
      onDismiss = {},
      onCopy = {},
  )
}
