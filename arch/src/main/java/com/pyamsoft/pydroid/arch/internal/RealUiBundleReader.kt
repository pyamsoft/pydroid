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

package com.pyamsoft.pydroid.arch.internal

import android.os.Bundle
import com.pyamsoft.pydroid.arch.UiBundleReader

/**
 * Bundle backed implementation of a UiBundleReader
 */
internal class RealUiBundleReader internal constructor(
    private val bundle: Bundle?
) : UiBundleReader {

    // Captures the common if (savedInstanceState == null) case
    override fun hasNoSavedState(): Boolean {
        return bundle == null
    }

    override fun <T : Any> get(key: String): T? {
        @Suppress("UNCHECKED_CAST")
        return bundle?.get(key) as? T
    }

    override fun <T : Any> getOrDefault(key: String, defaultValue: T): T {
        return get(key) ?: defaultValue
    }

    override fun <T : Any> useIfAvailable(key: String, func: (value: T) -> Unit) {
        val value: T? = get(key)
        if (value != null) {
            func(value)
        }
    }

    override fun <T : Any> use(key: String, defaultValue: T, func: (value: T) -> Unit) {
        val value = getOrDefault(key, defaultValue)
        func(value)
    }
}
