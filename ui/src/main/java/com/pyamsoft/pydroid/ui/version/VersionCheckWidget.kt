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

package com.pyamsoft.pydroid.ui.version

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.annotation.CheckResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.ui.app.PYDroidActivity

@CheckResult
private fun getActivityFromContext(context: Context?): PYDroidActivity {
  if (context != null) {
    if (context is Activity) {
      return (context as? PYDroidActivity).requireNotNull {
        "Could not cast Activity context to PYDroidActivity: $context"
      }
    }

    if (context is ContextWrapper) {
      return getActivityFromContext(context.baseContext)
    }
  }

  throw IllegalStateException("Could not find PYDroidActivity from context resolution loop.")
}

@Composable
@CheckResult
private fun rememberLocalPYDroidActivity(): PYDroidActivity {
  val context = LocalContext.current
  return remember(context) { getActivityFromContext(context) }
}

/** Renders custom UI which handles the state of version check flows */
@Composable
public fun VersionCheckWidget(
    content: @Composable (VersionCheckViewState) -> Unit,
) {
  val pyDroidActivity = rememberLocalPYDroidActivity()
  pyDroidActivity.RenderVersionCheck(content)
}
