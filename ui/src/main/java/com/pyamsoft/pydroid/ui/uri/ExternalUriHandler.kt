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

package com.pyamsoft.pydroid.ui.uri

import androidx.annotation.CheckResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import com.pyamsoft.pydroid.core.cast
import com.pyamsoft.pydroid.ui.internal.uri.DefaultExternalUriHandler
import com.pyamsoft.pydroid.ui.internal.uri.PYDroidExternalUriHandler
import com.pyamsoft.pydroid.ui.internal.uri.PYDroidExternalUriPortal

/** A UriHandler that provides additional confirmation UI when launching external Uri views */
public interface ExternalUriHandler : UriHandler

@Composable
public fun ExternalUriPortal(
    modifier: Modifier = Modifier,
    appName: String,
) {
  LocalExternalUriHandler.current?.cast<PYDroidExternalUriHandler>()?.also { handler ->
    PYDroidExternalUriPortal(
        modifier = modifier,
        appName = appName,
        uriHandler = handler,
    )
  }
}

/**
 * Remembers a default external URI handler
 *
 * Can be used with LocalExternalUriHandler provides handler
 */
@Composable
@CheckResult
public fun rememberExternalUriHandler(): ExternalUriHandler {
  val context = LocalContext.current
  return remember(context) { DefaultExternalUriHandler(context) }
}

/**
 * Returns the closest valid UriHandler implementation
 *
 * Prefers "external" but falls back to the default handler if no external exists
 */
@Composable
@CheckResult
public fun rememberUriHandler(): UriHandler {
  val external = LocalExternalUriHandler.current
  val local = LocalUriHandler.current
  return remember(external, local) { external ?: local }
}
