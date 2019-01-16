/*
 * Copyright 2019 Peter Kenji Yamanaka
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
 *
 */

package com.pyamsoft.pydroid.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.ui.app.fragment.ToolbarFragment
import com.pyamsoft.pydroid.ui.databinding.LayoutCoordinatorBinding
import com.pyamsoft.pydroid.ui.util.commit

abstract class AppSettingsFragment : ToolbarFragment() {

  private lateinit var binding: LayoutCoordinatorBinding

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = LayoutCoordinatorBinding.inflate(inflater, container, false)
    return binding.layoutCoordinator
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
          .add(binding.layoutCoordinator.id, provideSettingsFragment(), tag)
          .commit(viewLifecycleOwner)
    }
  }

  @CheckResult
  abstract fun provideSettingsFragment(): AppSettingsPreferenceFragment

  @CheckResult
  abstract fun provideSettingsTag(): String
}
