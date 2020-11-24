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

package com.pyamsoft.pydroid.bootstrap.libraries

import androidx.annotation.CheckResult

/**
 * Enum about supported open source licenses
 */
public enum class OssLicenses(
    override val license: String,
    override val location: String
) : LibraryLicense {

    /**
     * The Apache v2.0 License
     */
    APACHE2("Apache v2", "https://www.apache.org/licenses/LICENSE-2.0.html"),

    /**
     * The MIT License
     */
    MIT("MIT", "https://mit-license.org/"),

    /**
     * The BSD 2-Clause or Simplified BSD License
     */
    BSD2("Simplified BSD", "https://opensource.org/licenses/BSD-2-Clause"),

    /**
     * The BSD 3-Clause License
     */
    BSD3("BSD 3-Clause", "https://opensource.org/licenses/BSD-3-Clause");

    public companion object {

        /**
         * A custom license
         */
        @JvmStatic
        @CheckResult
        public fun custom(license: String, location: String): LibraryLicense {
            return object : LibraryLicense {
                override val license = license
                override val location = location
            }
        }
    }
}
