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

import android.arch.lifecycle.Lifecycle.Event.ON_CREATE
import android.arch.lifecycle.Lifecycle.Event.ON_DESTROY
import android.arch.lifecycle.Lifecycle.Event.ON_PAUSE
import android.arch.lifecycle.Lifecycle.Event.ON_RESUME
import android.arch.lifecycle.Lifecycle.Event.ON_START
import android.arch.lifecycle.Lifecycle.Event.ON_STOP
import android.arch.lifecycle.LifecycleOwner
import android.os.Bundle
import android.support.annotation.CheckResult
import android.support.v7.preference.PreferenceFragmentCompat
import android.view.View
import com.pyamsoft.pydroid.ui.app.activity.ToolbarActivity

abstract class ToolbarPreferenceFragment : PreferenceFragmentCompat(), BackPressHandler,
        ToolbarProvider, ViewLifecycleProvider {

    private val viewLifecycleOwner = ViewLifecycleOwner()
    final override val viewLifecycle: LifecycleOwner = viewLifecycleOwner

    override val toolbarActivity: ToolbarActivity
        @get:CheckResult get() {
            val a = activity
            if (a is ToolbarActivity) {
                return a
            } else {
                throw ClassCastException("Activity does not implement ToolbarActivity")
            }
        }

    override fun onBackPressed(): Boolean {
        return false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.registry.handleLifecycleEvent(ON_CREATE)
    }

    override fun onStart() {
        super.onStart()
        viewLifecycleOwner.registry.handleLifecycleEvent(ON_START)
    }

    override fun onResume() {
        super.onResume()
        viewLifecycleOwner.registry.handleLifecycleEvent(ON_RESUME)
    }

    override fun onPause() {
        super.onPause()
        viewLifecycleOwner.registry.handleLifecycleEvent(ON_PAUSE)
    }

    override fun onStop() {
        super.onStop()
        viewLifecycleOwner.registry.handleLifecycleEvent(ON_STOP)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewLifecycleOwner.registry.handleLifecycleEvent(ON_DESTROY)
    }
}