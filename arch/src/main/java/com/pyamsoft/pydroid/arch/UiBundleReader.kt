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

package com.pyamsoft.pydroid.arch

import android.os.Bundle
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.arch.internal.RealUiBundleReader

/**
 * Reads saved state from a Bundle
 *
 * TODO(Peter): Remove in favor of UiSavedStateReader
 */
public interface UiBundleReader {

    /**
     * This will be false if the Activity/Fragment/View is being created for the first time.
     */
    @CheckResult
    public fun hasNoSavedState(): Boolean

    /**
     * Return the bundled value if one exists
     */
    @CheckResult
    public fun <T : Any> get(key: String): T?

    /**
     * Return the bundled value, or a default
     */
    @CheckResult
    public fun <T : Any> getOrDefault(key: String, defaultValue: T): T

    /**
     * Consume the bundled value if one exists
     */
    public fun <T : Any> useIfAvailable(key: String, func: (value: T) -> Unit)

    /**
     * Consume the bundled value, or a default.
     */
    public fun <T : Any> use(key: String, defaultValue: T, func: (value: T) -> Unit)

    public companion object {

        /**
         * Create an implementation of the UiBundleReader
         */
        @JvmStatic
        @CheckResult
        public fun create(bundle: Bundle?): UiBundleReader {
            return RealUiBundleReader(bundle)
        }
    }
}
