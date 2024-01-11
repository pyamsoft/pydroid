/*
 * Copyright 2024 pyamsoft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pydroid.ui.internal.haptics

import android.app.Application
import android.os.Build
import android.view.HapticFeedbackConstants
import android.view.View
import com.pyamsoft.pydroid.ui.haptics.HapticManager
import com.pyamsoft.pydroid.ui.haptics.HapticPreferences
import com.pyamsoft.pydroid.ui.internal.pydroid.ObjectGraph
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class AndroidViewHapticManager
internal constructor(
    private val scope: CoroutineScope,
    private val view: View,
) : HapticManager {

  private var isHapticsEnabled = false
  internal var preferences: HapticPreferences? = null

  init {
    injectAndBeginWatchingPreferences()
  }

  private fun injectAndBeginWatchingPreferences() {
    if (preferences == null) {
      val context = view.context.applicationContext
      if (context is Application) {
        ObjectGraph.ApplicationScope.retrieve(context).injector().inject(this)
      }
    }

    preferences?.listenForHapticsChanges()?.also { f ->
      scope.launch(context = Dispatchers.Default) { f.collect { isHapticsEnabled = it } }
    }
  }

  override fun toggleOff() {
    if (!isHapticsEnabled) {
      return
    }

    if (Build.VERSION.SDK_INT >= 34) {
      view.performHapticFeedback(HapticFeedbackConstants.TOGGLE_OFF)
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
      view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY_RELEASE)
    } else {
      view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
    }
  }

  override fun toggleOn() {
    if (!isHapticsEnabled) {
      return
    }

    if (Build.VERSION.SDK_INT >= 34) {
      view.performHapticFeedback(HapticFeedbackConstants.TOGGLE_ON)
    } else {
      view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
    }
  }

  override fun confirmButtonPress() {
    // For now, we do not differentiate
    actionButtonPress()
  }

  override fun cancelButtonPress() {
    if (!isHapticsEnabled) {
      return
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
      view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY_RELEASE)
    } else {
      view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
    }
  }

  override fun actionButtonPress() {
    if (!isHapticsEnabled) {
      return
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
    } else {
      view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
    }
  }

  override fun clockTick() {
    if (!isHapticsEnabled) {
      return
    }

    view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
  }

  override fun longPress() {
    if (!isHapticsEnabled) {
      return
    }

    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
  }
}
