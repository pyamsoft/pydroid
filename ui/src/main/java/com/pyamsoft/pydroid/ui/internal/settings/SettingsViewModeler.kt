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

package com.pyamsoft.pydroid.ui.internal.settings

import androidx.compose.runtime.saveable.SaveableStateRegistry
import com.pyamsoft.pydroid.arch.AbstractViewModeler
import com.pyamsoft.pydroid.bootstrap.changelog.ChangeLogInteractor
import com.pyamsoft.pydroid.ui.theme.Theming
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

internal class SettingsViewModeler
internal constructor(
    override val state: MutableSettingsViewState,
    private val bugReportUrl: String,
    private val viewSourceUrl: String,
    private val privacyPolicyUrl: String,
    private val termsConditionsUrl: String,
    private val theming: Theming,
    private val changeLogInteractor: ChangeLogInteractor,
) : AbstractViewModeler<SettingsViewState>(state) {

  override fun registerSaveState(
      registry: SaveableStateRegistry
  ): List<SaveableStateRegistry.Entry> =
      mutableListOf<SaveableStateRegistry.Entry>().apply {
        registry
            .registerProvider(KEY_SHOW_ABOUT_DIALOG) { state.isShowingAboutDialog.value }
            .also { add(it) }
        registry
            .registerProvider(KEY_SHOW_RESET_DIALOG) { state.isShowingResetDialog.value }
            .also { add(it) }
        registry
            .registerProvider(KEY_SHOW_DATA_POLICY_DIALOG) { state.isShowingDataPolicyDialog.value }
            .also { add(it) }
      }

  override fun consumeRestoredState(registry: SaveableStateRegistry) {
    registry
        .consumeRestored(KEY_SHOW_ABOUT_DIALOG)
        ?.let { it as Boolean }
        ?.also { state.isShowingAboutDialog.value = it }

    registry
        .consumeRestored(KEY_SHOW_RESET_DIALOG)
        ?.let { it as Boolean }
        ?.also { state.isShowingResetDialog.value = it }

    registry
        .consumeRestored(KEY_SHOW_DATA_POLICY_DIALOG)
        ?.let { it as Boolean }
        ?.also { state.isShowingDataPolicyDialog.value = it }
  }

  internal fun bind(scope: CoroutineScope) {
    scope.launch(context = Dispatchers.Main) {
      val name = changeLogInteractor.getDisplayName()
      state.applicationName.value = name
    }
  }

  internal fun handleLoadPreferences(scope: CoroutineScope) {
    val s = state
    s.loadingState.value = SettingsViewState.LoadingState.LOADING

    scope.launch(context = Dispatchers.Main) {
      theming.listenForModeChanges().collectLatest {
        s.darkMode.value = it

        // Upon sync, mark loaded
        if (s.loadingState.value == SettingsViewState.LoadingState.LOADING) {
          s.loadingState.value = SettingsViewState.LoadingState.DONE
        }
      }
    }
  }

  internal fun handleChangeDarkMode(
      scope: CoroutineScope,
      mode: Theming.Mode,
  ) {
    scope.launch(context = Dispatchers.Main) {
      state.darkMode.value = mode
      theming.setDarkTheme(mode)
    }
  }

  internal fun handleOpenDataPolicyDialog() {
    state.isShowingDataPolicyDialog.value = true
  }

  internal fun handleCloseDataPolicyDialog() {
    state.isShowingDataPolicyDialog.value = false
  }

  internal fun handleOpenAboutDialog() {
    state.isShowingAboutDialog.value = true
  }

  internal fun handleCloseAboutDialog() {
    state.isShowingAboutDialog.value = false
  }

  internal fun handleOpenResetDialog() {
    state.isShowingResetDialog.value = true
  }

  internal fun handleCloseResetDialog() {
    state.isShowingResetDialog.value = false
  }

  internal fun handleViewSocialMedia(
      onOpenUrl: (String) -> Unit,
  ) {
    onOpenUrl(FACEBOOK)
  }

  internal fun handleViewBlog(onOpenUrl: (String) -> Unit) {
    onOpenUrl(BLOG)
  }

  internal fun handleViewTermsOfService(onOpenUrl: (String) -> Unit) {
    onOpenUrl(termsConditionsUrl)
  }

  internal fun handleViewPrivacyPolicy(onOpenUrl: (String) -> Unit) {
    onOpenUrl(privacyPolicyUrl)
  }

  internal fun handleViewSourceCode(onOpenUrl: (String) -> Unit) {
    onOpenUrl(viewSourceUrl)
  }

  internal fun handleReportBug(onOpenUrl: (String) -> Unit) {
    onOpenUrl(bugReportUrl)
  }

  companion object {

    private const val FACEBOOK = "https://www.facebook.com/pyamsoftware"
    private const val BLOG = "https://pyamsoft.blogspot.com/"

    private const val KEY_SHOW_ABOUT_DIALOG = "settings_show_about_dialog"
    private const val KEY_SHOW_RESET_DIALOG = "settings_show_reset_dialog"
    private const val KEY_SHOW_DATA_POLICY_DIALOG = "settings_show_data_policy_dialog"
  }
}
