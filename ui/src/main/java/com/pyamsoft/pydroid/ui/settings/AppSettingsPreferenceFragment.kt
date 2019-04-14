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
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.XmlRes
import androidx.preference.PreferenceFragmentCompat
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.about.AboutFragment
import com.pyamsoft.pydroid.ui.app.ActivityBase
import com.pyamsoft.pydroid.ui.util.MarketLinker
import com.pyamsoft.pydroid.util.HyperlinkIntent
import timber.log.Timber
import javax.inject.Inject

abstract class AppSettingsPreferenceFragment : PreferenceFragmentCompat(),
    AppSettingsUiComponent.Callback {

  protected open val preferenceXmlResId: Int = 0

  protected open val hideUpgradeInformation: Boolean = false

  protected open val hideClearAll: Boolean = false

  @field:Inject internal lateinit var component: AppSettingsUiComponent

  @CallSuper
  override fun onCreatePreferences(
    savedInstanceState: Bundle?,
    rootKey: String?
  ) {
    @XmlRes val xmlResId: Int = preferenceXmlResId
    if (xmlResId != 0) {
      addPreferencesFromResource(xmlResId)
    }
    addPreferencesFromResource(R.xml.pydroid)
  }

  @CallSuper
  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)

    PYDroid.obtain(view.context.applicationContext)
        .plusSettingsComponent()
        .create(preferenceScreen, hideClearAll, hideUpgradeInformation)
        .inject(this)

    component.bind(viewLifecycleOwner, savedInstanceState, this)
  }

  final override fun onViewMorePyamsoftApps() {
    val error = MarketLinker.linkToDeveloperPage(requireContext())
    if (error != null) {
      component.failedNavigation(error)
    }
  }

  final override fun onDarkThemeChanged(dark: Boolean) {
    onDarkThemeClicked(dark)
  }

  final override fun onClearAppData() {
    onClearAllClicked()
  }

  final override fun onViewLicenses() {
    onLicenseItemClicked()
  }

  final override fun onRateApp() {
    requireContext().also { c ->
      val link = c.packageName
      val error = MarketLinker.linkToMarketPage(c, link)
      if (error != null) {
        component.failedNavigation(error)
      }
    }
  }

  final override fun onNavigateToLink(link: HyperlinkIntent) {
    val error = link.navigate()
    if (error != null) {
      component.failedNavigation(error)
    }
  }

  @CallSuper
  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    component.saveState(outState)
  }

  /**
   * Logs when the Clear All option is clicked, override to use unique implementation
   */
  @CallSuper
  protected open fun onClearAllClicked() {
    Timber.d("Clear all preferences clicked")
  }

  /**
   * Toggles dark theme, override or extend to use unique implementation
   */
  @CallSuper
  protected open fun onDarkThemeClicked(dark: Boolean) {
    Timber.d("Dark theme set: $dark")
    requireActivity().recreate()
  }

  /**
   * Shows a page for Open Source licenses, override or extend to use unique implementation
   */
  @CallSuper
  protected open fun onLicenseItemClicked() {
    val a = requireActivity()
    if (a is ActivityBase) {
      Timber.d("Show about licenses fragment")
      AboutFragment.show(a, a.fragmentContainerId)
    }
  }
}
