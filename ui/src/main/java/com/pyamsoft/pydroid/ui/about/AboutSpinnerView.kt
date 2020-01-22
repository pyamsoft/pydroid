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

package com.pyamsoft.pydroid.ui.about

import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.view.isVisible
import com.pyamsoft.pydroid.arch.BaseUiView
import com.pyamsoft.pydroid.ui.R

internal class AboutSpinnerView internal constructor(
    parent: ViewGroup
) : BaseUiView<AboutViewState, AboutViewEvent>(parent) {

    override val layout: Int = R.layout.loading_spinner

    override val layoutRoot by boundView<View>(R.id.spinner_root)

    private val spinner by boundView<ProgressBar>(R.id.spinner)

    init {
        doOnTeardown {
            spinner.isVisible = false
        }
    }

    override fun onRender(state: AboutViewState) {
        state.isLoading.let { loading ->
            spinner.isVisible = loading
        }
    }
}
