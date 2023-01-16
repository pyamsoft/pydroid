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

package com.pyamsoft.pydroid.ui.arch

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.saveable.LocalSaveableStateRegistry

/** Connect a [ComposableViewModeler] to the local saved state registry for save/restore hooks */
@Composable
public fun SaveStateDisposableEffect(viewModeler: ComposableViewModeler) {
  // Attach to save state registry
  val registry = LocalSaveableStateRegistry.current
  if (registry != null) {
    DisposableEffect(registry) {
      viewModeler.consumeRestoredState(registry)

      val entries = viewModeler.registerSaveState(registry)
      onDispose { entries.forEach { it.unregister() } }
    }
  }
}
