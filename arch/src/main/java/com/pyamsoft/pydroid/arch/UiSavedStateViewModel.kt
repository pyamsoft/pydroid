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

package com.pyamsoft.pydroid.arch

import androidx.annotation.CheckResult
import androidx.annotation.UiThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * A default implementation of a UiStateViewModel which knows how to set up along with UiViews and a
 * UiController to become a full UiComponent
 *
 * Knows how to save and restore state from an androidx.SavedStateHandle
 */
public abstract class UiSavedStateViewModel<S : UiViewState, C : UiControllerEvent>
protected constructor(savedState: UiSavedState, initialState: S) : UiViewModel<S, C>(initialState) {

  @PublishedApi internal var savedState: UiSavedState? = savedState

  init {
    doOnCleared {
      // Clear out ref to handle
      this.savedState = null
    }
  }

  /**
   * Use this to restore data from a SavedStateHandle
   *
   * This is generally used at a variable declaration site
   *
   * private val userId = restoreSavedState("user_id") { 0 }
   */
  @UiThread
  @CheckResult
  protected suspend inline fun <T : Any> restoreSavedState(
      key: String,
      crossinline defaultValue: suspend () -> T
  ): T =
      withContext(context = Dispatchers.Main) {
        return@withContext requireNotNull(savedState).get(key) ?: defaultValue()
      }

  /**
   * Use this to save data to a SavedStateHandle
   *
   * fun doThing() { val result = doStuff() putSavedState("stuff", result) }
   */
  @UiThread
  protected suspend fun <T : Any> putSavedState(key: String, value: T): Unit =
      withContext(context = Dispatchers.Main) { requireNotNull(savedState).put(key, value) }

  /** Use this to remove data from a SavedStateHandle */
  @UiThread
  protected suspend fun <T : Any> removeSavedState(key: String): T? =
      withContext(context = Dispatchers.Main) {
        return@withContext requireNotNull(savedState).remove(key)
      }
}
