/*
 * Copyright 2023 pyamsoft
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

package com.pyamsoft.pydroid.ui.inject

import androidx.fragment.app.FragmentActivity
import com.pyamsoft.pydroid.core.Logger

/** Base class implementing a simple DI lifecycle */
public abstract class ComposableInjector {

  private var isInjected = false

  /** Inject DI objects from the graph to this object */
  internal fun inject(activity: FragmentActivity) {
    if (isInjected) {
      Logger.w("$this is already injected")
      return
    }

    isInjected = true
    onInject(activity)
  }

  /** Dispose of injected objects from the graph */
  internal fun dispose() {
    if (!isInjected) {
      Logger.w("$this is not injected yet")
      return
    }

    isInjected = false
    onDispose()
  }

  /** Called to inject from the DI graph */
  protected abstract fun onInject(activity: FragmentActivity)

  /** Called to dispose of injected data */
  protected abstract fun onDispose()
}
