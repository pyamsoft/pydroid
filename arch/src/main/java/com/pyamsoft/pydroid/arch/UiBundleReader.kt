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
 * NOTE: This class will be going away in favor of UiSavedStateReader.
 *       That class only supports the get(String): T? function. Migrate
 *       all existing UiViews and other code to only use the get(String): T?
 *       function from this class to prepare for future updates.
 *
 * TODO(Peter): Remove in favor of UiSavedStateReader
 */
public interface UiBundleReader {

    /**
     * This will be false if the Activity/Fragment/View is being created for the first time.
     */
    @CheckResult
    @Deprecated("Use get(key): T? and handle the null case")
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
    @Deprecated("Use get(key): T?")
    public fun <T : Any> getOrDefault(key: String, defaultValue: T): T

    /**
     * Consume the bundled value if one exists
     */
    @Deprecated("Use get(key): T?")
    public fun <T : Any> useIfAvailable(key: String, func: (value: T) -> Unit)

    /**
     * Consume the bundled value, or a default.
     */
    @Deprecated("Use get(key): T?")
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
