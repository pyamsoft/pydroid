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

package com.pyamsoft.pydroid.bootstrap.otherapps.api

/** A representation of an other pyamsoft application */
public data class OtherApp
// No longer internal to allow preview to work
/*internal*/ constructor(
    /** Package name */
    val packageName: String,

    /** Application name */
    val name: String,

    /** Description */
    val description: String,

    /** Application icon url */
    val icon: String,

    /** Application play store URL */
    val storeUrl: String,

    /** Application source code URL */
    val sourceUrl: String
)
