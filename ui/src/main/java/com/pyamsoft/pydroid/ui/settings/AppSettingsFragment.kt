/*
 * Copyright 2020 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.CheckResult
import androidx.fragment.app.Fragment
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.databinding.LayoutSettingsBinding
import com.pyamsoft.pydroid.ui.util.commit

/** Fragment for displaying a settings page */
@Deprecated("Migrate to Jetpack Compose via com.pyamsoft.pydroid.ui.settings.SettingsFragment")
public abstract class AppSettingsFragment : Fragment() {

  final override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.layout_settings, container, false)
  }

  /** Created view */
  @CallSuper
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val binding = LayoutSettingsBinding.bind(view)
    showPreferenceFragment(binding)
  }

  private fun showPreferenceFragment(binding: LayoutSettingsBinding) {
    val fragmentManager = childFragmentManager
    val tag: String = provideSettingsTag()
    if (fragmentManager.findFragmentByTag(tag) == null) {
      fragmentManager.commit(viewLifecycleOwner) {
        add(binding.settingsContainer.id, provideSettingsFragment(), tag)
      }
    }
  }

  /** Create a new settings fragment */
  @CheckResult protected abstract fun provideSettingsFragment(): AppSettingsPreferenceFragment

  /** Create a settings tag */
  @CheckResult protected abstract fun provideSettingsTag(): String
}
