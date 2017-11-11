/*
 *     Copyright (C) 2017 Peter Kenji Yamanaka
 *
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.pyamsoft.pydroid.helper

import android.support.annotation.CheckResult
import com.pyamsoft.pydroid.helper.Optional.Absent
import com.pyamsoft.pydroid.helper.Optional.Present

sealed class Optional<out T : Any> {

  @CheckResult
  fun get(): T? = when (this) {
    is Present -> value
    is Absent -> null
  }

  data class Present<out T : Any>(val value: T) : Optional<T>()
  object Absent : Optional<Nothing>()

  companion object {

    @JvmStatic
    @CheckResult
    fun <T : Any> asOptional(source: T?): Optional<T> = source.asOptional()
  }
}

@CheckResult
fun <T : Any> T?.asOptional(): Optional<T> = if (this == null) Absent else Present(
    this)