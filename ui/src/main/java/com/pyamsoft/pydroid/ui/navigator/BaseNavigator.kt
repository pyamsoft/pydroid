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
import androidx.compose.runtime.mutableStateOf

/** A base class navigator, not backed by any specific system */
public abstract class BaseNavigator<S : Any> : Navigator<S> {

  private val screen = mutableStateOf<S?>(null)

  /** Updates the backing field which tracks the current screen */
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

  final override fun restore(
      savedInstanceState: Bundle?,
      onLoadDefaultScreen: () -> Navigator.Screen<S>
  ) {
    if (savedInstanceState != null) {
      val key = savedInstanceState.getString(KEY_SCREEN_ID, null)
      if (key != null) {
        val screen = toScreenFromKey(key)
        updateCurrentScreen(screen)
      }
    }

    onRestore(savedInstanceState, onLoadDefaultScreen)
  }

  final override fun saveState(outState: Bundle) {
    val c = currentScreen()
    if (c != null) {
      outState.putString(KEY_SCREEN_ID, fromScreenToKey(c))
    }

    onSaveState(outState)
  }

  /** Restore Screen data from a string key */
  @CheckResult protected abstract fun toScreenFromKey(key: String): S

  /** Parse Screen data into a string key */
  @CheckResult protected abstract fun fromScreenToKey(screen: S): String

  /** Called after screen state has been restored */
  protected abstract fun onRestore(
      savedInstanceState: Bundle?,
      onLoadDefaultScreen: () -> Navigator.Screen<S>,
  )

  /** Called after screen state has been saved */
  protected abstract fun onSaveState(outState: Bundle)

  public companion object {

    /** Key for Screen saving */
    private const val KEY_SCREEN_ID = "key_navigator_screen_id"
  }
}
