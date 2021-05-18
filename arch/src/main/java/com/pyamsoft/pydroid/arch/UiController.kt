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

/** The UiController drives the events. It orchestrates events between the UiView and UiViewModel */
public interface UiController<C : UiControllerEvent> {

  /** Respond to a published UiControllerEvent */
  public fun onControllerEvent(event: C)
}

/**
 * Create a new anonymous UiController
 *
 * For when a single Android component serves as multiple UiControllers
 */
@CheckResult
public inline fun <C : UiControllerEvent> newUiController(
    crossinline onEvent: (C) -> Unit
): UiController<C> {
  return object : UiController<C> {
    override fun onControllerEvent(event: C) {
      return onEvent(event)
    }
  }
}

private object UiControllers {

  /** An empty Ui Controller singleton */
  val EMPTY = newUiController<UnitControllerEvent> {}
}

/** Returns the no-op Empty Controller */
@CheckResult
public fun emptyController(): UiController<UnitControllerEvent> {
  return UiControllers.EMPTY
}
