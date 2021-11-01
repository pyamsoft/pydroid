/*
 * Copyright 2021 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.ui.navigator

import android.os.Bundle
import androidx.annotation.CheckResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State

/**
 * Handles navigation between screens
 *
 * Can be backed by generally any navigation system
 *
 * Does not care or maintain a backstack in any way.
 *
 * Applications that will need a backstack will first want to extend this interface with their own
 * interface which understands their backstack needs at an interface level, and then inject an
 * implementation which implements that interface, not this base Navigator interface.
 */
public interface Navigator<S : Any> {

  /** Get the current screen */
  @CheckResult public fun currentScreen(): S

  /** Get the current screen as a composable state */
  @Composable @CheckResult public fun currentScreenState(): State<S>

  /** Restores the state after process recreation */
  public fun restore(savedInstanceState: Bundle?)

  /**
   * Select a new screen.
   *
   * If the screen cannot be committed, this action will not perform a navigation
   */
  @CheckResult public fun select(screen: Screen<S>): Boolean

  /** Select a new screen (optionally force commit the selection) */
  @CheckResult
  public fun select(
      screen: Screen<S>,
      force: Boolean,
  ): Boolean

  /**
   * A screen object
   */
  public interface Screen<S : Any> {
    /** The screen */
    public val screen: S

    /** Any arguments to construct the screen */
    public val arguments: Bundle?
  }
}
