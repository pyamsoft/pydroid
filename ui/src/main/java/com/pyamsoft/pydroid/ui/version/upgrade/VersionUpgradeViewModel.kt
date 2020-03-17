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

package com.pyamsoft.pydroid.ui.version.upgrade

import android.content.ActivityNotFoundException
import com.pyamsoft.pydroid.arch.UiViewModel
import com.pyamsoft.pydroid.ui.version.upgrade.VersionUpgradeControllerEvent.CancelDialog
import com.pyamsoft.pydroid.ui.version.upgrade.VersionUpgradeControllerEvent.OpenMarket
import com.pyamsoft.pydroid.ui.version.upgrade.VersionUpgradeViewEvent.Cancel
import com.pyamsoft.pydroid.ui.version.upgrade.VersionUpgradeViewEvent.Upgrade

internal class VersionUpgradeViewModel internal constructor(
    applicationName: CharSequence,
    currentVersion: Int,
    debug: Boolean
) : UiViewModel<VersionUpgradeViewState, VersionUpgradeViewEvent, VersionUpgradeControllerEvent>(
    initialState = VersionUpgradeViewState(
        throwable = null,
        applicationName = applicationName,
        currentVersion = currentVersion,
        newVersion = 0
    ), debug = debug
) {

    override fun handleViewEvent(event: VersionUpgradeViewEvent) {
        return when (event) {
            Upgrade -> publish(OpenMarket)
            Cancel -> publish(CancelDialog)
        }
    }

    fun initialize(newVersion: Int) {
        this.setState { copy(newVersion = newVersion) }
    }

    fun navigationFailed(error: ActivityNotFoundException) {
        setState { copy(throwable = error) }
    }

    fun navigationSuccess() {
        setState { copy(throwable = null) }
    }
}
