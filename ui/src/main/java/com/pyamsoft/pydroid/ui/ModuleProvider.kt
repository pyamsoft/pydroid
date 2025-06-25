/*
 * Copyright 2025 pyamsoft
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

package com.pyamsoft.pydroid.ui

import androidx.annotation.CheckResult
import coil3.ImageLoader
import com.pyamsoft.pydroid.core.ThreadEnforcer
import com.pyamsoft.pydroid.ui.debug.InAppDebugStatus
import com.pyamsoft.pydroid.ui.theme.Theming

/** Provide constructed objects from PYDroid to outside consumers */
public interface ModuleProvider {

  /** Provide modules */
  @CheckResult public fun get(): Modules

  /** Modules from PYDroid */
  public interface Modules {

    /** System dark-light theming interface */
    @CheckResult public fun imageLoader(): ImageLoader

    /** System dark-light theming interface */
    @CheckResult public fun theming(): Theming

    /** Thread Enforcer */
    @CheckResult public fun enforcer(): ThreadEnforcer

    /** In-App Debug Status */
    @CheckResult public fun inAppDebugStatus(): InAppDebugStatus
  }
}
