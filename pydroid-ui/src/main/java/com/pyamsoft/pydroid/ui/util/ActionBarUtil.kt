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

import android.app.Activity
import android.graphics.drawable.Drawable
import android.support.annotation.CheckResult
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.content.res.AppCompatResources

object ActionBarUtil {

  @JvmStatic
  @CheckResult
  fun getActionBar(activity: Activity): ActionBar {
    if (activity is AppCompatActivity) {
      val bar = activity.supportActionBar
      if (bar == null) {
        throw IllegalStateException("ActionBar is NULL")
      } else {
        return bar
      }
    } else {
      throw ClassCastException("Activity not instance of AppCompatActivity")
    }
  }

  @JvmStatic
  fun setActionBarUpEnabled(activity: Activity, up: Boolean, @DrawableRes icon: Int) {
    val d: Drawable? = if (icon != 0) {
      AppCompatResources.getDrawable(activity, icon)
    } else {
      null
    }

    setActionBarUpEnabled(activity, up, d)
  }

  @JvmStatic
  @JvmOverloads
  fun setActionBarUpEnabled(activity: Activity, up: Boolean,
      icon: Drawable? = null) {
    val bar = getActionBar(activity)
    bar.setHomeButtonEnabled(up)
    bar.setDisplayHomeAsUpEnabled(up)
    bar.setHomeAsUpIndicator(icon)
  }

  @JvmStatic
  fun setActionBarTitle(activity: Activity, title: CharSequence) {
    getActionBar(activity).title = title
  }

  @JvmStatic
  fun setActionBarTitle(activity: Activity, @StringRes title: Int) {
    getActionBar(activity).setTitle(title)
  }
}
