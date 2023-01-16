/*
 * Copyright 2023 Peter Kenji Yamanaka
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

import androidx.annotation.CheckResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import com.pyamsoft.pydroid.core.requireNotNull

/** Assume not null and remember the result */
@Composable
@CheckResult
public fun <T : Any> rememberNotNull(anything: T?): T {
  return remember(anything) { anything.requireNotNull() }
}

/** Assume not null and remember the result, with custom message */
@Composable
@CheckResult
public fun <T : Any> rememberNotNull(anything: T?, lazyMessage: () -> String): T {
  val handleLazyMessage = rememberUpdatedState(lazyMessage)
  return remember(
      anything,
      handleLazyMessage,
  ) {
    anything.requireNotNull(handleLazyMessage.value)
  }
}
