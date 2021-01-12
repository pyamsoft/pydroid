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
import com.pyamsoft.pydroid.ui.util.Snackbreak

internal class VersionCheckView internal constructor(
    private val owner: LifecycleOwner,
    private val snackbarRootProvider: () -> ViewGroup
) : UiView<VersionCheckViewState, VersionCheckViewEvent>() {

    override fun render(state: UiRender<VersionCheckViewState>) {
        state.mapChanged { it.isLoading }.render(viewScope) { handleLoading(it) }
        state.mapChanged { it.throwable }.render(viewScope) { handleUpdateError(it) }
        state.mapChanged { it.navigationError }.render(viewScope) { handleNavigationError(it) }
    }

    private fun handleLoading(loading: Boolean) {
        if (loading) {
            showUpdating()
        }
    }

    private fun handleUpdateError(throwable: Throwable?) {
        if (throwable != null) {
            showUpdatingError(throwable)
        }
    }

    private fun handleNavigationError(throwable: Throwable?) {
        if (throwable != null) {
            showNavigationError(throwable)
        }
    }

    private fun showUpdating() {
        Snackbreak.bindTo(owner) {
            long(
                snackbarRootProvider(),
                "Checking for updates",
                onHidden = { _, _ -> publish(VersionCheckViewEvent.LoadingHidden) }
            )
        }
    }

    private fun showUpdatingError(throwable: Throwable) {
        Snackbreak.bindTo(owner) {
            long(
                snackbarRootProvider(),
                throwable.message ?: "An error occurred while checking for updates.",
                onHidden = { _, _ -> publish(VersionCheckViewEvent.ErrorHidden) }
            )
        }
    }

    private fun showNavigationError(throwable: Throwable) {
        Snackbreak.bindTo(owner) {
            long(
                snackbarRootProvider(),
                throwable.message ?: "An unexpected error occurred.",
                onHidden = { _, _ -> publish(VersionCheckViewEvent.NavigationHidden) }
            )
        }
    }
}
