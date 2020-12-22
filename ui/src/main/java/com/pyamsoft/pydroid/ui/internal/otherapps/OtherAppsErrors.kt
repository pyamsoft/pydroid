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

package com.pyamsoft.pydroid.ui.internal.otherapps

import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.arch.UiRender
import com.pyamsoft.pydroid.arch.UiView
import com.pyamsoft.pydroid.ui.util.Snackbreak

internal class OtherAppsErrors internal constructor(
    private val owner: LifecycleOwner,
    private val parent: ViewGroup
) : UiView<OtherAppsViewState, OtherAppsViewEvent.ErrorEvent>() {

    override fun render(state: UiRender<OtherAppsViewState>) {
        state.distinctBy { it.navigationError }.render(viewScope) { handleNavigationError(it) }
        state.distinctBy { it.appsError }.render(viewScope) { handleAppsError(it) }
    }

    private fun handleAppsError(throwable: Throwable?) {
        if (throwable != null) {
            showAppsError(throwable)
        }
    }

    private fun handleNavigationError(throwable: Throwable?) {
        if (throwable != null) {
            showNavigationError(throwable)
        }
    }

    private fun showAppsError(throwable: Throwable) {
        Snackbreak.bindTo(owner) {
            short(
                parent,
                throwable.message ?: "An unexpected error occurred.",
                onHidden = { _, _ -> publish(OtherAppsViewEvent.ErrorEvent.HideAppsError) }
            ) {
                setAction("Go to Store") {
                    publish(OtherAppsViewEvent.ErrorEvent.HideAppsError)
                }
            }
        }
    }

    private fun showNavigationError(error: Throwable) {
        Snackbreak.bindTo(owner) {
            long(
                parent,
                error.message ?: "An unexpected error occurred.",
                onHidden = { _, _ -> publish(OtherAppsViewEvent.ErrorEvent.HideNavigationError) }
            )
        }
    }

}
