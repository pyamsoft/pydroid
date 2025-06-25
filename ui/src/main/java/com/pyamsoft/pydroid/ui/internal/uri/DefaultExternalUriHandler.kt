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

package com.pyamsoft.pydroid.ui.internal.uri

import android.content.Context
import android.widget.Toast
import androidx.compose.ui.platform.UriHandler
import com.pyamsoft.pydroid.util.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/** Handles external URI confirmation and attempts to navigate */
internal class DefaultExternalUriHandler
internal constructor(
    context: Context,
) : PYDroidExternalUriHandler {

  /** Hold only the application context to avoid leaking context from Toast error fallback */
  private val appContext by lazy { context.applicationContext }

  private val confirmUri = MutableStateFlow("")
  private var toasting: Toast? = null

  override val awaitingConfirmation: StateFlow<String> = confirmUri

  private fun killToast() {
    toasting?.cancel()
    toasting = null
  }

  override fun openUri(uri: String) {
    confirmUri.value = uri
  }

  override fun dismiss() {
    killToast()
    confirmUri.value = ""
  }

  override fun confirm(
      handler: UriHandler,
      uri: String,
  ) {
    killToast()

    val expected = confirmUri.value
    if (uri != expected) {
      Logger.w { "Confirmation received for wrong URI! (Saw: $uri, Expected: $expected)" }
      return
    }

    // Close the dialog that is showing now that we are confirmed
    dismiss()

    try {
      Logger.d { "Confirmed: attempt open external URI: $uri" }
      handler.openUri(uri)
    } catch (e: Throwable) {
      toasting =
          Toast.makeText(
                  appContext,
                  "Unable to open external URL: $uri",
                  Toast.LENGTH_LONG,
              )
              .also { it.show() }
    }
  }
}
