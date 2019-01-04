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

package com.pyamsoft.pydroid.core.optional

import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.core.optional.Optional.Absent
import com.pyamsoft.pydroid.core.optional.Optional.Present

@CheckResult
fun <T : Any> T?.asOptional(): Optional<T> {
  if (this == null) {
    return Absent
  } else {
    return Present(this)
  }
}
