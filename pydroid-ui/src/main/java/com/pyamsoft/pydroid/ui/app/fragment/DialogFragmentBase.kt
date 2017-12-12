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

package com.pyamsoft.pydroid.ui.app.fragment

import android.app.Dialog
import android.os.Bundle
import android.support.annotation.CheckResult
import android.support.v4.app.DialogFragment
import android.view.Window
import com.pyamsoft.pydroid.ui.app.activity.ToolbarActivity

abstract class DialogFragmentBase : DialogFragment(), ToolbarProvider {

    override val toolbarActivity: ToolbarActivity
        @get:CheckResult get() {
            val a = activity
            if (a is ToolbarActivity) {
                return a
            } else {
                throw ClassCastException("Activity does not implement ToolbarActivity")
            }
        }

    protected open val hasTitle: Boolean = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        if (!hasTitle) {
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        }

        return dialog
    }

}
