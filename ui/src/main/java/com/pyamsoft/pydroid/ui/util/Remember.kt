/*
 * Copyright 2023 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.ui.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.annotation.CheckResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.core.requireNotNull

/** Assume not null and remember the result */
@Composable
@CheckResult
public fun <T : Any> rememberNotNull(anything: T?): T {
  return remember(anything) { anything.requireNotNull() }
}

/** Assume not null and remember the result, with custom message */
@Composable
@CheckResult
public fun <T : Any> rememberNotNull(anything: T?, lazyMessage: () -> String): T {
  val handleLazyMessage by rememberUpdatedState(lazyMessage)
  return remember(anything) { anything.requireNotNull(handleLazyMessage) }
}

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

/**
 * Resolve the LocalContext as an Activity
 *
 * This should basically always work, given that all ComposeViews are held inside of Activities
 *
 * In the future, if we use Compose in a RemoteView context, this will not work.
 */
@Composable
@CheckResult
public fun rememberActivity(): FragmentActivity {
  val context = LocalContext.current
  return remember(context) { resolveActivity(context) }
}

/**
 * Any list in Compose is unstable and will fire recompositions a ton. Use this helper to turn your
 * data State list into a compose ready list
 */
@Composable
@CheckResult
public fun <T : Any> Collection<T>.rememberAsStateList(): SnapshotStateList<T> {
  return remember(this) { this.toMutableStateList() }
}

/** rememberAsStateList convenience for Maps */
@Composable
@CheckResult
public fun <K : Any, V : Any> Map<K, V>.rememberAsStateList(): SnapshotStateList<Map.Entry<K, V>> {
  return remember(this) { this.entries.toMutableStateList() }
}
