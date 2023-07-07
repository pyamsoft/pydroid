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

package com.pyamsoft.pydroid.ui.inject

import androidx.annotation.CheckResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.theme.LocalActivity

/** Gets the provided ComposableInjector in the current Composable scope and holds it around */
@Composable
@CheckResult
public fun <I : ComposableInjector> rememberComposableInjector(create: () -> I): I {
  val activity =
      LocalActivity.current.requireNotNull {
        "ComposableInjector expects a LocalActivity to be provided."
      }
  val handleCreate by rememberUpdatedState(create)

  // Don't use any keys because we want this evaluated only once and then never again
  // for any reason.
  //
  // Make sure your DI graph is resolved before calling create()
  //
  // We only pass the Activity to ensure an Activity change causes a recomposition to avoid mem leak
  val injector =
      remember(activity) {
        handleCreate().apply {
          // We inject immediately because a composable may expect the Injector data to be present
          inject(activity)
        }
      }

  // Set up to dispose when we leave scope
  DisposableEffect(injector) { onDispose { injector.dispose() } }

  // Return our injector
  return injector
}
