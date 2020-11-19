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

package com.pyamsoft.pydroid.ui.changelog

import androidx.annotation.CheckResult

class ChangeLogBuilder {

    private val builder = mutableListOf<String>()

    @CheckResult
    fun bugfix(line: String): ChangeLogBuilder {
        return this.also { builder.add("BUGFIX: $line") }
    }

    @CheckResult
    fun change(line: String): ChangeLogBuilder {
        return this.also { builder.add("CHANGE: $line") }
    }

    @CheckResult
    fun feature(line: String): ChangeLogBuilder {
        return this.also { builder.add("FEATURE: $line") }
    }

    @CheckResult
    fun build(): List<String> {
        return builder
    }
}

@CheckResult
inline fun buildChangeLog(crossinline func: ChangeLogBuilder.() -> Unit): ChangeLogBuilder {
    return ChangeLogBuilder().apply(func)
}
