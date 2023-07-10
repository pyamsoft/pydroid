/*
 * Copyright 2023 pyamsoft
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

package com.pyamsoft.pydroid.ui.internal.uri

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.app.rememberDialogProperties
import com.pyamsoft.pydroid.ui.defaults.DialogDefaults
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

  val uri by uriHandler.awaitingConfirmation.collectAsState()

  val show = remember(uri) { uri.isNotBlank() }

  DisposableEffect(uriHandler) { onDispose { uriHandler.dismiss() } }

  if (show) {
    val handleDismiss by rememberUpdatedState {
      hapticManager?.cancelButtonPress()
      uriHandler.dismiss()
    }

    val confirmation =
        remember(
            uri,
            appName,
        ) {
          buildAnnotatedString {
            append("The link you tried to go to is outside $appName:")
            append(" ")

            withStyle(
                style =
                    SpanStyle(
                        fontWeight = FontWeight.W700,
                    ),
            ) {
              append(uri)
            }

            appendLine()
            appendLine()
            append("Are you sure you want to follow this link?")
          }
        }

    Dialog(
        onDismissRequest = { handleDismiss() },
        properties = rememberDialogProperties(),
    ) {
      Surface(
          modifier = modifier.padding(MaterialTheme.keylines.content),
          elevation = DialogDefaults.Elevation,
          shape = MaterialTheme.shapes.medium,
      ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(MaterialTheme.keylines.content),
        ) {
          Text(
              modifier = Modifier.fillMaxWidth().padding(bottom = MaterialTheme.keylines.content),
              style = MaterialTheme.typography.body1,
              text = "You're going to a link outside $appName",
              fontWeight = FontWeight.W700,
          )

          Text(
              modifier = Modifier.fillMaxWidth().padding(bottom = MaterialTheme.keylines.content),
              style = MaterialTheme.typography.body2,
              text = confirmation,
          )

          Text(
              modifier = Modifier.fillMaxWidth().padding(bottom = MaterialTheme.keylines.content),
              style =
                  MaterialTheme.typography.caption.copy(
                      color =
                          MaterialTheme.colors.onSurface.copy(
                              alpha = ContentAlpha.medium,
                          ),
                  ),
              text =
                  "pyamsoft does not control this link and is not responsible for any of the content displayed.",
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
                        contentColor =
                            MaterialTheme.colors.onSurface.copy(
                                alpha = ContentAlpha.medium,
                            ),
                    ),
            ) {
              Text(
                  text = "Cancel",
              )
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
                  text = "Follow Link",
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
