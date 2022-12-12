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

package com.pyamsoft.pydroid.ui.internal.widget

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.annotation.CheckResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.fragment.app.FragmentActivity
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.ui.internal.pydroid.ObjectGraph
import com.pyamsoft.pydroid.ui.internal.pydroid.PYDroidActivityDelegateInternal

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
internal fun rememberPYDroidDelegate(context: Context): PYDroidActivityDelegateInternal {
  return remember(context) {
    val act = resolveActivity(context)
    return@remember ObjectGraph.ActivityScope.retrieve(act)
  }
}
