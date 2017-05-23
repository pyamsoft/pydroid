/*
 * Copyright 2017 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.helper

import android.support.annotation.CheckResult

data class Optional<out T> protected constructor(private val source: T?) {

  @CheckResult fun isPresent(): Boolean {
    return source != null
  }

  @CheckResult fun item(): T {
    return source!!
  }

  companion object {

    @JvmStatic @CheckResult fun <T> ofNullable(source: T?): Optional<T> {
      return Optional(source)
    }

    @JvmStatic @CheckResult fun <T> of(source: T): Optional<T> {
      return Optional(source)
    }
  }

}

