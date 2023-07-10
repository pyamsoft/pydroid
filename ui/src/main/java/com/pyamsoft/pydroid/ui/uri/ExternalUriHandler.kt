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

package com.pyamsoft.pydroid.ui.uri

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.UriHandler
import com.pyamsoft.pydroid.ui.internal.uri.PYDroidExternalUriPortal
import com.pyamsoft.pydroid.ui.internal.util.rememberPYDroidDelegate

/** A UriHandler that provides additional confirmation UI when launching external Uri views */
public interface ExternalUriHandler : UriHandler

@Composable
public fun ExternalUriPortal(
    modifier: Modifier = Modifier,
    appName: String,
) {
  val delegate = rememberPYDroidDelegate()
  val uriHandler = remember(delegate) { delegate.externalUriHandler() }

  PYDroidExternalUriPortal(
      modifier = modifier,
      appName = appName,
      uriHandler = uriHandler,
  )
}
