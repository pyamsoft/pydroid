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

/**
 * A ViewModel
 *
 * Why not AAC ViewModel?
 *
 * AAC ViewModel solves a problem of preserving state across Activity re-creation, but cannot handle
 * instance state or persistence after process death. To solve these shortcomings, SavedStateHandle
 * was introduced.
 *
 * The problem with AAC ViewModel is the amount of ceremony around creating one and keeping the same
 * one shared across components. Component scoping is a solved problem with Dagger and Hilt DI
 * libraries, but these cannot be easily used with an AAC ViewModel Factory.
 *
 * With the change to Jetpack Compose though, a new recommendation comes from the compose team, that
 * Activities should handle config changes themselves
 *
 * <activity ...
 * android:configChanges="orientation|keyboardHidden|keyboard|screenSize|smallestScreenSize|locale|layoutDirection|fontScale|screenLayout|density|uiMode"
 * > ... </activity>
 *
 * This is because of Composes composition nature, the context and resources are re-evaluated
 * correctly on each pass, which was originally the one downside of handling config changes yourself
 * - you would need to re-access and recreate the resources system for your new context.
 *
 * Process death is handled by the [StateSaver] and will write to the Bundle IF you call it in
 * onSaveInstanceState
 *
 * This ViewModeler class is a proper VM in the MVVM architecture as it owns and manages a state
 * object which is then passed to the view for drawing.
 */
public interface ViewModeler<S : UiViewState> : StateSaver, StateRestorer {

  /** Render this state with arbitrary content */
  @Composable
  @Deprecated("Use state() instead")
  public fun Render(content: @Composable (state: S) -> Unit)

  /** Get the current state */
  @Composable @CheckResult public fun state(): S

  /** Save state to a bundle */
  @Deprecated("Start migrating over to registerSaveState")
  override fun saveState(outState: Bundle) {
    saveState(outState = outState.toWriter())
  }

  /** Restore state from a bundle */
  @Deprecated("Start migrating over to consumeRestoredState")
  override fun restoreState(savedInstanceState: Bundle?) {
    restoreState(savedInstanceState = savedInstanceState.toReader())
  }
}
