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
import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.pyamsoft.pydroid.arch.UiSavedStateReader
import com.pyamsoft.pydroid.arch.UiSavedStateWriter
import com.pyamsoft.pydroid.arch.toReader
import com.pyamsoft.pydroid.arch.toWriter

/** A base class navigator, not backed by any specific system */
public abstract class BaseNavigator<S : Parcelable> : Navigator<S> {

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
    restore(
        savedInstanceState = savedInstanceState.toReader(),
        onLoadDefaultScreen = onLoadDefaultScreen,
    )
  }

  final override fun saveState(outState: Bundle) {
    saveState(outState = outState.toWriter())
  }

  /** Called to restore screen state */
  protected abstract fun restore(
      savedInstanceState: UiSavedStateReader,
      onLoadDefaultScreen: () -> Navigator.Screen<S>,
  )

  /** Called to save screen state */
  protected abstract fun saveState(outState: UiSavedStateWriter)
}
