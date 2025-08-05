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

package com.pyamsoft.pydroid.ui.internal.debug

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.pyamsoft.pydroid.arch.SaveStateDisposableEffect
import com.pyamsoft.pydroid.bootstrap.version.fake.FakeUpgradeRequest
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.app.rememberDialogProperties
import com.pyamsoft.pydroid.ui.defaults.TypographyDefaults
import com.pyamsoft.pydroid.ui.inject.rememberComposableInjector
import com.pyamsoft.pydroid.ui.internal.app.DialogToolbar
import com.pyamsoft.pydroid.ui.internal.debug.InAppDebugLogLine.Level.DEBUG
import com.pyamsoft.pydroid.ui.internal.debug.InAppDebugLogLine.Level.ERROR
import com.pyamsoft.pydroid.ui.internal.debug.InAppDebugLogLine.Level.WARNING
import com.pyamsoft.pydroid.ui.util.collectAsStateListWithLifecycle
import com.pyamsoft.pydroid.ui.util.rememberNotNull
import java.time.Instant
import kotlinx.coroutines.flow.MutableStateFlow

private const val FAKE_UPGRADE_NONE_DISPLAY_NAME = "NONE"

private enum class InAppDebugContentTypes {
  LINE,
  PLACEHOLDER,
  TITLE,
  SPACER,
  DISABLED,
  DEBUG_OPTION,
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

  // Use the LifecycleOwner.CoroutineScope (Activity usually)
  // so that the scope does not die because of navigation events
  val owner = LocalLifecycleOwner.current
  val lifecycleScope = owner.lifecycleScope

  MountHooks(
      viewModel = viewModel,
  )

  InAppDebugScreen(
      modifier = modifier,
      state = viewModel,
      extraContent = extraContent,
      onDismiss = onDismiss,
      onCopyLogs = { viewModel.handleCopy(scope = lifecycleScope) },
      onToggleShowChangelog = { viewModel.handleToggleShowChangelog() },
      onToggleShowRatingUpsell = { viewModel.handleToggleShowRatingUpsell() },
      onToggleShowBillingUpsell = { viewModel.handleToggleShowBillingUpsell() },
      onUpdateVersionRequest = { viewModel.handleUpdateVersionRequest(it) },
  )
}

@Composable
private fun InAppDebugScreen(
    modifier: Modifier = Modifier,
    state: DebugViewState,
    onDismiss: () -> Unit,
    onCopyLogs: () -> Unit,
    onUpdateVersionRequest: (FakeUpgradeRequest?) -> Unit,
    onToggleShowChangelog: () -> Unit,
    onToggleShowBillingUpsell: () -> Unit,
    onToggleShowRatingUpsell: () -> Unit,
    extraContent: LazyListScope.() -> Unit = {},
) {
  val isEnabled by state.isInAppDebuggingEnabled.collectAsStateWithLifecycle()
  val lines = state.inAppDebuggingLogLines.collectAsStateListWithLifecycle()

  val snackbarHostState = remember { SnackbarHostState() }
  val (copied, setCopied) = remember { mutableStateOf(false) }

  val handleCopied by rememberUpdatedState {
    onCopyLogs()
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
          title = R.string.debug_title,
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
            modifier = Modifier.padding(MaterialTheme.keylines.content),
            contentAlignment = Alignment.BottomCenter,
        ) {
          LazyColumn {
            if (isEnabled) {

              // Debug options
              item(
                  contentType = InAppDebugContentTypes.DEBUG_OPTION,
              ) {
                val option by state.debugFakeVersionUpdate.collectAsStateWithLifecycle()
                val allValues: Map<FakeUpgradeRequest?, String> = remember {
                  val validValues = FakeUpgradeRequest.entries
                  return@remember mapOf(
                      null to FAKE_UPGRADE_NONE_DISPLAY_NAME,
                      *validValues.map { it to it.name }.toTypedArray(),
                  )
                }

                val displayValue =
                    remember(option, allValues) {
                      allValues[option] ?: FAKE_UPGRADE_NONE_DISPLAY_NAME
                    }

                DebugSelect(
                    modifier = Modifier.padding(top = MaterialTheme.keylines.content),
                    title = "Show Version Update",
                    description = "Fake visual state as if a version upgrade was available",
                    value = option,
                    displayValue = displayValue,
                    allValues = allValues,
                    onSelect = { onUpdateVersionRequest(it) },
                )
              }

              item(
                  contentType = InAppDebugContentTypes.DEBUG_OPTION,
              ) {
                val isChecked by state.isDebugFakeShowChangelog.collectAsStateWithLifecycle()
                DebugOption(
                    modifier = Modifier.padding(top = MaterialTheme.keylines.content),
                    title = "Show Changelog",
                    description = "Fake visual state as if a changelog was available",
                    isChecked = isChecked,
                    onCheckedChange = { onToggleShowChangelog() },
                )
              }

              item(
                  contentType = InAppDebugContentTypes.DEBUG_OPTION,
              ) {
                val isChecked by state.isDebugFakeShowBillingUpsell.collectAsStateWithLifecycle()
                DebugOption(
                    title = "Show Billing Upsell",
                    description = "Show the upsell for the tip jar",
                    isChecked = isChecked,
                    onCheckedChange = { onToggleShowBillingUpsell() },
                )
              }

              item(
                  contentType = InAppDebugContentTypes.DEBUG_OPTION,
              ) {
                val isChecked by state.isDebugFakeShowRatingUpsell.collectAsStateWithLifecycle()
                DebugOption(
                    modifier = Modifier.padding(bottom = MaterialTheme.keylines.content),
                    title = "Show Rating Upsell",
                    description = "Show the upsell to rate the app",
                    isChecked = isChecked,
                    onCheckedChange = { onToggleShowRatingUpsell() },
                )
              }

              item(
                  contentType = InAppDebugContentTypes.TITLE,
              ) {
                Text(
                    text = stringResource(R.string.logs),
                    style =
                        MaterialTheme.typography.labelMedium.copy(
                            color =
                                MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = TypographyDefaults.ALPHA_DISABLED,
                                ),
                        ),
                )
              }

              extraContent()

              if (sortedLines.isEmpty()) {
                item(
                    contentType = InAppDebugContentTypes.PLACEHOLDER,
                ) {
                  Text(
                      modifier =
                          Modifier.padding(
                              horizontal = MaterialTheme.keylines.content,
                              vertical = MaterialTheme.keylines.content * 2,
                          ),
                      text = stringResource(R.string.empty_logs),
                      style =
                          MaterialTheme.typography.bodyMedium.copy(
                              fontFamily = FontFamily.Monospace,
                              color =
                                  MaterialTheme.colorScheme.onSurface.copy(
                                      alpha = TypographyDefaults.ALPHA_DISABLED,
                                  ),
                          ),
                  )
                }
              } else {
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
                          MaterialTheme.typography.bodyMedium.copy(
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
              }

              item(
                  contentType = InAppDebugContentTypes.SPACER,
              ) {
                // Padding to offset so the copy button doesn't cover
                Spacer(
                    modifier = Modifier.height(MaterialTheme.keylines.content * 3),
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
                      text = stringResource(R.string.not_enabled),
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
              enabled = isEnabled,
              onCopy = handleCopied,
              onSnackbarDismissed = { setCopied(false) },
          )
        }
      }
    }
  }
}

@Composable
private fun DebugOption(
    modifier: Modifier = Modifier,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    title: String,
    description: String,
) {
  Row(
      modifier = modifier,
      verticalAlignment = Alignment.CenterVertically,
  ) {
    Checkbox(
        modifier = Modifier.padding(end = MaterialTheme.keylines.baseline),
        checked = isChecked,
        onCheckedChange = onCheckedChange,
    )
    Column(
        modifier = Modifier.weight(1F),
    ) {
      Text(
          text = title,
          style = MaterialTheme.typography.bodyMedium,
      )
      Text(
          text = description,
          style =
              MaterialTheme.typography.bodySmall.copy(
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
              ),
      )
    }
  }
}

@Composable
private fun <T : Any> DebugSelect(
    modifier: Modifier = Modifier,
    value: T?,
    allValues: Map<T?, String>,
    displayValue: String,
    title: String,
    description: String,
    onSelect: (T?) -> Unit,
) {
  val (isOpen, setOpen) = remember { mutableStateOf(false) }

  Row(
      modifier = modifier,
      verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
        modifier =
            Modifier.padding(end = MaterialTheme.keylines.baseline).clickable { setOpen(!isOpen) },
        text = displayValue,
        style = MaterialTheme.typography.bodyLarge,
    )

    DropdownMenu(
        expanded = isOpen,
        onDismissRequest = { setOpen(false) },
        properties = remember { PopupProperties() },
    ) {
      for (entry in allValues) {
        DropdownMenuItem(
            text = {
              Text(
                  text = entry.value,
              )
            },
            onClick = { onSelect(entry.key) },
        )
      }
    }
    Column(
        modifier = Modifier.weight(1F),
    ) {
      Text(
          text = title,
          style = MaterialTheme.typography.bodyMedium,
      )
      Text(
          text = description,
          style =
              MaterialTheme.typography.bodySmall.copy(
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
              ),
      )
    }
  }
}

@Composable
private fun LogLinesCopied(
    modifier: Modifier = Modifier,
    snackBarModifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    enabled: Boolean,
    show: Boolean,
    onCopy: () -> Unit,
    onSnackbarDismissed: () -> Unit,
) {
  if (enabled) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
      Spacer(
          modifier = Modifier.weight(1F),
      )

      Button(
          onClick = { onCopy() },
      ) {
        Text(
            text = stringResource(R.string.copy_logs),
        )
      }
    }
  }

  SnackbarHost(
      modifier = snackBarModifier,
      hostState = snackbarHostState,
  )

  if (show) {
    val copiedMessage = stringResource(R.string.log_copied)

    LaunchedEffect(copiedMessage) {
      snackbarHostState.showSnackbar(
          message = copiedMessage,
          duration = SnackbarDuration.Short,
      )

      // We ignore the showSnackbar result because we don't care (no actions)
      onSnackbarDismissed()
    }
  }
}

@Preview
@Composable
private fun PreviewInAppDebugScreenDisabled() {
  InAppDebugScreen(
      state =
          MutableDebugViewState(
              logLinesBus = MutableStateFlow(emptyList()),
          ),
      onDismiss = {},
      onCopyLogs = {},
      onToggleShowChangelog = {},
      onToggleShowRatingUpsell = {},
      onToggleShowBillingUpsell = {},
      onUpdateVersionRequest = {},
  )
}

@Preview
@Composable
private fun PreviewInAppDebugScreenEnabledEmptyLog() {
  val bus = MutableStateFlow<List<InAppDebugLogLine>>(emptyList())
  InAppDebugScreen(
      state =
          MutableDebugViewState(
                  logLinesBus = bus,
              )
              .apply { isInAppDebuggingEnabled.value = true },
      onDismiss = {},
      onCopyLogs = {},
      onToggleShowChangelog = {},
      onToggleShowRatingUpsell = {},
      onToggleShowBillingUpsell = {},
      onUpdateVersionRequest = {},
  )
}

@Preview
@Composable
private fun PreviewInAppDebugScreenEnabledDummyLog() {
  val bus =
      MutableStateFlow(
          listOf(
              InAppDebugLogLine(
                  DEBUG, "Hello Debug", null, Instant.now().minusSeconds(10).toEpochMilli()),
              InAppDebugLogLine(
                  WARNING, "Hello Warning", null, Instant.now().minusSeconds(9).toEpochMilli()),
              InAppDebugLogLine(
                  ERROR,
                  "Hello Error",
                  IllegalStateException("Hello Error"),
                  Instant.now().minusSeconds(8).toEpochMilli())))
  InAppDebugScreen(
      state =
          MutableDebugViewState(
                  logLinesBus = bus,
              )
              .apply { isInAppDebuggingEnabled.value = true },
      onDismiss = {},
      onCopyLogs = {},
      onToggleShowChangelog = {},
      onToggleShowRatingUpsell = {},
      onToggleShowBillingUpsell = {},
      onUpdateVersionRequest = {},
  )
}
