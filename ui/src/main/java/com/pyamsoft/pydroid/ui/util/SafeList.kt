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
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember

/** A list that is safe for Compose */
@Stable
@Immutable
public data class SafeList<T : Any>(
    val list: List<T>,
)

/** Create a safe list */
@CheckResult
public fun <T : Any> List<T>.safe(): SafeList<T> {
  return SafeList(this)
}

/** Remember a list safely */
@Composable
@CheckResult
public fun <T : Any> List<T>.rememberSafe(): SafeList<T> {
  return remember(this) { this.safe() }
}
