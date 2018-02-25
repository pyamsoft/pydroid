/*
 * Copyright (C) 2018 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pydroid.optional

import android.support.annotation.CheckResult

/**
 * A simple Optional API, You can drop in your own implementation if needed.
 *
 * The PYDroid standard implementation is found in OptionalImpl
 * Keep the unused T here for better casting
 */
interface Optional<out T : Any> {

  interface Present<out T : Any> : Optional<T> {

    val value: T
  }

  interface Absent : Optional<Nothing>
}

internal data class PresentImpl<out T : Any> internal constructor(override val value: T) :
    Optional.Present<T>

internal object AbsentImpl : Optional.Absent

@CheckResult
fun <T : Any> T?.asOptional(): Optional<T> = if (this == null) AbsentImpl else PresentImpl(this)
