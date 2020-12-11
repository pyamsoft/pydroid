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

package com.pyamsoft.pydroid.ui.internal.version

import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.arch.UiRender
import com.pyamsoft.pydroid.arch.UiView
import com.pyamsoft.pydroid.bootstrap.version.AppUpdateLauncher
import com.pyamsoft.pydroid.ui.util.Snackbreak

internal class VersionCheckView internal constructor(
    private val owner: LifecycleOwner,
    private val snackbarRootProvider: () -> ViewGroup
) : UiView<VersionCheckViewState, VersionCheckViewEvent>() {

    override fun render(state: UiRender<VersionCheckViewState>) {
        state.distinctBy { it.isLoading }.render(viewScope) { handleLoading(it) }
        state.distinctBy { it.throwable }.render(viewScope) { handleError(it) }
        state.distinctBy { it.updater }.render(viewScope) { handleUpdater(it) }
    }

    private fun handleLoading(loading: Boolean) {
        if (loading) {
            showUpdating()
        } else {
            dismissUpdating()
        }
    }

    private fun handleUpdater(launcher: AppUpdateLauncher?) {
        if (launcher == null) {
            clearUpdater()
        } else {
            if (launcher.canUpdate()) {
                showUpdater(launcher)
            } else {
                clearUpdater()
            }
        }
    }

    private fun handleError(throwable: Throwable?) {
        if (throwable == null) {
            clearError()
        } else {
            showError(throwable)
        }
    }

    private fun showUpdating() {
        Snackbreak.bindTo(owner) {
            make(snackbarRootProvider(), "Checking for updates")
        }
    }

    private fun dismissUpdating() {
        Snackbreak.bindTo(owner) {
            dismiss()
        }
    }

    private fun showError(throwable: Throwable) {
        Snackbreak.bindTo(owner) {
            short(
                snackbarRootProvider(),
                throwable.message ?: "An error occurred while checking for updates.",
                onHidden = { _, _ -> publish(VersionCheckViewEvent.SnackbarHidden) }
            )
        }
    }

    private fun clearError() {
        Snackbreak.bindTo(owner) {
            dismiss()
        }
    }

    private fun showUpdater(launcher: AppUpdateLauncher) {
        Snackbreak.bindTo(owner) {
            make(snackbarRootProvider(), "A new update is available!") {
                setAction("Update") { publish(VersionCheckViewEvent.LaunchUpdate(launcher)) }
            }
        }
    }

    private fun clearUpdater() {
        Snackbreak.bindTo(owner) {
            dismiss()
        }
    }
}
