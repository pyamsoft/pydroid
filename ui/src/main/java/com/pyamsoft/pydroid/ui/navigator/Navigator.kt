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
 * Applications that will need a backstack will want to extend from [BackstackNavigator]
 */
public interface Navigator<S : Any> {

  /** Get the current screen */
  @CheckResult public fun currentScreen(): S?

  /** Get the current screen as a composable state */
  @Composable @CheckResult public fun currentScreenState(): State<S?>

  /** Restores the state after process recreation */
  public fun restoreState(savedInstanceState: Bundle?)

  /** Saves the state during process recreation */
  public fun saveState(outState: Bundle)

  /**
   * Restores a default screen if needed from an otherwise empty state
   *
   * If your default screen state is "blank", then do not call this function or represent a blank
   * screen [S]
   */
  public fun loadIfEmpty(onLoadDefaultScreen: () -> S)

  /**
   * Navigate to a new screen
   *
   * This may not actually navigate if conditions prevent it, such as, we are already on the screen.
   * but if the [force] parameter is true, this will always navigate. This may cause an existing
   * screen to be replaced with the same screen again
   */
  public fun navigateTo(screen: S, force: Boolean)

  /**
   * Navigate to a new screen
   *
   * This may not actually navigate if conditions prevent it, such as, we are already on the screen.
   */
  public fun navigateTo(screen: S)

  public companion object {

    /** Gets the tag used internally by the Navigator for a given screen instance */
    @JvmStatic
    @CheckResult
    public fun <S : Any> getTagForScreen(screen: S): String {
      return "PYDroid-Navigator-${screen::class.java.name}"
    }
  }
}
