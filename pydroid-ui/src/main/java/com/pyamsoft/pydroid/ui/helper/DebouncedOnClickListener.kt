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

package com.pyamsoft.pydroid.ui.helper

import android.support.annotation.CheckResult
import android.view.View

/**
 * Click listener which debounces all other click events for the frame
 */
abstract class DebouncedOnClickListener : View.OnClickListener {

    final override fun onClick(view: View) {
        if (enabled) {
            enabled = false
            view.post(enableAgain)
            doClick(view)
        }
    }

    abstract fun doClick(view: View)

    companion object {
        private var enabled: Boolean = true
        private var enableAgain: Runnable = Runnable { enabled = true }

        @CheckResult
        @JvmStatic
        inline fun create(crossinline func: (View) -> Unit): View.OnClickListener {
            return object : DebouncedOnClickListener() {
                override fun doClick(view: View) {
                    func(view)
                }
            }
        }
    }
}

fun View.setOnDebouncedClickListener(listener: DebouncedOnClickListener?) {
    setOnClickListener(listener)
}

inline fun View.setOnDebouncedClickListener(crossinline func: (View) -> Unit) {
    setOnClickListener(DebouncedOnClickListener.create(func))
}

