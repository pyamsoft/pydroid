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

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.annotation.CheckResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.fragment.app.FragmentActivity
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.ui.internal.pydroid.PYDroidActivityInstallTracker
import com.pyamsoft.pydroid.ui.version.VersionUpgradeAvailable

@CheckResult
private fun resolveActivity(context: Context): FragmentActivity {
  return when (context) {
    is Activity -> context as? FragmentActivity
    is ContextWrapper -> resolveActivity(context.baseContext)
    else -> {
      Logger.w("Provided Context is not an Activity or a ContextWrapper: $context")
      null
    }
  }
      ?: throw IllegalStateException("Could not resolve FragmentActivity from Context: $context")
}

@Composable
@CheckResult
private fun rememberVersionUpgrader(context: Context): VersionUpgradeAvailable {
  return remember(context) {
    val act = resolveActivity(context)
    return@remember PYDroidActivityInstallTracker.retrieve(act).versionUpgrader()
  }
}

/**
 * Shows the default UI for when a new version is available
 *
 * Must be hosted in a PYDroidActivity
 */
@Composable
public fun NewVersionWidget(
    modifier: Modifier = Modifier,
) {
  val context = LocalContext.current
  val versionUpgrader = rememberVersionUpgrader(context)

  // If isEditMode, we don't render nothing
  if (LocalInspectionMode.current) {
    return
  }

  versionUpgrader.RenderVersionCheckWidget(
      modifier = modifier,
  )
}
