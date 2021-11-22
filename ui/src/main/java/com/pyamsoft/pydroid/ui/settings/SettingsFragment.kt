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

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.CheckResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.unit.Dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.ViewWindowInsetObserver
import com.pyamsoft.pydroid.bootstrap.version.AppUpdateLauncher
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.inject.Injector
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.app.ComposeTheme
import com.pyamsoft.pydroid.ui.app.PYDroidActivity
import com.pyamsoft.pydroid.ui.internal.about.AboutDialog
import com.pyamsoft.pydroid.ui.internal.app.AppComponent
import com.pyamsoft.pydroid.ui.internal.app.NoopTheme
import com.pyamsoft.pydroid.ui.internal.billing.BillingDialog
import com.pyamsoft.pydroid.ui.internal.changelog.ChangeLogViewModeler
import com.pyamsoft.pydroid.ui.internal.changelog.dialog.ChangeLogDialog
import com.pyamsoft.pydroid.ui.internal.datapolicy.DataPolicyViewModeler
import com.pyamsoft.pydroid.ui.internal.datapolicy.dialog.DataPolicyDisclosureDialog
import com.pyamsoft.pydroid.ui.internal.otherapps.OtherAppsDialog
import com.pyamsoft.pydroid.ui.internal.rating.RatingViewModeler
import com.pyamsoft.pydroid.ui.internal.settings.SettingsScreen
import com.pyamsoft.pydroid.ui.internal.settings.SettingsViewModeler
import com.pyamsoft.pydroid.ui.internal.settings.reset.ResetDialog
import com.pyamsoft.pydroid.ui.internal.version.VersionCheckViewModeler
import com.pyamsoft.pydroid.ui.preference.Preferences
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.pydroid.ui.util.recompose
import com.pyamsoft.pydroid.util.MarketLinker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** Fragment for displaying a settings page */
public abstract class SettingsFragment : Fragment() {

  /** May be provided by PYDroid, otherwise this is just a noop */
  internal var composeTheme: ComposeTheme = NoopTheme

  internal var viewModel: SettingsViewModeler? = null
  internal var ratingViewModel: RatingViewModeler? = null
  internal var versionViewModel: VersionCheckViewModeler? = null
  internal var changeLogViewModel: ChangeLogViewModeler? = null
  internal var dataPolicyViewModel: DataPolicyViewModeler? = null

  // Watches the window insets
  private var windowInsetObserver: ViewWindowInsetObserver? = null

  @CheckResult
  private fun hideDataPolicy(): Boolean {
    // Data Policy is only supported when Activity is PYDroidActivity, hide it otherwise
    return requireActivity() !is PYDroidActivity
  }

  private fun openPage(handler: UriHandler, url: String) {
    val vm = viewModel.requireNotNull()

    try {
      vm.handleClearNavigationError()
      handler.openUri(url)
    } catch (e: Throwable) {
      vm.handleNavigationFailed(e)
    }
  }

  private fun handleChangeDarkMode(mode: Theming.Mode) {
    val act = requireActivity()
    viewModel
        .requireNotNull()
        .handleChangeDarkMode(
            scope = act.lifecycleScope,
            mode = mode,
        )
  }

  private fun handleViewBlog(handler: UriHandler) {
    viewModel.requireNotNull().handleViewBlog { url ->
      openPage(
          handler = handler,
          url = url,
      )
    }
  }

  private fun handleViewSocialMedia(handler: UriHandler) {
    viewModel.requireNotNull().handleViewSocialMedia { url ->
      openPage(
          handler = handler,
          url = url,
      )
    }
  }

  private fun handleViewTermsOfService(handler: UriHandler) {
    viewModel.requireNotNull().handleViewTermsOfService { url ->
      openPage(
          handler = handler,
          url = url,
      )
    }
  }

  private fun handleViewPrivacyPolicy(handler: UriHandler) {
    viewModel.requireNotNull().handleViewPrivacyPolicy { url ->
      openPage(
          handler = handler,
          url = url,
      )
    }
  }

  private fun handleViewSourceCode(handler: UriHandler) {
    viewModel.requireNotNull().handleViewSourceCode { url ->
      openPage(
          handler = handler,
          url = url,
      )
    }
  }

  private fun handleReportBug(handler: UriHandler) {
    viewModel.requireNotNull().handleReportBug { url ->
      openPage(
          handler = handler,
          url = url,
      )
    }
  }

  private fun handleViewMoreApps() {
    viewModel
        .requireNotNull()
        .handleViewMoreApps(
            onOpenDeveloperPage = { handleOpenDeveloperPage() },
            onOpenOtherApps = { OtherAppsDialog.show(requireActivity()) },
        )
  }

  private fun handleShowDisclosure() {
    dataPolicyViewModel
        .requireNotNull()
        .handleShowDisclosure(
            scope = viewLifecycleOwner.lifecycleScope,
            force = true,
            onShowPolicy = { DataPolicyDisclosureDialog.show(requireActivity()) },
        )
  }

  private fun handleViewMarketPage() {
    ratingViewModel
        .requireNotNull()
        .handleViewMarketPage(
            scope = viewLifecycleOwner.lifecycleScope,
            onLauchMarketPage = { launcher ->
              val act = requireActivity()
              act.lifecycleScope.launch(context = Dispatchers.Main) {
                launcher.rate(act).onFailure {
                  Logger.e(it, "Unable to show Market page from settings")
                }
              }
            },
        )
  }

  private fun handleShowChangeLog() {
    changeLogViewModel
        .requireNotNull()
        .handleShow(
            scope = viewLifecycleOwner.lifecycleScope,
            force = true,
            onShowChangeLog = { ChangeLogDialog.open(requireActivity()) },
        )
  }

  private fun handleCheckForUpdates() {
    versionViewModel
        .requireNotNull()
        .handleCheckForUpdates(
            scope = viewLifecycleOwner.lifecycleScope,
            force = true,
            onLaunchUpdate = { isFallback, launcher ->
              showVersionUpgrade(
                  activity = requireActivity(),
                  isFallbackEnabled = isFallback,
                  launcher = launcher,
              )
            })
  }

  private fun showVersionUpgrade(
      activity: FragmentActivity,
      isFallbackEnabled: Boolean,
      launcher: AppUpdateLauncher
  ) {
    // Enforce that we do this on the Main thread
    activity.lifecycleScope.launch(context = Dispatchers.Main) {
      launcher.update(activity, RC_APP_UPDATE).onFailure { err ->
        Logger.e(err, "Unable to launch in-app update flow")
        if (isFallbackEnabled) {
          val vm = viewModel.requireNotNull()
          MarketLinker.linkToMarketPage(activity)
              .onSuccess { vm.handleClearNavigationError() }
              .onFailure { vm.handleNavigationFailed(it) }
        }
      }
    }
  }

  private fun handleOpenDeveloperPage() {
    val vm = viewModel.requireNotNull()
    MarketLinker.linkToDeveloperPage(requireContext())
        .onSuccess { vm.handleClearNavigationError() }
        .onFailure { Logger.e(it, "Failed to navigate to market page") }
        .onFailure { vm.handleNavigationFailed(it) }
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

  final override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View {
    val act = requireActivity()

    Injector.obtainFromActivity<AppComponent>(act).plusSettings().create().inject(this)

    return ComposeView(act).apply {
      id = R.id.fragment_settings

      val observer = ViewWindowInsetObserver(this)
      val windowInsets = observer.start()
      windowInsetObserver = observer

      val vm = viewModel.requireNotNull()
      setContent {
        val handler = LocalUriHandler.current

        vm.Render { state ->
          composeTheme(act) {
            CompositionLocalProvider(LocalWindowInsets provides windowInsets) {
              SettingsScreen(
                  state = state,
                  hideClearAll = hideClearAll,
                  hideUpgradeInformation = hideUpgradeInformation,
                  hideDataPolicy = hideDataPolicy(),
                  topItemMargin = customTopItemMargin(),
                  bottomItemMargin = customBottomItemMargin(),
                  customPreContent = customPrePreferences(),
                  customPostContent = customPostPreferences(),
                  onDarkModeChanged = { handleChangeDarkMode(it) },
                  onLicensesClicked = { AboutDialog.show(act) },
                  onCheckUpdateClicked = { handleCheckForUpdates() },
                  onShowChangeLogClicked = { handleShowChangeLog() },
                  onResetClicked = { ResetDialog.open(act) },
                  onRateClicked = { handleViewMarketPage() },
                  onDonateClicked = { BillingDialog.open(act) },
                  onBugReportClicked = { handleReportBug(handler) },
                  onViewSourceClicked = { handleViewSourceCode(handler) },
                  onViewDataPolicyClicked = { handleShowDisclosure() },
                  onViewPrivacyPolicyClicked = { handleViewPrivacyPolicy(handler) },
                  onViewTermsOfServiceClicked = { handleViewTermsOfService(handler) },
                  onViewMoreAppsClicked = { handleViewMoreApps() },
                  onViewSocialMediaClicked = { handleViewSocialMedia(handler) },
                  onViewBlogClicked = { handleViewBlog(handler) },
                  onNavigationErrorDismissed = { vm.handleClearNavigationError() },
              )
            }
          }
        }
      }
    }
  }

  @CallSuper
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    ratingViewModel.requireNotNull().restoreState(savedInstanceState)
    dataPolicyViewModel.requireNotNull().restoreState(savedInstanceState)
    changeLogViewModel.requireNotNull().restoreState(savedInstanceState)
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
    ratingViewModel?.saveState(outState)
    dataPolicyViewModel?.saveState(outState)
    changeLogViewModel?.saveState(outState)
    versionViewModel?.saveState(outState)
  }

  @CallSuper
  override fun onConfigurationChanged(newConfig: Configuration) {
    super.onConfigurationChanged(newConfig)
    recompose()
  }

  @CallSuper
  override fun onDestroyView() {
    super.onDestroyView()
    (view as? ComposeView)?.disposeComposition()
    viewModel = null
    changeLogViewModel = null
    dataPolicyViewModel = null
    ratingViewModel = null
    versionViewModel = null

    windowInsetObserver?.stop()
    windowInsetObserver = null
  }

  public companion object {

    // Only bottom 16 bits.
    private const val RC_APP_UPDATE = 146
  }
}
