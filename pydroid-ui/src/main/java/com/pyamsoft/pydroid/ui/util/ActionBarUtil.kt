/*
 * Copyright 2017 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pydroid.ui.util

import android.app.Activity
import android.graphics.drawable.Drawable
import android.support.annotation.CheckResult
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.support.v4.content.ContextCompat
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity

class ActionBarUtil private constructor() {

  init {
    throw RuntimeException("No instances")
  }

  companion object {

    @JvmStatic @CheckResult fun getActionBar(activity: Activity): ActionBar {
      if (activity is AppCompatActivity) {
        return activity.supportActionBar!!
      } else {
        throw ClassCastException("Activity not instance of AppCompatActivity")
      }
    }

    @JvmStatic fun setActionBarUpEnabled(activity: Activity, up: Boolean, @DrawableRes icon: Int) {
      val d: Drawable?
      if (icon != 0) {
        d = ContextCompat.getDrawable(activity, icon)
      } else {
        d = null
      }

      setActionBarUpEnabled(activity, up, d)
    }

    @JvmStatic @JvmOverloads fun setActionBarUpEnabled(activity: Activity, up: Boolean,
        icon: Drawable? = null) {
      val bar = getActionBar(activity)
      bar.setHomeButtonEnabled(up)
      bar.setDisplayHomeAsUpEnabled(up)
      bar.setHomeAsUpIndicator(icon)
    }

    @JvmStatic fun setActionBarTitle(activity: Activity, title: CharSequence) {
      getActionBar(activity).title = title
    }

    @JvmStatic fun setActionBarTitle(activity: Activity, @StringRes title: Int) {
      getActionBar(activity).setTitle(title)
    }
  }
}
