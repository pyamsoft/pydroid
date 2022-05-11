/*
 * Copyright 2022 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.ui.changelog

import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.ui.internal.changelog.ChangeLogLine
import com.pyamsoft.pydroid.ui.internal.changelog.asBugfix
import com.pyamsoft.pydroid.ui.internal.changelog.asChange
import com.pyamsoft.pydroid.ui.internal.changelog.asFeature

/** Constructs a formatted change log */
public class ChangeLogBuilder {

  private val builder = mutableListOf<ChangeLogLine>()

  /** Adds a line about a bug fix */
  public fun bugfix(line: String): ChangeLogBuilder {
    return this.also { builder.add(line.asBugfix()) }
  }

  /** Adds a line about a behavior change */
  public fun change(line: String): ChangeLogBuilder {
    return this.also { builder.add(line.asChange()) }
  }

  /** Adds a line about a new feature */
  public fun feature(line: String): ChangeLogBuilder {
    return this.also { builder.add(line.asFeature()) }
  }

  @CheckResult
  internal fun build(): List<ChangeLogLine> {
    return builder
  }
}

/** Construct a changelog from a builder DSL */
@CheckResult
public inline fun buildChangeLog(crossinline func: ChangeLogBuilder.() -> Unit): ChangeLogBuilder {
  return ChangeLogBuilder().apply(func)
}
