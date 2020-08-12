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

import android.app.Activity
import android.content.ActivityNotFoundException
import androidx.lifecycle.viewModelScope
import com.pyamsoft.highlander.highlander
import com.pyamsoft.pydroid.arch.UiViewModel
import com.pyamsoft.pydroid.arch.onActualError
import com.pyamsoft.pydroid.bootstrap.otherapps.OtherAppsInteractor
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.pydroid.ui.theme.toMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

internal class AppSettingsViewModel internal constructor(
    private val theming: Theming,
    interactor: OtherAppsInteractor,
    debug: Boolean
) : UiViewModel<AppSettingsViewState, AppSettingsViewEvent, AppSettingsControllerEvent>(
    initialState = AppSettingsViewState(
        isDarkTheme = null,
        throwable = null,
        otherApps = emptyList()
    ),
    debug = debug
) {

    private val otherAppsRunner = highlander<Unit, Boolean> { force ->
        try {
            val otherApps = interactor.getApps(force)
            setState { copy(otherApps = otherApps) }
        } catch (error: Throwable) {
            error.onActualError { e ->
                Timber.e(e, "Error checking for other apps")
            }
        }
    }

    init {
        doOnInit {
            viewModelScope.launch(context = Dispatchers.Default) { otherAppsRunner.call(false) }
        }
    }

    override fun handleViewEvent(event: AppSettingsViewEvent) {
        return when (event) {
            is AppSettingsViewEvent.MoreApps -> seeMoreApps()
            is AppSettingsViewEvent.Hyperlink -> publish(
                AppSettingsControllerEvent.NavigateHyperlink(
                    event.hyperlinkIntent
                )
            )
            is AppSettingsViewEvent.RateApp -> publish(AppSettingsControllerEvent.NavigateRateApp)
            is AppSettingsViewEvent.ViewLicense -> publish(AppSettingsControllerEvent.ShowLicense)
            is AppSettingsViewEvent.CheckUpgrade -> publish(AppSettingsControllerEvent.CheckUpgrade)
            is AppSettingsViewEvent.ClearData -> publish(AppSettingsControllerEvent.AttemptClearData)
            is AppSettingsViewEvent.ShowUpgrade -> publish(AppSettingsControllerEvent.OpenShowUpgrade)
            is AppSettingsViewEvent.ToggleDarkTheme -> changeDarkMode(event.mode)
        }
    }

    private fun seeMoreApps() {
        withState {
            otherApps.let { others ->
                if (others.isEmpty()) {
                    publish(AppSettingsControllerEvent.NavigateMoreApps)
                } else {
                    publish(AppSettingsControllerEvent.OpenOtherAppsPage(others))
                }
            }
        }
    }

    fun syncDarkThemeState(activity: Activity) {
        setState { copy(isDarkTheme = AppSettingsViewState.DarkTheme(theming.isDarkTheme(activity))) }
    }

    private fun changeDarkMode(mode: String) {
        theming.setDarkTheme(mode.toMode()) {
            publish(AppSettingsControllerEvent.ChangeDarkTheme(it))
        }
    }

    fun navigationFailed(error: ActivityNotFoundException) {
        setState { copy(throwable = error) }
    }

    fun navigationSuccess() {
        setState { copy(throwable = null) }
    }
}
