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

package com.pyamsoft.pydroid.ui.about.listitem

import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.pyamsoft.pydroid.arch.BaseUiView
import com.pyamsoft.pydroid.arch.UiSavedState
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.about.listitem.AboutItemViewEvent.OpenUrl
import com.pyamsoft.pydroid.ui.util.setOnDebouncedClickListener

internal class AboutItemActionView internal constructor(
    parent: ViewGroup
) : BaseUiView<AboutItemViewState, AboutItemViewEvent>(parent) {

    private val viewLicense by boundView<Button>(R.id.action_view_license)
    private val visitHomepage by boundView<Button>(R.id.action_visit_homepage)

    override val layout: Int = R.layout.about_item_actions

    override val layoutRoot by boundView<View>(R.id.about_actions)

    init {
        doOnTeardown {
            clear()
        }
    }

    private fun clear() {
        viewLicense.setOnDebouncedClickListener(null)
        visitHomepage.setOnDebouncedClickListener(null)
    }

    override fun onRender(
        state: AboutItemViewState,
        savedState: UiSavedState
    ) {
        state.library.let { library ->
            if (library == null) {
                clear()
            } else {
                viewLicense.setOnDebouncedClickListener {
                    publish(OpenUrl(library.licenseUrl))
                }

                visitHomepage.setOnDebouncedClickListener {
                    publish(OpenUrl(library.libraryUrl))
                }
            }
        }
    }
}
