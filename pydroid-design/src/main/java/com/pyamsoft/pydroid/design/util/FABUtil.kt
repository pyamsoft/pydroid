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

package com.pyamsoft.pydroid.design.util

import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton

object FABUtil {

    @JvmStatic
    @JvmOverloads
    fun setupFABBehavior(fab: FloatingActionButton,
            behavior: FloatingActionButton.Behavior = FloatingActionButton.Behavior()) {
        val params = fab.layoutParams
        if (params is CoordinatorLayout.LayoutParams) {
            params.behavior = behavior
        }
    }
}
