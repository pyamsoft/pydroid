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

import com.pyamsoft.pydroid.arch.AbstractViewModeler
import com.pyamsoft.pydroid.bootstrap.changelog.ChangeLogInteractor
import com.pyamsoft.pydroid.ui.theme.Theming
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

internal class SettingsViewModeler
internal constructor(
    private val state: MutableSettingsViewState,
    private val bugReportUrl: String,
    private val viewSourceUrl: String,
    private val privacyPolicyUrl: String,
    private val termsConditionsUrl: String,
    private val theming: Theming,
    private val changeLogInteractor: ChangeLogInteractor,
) : AbstractViewModeler<SettingsViewState>(state) {

  internal fun bind(scope: CoroutineScope) {
    scope.launch(context = Dispatchers.Main) {
      val name = changeLogInteractor.getDisplayName()
      state.applicationName = name
    }
  }

  internal fun handleLoadPreferences(scope: CoroutineScope) {
    val s = state
    s.loadingState = SettingsViewState.LoadingState.LOADING

    scope.launch(context = Dispatchers.Main) {
      theming.listenForModeChanges().collectLatest {
        s.darkMode = it

        // Upon sync, mark loaded
        if (s.loadingState == SettingsViewState.LoadingState.LOADING) {
          s.loadingState = SettingsViewState.LoadingState.DONE
        }
      }
    }
  }

  internal fun handleChangeDarkMode(
      scope: CoroutineScope,
      mode: Theming.Mode,
  ) {
    scope.launch(context = Dispatchers.Main) {
      state.darkMode = mode
      theming.setDarkTheme(mode)
    }
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
  }
}
