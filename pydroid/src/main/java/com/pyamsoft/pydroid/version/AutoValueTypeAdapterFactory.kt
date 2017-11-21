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

package com.pyamsoft.pydroid.version

import android.support.annotation.CheckResult
import android.support.annotation.RestrictTo
import com.google.gson.TypeAdapterFactory
import com.ryanharter.auto.value.gson.GsonTypeAdapterFactory

@RestrictTo(RestrictTo.Scope.LIBRARY)
@GsonTypeAdapterFactory
abstract internal class AutoValueTypeAdapterFactory internal constructor() : TypeAdapterFactory {

    companion object {

        @JvmStatic
        @CheckResult
        fun create(): TypeAdapterFactory =
                AutoValueGson_AutoValueTypeAdapterFactory()
    }
}
