/*
 * Copyright 2022 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.ui.widget

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import com.pyamsoft.pydroid.ui.internal.widget.rememberPYDroidDelegate

/**
 * Shows the default UI for when a new version is available
 *
 * Must be hosted in a PYDroidActivity
 */
@Composable
public fun NewVersionWidget(
    modifier: Modifier = Modifier,
) {
  // If isEditMode, we don't render nothing
  if (LocalInspectionMode.current) {
    return
  }

  val context = LocalContext.current
  val delegate = rememberPYDroidDelegate(context)
  val versionUpgrader = remember(delegate) { delegate.versionUpgrader() }

  versionUpgrader.RenderVersionCheckWidget(
      modifier = modifier,
  )
}
