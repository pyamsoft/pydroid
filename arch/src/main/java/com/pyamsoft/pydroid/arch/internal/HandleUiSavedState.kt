/*
 * Copyright 2020 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.arch.internal

import androidx.lifecycle.SavedStateHandle
import com.pyamsoft.pydroid.arch.UiSavedState

/** SavedStateHandle backed implementation of a UiSavedState */
internal class HandleUiSavedState internal constructor(private val handle: SavedStateHandle) :
    UiSavedState {

  override fun <T : Any> put(key: String, value: T) {
    handle.set(key, value)
  }

  override fun <T : Any> remove(key: String): T? {
    return handle.remove(key)
  }

  override fun <T : Any> get(key: String): T? {
    return handle.get<T>(key)
  }
}
