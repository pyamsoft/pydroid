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

package com.pyamsoft.pydroid.ui.util

import android.content.res.Configuration
import androidx.annotation.CheckResult

/** Is this configuration in portrait mode */
@get:CheckResult
public inline val Configuration.isPortrait: Boolean
  get() = orientation == Configuration.ORIENTATION_PORTRAIT

/** Is this configuration in landscape mode */
@get:CheckResult
public inline val Configuration.isLandscape: Boolean
  get() = orientation == Configuration.ORIENTATION_LANDSCAPE
