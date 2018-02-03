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

import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.annotation.CheckResult
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.databinding.FragmentAppSettingsBinding

abstract class AppSettingsFragment : ToolbarFragment() {

  private lateinit var binding: FragmentAppSettingsBinding

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = FragmentAppSettingsBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onDestroyView() {
    super.onDestroyView()
    binding.unbind()
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    showPreferenceFragment()
  }

  private fun showPreferenceFragment() {
    val fragmentManager = childFragmentManager
    val tag: String = provideSettingsTag()
    if (fragmentManager.findFragmentByTag(tag) == null) {
      fragmentManager.beginTransaction()
          .add(R.id.app_settings_content, provideSettingsFragment(), tag)
          .commit()
    }
  }

  @CallSuper
  override fun onBackPressed(): Boolean {
    return false
  }

  @CheckResult
  abstract fun provideSettingsFragment(): SettingsPreferenceFragment

  @CheckResult
  abstract fun provideSettingsTag(): String
}
