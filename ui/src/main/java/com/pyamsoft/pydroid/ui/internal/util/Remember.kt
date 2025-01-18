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

package com.pyamsoft.pydroid.ui.internal.util

import androidx.activity.ComponentActivity
import androidx.annotation.CheckResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.pyamsoft.pydroid.ui.app.LocalActivity
import com.pyamsoft.pydroid.ui.internal.pydroid.ObjectGraph
import com.pyamsoft.pydroid.ui.internal.pydroid.PYDroidActivityDelegateInternal
import com.pyamsoft.pydroid.ui.util.rememberActivity

/** Grab the PYDroidActivity delegate from the current activity */
@Composable
@CheckResult
internal fun rememberPYDroidDelegate(): PYDroidActivityDelegateInternal {
  val activity = rememberResolvedActivity()
  return remember(activity) { ObjectGraph.ActivityScope.retrieve(activity) }
}

/**
 * Resolve the local Activity from the Composing context
 *
 * Prefers LocalActivity for performance but falls back to the resolving lookup
 */
@Composable
@CheckResult
internal fun rememberResolvedActivity(): ComponentActivity {
  @Suppress("DEPRECATION")
  return LocalActivity.current ?: rememberActivity()
}
