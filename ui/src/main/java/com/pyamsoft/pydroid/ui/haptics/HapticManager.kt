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

package com.pyamsoft.pydroid.ui.haptics

/** An abstraction over haptics on Android for Compose */
public interface HapticManager {

  /** Switch button toggled off */
  public fun toggleOff()

  /** Switch button toggled on */
  public fun toggleOn()

  /**
   * A confirm press
   *
   * A "confirm" press is for buttons that "go" or "start"
   */
  public fun confirmButtonPress()

  /**
   * A cancel press
   *
   * For things like "close" or "dismiss" or "cancel"
   */
  public fun cancelButtonPress()

  /**
   * An action press
   *
   * For generic actions that do not fall into the other button categories
   */
  public fun actionButtonPress()

  /** A clock tick on a time picker UI */
  public fun clockTick()

  /** A long press */
  public fun longPress()
}
