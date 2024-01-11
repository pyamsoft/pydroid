/*
 * Copyright 2024 pyamsoft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
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

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

/** Representation of an open source library dependency */
@Stable
@Immutable
public data class OssLibrary
internal constructor(
    /** Library name */
    val name: String,

    /** Library description */
    val description: String,

    /** Library source code URL */
    val libraryUrl: String,

    /** Library license name */
    val licenseName: String,

    /** Library license URL */
    val licenseUrl: String,

    /** A unique key per library */
    val key: String = "${name}:${libraryUrl}"
)
