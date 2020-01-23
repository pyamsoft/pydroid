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
 *
 */

package com.pyamsoft.pydroid.arch

import android.os.Bundle
import androidx.annotation.CheckResult

interface UiBundleReader {

    @CheckResult
    fun <T : Any> get(key: String): T?

    fun <T : Any> getOrDefault(key: String, defaultValue: T): T

    fun <T : Any> useIfAvailable(key: String, func: (value: T) -> Unit)

    fun <T : Any> use(key: String, defaultValue: T, func: (value: T) -> Unit)


    companion object {

        @CheckResult
        fun create(bundle: Bundle?): UiBundleReader {
            return RealUiBundleReader(bundle)
        }
    }
}
