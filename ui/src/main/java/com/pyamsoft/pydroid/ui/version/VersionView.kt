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

package com.pyamsoft.pydroid.ui.version

import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.arch.UiBundleReader
import com.pyamsoft.pydroid.arch.UiView
import com.pyamsoft.pydroid.ui.util.Snackbreak
import com.pyamsoft.pydroid.ui.version.VersionViewEvent.SnackbarHidden

internal class VersionView internal constructor(
    private val owner: LifecycleOwner,
    private val snackbarRootProvider: () -> ViewGroup
) : UiView<VersionViewState, VersionViewEvent>() {

    override fun onInit(savedInstanceState: UiBundleReader) {
    }

    override fun render(state: VersionViewState) {
        handleLoading(state)
        handleError(state)
        handleUpdateAvailable(state)
    }

    private fun handleLoading(state: VersionViewState) {
        state.isLoading.let { loading ->
            if (loading) {
                showUpdating()
            } else {
                dismissUpdating()
            }
        }
    }

    private fun handleError(state: VersionViewState) {
        state.throwable.let { throwable ->
            if (throwable == null) {
                clearError()
            } else {
                showError(throwable)
            }
        }
    }

    private fun handleUpdateAvailable(state: VersionViewState) {
        state.isUpdateAvailable.let { isUpdateAvailable ->
            if (isUpdateAvailable) {
                showUpdatePrompt()
            } else {
                clearUpdatePrompt()
            }
        }
    }

    private fun showUpdatePrompt() {
        Snackbreak.bindTo(owner, "update_restart") {
            make(snackbarRootProvider(), "A new update is ready!") {
                setAction("RESTART") { publish(VersionViewEvent.UpdateRestart) }
            }
        }
    }

    private fun clearUpdatePrompt() {
        Snackbreak.bindTo(owner, "update_restart") {
            dismiss()
        }
    }

    private fun showUpdating() {
        Snackbreak.bindTo(owner, "check_update") {
            make(snackbarRootProvider(), "Checking for updates")
        }
    }

    private fun dismissUpdating() {
        Snackbreak.bindTo(owner, "check_update") {
            dismiss()
        }
    }

    private fun showError(throwable: Throwable) {
        Snackbreak.bindTo(owner, "error") {
            short(
                snackbarRootProvider(),
                throwable.message ?: "An error occurred while checking for updates.",
                onHidden = { _, _ -> publish(SnackbarHidden) }
            )
        }
    }

    private fun clearError() {
        Snackbreak.bindTo(owner, "error") {
            dismiss()
        }
    }
}
