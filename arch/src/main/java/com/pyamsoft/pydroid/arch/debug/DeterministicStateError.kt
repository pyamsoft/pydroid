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

package com.pyamsoft.pydroid.arch.debug

/** Error when two state objects do not match up */
@Deprecated(
    "PYDroid has migrated to ViewModeler and handling Activity configChanges as recommended by Compose")
internal class DeterministicStateError
internal constructor(state1: Any?, state2: Any?, prop: String?) :
    IllegalStateException(
        """State changes must be deterministic
       ${if (prop != null) "Property '$prop' changed:" else ""}
       $state1
       $state2
       """.trimIndent())
