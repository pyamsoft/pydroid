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

package com.pyamsoft.pydroid.ui.preference

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

/** Wrap a bunch of preference content in an alpha wrapper */
@Composable
internal fun PreferenceAlphaWrapper(
    isEnabled: Boolean = true,
    content: @Composable () -> Unit,
) {
  val scheme = MaterialTheme.colorScheme
  CompositionLocalProvider(
      LocalContentColor provides if (isEnabled) scheme.onSurface else scheme.onSurfaceVariant,
  ) {
    content()
  }
}
