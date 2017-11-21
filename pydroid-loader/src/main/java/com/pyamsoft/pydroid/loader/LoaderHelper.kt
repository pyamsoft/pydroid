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

package com.pyamsoft.pydroid.loader

import android.support.annotation.CheckResult
import com.pyamsoft.pydroid.loader.loaded.Loaded

object LoaderHelper {

    @JvmOverloads
    @JvmStatic
    @CheckResult
    fun unload(entry: Loaded?,
            defaultLoaded: Loaded = empty()): Loaded {
        if (entry == null) {
            return defaultLoaded
        }

        if (!entry.isUnloaded) {
            entry.unload()
        }
        return defaultLoaded
    }

    @JvmStatic
    @CheckResult
    fun empty(): Loaded {
        return object : Loaded {

            private var unloaded = false

            override val isUnloaded: Boolean = unloaded

            override fun unload() {
                unloaded = true
            }
        }
    }
}
