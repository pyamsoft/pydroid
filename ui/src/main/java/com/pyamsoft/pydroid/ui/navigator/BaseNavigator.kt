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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf

/** A base class navigator, not backed by any specific system */
public abstract class BaseNavigator<S : Any> : Navigator<S> {

  private val screen = mutableStateOf<S?>(null)

  /**
   * Updates the backing field which tracks the current screen
   *
   * This operation should be called once during your [select] function
   */
  protected fun updateCurrentScreen(newScreen: S) {
    screen.value = newScreen
  }

  final override fun currentScreen(): S? {
    return screen.value
  }

  @Composable
  final override fun currentScreenState(): State<S?> {
    return screen
  }

  final override fun navigateTo(screen: Navigator.Screen<S>) {
    navigateTo(screen, force = false)
  }
}
