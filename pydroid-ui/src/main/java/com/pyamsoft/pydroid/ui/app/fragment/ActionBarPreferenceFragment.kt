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

import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.support.v7.preference.PreferenceFragmentCompat
import com.pyamsoft.pydroid.ui.util.ActionBarUtil

abstract class ActionBarPreferenceFragment : PreferenceFragmentCompat(), ActionBarProvider {

  override fun setActionBarUpEnabled(up: Boolean) {
    ActionBarUtil.setActionBarUpEnabled(activity!!, up)
  }

  override fun setActionBarUpEnabled(up: Boolean, @DrawableRes icon: Int) {
    ActionBarUtil.setActionBarUpEnabled(activity!!, up, icon)
  }

  override fun setActionBarUpEnabled(up: Boolean, icon: Drawable?) {
    ActionBarUtil.setActionBarUpEnabled(activity!!, up, icon)
  }

  override fun setActionBarTitle(title: CharSequence) {
    ActionBarUtil.setActionBarTitle(activity!!, title)
  }

  override fun setActionBarTitle(@StringRes title: Int) {
    ActionBarUtil.setActionBarTitle(activity!!, title)
  }
}
