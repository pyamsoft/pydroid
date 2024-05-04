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

package com.pyamsoft.pydroid.ui.internal.uri

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.app.rememberDialogProperties
import com.pyamsoft.pydroid.ui.defaults.TypographyDefaults
import com.pyamsoft.pydroid.ui.haptics.LocalHapticManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/** Default confirmation UI for the external URI navigation confirmation */
@Composable
internal fun PYDroidExternalUriPortal(
    modifier: Modifier = Modifier,
    appName: String,
    uriHandler: PYDroidExternalUriHandler,
) {
  val context = LocalContext.current
  val handler = LocalUriHandler.current
  val hapticManager = LocalHapticManager.current

  val uri by uriHandler.awaitingConfirmation.collectAsStateWithLifecycle()

  val show = remember(uri) { uri.isNotBlank() }

  DisposableEffect(uriHandler) { onDispose { uriHandler.dismiss() } }

  if (show) {
    val handleDismiss by rememberUpdatedState {
      hapticManager?.cancelButtonPress()
      uriHandler.dismiss()
    }

    val confirmation = stringResource(R.string.external_link_url, appName, uri)

    Dialog(
        onDismissRequest = { handleDismiss() },
        properties = rememberDialogProperties(),
    ) {
      Card(
          modifier = modifier.padding(MaterialTheme.keylines.content),
          shape = MaterialTheme.shapes.medium,
          elevation = CardDefaults.elevatedCardElevation(),
          colors = CardDefaults.elevatedCardColors(),
      ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(MaterialTheme.keylines.content),
        ) {
          Text(
              modifier = Modifier.fillMaxWidth().padding(bottom = MaterialTheme.keylines.content),
              style = MaterialTheme.typography.bodyLarge,
              text = stringResource(R.string.external_link_title, appName),
              fontWeight = FontWeight.W700,
          )

          Text(
              modifier = Modifier.fillMaxWidth().padding(bottom = MaterialTheme.keylines.content),
              style = MaterialTheme.typography.bodyMedium,
              text = confirmation,
          )

          Text(
              modifier = Modifier.fillMaxWidth().padding(bottom = MaterialTheme.keylines.content),
              style =
                  MaterialTheme.typography.bodySmall.copy(
                      color =
                          MaterialTheme.colorScheme.onSurfaceVariant.copy(
                              alpha = TypographyDefaults.ALPHA_DISABLED,
                          ),
                  ),
              text = stringResource(R.string.external_link_disclaimer),
          )

          Row(
              modifier = Modifier.fillMaxWidth(),
          ) {
            Spacer(
                modifier = Modifier.weight(1F),
            )

            TextButton(
                modifier = Modifier.padding(end = MaterialTheme.keylines.baseline),
                onClick = { handleDismiss() },
                colors =
                    ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
            ) {
              Text(text = stringResource(android.R.string.cancel))
            }

            Button(
                onClick = {
                  hapticManager?.confirmButtonPress()
                  uriHandler.confirm(
                      context = context,
                      handler = handler,
                      uri = uri,
                  )
                },
            ) {
              Text(
                  text = stringResource(R.string.follow_link),
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun PreviewExternalUriPortal(uri: String) {
  PYDroidExternalUriPortal(
      appName = "TEST",
      uriHandler = TestExternalUriHandler(uri),
  )
}

@Preview
@Composable
private fun PreviewExternalUriPortalNoUri() {
  PreviewExternalUriPortal(
      uri = "",
  )
}

@Preview
@Composable
private fun PreviewExternalUriPortalWithUri() {
  PreviewExternalUriPortal(
      uri = "https://www.example.com",
  )
}

private class TestExternalUriHandler(
    uri: String,
) : PYDroidExternalUriHandler {

  private val state = MutableStateFlow(uri)

  override val awaitingConfirmation: StateFlow<String> = state

  override fun dismiss() {
    state.value = ""
  }

  override fun confirm(context: Context, handler: UriHandler, uri: String) {}

  override fun openUri(uri: String) {
    state.value = uri
  }
}
