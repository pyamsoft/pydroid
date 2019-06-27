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
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.util.commit
import kotlinx.coroutines.ExperimentalCoroutinesApi

abstract class AppSettingsFragment : Fragment() {

  private var coordinatorLayout: CoordinatorLayout? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.layout_coordinator, container, false)
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    coordinatorLayout = view.findViewById(R.id.layout_coordinator)
    showPreferenceFragment()
  }

  private fun showPreferenceFragment() {
    val fragmentManager = childFragmentManager
    val tag: String = provideSettingsTag()
    if (fragmentManager.findFragmentByTag(tag) == null) {
      fragmentManager.commit(viewLifecycleOwner) {
        add(requireNotNull(coordinatorLayout).id, provideSettingsFragment(), tag)
      }
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    coordinatorLayout = null
  }

  @CheckResult
  abstract fun provideSettingsFragment(): AppSettingsPreferenceFragment

  @CheckResult
  abstract fun provideSettingsTag(): String
}
