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
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember

/**
 * Compose does weird performance things if your classes are not [Stable]
 *
 * This is a wrapper class allowing you to mark any arbitrary object as [Stable] for Compose It is
 * useful if you do not own the data type but need to consume it in Compose
 */
@Stable
public interface Stabilized<T> {

  /** Any arbitrary data */
  @get:CheckResult public val data: T
}

@Stable
private data class StabilizedImpl<T>(
    override val data: T,
) : Stabilized<T>

/** Wrap arbitrary data as Stabilized */
@CheckResult
public fun <T> T.stabilize(): Stabilized<T> {
  return StabilizedImpl(this)
}

/** Wrap and remember arbitrary data as Stabilized */
@Composable
@CheckResult
public fun <T> T.rememberStable(): Stabilized<T> {
  return remember(this) { this.stabilize() }
}
