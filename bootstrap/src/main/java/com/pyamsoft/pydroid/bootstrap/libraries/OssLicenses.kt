/*
 * Copyright 2019 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.bootstrap.libraries

import androidx.annotation.CheckResult

enum class OssLicenses(
    override val license: String,
    override val location: String
) : LibraryLicense {
    APACHE2("Apache v2", "https://www.apache.org/licenses/LICENSE-2.0.html"),
    MIT("MIT", "https://mit-license.org/"),
    BSD2("Simplified BSD", "https://opensource.org/licenses/BSD-2-Clause"),
    BSD3("BSD 3-Clause", "https://opensource.org/licenses/BSD-3-Clause");

    companion object {
        @JvmStatic
        @CheckResult
        fun custom(license: String, location: String): LibraryLicense {
            return object : LibraryLicense {
                override val license = license
                override val location = location
            }
        }
    }
}
