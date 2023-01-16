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

package com.pyamsoft.pydroid.arch

import android.os.Bundle
import androidx.annotation.CheckResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.SaveableStateRegistry

/**
 * A base class ViewModeler which implements a simple Render function and can handle saving state
 */
public abstract class AbstractViewModeler<S : UiViewState>
protected constructor(
    private val state: S,
) : ViewModeler<S> {

  @Composable
  @CheckResult
  final override fun state(): S {
    return state
  }

  @Composable
  @Deprecated("Use state() instead")
  final override fun Render(content: @Composable (state: S) -> Unit) {
    content(state)
  }

  @Deprecated("Start migrating over to registerSaveState")
  final override fun saveState(outState: Bundle) {
    super.saveState(outState)
  }

  @Deprecated("Start migrating over to registerSaveState")
  override fun saveState(outState: UiSavedStateWriter) {}

  @Deprecated("Start migrating over to consumeRestoredState")
  final override fun restoreState(savedInstanceState: Bundle?) {
    super.restoreState(savedInstanceState)
  }

  @Deprecated("Start migrating over to consumeRestoredState")
  override fun restoreState(savedInstanceState: UiSavedStateReader) {}

  override fun consumeRestoredState(registry: SaveableStateRegistry) {}

  override fun registerSaveState(
      registry: SaveableStateRegistry
  ): List<SaveableStateRegistry.Entry> {
    return DEFAULT_EMPTY_REGISTRY_ENTRIES
  }

  public companion object {
    private val DEFAULT_EMPTY_REGISTRY_ENTRIES = emptyList<SaveableStateRegistry.Entry>()
  }
}
