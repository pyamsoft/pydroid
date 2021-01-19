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

package com.pyamsoft.pydroid.ui.internal.settings

import android.app.Activity
import androidx.lifecycle.viewModelScope
import com.pyamsoft.highlander.highlander
import com.pyamsoft.pydroid.arch.UiViewModel
import com.pyamsoft.pydroid.bootstrap.otherapps.OtherAppsInteractor
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.pydroid.ui.theme.toMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class AppSettingsViewModel internal constructor(
    private val theming: Theming,
    interactor: OtherAppsInteractor,
) : UiViewModel<AppSettingsViewState, AppSettingsViewEvent, AppSettingsControllerEvent>(
    initialState = AppSettingsViewState(
        applicationName = "",
        isDarkTheme = null,
        throwable = null,
        otherApps = emptyList()
    ),
) {

    private val otherAppsRunner = highlander<Unit, Boolean> { force ->
        interactor.getApps(force)
            .onSuccess { setState { copy(otherApps = it) } }
            .onFailure { setState { copy(otherApps = emptyList()) } }
    }

    init {
        viewModelScope.launch(context = Dispatchers.Default) {
            otherAppsRunner.call(false)
        }

        viewModelScope.launch(context = Dispatchers.Default) {
            val name = interactor.getDisplayName()
            setState { copy(applicationName = name) }
        }
    }

    override fun handleViewEvent(event: AppSettingsViewEvent) = when (event) {
        is AppSettingsViewEvent.MoreApps -> seeMoreApps()
        is AppSettingsViewEvent.RateApp -> publish(AppSettingsControllerEvent.NavigateRateApp)
        is AppSettingsViewEvent.ViewLicense -> publish(AppSettingsControllerEvent.ShowLicense)
        is AppSettingsViewEvent.CheckUpgrade -> publish(AppSettingsControllerEvent.CheckUpgrade)
        is AppSettingsViewEvent.ClearData -> publish(AppSettingsControllerEvent.AttemptClearData)
        is AppSettingsViewEvent.ShowUpgrade -> publish(AppSettingsControllerEvent.OpenShowUpgrade)
        is AppSettingsViewEvent.ToggleDarkTheme -> changeDarkMode(event.mode)
        is AppSettingsViewEvent.Hyperlink -> publish(AppSettingsControllerEvent.Navigate(event.hyperlinkIntent))
        is AppSettingsViewEvent.ShowDonate -> publish(AppSettingsControllerEvent.OpenDonation)
    }

    private fun seeMoreApps() {
        state.otherApps.let { others ->
            if (others.isEmpty()) {
                publish(AppSettingsControllerEvent.NavigateMoreApps)
            } else {
                publish(AppSettingsControllerEvent.OpenOtherAppsPage(others))
            }
        }
    }

    internal fun syncDarkThemeState(activity: Activity) {
        setState {
            copy(isDarkTheme = AppSettingsViewState.DarkTheme(theming.isDarkTheme(activity)))
        }
    }

    private fun changeDarkMode(mode: String) {
        val newMode = mode.toMode()

        viewModelScope.launch(context = Dispatchers.Main) {
            theming.setDarkTheme(newMode)
            publish(AppSettingsControllerEvent.ChangeDarkTheme(newMode))
        }
    }

    internal fun navigationFailed(error: Throwable) {
        setState { copy(throwable = error) }
    }

    internal fun navigationSuccess() {
        setState { copy(throwable = null) }
    }
}
