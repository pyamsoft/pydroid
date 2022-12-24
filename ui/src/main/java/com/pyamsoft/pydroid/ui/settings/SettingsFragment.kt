/*
 * Copyright 2022 Peter Kenji Yamanaka
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

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.CheckResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.unit.Dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.pyamsoft.pydroid.bootstrap.version.update.AppUpdateLauncher
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.app.PYDroidActivityOptions
import com.pyamsoft.pydroid.ui.internal.about.AboutDialog
import com.pyamsoft.pydroid.ui.internal.app.ComposeTheme
import com.pyamsoft.pydroid.ui.internal.app.NoopTheme
import com.pyamsoft.pydroid.ui.internal.app.invoke
import com.pyamsoft.pydroid.ui.internal.billing.dialog.BillingDialog
import com.pyamsoft.pydroid.ui.internal.changelog.dialog.ChangeLogDialog
import com.pyamsoft.pydroid.ui.internal.datapolicy.dialog.DataPolicyDisclosureDialog
import com.pyamsoft.pydroid.ui.internal.pydroid.ObjectGraph
import com.pyamsoft.pydroid.ui.internal.settings.SettingsScreen
import com.pyamsoft.pydroid.ui.internal.settings.SettingsViewModeler
import com.pyamsoft.pydroid.ui.internal.settings.reset.ResetDialog
import com.pyamsoft.pydroid.ui.internal.version.VersionCheckViewModeler
import com.pyamsoft.pydroid.ui.preference.Preferences
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.pydroid.ui.theme.ZeroElevation
import com.pyamsoft.pydroid.ui.util.dispose
import com.pyamsoft.pydroid.ui.util.recompose
import com.pyamsoft.pydroid.util.MarketLinker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** Fragment for displaying a settings page */
public abstract class SettingsFragment : Fragment() {

  /** May be provided by PYDroid, otherwise this is just a noop */
  internal var composeTheme: ComposeTheme = NoopTheme

  internal var options: PYDroidActivityOptions? = null

  internal var viewModel: SettingsViewModeler? = null
  internal var versionViewModel: VersionCheckViewModeler? = null

  private fun openPage(handler: UriHandler, url: String) {
    handler.openUri(url)
  }

  private fun onChangeDarkMode(mode: Theming.Mode) {
    val act = requireActivity()
    viewModel
        .requireNotNull()
        .handleChangeDarkMode(
            scope = act.lifecycleScope,
            mode = mode,
        )
  }

  private fun onViewBlog(handler: UriHandler) {
    viewModel.requireNotNull().handleViewBlog { url ->
      openPage(
          handler = handler,
          url = url,
      )
    }
  }

  private fun onViewSocialMedia(handler: UriHandler) {
    viewModel.requireNotNull().handleViewSocialMedia { url ->
      openPage(
          handler = handler,
          url = url,
      )
    }
  }

  private fun onViewTermsOfService(handler: UriHandler) {
    viewModel.requireNotNull().handleViewTermsOfService { url ->
      openPage(
          handler = handler,
          url = url,
      )
    }
  }

  private fun onViewPrivacyPolicy(handler: UriHandler) {
    viewModel.requireNotNull().handleViewPrivacyPolicy { url ->
      openPage(
          handler = handler,
          url = url,
      )
    }
  }

  private fun onViewSourceCode(handler: UriHandler) {
    viewModel.requireNotNull().handleViewSourceCode { url ->
      openPage(
          handler = handler,
          url = url,
      )
    }
  }

  private fun onReportBug(handler: UriHandler) {
    viewModel.requireNotNull().handleReportBug { url ->
      openPage(
          handler = handler,
          url = url,
      )
    }
  }

  private fun onCheckForUpdates() {
    if (options.requireNotNull().disableVersionCheck) {
      Logger.w("Application has disabled the VersionCheck component")
      return
    }

    versionViewModel
        .requireNotNull()
        .handleCheckForUpdates(
            scope = viewLifecycleOwner.lifecycleScope,
            force = true,
            onLaunchUpdate = {
              showVersionUpgrade(
                  activity = requireActivity(),
                  launcher = it,
              )
            },
        )
  }

  private fun showVersionUpgrade(
      activity: FragmentActivity,
      launcher: AppUpdateLauncher,
  ) {
    if (options.requireNotNull().disableVersionCheck) {
      Logger.w("Application has disabled the VersionCheck component")
      return
    }

    // Enforce that we do this on the Main thread
    activity.lifecycleScope.launch(context = Dispatchers.Main) {
      launcher
          .update(activity, RC_APP_UPDATE)
          .onSuccess { Logger.d("Launched an in-app update flow") }
          .onFailure { err -> Logger.e(err, "Unable to launch in-app update flow") }
    }
  }

  private fun onOpenMarketPage(uriHandler: UriHandler) {
    uriHandler.openUri(MarketLinker.getStorePageLink(requireActivity()))
  }

  private fun handleConfigurationChanged() {
    recompose()
  }

  /** Hide upgrade */
  protected abstract val hideUpgradeInformation: Boolean

  /** Hide clear button */
  protected abstract val hideClearAll: Boolean

  /** Override this method to implement any custom preferences in your app */
  @Composable @CheckResult protected abstract fun customPrePreferences(): List<Preferences>

  /** Override this method to implement any custom preferences in your app */
  @Composable @CheckResult protected abstract fun customPostPreferences(): List<Preferences>

  /** Override this method to add additional margin to the top settings item */
  @Composable @CheckResult protected abstract fun customTopItemMargin(): Dp

  /** Override this method to add additional margin to the top settings item */
  @Composable @CheckResult protected abstract fun customBottomItemMargin(): Dp

  /** Override this method to add additional margin to the top settings item */
  @Composable
  @CheckResult
  protected open fun customElevation(): Dp {
    return ZeroElevation
  }

  final override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View {
    val act = requireActivity()

    ObjectGraph.ActivityScope.retrieve(act).injector().plusSettings().create().inject(this)

    return ComposeView(act).apply {
      id = R.id.fragment_settings

      val vm = viewModel.requireNotNull()
      val opts = options.requireNotNull()
      setContent {
        val handler = LocalUriHandler.current

        val handleChangeDarkMode by rememberUpdatedState { mode: Theming.Mode ->
          onChangeDarkMode(mode)
        }

        val handleLicensesClicked by rememberUpdatedState { AboutDialog.show(act) }

        val handleCheckUpdates by rememberUpdatedState { onCheckForUpdates() }

        val handleShowChangelog by rememberUpdatedState { ChangeLogDialog.show(act) }

        val handleViewPrivacyPolicy by rememberUpdatedState { onViewPrivacyPolicy(handler) }

        val handleViewTermsOfService by rememberUpdatedState { onViewTermsOfService(handler) }

        val handleReportBug by rememberUpdatedState { onReportBug(handler) }

        val handleResetClicked by rememberUpdatedState { ResetDialog.show(act) }

        val handleDonateClicked by rememberUpdatedState { BillingDialog.show(act) }

        val handleViewSourceCode by rememberUpdatedState { onViewSourceCode(handler) }

        val handleViewSocialMedia by rememberUpdatedState { onViewSocialMedia(handler) }

        val handleViewBlog by rememberUpdatedState { onViewBlog(handler) }

        val handleViewMarketPage by rememberUpdatedState { onOpenMarketPage(handler) }

        val handleViewDataPolicy by rememberUpdatedState { DataPolicyDisclosureDialog.show(act) }

        composeTheme(act) {
          SettingsScreen(
              elevation = customElevation(),
              state = vm.state(),
              options = opts,
              hideClearAll = hideClearAll,
              hideUpgradeInformation = hideUpgradeInformation,
              topItemMargin = customTopItemMargin(),
              bottomItemMargin = customBottomItemMargin(),
              customPreContent = customPrePreferences(),
              customPostContent = customPostPreferences(),
              onDarkModeChanged = handleChangeDarkMode,
              onLicensesClicked = handleLicensesClicked,
              onCheckUpdateClicked = handleCheckUpdates,
              onShowChangeLogClicked = handleShowChangelog,
              onResetClicked = handleResetClicked,
              onDonateClicked = handleDonateClicked,
              onBugReportClicked = handleReportBug,
              onViewSourceClicked = handleViewSourceCode,
              onViewDataPolicyClicked = handleViewDataPolicy,
              onViewPrivacyPolicyClicked = handleViewPrivacyPolicy,
              onViewTermsOfServiceClicked = handleViewTermsOfService,
              onViewSocialMediaClicked = handleViewSocialMedia,
              onViewBlogClicked = handleViewBlog,
              onOpenMarketPage = handleViewMarketPage,
          )
        }
      }
    }
  }

  @CallSuper
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    versionViewModel.requireNotNull().restoreState(savedInstanceState)
    viewModel.requireNotNull().also { vm ->
      vm.restoreState(savedInstanceState)
      vm.bind(scope = viewLifecycleOwner.lifecycleScope)
      vm.handleLoadPreferences(scope = viewLifecycleOwner.lifecycleScope)
    }
  }

  @CallSuper
  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    viewModel?.saveState(outState)
    versionViewModel?.saveState(outState)
  }

  @CallSuper
  override fun onConfigurationChanged(newConfig: Configuration) {
    super.onConfigurationChanged(newConfig)
    handleConfigurationChanged()
  }

  @CallSuper
  override fun onDestroyView() {
    super.onDestroyView()
    dispose()

    viewModel = null
    versionViewModel = null
    options = null
  }

  public companion object {

    // Only bottom 16 bits.
    private const val RC_APP_UPDATE = 146
  }
}
