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

package com.pyamsoft.pydroid.ui.internal.changelog

import androidx.annotation.CheckResult
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

@Stable
@Immutable
internal data class ChangeLogLine
internal constructor(
    val type: Type,
    val line: String,
) {

  @Stable
  @Immutable
  internal enum class Type {
    BUGFIX,
    CHANGE,
    FEATURE
  }
}

@CheckResult
internal fun String.asBugfix(): ChangeLogLine {
  return ChangeLogLine(ChangeLogLine.Type.BUGFIX, this)
}

@CheckResult
internal fun String.asFeature(): ChangeLogLine {
  return ChangeLogLine(ChangeLogLine.Type.FEATURE, this)
}

@CheckResult
internal fun String.asChange(): ChangeLogLine {
  return ChangeLogLine(ChangeLogLine.Type.CHANGE, this)
}
