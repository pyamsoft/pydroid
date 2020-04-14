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

package com.pyamsoft.pydroid.ui.privacy

import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.arch.UiBundleReader
import com.pyamsoft.pydroid.arch.UiView
import com.pyamsoft.pydroid.ui.privacy.PrivacyViewEvent.SnackbarHidden
import com.pyamsoft.pydroid.ui.util.Snackbreak

internal class PrivacyView internal constructor(
    private val owner: LifecycleOwner,
    private val snackbarRootProvider: () -> ViewGroup
) : UiView<PrivacyViewState, PrivacyViewEvent>() {

    override fun onInit(savedInstanceState: UiBundleReader) {
    }

    override fun render(state: PrivacyViewState) {
        state.throwable.let { throwable ->
            if (throwable == null) {
                clearError()
            } else {
                showError(throwable)
            }
        }
    }

    private fun showError(throwable: Throwable) {
        Snackbreak.bindTo(owner, "error") {
            short(snackbarRootProvider(),
                throwable.message ?: "An error occurred while showing policy.",
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
