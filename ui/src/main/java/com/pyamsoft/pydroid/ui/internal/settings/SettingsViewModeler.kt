/*
 * Copyright 2025 pyamsoft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
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
import com.pyamsoft.pydroid.core.cast
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.ui.haptics.HapticPreferences
import com.pyamsoft.pydroid.ui.internal.debug.DebugPreferences
import com.pyamsoft.pydroid.ui.theme.Theming
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.updateAndGet
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
    private val debugPreferences: DebugPreferences,
    private val hapticPreferences: HapticPreferences,
) : SettingsViewState by state, AbstractViewModeler<SettingsViewState>(state) {

  private data class LoadConfig(
      var name: Boolean = false,
      var inAppDebug: Boolean = false,
      var darkMode: Boolean = false,
      var isHapticsEnabled: Boolean = false,
  )

  private fun markConfigLoaded(loadConfig: LoadConfig) {
    if (
        loadConfig.darkMode &&
            loadConfig.inAppDebug &&
            loadConfig.name &&
            loadConfig.isHapticsEnabled
    ) {
      state.loadingState.value = SettingsViewState.LoadingState.DONE
    }
  }

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
        registry
            .registerProvider(KEY_SHOW_IN_APP_DEBUG_DIALOG) {
              state.isShowingInAppDebugDialog.value
            }
            .also { add(it) }
      }

  override fun consumeRestoredState(registry: SaveableStateRegistry) {
    registry.consumeRestored(KEY_SHOW_ABOUT_DIALOG)?.cast<Boolean>()?.also {
      state.isShowingAboutDialog.value = it
    }
    registry.consumeRestored(KEY_SHOW_RESET_DIALOG)?.cast<Boolean>()?.also {
      state.isShowingResetDialog.value = it
    }
    registry.consumeRestored(KEY_SHOW_DATA_POLICY_DIALOG)?.cast<Boolean>()?.also {
      state.isShowingDataPolicyDialog.value = it
    }
    registry.consumeRestored(KEY_SHOW_IN_APP_DEBUG_DIALOG)?.cast<Boolean>()?.also {
      state.isInAppDebuggingEnabled.value = it
    }
  }

  internal fun bind(scope: CoroutineScope) {
    val s = state

    // Create a config to mark which bits are loaded
    val config = LoadConfig()
    s.loadingState.value = SettingsViewState.LoadingState.LOADING

    scope.launch(context = Dispatchers.Default) {
      val name = changeLogInteractor.getDisplayName()
      s.applicationName.value = name

      if (!config.name) {
        config.name = true
        markConfigLoaded(config)
      }
    }

    scope.launch(context = Dispatchers.Default) {
      debugPreferences.listenForInAppDebuggingEnabled().collect { enabled ->
        s.isInAppDebuggingEnabled.value = enabled

        if (!config.inAppDebug) {
          config.inAppDebug = true
          markConfigLoaded(config)
        }
      }
    }

    scope.launch(context = Dispatchers.Default) {
      hapticPreferences.listenForHapticsChanges().collect {
        s.isHapticsEnabled.value = it

        if (!config.isHapticsEnabled) {
          config.isHapticsEnabled = true
          markConfigLoaded(config)
        }
      }
    }

    combineTransform(
            theming.listenForModeChanges(),
            theming.listenForMaterialYouChanges(),
        ) { mode, isMaterialYou ->
          emit(listOf(mode, isMaterialYou))
        }
        .flowOn(context = Dispatchers.Default)
        .also { f ->
          scope.launch(context = Dispatchers.Default) {
            f.collect { list ->
              if (!config.darkMode) {
                config.darkMode = true
                markConfigLoaded(config)
              }

              val mode = list[0].cast<Theming.Mode>().requireNotNull()
              val isMaterialYou = list[1].cast<Boolean>().requireNotNull()

              state.darkMode.value = mode
              state.isMaterialYou.value = isMaterialYou
            }
          }
        }
  }

  internal fun handleChangeInAppDebugEnabled() {
    val newEnabled = state.isInAppDebuggingEnabled.updateAndGet { !it }
    debugPreferences.setInAppDebuggingEnabled(newEnabled)
  }

  internal fun handleChangeDarkMode(
      scope: CoroutineScope,
      mode: Theming.Mode,
  ) {
    theming.setDarkTheme(scope, mode)
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

  internal fun handleViewSocialMedia(onOpenUrl: (String) -> Unit) {
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

  fun handleCloseInAppDebuggingDialog() {
    state.isShowingInAppDebugDialog.value = false
  }

  fun handleOpenInAppDebuggingDialog() {
    state.isShowingInAppDebugDialog.value = true
  }

  fun handleHapticsChanged(enabled: Boolean) {
    if (enabled) {
      hapticPreferences.enableHaptics()
    } else {
      hapticPreferences.disableHaptics()
    }
  }

  fun handleMaterialYouChange(enabled: Boolean) {
    theming.setMaterialYou(enabled)
  }

  companion object {

    private const val FACEBOOK = "https://www.facebook.com/pyamsoftware"
    private const val BLOG = "https://pyamsoft.blogspot.com/"

    private const val KEY_SHOW_ABOUT_DIALOG = "settings_show_about_dialog"
    private const val KEY_SHOW_RESET_DIALOG = "settings_show_reset_dialog"
    private const val KEY_SHOW_DATA_POLICY_DIALOG = "settings_show_data_policy_dialog"
    private const val KEY_SHOW_IN_APP_DEBUG_DIALOG = "settings_show_in_app_debug_dialog"
  }
}
