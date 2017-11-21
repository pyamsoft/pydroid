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

package com.pyamsoft.pydroid.ui

import android.content.Context
import android.content.SharedPreferences
import android.support.v7.preference.PreferenceManager

internal class PYDroidPreferencesImpl internal constructor(context: Context) : RatingPreferences {

  private val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(
      context.applicationContext)

  override fun getRatingAcceptedVersion(): Int = preferences.getInt(RATING_ACCEPTED_VERSION, 1)

  override fun setRatingAcceptedVersion(version: Int) {
    preferences.edit().putInt(RATING_ACCEPTED_VERSION, version).apply()
  }

  companion object {

    private const val RATING_ACCEPTED_VERSION = "rating_dialog_accepted_version"
  }
}
