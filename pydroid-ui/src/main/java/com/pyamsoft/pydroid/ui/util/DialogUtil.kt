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

package com.pyamsoft.pydroid.ui.util

import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentActivity
import timber.log.Timber

object DialogUtil {

    /**
     * Using the fragment manager to handle transactions, this guarantees that any old
     * versions of the dialog fragment are removed before a new one is added.
     */
    @JvmStatic
    fun guaranteeSingleDialogFragment(
        fragmentActivity: FragmentActivity?,
        dialogFragment: DialogFragment, tag: String
    ) {
        if (fragmentActivity == null) {
            Timber.w("Cannot attach a fragment to a NULL activity. No-op")
            return
        }

        if (tag.isEmpty()) {
            throw IllegalArgumentException("Cannot use EMPTY tag")
        }

        val fragmentManager = fragmentActivity.supportFragmentManager
        val ft = fragmentManager.beginTransaction()
        val prev = fragmentManager.findFragmentByTag(tag)
        if (prev != null) {
            Timber.d("Remove existing fragment with tag: %s", tag)
            ft.remove(prev)
        }

        Timber.d("Add new fragment with tag: %s", tag)
        dialogFragment.show(ft, tag)
    }
}
