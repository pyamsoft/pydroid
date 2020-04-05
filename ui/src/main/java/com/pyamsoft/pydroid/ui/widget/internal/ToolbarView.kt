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

package com.pyamsoft.pydroid.ui.widget.internal

import com.pyamsoft.pydroid.arch.UiBundleReader
import com.pyamsoft.pydroid.arch.UiView
import com.pyamsoft.pydroid.arch.UiViewEvent
import com.pyamsoft.pydroid.arch.UiViewState
import com.pyamsoft.pydroid.ui.app.ToolbarActivity
import com.pyamsoft.pydroid.ui.arch.InvalidIdException
import com.pyamsoft.pydroid.ui.util.DebouncedOnClickListener
import com.pyamsoft.pydroid.ui.util.setUpEnabled

internal abstract class ToolbarView<S : UiViewState, E : UiViewEvent> protected constructor(
    backstackCount: Int,
    protected val toolbarActivity: ToolbarActivity
) : UiView<S, E>() {

    private var oldTitle: CharSequence? = null

    init {
        doOnInflate { savedInstanceState ->
            val title = savedInstanceState.get<CharSequence>(KEY_OLD_TITLE)
            if (title != null) {
                oldTitle = title
            }

            toolbarActivity.withToolbar { toolbar ->
                if (oldTitle == null) {
                    oldTitle = toolbar.title
                }

                toolbar.setUpEnabled(true)
                toolbar.setNavigationOnClickListener(DebouncedOnClickListener.create {
                    onNavigationClicked()
                })
            }
        }

        doOnTeardown {
            toolbarActivity.withToolbar { toolbar ->
                // Set title back to original
                toolbar.title = oldTitle ?: toolbar.title

                // If this page was last on the back stack, set up false
                if (backstackCount == 0) {
                    toolbar.setUpEnabled(false)
                }

                toolbar.setNavigationOnClickListener(null)
            }
        }

        doOnSaveState { outState ->
            val title = oldTitle
            if (title == null) {
                outState.remove(KEY_OLD_TITLE)
            } else {
                outState.put(KEY_OLD_TITLE, title)
            }
        }
    }

    final override fun id(): Int {
        throw InvalidIdException
    }

    final override fun onInit(savedInstanceState: UiBundleReader) {
    }

    protected abstract fun onNavigationClicked()

    companion object {

        private const val KEY_OLD_TITLE = "key_old_title"
    }
}
