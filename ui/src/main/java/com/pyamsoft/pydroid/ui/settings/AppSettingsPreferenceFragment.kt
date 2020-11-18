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

import android.content.ActivityNotFoundException
import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.XmlRes
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceFragmentCompat
import com.pyamsoft.pydroid.arch.StateSaver
import com.pyamsoft.pydroid.arch.createComponent
import com.pyamsoft.pydroid.bootstrap.otherapps.api.OtherApp
import com.pyamsoft.pydroid.bootstrap.rating.AppReviewLauncher
import com.pyamsoft.pydroid.bootstrap.version.AppUpdateLauncher
import com.pyamsoft.pydroid.ui.Injector
import com.pyamsoft.pydroid.ui.PYDroidComponent
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.about.AboutDialog
import com.pyamsoft.pydroid.ui.arch.viewModelFactory
import com.pyamsoft.pydroid.ui.otherapps.OtherAppsDialog
import com.pyamsoft.pydroid.ui.rating.RatingActivity
import com.pyamsoft.pydroid.ui.rating.RatingControllerEvent.LoadRating
import com.pyamsoft.pydroid.ui.rating.RatingViewModel
import com.pyamsoft.pydroid.ui.settings.clear.SettingsClearConfigDialog
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.pydroid.ui.util.MarketLinker
import com.pyamsoft.pydroid.ui.util.removeAllItemDecorations
import com.pyamsoft.pydroid.ui.util.show
import com.pyamsoft.pydroid.ui.version.VersionCheckActivity
import com.pyamsoft.pydroid.ui.version.VersionCheckControllerEvent.LaunchUpdate
import com.pyamsoft.pydroid.ui.version.VersionCheckControllerEvent.ShowUpgrade
import com.pyamsoft.pydroid.ui.version.VersionCheckView
import com.pyamsoft.pydroid.ui.version.VersionCheckViewModel
import com.pyamsoft.pydroid.ui.version.upgrade.VersionUpgradeDialog
import com.pyamsoft.pydroid.util.HyperlinkIntent
import me.zhanghai.android.fastscroll.FastScrollerBuilder
import timber.log.Timber

abstract class AppSettingsPreferenceFragment : PreferenceFragmentCompat() {

    private var settingsStateSaver: StateSaver? = null
    private var ratingStateSaver: StateSaver? = null
    private var versionStateSaver: StateSaver? = null

    protected open val preferenceXmlResId: Int = 0

    protected open val hideUpgradeInformation: Boolean = false

    protected open val hideClearAll: Boolean = false

    internal var settingsView: AppSettingsView? = null

    internal var versionCheckView: VersionCheckView? = null

    internal var factory: ViewModelProvider.Factory? = null
    private val settingsViewModel by viewModelFactory<AppSettingsViewModel>(activity = true) { factory }
    private val versionViewModel by viewModelFactory<VersionCheckViewModel>(activity = true) { factory }
    private val ratingViewModel by viewModelFactory<RatingViewModel>(activity = true) { factory }

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

        Injector.obtain<PYDroidComponent>(view.context.applicationContext)
            .plusSettings()
            .create(
                viewLifecycleOwner,
                preferenceScreen,
                hideClearAll,
                hideUpgradeInformation
            ) { listView }
            .inject(this)

        settingsStateSaver = createComponent(
            savedInstanceState, viewLifecycleOwner,
            settingsViewModel,
            requireNotNull(settingsView)
        ) {
            return@createComponent when (it) {
                is AppSettingsControllerEvent.NavigateMoreApps -> viewMorePyamsoftApps()
                is AppSettingsControllerEvent.NavigateHyperlink -> navigateHyperlink(it.hyperlinkIntent)
                is AppSettingsControllerEvent.NavigateRateApp -> openMarkForRating()
                is AppSettingsControllerEvent.ShowLicense -> openLicensesPage()
                is AppSettingsControllerEvent.CheckUpgrade -> versionViewModel.checkForUpdates(true)
                is AppSettingsControllerEvent.AttemptClearData -> openClearDataDialog()
                is AppSettingsControllerEvent.OpenShowUpgrade -> ratingViewModel.load(true)
                is AppSettingsControllerEvent.ChangeDarkTheme -> darkThemeChanged(it.newMode)
                is AppSettingsControllerEvent.OpenOtherAppsPage -> openOtherAppsPage(it.apps)
            }
        }

        versionStateSaver = createComponent(
            savedInstanceState, viewLifecycleOwner,
            versionViewModel,
            requireNotNull(versionCheckView)
        ) {
            return@createComponent when (it) {
                is LaunchUpdate -> handleLaunchUpdate(it.launcher)
                is ShowUpgrade -> handleShowUpgrade()
            }
        }

        ratingStateSaver = createComponent(
            savedInstanceState, viewLifecycleOwner,
            ratingViewModel
        ) {
            return@createComponent when (it) {
                is LoadRating -> openUpdateInfo(it.launcher)
            }
        }

        settingsViewModel.syncDarkThemeState(requireActivity())

        setupPreferenceListView()
    }

    private fun setupPreferenceListView() {
        val list = listView ?: return
        list.isHorizontalScrollBarEnabled = false
        list.isVerticalScrollBarEnabled = false

        FastScrollerBuilder(list)
            .useMd2Style()
            .build()
    }

    private fun handleShowUpgrade() {
        val act = requireActivity()
        if (act is VersionCheckActivity) {
            Timber.d("ShowUpgrade event handled by activity")
        } else {
            VersionUpgradeDialog.show(act)
        }
    }

    private fun openOtherAppsPage(apps: List<OtherApp>) {
        onViewMorePyamsoftAppsClicked(false)
        Timber.d("Show other apps fragment: $apps")
        OtherAppsDialog().show(requireActivity(), OtherAppsDialog.TAG)
    }

    private fun openUpdateInfo(launcher: AppReviewLauncher) {
        val act = requireActivity() as RatingActivity
        launcher.review(act)
    }

    private fun handleLaunchUpdate(launcher: AppUpdateLauncher) {
        val act = requireActivity() as VersionCheckActivity
        act.showVersionUpgrade(launcher)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        settingsView = null
        factory = null
        settingsStateSaver = null
        ratingStateSaver = null
        versionStateSaver = null

        // Clear list view
        listView?.removeAllItemDecorations()
    }

    private fun failedNavigation(error: ActivityNotFoundException?) {
        if (error == null) {
            settingsViewModel.navigationSuccess()
        } else {
            settingsViewModel.navigationFailed(error)
        }
    }

    private fun viewMorePyamsoftApps() {
        onViewMorePyamsoftAppsClicked(true)
        val error = MarketLinker.linkToDeveloperPage(requireContext())
        failedNavigation(error)
    }

    private fun darkThemeChanged(mode: Theming.Mode) {
        onDarkThemeClicked(mode)
    }

    private fun openClearDataDialog() {
        onClearAllPrompt()
    }

    /**
     * Logs when the Clear All option is clicked, override to use unique implementation
     *
     * NOTE: In the future this method will be going away as the clear all flow will be handled by the library.
     * Custom clear logic in the middle will be supported but the UI for the prompt, and the end result of clearing
     * user application data will be enforced by the library.
     */
    protected open fun onClearAllPrompt() {
        SettingsClearConfigDialog.newInstance()
            .show(requireActivity(), SettingsClearConfigDialog.TAG)
    }

    private fun openLicensesPage() {
        onLicenseItemClicked()
    }

    private fun openMarkForRating() {
        requireContext().also { c ->
            val link = c.packageName
            val error = MarketLinker.linkToMarketPage(c, link)
            failedNavigation(error)
        }
    }

    private fun navigateHyperlink(link: HyperlinkIntent) {
        val error = link.navigate()
        failedNavigation(error)
    }

    @CallSuper
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        settingsStateSaver?.saveState(outState)
        ratingStateSaver?.saveState(outState)
        versionStateSaver?.saveState(outState)
    }

    /**
     * Toggles dark theme, override or extend to use unique implementation
     */
    @CallSuper
    protected open fun onDarkThemeClicked(mode: Theming.Mode) {
        Timber.d("Dark theme set: $mode")
    }

    /**
     * Shows a page for Open Source licenses, override or extend to use unique implementation
     */
    @CallSuper
    protected open fun onLicenseItemClicked() {
        Timber.d("Show about licenses fragment")
        AboutDialog().show(requireActivity(), AboutDialog.TAG)
    }

    /**
     * Shows a page for  Source licenses, override or extend to use unique implementation
     */
    protected open fun onViewMorePyamsoftAppsClicked(navigatingAway: Boolean) {
    }
}
